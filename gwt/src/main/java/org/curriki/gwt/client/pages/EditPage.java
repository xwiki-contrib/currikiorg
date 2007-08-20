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
package org.curriki.gwt.client.pages;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.*;
import org.curriki.gwt.client.utils.WindowUtils;
import org.curriki.gwt.client.editor.Editor;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItemImpl;
import org.curriki.gwt.client.widgets.currikiitem.display.AbstractItemDisplay;
import org.curriki.gwt.client.widgets.preview.PreviewDialog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import asquare.gwt.tk.client.ui.ModalDialog;


public class EditPage extends AbstractPage {
    Panel itemsPanel = new VerticalPanel();
    final Map assetsMap = new HashMap();
    private static EditPage singleton;
    private Label compositeAssetTitle;
    private long selectedIndex = -1;


    public static EditPage getSingleton() {
        return singleton;
    }

    public EditPage() {
        singleton = this;

        FlowPanel linkPanel = new FlowPanel();
        linkPanel.addStyleName("edit-panel-links");
        Hyperlink viewLink = new Hyperlink();
        viewLink.addStyleName("edit-panel-link-view");
        viewLink.addClickListener(new ClickListener() {
            public void onClick(Widget widget) {
                Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
                String url = currentAsset.getViewURL();
                Window.open(url, "_blank", "");
            }
        });
        viewLink.setText(Main.getTranslation("editor.view"));
        linkPanel.add(viewLink);
        Hyperlink printLink = new Hyperlink();
        printLink.addStyleName("edit-panel-link-print");
        printLink.addClickListener(new ClickListener() {
            public void onClick(Widget widget) {
                Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
                String url = currentAsset.getViewURL() + "?viewer=print";
                Window.open(url, "_blank", "");
            }
        });
        printLink.setText(Main.getTranslation("editor.print"));
        linkPanel.add(printLink);
        panel.add(linkPanel);

        compositeAssetTitle = new Label();
        compositeAssetTitle.addStyleName("edit-panel-title");
        panel.add(compositeAssetTitle);

        itemsPanel.setStyleName("items-panel");
        panel.add(itemsPanel);

        panel.setStyleName("edit-page");
        initWidget(panel);
    }

    public void init() {
        super.init();
        updateTitle();
        loadAsset(Main.getSingleton().getEditor().getCurrentAsset());
    }

    public void initIfSelected() {
        if (isSelectedTab()) {
            initIfNotLoaded();
            Iterator it = itemsPanel.iterator();
            Editor editor = Main.getSingleton().getEditor();
            while(it.hasNext()){
                Object widget = it.next();
                if (widget instanceof CurrikiItem){
                    CurrikiItem item = (CurrikiItem) widget;
                    if (item.getDocumentFullName().equals(editor.getSelectedDocumentName())) {
                        item.setSelected(true);
                        if (editor.getSelectedDocumentEditMode()==true) {
                            // This is only a temporary state to make the item go in edit mode
                            editor.setSelectedDocumentEditMode(false);
                            if (item.getType().equals(Constants.TYPE_TEXT))
                                item.onEditClick();
                        }
                        this.selectedIndex = item.getIndex();
                        Main.getSingleton().getEditor().ensureVisibleWidget((UIObject) item);
                    }
                    else
                        item.setSelected(false);
                }
            }
        } else {
            Iterator it = itemsPanel.iterator();
            Editor editor = Main.getSingleton().getEditor();
            while(it.hasNext()){
                Object widget = it.next();
                if (widget instanceof CurrikiItem){
                    CurrikiItem item = (CurrikiItem) widget;
                    item.refreshHeader();
                }
            }
        }
    }

    private void loadAsset(Document doc) {
        itemsPanel.clear();
        if (isCompositeAsset(doc)) {
            loadAssetCollection();
        } else {
            loadSourceAsset(doc);
        }
    }

    private boolean isCompositeAsset(Document doc){
        return doc.getObject(Constants.COMPOSITEASSET_CLASS) != null;
    }

    private void loadSourceAsset(Document doc) {
        loadSourceAsset(doc, -1);
    }

    private void loadSourceAsset(Document doc, long index) {
        CurrikiItemImpl item = new CurrikiItemImpl();
        item.setIndex(index);
        item.loadItemDisplay(doc);
        item.setParentAsset(doc.getParent());
        itemsPanel.add(item);
    }

    private void loadAssetCollection(){
        selectedIndex = -1;
        assetsMap.clear();
        List subAssets = Main.getSingleton().getEditor().getCurrentSubAssets();
        Iterator it = subAssets.iterator();
        for (int i = 0; it.hasNext(); i++){
            Object obj = it.next();
            if (obj == null)
                continue;
            if (obj instanceof Document) {
                loadSourceAsset((Document)obj, i);
            }
        }
    }


    public int getAssetCount() {
        return assetsMap.size();
    }


    private void updateTitle(){
        Document doc = Main.getSingleton().getEditor().getCurrentAsset();
        XObject obj = doc.getObject(Constants.ASSET_CLASS);
        compositeAssetTitle.setText((obj==null) ? "" : (String) obj.get(Constants.ASSET_TITLE_PROPERTY));
    }

    public long getSelectedIndex() {
        if (selectedIndex == -1) {
            Iterator it = itemsPanel.iterator();
            long max = -1;

            while(it.hasNext()){
                Object widget = it.next();
                if (widget instanceof CurrikiItem && ((CurrikiItem)widget).getIndex() > max)
                    max = ((CurrikiItem)widget).getIndex();
            }

            return max;
        }
        return selectedIndex;
    }

