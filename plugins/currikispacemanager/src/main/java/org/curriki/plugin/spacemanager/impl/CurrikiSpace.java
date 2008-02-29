package org.curriki.plugin.spacemanager.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.plugin.spacemanager.api.SpaceManagerException;
import com.xpn.xwiki.plugin.spacemanager.impl.SpaceImpl;
import com.xpn.xwiki.plugin.spacemanager.impl.SpaceManagerImpl;

public class CurrikiSpace extends SpaceImpl {
	
	public static final String SPACE_LICENCE = "licence";
	public static final String SPACE_ACCESSLEVEL = "accessprivileges";
	public static final String SPACE_EDUCATION_LEVEL = "educationLevel";
	public static final String SPACE_TOPIC = "topic";
    public static final String SPACE_LOGO = "logo";
    
    public static final String VALIDATION_TITLE_SHORT = "title-short";
    public static final String VALIDATION_TITLE_LONG = "title-long";
    public static final String VALIDATION_TITLE_INVALID = "title-invalid";
    public static final String VALIDATION_DESC_SHORT = "desc-short";
    public static final String VALIDATION_DESC_LONG = "desc-long";
    public static final String VALIDATION_SPACE_EXISTS = "space-exists";
    public static final String VALIDATION_URL_SHORT = "url-short";
    public static final String VALIDATION_URL_LONG = "url-long";
    public static final String VALIDATION_URL_EXISTS = "url-exists";
    public static final String VALIDATION_URL_INVALID = "url-invalid";
    public static final String VALIDATION_EDUCATION_REQUIRED = "education-required";
    public static final String VALIDATION_TYPE_REQUIRED = "type-required";
    public static final String VALIDATION_SUBJECT_REQUIRED = "subject-required";
    public static final String VALIDATION_LICENCE_REQUIRED = "licence-required";
    public static final String VALIDATION_PRIVACY_REQUIRED = "privacy-required";
    public static final String VALIDATION_TITLE_EXISTS = "title-exists";

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
	
	public boolean validateSpaceData() throws SpaceManagerException{
		boolean success = true;
		Map errors = new HashMap();
	
		try {                                                                                                                                           
            success &= doc.validate(context);
            
            //title
            String title = this.getDisplayTitle();
            if(title.length() < 1)	
            	errors.put( this.VALIDATION_TITLE_SHORT, "1" );
           
            if(title.length() > 64)
             	errors.put( this.VALIDATION_TITLE_LONG, "1" );
				
			//valid characters in title
            /*Pattern pt = Pattern.compile("([ a-zA-Z0-9\\-\\_\\+\\,\\.\\?\\!\\@\\$\\&\\*\\(\\)]*)");
			Matcher mt = pt.matcher(title);
            if( !mt.matches() )
            	errors.put( this.VALIDATION_TITLE_INVALID, "1" );*/

            //existance of a group with this name
            // this won't work because we manage space name uniqueness
            // if(!this.isNew())
            //	errors.put( this.VALIDATION_SPACE_EXISTS, "1" );
            // we need to test for the title itself

            //same title
            List list = context.getWiki().getStore().searchDocumentsNames(",BaseObject as obj, StringProperty as tprop where doc.fullName=obj.name and obj.className='"
                                    + manager.getSpaceClassName() + "' and obj.id=tprop.id.id and tprop.id.name='"
                                    + SPACE_DISPLAYTITLE + "' and tprop.value='" + this.getDisplayTitle().replaceAll("'","''") + "' and doc.web<>'" + getSpaceName() + "'", context);
            if(list!=null && list.size()>0)
                        errors.put( this.VALIDATION_TITLE_EXISTS, "1" );


            String url = this.getHomeShortcutURL();
            if(url.length() < 1)
                errors.put( this.VALIDATION_URL_SHORT, "1" );
            if(url.length() > 32)
                errors.put( this.VALIDATION_URL_LONG, "1" );

            //same shortcut url
            if (url.length()>0) {
                list = context.getWiki().getStore().searchDocumentsNames(",BaseObject as obj, StringProperty as urlprop where doc.fullName=obj.name and obj.className='"
                        + manager.getSpaceClassName() + "' and obj.id=urlprop.id.id and urlprop.id.name='"
                        + SPACE_URLSHORTCUT + "' and urlprop.value='" + this.getHomeShortcutURL().replaceAll("'","''") + "' and doc.web<>'" + getSpaceName() + "'", context);
                if(list!=null && list.size()>0)
                    errors.put( this.VALIDATION_URL_EXISTS, "1" );
            }
            
            //valid characters in url
            Pattern p = Pattern.compile("([a-zA-Z])([a-zA-Z0-9\\-]*)");
            Matcher m = p.matcher(url);
            if( !m.matches() )
            	errors.put( this.VALIDATION_URL_INVALID, "1" );
            

            //description is set
            String desc = this.getDescription();
            if(desc.length() < 5 )
            	errors.put( this.VALIDATION_DESC_SHORT, "1" );
            if(desc.length() > 960 )
            	errors.put( this.VALIDATION_DESC_LONG, "1" );
            
            //categories is set
            List categories = this.doc.getListValue(CurrikiSpace.SPACE_EDUCATION_LEVEL);
            if(categories==null || categories.size()<1)
            	errors.put( this.VALIDATION_EDUCATION_REQUIRED, "1" );
            
            //subject is set
            List subjects = this.doc.getListValue(CurrikiSpace.SPACE_TOPIC);
            if(subjects==null || subjects.size()<1)
            	errors.put( this.VALIDATION_SUBJECT_REQUIRED, "1" );
            
                        	
            //licence is set
            String licence = this.doc.getStringValue(CurrikiSpace.SPACE_LICENCE);
            if(licence!=null && licence=="")
            	errors.put( this.VALIDATION_LICENCE_REQUIRED, "1" );
            
            //privacy level is set
            String privacy = this.doc.getStringValue(CurrikiSpace.SPACE_POLICY);
            if(privacy!=null && privacy=="")
            	errors.put( this.VALIDATION_PRIVACY_REQUIRED, "1" );
            
            if(errors.size()>0)
            {
            	context.put("validation", errors);
            	success &= false;
            }
            
        } catch (XWikiException e) {
        	success &= false;
            throw new SpaceManagerException(e);
        }
        
        return success;
	}
}