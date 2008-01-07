package org.xwiki.plugin.spacemanager.api;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

import java.util.List;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 28 nov. 2007
 * Time: 12:17:53
 * To change this template use File | Settings | File Templates.
 */
public interface SpaceManager extends XWikiPluginInterface {
    public static interface SpaceAction
    {
        String CREATE = "Create";
    }

    public static final String SPACE_DEFAULT_TYPE = "space";
    public static final String SPACE_CLASS_NAME = "XWiki.SpaceClass";
    String DEFAULT_RESOURCE_SPACE = "Spaces";

    /**
     * Translate a space name to a space Wiki name
     *
     * @param spaceTitle
     * @param unique
     * @param context
     * @return
     */
    public String getSpaceWikiName(String spaceTitle, boolean unique, XWikiContext context);

    /**
     * Loads the SpaceManagerExtension specified in the config file
     * @throws SpaceManagerException
     */
    public SpaceManagerExtension getSpaceManagerExtension(XWikiContext context) throws SpaceManagerException;

    /**
     * Gets the name use to define spaces
     */
    public String getSpaceTypeName();

    /**
     * Gets the class name used to store class data
     */
    public String getSpaceClassName();

    /**
     * Create a space from scratch
     * It will create an empty space or will copy the default space template if there is one
     *
     * @param spaceName
     * @param context
     * @param
     * @return On success returns the newly created space and null on failure
     * @throws SpaceManagerException
     */
    public Space createSpace(String spaceName, XWikiContext context) throws SpaceManagerException;

    /**
     * Create a space based on a template space
     *
     * @param spaceName
     * @param templateSpaceName
     * @param context
     * @return On success returns the newly created space and null on failure
     */
    public Space createSpaceFromTemplate(String spaceName, String templateSpaceName, XWikiContext context) throws SpaceManagerException;

    /**
     * Create a space and install an application in the space
     * An application is handled by the ApplicationManager plugin and can include other sub-applications
     *
     * @param spaceName
     * @param applicationName
     * @param context
     * @return On success returns the newly created space and null on failure
     */
    public Space createSpaceFromApplication(String spaceName, String applicationName, XWikiContext context) throws SpaceManagerException;

    /**
     *
     * @param context
     * @return On success returns the newly created space and null on failure
     */
    public Space createSpaceFromRequest(XWikiContext context ) throws SpaceManagerException;

    /**
     * @param templateSpaceName template space name to copy
     * @param context
     * @return On success returns the newly created space and null on failure
     */
    public Space createSpaceFromRequest(String templateSpaceName, XWikiContext context ) throws SpaceManagerException;

    /**
     * Delete a space, including or not the space data
     *
     * @param spaceName
     * @param deleteData
     * @param context
     */

    public void deleteSpace(String spaceName, boolean deleteData, XWikiContext context) throws SpaceManagerException;

    /**
     *
     * @param spaceName
     * @param context
     */
    public void undeleteSpace(String spaceName, XWikiContext context) throws SpaceManagerException;

    /**
     * Get the list of space objects
     *
     * @param nb
     * @param start
     * @param context
     * @return list of space objects
     */
    public List getSpaces(int nb, int start, XWikiContext context) throws SpaceManagerException;

    /**
     * Get the list of space objects
     *
     * @param start
     * @param nb
     * @param context
     * @return list of space names
     */
    public List getSpaceNames(int nb, int start, XWikiContext context) throws SpaceManagerException;

    /**
     * Search for spaces using an HQL query returning Space objects
     *
     * @param fromsql
     *@param wherehql
     * @param nb
     * @param start
     * @param context @return list of space objects
     */
    public List searchSpaces(String fromsql, String wherehql, int nb, int start, XWikiContext context) throws SpaceManagerException;

    /**
     * Search for spaces using an HQL query returning Space Names
     *
     * @param fromsql
     *@param wheresql
     * @param nb
     * @param start
     * @param context @return
     */
    public List searchSpaceNames(String fromsql, String wheresql, int nb, int start, XWikiContext context) throws SpaceManagerException;

    /**
     * Get the list of spaces for a user in a specific role
     * If role is null it will get all spaces in which the user is member
     * return space name
     *
     * @param userName
     * @param role
     * @param context
     * @return list of space objects
     */
    public List getSpaces(String userName, String role, XWikiContext context) throws SpaceManagerException;

    /**
     * Get the list of spaces for a user in a specific role
     * If role is null it will get all spaces in which the user is member
     *
     * @param userName
     * @param role
     * @param context
     * @return list of space names
     */
    public List getSpaceNames(String userName, String role, XWikiContext context) throws SpaceManagerException;

