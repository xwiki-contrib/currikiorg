package org.curriki.plugin.activitystream.impl;

import org.xwiki.plugin.activitystream.impl.ActivityStreamImpl;
import org.xwiki.plugin.activitystream.api.ActivityEventType;
import com.xpn.xwiki.notify.XWikiNotificationRule;
import com.xpn.xwiki.notify.XWikiDocChangeNotificationInterface;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;

import java.util.ArrayList;


public class CurrikiActivityStream extends ActivityStreamImpl {
    private String CURRIKI_SPACE_TYPE = "currikispace";
    private String SPACE_CLASS_NAME = "XWiki.SpaceClass";

    public CurrikiActivityStream()
    {
        super();
    }

    public void notify(XWikiNotificationRule rule, XWikiDocument newdoc, XWikiDocument olddoc, int event, XWikiContext context) {
        try {
        ArrayList params = new ArrayList();
        params.add(newdoc.getDisplayTitle(context));

        String streamName = getStreamName(newdoc.getSpace(), context);
        if (streamName==null)
         return;

            switch (event) {
                case XWikiDocChangeNotificationInterface.EVENT_CHANGE:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.UPDATE, "as_document_has_been_updated", params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_NEW:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.CREATE, "as_document_has_been_created", params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_DELETE:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.DELETE, "as_document_has_been_deleted", params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_UPDATE_CONTENT:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.UPDATE, "as_document_has_been_updated", params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_UPDATE_OBJECT:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.UPDATE, "as_document_has_been_updated", params, context);
                    break;
                case XWikiDocChangeNotificationInterface.EVENT_UPDATE_CLASS:
                    addDocumentActivityEvent(streamName, newdoc, ActivityEventType.UPDATE, "as_document_has_been_updated", params, context);
                    break;
            }
        } catch (Throwable e) {
            // Error in activity stream notify should be ignored but logged in the log file
            e.printStackTrace();
        }
    }

    public String getStreamName(String space, XWikiContext context) {
        XWikiDocument doc;
        try {
            doc = context.getWiki().getDocument(space, "WebPreferences", context);
            String type = doc.getStringValue(SPACE_CLASS_NAME, "type");

            if (CURRIKI_SPACE_TYPE.equals(type))
                return space;

            String parentSpace = space.substring(space.indexOf("_") + 1);
            doc = context.getWiki().getDocument(parentSpace, "WebPreferences", context);
            type = doc.getStringValue(SPACE_CLASS_NAME, "type");
            if (CURRIKI_SPACE_TYPE.equals(type))
                return parentSpace;

            // could not find a curriki space
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
