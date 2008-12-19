package org.curriki.xwiki.plugin.asset.external;

import org.curriki.xwiki.plugin.asset.DefaultAssetManager;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.attachment.DocumentAsset;
import org.curriki.xwiki.plugin.asset.text.TextAsset;
import com.xpn.xwiki.XWikiException;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 10 déc. 2008
 * Time: 19:43:24
 * To change this template use File | Settings | File Templates.
 */
public class ExternalAssetManager extends DefaultAssetManager {

    public static String CATEGORY_NAME = Constants.ASSET_CATEGORY_EXTERNAL;
    public static  Class<? extends Asset> ASSET_CLASS = ExternalAsset.class;


    public String getCategory() {
         return CATEGORY_NAME;
    }

    public Class<? extends Asset> getAssetClass() {
        return ASSET_CLASS;
    }
    
    public ExternalAsset makeExternalAsset(Asset assetDoc, String link, String linktext) throws XWikiException {
        ExternalAsset asset = assetDoc.subclassAs(ExternalAsset.class);
        asset.makeExternalAsset(link, linktext);
        return asset;
    }
}
