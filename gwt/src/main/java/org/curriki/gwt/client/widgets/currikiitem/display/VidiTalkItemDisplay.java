package org.curriki.gwt.client.widgets.currikiitem.display;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.DOM;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;

public class VidiTalkItemDisplay extends AttachementItemDisplay {

    public VidiTalkItemDisplay(Document doc, CurrikiItem item) {
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
        XObject obj = doc.getObject(Constants.VIDITALK_CLASS);

        // Viditalk Item
        final String vId = obj.getViewProperty(Constants.VIDITALK_VIDEO_ID_PROPERTY);
        if (vId == null || vId.equals("")){
            // No video id present
            panel.add(new HTML(doc.getFullName() + Main.getTranslation("asset.asset_corrupted")));
        } else {
            // Load Viditalk player
            VerticalPanel vPlayer = new VerticalPanel();
            vPlayer.addStyleName("cb-video-viditalk-player");

            String rnd = (new Integer (Random.nextInt(2000000000))).toString();
            final String divId = "viditalk_div_"+rnd;
            // DONE GWT15 Upgrade Issue vPlayer.setId(divId);  - moved after .add using setElementProperty
            HTML msg = new HTML(Main.getTranslation("viditalk.loading_player"));
            vPlayer.add(msg);

            HTML dLink = new HTML("<a href=\""+Constants.VIDITALK_DOWNLOAD_DIR+vId+".mpg\">"+Main.getTranslation("viditalk.download_msg")+"</a>");
            dLink.addStyleName("cb-video-viditalk-download-link");

            panel.add(vPlayer);
            panel.add(dLink);

            DOM.setElementProperty(vPlayer.getElement(), "id", divId);

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
    }

    protected void initEditAttachment(){
        // Viditalk Item

        // Load Viditalk capture
        VerticalPanel vPlayer = new VerticalPanel();
        vPlayer.addStyleName("cb-video-viditalk-capture");

        String rnd = (new Integer (Random.nextInt(2000000000))).toString();
        final String divId = "viditalk_div_"+rnd;
        // DONE GWT15 Upgrade Issue vPlayer.setId(divId); - moved after .add using setElementProperty
        HTML msg = new HTML(Main.getTranslation("viditalk.loading_capture"));
        vPlayer.add(msg);

        panel.add(vPlayer);

        DOM.setElementProperty(vPlayer.getElement(), "id", divId);

        final String siteCode = Main.getTranslation(Constants.VIDITALK_SITECODE_VAR);

        final VidiTalkItemDisplay curr = this;

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
    }

    public void cancelEditMode() {
        resetEmbedCapture();
        status = Constants.VIEW;
        initDisplay(doc);
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
                resetEmbedCapture();
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
        $wnd.called_once = false;
    }-*/;

    public native void embedCapture(VidiTalkItemDisplay x, String divId, String siteCode) /*-{
        $wnd.embedVidiCapture(divId, siteCode, null, null, false);
        $wnd.uploadComplete = function(vId){
            x.@org.curriki.gwt.client.widgets.currikiitem.display.VidiTalkItemDisplay::vidiTalkUpload(Ljava/lang/String;)(vId);
        }
    }-*/;
}