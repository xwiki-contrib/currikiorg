package org.curriki.plugin.analytics;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.api.Context;
import com.xpn.xwiki.api.XWiki;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import com.xpn.xwiki.web.XWikiMessageTool;
import org.curriki.plugin.analytics.module.AnalyticsModule;
import org.curriki.plugin.analytics.module.justloggedin.JustLoggedInAnalyticsModule;
import org.curriki.plugin.analytics.module.logintoview.LoginToViewAnalyticsModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * The main class of the CurrikiAnalyticsPlugin.
 * Mainly responsible to hold a list of AnalyticsModules and let them crawl the user session
 * when a page view was logged.
 *
 * @see AnalyticsModule
 */
public class CurrikiAnalyticsPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface {

    private static final Logger LOG = LoggerFactory.getLogger(CurrikiAnalyticsPlugin.class);

    private Map<String, AnalyticsModule> analyticsModules;

    public CurrikiAnalyticsPlugin(String name, String className, XWikiContext context) {
        super(name, className, context);
        LOG.warn("CurrikiAnalyticsPlugin initialized");
        analyticsModules = new HashMap<String,AnalyticsModule>();
        initModules(context);
    }

    private void initModules(XWikiContext context) {
        LOG.warn("Initialize AnalyticsModules");
        analyticsModules.put(LoginToViewAnalyticsModule.NAME, new LoginToViewAnalyticsModule(context));
        //analyticsModules.put(JustLoggedInAnalyticsModule.NAME, new JustLoggedInAnalyticsModule(context));
    }

    public String getName()
    {
        return "currikianalytics";
    }

    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context)
    {
        return new CurrikiAnalyticsPluginApi((CurrikiAnalyticsPlugin) plugin, context);
    }

    public void logPageView(XWiki xwiki, Context context, HttpServletRequest request, HttpServletResponse response, XWikiMessageTool msg) {
        LOG.warn("Logging page view for Analytics");
        CurrikiAnalyticsSession currikiAnalyticsSession = new CurrikiAnalyticsSession(xwiki, context, request, response, msg);

        //Don't log things from the DocChangeController
        if(currikiAnalyticsSession.getRefererOfLastRequest().contains("/xwiki/bin/view/Search2/DocChangeController")
           || currikiAnalyticsSession.getURIOfLastRequest().contains("/xwiki/bin/view/Search2/DocChangeController")) {
            return;
        }

        currikiAnalyticsSession.addCurrentRequestUrlToStore();

        // Let all AnalyticsModules crawl the current UrlStore of the users session
        LOG.warn(analyticsModules.size() + " modules active");
        for(String moduleName : analyticsModules.keySet()){
            AnalyticsModule analyticsModule = analyticsModules.get(moduleName);
            analyticsModule.setCurrentAnalyticsSession(currikiAnalyticsSession);
            analyticsModule.evaluateTriggers(currikiAnalyticsSession);
        }
    }
}