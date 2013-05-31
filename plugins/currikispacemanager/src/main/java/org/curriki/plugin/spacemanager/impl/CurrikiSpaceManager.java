package org.curriki.plugin.spacemanager.impl;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import com.xpn.xwiki.plugin.spacemanager.api.Space;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerExtension;
import com.xpn.xwiki.plugin.spacemanager.impl.SpaceManagerImpl;
import org.curriki.plugin.spacemanager.plugin.CurrikiSpaceManagerPluginApi;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 14 dec. 2007
 * Time: 09:23:04
 * To change this template use File | Settings | File Templates.
 */
public class CurrikiSpaceManager extends SpaceManagerImpl {
    public static final String CURRIKI_SPACEMANGER_NAME = "csm";
    private static final String CURRIKI_SPACEMANAGER_DEFAULT_EXTENSION = "org.xwiki.plugin.spacemanager.impl.CurrikiSpaceManagerExtension";
    private static final String CURRIKI_SPACEMANAGER_DEFAULT_PROTECTED_SUBSPACES = "UserProfiles,Messages,Documentation,Discussions";

    /**
	 * Space manager constructor
	 * @param name
	 * @param className
	 * @param context
	 */
    public CurrikiSpaceManager(String name, String className, XWikiContext context)
    {
        super(name, className, context);
    }

    public String getName() {
        return CURRIKI_SPACEMANGER_NAME;
    }


    /**
	* Loads the CurrikiSpaceManagerExtension specified in the config file
	* @return Returns the space manager extension
	 * @throws SpaceManagerException
	*/
	public SpaceManagerExtension getSpaceManagerExtension(XWikiContext context) throws SpaceManagerException
	{
        if (spaceManagerExtension==null) {
            String extensionName = context.getWiki().Param(SPACEMANAGER_EXTENSION_CFG_PROP,CURRIKI_SPACEMANAGER_DEFAULT_EXTENSION);
            
            try {
                if (extensionName!=null){
                	spaceManagerExtension = (CurrikiSpaceManagerExtension)Class.forName(extensionName).newInstance();
                }
            } catch (Throwable e){
                try{
                	spaceManagerExtension = (CurrikiSpaceManagerExtension)Class.forName(CURRIKI_SPACEMANAGER_DEFAULT_EXTENSION).newInstance();
                } catch(Throwable  e2){
                	
                }
            }
        }

        if (spaceManagerExtension==null) {
            spaceManagerExtension = new CurrikiSpaceManagerExtension( );
        }

        return spaceManagerExtension;
    }

    /**
     * Get the list of sub spaces to protect
     * @param context
     * @return
     */
    public String[] getProtectedSubSpaces(XWikiContext context) {
        String protectedSubSpaces = context.getWiki().Param(SPACEMANAGER_PROTECTED_SUBSPACES_PROP, CURRIKI_SPACEMANAGER_DEFAULT_PROTECTED_SUBSPACES);
        if ((protectedSubSpaces!=null)&&(!protectedSubSpaces.equals(""))) {
            return protectedSubSpaces.split(",");
        } else {
            return new String[0];
        }
    }

