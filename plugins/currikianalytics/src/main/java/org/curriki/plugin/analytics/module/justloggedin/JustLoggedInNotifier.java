package org.curriki.plugin.analytics.module.justloggedin;

import org.curriki.plugin.analytics.CurrikiAnalyticsSession;
import org.curriki.plugin.analytics.module.Notifier;

public class JustLoggedInNotifier implements Notifier {

    JustLoggedInNotifier() {
        throw new IllegalStateException("Should not be used.");
    }

    JustLoggedInNotifier(JustLoggedInAnalyticsModule module) {
        this.module = module;
    }

    JustLoggedInAnalyticsModule module;

    static String HTTP_ATTRIBUTE_NAME = "JUST_LOGGED_IN_ATTRIBUTE";

    public void setNotification(Object notifObj) {
        CurrikiAnalyticsSession session = module.getCurrentAnalyticsSession();
        JustLoggedInNotification notification = new JustLoggedInNotification();
        notification.notifObj = notifObj;
        session.setHttpSessionAttribute(HTTP_ATTRIBUTE_NAME, notification);
    }

    public void removeNotification(Object notification) {
        CurrikiAnalyticsSession session = module.getCurrentAnalyticsSession();
        session.removeHttpSessionAttribute(HTTP_ATTRIBUTE_NAME);
    }

    public class JustLoggedInNotification {

        public Object notifObj;

        public void clear() {
            removeNotification(notifObj);
        }

    }
}
