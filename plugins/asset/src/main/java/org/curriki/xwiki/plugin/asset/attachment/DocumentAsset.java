package org.curriki.xwiki.plugin.asset.attachment;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.Constants;

/**
 */
public class DocumentAsset extends AttachmentAsset {

    public final static String CATEGORY_NAME = Constants.ASSET_CATEGORY_DOCUMENT;

    public DocumentAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }
}