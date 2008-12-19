package org.curriki.xwiki.plugin.asset.attachment;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Constants;

/**
 */
public class ArchiveAsset extends AttachmentAsset {

    public ArchiveAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }
}