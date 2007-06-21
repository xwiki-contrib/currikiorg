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
package org.curriki.gwt.client.wizard;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.widgets.siteadd.ChooseCollectionDialog;
import org.curriki.gwt.client.widgets.siteadd.ThankYouDialog;
import org.curriki.gwt.client.widgets.addfile.AddFileDialog;
import org.curriki.gwt.client.widgets.metadata.MetadataEdit;
import org.curriki.gwt.client.widgets.upload.UploadWidget;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.ClickListenerDocument;
import org.curriki.gwt.client.utils.Loading;
import org.curriki.gwt.client.utils.Translator;
import org.curriki.gwt.client.utils.ClickListenerMetadata;
import org.curriki.gwt.client.utils.CompletionCallback;
import asquare.gwt.tk.client.ui.ModalDialog;

public class AddFileWizard implements CompletionCallback
{
    private static ChooseCollectionDialog collections;
    private static ThankYouDialog thankYouDialog;
    private static AddFileDialog file;
    private static ModalDialog metaPanel;
    private String collectionName;

    private Document newDoc;
    private String category;

   private Command callbackCommand;

    public void addFile(){
        // 1. Choose a collection to add it to (if more than just the default)
        // 2. Choose if a link or a file
        // 2a. Get link URL
        // 2b. Upload File
        // 3. Fill out Metadata
        // 4. "Thank You" dialog
        ClickListener next = new ClickListener(){
            public void onClick(Widget sender){
                if (collections == null || collections.getSelectedItem() == null || collections.getSelectedItem().getPageName() == null || collections.getSelectedItem().getPageName().equals("__NOSELECT__")){
                    Window.alert(Main.getTranslation("addfile.selectcollection"));
                } else{
                    collections.hide();
                    addFile(collections.getSelectedItem().getPageName());
                }
            }
        };
        ClickListener cancel =  new ClickListener(){
            public void onClick(Widget sender){
                collections.hide();
            }
        };
        collections = new ChooseCollectionDialog(next, cancel);

    }

    public void addFile(String collection) {
        this.collectionName = collection;

        // We have a collection now
        // 2. Choose if link or file and get the URL or Upload the file
        ClickListenerDocument next = new ClickListenerDocument(){
            public void onClick(Widget sender){
                // We should now have a file or link
                file.hide();

                if (sender instanceof TextBox){
                    // Was a LINK
                    category = Constants.CATEGORY_LINK;
                    newDoc = doc;
                    initMetadata(false);
                } else if (sender instanceof UploadWidget){
                    // Was a file
                    newDoc = doc;
                    initMetadata(false);
                }
            }
        };

        ClickListener cancel =  new ClickListener(){
            public void onClick(Widget sender){
                file.hide();
            }
        };
        file = new AddFileDialog(collectionName, next, cancel);
    }

    private void getCategory() {
        XObject obj = newDoc.getObject(Constants.ASSET_CLASS);
        String category = (String) obj.get(Constants.ASSET_CATEGORY_PROPERTY);
        if ((category != null)&&(this.category==null)) {
            this.category = category;
        }
    }


    public void initMetadata(boolean fromTemplate){
        CurrikiService.App.getInstance().updateMetadata(newDoc.getFullName(), fromTemplate, new CurrikiAsyncCallback() {
            public void onSuccess(Object result) {
                super.onSuccess(result);
                newDoc = (Document) result;
                getCategory();
                initMetadataUI();
            }
        });

    }

    public void initMetadataUI(){
        MetadataEdit meta = new MetadataEdit(newDoc, false);

        // This really should be somewhere else
        meta.SetHiddenCategoryValue(category);

        // Add an event handler to the form.
        meta.addFormHandler(new FormHandler() {
            public void onSubmit(FormSubmitEvent formSubmitEvent) {
                Main.getSingleton().startLoading();
            }

            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                Main.getSingleton().finishLoading();
                finishWizard();
            }
        });

        metaPanel = new ModalDialog();
        metaPanel.addStyleName("dialog-metadata");
        metaPanel.setCaption(Main.getTranslation("addfile.describelearningresource"), false);

        metaPanel.add(meta);

        Button bttCancel = new Button(Main.getTranslation("editor.btt_cancel"), new ClickListener() {
            public void onClick(Widget sender){
                // TODO: We really should delete from AssetTemp here
                metaPanel.hide();
            }
        });
        bttCancel.addStyleName("dialog-metadata-button-cancel");

        Button bttNext = new Button(Main.getTranslation("editor.btt_finish"), new ClickListenerMetadata(
            meta));
        bttNext.addStyleName("gwt-ButtonOrange");
        bttNext.addStyleName("dialog-metadata-button-finish");

        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.addStyleName("dialog-metadata-button-panel");
        buttonPanel.add(bttCancel);
        buttonPanel.add(bttNext);

        metaPanel.add(buttonPanel);
        metaPanel.show();
    }

    private void finishWizard() {
        metaPanel.hide();

        // We need to move the asset to this collection
        CurrikiService.App.getInstance().finalizeAssetCreation(newDoc.getFullName(), collectionName, -1,
                new CurrikiAsyncCallback(){
                    public void onFailure(Throwable caught) {
                        super.onFailure(caught);
                        Main.getSingleton().showError(caught);
                    }

                    public void onSuccess(Object result) {
                        super.onSuccess(result);
                        ClickListener cancel =  new ClickListener(){
                            public void onClick(Widget sender){
                                thankYouDialog.hide();
                                thankYouDialog = null;
                                if (callbackCommand != null){
                                    callbackCommand.execute();
                                }
                            }
                        };

                        thankYouDialog = new ThankYouDialog(Constants.DIALOG_THANKYOU_ADD_CURRIKI, cancel);
                    }
                });
    }

    public void setCompletionCallback(Command cmd)
    {
        callbackCommand = cmd;
    }
}
