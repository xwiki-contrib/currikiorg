package org.curriki.plugins.googlecheckout;


import com.sun.star.auth.UnsupportedException;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.api.XWiki;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import com.xpn.xwiki.web.XWikiMessageTool;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleCheckoutPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface, GCheckoutConstants  {

    static String merchant, key, host;
    static URL checkoutURL, orderInfoURL;
    HttpClient client = null;


    public GoogleCheckoutPlugin(String name, String className, XWikiContext xcontext) throws Exception {
        super(name, className, xcontext);

        if(merchant==null) {
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

            Properties props = new Properties();
            props.load(xcontext.getWiki().getResourceAsStream("WEB-INF/googlecheckout_config.properties"));

            merchant= props.getProperty("googleCheckout.merchantID"); // e.g. "669895943580289";
            key= props.getProperty("googleCheckout.merchantKey");//"Ea0jLLapBsYxX2hRvapowg"
            checkoutURL = new URL(props.getProperty("googleCheckout.notificationApiEndpoint")); // "https://sandbox.google.com/checkout/api/checkout/v2/merchantCheckout/Donations/" + merchant
            orderInfoURL = new URL(props.getProperty("googleCheckout.orderInfoApiEndpoint"));
            host=xcontext.getWiki().Param("curriki.system.hostname"); // e.g. "hoplahup.homeip.net";
        }


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



    private PostMethod createCheckoutPost(URL url) {
        if(client==null) {
            client = new HttpClient();
            client.getParams().setAuthenticationPreemptive(true);
            client.getState().setCredentials(
                    new AuthScope(checkoutURL.getHost(), checkoutURL.getPort(), AuthScope.ANY_REALM),
                    new UsernamePasswordCredentials(merchant, key));
        }
        String u = url.toExternalForm();
        LOG.info("Creating a POST to " + u);
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


    public String processNotificationAPICall(HttpServletRequest request, HttpServletResponse response, XWiki xwiki, XWikiMessageTool msg, String urlToHere) {
        throw new UnsupportedOperationException("Not implemented anymore.");
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

    public String orderNotification(String historyStepSerialNumber, XWiki xwiki, XWikiMessageTool msg, String urlToHere) {
        throw new UnsupportedOperationException("Not implemented anymore.");
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
            String orderNumber = GCheckoutUtils.selectSingleEltXPath("./co:order-summary/co:google-order-number", node).getTextNormalize();
            float amount = currencies.get().parse(GCheckoutUtils.selectSingleEltXPath("./co:order-summary/co:order-total", node).getTextNormalize()).longValue();
            com.xpn.xwiki.api.Object orderObj = orderList.getObject(DOCNAME_orderClass,ORDERPROP_serialNumber, orderNumber);
            String userName;
            String cartType;
            String privateData = GCheckoutUtils.selectSingleEltXPath("//co:merchant-private-data", node).getTextTrim();
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
                    GCheckoutUtils.selectSingleEltXPath("//co:financial-order-state", node).getTextNormalize());
            orderObj.set(ORDERPROP_fulfillmentState,
                    GCheckoutUtils.selectSingleEltXPath("//co:fulfillment-order-state", node).getTextNormalize());
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

    public void updateOrderListDoc(Element node, String orderStateDoc, String historyStepSerialNumber, XWiki xwiki, XWikiMessageTool msg, String urlToHere) throws Exception {
        throw new UnsupportedOperationException("Not implemented anymore.");
    }

    public String archiveOrder(XWiki xwiki, String serialNumber, Element node, XWikiMessageTool msg, String urlToHere, String userName, String cartType, float amount, XWikiContext context) throws XWikiException{
        new Thread(new GCheckoutNotificationWorker(serialNumber, node, msg, urlToHere, userName, cartType, amount, context), "GCheckoutArchiver-" + serialNumber).start();
        return "launched";
    }

    public java.lang.String archiveOrder(XWiki xwiki , String serialNumber) {
        throw new UnsupportedOperationException("Not implemented anymore.");
    }


}