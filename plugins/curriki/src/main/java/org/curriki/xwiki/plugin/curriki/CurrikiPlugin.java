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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.curriki.plugin.spacemanager.impl.CurrikiSpaceManager;
import org.curriki.plugin.spacemanager.plugin.CurrikiSpaceManagerPluginApi;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.CollectionSpace;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.composite.RootCollectionCompositeAsset;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.api.Property;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import com.xpn.xwiki.plugin.spacemanager.api.Space;
import com.xpn.xwiki.web.XWikiRequest;

/**
 */
public class CurrikiPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface {
    public static final String PLUGIN_NAME = "curriki";

    private static final Log LOG = LogFactory.getLog(CurrikiPlugin.class);

    public CurrikiPlugin(String name, String className, XWikiContext context) {
        super(name, className, context);
        init(context);
        LOG.debug("Curriki plugin constructed");
    }

    @Override public void init(XWikiContext context) {
        super.init(context);
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



    public List<String> fetchCollectionsList(String entity, XWikiContext context) {
        RootCollectionCompositeAsset root = fetchRootCollection(entity, context);
        if (root == null) {
            // Ignore any error, will just return 0 results
            return new ArrayList<String>();
        }

        return root.fetchCollectionsList();
    }

    public Map<String,Object> fetchCollectionsInfo(String entity, XWikiContext context) {
        RootCollectionCompositeAsset root = fetchRootCollection(entity, context);
        if (root == null) {
            // Ignore any error, will just return 0 results
            return new HashMap<String,Object>();
        }

        return root.fetchCollectionsInfo();
    }

    public RootCollectionCompositeAsset fetchRootCollection(String entity, XWikiContext context) {
        entity = entity.replaceFirst("XWiki.", ""); // For users
        entity = entity.replaceFirst("."+Constants.ROOT_COLLECTION_PAGE+"$", ""); // For groups

        RootCollectionCompositeAsset root = null;
        try {
            root = CollectionSpace.getRootCollection("Coll_"+entity, context);
        } catch (XWikiException e) {
            // Ignore any error, will just return null value
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
       Asset asset = fetchAssetAs(assetName, null, context);
       String ictStr = "";
       List<String> listICT = new ArrayList<String>();

       if (asset.getObject(Constants.ASSET_CLASS) != null){
    	   asset.use(Constants.ASSET_CLASS);
           if (asset.get(Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY) != null){
        	   ictStr = (String)asset.get(Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY);
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
    }

    public String formatDate(Date date,String pattern)
    {
    	if (date!=null && date instanceof Date)
			try {
				return (new SimpleDateFormat(pattern)).format(date);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
    	auxHqlPart1+=auxHqlPart1+sql;
    	String hqlPart1 = "select sprop.value, count(doc.id) from XWikiDocument as doc "+auxHqlPart1+"  and obj.id=sprop.id.id and sprop.id.name='fcstatus' group by sprop.value";
    	// Add the second part of the query for getting the number of docs without status
    	String hqlPart2 = "select '0', count(distinct doc.id) from XWikiDocument as doc "+sql+" and obj.id not in (select obj2.id from BaseObject as obj2, StringProperty as sprop2 where obj2.className='XWiki.AssetClass' and obj2.id=sprop2.id.id and sprop2.id.name='fcstatus' and (sprop2.value is not null or sprop2.value = '0'))";

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
}
