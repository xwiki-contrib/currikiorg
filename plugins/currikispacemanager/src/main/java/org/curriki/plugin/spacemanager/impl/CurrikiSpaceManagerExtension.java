package org.curriki.plugin.spacemanager.impl;

import org.xwiki.plugin.spacemanager.api.SpaceManagerException;
import org.xwiki.plugin.spacemanager.impl.SpaceManagerExtensionImpl;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;

public class CurrikiSpaceManagerExtension extends SpaceManagerExtensionImpl {

	public static final String CURRIKI_SPACE_TYPE = "currikispace";
	public static final String CURRIKI_SPACE_CLASS_NAME = "XWiki.CurrikiSpaceClass";
	
	public CurrikiSpaceManagerExtension() {
		// TODO Auto-generated constructor stub
	}
	
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

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
        return bclass;
    }

	
	public boolean addSpecificFields(BaseClass bclass) {
		return false;
	}
	
	public void init(XWikiContext context) throws SpaceManagerException {
		try {
			getCurrikiSpaceClass(context);
		} catch (XWikiException e) {
			throw new SpaceManagerException(e);
		}	
	}

	public void virtualInit(XWikiContext context) throws SpaceManagerException {
		try {
			getCurrikiSpaceClass(context);
		} catch (XWikiException e) {
			throw new SpaceManagerException(e);
		}
	}
}