    /**
     * Get the space object corresponding to the space named "space"
     *
     * @param spaceName
     * @param context
     * @return
     */
    public Space getSpace(String spaceName, XWikiContext context) throws SpaceManagerException;

    /**
     * Updates a space object from the HTTP request data
     *
     * @param space
     * @param context
     */
    public void updateSpaceFromRequest(Space space, XWikiContext context) throws SpaceManagerException;

    /**
     * Validate that the space data is valid. Wrong data are stored in the context
     *
     * @param space
     * @param context
     * @return
     */
    public boolean validateSpaceData(Space space, XWikiContext context) throws SpaceManagerException;

    /**
     * Save the space data to the storage system
     *
     * @param space
     * @param context
     */
    public void saveSpace(Space space, XWikiContext context) throws XWikiException;

    /**
     * Get the list of last modified documents in the space
     *
     * @param spaceName The space in which the search is performed
     * @param context The XWikiContext of the request
     * @param recursive Determines if the search is performed in the child spaces too  
     * @param nb Number of documents to be retrieved
     * @return start Pagination option saying at what document index to start the search
     */
    public List getLastModifiedDocuments(String spaceName, XWikiContext context, boolean recursive, int nb, int start) throws SpaceManagerException;

    /**
     * Search for documents in the space
     *
     * @param spaceName
     * @param hql
     * @param context
     * @return
     */
    public List searchDocuments(String spaceName, String hql, XWikiContext context) throws SpaceManagerException;


    /**
     *
     * @param spaceName
     * @param usernames
     * @param context
     * @throws SpaceManagerException
     */
    public void addMembers(String spaceName, List usernames, XWikiContext context ) throws SpaceManagerException;

    /**
     *
     * @param spaceName
     * @param username
     * @param context
     * @throws SpaceManagerException
     */
    public void addMember(String spaceName, String username, XWikiContext context) throws SpaceManagerException;

    /**
     *
     * @param spaceName
     * @param context
     * @throws SpaceManagerException
     */
    public Collection getMembers(String spaceName, XWikiContext context) throws SpaceManagerException;

    /**
     * Add a wiki user as admin in the space
     *
     * @param spaceName
     * @param username
     * @param context
     */
    public void addAdmin(String spaceName, String username, XWikiContext context) throws SpaceManagerException;

    /**
     * Add a list of admins in the space
     *
     * @param spaceName
     * @param usernames
     * @param context
     */
    public void addAdmins(String spaceName, List usernames, XWikiContext context) throws SpaceManagerException;

    /**
     * @return the list of all members of the space that are admins
     */
    public Collection getAdmins(String spaceName, XWikiContext context) throws SpaceManagerException;

    /**
     * Add user to the specific role in the space
     *
     * @param spaceName
     * @param username
     * @param roles
     * @param context
     */
    public void addUserToRoles(String spaceName, String username, List roles, XWikiContext context) throws SpaceManagerException;

    /**
     * Add a list of users to a specific role in the space
     *
     * @param spaceName
     * @param usernames
     * @param roles
     * @param context
     */
    public void addUsersToRoles(String spaceName, List usernames, List roles, XWikiContext context) throws SpaceManagerException;

    /**
     * Get the list of users for a role
     *
     * @param spaceName
     * @param role
     * @param context
     * @return
     */
    public Collection getUsersForRole(String spaceName, String role, XWikiContext context) throws SpaceManagerException;

    /**
     * Checks id the a user is a member in a space
     * @param spaceName
     * @param user
     * @param context
     * @return
     * @throws SpaceManagerException
     */
    public boolean isMember(String spaceName, String user, XWikiContext context) throws SpaceManagerException;

    /**
     *
     * @param spaceName
     * @param context
     * @return
     */
    public Collection getRoles(String spaceName, XWikiContext context) throws SpaceManagerException;

    /**
     *
     * @param spaceName
     * @param user
     * @param context
     * @return
     */
    public SpaceUserProfile getSpaceUserProfile(String spaceName, String user, XWikiContext context) throws SpaceManagerException;

    /**
     * Get the space user profile page name
     * @param userName
     * @param spaceName
     * @return
     */
    public String getSpaceUserProfilePageName(String userName, String spaceName);

    /**
     * Count spaces
     * @param context
     * @return int
     */
    public int countSpaces(XWikiContext context) throws SpaceManagerException;

    /**
     * Allows the current user to join the space
     * @param spaceName
     * @param context
     * @return
     */
    public boolean joinSpace(String spaceName, XWikiContext context) throws SpaceManagerException;
}
