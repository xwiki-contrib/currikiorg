package org.curriki.xwiki.servlet.restlet.resource.users;

import com.xpn.xwiki.XWikiException;
import org.curriki.xwiki.servlet.restlet.router.CTVRepresentation;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;

import java.io.IOException;
import java.util.Map;

import net.sf.json.JSONArray;

/**
 */
public class UserGroupsResource extends BaseResource {
    public UserGroupsResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String forUser = (String) request.getAttributes().get("userName");

        try {
            CTVRepresentation rep = new CTVRepresentation(forUser, CTVRepresentation.Type.USER_GROUPS, xwikiContext);
            rep.init(xwikiContext);
            return rep;
        } catch (IOException e) {
            throw error(Status.CONNECTOR_ERROR_COMMUNICATION, e.getMessage());
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
        /* Map<String,Object> results = plugin.fetchUserGroups(forUser);

        JSONArray json = flattenMapToJSONArray(results, "groupSpace");

        return formatJSON(json, variant); */
    }
}