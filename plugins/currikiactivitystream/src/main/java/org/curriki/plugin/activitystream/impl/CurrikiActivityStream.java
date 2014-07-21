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
package org.curriki.plugin.activitystream.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.xpn.xwiki.util.AbstractXWikiRunnable;
import com.xpn.xwiki.web.Utils;
import org.apache.velocity.VelocityContext;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.api.XWiki;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.notify.XWikiDocChangeNotificationInterface;
import com.xpn.xwiki.notify.XWikiNotificationRule;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEvent;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEventPriority;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEventType;
import com.xpn.xwiki.plugin.activitystream.api.ActivityStreamException;
import com.xpn.xwiki.plugin.activitystream.impl.ActivityEventImpl;
import com.xpn.xwiki.plugin.activitystream.impl.ActivityStreamImpl;
import com.xpn.xwiki.plugin.mailsender.Mail;
import com.xpn.xwiki.plugin.mailsender.MailSenderPlugin;
import com.xpn.xwiki.plugin.spacemanager.api.Space;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManager;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagers;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceUserProfile;
import com.xpn.xwiki.render.XWikiVelocityRenderer;
import org.curriki.plugin.activitystream.plugin.CurrikiActivityStreamPluginApi;
import org.xwiki.context.Execution;
import org.xwiki.observation.event.Event;
import org.xwiki.observation.remote.RemoteObservationManagerContext;

import javax.servlet.http.HttpServletRequest;

public class CurrikiActivityStream extends ActivityStreamImpl implements XWikiDocChangeNotificationInterface
{
    private static final String CURRIKI_SPACE_TYPE = "currikispace";

    private static final String SPACE_CLASS_NAME = "XWiki.SpaceClass";

    public static final String DOCUMENTATION_FILE = "documentation-file";

    public static final String DOCUMENTATION_WIKI = "documentation-wiki";

    public static final String DISCUSSION_TOPIC = "discussion-topic";

    public static final String DISCUSSION_ANSWER = "discussion-answer";

    public CurrikiActivityStream()
    {
        super();
    }

    public void onEvent(Event event, Object source, Object data) {
        System.out.println("onEvent for CurrikiActivityStream! We should not be there.");
        // ignoring these calls, we get the notify calls in principle
    }


    /**
     * {@inheritDoc}
     */
    public void notify(XWikiNotificationRule rule, XWikiDocument newdoc, XWikiDocument olddoc,
        int event, XWikiContext context)
    {
        System.out.println("STREAM: in notify");
        System.out.println("TempAttributes; " + tempStorage.get());

        if(Utils.getComponent(RemoteObservationManagerContext.class).isRemoteState()) {
            System.out.println("Ignoring remote DocumentUpdatedEvent for " + newdoc);
            return;
        }
        try {
            String spaceName = newdoc.getSpace();
            System.out.println("STREAM: in space" + spaceName + " with event " + event);

            if (spaceName == null) {
                return;
            }
            if (spaceName.startsWith("Messages_Group_")) {
                handleMessageEvent(newdoc, olddoc, event, context);
            } else if (spaceName.startsWith("Documentation_Group_")) {
                handleDocumentationEvent(newdoc, olddoc, event, context);
            } else if (spaceName.startsWith("Discussions_Group_")) {
                handleDiscussionsEvent(newdoc, olddoc, event, context);
            } else if (spaceName.startsWith("Coll_Group_")) {
                handleResourceEvent(newdoc, olddoc, event, context);
            } else if (spaceName.startsWith("UserProfiles_Group_")) {
                handleMemberEvent(newdoc, olddoc, event, context);
            }
            // TODO handle events from MemberGroup, AdminGroup and Role_<roleName>Group
        } catch (Throwable t) {
            // Error in activity stream notify should be ignored but logged in the log file
            t.printStackTrace();
        } finally {
            this.clearTempAttributes();
        }
    }


    @Override
    public void addActivityEvent(ActivityEvent event, XWikiDocument doc, XWikiContext context) throws ActivityStreamException {
        super.addActivityEvent(event, doc, context);
        dispatchEventNotification(event, context);
    }

