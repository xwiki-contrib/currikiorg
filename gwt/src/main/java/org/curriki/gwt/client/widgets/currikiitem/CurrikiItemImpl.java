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
package org.curriki.gwt.client.widgets.currikiitem;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.AssetDocument;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.editor.Editor;
import org.curriki.gwt.client.pages.ComponentsPage;
import org.curriki.gwt.client.pages.EditPage;
import org.curriki.gwt.client.widgets.currikiitem.display.AbstractItemDisplay;
import org.curriki.gwt.client.widgets.modaldialogbox.ChoiceDialog;
import org.curriki.gwt.client.widgets.modaldialogbox.NextCancelDialog;
import org.curriki.gwt.client.widgets.moveasset.MoveModalBox;
import org.curriki.gwt.client.wizard.DuplicateAssetWizard;


public class CurrikiItemImpl extends Composite implements CurrikiItem {
    private CurrikiItemHeader header = null;
    private VerticalPanel panel = new VerticalPanel();
    private Panel rpanel;
    private AbstractItemDisplay item = null;
    private boolean selected = false;
    private long index = -1;
    private String parentAsset = null;

    private NextCancelDialog proposeTemplateDuplicationDialog;
    private ChoiceDialog proposeEditDuplicationDialog;

    public CurrikiItemImpl(){
        init();
    }

    public void init() {
        header = new CurrikiItemHeader(this);
        panel.clear();
        panel.add(header);
        // panel.setWidth("100%");

        rpanel = new FlowPanel();
        rpanel.add(panel);
        setStandardStyles();
        addItemTypeStyles();
        initWidget(rpanel);
    }

    private void setStandardStyles() {
        rpanel.setStyleName("item-panel");
        panel.setStyleName("item-panel2");
        if (item!=null) {
            item.setStyleName("item-panel-content");
        }
    }

    private void addItemTypeStyles() {
        if (item!=null) {
            String type = item.getType();
            panel.addStyleName("item-" + type + "-panel");
            rpanel.addStyleName("item-" + type + "-panel2");
            item.addStyleName("item-" + type + "-panel-content");
            AssetDocument doc = item.getDocument();
            if (doc.isDirectionBlock()) {
                panel.addStyleName("item-direction-panel");
                rpanel.addStyleName("item-direction-panel2");
                item.addStyleName("item-direction-panel-content");
            }
        }
    }

    private void addSelectedStyles() {
        rpanel.addStyleName("item-panel-selected");
        panel.addStyleName("item-panel2-selected");
        if (item!=null) {
            item.addStyleName("item-panel-content-selected");
        }
    }

    private void addEditStyles() {
        rpanel.addStyleName("item-panel-edit");
        panel.addStyleName("item-panel2-edit");
        if (item!=null)
            item.addStyleName("item-panel-edit-content");
    }

    public void onBrowserEvent(Event event) {
        if (DOM.eventGetType(event) == Event.ONDBLCLICK)
        {
            onEditClick();    
        }
        else if (DOM.eventGetType(event) == Event.ONCLICK)
        {
            onSelectClick();
        }
    }


    public void refreshItemInfos(){
        refreshItemInfos(this.item);
    }

    public void refreshItemInfos(AbstractItemDisplay item) {
        if (this.item != null)
            panel.remove(this.item);
        this.item = item;

        header.setTitle(item.getTitle());

        panel.add(item);
    }

    public int getStatus() {
        return item.getStatus();
    }

    public String getParentAsset() {
        return parentAsset;
    }

    public void setParentAsset(String parent) {
        this.parentAsset = parent;
    }

