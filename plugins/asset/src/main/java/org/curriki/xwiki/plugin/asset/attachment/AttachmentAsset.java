package org.curriki.xwiki.plugin.asset.attachment;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import org.curriki.xwiki.plugin.asset.Asset;

/**
 */
public class AttachmentAsset extends Asset {
    public AttachmentAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }
}