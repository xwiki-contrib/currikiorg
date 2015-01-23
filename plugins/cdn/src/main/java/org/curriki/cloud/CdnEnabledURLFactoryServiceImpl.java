package org.curriki.cloud;


import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.web.XWikiURLFactoryServiceImpl;

public class CdnEnabledURLFactoryServiceImpl extends XWikiURLFactoryServiceImpl {

    public CdnEnabledURLFactoryServiceImpl(XWiki xwiki) {
        super(xwiki);
        register(xwiki, XWikiContext.MODE_SERVLET, CdnEnabledURLFactory.class, "xwiki.urlfactory.servletclass");
    }

}
