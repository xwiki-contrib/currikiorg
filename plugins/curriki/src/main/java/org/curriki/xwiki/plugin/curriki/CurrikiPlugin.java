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
import org.curriki.xwiki.plugin.asset.AssetException;
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
     * Get a list of all viewable collections owned by a specific user with extra info
     *
     * @param forUser Owner of collections to search for
     * @param context Standard XWikiContext object
     * @return A list of collections with information about each
     */
    public Map<String,Object> fetchUserCollectionsInfo(String forUser, XWikiContext context) {
        String shortName = forUser.replaceFirst("XWiki.", "");
        return fetchCollectionsInfo(shortName, context);
    }

    /**
     * Get a list of all viewable collections owned by a specific user
     *
     * @param forUser Owner of collections to search for
     * @param context Standard XWikiContext object
     * @return A list of collections
     */
   public List<String> fetchUserCollectionsList(String forUser, XWikiContext context) {
        String shortName = forUser.replaceFirst("XWiki.", "");
        return fetchCollectionsList(shortName, context);
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
            Map<String,Object> collections = fetchGroupCollectionsInfo(group, context);
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

    public List<String> fetchGroupCollectionsList(String forGroup, XWikiContext context) {
        String shortName = forGroup.replaceFirst("."+Constants.ROOT_COLLECTION_PAGE+"$", "");
        return fetchCollectionsList(shortName, context);
    }

    public Map<String, Object> fetchGroupCollectionsInfo(String forGroup, XWikiContext context) {
        String shortName = forGroup.replaceFirst("."+Constants.ROOT_COLLECTION_PAGE+"$", "");
        return fetchCollectionsInfo(shortName, context);
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



    protected List<String> fetchCollectionsList(String entity, XWikiContext context) {
        RootCollectionCompositeAsset root;
        try {
            root = CollectionSpace.getRootCollection("Coll_"+entity, context);
        } catch (XWikiException e) {
            // Ignore any error, will just return 0 results
            return new ArrayList<String>();
        }

        return root.fetchCollectionsList();
    }

    protected Map<String,Object> fetchCollectionsInfo(String entity, XWikiContext context) {
        RootCollectionCompositeAsset root;
        try {
            root = CollectionSpace.getRootCollection("Coll_"+entity, context);
        } catch (XWikiException e) {
            // Ignore any error, will just return 0 results
            return new HashMap<String,Object>();
        }

        return root.fetchCollectionsInfo();
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

    public String getBFCSHql(String directionOrder,boolean ordered, XWikiContext context) throws XWikiException {
    	XWikiRequest req = context.getRequest();
    	String assetFilterCreationDateFrom = req.getCookie("assetFilterCreationDateFrom")!=null?req.getCookie("assetFilterCreationDateFrom").getValue():null;
    	String assetFilterCreationDateTo = req.getCookie("assetFilterCreationDateTo")!=null?req.getCookie("assetFilterCreationDateTo").getValue():null;
    	String assetFilterFileCheckStatus = req.getCookie("assetFilterFileCheckStatus")!=null?req.getCookie("assetFilterFileCheckStatus").getValue():null;
    	String assetFilterSubjectArea = req.getCookie("assetFilterSubjectArea")!=null?req.getCookie("assetFilterSubjectArea").getValue():null;

    	String sql ="";
    	String [] excludeList = {"jmarks", "LudovicDubost", "rmlucas", "RoadofLife", "BobandGeorge", "jkl1231", "Templates", "curriki", "demo", "Entrepreneurship1", "PopTech", "NROCscience", "NROCMath", "Athabasca", "mopsyhop", "ShermanTank", "TravelsWithMusic", "Panwapa", "ALTEC", "Wyoming", "nextvista", "Norteltest", "NortelLearniTTwentyFirstCenturyeLearning", "LearniT", "TestingTodd", "cybersmart", "smarthinkingMath", "dward", "GBelvins", "msutro", "HeyMath", "passandr", "noahk", "katprince", "bobbi", "aschreiber", "rdang", "driscoll"};

    	String wheresql = "and doc.creator not in (";

    	for (int i = 0; i < excludeList.length; i++) {
    		wheresql += "'XWiki."+excludeList[i]+"'";
    		wheresql += i==excludeList.length-1?"":",";
		}
    	wheresql += ")";

    	wheresql += " and doc.fullName not in (select obj3.name from BaseObject as obj3, LongProperty as lprop3 where obj3.name = doc.fullName and obj3.id=lprop3.id.id and lprop3.id.name='type' and lprop3.value=2)";

    	if(assetFilterCreationDateFrom!=null && !assetFilterCreationDateFrom.equals("") && !assetFilterCreationDateFrom.equals("MM/DD/YYYY")){
        	String date = changeFormatDate(assetFilterCreationDateFrom,"MM/DD/YYYY","YYYY/MM/DD","/");
    		wheresql += " and doc.creationDate >= '"+date+"'";
    	}

    	if(assetFilterCreationDateTo!=null && !assetFilterCreationDateTo.equals("") && assetFilterCreationDateTo.equals("MM/DD/YYYY")){
    		String date = changeFormatDate(assetFilterCreationDateTo,"MM/DD/YYYY","YYYY/MM/DD","/");
    		wheresql += " and doc.creationDate <= '"+date+"'";
    	}

    	String fromsql = " ,StringProperty as sprop";

    	if(assetFilterFileCheckStatus!=null){
	    	if(assetFilterFileCheckStatus.equals("1")){//without status
	    		wheresql += " and obj.id not in (select obj2.id from BaseObject as obj2, StringProperty as sprop2 where obj2.className='XWiki.AssetClass' and obj2.id=sprop2.id.id and sprop2.id.name='fcstatus' and sprop2.value is not null)";
	    	}else if(!assetFilterFileCheckStatus.equals("1") && !assetFilterFileCheckStatus.equals("0")){//with status
	    				wheresql += " and obj.id=sprop.id.id and sprop.id.name='fcstatus' and sprop.value is not null ";
	    				if(assetFilterFileCheckStatus.equals("2"))//with any status
	    					wheresql += " and sprop.value <> '0'";
	    				if(assetFilterFileCheckStatus.equals("3"))//with status ok
	    					wheresql += " and sprop.value = '1'";
	    				if(assetFilterFileCheckStatus.equals("4"))//with status Special Check Required
	    					wheresql += " and sprop.value = '2'";
	    				if(assetFilterFileCheckStatus.equals("5"))//with status Improvement Requested
	    					wheresql += " and sprop.value = '3'";
	    				if(assetFilterFileCheckStatus.equals("6"))//with status Deleted
	    					wheresql += " and sprop.value = '4'";
	    			}
    	}

    	String order="";
    	if(ordered){
	    	order="	order by ";
	    	if(req.get("order")!=null){
		    	if(req.get("order").equals("fcstatus")){
		    		if(assetFilterFileCheckStatus!=null && !assetFilterFileCheckStatus.equals("2")){
		    			wheresql += " and obj.id=sprop.id.id and sprop.id.name='fcstatus'";
		    		}
		    		order += "sprop.value "+directionOrder;
		    		}else if(req.get("order").equals("fcdate")){
		    			fromsql += " ,DateProperty as dprop";
		    			wheresql += " and obj.id=dprop.id.id and dprop.id.name='fcdate'";
		    			order += "dprop.value "+directionOrder;
		    		}else if(req.get("order").equals("contributor")){
								fromsql += " ,BaseObject as userObj,StringProperty as userSprop";
								wheresql += " and doc.creator=userObj.name and userObj.className='XWiki.XWikiUsers' and userObj.id=userSprop.id.id and userSprop.id.name='first_name'";
								order += "userSprop.value "+directionOrder;
		    					}
								else if(req.get("order").equals("resourcetitle")){
										fromsql += " ,StringProperty as dprop";
										wheresql += " and obj.id=dprop.id.id and dprop.id.name='title'";
										order += "dprop.value "+directionOrder;
									}

	    	}else
	    		order += "doc.creationDate desc";
    	}

    	// Check which Subject was selected in the filter combo
    	if((assetFilterSubjectArea!=null)&&(!assetFilterSubjectArea.equals(""))){
    		fromsql += " ,DBStringListProperty as prop2 join prop2.list list ";
    		wheresql += "  and obj.id=prop2.id.id and prop2.id.name='fw_items' and list = '"+assetFilterSubjectArea+"'";
    	}

    	// Filter for excluding Favorites Collections
    	String notFavoritesFoldersSQL = " and doc.name != 'Favorites' ";

    	sql += ", BaseObject as obj "+fromsql+" where doc.web like 'Coll_%' and doc.fullName=obj.name and obj.className='XWiki.AssetClass' "+notFavoritesFoldersSQL+" "+wheresql+" "+order;


    	return sql;

    }

    public Map getSeeCountsByStatus(XWikiContext context) throws XWikiException {
    	// Add the first part of the query for getting the number of docs with each status
    	String sql = getBFCSHql("",false,context);
    	String hqlPart1 = "select sprop.value, count(doc.id) from XWikiDocument as doc "+sql+"  and obj.id=sprop.id.id and sprop.id.name='fcstatus' group by sprop.value";
    	// Add the second part of the query for getting the number of docs without status
    	String hqlPart2 = "select '0', count(distinct doc.id) from XWikiDocument as doc "+sql+" and obj.id not in (select obj2.id from BaseObject as obj2, StringProperty as sprop2 where obj2.className='XWiki.AssetClass' and obj2.id=sprop2.id.id and sprop2.id.name='fcstatus' and sprop2.value is not null or sprop.value = '0')";

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
