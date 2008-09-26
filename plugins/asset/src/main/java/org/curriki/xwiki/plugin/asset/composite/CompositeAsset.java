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

import java.util.*;

/**
 */
abstract class CompositeAsset extends Asset {
    private static final Log LOG = LogFactory.getLog(RootCollectionCompositeAsset.class);

    public CompositeAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    protected void initSubType() throws XWikiException {
        super.initSubType();
        BaseObject obj = doc.newObject(Constants.COMPOSITE_ASSET_CLASS, context);
        obj.setStringValue(Constants.COMPOSITE_ASSET_CLASS_TYPE, compositeAssetType());

        obj = doc.getObject(Constants.ASSET_CLASS);
        obj.setStringValue(Constants.ASSET_CLASS_CATEGORY, Constants.CATEGORY_COLLECTION);

        setDefaultContent();
    }

    protected void setDefaultContent() throws XWikiException {
        assertCanEdit();
        doc.setContent(Constants.COMPOSITE_ASSET_COMPOSITE_CONTENT);
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
        docInfo.put("assetType", determineAssetSubtype().getSimpleName().replaceAll("Asset$", ""));

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
        List objs = doc.getObjects(Constants.SUBASSET_CLASS);

        if (objs != null ) {
            List<Map<String,Object>> subList = new ArrayList<Map<String,Object>>(objs.size());

            for (Object obj : objs) {
                if (obj instanceof BaseObject) {
                    BaseObject xObj = (BaseObject) obj;

                    String subPage = xObj.getStringValue(Constants.SUBASSET_CLASS_PAGE);

                    Map<String,Object> subInfo = new HashMap<String, Object>(6);
                    subInfo.put(Constants.SUBASSET_CLASS_PAGE, subPage);
                    subInfo.put(Constants.SUBASSET_CLASS_ORDER, xObj.getLongValue(Constants.SUBASSET_CLASS_ORDER));

                    com.xpn.xwiki.api.XWiki xwikiApi = new com.xpn.xwiki.api.XWiki(context.getWiki(), context);
                    try {
                        Document doc = xwikiApi.getDocument(subPage);
                        if (doc instanceof Asset) {
                            subInfo.put("displayTitle", doc.getDisplayTitle());
                            subInfo.put("description", ((Asset) doc).getDescription());
                            subInfo.put("assetType", ((Asset) doc).determineAssetSubtype().getSimpleName().replaceAll("Asset$", ""));
                            subInfo.put("rights", ((Asset) doc).getRightsList());
                        } else if (doc == null) {
                            // getDocument returns null if the page is not viewable by the user
                            subInfo.put("displayTitle", "");
                            subInfo.put("description", "");
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
        if (objs != null) {
            Collections.sort(objs, new Comparator<BaseObject>(){
                public int compare(BaseObject s1, BaseObject s2){
                    Long c1 = s1.getLongValue(Constants.SUBASSET_CLASS_ORDER);
                    Long c2 = s2.getLongValue(Constants.SUBASSET_CLASS_ORDER);
                    return (c1.compareTo(c2));
                }
            });

            List<String> list = new ArrayList<String>();
            for (Object obj: objs){
                list.add(((BaseObject) obj).getStringValue(Constants.SUBASSET_CLASS_PAGE));
            }

            return filterViewablePages(list);
        }

        return new ArrayList<String>();
    }

    public Map<String,Object> getSubassetInfo(long subassetId) throws AssetException {
        List objs = doc.getObjects(Constants.SUBASSET_CLASS);
        Map<String,Object> subInfo = new HashMap<String, Object>(5);

        if (objs != null) {
            for (Object obj : objs){
                if (obj instanceof BaseObject) {
                    BaseObject xObj = (BaseObject) obj;

                    String subPage = xObj.getStringValue(Constants.SUBASSET_CLASS_PAGE);

                    Long order = xObj.getLongValue(Constants.SUBASSET_CLASS_ORDER);
                    if (order.equals(subassetId)) {
                        subInfo.put(Constants.SUBASSET_CLASS_PAGE, subPage);
                        subInfo.put(Constants.SUBASSET_CLASS_ORDER, order);

                        com.xpn.xwiki.api.XWiki xwikiApi = new com.xpn.xwiki.api.XWiki(context.getWiki(), context);
                        try {
                            Document doc = xwikiApi.getDocument(subPage);
                            if (doc instanceof Asset) {
                                subInfo.put("displayTitle", doc.getDisplayTitle());
                                subInfo.put("description", ((Asset) doc).getDescription());
                                subInfo.put("assetType", ((Asset) doc).determineAssetSubtype().getSimpleName().replaceAll("Asset$", ""));
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

        List objs = doc.getObjects(Constants.SUBASSET_CLASS);
        Long beforePosition = null;
        if (objs != null) {
            for (Object obj : objs) {
                if (obj instanceof BaseObject) {
                    BaseObject xObj = (BaseObject) obj;
                    String objName = xObj.getStringValue(Constants.SUBASSET_CLASS_PAGE);
                    if (objName.equals(beforePage)){
                        beforePosition = xObj.getLongValue(Constants.SUBASSET_CLASS_ORDER);
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
        List objs = doc.getObjects(Constants.SUBASSET_CLASS);
        if (objs == null) {
            return ;
        }
        for (Object obj : objs) {
            if (obj instanceof BaseObject) {
                BaseObject xObj = (BaseObject) obj;
                long objPos = xObj.getLongValue(Constants.SUBASSET_CLASS_ORDER);
                if (objPos >= freePosition) {
                    xObj.setLongValue(Constants.SUBASSET_CLASS_ORDER, objPos + 1);
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
                throw new AssetException("Original list does not match current list");
            }
            ++i;
        }

        // Delete all subassets
        XWikiDocument assetDoc = getDoc();
        assetDoc.removeObjects(Constants.SUBASSET_CLASS);

        // Add all from want
        i = 0;
        for (String page : want){
            BaseObject sub = assetDoc.newObject(Constants.SUBASSET_CLASS, context);
            sub.setStringValue(Constants.SUBASSET_CLASS_PAGE, page);
            sub.setLongValue(Constants.SUBASSET_CLASS_ORDER, i);
            ++i;
        }
    }

    protected long getLastPosition() {
        List objs = doc.getObjects(Constants.SUBASSET_CLASS);
        long highestOrder = (long) -1;
        if (objs != null) {
            for (Object obj : objs) {
                if (obj instanceof BaseObject) {
                    BaseObject xObj = (BaseObject) obj;
                    long objOrder = xObj.getLongValue(Constants.SUBASSET_CLASS_ORDER);
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
}
