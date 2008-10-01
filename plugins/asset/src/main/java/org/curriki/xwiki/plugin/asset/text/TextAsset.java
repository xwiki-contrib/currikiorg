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
        if (!hasA(Constants.TEXT_ASSET_CLASS)) {
            throw new AssetException("This asset has no text.");
        }

        BaseObject obj = doc.getObject(Constants.TEXT_ASSET_CLASS);
        return obj.getStringValue(Constants.TEXT_ASSET_CLASS_TEXT);
    }

    public Long getType() throws AssetException {
        if (!hasA(Constants.TEXT_ASSET_CLASS)) {
            throw new AssetException("This asset has no text.");
        }

        BaseObject obj = doc.getObject(Constants.TEXT_ASSET_CLASS);
        return obj.getLongValue(Constants.TEXT_ASSET_CLASS_TYPE);
    }

    public void addText(Long type, String content) throws XWikiException {
        if (hasA(Constants.TEXT_ASSET_CLASS)) {
            throw new AssetException("This asset already has text.");
        }

        BaseObject obj = doc.newObject(Constants.TEXT_ASSET_CLASS, context);
        obj.setStringValue(Constants.TEXT_ASSET_CLASS_TEXT, content);
        obj.setLongValue(Constants.TEXT_ASSET_CLASS_TYPE, type);

        determineCategory();
    }

    protected void determineCategory() throws XWikiException {
        BaseObject obj = doc.getObject(Constants.ASSET_CLASS);
        if (obj != null) {
            obj.setStringValue(Constants.ASSET_CLASS_CATEGORY, Constants.CATEGORY_TEXT);
        }
    }
}