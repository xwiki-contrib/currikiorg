package org.curriki.xwiki.plugin.asset.attachment;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.Asset;

/**
 */
public class InteractiveAsset extends AttachmentAsset {

    public InteractiveAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

}