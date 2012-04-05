package org.curriki.plugins.googlecheckout;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.XWiki;
import com.xpn.xwiki.plugin.PluginApi;
import com.xpn.xwiki.web.XWikiMessageTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GoogleCheckoutPluginApi extends PluginApi<GoogleCheckoutPlugin> {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleCheckoutPlugin.class);

    public GoogleCheckoutPluginApi(GoogleCheckoutPlugin plugin, XWikiContext context)
    {
        super(plugin, context);
    }


    public String processNotification(HttpServletRequest request, HttpServletResponse response, XWikiMessageTool msg) throws IOException {
        try {
            return getProtectedPlugin().processNotificationAPICall(request, response, new XWiki(context.getWiki(), context), msg,
                    request.getRequestURL().toString());
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("Couldn't process notification.", e);
            return "<error>" + e + "</error>";
        }
    }

    public String archiveOrder(String serialNumber) {
        try {
            XWiki xwiki = new XWiki(context.getWiki(), context);
            return getProtectedPlugin().archiveOrder(xwiki, serialNumber);
        } catch (XWikiException e) {
            LOG.warn("error at archiving ", e);
            return "error";
        }
    }

    /**
     *
     * @param request the details of the request (user and amount is needed)
     * @return either a string "errors: " with a space separated list of error messages or a URL
     */
    public String  processCartCheckout(HttpServletRequest request, XWikiMessageTool msg) throws IOException {
        try {
            return getProtectedPlugin()
                    .getCheckoutRedirect(request.getParameter("user"), request.getParameter("amount"), request.getParameter("type"), msg);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("Couldn't launch cart-checkout.", e);
            throw new RuntimeException(e);
        }
    }

}
