package org.curriki.xwiki.servlet.restlet.resource.assets;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;

import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import com.xpn.xwiki.XWikiException;

/**
 */
public class NominateResource extends BaseResource {
    public NominateResource(Context context, Request request, Response response) {
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
        //json.put("nominated", asset.isPublished());

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

        try {
        com.xpn.xwiki.api.Object obj = asset.getObject("CRS.CurrikiReviewStatusClass");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String sdate = formatter.format(new Date());
        String suser =  this.xwikiContext.getUser();
        if (obj==null) {
            obj = asset.newObject("CRS.CurrikiReviewStatusClass");
            obj.set("name",asset.getFullName());
            obj.set("number",0);
        }
        obj.set("nomination_user", suser);
        obj.set("nomination_date", sdate);
        String comments = json.getString("comments");
        obj.set("nomination_comment", comments);
        obj.set("reviewpending", "1");

        asset.save("save CRS nomination");

        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_BAD_REQUEST, e.getFullMessage());
        }

        String newPage = getRequest().getRootRef().toString();
        if (!newPage.endsWith("/")) {
            newPage += "/";
        }
        newPage += "assets/"+asset.getFullName();

        getResponse().redirectSeeOther(newPage);
    }
}
