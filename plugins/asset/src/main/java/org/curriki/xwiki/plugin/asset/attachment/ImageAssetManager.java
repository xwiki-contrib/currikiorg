package org.curriki.xwiki.plugin.asset.attachment;

import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.Asset;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.image.ImagePlugin;

/**
 */
public class ImageAssetManager extends AttachmentAssetManager {

    public static String CATEGORY_NAME = Constants.ASSET_CATEGORY_IMAGE;
    public static  Class<? extends Asset> ASSET_CLASS = ImageAsset.class;

    public String getCategory() {
         return CATEGORY_NAME;
     }

    public Class<? extends Asset> getAssetClass() {
        return ASSET_CLASS;
    }
    
    public void updateSubAssetClass(XWikiDocument assetDoc, String filetype, String category, XWikiAttachment attachment, XWikiContext context) {
        assetDoc.getObject(Constants.IMAGE_ASSET_CLASS, true, context);

        ImagePlugin imgPlugin = (ImagePlugin) context.getWiki().getPlugin("image", context);

        BaseObject imageObject = assetDoc.getObject(Constants.IMAGE_ASSET_CLASS, true, context);

        if (imgPlugin != null) {
            try {
                int height = imgPlugin.getHeight(attachment, context);
                int width = imgPlugin.getWidth(attachment, context);
                imageObject.setIntValue("height", height);
                imageObject.setIntValue("width", width);
            } catch (Exception e) {
                // Ignore exception
            }
        }
    }

}
