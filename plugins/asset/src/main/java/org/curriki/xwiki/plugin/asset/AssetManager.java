package org.curriki.xwiki.plugin.asset;

import org.curriki.xwiki.plugin.asset.composite.CompositeAsset;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.XWikiContext;

/**
 */
public interface AssetManager {

    /**
     * Retrieves the category corresponding to this asset manager
     * There should be one asset manager per category
     * @return
     */
    public String getCategory();

    /**
     * Retrieves the Java Asset class handled by this asset manager
     * This Asset class allows further custom processing to be done
     * @return
     */
    public Class<? extends Asset> getAssetClass();

    /**
     * General function used in the case of attachment sub-types.
     * The correct asset manager is chosen based on the attachment extension to category mapping
     * The mappings are stored in CurrikiCode.MimeTypeConfig
     *
     * This method is then called for further processing, including adding an XWiki model class
     * and inserting specific data in it.
     * @param assetDoc
     * @param filetype
     * @param category
     * @param attachment
     * @param context
     */
    public void updateSubAssetClass(XWikiDocument assetDoc, String filetype, String category, XWikiAttachment attachment, XWikiContext context);
}
