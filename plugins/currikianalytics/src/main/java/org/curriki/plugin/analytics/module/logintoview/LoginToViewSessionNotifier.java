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
     * The key for NUMBER_OF_REMAINING_VIEWS_NOTIFICATIONS_VALUE in the notificationValues object (map)
     */
    public final static String NUMBER_OF_REMAINING_VIEWS_NOTIFICATIONS_VALUE = "NUMBER_OF_REMAINING_VIEWS_NOTIFICATIONS_VALUE";

    /**
     * The key for THRESHOLD_NOTIFICATION_VALUE in the notificationValues object (map)
     */
    public final static String THRESHOLD_NOTIFICATION_VALUE = "THRESHOLD_NOTIFICATION_VALUE";

    /**
     * The module this notifier is part of.
     */
    private LoginToViewAnalyticsModule loginToViewAnalyticsModule;

    public LoginToViewSessionNotifier(LoginToViewAnalyticsModule loginToViewAnalyticsModule){
        this.loginToViewAnalyticsModule = loginToViewAnalyticsModule;
    }

    /**
     * Place a value in the session which then causes to open the login dialog.
     * If the remaning number of views is 0 set a long living cookie, that indicates
     * visitors that exceeded the limit of views.
     *
     * @param notificationValues the values of the notification (see LoginToViewTrigger)
     */
    public void setNotification(Object notificationValues) {
        Integer numberOfMatches = (Integer)((Map)notificationValues).get(NUMBER_OF_MATCHES_NOTIFICATION_VALUE);
        Integer numberOfRemainingViews = (Integer)((Map)notificationValues).get(NUMBER_OF_REMAINING_VIEWS_NOTIFICATIONS_VALUE);

        LOG.warn("LoginToViewSessionNotifier: viewed=" + String.valueOf(numberOfMatches) + " remaining=" + String.valueOf(numberOfRemainingViews));
        CurrikiAnalyticsSession currentAnalyticsSession = loginToViewAnalyticsModule.getCurrentAnalyticsSession();
        currentAnalyticsSession.setHttpSessionAttribute(LOGIN_TO_VIEW_SESSION_FLAG, getNotificationMessage(notificationValues));
        currentAnalyticsSession.setHttpSessionAttribute(AFTER_LOGIN_SESSION_KEY, currentAnalyticsSession.getURIWithQueryStringOfLastRequest());

        if(currentAnalyticsSession.getCookie(LOGIN_TO_VIEW_COOKIE_NAME) == null && numberOfRemainingViews == 0){
            Cookie cookie = new Cookie(LOGIN_TO_VIEW_COOKIE_NAME, String.valueOf(numberOfMatches));
            cookie.setMaxAge(60*60*24*30);
            currentAnalyticsSession.setCookie(cookie);
        }
    }

    /**
     * Remove all values from the session this notifier ever set.
     */
    public void removeNotification() {
        LOG.warn("LoginToViewSessionNotifier: " + "Remove all existing notifications");
        loginToViewAnalyticsModule.getCurrentAnalyticsSession().removeHttpSessionAttribute(LOGIN_TO_VIEW_SESSION_FLAG);
        loginToViewAnalyticsModule.getCurrentAnalyticsSession().removeHttpSessionAttribute(AFTER_LOGIN_SESSION_KEY);
    }

    /**
     * Get the correct message from the messaage tool.
     * @param notificationValues the values of the notification (see LoginToViewTrigger)
     * @return the correct message
     */
    private String getNotificationMessage(Object notificationValues){
        Integer numberOfMatches = (Integer)((Map)notificationValues).get(NUMBER_OF_MATCHES_NOTIFICATION_VALUE);
        Integer numberOfRemainingViews = (Integer)((Map)notificationValues).get(NUMBER_OF_REMAINING_VIEWS_NOTIFICATIONS_VALUE);
        Integer threshold = (Integer)((Map)notificationValues).get(THRESHOLD_NOTIFICATION_VALUE);

        XWikiMessageTool msg = loginToViewAnalyticsModule.getCurrentAnalyticsSession().getMessageTool();
        String message = "";

        switch(numberOfRemainingViews){
            case 0:
                message = msg.get("login.view.resources.max", threshold) + " " + msg.get("login.view.message1");
                break;
            default:
                message = msg.get("login.view.resources.viewed", numberOfMatches) + " " + msg.get("login.view.resources.remaining", threshold);
                break;
        }
        return message;
    }
}