package org.curriki.xwiki.plugin.asset.external;

import org.curriki.xwiki.plugin.asset.attachment.AttachmentAssetManager;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.Asset;
import com.xpn.xwiki.XWikiException;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 10 déc. 2008
 * Time: 19:43:35
 * To change this template use File | Settings | File Templates.
 */
public class VideoAssetManager extends AttachmentAssetManager {

    public static String CATEGORY_NAME = Constants.ASSET_CATEGORY_VIDEO;
    public static  Class<? extends Asset> ASSET_CLASS = VideoAsset.class;


    public String getCategory() {
         return CATEGORY_NAME;
    }

    public Class<? extends Asset> getAssetClass() {
        return ASSET_CLASS;
    }

    public VideoAsset makeVideoAsset(Asset assetDoc, String videoId, String partner) throws XWikiException {
        VideoAsset asset = assetDoc.subclassAs(VideoAsset.class);
        asset.makeVideoAsset(videoId, partner);
        return asset;
    }
}
