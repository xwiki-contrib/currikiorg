package org.xwiki.plugin.spacemanager.impl;

import org.xwiki.plugin.spacemanager.api.SpaceUserProfile;
import org.xwiki.plugin.spacemanager.api.SpaceManagerException;
import org.xwiki.plugin.spacemanager.api.SpaceManager;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Document;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 13 déc. 2007
 * Time: 14:37:24
 * To change this template use File | Settings | File Templates.
 */
public class SpaceUserProfileImpl extends Document implements SpaceUserProfile {
    private static final String SPACE_USER_PROFILE_CLASS_NAME = "XWiki.SpaceUserProfileClass";
    private String userName;
    private SpaceManager manager;
    private Document userDoc;


    public SpaceUserProfileImpl(String userName, String spaceName, SpaceManager manager, XWikiContext context) throws XWikiException {
        super(null, context);
        this.manager = manager;
        this.userName = userName;
        String docName = manager.getSpaceUserProfilePageName(userName, spaceName);
        doc = context.getWiki().getDocument(docName, context);
    }

    protected String getSpaceUserProfileClassName() {
        return SPACE_USER_PROFILE_CLASS_NAME;
    }

    protected Document getUserDocument() throws XWikiException {
        if (userDoc==null)
         userDoc = new Document(context.getWiki().getDocument(userName, context), context);
        return userDoc;
    }

    public String getProfile() {
        return doc.getStringValue(getSpaceUserProfileClassName(), "profile");
    }

    public void setProfile(String profile) {
        getDoc().setStringValue(getSpaceUserProfileClassName(), "profile", profile);
    }

    public boolean getAllowNotifications() {
        return (doc.getIntValue(getSpaceUserProfileClassName(), "allowNotifications")==1);
    }

    public boolean getAllowNotificationsFromSelf() {
        return (doc.getIntValue(getSpaceUserProfileClassName(), "allowNotificationsFromSelf")==1);
    }

    public void setAllowNotifications(boolean allowNotifications) {
        getDoc().setIntValue(getSpaceUserProfileClassName(), "allowNotifications", allowNotifications ? 1 : 0);
    }

    public void setAllowNotificationsFromSelf(boolean allowNotificationsFromSelf) {
        getDoc().setIntValue(getSpaceUserProfileClassName(), "allowNotificationsFromSelf", allowNotificationsFromSelf ? 1 : 0);
    }

    public void updateProfileFromRequest() throws SpaceManagerException {
        try {
            updateObjectFromRequest(getSpaceUserProfileClassName());
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    public String getUserProperty(String propName) {
        try {
            return (String) getUserDocument().getObject("XWiki.XWikiUsers").display(propName, "view");
        } catch (XWikiException e) {
            return "";
        }
    }

    public String getFirstName() {
        return getUserProperty("first_name");
    }

    public String getLastName() {
        return getUserProperty("last_name");
    }

    public String getEmail() {
        return getUserProperty("email");
    }

    public String getUserURL() {
        try {
            return getUserDocument().getURL();
        } catch (XWikiException e) {
            return "";
        }
    }
}
