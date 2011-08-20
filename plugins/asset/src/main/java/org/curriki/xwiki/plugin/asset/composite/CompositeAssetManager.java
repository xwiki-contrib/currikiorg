package org.curriki.xwiki.plugin.asset.composite;

import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.DefaultAssetManager;
import org.curriki.xwiki.plugin.asset.Constants;

/**
 */
public class CompositeAssetManager extends DefaultAssetManager {

    public static String CATEGORY_NAME = Constants.ASSET_CATEGORY_COLLECTION;
    private static  Class<? extends Asset> ASSET_CLASS = CompositeAsset.class;

    public String getCategory() {
          return CATEGORY_NAME;
    }
    
    public Class<? extends Asset> getAssetClass() {
        return ASSET_CLASS;
    }

}
