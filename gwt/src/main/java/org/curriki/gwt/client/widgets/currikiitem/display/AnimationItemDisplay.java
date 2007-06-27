package org.curriki.gwt.client.widgets.currikiitem.display;

import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.Constants;

import com.google.gwt.user.client.ui.HTML;
import com.xpn.xwiki.gwt.api.client.Attachment;
import com.xpn.xwiki.gwt.api.client.Document;

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
            String flashUrl = att.getDownloadUrl();
            String content =
                    "<center>" +
                            "<OBJECT CLASSID=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" CODEBASE=\"http://active.macromedia.com/flash5/cabs/swflash.cab#version=5,0,0,0\">" +
                            "<PARAM NAME=movie VALUE=\"" + flashUrl + "\">" +
                            "<PARAM NAME=play VALUE=true>" +
                            "<PARAM NAME=loop VALUE=false>" +
                            "<PARAM NAME=quality VALUE=low>" +
                            "<EMBED SRC=\"" + flashUrl + "\" quality=low loop=false TYPE=\"application/x-shockwave-flash\" PLUGINSPAGE=\"http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash\">" +
                            "</EMBED>" +
                            "</OBJECT>" +
                            "</center>";
            html.setHTML(content);
            panel.add(html);
        }
    }

}
