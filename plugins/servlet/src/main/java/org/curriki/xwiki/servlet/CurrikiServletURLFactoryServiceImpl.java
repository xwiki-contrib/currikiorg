package org.curriki.xwiki.servlet;


import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.web.XWikiURLFactoryServiceImpl;

import java.net.MalformedURLException;
import java.net.URL;

public class CurrikiServletURLFactoryServiceImpl extends XWikiURLFactoryServiceImpl {

    public CurrikiServletURLFactoryServiceImpl(XWiki xwiki) {
        super(xwiki);
        register(xwiki, XWikiContext.MODE_SERVLET, CurrikiServletURLFactory.class, "xwiki.urlfactory.servletclass");
    }

}
