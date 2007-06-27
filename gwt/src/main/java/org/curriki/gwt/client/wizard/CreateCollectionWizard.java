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

import asquare.gwt.tk.client.ui.ModalDialog;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.AssetDocument;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.ClickListenerMetadata;
import org.curriki.gwt.client.widgets.metadata.MetadataEdit;
import org.curriki.gwt.client.widgets.siteadd.ThankYouDialog;
import org.curriki.gwt.client.widgets.template.TemplateCopier;

public class CreateCollectionWizard {
    private String createdPage;
    private String category;
    private static ModalDialog metaPanel;
    private ThankYouDialog thankYouDialog;

    private Document doc;

    public void addCollection(){
        createCollection(null, null, null);
    }

    public void createCollection(String space, String pageName, String pageTitle){
        CurrikiService.App.getInstance().createCollectionDocument(space, pageName, pageTitle, new CurrikiAsyncCallback() {
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);
                Window.alert(Main.getTranslation("createcollection.couldnotaddcolection") + ": " + throwable.getMessage());
            }

            public void onSuccess(Object object) {
                super.onSuccess(object);
                if (object == null){
                    Window.alert(Main.getTranslation("createcollection.failedtoaddtocollection"));
                } else {
                    doc = (Document) object;

                    //Now get metadata
                    category = Constants.CATEGORY_COLLECTION;
                    initMetadata();
                }
            }
        });
    }

    public void initMetadata(){
        MetadataEdit meta = new MetadataEdit(doc, false);

        // This really should be somewhere else
        meta.SetHiddenCategoryValue(category);

        // Add an event handler to the form.
        meta.addFormHandler(new FormHandler() {
            public void onSubmit(FormSubmitEvent formSubmitEvent) {
                Main.getSingleton().startLoading();
            }

            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                Main.getSingleton().finishLoading();
                metaPanel.hide();

                // We need to move the asset to this collection
                CurrikiService.App.getInstance().finalizeAssetCreation(doc.getFullName(), Constants.ROOT_COLLECTION_PAGE, -1,
                        new CurrikiAsyncCallback(){
                            public void onFailure(Throwable caught) {
                                super.onFailure(caught);
                                Main.getSingleton().showError(caught);
                            }

                            public void onSuccess(Object result) {
                                super.onSuccess(result);
                                final Document finalDoc = (Document) result;
                                // We now have a collection, we need to copy the "About" template to it

                                TemplateCopier copier = new TemplateCopier();
                                copier.copyTemplate(Constants.TEMPLATE_ABOUT_COLLECTION, finalDoc.getFullName(), new CurrikiAsyncCallback() {
                                    public void onSuccess(Object result){
                                        super.onSuccess(result);

                                        AssetDocument newDoc = (AssetDocument) result;

                                        CurrikiService.App.getInstance().finalizeAssetCreation(newDoc.getFullName(), finalDoc.getFullName(), -1,
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
                                                            String editURL = Main.getTranslation("params.gwturl")+"page="+finalDoc.getFullName()+"&newCollection=1";
                                                            Window.open(editURL, "_blank", "");
                                                        }
                                                    };

                                                    thankYouDialog = new ThankYouDialog(Constants.DIALOG_THANKYOU_CREATE_COLLECTION, cancel);
                                                }
                                            });
                                    }
                                });
                            }
                        });
            }
        });

        metaPanel = new ModalDialog();
        metaPanel.addStyleName("dialog-metadata");
        metaPanel.setCaption(Main.getTranslation("createcollection.describelearningresource"), false);

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
}
