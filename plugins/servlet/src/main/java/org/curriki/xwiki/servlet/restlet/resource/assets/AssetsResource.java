package org.curriki.xwiki.servlet.restlet.resource.assets;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.AssetException;
import net.sf.json.JSONObject;
import net.sf.json.JSONException;
import com.xpn.xwiki.XWikiException;

/**
 */
public class AssetsResource extends BaseResource {
    public AssetsResource(Context context, Request request, Response response) {
        super(context, request, response);
        setModifiable(true);
        defaultVariants();
    }

    @Override public void acceptRepresentation(Representation representation) throws ResourceException {
        setupXWiki();

        JSONObject json = representationToJSONObject(representation);
        String parent = null;
        try {
            parent = json.getString("parent");
            if (parent.length() < 1){
                parent = null;
            }
        } catch (JSONException e) {
            // No parent key to get
        }

        String copyOf = null;
        try {
            copyOf = json.getString("copyOf");
            if (copyOf.length() < 1){
                copyOf = null;
            }
        } catch (JSONException e) {
            // No parent key to get
        }

        String publishSpace = null;
        try {
            publishSpace = json.getString("publishSpace");
            if (publishSpace.length() < 1){
                publishSpace = null;
            }
        } catch (JSONException e) {
            // No parent key to get
        }

        Asset createdPage;
        try {
            if (copyOf != null) {
                createdPage = plugin.copyAsset(copyOf, publishSpace);
            } else {
                createdPage = plugin.createAsset(parent, publishSpace);
            }
        } catch (XWikiException e) {
            if (e instanceof AssetException) {
                if (e.getCode() == AssetException.ERROR_ASSET_NOT_FOUND) {
                    throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
                } else if (e.getCode() == AssetException.ERROR_ASSET_FORBIDDEN) {
                    throw error(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
                } else {
                    throw error(Status.SERVER_ERROR_INTERNAL, e.getFullMessage());
                }
            } else {
                throw error(Status.SERVER_ERROR_INTERNAL, e.getFullMessage());
            }
        }

        getResponse().redirectSeeOther(getChildReference(getRequest().getResourceRef(), createdPage.getFullName()));
    }
}