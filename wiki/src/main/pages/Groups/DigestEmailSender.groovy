import com.xpn.xwiki.api.Document
import com.xpn.xwiki.api.XWiki
import com.xpn.xwiki.plugin.activitystream.api.ActivityEvent
import com.xpn.xwiki.plugin.spacemanager.api.Space
import org.curriki.plugin.activitystream.plugin.CurrikiActivityStreamPluginApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat

public class DigestEmailSender {

    /**
     * LOGGER
     */
    private static final Logger LOG = LoggerFactory.getLogger(DigestEmailSender.class);

    /**
     * The xwiki object of the running curriki instance
     */
    private XWiki wiki;
    private Long sinceHowLong = 24*60*60*1000;

    public void init(XWiki xwiki) {
        this.wiki = xwiki;
        LOG.warn("Inited DigestEmailSender");
    }

    public void setSinceHowLong(String s) { this.sinceHowLong = s;}

    public int sendDigestEmailToAllGroupsMatching(String pattern, List<String> groupAdminsUserNames){
        boolean hasWildcard = pattern.endsWith("*");
        String prefix = pattern; if(hasWildcard) prefix = pattern.substring(0, pattern.length()-1);
        int count = 0;
        for(name in wiki.csm.getSpaceNames(10000,0)) {
            if(hasWildcard && !(name.startsWith(prefix))) continue;
            println("Processing " + name);
            System.out.println("Processing " + name);
            count += sendDigestEmailForGroup(name, groupAdminsUserNames);
        }
        return count;
    }

    public int sendDigestEmailForGroup(String groupName, List<String> groupAdminsUserNames){
        List<ActivityEvent> activityEvents = getActivityEventsForGroup(groupName);

        // If we have no events to send we don't proceed here.
        // Log that and return quietly
        if(activityEvents == null || activityEvents.size() == 0) {
            LOG.warn("No Events to send for group with name: " + groupName)
            return 0;
        }

        if(groupAdminsUserNames == null || groupAdminsUserNames.size() == 0 || groupAdminsUserNames.contains("")){
            groupAdminsUserNames = wiki.csm.getAdmins(groupName);
        }

        Space space = wiki.csm.getSpace(groupName);
        wiki.context.put("GROUPNAME", groupName);
        wiki.context.put("DISPLAY_GROUPNAME", space.getDisplayTitle());
        wiki.context.put("EVENTS", activityEvents);
        wiki.context.put("DIGEST_EMAIL_SENDER", this);

        Document emailDoc = wiki.getDocument("Groups.DigestEmailMailTemplate");
        String subject = wiki.renderText(emailDoc.title, emailDoc);
        String from = wiki.getXWikiPreference("admin_email");
        String text = emailDoc.getRenderedContent();

        LOG.warn("Events ###  " + activityEvents);
        LOG.warn("Groupadmins ### "+groupAdminsUserNames.size() + " " + groupAdminsUserNames);
        for (groupAdminUserName in groupAdminsUserNames) {
            Document groupAdminUserDoc = wiki.getDocument(groupAdminUserName);
            com.xpn.xwiki.api.Object userObj = groupAdminUserDoc.getObject("XWiki.XWikiUsers", true);
            if(userObj==null || !userObj.getProperty("email")) continue;
            String to = userObj.getProperty("email").getValue();
            LOG.warn("Sending mail to " + to);
            sendMail(from, to, subject, text);
        }
        return groupAdminsUserNames.size();
    }

    public String formatDate(Date date){
        SimpleDateFormat isoFormat = new SimpleDateFormat("hh:mm a");
        isoFormat.setTimeZone(TimeZone.getTimeZone("EST5EDT"));
        return isoFormat.format(date);
    }

    private List<ActivityEvent> getActivityEventsForGroup(String groupName) {
        CurrikiActivityStreamPluginApi activityStream = wiki.activitystream;
        String streamName = activityStream.getStreamName(groupName);
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        Date date = new Date(new Date().getTime() - sinceHowLong);
        String dateStr = isoFormat.format(date);
        LOG.warn("Selecting events from stream \"" + streamName + "\" with date > " + dateStr);
        return activityStream.searchEvents("act.stream='"+ streamName+"'  and act.date > ${dateStr}", false, 25, 0)
    }

    private void sendMail(String from, String to, String subject, String text){
        wiki.mailsender.sendHtmlMessage(from, to, null, null, subject, text, text.replaceAll("<[^>]*>",""), []);
    }

}