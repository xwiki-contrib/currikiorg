package org.curriki.plugin.analytics.module;

import org.curriki.plugin.analytics.CurrikiAnalyticsSession;
import org.curriki.plugin.analytics.UrlStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

/**
 * An abstract implementation of a Trigger. A trigger gets the current CurrikiAnalyticsSession and
 * does a matching to it. What this matching is, is not defined here, but in special implementations
 * of this class. Altough, all triggers have in common that they have a set of Notifiers on which they can
 * add or remove notifications.
 */
public abstract class Trigger {

    private static final Logger LOG = LoggerFactory.getLogger(Trigger.class);

    /**
     * A list of Notifiers
     */
    protected List<Notifier> notifiers;

    public Trigger(List<Notifier> notifiers){
        this.notifiers = notifiers;

    }

    /**
     * Entry point for the trigger
     *
     * @param currikiAnalyticsSession the current currikiAnalyticsSession
     * @param referer the referer of the request we currently handle
     */
    public abstract void trigger(CurrikiAnalyticsSession currikiAnalyticsSession, String referer);

    /**
     * Match the current UrlStore and return the number of matches
     *
     * @param urlStore the UrlStore of the current currikiAnalyticsSession
     * @param referer the referer of the request we currently handle
     * @return the number of matches
     */
    protected abstract int match(UrlStore urlStore, String referer);

    /**
     * Add a notification to the notifiers
     * @param notification an object that can be handled by the notifiers
     */
    protected abstract void addNotifications(Object notification);

    /**
     * Call notifiers to remove their notifications
     */
    protected abstract void removeNotifications(Object notification);
}