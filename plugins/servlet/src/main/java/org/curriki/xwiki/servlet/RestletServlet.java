package org.curriki.xwiki.servlet;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.user.api.XWikiUser;
import com.xpn.xwiki.render.XWikiVelocityRenderer;
import com.xpn.xwiki.web.XWikiResponse;
import com.xpn.xwiki.web.XWikiRequest;
import com.xpn.xwiki.web.XWikiEngineContext;
import com.xpn.xwiki.web.XWikiServletRequest;
import com.xpn.xwiki.web.Utils;
import com.xpn.xwiki.web.XWikiURLFactory;
import com.xpn.xwiki.web.XWikiServletContext;
import com.xpn.xwiki.web.XWikiServletResponse;
import com.noelios.restlet.ext.servlet.ServletConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
                converter.setTarget(new BaseRouter(converter.getContext()));
                converter.service(req, res);
            } finally {
                cleanupComponents();
            }
        } catch (XWikiException e) {
            throw new ServletException(e);
        }
    }

    private Object generateDummy(Class someClass) {
        ClassLoader loader = someClass.getClassLoader();
        InvocationHandler handler = new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return null;
                }
            };
        Class[] interfaces = new Class[] {someClass};
        return Proxy.newProxyInstance(loader, interfaces, handler);
    }

    protected XWikiContext getXWikiContext(HttpServletRequest req, HttpServletResponse res) throws XWikiException, ServletException {
        XWikiEngineContext engine;

        ServletContext sContext = null;
        try {
            sContext = getServletContext();
        } catch (Exception ignore) { }
        if (sContext != null) {
            engine = new XWikiServletContext(sContext);
        } else {
            // use fake server context (created as dynamic proxy)
            ServletContext contextDummy = (ServletContext)generateDummy(ServletContext.class);
            engine = new XWikiServletContext(contextDummy);
        }

        XWikiRequest  request = new XWikiServletRequest(req);
        XWikiResponse response = new XWikiServletResponse(res);
        XWikiContext context = Utils.prepareContext("", request, response, engine);
        context.setMode(XWikiContext.MODE_SERVLET);
        context.setDatabase("xwiki");

        // We need to initialize the new components for Velocity to work
        initializeContainerComponent(context);

        XWiki xwiki = XWiki.getXWiki(context);
        XWikiURLFactory urlf = xwiki.getURLFactoryService().createURLFactory(context.getMode(), context);
        context.setURLFactory(urlf);
        // TODO: Fix velocity init in servlet
        // XWikiVelocityRenderer.prepareContext(context);
        xwiki.prepareResources(context);

        String username = "XWiki.XWikiGuest";
        XWikiUser user = context.getWiki().checkAuth(context);
        if (user != null) {
            username = user.getUser();
        }
        context.setUser(username);

        // Give servlet "programming" rights
        XWikiDocument rightsDoc = context.getWiki().getDocument("XWiki.XWikiPreferences", context);
        context.put("sdoc", rightsDoc);

        if (context.getDoc() == null) {
            context.setDoc(new XWikiDocument("Fake", "Document"));
        }

        context.put("ajax", new Boolean(true));
        return context;
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
                (ServletContainerInitializer) Utils.getComponent(ServletContainerInitializer.class);

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
        Container container = (Container) Utils.getComponent(Container.class);
        Execution execution = (Execution) Utils.getComponent(Execution.class);

        // We must ensure we clean the ThreadLocal variables located in the Container and Execution
        // components as otherwise we will have a potential memory leak.
        container.removeRequest();
        container.removeResponse();
        container.removeSession();
        execution.removeContext();
    }
}
