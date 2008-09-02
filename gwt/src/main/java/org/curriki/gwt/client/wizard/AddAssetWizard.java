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
 * @author jeremi
 */
package org.curriki.gwt.client.wizard;

import org.curriki.gwt.client.widgets.basicpanel.BasicPanel;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.AssetDocument;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.editor.Editor;
import org.curriki.gwt.client.pages.EditPage;
import org.curriki.gwt.client.search.editor.ResourceAdder;
import org.curriki.gwt.client.search.editor.SearchPanel;
import org.curriki.gwt.client.utils.ClickListenerDocument;
import org.curriki.gwt.client.utils.ClickListenerMetadata;
import org.curriki.gwt.client.widgets.addfile.AddFileDialog;
import org.curriki.gwt.client.widgets.addfile.URLEntry;
import org.curriki.gwt.client.widgets.addfile.VidiTalkUploadComponent;
import org.curriki.gwt.client.widgets.metadata.MetadataEdit;
import org.curriki.gwt.client.widgets.template.ChooseTemplateDialog;
import org.curriki.gwt.client.widgets.upload.UploadWidget;


public class AddAssetWizard extends Wizard implements ClickListener, ResourceAdder {

    private Document newDoc = null;
    private String category = null;

    private Grid helpGrid;

    // Button to insert a file or a link
    private Button bttLink;
    // Button to insert an existing resources
    private Button bttExistingResource;

    // Button to insert a separator (not in the spec anymore)
    private Button bttSeparator;

    // Button to insert a blank content block
    private Button bttBlankContent;

    // Button to insert an html content block
    private Button bttHTMLContent;

    // Button to insert an direction content block
    private Button bttDirectionContent;

    // Button to insert a new folder of content
    private Button bttNewFolder;

    // Button to insert a new document from a template
    private Button bttFromTemplate;

    // Button to create new template
    private Button bttNewTemplate;

    // Button to cancel
    private Button bttCancel;


    private AddFileDialog addFileDialog;
    private ChooseTemplateDialog chooseTemplateDialog;

    UploadWidget upload = null;

    private Button bttNext = null;


    public AddAssetWizard() {
        initWhatToAdd();
        initWidget(panel);
    }

