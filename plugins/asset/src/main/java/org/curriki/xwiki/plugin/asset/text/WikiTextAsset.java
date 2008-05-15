package org.curriki.xwiki.plugin.asset.text;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;

/**
 */
public class WikiTextAsset extends TextAsset {
    public WikiTextAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }
}