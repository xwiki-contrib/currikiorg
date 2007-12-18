package org.curriki.plugin.spacemanager.impl;

import org.xwiki.plugin.spacemanager.api.SpaceManagerException;
import org.xwiki.plugin.spacemanager.impl.SpaceImpl;
import org.xwiki.plugin.spacemanager.impl.SpaceManagerImpl;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

public class CurrikiSpace extends SpaceImpl {
	
	public static final String SPACE_LICENCE = "licence";
	public static final String SPACE_ACCESSLEVEL = "accessprivileges";
	public static final String SPACE_EDUCATION_LEVEL = "educationLevel";
	public static final String SPACE_TOPIC = "topic";

	public CurrikiSpace(String spaceName, String spaceTitle, boolean create, SpaceManagerImpl manager, XWikiContext context)
			throws SpaceManagerException {
		super(spaceName, spaceTitle, create, manager, context);
		// TODO Auto-generated constructor stub
	}
	
	public void updateSpaceFromRequest() throws SpaceManagerException {
        super.updateSpaceFromRequest();
        try {
        	 XWikiDocument doc = getDoc();
        	 doc.updateObjectFromRequest(((CurrikiSpaceManagerExtension)manager.getSpaceManagerExtension()).getCurrikiSpaceClassName(), context);
        } catch (XWikiException e) {
        	throw new SpaceManagerException(e);
        }
	}
}