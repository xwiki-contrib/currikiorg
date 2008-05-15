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
import net.sf.json.JSONObject;
import com.xpn.xwiki.XWikiException;

/**
 */
public class PublishedResource extends BaseResource {
    public PublishedResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        setModifiable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");

        Asset asset;
        try {
            asset = plugin.fetchAsset(assetName);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        JSONObject json = new JSONObject();
        json.put("published", asset.isPublished());

        return formatJSON(json, variant);
    }

    @Override public void storeRepresentation(Representation representation) throws ResourceException {
        setupXWiki();

        JSONObject json = representationToJSONObject(representation);

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");
        Asset asset;
        try {
            asset = plugin.fetchAsset(assetName);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        String space = json.getString("space");
        if (space == null || space.equals("")) {
            throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide a space name.");
        }

        Asset published;
        try {
            published = asset.publish(space);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_BAD_REQUEST, e.getFullMessage());
        }

        //JSONObject out = new JSONObject();
        //getResponse().setEntity(formatJSON(out, getPreferredVariant()));

        String newPage = getRequest().getRootRef().toString();
        if (!newPage.endsWith("/")) {
            newPage += "/";
        }
        newPage += "assets/"+published.getFullName();

        getResponse().redirectSeeOther(newPage);
    }
}