/*
 * Copyright 2006, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.*;
import org.curriki.gwt.client.editor.Editor;
import org.curriki.gwt.client.widgets.find.FindPanel;
import org.curriki.gwt.client.widgets.find.ResourceAdder;
import org.curriki.gwt.client.widgets.metadata.MetadataEdit;
import org.curriki.gwt.client.widgets.addfile.AddFileDialog;
import org.curriki.gwt.client.widgets.upload.UploadWidget;
import org.curriki.gwt.client.widgets.template.ChooseTemplateDialog;
import org.curriki.gwt.client.pages.EditPage;
import org.curriki.gwt.client.utils.ClickListenerDocument;
import asquare.gwt.tk.client.ui.BasicPanel;


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
    //

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

        bttNext = new Button(Main.getTranslation("editor.btt_next"), new MetadataClickListener(meta));
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

    public class MetadataClickListener implements ClickListener {
        MetadataEdit meta;

        public MetadataClickListener(MetadataEdit meta) {
            this.meta = meta;
        }

        public void onClick(Widget sender) {
            meta.submit();
        }
    }

    public void initFileOrLink() {
        panel.getParent().getParent().getParent().setVisible(false);

        ClickListenerDocument nextCallback = new ClickListenerDocument(){
            public void onClick(Widget sender){
                // We should now have a file or link
                addFileDialog.hide();
                panel.getParent().getParent().getParent().setVisible(true);

                if (sender instanceof TextBox){
                    // Was a LINK
                    newDoc = doc;
                    initMetadata(false);
                } else if (sender instanceof UploadWidget){
                    // Was a file
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


    private void initBlankContentBlock(long html) {
        String space = Main.getSingleton().getEditor().getCurrentSpace();
        if (space == null)
            space = Constants.TEMPORARY_ASSET_SPACE;
        CurrikiService.App.getInstance().createTextSourceAsset(Main.getSingleton().getEditor().getCurrentAssetPageName(), html, new CurrikiAsyncCallback() {

            public void onSuccess(Object object) {
                super.onSuccess(object);
                newDoc = (Document) object;

                category = Constants.TYPE_TEXT;
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
        if (sender == bttLink)
            initFileOrLink();
        else if (sender == bttBlankContent)
            initBlankContentBlock(Constants.TEXTASSET_TYPE_TEXT);
        else if (sender == bttHTMLContent)
            initBlankContentBlock(Constants.TEXTASSET_TYPE_HTML);
        else if (sender == bttNewFolder)
            initFolder();
        else if (sender == bttExistingResource)
            findResource();
        else if (sender == bttFromTemplate)
            initFromTemplate();
    }

    public void setActive(Button button) {
        bttLink.removeStyleName("gwt-ButtonNav-active");
        bttBlankContent.removeStyleName("gwt-ButtonNav-active");
        bttHTMLContent.removeStyleName("gwt-ButtonNav-active");
        bttNewFolder.removeStyleName("gwt-ButtonNav-active");
        bttExistingResource.removeStyleName("gwt-ButtonNav-active");
        bttFromTemplate.removeStyleName("gwt-ButtonNav-active");
        button.addStyleName("gwt-ButtonNav-active");
    }

    // Launch Find Existing resources Dialog
    private void findResource() {
        close();
        FindPanel findPanel = new FindPanel(this);
        findPanel.show();
    }

    // Launch From Template Dialog
    private void initFromTemplate() {
        panel.getParent().getParent().getParent().setVisible(false);

        AsyncCallback callback = new AsyncCallback(){
            public void onFailure(Throwable throwable) {
                chooseTemplateDialog.hide();
                close();
            }                                   

            public void onSuccess(Object result) {
                // We should now have a file or link
                chooseTemplateDialog.hide();
                panel.getParent().getParent().getParent().setVisible(true);
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
            helpGrid.setText(0, 0, title);
            helpGrid.setText(1, 0, Main.getTranslation("addasset.desc_" + key));
        }
    }

    public void initWhatToAdd() {
        panel.clear();
        setParentCaption(Main.getTranslation("addasset.what_would_you_like_to_add"));
        Grid grid = new Grid(10, 1);
        helpGrid = new Grid(2, 1);
        helpGrid.getCellFormatter().addStyleName(0, 0, "help-grid-head");
        helpGrid.getCellFormatter().addStyleName(1, 0, "help-grid-content");
        helpGrid.addStyleName("help-grid");


        // Separator: Existing element block
        Label label = new Label(Main.getTranslation("addasset.existing_element"));
        label.addStyleName("curriki-subtitle");
        grid.setWidget(0, 0, label);

        // New resource: File of Link
        bttLink = new WhatToAddButton("file_or_link", this);
        grid.setWidget(1, 0, bttLink);
        ((WhatToAddButton)bttLink).showDescription();

        // New resource: Existing Resource
        bttExistingResource = new WhatToAddButton("existing_resource", this);
        grid.setWidget(2, 0, bttExistingResource);

        /* Not in the spec anymore
        bttSeparator = new WhatToAddButton("separator", this);
        grid.setWidget(2, 0, bttSeparator);      */

        // Separator: New Element
        label = new Label(Main.getTranslation("addasset.new_element"));
        label.addStyleName("curriki-subtitle");
        grid.setWidget(3, 0, label);

        // New resource: Blank content block
        bttBlankContent = new WhatToAddButton("blank_content_block", this);
        grid.setWidget(4, 0, bttBlankContent);

        // New resource: HTML content block
        bttHTMLContent = new WhatToAddButton("html_content_block", this);
        grid.setWidget(5, 0, bttHTMLContent);

        // New resource: Folder
        bttNewFolder = new WhatToAddButton("folder", this);
        grid.setWidget(6, 0, bttNewFolder);

        // New resource: Template
        bttFromTemplate = new WhatToAddButton("template", this);
        grid.setWidget(7, 0, bttFromTemplate);

        // Separator: New Element
        label = new Label(Main.getTranslation("addasset.formatting_element"));
        label.addStyleName("curriki-subtitle");
        grid.setWidget(8, 0, label);

        // Template is added a second time
        grid.setWidget(9, 0, bttFromTemplate);


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
