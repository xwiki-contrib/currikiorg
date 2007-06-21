package org.gelc.xwiki.plugins.mdfw;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;

/**
 * Created for GELC (http://www.gelc.org)
 * User: ldubost
 * Date: 15 sept. 2006
 * Time: 13:27:22
 * Copyright 2006-2007 (c) GELC
 */
public class MetaDataFrameworkPluginAPI extends Api {
        MetaDataFrameworkPlugin plugin;

        public MetaDataFrameworkPluginAPI(MetaDataFrameworkPlugin plugin, XWikiContext context) {
            super(context);
            this.plugin = plugin;
        }

        public boolean active() {
            return true;
        }
}
