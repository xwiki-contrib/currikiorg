package org.curriki.xwiki.plugin.asset.text;

import org.curriki.xwiki.plugin.asset.DefaultAssetManager;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.attachment.AttachmentAsset;
import com.xpn.xwiki.XWikiException;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 10 déc. 2008
 * Time: 19:43:51
 * To change this template use File | Settings | File Templates.
 */
public class TextAssetManager extends DefaultAssetManager {

    private static String CATEGORY_NAME = Constants.ASSET_CATEGORY_TEXT;
    private static  Class<? extends Asset> ASSET_CLASS = TextAsset.class;


    public String getCategory() {
         return CATEGORY_NAME;
    }

    public Class<? extends Asset> getAssetClass() {
        return ASSET_CLASS;
    }

    public TextAsset makeTextAsset(Asset assetDoc, String syntax, String content) throws XWikiException {
        TextAsset asset = assetDoc.subclassAs(TextAsset.class);
        asset.makeTextAsset(syntax, content);
        return asset;
    }
}
