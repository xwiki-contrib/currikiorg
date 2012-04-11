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
import java.util.List;

import com.xpn.xwiki.plugin.activitystream.api.ActivityStreamException;
import com.xpn.xwiki.plugin.activitystream.plugin.ActivityEvent;
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
        return (CurrikiActivityStream) (getProtectedPlugin())
            .getActivityStream();
    }

    public List<ActivityEvent> getEvents(boolean filter, int nb, int start) throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().getEvents(filter, nb, start, this.context));
        } else {
            return null;
        }
    }

    public List<ActivityEvent> getEventsForSpace(String space, boolean filter, int nb, int start)
            throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().getEventsForSpace(space, filter, nb, start, this.context));
        } else {
            return null;
        }
    }
    public List<ActivityEvent> getEventsForSpace(String streamName, String space, boolean filter, int nb, int start)
            throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().getEventsForSpace(streamName, space, filter, nb, start, this.context));
        } else {
            return null;
        }
    }

    public List<ActivityEvent> getEventsForUser(String user, boolean filter, int nb, int start)
            throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().getEventsForUser(user, filter, nb, start, this.context));
        } else {
            return null;
        }
    }

    public List<ActivityEvent> getEventsForUser(String streamName, String user, boolean filter, int nb, int start)
            throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().getEventsForUser(streamName, user, filter, nb, start, this.context));
        } else {
            return null;
        }
    }

    public List<ActivityEvent> searchEvents(String hql, boolean filter, boolean globalSearch, int nb, int start)
            throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().searchEvents(hql, filter, globalSearch, nb, start, this.context));
        } else {
            return null;
        }
    }

    public List<ActivityEvent> searchEvents(String hql, boolean filter, boolean globalSearch, int nb, int start,
                                            List<Object> parameterValues) throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().searchEvents("", hql, filter, globalSearch, nb, start,
                    parameterValues, this.context));
        } else {
            return null;
        }
    }



    public List<ActivityEvent> getEvents(String streamName, boolean filter, int nb, int start)
            throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().getEvents(streamName, filter, nb, start, this.context));
        } else {
            return null;
        }
    }

    private List<ActivityEvent> wrapEvents(List<com.xpn.xwiki.plugin.activitystream.api.ActivityEvent> events)
    {
        if (events == null || events.size() == 0) {
            return new ArrayList<ActivityEvent>(0);
        }
        List<ActivityEvent> result = new ArrayList<ActivityEvent>(events.size());
        for(com.xpn.xwiki.plugin.activitystream.api.ActivityEvent event: events) {
            com.xpn.xwiki.plugin.activitystream.plugin.ActivityEvent wrappedEvent;
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
                    new com.xpn.xwiki.plugin.activitystream.plugin.ActivityEvent(event,
                        getXWikiContext());
            }
            result.add(wrappedEvent);
        }
        return result;
    }

    public List<ActivityEvent> searchEvents(String hql, boolean filter, int nb, int start)
            throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().searchEvents(hql, filter, nb, start, this.context));
        } else {
            return null;
        }
    }
    public List<ActivityEvent> searchEvents(String hql, boolean filter, int nb, int start, List<Object> parameterValues)
            throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().searchEvents("", hql, filter, nb, start, parameterValues,
                    this.context));
        } else {
            return null;
        }
    }

    public List<ActivityEvent> searchEvents(String fromHql, String hql, boolean filter, boolean globalSearch, int nb,
                                            int start) throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().searchEvents(fromHql, hql, filter, globalSearch, nb, start,
                    this.context));
        } else {
            return null;
        }
    }

    public List<ActivityEvent> searchEvents(String fromHql, String hql, boolean filter, boolean globalSearch, int nb,
                                            int start, List<Object> parameterValues) throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().searchEvents(fromHql, hql, filter, globalSearch, nb, start,
                    this.context));
        } else {
            return null;
        }
    }

    public List<ActivityEvent> searchEvents(String fromHql, String hql, boolean filter, int nb, int start)
            throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().searchEvents(fromHql, hql, filter, nb, start, this.context));
        } else {
            return null;
        }
    }

    public List<ActivityEvent> searchEvents(String fromHql, String hql, boolean filter, int nb, int start,
                                            List<Object> parameterValues) throws ActivityStreamException
    {
        if (hasProgrammingRights()) {
            return wrapEvents(getActivityStream().searchEvents(fromHql, hql, filter, nb, start, this.context));
        } else {
            return null;
        }
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
