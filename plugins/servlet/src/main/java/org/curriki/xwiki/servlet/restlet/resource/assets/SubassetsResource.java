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
import org.curriki.xwiki.plugin.asset.composite.FolderCompositeAsset;

import java.util.Map;
import java.util.List;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import com.xpn.xwiki.XWikiException;

/**
 */
public class SubassetsResource extends BaseResource {
    public SubassetsResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        setModifiable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");

        List<Map<String,Object>> results = null;

        try {
            FolderCompositeAsset doc = (FolderCompositeAsset) plugin.fetchAssetAs(assetName, FolderCompositeAsset.class);
            if (doc != null) {
                FolderCompositeAsset fAsset = doc.as(FolderCompositeAsset.class);
                results = fAsset.getSubassetsInfo();
            }
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        JSONArray json = JSONArray.fromObject(results);

        return formatJSON(json, variant);
    }

    @Override public void acceptRepresentation(Representation representation) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");

        JSONObject json = representationToJSONObject(representation);

        String page;
        try {
            page = json.getString("page");
            if (page.equals("")){
                page = null;
            }
        } catch (JSONException e) {
            page = null;
        }

        String collectionType;
        try {
            collectionType = json.getString("collectionType");
            if (collectionType.equals("")){
                collectionType = "folder";
            }
        } catch (JSONException e) {
            collectionType = "folder";
        }

        Long order;
        try {
            order = json.getLong("order");
        } catch (JSONException e) {
            order = (long) -1;
        }

        Asset asset;
        try {
            asset = plugin.fetchAsset(assetName);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        if (asset == null) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Asset "+assetName+" not found.");
        }

        if (asset instanceof FolderCompositeAsset) {
            if (page == null) {
                throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide a page name.");
            }

            try {
                FolderCompositeAsset fAsset = asset.as(FolderCompositeAsset.class);
                order = fAsset.insertSubassetAt(page, order);
                fAsset.save(xwikiContext.getMessageTool().get("curriki.comment.addsubasset"));
            } catch (XWikiException e) {
                throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, e.getMessage());
            }
        } else {
            try {
                if (collectionType.equals("collection")) {
                    asset.makeCollection(page);
                    order = (long) 0;
                } else {
                    asset.makeFolder(page);
                    order = (long) 0;
                }
            } catch (XWikiException e) {
                throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, e.getMessage());
            }
        }

        if (page == null) {
            getResponse().redirectSeeOther(getRequest().getResourceRef());
        } else {
            getResponse().redirectSeeOther(getChildReference(getRequest().getResourceRef(), order.toString()));
        }
    }

    @Override public void storeRepresentation(Representation representation) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");

        JSONObject json = representationToJSONObject(representation);

        Asset asset;
        try {
            asset = plugin.fetchAsset(assetName);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        if (asset == null) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Asset "+assetName+" not found.");
        }

        JSONArray orig = null;
        if (json.containsKey("original")) {
            try {
                orig = json.getJSONArray("original");
                if (orig.isEmpty()){
                    throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide the orignal order.");
                }
            } catch (JSONException e) {
                throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide the original order.");
            }
        }

        String rev = "";
        if (json.containsKey("previousRevision")) {
            try {
                rev = json.getString("previousRevision");
                if (rev.length() == 0){
                    throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide the previous revision number.");
                }
            } catch (JSONException e) {
                throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide the previous revision number.");
            }
        }

        String logMsg;
        if (json.containsKey("logMessage")) {
            logMsg = json.getString("logMessage");
        } else {
            logMsg = xwikiContext.getMessageTool().get("curriki.comment.reordered");
        }

        JSONArray want;
        try {
            want = json.getJSONArray("wanted");
        } catch (JSONException e) {
            throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide a new order.");
        }

        if (asset instanceof FolderCompositeAsset) {
            try {
                FolderCompositeAsset fAsset = asset.as(FolderCompositeAsset.class);
                if (json.containsKey("original")) {
                    fAsset.reorder(orig, want);
                } else if (json.containsKey("previousRevision")) {
                    fAsset.reorder(rev, want);
                } else if (json.containsKey("ignorePreviousRevision")) {
                    fAsset.setSubassets(want);
                } else {
                    throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, "You must provide previous revision information.");
                }
                fAsset.save(logMsg);
            } catch (XWikiException e) {
                throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, "Save failed: "+e.getMessage(), e);
            }
        } else {
            throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, "Asset is not a folder.");
        }

        // IE7 has a bug where a redirect still tries to use PUT
        //getResponse().redirectSeeOther(getRequest().getResourceRef());
        getResponse().setEntity(represent(getPreferredVariant()));
    }
}
