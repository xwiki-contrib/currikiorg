package org.curriki.xwiki.plugin.asset.other;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import org.curriki.xwiki.plugin.asset.Asset;

/**
 */
public class ProtectedAsset extends Asset {
    public ProtectedAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }
}