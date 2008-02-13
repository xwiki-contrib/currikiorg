package org.curriki.plugin.activitystream.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.curriki.plugin.activitystream.impl.CurrikiActivityStream;
import org.xwiki.plugin.activitystream.plugin.ActivityStreamPluginApi;

import com.xpn.xwiki.XWikiContext;

public class CurrikiActivityStreamPluginApi extends ActivityStreamPluginApi
{

    public CurrikiActivityStreamPluginApi(CurrikiActivityStreamPlugin plugin, XWikiContext context)
    {
        super(plugin, context);
    }

    protected CurrikiActivityStream getCurrikiActivityStream()
    {
        return (CurrikiActivityStream) ((CurrikiActivityStreamPlugin) getPlugin())
            .getActivityStream();
    }

    protected List wrapEvents(List events)
    {
        if (events == null || events.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        List result = new ArrayList();
        Iterator iter = events.iterator();
        while (iter.hasNext()) {
            org.xwiki.plugin.activitystream.api.ActivityEvent event =
                (org.xwiki.plugin.activitystream.api.ActivityEvent) iter.next();
            org.xwiki.plugin.activitystream.plugin.ActivityEvent wrappedEvent;
            if (event.getSpace().startsWith("Messages_Group_")) {
                wrappedEvent = new MessageActivityEvent(event, getXWikiContext());
            } else if (event.getSpace().startsWith("UserProfiles_Group_")) {
                wrappedEvent = new MemberActivityEvent(event, getXWikiContext());
            } else if (event.getSpace().startsWith("Coll_Group_")) {
                wrappedEvent = new ResourceActivityEvent(event, getXWikiContext());
            } else if (event.getSpace().startsWith("Documentation_Group_")) {
                wrappedEvent = new DocumentationActivityEvent(event, getXWikiContext());
            } else {
                wrappedEvent =
                    new org.xwiki.plugin.activitystream.plugin.ActivityEvent(event,
                        getXWikiContext());
            }
            result.add(wrappedEvent);
        }
        return result;
    }

    protected List unwrapEvents(List events)
    {
        List result = super.unwrapEvents(events);
        for (int i = 0; i < events.size(); i++) {
            org.xwiki.plugin.activitystream.api.ActivityEvent event =
                (org.xwiki.plugin.activitystream.api.ActivityEvent) result.get(i);
            org.xwiki.plugin.activitystream.plugin.ActivityEvent wrappedEvent =
                (org.xwiki.plugin.activitystream.plugin.ActivityEvent) events.get(i);
            event.setTitle(wrappedEvent.getDisplayTitle());
            event.setBody(wrappedEvent.getDisplayBody());
        }
        return result;
    }
}
