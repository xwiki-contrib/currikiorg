package org.curriki.xwiki.plugin.asset.composite;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.api.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
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
        Vector<Map<String,Object>> subList = getSubassetsInfo();
        if (subList.size() > 0) {
            docInfo.put("children", subList);
        }

        return docInfo;
    }

    public Vector<Map<String,Object>> getSubassetsInfo() {
        Vector objs = getObjects(Constants.SUBASSET_CLASS);
        Vector<Map<String,Object>> subList = new Vector<Map<String,Object>>(objs.size());
        for (Object obj : objs){
            if (obj instanceof com.xpn.xwiki.api.Object) {
                com.xpn.xwiki.api.Object xObj = (com.xpn.xwiki.api.Object) obj;

                String subPage = (String) getValue(Constants.SUBASSET_CLASS_PAGE, xObj);

                Map<String,Object> subInfo = new HashMap<String, Object>(2);
                subInfo.put(Constants.SUBASSET_CLASS_PAGE, subPage);
                subInfo.put(Constants.SUBASSET_CLASS_ORDER, getValue(Constants.SUBASSET_CLASS_ORDER, xObj));

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

        return subList;
    }

    public Map<String,Object> getSubassetInfo(long subassetId) throws AssetException {
        Vector objs = getObjects(Constants.SUBASSET_CLASS);
        Map<String,Object> subInfo = new HashMap<String, Object>(5);
        for (Object obj : objs){
            if (obj instanceof com.xpn.xwiki.api.Object) {
                com.xpn.xwiki.api.Object xObj = (com.xpn.xwiki.api.Object) obj;

                String subPage = (String) getValue(Constants.SUBASSET_CLASS_PAGE, xObj);

                Long order = Long.getLong((String) getValue(Constants.SUBASSET_CLASS_ORDER, xObj));
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

    public long addSubasset(String page) throws XWikiException {
        Vector objs = getObjects(Constants.SUBASSET_CLASS);
        Long highestOrder = null;
        for (Object obj : objs){
            if (obj instanceof com.xpn.xwiki.api.Object) {
                com.xpn.xwiki.api.Object xObj = (com.xpn.xwiki.api.Object) obj;
                Long objOrder = Long.getLong((String) getValue(Constants.SUBASSET_CLASS_ORDER, xObj));
                if (highestOrder == null || objOrder > highestOrder) {
                    highestOrder = objOrder;
                }
            }
        }

        if (highestOrder == null) {
            highestOrder = new Long(0);
        } else {
            highestOrder++;
        }

        BaseObject obj = doc.newObject(Constants.SUBASSET_CLASS, context);
        obj.setStringValue(Constants.SUBASSET_CLASS_PAGE, page);
        obj.setLongValue(Constants.SUBASSET_CLASS_ORDER, highestOrder);

        return highestOrder;
    }
}
