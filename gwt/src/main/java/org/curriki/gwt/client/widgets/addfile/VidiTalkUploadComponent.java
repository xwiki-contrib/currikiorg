/*
 * Copyright 2007, The Global Education and Learning Community,
 * and individual contributors as indicated by the contributors.txt.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * @author dward
 *
 */
package org.curriki.gwt.client.widgets.addfile;

import asquare.gwt.tk.client.ui.BasicPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.DOM;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.utils.ClickListenerDocument;

public class VidiTalkUploadComponent extends BasicPanel
{
    ClickListenerDocument nextCallback;
    ClickListener cancelCallback;
    private String videoId;
    private String divId;
    private String siteCode;
    private VidiTalkUploadComponent vUpload;
    private Document doc;
    private String collectionName;

    public VidiTalkUploadComponent(String collectionName, ClickListenerDocument nextCallback, ClickListener cancelCallback)
    {
        this.collectionName = collectionName;
        this.nextCallback = nextCallback;
        this.cancelCallback = cancelCallback;

        vUpload = this;

        String rnd = (new Integer (Random.nextInt(2000000000))).toString();
        divId = "viditalk_capture_div"+rnd;
        setId(divId);
        addStyleName("dialog-addfile-text");
        addStyleName("dialog-viditalk-capture");

        siteCode = getViditalkSitecode();

        // The div isn't actually on the page yet, so we need to try to do the embed later
        Timer t = new Timer() {
            public void run() {
                if (DOM.getElementById(divId) != null) {
                    embedCapture(vUpload, divId, siteCode);
                } else {
                    this.schedule(Random.nextInt(3000)+1000);
                }
            }
        };
        t.schedule(Random.nextInt(4000)+1000);
    }

    public native void embedCapture(VidiTalkUploadComponent x, String divId, String siteCode) /*-{
        $wnd.uploadComplete = function(videoId) {
            x.@org.curriki.gwt.client.widgets.addfile.VidiTalkUploadComponent::setViditalkVideoId(Ljava/lang/String;)(videoId);
        };
        $wnd.embedVidiCapture(divId, siteCode);
    }-*/;

    public void setViditalkVideoId(String vidId){
        this.videoId = vidId;

        CurrikiService.App.getInstance().createTempSourceAsset(collectionName, new CurrikiAsyncCallback() {
            public void onSuccess(Object object) {
                super.onSuccess(object);
                doc = (Document) object;

                // 2. We need to set the video Id in the document
                CurrikiService.App.getInstance().updateViditalk(doc.getFullName(), videoId, new CurrikiAsyncCallback() {
                    public void onSuccess(Object result) {
                        super.onSuccess(result);
                        doc = (Document) result;
                        nextCallback.setDoc(doc);
                        resetVidiTalkJS();
                        nextCallback.onClick(vUpload);
                    }
                });
            }
        });
    }

    public String getVideoId() {
        return videoId;
    }

    public String getViditalkSitecode() {
        return Main.getTranslation("viditalk.sitecode");
    }

    /* By default VIDITalk will only allow one capture compontent per page,
     * but we are not reloading the page after closing one, so we trick the component (v1.78) here.
     */
    public native void resetVidiTalkJS() /*-{
        $wnd.capture_div = "";
        $wnd.flashLoaded = false;
        $wnd.called_once = false;
    }-*/;
}
