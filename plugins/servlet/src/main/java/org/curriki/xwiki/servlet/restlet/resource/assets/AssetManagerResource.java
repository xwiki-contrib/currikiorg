package org.curriki.xwiki.servlet.restlet.resource.assets;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;

import net.sf.json.JSONObject;
import com.xpn.xwiki.api.Property;
import com.xpn.xwiki.XWikiException;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;

/**
 */
public class AssetManagerResource extends BaseResource {
    public AssetManagerResource(Context context, Request request, Response response) {
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

        /**
         * this json parameter indicate the action to execute
         */
        String action = json.getString("action");
        
        if (action!=null)
        {
	        if (action.equalsIgnoreCase("setAsterixReview"))
	        {
	        	setAsterixReview(json.getString("asterixReviewValue"));
	        }
	        else
	        if (action.equalsIgnoreCase("removeAsterixReview"))
	        {
	        	removeAsterixReview();
	        }
        }
    }

    /**
     * remove the asterix review of a resource
     * @throws ResourceException
     */
	private void removeAsterixReview()  throws ResourceException {
		Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");
        Asset asset;
        try {
            asset = plugin.fetchAsset(assetName);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        
        asset.use("CRS.CurrikiReviewStatusClass");
        String asterixReviewValue = (String) asset.getValue("status");
        asset.set("status", "100");
        try {
            List<String> crsvalue = new ArrayList<String>(1);
            crsvalue.add(xwikiContext.getMessageTool().get("curriki.crs.review.setas"+asterixReviewValue));
            asset.save(xwikiContext.getMessageTool().get("curriki.comment.crsvalueremoved", crsvalue));
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

	/**
	 * set the asterix review of a resource
	 * @param asterixReviewValue = new asterix review value
	 * @throws ResourceException
	 */
	private void setAsterixReview(String asterixReviewValue) throws ResourceException {
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
	            obj.set("status", "100");
	            asset.save();
	        }
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        
        asset.use("CRS.CurrikiReviewStatusClass");
        asset.set("status", asterixReviewValue);
        asset.set("reviewpending", 0);
        asset.set("lastreview_user", xwikiContext.getUser());
        asset.set("lastreview_date", xwikiContext.getWiki().formatDate(new Date(), "MM/dd/yyyy HH:mm:ss", xwikiContext));
        try {
            List<String> crsvalue = new ArrayList<String>(1);
            crsvalue.add(xwikiContext.getMessageTool().get("curriki.crs.review.setas"+asterixReviewValue));
            asset.save(xwikiContext.getMessageTool().get("curriki.comment.crsvalueadded", crsvalue));
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