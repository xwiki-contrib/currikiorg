package org.gelc.xwiki.plugins.framework;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Document;

public class FrameworkItem extends Document implements FrameworkConstant{


    public FrameworkItem(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    public String getIdentifier(XWikiContext context) {
        use(getObject(FRAMEWORK_ITEM_CLASS_FULLNAME, true));
        return (String) get(CLASS_ITEM_IDENTIFIER);
    }

    public void setIdentifier(String text) {
        use(getObject(FRAMEWORK_ITEM_CLASS_FULLNAME, true));
        set(CLASS_ITEM_IDENTIFIER, text);
    }

    public String getParentIdentifier(XWikiContext context) {
        use(getObject(FRAMEWORK_ITEM_CLASS_FULLNAME, true));
        return (String) get(CLASS_ITEM_PARENT_IDENTIFIER);
    }

    public void setParentIdentifier(String text) {
        use(getObject(FRAMEWORK_ITEM_CLASS_FULLNAME, true));
        set(CLASS_ITEM_PARENT_IDENTIFIER, text);
    }


}
