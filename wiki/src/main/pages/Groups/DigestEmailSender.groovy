import com.xpn.xwiki.api.Context
import com.xpn.xwiki.api.Document
import com.xpn.xwiki.api.XWiki
import com.xpn.xwiki.plugin.activitystream.api.ActivityEvent
import org.curriki.plugin.activitystream.plugin.CurrikiActivityStreamPluginApi
import org.curriki.plugin.spacemanager.plugin.CurrikiSpaceManagerPluginApi
import org.curriki.xwiki.plugin.curriki.CurrikiPluginApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletRequest

public class DigestEmailSender {

    /**
     * LOGGER
     */
    private static final Logger LOG = LoggerFactory.getLogger(DigestEmailSender.class);

    /**
     * The Request of the velocity page containing the parameters such as the username
     */
    private HttpServletRequest request;

    /**
     * The xwiki object of the running curriki instance
     */
    private XWiki wiki;

    /**
     * The interface to interact with the curriki api
     */
    private CurrikiPluginApi currikiPluginApi;

    /**
     * The context of the request
     */
    private Context context;

    /**
     * The space manager
     */
    private CurrikiSpaceManagerPluginApi spaceManager;

    public void init(HttpServletRequest request, XWiki xwiki, Context context) {
        this.request = request;
        this.wiki = xwiki;
        this.context = context;
        if (xwiki != null){
            this.currikiPluginApi = xwiki.curriki;
            this.spaceManager = xwiki.csm;
        }

        LOG.warn("Inited DigestEmailSender");
    }

    public int sendDigestEmailForGroup(String groupName){
        List<ActivityEvent> activityEvents = getActivityEventsForGroup(groupName);
        List<String> groupAdminsUserNames = spaceManager.getAdmins(groupName);

        wiki.context.put("GROUPNAME", groupName);
        wiki.context.put("EVENTS", activityEvents);

        Document emailDoc = wiki.getDocument("Groups.DigestEmailMailTemplate");
        String subject = wiki.renderText(emailDoc.title, emailDoc);
        String from = wiki.getXWikiPreference("admin_email");
        String text = emailDoc.getRenderedContent();

        LOG.warn("Events ###  " + activityEvents);
        LOG.warn("Groupadmins ###  " + groupAdminsUserNames);
        for (groupAdminUserName in groupAdminsUserNames) {
            Document groupAdminUserDoc = wiki.getDocument(groupAdminUserName);
            com.xpn.xwiki.api.Object userObj = groupAdminUserDoc.getObject("XWiki.XWikiUsers", true);
            String to = userObj.getProperty("email").getValue();
            LOG.warn("Sending mail to " + to);
            sendMail(from, to, subject, text);
        }
        return groupAdminsUserNames.size();
    }

    private List<ActivityEvent> getActivityEventsForGroup(String groupName) {
        CurrikiActivityStreamPluginApi activityStream = wiki.activitystream;
        String streamName = activityStream.getStreamName(groupName);
        return activityStream.getEvents(streamName, true, 15, 0);
    }

    private void sendMail(String from, String to, String subject, String text){
        wiki.mailsender.sendHtmlMessage(from, to, null, null, subject, text, text.replaceAll("<[^>]*>",""), []);
    }

}