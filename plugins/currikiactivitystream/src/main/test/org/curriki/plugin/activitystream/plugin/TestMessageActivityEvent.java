package org.curriki.plugin.activitystream.plugin;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.plugin.activitystream.api.ActivityEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMessageActivityEvent {

    private MessageActivityEventStub1 msg1 = new MessageActivityEventStub1(null, null);
    private MessageActivityEventStub2 msg2 = new MessageActivityEventStub2(null, null);
    private MessageActivityEventStub3 msg3 = new MessageActivityEventStub3(null, null);
    private MessageActivityEventStub4 msg4 = new MessageActivityEventStub4(null, null);

    @Test
    public void testGetRecipientRole(){
        assertEquals("Group_FelixDigestTest.AdminGroup", msg1.getRecipientRole());
        assertEquals("Group_FelixDigestTest.AdminGroup", msg2.getRecipientRole());
        assertEquals(null, msg3.getRecipientRole());
        assertEquals(null, msg4.getRecipientRole());
    }

    @Test
    public void testGetMailTo(){
        assertEquals("XWiki.Flixt", msg1.getMailTo());
        assertEquals("XWiki.Flixt", msg2.getMailTo());
        assertEquals(null, msg3.getMailTo());
        assertEquals(null, msg4.getMailTo());
    }

    @Test
    public void testGetMailToGroup(){
        assertEquals("on", msg1.getMailToGroup());
        assertEquals(null, msg2.getMailToGroup());
        assertEquals(null, msg3.getMailToGroup());
        assertEquals(null, msg4.getMailToGroup());
    }

}

class MessageActivityEventStub1 extends MessageActivityEvent {
    public MessageActivityEventStub1(ActivityEvent event, XWikiContext context) {super(event, context);}

    @Override
    public String getParam5() {
        return "{\"recipientRole\":\"Group_FelixDigestTest.AdminGroup\",\"mailTo\":\"XWiki.Flixt,\",\"mailToGroup\":\"on\"}";
    }
}

class MessageActivityEventStub2 extends MessageActivityEvent {
    public MessageActivityEventStub2(ActivityEvent event, XWikiContext context) {super(event, context);}

    @Override
    public String getParam5() {
        return "{\"recipientRole\":\"Group_FelixDigestTest.AdminGroup\",\"mailTo\":\"XWiki.Flixt\"}";
    }
}

class MessageActivityEventStub3 extends MessageActivityEvent {
    public MessageActivityEventStub3(ActivityEvent event, XWikiContext context) {super(event, context);}

    @Override
    public String getParam5() {
        return null;
    }
}

class MessageActivityEventStub4 extends MessageActivityEvent {
    public MessageActivityEventStub4(ActivityEvent event, XWikiContext context) {super(event, context);}

    @Override
    public String getParam5() {
        return "crap";
    }
}
