package org.curriki.plugin.spacemanager.impl;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.plugin.spacemanager.api.Space;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManager;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceUserProfile;
import com.xpn.xwiki.plugin.spacemanager.impl.SpaceManagerExtensionImpl;

public class CurrikiSpaceManagerExtension extends SpaceManagerExtensionImpl {

	public static final String CURRIKI_SPACE_TYPE = "currikispace";
	public static final String CURRIKI_SPACE_CLASS_NAME = "XWiki.CurrikiSpaceClass";
	
	public static final String LEADERS_GROUP_NAME = "Group_CurrikiLeadersGroup";
	
	public String getSpaceTypeName() {
        return CURRIKI_SPACE_TYPE;
    }
	
	public String getCurrikiSpaceClassName() {
        return CURRIKI_SPACE_CLASS_NAME;
    }

	 /**
     * 
     * @param context Xwiki context
     * @return Returns the Space Class as defined by the extension
     * @throws XWikiException
     */
     protected BaseClass getCurrikiSpaceClass(XWikiContext context) throws XWikiException {
    	XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;
        
        try {
            doc = xwiki.getDocument(getCurrikiSpaceClassName(), context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setFullName(getCurrikiSpaceClassName());
            needsUpdate = true;
        }
        
        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(getCurrikiSpaceClassName());
        

        String content = doc.getContent();
        if ((content == null) || (content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 XWikiSpaceClass");
        }

        needsUpdate |= bclass.addDBListField(CurrikiSpace.SPACE_LICENCE, "Default licence policy",1, false,"select obj.name, prop.value from BaseObject as obj, StringProperty as prop, IntegerProperty as oprop where  obj.className='XWiki.LicenceClass' and prop.id.id = obj.id  and prop.id.name = 'name' and oprop.id.id = obj.id and oprop.id.name = 'order' order by oprop.value");
        needsUpdate |= bclass.addStaticListField(CurrikiSpace.SPACE_ACCESSLEVEL, "Default access privileges", 1, false, "open=Open|protected=Protected|private=Private", "radio");
        needsUpdate |= bclass.addStaticListField(CurrikiSpace.SPACE_EDUCATION_LEVEL, "Educational level", 12, true, "choose=Choose from list...|preschool=Preschool|earlyelementary=Early Elementary|upperelementary=Upper Elementary|middleschool=Middle School|highschool=High School|highered=Higher Ed|professional=Professional|na=NA");
        needsUpdate |= bclass.addDBTreeListField(CurrikiSpace.SPACE_TOPIC, "Topic", 12, true, "select doc.fullName, doc.title, doc.parent from XWikiDocument as doc, BaseObject as obj where doc.web in ('FW_masterFramework') and doc.fullName=obj.name and obj.className='XWiki.FrameworkItemClass' order by doc.title");
        needsUpdate |= bclass.addTextField(CurrikiSpace.SPACE_LOGO, "Logo Filename", 32);

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
        return bclass;
    }

	
	public boolean addSpecificFields(BaseClass bclass) {
		return false;
	}
	
	public void init(SpaceManager _sm, XWikiContext context) throws SpaceManagerException {
		try {
			getCurrikiSpaceClass(context);
			this.sm = _sm;
		} catch (XWikiException e) {
			throw new SpaceManagerException(e);
		}	
	}

	public void virtualInit(SpaceManager _sm, XWikiContext context) throws SpaceManagerException {
		try {
			getCurrikiSpaceClass(context);
			this.sm = _sm;
		} catch (XWikiException e) {
			throw new SpaceManagerException(e);
		}
	}
	
	public void postCreateSpace(String spaceName, XWikiContext context) throws SpaceManagerException{
		try{
			Space s = sm.getSpace(spaceName, context);
            // we want to set default settings of admin
            SpaceUserProfile adminUserProfile = sm.getSpaceUserProfile(spaceName, s.getCreator(), context);
            adminUserProfile.setAllowNotifications(true);
            adminUserProfile.setAllowNotificationsFromSelf(true);
            adminUserProfile.saveWithProgrammingRights();

            // Adding user to leaders group
            sm.addMember( LEADERS_GROUP_NAME, s.getCreator(), context);
            // we want to set default settings of leadersgroup
            SpaceUserProfile leadersGroupUserProfile = sm.getSpaceUserProfile(LEADERS_GROUP_NAME, s.getCreator(), context);
            leadersGroupUserProfile.setAllowNotifications(true);
            leadersGroupUserProfile.setAllowNotificationsFromSelf(true);
            leadersGroupUserProfile.saveWithProgrammingRights();
        }catch(XWikiException e){
			throw new SpaceManagerException(e);
		}
	}

    /**
     * Gets the full page name of the role
     *
     * @param spaceName The space name that the role is in
     * @param role The page name of the role we want to use
     * @return The full page name of the role
     *
     */
    public String getRoleGroupName(String spaceName, String role) {
        if (getAdminGroupName(spaceName).equals(role) || getMemberGroupName(spaceName).equals(role)){
            return role;
        } else if (getAdminGroupName(spaceName).equals(spaceName+"."+role)){
            return getAdminGroupName(spaceName);
        } else if (getMemberGroupName(spaceName).equals(spaceName+"."+role)){
            return getMemberGroupName(spaceName);
        }

        return (role.startsWith(spaceName+".")?role:spaceName+"."+role);
    }
}
