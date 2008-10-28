package org.curriki.xwiki.servlet;

import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.web.Utils;
import com.noelios.restlet.ext.servlet.ServletConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import org.curriki.xwiki.servlet.restlet.router.BaseRouter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xwiki.container.servlet.ServletContainerInitializer;
import org.xwiki.container.servlet.ServletContainerException;
import org.xwiki.container.Container;
import org.xwiki.context.Execution;

/**
 */
public class RestletServlet extends BaseServlet {
    protected ServletConverter converter;
    private static final Log LOG = LogFactory.getLog(RestletServlet.class);

    @Override protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            converter = new ServletConverter(getServletContext());
            XWikiContext context = getXWikiContext(req, res);
            converter.getContext().getAttributes().put("XWikiContext", context);

            try {
                // We need to initialize the new components for Velocity to work
                initializeContainerComponent(context);
                converter.setTarget(new BaseRouter(converter.getContext()));
                converter.service(req, res);
            } finally {
                cleanupComponents();
            }
        } catch (XWikiException e) {
            throw new ServletException(e);
        }
    }

    protected void initializeContainerComponent(XWikiContext context)
            throws ServletException
    {
        // Initialize the Container fields (request, response, session).
        // Note that this is a bridge between the old core and the component architecture.
        // In the new component architecture we use ThreadLocal to transport the request,
        // response and session to components which require them.
        // In the future this Servlet will be replaced by the XWikiPlexusServlet Servlet.
        ServletContainerInitializer containerInitializer =
                (ServletContainerInitializer) Utils.getComponent(ServletContainerInitializer.ROLE);

        try {
            containerInitializer.initializeRequest(context.getRequest().getHttpServletRequest(),
                    context);
            containerInitializer.initializeResponse(context.getResponse().getHttpServletResponse());
            containerInitializer.initializeSession(context.getRequest().getHttpServletRequest());
        } catch (ServletContainerException e) {
            throw new ServletException("Failed to initialize Request/Response or Session", e);
        }
    }

    protected void cleanupComponents()
    {
        Container container = (Container) Utils.getComponent(Container.ROLE);
        Execution execution = (Execution) Utils.getComponent(Execution.ROLE);

        // We must ensure we clean the ThreadLocal variables located in the Container and Execution
        // components as otherwise we will have a potential memory leak.
        container.removeRequest();
        container.removeResponse();
        container.removeSession();
        execution.removeContext();
    }
}
