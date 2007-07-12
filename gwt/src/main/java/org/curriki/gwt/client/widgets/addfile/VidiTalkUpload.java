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

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.utils.ClickListenerDocument;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;

public class VidiTalkUpload extends Label {
    Document doc;
    VidiTalkUploadDialog dialog;
    VidiTalkUpload videoUpload;

    String videoId;

    public VidiTalkUpload(String label){
        super(label);
    }

    public void upload(final ClickListenerDocument nextCallback, final ClickListener cancelCallback) {
        // 1. Display VIDITalk component
        // 2. Wait for JS callback from it to get Video ID
        // 3. Add video id to the VIDITalkAssetClass object in the doc
        // 4. Do next callback

        doc = nextCallback.getDoc();
        videoUpload = this;

        ClickListener next = new ClickListener(){
            public void onClick(Widget sender){
                videoId = dialog.getVideoId();
                CurrikiService.App.getInstance().updateViditalk(doc.getFullName(), videoId, new CurrikiAsyncCallback() {
                    public void onSuccess(Object result) {
                        super.onSuccess(result);
                        doc = (Document) result;
                        dialog.hide();
                        nextCallback.onClick(videoUpload);
                    }
                });
            }
        };
        ClickListener cancel = new ClickListener(){
            public void onClick(Widget sender){
                dialog.hide();
                cancelCallback.onClick(videoUpload);
            }
        };

        dialog = new VidiTalkUploadDialog(next, cancel);
    }
}
