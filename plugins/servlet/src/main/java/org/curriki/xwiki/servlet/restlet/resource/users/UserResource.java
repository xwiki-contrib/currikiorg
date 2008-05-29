package org.curriki.xwiki.servlet.restlet.resource.users;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;

import java.util.Map;

import net.sf.json.JSONObject;

/**
 */
public class UserResource extends BaseResource {
    public UserResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Map<String,Object> results = plugin.fetchUserInfo();

        JSONObject json = JSONObject.fromObject(results);

        return formatJSON(json, variant);
    }
}