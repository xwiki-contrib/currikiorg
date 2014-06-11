package org.curriki.cloud;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.xpn.xwiki.internal.event.AttachmentDeletedEvent;
import com.xpn.xwiki.internal.event.AttachmentUpdatedEvent;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.internal.event.XObjectAddedEvent;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.mailsender.MailSenderPluginApi;

@Component
@Named("CommentEventListener")
@Singleton
public class CloudComponent implements EventListener
{
    @Inject
    private Logger logger;

    private EntityReference commentClassReference = new EntityReference("XWikiComments", EntityType.DOCUMENT,
        new EntityReference("XWiki", EntityType.SPACE));

    public String getName()
    {
        return "CommentEventListener";
    }

    public List<Event> getEvents() {
        return Arrays.<Event>asList(
                new AttachmentUpdatedEvent(),
                new AttachmentDeletedEvent());
    }

    public void onEvent(Event event, Object source, Object data)
    {
        XWikiDocument document = (XWikiDocument) source;
        logger.warn("Eventreceived ");
        logger.warn("source: " + source + (source!=null ? " of class " : source.getClass()));
        logger.warn("event : " + event +  (event!=null ?  " of class " : event.getClass()));
        logger.warn("data  : " + data +   (data!=null ?   " of class " : data.getClass()));

        /* if(event instanceof AttachmentUpdatedEvent)
            ((AttachmentUpdatedEvent) event).ge
        //((AttachmentUpdatedEvent) event);
        BaseObject commentObject = document.getXObject(this.commentClassReference);
        if (commentObject != null) {
            try {
                // Get comment
                String comment = commentObject.getStringValue("comment");
                // Send email
                XWikiContext xcontext = (XWikiContext) data;
                MailSenderPluginApi mailSender = (MailSenderPluginApi) xcontext.getWiki().getPluginApi("mailsender", xcontext);
                mailSender.sendTextMessage("XWiki <xwiki@no-reply>", "john@doe.com",
                    "[XWiki] Comment added to " + document.toString(), comment);
            } catch (Exception e) {
                this.logger.error("Failure in comment listener", e);
            }

        } */
    }
}