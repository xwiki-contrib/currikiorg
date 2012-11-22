package org.curriki.plugins.googlecheckout;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.XWiki;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.util.AbstractXWikiRunnable;
import com.xpn.xwiki.web.Utils;
import com.xpn.xwiki.web.XWikiMessageTool;
import org.jdom.Element;
import org.xwiki.context.Execution;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 */
public class GCheckoutNotificationWorker extends AbstractXWikiRunnable implements GCheckoutConstants {

    public GCheckoutNotificationWorker(String serialNumber, Element nodeInNotifDoc, XWikiMessageTool msg, String urlToHere, String userName, String cartType, float amount, XWikiContext xcontext) {
        super(XWikiContext.EXECUTIONCONTEXT_KEY, xcontext.clone());
        this.serialNumber = serialNumber;
        this.nodeInNotifDoc = nodeInNotifDoc;
        this.userName = userName;
        this.msg = msg;
        this.urlToHere = urlToHere;
        this.amount = amount;
        this.cartType = cartType;
    }
    private String serialNumber;
    private Element nodeInNotifDoc;
    private String userName;
    private String urlToHere;
    private XWikiMessageTool msg;
    private float amount;
    private String cartType;


    @Override
    protected void runInternal() {
        try {
            XWikiContext xcontext = (XWikiContext) Utils.getComponent(Execution.class).getContext()
                    .getProperty(XWikiContext.EXECUTIONCONTEXT_KEY);

            // adjust user document
            XWikiDocument userDoc = xcontext.getWiki().getDocument(userName, xcontext);
            BaseObject userObj = userDoc.getObject("XWiki.XWikiUsers");


            if(userName!=null && nodeInNotifDoc!=null) {
                // if email is there and matches, activate
                Set<String> doneEmails = new HashSet<String>();
                for(Object emailElt : GCheckoutUtils.selectMultipleXPath("//co:email", nodeInNotifDoc)) {
                    String email = ((Element) emailElt).getTextNormalize();
                    if(email!=null && email.equals(userObj.getStringValue("email"))) {
                        if(doneEmails.contains(email)) continue;
                        doneEmails.add(email);
                        if(1==userObj.getIntValue("email_undeliverable")) {
                            LOG.warn("activating user's email " + email);
                            userObj.setIntValue("email_undeliverable",0);
                            userObj.setIntValue("active", 1);
                            xcontext.getWiki().saveDocument(userDoc, "Validating thanks to payment.", xcontext);
                            GCheckoutUtils.sendConfirmationEmail(new XWiki(xcontext.getWiki(), xcontext), userDoc.getName(), userObj.getStringValue("email"),
                                    languages.get(userObj.getStringValue("language")), userObj.getStringValue("memberType"),
                                    msg, urlToHere);
                        }
                    }
                }

                // update user-donation-object
                BaseObject donationTrackObj = userDoc.getObject(DOCNAME_donationTrackClass);
                if(donationTrackObj==null) {
                    userDoc.createNewObject(DOCNAME_donationTrackClass, xcontext);
                    donationTrackObj = userDoc.getObject(DOCNAME_donationTrackClass);
                }
                donationTrackObj.setFloatValue("lastDonated", amount);
                Date date = new Date();
                donationTrackObj.setDateValue("lastDonatedDate", date);

                float totalDonated = donationTrackObj.getFloatValue("totalDonated");
                donationTrackObj.setFloatValue("totalDonated", totalDonated + amount);

                if("corporate-membership".equals(cartType)) {
                    donationTrackObj.setFloatValue("lastCorpMembershipDonated", amount);
                    donationTrackObj.setDateValue("lastCorpMembershipDate",   date);
                    if(amount> MIN_CORP_AMOUNT)
                        donationTrackObj.setIntValue("corpMembershipValid", 1);
                } else {
                    donationTrackObj.setIntValue("corpMembershipValid", 0);
                }

                xcontext.getWiki().saveDocument(userDoc, xcontext);

            }

            // move order object to finished orders

            XWikiDocument dd1 = xcontext.getWiki().getDocument("xwiki:GCheckout.Orders", xcontext),
                    dd2=xcontext.getWiki().getDocument("xwiki:GCheckout.OldOrders", xcontext);
            LOG.warn("Obtained document " + dd1 + " and " + dd2);
            BaseObject obj1 = dd1.getObject(DOCNAME_orderClass,ORDERPROP_serialNumber, serialNumber);
            //obj1.setDocumentReference(dd2.getDocumentReference());
            dd2.addXObject(obj1.clone());
            String versionMsg = "Archiving " + serialNumber;
            xcontext.getWiki().saveDocument(dd2, versionMsg, xcontext);
            dd1.removeXObject(obj1);
            xcontext.getWiki().saveDocument(dd1, versionMsg, xcontext);
            LOG.warn("Finished " + versionMsg);


            LOG.warn("Finished archiving " + serialNumber);
        } catch (XWikiException e) {
            LOG.warn("Issue at archiving " + e);
        }
    }

}
