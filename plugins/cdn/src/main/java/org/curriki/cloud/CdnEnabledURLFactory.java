package org.curriki.cloud;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.web.XWikiServletURLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class CdnEnabledURLFactory extends XWikiServletURLFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CdnEnabledURLFactory.class);

    @Override
    public URL createSkinURL(String filename, String skin, XWikiContext context) {
        URL u = createSkinURLImpl(filename, skin, context);
        //System.err.println("Skin file: " + filename + " requested, responded " + u);
        return u;
    }



    private URL createSkinURLImpl(String filename, String skin, XWikiContext context) {

        String cdn = context.getWiki().Param("curriki.system.CDN",null);
        StringBuffer newpath = new StringBuffer();
        if(cdn!=null)
            newpath.append(cdn);
        newpath.append('/').append(this.contextPath);
        newpath.append("skins/");
        newpath.append(skin);
        addFileName(newpath, filename, false, context);
        try {
            return new URL(getServerURL(context), newpath.toString());
        } catch (MalformedURLException e) {
            // This should not happen
            return null;
        }
    }

    public java.lang.String getURL(java.net.URL url, com.xpn.xwiki.XWikiContext context) {
        if(url==null) return null;
        return url.toExternalForm();
    }

    public URL createAttachmentURL(String filename, String web, String name, String action, String querystring,
                                   String xwikidb, XWikiContext context) {
        if(filename==null || web==null || name==null || action==null || xwikidb==null || context==null) return null;
        URL u = super.createAttachmentURL(filename, web, name, action, querystring, xwikidb, context);
        String cdnBaseURL = context.getWiki().Param("curriki.system.attachmentsCDNbaseURL");
        if(cdnBaseURL!=null) {
            try {
                String p = u.toExternalForm();
                int firstSlash = p.indexOf("/", 7);
                if (firstSlash <= 7) return u;
                StringBuffer b = new StringBuffer();
                b.append(cdnBaseURL).append(p.substring(firstSlash));
                if(p.contains("?")) b.append("&"); else b.append("?");
                b.append("v=").append(context.getWiki().getDocument(web, name, context).getVersion());
                if(LOGGER.isInfoEnabled()) LOGGER.info("createAttachmentURL: " + action + " " + web + " " + name + " " + filename + ": returning \"" + b + "\".");
                u = new URL(b.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //System.err.println("createAttachment URL "+ filename +" returning \"" + u + "\".");
        return u;
    }





    /* currently given up
    @Override
    public URL createSkinURL(String filename, String web, String name, String xwikidb, XWikiContext context) {
        StringBuffer newpath = new StringBuffer(this.contextPath);
        addServletPath(newpath, xwikidb, context);
        addAction(newpath, "skin", context);
        addSpace(newpath, web, "skin", context);
        addName(newpath, name, "skin", context);
        addFileName(newpath, filename, false, context);
        try {
            return new URL(getServerURL(xwikidb, context), newpath.toString());
        } catch (MalformedURLException e) {
            // This should not happen
            return null;
        }
    }*/

}
