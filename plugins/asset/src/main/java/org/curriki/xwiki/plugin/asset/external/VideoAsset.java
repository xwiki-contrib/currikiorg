package org.curriki.xwiki.plugin.asset.external;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.AssetException;
import org.curriki.xwiki.plugin.mimetype.MimeTypePlugin;

/**
 */
public class VideoAsset extends Asset {

    public VideoAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    public String getVideoId() throws XWikiException {
        if (!hasA(Constants.VIDEO_ASSET_CLASS)) {
            throw new AssetException("This asset has no video.");
        }

        BaseObject obj = doc.getObject(Constants.VIDEO_ASSET_CLASS);
        return obj.getStringValue(Constants.VIDEO_ASSET_ID);
    }

    public String getVideoPartner() throws XWikiException {
         if (!hasA(Constants.VIDEO_ASSET_CLASS)) {
             throw new AssetException("This asset has no video.");
         }

         BaseObject obj = doc.getObject(Constants.VIDEO_ASSET_CLASS);
         return obj.getStringValue(Constants.VIDEO_ASSET_PARTNER);
     }

    public void makeVideoAsset(String videoId, String partner) throws XWikiException {
        assertCanEdit();
        BaseObject obj = doc.getObject(Constants.VIDEO_ASSET_CLASS, true, context);
        obj.setStringValue(Constants.VIDEO_ASSET_ID, videoId);
        obj.setStringValue(Constants.VIDEO_ASSET_PARTNER, partner);
        setCategory(Constants.ASSET_CATEGORY_VIDEO);
        saveDocument(context.getMessageTool().get("curriki.comment.createvideosourceasset"), true);
    }

    @Override
    public String getCategorySubtype() {
        String partner = "";
        try {
            partner = getVideoPartner();
        } catch (XWikiException e) {
            partner = "";
        }
        String result = "";
        if (Constants.VIDEO_ASSET_PARTNER_VIDITALK.equals(partner)) {
            return Constants.VIDEO_ASSET_CATEGORY_SUBTYPE_VIDITALK;
        }

        if (hasA(Constants.ATTACHMENT_ASSET_CLASS)) {
            use(getObject(Constants.ATTACHMENT_ASSET_CLASS));
            return (String) getValue(Constants.ATTACHMENT_ASSET_FILE_TYPE);
        }

        return Constants.ASSET_CATEGORY_SUBTYPE_UNKNOWN;
    }

    /**
     * This functions will display the asset including a fallback system
     * For a specific mode. This function can be overidden for a specific asset type
     * Otherwise it will use a default rule system to find the appropriate template
     * @return
     */
    protected String displayAssetTemplate(String mode) {
        String partner = "";
        try {
            partner = getVideoPartner();
        } catch (XWikiException e) {
            partner = "";
        }
        String result = "";
        if (Constants.VIDEO_ASSET_PARTNER_VIDITALK.equals(partner))
         result = context.getWiki().parseTemplate("assets/displayers/viditalk_" + mode + ".vm", context);
        if (result.equals(""))
           result =  super.displayAssetTemplate(mode);
        return result;
    }
}