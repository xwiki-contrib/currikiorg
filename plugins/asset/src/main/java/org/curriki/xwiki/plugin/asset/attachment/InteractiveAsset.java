package org.curriki.xwiki.plugin.asset.attachment;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Constants;

/**
 */
public class InteractiveAsset extends DocumentAsset {
    public InteractiveAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    protected void determineCategory() throws XWikiException {
        BaseObject obj = doc.getObject(Constants.ASSET_CLASS);
        if (obj != null) {
            obj.setStringValue(Constants.ASSET_CLASS_CATEGORY, Constants.ASSET_CATEGORY_INTERACTIVE);
        }
    }

}