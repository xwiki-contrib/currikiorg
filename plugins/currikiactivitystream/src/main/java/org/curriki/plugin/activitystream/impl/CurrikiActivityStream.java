package org.curriki.plugin.activitystream.impl;

import java.util.ArrayList;
import java.util.List;

import org.xwiki.plugin.activitystream.api.ActivityEvent;
import org.xwiki.plugin.activitystream.api.ActivityEventPriority;
import org.xwiki.plugin.activitystream.api.ActivityEventType;
import org.xwiki.plugin.activitystream.impl.ActivityEventImpl;
import org.xwiki.plugin.activitystream.impl.ActivityStreamImpl;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
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
        params.add(article.getStringValue("title"));
        params.add(getUserName(context.getUser(), context));

        try {
            switch (event) {
                case XWikiDocChangeNotificationInterface.EVENT_NEW:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.CREATE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_CHANGE:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.UPDATE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_DELETE:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.DELETE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
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

        String docTitle = newdoc.getTitle();
        String docType =
            newdoc.getTags(context).contains("documentation-file") ? "file" : "wikipage";

        // update event parameter (workaround)
        if (newdoc.isNew()) {
            docTitle = olddoc.getTitle();
            docType =
                olddoc.getTags(context).contains("documentation-file") ? "file" : "wikipage";
            event = XWikiDocChangeNotificationInterface.EVENT_DELETE;
        } else if (olddoc == null && "1.2".equals(newdoc.getVersion())) {
            event = XWikiDocChangeNotificationInterface.EVENT_NEW;
        }

        List params = new ArrayList();
        params.add(docTitle);
        params.add(getUserName(context.getUser(), context));
        params.add(docType);

        try {
            switch (event) {
                case XWikiDocChangeNotificationInterface.EVENT_NEW:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.CREATE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_CHANGE:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.UPDATE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_DELETE:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.DELETE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
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
        params.add(asset.getStringValue("title"));
        params.add(getUserName(context.getUser(), context));

        try {
            switch (event) {
                case XWikiDocChangeNotificationInterface.EVENT_NEW:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.CREATE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_CHANGE:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.UPDATE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_DELETE:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.DELETE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
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
            String user = "XWiki." + profileName.substring(profileName.indexOf(".") + 1);
            XWikiDocument userDoc = context.getWiki().getDocument(user, context);

            List params = new ArrayList();
            params.add(user);
            params.add(getUserName(user, context));

            ActivityEvent activityEvent = new ActivityEventImpl();
            activityEvent.setStream(streamName);
            activityEvent.setPriority(ActivityEventPriority.NOTIFICATION);
            activityEvent.setUrl(userDoc.getExternalURL("view", context));
            activityEvent.setParams(params);
            activityEvent.setDate(newdoc.getDate());

            switch (event) {
                case XWikiDocChangeNotificationInterface.EVENT_NEW:
                    activityEvent.setType(ActivityEventType.CREATE);
                    addActivityEvent(activityEvent, newdoc, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_CHANGE:
                    activityEvent.setType(ActivityEventType.UPDATE);
                    addActivityEvent(activityEvent, newdoc, context);
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

    private String getUserName(String user, XWikiContext context)
    {
        String userName = null;
        try {
            XWikiDocument userDoc = context.getWiki().getDocument(user, context);
            if (!userDoc.isNew()) {
                userName =
                    (userDoc.getStringValue("XWiki.XWikiUsers", "first_name") + " " + userDoc
                        .getStringValue("XWiki.XWikiUsers", "last_name")).trim();
            }
        } catch (XWikiException e) {
        }
        if (userName == null) {
            userName = user.substring(user.indexOf(".") + 1);
        }
        return userName;
    }
}
