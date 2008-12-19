package org.curriki.xwiki.plugin.asset.composite;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.XWikiMessageTool;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.api.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.lang.Object;

import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.AssetException;
import org.curriki.xwiki.plugin.asset.other.ProtectedAsset;
import org.curriki.xwiki.plugin.asset.other.InvalidAsset;

/**
 */
public class FolderCompositeAsset extends CompositeAsset {

    public FolderCompositeAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    protected String compositeAssetType() {
        return Constants.COMPOSITE_ASSET_CLASS_TYPE_SUBFOLDER;
    }

    public boolean isFolder() {
        return true;
    }

    public Map<String,Object> getFolderInfo() {
        return getCompositeInfo();
    }
}
