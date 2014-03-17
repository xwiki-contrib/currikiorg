package org.curriki.plugin.analytics.module.justloggedin;

import com.xpn.xwiki.XWikiContext;
import org.curriki.plugin.analytics.CurrikiAnalyticsSession;
import org.curriki.plugin.analytics.module.AnalyticsModule;

public class JustLoggedInAnalyticsModule extends AnalyticsModule {

    public JustLoggedInAnalyticsModule(XWikiContext initializationContext) {
        super(initializationContext);
        reloadConfig();
    }

    public static final String NAME = JustLoggedInAnalyticsModule.class.getName();
    private JustLoggedInTrigger trigger;

    @Override
    public void reloadConfig() {
        JustLoggedInNotifier notifier = new JustLoggedInNotifier(this);
        trigger = new JustLoggedInTrigger(notifier);
        triggers.clear();
        triggers.add(trigger);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
