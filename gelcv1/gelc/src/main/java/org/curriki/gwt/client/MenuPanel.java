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
 *
 */
package org.curriki.gwt.client;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.widgets.browseasset.AssetTreeItem;
import org.curriki.gwt.client.widgets.browseasset.BrowseAsset;
import org.curriki.gwt.client.pages.EditPage;
import org.curriki.gwt.client.pages.AbstractPage;
import org.curriki.gwt.client.editor.Editor;


public class MenuPanel extends Composite implements WindowResizeListener {
    private final VerticalPanel menuPanel = new VerticalPanel();
    private final ScrollPanel menuScrollPanel = new ScrollPanel();
    private final BrowseAsset assetTree = new BrowseAsset();
    private final Button bttAdd = new Button("Add");
    private static MenuPanel singleton;

    public MenuPanel() {

        Label label = new Label(Main.getTranslation("menu.table_of_content"));
        label.addStyleName("table-of-content");
        menuPanel.add(label);

        FlowPanel treePanel = new FlowPanel();
        assetTree.addStyleName("menu-left");
        treePanel.add(assetTree);

        bttAdd.addClickListener(new bttAddClickListener());
        bttAdd.addStyleName("menu-add");

        SimplePanel buttonPanel = new SimplePanel();
        buttonPanel.addStyleName("menu-left-button-panel");
        buttonPanel.add(bttAdd);
        treePanel.add(buttonPanel);

        menuPanel.addStyleName("menu-panel");
        menuScrollPanel.addStyleName("menu-scroll-panel");
        treePanel.addStyleName("menu-tree-panle");
        menuScrollPanel.add(treePanel);
        menuPanel.add(menuScrollPanel);
        singleton = this;

        initWidget(menuPanel);
        initTree();

        // set initial height and add a resize listener
        resizeWindow();
        Window.addWindowResizeListener(this);
    }

    public void initTree(){
        assetTree.addTreeListener(new TreeListener(){
            public void onTreeItemSelected(TreeItem item) {
                Editor editor = Main.getSingleton().getEditor();
                AssetTreeItem selectedItem = (AssetTreeItem) item;
                String selectedPageName = selectedItem.getPageName();
                AssetTreeItem parentItem = (AssetTreeItem) item.getParentItem();
                String parentPageName = (parentItem==null) ? "" : parentItem.getPageName();

                AbstractPage selectedTab = Main.getSingleton().getEditor().getSelectedTab();
                boolean sourceAssetTab = (selectedTab==null) ? true : selectedTab.isSourceAssetPage();

                // If the newly selected asset is already the one loaded in the editor then we should not change anything
                if (selectedPageName.equals(editor.getCurrentAssetPageName())) {
                    // we need to make sure the composite asset is selected
                    editor.setSelectedDocumentName(selectedPageName);
                    editor.refreshState();
                    return;
                }

                // If the currently selected item is the same then we should not change anything
                if (selectedPageName.equals(editor.getSelectedDocumentName())) {
                    return;
                }

                // What to do if the tab is a source asset (metadata, comments, history)
                if (sourceAssetTab) {
                    if (selectedPageName.equals(editor.getCurrentAssetPageName())) {
                        // if the currently selected page is already loaded in main view
                        // then we don't want to do anything
                        return;
                    } else {
                        if (editor.isInEditMode()) {
                            Window.alert(Main.getTranslation("editor.youareeditinganassetsaveyourworkfirst"));
                        } else {
                            // else we load it in main view
                            editor.setCurrentAssetPageName(selectedPageName);
                            editor.setSelectedDocumentName(selectedPageName);
                        }
                    }
                } else {
                    // What to do if the tab is a composite asset tab (edit and preview)
                    if ((parentItem!=null)&&(parentPageName.equals(editor.getCurrentAssetPageName())&&!selectedItem.getType().equals(Constants.CATEGORY_COLLECTION))) {
                        // If we have a source asset and the parent is already loaded then we only need to select
                        editor.setSelectedDocumentName(selectedPageName);
                    } else {
                        if (editor.isInEditMode()) {
                            Window.alert(Main.getTranslation("editor.youareeditinganassetsaveyourworkfirst"));
                        } else {

                            // If the parent is not loaded properly or if it is a collection we want to select the item
                            editor.setSelectedDocumentName(selectedPageName);
                            if (selectedItem.getType().equals(Constants.CATEGORY_COLLECTION)) {
                                // if it is a collection then we want to open the item in main view
                                editor.setCurrentAssetPageName(selectedPageName);
                            } else {
                                // if it is a source asset then we load the parent
                                editor.setCurrentAssetPageName(parentPageName);
                            }
                        }
                    }
                }
                editor.refreshState();
            }

            public void onTreeItemStateChanged(TreeItem item) {
                
            }
        });
    }

    public static MenuPanel getSingleton() {
        return singleton;
    }

    /*
    public void selectCurrentItem(String fullName) {
        assetTree.selectCurrentItem(fullName);
    } */

    public void onSelectedAssetChange(SelectedAssetChangeEvent evt) {
        assetTree.onSelectedAssetChange(evt);
    }

    public String findParent(String currentAssetPageName) {
        return assetTree.findParent(currentAssetPageName);
    }

    public boolean isAssetInTree(String currentAssetPageName) {
        return assetTree.isAssetInTree(currentAssetPageName);
    }

    public void updateAssetTitle(String fullName, String title) {
        assetTree.updateAssetTitle(fullName, title);
    }

    public void resizeWindow() {
        int absoluteTop = Main.getAbsoluteTop(menuScrollPanel);
        menuScrollPanel.setHeight((Window.getClientHeight() - absoluteTop) + "px");
    }

    public class TreeListenerCallback extends CurrikiAsyncCallback {
        private String docName;
        public TreeListenerCallback(String docName){
            super();
            this.docName = docName;        
        }


        public void onSuccess(Object result) {
            super.onSuccess(result);
            assetTree.selectItem(docName);
            assetTree.selectCurrentItem(Main.getSingleton().getEditor().getCurrentAsset().getFullName());
            // EditPage.getSingleton().selectAsset(docName);
        }
    }

    public class bttAddClickListener implements ClickListener{
        public void onClick(Widget sender) {
            if (Main.getSingleton().getEditor().isInEditMode()) {
                Window.alert(Main.getTranslation("editor.youareeditinganassetsaveyourworkfirst"));
                return;
            }

            Main.getSingleton().getEditor().launchInsertWizard();
        }
    }

    private boolean isCompositeAsset(Document doc){
        return doc.getObject(Constants.COMPOSITEASSET_CLASS) != null;
    }

    public void refreshTree(CurrikiAsyncCallback callback) {
        assetTree.initTree(Main.getSingleton().getEditor().getRootAssetPageName(), callback);
    }

    public void refreshAddButton() {
        if (!isCompositeAsset(Main.getSingleton().getEditor().getCurrentAsset()))
            disableAdd();
        else
            enableAdd();
    }

    public void refreshState() {
        refreshAddButton();
        assetTree.selectItem(Main.getSingleton().getEditor().getSelectedDocumentName());
    }

    public void disableAdd(){
        bttAdd.setEnabled(false);
    }

    public void enableAdd(){
        bttAdd.setEnabled(true);
    }

    /**
     * Window display functions
     * @param width
     * @param height
     */
    public void onWindowResized(int width, int height) {
        resizeWindow();
    }

}
