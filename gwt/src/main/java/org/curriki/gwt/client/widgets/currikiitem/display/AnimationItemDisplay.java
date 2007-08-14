package org.curriki.gwt.client.widgets.currikiitem.display;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.xpn.xwiki.gwt.api.client.Attachment;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;

public class AnimationItemDisplay extends AttachementItemDisplay {

    public AnimationItemDisplay(Document doc, CurrikiItem item) {
        super(doc, item);
    }

    public String getType() {
        return Constants.TYPE_ANIMATION;
    }

    public void initDisplay(Document doc) {
        panel.clear();
        initDisplayDescription(doc);
        displayAnimation(doc);
        initDisplayAttachment(doc);
    }

    protected void initEdit(){
        initEditDescription();
        displayAnimation(doc);
        initEditAttachment();
    }

    private void displayAnimation(Document doc) {
        HTML html = new HTML();

        if (doc.getAttachments().size() > 0) {
            Attachment att = (Attachment) doc.getAttachments().get(0);
            final String flashUrl = att.getDownloadUrl();
            final String flashNum = (new Integer (Random.nextInt(2000000000))).toString();
            String content =
                "<center>" +
                "<div id='flashContent"+flashNum+"'>"+Main.getTranslation("mimetype.flashdisplay.flashcontenttext")+"</div>"+
                "<button id='flashControl"+flashNum+"' class='flash-control-button flash-control-button-play' title='"+Main.getTranslation("mimetype.flashdisplay.play")+"' onclick='playFlash("+flashNum+", this);'>"+Main.getTranslation("mimetype.flashdisplay.play")+"</ button><br />"+
                "<a href='"+flashUrl+"' class='flash-control-newwindow' target='_blank'>"+Main.getTranslation("mimetype.flashdisplay.newwindow")+"</a>"+
                "</center>";
            html.setHTML(content);
            panel.add(html);

            // The div isn't actually on the page yet, so we need to try to do the embed later
            Timer t = new Timer() {
                public void run() {
                    if (DOM.getElementById("flashItem"+flashNum) != null) {
                        embedFlash(flashUrl, flashNum);
                    } else {
                        this.schedule(Random.nextInt(4000)+1000);
                    }
                }
            };

            t.schedule(Random.nextInt(4000)+1000);
        }
    }

    public native void embedFlash(String flashURL, String flashNum) /*-{
        $wnd.displayFlash(flashURL, flashNum);
        $wnd.initialStopFlashMovie(flashNum);
    }-*/;

}
