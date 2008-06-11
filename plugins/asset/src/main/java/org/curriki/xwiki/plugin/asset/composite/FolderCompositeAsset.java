package org.curriki.xwiki.plugin.asset.composite;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.api.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.lang.Object;

import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.AssetException;
import org.curriki.xwiki.plugin.asset.other.ProtectedAsset;
import org.curriki.xwiki.plugin.asset.other.InvalidAsset;

/**
 */
public class FolderCompositeAsset extends CompositeAsset {
    public FolderCompositeAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    protected String compositeAssetType() {
        return Constants.COMPOSITE_ASSET_CLASS_TYPE_SUBFOLDER;
    }

    public boolean isFolder() {
        return true;
    }

    public Map<String,Object> getFolderInfo() {
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
        List<Map<String,Object>> subList = new ArrayList<Map<String,Object>>(objs.size());
        for (Object obj : objs){
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

    public Map<String,Object> getSubassetInfo(long subassetId) throws AssetException {
        List objs = doc.getObjects(Constants.SUBASSET_CLASS);
        Map<String,Object> subInfo = new HashMap<String, Object>(5);
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

        throw new AssetException(AssetException.ERROR_ASSET_SUBASSET_NOTFOUND, "No subasset exists with the order number "+subassetId);
    }

    public long insertSubassetBefore(String page, String beforePage) throws XWikiException {
        if (beforePage == null){
            return addSubasset(page);
        }

        List objs = doc.getObjects(Constants.SUBASSET_CLASS);
        Long beforePosition = null;
        for (Object obj : objs) {
            if (obj instanceof BaseObject) {
                BaseObject xObj = (BaseObject) obj;
                String objName = xObj.getStringValue(Constants.SUBASSET_CLASS_PAGE);
                if (objName.equals(beforePage)){
                    beforePosition = xObj.getLongValue(Constants.SUBASSET_CLASS_ORDER);
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

    protected long getLastPosition(){
        List objs = doc.getObjects(Constants.SUBASSET_CLASS);
        long highestOrder = (long) -1;
        for (Object obj : objs){
            if (obj instanceof BaseObject) {
                BaseObject xObj = (BaseObject) obj;
                long objOrder = xObj.getLongValue(Constants.SUBASSET_CLASS_ORDER);
                if (objOrder > highestOrder) {
                    highestOrder = objOrder;
                }
            }
        }

        return highestOrder;
    }
}
