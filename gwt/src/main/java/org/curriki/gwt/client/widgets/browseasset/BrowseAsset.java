package org.curriki.gwt.client.widgets.browseasset;

import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Tree;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.SelectedAssetChangeEvent;
import org.curriki.gwt.client.SelectedAssetChangeListener;
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

public class BrowseAsset  extends Tree implements SelectedAssetChangeListener {
    protected AssetTreeItem rootItem = null;
    protected WindowResizeListener resizeListener = null;
    protected AssetTreeItem currentItem = null;

    public BrowseAsset(){
        super();
        addStyleName("browse-asset-tree");
    }

    public BrowseAsset(String rootAsset){
        super();
        initTree(rootAsset, null);
        addStyleName("browse-asset-tree");
    }

    public String getRootItemName(){
        return rootItem.getPageName();
    }

    public BrowseAsset(String rootAsset, WindowResizeListener resizeListener){
        super();
        this.resizeListener = resizeListener;
        initTree(rootAsset, null);
    }

    public boolean selectItem(String assetPageName){
        return selectItem(assetPageName, null, false);
    }

    public boolean selectCurrentItem(String assetPageName){
        return selectItem(assetPageName, null, true);
    }

    private boolean selectItem(String assetPageName, AssetTreeItem treeItem, boolean isCurrentItem){
        if (assetPageName == null)
            return false;

        if (treeItem == null){
            if (rootItem == null) {
                return false;
            }
            treeItem = (AssetTreeItem) getItem(0);
            if (treeItem == null) {
                return false;
            }
        }
        if (treeItem.getPageName().equals(assetPageName)) {
            if (isCurrentItem)
                setCurrentItem(treeItem);
            else {
                // For selection state
                treeItem.setSelected(true);
                // Mark it selected in the tree
                setSelectedItem(treeItem, false);

            }
            // make sure it is visible
            ensureSelectedItemVisible();
            return true;
        }

        for (int i = 0; i < treeItem.getChildCount(); i++){
            if (!(treeItem.getChild(i) instanceof AssetTreeItem))
                continue;
            AssetTreeItem child = (AssetTreeItem) treeItem.getChild(i);
            if(selectItem(assetPageName, child, isCurrentItem)) {
                return true;
            }
        }
        return false;
    }

    public void initTree(String rootAsset, CurrikiAsyncCallback callback){
        loadChildren(rootAsset, callback);
    }


    private void setCurrentItem(AssetTreeItem item){
        if (currentItem != null) {
            currentItem.setCurrentItem(false);
        }
        currentItem = item;
        currentItem.setCurrentItem(true);
    }

    /**
     * Force a reload of the tree
     */
    public void reload(CurrikiAsyncCallback callback){
        if (rootItem != null)
            loadChildren(rootItem.getPageName(), callback);
    }


    private void loadChildren(String rootAsset, final CurrikiAsyncCallback callback){
        CurrikiService.App.getInstance().getFullTreeItem(rootAsset, new CurrikiAsyncCallback(){

            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                if (callback!=null)
                 callback.onFailure(caught);
            }

            public void onSuccess(Object result) {
                super.onSuccess(result);
                AssetItem items = (AssetItem) result;
                rootItem = new AssetTreeItem(items);
                clear();
                addItem(rootItem);
                selectCurrentItem(Main.getSingleton().getEditor().getCurrentAsset().getFullName());
                rootItem.setState(true, false);
                if (resizeListener != null){
                    resizeListener.onWindowResized(getOffsetHeight(), getOffsetWidth());
                }
                if (callback!=null)
                 callback.onSuccess(result);
            }
        });
    }


    public void setResizeListener(WindowResizeListener resizeListener) {
        this.resizeListener = resizeListener;
    }

    public void onSelectedAssetChange(SelectedAssetChangeEvent evt) {
        selectItem(evt.getFullName());
    }

    public String findParent(String currentAssetPageName) {
        if (rootItem==null)
         return "";
        else
         return findParent(currentAssetPageName, rootItem);
    }

    private String findParent(String assetPageName, AssetTreeItem treeItem){
        if (assetPageName == null)
            return "";

        if (treeItem == null){
            treeItem = (AssetTreeItem) getItem(0);
            if (treeItem == null) {
                return "";
            }
        }
        if (treeItem.getPageName().equals(assetPageName)) {
            if (treeItem.getParentItem()!=null) {
              String pageName = ((AssetTreeItem)treeItem.getParentItem()).getPageName();
              if ((pageName!=null)&&(!pageName.equals("")))
                return pageName;
            }
            return "";
        }

        for (int i = 0; i < treeItem.getChildCount(); i++){
            if (!(treeItem.getChild(i) instanceof AssetTreeItem))
                continue;
            AssetTreeItem child = (AssetTreeItem) treeItem.getChild(i);
            String parent = findParent(assetPageName, child);
            if ((!parent.equals("")) && (parent!=null))
             return parent;
        }
        return "";
    }

    public boolean isAssetInTree(String currentAssetPageName) {
        if (rootItem==null)
         return false;
        else
         return isAssetInTree(currentAssetPageName, rootItem);
    }

    private boolean isAssetInTree(String assetPageName, AssetTreeItem treeItem){
        if (assetPageName == null)
            return false;

        if (treeItem == null){
            treeItem = (AssetTreeItem) getItem(0);
            if (treeItem == null) {
                return false;
            }
        }
        if (treeItem.getPageName().equals(assetPageName)) {
            return true;
        }

        for (int i = 0; i < treeItem.getChildCount(); i++){
            if (!(treeItem.getChild(i) instanceof AssetTreeItem))
                continue;
            AssetTreeItem child = (AssetTreeItem) treeItem.getChild(i);
            boolean parent = isAssetInTree(assetPageName, child);
            if (parent)
             return true;
        }
        return false;
    }

    public void updateAssetTitle(String fullName, String title) {
        if (rootItem!=null)
         updateAssetTitle(fullName, title, rootItem);
    }

    private void updateAssetTitle(String assetPageName, String title, AssetTreeItem treeItem){
        if (assetPageName == null)
            return;

        if (treeItem == null){
            treeItem = (AssetTreeItem) getItem(0);
            if (treeItem == null) {
            }
        }
        if (treeItem.getPageName().equals(assetPageName)) {
            treeItem.setHTML(title);
        }

        for (int i = 0; i < treeItem.getChildCount(); i++){
            if (!(treeItem.getChild(i) instanceof AssetTreeItem))
                continue;
            AssetTreeItem child = (AssetTreeItem) treeItem.getChild(i);
            updateAssetTitle(assetPageName, title, child);
        }
    }

}
