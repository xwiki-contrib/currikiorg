package org.curriki.gwt.client.widgets.currikiitem.display;

import com.google.gwt.user.client.ui.HTML;
import com.xpn.xwiki.gwt.api.client.Attachment;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;

public class AudioItemDisplay extends AttachementItemDisplay {

    public AudioItemDisplay(Document doc, CurrikiItem item) {
        super(doc, item);
    }

    public String getType() {
        return Constants.TYPE_AUDIO;
    }

    public void initDisplay(Document doc) {
        panel.clear();
        initDisplayDescription(doc);
        displayAudio(doc);
        item.refreshItemInfos();
        item.switchHeaderButtonsToViewMode();
    }

    private void displayAudio(Document doc) {
        HTML html = new HTML();

        if (doc.getAttachments().size() > 0) {
            Attachment att = (Attachment) doc.getAttachments().get(0);
            String extension = getFileExtension(att.getFilename());
            if (extension.equals("MP3")) {
                String playerUrl = Main.getTranslation("editor.mp3playerurl");
                if ((playerUrl==null)||playerUrl.equals("")||playerUrl.equals("editor.mp3playerurl"))
                    playerUrl =  Constants.DEFAULT_MP3PLAYER_URL;

                String mp3Url = att.getDownloadUrl();
                String content =
                        "<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,0,0\" id=\"mini_player_mp3\" align=\"middle\" height=\"25\" width=\"331\">" +
                                "<param name=\"allowScriptAccess\" value=\"sameDomain\">" +
                                "<param name=\"movie\" value=\""+playerUrl+"?my_mp3="+mp3Url+"&my_text="+att.getFilename()+"\">" +
                                "<param name=\"quality\" value=\"high\">" +
                                "<param name=\"bgcolor\" value=\"#ffffff\">" +
                                "<embed src=\""+playerUrl+"?my_mp3="+mp3Url+"&my_text="+att.getFilename()+"\" quality=\"high\" bgcolor=\"#ffffff\" name=\"mini_player_mp3\" allowscriptaccess=\"sameDomain\" type=\"application/x-shockwave-flash\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" align=\"middle\" height=\"25\" width=\"331\">" +
                                "</object>";
                html.setHTML(content);
                panel.add(html);
            }
        }
        initDisplayAttachment(doc);
    }
}
