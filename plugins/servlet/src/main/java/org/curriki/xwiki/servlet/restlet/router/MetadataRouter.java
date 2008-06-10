package org.curriki.xwiki.servlet.restlet.router;

import org.restlet.Context;
import org.restlet.Router;
import org.restlet.util.Template;
import org.curriki.xwiki.servlet.restlet.resource.metadata.FieldsResource;
import org.curriki.xwiki.servlet.restlet.resource.metadata.FieldResource;

/**
 */
public class MetadataRouter extends Router {
    public MetadataRouter(Context context) {
        super(context);
        attach("/{className}/fields", FieldsResource.class);
        attach("/{className}/fields/{fieldName}", FieldResource.class);
    }
}