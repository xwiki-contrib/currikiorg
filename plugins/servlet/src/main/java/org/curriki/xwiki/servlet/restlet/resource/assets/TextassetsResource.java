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
import org.curriki.xwiki.plugin.asset.AssetException;
import org.curriki.xwiki.plugin.asset.text.TextAsset;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.xpn.xwiki.XWikiException;

/**
 */
public class TextassetsResource extends BaseResource {
    public TextassetsResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        setModifiable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");

        TextAsset asset = null;
        try {
            asset = (TextAsset) plugin.fetchAssetAs(assetName, TextAsset.class);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        if (asset == null) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Asset "+assetName+" not found.");
        }

        Long type = null;
        String content = null;
        try {
            content = asset.getText();
            type = asset.getType();
        } catch (AssetException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Asset "+assetName+" does not contain any texts.");
        }

        JSONArray jsonArray = new JSONArray();

        JSONObject json = new JSONObject();
        json.put("href", getChildReference(getRequest().getResourceRef(), "0"));
        json.put("type", type);
        json.put("text", content);

        jsonArray.add(json);

        return formatJSON(jsonArray, variant);
    }

    @Override public void acceptRepresentation(Representation representation) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");

        JSONObject json = representationToJSONObject(representation);

        String content = json.getString("text");
        if (content == null) {
            throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide text.");
        }

        Long type = null;
        try {
            type = new Long(json.getString("type"));
        } catch (NumberFormatException e) {
            throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide the text type.");
        }

        Asset asset = null;
        try {
            asset = plugin.fetchAsset(assetName);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        if (asset == null) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Asset "+assetName+" not found.");
        }

        try {
            asset.makeTextAsset(type, content);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, "Could not add text");
        }

        getResponse().redirectSeeOther(getChildReference(getRequest().getResourceRef(), "0"));
    }
}