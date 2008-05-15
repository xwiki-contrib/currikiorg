package org.curriki.xwiki.servlet.restlet.resource;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import com.xpn.xwiki.XWikiContext;

/**
 */
public class DefaultResource extends BaseResource {
    public DefaultResource(Context context, Request request, Response response) {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        XWikiContext xwikiContext = getXWikiContext();

        String showInfo = getContext().getParameters().getValues("showinfo");
        if (showInfo == null || !showInfo.equals("true")){
            throw error(Status.CLIENT_ERROR_NOT_FOUND, "Page not found.");
        }

        Request request = getRequest();
        String message = "UNMATCHED Resource URI  : " + request.getResourceRef()
                + '\n' + "Root URI      : " + request.getRootRef()
                + '\n' + "Routed part   : " + request.getResourceRef().getBaseRef()
                + '\n' + "Remaining part: " + request.getResourceRef().getRemainingPart()
                + '\n' + "Identifier    : " + request.getResourceRef().getIdentifier()
                + '\n' + "User: " + xwikiContext.getUser();

        for (String key : request.getAttributes().keySet()){
            message += "\nAttribute " + key + ": " + request.getAttributes().get(key);
        }
        for (String key : getContext().getParameters().getNames()){
            message += "\nParam " + key + ": " + getContext().getParameters().getValues(key);
        }

        message += "\nQuery: " + getRequest().getEntityAsForm().getQueryString();

        Form form = getRequest().getEntityAsForm();
        for (String key : form.getNames()){
            message += "\nEntForm " + key + ": " + form.getValues(key);
        }

        Form form2 = getRequest().getResourceRef().getQueryAsForm();
        for (String key : form2.getNames()){
            message += "\nRRForm " + key + ": " + form2.getValues(key);
        }

        return new StringRepresentation(message, MediaType.TEXT_PLAIN);
    }
}