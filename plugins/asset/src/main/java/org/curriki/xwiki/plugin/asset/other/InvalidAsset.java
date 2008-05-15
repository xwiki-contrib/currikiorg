package org.curriki.xwiki.plugin.asset.other;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import org.curriki.xwiki.plugin.asset.Asset;

/**
 */
public class InvalidAsset extends Asset {
    public InvalidAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }
}