package org.curriki.gwt.client.widgets.currikiitem.display;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.xpn.xwiki.gwt.api.client.Attachment;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.utils.URLUtils;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;
import asquare.gwt.tk.client.ui.BasicPanel;

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

        XObject obj = doc.getObject(Constants.VIDITALK_CLASS);

        if (obj != null){
            // Viditalk Item
            final String vId = obj.getViewProperty(Constants.VIDITALK_VIDEO_ID_PROPERTY);
            if (vId == null || vId.equals("")){
                // No video id present
                panel.add(new HTML(doc.getFullName() + Main.getTranslation("asset.asset_corrupted")));
            } else {
                // Load Viditalk player
                BasicPanel vPlayer = new BasicPanel();
                vPlayer.addStyleName("cb-video-viditalk-player");

                String rnd = (new Integer (Random.nextInt(2000000000))).toString();
                final String divId = "viditalk_div_"+rnd;
                vPlayer.setId(divId);
                HTML msg = new HTML(Main.getTranslation("viditalk.loading_player"));
                vPlayer.add(msg);

                panel.add(vPlayer);

                final String siteCode = Main.getTranslation(Constants.VIDITALK_SITECODE_VAR);

                // The div isn't actually on the page yet, so we need to try to do the embed later
                Timer t = new Timer() {
                    public void run() {
                        if (DOM.getElementById(divId) != null) {
                            embedPlayer(divId, siteCode, vId);
                        } else {
                            this.schedule(Random.nextInt(4000)+1000);
                        }
                    }
                };

                t.schedule(Random.nextInt(4000)+1000);
            }
        } else {
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

    protected void initEditAttachment(){
        XObject obj = doc.getObject(Constants.VIDITALK_CLASS);

        if (obj != null){
            // Viditalk Item

            // Load Viditalk capture
            BasicPanel vPlayer = new BasicPanel();
            vPlayer.addStyleName("cb-video-viditalk-capture");

            String rnd = (new Integer (Random.nextInt(2000000000))).toString();
            final String divId = "viditalk_div_"+rnd;
            vPlayer.setId(divId);
            HTML msg = new HTML(Main.getTranslation("viditalk.loading_capture"));
            vPlayer.add(msg);

            panel.add(vPlayer);

            final String siteCode = Main.getTranslation(Constants.VIDITALK_SITECODE_VAR);

            final AudioItemDisplay curr = this;

            // The div isn't actually on the page yet, so we need to try to do the embed later
            Timer t = new Timer() {
                public void run() {
                    if (DOM.getElementById(divId) != null) {
                        embedCapture(curr, divId, siteCode);
                    } else {
                        this.schedule(Random.nextInt(4000)+1000);
                    }
                }
            };

            t.schedule(Random.nextInt(4000)+1000);
        } else {
            super.initEditAttachment();
        }
    }

    public void vidiTalkUpload(String vId){
        saveVidiTalkId(vId);
    }

    public boolean saveVidiTalkId(String vId){
        XObject obj = doc.getObject(Constants.VIDITALK_CLASS);

        obj.set(Constants.VIDITALK_VIDEO_ID_PROPERTY, vId);

        CurrikiService.App.getInstance().saveObject(obj, new CurrikiAsyncCallback(){
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);
            }

            public void onSuccess(Object object) {
                super.onSuccess(object);
                reloadDocument();
                status = Constants.VIEW;
                resetEmbedCapture();
            }
        });
        
        return true;
    }

    public native void embedPlayer(String divId, String siteCode, String vId) /*-{
        $wnd.embedVidiPlayback(divId, siteCode, vId);
    }-*/;

    /* By default VIDITalk will only allow one capture compontent per page,
     * but we are not reloading the page after closing one, so we trick the component (v1.78) here.
     */
    public native void resetEmbedCapture() /*-{
        $wnd.capture_div = "";
        $wnd.flashLoaded = false;
    }-*/;

    public native void embedCapture(AudioItemDisplay x, String divId, String siteCode) /*-{
        $wnd.embedVidiCapture(divId, siteCode);
        $wnd.uploadComplete = function(vId){
            x.@org.curriki.gwt.client.widgets.currikiitem.display.AudioItemDisplay::vidiTalkUpload(Ljava/lang/String;)(vId);
        }
    }-*/;
}
