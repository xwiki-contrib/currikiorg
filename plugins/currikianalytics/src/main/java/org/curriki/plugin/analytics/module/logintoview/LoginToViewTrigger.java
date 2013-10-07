package org.curriki.plugin.analytics.module.logintoview;

import org.curriki.plugin.analytics.CurrikiAnalyticsSession;
import org.curriki.plugin.analytics.UrlStore;
import org.curriki.plugin.analytics.module.Notifier;
import org.curriki.plugin.analytics.module.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The Trigger implementation for the LoginToViewAnalyticsModule
 * It try to count resource view by matching the history of visited urls.
 * It has as threshold of allowed views and calls the LoginToViewNotifier if a
 * match was found and the login dialog needs to be shown.
 */
public class LoginToViewTrigger extends Trigger {

    private static final Logger LOG = LoggerFactory.getLogger(LoginToViewTrigger.class);

    /**
     * The numbers of matches that are allowed per visitor
     */
    private int threshold;

    /**
     * A list of patterns which are used to match the current url history
     */
    private List<Pattern> patterns;

    /**
     * A list of patterns that are exceptions to the matches above
     */
    private List<Pattern> exceptions;

    public LoginToViewTrigger(int threshold, List<Notifier> notifiers, List<Pattern> patterns, List<Pattern> exceptions) {
        super(notifiers);
        this.threshold = threshold;
        this.patterns = patterns;
        this.exceptions = exceptions;
    }

    @Override
    public void trigger(CurrikiAnalyticsSession currikiAnalyticsSession, String referer) {
        LOG.warn("Triggered");
        int matches = match(currikiAnalyticsSession.getUrlStore(), referer);
        boolean currentUrlMatches = matchCurrentUrl(currikiAnalyticsSession.getUrlStore(), referer, matches);
        boolean currentUrlIsException = urlIsException(currikiAnalyticsSession.getUrlStore().getLast());
        boolean loginToViewCookieIsPresent = currikiAnalyticsSession.getCookie(LoginToViewSessionNotifier.LOGIN_TO_VIEW_COOKIE_NAME) != null;
        boolean currentUserIsGuest = currikiAnalyticsSession.getUser() != null && ("XWiki.XWikiGuest".equals(currikiAnalyticsSession.getUser()));

        // If the current user is logged in. Don't show notifications and remove the login to view cookie
        if(!currentUserIsGuest){
            LOG.warn("User is already logged in");
            Map notificationValues = new HashMap<String, Boolean>();
            notificationValues.put(LoginToViewSessionNotifier.DELETE_COOKIE_VALUE, true);
            removeNotifications(notificationValues);
        }

        // If the user has an analytics cookie set, we need to add the notification
        // for the exceeded threshold. But if it is an exception we go further with matching
        else if(loginToViewCookieIsPresent && !currentUrlIsException && currentUrlMatches){
            LOG.warn("LTV Cookie found and current url matches");
            addNotifications(this.threshold);
        }

        // If the current url matches add notifications
        else if(currentUrlMatches){
            LOG.warn("No LTV Cookie but the current url matches");
            addNotifications(matches);
        }

        // If no match was found an no other condition was given, don't show notifications
        else {
            LOG.warn("Wether a LTV Cookie or a match for the current url, do nothing.");
            Map notificationValues = new HashMap<String, Boolean>();
            notificationValues.put(LoginToViewSessionNotifier.DELETE_COOKIE_VALUE, false);
            removeNotifications(notificationValues);
        }
    }

    @Override
    protected int match(UrlStore urlStore, String referer) {
        int matches = 0;
        for (Pattern pattern : patterns) {
            LOG.warn("Matching pattern \"" + pattern.toString() + "\" against UrlStore");
            matches += matchUrlHistory(pattern, urlStore, referer);
        }
        return matches;
    }

