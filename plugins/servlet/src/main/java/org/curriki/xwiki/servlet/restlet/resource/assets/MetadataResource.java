package org.curriki.xwiki.servlet.restlet.resource.assets;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;

import net.sf.json.JSONObject;
import com.xpn.xwiki.api.Property;
import com.xpn.xwiki.XWikiException;

import java.util.List;

/**
 */
public class MetadataResource extends BaseResource {
    public MetadataResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        setModifiable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");

        List<Property> results = null;
        try {
            results = plugin.fetchAssetMetadata(assetName);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        JSONObject json = new JSONObject();
        for (Property prop : results) {
            json.put(prop.getName(), prop.getValue());
        }

        return formatJSON(json, variant);
    }

    @Override public void storeRepresentation(Representation representation) throws ResourceException {
        setupXWiki();

        JSONObject json = representationToJSONObject(representation);

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");
        List<Property> metadata = null;
        try {
            metadata = plugin.fetchAssetMetadata(assetName);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        JSONObject out = new JSONObject();

        for (Property prop : metadata){
            if (json.get(prop.getName()) != null){
                out.put(prop.getName(), prop.getValue());
            }
        }

        getResponse().setEntity(formatJSON(out, getPreferredVariant()));
    }
}