package org.curriki.xwiki.servlet.restlet.resource;

import org.restlet.resource.Resource;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.data.Reference;
import org.curriki.xwiki.plugin.curriki.CurrikiPluginApi;
import org.curriki.xwiki.plugin.curriki.CurrikiPlugin;
import com.xpn.xwiki.XWikiContext;
import net.sf.json.xml.XMLSerializer;
import net.sf.json.JSONObject;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;
import net.sf.json.JSONException;
import net.sf.json.util.JSONUtils;

import java.util.Map;
import java.io.IOException;

/**
 */
public class BaseResource extends Resource {
    protected XWikiContext xwikiContext;
    protected CurrikiPluginApi plugin;

    public BaseResource() {
        super();
    }

    public BaseResource(Context context, Request request, Response response) {
        super(context, request, response);
    }

    protected XWikiContext getXWikiContext() {
        return (XWikiContext) getContext().getAttributes().get("XWikiContext");
    }

    protected void setupXWiki() {
        xwikiContext = getXWikiContext();
        plugin = (CurrikiPluginApi) xwikiContext.getWiki().getPluginApi(CurrikiPlugin.PLUGIN_NAME, xwikiContext);
    }

    protected ResourceException error(Status status, String message) {
        getResponse().setEntity(message, MediaType.TEXT_PLAIN);
        return new ResourceException(status, message);
    }

    protected Representation formatJSON(JSON json, Variant variant) {
        Representation r = null;
        if (MediaType.APPLICATION_JSON.equals(variant.getMediaType())
            || MediaType.APPLICATION_JAVASCRIPT.equals(variant.getMediaType())
            || MediaType.TEXT_JAVASCRIPT.equals(variant.getMediaType())) {
            r = new StringRepresentation(json.toString(), variant.getMediaType());
        } else if (MediaType.APPLICATION_XML.equals(variant.getMediaType())
            || MediaType.TEXT_XML.equals(variant.getMediaType())) {
            r = new StringRepresentation(new XMLSerializer().write(json), variant.getMediaType());
        } else if (MediaType.TEXT_PLAIN.equals(variant.getMediaType())
            || MediaType.TEXT_HTML.equals(variant.getMediaType())) {
            r = new StringRepresentation(json.toString(4), variant.getMediaType());
        } else {
            getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
        }

        return r;
    }

    protected void defaultVariants(){
        getVariants().add(new Variant(MediaType.APPLICATION_JSON));
        getVariants().add(new Variant(MediaType.APPLICATION_JAVASCRIPT));
        getVariants().add(new Variant(MediaType.TEXT_JAVASCRIPT));
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
        getVariants().add(new Variant(MediaType.TEXT_XML));
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
        getVariants().add(new Variant(MediaType.TEXT_HTML));
    }

    protected JSONArray flattenMapToJSONArray(Map<String,Object> map, String itemName) {
        JSONArray json = new JSONArray();

        for (String item : map.keySet()) {
            JSONObject o = new JSONObject();
            o.put(itemName, item);
            Map<String,Object> info = (Map<String,Object>) map.get(item);
            for (String infoItem : info.keySet()) {
                o.put(infoItem, info.get(infoItem));
            }
            json.add(o);
        }

        return json;
    }

    protected JSONObject representationToJSONObject(Representation representation) throws ResourceException {
        String posted;
        try {
            posted = representation.getText();
        } catch (IOException e) {
            throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "IOException on input.");
        }
        if (!JSONUtils.mayBeJSON(posted)) {
            throw error(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, "Please PUT in JSON format.");
        }

        JSONObject json;
        try {
            JSON input = JSONSerializer.toJSON(posted);
            if (!input.isArray()){
                json = (JSONObject) input;
            } else {
                throw error(Status.CLIENT_ERROR_NOT_ACCEPTABLE, "Please send a proper JSON object.");
            }
        } catch (JSONException e){
            throw error(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, "JSON format required.");
        }

        return json;
    }

    protected Reference getChildReference(Reference parentRef, String childId) {
        if (parentRef.getIdentifier().endsWith("/")) {
            return new Reference(parentRef.getIdentifier() + childId);
        } else {
            return new Reference(parentRef.getIdentifier() + "/" + childId);
        }
    }
}
