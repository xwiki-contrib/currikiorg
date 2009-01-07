package org.curriki.xwiki.plugin.asset.external;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.AssetException;

import java.net.URL;
import java.net.MalformedURLException;

/**
 */
public class ExternalAsset extends Asset {

    public final static String CATEGORY_NAME = Constants.ASSET_CATEGORY_EXTERNAL;

    public ExternalAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    @Override
    public String getCategorySubtype() {
        return Constants.EXTERNAL_ASSET_CATEGORY_SUBTYPE_DEFAULT;
    }

    public String getDisplayTitle() {
        String title = super.getDisplayTitle();

        if (title.equals(Constants.ASSET_DISPLAYTITLE_UNTITLED)) {
            if (doc.getObject(Constants.EXTERNAL_ASSET_CLASS) != null){
                title = doc.getObject(Constants.EXTERNAL_ASSET_CLASS).getStringValue(Constants.EXTERNAL_ASSET_LINK);
                if (title != null) {
                    if (title.lastIndexOf("/") == title.length() - 1){
                        title = title.substring(0, title.length() - 1);
                    }
                    if (title.contains("/")){
                        title = title.substring(title.lastIndexOf("/") + 1, title.length());
                    }
                    if (title.contains(".")) {
                        title = title.substring(0, title.lastIndexOf("."));
                    }
                    title = title.replace("_", " ");
                }
            }
        }

        return title;
    }

    public String getLink() throws XWikiException {
        if (!hasA(Constants.EXTERNAL_ASSET_CLASS)) {
            throw new AssetException("This asset has no link.");
        }

        BaseObject obj = doc.getObject(Constants.EXTERNAL_ASSET_CLASS);
        return obj.getStringValue(Constants.EXTERNAL_ASSET_LINK);
    }

    public String getLinkText() throws XWikiException {
         if (!hasA(Constants.EXTERNAL_ASSET_CLASS)) {
             return "";
         }

         BaseObject obj = doc.getObject(Constants.EXTERNAL_ASSET_CLASS);
         return obj.getStringValue(Constants.EXTERNAL_ASSET_LINKTEXT);
     }

    public void makeExternalAsset(String link, String linktext) throws XWikiException {
        assertCanEdit();

        BaseObject obj = doc.getObject(Constants.EXTERNAL_ASSET_CLASS, true, context);
        if (obj != null) {
            obj.setStringValue(Constants.EXTERNAL_ASSET_LINK, link);
            obj.setStringValue(Constants.EXTERNAL_ASSET_LINKTEXT, linktext);
             }
        setCategory(Constants.ASSET_CATEGORY_EXTERNAL);
        saveDocument(context.getMessageTool().get("curriki.comment.createlinksourceasset"), true);
    }

}