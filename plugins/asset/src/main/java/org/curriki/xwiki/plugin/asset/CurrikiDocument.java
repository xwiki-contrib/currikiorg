package org.curriki.xwiki.plugin.asset;

import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.PropertyInterface;
import com.xpn.xwiki.objects.BaseElement;
import com.xpn.xwiki.doc.XWikiDocument;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CurrikiDocument extends Document {
    public CurrikiDocument(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    protected void assertCanEdit() throws XWikiException {
        if (!Constants.GUEST_USER.equals(context.getUser()) && isNew() && Constants.ASSET_TEMPORARY_SPACE.equals(getSpace())) {
            // Allow access to temporary documents (so that they can be created in the ASSET_TEMPORARY_SPACE)
            return;
        }
        if (!hasAccessLevel("edit")) {
            throw new XWikiException(XWikiException.MODULE_XWIKI_ACCESS, XWikiException.ERROR_XWIKI_ACCESS_DENIED, "User needs appropriate rights");
        }
    }

    protected boolean copyProperty(BaseObject fromObj, BaseObject destObj, String key) throws XWikiException {
        PropertyInterface prop = fromObj.get(key);

        if (prop != null) {
            PropertyInterface newProp = (PropertyInterface) ((BaseElement)prop).clone();
            newProp.setObject(destObj);
            destObj.safeput(key, newProp);
            return true;
        }

        return false;
    }

    public Boolean hasA(String objectClass) {
        if (doc.getObjectNumbers(objectClass) == 0) {
            // No objects
        } else {
            // Work around a bug XWIKI-1624
            // TODO: Remove the work-around once XWIKI-1624 is fixed
            List objList = doc.getObjects(objectClass);
            for (Object obj : objList) {
                if (obj != null) {
                    return true;
                }
            }
        }

        return false;
    }

    public Integer countOfObjects(String objectClass) {
        int count = 0;
        if (doc.getObjectNumbers(objectClass) == 0) {
            // Nothing to count
        } else {
            // Work around a bug XWIKI-1624
            // TODO: Remove the work-around once XWIKI-1624 is fixed
            List objList = doc.getObjects(objectClass);
            for (Object obj : objList) {
                if (obj != null) {
                    count++;
                }
            }
        }

        return count;
    }

    public Map<String,Boolean> getRightsList() {
        Map<String,Boolean> rightsInfo = new HashMap<String, Boolean>();

        rightsInfo.put("view", hasAccessLevel("view"));
        rightsInfo.put("edit", hasAccessLevel("edit"));
        rightsInfo.put("delete", hasAccessLevel("delete"));

        return rightsInfo;
    }
    
    public String formatDate(Date date,String pattern)
    {
    	if (date!=null && date instanceof Date)
			try {
				return (new SimpleDateFormat(pattern)).format(date);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	return ""+date;
    }
}
