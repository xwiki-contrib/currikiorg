package org.curriki.xwiki.servlet.restlet.router;

import org.restlet.Context;
import org.restlet.Router;
import org.curriki.xwiki.servlet.restlet.resource.DefaultResource;

/**
 */
public class ServiceRouter extends Router {
    public ServiceRouter(Context context) {
        super(context);
        attachDefault(DefaultResource.class);
    }
}