    /**
     * Crawl the UrlStore for the given pattern and count matches.
     * Double entries in the UrlStore with a match are only counted as +1.
     * @param pattern the pattern to match with
     * @param urlStore the url store to search in
     * @param referer the referer of the request.
     * @return the number of matches of the pattern against the url store.
     */
    private int matchUrlHistory(Pattern pattern, UrlStore urlStore, String referer){
        int matches = 0;
        LinkedList<String> alreadyMatched = new LinkedList<String>();
        for (String url : urlStore) {
            if (!urlIsException(url) && pattern.matcher(url).matches() && !alreadyMatched.contains(url)) {
                LOG.warn("Match for: " + url);
                alreadyMatched.add(url);
                matches++;
            } else if(alreadyMatched.contains(url)){
                LOG.warn("Already matched: " + url);
            } else if (exceptions.contains(url)){
                LOG.warn("Url is an exception: " + url);
            } else {
                LOG.warn("No match for: " + url);
            }
        }
        return matches;
    }

    /**
     * Check the url of the current request, if it matches a set of rules
     * and other circumstances to decide whether we need to send out a notification
     * for the current page or not.
     *
     * @param urlStore the url store of the current currikiAnalyticsSession
     * @param referer the referer of the current request
     * @param historicMatches the number of matches we found so far when matching before
     * @return true to send notifications, false to not
     */
    private boolean matchCurrentUrl(UrlStore urlStore, String referer, int historicMatches){
        String currentUrl = urlStore.getLast();
        LOG.warn("Check for matches of current url " + currentUrl);
        LinkedList<String> history = new LinkedList<String>(urlStore);
        history.removeLast();

        if(urlIsException(currentUrl)){
            return false;
        }

        // Don't match the current url if it is the first resource you visit.
        if(historicMatches == 1){
            LOG.warn("First view on a resource, don't show a login dialog.");
            return false;
        }

        // If we come from the login page and the limit is not exceeded, we don't match the current url
        if("/xwiki/bin/view/Registration/LoginOrRegister".equals(referer) && historicMatches < this.threshold){
            LOG.warn("Coming from the login page, don't show the login page again");
            return false;
        }

        // If we come from the login page and the limit is exceeded, we match the current url
        if("/xwiki/bin/view/Registration/LoginOrRegister".equals(referer) && historicMatches >= this.threshold){
            LOG.warn("Coming from the login page, but the threshold is exceeded. Show the login page again");
            return true;
        }

        // If the url history contains the current url we don't match it again because
        // we need to assume that we already had shown a login dialog
        if(history.contains(currentUrl)){
            LOG.warn("The current url was already seen and we assume a notification was shown there");
            return false;
        }

        // Normal case matching, if the current url is matched by a pattern and no case before applied
        // we will show the log in dialog
        for (Pattern pattern : patterns) {
            if(pattern.matcher(currentUrl).matches()) {
                LOG.warn("The current url does match a pattern, show notification on the current page");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given url does match any of the exception patterns
     * @param url the url to check
     * @return true if the url is an exception
     */
    private boolean urlIsException(String url){
        for(Pattern exception : exceptions) {
            // If the current url is an exceptions it does not match
            if (exception.matcher(url).matches()) {
                LOG.warn("Match for exception\"" + exception.toString() + "\"");
                return true;
            }
        }
        LOG.warn("No match for exception");
        return false;
    }

    @Override
    protected void addNotifications(Object notification) {
        int numberOfMatches = 0;
        if(notification != null && notification instanceof Integer){
            numberOfMatches = (Integer) notification;
        }
        LOG.warn("Calling " + super.notifiers.size() + " Notifiers to tell them about found matches");
        for (Notifier notifier : super.notifiers) {
            int numberOfViewsRemaining = threshold - numberOfMatches;
            numberOfViewsRemaining = (numberOfViewsRemaining < 0) ? 0 : numberOfViewsRemaining;
            Map notificationValues = new HashMap<String, Integer>();
            notificationValues.put(LoginToViewSessionNotifier.NUMBER_OF_MATCHES_NOTIFICATION_VALUE, numberOfMatches);
            notificationValues.put(LoginToViewSessionNotifier.NUMBER_OF_REMAINING_VIEWS_NOTIFICATIONS_VALUE, numberOfViewsRemaining);
            notificationValues.put(LoginToViewSessionNotifier.THRESHOLD_NOTIFICATION_VALUE, this.threshold);
            notifier.setNotification(notificationValues);
        }
    }

    @Override
    protected void removeNotifications(Object notification) {
        LOG.warn("Calling " + super.notifiers.size() + " Notifiers to tell them to remove their notifications");
        for (Notifier notifier : super.notifiers) {
            notifier.removeNotification(notification);
        }
    }
}