    public void onEditClick() {
        AssetDocument doc = item.getDocument();
        int proposeDialog = 0;

        if (doc.isCurrikiTemplate()&&!doc.isParentCurrikiTemplate()) {
            proposeDialog = Constants.PROPOSE_DUPLICATE_TEMPLATE;
        } else if (!doc.getCreator().equals(Main.getSingleton().getUser().getFullName())){
            proposeDialog = Constants.PROPOSE_DUPLICATE_EDIT;
        }
        if (proposeDialog == Constants.PROPOSE_DUPLICATE_TEMPLATE){
            // Propose duplicating the template document in place
            proposeDuplicatingTemplate(new AsyncCallback() {
                public void onFailure(Throwable throwable) {
                    // If not edit right we had put the button only because it was a template
                    proposeTemplateDuplicationDialog.hide();
                    if (item.getDocument().hasEditRight())
                        changeToEditMode();
                }

                public void onSuccess(Object object) {
                    proposeTemplateDuplicationDialog.hide();

                    CurrikiService.App.getInstance().duplicateTemplateAsset(getParentAsset(), getDocumentFullName(), getIndex(), new CurrikiAsyncCallback() {
                        public void onFailure(Throwable caught) {
                            super.onFailure(caught);
                            // Action failed but we want to reload anyway to see if something happened
                            // The action worked we want to reload the current asset and the tree
                            Editor editor = Main.getSingleton().getEditor();
                            editor.setCurrentAssetInvalid(true);
                            editor.setTreeContentInvalid(true);
                            editor.refreshState();
                        }

                        public void onSuccess(Object result) {
                            super.onSuccess(result);

                            // Duplicate should have happened
                            String newPageName = (String) result;

                            // The action worked we want to reload the current asset and the tree
                            // We want to select the newly duplicated asset
                            Editor editor = Main.getSingleton().getEditor();
                            editor.setCurrentAssetInvalid(true);
                            editor.setTreeContentInvalid(true);
                            editor.setSelectedDocumentName(newPageName);
                            editor.setSelectedDocumentEditMode(true);
                            editor.refreshState();
                        }
                    });
                }
            });
        } else if (proposeDialog == Constants.PROPOSE_DUPLICATE_EDIT){
            // Unfortunately the two dialogues ask the quetion in different ways -- this one has 3 options -- so we can't reuse the code
            // Propose duplicating the resource about to be edited as it belongs to someone else
            proposeDuplicatingForEdit(doc.getCreator(), new AsyncCallback() {
                // Cancel was selected -- Don't do anything.
                public void onFailure(Throwable throwable) {
                    // Cancel -- so do nothing
                }

                public void onSuccess(Object object) {
                    // What was selected?
                    String result = (String) object;
                    if (result.equals("Edit")){
                        if (item.getDocument().hasEditRight()){
                            changeToEditMode();
                        }
                    } else if (result.equals("Copy")){
                        onDuplClick(true);
                    }
                }
            });
        } else {
            changeToEditMode();
        }
    }

    public void refreshHeader() {
        header.refreshHeader();
    }

    public void changeToEditMode() {
        if (!item.getDocument().hasEditRight()) {
            Window.alert(Main.getTranslation("asset.this_asset_is_not_editable"));
            return ;
        }
        header.setMode(header.MODE_EDIT);
        header.refreshHeader();
        item.changeToEditMode();
        onSelectClick();
    }

    private void proposeDuplicatingTemplate(AsyncCallback cb) {
        String titleText = "template.propose_duplication_title";
        String questionText;
        if (item.getDocument().hasEditRight())
            questionText = "template.propose_duplication_text_editable";
        else
            questionText = "template.propose_duplication_text_noneditable";

        proposeTemplateDuplicationDialog = new NextCancelDialog(titleText, questionText, "proposeduplication", cb);
    }

    private void proposeDuplicatingForEdit(String creator, final AsyncCallback cb) {
        String stylename = "proposedduplication";

        creator = creator.replaceFirst("XWiki.", "");
        String titleText = "edit.propose_duplication_title";
        String questionText = "edit.propose_duplication_text";

        Button next = new Button(Main.getTranslation("edit.propose_continue"), new ClickListener(){
            public void onClick(Widget widget)
            {
                cb.onSuccess("Edit");
            }
        });
        next.addStyleName("dialog-"+stylename+"-continue");
        Button copy = new Button(Main.getTranslation("edit.propose_copy"), new ClickListener(){
            public void onClick(Widget widget)
            {
                cb.onSuccess("Copy");
            }
        });
        copy.addStyleName("dialog-"+stylename+"-copy");
        Button cancel = new Button(Main.getTranslation("edit.propose_cancel"), new ClickListener(){
            public void onClick(Widget widget)
            {
                cb.onFailure(null);
            }
        });
        cancel.addStyleName("dialog-"+stylename+"-cancel");
        Button[] buttons = { next, copy, cancel };

        String[] args = {creator};
        proposeEditDuplicationDialog = new ChoiceDialog(Main.getTranslation(titleText), Main.getSingleton().getTranslator().getTranslation(questionText, args), buttons, stylename);
    }

    public void onSelectClick() {
        Editor editor = Main.getSingleton().getEditor();
        editor.setSelectedDocumentName(getDocumentFullName());
        editor.refreshState();
    }

    public void setSelected(boolean flag){
        if (flag) {
            if (getDocumentFullName() == null)
                return ;

            setStandardStyles();
            addSelectedStyles();
        } else {
            setStandardStyles();
        }
        if (getStatus()== Constants.EDIT) {
            addEditStyles();
        }
        addItemTypeStyles();
        selected = flag;
    }

    public boolean isSelected(){
        return selected;
    }

    public void onCancelClick() {
        clearEditStyles();
        header.setMode(header.MODE_VIEW);
        header.refreshHeader();
        item.cancelEditMode();
    }

