package org.curriki.cloud;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.web.XWikiServletURLFactory;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.model.reference.DocumentReference;

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

/*    public URL createSkinURL(String filename, boolean forceSkin, XWikiContext context) {

    } */

    @Override
    public void init(XWikiContext context) {
        super.init(context);
        String hostnameParam = context.getWiki().Param("curriki.system.baseURL");
        if(hostnameParam!=null && hostnameParam.trim().length()>0)
            try {
                super.serverURL = new URL(hostnameParam.trim());
                LOGGER.warn("Corrected baseURL to " + serverURL);
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } else {
            LOGGER.warn("Did not correct baseURL: vcontext: " + hostnameParam);
        }
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
        if(filename==null || web==null || name==null || action==null || xwikidb==null || context==null)
            return null;
        boolean isPublic = false;
        try {
            isPublic = context.getWiki().getRightService().hasAccessLevel("view", "XWiki.XWikiGuest",
                    context.getWiki().getDocument(new DocumentReference(xwikidb, web, name), context).getPrefixedFullName(), context);
        } catch(Exception ex) {ex.printStackTrace();}
        if(isPublic && web!=null && "Temp".equals(web) || "AssetTemp".equals(web)) isPublic = false;
        URL u = super.createAttachmentURL(filename, web, name, action, querystring, xwikidb, context);
        if("download".equals(action)) {
            try {
                String p = u.toExternalForm();
                StringBuffer b = new StringBuffer();
                String cdnBaseURL = context.getWiki().Param("curriki.system.attachmentsCDNbaseURL");
                if(isPublic && cdnBaseURL!=null) {
                    int firstSlash = p.indexOf("/", 7);
                    if (firstSlash <= 7) return u;
                    b.append(cdnBaseURL).append(p.substring(firstSlash));
                } else
                    b.append(p);
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
