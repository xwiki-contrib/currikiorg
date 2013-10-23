package org.curriki.plugin.analytics.module.logintoview;

import com.xpn.xwiki.web.XWikiMessageTool;
import org.curriki.plugin.analytics.CurrikiAnalyticsSession;
import org.curriki.plugin.analytics.module.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.util.Map;

public class LoginToViewSessionNotifier implements Notifier {

    private static final Logger LOG = LoggerFactory.getLogger(LoginToViewSessionNotifier.class);

    /**
     * The name of the cookie for the long term tracking of exceeded tresholds
     */
    public static final String LOGIN_TO_VIEW_COOKIE_NAME = "LOGIN_TO_VIEW_COOKIE_NAME";

    /**
     * The key of the session flag that is used in the frontend to show the login dialog
     */
    public final static String LOGIN_TO_VIEW_SESSION_FLAG = "LOGIN_TO_VIEW_SESSION_FLAG";

    /**
     * The key of the session attributes that is needed to set to steer where a click of the
     * cancel button of the login dialogs goes.
     */
    public final static String AFTER_LOGIN_SESSION_KEY = "nologin";

    /**
     * The key for the NUMBER_OF_MATCHES_NOTIFICATION_VALUE in the notificationValues object (map)
     */
    public final static String NUMBER_OF_MATCHES_NOTIFICATION_VALUE = "NUMBER_OF_MATCHES_NOTIFICATION_VALUE";

    /**
     * The key for THRESHOLD_NOTIFICATION_VALUE in the notificationValues object (map)
     */
    public final static String THRESHOLD_NOTIFICATION_VALUE = "THRESHOLD_NOTIFICATION_VALUE";

    /**
     * The key for NUMBER_OF_WARNINGS_VALUE in the notificationValues object (map)
     */
    public final static String NUMBER_OF_WARNINGS_VALUE = "NUMBER_OF_WARNINGS_VALUE";

    /**
     *
     */
    public final static String DELETE_COOKIE_VALUE = "DELETE_COOKIE";

    /**
     * The module this notifier is part of.
     */
    private LoginToViewAnalyticsModule loginToViewAnalyticsModule;

    public LoginToViewSessionNotifier(LoginToViewAnalyticsModule loginToViewAnalyticsModule){
        this.loginToViewAnalyticsModule = loginToViewAnalyticsModule;
    }

    /**
     * Place a value in the session which then causes to open the login dialog.
     * Set a long living cookie, that holds the number of matches for the user.
     *
     * @param notificationValues the values of the notification (see LoginToViewTrigger)
     */
    public void setNotification(Object notificationValues) {
        Integer numberOfMatches = (Integer)((Map)notificationValues).get(NUMBER_OF_MATCHES_NOTIFICATION_VALUE);
        Integer threshold = (Integer)((Map)notificationValues).get(THRESHOLD_NOTIFICATION_VALUE);
        Integer numberOfWarnings = (Integer)((Map)notificationValues).get(NUMBER_OF_WARNINGS_VALUE);
        CurrikiAnalyticsSession currentAnalyticsSession = loginToViewAnalyticsModule.getCurrentAnalyticsSession();

        LOG.warn("LoginToViewSessionNotifier: numberOfMatches=" + String.valueOf(numberOfMatches) + " numberOfRemainingViews=" + String.valueOf(threshold-numberOfMatches)
        + " threshold=" + String.valueOf(threshold) + " numberOfWarnigns=" + numberOfWarnings);
        if(numberOfMatches >= (threshold-numberOfWarnings)) {
            currentAnalyticsSession.setHttpSessionAttribute(LOGIN_TO_VIEW_SESSION_FLAG, getNotificationMessage(notificationValues));
            currentAnalyticsSession.setHttpSessionAttribute(AFTER_LOGIN_SESSION_KEY, currentAnalyticsSession.getURIWithQueryStringOfLastRequest());
        }

        currentAnalyticsSession.setCookie(createLoginToViewCookie(numberOfMatches));
    }

    /**
     * Remove all values from the session this notifier ever set.
     */
    public void removeNotification(Object notification) {
        boolean delete_cookie = ((Map<String, Boolean>) notification).get(DELETE_COOKIE_VALUE);
        LOG.warn("LoginToViewSessionNotifier: " + "Remove all existing notifications");
        loginToViewAnalyticsModule.getCurrentAnalyticsSession().removeHttpSessionAttribute(LOGIN_TO_VIEW_SESSION_FLAG);
        loginToViewAnalyticsModule.getCurrentAnalyticsSession().removeHttpSessionAttribute(AFTER_LOGIN_SESSION_KEY);

        if(delete_cookie){
            loginToViewAnalyticsModule.getCurrentAnalyticsSession().removeCookie(createLoginToViewCookie(0));
        }
    }

    /**
     * Get the correct message from the messaage tool.
     * @param notificationValues the values of the notification (see LoginToViewTrigger)
     * @return the correct message
     */
    private String getNotificationMessage(Object notificationValues){
        Integer numberOfMatches = (Integer)((Map)notificationValues).get(NUMBER_OF_MATCHES_NOTIFICATION_VALUE);
        Integer threshold = (Integer)((Map)notificationValues).get(THRESHOLD_NOTIFICATION_VALUE);

        XWikiMessageTool msg = loginToViewAnalyticsModule.getCurrentAnalyticsSession().getMessageTool();
        String message = "";

        if(threshold-numberOfMatches >= 0) {
            message = msg.get("login.view.resources.viewed", numberOfMatches) + " " + msg.get("login.view.resources.remaining", threshold);
        } else {
            message = msg.get("login.view.resources.max", threshold);

        }
        return message;
    }

    /**
     * Create a cookie for LTV with the given number of matches
     * @param numberOfMatches the value for the cookie
     * @return the cookie for LTV
     */
    private Cookie createLoginToViewCookie(int numberOfMatches){
        Cookie cookie = new Cookie(LOGIN_TO_VIEW_COOKIE_NAME, String.valueOf(numberOfMatches));
        cookie.setMaxAge(60*60*24*30);
        cookie.setPath("/");
        return cookie;
    }
}