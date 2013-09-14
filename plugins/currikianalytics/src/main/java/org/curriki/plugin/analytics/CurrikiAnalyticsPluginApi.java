package org.curriki.plugin.analytics;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Context;
import com.xpn.xwiki.api.XWiki;
import com.xpn.xwiki.plugin.PluginApi;
import com.xpn.xwiki.web.XWikiMessageTool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * XWiki Plugin API of the Curriki Analytics Plugin
 */
public class CurrikiAnalyticsPluginApi extends PluginApi<CurrikiAnalyticsPlugin> {

    public CurrikiAnalyticsPluginApi(CurrikiAnalyticsPlugin plugin, XWikiContext context) {
        super(plugin, context);
    }

    /**
     * This is the main entry point for calls to the analytics modules. It is called on every request
     * that renders a template included by curriki-analytics.vm
     *
     * @see CurrikiAnalyticsPlugin for more information
     * @param xwiki the xwiki instance injected from the view
     * @param context the context injected from the view
     * @param request the current request
     * @param response the current response
     * @param msg the xwiki message translatation tool
     */
    public void logPageView(XWiki xwiki, Context context, HttpServletRequest request, HttpServletResponse response, XWikiMessageTool msg){
        getProtectedPlugin().logPageView(xwiki, context, request, response, msg);
    }
}