    /**
     * Gets the space plugin Api
     * @param plugin The plugin interface
     * @param context Xwiki context
     * @return
     */
    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
        return new CurrikiSpaceManagerPluginApi((CurrikiSpaceManager) plugin, context);
    }


    protected Space newSpace(String spaceName, String spaceTitle, boolean create, XWikiContext context) throws SpaceManagerException {
        return new CurrikiSpace(spaceName, spaceTitle, create, this, context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected String getCurrikiSpaceClassName() {
        return ((CurrikiSpaceManagerExtension) getSpaceManagerExtension()).getCurrikiSpaceClassName();
    }

    public List getSpacesByTopic(String topic, int nb, int start, XWikiContext context) throws SpaceManagerException {
        String currikiClassName = getCurrikiSpaceClassName();
        String fromhql = ", BaseObject as cobj, DBStringListProperty as lprop";
        String wheresql = " and doc.fullName=cobj.name and cobj.className='" + currikiClassName
                        + "' and cobj.id=lprop.id.id and lprop.id.name='" + CurrikiSpace.SPACE_TOPIC + "' and '" + topic + "' in elements(lprop.list)" ;
        return searchSpaces(fromhql, wheresql, "order by doc.creationDate desc", nb, start, context);
    }

    public List getSpaceNamesByTopic(String topic, int nb, int start, XWikiContext context) throws SpaceManagerException {
        String currikiClassName = getCurrikiSpaceClassName();
        String fromhql = ", BaseObject as cobj, DBStringListProperty as lprop";
        String wheresql = " and doc.fullName=cobj.name and cobj.className='" + currikiClassName
                        + "' and cobj.id=lprop.id.id and lprop.id.name='" + CurrikiSpace.SPACE_TOPIC + "' and '" + topic + "' in elements(lprop.list)" ;
        return searchSpaceNames(fromhql, wheresql, "order by doc.creationDate desc", nb, start, context);
    }

    public List countSpacesByTopic(String parentTopic, XWikiContext context) throws SpaceManagerException {
        String type = getSpaceTypeName();
        String className = getSpaceClassName();
        String currikiClassName = getCurrikiSpaceClassName();
        String sql;
        String parentfromsql = (parentTopic==null) ? "" : ", XWikiDocument as doc2";
        String parentsql = (parentTopic==null) ? "" : " and doc2.fullName = topic.id and doc2.parent='" + parentTopic + "'";
        if (hasCustomMapping())
            sql = "select topic.id, count(*) from XWikiDocument as doc, BaseObject as obj, " + className + " as space, BaseObject as cobj, DBStringListProperty as lprop join lprop.list as topic" + parentfromsql
                 + " where doc.fullName = obj.name and obj.className='" + className + "' and obj.id = space.id and space.type='" + type + "'"
                 + " and doc.fullName=cobj.name and cobj.className='" + currikiClassName
                 + "' and cobj.id=lprop.id.id and lprop.id.name='" + CurrikiSpace.SPACE_TOPIC + "'" + parentsql + " group by 1" ;
        else
            sql = "select topic.id, count(*) from XWikiDocument as doc, BaseObject as obj, StringProperty as typeprop, BaseObject as cobj, DBStringListProperty as lprop join lprop.list as topic" + parentfromsql
                 + " where doc.fullName=obj.name and obj.className = '" + className + "' and obj.id=typeprop.id.id and typeprop.id.name='type' and typeprop.value='" + type + "'"
                    + " and doc.fullName=cobj.name and cobj.className='" + currikiClassName
                    + "' and cobj.id=lprop.id.id and lprop.id.name='" + CurrikiSpace.SPACE_TOPIC + "'" + parentsql + " group by 1" ;
                      
        try {
            return context.getWiki().search(sql, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection getRoles(String spaceName, XWikiContext context) throws SpaceManagerException {
        List parameterValues = new ArrayList();
        String where = "where doc.web = ? and doc.name like ? order by doc.title";
        parameterValues.add(spaceName);
        parameterValues.add("Role_%Group");

        List roles;
        try {
            roles = context.getWiki().getStore().searchDocumentsNames(where, 0, 0, parameterValues, context);
        } catch (XWikiException e) {
            throw new SpaceManagerException(e);
        }

        return roles;
    }

    /**
     * Gives a group certain rights over a space
     * This function is overridden from standard space manager
     * which currently has a bug that needs to be fixed
     *
     * @param spaceName Name of the space
     * @param groupName Name of the group that will have the value
     * @param level Access level
     * @param allow True if the right is allow, deny if not
     */
    protected boolean addRightToGroup(String spaceName, String groupName, String level,
        boolean allow, boolean global, XWikiContext context) throws XWikiException
    {
        final String rightsClass = global ? "XWiki.XWikiGlobalRights" : "XWiki.XWikiRights";
        final String prefDocName = spaceName + ".WebPreferences";
        final String groupsField = "groups";
        final String levelsField = "levels";
        final String allowField = "allow";

        XWikiDocument prefDoc;
        prefDoc = context.getWiki().getDocument(prefDocName, context);

        // checks to see if the right is not already given
        boolean exists = false;
        boolean isUpdated = false;
        int indx = -1;
        boolean foundlevel = false;
        int allowInt;
        if (allow)
            allowInt = 1;
        else
            allowInt = 0;
        List objs = prefDoc.getObjects(rightsClass);
        if (objs != null) {
            for (int i = 0; i < objs.size(); i++) {
                BaseObject bobj = (BaseObject) objs.get(i);
                if (bobj == null)
                    continue;
                String groups = bobj.getLargeStringValue(groupsField);
                String levels = bobj.getStringValue(levelsField);
                int allowDeny = bobj.getIntValue(allowField);
                boolean allowdeny = (bobj.getIntValue(allowField) == 1);
                String[] levelsarray = StringUtils.split(levels, " ,|");
                String[] groupsarray = StringUtils.split(groups, " ,|");
                if (ArrayUtils.contains(groupsarray, groupName)) {
                    exists = true;
                    if (!foundlevel)
                        indx = i;
                    if (ArrayUtils.contains(levelsarray, level)) {
                        foundlevel = true;
                        if (allowInt == allowDeny) {
                            isUpdated = true;
                            break;
                        }
                    }
                }
            }
        }

        // sets the rights. the aproach is to break rules/levels in as many
        // XWikiRigts elements so
        // we don't have to handle lots of situation when we change rights
        if (!exists) {
            BaseObject bobj = new BaseObject();
            bobj.setClassName(rightsClass);
            bobj.setName(prefDoc.getFullName());
            bobj.setLargeStringValue(groupsField, groupName);
            bobj.setStringValue(levelsField, level);
            bobj.setIntValue(allowField, allowInt);
            prefDoc.addObject(rightsClass, bobj);
            context.getWiki().saveDocument(prefDoc, context);
            return true;
        } else {
            if (isUpdated) {
                return true;
            } else {
                BaseObject bobj = (BaseObject) objs.get(indx);
                String groups = bobj.getLargeStringValue(groupsField);
                String levels = bobj.getStringValue(levelsField);
                String[] levelsarray = StringUtils.split(levels, " ,|");
                String[] groupsarray = StringUtils.split(groups, " ,|");

                if (levelsarray.length == 1 && groupsarray.length == 1 && levelsarray[0] == level) {
                    // if there is only this group and this level in the rule
                    // update this rule
                } else {
                    // if there are more groups/levels, extract this one(s)
                    bobj = new BaseObject();
                    bobj.setName(prefDoc.getFullName());
                    bobj.setClassName(rightsClass);
                    bobj.setStringValue(levelsField, level);
                    bobj.setIntValue(allowField, allowInt);
                    bobj.setLargeStringValue(groupsField, groupName);
                }

                prefDoc.addObject(rightsClass, bobj);
                context.getWiki().saveDocument(prefDoc, context);
                return true;
            }
        }
    }

    /**
     * Gives a group certain rights over a space
     * This function is overridden from standard space manager
     * which currently has a bug that needs to be fixed
     *
     * @param spaceName Name of the space
     * @param groupName Name of the group that will have the value
     * @param level Access level
     * @param allow True if the right is allow, deny if not
     */
    protected boolean removeRightFromGroup(String spaceName, String groupName, String level,
        boolean allow, boolean global, XWikiContext context) throws XWikiException
    {
        final String rightsClass = global ? "XWiki.XWikiGlobalRights" : "XWiki.XWikiRights";
        final String prefDocName = spaceName + ".WebPreferences";
        final String groupsField = "groups";
        final String levelsField = "levels";
        final String allowField = "allow";

        XWikiDocument prefDoc;
        prefDoc = context.getWiki().getDocument(prefDocName, context);

        boolean foundlevel = false;
        int allowInt;
        if (allow)
            allowInt = 1;
        else
            allowInt = 0;
        List objs = prefDoc.getObjects(rightsClass);
        if (objs != null) {
            for (int i = 0; i < objs.size(); i++) {
                BaseObject bobj = (BaseObject) objs.get(i);
                if (bobj == null)
                    continue;
                String groups = bobj.getLargeStringValue(groupsField);
                String levels = bobj.getStringValue(levelsField);
                int allowDeny = bobj.getIntValue(allowField);
                boolean allowdeny = (bobj.getIntValue(allowField) == 1);
                String[] levelsarray = StringUtils.split(levels, " ,|");
                String[] groupsarray = StringUtils.split(groups, " ,|");
                if (ArrayUtils.contains(groupsarray, groupName)) {
                        if (ArrayUtils.contains(levelsarray, level)) {
                            foundlevel = true;
                            if (allowInt == allowDeny) {
                                prefDoc.removeObject(bobj);
                            }
                        }
                }
            }
        }

        if (foundlevel) {
            context.getWiki().saveDocument(prefDoc, context);
            return true;
        }

        return false;
    }

}
