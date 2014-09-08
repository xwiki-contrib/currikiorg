package org.curriki.xwiki.plugin.asset.composite;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

/**
 */
public abstract class CompositeAsset extends Asset {
    private static final Logger LOG = LoggerFactory.getLogger(RootCollectionCompositeAsset.class);

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

    @Override
    public String getCategorySubtype() {
        return compositeAssetType();
    }

    abstract protected String compositeAssetType();

    public Map<String,Object> getCompositeInfo() {
        Map<String,Object> docInfo = new HashMap<String, Object>();

        addSubinfo(docInfo, this);

        // Children
        List<Map<String,Object>> subList = getSubassetsInfo();
        if (subList.size() > 0) {
            docInfo.put("children", subList);
        }

        return docInfo;
    }

    protected Map<String,Object> addEmptySubinfo(Map<String,Object> subInfo, String assetType) {
        subInfo.put("displayTitle", "");
        subInfo.put("description", "");
        subInfo.put("revision", "");
        subInfo.put("fwItems", new String[]{});
        subInfo.put("levels", new String[]{});
        subInfo.put("category", "");
        subInfo.put("subcategory", "");
        subInfo.put("ict", "");
        subInfo.put("assetType", assetType);

        Map<String,Boolean> rightsInfo = new HashMap<String, Boolean>(3);
        rightsInfo.put("view", false);
        rightsInfo.put("edit", false);
        rightsInfo.put("delete", false);
        subInfo.put("rights", rightsInfo);

        return subInfo;
    }

    protected Map<String,Object> addSubinfo(Map<String,Object> subInfo, Document doc) {
        if (doc instanceof Asset) {
            Asset aDoc = (Asset) doc;
            subInfo.put("displayTitle", aDoc.getDisplayTitle());
            subInfo.put("description", aDoc.getDescription());
            subInfo.put("revision", aDoc.getVersion());
            subInfo.put("fwItems", aDoc.getValue(Constants.ASSET_CLASS_FRAMEWORK_ITEMS));
            subInfo.put("levels", aDoc.getValue(Constants.ASSET_CLASS_EDUCATIONAL_LEVEL));
            subInfo.put("category", aDoc.getCategory());
            subInfo.put("subcategory", aDoc.getCategorySubtype());
            subInfo.put("ict", aDoc.getValue(Constants.ASSET_CLASS_INSTRUCTIONAL_COMPONENT));
            if (aDoc instanceof CompositeAsset) {
                subInfo.put("collectionType", ((CompositeAsset) aDoc).compositeAssetType());
            }
            subInfo.put("assetType", aDoc.getAssetType());
            subInfo.put("rights", aDoc.getRightsList());
        }

        return subInfo;
    }

    public enum Order {
        BY_DATE, BY_NUMBER
    }

    public List<Map<String, Object>> getSubassetsInfo() {
        Order order = Order.BY_DATE;
        BaseObject sortMethod = doc.getObject("CurrikiCode.CollectionReorderedClass");
        if(isRootCollection() && sortMethod!=null && sortMethod.getIntValue("reordered")==1) {
            order = Order.BY_NUMBER;
        }
        return getSubassetsInfo(order);
    }

