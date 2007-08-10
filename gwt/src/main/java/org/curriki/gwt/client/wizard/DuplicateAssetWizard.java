package org.curriki.gwt.client.wizard;

import asquare.gwt.tk.client.ui.ModalDialog;
import asquare.gwt.tk.client.ui.behavior.TabFocusController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.AssetDocument;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.editor.Editor;
import org.curriki.gwt.client.utils.ClickListenerMetadata;
import org.curriki.gwt.client.widgets.metadata.MetadataEdit;
import org.curriki.gwt.client.widgets.modaldialogbox.NextCancelDialog;
import org.curriki.gwt.client.widgets.siteadd.ChooseCollectionDialog;
import org.curriki.gwt.client.widgets.template.ChooseTemplateDialog;

/** Copyright 2006,XpertNet SARL,and individual contributors as indicated
 * by the contributors.txt.
 *
 * This is free software;you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation;either version2.1of
 * the License,or(at your option)any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software;if not,write to the Free
 * Software Foundation,Inc.,51 Franklin St,Fifth Floor,Boston,MA
 * 02110-1301 USA,or see the FSF site:http://www.fsf.org.
 *
 * @author ldubost
 */

public class DuplicateAssetWizard {
    private static ChooseCollectionDialog collections;
    private static ChooseTemplateDialog chooseTemplateDialog;
    private static ModalDialog metaPanel;
    private String collectionName;
    private MetadataEdit meta;
    private Document newDoc;
    private NextCancelDialog confirmDialog;
    private boolean markAsCopy = false;

    public DuplicateAssetWizard(String assetName) {
        duplicateAsset(assetName);
    }

    public DuplicateAssetWizard(String assetName, boolean markAsCopy) {
        this.markAsCopy = markAsCopy;
        duplicateAsset(assetName);
    }

    public void duplicateAsset(final String assetName) {
        // 1. Choose a collection to add it to (if more than just the default)
        // 2. Choose the template
        // 3. Fill out Metadata
        // 4. Propose redirect to composite asset
        ClickListener next = new ClickListener(){
            public void onClick(Widget sender){
                if (collections == null || collections.getSelectedItem() == null || collections.getSelectedItem().getPageName() == null || collections.getSelectedItem().getPageName() == "__NOSELECT__"){
                    Window.alert(Main.getTranslation("duplicateasset.selectcollection"));
                } else{
                    collections.hide();
                    duplicateAsset(assetName, collections.getSelectedItem().getPageName());
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

    public void duplicateAsset(String assetName, String collection) {
        this.collectionName = collection;

        CurrikiService.App.getInstance().createTempSourceAssetFromTemplate(assetName, collectionName, false, new CurrikiAsyncCallback() {
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object result) {
                super.onSuccess(result);
                newDoc = (AssetDocument) result;

                if (markAsCopy){
                    // TODO: There should be a better way to set the property value on the browser side, but it seems to only be able to be set on the server side
                    XObject assetObj = newDoc.getObject(Constants.ASSET_CLASS);
                    String editVersion = assetObj.getEditProperty(Constants.ASSET_TITLE_PROPERTY);

                    //Look for ' value="title"' and insert "Copy of" before the title part
                    int titlePos = editVersion.indexOf("value=");
                    editVersion = editVersion.substring(0, titlePos+7)+Main.getTranslation("duplicate.copy_of")+" "+editVersion.substring(titlePos+7);
                    assetObj.setEditProperty(Constants.ASSET_TITLE_PROPERTY, editVersion);

                }

                initMetadataUI();
            }
        });
    }

    public void initMetadataUI(){
        meta = new MetadataEdit(newDoc, false);

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
        metaPanel.removeController(metaPanel.getController(TabFocusController.class));
        metaPanel.addStyleName("dialog-metadata");
        metaPanel.setCaption(Main.getTranslation("duplicateasset.describelearningresource"), false);

        metaPanel.add(meta);

        Button bttCancel = new Button(Main.getTranslation("editor.btt_cancel"), new ClickListener() {
            public void onClick(Widget sender){
                // TODO: We really should delete from AssetTemp here
                metaPanel.hide();
            }
        });
        bttCancel.addStyleName("dialog-metadata-button-cancel");

        Button bttNext = new Button(Main.getTranslation("editor.btt_finish"), new ClickListenerMetadata(meta));
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
                        final String editURL = Main.getTranslation("params.gwturl") + "page=" + collectionName + "&new=1";
                        confirmDialog = new NextCancelDialog("duplicateasset.title", "duplicateasset.confirmredirect", "duplicateconfirm", new AsyncCallback() {
                            public void onFailure(Throwable throwable) {
                                confirmDialog.hide();
                                // If we find the collection in the tree we need to reload
                                Editor editor = Main.getSingleton().getEditor();
                                if (editor.isAssetInTree(collectionName)) {
                                    editor.setCurrentAssetInvalid(true);
                                    editor.setTreeContentInvalid(true);
                                    editor.refreshState();
                                }
                            }

                            public void onSuccess(Object object) {
                                confirmDialog.hide();
                                Main.changeWindowHref(editURL);
                            }
                        });
                    }
                });
    }

}
