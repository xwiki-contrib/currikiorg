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
public class AssetResource extends BaseResource {
    public AssetResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");

        List<Property> results;
        try {
            results = plugin.fetchAssetMetadata(assetName);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        JSONObject json = new JSONObject();
        json.put("assetPage", assetName);
        if (results != null) {
            for (Property prop : results) {
                String propName = prop.getName();
                if (propName.equals("title")
                    || propName.equals("description")
                    || propName.equals("creator")
                    || propName.equals("creatorName")
                    || propName.equals("assetType")
                    || propName.equals("fullAssetType")) {
                    json.put(prop.getName(), prop.getValue());
                }
            }
        }

        return formatJSON(json, variant);
    }
}