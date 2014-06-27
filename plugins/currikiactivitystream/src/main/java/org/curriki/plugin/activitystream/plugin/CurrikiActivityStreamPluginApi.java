/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.curriki.plugin.activitystream.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.curriki.plugin.activitystream.impl.CurrikiActivityStream;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.plugin.activitystream.plugin.ActivityStreamPluginApi;

public class CurrikiActivityStreamPluginApi extends ActivityStreamPluginApi
{

    public CurrikiActivityStreamPluginApi(CurrikiActivityStreamPlugin plugin, XWikiContext context)
    {
        super(plugin, context);
    }

    protected CurrikiActivityStream getCurrikiActivityStream()
    {
        return (CurrikiActivityStream) ((CurrikiActivityStreamPlugin) getProtectedPlugin())
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
            com.xpn.xwiki.plugin.activitystream.api.ActivityEvent event =
                    (com.xpn.xwiki.plugin.activitystream.api.ActivityEvent) iter.next();
            com.xpn.xwiki.plugin.activitystream.plugin.ActivityEvent wrappedEvent;
            if (event.getSpace().startsWith("Messages_Group_")) {
                wrappedEvent = new MessageActivityEvent(event, getXWikiContext());
            } else if (event.getSpace().startsWith("UserProfiles_Group_")) {
                wrappedEvent = new MemberActivityEvent(event, getXWikiContext());
            } else if (event.getSpace().startsWith("Coll_Group_")) {
                wrappedEvent = new ResourceActivityEvent(event, getXWikiContext());
            } else if (event.getSpace().startsWith("Documentation_Group_")) {
                wrappedEvent = new DocumentationActivityEvent(event, getXWikiContext());
            } else if (event.getSpace().startsWith("Discussions_Group_")) {
                wrappedEvent = new DiscussionActivityEvent(event, getXWikiContext());
            } else {
                wrappedEvent =
                        new com.xpn.xwiki.plugin.activitystream.plugin.ActivityEvent(event,
                                getXWikiContext());
            }
            result.add(wrappedEvent);
            System.out.println("Wrapping event " + event +"(space: " + event.getSpace() + ") into " + wrappedEvent );
        }
        return result;
    }
/*
    protected List unwrapEvents(List events)
    {
        List result =
            new ArrayList();
        if (events != null) {
            for (Object event : events) {
                Object unwrappedEvent = ((com.xpn.xwiki.plugin.activitystream.api.ActivityEvent)event).getEvent();
                result.add(unwrappedEvent);
            }
        }
        return result;
    }
*/
}
