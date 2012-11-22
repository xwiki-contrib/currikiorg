package org.curriki.xwiki.plugin.asset.attachment;

import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.Asset;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.XWikiContext;

/**
 */
public class ArchiveAssetManager extends AttachmentAssetManager {

    public static String CATEGORY_NAME = Constants.ASSET_CATEGORY_ARCHIVE;
    public static  Class<? extends Asset> ASSET_CLASS = ArchiveAsset.class;

    public String getCategory() {
         return CATEGORY_NAME;
    }

    public Class<? extends Asset> getAssetClass() {
        return ASSET_CLASS;
    }
    
    public void updateSubAssetClass(XWikiDocument assetDoc, String filetype, String category, XWikiAttachment attachment, XWikiContext context) {
        // We need to set the archive type for the cases we can
        // TODO: auto detection of knows package types.
        BaseObject archiveObject = assetDoc.getObject(Constants.ARCHIVE_ASSET_CLASS, true, context);
         String archiveType = Constants.ARCHIVE_ASSET_TYPE_ZIP;
         if ((filetype!=null) && filetype.equals(Constants.ATTACHMENT_ASSET_FILE_TYPE_XO))
          archiveType = Constants.ARCHIVE_ASSET_TYPE_XO;

         archiveObject.setStringValue(Constants.ARCHIVE_ASSET_TYPE, archiveType);
    }

}
