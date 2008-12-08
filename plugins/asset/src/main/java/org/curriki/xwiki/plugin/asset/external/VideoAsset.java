package org.curriki.xwiki.plugin.asset.external;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.AssetException;

/**
 */
public class VideoAsset extends Asset {
    public VideoAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    public String getVideoId() throws XWikiException {
        if (!hasA(Constants.VIDEO_ASSET_CLASS)) {
            throw new AssetException("This asset has no video.");
        }

        BaseObject obj = doc.getObject(Constants.VIDEO_ASSET_CLASS);
        return obj.getStringValue(Constants.VIDEO_ASSET_ID);
    }

    public void addVideoId(String videoId) throws XWikiException {
        if (hasA(Constants.VIDEO_ASSET_CLASS)) {
            throw new AssetException("This asset already has video.");
        }

        setVideoId(videoId);
    }

    public void setVideoId(String videoId) throws XWikiException {
        doc.removeObjects(Constants.VIDEO_ASSET_CLASS);

        BaseObject obj = doc.newObject(Constants.VIDEO_ASSET_CLASS, context);
        obj.setStringValue(Constants.VIDEO_ASSET_ID, videoId);

        determineCategory();
    }

    protected void determineCategory() throws XWikiException {
        BaseObject obj = doc.getObject(Constants.ASSET_CLASS);
        if (obj != null) {
            obj.setStringValue(Constants.ASSET_CLASS_CATEGORY, Constants.ASSET_CATEGORY_VIDEO);
        }
    }
}