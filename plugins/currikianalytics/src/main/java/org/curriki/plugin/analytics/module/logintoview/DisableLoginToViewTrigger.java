package org.curriki.plugin.analytics.module.logintoview;

import org.curriki.plugin.analytics.CurrikiAnalyticsSession;
import org.curriki.plugin.analytics.UrlStore;
import org.curriki.plugin.analytics.module.Notifier;
import org.curriki.plugin.analytics.module.Trigger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Trigger is just a dummy to remove all notifications
 * from the user session when the LoginToViewModule is turned of
 */
public class DisableLoginToViewTrigger extends Trigger {

    public DisableLoginToViewTrigger(List<Notifier> notifiers) {
        super(notifiers);
    }

    @Override
    public void trigger(CurrikiAnalyticsSession currikiAnalyticsSession, String referer) {
        removeNotifications(null);
    }

    @Override
    protected int match(UrlStore urlStore, String referer) {
        // Noting to do here
        return 0;
    }

    @Override
    protected void addNotifications(Object notification) {
        // Nothing to do here
    }

    @Override
    protected void removeNotifications(Object notification) {
        for(Notifier notifier : notifiers){
            Map notificationValues = new HashMap<String, Boolean>();
            notificationValues.put(LoginToViewSessionNotifier.DELETE_COOKIE_VALUE, true);
            notifier.removeNotification(notificationValues);
        }
    }
}