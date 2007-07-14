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

import asquare.gwt.tk.client.ui.ModalDialog;
import asquare.gwt.tk.client.ui.BasicPanel;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import org.curriki.gwt.client.Main;

public class VidiTalkUploadDialog extends ModalDialog {
    ClickListener nextCallback;
    ClickListener cancelCallback;
    private String videoId;

    public VidiTalkUploadDialog(ClickListener nextCallback, ClickListener cancelCallback){
        this.nextCallback = nextCallback;
        this.cancelCallback = cancelCallback;

        init();
    }

    public void init(){
        // Load the VIDITalk component and register a JavaScript callback function for it

        addStyleName("dialog-addfile");
        setCaption(Main.getTranslation("addfile.add_a_learning_resource"), false);
        setContentMinWidth(380);
        setContentMinHeight(380);

        BasicPanel main = new BasicPanel();
        main.addStyleName("dialog-addfile-content");

        BasicPanel text = new BasicPanel();
        text.addStyleName("dialog-addfile-text");
        text.addStyleName("dialog-viditalk-capture");
        
        String rnd = (new Integer (Random.nextInt(2000000000))).toString();
        text.setId("viditalk_capture_div_"+rnd);
        HTML msg = new HTML(Main.getTranslation("viditalk.loading_capture"));
        text.add(msg);

        Grid bottom = new Grid(2, 2);
        bottom.addStyleName("dialog-addfile-bottom");
        bottom.getColumnFormatter().addStyleName(0, "addfile-dialog-col1");
        bottom.getColumnFormatter().addStyleName(1, "addfile-dialog-col2");

        BasicPanel actions = new BasicPanel();
        actions.addStyleName("addfile-dialog-actions");

        ClickListener cancelListener = new ClickListener(){
            public void onClick(Widget sender){
                cancelCallback.onClick(sender);
            }
        };
        Button cancel = new Button(Main.getTranslation("editor.btt_cancel"), cancelListener);
        cancel.addStyleName("dialog-cancel");
        cancel.addStyleName("addfile-dialog-cancel");
        actions.add(cancel);

        bottom.setWidget(1, 1, actions);

        main.add(text);
        main.add(bottom);

        add(main);
        
        show();

        addJSCallback(this, rnd);
    }

    public native void addJSCallback(VidiTalkUploadDialog x, String rnd) /*-{
        $wnd.uploadComplete = function(videoId) {
            x.@org.curriki.gwt.client.widgets.addfile.VidiTalkUploadDialog::setViditalkVideoId(Ljava/lang/String;)(videoId);
        };
        $wnd.embedVidiCapture("viditalk_capture_div_"+rnd, x.@org.curriki.gwt.client.widgets.addfile.VidiTalkUploadDialog::getViditalkSitecode()());
    }-*/;

    public void setViditalkVideoId(String videoId){
        this.videoId = videoId;
        nextCallback.onClick(this);
    }

    public String getVideoId() {
        return videoId;
    }

    public String getViditalkSitecode() {
        return Main.getTranslation("viditalk.sitecode");
    }
}
