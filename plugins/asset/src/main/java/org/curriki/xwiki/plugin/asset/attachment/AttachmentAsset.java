package org.curriki.xwiki.plugin.asset.attachment;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.mimetype.MimeTypePlugin;

/**
 */
public class AttachmentAsset extends Asset {

    public AttachmentAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }


    public String getFileType() {
        use(getObject(Constants.ATTACHMENT_ASSET_CLASS));
        return (String) getValue(Constants.ATTACHMENT_ASSET_FILE_TYPE);
    }

    @Override
    public String getCategorySubtype() {
        return getFileType();
    }

    /**
     * This functions will display the asset including a fallback system
     * For a specific mode. This function can be overidden for a specific asset type
     * Otherwise it will use a default rule system to find the appropriate template
     * @return
     */
    protected String displayAssetTemplate(String mode) {
        MimeTypePlugin mimePlugin = getMimeTypePlugin();
        String category = getCategory();
        String displayer = mimePlugin.getDisplayer(category, getFileType(), context);
        String result = context.getWiki().parseTemplate("assets/displayers/" + displayer  + "_" + mode + ".vm", context);
        if (result.equals(""))
           result =  context.getWiki().parseTemplate("assets/displayers/" + category + "_" + mode + ".vm", context);
        if (result.equals(""))
           result =  context.getWiki().parseTemplate("assets/displayers/attachment_" + mode + ".vm", context);
        return result;
    }
}