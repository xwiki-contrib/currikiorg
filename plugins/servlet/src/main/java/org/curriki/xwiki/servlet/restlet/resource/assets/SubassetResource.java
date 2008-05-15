package org.curriki.xwiki.servlet.restlet.resource.assets;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import org.curriki.xwiki.plugin.asset.composite.FolderCompositeAsset;
import org.curriki.xwiki.plugin.asset.AssetException;
import net.sf.json.JSONObject;
import com.xpn.xwiki.XWikiException;

import java.util.Map;

/**
 */
public class SubassetResource extends BaseResource {
    public SubassetResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");
        String subassetIdString = (String) request.getAttributes().get("subassetId");
        Long subassetId = new Long(subassetIdString);

        FolderCompositeAsset asset = null;
        try {
            asset = (FolderCompositeAsset) plugin.fetchAssetAs(assetName, FolderCompositeAsset.class);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        if (asset == null) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Asset "+assetName+" not found.");
        }

        Map<String,Object> link = null;
        try {
            link = asset.getSubassetInfo(subassetId);
        } catch (AssetException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "SubAsset "+subassetIdString+" not found for "+assetName+".");
        }

        JSONObject json = new JSONObject();
        json.put("link", link);

        return formatJSON(json, variant);
    }
}