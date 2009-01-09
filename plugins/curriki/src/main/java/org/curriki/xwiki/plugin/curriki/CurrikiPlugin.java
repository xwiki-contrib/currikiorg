package org.curriki.xwiki.plugin.curriki;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.curriki.plugin.spacemanager.impl.CurrikiSpaceManager;
import org.curriki.plugin.spacemanager.plugin.CurrikiSpaceManagerPluginApi;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.CollectionSpace;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.DefaultAssetManager;
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

/**
 */
public class CurrikiPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface, XWikiDocChangeNotificationInterface {
    public static final String PLUGIN_NAME = "curriki";

    private static final Log LOG = LogFactory.getLog(CurrikiPlugin.class);

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
        Map<String,Object> groups = new HashMap<String,Object>();
        CurrikiSpaceManagerPluginApi sm = (CurrikiSpaceManagerPluginApi) context.getWiki().getPluginApi(CurrikiSpaceManager.CURRIKI_SPACEMANGER_NAME, context);
        List spaces;
        try {
            spaces = sm.getSpaceNames(forUser, null);
        } catch (Exception e) {
            // Ignore exception -- just return an empty list
            LOG.error("Error getting user groups", e);
            return null;
        }

        for (Object space : spaces) {
            if (space instanceof String) {
                groups.put((String) space, getGroupInfo((String) space, context));
            }
        }

        return groups;
    }

    protected Map<String,Object> getGroupInfo(String group, XWikiContext context) {
        Map<String,Object> groupInfo = new HashMap<String,Object>();
        CurrikiSpaceManagerPluginApi sm = (CurrikiSpaceManagerPluginApi) context.getWiki().getPluginApi("csm", context);

        try {
            Space space = sm.getSpace(group);
            groupInfo.put("displayTitle", space.getDisplayTitle());
            groupInfo.put("description", space.getDescription());
            Map<String,Object> collections = fetchCollectionsInfo(group, context);
            groupInfo.put("collectionCount", collections.size());
            int editableCount = 0;
            for (String collection : collections.keySet()) {
                Map<String,Object> cInfo = (Map<String,Object>) collections.get(collection);
                Map<String,Boolean> rInfo = (Map<String,Boolean>) cInfo.get("rights");
                if (rInfo.get("edit")) {
                    editableCount++;
                }
            }
            groupInfo.put("editableCollectionCount", editableCount);
        } catch (Exception e) {
            LOG.error("Error getting group space", e);
            return null;
        }

        return groupInfo;
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
        RootCollectionCompositeAsset root = fetchRootCollection(entity, context);
        if (root == null) {
            // Ignore any error, will just return 0 results
            return new ArrayList<String>();
        }

        return root.getSubassetList();
    }

    public Map<String,Object> fetchCollectionsInfo(String entity, XWikiContext context) throws XWikiException {
        RootCollectionCompositeAsset root = fetchRootCollection(entity, context);
        if (root == null) {
            // Ignore any error, will just return 0 results
            return new HashMap<String,Object>();
        }

        return root.fetchCollectionsInfo();
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

            System.out.println("Action " + context.getAction() + " " + newdoc.getFullName());
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
}
