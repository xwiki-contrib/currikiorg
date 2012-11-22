package org.curriki.plugins.googlecheckout;

import com.xpn.xwiki.api.XWiki;
import com.xpn.xwiki.plugin.mailsender.MailSenderPluginApi;
import com.xpn.xwiki.web.XWikiMessageTool;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class GCheckoutUtils {
    static XPath getOrPutXPath(String s) {
        try {
            XPath exp = GCheckoutConstants.expressions.get(s);
            if(exp==null) {
                exp = XPath.newInstance(s);
                exp.addNamespace(GCheckoutConstants.checkoutNS);
                GCheckoutConstants.expressions.put(s,exp);
            }
            return exp;
        } catch (JDOMException e) {
            e.printStackTrace();
            throw new IllegalStateException("XPath expressions should be ok.", e);
        }
    }

    static Element selectSingleEltXPath(String s, Element elt) {
        try {
            XPath exp = getOrPutXPath(s);
            return (Element) exp.selectSingleNode(elt);
        } catch (JDOMException e) {
            e.printStackTrace();
            throw new IllegalStateException("XPath expressions should be ok.", e);
        }
    }

    static List selectMultipleXPath(String s, Element elt) {
        try {
            XPath exp = getOrPutXPath(s);
            return exp.selectNodes(elt);
        } catch (JDOMException e) {
            e.printStackTrace();
            throw new IllegalStateException("XPath expressions should be ok.", e);
        }
    }

    static void sendConfirmationEmail(XWiki xwiki, String username, String email, String lang, String memberType, XWikiMessageTool msg, String urlToHere) {
        try {
            String emailDocName = "corporate".equals(memberType) ?
                    "CorporateRegCompleteEmail" :"MemberRegCompleteEmail";

            long time=System.currentTimeMillis();
            MailSenderPluginApi mailsender = (MailSenderPluginApi) xwiki.getPlugin("mailsender");


            Object emailDocO = null;
            URL url = new URL( new URL("http://127.0.0.1:8080")//new URL(urlToHere),
                    ,"/xwiki/bin/view/Registration/" + emailDocName + "?xpage=plain&language=" + lang + "&username=" + username);
            GCheckoutConstants.LOG.info("Fetching " + url + " as mail body.");
            emailDocO = url.getContent();
            if(emailDocO instanceof InputStream) {
                emailDocO = org.apache.commons.io.IOUtils.toString((InputStream) emailDocO, "utf-8");
            }
            String text = (String) emailDocO;

            GCheckoutConstants.LOG.warn("Sending mail to " + email + " with page " + emailDocName + '.');
            System.out.println("Took: " + (System.currentTimeMillis()-time) + " ms to prepare email body.");
            time=System.currentTimeMillis();

            String from = msg.get("registration.email");
            if(from==null || from.length()==0) from="webmaster@curriki.org";
            if(!msg.get("registration.email.name").equals("registration.email.name"))
                from = msg.get("registration.email.name") + "<" + from + ">";

            mailsender.sendHtmlMessage(from, email, null, null,
                    xwiki.getDocument("Registration."+emailDocName).getTitle(), text, text.replaceAll("<[^>]*>",""), null);
            System.out.println("Took: " + (System.currentTimeMillis()-time) + " ms to send email.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
