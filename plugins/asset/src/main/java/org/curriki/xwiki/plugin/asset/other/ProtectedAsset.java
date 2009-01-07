package org.curriki.xwiki.plugin.asset.other;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.Constants;

/**
 */
public class ProtectedAsset extends Asset {
    public ProtectedAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    @Override
    public String getCategorySubtype() {
        return Constants.ASSET_CATEGORY_SUBTYPE_PROTECTED;
    }
}