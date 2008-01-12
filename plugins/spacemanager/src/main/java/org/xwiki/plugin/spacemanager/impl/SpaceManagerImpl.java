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
import com.xpn.xwiki.plugin.mailsender.Mail;
import com.xpn.xwiki.plugin.mailsender.MailSenderPlugin;
import com.xpn.xwiki.render.XWikiVelocityRenderer;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.xwiki.plugin.spacemanager.api.*;
import org.xwiki.plugin.spacemanager.plugin.SpaceManagerPluginApi;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;

/**
 * Manages spaces
 */
public class SpaceManagerImpl extends XWikiDefaultPlugin implements SpaceManager {

    public final static String SPACEMANAGER_EXTENSION_CFG_PROP = "xwiki.spacemanager.extension";
    public final static String SPACEMANAGER_DEFAULT_EXTENSION = "org.xwiki.plugin.spacemanager.impl.SpaceManagerExtensionImpl";
    public final static String SPACEMANAGER_DEFAULT_MAIL_NOTIFICATION = "1";

    /**
     * The extension that defines specific functions for this space manager
     */
    protected SpaceManagerExtension spaceManagerExtension;

    protected boolean mailNotification;

    /**
     * Space manager constructor
     * @param name
     * @param className
     * @param context
     */
    public SpaceManagerImpl(String name, String className, XWikiContext context)
    {
        super(name, className, context);
        String mailNotificationCfg =
                context.getWiki().Param("xwiki.spacemanager.mailnotification",
                        SpaceManagerImpl.SPACEMANAGER_DEFAULT_MAIL_NOTIFICATION).trim();
        mailNotification = "1".equals(mailNotificationCfg);
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
        needsUpdate |= bclass.addTextField(SpaceImpl.SPACE_URLSHORTCUT, "URL Shortcut", 40);
        needsUpdate |= bclass.addStaticListField(SpaceImpl.SPACE_POLICY, "Membership Policy", 1, false, "open=Open membership|closed=Closed membership","radio");
        needsUpdate |= bclass.addStaticListField(SpaceImpl.SPACE_LANGUAGE, "Language", "en=English|zh=Chinese|nl=Dutch|fr=French|de=German|it=Italian|jp=Japanese|kr=Korean|po=Portuguese|ru=Russian|sp=Spanish");

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
            SpaceUserProfileImpl.getSpaceUserProfileClass(context);
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
     * Gives a group certain rights over a space
     * @param spaceName Name of the space
     * @param groupName Name of the group that will have the value
     * @param level Access level
     * @param allow True if the right is allow, deny if not
     */
    protected boolean giveRightToGroup( String spaceName, String groupName, String level, boolean allow, boolean global, XWikiContext context) throws XWikiException {
        final String rightsClass = global ? "XWiki.XWikiGlobalRights" : "XWiki.XWikiRights";
        final String prefDocName = spaceName+".WebPreferences";
        final String groupsField = "groups";
        final String levelsField = "levels";
        final String allowField = "allow";

        XWikiDocument prefDoc;
        prefDoc = context.getWiki().getDocument(prefDocName,context);

        //checks to see if the right is not already given
        boolean exists = false;
        boolean isUpdated = false;
        int indx = -1;
        boolean foundlevel = false;
        int allowInt;
        if(allow)	allowInt = 1;
        else	allowInt = 0;
        List objs = prefDoc.getObjects(rightsClass);
        if (objs!=null) {
            for(int i=0; i<objs.size(); i++){
                BaseObject bobj = (BaseObject) objs.get(i);
                if(bobj==null)
                    continue;
                String groups = bobj.getStringValue(groupsField);
                String levels = bobj.getStringValue(levelsField);
                int allowDeny = bobj.getIntValue(allowField);
                boolean allowdeny = (bobj.getIntValue(allowField) == 1);
                String[] levelsarray = levels.split( " ,|" );
                String[] groupsarray = groups.split( " ,|" );
                if( ArrayUtils.contains(groupsarray,groupName) ){
                    exists = true;
                    if(!foundlevel)
                        indx = i;
                    if( ArrayUtils.contains(levelsarray,level) ){
                        foundlevel = true;
                        if(allowInt == allowDeny){
                            isUpdated = true;
                            break;
                        }
                    }
                }
            }
        }

        //sets the rights. the aproach is to break rules/levels in as many XWikiRigts elements so
        //we don't have to handle lots of situation when we change rights
        if( !exists ){
            BaseObject bobj = new BaseObject();
            bobj.setClassName(rightsClass);
            bobj.setName(prefDoc.getFullName());
            bobj.setStringValue(groupsField, groupName);
            bobj.setStringValue(levelsField, level);
            bobj.setIntValue(allowField, allowInt);
            prefDoc.addObject(rightsClass,bobj);
            context.getWiki().saveDocument(prefDoc, context);
            return true;
        }else{
            if(isUpdated){
                return true;
            }else{
                BaseObject bobj = (BaseObject) objs.get(indx);
                String groups = bobj.getStringValue(groupsField);
                String levels = bobj.getStringValue(levelsField);
                String[] levelsarray = levels.split( " ,|" );
                String[] groupsarray = groups.split( " ,|" );

                if( levelsarray.length == 1 && groupsarray.length == 1 && levelsarray[0] == level ){
                    //if there is only this group and this level in the rule update this rule
                } else {
                    //if there are more groups/levels, extract this one(s)
                    bobj = new BaseObject();
                    bobj.setName(prefDoc.getFullName());
                    bobj.setClassName(rightsClass);
                    bobj.setStringValue(levelsField, level);
                    bobj.setIntValue(allowField, allowInt);
                    bobj.setStringValue(groupsField, groupName);
                }

                prefDoc.addObject(rightsClass,bobj);
                context.getWiki().saveDocument(prefDoc, context);
                return true;
            }
        }
    }

    protected void setGroupRights(Space newspace, XWikiContext context) throws XWikiException {
        // Set admin edit rights on group prefs
        giveRightToGroup( newspace.getSpaceName(), getAdminGroupName( newspace.getSpaceName() ), "edit", true, false, context );
        // Set admin admin rights on group prefs
        giveRightToGroup( newspace.getSpaceName(), getAdminGroupName( newspace.getSpaceName() ), "admin", true, true, context );
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
            newspace.saveWithProgrammingRights();
            // we need to add the creator as a member and as an admin
            addAdmin(newspace.getSpaceName(), context.getUser(), context);
            addMember(newspace.getSpaceName(), context.getUser(), context);
            setGroupRights(newspace, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }

        sendMail(SpaceAction.CREATE, newspace, context);
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
            newspace.saveWithProgrammingRights();
            // we need to add the creator as a member and as an admin
            addAdmin(newspace.getSpaceName(), context.getUser(), context);
            addMember(newspace.getSpaceName(), context.getUser(), context);
            setGroupRights(newspace, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
        sendMail(SpaceAction.CREATE, newspace, context);

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
            newspace.saveWithProgrammingRights();
            // we need to add the creator as a member and as an admin
            addAdmin(newspace.getSpaceName(), context.getUser(), context);
            addMember(newspace.getSpaceName(), context.getUser(), context);
            setGroupRights(newspace, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }

        sendMail(SpaceAction.CREATE, newspace, context);
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
                space.saveWithProgrammingRights();
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
                space.saveWithProgrammingRights();
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
            space.saveWithProgrammingRights();
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

    /**
     * {@inheritDoc}
     *
     * @see SpaceManager#removeAdmin(String, String, XWikiContext)
     */
    public void removeAdmin(String spaceName, String userName, XWikiContext context)
            throws SpaceManagerException
    {
        try {
            removeUserFromGroup(userName, getAdminGroupName(spaceName), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    public boolean isAdmin(String spaceName, String userName, XWikiContext context)
            throws SpaceManagerException
    {
        try {
            return isMemberOfGroup(userName, getAdminGroupName(spaceName), context);
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


    public boolean isMember(String spaceName, String username, XWikiContext context) throws SpaceManagerException {
        try {
            return isMemberOfGroup(username, getMemberGroupName(spaceName), context);
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

    /**
     * {@inheritDoc}
     *
     * @see SpaceManager#removeUserFromRoles(String, String, List, XWikiContext)
     */
    public void removeUserFromRoles(String spaceName, String userName, List roles,
                                    XWikiContext context) throws SpaceManagerException
    {
        for (int i = 0; i < roles.size(); i++) {
            removeUserFromRole(spaceName, userName, (String) roles.get(i), context);
        }
    }

    public void removeUserFromRole(String spaceName, String userName, String role,
                                   XWikiContext context) throws SpaceManagerException
    {
        try {
            removeUserFromGroup(userName, getRoleGroupName(spaceName, role), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    public void addMember(String spaceName, String username, XWikiContext context) throws SpaceManagerException {
        try {
            addUserToGroup(username, getMemberGroupName(spaceName), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see SpaceManager#removeMember(String, String, XWikiContext)
     */
    public void removeMember(String spaceName, String userName, XWikiContext context)
            throws SpaceManagerException
    {
        try {
            // remove admin role
            if (isAdmin(spaceName, userName, context)) {
                removeAdmin(spaceName, userName, context);
            }
            // remove all the other roles
            //Iterator it = getRoles(spaceName, context).iterator();
            //while (it.hasNext()) {
            //    String role = (String) it.next();
            //    removeUserFromRole(spaceName, userName, role, context);
            //}
            // delete space user profile
            deleteSpaceUserProfile(spaceName, userName, context);
            // remove member
            removeUserFromGroup(userName, getMemberGroupName(spaceName), context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }


    private boolean isMemberOfGroup(String username, String groupname, XWikiContext context) throws XWikiException {
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
        if (isMemberOfGroup(username, groupName, context))
            return;

        XWiki xwiki = context.getWiki();
        BaseClass groupClass = xwiki.getGroupClass(context);
        XWikiDocument groupDoc = xwiki.getDocument(groupName, context);

        BaseObject memberObject = (BaseObject) groupClass.newObject(context);
        memberObject.setClassName(groupClass.getName());
        memberObject.setName(groupDoc.getFullName());
        memberObject.setStringValue("member", username);
        groupDoc.addObject(groupClass.getName(), memberObject);
        String content = groupDoc.getContent();
        if ((content == null)||(content.equals("")))
            groupDoc.setContent("#includeForm(\"XWiki.XWikiGroupSheet\")");
        xwiki.saveDocument(groupDoc, context.getMessageTool().get("core.comment.addedUserToGroup"),
                context);
        /*
        if (groupDoc.isNew()) {
        } else {
            xwiki.getHibernateStore().saveXWikiObject(memberObject, context, true);
        }
        // we need to make sure we add the user to the group cache
        try {
            xwiki.getGroupService(context).addUserToGroup(username, context.getDatabase(), groupName, context);
        } catch (Exception e) {}
        */
    }

    private void removeUserFromGroup(String userName, String groupName, XWikiContext context)
            throws XWikiException
    {
        // don't remove if he's not a member
        if (!isMemberOfGroup(userName, groupName, context)) {
            return;
        }

        XWiki xwiki = context.getWiki();
        BaseClass groupClass = xwiki.getGroupClass(context);
        XWikiDocument groupDoc = xwiki.getDocument(groupName, context);
        BaseObject memberObject = groupDoc.getObject(groupClass.getName(), "member", userName);
        if (memberObject == null) {
            return;
        }
        groupDoc.removeObject(memberObject);
        xwiki.saveDocument(groupDoc, context.getMessageTool()
                .get("core.comment.removedUserFromGroup"), context);
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

    public SpaceUserProfile getSpaceUserProfile(String spaceName, String username, XWikiContext context) throws SpaceManagerException {
        return newUserSpaceProfile(username, spaceName, context);
    }

    private void deleteSpaceUserProfile(String spaceName, String userName, XWikiContext context)
            throws SpaceManagerException
    {
        try {
            String docName = getSpaceUserProfilePageName(userName, spaceName);
            XWikiDocument doc = context.getWiki().getDocument(docName, context);
            context.getWiki().deleteDocument(doc, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
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

    public boolean joinSpace(String spaceName, XWikiContext context) throws SpaceManagerException {
        try {
            SpaceUserProfile userProfile = newUserSpaceProfile(context.getUser(), spaceName, context);
            userProfile.updateProfileFromRequest();
            userProfile.saveWithProgrammingRights();
            addMember(spaceName, context.getUser(), context);
            return true;
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }


    }

    private void sendMail(String action, Space space, XWikiContext context)
            throws SpaceManagerException
    {
        if (!mailNotification) {
            return;
        }

        VelocityContext vContext = new VelocityContext();
        vContext.put("space", space);
        String fromUser = context.getWiki().getXWikiPreference("space_email", context);
        if (fromUser == null || fromUser.trim().length() == 0) {
            fromUser = context.getWiki().getXWikiPreference("admin_email", context);
        }
        String[] toUsers = new String[0];
        if (SpaceAction.CREATE.equals(action)) {
            // notify space administrators upon space creation
            Collection admins = getAdmins(space.getSpaceName(), context);
            toUsers = (String[]) admins.toArray(new String[admins.size()]);
        }

        if (fromUser == null) {
            throw new SpaceManagerException(SpaceManagerException.MODULE_PLUGIN_SPACEMANAGER,
                    SpaceManagerException.ERROR_SPACE_SENDER_EMAIL_INVALID,
                    "Sender email is invalid");
        }

        boolean toUsersValid = toUsers.length > 0;
        for (int i = 0; i < toUsers.length && toUsersValid; i++) {
            if (!isEmailAddress(toUsers[i])) {
                toUsers[i] = getEmailAddress(toUsers[i], context);
            }
            if (toUsers[i] == null) {
                toUsersValid = false;
            }
        }

        if (!toUsersValid) {
            throw new SpaceManagerException(SpaceManagerException.MODULE_PLUGIN_SPACEMANAGER,
                    SpaceManagerException.ERROR_SPACE_TARGET_EMAIL_INVALID,
                    "Target email is invalid");
        }
        String strToUsers = join(toUsers, ",");

        MailSenderPlugin mailSender = getMailSenderPlugin(context);

        try {
            String templateDocFullName =
                    getTemplateMailPageName(space.getSpaceName(), action, context);
            XWikiDocument mailDoc = context.getWiki().getDocument(templateDocFullName, context);
            XWikiDocument translatedMailDoc = mailDoc.getTranslatedDocument(context);
            mailSender.prepareVelocityContext(fromUser, strToUsers, "", vContext, context);
            vContext.put("xwiki", new com.xpn.xwiki.api.XWiki(context.getWiki(), context));
            String mailSubject =
                    XWikiVelocityRenderer.evaluate(translatedMailDoc.getTitle(), templateDocFullName,
                            vContext);
            String mailContent =
                    XWikiVelocityRenderer.evaluate(translatedMailDoc.getContent(),
                            templateDocFullName, vContext);

            Mail mail =
                    new Mail(fromUser, strToUsers, null, null, mailSubject, mailContent, null);
            mailSender.sendMail(mail, context);
        } catch (Exception e) {
            throw new SpaceManagerException(SpaceManagerException.MODULE_PLUGIN_SPACEMANAGER,
                    SpaceManagerException.ERROR_SPACE_SENDING_EMAIL_FAILED,
                    "Sending notification email failed",
                    e);
        }
    }

    private MailSenderPlugin getMailSenderPlugin(XWikiContext context)
            throws SpaceManagerException
    {
        MailSenderPlugin mailSender =
                (MailSenderPlugin) context.getWiki().getPlugin("mailsender", context);

        if (mailSender == null)
            throw new SpaceManagerException(SpaceManagerException.MODULE_PLUGIN_SPACEMANAGER,
                    SpaceManagerException.ERROR_SPACE_MANAGER_REQUIRES_MAILSENDER_PLUGIN,
                    "SpaceManager requires the mail sender plugin");

        return mailSender;
    }

    // code duplicated from InvitationManagerImpl !!!
    private static final String join(String[] array, String separator)
    {
        StringBuffer result = new StringBuffer("");
        if (array.length > 0) {
            result.append(array[0]);
        }
        for (int i = 1; i < array.length; i++) {
            result.append("," + array[i]);
        }
        return result.toString();
    }

    // code duplicated from InvitationManagerImpl !!!
    private boolean isEmailAddress(String str)
    {
        return str.contains("@");
    }

    // code duplicated from InvitationManagerImpl !!!
    private String getEmailAddress(String user, XWikiContext context)
            throws SpaceManagerException
    {
        try {
            String wikiuser = (user.startsWith("XWiki.")) ? user : "XWiki." + user;

            if (wikiuser == null)
                return null;

            XWikiDocument userDoc = null;
            userDoc = context.getWiki().getDocument(wikiuser, context);

            if (userDoc.isNew())
                return null;

            String email = "";
            try {
                email = userDoc.getObject("XWiki.XWikiUsers").getStringValue("email");
            } catch (Exception e) {
                return null;
            }
            if ((email == null) || (email.equals("")))
                return null;

            return email;
        } catch (Exception e) {
            throw new SpaceManagerException(SpaceManagerException.MODULE_PLUGIN_SPACEMANAGER,
                    SpaceManagerException.ERROR_SPACE_CANNOT_FIND_EMAIL_ADDRESS,
                    "Cannot find email address of user " + user,
                    e);
        }
    }

    private String getTemplateMailPageName(String spaceName, String action, XWikiContext context)
    {
        String docName = spaceName + "." + "MailTemplate" + action + "Space";
        try {
            if (context.getWiki().getDocument(docName, context).isNew()) {
                docName = null;
            }
        } catch (XWikiException e) {
            docName = null;
        }
        if (docName == null) {
            docName = getDefaultResourceSpace(context) + "." + "MailTemplate" + action + "Space";
        }

        return docName;
    }

    private String getDefaultResourceSpace(XWikiContext context)
    {
        return context.getWiki().Param("xwiki.spacemanager.resourcespace",
                SpaceManager.DEFAULT_RESOURCE_SPACE);
    }

    public boolean isMailNotification()
    {
        return mailNotification;
    }

    public void setMailNotification(boolean mailNotification)
    {
        this.mailNotification = mailNotification;
    }
}
