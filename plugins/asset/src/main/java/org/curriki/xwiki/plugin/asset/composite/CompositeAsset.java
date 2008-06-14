package org.curriki.xwiki.plugin.asset.composite;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.Constants;

/**
 */
abstract class CompositeAsset extends Asset {
    public CompositeAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    protected void initSubType() throws XWikiException {
        super.initSubType();
        BaseObject obj = doc.newObject(Constants.COMPOSITE_ASSET_CLASS, context);
        obj.setStringValue(Constants.COMPOSITE_ASSET_CLASS_TYPE, compositeAssetType());

        setDefaultContent();
    }

    protected void setDefaultContent() throws XWikiException {
        assertCanEdit();
        doc.setContent(Constants.COMPOSITE_ASSET_COMPOSITE_CONTENT);
    }

    abstract protected String compositeAssetType();
}
