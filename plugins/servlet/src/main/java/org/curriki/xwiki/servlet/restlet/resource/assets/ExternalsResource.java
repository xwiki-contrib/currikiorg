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
import org.curriki.xwiki.plugin.asset.external.ExternalAsset;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONException;
import com.xpn.xwiki.XWikiException;

/**
 */
public class ExternalsResource extends BaseResource {
    public ExternalsResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        setModifiable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");

        ExternalAsset asset = null;
        try {
            asset = (ExternalAsset) plugin.fetchAssetAs(assetName, ExternalAsset.class);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        if (asset == null) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "External Asset "+assetName+" not found.");
        }

        String link = null;
        try {
            link = asset.getLink();
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "No links found for "+assetName);
        }

        JSONArray jsonArray = new JSONArray();

        JSONObject json = new JSONObject();
        json.put("href", getChildReference(getRequest().getResourceRef(), "0"));
        json.put("link", link);
        
        jsonArray.add(json);

        return formatJSON(jsonArray, variant);
    }

    @Override public void acceptRepresentation(Representation representation) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");

        JSONObject json = representationToJSONObject(representation);
        String link;
        try {
            link = json.getString("link");
        } catch (JSONException e) {
            throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide a link.");
        }

        Asset asset;
        try {
            asset = plugin.fetchAsset(assetName);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        if (asset == null) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "External Asset "+assetName+" not found.");
        }

        try {
            asset.makeExternal(link);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, "Could not add external link");
        }

        getResponse().redirectSeeOther(getChildReference(getRequest().getResourceRef(), "0"));
    }
}