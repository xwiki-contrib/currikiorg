import com.xpn.xwiki.api.Context
import com.xpn.xwiki.api.Document
import com.xpn.xwiki.api.XWiki
import com.xpn.xwiki.plugin.spacemanager.api.SpaceUserProfile
import com.xpn.xwiki.util.AbstractXWikiRunnable
import org.curriki.plugin.spacemanager.plugin.CurrikiSpaceManagerPluginApi
import org.curriki.xwiki.plugin.curriki.CurrikiPluginApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletRequest

public class GroupMessageNotificationMailSender {


    /**
     * LOGGER
     */
    private static final Logger LOG = LoggerFactory.getLogger(GroupMessageNotificationMailSender.class);

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
     * The spae manager
     */
    private CurrikiSpaceManagerPluginApi spaceManager;

    /**
     *
     */
    private BackgroundGroupMessageNotificationMailSender backgroundGroupMessageNotificationMailSender

    /**
     *
     * @param request
     * @param xwiki
     * @param context
     */
    public void init(HttpServletRequest request, XWiki xwiki, Context context) {
        this.request = request;
        this.wiki = xwiki;
        this.context = context;
        if (xwiki != null){
            this.currikiPluginApi = xwiki.curriki;
            this.spaceManager = xwiki.csm;
        }

        LOG.warn("Inited GroupMessageNotificationMailSender");
    }

    /**
     *
     * @param spaceName
     */
    public void messageSendNotificationMail(Document messageDocument, String spaceName){
        LOG.warn("Prepare background task to send emails about new messages in the group "  + spaceName);



        backgroundGroupMessageNotificationMailSender = new BackgroundGroupMessageNotificationMailSender(wiki, context, request, messageDocument, spaceName);
        backgroundGroupMessageNotificationMailSender.start();
    }



}

class BackgroundGroupMessageNotificationMailSender extends AbstractXWikiRunnable {

    /**
     *
      */
    private static final Logger LOG = LoggerFactory.getLogger(BackgroundGroupMessageNotificationMailSender.class);

    /**
     *
     */
    private XWiki wiki;

    /**
     *
     */
    private CurrikiSpaceManagerPluginApi spaceManager;

    /**
     *
     */
    private Context context;

    /**
     *
     */
    private HttpServletRequest request;

    /**
     *
     */
    private Document document;

    /**
     *
     */
    private String spaceName;

    /**
     *
     */
    Thread workerThread;

    /**
     *
     * @param wiki
     * @param context
     * @param request
     */
    BackgroundGroupMessageNotificationMailSender(XWiki wiki, Context context, HttpServletRequest request, Document document, String spaceName) {
        this.wiki = wiki
        this.spaceManager = wiki.csm;
        this.context = context;
        this.request = request
        this.document = document;
        this.spaceName = spaceName;
        LOG.warn("Inited BackgroundGroupMessageNotificationMailSender");

    }

    /**
     *
     * @param document
     * @param spaceName
     */
    private void addMessageSendNotificationMail(){
        if(request.getParameter("notify") == null) return; // If the notification was not checked we will not do it
        LOG.warn("Notification for new group message via email was checked")

        String mailTo = "";

        if(request.getParameter("toGroup") != null){  // We send the mails to all members of the group
            List<String> fullNames = spaceManager.getMembers(spaceName);
            LOG.warn("Loading members for group " + spaceName)
            addEmailsToRecipient(fullNames, spaceName);
        } else {  // We send the mails to specific members by name or role

            if(request.getParameter("toMember") && request.getParameter("selectedMembersList")){ // By name

                List<String> fullNames = request.getParameter("selectedMembersList").split(",");
                mailTo +=  addEmailsToRecipient(fullNames, spaceName);

            }

            if(request.getParameter("toRole") && request.getParameter("selectedRolesList")){ // By role

                List<String> roles = request.getParameter("selectedRolesList").split(",");
                for(String role : roles) {
                    List<String> fullNames = spaceManager.getUsersForRole(spaceName, role);
                    mailTo +=  addEmailsToRecipient(fullNames, spaceName);
                }

            }

        }

        if(!mailTo.equals("")){
            LOG.warn("Notification for new group message will be sent to: " + mailTo);
            sendMail(mailTo, document);
        }else {
            LOG.warn("MailTo was empty, cannot send email.");
        }


    }

    /**
     *
     * @param fullNames
     * @param spaceName
     * @return
     */
    private String addEmailsToRecipient(List<String> fullNames, String spaceName){
        String recipients = "";
        for(String fullName : fullNames){
            SpaceUserProfile profile = spaceManager.getSpaceUserProfile(spaceName, fullName);
            if (profile != null && profile.getUserProperty("allowNotification") != null){
                Document userDoc = this.wiki.getDocument(fullName);
                String email = userDoc.get("email");
                recipients = recipients + email + ",";
                LOG.warn("Added email: " + email);
            }
        }
        return recipients;
    }


    /**
     *
     * @param mailTo
     * @param document
     */
    private void sendMail(String mailTo, Document document){
        LOG.warn("Try to send email")
        String mailFrom = wiki.getXWikiPreference("admin_email");
        Document mailDoc = wiki.getDocument("Groups.MailTemplateCreateMessage");
        mailDoc.use("XWiki.ArticleClass")
        String mailSubject = document.getRenderedContent(mailDoc.getTitle(), "xwiki/1.0");
        String mailContent = document.getRenderedContent(mailDoc.getContent(), "xwiki/1.0");
        wiki.mailsender.sendTextMessage(mailFrom, null, null, mailTo, mailSubject, mailContent, null);
    }

    public void start(){
        LOG.warn("Started BackgroundGroupMessageNotificationMailSender");
        workerThread = new Thread(this, "BackgroundGroupMessageNotificationMailSender");
        workerThread.start();
    }

    @Override
    protected void runInternal() {
        Iterator<String> it = request.getParameterMap().keySet().iterator();
        StringBuffer buffer = new StringBuffer()
        buffer.append("RequestParameter: \n");
        while (it.hasNext()) {
            String parameterName  =  it.next();
            String parameterValue = request.getParameterMap().get(parameterName)[0];
            buffer.append(parameterName+":"+parameterValue+"\n");
        }
        LOG.warn(buffer.toString());
        addMessageSendNotificationMail();
    }
}