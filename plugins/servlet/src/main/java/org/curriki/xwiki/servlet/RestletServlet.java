package org.curriki.xwiki.servlet;

import com.xpn.xwiki.XWikiException;
import com.noelios.restlet.ext.servlet.ServletConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import org.curriki.xwiki.servlet.restlet.router.BaseRouter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public class RestletServlet extends BaseServlet {
    protected ServletConverter converter;
    private static final Log LOG = LogFactory.getLog(RestletServlet.class);

    @Override protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            converter = new ServletConverter(getServletContext());
            converter.getContext().getAttributes().put("XWikiContext", getXWikiContext(req, res));

            converter.setTarget(new BaseRouter(converter.getContext()));
            converter.service(req, res);
        } catch (XWikiException e) {
            throw new ServletException(e);
        }
    }
}
