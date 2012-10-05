package org.curriki.plugins.googlecheckout;

import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public interface GCheckoutConstants {


    Logger LOG = LoggerFactory.getLogger(GoogleCheckoutPlugin.class);
    String ORDERPROP_user ="user";
    String ORDERPROP_serialNumber = "serialNumber";
    String ORDERPROP_lastHistoryState="lastOrderState";
    String ORDERPROP_financialState = "financialState";
    String ORDERPROP_fulfillmentState = "fulfillmentState";
    String ORDERPROP_orderType = "orderType";
    String ORDERPROP_amount = "amount";
    String ORDERPROP_date = "date";
    ThreadLocal<NumberFormat> currencies = new ThreadLocal<NumberFormat>() {protected NumberFormat initialValue() {
            return new DecimalFormat("########.##");
        }};
    String checkoutNSuri = "http://checkout.google.com/schema/2";
    String DOCNAME_orderList = "GCheckout.Orders";
    String DOCNAME_oldOrderList = "GCheckout.OldOrders";
    String DOCNAME_orderClass = "GCheckout.GChOrder";
    String DOCNAME_donationTrackClass = "Registration.DonationTrack";
    float MIN_CORP_AMOUNT = 75f;
    Namespace checkoutNS = Namespace.getNamespace("co", checkoutNSuri);
    Map<String,String> languages = new HashMap<String, String>();

    static Map<String,XPath> expressions = new HashMap<String, XPath>();
}
