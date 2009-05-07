package org.curriki.xwiki.servlet.restlet.resource.users;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.composite.RootCollectionCompositeAsset;

import java.util.Map;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONException;
import com.xpn.xwiki.XWikiException;

/**
 */
public class UserCollectionsResource extends BaseResource {
    public UserCollectionsResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        setModifiable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String forUser = (String) request.getAttributes().get("userName");

        List<String> resultList;
        Map<String,Object> results;
        try {
            resultList = plugin.fetchUserCollectionsList(forUser);
            results = plugin.fetchUserCollectionsInfo(forUser);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        JSONArray json = flattenMapToJSONArray(results, resultList, "collectionPage");

        return formatJSON(json, variant);
    }

    @Override public void storeRepresentation(Representation representation) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String forUser = (String) request.getAttributes().get("userName");

        JSONObject json = representationToJSONObject(representation);

        Asset asset;
        try {
            asset = plugin.fetchRootCollection(forUser);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        if (asset == null) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Collection for "+forUser+" not found.");
        }

        JSONArray orig;
        try {
            orig = json.getJSONArray("original");
            if (orig.isEmpty()){
                throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide the orignal order.");
            }
        } catch (JSONException e) {
            throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide the original order.");
        }

        JSONArray want;
        try {
            want = json.getJSONArray("wanted");
            if (want.isEmpty()){
                throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide a new order.");
            }
        } catch (JSONException e) {
            throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide a new order.");
        }

        if (asset instanceof RootCollectionCompositeAsset) {
            try {
                RootCollectionCompositeAsset fAsset = asset.as(RootCollectionCompositeAsset.class);
                fAsset.reorder(orig, want);
                fAsset.save(xwikiContext.getMessageTool().get("curriki.comment.reordered"));
            } catch (XWikiException e) {
                throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, "Reorder failed: "+e.getMessage(), e);
            }
        } else {
            throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, "Asset is not a root collection.");
        }

        getResponse().setEntity(represent(getPreferredVariant()));
    }
}
