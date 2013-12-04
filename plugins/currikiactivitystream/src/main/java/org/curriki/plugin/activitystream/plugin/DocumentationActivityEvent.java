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

import org.curriki.plugin.activitystream.impl.CurrikiActivityStream;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEventType;
import com.xpn.xwiki.plugin.activitystream.plugin.ActivityEvent;

public class DocumentationActivityEvent extends ActivityEvent
{
    private String displayTitle;

    private String displayBody;

    public DocumentationActivityEvent(com.xpn.xwiki.plugin.activitystream.api.ActivityEvent event,
        XWikiContext context)
    {
        super(event, context);

        String docTitle = event.getParam1();
        String docType = event.getParam3();
        String docLink = docTitle;
        XWikiDocument doc;
        BaseObject tag;
        try {
            doc = context.getWiki().getDocument(event.getPage(), context);
            tag = doc.getObject("XWiki.TagClass");
            if (tag != null) {
                docTitle = doc.getTitle();
                docType = tag.getStringValue("tags");
                docLink = "[[" + docTitle + ">>" + doc.getFullName().replaceAll("@", "%40") + "]]";
            }
        } catch (XWikiException e) {
        }

        String userName = event.getParam2();
        String userLink = userName;
        XWikiDocument userDoc;
        try {
            userDoc = context.getWiki().getDocument(event.getUser(), context);
            if (!userDoc.isNew()) {
                userName =
                    (userDoc.getStringValue("XWiki.XWikiUsers", "first_name") + " " + userDoc
                        .getStringValue("XWiki.XWikiUsers", "last_name")).trim();
                userLink = context.getWiki().getUserName(event.getUser(), context);
            }
        } catch (XWikiException e) {
        }

        String eventTitle = "";
        if (docType.contains(CurrikiActivityStream.DOCUMENTATION_FILE)) {
            if (ActivityEventType.UPDATE.equals(event.getType())) {
                eventTitle = "groups_home_activity_doc_upd";
            } else if (ActivityEventType.CREATE.equals(event.getType())) {
                eventTitle = "groups_home_activity_doc_add";
            } else if (ActivityEventType.DELETE.equals(event.getType())) {
                eventTitle = "groups_home_activity_doc_del";
            }
        } else if (docType.contains(CurrikiActivityStream.DOCUMENTATION_WIKI)) {
            if (ActivityEventType.UPDATE.equals(event.getType())) {
                eventTitle = "groups_home_activity_doc_wiki_upd";
            } else if (ActivityEventType.CREATE.equals(event.getType())) {
                eventTitle = "groups_home_activity_doc_wiki_add";
            } else if (ActivityEventType.DELETE.equals(event.getType())) {
                eventTitle = "groups_home_activity_doc_wiki_del";
            }
        }

        List params = new ArrayList();
        params.add(docTitle);
        params.add(userName);
        displayTitle = context.getMessageTool().get(eventTitle, params);

        params.set(0, docLink);
        params.set(1, userLink);
        displayBody = context.getMessageTool().get(eventTitle, params);
    }

    public String getDisplayTitle()
    {
        return displayTitle;
    }

    public String getDisplayBody()
    {
        return displayBody;
    }
}
