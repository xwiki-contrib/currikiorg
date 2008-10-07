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
 * Used to set  a asset as Partner resource
 */
public class PartnerResource extends BaseResource {
    public PartnerResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        setModifiable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        return null;
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
        
        try {
	        com.xpn.xwiki.api.Object obj = asset.getObject("CRS.CurrikiReviewStatusClass");
	        if (obj==null) {
	            obj = asset.newObject("CRS.CurrikiReviewStatusClass");
	            obj.set("name",asset.getFullName());
	            obj.set("number",0);
	            obj.set("reviewpending", 0);
	            obj.set("status", "200");
	            asset.save();
	        }
	        else
	        {
		        asset.use("CRS.CurrikiReviewStatusClass");
		        asset.set("reviewpending", 0);
		        asset.set("status", "200");
		        asset.save();
	        }
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        String newPage = getRequest().getRootRef().toString();
        if (!newPage.endsWith("/")) {
            newPage += "/";
        }
        newPage += "assets/"+asset.getFullName();

        getResponse().redirectSeeOther(newPage);
    }
}