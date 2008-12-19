package org.curriki.xwiki.servlet.restlet.resource.assets;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import org.curriki.xwiki.plugin.asset.external.VideoAsset;
import net.sf.json.JSONObject;
import com.xpn.xwiki.XWikiException;

/**
 */
public class VideoResource extends BaseResource {
    public VideoResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");
        String viditalkId = (String) request.getAttributes().get("viditalkId");
        if (!viditalkId.equals("0")) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "An asset may only have one viditalk movie");
        }

        VideoAsset asset = null;
        try {
            asset = (VideoAsset) plugin.fetchAssetAs(assetName, VideoAsset.class);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        if (asset == null) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Asset "+assetName+" not found.");
        }

        String link = null;
        try {
            link = asset.getVideoId();
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "No movies found for "+assetName);
        }

        JSONObject json = new JSONObject();
        json.put("video_id", link);

        return formatJSON(json, variant);
    }
}