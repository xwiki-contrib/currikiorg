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
package org.xwiki.plugin.spacemanager.impl;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.user.api.XWikiGroupService;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

import org.xwiki.plugin.spacemanager.api.*;
import org.xwiki.plugin.spacemanager.plugin.SpaceManagerPluginApi;

import java.util.*;

/**
 * Manages spaces
 */
public class SpaceManagerImpl extends XWikiDefaultPlugin implements SpaceManager {
	
	public final static String SPACEMANAGER_EXTENSION_CFG_PROP = "xwiki.spacemanager.extension";
	public final static String SPACEMANAGER_DEFAULT_EXTENSION = "org.xwiki.plugin.spacemanager.impl.SpaceManagerExtensionImpl";
	/**
	 * The extension that defines specific functions for this space manager
	 */
    protected SpaceManagerExtension spaceManagerExtension;

    /**
	 * Space manager constructor
	 * @param name
	 * @param className
	 * @param context
	 */
    public SpaceManagerImpl(String name, String className, XWikiContext context)
    {
        super(name, className, context);
    }

    /**
     * Flushes cache
     */
    public void flushCache() {
        super.flushCache();
    }

    /**
     * 
     * @param context Xwiki context
     * @return Returns the Space Class as defined by the extension
     * @throws XWikiException
     */
    protected BaseClass getSpaceClass(XWikiContext context) throws XWikiException {
    	XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;
        
        try {
            doc = xwiki.getDocument(getSpaceClassName(), context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(getSpaceClassName());
            needsUpdate = true;
        }
        
        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(getSpaceClassName());
        
        needsUpdate |= bclass.addTextField(SpaceImpl.SPACE_DISPLAYTITLE, "Display Name", 64);
        needsUpdate |= bclass.addTextAreaField(SpaceImpl.SPACE_DESCRIPTION, "Description", 45, 4);
        needsUpdate |= bclass.addTextField(SpaceImpl.SPACE_TYPE, "Group or plain space", 32);
        needsUpdate |= bclass.addTextField(SpaceImpl.SPACE_URLSHORTCUT, "URL Shortcut", 128);
        needsUpdate |= bclass.addStaticListField(SpaceImpl.SPACE_POLICY, "Membership Policy", 1, false, "open=Open membership|closed=Closed membership","radio");
        needsUpdate |= bclass.addStaticListField(SpaceImpl.SPACE_LANGUAGE, "Language", "eng=English|zho=Chinese|nld=Dutch|fra=French|deu=German|ita=Italian|jpn=Japanese|kor=Korean|por=Portuguese|rus=Russian|spa=Spanish");

        String content = doc.getContent();
        if ((content == null) || (content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 XWikiSpaceClass");
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
        return bclass;
    }

    /**
     * Gets the space type from the space manager extension
     * @return
     */
    public String getSpaceTypeName() {
        return getSpaceManagerExtension().getSpaceTypeName();
    }

    /**
     * Gets the space type from the space manager extension
     * @return
     */
    public String getSpaceClassName() {
        return getSpaceManagerExtension().getSpaceClassName();
    }

    /**
     * Checks if this space manager has custom mapping
     * @return
     */
    public boolean hasCustomMapping() {
        return getSpaceManagerExtension().hasCustomMapping();
    }

    
    /**
     * Initializes the plugin on the main wiki
     * @param context Xwiki context
     */
    public void init(XWikiContext context) {
        try {
        	getSpaceManagerExtension(context);
        	getSpaceManagerExtension().init(context);
            SpaceManagers.addSpaceManager(this);
        	getSpaceClass(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the plugin on a virtual wiki
     * @param context Xwiki context
     */
    public void virtualInit(XWikiContext context) {
        try {
        	getSpaceClass(context);
        	getSpaceManagerExtension().virtualInit(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the space plugin Api
     * @param plugin The plugin interface
     * @param context Xwiki context
     * @return
     */
    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
        return new SpaceManagerPluginApi((SpaceManager) plugin, context);
    }
	
	/**
	* Loads the SpaceManagerExtension specified in the config file
	* @return Returns the space manager extension
	 * @throws SpaceManagerException 
	*/
	public SpaceManagerExtension getSpaceManagerExtension(XWikiContext context) throws SpaceManagerException
	{
        if (spaceManagerExtension==null) {
            String extensionName = context.getWiki().Param(SPACEMANAGER_EXTENSION_CFG_PROP,SPACEMANAGER_DEFAULT_EXTENSION);
            try {
                if (extensionName!=null)
                 spaceManagerExtension = (SpaceManagerExtension) Class.forName(extensionName).newInstance();
            } catch (Throwable e){
                try{
                    spaceManagerExtension = (SpaceManagerExtension) Class.forName(SPACEMANAGER_DEFAULT_EXTENSION).newInstance();
                } catch(Throwable  e2){
                }
            }
        }

        if (spaceManagerExtension==null) {
            spaceManagerExtension = new SpaceManagerExtensionImpl();
        }

        return spaceManagerExtension;
    }
	
	public SpaceManagerExtension getSpaceManagerExtension(){
		return spaceManagerExtension;
	}
	
	/**
     * Gets the name of the space manager
     * @return
     */
    public String getName()
    {
        return "spacemanager";
    }


    private Object notImplemented() throws SpaceManagerException {
        throw new SpaceManagerException(SpaceManagerException.MODULE_PLUGIN_SPACEMANAGER, SpaceManagerException.ERROR_XWIKI_NOT_IMPLEMENTED, "not implemented");
    }
    
    /**
     * Returns the wikiname of a space
     * @param spaceTitle The name of the space
     * @param unique make space title unique
     * @param context XWiki Context
     * @return
     */
    public String getSpaceWikiName(String spaceTitle, boolean unique, XWikiContext context) {
        return getSpaceManagerExtension().getSpaceWikiName(spaceTitle,unique,  context);
    }

    /**
     * Gets the name of the space document for a specific space
     * @param spaceName The name of the space
     * @return
     */
    protected String getSpaceDocumentName(String spaceName) {
		return spaceName + ".WebPreferences";
	}
    
    /**
     * Creates a new space from scratch
     * @param spaceTitle The name(display title) of the new space
     * @param context The XWiki Context
     * @return Returns the newly created space
     * @throws SpaceManagerException 
     */
    public Space createSpace(String spaceTitle, XWikiContext context) throws SpaceManagerException {	
    	// Init out space object by creating the space
        // this will throw an exception when the space exists
        Space newspace = newSpace(null, spaceTitle, true, context);
        // Make sure we set the type
        newspace.setType(getSpaceTypeName());
        try {
            newspace.save();
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
        // we need to add the creator as a member and as an admin
        addAdmin(newspace.getSpaceName(), context.getUser(), context);
        addMember(newspace.getSpaceName(), context.getUser(), context);
        return newspace;
    }

    /**
     * Creates a new space based on an already existing space.   
     * @param spaceTitle The name(display title) of the new space
     * @param templateSpaceName The name of the space that will be cloned
     * @param context The XWiki Context
     * @return Returns the newly created space
     * @throws SpaceManagerException 
     */
	public Space createSpaceFromTemplate(String spaceTitle, String templateSpaceName, XWikiContext context) throws SpaceManagerException {
        // Init out space object by creating the space
        // this will throw an exception when the space exists
        Space newspace = newSpace(null, spaceTitle, false, context);

        // Make sure this space does not already exist
        if (!newspace.isNew())
            throw new SpaceManagerException(SpaceManagerException.MODULE_PLUGIN_SPACEMANAGER, SpaceManagerException.ERROR_SPACE_ALREADY_EXISTS, "Space already exists");

        // Copy over template data over our current data
        try {
            context.getWiki().copyWikiWeb(templateSpaceName, context.getDatabase(), context.getDatabase(), null, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }

        // Make sure we set the type
        newspace.setType(getSpaceTypeName());
        newspace.setDisplayTitle(spaceTitle);
        newspace.setCreator(context.getUser());
        try {
            newspace.save();
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
        // we need to add the creator as a member and as an admin
        addAdmin(newspace.getSpaceName(), context.getUser(), context);
        addMember(newspace.getSpaceName(), context.getUser(), context);
        return newspace;
    }

	/**
	 * 
	 */
    public Space createSpaceFromApplication(String spaceTitle, String applicationName, XWikiContext context) throws SpaceManagerException {
        notImplemented();
        return null;
    }


    /**
     * Creates a new space based on the data send in the request (possibly from a form) 
     * @param context The XWiki Context
     * @return Returns the newly created space
     * @throws SpaceManagerException 
     */
	public Space createSpaceFromRequest(String templateSpaceName, XWikiContext context) throws SpaceManagerException {
        // Init out space object by creating the space
        // this will throw an exception when the space exists
        String spaceTitle = context.getRequest().get(spaceManagerExtension.getSpaceClassName() + "_0_displayTitle");
        if (spaceTitle==null) {
            throw new SpaceManagerException(SpaceManagerException.MODULE_PLUGIN_SPACEMANAGER, SpaceManagerException.ERROR_SPACE_TITLE_MISSING, "Space title is missing");
        }
        Space newspace = newSpace(null, spaceTitle, true, context);
        newspace.updateSpaceFromRequest();
        if (!newspace.validateSpaceData())
            throw new SpaceManagerException(SpaceManagerException.MODULE_PLUGIN_SPACEMANAGER, SpaceManagerException.ERROR_SPACE_DATA_INVALID, "Space data is not valid");

        // Copy over template data over our current data
        if(templateSpaceName != null){
            try {
                List list = context.getWiki().getStore().searchDocumentsNames("where doc.web='" + templateSpaceName + "'", context);
                for (Iterator it = list.iterator(); it.hasNext();) {
                    String docname = (String) it.next();
                    XWikiDocument doc = context.getWiki().getDocument(docname, context);
                    context.getWiki().copyDocument(doc.getFullName(), newspace.getSpaceName() + "." + doc.getName(), context);
                }
            } catch (XWikiException e) {
                throw new SpaceManagerException(e);
            }
        }

        // Make sure we set the type
        newspace.setType(getSpaceTypeName());
        // we need to do it twice because data could have been overwritten by copyWikiWeb
		newspace.updateSpaceFromRequest();
        try {
            newspace.save();
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
        // we need to add the creator as a member and as an admin
        addAdmin(newspace.getSpaceName(), context.getUser(), context);
        addMember(newspace.getSpaceName(), context.getUser(), context);
        return newspace;
	}

    protected Space newSpace(String spaceName, String spaceTitle, boolean create, XWikiContext context) throws SpaceManagerException {
        return new SpaceImpl(spaceName, spaceTitle, create, this, context );
    }

    /**
     * Creates a new space based on the data send in the request (possibly from a form)
     * @param context The XWiki Context
     * @return Returns the newly created space
     * @throws SpaceManagerException
     */
	public Space createSpaceFromRequest(XWikiContext context) throws SpaceManagerException {
        return createSpaceFromRequest(null, context);
	}

    /**
	 * Deletes a space
	 * @param spaceName The name of the space to be deleted
	 * @param deleteData Full delete (all the space data will be deleted)
	 * @param context The XWiki Context
     * @throws SpaceManagerException  
	 */
    public void deleteSpace(String spaceName, boolean deleteData, XWikiContext context) throws SpaceManagerException {
        if (deleteData) {
            // we are not implementing full delete yet
            throw new SpaceManagerException(SpaceManagerException.MODULE_PLUGIN_SPACEMANAGER, SpaceManagerException.ERROR_XWIKI_NOT_IMPLEMENTED, "Not implemented");
        }
        Space space = getSpace(spaceName, context);
        if (!space.isNew()) {
            space.setType("deleted");
            try {
                space.save();
            } catch (XWikiException e) {
                throw new SpaceManagerException(e);                
            }
        }

    }

    /**
	 * Deletes a space without deleting the data
	 * @param spaceName The name of the space to be deleted
	 * @param context The XWiki Context
     * @throws SpaceManagerException  
	 */
    public void deleteSpace(String spaceName, XWikiContext context) throws SpaceManagerException {
        deleteSpace(spaceName, false, context);
    }

    /**
	 * Restores a space that hasn't been fully deleted
	 * @param spaceName The name of the space to be restored
	 * @param context The XWiki Context
     * @throws SpaceManagerException  
	 */
	public void undeleteSpace(String spaceName, XWikiContext context) throws SpaceManagerException {
        Space space = getSpace(spaceName, context);
        if (space.isDeleted()) {
             space.setType(getSpaceTypeName());
            try {
                space.save();
            } catch (XWikiException e) {
                throw new SpaceManagerException(e);
            }
        }	
	}

	/**
	 * Returns the space with the spaceName
	 * @param spaceName The name of the space to be deleted
	 * @param context The XWiki Context
     * @throws SpaceManagerException  
	 */
    public Space getSpace(String spaceName, XWikiContext context) throws SpaceManagerException {
        // Init the space object but do not create anything if it does not exist
        return newSpace(spaceName, spaceName, false, context);
    }

    /**
	 * Returns a list of nb spaces starting at start
	 * @param nb Number of spaces to be returned
	 * @param start Index at which we will start the search
	 * @param context The XWiki Context
	 * @return list of Space objects
     * @throws SpaceManagerException  
	 */
    public List getSpaces(int nb, int start, XWikiContext context) throws SpaceManagerException {
        List spaceNames = getSpaceNames(nb, start, context);
        return getSpaceObjects(spaceNames, context);
    }

    /**
	 * Returns a list of nb space names starting at start
	 * @param context The XWiki Context
	 * @return list of Space objects
     * @throws SpaceManagerException  
	 */
    protected List getSpaceObjects(List spaceNames, XWikiContext context) throws SpaceManagerException {
        if (spaceNames==null)
         return null;
        List spaceList = new ArrayList();
        for (int i=0;i<spaceNames.size();i++) {
            String spaceName = (String) spaceNames.get(i);
            Space space = getSpace(spaceName, context);
            spaceList.add(space);
        }
        return spaceList;
    }

    /**
	 * Gets the names of nb spaces starting at start 
	 * @param nb Number of spaces to be returned
	 * @param start Index at which we will start the search
	 * @param context The XWiki Context
	 * @return list of Strings (space names)
     * @throws SpaceManagerException  
	 */
    public List getSpaceNames(int nb, int start, XWikiContext context) throws SpaceManagerException {
        String type = getSpaceTypeName();
        String className = getSpaceClassName();
        String sql;
        if (hasCustomMapping())
            sql = "select distinct doc.web from XWikiDocument as doc, BaseObject as obj, " + className + " as space where doc.fullName = obj.name and obj.className='"
                 + className + "' and obj.id = space.id and space.type='" + type + "'";
        else
            sql = "select distinct doc.web from XWikiDocument as doc, BaseObject as obj, StringProperty typeprop where doc.fullName=obj.name and obj.className = '"
                    + className + "' and obj.id=typeprop.id.id and typeprop.id.name='type' and typeprop.value='" + type + "'";

        List spaceList = null;
        try {
            spaceList = context.getWiki().getStore().search(sql, nb, start, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
        return spaceList;
    }
    
    /**
	 * Performs a hql search and returns the matching Space objects  
	 * @param fromsql
     *@param wherehql
     * @param nb Number of objects to be returned
     * @param start Index at which we will start the search
     * @param context The XWiki Context @return list of Space objects
     * @throws SpaceManagerException  
	 */
    public List searchSpaces(String fromsql, String wherehql, int nb, int start, XWikiContext context) throws SpaceManagerException {
        List spaceNames = searchSpaceNames(fromsql, wherehql, nb, start, context);
        return getSpaceObjects(spaceNames, context);
    }

    /**
	 * Performs a hql search and returns the matching space names  
	 * @param fromsql
     *@param wheresql
     * @param nb Number of objects to be returned
     * @param start Index at which we will start the search
     * @param context The XWiki Context @return list of Strings (space names)
     * @throws SpaceManagerException  
	 */
    public List searchSpaceNames(String fromsql, String wheresql, int nb, int start, XWikiContext context) throws SpaceManagerException {
        String type = getSpaceTypeName();
        String className = getSpaceClassName();
        String sql;
        if (hasCustomMapping())
            sql = "select distinct doc.web from XWikiDocument as doc, BaseObject as obj, " + className + " as space" + fromsql + " where doc.fullName = obj.name and obj.className='"
                 + className + "' and obj.id = space.id and space.type='" + type + "'" + wheresql;
        else
            sql = "select distinct doc.web from XWikiDocument as doc, BaseObject as obj, StringProperty as typeprop" + fromsql + " where doc.fullName=obj.name and obj.className = '"
                    + className + "' and obj.id=typeprop.id.id and typeprop.id.name='type' and typeprop.value='" + type + "'" + wheresql;

        List spaceList = null;
        try {
            spaceList = context.getWiki().getStore().search(sql, nb, start, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
        return spaceList;
    }

    /**
	 * Gets a list of spaces in which a specific user has a specific role  
	 * @param userName The username of the targeted user
	 * @param role The role which the user must have
	 * @param context The XWiki Context
	 * @return list of Space objects
     * @throws SpaceManagerException  
	 */
    public List getSpaces(String userName, String role, XWikiContext context) throws SpaceManagerException {
        List spaceNames = getSpaceNames(userName, role, context);
        return getSpaceObjects(spaceNames, context);
    }

    /**
	 * Gets a list of spaces names in which a specific user has a specific role  
	 * @param userName The username of the targeted user
	 * @param role The role which the user must have
	 * @param context The XWiki Context
	 * @return list of Strings (space names)
     * @throws SpaceManagerException  
	 */
    public List getSpaceNames(String userName, String role, XWikiContext context) throws SpaceManagerException {
        String sql;
        if (role==null)
         sql = "select distinct doc.web from XWikiDocument as doc, BaseObject as obj, StringProperty as memberprop where doc.name='MemberGroup' and doc.fullName=obj.name and obj.className = 'XWiki.XWikiGroups'"
                + " and obj.id=memberprop.id.id and memberprop.id.name='member' and memberprop.value='" + userName + "'";
        else {
            String roleGroupName = getRoleGroupName("", role).substring(1);
            sql = "select distinct doc.web from XWikiDocument as doc, BaseObject as obj, StringProperty as memberprop where doc.name='" + roleGroupName + "' and doc.fullName=obj.name and obj.className = 'XWiki.XWikiGroups'"
                   + " and obj.id=memberprop.id.id and memberprop.id.name='member' and memberprop.value='" + userName + "'";

        }
        List spaceList = null;
        try {
            spaceList = context.getWiki().getStore().search(sql, 0, 0, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
        return spaceList;
    }

    public void updateSpaceFromRequest(Space space, XWikiContext context) throws SpaceManagerException {
        space.updateSpaceFromRequest();
    }

    public boolean validateSpaceData(Space space, XWikiContext context) throws SpaceManagerException {
        return space.validateSpaceData();
    }

    /**
     * Saves a space
     * @param space Name of the space to be saved
     * @throws SpaceManagerException
     */
    public void saveSpace(Space space, XWikiContext context) throws SpaceManagerException {
        try {
            space.save();
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    public void addAdmin(String spaceName, String username, XWikiContext context) throws SpaceManagerException {
        try {
            addUserToGroup(username, getAdminGroupName(spaceName), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }


    public void addAdmins(String spaceName, List usernames, XWikiContext context) throws SpaceManagerException {
        for(int i=0;i<usernames.size();i++) {
            addAdmin(spaceName, (String) usernames.get(i), context);
        }
    }


    public Collection getAdmins(String spaceName, XWikiContext context) throws SpaceManagerException {
        try {
            return getGroupService(context).getAllMembersNamesForGroup(getAdminGroupName(spaceName), 0, 0, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }


    public void addUserToRole(String spaceName, String username, String role, XWikiContext context) throws SpaceManagerException {
        try {
            addUserToGroup(username, getRoleGroupName(spaceName, role), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }


    public void addUsersToRole(String spaceName, List usernames, String role, XWikiContext context) throws SpaceManagerException {
        for(int i=0;i<usernames.size();i++) {
            addUserToRole(spaceName, (String) usernames.get(i), role, context);
        }
    }


    public Collection getUsersForRole(String spaceName, String role, XWikiContext context) throws SpaceManagerException {
        try {
            return getGroupService(context).getAllMembersNamesForGroup(getMemberGroupName(spaceName), 0, 0, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    
    public boolean userIsMember(String spaceName, String username, XWikiContext context) throws SpaceManagerException {
        try {
            return isMember(username, getMemberGroupName(spaceName), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

	public void addUserToRoles(String spaceName, String username, List roles, XWikiContext context) throws SpaceManagerException {
        for(int i=0;i<roles.size();i++) {
            addUserToRole(spaceName, username, (String) roles.get(i), context);
        }
    }


	public void addUsersToRoles(String spaceName, List usernames, List roles, XWikiContext context) throws SpaceManagerException {
        for(int i=0;i<usernames.size();i++) {
            addUserToRoles(spaceName, (String) usernames.get(i), roles, context);
        }
    }



	public void addMember(String spaceName, String username, XWikiContext context) throws SpaceManagerException {
        try {
            addUserToGroup(username, getMemberGroupName(spaceName), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }


    private boolean isMember(String username, String groupname, XWikiContext context) throws XWikiException {
        Collection coll = context.getWiki().getGroupService(context).getAllGroupsNamesForMember(username, 0, 0, context);
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            if (groupname.equals((String) it.next()))
                return true;
        }
        return false;
    }

    /**
     * High speed user adding without resaving the whole groups doc
     * @param username
     * @param groupName
     * @param context
     * @throws XWikiException
     */
    private void addUserToGroup(String username, String groupName, XWikiContext context) throws XWikiException
    {
        // don't add if he is already a member
        if (isMember(username, groupName, context))
         return;

        XWiki xwiki = context.getWiki();
        BaseClass groupClass = xwiki.getGroupClass(context);
        XWikiDocument groupDoc = xwiki.getDocument(groupName, context);

        BaseObject memberObject = (BaseObject) groupClass.newObject(context);
        memberObject.setClassName(groupClass.getName());
        memberObject.setName(groupDoc.getFullName());
        memberObject.setStringValue("member", username);
        groupDoc.addObject(groupClass.getName(), memberObject);
        if (groupDoc.isNew()) {
            xwiki.saveDocument(groupDoc, context.getMessageTool().get("core.comment.addedUserToGroup"),
                context);
        } else {
            xwiki.getHibernateStore().saveXWikiObject(memberObject, context, true);
        }
   }



    public void addMembers(String spaceName, List usernames, XWikiContext context) throws SpaceManagerException {
        for(int i=0;i<usernames.size();i++) {
            addMember(spaceName, (String) usernames.get(i), context);
        }
    }


	public Collection getMembers(String spaceName, XWikiContext context) throws SpaceManagerException {
        try {
            return getGroupService(context).getAllMembersNamesForGroup(getMemberGroupName(spaceName), 0, 0, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }


    public String getMemberGroupName(String spaceName) {
        return getSpaceManagerExtension().getMemberGroupName(spaceName);
    }

    public String getAdminGroupName(String spaceName) {
        return getSpaceManagerExtension().getAdminGroupName(spaceName);
    }

    public String getRoleGroupName(String spaceName, String role) {
        return getSpaceManagerExtension().getRoleGroupName(spaceName, role);
    }

    protected XWikiGroupService getGroupService(XWikiContext context) throws XWikiException {
       return context.getWiki().getGroupService(context);
    }

    public SpaceUserProfile getUserSpaceProfile(String spaceName, String username, XWikiContext context) throws SpaceManagerException {
        return newUserSpaceProfile(username, spaceName, context);
    }

    public String getSpaceUserProfilePageName(String userName, String spaceName) {
        return getSpaceManagerExtension().getSpaceUserProfilePageName(userName, spaceName);
    }

    protected SpaceUserProfile newUserSpaceProfile(String user, String space, XWikiContext context) throws SpaceManagerException {
        try {
            return new SpaceUserProfileImpl(user, space, this, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    public List getLastModifiedDocuments(String spaceName, XWikiContext context, boolean recursive, int nb, int start) throws SpaceManagerException {
        //notImplemented();
        return null;
    }


    public Collection getRoles(String spaceName, XWikiContext context) throws SpaceManagerException {
        notImplemented();
        return null;
    }

    public List getLastModifiedDocuments(String spaceName, XWikiContext context) throws SpaceManagerException {
        notImplemented();
        return null;
    }

    public List searchDocuments(String spaceName, String hql, XWikiContext context) throws SpaceManagerException {
        notImplemented();
        return null;
    }

    public int countSpaces(XWikiContext context) throws SpaceManagerException {
        String type = getSpaceTypeName();
        String className = getSpaceClassName();
        String sql;
        if (hasCustomMapping())
            sql = "select count(*) from XWikiDocument as doc, BaseObject as obj, " + className + " as space"
                 + " where doc.fullName = obj.name and obj.className='" + className + "' and obj.id = space.id and space.type='" + type + "'";
        else
            sql = "select count(*) from XWikiDocument as doc, BaseObject as obj, StringProperty as typeprop"
                 + " where doc.fullName=obj.name and obj.className = '" + className + "' and obj.id=typeprop.id.id and typeprop.id.name='type' and typeprop.value='" + type + "'";
        
        try {
            List result = context.getWiki().search(sql, context);
            Integer res = (Integer) result.get(0);
            return res.intValue();                            
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

}
