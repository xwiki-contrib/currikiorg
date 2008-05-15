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
import java.util.Vector;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
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

        Vector<Map<String,Object>> results = null;

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

        String page = json.getString("page");
        if (page == null) {
            throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "You must provide a page name.");
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

        Long order;
        if (asset instanceof FolderCompositeAsset) {
            try {
                FolderCompositeAsset fAsset = asset.as(FolderCompositeAsset.class);
                order = fAsset.addSubasset(page);
                fAsset.save(xwikiContext.getMessageTool().get("curriki.comment.addsubasset"));
            } catch (XWikiException e) {
                // Should never occur
                throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, "Asset is not a Folder");
            }
        } else {
            try {
                asset.makeFolder(page);
                order = new Long(0);
            } catch (XWikiException e) {
                throw error(Status.CLIENT_ERROR_PRECONDITION_FAILED, "Could not add external link");
            }
        }

        getResponse().redirectSeeOther(getChildReference(getRequest().getResourceRef(), order.toString()));
    }
}