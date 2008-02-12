package org.curriki.plugin.activitystream.impl;

import java.util.ArrayList;
import java.util.List;

import org.xwiki.plugin.activitystream.api.ActivityEventPriority;
import org.xwiki.plugin.activitystream.api.ActivityEventType;
import org.xwiki.plugin.activitystream.impl.ActivityStreamImpl;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.notify.XWikiDocChangeNotificationInterface;
import com.xpn.xwiki.notify.XWikiNotificationRule;
import com.xpn.xwiki.objects.BaseObject;

public class CurrikiActivityStream extends ActivityStreamImpl
{
    private static final String CURRIKI_SPACE_TYPE = "currikispace";

    private static final String SPACE_CLASS_NAME = "XWiki.SpaceClass";

    public CurrikiActivityStream()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public void notify(XWikiNotificationRule rule, XWikiDocument newdoc, XWikiDocument olddoc,
        int event, XWikiContext context)
    {
        String spaceName = newdoc.getSpace();
        if (spaceName == null) {
            return;
        }
        if (spaceName.startsWith("Messages_Group_")) {
            handleMessageEvent(newdoc, olddoc, event, context);
        } else if (spaceName.startsWith("Documentation_Group_")) {
            handleDocumentationEvent(newdoc, olddoc, event, context);
        } else if (spaceName.startsWith("Coll_Group_")) {
            handleResourceEvent(newdoc, olddoc, event, context);
        } else if (spaceName.startsWith("UserProfiles_Group_")) {
            handleMemberEvent(newdoc, olddoc, event, context);
        }
        // TODO handle events from MemberGroup, AdminGroup and Role_<roleName>Group
    }

    protected void handleMessageEvent(XWikiDocument newdoc, XWikiDocument olddoc, int event,
        XWikiContext context)
    {
        String streamName = getStreamName(newdoc.getSpace(), context);
        if (streamName == null) {
            return;
        }

        BaseObject article = newdoc.getObject("XWiki.ArticleClass");
        if (article == null) {
            if (olddoc == null) {
                return;
            }
            article = olddoc.getObject("XWiki.ArticleClass");
            if (article == null) {
                return;
            }
            event = XWikiDocChangeNotificationInterface.EVENT_DELETE;
        } else if ((olddoc != null && olddoc.getObject("XWiki.ArticleClass") == null)
            || (olddoc == null && "1.4".equals(newdoc.getVersion()))) {
            event = XWikiDocChangeNotificationInterface.EVENT_NEW;
        }

        List params = new ArrayList();
        String articleTitle = article.getStringValue("title");
        String articleLink =
            "[" + articleTitle + ">" + article.getName().replaceAll("@", "%40") + "]";
        // should "from" be the creator of the message or the event initiator?
        String from = context.getWiki().getUserName(context.getUser(), context);

        try {
            switch (event) {
                case XWikiDocChangeNotificationInterface.EVENT_NEW:
                    params.add(articleLink);
                    params.add(from);
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.CREATE,
                        ActivityEventPriority.NOTIFICATION, "groups_home_activity_mes_add",
                        params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_CHANGE:
                    params.add(articleLink);
                    params.add(from);
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.UPDATE,
                        ActivityEventPriority.NOTIFICATION, "groups_home_activity_mes_edit",
                        params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_DELETE:
                    params.add(articleTitle);
                    params.add(from);
                    addDocumentActivityEvent(streamName, olddoc, ActivityEventType.DELETE,
                        ActivityEventPriority.NOTIFICATION, "groups_home_activity_mes_del",
                        params, context);
                    break;
            }
        } catch (Throwable e) {
            // Error in activity stream notify should be ignored but logged in the log file
            e.printStackTrace();
        }
    }

    protected void handleDocumentationEvent(XWikiDocument newdoc, XWikiDocument olddoc,
        int event, XWikiContext context)
    {
        String streamName = getStreamName(newdoc.getSpace(), context);
        if (streamName == null) {
            return;
        }

        // TODO is this truly a documentation
        // update event parameter (workaround)
        if (newdoc.isNew()) {
            event = XWikiDocChangeNotificationInterface.EVENT_DELETE;
        }

        List params = new ArrayList();
        String docDisplayTitle = newdoc.getDisplayTitle(context);
        String docLink =
            "[" + docDisplayTitle + ">" + newdoc.getSpace() + "."
                + newdoc.getName().replaceAll("@", "%40") + "]";
        // should "user" be the creator of the message or the event initiator?
        String user = context.getWiki().getUserName(context.getUser(), context);

        try {
            switch (event) {
                case XWikiDocChangeNotificationInterface.EVENT_NEW:
                    params.add(docLink);
                    params.add(user);
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.CREATE,
                        ActivityEventPriority.NOTIFICATION, "groups_home_activity_doc_wiki_add",
                        params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_CHANGE:
                    params.add(docLink);
                    params.add(user);
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.UPDATE,
                        ActivityEventPriority.NOTIFICATION, "groups_home_activity_doc_wiki_upd",
                        params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_DELETE:
                    params.add(docDisplayTitle);
                    params.add(user);
                    addDocumentActivityEvent(streamName, olddoc, ActivityEventType.DELETE,
                        ActivityEventPriority.NOTIFICATION, "groups_home_activity_doc_wiki_del",
                        params, context);
                    break;
            }
        } catch (Throwable e) {
            // Error in activity stream notify should be ignored but logged in the log file
            e.printStackTrace();
        }
    }

