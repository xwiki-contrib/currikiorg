package org.curriki.xwiki.plugin.asset.other;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.Constants;

/**
 */
public class UnknownAsset extends Asset {
    public UnknownAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    @Override
    public String getCategorySubtype() {
        return Constants.ASSET_CATEGORY_SUBTYPE_UNKNOWN;
    }
}