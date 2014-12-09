package org.curriki.xwiki.plugin.curriki;

import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.output.StringBuilderWriter;
import org.curriki.plugin.spacemanager.impl.CurrikiSpaceManager;
import org.curriki.plugin.spacemanager.plugin.CurrikiSpaceManagerPluginApi;
import org.curriki.xwiki.plugin.asset.*;
import org.curriki.xwiki.plugin.asset.composite.RootCollectionCompositeAsset;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.notify.XWikiNotificationRule;
import com.xpn.xwiki.notify.XWikiDocChangeNotificationInterface;
import com.xpn.xwiki.notify.DocChangeRule;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.api.Property;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.PropertyInterface;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.objects.classes.DBTreeListClass;
import com.xpn.xwiki.objects.classes.StaticListClass;
import com.xpn.xwiki.objects.classes.BooleanClass;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import com.xpn.xwiki.plugin.spacemanager.api.Space;
import com.xpn.xwiki.web.XWikiRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParserFactory;

/**
 */
public class CurrikiPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface, XWikiDocChangeNotificationInterface {
    public static final String PLUGIN_NAME = "curriki";

    private static final Logger LOG = LoggerFactory.getLogger(CurrikiPlugin.class);
    private static ThreadLocal<SimpleDateFormat> durationDf = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("mm'm'ss's'SSS'ms'");
        }};
    private static boolean startupURLLaunched = false;

    public CurrikiPlugin(String name, String className, XWikiContext context) {
        super(name, className, context);
        init(context);
        LOG.debug("Curriki plugin constructed");
    }

    @Override public void init(XWikiContext context) {
        super.init(context);

        // Creating classes
        try {
            // we need to create the asset classes if they don't exist
            initAssetClass(context);
            initAssetLicenseClass(context);
            initAssetTextClass(context);
            initAssetDocumentClass(context);
            initAssetImageClass(context);
            initAssetVideoClass(context);
            initAssetArchiveClass(context);
            initAssetExternalClass(context);
            initSubAssetClass(context);
            initReorderAssetClass(context);
            initCompositeAssetClass(context);
            initSolrClient(context);
        } catch (XWikiException e) {
            if (LOG.isErrorEnabled())
                LOG.error("Error generating asset classes", e);
        }

        try {
            // init the asset type managers
            DefaultAssetManager.initAssetSubTypes(context);
        } catch (Exception e) {
            if (LOG.isErrorEnabled())
                LOG.error("Error initing sub asset type classes", e);
        }

        // Insert a notification so that we can handle rollback and convert assets
        // if we are reading an asset in the old format
        context.getWiki().getNotificationManager(). addGeneralRule(new DocChangeRule(this, true, false));

        if(!startupURLLaunched) {
            String startupURLs = context.getWiki().Param("curriki.startupURLs");
            final Pattern ptrn = Pattern.compile("http://(.*):(.*)@([^/]+)(:([0-9]+))?/.*");
            if(startupURLs!=null) for(final String startupURL: startupURLs.split("[,\t\n ]+")) {
                new Thread("Startup URL fetch " + startupURL) {
                    public void run() {
                        try {
                            LOG.warn("Loading startup URL " + startupURL + ".");
                            HttpClient client = new HttpClient();
                            Matcher matcher = ptrn.matcher(startupURL);
                            if(matcher.matches()) {
                                client.getParams().setAuthenticationPreemptive(true);
                                Credentials defaultcreds = new UsernamePasswordCredentials(matcher.group(1), matcher.group(2));
                                String host = matcher.group(3);
                                int port = 80;
                                if(matcher.group(5)!=null && matcher.group(5).length()>0) port = Integer.parseInt(matcher.group(5));
                                client.getState().setCredentials(new AuthScope(host, port, AuthScope.ANY_REALM), defaultcreds);
                            }
                            GetMethod method = new GetMethod(startupURL);
                            int status = client.executeMethod(method);
                            if(status!=200) throw new IllegalStateException("URL " + startupURL + " responded " + method.getStatusLine());
                            LOG.warn("Startup URL " + startupURL + " successfully loaded.");
                        } catch (Exception e) {
                            LOG.warn("Notification page " + startupURL + " failed to load.",e);
                        }
                    }
                }.start();
            }
            startupURLLaunched = true;
        }


    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
        return new CurrikiPluginApi((CurrikiPlugin) plugin, context);
    }

    @Override public void flushCache() {
        super.flushCache();
    }



    public Asset createAsset(String parent, String publishSpace, XWikiContext context) throws XWikiException {
        return Asset.createTempAsset(parent, publishSpace, context);
    }

    public Asset createAsset(String parent, XWikiContext context) throws XWikiException {
        return Asset.createTempAsset(parent, context);
    }


    public Asset copyAsset(String copyOf, String publishSpace, XWikiContext context) throws XWikiException {
        return Asset.copyTempAsset(copyOf, publishSpace, context);
    }

    public Asset copyAsset(String copyOf, XWikiContext context) throws XWikiException {
        return Asset.copyTempAsset(copyOf, context);
    }


    public Asset fetchAsset(String assetName, XWikiContext context) throws XWikiException {
        return fetchAssetAs(assetName, null, context);
    }

    public Asset fetchAssetAs(String assetName, Class<? extends Asset> classType, XWikiContext context) throws XWikiException {
        return Asset.fetchAsset(assetName, context).as(classType);
        /*
        com.xpn.xwiki.api.XWiki xwikiApi = new com.xpn.xwiki.api.XWiki(context.getWiki(), context);
        Document doc = xwikiApi.getDocument(assetName);
        if (doc instanceof Asset) {
            return ((Asset) doc).as(classType);
        }

        throw new AssetException("Asset "+assetName+" could not be found");
        */
    }

    public Asset fetchAssetSubclassAs(String assetName, Class<? extends Asset> classType, XWikiContext context) throws XWikiException {
        return Asset.fetchAsset(assetName, context).subclassAs(classType);
    }

    public List<Property> fetchAssetMetadata(String assetName, XWikiContext context) throws XWikiException {
        Asset asset = fetchAsset(assetName, context);
        if (asset != null) {
            return asset.getMetadata();
        }

        return null;
    }



    public Map<String, Object> fetchUserInfo(XWikiContext context) {
        Map<String,Object> userInfo = new HashMap<String,Object>();

        userInfo.put("username", context.getUser());

        if (Constants.GUEST_USER.equals(context.getUser())) {
            userInfo.put("fullname", "");
        } else {
            userInfo.put("fullname", context.getWiki().getUserName(context.getUser(), null, false, context));
        }

        return userInfo;
    }


    /**
     * Obtain a list of all groups a user is in
     *
     * @param forUser User to search for
     * @param context Standard XWikiContext object
     * @return List of all groups that the specified user is in
     */
    public Map<String,Object> fetchUserGroups(String forUser, XWikiContext context) {
        if(LOG.isDebugEnabled()) LOG.debug("fetchUserGroups " + forUser);
        long start = System.nanoTime(), time = start;
        Map<String,Object> groups = new HashMap<String,Object>();
        CurrikiSpaceManagerPluginApi sm = (CurrikiSpaceManagerPluginApi) context.getWiki().getPluginApi(CurrikiSpaceManager.CURRIKI_SPACEMANGER_NAME, context);
        List spaces;
        try {
            spaces = sm.getSpaceNames(forUser, null);
            time = System.nanoTime();
            if(LOG.isDebugEnabled()) LOG.debug("fetchUserGroups: getSpaceNames: " + durationDf.get().format((time-start)/1000000)+ " " + ((time-start)%1000000) + "ns");
            start = time;
        } catch (Exception e) {
            // Ignore exception -- just return an empty list
            LOG.error("Error getting user groups", e);
            return null;
        }

        //CURRIKI-5472: this is a dangerous spot: it has to deserialize all the collections of each of the group
        for (Object space : spaces) {
            if (space instanceof String) {
                groups.put((String) space, getGroupInfo((String) space, context));
                time = System.nanoTime();
                if(LOG.isDebugEnabled()) LOG.debug("fetchUserGroups: getGroupInfo: "+ space+ ": " + durationDf.get().format((time-start)/1000000)+ " " + ((time-start)%1000000) + "ns");
                start = time;
            }
        }

        return groups;
    }

    protected Map<String,Object> getGroupInfo(String group, XWikiContext context) {
        if(LOG.isDebugEnabled()) LOG.debug("getGroupInfo " + group);
        Map<String,Object> groupInfo = new HashMap<String,Object>();
        CurrikiSpaceManagerPluginApi sm = (CurrikiSpaceManagerPluginApi) context.getWiki().getPluginApi("csm", context);

        try {
            Space space = sm.getSpace(group);
            groupInfo.put("displayTitle", space.getDisplayTitle());
            groupInfo.put("description", space.getDescription());
            //CURRIKI-5472: this is a dangerous spot: it has to deserialize all the collections of the group

            if(hasCollections(group, context)) {
                if(LOG.isDebugEnabled()) LOG.debug("Group " + group +" has collections.");
                groupInfo.put("collectionCount", 1);
                groupInfo.put("editableCollectionCount", 1);
            } else {
                if(LOG.isDebugEnabled()) LOG.debug("Group " + group + " has no collections.");
                groupInfo.put("collectionCount", 0);
                groupInfo.put("editableCollectionCount", 0);
            }

            /* groupInfo.put("collectionCount", collections.size());
            Map<String,Object> collections = fetchCollectionsInfo(group, context);
            int editableCount = 0;
            for (String collection : collections.keySet()) {
                Map<String,Object> cInfo = (Map<String,Object>) collections.get(collection);
                Map<String,Boolean> rInfo = (Map<String,Boolean>) cInfo.get("rights");
                if (rInfo.get("edit")) {
                    editableCount++;
                }
            }
            groupInfo.put("editableCollectionCount", editableCount); */
        } catch (Exception e) {
            LOG.error("Error getting group space", e);
            return null;
        }

        return groupInfo;
    }

    private boolean hasCollections(String entity, XWikiContext context) {
        entity = entity.replaceFirst(Constants.USER_PREFIX_REGEX, ""); // For users
        entity = entity.replaceFirst("\\."+Constants.ROOT_COLLECTION_PAGE+"$", ""); // For groups

        if (Constants.GUEST_USER.replaceFirst(Constants.USER_PREFIX_REGEX, "").equals(entity)) {
            return false;
        }

        String spaceName = Constants.COLLECTION_PREFIX+entity;
        String rootPage = spaceName+"."+Constants.ROOT_COLLECTION_PAGE;
        RootCollectionCompositeAsset root;
        try {
            root = Asset.fetchAsset(rootPage, context).as(RootCollectionCompositeAsset.class);
            if(root!=null) {
                List<String> subAssets = root.getSubassetList();
                if(!subAssets.isEmpty()) return true;
            }
        } catch (XWikiException ex) {
            // The page exists, but must not be a root collection -- ah well
        }
        return false;
    }



    /**
     * Verificate if a user is in Group
     * @param groupName
     * @param context
     * @return
     * @throws XWikiException
     */
    public Boolean isMember(String groupName,XWikiContext context) throws XWikiException {
        XWikiDocument doc = context.getWiki().getDocument(groupName, context);
        Vector<BaseObject> groups = doc.getObjects("XWiki.XWikiGroups");
        if (groups!=null)
        {
            for (Iterator iterator = groups.iterator(); iterator.hasNext();) {
                BaseObject group = (BaseObject) iterator.next();
                if (group!=null)
                {
                    String groupMember = group.getStringValue("member");
                    if (groupMember!=null && context.getUser().equals(groupMember))
                        return true;
                }
            }
        }
        return false;
    }



    public List<String> fetchCollectionsList(String entity, XWikiContext context) throws XWikiException {
        if(LOG.isDebugEnabled()) LOG.debug("fetchCollectionsList " + entity);
        long start = System.nanoTime();
        RootCollectionCompositeAsset root = fetchRootCollection(entity, context);
        if (root == null) {
            // Ignore any error, will just return 0 results
            return new ArrayList<String>();
        }

        List<String> l = root.getSubassetList();

        long time = System.nanoTime();
        if(LOG.isDebugEnabled()) LOG.debug("fetchCollectionsList: " + durationDf.get().format((time-start)/1000000)+ " " + ((time-start)%1000000) + "ns");
        return l;
    }

    public Map<String,Object> fetchCollectionsInfo(String entity, XWikiContext context) throws XWikiException {
        if(LOG.isDebugEnabled()) LOG.debug("fetchCollectionsInfo " + entity);
        long start = System.nanoTime();
        RootCollectionCompositeAsset root = fetchRootCollection(entity, context);
        if (root == null) {
            // Ignore any error, will just return 0 results
            return new HashMap<String,Object>();
        }

        Map<String,Object> r = root.fetchCollectionsInfo();
        long time = System.nanoTime();
        if(LOG.isDebugEnabled()) LOG.debug("fetchCollectionsList: " + durationDf.get().format((time-start)/1000000)+ " " + ((time-start)%1000000) + "ns");
        return r;
    }



    public RootCollectionCompositeAsset fetchRootCollection(String entity, XWikiContext context) throws XWikiException {
        entity = entity.replaceFirst(Constants.USER_PREFIX_REGEX, ""); // For users
        entity = entity.replaceFirst("\\."+Constants.ROOT_COLLECTION_PAGE+"$", ""); // For groups

        if (Constants.GUEST_USER.replaceFirst(Constants.USER_PREFIX_REGEX, "").equals(entity)) {
            return null;
        }

        RootCollectionCompositeAsset root;
        try {
            root = CollectionSpace.getRootCollection(Constants.COLLECTION_PREFIX+entity, context);
        } catch (XWikiException e) {
            root = null;
        }

        return root;
    }



    /**
     * Returns a {@link java.util.List} containing the String names of each ICT of the asset
     *
     * @param assetName
     * @param context
     * @return
     * @throws XWikiException
     */
    public List<String> getAssetICT(String assetName, XWikiContext context) throws XWikiException {
        List<String> listICT = new ArrayList<String>();
        if(assetName!=null && (assetName.lastIndexOf("'")>-1 || assetName.lastIndexOf("’")>-1)){
            return listICT;
        }
        Asset asset = fetchAssetAs(assetName, null, context);
        String ictStr = "";

        if (asset.getObject(Constants.ASSET_CLASS) != null){
            asset.use(Constants.ASSET_CLASS);
            if (asset.get(Constants.ASSET_CLASS_INSTRUCTIONAL_COMPONENT) != null){
                ictStr = (String)asset.get(Constants.ASSET_CLASS_INSTRUCTIONAL_COMPONENT);
                ictStr=ictStr.replaceAll("#--#", ",");
                StringTokenizer elements = new StringTokenizer(ictStr,",");
                while(elements.hasMoreTokens()){
                    listICT.add(elements.nextToken());
                }

            }
        }

        return listICT;

    }

    /**
     * change the date format from a date string.
     * @param date
     * @param currentPattern
     * @param newPattern
     * @param delim
     * @return
     */
    public String changeFormatDate(String date,String currentPattern,String newPattern,String delim)
    {
        try {
            StringTokenizer tokenDate = new StringTokenizer(date,delim);
            StringTokenizer tokenPattern = new StringTokenizer(currentPattern,delim);
            Map hashData = new HashMap();
            int count = tokenPattern.countTokens();
            for (int i = 0; i < count; i++) {
                hashData.put(tokenPattern.nextToken(), tokenDate.nextToken());
            }

            tokenPattern = new StringTokenizer(newPattern,delim);
            String result = "";
            count = tokenPattern.countTokens();
            for (int i = 0; i < count; i++) {
                result += hashData.get(tokenPattern.nextToken());
                if (i<count-1)
                    result += delim;
            }
            return result;
        } catch (Exception e) {
            LOG.debug(e.getMessage(),e);
            return "";
        }
    }

    public String formatDate(Date date,String pattern)
    {
        if (date!=null && date instanceof Date)
            try {
                return (new SimpleDateFormat(pattern)).format(date);
            } catch (Exception e) {
                LOG.debug(e.getMessage(),e);
            }
        return ""+date;
    }


    /**
     * Builds a map with the number of resources in each status given the criteria used in the BFCS queue filter
     * @param baseHql
     * @param context
     * @return
     * @throws XWikiException
     */
    public Map getSeeCountsByStatus(String baseHql, XWikiContext context) throws XWikiException {
        // Add the first part of the query for getting the number of docs with each status
        String sql = baseHql;
        XWikiRequest req = context.getRequest();
        String assetFilterFileCheckStatus = req.getCookie("assetFilterFileCheckStatus")!=null?req.getCookie("assetFilterFileCheckStatus").getValue():null;

        String auxHqlPart1="";
        if(assetFilterFileCheckStatus==null || assetFilterFileCheckStatus.equals("1")||assetFilterFileCheckStatus.equals("0")){
            //the user has not search by status but we need the corresponding table in the "from" for hqlPart1
            auxHqlPart1= ", StringProperty as sprop ";
        }
        auxHqlPart1+=sql;
        String hqlPart1 = "select sprop.value, count(doc.id) from XWikiDocument as doc "+auxHqlPart1+"  and obj.id=sprop.id.id and sprop.id.name='fcstatus' group by sprop.value";
        // Add the second part of the query for getting the number of docs without status
        String hqlPart2 = "select '0', count(distinct doc.id) from XWikiDocument as doc "+sql+" and obj.id not in (select obj2.id from BaseObject as obj2, StringProperty as sprop2 where obj2.className='CurrikiCode.AssetClass' and obj2.id=sprop2.id.id and sprop2.id.name='fcstatus' and (sprop2.value is not null or sprop2.value = '0'))";

        com.xpn.xwiki.api.XWiki xwikiApi = new com.xpn.xwiki.api.XWiki(context.getWiki(), context);
        List queryResults = new ArrayList();
        Map results = new HashMap();


        queryResults = xwikiApi.search(hqlPart1);
        if(queryResults!=null && queryResults.size()>0){
            Iterator iter = queryResults.iterator();
            while(iter.hasNext()){
                Object[] item = (Object[])(iter.next());
                //List itemResults = new ArrayList<String>();
                results.put((String)item[0],(Long)item[1]);

            }
        }

        queryResults = xwikiApi.search(hqlPart2);
        if(queryResults!=null && queryResults.size()>0){
            Iterator iter = queryResults.iterator();
            while(iter.hasNext()){
                Object[] item = (Object[])(iter.next());
                //List itemResults = new ArrayList<String>();
                results.put((String)item[0],(Long)item[1]);

            }
        }
        return results;
    }


    /**
     * Get class property values as a list. This code was extracted from FieldResource.represent
     * @param className
     * @param fieldName
     * @param xwikiContext
     * @return
     * @throws XWikiException
     */
    public List getValues(String className, String fieldName, XWikiContext xwikiContext)throws XWikiException{
        List result = new ArrayList();
        PropertyInterface field = null;
        BaseClass xwikiClass = xwikiContext.getWiki().getDocument(className, xwikiContext).getxWikiClass();
        field = xwikiClass.get(fieldName);
        String fieldType = field.getClass().getCanonicalName();
        String shortFieldType = fieldType.replaceFirst("^com\\.xpn\\.xwiki\\.objects\\.classes\\.", "");
        shortFieldType = shortFieldType.replaceFirst("Class$", "");
        if (shortFieldType.equals("DBList")) {
            result.addAll(((com.xpn.xwiki.objects.classes.DBListClass) field).getList(xwikiContext));
        }  else if (shortFieldType.equals("StaticList")) {
            result.addAll(((com.xpn.xwiki.objects.classes.StaticListClass) field).getList(xwikiContext));
        }

        return result;
    }


    private void initAssetClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(Constants.ASSET_CLASS, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(Constants.ASSET_CLASS);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(Constants.ASSET_CLASS);
        needsUpdate |= bclass.addTextAreaField(Constants.ASSET_CLASS_DESCRIPTION, Constants.ASSET_CLASS_DESCRIPTION, 60, 6);
        needsUpdate |= bclass.addStaticListField(Constants.ASSET_CLASS_KEYWORDS, Constants.ASSET_CLASS_KEYWORDS, 40, true, "", "input", " ,|");
        needsUpdate |= bclass.addStaticListField(Constants.ASSET_CLASS_CATEGORY, Constants.ASSET_CLASS_CATEGORY, 1, false,
                Constants.ASSET_CATEGORY_TEXT  + "|" + Constants.ASSET_CATEGORY_IMAGE
                        + "|" + Constants.ASSET_CATEGORY_AUDIO + "|" + Constants.ASSET_CATEGORY_VIDEO + "|" + Constants.ASSET_CATEGORY_INTERACTIVE + "|" + Constants.ASSET_CATEGORY_ARCHIVE  + "|" + Constants.ASSET_CATEGORY_DOCUMENT + "|" + Constants.ASSET_CATEGORY_EXTERNAL
                        + "|" + Constants.ASSET_CATEGORY_COLLECTION  + "|" + Constants.ASSET_CATEGORY_ATTACHMENT + "|" + Constants.ASSET_CATEGORY_UNKNOWN);
        needsUpdate |= bclass.addDBTreeListField(Constants.ASSET_CLASS_FRAMEWORK_ITEMS, Constants.ASSET_CLASS_FRAMEWORK_ITEMS, 10, true, Constants.ASSET_CLASS_FRAMEWORK_ITEMS_QUERY);
        ((DBTreeListClass)bclass.get(Constants.ASSET_CLASS_FRAMEWORK_ITEMS)).setCache(true);
        ((DBTreeListClass)bclass.get(Constants.ASSET_CLASS_FRAMEWORK_ITEMS)).setSeparators("|");
        ((DBTreeListClass)bclass.get(Constants.ASSET_CLASS_FRAMEWORK_ITEMS)).setSeparator(" ");
        ((DBTreeListClass)bclass.get(Constants.ASSET_CLASS_FRAMEWORK_ITEMS)).setPicker(true);
        needsUpdate |= bclass.addStaticListField(Constants.ASSET_CLASS_EDUCATIONAL_LEVEL, Constants.ASSET_CLASS_EDUCATIONAL_LEVEL, 5, true, Constants.ASSET_CLASS_EDUCATIONAL_LEVEL_VALUES);
        ((StaticListClass)bclass.get(Constants.ASSET_CLASS_EDUCATIONAL_LEVEL)).setSeparators(" ,|");
        ((StaticListClass)bclass.get(Constants.ASSET_CLASS_EDUCATIONAL_LEVEL)).setSeparator("#--#");
        ((StaticListClass)bclass.get(Constants.ASSET_CLASS_EDUCATIONAL_LEVEL)).setCache(true);
        needsUpdate |= bclass.addStaticListField(Constants.ASSET_CLASS_INSTRUCTIONAL_COMPONENT, Constants.ASSET_CLASS_INSTRUCTIONAL_COMPONENT, 5, true, Constants.ASSET_CLASS_INSTRUCTIONAL_COMPONENT_VALUES);
        ((StaticListClass)bclass.get(Constants.ASSET_CLASS_INSTRUCTIONAL_COMPONENT)).setSeparators(" ,|");
        ((StaticListClass)bclass.get(Constants.ASSET_CLASS_INSTRUCTIONAL_COMPONENT)).setSeparator("#--#");
        ((StaticListClass)bclass.get(Constants.ASSET_CLASS_INSTRUCTIONAL_COMPONENT)).setCache(true);
        needsUpdate |= bclass.addStaticListField(Constants.ASSET_CLASS_RIGHT, Constants.ASSET_CLASS_RIGHT, 1, false, Constants.ASSET_CLASS_RIGHT_VALUES, "radio");
        ((StaticListClass)bclass.get(Constants.ASSET_CLASS_RIGHT)).setCache(true);
        needsUpdate |= bclass.addStaticListField(Constants.ASSET_CLASS_LANGUAGE, Constants.ASSET_CLASS_LANGUAGE, 1, false, Constants.ASSET_CLASS_LANGUAGE_VALUES);
        ((StaticListClass)bclass.get(Constants.ASSET_CLASS_LANGUAGE)).setCache(true);
        needsUpdate |= bclass.addBooleanField(Constants.ASSET_CLASS_HIDDEN_FROM_SEARCH, Constants.ASSET_CLASS_HIDDEN_FROM_SEARCH, "checkbox");
        ((BooleanClass)bclass.get(Constants.ASSET_CLASS_HIDDEN_FROM_SEARCH)).setDefaultValue(0);
        needsUpdate |= bclass.addTextField(Constants.ASSET_CLASS_TRACKING, Constants.ASSET_CLASS_TRACKING, 60);

        // file check fields
        needsUpdate |= bclass.addTextField(Constants.ASSET_CLASS_FCREVIEWER, Constants.ASSET_CLASS_FCREVIEWER, 30);
        needsUpdate |= bclass.addDateField(Constants.ASSET_CLASS_FCDATE, Constants.ASSET_CLASS_FCDATE, Constants.ASSET_CLASS_FCDATE_FORMAT, 0);
        needsUpdate |= bclass.addTextAreaField(Constants.ASSET_CLASS_FCNOTES, Constants.ASSET_CLASS_FCNOTES, 80, 10);
        needsUpdate |= bclass.addStaticListField(Constants.ASSET_CLASS_FCSTATUS, Constants.ASSET_CLASS_FCSTATUS, 1, false, Constants.ASSET_CLASS_FCSTATUS_VALUES);

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + Constants.ASSET_CLASS);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    private void initAssetLicenseClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(Constants.ASSET_LICENCE_CLASS, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(Constants.ASSET_LICENCE_CLASS);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(Constants.ASSET_LICENCE_CLASS);

        needsUpdate |= bclass.addTextField(Constants.ASSET_LICENCE_ITEM_RIGHTS_HOLDER, Constants.ASSET_LICENCE_ITEM_RIGHTS_HOLDER, 60);
        needsUpdate |= bclass.addTextField(Constants.ASSET_LICENCE_ITEM_EXTERNAL_RIGHTS_HOLDER, Constants.ASSET_LICENCE_ITEM_EXTERNAL_RIGHTS_HOLDER, 60);
        needsUpdate |= bclass.addDateField(Constants.ASSET_LICENCE_ITEM_EXPIRY_DATE, Constants.ASSET_LICENCE_ITEM_EXPIRY_DATE, Constants.ASSET_LICENCE_ITEM_EXPIRY_DATE_FORMAT);
        needsUpdate |= bclass.addStaticListField(Constants.ASSET_LICENCE_ITEM_LICENCE_TYPE, Constants.ASSET_LICENCE_ITEM_LICENCE_TYPE, 1, false, Constants.ASSET_LICENCE_ITEM_LICENCE_TYPE_VALUES);
        ((StaticListClass)bclass.get(Constants.ASSET_LICENCE_ITEM_LICENCE_TYPE)).setCache(true);

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + Constants.ASSET_LICENCE_CLASS);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    private void initAssetDocumentClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(Constants.ATTACHMENT_ASSET_CLASS, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(Constants.ATTACHMENT_ASSET_CLASS);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(Constants.ATTACHMENT_ASSET_CLASS);

        needsUpdate |= bclass.addTextAreaField(Constants.ATTACHMENT_ASSET_ALT_TEXT, Constants.ATTACHMENT_ASSET_ALT_TEXT, 40, 5);
        needsUpdate |= bclass.addTextAreaField(Constants.ATTACHMENT_ASSET_CAPTION_TEXT, Constants.ATTACHMENT_ASSET_CAPTION_TEXT, 40, 5);
        needsUpdate |= bclass.addTextField(Constants.ATTACHMENT_ASSET_FILE_TYPE, Constants.ATTACHMENT_ASSET_FILE_TYPE, 10);
        needsUpdate |= bclass.addNumberField(Constants.ATTACHMENT_ASSET_FILE_SIZE, Constants.ATTACHMENT_ASSET_FILE_SIZE, 10, "long");

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + Constants.ATTACHMENT_ASSET_CLASS);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    private void initAssetVideoClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(Constants.VIDEO_ASSET_CLASS, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(Constants.VIDEO_ASSET_CLASS);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(Constants.VIDEO_ASSET_CLASS);

        needsUpdate |= bclass.addStaticListField(Constants.VIDEO_ASSET_PARTNER, Constants.VIDEO_ASSET_PARTNER, 1, false,
                Constants.VIDEO_ASSET_PARTNER_VALUES);
        needsUpdate |= bclass.addTextField(Constants.VIDEO_ASSET_ID, Constants.VIDEO_ASSET_ID, 30);

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + Constants.VIDEO_ASSET_CLASS);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    private void initAssetArchiveClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(Constants.ARCHIVE_ASSET_CLASS, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(Constants.ARCHIVE_ASSET_CLASS);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(Constants.ARCHIVE_ASSET_CLASS);

        needsUpdate |= bclass.addTextField(Constants.ARCHIVE_ASSET_START_FILE, Constants.ARCHIVE_ASSET_START_FILE, 60);
        needsUpdate |= bclass.addStaticListField(Constants.ARCHIVE_ASSET_TYPE, Constants.ARCHIVE_ASSET_TYPE, 1, false,
                Constants.ARCHIVE_ASSET_TYPE_VALUES);


        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + Constants.ARCHIVE_ASSET_CLASS);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    private void initAssetImageClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(Constants.IMAGE_ASSET_CLASS, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(Constants.IMAGE_ASSET_CLASS);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(Constants.IMAGE_ASSET_CLASS);

        needsUpdate |= bclass.addNumberField(Constants.IMAGE_ASSET_WIDTH, Constants.IMAGE_ASSET_WIDTH, 5, "integer");
        needsUpdate |= bclass.addNumberField(Constants.IMAGE_ASSET_HEIGHT, Constants.IMAGE_ASSET_HEIGHT, 5, "integer");

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + Constants.IMAGE_ASSET_CLASS);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    private void initAssetExternalClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(Constants.EXTERNAL_ASSET_CLASS, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(Constants.EXTERNAL_ASSET_CLASS);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(Constants.EXTERNAL_ASSET_CLASS);

        needsUpdate |= bclass.addTextField(Constants.EXTERNAL_ASSET_LINK, Constants.EXTERNAL_ASSET_LINK, 80);
        needsUpdate |= bclass.addTextField(Constants.EXTERNAL_ASSET_LINKTEXT, Constants.EXTERNAL_ASSET_LINKTEXT, 80);

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + Constants.EXTERNAL_ASSET_CLASS);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }
    private void initAssetTextClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(Constants.TEXT_ASSET_CLASS, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(Constants.TEXT_ASSET_CLASS);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(Constants.TEXT_ASSET_CLASS);

        needsUpdate |= bclass.addStaticListField(Constants.TEXT_ASSET_SYNTAX, Constants.TEXT_ASSET_SYNTAX, 1, false,
                Constants.TEXT_ASSET_SYNTAX_TEXT  + "|" + Constants.TEXT_ASSET_SYNTAX_XHTML1
                        + "|" + Constants.TEXT_ASSET_SYNTAX_XWIKI1 + "|" + Constants.TEXT_ASSET_SYNTAX_XWIKI2);

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + Constants.TEXT_ASSET_CLASS);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    private void initSubAssetClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(Constants.SUBASSET_CLASS, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(Constants.SUBASSET_CLASS);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(Constants.SUBASSET_CLASS);

        needsUpdate |= bclass.addTextField(Constants.SUBASSET_CLASS_PAGE, Constants.SUBASSET_CLASS_PAGE, 40);
        needsUpdate |= bclass.addNumberField(Constants.SUBASSET_CLASS_ORDER, Constants.SUBASSET_CLASS_ORDER, 10, "long");

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + Constants.SUBASSET_CLASS);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }
    private void initReorderAssetClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(Constants.COLLECTION_REORDERED_CLASS, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(Constants.COLLECTION_REORDERED_CLASS);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(Constants.COLLECTION_REORDERED_CLASS);

        needsUpdate |= bclass.addBooleanField(Constants.COLLECTION_REORDERED_CLASS_REORDERD, Constants.COLLECTION_REORDERED_CLASS_REORDERD, "select");

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + Constants.COLLECTION_REORDERED_CLASS);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    private void initCompositeAssetClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(Constants.COMPOSITE_ASSET_CLASS, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(Constants.COMPOSITE_ASSET_CLASS);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(Constants.COMPOSITE_ASSET_CLASS);

        needsUpdate |= bclass.addStaticListField(Constants.COMPOSITE_ASSET_CLASS_TYPE, Constants.COMPOSITE_ASSET_CLASS_TYPE, 1, false,
                Constants.COMPOSITE_ASSET_CLASS_TYPE_SUBFOLDER + "|" + Constants.COMPOSITE_ASSET_CLASS_TYPE_COLLECTION+ "|" + Constants.COMPOSITE_ASSET_CLASS_TYPE_ROOT_COLLECTION);

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + Constants.COMPOSITE_ASSET_CLASS);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }


    /**
     * Notification to handle a rollback and check the result
     */
    public void notify(XWikiNotificationRule rule, XWikiDocument newdoc, XWikiDocument olddoc, int event, XWikiContext context) {
        try {
            // we are called in pre saving and we are modifying the document directly
            // we need to take the document from the context
            // because the previous one is a copy
            Document apidoc = newdoc.newDocument(context);

            if(LOG.isWarnEnabled()) LOG.warn("Action " + context.getAction() + " " + newdoc.getFullName());

            // invalidate config Cache if a CurrikiConfig page was saved: => gives wiki writable configs
            if("CurrikiConfig".equals(newdoc.getSpace())) {
                publicConfigCache.remove(newdoc.getName());
            }
            if (context.getAction().equals("rollback")&&(apidoc instanceof Asset)) {
                Asset asset = (Asset) apidoc;
                if (!asset.isLatestVersion()) {
                    // We need to convert this document
                    if (LOG.isInfoEnabled())
                        LOG.info("CURRIKI ASSET CONVERTER: asset Needs to be converted: " + newdoc.getFullName());

                    LOG.error("Converting " + newdoc.getFullName());

                    // This is a very big hack allowing to bypass the cloning performed by Document
                    // and therefore allowing to modify the right document that will then be saved
                    try {
                        Method method = Asset.class.getDeclaredMethod("setAlreadyCloned");
                        method.setAccessible(true);
                        method.invoke(apidoc);
                    } catch (Exception e) {
                        if (LOG.isErrorEnabled())
                            LOG.error("CURRIKI ASSET CONVERTER: could not overide clone field for asset: " + newdoc.getFullName(), e);
                        return;
                    }

                    // run the actual conversion without saving
                    asset.convertWithoutSave();

                    LOG.error("Converted " + newdoc.getFullName());

                    if (LOG.isInfoEnabled())
                        LOG.info("CURRIKI ASSET CONVERTER: asset has been converted: " + newdoc.getFullName());
                }
            }
        } catch (Exception e) {
            if (LOG.isErrorEnabled())
                LOG.error("CURRIKI ASSET CONVERTER: Error evaluating asset conversion or converting asset for asset: " + newdoc.getFullName(), e);
        }
    }

    private long timeSpentEnsuringCookies = 0;
    private int numRequestsEnsuringCookies = 0;

    public void ensureUsernameCookie(HttpServletRequest req, HttpServletResponse resp, String username) {
        long start = System.nanoTime();
        if("XWiki.XWikiGuest".equals(username)) username = null;
        Cookie[] cookies = req.getCookies();
        Cookie unameCookie = null;
        for(Cookie cookie: cookies) {
            if("uname".equals(cookie.getName())) {
                unameCookie = cookie; break;
            }
        }
        if(username==null) {
            if(unameCookie != null) { // remove it
                Cookie c = new Cookie("uname", "");
                c.setPath(unameCookie.getPath());
                c.setDomain(unameCookie.getDomain());
                c.setMaxAge(0);
                resp.addCookie(c);
            } // otherwise nothing to do, leave it off
        } else { // username !=null
            if(unameCookie != null) { // check it
                if(username.equals(unameCookie.getValue())) {
                    // nothing to do, things are ok, it won't install it
                } else { // username change... that seems harsh
                    Cookie c = new Cookie("uname", username);
                    c.setPath(unameCookie.getPath());
                    c.setDomain(unameCookie.getDomain());
                    c.setMaxAge(1800); // 30 minutes
                    resp.addCookie(c);
                }
            }
            if(unameCookie==null) {
                // install it
                Cookie c = new Cookie("uname", username);
                c.setPath("/");
                c.setMaxAge(1800); // 30 minutes
                resp.addCookie(c);
            }
        }
        numRequestsEnsuringCookies++;
        timeSpentEnsuringCookies+= (System.nanoTime()-start);
        if(numRequestsEnsuringCookies>0 && numRequestsEnsuringCookies % 10==0)
            if(LOG.isDebugEnabled()) LOG.debug("Spent mean time of " + (timeSpentEnsuringCookies/numRequestsEnsuringCookies) + " nanoseconds in " + numRequestsEnsuringCookies + " requests to ensure cookies.");
    }

    // ---- solr specific ------
    // this code is not meant to stay here but is expected to move after the GSoC for SOLR is concluded
    private HttpClient solrClient = null;
    private String solrBaseURL;

    public void initSolrClient(XWikiContext context) {
        solrBaseURL = context.getWiki().Param("org.curriki.solrUrl");
        MultiThreadedHttpConnectionManager connectionManager =
                new MultiThreadedHttpConnectionManager();
        solrClient = new HttpClient(connectionManager);
    }

    public String solrGetSingleValue(String query, String fieldName) throws IOException {
        GetMethod g = solrCreateQueryGetMethod(query, fieldName, 0, 1);
        int status = solrClient.executeMethod(g);
        if(status !=200) throw new IllegalStateException("Solr responded status " + g.getStatusCode() + " " + g.getStatusText());
        feedFieldFromXmlStream(g, singleValueReadBuff.get(), fieldName);
        StringBuilder builder = singleValueReadBuff.get().getBuilder();
        String result = builder.toString().trim();
        builder.delete(0, builder.length());
        return result;
    }

    public GetMethod solrCreateQueryGetMethod(String query, String fieldNames, int start, int rows) throws IOException {
        return solrCreateQueryGetMethod(query, fieldNames, null, start, rows);
    }

    public GetMethod solrCreateQueryGetMethod(String query, String fieldNames, String sortParam, int start, int rows) throws IOException {
        // TODO: only allow with programming right?
        String url = solrBaseURL + "/select?q=" + URLEncoder.encode(query, "UTF-8") + "&fl=" + fieldNames + "&start=" + start + "&rows=" + rows;
        if(sortParam!=null && sortParam.length()>0){
            url += "&sort=" + sortParam;
        }
        GetMethod result = new GetMethod(url);
        return result;
    }

    static ThreadLocal<StringBuilderWriter> pingReadBuff = new ThreadLocal<StringBuilderWriter>() {
        protected StringBuilderWriter initialValue() {
            return new StringBuilderWriter(128);
        }
    }, singleValueReadBuff = new ThreadLocal<StringBuilderWriter>() {
        protected StringBuilderWriter initialValue() {
            return new StringBuilderWriter(1024);
        }
    };
    public boolean solrCheckIsUp() {
        if(solrBaseURL == null) return false;
        try {
            GetMethod g = new GetMethod(solrBaseURL + "/admin/ping");
            startSolrMethod(g);
            feedFieldFromXmlStream(g, pingReadBuff.get(), "status");
            // TODO: shorter timeout here
            String val=pingReadBuff.get().toString();
            pingReadBuff.get().getBuilder().delete(0, val.length());
            return val.trim().equals("OK");
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> solrListDocNames(String query, int start, int num) {
        if(solrBaseURL == null) throw new IllegalStateException("No SOLR configured.");
        try {
            GetMethod g = solrCreateQueryGetMethod(query, "fullname", start, num);
            startSolrMethod(g);
            List<String> fullnames = collectFieldValuesFromXmlStream(g, "fullname");
            return fullnames;
        } catch (Exception e) {
            throw new IllegalStateException("Error at SOLR: ", e);
        }
    }

    public int solrCountDocs(String query) {
        if(solrBaseURL == null) throw new IllegalStateException("No SOLR configured.");
        try {
            GetMethod g = solrCreateQueryGetMethod(query, "fullname", 0, 0);
            startSolrMethod(g);
            int count = collectDocCount(g);
            return count;
        } catch (Exception e) {
            throw new IllegalStateException("Error at SOLR: ", e);
        }
    }

    public void solrCollectResultsFromQueryWithSort(String query, String fields, String sortParam, int start, int max, SolrResultCollector collector) {
        try {
            GetMethod get = solrCreateQueryGetMethod(query, fields, sortParam, start, max);
            startSolrMethod(get);
            feedFieldFromXmlStream(get, collector, fields);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void solrCollectResultsFromQuery(String query, String fields, int start, int max, SolrResultCollector collector) {
        solrCollectResultsFromQueryWithSort(query, fields, null, start, max, collector);
    }

    public void startSolrMethod(GetMethod g) {
        try {
            int status = solrClient.executeMethod(g);
            if(status!=200) throw new IllegalStateException("Solr server responded: " + status + ":" + g.getStatusText());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<String> publicConfigNames = new TreeSet(Arrays.asList("hostname", "GA", "addthis", "standardstab", "mediahost", "globalDebug","appserverHost", "mediajwplayerkey", "geometryHome", "CDN", "CDNsemiStatic"));
    private static Map<String,String> publicConfigCache = new TreeMap<String,String>();
    private static final String MISSING = "----missing----123123";

    public String getPublicCurrikiConfig(String name, String defaultVal, XWikiContext context) {
        if(name==null || !publicConfigNames.contains(name)) throw new IllegalAccessError("Property \"" + name + "\" not allowed for read.");
        String r = publicConfigCache.get(name);
        if(r==MISSING) return defaultVal;
        if(r!=null) return r;

        if("appserverHost".equals(name)) {
            try {
                publicConfigCache.put(name, InetAddress.getLocalHost().getHostName());
                return publicConfigCache.get(name);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                publicConfigCache.put(name, MISSING);
                return defaultVal;
            }
        }
        r = context.getWiki().Param("curriki.system." + name, null);

        try {
            if(context.getWiki().exists("CurrikiConfig." + name, context)) {
                XWikiDocument doc = context.getWiki().getDocument("CurrikiConfig", name, context);
                if(!doc.isNew()) {
                    String content = doc.getContent();
                    if(content!=null) content = content.replaceAll("\\#.*(\\r|\\n)","").trim();
                    if(content!=null && content.length()>0)
                        r = content;
                }
            }
        } catch (XWikiException e) {
            e.printStackTrace();
        }


        if(r==null) r = MISSING;
        if(publicConfigCache.size()>1000) throw new IllegalStateException("Can't have more than 1000 properties for curriki.");
        publicConfigCache.put(name, r);
        if(r==MISSING) return defaultVal;
        return r;
    }

    public interface SolrResultCollector {
        /** called at beginning to indicate the first results information */
        public void status(int statusCode, int qTime, int numFound, int start);

        /** This called back by the method feedField... and should not call methods of the solr-client. */
        public void addValue(String name, String value);

        /** indicates that the following values concern a new document */
        public void newDocument();
    }


    public void feedFieldFromXmlStream(GetMethod g, final SolrResultCollector collector, final String fieldNames) throws IOException {
        try {
            // TODO: use new API completely
            SAXParserFactory.newInstance().newSAXParser().parse(g.getResponseBodyAsStream(), new DefaultHandler() {;
                boolean isInInterestingZone = false, isInInterestingArray = false, isInStatus = false;
                Set<String> names = new TreeSet(Arrays.asList(fieldNames.split(",")));
                int statusCode=-1, qTime=-1;
                String name = null;

                @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    String n=attributes.getValue("name");
                    if("doc".equals(qName)) {
                        collector.newDocument();
                    }
                    if("lst".equals(qName) && "responseHeader".equals(n)) isInStatus = true;
                    else if("result".equals(qName) && "response".equals(n)) {
                        isInStatus = false;
                        String numFound = attributes.getValue("numFound"), start = attributes.getValue("start");
                        if(numFound==null) numFound="-1"; if(start==null) start="-1";
                        collector.status(statusCode, qTime, Integer.parseInt(numFound), Integer.parseInt(start));
                    }
                    else if("str".equals(qName) || "arr".equals(qName) || "int".equals(qName) || "bool".equals(qName)) {
                        if(isInInterestingArray) isInInterestingZone = true;
                        else if(isInStatus && n!=null && ("QTime".equals(n) || "status".equals(n) )|| names.contains(n)) {
                            if("str".equals(qName) || "int".equals(qName) || "bool".equals(qName)) isInInterestingZone = true;
                            else isInInterestingArray = true;
                            name = n;
                        }
                    }
                }

                private String collectValue() {
                    String value = singleValueReadBuff.get().toString();
                    singleValueReadBuff.get().getBuilder().delete(0, value.length());
                    return value;
                }

                @Override public void endElement(String uri, String localName, String qName) throws SAXException {
                    if(isInStatus && "int".equals(qName)) {
                        if("status".equals(name)) {
                            statusCode = Integer.parseInt(collectValue());
                            isInInterestingZone = false;
                        } else if("QTime".equals(name)) {
                            statusCode = Integer.parseInt(collectValue());
                            isInInterestingZone = false;
                        }
                    }
                    if(isInInterestingZone && !isInStatus &&  (("str".equals(qName)) || "bool".equals(qName) || "int".equals(qName)) ) {
                        isInInterestingZone = false;
                        collector.addValue(name, collectValue());
                        name = null;
                    }
                    if(isInInterestingArray && "arr".equals(qName)) isInInterestingArray = false;
                    if("lst".equals(qName) && isInStatus) {
                        isInStatus = false;
                    }

                }

                @Override public void characters(char[] ch, int start, int length) throws SAXException {
                    if(isInInterestingZone) {
                        singleValueReadBuff.get().write(ch, start, length);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    public void feedFieldFromXmlStream(GetMethod g, final Writer out, final String elementName) throws IOException {
            try {
            SAXParserFactory.newInstance().newSAXParser().parse(g.getResponseBodyAsStream(), new DefaultHandler() {
                boolean isInInterestingStringBit = false, isInInterestingArray = false;
                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    try {
                        if(isInInterestingStringBit || isInInterestingArray) out.write(ch, start, length);

                    } catch (IOException e) {
                        throw new IllegalStateException("Broken stream, can't write to " + out);
                    }
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    String nameAtt = attributes.getValue("name");
                    if("str".equals(qName)) {
                        if(elementName.equals(nameAtt)) isInInterestingStringBit = true;
                    }
                    if("arr".equals(qName) && elementName.equals(nameAtt)) isInInterestingArray = true;
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if("str".equals(localName)) isInInterestingStringBit = false;
                    if(isInInterestingArray && "arr".equals(qName)) isInInterestingArray = false;
                }
            });
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private List<String> collectFieldValuesFromXmlStream(GetMethod g, final String elementName) throws IOException {
        try {
            final LinkedList<String> list = new LinkedList<String>();
            SAXParserFactory.newInstance().newSAXParser().parse(g.getResponseBodyAsStream(), new DefaultHandler() {
                boolean isInInterestingStringBit = false, isInInterestingArray = false;
                StringBuilderWriter writer = singleValueReadBuff.get();
                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    if(isInInterestingStringBit) writer.write(ch, start, length);
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    String nameAtt = attributes.getValue("name");
                    if("str".equals(qName)) {
                        if(elementName.equals(nameAtt)) isInInterestingStringBit = true;
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if("str".equals(qName)) {
                        if(isInInterestingStringBit) {
                            isInInterestingStringBit = false;
                            String value = writer.getBuilder().toString();
                            pushValue(value);
                            writer.getBuilder().delete(0, value.length());
                        }
                    }
                }

                private void pushValue(String value) {
                    list.add(value);
                }
            });
            return list;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }


    private int collectDocCount(GetMethod g) throws IOException {
        try {
            final StringBuilder b = singleValueReadBuff.get().getBuilder();
            SAXParserFactory.newInstance().newSAXParser().parse(g.getResponseBodyAsStream(), new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if("result".equals(qName)) {
                        b.append(attributes.getValue("numFound"));
                    }
                }

            });
            String result = b.toString();
            b.delete(0, result.length());
            return Integer.parseInt(result);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }


}
