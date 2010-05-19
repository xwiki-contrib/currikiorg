package org.curriki.xwiki.plugin.asset.text;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.AssetException;
import org.curriki.xwiki.plugin.asset.external.VideoAsset;

/**
 */
public class TextAsset extends Asset {
    public TextAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    public String getText() throws AssetException {
        return doc.getContent();
    }

    public String getTextSyntax() throws AssetException {
        if (!hasA(Constants.TEXT_ASSET_CLASS)) {
            throw new AssetException("This asset is not a text asset.");
        }

        BaseObject obj = doc.getObject(Constants.TEXT_ASSET_CLASS);
        return obj.getStringValue(Constants.TEXT_ASSET_SYNTAX);
    }

    @Override
    public String getCategorySubtype() {
        String syntax = "";
        try {
            syntax = getTextSyntax();
        } catch (AssetException e) {
            syntax = Constants.ASSET_CATEGORY_SUBTYPE_UNKNOWN;
        }

        // Turn html/1.0, xwiki/1.0, xwiki/2.0 into just the first portion (no /x.0)
        return syntax.replaceAll("/.*$", "");
    }

    public void makeTextAsset(String syntax, String content) throws XWikiException {
        assertCanEdit();
        doc.setContent(content);
        BaseObject obj = doc.getObject(Constants.TEXT_ASSET_CLASS, true, context);
        if (obj != null) {
            obj.setStringValue(Constants.TEXT_ASSET_SYNTAX, syntax);
        }
        setCategory(Constants.ASSET_CATEGORY_TEXT);
        saveDocument(context.getMessageTool().get("curriki.comment.createtextsourceasset"), true);
    }

}
