package org.curriki.plugin.activitystream.plugin;


import org.curriki.plugin.activitystream.impl.CurrikiActivityStream;
import org.xwiki.plugin.activitystream.plugin.ActivityStreamPluginApi;

import com.xpn.xwiki.XWikiContext;

public class CurrikiActivityStreamPluginApi extends ActivityStreamPluginApi {

    public CurrikiActivityStreamPluginApi(CurrikiActivityStreamPlugin plugin, XWikiContext context)
    {
        super(plugin, context);
    }

    protected CurrikiActivityStream getCurrikiActivityStream() {
        return (CurrikiActivityStream) ((CurrikiActivityStreamPlugin) getPlugin()).getActivityStream();
    }
}
