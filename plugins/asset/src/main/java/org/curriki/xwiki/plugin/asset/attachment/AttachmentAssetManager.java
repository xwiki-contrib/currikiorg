package org.curriki.xwiki.plugin.asset.attachment;

import org.curriki.xwiki.plugin.asset.DefaultAssetManager;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.external.ExternalAsset;
import com.xpn.xwiki.XWikiException;

/**
 */
public class AttachmentAssetManager extends DefaultAssetManager {
    public static String CATEGORY_NAME = Constants.ASSET_CATEGORY_ATTACHMENT;
    public static  Class<? extends Asset> ASSET_CLASS = AttachmentAsset.class;

    public String getCategory() {
         return CATEGORY_NAME;
     }
        
    public Class<? extends Asset> getAssetClass() {
        return ASSET_CLASS;
    }

}