    public List<Map<String, Object>> getSubassetsInfo(Order order) {
        List<BaseObject> objs = doc.getObjects(Constants.SUBASSET_CLASS);

        if (objs != null ) {
            List<Map<String,Object>> subList = new ArrayList<Map<String,Object>>(objs.size());

            for (BaseObject obj : objs) {
                if (obj != null) {
                    String subPage = obj.getStringValue(Constants.SUBASSET_CLASS_PAGE);

                    Map<String,Object> subInfo = new HashMap<String, Object>(11);
                    subInfo.put(Constants.SUBASSET_CLASS_PAGE, subPage);
                    subInfo.put(Constants.SUBASSET_CLASS_ORDER, obj.getLongValue(Constants.SUBASSET_CLASS_ORDER));

                    com.xpn.xwiki.api.XWiki xwikiApi = new com.xpn.xwiki.api.XWiki(context.getWiki(), context);
                    try {
                        Document doc = xwikiApi.getDocument(subPage);
                        if (doc instanceof Asset) {
                            subInfo = addSubinfo(subInfo, doc);
                        } else if (doc == null) {
                            // getDocument returns null if the page is not viewable by the user
                            subInfo = addEmptySubinfo(subInfo, ProtectedAsset.class.getSimpleName().replaceAll("Asset$", ""));
                        } else if (doc.isNew()) {
                            // Document does not exist -- thus invalid
                            subInfo = addEmptySubinfo(subInfo, InvalidAsset.class.getSimpleName().replaceAll("Asset$", ""));
                        }
                    } catch (Exception e) {
                        subInfo = addEmptySubinfo(subInfo, InvalidAsset.class.getSimpleName().replaceAll("Asset$", ""));
                    }

                    subList.add(subInfo);
                }
            }

            if(order==Order.BY_NUMBER)
                Collections.sort(subList, new Comparator<Map<String,Object>>(){ public int compare(Map<String,Object> s1, Map<String,Object> s2){
                    return ((Long) s1.get(Constants.SUBASSET_CLASS_ORDER)).compareTo((Long) s2.get(Constants.SUBASSET_CLASS_ORDER));
                }
                });
            else
                Collections.sort(subList, new Comparator<Map<String,Object>>(){ public int compare(Map<String,Object> s1, Map<String,Object> s2){
                    try {
                        String s1Name = (String) (s1.get("assetpage")),
                                s2Name=(String) (s2.get("assetpage"));
                        if(s1Name == null) s1Name = (String) (s1.get("collectionPage"));
                        if(s2Name == null) s2Name = (String) (s1.get("collectionPage"));
                        XWikiContext ctx = getXWikiContext();
                        int comparison =
                                getXWikiContext().getWiki().getDocument(s1Name, ctx).getDate().compareTo(
                                        context.getWiki().getDocument(s2Name, ctx).getDate()
                                );
                        //getXWikiContext().getWiki().getResourceLastModificationDate(s1Name)
                        //.compareTo(getXWikiContext().getWiki().getResourceLastModificationDate(s2Name));
                        if(comparison>0) return -1;
                        else if (comparison==0) return 0;
                        else return +1;
                    } catch (XWikiException e) {
                        e.printStackTrace();
                        return 0;
                    }
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
        Map<String,Object> subInfo = new HashMap<String, Object>(11);

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
                                subInfo = addSubinfo(subInfo, doc);
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
                            sql = sql + "or prop.value='" + item + "'";
                        } else {
                            sql = "prop.value='" + item + "'";
                        }
                    }

                    sql = ", BaseObject as obj, StringProperty as prop where obj.name=doc.fullName and obj.className='"+Constants.SUBASSET_CLASS+"' and prop.id.id = obj.id and prop.name='"+Constants.SUBASSET_CLASS_PAGE+"' and (" + sql + ")";
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

    public void setSubassets(List<String> wantedList) throws XWikiException {
        XWikiDocument assetDoc = getDoc();
        List<BaseObject> existingList = assetDoc.getObjects(Constants.SUBASSET_CLASS);

        String[] want = new String[0];
        if (wantedList != null) {
            want = wantedList.toArray(want);
        }

        BaseObject[] existing = new BaseObject[0];
        if (existingList != null) {
            existing = existingList.toArray(existing);
        }

// TODO: Remove
// DEBUGGING CODE for CURRIKI-4238
        System.out.println("REORDER "+assetDoc.getFullName()+" want: "+(wantedList==null?"NULL":wantedList.toString()));
        System.out.println("REORDER "+assetDoc.getFullName()+" existing: "+(existingList==null?"NULL":existingList.toString()));

        int wSize = (wantedList != null)?want.length:0;
        int eSize = (existingList != null)?existing.length:0;

        int e = 0;
        int w = 0;

        while (w < wSize) {
            if (want[w] != null && context.getWiki().exists(want[w], context)) {
                // Only add the asset if it still exists
                BaseObject b = null;
                while (b == null && e < eSize) {
                    b = existing[e];
                    e++;
                }
                if (b == null) {
                    System.out.println("REORDER "+assetDoc.getFullName()+" Adding object w="+w+" e="+e);
                    b = assetDoc.newObject(Constants.SUBASSET_CLASS, context);
                }
                else {
                    System.out.println("REORDER "+assetDoc.getFullName()+" Updating object w="+w+" e="+e);
                }

                b.setStringValue(Constants.SUBASSET_CLASS_PAGE, want[w]);
                b.setLongValue(Constants.SUBASSET_CLASS_ORDER, w);
            }
            w++;
        }

        while (e < eSize) {
            BaseObject b = null;
            while (b == null && e < eSize) {
                b = existing[e];
                e++;
            }
            if (b != null) {
                System.out.println("REORDER "+assetDoc.getFullName()+" Removing object w="+w+" e="+e);
                assetDoc.removeObject(b);
            }
        }
    }

    public void reorder(String previousRevision, List<String> want) throws XWikiException {
        if (!getVersion().equals(previousRevision)){
            throw new AssetException(AssetException.ERROR_ASSET_REORDER_NOTMATCH, "This resource has been updated since originally checked");
        }

        setSubassets(want);
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

        setSubassets(want);
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
