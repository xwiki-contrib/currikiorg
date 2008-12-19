package org.curriki.xwiki.servlet.restlet.resource.assets;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import org.curriki.xwiki.plugin.asset.external.ExternalAsset;
import net.sf.json.JSONObject;
import com.xpn.xwiki.XWikiException;

/**
 */
public class ExternalResource extends BaseResource {
    public ExternalResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");
        String externalId = (String) request.getAttributes().get("externalId");
        if (!externalId.equals("0")) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "An asset may only have one link");
        }

        ExternalAsset asset;
        try {
            asset = (ExternalAsset) plugin.fetchAssetAs(assetName, ExternalAsset.class);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        if (asset == null) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "External Asset "+assetName+" not found.");
        }

        String link;
        try {
            link = asset.getLink();
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "No links found for "+assetName);
        }
        String linktext;
        try {
            linktext = asset.getLinkText();
        } catch (XWikiException e) {
            linktext = "";
        }

        JSONObject json = new JSONObject();
        json.put("link", link);
        json.put("linktext", (linktext==null) ? "" : linktext);

        return formatJSON(json, variant);
    }
}