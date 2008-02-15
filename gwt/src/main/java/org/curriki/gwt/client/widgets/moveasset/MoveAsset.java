package org.curriki.gwt.client.widgets.moveasset;

import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.editor.Editor;
import org.curriki.gwt.client.widgets.browseasset.AssetTreeItem;
import org.curriki.gwt.client.widgets.browseasset.BrowseAsset;
import org.curriki.gwt.client.widgets.browseasset.InsertHereTreeItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

public class MoveAsset extends BrowseAsset {
    private long fromPosition = -1;
    private String fromParent = null;
    private String assetName = null;
    private Widget parentDialog = null;

    public MoveAsset(){
        super();
        initListener();
    }

    public MoveAsset(String rootAsset, String assetName, String fromParent, long fromPosition){
        this(rootAsset, assetName, fromParent, fromPosition, null);
    }

    public MoveAsset(String rootAsset, String assetName, String fromParent, long fromPosition,
                     WindowResizeListener resizeListener){
        super(rootAsset, resizeListener);
        this.assetName = assetName;
        this.fromParent = fromParent;
        this.fromPosition = fromPosition;
        addStyleName("move-asset");
        initListener();
    }

    private void cleanTree(TreeItem parent){
        List toRemove = new ArrayList();
        for (int i = 0; i < parent.getChildCount(); i++){
            TreeItem item = parent.getChild(i);
            if (item instanceof InsertHereTreeItem){
                toRemove.add(item);
            }
            else
                cleanTree(item);
        }

        Iterator it = toRemove.iterator();
        while(it.hasNext()){
            TreeItem item = (TreeItem) it.next();
            parent.removeItem(item);
        }

    }


    private void initListener(){
        addTreeListener(new TreeListener(){
            public void onTreeItemSelected(TreeItem item) {
                TreeItem parent = item.getParentItem();
                if (item instanceof InsertHereTreeItem) {
                    int treePos = parent.getChildIndex(item);
                    long newPosition = ((InsertHereTreeItem) item).getPosition();
                    // if we have selected the one after the asset
                    /* if ((treePos > 1) && (parent.getChild(treePos - 2) instanceof InsertHereTreeItem)){
                        newPosition = ((AssetTreeItem)(parent.getChild(treePos - 1))).getIndex() + 1;
                    }
                    else{
                        newPosition = ((AssetTreeItem)(parent.getChild(treePos + 1))).getIndex();
                    }
                    */
                    final String newParent = ((AssetTreeItem)parent).getPageName();
                    final boolean isNewParentDifferent = !fromParent.equals(newParent);
                    moveAsset(newPosition, newParent, isNewParentDifferent);
                    return ;
                }

                cleanTree(rootItem);
                if (parent != null) {
                    int index = parent.getChildIndex(item);
                    List items = new ArrayList();
                    for (int i = 0; i < parent.getChildCount(); i++){
                        items.add(parent.getChild(i));
                    }
                    parent.removeItems();

                    int i = 0;
                    for (; i < index; i++){
                        parent.addItem((TreeItem) items.get(i));
                    }
                    parent.addItem(new InsertHereTreeItem(index));
                    AssetTreeItem assetTreeItem = (AssetTreeItem) items.get(i);
                    if (Constants.CATEGORY_COLLECTION.equals(assetTreeItem.getType())) {
                      assetTreeItem.addItem(new InsertHereTreeItem(assetTreeItem.getChildCount()));
                      assetTreeItem.getChild(0).setVisible(true);
                    }
                    parent.addItem(assetTreeItem);

                    parent.addItem(new InsertHereTreeItem(index+1));
                    for (i++; i < items.size(); i++){
                        parent.addItem((TreeItem) items.get(i));
                    }

                }                
            }

            private void moveAsset(final long newPosition, final String newParent, final boolean newParentDifferent) {
                            CurrikiService.App.getInstance().moveAsset(assetName, fromParent, fromPosition, newParent, newPosition, new CurrikiAsyncCallback(){

                                public void onSuccess(Object result) {
                                    super.onSuccess(result);
                                    // If parent has changed we need to reload the parent one
                                    // If the moved asset is a source asset then we need to select the source asset
                                    // If the moved asset is a folder then we need to select the parent folder
                                    Editor editor =  Main.getSingleton().getEditor();
                                    if (newParentDifferent) {
                                        // Parent has changed, switch to that new parent
                                        editor.setCurrentAssetPageName(newParent);
                                        // If it was not the asset selected then we will select the new parent asset
                                        // If not we don't need to change anything as the asset will be selected
                                        if (!editor.getSelectedDocumentName().equals(assetName)) {
                                            editor.setSelectedDocumentName(newParent);
                                        }
                                    } else {
                                        // If the parent has not changed force the current asset reload
                                        editor.setCurrentAssetInvalid(true);
                                    }
                                    // The tree is invalid with a move. We need to reload it.
                                    editor.setTreeContentInvalid(true);
                                    // Let's launch the refresh
                                    editor.refreshState();

                                    if (parentDialog instanceof MoveModalBox){
                                        ((MoveModalBox)parentDialog).hide();
                                    }
                                }
                            });
                        }


            public void onTreeItemStateChanged(TreeItem item) {

            }
        });
    }


    public void setParentDialog(Widget widget) {
        this.parentDialog = widget;
    }
}
