package org.xwiki.plugin.spacemanager.api;

import com.xpn.xwiki.XWikiException;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 30 nov. 2007
 * Time: 00:10:14
 * To change this template use File | Settings | File Templates.
 */
public interface SpaceUserProfile {
    public String getProfile();

    public void setProfile(String profile);

    public boolean getAllowNotifications();

    public boolean getAllowNotificationsFromSelf();

    public void setAllowNotifications(boolean allowNotifications);

    public void setAllowNotificationsFromSelf(boolean allowNotificationsFromSelf);

    public void updateProfileFromRequest() throws SpaceManagerException;

    public void save() throws XWikiException;
}
