import com.xpn.xwiki.api.Context
import com.xpn.xwiki.api.Document
import com.xpn.xwiki.api.XWiki
import com.xpn.xwiki.doc.XWikiDocument
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
    public void messageSendNotificationMail(String spaceName){
            XWikiDocument document = new XWikiDocument();
            document.setTitle("TestTitle");
            document.setContent("TestContent");
            BackgroundGroupMessageNotificationMailSender bgnms = new BackgroundGroupMessageNotificationMailSender(wiki, context, request);
            bgnms.addMessageSendNotificationMail(document, spaceName);
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
    private XWikiDocument document;

    /**
     *
     * @param wiki
     * @param context
     * @param request
     */
    BackgroundGroupMessageNotificationMailSender(XWiki wiki, Context context, HttpServletRequest request) {
        this.wiki = wiki
        this.spaceManager = wiki.csm;
        this.context = context;
        this.request = request
    }

    /**
     *
     * @param document
     * @param spaceName
     */
    public void addMessageSendNotificationMail(XWikiDocument document, String spaceName){
        if(request.getAttribute("notify") == null) return; // If the notification was not checked we will not do it

        String mailTo = "";

        if(request.getAttribute("toGroup") != null){  // We send the mails to all members of the group

            List<String> fullNames = spaceManager.getMembers(spaceName);
            addEmailsToRecipient(fullNames, spaceName);

        } else {  // We send the mails to specific members by name or role

            if(request.getAttribute("toMember") && request.getAttribute("selectedMembersList")){ // By name

                List<String> fullNames = request.getAttribute("selectedMembersList").split(",");
                addEmailsToRecipient(fullNames, spaceName);

            }

            if(request.getAttribute("toRole") && request.getAttribute("selectedRolesList")){ // By role

                List<String> roles = request.getAttribute("selectedRolesList").split(",");
                for(String role : roles) {
                    List<String> fullNames = spaceManager.getUsersForRole(spaceName, role);
                    addEmailsToRecipient(fullNames, spaceName);
                }

            }

        }

        if(!mailTo.equals("")){
            sendMail(mailTo, document);
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
    private void sendMail(String mailTo, XWikiDocument document){

    }

    @Override
    protected void runInternal() {

    }
}