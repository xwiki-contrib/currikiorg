package org.curriki.plugin.analytics.module;

/**
 * A simple interface to have a common method set for Objects that can behave like Notifiers.
 * By using an interface no limitation is given so a real notifier can
 * send http request, set or remove session data or store values to a database.
 */
public interface Notifier {
    /**
     * Set the notification of this notifier.
     * @param notification an object that contains the notification or values of it.
     *                     No closer description of the type, to not loose generality
     */
    public void setNotification(Object notification);

    /**
     * Remove the notificaton if this notifier
     */
    public void removeNotification(Object notification);
}