package org.curriki.plugin.analytics.module;

import com.xpn.xwiki.XWikiContext;
import org.curriki.plugin.analytics.CurrikiAnalyticsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * An abstract implementation of an Analytics module and how basic functionality
 * can look like. This class is abstract by intend, since all concrete implementations
 * will have a bunch of dependencies which this general class can not fit into and should not.
 * Anyway the principle is the same in all subclasses. An Analytics module has a list
 * of Triggers and is handing of the current CurrikiAnalyitcsSession to each trigger.
 * The trigger can then do the matching and call notifiers if any action is needed in the system.
 */
public abstract class AnalyticsModule {

    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsModule.class);

    /**
     * The XwikiContext
     */
    protected XWikiContext context;

    /**
     * A list of triggers
     * @see Trigger
     */
    protected List<Trigger> triggers;

    /**
     * The CurrikiAnalyticsSession
     * @see  CurrikiAnalyticsSession
     */
    protected CurrikiAnalyticsSession currikiAnalyticsSession;

    private static ThreadLocal<CurrikiAnalyticsSession> currentSession = new ThreadLocal<CurrikiAnalyticsSession>();

    public AnalyticsModule(XWikiContext context) {
        this.context = context;
        this.triggers = new LinkedList<Trigger>();
    }

    /**
     * An abstract method to remember developers that they need to
     * implement the config reload mechanism
     */
    public abstract void reloadConfig();

    /**
     * The AnalyticsModule should return a readable name
     * @return the name of the current module
     */
    public abstract String getName();

    /**
     * Iterate over all triggers and hand of the current CurrikiANalyticsSession to them
     */
    public void evaluateTriggers(CurrikiAnalyticsSession currikiAnalyticsSession) {
        LOG.warn("Crawling UrlStore");
        for (Trigger t : triggers) {
            if (currikiAnalyticsSession != null) {
                t.trigger(currikiAnalyticsSession, currikiAnalyticsSession.getRefererOfLastRequest());
            } else {
                LOG.warn("No AnalyticsSession to get the UrlStore");
            }
        }
    }

    public void crawlUrlStore() {
        throw new IllegalStateException("This method should not be called anymore.");
    }

    /**
     * Set the currikiAnalyticsSession
     * @param currikiAnalyticsSession
     */
    public void setCurrentAnalyticsSession(CurrikiAnalyticsSession currikiAnalyticsSession) {
        LOG.warn("Set current CurrikiAnalyticsSession for module " + getName());
        if(currikiAnalyticsSession!=null)
            currentSession.set(currikiAnalyticsSession);
        else
            currentSession.remove();
    }

    /**
     * Get the currikiAnalyticsSession
     * @return currikiAnalyticsSession
     */
    public CurrikiAnalyticsSession getCurrentAnalyticsSession() {
        return currentSession.get();
    }


}