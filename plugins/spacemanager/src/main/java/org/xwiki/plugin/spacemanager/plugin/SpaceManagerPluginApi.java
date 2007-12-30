/**
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * <p/>
 * This is free software;you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation;either version2.1of
 * the License,or(at your option)any later version.
 * <p/>
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software;if not,write to the Free
 * Software Foundation,Inc.,51 Franklin St,Fifth Floor,Boston,MA
 * 02110-1301 USA,or see the FSF site:http://www.fsf.org.
 */
package org.xwiki.plugin.spacemanager.plugin;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.plugin.PluginApi;
import org.xwiki.plugin.spacemanager.api.Space;
import org.xwiki.plugin.spacemanager.api.SpaceManager;
import org.xwiki.plugin.spacemanager.api.SpaceManagerException;
import org.xwiki.plugin.spacemanager.api.SpaceUserProfile;

import java.util.List;
import java.util.Collection;

/**
 * Api for creating and retrieving Spaces 
 */
public class SpaceManagerPluginApi extends PluginApi
{
	public static String getVersion(){
		return "dd";
	}
	
    public SpaceManagerPluginApi(SpaceManager plugin, XWikiContext context)
    {
        super(plugin, context);
    }

    protected SpaceManager getSpaceManager() {
        return (SpaceManager) getPlugin();
    }

    /**
     * The plugin will contain SpaceManager api calls to the underlying space manager plugin.
     * Security will be handled by this plugin.
     */

    /**
     * @return the Space associated with the context web
     */
    public Space getCurrentSpace() throws SpaceManagerException {
        return getSpace(context.getDoc().getSpace());
    }


    public Space getSpace(String spaceName) throws SpaceManagerException {
        Space space =
                getSpaceManager().getSpace(spaceName, context);
        return space;
    }

    /**
     * Create a space from scratch
     * It will create an empty space or will copy the default space template if there is one
     *
     * @param spaceName
     * @return On success returns the newly created space and null on failure
     * @throws SpaceManagerException
     */
    public Space createSpace(String spaceName) throws SpaceManagerException {
        if (!hasProgrammingRights())
            return null;
        Space space =
                getSpaceManager().createSpace(spaceName, context);
        return space;
    }

    /**
     * Create a space based on a template space
     *
     * @param spaceName
     * @param templateSpaceName
     * @return On success returns the newly created space and null on failure
     */
    public Space createSpaceFromTemplate(String spaceName, String templateSpaceName) throws SpaceManagerException {
        if (!hasProgrammingRights())
            return null;
        Space space =
                getSpaceManager().createSpaceFromTemplate(spaceName, templateSpaceName, context);
        return space;

    }

    /**
     * Create a space and install an application in the space
     * An application is handled by the ApplicationManager plugin and can include other sub-applications
     *
     * @param spaceName
     * @param applicationName
     * @return On success returns the newly created space and null on failure
     */
    public Space createSpaceFromApplication(String spaceName, String applicationName) throws SpaceManagerException {
        if (!hasProgrammingRights())
            return null;
        Space space =
                getSpaceManager().createSpaceFromApplication(spaceName, applicationName, context);
        return space;
    }

    /**
     * @return On success returns the newly created space and null on failure
     */
    public Space createSpaceFromRequest() throws SpaceManagerException {
        if (!hasProgrammingRights())
            return null;
        Space space =
                getSpaceManager().createSpaceFromRequest(context);
        return space;
    }

    /**
     * @return On success returns the newly created space and null on failure
     */
    public Space createSpaceFromRequest(String templateSpace) throws SpaceManagerException {
        if (!hasProgrammingRights())
            return null;
        Space space =
                getSpaceManager().createSpaceFromRequest(templateSpace, context);
        return space;
    }


    /**
     * Delete a space, including or not the space data
     *
     * @param spaceName
     * @param deleteData
     */

    public boolean deleteSpace(String spaceName, boolean deleteData) throws SpaceManagerException {
        if (!hasProgrammingRights())
            return false;
        getSpaceManager().deleteSpace(spaceName, deleteData, context);
        return true;
    }

    /**
     *
     * @param spaceName
     */
    public boolean undeleteSpace(String spaceName) throws SpaceManagerException {
        if (!hasProgrammingRights())
            return false;
        getSpaceManager().undeleteSpace(spaceName, context);
        return true;

    }

    /**
     * Get the list of space objects
     *
     * @param nb
     * @param start
     * @return list of space objects
     */
    public List getSpaces(int nb, int start) throws SpaceManagerException {
        List spacesList = getSpaceManager().getSpaces(nb, start, context);
        return spacesList;
    }

    /*
    private List wrapSpaces(List spacesList) {
        List list = new ArrayList();
        if (spacesList==null)
         return null;
        for (int i=0;i<spacesList.size();i++) {
            list.add(new Space((Space) spacesList.get(i), context));
        }
        return list;
    } */

    /**
     * Get the list of space objects
     *
     * @param start
     * @param nb
     * @return list of space names
     */
    public List getSpaceNames(int nb, int start) throws SpaceManagerException {
        return getSpaceManager().getSpaceNames(nb, start, context);
    }

    /**
     * Search for spaces using an HQL query returning Space objects
     *
     * @param hql
     * @param start
     * @param nb
     * @return list of space objects
     */
    public List searchSpaces(String hql, int nb, int start) throws SpaceManagerException {
        List spacesList = getSpaceManager().searchSpaces(hql, "", nb, start, context);
        return spacesList;

    }

