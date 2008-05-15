package org.curriki.xwiki.servlet.restlet.resource.users;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;

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

        Map<String,Object> results = plugin.fetchUserGroups(forUser);
        
        JSONArray json = flattenMapToJSONArray(results, "groupSpace");

        return formatJSON(json, variant);
    }
}