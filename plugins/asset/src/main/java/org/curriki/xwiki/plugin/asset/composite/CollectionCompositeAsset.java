package org.curriki.xwiki.plugin.asset.composite;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;

import java.util.Map;

import org.curriki.xwiki.plugin.asset.Constants;

/**
 */
public class CollectionCompositeAsset extends FolderCompositeAsset {

    public final static String CATEGORY_NAME = "collection";

    public CollectionCompositeAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    protected String compositeAssetType() {
        return Constants.COMPOSITE_ASSET_CLASS_TYPE_COLLECTION;
    }

    public boolean isCollection() {
        return true;
    }

    public Map<String,Object> getCollectionInfo() {
        return getFolderInfo();
    }
}