    protected void dispatchEventNotification(final ActivityEvent event, XWikiContext xcontext) {
        AbstractXWikiRunnable runnable = new AbstractXWikiRunnable(XWikiContext.EXECUTIONCONTEXT_KEY, xcontext.clone()) {
            public void runInternal() {
                try {
                    // here we could synchronize on anything we think is useful since we are on separate threads for each event
                    // I wonder what... some representative of the group so we don't get flooded by actions that trigger huge things?
                    XWikiContext xcontext = (XWikiContext) Utils.getComponent(Execution.class).getContext()
                            .getProperty(XWikiContext.EXECUTIONCONTEXT_KEY);
                    Object notificationMailSender = xcontext.getWiki().parseGroovyFromPage("Groups.NotificationMailSender", xcontext);
                    Method sendNotificationEmailForEventMethod = notificationMailSender.getClass().getMethod("sendNotificationEmailForEvent", String.class, ActivityEvent.class);
                    Method initMethod = notificationMailSender.getClass().getMethod("init", com.xpn.xwiki.api.Context.class);
                    initMethod.invoke(notificationMailSender, new com.xpn.xwiki.api.Context(xcontext));
                    sendNotificationEmailForEventMethod.invoke(notificationMailSender, getStreamName(event.getSpace(), xcontext), event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable,"CurrikiActivityStreamDispatchNotification").start();
    }

    protected void handleMessageEvent(XWikiDocument newdoc, XWikiDocument olddoc, int event,
        XWikiContext context)
    {
        System.out.println("handleMessageEvent");
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
        } else {
            double version = Double.parseDouble(newdoc.getVersion());
            double initialVersion = 3.1;
            if ((olddoc != null && olddoc.getObject("XWiki.ArticleClass") == null)
                || (olddoc == null && version == initialVersion)) {
                event = XWikiDocChangeNotificationInterface.EVENT_NEW;
            } else if (version < initialVersion) {
                return;
            }
        }

        boolean notify = false;
        String level = "message";
        if ("commentadd".equals(context.getAction())) {
            event = XWikiDocChangeNotificationInterface.EVENT_NEW;
            level = "comment";
            notify = true;
        }

        // cut the messageBody at max 200 (but at a word please!)
        String messageBody = teasify(readCommentBody(newdoc, context));


        List params = new ArrayList();
        params.add(article.getStringValue("title"));
        params.add(getUserName(context.getUser(), context));
        params.add(level);
        params.add(messageBody);
        System.out.println("Params: " + params);

        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("mailTo", (String) getTempAttribute("mailTo"));
        paramsMap.put("recipientRole", (String) getTempAttribute("recipientRole"));
        paramsMap.put("mailToGroup", (String) getTempAttribute("mailToGroup"));
        Gson gson = new Gson();
        params.add(gson.toJson(paramsMap));

        try {
            switch (event) {
                case XWikiDocChangeNotificationInterface.EVENT_NEW:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.CREATE,
                            ActivityEventPriority.NOTIFICATION, "", params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_CHANGE:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.UPDATE,
                            ActivityEventPriority.NOTIFICATION, "", params, context);
                    notify = true;
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_DELETE:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.DELETE,
                            ActivityEventPriority.NOTIFICATION, "", params, context);
                    break;
            }
            if (notify) {
                sendUpdateNotification(newdoc.getSpace().substring("Messages_".length()), newdoc,
                    context);
            }
        } catch (Throwable e) {
            // Error in activity stream notify should be ignored but logged in the log file
            e.printStackTrace();
        }
    }

    private String readCommentBody(XWikiDocument newdoc, XWikiContext context) {
        String messageBody = (String) getTempAttribute("messageBody");
        if(messageBody==null || messageBody.length()==0) {
            messageBody = ((HttpServletRequest) ((VelocityContext) context.get("vcontext")).get("request")).getParameter("XWiki.XWikiComments_comment");
            messageBody = newdoc.getRenderedContent(messageBody, newdoc.getSyntaxId(), context);
        }
        return messageBody;
    }

