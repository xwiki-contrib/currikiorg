package org.curriki.xwiki.servlet.restlet.router;

import org.restlet.Router;
import org.restlet.Context;
import org.curriki.xwiki.servlet.restlet.resource.DefaultResource;

/**
 */
public class BaseRouter extends Router {
    public BaseRouter(Context context) {
        super(context);
        attach("/assets", new AssetsRouter(context));
        attach("/users", new UsersRouter(context));
        attach("/groups", new GroupsRouter(context));
        attach("/metadata", new MetadataRouter(context));
        attach("/service", new ServiceRouter(context));
        attachDefault(DefaultResource.class);
    }
}