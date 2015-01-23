package org.curriki.cloud;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.internal.event.AttachmentAddedEvent;
import com.xpn.xwiki.internal.event.AttachmentDeletedEvent;
import com.xpn.xwiki.internal.event.AttachmentUpdatedEvent;
import org.xwiki.bridge.event.AbstractDocumentEvent;
import org.xwiki.bridge.event.DocumentUpdatedEvent;
import com.xpn.xwiki.web.Utils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.ObservationManager;
import org.xwiki.observation.event.Event;
import org.xwiki.observation.event.filter.FixedNameEventFilter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

@Singleton
@Component
public class AWSCloudFrontCdnWatcher implements EventListener, CdnWatcher, Initializable  {
    @Inject
    private Logger logger;

    //@Inject
    //ObservationManager observationManager;

    public String getName()
    {
        return "CdnWatcher";
    }

    public void initialize() throws InitializationException {
        //observationManager.addListener(this);
        logger.warn("AWSCloudFrontCdnWatcher initializing.");
    }

    public List<Event> getEvents() {
        return Arrays.<Event>asList(
                new AttachmentUpdatedEvent(), new AttachmentAddedEvent(),
                new AttachmentDeletedEvent(), new DocumentUpdatedEvent());
    }

    public void invalidateAttachment(String fullName, String attachmentName) {
        logger.warn("Would invalidate document: " + fullName);
    }

    public void onEvent(Event event, Object source, Object data)
    {
        XWikiDocument document = (XWikiDocument) source;
        XWikiContext context = (XWikiContext) data;
        logger.warn("Eventreceived ");
        logger.warn("source: " + source + (source != null ? " of class " + source.getClass() : ""));
        logger.warn("event : " + event + (event != null ? " of class " + event.getClass() : ""));
        logger.warn("data  : " + data + (data != null ? " of class " + data.getClass() : ""));

        /* if(event instanceof AbstractDocumentEvent) {
            System.out.println("Name: " + )
            AbstractDocumentEvent e = (AbstractDocumentEvent) event;
            if(e.getEventFilter() instanceof FixedNameEventFilter) {
                FixedNameEventFilter filter = (FixedNameEventFilter) (e.getEventFilter());
                filter.getFilter();
            }
        }*/

        String name = null;
        if(event instanceof AttachmentAddedEvent)
            name = ((AttachmentAddedEvent) event).getName();
        else if (event instanceof AttachmentUpdatedEvent)
            name = ((AttachmentUpdatedEvent) event).getName();
        else if (event instanceof AttachmentDeletedEvent)
            name = ((AttachmentDeletedEvent) event).getName();

        logger.warn("Event's name: " + name);

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
    }}