    /**
     * Search for spaces using an HQL query returning Space Names
     *
     * @param hql
     * @param start
     * @param nb
     * @return
     */
    public List searchSpaceNames(String hql, int nb, int start) throws SpaceManagerException {
        return getSpaceManager().searchSpaceNames(hql, "", nb, start, context);
    }
    /**
     * Get the list of spaces for a user in a specific role
     * If role is null it will get all spaces in which the user is member
     * return space name
     *
     * @param userName
     * @param role
     * @return list of space objects
     */
    public List getSpaces(String userName, String role) throws SpaceManagerException {
        List spacesList = getSpaceManager().getSpaces(userName, role, context);
        return spacesList;
    }

    /**
     * Get the list of spaces for a user in a specific role
     * If role is null it will get all spaces in which the user is member
     *
     * @param userName
     * @param role
     * @return list of space names
     */
    public List getSpaceNames(String userName, String role) throws SpaceManagerException {
        return getSpaceManager().getSpaceNames(userName, role, context);
    }

    /**
     * Updates a space object from the HTTP request data
     *
     * @param space
     */
    public boolean updateSpaceFromRequest(Space space) throws SpaceManagerException {
        if (!hasProgrammingRights())
         return false;

        getSpaceManager().updateSpaceFromRequest(space, context);
        return true;
    }

    /**
     * Validate that the space data is valid. Wrong data are stored in the context
     *
     * @param space
     * @return
     */
    public boolean validateSpaceData(Space space) throws SpaceManagerException {
        return getSpaceManager().validateSpaceData(space, context);
    }

    /**
     * Save the space data to the storage system
     *
     * @param space
     */
    // public void saveSpace(Space space) throws SpaceManagerException;

    /**
     * Get the list of last modified documents in the space
     *
     * @param space The space in which the search is performed
     * @param recursive Determines if the search is performed in the child spaces too
     * @param nb Number of documents to be retrieved
     * @return start Pagination option saying at what document index to start the search
     */
     public List getLastModifiedDocuments(String spaceName, boolean recursive, int nb, int start) throws SpaceManagerException{
    	 return getSpaceManager().getLastModifiedDocuments(spaceName, context, recursive, nb, start);
     }


    /**
     * Return the list of members of the space
     * @param spaceName
     * @throws SpaceManagerException
     */  
     public Collection getMembers(String spaceName) throws SpaceManagerException {
         return getSpaceManager().getMembers(spaceName, context);
     }

    /**
     * Join the space
     *
     * @param spaceName
     */
    public boolean joinSpace(String spaceName) throws SpaceManagerException {
        Space space = getSpace(spaceName);
        if ("open".equals(space.getPolicy())) {
            return getSpaceManager().joinSpace(spaceName, context);
        } else {
            return false;
        }
    }

    /**
     * Add a wiki user as member in the space
     *
     * @param spaceName
     * @param wikiname
     */
    public void addMember(String spaceName, String wikiname) throws SpaceManagerException {
        if (hasProgrammingRights())
         getSpaceManager().addMember(spaceName, wikiname, context);
    }

    /**
     * Add a wiki user as admin in the space
     *
     * @param spaceName
     * @param wikiname
     */
    public void addAdmin(String spaceName, String wikiname) throws SpaceManagerException {
        if (hasProgrammingRights())
         getSpaceManager().addAdmin(spaceName, wikiname, context);
    }


    /**
     * Search for documents in the space
     *
     * @param space
     * @param hql
     * @return
     */
    // public List searchDocuments(Space space, String hql) throws SpaceManagerException;

    /**
     * Add a wiki user as admin in the space
     *
     * @param space
     * @param wikiname
     */
    // public void addAdmin(Space space, String wikiname) throws SpaceManagerException;

    /**
     * Add a list of admins in the space
     *
     * @param space
     * @param wikinames
     */
    // public void addAdmins(Space space, List wikinames) throws SpaceManagerException;

    /**
     * @return the list of all members of the space that are admins
     */
    // public List getAdmins(Space space) throws SpaceManagerException;

    /**
     * Add user to the specific role in the space
     *
     * @param space
     * @param wikiname
     * @param roles
     */
    // public void addUserToRole(Space space, String wikiname, List roles) throws SpaceManagerException;

    /**
     * Add a list of users to a specific role in the space
     *
     * @param space
     * @param wikinames
     * @param roles
     */
    // public void addUsersToRole(Space space, List wikinames, List roles) throws SpaceManagerException;

    /**
     * Get the list of users for a role
     *
     * @param space
     * @param role
     * @return
     */
    // public List getUsersForRole(Space space, String role) throws SpaceManagerException;

    /**
     *
     * @param space
     * @return
     */
    // public List getRoles(Space space) throws SpaceManagerException;

    /**
     * Gets a user profile object
     * @param spaceName
     * @param user
     * @return
     */
    public SpaceUserProfile getSpaceUserProfile(String spaceName, String user) throws SpaceManagerException {
        return getSpaceManager().getSpaceUserProfile(spaceName, user, context);
    }

    /**
     * Count number of spaces
     * @return
     */
    public int countSpaces() throws SpaceManagerException {
        return getSpaceManager().countSpaces(context);
    }

    public boolean isMember(String spaceName, String username) throws SpaceManagerException {
        return getSpaceManager().isMember(spaceName, username, context);
    }

}
