package org.curriki.plugins.googlecheckout;


import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.api.Context;
import com.xpn.xwiki.api.XWiki;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import com.xpn.xwiki.plugin.mailsender.MailSenderPluginApi;
import com.xpn.xwiki.util.AbstractXWikiRunnable;
import com.xpn.xwiki.web.Utils;
import com.xpn.xwiki.web.XWikiMessageTool;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleCheckoutPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleCheckoutPlugin.class);

    private String merchant, key, host;
    private URL checkoutURL, orderInfoURL;
            //orderInfoURL = googleCheckout.orderInfoApiEndpoint=https://sandbox.google.com/checkout/api/checkout/v2/reports/Merchant/" + merchant);

    private static final String ORDERPROP_user ="user",
            ORDERPROP_serialNumber = "serialNumber",
            ORDERPROP_lastHistoryState="lastOrderState",
            ORDERPROP_financialState = "financialState",
            ORDERPROP_fulfillmentState = "fulfillmentState",
            ORDERPROP_orderType = "orderType",
            ORDERPROP_amount = "amount",
            ORDERPROP_date = "date";
    private static ThreadLocal<NumberFormat> currencies = new ThreadLocal<NumberFormat>() {protected NumberFormat initialValue() {
            return new DecimalFormat("########.##");
        }};
    private static final String checkoutNSuri = "http://checkout.google.com/schema/2";

    private static final String DOCNAME_orderList = "GCheckout.Orders",
            DOCNAME_oldOrderList = "GCheckout.OldOrders",
            DOCNAME_orderClass = "GCheckout.GChOrder",
            DOCNAME_donationTrackClass = "Registration.DonationTrack";

    private static float MIN_CORP_AMOUNT = 75f;

    private static Namespace checkoutNS = Namespace.getNamespace("co", checkoutNSuri);

    private static Map<String,String> languages = languages = new HashMap<String, String>();
    static {
        languages.put("eng","en");
        languages.put("fra","fr");
        languages.put("rus", "ru");
        languages.put("spa", "es");
        languages.put("deu", "de");
        languages.put("por", "pt");
        languages.put("nld", "nl");
        languages.put("ces", "cs");
        languages.put("eus", "mk");
        languages.put("zho", "zh");
        languages.put("cha", "ch");
    }


    public GoogleCheckoutPlugin(String name, String className, XWikiContext xcontext) throws Exception {
        super(name, className, xcontext);

        Properties props = new Properties();
        props.load(xcontext.getWiki().getResourceAsStream("WEB-INF/googlecheckout_config.properties"));

        merchant= props.getProperty("googleCheckout.merchantID"); // e.g. "669895943580289";
        key= props.getProperty("googleCheckout.merchantKey");//"Ea0jLLapBsYxX2hRvapowg"
        checkoutURL = new URL(props.getProperty("googleCheckout.notificationApiEndpoint")); // "https://sandbox.google.com/checkout/api/checkout/v2/merchantCheckout/Donations/" + merchant
        orderInfoURL = new URL(props.getProperty("googleCheckout.orderInfoApiEndpoint"));
        host=xcontext.getWiki().Param("curriki.system.hostname"); // e.g. "hoplahup.homeip.net";

         // TODO: a few more
    }



    public String getName()
    {
        return "googleCheckout";
    }

    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context)
    {
        return new GoogleCheckoutPluginApi((GoogleCheckoutPlugin) plugin, context);
    }





    HttpClient client = null;
    private PostMethod createCheckoutPost(URL url) {
        if(client==null) {
            client = new HttpClient();
            //Protocol myhttps = new Protocol("https", new EasySSLProtocolSocketFactory());
            //client.getHostConfiguration().setHost("sandbox.google.com", 443, myhttps)
            client.getParams().setAuthenticationPreemptive(true);
            client.getState().setCredentials(
                    new AuthScope(checkoutURL.getHost(), checkoutURL.getPort(), AuthScope.ANY_REALM),
                    new UsernamePasswordCredentials(merchant, key));
        }
        String u = url.toExternalForm();
        LOG.warn("Creating a POST to " + u);
        PostMethod post = new PostMethod(u);
        post.setDoAuthentication(true);
        return post;
    }

    public String getCheckoutRedirect(String userName, String amount, String type, XWikiMessageTool msg) throws IOException {

        List<String> errors = new ArrayList<String>();
        if(userName==null) {
            errors.add("missing-username");
        }
        if(amount==null)
            errors.add("missing-amount");

        if(!errors.isEmpty()) {
            StringBuilder msgs = new StringBuilder("errors:");
            for(String error: errors) msgs.append("googlecheckout.errors.").append(error).append(' ');
            return msgs.toString();
        }

        PostMethod post = createCheckoutPost(checkoutURL);


        if(userName==null || userName.length()==0) userName = "XWikiGuest";
        if(userName.startsWith("XWiki.") && userName.length()>6) userName = userName.substring(6);
        String cartType, itemDescription;
        if("corporation".equals(type)) {
            cartType ="corporate-membership";
            itemDescription = msg.get("googlecheckout.cart.corporate-membership.details",
                                   Arrays.asList(userName,  "http://"+host));
        } else {
            cartType ="donation";
            if("XWikiGuest".equals(userName))
                itemDescription =  msg.get("googlecheckout.cart.donation.details.anonymous",
                   Arrays.asList("http://"+host));
            else itemDescription = msg.get("googlecheckout.cart.donation.details",
                    Arrays.asList(userName,  "http://"+host));
        }
        String itemName = msg.get("googlecheckout.cart."+cartType+".title");

        String cart =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\n" +
                "<checkout-shopping-cart xmlns=\""+checkoutNSuri +"\">\n" +
                "  <shopping-cart>\n" +
                "    <items>\n" +
                "      <item>\n" +
                "        <item-name>"+itemName+"</item-name>\n" +
                "        <item-description>" + itemDescription + "</item-description>\n" +
                "        <unit-price currency=\"USD\">"+amount+"</unit-price>\n" +
                "        <quantity>1</quantity>\n" +
                "      </item>\n" +
                "    </items>\n" +
                "    <merchant-private-data >Username:"+userName+" Carttype:" + cartType +
                "    </merchant-private-data>\n" +
                "  </shopping-cart>\n" +
                "  <checkout-flow-support>\n" +
                "    <merchant-checkout-flow-support>\n" +
                "      <continue-shopping-url>http://"+host+"/xwiki/bin/view/GCheckout/BackFromGCheckout?xpage=popup&amp;cartType="+ cartType +"&amp;user="+userName+"</continue-shopping-url>\n" +
                //"      <edit-cart-url >http://"+host+"/xwiki/bin/view/GCheckout/BackFromGCheckout?user="+userName+"</edit-cart-url>\n" +
                "    </merchant-checkout-flow-support>\n" +
                "  </checkout-flow-support>\n" +
                "</checkout-shopping-cart>";


        LOG.warn("Request: " + cart);
        post.setRequestBody(cart);
        post.setRequestHeader("Content-Type","application/xml;charset=UTF-8");
        post.setRequestHeader("Accept","application/xml;charset=UTF-8");

        int status = -1;
        synchronized (client) {
            status = client.executeMethod(post);
        }
        if(status!=200) throw new IllegalStateException("Error " + post.getStatusText());
        String responseString = post.getResponseBodyAsString();
        org.jdom.Element response = parseToElement(responseString);
        //initiateOrderState(response, userName);
        //def response = new XmlParser().parseText("<x><a>blabla</a></x>");
        LOG.warn("response: ");
        return ((Element) response.getChildren().get(0)).getTextTrim();// ./redirect-url/checkout-redirect/text()
    }



    public String processNotificationAPICall(HttpServletRequest request, HttpServletResponse response, XWiki xwiki, XWikiMessageTool msg, String urlToHere, XWikiContext context) {
        String serialNumber = request.getParameter("serial-number");
        String olderThreadName=Thread.currentThread().getName();
        DateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");
        Thread.currentThread().setName(Thread.currentThread().getName() + " started-" + df.format(new Date()));
        LOG.warn("Received notification from : " + request.getRemoteHost() + " params: " + request.getParameterMap());
        String processStatus = orderNotification(serialNumber, xwiki, msg, urlToHere, context);
        int status = Integer.parseInt(processStatus.substring(0,3));
        processStatus = processStatus.substring(3);
        if(status==200) {
            response.setContentType("application/xml;charset=utf-8");
            Thread.currentThread().setName(olderThreadName);
            return "<notification-acknowledgment xmlns='http://checkout.google.com/schema/2' "+
                    "serial-number='"+serialNumber+"' />";
        } else {
            Thread.currentThread().setName(olderThreadName);
            response.setStatus(status, processStatus);
            return "<error>" + processStatus + "</error>";
        }
    }


    private Element parseToElement(String completeDoc) {
        try {
            return new SAXBuilder().build(new StringReader(completeDoc)).getRootElement();
        } catch (Exception e) {
            throw new RuntimeException("Parsing issue: " + e, e);
        }
    }

    private Object[] obtainOrderHistory(String serialNumber) throws IOException {
        PostMethod post = createCheckoutPost(orderInfoURL);
        post.setRequestBody("<notification-history-request xmlns='http://checkout.google.com/schema/2'> " +
                "<serial-number>"+serialNumber+"</serial-number>" +
                "</notification-history-request>");
        int status = -1;
        synchronized (client) {
            status = client.executeMethod(post);
        }
        String responseText = post.getResponseBodyAsString(32768);
        LOG.warn(responseText);
        if(status==200) {
            Element n= parseToElement(responseText);
            return new Object[]{n, responseText};
        } else
            throw new IllegalStateException(post.getStatusCode() + " " + post.getStatusText());
    }

    // the central method of processing
    public String orderNotification(String historyStepSerialNumber, XWiki xwiki, XWikiMessageTool msg, String urlToHere, XWikiContext context) {
        try {
            Object[] r= obtainOrderHistory(historyStepSerialNumber);
            Element node = (Element) r[0];
            String fullDoc = (String) r[1];
            LOG.warn("Obtained History: " + fullDoc);
            updateOrderListDoc(node, fullDoc, historyStepSerialNumber, xwiki, msg, urlToHere, context);
            return "200 OK";
        } catch (Exception ex) {
            LOG.warn("Issue at order processing.", ex);
            String status = ex.getMessage();
            if(! (status.matches("[0-9][0-9][0-9].*")))
                return "500 " + status;
            else return status;
        }

    }

    private static Map<String,XPath> expressions = new HashMap<String, XPath>();

    private static XPath getOrPutXPath(String s) {
        try {
            XPath exp = expressions.get(s);
            if(exp==null) {
                exp = XPath.newInstance(s);
                exp.addNamespace(checkoutNS);
                expressions.put(s,exp);
            }
            return exp;
        } catch (JDOMException e) {
            e.printStackTrace();
            throw new IllegalStateException("XPath expressions should be ok.", e);
        }
    }

    private static Element selectSingleEltXPath(String s, Element elt) {
        try {
            XPath exp = getOrPutXPath(s);
            return (Element) exp.selectSingleNode(elt);
        } catch (JDOMException e) {
            e.printStackTrace();
            throw new IllegalStateException("XPath expressions should be ok.", e);
        }
    }


    private static List selectMultipleXPath(String s, Element elt) {
        try {
            XPath exp = getOrPutXPath(s);
            return exp.selectNodes(elt);
        } catch (JDOMException e) {
            e.printStackTrace();
            throw new IllegalStateException("XPath expressions should be ok.", e);
        }
    }

    /**
     *
     * @param node the received history document's root
     * @param orderStateDoc the full String (for storage)
     * @param historyStepSerialNumber the serial-number indicated
     * @param xwiki the wiki we're living in
     */
    public void updateOrderListDoc(Element node, String orderStateDoc, String historyStepSerialNumber, XWiki xwiki, XWikiMessageTool msg, String urlToHere, XWikiContext context) throws Exception {
        try {
            Document orderList = xwiki.getDocument(DOCNAME_orderList);
            String orderNumber = selectSingleEltXPath("./co:order-summary/co:google-order-number", node).getTextNormalize();
            float amount = currencies.get().parse(selectSingleEltXPath("./co:order-summary/co:order-total", node).getTextNormalize()).longValue();
            com.xpn.xwiki.api.Object orderObj = orderList.getObject(DOCNAME_orderClass,ORDERPROP_serialNumber, orderNumber);
            String userName;
            String cartType;
            String privateData = selectSingleEltXPath("//co:merchant-private-data", node).getTextTrim();
            Matcher matcher = Pattern.compile(".*Username:([^ ]*)[ ]+Carttype:([^ ]*)").matcher(privateData);
            if(!matcher.matches()) throw new IllegalArgumentException("Can't understand merchantData!");
            userName = matcher.group(1); cartType = matcher.group(2);
            if(!userName.startsWith("XWiki.")) userName = "XWiki." + userName;
            if(orderObj==null) {
                int i = orderList.createNewObject(DOCNAME_orderClass);
                orderObj = orderList.getObject(DOCNAME_orderClass, i);
                orderObj.set(ORDERPROP_serialNumber, orderNumber);
                orderObj.set(ORDERPROP_amount, amount);
                orderObj.set(ORDERPROP_user, userName);
            }

            // note last state
            orderObj.set(ORDERPROP_orderType, cartType);
            orderObj.set(ORDERPROP_lastHistoryState, orderStateDoc);
            orderObj.set(ORDERPROP_financialState,
                    selectSingleEltXPath("//co:financial-order-state",node).getTextNormalize());
            orderObj.set(ORDERPROP_fulfillmentState,
                    selectSingleEltXPath("//co:fulfillment-order-state",node).getTextNormalize());
            orderObj.set(ORDERPROP_date, new Date());

            // finished?

            // see http://code.google.com/intl/fr/apis/checkout/developer/Google_Checkout_XML_Donation_API_Notification_API.html#tag_financial-order-state
            // we should get CHARGED at some point but I always got CHARGING and received the mails
            boolean finished = ("CHARGEABLE".equals(orderObj.get(ORDERPROP_financialState)));
            userName = (String) orderObj.get(ORDERPROP_user);
            LOG.warn("financialState is " + orderObj.get(ORDERPROP_financialState));
            if(!finished) {
                LOG.warn("Saving " + orderList);
                orderList.saveWithProgrammingRights("Updating financialState " + orderNumber + " to " + orderObj.get(ORDERPROP_financialState));
            }

            if(finished) {
                LOG.warn("Finished checkout for user " + userName);
                Document userDoc = xwiki.getDocument(userName);
                com.xpn.xwiki.api.Object userObj = userDoc.getObject("XWiki.XWikiUsers");
                if(userObj!=null) {
                    archiveOrder(xwiki, orderNumber, node, msg, urlToHere, userName, cartType, amount, context);
                }

                LOG.warn("Finished successfully processing notification : " + historyStepSerialNumber);
            }
        } catch (Exception ex) {
            LOG.warn("Issue at processing order: ", ex);
        }
    }

    private static void sendConfirmationEmail(XWiki xwiki, String username, String email, String lang, String memberType, XWikiMessageTool msg, String urlToHere) {
        try {
            String emailDocName = "corporate".equals(memberType) ?
                    "CorporateRegCompleteEmail" :"MemberRegCompleteEmail";

            long time=System.currentTimeMillis();
            MailSenderPluginApi mailsender = (MailSenderPluginApi) xwiki.getPlugin("mailsender");


            Object emailDocO = null;
            URL url = new URL( new URL("http://127.0.0.1:8080")//new URL(urlToHere),
                    ,"/xwiki/bin/view/Registration/" + emailDocName + "?xpage=plain&language=" + lang + "&username=" + username);
            LOG.info("Fetching " + url + " as mail body.");
            emailDocO = url.getContent();
            if(emailDocO instanceof InputStream) {
                emailDocO = org.apache.commons.io.IOUtils.toString((InputStream) emailDocO, "utf-8");
            }
            String text = (String) emailDocO;

            LOG.warn("Sending mail to " + email + " with page " + emailDocName + '.');
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

    public String archiveOrder(XWiki xwiki, String serialNumber, Element node, XWikiMessageTool msg, String urlToHere, String userName, String cartType, float amount, XWikiContext context) throws XWikiException{
        new Thread(new GCheckoutArchivingWorker(serialNumber, node, msg, urlToHere, userName, cartType, amount, context), "GCheckoutArchiver-" + serialNumber).start();
        return "launched";
    }


    public static class GCheckoutArchivingWorker extends AbstractXWikiRunnable {

        public GCheckoutArchivingWorker(String serialNumber, Element nodeInNotifDoc, XWikiMessageTool msg, String urlToHere, String userName, String cartType, float amount, XWikiContext xcontext) {
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
                    for(Object emailElt : selectMultipleXPath("//co:email", nodeInNotifDoc)) {
                        String email = ((Element) emailElt).getTextNormalize();
                        if(email!=null && email.equals(userObj.getStringValue("email"))) {
                            if(doneEmails.contains(email)) continue;
                            doneEmails.add(email);
                            if(1==userObj.getIntValue("email_undeliverable")) {
                                LOG.warn("activating user's email " + email);
                                userObj.setIntValue("email_undeliverable",0);
                                userObj.setIntValue("active", 1);
                                xcontext.getWiki().saveDocument(userDoc, "Validating thanks to payment.", xcontext);
                                sendConfirmationEmail(new XWiki(xcontext.getWiki(), xcontext), userDoc.getName(), userObj.getStringValue("email"),
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
}