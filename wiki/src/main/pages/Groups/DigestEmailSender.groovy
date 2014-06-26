import com.xpn.xwiki.api.Document
import com.xpn.xwiki.api.XWiki
import com.xpn.xwiki.plugin.activitystream.api.ActivityEvent
import org.curriki.plugin.activitystream.plugin.CurrikiActivityStreamPluginApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class DigestEmailSender {

    /**
     * LOGGER
     */
    private static final Logger LOG = LoggerFactory.getLogger(DigestEmailSender.class);

    /**
     * The xwiki object of the running curriki instance
     */
    private XWiki wiki;

    public void init(XWiki xwiki) {
        this.wiki = xwiki;
        LOG.warn("Inited DigestEmailSender");
    }

    public int sendDigestEmailForGroup(String groupName, List<String> groupAdminsUserNames){
        List<ActivityEvent> activityEvents = getActivityEventsForGroup(groupName);

        if(groupAdminsUserNames == null || groupAdminsUserNames.size() == 0 || groupAdminsUserNames.contains("")){
            groupAdminsUserNames = wiki.csm.getAdmins(groupName);
        }

        wiki.context.put("GROUPNAME", groupName);
        wiki.context.put("EVENTS", activityEvents);

        Document emailDoc = wiki.getDocument("Groups.DigestEmailMailTemplate");
        String subject = wiki.renderText(emailDoc.title, emailDoc);
        String from = wiki.getXWikiPreference("admin_email");
        String text = emailDoc.getRenderedContent();

        LOG.warn("Events ###  " + activityEvents);
        LOG.warn("Groupadmins ### "+groupAdminsUserNames.size() + " " + groupAdminsUserNames);
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