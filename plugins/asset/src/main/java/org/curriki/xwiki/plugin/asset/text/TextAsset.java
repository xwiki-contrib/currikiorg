package org.curriki.xwiki.plugin.asset.text;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.AssetException;

/**
 */
public class TextAsset extends Asset {
    public TextAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    public String getText() throws AssetException {
        return doc.getContent();
    }

    public String getSyntax() throws AssetException {
        if (!hasA(Constants.TEXT_ASSET_CLASS)) {
            throw new AssetException("This asset is not a text asset.");
        }

        BaseObject obj = doc.getObject(Constants.TEXT_ASSET_CLASS);
        return obj.getStringValue(Constants.TEXT_ASSET_SYNTAX);
    }

    public void addText(String syntax, String content) throws XWikiException {
        doc.setContent(content);
        BaseObject obj = doc.getObject(Constants.TEXT_ASSET_CLASS, true, context);
        if (obj != null) {
            obj.setStringValue(Constants.TEXT_ASSET_SYNTAX, syntax);
        }
    }
}