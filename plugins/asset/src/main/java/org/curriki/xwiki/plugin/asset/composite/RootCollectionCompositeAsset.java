package org.curriki.xwiki.plugin.asset.composite;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import org.curriki.xwiki.plugin.asset.Constants;

/**
 */
public class RootCollectionCompositeAsset extends CollectionCompositeAsset {
    public RootCollectionCompositeAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    protected String compositeAssetType() {
        return Constants.COMPOSITE_ASSET_CLASS_TYPE_ROOT_COLLECTION;
    }

    public boolean isRootCollection() {
        return true;
    }
}
