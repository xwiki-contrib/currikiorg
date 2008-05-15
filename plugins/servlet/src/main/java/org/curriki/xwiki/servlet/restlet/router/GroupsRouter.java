package org.curriki.xwiki.servlet.restlet.router;

import org.restlet.Context;
import org.restlet.Router;
import org.curriki.xwiki.servlet.restlet.resource.groups.GroupCollectionsResource;

/**
 */
public class GroupsRouter extends Router {
    public GroupsRouter(Context context) {
        super(context);
        attach("/{groupName}/collections", GroupCollectionsResource.class);
    }
}
