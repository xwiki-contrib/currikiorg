package org.curriki.xwiki.plugin.asset.composite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiMessageTool;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.AssetException;
import org.curriki.xwiki.plugin.asset.other.ProtectedAsset;
import org.curriki.xwiki.plugin.asset.other.InvalidAsset;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

/**
 */
public abstract class CompositeAsset extends Asset {
    private static final Log LOG = LogFactory.getLog(RootCollectionCompositeAsset.class);

    public final static String CATEGORY_NAME = Constants.ASSET_CATEGORY_COLLECTION;

    public CompositeAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    protected void initSubType() throws XWikiException {
        super.initSubType();
        BaseObject obj = doc.newObject(Constants.COMPOSITE_ASSET_CLASS, context);
        obj.setStringValue(Constants.COMPOSITE_ASSET_CLASS_TYPE, compositeAssetType());

        determineCategory();

        setDefaultContent();
    }

    protected void setDefaultContent() throws XWikiException {
        assertCanEdit();
        doc.setContent("");
    }

    abstract protected String compositeAssetType();

    public Map<String,Object> getCompositeInfo() {
        Map<String,Object> docInfo = new HashMap<String, Object>();

        // displayTitle
        docInfo.put("displayTitle", getDisplayTitle());

        // description
        docInfo.put("description", getDescription());

        // collection type
        use(Constants.COMPOSITE_ASSET_CLASS);
        docInfo.put("collectionType", getValue(Constants.COMPOSITE_ASSET_CLASS_TYPE));
        docInfo.put("assetType", getAssetClass().getSimpleName().replaceAll("Asset$", ""));

        // access rights
        docInfo.put("rights", getRightsList());

        // Children
        List<Map<String,Object>> subList = getSubassetsInfo();
        if (subList.size() > 0) {
            docInfo.put("children", subList);
        }

        return docInfo;
    }

    public List<Map<String, Object>> getSubassetsInfo() {
        List<BaseObject> objs = doc.getObjects(Constants.SUBASSET_CLASS);

        if (objs != null ) {
            List<Map<String,Object>> subList = new ArrayList<Map<String,Object>>(objs.size());

            for (BaseObject obj : objs) {
                if (obj != null) {
                    String subPage = obj.getStringValue(Constants.SUBASSET_CLASS_PAGE);

                    Map<String,Object> subInfo = new HashMap<String, Object>(6);
                    subInfo.put(Constants.SUBASSET_CLASS_PAGE, subPage);
                    subInfo.put(Constants.SUBASSET_CLASS_ORDER, obj.getLongValue(Constants.SUBASSET_CLASS_ORDER));

                    com.xpn.xwiki.api.XWiki xwikiApi = new com.xpn.xwiki.api.XWiki(context.getWiki(), context);
                    try {
                        Document doc = xwikiApi.getDocument(subPage);
                        if (doc instanceof Asset) {
                            subInfo.put("displayTitle", doc.getDisplayTitle());
                            subInfo.put("description", ((Asset) doc).getDescription());
                            subInfo.put("fwItems", ((Asset) doc).getValue(Constants.ASSET_CLASS_FRAMEWORK_ITEMS));
                            subInfo.put("levels", ((Asset) doc).getValue(Constants.ASSET_CLASS_EDUCATIONAL_LEVEL));
                            subInfo.put("assetType", ((Asset) doc).getAssetClass().getSimpleName().replaceAll("Asset$", ""));
                            subInfo.put("rights", ((Asset) doc).getRightsList());
                        } else if (doc == null) {
                            // getDocument returns null if the page is not viewable by the user
                            subInfo.put("displayTitle", "");
                            subInfo.put("description", "");
                            subInfo.put("fwItems", new String[]{});
                            subInfo.put("levels", new String[]{});
                            subInfo.put("assetType", ProtectedAsset.class.getSimpleName().replaceAll("Asset$", ""));

                            Map<String,Boolean> rightsInfo = new HashMap<String, Boolean>();
                            rightsInfo.put("view", false);
                            rightsInfo.put("edit", false);
                            rightsInfo.put("delete", false);
                            subInfo.put("rights", rightsInfo);
                        }
                    } catch (Exception e) {
                        subInfo.put("displayTitle", "");
                        subInfo.put("description", "");
                        subInfo.put("fwItems", new String[]{});
                        subInfo.put("levels", new String[]{});
                        subInfo.put("assetType", InvalidAsset.class.getSimpleName().replaceAll("Asset$", ""));

                        Map<String,Boolean> rightsInfo = new HashMap<String, Boolean>();
                        rightsInfo.put("view", false);
                        rightsInfo.put("edit", false);
                        rightsInfo.put("delete", false);
                        subInfo.put("rights", rightsInfo);
                    }

                    subList.add(subInfo);
                }
            }

            Collections.sort(subList, new Comparator<Map<String,Object>>(){
                public int compare(Map<String,Object> s1, Map<String,Object> s2){
                    return ((Long) s1.get(Constants.SUBASSET_CLASS_ORDER)).compareTo((Long) s2.get(Constants.SUBASSET_CLASS_ORDER));
                }
            });

            return subList;
        }

        return new ArrayList<Map<String,Object>>(1);
    }

