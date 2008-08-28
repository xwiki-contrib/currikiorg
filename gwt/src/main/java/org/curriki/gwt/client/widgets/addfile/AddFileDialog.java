/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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

import org.curriki.gwt.client.widgets.basicpanel.BasicPanel;
import org.curriki.gwt.client.widgets.modaldialogbox.CurrikiDialog;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.ClickListenerDocument;
import org.curriki.gwt.client.utils.XWikiGWTPanelLoader;
import org.curriki.gwt.client.widgets.upload.UploadWidget;

public class AddFileDialog extends CurrikiDialog {
    ClickListenerDocument nextCallback;
    ClickListener cancelCallback;
    String collectionName;

    private RadioButton fComputer;
    private RadioButton fWeb;
    private RadioButton fVideo;
    private UploadWidget tFile;
    private URLEntry tURL;
    private VidiTalkUploadComponent tVideo;
    private Document doc;
    private Button next;

    public AddFileDialog(String collectionName, ClickListenerDocument nextCallback, ClickListener cancelCallback) {
        this.nextCallback = nextCallback;
        this.cancelCallback = cancelCallback;
        this.collectionName = collectionName;
        init();
    }

    public void init(){
        addStyleName("dialog-addfile");
        //addController(new CurrikiDialog.DragStyleController(this));
        setCaption(Main.getTranslation("addfile.add_a_learning_resource"), true);
        // TODO GWT15 setContentMinWidth(579);
        // TODO GWT15 setContentMinHeight(468);

        BasicPanel main = new BasicPanel();
        main.addStyleName("dialog-addfile-content");

        BasicPanel text = new BasicPanel();
        text.addStyleName("dialog-addfile-text");
        XWikiGWTPanelLoader.loadWikiPage(Constants.DIALOG_ADD_FILE, text);

        Grid bottom = new Grid(2, 2);
        bottom.addStyleName("dialog-addfile-bottom");
        bottom.getColumnFormatter().addStyleName(0, "addfile-dialog-col1");
        bottom.getColumnFormatter().addStyleName(1, "addfile-dialog-col2");
        bottom.getCellFormatter().addStyleName(0, 0, "addfile-dialog-left-content");
        bottom.getCellFormatter().addStyleName(0, 1, "addfile-dialog-right-content");
        bottom.getCellFormatter().addStyleName(1, 0, "addfile-dialog-left-action");
        bottom.getCellFormatter().addStyleName(1, 1, "addfile-dialog-right-action");


        VerticalPanel chooser = new VerticalPanel();
        chooser.addStyleName("dialog-addfile-chooser");
        HTML sText = new HTML(Main.getTranslation("addfile.select_file_or_link"));
        fComputer = new RadioButton("from", Main.getTranslation("addfile.this_is_a_file_on_my_computer"));
        fWeb = new RadioButton("from", Main.getTranslation("addfile.this_is_a_web_link"));
        fVideo = new RadioButton("from", Main.getTranslation("addfile.record_a_video"));

        tFile = new UploadWidget("", false);

        tURL = new URLEntry();
        tURL.setVisibleLength(60);
        tURL.setText("http://");

        tVideo = new VidiTalkUploadComponent(collectionName, nextCallback, cancelCallback);

        next = new Button(Main.getTranslation("editor.btt_next"));

        fComputer.addClickListener(new ClickListener(){
            public void onClick(Widget sender){
                // Show the tFile box, not the tURL box
                tURL.setVisible(false);
                tFile.setVisible(true);
                tVideo.setVisible(false);
                next.setVisible(true);
            }
        });
        fWeb.addClickListener(new ClickListener(){
            public void onClick(Widget sender){
                // Show the tFile box, not the tURL box
                tURL.setVisible(true);
                tURL.setFocus(true);
                tFile.setVisible(false);
                tVideo.setVisible(false);
                next.setVisible(true);
            }
        });
        fVideo.addClickListener(new ClickListener(){
            public void onClick(Widget sender){
                // Show the tFile box, not the tURL box
                tURL.setVisible(false);
                tFile.setVisible(false);
                tVideo.setVisible(true);
                next.setVisible(false);
            }
        });

        tFile.setVisible(true);
        tURL.setVisible(false);
        tVideo.setVisible(false);

        fComputer.setChecked(true);

        chooser.add(sText);
        chooser.add(fComputer);
        chooser.add(fWeb);
        chooser.add(fVideo);

        chooser.add(tURL);
        chooser.add(tFile);
        bottom.setWidget(0, 1, tVideo);


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

        ClickListener nextListener = new ClickListener(){
            public void onClick(Widget sender){
                int chosen = (fWeb.isChecked()?1:(fComputer.isChecked()?2:(fVideo.isChecked()?3:0)));
                switch (chosen){
                    case 1: // fWeb
                        if (tURL.isValidUrl()) {
                            CurrikiService.App.getInstance().createLinkAsset(collectionName, tURL.getURL(), new CurrikiAsyncCallback() {
                                public void onSuccess(Object result) {
                                    super.onSuccess(result);
                                    Document newDoc = (Document) result;
                                    nextCallback.setDoc(newDoc);
                                    nextCallback.onClick(tURL);
                                }
                            });
                        } else {
                            Window.alert(Main.getTranslation("addfile.invalid_url"));
                        }
                        break;
                    case 2: // fComputer
                        // Okay, this is a bit more complicated
                        // 1. We need a temporary asset
                        CurrikiService.App.getInstance().createTempSourceAsset(collectionName, new CurrikiAsyncCallback() {
                            public void onSuccess(Object object) {
                                super.onSuccess(object);
                                doc = (Document) object;

                                // 2. We set the target URL to be the add Attachment URL for the temp asset
                                tFile.setAction(doc.getUploadURL());
                                tFile.addFormHandler(new FormHandler() {
                                    public void onSubmit(FormSubmitEvent formSubmitEvent) {
                                        // We don't really need to do anything here
                                        // Although it might be nice to be able to show an upload-progress bar or such
                                    }

                                    public void onSubmitComplete(FormSubmitCompleteEvent event) {
                                        nextCallback.setDoc(doc);
                                        nextCallback.onClick(tFile);
                                    }
                                });
                                // 3. We submit the form
                                tFile.sendFile();
                            }
                        });
                        break;
                    case 3: // fVideo
                        break; // We should not be able to get here
                    default: // None
                        Window.alert(Main.getTranslation("addfile.please_make_a_selection"));
                        break;
                }
            }
        };

        next.addClickListener(nextListener);
        next.addStyleName("dialog-next");
        next.addStyleName("addfile-dialog-next");
        actions.add(next);


        bottom.setWidget(0, 0, chooser);
        bottom.setWidget(1, 1, actions);

        main.add(text);
        main.add(bottom);

        add(main);
        show();
    }
}