    protected void handleResourceEvent(XWikiDocument newdoc, XWikiDocument olddoc, int event,
        XWikiContext context)
    {
        String streamName = getStreamName(newdoc.getSpace(), context);
        if (streamName == null) {
            return;
        }

        BaseObject asset = newdoc.getObject("XWiki.AssetClass");
        if (asset == null) {
            if (olddoc == null) {
                return;
            }
            asset = olddoc.getObject("XWiki.AssetClass");
            if (asset == null) {
                return;
            }
            event = XWikiDocChangeNotificationInterface.EVENT_DELETE;
        } else if ((olddoc != null && olddoc.getObject("XWiki.AssetClass") == null)
            || (olddoc == null && "1.2".equals(newdoc.getVersion()))) {
            event = XWikiDocChangeNotificationInterface.EVENT_NEW;
        }

        List params = new ArrayList();
        String docDisplayTitle = newdoc.getDisplayTitle(context);
        String docLink =
            "[" + docDisplayTitle + ">" + newdoc.getSpace() + "."
                + newdoc.getName().replaceAll("@", "%40") + "]";
        // should "from" be the creator of the message or the event initiator?
        String user = context.getWiki().getUserName(context.getUser(), context);

        try {
            switch (event) {
                case XWikiDocChangeNotificationInterface.EVENT_NEW:
                    params.add(docLink);
                    params.add(user);
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.CREATE,
                        ActivityEventPriority.NOTIFICATION, "groups_home_activity_res_add",
                        params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_CHANGE:
                    params.add(docLink);
                    params.add(user);
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.UPDATE,
                        ActivityEventPriority.NOTIFICATION, "groups_home_activity_res_edit",
                        params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_DELETE:
                    params.add(docDisplayTitle);
                    params.add(user);
                    addDocumentActivityEvent(streamName, olddoc, ActivityEventType.DELETE,
                        ActivityEventPriority.NOTIFICATION, "groups_home_activity_res_del",
                        params, context);
                    break;
            }
        } catch (Throwable e) {
            // Error in activity stream notify should be ignored but logged in the log file
            e.printStackTrace();
        }
    }

    protected void handleMemberEvent(XWikiDocument newdoc, XWikiDocument olddoc, int event,
        XWikiContext context)
    {
        String streamName = getStreamName(newdoc.getSpace(), context);
        if (streamName == null) {
            return;
        }

        BaseObject profile = newdoc.getObject("XWiki.SpaceUserProfileClass");
        if (profile == null) {
            if (olddoc == null) {
                return;
            }
            profile = olddoc.getObject("XWiki.SpaceUserProfileClass");
            if (profile == null) {
                return;
            }
            event = XWikiDocChangeNotificationInterface.EVENT_DELETE;
        } else if ((olddoc != null && olddoc.getObject("XWiki.SpaceUserProfileClass") == null)
            || (olddoc == null && "1.2".equals(newdoc.getVersion()))) {
            event = XWikiDocChangeNotificationInterface.EVENT_NEW;
        }

        try {
            String profileName = profile.getName();
            String userName = "XWiki." + profileName.substring(profileName.indexOf(".") + 1);
            XWikiDocument userDoc = context.getWiki().getDocument(userName, context);

            List params = new ArrayList();
            params.add(context.getWiki().getUserName(userName, context));

            switch (event) {
                case XWikiDocChangeNotificationInterface.EVENT_NEW:
                    addDocumentActivityEvent(streamName, userDoc, ActivityEventType.CREATE,
                        ActivityEventPriority.NOTIFICATION, "groups_home_activity_mem_new",
                        params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_CHANGE:
                    addDocumentActivityEvent(streamName, userDoc, ActivityEventType.UPDATE,
                        ActivityEventPriority.NOTIFICATION, "groups_home_activity_mem_upd",
                        params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_DELETE:
                    // ignore
                    break;
            }
        } catch (Throwable e) {
            // Error in activity stream notify should be ignored but logged in the log file
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getStreamName(String space, XWikiContext context)
    {
        XWikiDocument doc;
        try {
            doc = context.getWiki().getDocument(space, "WebPreferences", context);
            String type = doc.getStringValue(SPACE_CLASS_NAME, "type");

            if (CURRIKI_SPACE_TYPE.equals(type))
                return space;

            String parentSpace = space.substring(space.indexOf("_") + 1);
            doc = context.getWiki().getDocument(parentSpace, "WebPreferences", context);
            type = doc.getStringValue(SPACE_CLASS_NAME, "type");
            if (CURRIKI_SPACE_TYPE.equals(type))
                return parentSpace;

            // could not find a Curriki space
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
