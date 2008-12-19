package org.curriki.xwiki.servlet.restlet.resource.assets;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.DefaultAssetManager;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.external.VideoAsset;
import org.curriki.xwiki.plugin.asset.external.VideoAssetManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONException;
import com.xpn.xwiki.XWikiException;

/**
 */
public class VideosResource extends BaseResource {
    public VideosResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        setModifiable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");

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
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "No links found for "+assetName);
        }

        String partner = null;
        try {
            partner = asset.getVideoPartner();
        } catch (XWikiException e) {
            partner = "";
        }

        JSONArray jsonArray = new JSONArray();

        JSONObject json = new JSONObject();
        json.put("href", getChildReference(getRequest().getResourceRef(), "0"));
        json.put("video_id", link);
        json.put("partner", partner);

        jsonArray.add(json);

        return formatJSON(jsonArray, variant);
    }

    @Override public void acceptRepresentation(Representation representation) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");

        JSONObject json = representationToJSONObject(representation);

        String videoId;
        try {
            videoId = json.getString("videoId");
        } catch (JSONException e) {
            throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide a video id.");
        }

        String partner;
        try {
            partner = json.getString("partner");
        } catch (JSONException e) {
            partner = "viditalk";
        }

        Asset asset = null;
        try {
            asset = plugin.fetchAssetSubclassAs(assetName, VideoAsset.class);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        if (asset == null) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Asset "+assetName+" not found.");
        }

        try {
            // call the sub type asset manager to get more details
            VideoAssetManager assetManager = (VideoAssetManager) DefaultAssetManager.getAssetSubTypeManager(Constants.ASSET_CATEGORY_VIDEO);
            if (assetManager!=null) {
                assetManager.makeVideoAsset(asset, videoId, partner);
            }
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, "Could not add video");
        }

        getResponse().redirectSeeOther(getChildReference(getRequest().getResourceRef(), "0"));
    }
}