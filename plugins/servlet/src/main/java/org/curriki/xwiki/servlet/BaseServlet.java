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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Curriki Servlet to process JavaScript (and other) requests
 */
public abstract class BaseServlet extends HttpServlet {
	private static final Log log = LogFactory.getLog(BaseServlet.class);

    protected XWikiContext getXWikiContext(HttpServletRequest req, HttpServletResponse res) throws XWikiException {
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

        XWiki xwiki = XWiki.getXWiki(context);
        XWikiURLFactory urlf = xwiki.getURLFactoryService().createURLFactory(context.getMode(), context);
        context.setURLFactory(urlf);
        XWikiVelocityRenderer.prepareContext(context);
        xwiki.prepareResources(context);

        String username = "XWiki.XWikiGuest";
        XWikiUser user = context.getWiki().checkAuth(context);
        if (user != null) {
            username = user.getUser();
        }
        context.setUser(username);

        if (context.getDoc() == null) {
            context.setDoc(new XWikiDocument("Fake", "Document"));
        }

        context.put("ajax", new Boolean(true));
        return context;
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
}
