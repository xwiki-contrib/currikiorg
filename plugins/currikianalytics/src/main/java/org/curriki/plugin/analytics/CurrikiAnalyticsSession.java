package org.curriki.plugin.analytics;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Context;
import com.xpn.xwiki.api.XWiki;
import com.xpn.xwiki.web.XWikiMessageTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

/**
 * This class in a central point for CurrikiAnalytics functions.
 * It lives in the session of every visitor and is mainly responsible for remembering views or
 * more precise visited urls with the help of the UrlStore.
 */
public class CurrikiAnalyticsSession {

    /**
     * The logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(CurrikiAnalyticsSession.class);

    /**
     * This constant is used everywhere to put the instance of this object into
     * the user session or to get it out there.
     */
    public static final String CURRIKI_ANALYTICS_OBJECT_SESSION_KEY = "CURRIKI_ANALYTICS_OBJECT_SESSION_KEY";

    /**
     * The $xwiki instance obtained from the view
     */
    private XWiki xwiki;

    /**
     * The $context instance obtained from the view
     */
    private Context context;

    /**
     * Always the current request
     */
    private HttpServletRequest request;

    /**
     * Always the current response
     */
    private HttpServletResponse response;

    /**
     * I18n tool obtained from the view
     */
    private XWikiMessageTool msg;

    /**
     * The List of all visited urls of the current users session
     */
    private UrlStore urlStore;

    /**
     * Initialize an instace of this object.
     *
     * @param xwiki - $xwiki from the view
     * @param context - $context from the view
     * @param request - $request from the view
     * @param msg - $msg from the view
     */
    public CurrikiAnalyticsSession(XWiki xwiki, Context context, HttpServletRequest request, HttpServletResponse response, XWikiMessageTool msg) {
        LOG.warn("Inited CurrikiAnalytics");
        this.xwiki = xwiki;
        this.context = context;
        this.request = request;
        this.response = response;
        this.msg = msg;
        this.loadFromSessionOrInit();
        this.saveToSession();
    }

    /**
     * Tries to load the instance of this object from the current users session with
     * CURRIKI_ANALYTICS_OBJECT_SESSION_KEY. And then copies the needed values
     * of the old object to the current one
     */
    private void loadFromSessionOrInit() {
        LOG.warn("Try to existing instance of CurrikiAnalyticsSession load from session");
        // Try to load an existent object of this class from the session
        Object o = request.getSession().getAttribute(CURRIKI_ANALYTICS_OBJECT_SESSION_KEY);
        if (o != null && o instanceof CurrikiAnalyticsSession) {
            CurrikiAnalyticsSession currikiAnalyticsSession = (CurrikiAnalyticsSession) o;
            this.copy(currikiAnalyticsSession);
        } else {
            this.init();
        }

    }

    /**
     * Initialize all relevant fields for this class
     */
    private void init() {
        LOG.warn("Init new CurrikiAnalyticsSession");
        this.urlStore = new UrlStore();
    }

    /**
     * Copy all relevant data from one instance of this class to the one this method is called on.
     * @param currikiAnalyticsSession the object to copy the relevant values from to keep the state
     */
    private void copy(CurrikiAnalyticsSession currikiAnalyticsSession) {
        LOG.warn("Loaded values from object(" + currikiAnalyticsSession.toString() + ")");
       this.urlStore = currikiAnalyticsSession.urlStore;
    }

    /**
     * Saves the object on which this methods is called to the users session
     * under the CURRIKI_ANALYTICS_OBJECT_SESSION_KEY
     */
    private void saveToSession() {
        this.request.getSession().setAttribute(CURRIKI_ANALYTICS_OBJECT_SESSION_KEY, this);
    }

    /**
     * Add the url of the current request to the end of the UrlStore
     */
    public void addCurrentRequestUrlToStore() { //TODO: Only use URI here?
        String url = this.request.getRequestURL().toString();
        LOG.warn("Add " + url + " to UrlStore");
        this.urlStore.addLast(this.request.getRequestURL().toString());
    }

    /**
     * Get the UrlStore of the current session.
     * @return UrlStore urlStore - the UrlStore of the current session
     */
    public UrlStore getUrlStore(){
        return this.urlStore;
    }

    /**
     * Wrapper around the setCookie function of HttpServletResponse
     * @param cookie the cookie to set.
     */
    public void setCookie(Cookie cookie){
        LOG.warn("Set Cookie: " + cookie.getName());
        this.response.addCookie(cookie);
    }

    /**
     * Wrapper around the getCookies function of HttpServletRequest
     * @return the cookies of the current user
     */
    public Cookie[] getCookies(){
        Cookie[] cookies = this.request.getCookies();
        if(cookies == null) cookies = new Cookie[0];
        return cookies;
    }

    /**
     * Crawl all cookies of the current user and remove the one with the
     * given name.
     * @param cookie the cookie to remove
     * @return true if
     */
    public boolean removeCookie(Cookie cookie){
        boolean removed = false;
        if(cookie != null){
            LOG.warn("Removing Cookie: " + cookie.getName());
            cookie.setMaxAge(0);
            this.setCookie(cookie);
            removed = true;
        }
        return removed;
    }

    /**
     * Get one specific cookie of the current user by cookie name
     * @param name the name of the cookie to find
     * @return the cookie with the name or null if not found
     */
    public Cookie getCookie(String name){
        Cookie[] cookies = getCookies();
        Cookie result = null;
        for (int i = 0; i < cookies.length; i++) {
            if(cookies[i].getName().equals(name)){
                result = cookies[i];
            }
        }
        return result;
    }

    /**
     * Transparent wrapper to set session attributes in the current Http-Session.
     * @param key the key to store the value with
     * @param value the value to store
     */
    public void setHttpSessionAttribute(String key, Object value){
        LOG.warn("Setting attribute in session[" + key + "]=" + value);
        this.request.getSession().setAttribute(key, value);
    }

    /**
     * Transparent wrapper to get a session attribute from the current Http-Session.
     * @param key the of the value
     * @return the value or null if not found
     */
    public Object getHttpSessionAttribute(String key){
        return this.request.getSession().getAttribute(key);
    }

    /**

     * @param key
     */
    public void removeHttpSessionAttribute(String key) {
        this.request.getSession().removeAttribute(key);
    }

    /**
     * * Try to extract the referer from the last request.
     * @return the uri of the referer of the last request or
     *         "UNKOWN REFERER" if extration was not possible
     */
    public String getRefererOfLastRequest(){
        String referer = "UNKOWN REFERER";
        if(this.request.getHeader("referer") != null) {
            referer = this.request.getHeader("referer");
        }
        LOG.warn("Referer: " + referer);
        return referer;
    }

    /**
     * Get the URI of the last request as string.
     * @return URI of last request as string or empty string if not there
     */
    public String getURIOfLastRequest(){
        String result = this.request.getRequestURI();
        if (result == null) result = "";
        return result;
    }

    /**
     * Get the URI of the last request as string (with query parameters)
     * @return the URI of the last request as string (with query parameters)
     */
    public String getURIWithQueryStringOfLastRequest() {
        String requestURI = this.request.getRequestURI();
        String queryString = this.request.getQueryString();
        return this.request.getRequestURI() + ((queryString != null) ? "?" + queryString : "");
    }

    /**
     * Get access to the xwiki message tool to translate messages
     * @return the xwiki message translation tool
     */
    public XWikiMessageTool getMessageTool(){
        return this.msg;
    }

    public Context getContext(){
        return this.context;
    }

    public String getUser(){
        return this.context.getUser();
    }
}