    public List<String> getSubassetList() {
        List<BaseObject> objs = doc.getObjects(Constants.SUBASSET_CLASS);
        if (objs != null && objs.size() > 0) {
            objs = sortSubassetList(objs);

            List<String> list = new ArrayList<String>();
            for (BaseObject obj : objs){
                if (obj != null) {
                    list.add(obj.getStringValue(Constants.SUBASSET_CLASS_PAGE));
                }
            }

            return filterViewablePages(list);
        }

        return new ArrayList<String>();
    }

    protected List<BaseObject> sortSubassetList(List<BaseObject> list) {
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<BaseObject>(){
                public int compare(BaseObject s1, BaseObject s2){
                    if (s1 == null) {
                        return s2 == null ? 0 : -1;
                    } else if (s2 == null) {
                        return 1;
                    }
                    Long c1 = s1.getLongValue(Constants.SUBASSET_CLASS_ORDER);
                    Long c2 = s2.getLongValue(Constants.SUBASSET_CLASS_ORDER);
                    if (c1 == null) {
                        return c2 == null ? 0 : -1;
                    } else if (c2 == null) {
                        return 1;
                    }
                    return (c1.compareTo(c2));
                }
            });
        }

        return list;
    }

    public Map<String,Object> getSubassetInfo(long subassetId) throws AssetException {
        List<BaseObject> objs = doc.getObjects(Constants.SUBASSET_CLASS);
        Map<String,Object> subInfo = new HashMap<String, Object>(5);

        if (objs != null) {
            for (BaseObject obj : objs){
                if (obj != null) {
                    String subPage = obj.getStringValue(Constants.SUBASSET_CLASS_PAGE);

                    Long order = obj.getLongValue(Constants.SUBASSET_CLASS_ORDER);
                    if (order.equals(subassetId)) {
                        subInfo.put(Constants.SUBASSET_CLASS_PAGE, subPage);
                        subInfo.put(Constants.SUBASSET_CLASS_ORDER, order);

                        com.xpn.xwiki.api.XWiki xwikiApi = new com.xpn.xwiki.api.XWiki(context.getWiki(), context);
                        try {
                            Document doc = xwikiApi.getDocument(subPage);
                            if (doc instanceof Asset) {
                                subInfo.put("displayTitle", doc.getDisplayTitle());
                                subInfo.put("description", ((Asset) doc).getDescription());
                                subInfo.put("assetType", ((Asset) doc).getAssetClass().getSimpleName().replaceAll("Asset$", ""));
                            } else {
                                subInfo.put("error", "Subasset does not exist");
                            }
                        } catch (Exception e) {
                            subInfo.put("error", "Subasset does not exist");
                        }

                        return subInfo;
                    }
                }
            }
        }

        throw new AssetException(AssetException.ERROR_ASSET_SUBASSET_NOTFOUND, "No subasset exists with the order number "+subassetId);
    }

    public long insertSubassetBefore(String page, String beforePage) throws XWikiException {
        if (beforePage == null){
            return addSubasset(page);
        }

        List<BaseObject> objs = doc.getObjects(Constants.SUBASSET_CLASS);
        Long beforePosition = null;
        if (objs != null) {
            for (BaseObject obj : objs) {
                if (obj != null) {
                    String objName = obj.getStringValue(Constants.SUBASSET_CLASS_PAGE);
                    if (objName.equals(beforePage)){
                        beforePosition = obj.getLongValue(Constants.SUBASSET_CLASS_ORDER);
                    }
                }
            }
        }

        return insertSubassetAt(page, beforePosition);
    }

    public long insertSubassetAt(String page, Long atPosition) throws XWikiException {
        if (atPosition == null || atPosition == -1){
            return addSubasset(page);
        }

        relocateAssets(atPosition);

        createSubasset(page, atPosition);

        return atPosition;
    }

    protected void relocateAssets(long freePosition) throws XWikiException {
        List<BaseObject> objs = doc.getObjects(Constants.SUBASSET_CLASS);
        if (objs == null) {
            return ;
        }
        for (BaseObject obj : objs) {
            if (obj != null) {
                long objPos = obj.getLongValue(Constants.SUBASSET_CLASS_ORDER);
                if (objPos >= freePosition) {
                    obj.setLongValue(Constants.SUBASSET_CLASS_ORDER, objPos + 1);
                }
            }
        }
    }

    public long addSubasset(String page) throws XWikiException {
        Long highestOrder = getLastPosition() + 1;

        createSubasset(page, highestOrder);

        return highestOrder;
    }

    public void createSubasset(String page, Long position) throws XWikiException {
        com.xpn.xwiki.api.XWiki xwikiApi = new com.xpn.xwiki.api.XWiki(context.getWiki(), context);
        try {
            Document subAsset = xwikiApi.getDocument(page);
            if (subAsset instanceof Asset) {
                // Do not allow ancestor to be added as a sub-asset
                boolean done = false;
                List<String> searchFor = new ArrayList<String>();
                searchFor.add(doc.getFullName());

                while (!done){
                    String sql = null;
                    for (String item : searchFor) {
                        if (item.equals(page)) {
                            XWikiMessageTool msg = (XWikiMessageTool) context.get("msg");
                            throw new AssetException(AssetException.ERROR_ASSET_SUBASSET_RECURSION, msg.get("addsubasset.recursive_add_message"));
                        }
                        if (sql != null) {
                            sql = sql + ", '" + item + "'";
                        } else {
                            sql = "'" + item + "'";
                        }
                    }

                    sql = ", BaseObject as obj, StringProperty as prop where obj.name=doc.fullName and obj.className='XWiki.SubAssetClass' and prop.id.id = obj.id and prop.name='assetpage' and prop.value in (" + sql + ")";
                    List<String> list = context.getWiki().getStore().searchDocumentsNames(sql, context);
                    if ((list==null)||(list.size()==0)){
                        done = true;
                    } else {
                        searchFor = list;
                    }
                }

                // Is not being added to itself
                BaseObject obj = doc.newObject(Constants.SUBASSET_CLASS, context);
                obj.setStringValue(Constants.SUBASSET_CLASS_PAGE, subAsset.getFullName());
                obj.setLongValue(Constants.SUBASSET_CLASS_ORDER, position);
            } else {
                throw new AssetException(AssetException.ERROR_ASSET_SUBASSET_NOTFOUND, "Subasset to add does not exist");
            }
        } catch (Exception e) {
            throw new AssetException(AssetException.ERROR_ASSET_SUBASSET_NOTFOUND, "Subasset to add does not exist");
        }
    }

    public void reorder(List<String> orig, List<String> want) throws XWikiException {
        List<String> cur = getSubassetList();

        // Check that the original list matches the current list
        int i = 0;
        for (String page : cur){
            if (!page.equals(orig.get(i))){
                throw new AssetException(AssetException.ERROR_ASSET_REORDER_NOTMATCH, "Original list does not match current list");
            }
            ++i;
        }

        // This may seem a bit convoluted, but a simple delete all subassets and add new list seems to sometimes get a
        // "Error number 3211 in 3: Exception while saving object <pagename>
        //  deleted instance passed to update(): [com.xpn.xwiki.objects.BaseObject#<null>]"
        // exception from hibernate.

        XWikiDocument assetDoc = getDoc();

        List<BaseObject> existing = assetDoc.getObjects(Constants.SUBASSET_CLASS);
        for (BaseObject b : existing) {
            if (b != null) {
                b.setLongValue(Constants.SUBASSET_CLASS_ORDER, -2);
            }
        }

        long j = 0;
        for (String page : want) {
            // Set order for exisiting item or add a new item
            boolean found = false;
            for (BaseObject b : existing) {
                if (b != null && b.getStringValue(Constants.SUBASSET_CLASS_PAGE).equals(page) && b.getLongValue(Constants.SUBASSET_CLASS_ORDER) == -2) {
                    // Put in new order location
                    b.setLongValue(Constants.SUBASSET_CLASS_ORDER, j);
                    j += 1;
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Add as it doesn't already exist in the list
                BaseObject sub = assetDoc.newObject(Constants.SUBASSET_CLASS, context);
                sub.setStringValue(Constants.SUBASSET_CLASS_PAGE, page);
                sub.setLongValue(Constants.SUBASSET_CLASS_ORDER, j);
                j += 1;
            }
        }

        // Now remove anything left
        /*
        List removeList = new ArrayList();
        for (Iterator<BaseObject> ei = existing.iterator(); ei.hasNext(); ) {
            BaseObject b = ei.next();
            if (b != null && b.getLongValue(Constants.SUBASSET_CLASS_ORDER) == -2) {
                removeList.add(b);
            }
        }
        for (Iterator<BaseObject> eid = removeList.iterator(); eid.hasNext(); ) {
            assetDoc.removeObject(eid.next());
        }
        */

    }

    protected long getLastPosition() {
        List<BaseObject> objs = doc.getObjects(Constants.SUBASSET_CLASS);
        long highestOrder = (long) -1;
        if (objs != null) {
            for (BaseObject obj : objs) {
                if (obj != null) {
                    long objOrder = obj.getLongValue(Constants.SUBASSET_CLASS_ORDER);
                    if (objOrder > highestOrder) {
                        highestOrder = objOrder;
                    }
                }
            }
        }

        return highestOrder;
    }

    protected List<String> filterViewablePages(List<String> pageList) {
        List<String> results = new ArrayList<String>();

        if (pageList!=null) {
            String favorites = getSpace()+"."+ Constants.FAVORITES_COLLECTION_PAGE;
            for (String page : pageList) {
                try {
                    if (context.getWiki().getRightService().hasAccessLevel("view", context.getUser(), page, context) && context.getWiki().exists(page, context) && !favorites.equals(page)) {
                        results.add(page);
                    }
                } catch (XWikiException e) {
                    // Ignore exception -- just don't add to result list
                    LOG.error("Error filtering collections", e);
                }
            }
        }

        return results;
    }

    protected void determineCategory() throws XWikiException {
        BaseObject obj = doc.getObject(Constants.ASSET_CLASS);
        if (obj != null) {
            obj.setStringValue(Constants.ASSET_CLASS_CATEGORY, CATEGORY_NAME);
        }
    }
}