    static String teasify(String text) {
        if(text==null) text = "";
        // put a space before block-separating elements (see http://de.selfhtml.org/html/referenz/elemente.htm)
        text = text.replaceAll("</?(address|blockquote|center|del|dir|div|dl|fieldset|form|h[0-6]|hr|ins|isindex|menu|noframes|noscript|ol|p|pre|table|ul)"," <x");
        text = text.replaceAll("<[^>]+>","");
        text = text.replaceAll("\\{\\{/?html[^}]*\\}\\}", "");
        text = text.replaceAll("[\\s]+", " ");
        text = text.trim();
        if(text.length()<200) return text;
        int p =0, max = Math.min(200, text.length());
        for(int i=0; i<max; i++) {
            if(!Character.isLetterOrDigit(text.charAt(i))) p = i;
        }
        if(p<150 && text.length()>=200) p = 150;
        if(p<text.length()) {
            text = text.substring(0, p);
            text = text.trim();
            text = text + "…";
        }
        return text;
    }

    protected void handleDocumentationEvent(XWikiDocument newdoc, XWikiDocument olddoc,
        int event, XWikiContext context)
    {
        String streamName = getStreamName(newdoc.getSpace(), context);
        if (streamName == null) {
            return;
        }

        String docTitle = newdoc.getTitle();
        BaseObject tag = newdoc.getObject("XWiki.TagClass");
        if (tag == null) {
            if (olddoc == null) {
                return;
            }
            tag = olddoc.getObject("XWiki.TagClass");
            if (tag == null) {
                return;
            }
            docTitle = olddoc.getTitle();
            event = XWikiDocChangeNotificationInterface.EVENT_DELETE;
        } else {
            double version = Double.parseDouble(newdoc.getVersion());
            double initialVersion = 4.1;
            if (tag.getStringValue("tags").contains(DOCUMENTATION_WIKI)) {
                initialVersion = 3.1;
            }
            if ((olddoc != null && olddoc.getObject("XWiki.TagClass") == null)
                || (olddoc == null && version == initialVersion)) {
                event = XWikiDocChangeNotificationInterface.EVENT_NEW;
            } else if (version < initialVersion) {
                return;
            }
        }

        String docType = tag.getStringValue("tags");
        if (docType.contains(DOCUMENTATION_FILE)) {
            docType = DOCUMENTATION_FILE;
        } else if (docType.contains(DOCUMENTATION_WIKI)) {
            docType = DOCUMENTATION_WIKI;
        } else {
            return;
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
                    sendUpdateNotification(
                        newdoc.getSpace().substring("Documentation_".length()), newdoc, context);
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

    public void addAnswerActivityEvent(String streamName, XWikiDocument topicDoc, XWikiDocument answerDoc, String type, int priority, String title,
        List<String> params, XWikiContext context) throws ActivityStreamException
        {
        ActivityEventImpl event = new ActivityEventImpl();
        event.setStream(streamName);
        event.setPage(topicDoc.getFullName());
        if (topicDoc.getDatabase() != null) {
            event.setWiki(topicDoc.getDatabase());
        }
        event.setDate(answerDoc.getDate());
        event.setPriority(priority);
        event.setType(type);
        event.setTitle(title);
        event.setBody(title);
        event.setVersion(answerDoc.getVersion());
        params.add(teasify((String) getTempAttribute("messageBody")));
        event.setParams(params);



        // This might be wrong once non-altering events will be logged.
        if(answerDoc!=null && answerDoc.getDate().compareTo(topicDoc.getDate())>0)
            event.setUser(answerDoc.getAuthor());
        else event.setUser(topicDoc.getAuthor());
        addActivityEvent(event, topicDoc, context);
        }

    protected void handleDiscussionsEvent(XWikiDocument newdoc, XWikiDocument olddoc,
        int event, XWikiContext context)
    {
        String streamName = getStreamName(newdoc.getSpace(), context);
        if (streamName == null) {
            return;
        }

        try {
            List params = new ArrayList();
            String docTitle = newdoc.getTitle();
            BaseObject answerClass = newdoc.getObject("ConversationCode.AnswerClass");
            BaseObject topicClass = newdoc.getObject("ConversationCode.TopicClass");
            BaseObject oldAnswerClass = olddoc.getObject("ConversationCode.AnswerClass");
            BaseObject oldTopicClass = olddoc.getObject("ConversationCode.TopicClass");

            if ((answerClass!=null)||(oldAnswerClass!=null)) {

                XWikiDocument topicDoc = context.getWiki().getDocument(newdoc.getParent(), context);
                params.add(topicDoc.getTitle());
                params.add(getUserName(context.getUser(), context));
                params.add(DISCUSSION_ANSWER);
                if (answerClass==null) {
                    // this means an answer has been deleted
                    params.add(teasify((String) getTempAttribute("messageBody")));
                    addAnswerActivityEvent(streamName, topicDoc, newdoc, ActivityEventType.DELETE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
                } else if (oldAnswerClass==null) {
                    // this means an answer has been created
                    params.add(teasify((String) getTempAttribute("messageBody")));
                    addAnswerActivityEvent(streamName, topicDoc, newdoc, ActivityEventType.CREATE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
                } else {
                    int oldCommentsCount = 0, newCommentsCount = 0;
                    if(newdoc!=null) {
                        List l = newdoc.getObjects("XWiki.XWikiComments");
                        newCommentsCount = l.size();
                    }
                    if(olddoc!=null) {
                        List l = olddoc.getObjects("XWiki.XWikiComments");
                        if(l!=null) oldCommentsCount = l.size();
                    }
                    if(newCommentsCount-oldCommentsCount==1) {
                        // this means a comment has been published
                        params.add(teasify(readCommentBody(newdoc, context)));
                        addAnswerActivityEvent(streamName, topicDoc, newdoc, ActivityEventType.ADD_COMMENT,
                                ActivityEventPriority.NOTIFICATION, "", params, context);
                    } else {
                        // this means an answer has been updated
                        params.add(teasify((String) getTempAttribute("messageBody")));
                        addAnswerActivityEvent(streamName, topicDoc, newdoc, ActivityEventType.UPDATE,
                                ActivityEventPriority.NOTIFICATION, "", params, context);
                    }

                }
            } else if ((topicClass!=null)||(oldTopicClass!=null)) {

                if (topicClass==null) {

                    // this means a topic has been deleted
                    params.add(olddoc.getTitle());
                    params.add(getUserName(context.getUser(), context));
                    params.add(DISCUSSION_TOPIC);
                    params.add(teasify((String) getTempAttribute("messageBody")));

                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.DELETE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
                } else if (oldTopicClass==null) {

                    // this means a topic has been created
                    params.add(newdoc.getTitle());
                    params.add(getUserName(context.getUser(), context));
                    params.add(DISCUSSION_TOPIC);
                    params.add(teasify((String) getTempAttribute("messageBody")));
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.CREATE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);

                } else {

                    // this means a topic has been updated
                    params.add(newdoc.getTitle());
                    params.add(getUserName(context.getUser(), context));
                    params.add(DISCUSSION_TOPIC);
                    params.add(teasify((String) getTempAttribute("messageBody")));
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.UPDATE,
                        ActivityEventPriority.NOTIFICATION, "", params, context);
                }
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

        BaseObject asset = newdoc.getObject("CurrikiCode.AssetClass");
        if (asset == null) {
            if (olddoc == null) {
                return;
            }
            asset = olddoc.getObject("CurrikiCode.AssetClass");
            if (asset == null) {
                return;
            }
            event = XWikiDocChangeNotificationInterface.EVENT_DELETE;
        } else {
            double version = Double.parseDouble(newdoc.getVersion());
            if (version==2.1) {
                event = XWikiDocChangeNotificationInterface.EVENT_NEW;
            } else if (!newdoc.getVersion().endsWith(".1")||version==1.1) {
                // we ignore minor version edits and the first version
                return;
            }


        }

        List params = new ArrayList();
        params.add(newdoc.getTitle());
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
                    sendUpdateNotification(newdoc.getSpace().substring("Coll_".length()), newdoc,
                        context);
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
        } else {
            double version = Double.parseDouble(newdoc.getVersion());
            double initialVersion = 2.1;
            if ((olddoc != null && olddoc.getObject("XWiki.SpaceUserProfileClass") == null)
                || (olddoc == null && version == initialVersion)) {
                event = XWikiDocChangeNotificationInterface.EVENT_NEW;
            } else if (version < initialVersion) {
                return;
            }
        }

        try {
            String profileName = profile.getName();
            String user = "XWiki." + profileName.substring(profileName.indexOf(".") + 1);
            XWikiDocument userDoc = context.getWiki().getDocument(user, context);

            List params = new ArrayList();
            params.add(user);
            params.add(getUserName(user, context));

            ActivityEventImpl activityEvent = new ActivityEventImpl();
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

    /**
     * Sends a notification email to the creator of the given document if it has been updated by
     * another user and he opted in his space profile for this kind of notifications.
     *
     * @param spaceName the space the changed document is associated with. Also, the document
     *            creator's profile in this space can block this notification
     * @param doc the changed document
     * @param context the XWiki context
     * @throws Exception
     */
    private void sendUpdateNotification(String spaceName, XWikiDocument doc, XWikiContext context)
        throws Exception
        {
        if (doc.getCreator().equals(context.getUser())) {
            return;
        }
        SpaceManager spaceManager = SpaceManagers.findSpaceManagerForSpace(spaceName, context);
        SpaceUserProfile profile =
            spaceManager.getSpaceUserProfile(spaceName, doc.getCreator(), context);
        if (!profile.getAllowNotificationsFromSelf()) {
            return;
        }

        Space space = spaceManager.getSpace(spaceName, context);
        String templateDocFullName = "Groups.MailTemplateUpdateNotification";
        XWikiDocument mailDoc = context.getWiki().getDocument(templateDocFullName, context);
        XWikiDocument translatedMailDoc = mailDoc.getTranslatedDocument(context);

        VelocityContext vContext = new VelocityContext();
        vContext.put("space", space);
        vContext.put("udoc", new Document(doc, context));
        vContext.put("xwiki", new XWiki(context.getWiki(), context));
        vContext.put("context", new com.xpn.xwiki.api.Context(context));

        String mailFrom = context.getWiki().getXWikiPreference("admin_email", context);
        String mailTo =
            context.getWiki().getDocument(doc.getCreator(), context).getStringValue(
                "XWiki.XWikiUsers", "email");
        String mailSubject =
            XWikiVelocityRenderer.evaluate(translatedMailDoc.getTitle(), templateDocFullName,
                vContext, context);
        String mailContent =
            XWikiVelocityRenderer.evaluate(translatedMailDoc.getContent(), templateDocFullName,
                vContext, context);

        MailSenderPlugin mailSender =
            (MailSenderPlugin) context.getWiki().getPlugin("mailsender", context);
        mailSender.prepareVelocityContext(mailFrom, mailTo, "", "", vContext, context);
        Mail mail = new Mail(mailFrom, mailTo, null, null, mailSubject, mailContent, null);
        mailSender.sendMail(mail, context);
        }

    /* Override searchEvents to change group by behavior for filter (give create items intead of update items)
     */
    public List<ActivityEvent> searchEvents(String hql, boolean filter, int nb, int start, XWikiContext context) throws ActivityStreamException
    {
        String searchHql;

        if (filter) {
            searchHql =
                "select act from ActivityEventImpl as act, ActivityEventImpl as act2 where act.eventId=act2.eventId and "
                    + hql
                    + " group by act.requestId having (act.priority)=max(act2.priority) and (act.type)=min(act.type) order by act.date desc";
        } else {
            searchHql =
                "select act from ActivityEventImpl as act where " + hql
                + " order by act.date desc";
        }

        try {
            return context.getWiki().search(searchHql, nb, start, context);
        } catch (XWikiException e) {
            throw new ActivityStreamException(e);
        }
    }

    private static ThreadLocal<Map<String, Object>> tempStorage = new ThreadLocal<Map<String,Object>>();

    public void setTempAttribute(String name, Object obj) {
        Map<String, Object> m = tempStorage.get();
        if(m==null) {
            m = new HashMap<String, Object>();
            tempStorage.set(m);
        }
        m.put(name, obj);
    }
    public void clearTempAttributes() {
        tempStorage.remove();
    }
    public Object getTempAttribute(String name) {
        if(tempStorage.get()==null) return null;
        return tempStorage.get().get(name);
    }



}
