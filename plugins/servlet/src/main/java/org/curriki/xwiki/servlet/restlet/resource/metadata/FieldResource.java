package org.curriki.xwiki.servlet.restlet.resource.metadata;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import net.sf.json.JsonConfig;
import net.sf.json.JSONObject;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.PropertyInterface;
import com.xpn.xwiki.objects.classes.BaseClass;

import java.util.Map;

/**
 */
public class FieldResource extends BaseResource {
    public FieldResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String className = (String) request.getAttributes().get("className");
        String fieldName = (String) request.getAttributes().get("fieldName");

        JSONObject json = new JSONObject();

        PropertyInterface field = null;
        try {
            BaseClass xwikiClass = xwikiContext.getWiki().getDocument(className, xwikiContext).getxWikiClass();
            if (xwikiClass == null){
                throw error(Status.CLIENT_ERROR_NOT_FOUND, "Class Not Found.");
            }
            field = xwikiClass.get(fieldName);
            if (field == null) {
                throw error(Status.CLIENT_ERROR_NOT_FOUND, "Class Field Not Found.");
            }
            String fieldType = field.getClass().getCanonicalName();
            String shortFieldType = fieldType.replaceFirst("^com\\.xpn\\.xwiki\\.objects\\.classes\\.", "");
            shortFieldType = shortFieldType.replaceFirst("Class$", "");
            json.put("className", className);
            json.put("fieldName", fieldName);
            json.put("fieldType", fieldType);
            json.put("shortFieldType", shortFieldType);
            if (shortFieldType.equals("Boolean")) {
                // No extra info
            } else if (shortFieldType.equals("Date")) {
                // No extra info
            } else if (shortFieldType.equals("DBList")) {
                json.put("allowedValues", ((com.xpn.xwiki.objects.classes.DBListClass) field).getList(xwikiContext));
            } else if (shortFieldType.equals("DBTreeList")) {
                JsonConfig config = new JsonConfig();
                config.setExcludes(new String[] {"value"});
                Map map = ((com.xpn.xwiki.objects.classes.DBTreeListClass) field).getTreeMap(xwikiContext);

                // The XML representation cannot have an empty name
                Object root = map.get("");
                if (root != null) {
                    map.remove("");
                    map.put("TREEROOTNODE", root);
                }
                
                // TODO:  The XML representation cannot use & in names

                json.accumulate("allowedValueMap", map, config);
            } else if (shortFieldType.equals("StaticList")) {
                json.put("allowedValues", ((com.xpn.xwiki.objects.classes.StaticListClass) field).getList(xwikiContext));
            } else if (shortFieldType.equals("String")) {
                // No extra info
            } else if (shortFieldType.equals("TextArea")) {
                // No extra info
            }
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Class Information Not Found.");
        }

        return formatJSON(json, variant);
    }
}