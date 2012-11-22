package org.curriki.xwiki.servlet.restlet.resource.assets;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import org.curriki.xwiki.plugin.asset.text.TextAsset;
import net.sf.json.JSONObject;
import com.xpn.xwiki.XWikiException;

/**
 */
public class TextassetResource extends BaseResource {
    public TextassetResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String assetName = (String) request.getAttributes().get("assetName");
        String textId = (String) request.getAttributes().get("textId");
        // if (!textId.equals("0")) {
        //    throw error(Status.CLIENT_ERROR_NOT_FOUND, "An asset may only have one viditalk movie");
        // }

        TextAsset asset = null;
        try {
            asset = (TextAsset) plugin.fetchAssetAs(assetName, TextAsset.class);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        if (asset == null) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Asset " + assetName + " not found.");
        }

        String content = null;
        String syntax = null;
        try {
            content = asset.getText();
            syntax = asset.getTextSyntax();
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "No texts found for "+assetName);
        }

        JSONObject json = new JSONObject();
        json.put("text", content);
        json.put("syntax", syntax);

        return formatJSON(json, variant);
    }
}
