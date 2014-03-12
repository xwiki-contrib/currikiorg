package org.curriki.plugin.analytics.module.justloggedin;

import org.curriki.plugin.analytics.CurrikiAnalyticsSession;
import org.curriki.plugin.analytics.UrlStore;
import org.curriki.plugin.analytics.module.Notifier;
import org.curriki.plugin.analytics.module.Trigger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class JustLoggedInTrigger  extends Trigger {

    public JustLoggedInTrigger(JustLoggedInNotifier notifier) {
        super(new LinkedList<Notifier>());
        notifiers.add(notifier);
    }

    @Override
    protected int match(UrlStore urlStore, String referer) {
        return 0;
    }

    @Override
    public void trigger(CurrikiAnalyticsSession currikiAnalyticsSession, String referer) {
        JustLoggedInNotifier notifier = (JustLoggedInNotifier) super.notifiers.get(0);
        String lastUserName = (String) currikiAnalyticsSession.getHttpSessionAttribute("last_username");
        String currentUserName = currikiAnalyticsSession.getUser();
        if(currentUserName!=null && !"XWiki.XWikiGuest".equals(currentUserName) && !currentUserName.equals(lastUserName)) {
            notifier.setNotification("just-logged-in");
            currikiAnalyticsSession.setHttpSessionAttribute("last_username", currentUserName);
        }

    }

    @Override
    protected void addNotifications(Object notification) {

    }

    @Override
    protected void removeNotifications(Object notification) {

    }
}
