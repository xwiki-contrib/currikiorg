package org.curriki.xwiki.plugin.curriki;

import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import com.xpn.xwiki.plugin.spacemanager.api.Space;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.api.Property;
import com.xpn.xwiki.doc.XWikiDocument;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.composite.CollectionCompositeAsset;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.AssetException;
import org.curriki.xwiki.plugin.asset.CollectionSpace;
import org.curriki.plugin.spacemanager.plugin.CurrikiSpaceManagerPluginApi;
import org.curriki.plugin.spacemanager.impl.CurrikiSpaceManager;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.lang.Object;
import java.lang.Class;

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
        com.xpn.xwiki.api.XWiki xwikiApi = new com.xpn.xwiki.api.XWiki(context.getWiki(), context);
        Document doc = xwikiApi.getDocument(assetName);
        if (doc instanceof Asset) {
            return ((Asset) doc).as(classType);
        }

        throw new AssetException("Asset "+assetName+" could not be found");
    }

    /**
     * Get a list of all viewable collections owned by a specific user with extra info
     *
     * @param forUser Owner of collections to search for
     * @param context Standard XWikiContext object
     * @return A list of collections with information about each
     */
    public Map<String,Object> fetchUserCollectionsInfo(String forUser, XWikiContext context) {
        Map<String,Object> colInfo = new HashMap<String,Object>();
        List<String> collections = fetchUserCollectionsList(forUser, context);

        for (String collection : collections) {
            try {
                Asset doc = fetchAsset(collection, context);
                if (doc != null) {
                    CollectionCompositeAsset cAsset = doc.as(CollectionCompositeAsset.class);
                    colInfo.put(collection, cAsset.getCollectionInfo());
                }
            } catch (Exception e) {
                // If we can't get the document then skip it
                LOG.error("Error fetching document", e);
            }
        }

        return colInfo;
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

        try {
            CollectionSpace.ensureExists("Coll_"+shortName, context);
        } catch (XWikiException e) {
            // Ignore any error, will just return 0 results
        }

        String qry = ", BaseObject as obj, StringProperty as props "
            + "where obj.id=props.id.id and doc.fullName=obj.name "
            + "and obj.className='"+ Constants.COMPOSITE_ASSET_CLASS+"' "
            + "and doc.creator=? "
            + "and doc.web=? "
            + "and doc.name != '"+ Constants.FAVORITES_COLLECTION_PAGE+"' "
            + "and doc.name != '"+ Constants.ROOT_COLLECTION_PAGE+"' "
            + "and props.id.name='"+ Constants.COMPOSITE_ASSET_CLASS_TYPE+"' "
            + "and props.value='"+ Constants.COMPOSITE_ASSET_CLASS_TYPE_COLLECTION+"' "
            + "order by doc.name";

        List<String> params = new ArrayList<String>();
        params.add(forUser);
        params.add("Coll_"+shortName);

        return fetchCollectionsList(qry, params, context);
    }

    protected List<String> filterViewablePages(List<String> pageList, XWikiContext context) {
        List<String> results = new ArrayList<String>();

        if (pageList!=null) {
            for (Object page : pageList) {
                try {
                    if (context.getWiki().getRightService().hasAccessLevel("view", context.getUser(), (String) page, context)) {
                        results.add((String) page);
                    }
                } catch (XWikiException e) {
                    // Ignore exception -- just don't add to result list
                    LOG.error("Error filtering collections", e);
                }
            }
        }

        return results;
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
        String shortName = forGroup.replaceFirst(".WebHome$", "");

        try {
            CollectionSpace.ensureExists("Coll_"+shortName, context);
        } catch (XWikiException e) {
            // Ignore any error, will just return 0 results
        }

        String qry = ", BaseObject as obj, StringProperty as props "
            + "where obj.id=props.id.id and doc.fullName=obj.name "
            + "and obj.className='"+ Constants.COMPOSITE_ASSET_CLASS+"' "
            + "and doc.web=? "
            + "and doc.name != '"+ Constants.ROOT_COLLECTION_PAGE+"' "
            + "and props.id.name='"+ Constants.COMPOSITE_ASSET_CLASS_TYPE+"' "
            + "and props.value='"+ Constants.COMPOSITE_ASSET_CLASS_TYPE_COLLECTION+"' "
            + "order by doc.date desc";

        List<String> params = new ArrayList<String>();
        params.add("Coll_"+shortName);

        return fetchCollectionsList(qry, params, context);
    }

    public Map<String, Object> fetchGroupCollectionsInfo(String forGroup, XWikiContext context) {
        Map<String,Object> colInfo = new HashMap<String,Object>();
        List<String> collections = fetchGroupCollectionsList(forGroup, context);

        for (String collection : collections) {
            try {
                Asset doc = fetchAsset(collection, context);
                if (doc != null) {
                    if (doc.isCollection()) {
                        CollectionCompositeAsset cAsset = doc.as(CollectionCompositeAsset.class);
                        colInfo.put(collection,  cAsset.getCollectionInfo());
                    }
                }
            } catch (Exception e) {
                // If we can't get the document then skip it
                LOG.error("Error fetching document", e);
            }
        }

        return colInfo;
    }

    protected List<String> fetchCollectionsList(String qry, XWikiContext context) {
        return fetchCollectionsList(qry, null, context);
    }

    protected List<String> fetchCollectionsList(String qry, List params, XWikiContext context) {
        List<String> results = new ArrayList<String>();
        try {
            List list = context.getWiki().getStore().searchDocumentsNames(qry, 0, 0, params, context);

            results = filterViewablePages(list, context);
        } catch (Exception e) {
            // Ignore exception, but will end up returning empty list
            LOG.error("Error fetching collections", e);
        }

        return results;
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
     * Verificate if a user is in Group
     * @param groupName
     * @param context
     * @return
     * @throws XWikiException
     */
    public Boolean isMember(String groupName,XWikiContext context) throws XWikiException
    { 
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

}