    private void getCategory() {
        XObject obj = newDoc.getObject(Constants.ASSET_CLASS);
        if (obj!=null) {
            String category = (String) obj.get(Constants.ASSET_CATEGORY_PROPERTY);
            if ((category != null)&&(this.category==null)) {
                this.category = category;
            }
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


    public void initMetadataUI() {
        panel.clear();
        showParentDialog();
        setParentCaption(Main.getTranslation("addasset.set_required_metadata"));
        MetadataEdit meta = new MetadataEdit(newDoc, false);
        meta.setResizeListener(resizeListener);

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


        meta.SetHiddenCategoryValue(category);
        panel.add(meta, DockPanel.CENTER);

        bttNext = new Button(Main.getTranslation("editor.btt_next"), new ClickListenerMetadata(meta));
        bttNext.addStyleName("gwt-ButtonOrange");
        bttNext.addStyleName("gwt-bttNext");

        bttCancel.addStyleName("gwt-bttCancel");

        BasicPanel bttPanel = new BasicPanel();
        panel.add(bttPanel, DockPanel.SOUTH);


        bttPanel.add(bttCancel);
        bttPanel.add(bttNext);
                                                                    
        onResize();
    }

    private void finishWizard() {
              final Editor editor =  Main.getSingleton().getEditor();
            CurrikiService.App.getInstance().checkVersion(editor.getCurrentAssetPageName(), editor.getCurrentAsset().getVersion(), new CurrikiAsyncCallback() {
                public void onFailure(Throwable caught) {
                    super.onFailure(caught);
                    // The action failed but we want to reload anyway in case something happened
                    editor.setCurrentAssetInvalid(true);
                    editor.setTreeContentInvalid(true);
                    editor.refreshState();
                }

                public void onSuccess(Object result) {
                    super.onSuccess(result);

                    if (!((Boolean) result).booleanValue()){
                        Window.alert(Main.getSingleton().getTranslator().getTranslation("checkversion.versionhaschanged"));
                        closeParent();
                        editor.setCurrentAssetInvalid(true);
                        editor.setTreeContentInvalid(true);
                        editor.refreshState();
                        return;
                    }

                    finishWizard2();
                };
            });
    }

    private void finishWizard2() {
           // finishWizard() should only be called when a new asset is created (does a rename from AssetTemp)
        CurrikiService.App.getInstance().finalizeAssetCreation(newDoc.getFullName(),
                Main.getSingleton().getEditor().getCurrentAssetPageName(), EditPage.getSingleton().getSelectedIndex() + 1,
                new CurrikiAsyncCallback(){
                    public void onFailure(Throwable caught) {
                        super.onFailure(caught);
                        Main.getSingleton().showError(caught);
                        closeParent();
                    }

                    public void onSuccess(Object result) {
                        super.onSuccess(result);
                        newDoc = (Document) result;
                        closeParent();
                        refreshEditor();
                    }
                });
    }

    public void refreshEditor() {
        Editor editor = Main.getSingleton().getEditor();
        editor.setSelectedDocumentName(newDoc.getFullName());
        if (category != null && category.equals(Constants.CATEGORY_COLLECTION)) {
            // If we added a folder we will switch it to main view
            editor.setCurrentAssetPageName(newDoc.getFullName());
        } else {
            // If we added a source asset we will select it and switch it to edit mode
            editor.setCurrentAssetInvalid(true);
            editor.setSelectedDocumentEditMode(true);
        }
        // Tree is invalid since we added an asset
        editor.setTreeContentInvalid(true);
        editor.refreshState();
    }

    public void initFileOrLink() {
        ClickListenerDocument nextCallback = new ClickListenerDocument(){
            public void onClick(Widget sender){
                // We should now have a file or link
                addFileDialog.hide();

                if (sender instanceof URLEntry){
                    // Was a LINK
                    category = Constants.CATEGORY_LINK;
                    newDoc = doc;
                    initMetadata(false);
                } else if (sender instanceof UploadWidget){
                    // Was a file
                    newDoc = doc;
                    initMetadata(false);
                } else if (sender instanceof VidiTalkUploadComponent){
                    // Was a VIDITalk Video
                    category = Constants.CATEGORY_VIDITALK_VIDEO;
                    newDoc = doc;
                    initMetadata(false);
                }
            }
        };
        ClickListener cancelCallback =  new ClickListener(){
            public void onClick(Widget sender){
                addFileDialog.hide();
                close();
            }
        };

        // we need to hide the previous dialog first
        addFileDialog = new AddFileDialog(Main.getSingleton().getEditor().getCurrentAssetPageName(), nextCallback, cancelCallback);
    }

    /*
    private void insertPageBreak() {
        category = Constants.PAGE_BREAK;
        CurrikiService.App.getInstance().insertSubAsset(Main.getSingleton().getEditor().getCurrentAssetPageName(), Constants.PAGE_BREAK, EditPage.getSingleton().getSelectedIndex() + 1, new CurrikiAsyncCallback() {

            public void onSuccess(Object result) {
                super.onSuccess(result);
                newDoc = (Document) result;
                refreshEditor();
            }
        });
    }
    */


    private void initBlankContentBlock(final long type) {
        String space = Main.getSingleton().getEditor().getCurrentSpace();
        if (space == null)
            space = Constants.TEMPORARY_ASSET_SPACE;
        CurrikiService.App.getInstance().createTextSourceAsset(Main.getSingleton().getEditor().getCurrentAssetPageName(), type, new CurrikiAsyncCallback() {

            public void onSuccess(Object object) {
                super.onSuccess(object);
                newDoc = (Document) object;

                category = Constants.TYPE_TEXT;
                if (type==Constants.TEXTASSET_TYPE_DIRECTION) {
                    // we should save the meta data and directly create the asset
                    CurrikiService.App.getInstance().updateMetadata(newDoc.getFullName(), false, new CurrikiAsyncCallback() {
                        public void onSuccess(Object result) {
                            super.onSuccess(result);
                            newDoc = (Document) result;
                            getCategory();

                            XObject assetObj = newDoc.getObject(Constants.ASSET_CLASS);
                            assetObj.set(Constants.ASSET_CATEGORY_PROPERTY, category);
                            assetObj.set(Constants.ASSET_DESCRIPTION_PROPERTY, "Direction Block");
                            CurrikiService.App.getInstance().saveObject(assetObj, new CurrikiAsyncCallback() {
                                public void onSuccess(Object result) {
                                    super.onSuccess(result);
                                    finishWizard();
                                }
                            });
                        }
                    });
                }
                else
                    initMetadata(false);
            }
        });
    }


    private void initFolder() {
        String space = Main.getSingleton().getEditor().getCurrentSpace();
        if (space == null)
            space = Constants.TEMPORARY_ASSET_SPACE;

        CurrikiService.App.getInstance().createTempCompositeAsset(Main.getSingleton().getEditor().getCurrentAssetPageName(), new CurrikiAsyncCallback() {

            public void onSuccess(Object object) {
                super.onSuccess(object);
                newDoc = (Document) object;

                category = Constants.CATEGORY_COLLECTION;
                initMetadata(false);
            }
        });
    }

    public void onClick(Widget sender) {
        if (sender == bttLink) {
            hideParentDialog();
            initFileOrLink();
        } else if (sender == bttBlankContent) {
            hideParentDialog();
            initBlankContentBlock(Constants.TEXTASSET_TYPE_TEXT);
        } else if (sender == bttHTMLContent) {
            hideParentDialog();
            initBlankContentBlock(Constants.TEXTASSET_TYPE_HTML);
        } else if (sender == bttDirectionContent) {
            hideParentDialog();
            initBlankContentBlock(Constants.TEXTASSET_TYPE_DIRECTION);
        } else if (sender == bttNewFolder) {
            hideParentDialog();
            initFolder();
        } else if (sender == bttExistingResource) {
            hideParentDialog();
            findResource();
        // Removed from EOU1 (1.6)
        /*
        } else if (sender == bttFromTemplate) {
            hideParentDialog();
            initFromTemplate();
        */
        }
    }

    public void setActive(Button button) {
        bttLink.removeStyleName("gwt-ButtonNav-active");
        bttBlankContent.removeStyleName("gwt-ButtonNav-active");
        bttHTMLContent.removeStyleName("gwt-ButtonNav-active");
        bttDirectionContent.removeStyleName("gwt-ButtonNav-active");
        bttNewFolder.removeStyleName("gwt-ButtonNav-active");
        bttExistingResource.removeStyleName("gwt-ButtonNav-active");
        // Removed from EOU1 (1.6)
        /*
        bttFromTemplate.removeStyleName("gwt-ButtonNav-active");
        */
        button.addStyleName("gwt-ButtonNav-active");
    }

    // Launch Find Existing resources Dialog
    private void findResource() {
        close();
        SearchPanel search = new SearchPanel(this);
        search.show();
    }

    // Launch From Template Dialog
    private void initFromTemplate() {
        AsyncCallback callback = new AsyncCallback(){
            public void onFailure(Throwable throwable) {
                chooseTemplateDialog.hide();
                close();
            }                                   

            public void onSuccess(Object result) {
                // We should now have a file or link
                chooseTemplateDialog.hide();
                // We retrieve a temporary document from the copy
                newDoc = (AssetDocument) result;
                // let's init the meta data with less work since we have retrieved the template
                initMetadataUI();
            }
        };
        chooseTemplateDialog = new ChooseTemplateDialog(Main.getSingleton().getEditor().getCurrentAssetPageName(), callback);
    }

    /**
     * If we add an existing asset we need to insert it and the reload our UI
     * @param page
     */
    public void addExistingResource(final String page){
        CurrikiService.App.getInstance().insertSubAsset(Main.getSingleton().getEditor().getCurrentAssetPageName(), page, EditPage.getSingleton().getSelectedIndex() + 1, new CurrikiAsyncCallback() {
            public void onSuccess(Object result) {
                super.onSuccess(result);
                Editor editor = Main.getSingleton().getEditor();
                editor.setCurrentAssetInvalid(true);
                editor.setSelectedDocumentName(page);
                // Tree is invalid since we added an asset
                editor.setTreeContentInvalid(true);
                editor.refreshState();
            }
        });
    }

    public void close(){
        closeParent();
    }

    private class WhatToAddButton extends Button{
        String key;
        public WhatToAddButton(String key, ClickListener callback){
            super(Main.getTranslation("addasset." + key), callback);
            this.key = key;
            addStyleName("gwt-ButtonNav");
            sinkEvents(Event.ONMOUSEOVER);
        }

        public void onBrowserEvent(Event event) {
            switch (DOM.eventGetType(event)) {
                case Event.ONMOUSEOVER:
                    setActive(this);
                    showDescription();
                    break;
            }

            super.onBrowserEvent(event);
        }

        public void showDescription(){
            String title = Main.getTranslation("addasset." + key);
            title = title.replaceAll("<br/>", "");
            helpGrid.setHTML(0, 0, title);
            helpGrid.setHTML(1, 0, Main.getTranslation("addasset.desc_" + key));
        }
    }

    public void initWhatToAdd() {
        panel.clear();
        setParentCaption(Main.getTranslation("addasset.what_would_you_like_to_add"));
        Grid grid = new Grid(8, 1);
        helpGrid = new Grid(2, 1);
        helpGrid.getCellFormatter().addStyleName(0, 0, "help-grid-head");
        helpGrid.getCellFormatter().addStyleName(1, 0, "help-grid-content");
        helpGrid.addStyleName("help-grid");


        // Separator: Existing element block
        Label label = new HTML(Main.getTranslation("addasset.existing_element"));
        label.addStyleName("curriki-subtitle");
        grid.setWidget(0, 0, label);

        // New resource: File of Link
        bttLink = new WhatToAddButton("file_or_link", this);
        grid.setWidget(1, 0, bttLink);
        ((WhatToAddButton)bttLink).showDescription();

        // Separator: New Element
        label = new HTML(Main.getTranslation("addasset.new_element"));
        label.addStyleName("curriki-subtitle");
        grid.setWidget(2, 0, label);

        // New resource: Blank content block
        bttBlankContent = new WhatToAddButton("blank_content_block", this);
        grid.setWidget(3, 0, bttBlankContent);

        // New resource: HTML content block
        bttHTMLContent = new WhatToAddButton("html_content_block", this);
        grid.setWidget(4, 0, bttHTMLContent);

        // New resource: Direction content block
        bttDirectionContent = new WhatToAddButton("direction_content_block", this);
        grid.setWidget(5, 0, bttDirectionContent);

        // New resource: Folder
        bttNewFolder = new WhatToAddButton("folder", this);
        grid.setWidget(6, 0, bttNewFolder);

        panel.add(grid, DockPanel.CENTER);
        panel.add(helpGrid, DockPanel.EAST);

        bttCancel = new Button(Main.getTranslation("editor.btt_cancel"), new ClickListener(){
            public void onClick(Widget widget) {
                closeParent();
            }
        });
        bttCancel.addStyleName("gwt-ButtonGrey");
        // bttCancel.addStyleName("curriki-btt-right");

        panel.add(bttCancel, DockPanel.SOUTH);                                                
        panel.setCellHorizontalAlignment(bttCancel, DockPanel.ALIGN_RIGHT);

        onResize();
    }

}
