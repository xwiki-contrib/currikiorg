package org.curriki.xwiki.servlet.restlet.resource.metadata;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import com.xpn.xwiki.XWikiException;

/**
 */
public class FieldsResource extends BaseResource {
    public FieldsResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String className = (String) request.getAttributes().get("className");

        Object[] fields = null;
        try {
            fields = xwikiContext.getWiki().getDocument(className, xwikiContext).getxWikiClass().getPropertyNames();
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Class Not Found.");
        }

        JSONObject json = new JSONObject();
        JsonConfig config = new JsonConfig();
        config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        
        json.put("class", className);
        if (fields != null) {
            for (Object field : fields) {
                json.accumulate("fieldList", field);
                try {
                    json.accumulate(field.toString(), xwikiContext.getWiki().getDocument(className, xwikiContext).getxWikiClass().get(field.toString()).getClass().getCanonicalName());
                } catch (XWikiException e) {
                    json.accumulate(field.toString(), "ERROR: Can't get type");
                }
            }
        } else {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Class Not Found.");
        }

        return formatJSON(json, variant);
    }
}