    public void onSaveClick() {
        clearEditStyles();
        item.setStatus(Constants.UNKNOWN);
        header.setMode(header.MODE_EMPTY);
        header.refreshHeader();
        if (item.isDirty()) {
            if (!item.save()) {
                header.setMode(header.MODE_EDIT);
                header.refreshHeader();
            }
        }
        else
            onCancelClick();
    }

    private void clearEditStyles() {
        // clean up edit styles
        rpanel.removeStyleName("item-panel-edit");
        panel.removeStyleName("item-panel2-edit");
        if (item!=null)
            item.removeStyleName("item-panel-edit-content");
    }

    public boolean isDirty(){
        return item.isDirty();
    }

    public void onShowClick() {
        item.show();
        header.setShow(true);
        header.refreshHeader();
    }

    public void onHideClick() {
        header.setShow(false);
        header.refreshHeader();
        item.hide();
    }

    public void onEditMetadataClick() {
        Editor editor = Main.getSingleton().getEditor();
        editor.setSelectedDocumentName(item.getDocumentFullName());
        ComponentsPage.getSingleton().switchPage("metadata");
    }

    public void onRemoveClick() {
        Main.getSingleton().getEditor().removeAsset(getIndex());
    }

    public void onCommentClick() {
        Editor editor = Main.getSingleton().getEditor();
        editor.setSelectedDocumentName(item.getDocumentFullName());
        ComponentsPage.getSingleton().switchPage("comment");
    }

    public void onDuplClick() {
        onDuplClick(false);
    }

    public void onDuplClick(boolean markAsCopy) {
        boolean duplicateInPlace = false;
        boolean duplicateToCollection = true;

        AssetDocument doc = item.getDocument();
        if (doc.isCurrikiTemplate()) {
            // Our document is a template
            if (doc.isParentCurrikiTemplate()||!doc.isParentEditable()) {
                // If Parent is also a template then we can't duplicate in place
                duplicateInPlace = false;
            } else {
                duplicateInPlace = true;
            }
        } 

        if (duplicateInPlace) {
            proposeDuplicatingTemplate(new AsyncCallback() {
                public void onFailure(Throwable throwable) {
                    proposeDuplicateToCollection();
                }
                public void onSuccess(Object result) {

                    // Duplicate should have happened
                    String newPageName = (String) result;

                    // The action worked we want to reload the current asset and the tree
                    // We want to select the newly duplicated asset
                    Editor editor = Main.getSingleton().getEditor();
                    editor.setCurrentAssetInvalid(true);
                    editor.setTreeContentInvalid(true);
                    editor.setSelectedDocumentName(newPageName);
                    editor.setSelectedDocumentEditMode(true);
                    editor.refreshState();
                }
            });
        } else {
            proposeDuplicateToCollection(markAsCopy);
        }
    }

    public void proposeDuplicateToCollection() {
       proposeDuplicateToCollection(false);
    }
    
    public void proposeDuplicateToCollection(boolean markAsCopy) {
            // Let's launch the duplicate asset wizard
            new DuplicateAssetWizard(getDocumentFullName(), markAsCopy);
    }

    public void onMoveClick() {
        MoveModalBox box = new MoveModalBox(Main.getSingleton().getEditor().getRootAssetPageName(), getItem().getDocumentFullName(), getParentAsset(), getIndex());
        box.show();
    }

    public String getType() {
        if (item==null)
         return Constants.TYPE_UNDEFINED;
        else
         return item.getType();
    }

    public void switchHeaderButtonsToViewMode(){
        header.setMode(header.MODE_VIEW);
        header.refreshHeader();
    }

    public void loadItemDisplay(Document doc){
        // if (currikiitem == null)
        //    currikiitem = this;
        /*
        if (doc.getObject(Constants.COMPOSITEASSET_CLASS) != null) {
            rpanel.setStyleName("item-composite-panel");
            panel.setStyleName("item-composite-panel2");
            if (item!=null)
                item.setStyleName("item-composite-panel-content");
        }
        */
        item = AbstractItemDisplay.loadItemDisplay(doc, this);
        EditPage.getSingleton().registerAsset(item);
    }

    public void loadItemDisplay(String fullName){
        CurrikiService.App.getInstance().getDocument(fullName, true, true, new CurrikiAsyncCallback(){

            public void onSuccess(Object result) {
                super.onSuccess(result);
                loadItemDisplay((Document)result);
            }
        });
    }

    public String getDocumentFullName(){
        if (item != null)
            return item.getDocumentFullName();
        return null;
    }

    public AbstractItemDisplay getItem() {
        return item;
    }

    public void setItem(AbstractItemDisplay item) {
        this.item = item;
    }

    public void setTitle(String title){

    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

}
