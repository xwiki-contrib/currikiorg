package org.gelc.xwiki.plugins.licence;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;

import java.util.List;


public class LicenceManagerPluginAPI  extends Api {
        LicenceManagerPlugin plugin;

    public LicenceManagerPluginAPI(LicenceManagerPlugin plugin, XWikiContext context) {
        super(context);
        this.plugin = plugin;
    }

    public String addLicence(String name, boolean compatible) throws XWikiException {
        return plugin.addLicence(name, compatible, context);
    }

    public List getNotCompatibleLicences() throws XWikiException{
        return plugin.getNotCompatibleLicences(context);
    }

    public List getCompatibleLicences() throws XWikiException{
        return plugin.getCompatibleLicences(context);    
    }

    public String getLicenceName(String name) throws XWikiException {
        return plugin.getLicenceName(name, context);
    }

}
