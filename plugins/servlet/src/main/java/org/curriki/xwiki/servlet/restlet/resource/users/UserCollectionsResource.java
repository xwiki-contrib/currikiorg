package org.curriki.xwiki.servlet.restlet.resource.users;

import org.curriki.xwiki.plugin.asset.composite.CompositeAsset;
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

import java.util.ArrayList;
import java.util.HashMap;
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
        boolean full = Boolean.parseBoolean(getQuery().getFirstValue("full"));
        // TODO: differentiate the calls in full or plain modes
        if(full) throw new ResourceException(501, new UnsupportedOperationException("Can't render a full tree for user's collections."));

        String forUser = (String) request.getAttributes().get("userName");

        //
        List<String> resultList;
        JSONArray json = new JSONArray();
        try {
            resultList = plugin.fetchUserCollectionsList(forUser);
            for(String collFullName: resultList) {
                JSONObject collInfo = new JSONObject();
                collInfo.put("collectionPage", collFullName);
                CompositeAsset asset = plugin.fetchAsset(collFullName).as(CompositeAsset.class);
                //Asset asset = plugin.fetchAsset(collFullName);
                collInfo.put("revision", asset.getVersion());
                collInfo.put("collectionType", "collection") ; // ???
                collInfo.put("displayTitle", asset.getTitle());
                collInfo.put("description", asset.getDescription());
                collInfo.put("assetType",asset.getAssetType());
                // levels? ict? category? subcategory? rights? fwItems?
                List<String> subAssetList = asset.getSubassetList();
                List<Map<String,Object>> subAssets = new ArrayList<Map<String, Object>>(subAssetList.size());
                for(String subAssetFullName: subAssetList) {
                    Map<String, Object> m = new HashMap<String,Object>();
                    m.put("assetpage", subAssetFullName);
                    subAssets.add(m);
                }
                collInfo.put("children", subAssets);
                json.add(collInfo);
            }
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        // -- previously:
        //   Map<String,Object> results;
        //   results = plugin.fetchUserCollectionsInfo(forUser);
        //   JSONArray json = flattenMapToJSONArray(results, resultList, "collectionPage");

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
                throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, e.getMessage());
            }
        } else {
            throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, "Asset is not a root collection.");
        }

        getResponse().setEntity(represent(getPreferredVariant()));
    }
}
