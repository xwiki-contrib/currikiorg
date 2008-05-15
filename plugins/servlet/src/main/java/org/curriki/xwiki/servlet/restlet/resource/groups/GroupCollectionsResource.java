package org.curriki.xwiki.servlet.restlet.resource.groups;

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
public class GroupCollectionsResource extends BaseResource {
    public GroupCollectionsResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String forGroup = (String) request.getAttributes().get("groupName");

        Map<String,Object> results = plugin.fetchGroupCollectionsInfo(forGroup);

        JSONArray json = flattenMapToJSONArray(results, "collectionPage");

        return formatJSON(json, variant);
    }
}