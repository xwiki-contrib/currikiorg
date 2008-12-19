package org.curriki.xwiki.plugin.asset.attachment;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.plugin.image.ImagePlugin;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Constants;

/**
 */
public class ImageAsset extends AttachmentAsset {

    public final static String CATEGORY_NAME = Constants.ASSET_CATEGORY_IMAGE;

    public ImageAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }
    
}