    public class ItemUpdaterAsyncCallback extends CurrikiAsyncCallback {
        AbstractItemDisplay itemDisplay;

        public ItemUpdaterAsyncCallback(AbstractItemDisplay itemDisplay) {
            super();
            this.itemDisplay = itemDisplay;
        }

        public void onSuccess(Object result) {
            super.onSuccess(result);
            if (!((Boolean) result).booleanValue()) {
                itemDisplay.onDocumentVersionChange();
            }
        }
    }

    public void registerAsset(AbstractItemDisplay itemDisplay) {
        assetsMap.put(itemDisplay.getDocumentFullName(), itemDisplay);
    }

    public void unregisterAsset(AbstractItemDisplay itemDisplay) {
        assetsMap.remove(itemDisplay.getDocumentFullName());
    }

    public void refreshState() {
        initIfSelected();
    }

    public boolean isInEditMode(){
        Iterator it = itemsPanel.iterator();
        while(it.hasNext()){
            Object widget = it.next();
            if (widget instanceof CurrikiItemImpl){
                CurrikiItem item = (CurrikiItem) widget;
                if (item.getStatus() == Constants.EDIT) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canEdit(CurrikiItem currikiItem){
        Iterator it = itemsPanel.iterator();
        while(it.hasNext()){
            Object widget = it.next();
            if (widget instanceof CurrikiItemImpl){
                CurrikiItem item = (CurrikiItem) widget;
                if (item.getStatus() == Constants.EDIT) {
                    if (item.isDirty()) {
                        askSaveChanges(item, currikiItem);
                        return false;
                    }
                    else {
                        item.onCancelClick();
                        return true;
                    }
                }
            }
        }
        return true;
    }

    private void askSaveChanges(CurrikiItem editedItem, CurrikiItem newlyEditedItem){
        final ModalDialog dialog = new ModalDialog();

        AskSaveChangesClickListener clickListener = new AskSaveChangesClickListener(editedItem, newlyEditedItem, dialog);

        Button bttCancel = new Button(Main.getTranslation("editor.btt_cancel"), clickListener);
        Button bttYes = new Button(Main.getTranslation("editor.btt_yes"), clickListener);
        Button bttNo = new Button(Main.getTranslation("editor.btt_no"), clickListener);

        clickListener.setBttCancel(bttCancel);
        clickListener.setBttNo(bttNo);
        clickListener.setBttYes(bttYes);

        dialog.setCaption(Main.getTranslation("editor.app_full_name"), false);

        Grid grid = new Grid(2,1);
        grid.setText(0, 0, Main.getTranslation("editor.do_you_want_to_save"));

        HorizontalPanel panel = new HorizontalPanel();

        panel.add(bttCancel);
        panel.add(bttYes);
        panel.add(bttNo);

        grid.setWidget(1, 0, panel);

        dialog.add(grid);

        dialog.show();

    }

    private class AskSaveChangesClickListener implements ClickListener{
        private Button bttCancel;
        private Button bttYes;
        private Button bttNo;
        private CurrikiItem editedItem;
        private CurrikiItem newlyEditedItem;
        private ModalDialog dialog;

        AskSaveChangesClickListener(CurrikiItem editedItem, CurrikiItem newlyEditedItem, ModalDialog dialog){
            this.editedItem = editedItem;
            this.newlyEditedItem = newlyEditedItem;
            this.dialog = dialog;
        }

        public void onClick(Widget sender) {
            if (sender.equals(bttYes)){
                editedItem.onSaveClick();
                newlyEditedItem.onEditClick();
            }
            else if (sender.equals(bttNo)){
                editedItem.onCancelClick();
                newlyEditedItem.onEditClick();
            }
            dialog.hide();
            dialog.removeFromParent();
        }

        public void setBttCancel(Button bttCancel) {
            this.bttCancel = bttCancel;
        }

        public void setBttYes(Button bttYes) {
            this.bttYes = bttYes;
        }

        public void setBttNo(Button bttNo) {
            this.bttNo = bttNo;
        }
    }


    /*
    public void selectAsset(String pageName){
        Iterator it = itemsPanel.iterator();
        while(it.hasNext()){
            Object widget = it.next();
            if (widget instanceof CurrikiItemImpl){
                CurrikiItem item = (CurrikiItem) widget;
                String name = item.getDocumentFullName();
                if (name != null && name.equals(pageName)) {
                    item.setSelected(true);
                    Main.getSingleton().getEditor().setSelectedDocumentName(pageName, true);
                    this.selectedIndex = item.getIndex();
                    Main.getSingleton().getEditor().ensureVisibleWidget((UIObject) item);
                }
                else
                    item.setSelected(false);
            }
        }
    }

    public void selectAssetAndEdit(String pageName) {
        Iterator it = itemsPanel.iterator();
        while(it.hasNext()){
            Object widget = it.next();
            if (widget instanceof CurrikiItemImpl){
                CurrikiItem item = (CurrikiItem) widget;
                String name = item.getDocumentFullName();
                if (name != null && name.equals(pageName)) {
                    item.setSelected(true);
                    if (item.getType().equals(Constants.TYPE_TEXT))
                        item.onEditClick();
                    Main.getSingleton().getEditor().setSelectedDocumentName(pageName, true);
                    this.selectedIndex = item.getIndex();
                }
                else
                    item.setSelected(false);
            }
        }
    }

    private void insertPageBreak(long index) {
        PageBreak item = new PageBreak();
        item.setIndex(index);
        itemsPanel.add(item);
    }
        */

}
