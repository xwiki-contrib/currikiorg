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
public class VIDITalkAsset extends Asset {
    public VIDITalkAsset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    public String getVideoId() throws XWikiException {
        if (!hasA(Constants.VIDITALK_CLASS)) {
            throw new AssetException("This asset has no video.");
        }

        BaseObject obj = doc.getObject(Constants.VIDITALK_CLASS);
        return obj.getStringValue(Constants.VIDITALK_CLASS_VIDEO_ID);
    }

    public void addVideoId(String videoId) throws XWikiException {
        if (hasA(Constants.EXTERNAL_ASSET_CLASS)) {
            throw new AssetException("This asset already has alink.");
        }

        BaseObject obj = doc.newObject(Constants.VIDITALK_CLASS, context);
        obj.setStringValue(Constants.VIDITALK_CLASS_VIDEO_ID, videoId);
    }

    public void setVideoId(String videoId) throws XWikiException {
        doc.removeObjects(Constants.EXTERNAL_ASSET_CLASS);

        BaseObject obj = doc.newObject(Constants.VIDITALK_CLASS, context);
        obj.setStringValue(Constants.VIDITALK_CLASS_VIDEO_ID, videoId);
    }
}