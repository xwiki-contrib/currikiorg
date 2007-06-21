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
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.*;
import org.curriki.gwt.client.editor.Editor;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


public class ComponentsPage extends Composite implements TabListener {
    protected com.google.gwt.user.client.ui.Panel panel = new VerticalPanel();
    final private TabPanel tabPanel = new TabPanel();
    private Map pages = new HashMap();
    private static ComponentsPage singleton;
    private boolean showAllTabs;

    public ComponentsPage(){
        singleton = this;
        panel.setStyleName("component-page");
        initWidget(panel);
    }

    public void init(){
        panel.clear();
        panel.add(tabPanel);
        // panel.setWidth("100%");
        // ((VerticalPanel)panel).setSpacing(4);
        loadPanels();
    }

    private void loadPanels(){
        showAllTabs = "1".equals(Main.getSingleton().getEditor().getParameter("alltabs"));

        AbstractPage editPage = new EditPage();
        tabPanel.add(editPage, Main.getTranslation("editor.edit_tab"));
        pages.put("edit", editPage);

        if (showAllTabs) {
            AbstractPage previewPage = new PreviewPage();
            tabPanel.add(previewPage, Main.getTranslation("editor.preview_tab"));
            pages.put("preview", previewPage);
        }

        AbstractPage metadataPage = new MetadataPage();
        tabPanel.add(metadataPage, Main.getTranslation("editor.metadata_tab"));
        pages.put("metadata", metadataPage);

        AbstractPage commentPage = new CommentPage();
        tabPanel.add(commentPage, getCommentTabName());
        pages.put("comment", commentPage);

        if (showAllTabs) {
            AbstractPage historyPage = new HistoryPage();
            tabPanel.add(historyPage, Main.getTranslation("editor.history_tab"));
            pages.put("history", historyPage);
        }
        tabPanel.selectTab(Constants.PAGE_EDIT);
        tabPanel.setStyleName("components-tabpanel");

        tabPanel.addTabListener(this);
    }

    /**
     * the index is the one of the tabpanel
     * @param tabIndex
     * @return
     */
    public AbstractPage getPage(int tabIndex){
        return (AbstractPage) tabPanel.getWidget(tabIndex);   
    }

    public void refreshState() {
        refreshCommentTabName();
        Iterator pageIt = pages.values().iterator();
        while (pageIt.hasNext()) {
            AbstractPage page = (AbstractPage) pageIt.next();
            page.refreshState();
        }
    }

    public int getCurrentCommentNumber() {
        Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
        if (currentAsset==null)
            return 0;
        else
            return currentAsset.getCommentsNumber();
    }

    private void refreshCommentTabName() {
        AbstractPage commentPage = (AbstractPage) pages.get("comment");
        if (commentPage!=null) {
            int index = tabPanel.getWidgetIndex(commentPage);
            int selectedIndex = tabPanel.getTabBar().getSelectedTab();
            tabPanel.remove(commentPage);
            tabPanel.insert(commentPage, getCommentTabName(), index);
            if (index==selectedIndex)
             tabPanel.selectTab(index);
        }
    }

    private String getCommentTabName() {
        return Main.getTranslation("editor.comment_tab") + " (" + getCurrentCommentNumber() + ")";
    }


    public static ComponentsPage getSingleton() {
        return singleton;
    }

    public AbstractPage getSelectedTab() {
        int index = tabPanel.getTabBar().getSelectedTab();
        if (index==-1)
         return null;
        else
         return (AbstractPage) tabPanel.getWidget(index);
    }

    public void switchPage(String pageName) {
        Widget widget = (Widget) pages.get(pageName);
        tabPanel.selectTab(tabPanel.getWidgetIndex(widget));
    }

    public void resetCache() {
        Iterator pageIt = pages.values().iterator();
        while (pageIt.hasNext()) {
            AbstractPage page = (AbstractPage) pageIt.next();
            page.resetCache();
        }
    }

    public boolean onBeforeTabSelected(SourcesTabEvents sourcesTabEvents, int i) {
        Editor editor = Main.getSingleton().getEditor();
        if (editor.isInEditMode()) {
            Window.alert(Main.getTranslation("editor.youareeditinganassetsaveyourworkfirst"));
            return false;
        }
        return true;
    }

    public void onTabSelected(SourcesTabEvents sourcesTabEvents, int index) {
        Editor editor = Main.getSingleton().getEditor();
        AbstractPage page = getPage(index);
        AssetDocument currentAsset = (AssetDocument) editor.getCurrentAsset();
        String currentAssetPageName = currentAsset.getFullName();
        if (!currentAssetPageName.equals(page.getAssetLoaded())) {
            if (!getSelectedTab().isSourceAssetPage()) {
                // If we are selecting the edit or preview tab then we need
                // to make sure that we have a composite asset loaded
                // If the current asset is not composite then we want to load it's parent asset and select the current one
                if (!currentAsset.isComposite()) {
                    String parentAssetPageName = (currentAsset==null) ? "" : currentAsset.getParent();
                    if ((parentAssetPageName==null)||(parentAssetPageName.equals(""))) {
                        // If we didn't have the parent it is probably because the source asset was in main view
                        // and the parent info is not stored anywhere. We need to find it in the tree
                        parentAssetPageName = editor.findParent(currentAssetPageName);
                    }
                    if ((parentAssetPageName!=null)&&(!parentAssetPageName.equals(""))) {
                        editor.setCurrentAssetPageName(parentAssetPageName);
                        editor.setSelectedDocumentName(currentAssetPageName);
                        editor.refreshState();
                        // If we have changed the editor the editor will call back our active page to refresh it
                        return;
                    }
                }
            } else {
                String selectedPageName = editor.getSelectedDocumentName();
                // If the current asset loaded is not the selected asset in the tree
                // then we want the selected asset loaded so that we can show it's comments and metadata
                if ((selectedPageName!=null)&&(!currentAssetPageName.equals(selectedPageName))&&(!selectedPageName.equals(""))) {
                    editor.setCurrentAssetPageName(selectedPageName);
                    editor.setSelectedDocumentName(selectedPageName);
                    editor.refreshState();
                    // If we have changed the editor the editor will call back our active page to refresh it
                    return;
                }
            }
        }
        // If we have not made any changes to the editor let's reload the active page
        getPage(index).initIfNotLoaded();
    }

    public void setCurrentAssetInvalid(String currentAssetPageName) {
        Iterator pageIt = pages.values().iterator();
        while (pageIt.hasNext()) {
            AbstractPage page = (AbstractPage) pageIt.next();
            page.setCurrentAssetInvalid(currentAssetPageName);
        }
    }

    public void resizeWindow() {
        Iterator pageIt = pages.values().iterator();
        while (pageIt.hasNext()) {
            AbstractPage page = (AbstractPage) pageIt.next();
            page.resizeWindow();
        }
    }
}
