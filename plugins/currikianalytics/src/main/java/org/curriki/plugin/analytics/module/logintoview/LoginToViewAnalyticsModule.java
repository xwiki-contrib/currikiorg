package org.curriki.plugin.analytics.module.logintoview;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.notify.DocChangeRule;
import com.xpn.xwiki.notify.XWikiDocChangeNotificationInterface;
import com.xpn.xwiki.notify.XWikiNotificationRule;
import org.curriki.plugin.analytics.Helper;
import org.curriki.plugin.analytics.module.AnalyticsModule;
import org.curriki.plugin.analytics.module.Notifier;
import org.curriki.plugin.analytics.module.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The concrete implementation of a AnalyticsModule. The main responsibility of this class
 * is to be the clue between all the components.
 * (Plugin <-> AnalyticsModules) (AnalyticsModules <-> Trigger) (Triggers <-> Notifiers)
 */
public class LoginToViewAnalyticsModule extends AnalyticsModule implements XWikiDocChangeNotificationInterface {

    private static final Logger LOG = LoggerFactory.getLogger(LoginToViewAnalyticsModule.class);

    /**
     * The name of this module
     */
    public static final String NAME = "LoginToViewAnalyticsModule";

    /**
     * The configuration values of this module
     */
    private Map<String, String> config;

    /**
     * The patterns for the matching
     */
    private List<Pattern> patterns;

    /**
     * The notifiers for the notifications
     */
    private List<Notifier> notifiers;

    /**
     * The list of exception patterns.
     */
    private List<Pattern> exceptions;

    public LoginToViewAnalyticsModule(XWikiContext context){
        super(context);
        this.reloadConfig();
        //Add this class as listener for changed documents.
        context.getWiki().getNotificationManager().addGeneralRule(new DocChangeRule(this));
    }

    /**
     * Create the list with patterns for the matching
     */
    private List<Pattern> loadPatternList(){
        List<Pattern> patternList = new LinkedList<Pattern>();
        patternList.add(Pattern.compile(".*/xwiki/bin/view/Coll_.*"));
        return patternList;
    }

    /**
     * Create the list for the exceptions. Read from an xwiki page.
     * @return the exception patterns
     */
    private List<Pattern> loadExceptionList(){
        List<String> lines = Helper.getLinesOfPage("CurrikiCode/LoginToViewExceptions", context);
        for (int i = 0; i < lines.size(); i++) {
            String line = ".*" + lines.get(i) + ".*";
            lines.set(i,line);
        }
        return Helper.compileStringsToPatterns(lines);
    }

    /**
     * The notifiers for this module
     * @return a list of notifiers
     */
    private List<Notifier> loadNotifierList(){
        List<Notifier> notifierList = new LinkedList<Notifier>();
        notifierList.add(new LoginToViewSessionNotifier(this));
        return notifierList;
    }

    /**
     * Create the list of triggers which are responsible for the matching
     * @return a list of Triggers
     */
    private List<Trigger> loadTriggerList(){
        List<Trigger> triggerList = new LinkedList<Trigger>();
        // If this module is not turned on in the configuration
        // a trigger to remove all session flags is set
        if("on".equals(config.get("login_to_view"))){
            int threshold = Integer.valueOf(config.get("number_of_resources_to_view"));
            triggerList.add(new LoginToViewTrigger(threshold, notifiers, patterns, exceptions));
        }else{
            triggerList.add(new DisableLoginToViewTrigger(notifiers));
        }
        return triggerList;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void reloadConfig() {
        LOG.warn("Reloading configuration for " + NAME);
        this.config = Helper.loadConfigFromPage("CurrikiCode/LoginToViewConfig",context);
        this.patterns = loadPatternList();
        this.exceptions = loadExceptionList();
        this.notifiers = loadNotifierList();
        this.triggers = loadTriggerList();
    }


    /**
     * This functions gets called when documents in the wiki are changing.
     * It checks if a change document is a config page of this module.
     * If yes it reloads the while configuration.
     */
    public void notify(XWikiNotificationRule rule, XWikiDocument newdoc, XWikiDocument olddoc, int event, XWikiContext context) {
        LOG.warn("Notify about doc change: " + newdoc.getFullName());
        if("CurrikiCode.LoginToViewExceptions".equals(newdoc.getFullName()) || "CurrikiCode.LoginToViewConfig".equals(newdoc.getFullName())){
            reloadConfig();
        }
    }
}