package org.curriki.xwiki.plugin.asset;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiAttachment;

import java.util.Map;
import java.util.HashMap;

import org.curriki.xwiki.plugin.asset.text.TextAssetManager;
import org.curriki.xwiki.plugin.asset.attachment.*;
import org.curriki.xwiki.plugin.asset.external.VideoAssetManager;
import org.curriki.xwiki.plugin.asset.external.ExternalAssetManager;
import org.curriki.xwiki.plugin.asset.composite.CompositeAssetManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public abstract class DefaultAssetManager implements AssetManager {
    private static final Log LOG = LogFactory.getLog(DefaultAssetManager.class);

    private static String CATEGORY_NAME = Constants.ASSET_CATEGORY_UNKNOWN;
    private static  Class<? extends Asset> ASSET_CLASS = AttachmentAsset.class;

    private static final Map<String, AssetManager> assetSubTypes = new HashMap<String, AssetManager>();

    public static void addAssetSubType(AssetManager assetManager) {
        assetSubTypes.put(assetManager.getCategory(), assetManager);
    }

    public static AssetManager getAssetSubTypeManager(String category) {
        return assetSubTypes.get(category);
    }

    public static void initAssetSubTypes(XWikiContext context) {
        // Add hardcoded asset subtypes
        addAssetSubType(new TextAssetManager());
        addAssetSubType(new AttachmentAssetManager());
        addAssetSubType(new DocumentAssetManager());
        addAssetSubType(new ImageAssetManager());
        addAssetSubType(new AudioAssetManager());
        addAssetSubType(new VideoAssetManager());
        addAssetSubType(new ArchiveAssetManager());
        addAssetSubType(new InteractiveAssetManager());
        addAssetSubType(new ExternalAssetManager());
        addAssetSubType(new CompositeAssetManager());

        // Add asset sub types from config
        String params = context.getWiki().getXWikiPreference("curriki_assetsubtypes", "", context);
        if (params.equals(""))
             params = context.getWiki().Param("curriki.assetsubtypes");

        if ((params!=null)&&!params.equals("")) {
            String[] assetsubtypes = StringUtils.split(", ");
            for (int i = 0;i<assetsubtypes.length;i++) {
                String assetsubtype = assetsubtypes[i];
                try {
                    Class<AssetManager> assetsubtypeClass = (Class<AssetManager>) Class.forName(assetsubtype);
                    AssetManager newAssetManager = assetsubtypeClass.newInstance();
                    addAssetSubType(newAssetManager);
                } catch (Exception e) {
                    if (LOG.isErrorEnabled())
                        LOG.error("Curriki Asset plugin: error loading class for asset subtype " + assetsubtype);
                }
            }
        }
    }


    abstract public String getCategory();
    abstract public Class<? extends Asset> getAssetClass();

    public void updateSubAssetClass(XWikiDocument assetDoc, String filetype, String category, XWikiAttachment attachment, XWikiContext context) {

    }
    
}
