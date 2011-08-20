package org.curriki.xwiki.plugin.asset.attachment;

import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.Asset;

/**
 */
public class AudioAssetManager extends AttachmentAssetManager {

    public static String CATEGORY_NAME = Constants.ASSET_CATEGORY_AUDIO;
    public static  Class<? extends Asset> ASSET_CLASS = AudioAsset.class;

    public String getCategory() {
         return CATEGORY_NAME;
     }

    public Class<? extends Asset> getAssetClass() {
        return ASSET_CLASS;
    }

}
