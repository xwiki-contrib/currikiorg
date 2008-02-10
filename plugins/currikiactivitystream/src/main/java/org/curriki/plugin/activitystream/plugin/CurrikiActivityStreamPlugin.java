package org.curriki.plugin.activitystream.plugin;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import org.xwiki.plugin.activitystream.plugin.ActivityStreamPlugin;
import org.curriki.plugin.activitystream.impl.CurrikiActivityStream;


public class CurrikiActivityStreamPlugin extends ActivityStreamPlugin {

    public CurrikiActivityStreamPlugin(String name, String className, XWikiContext context)
    {
        super(name, className, context);
        setActivityStream(new CurrikiActivityStream());       
    }

    public String getName() {
        return "activitystream";
    }



    /**
     * Gets the activity plugin Api
     * @param plugin The plugin interface
     * @param context Xwiki context
     * @return                              
     */
    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
        return new CurrikiActivityStreamPluginApi((CurrikiActivityStreamPlugin) plugin, context);
    }

}
