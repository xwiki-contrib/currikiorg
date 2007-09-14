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
 * @author dward
 *
 */
package org.curriki.gwt.client.widgets.siteadd;

import asquare.gwt.tk.client.ui.BasicPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;
import org.curriki.gwt.client.utils.Translator;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.widgets.browseasset.AssetItem;
import org.curriki.gwt.client.widgets.browseasset.AssetTreeItem;

import java.util.List;
import java.util.Iterator;

public class MyCollectionsPanel extends BasicPanel {
    Tree tree;
    BasicPanel treePanel;
    AsyncCallback countCallback;
    AssetTreeItem onlyItem = null;

    public MyCollectionsPanel(AsyncCallback countCallback){
        this.countCallback = countCallback;

        Label caption = new Label(Main.getTranslation("mycollections.title"));
        caption.addStyleName("selector-mycollections-caption");
        add(caption);

        treePanel = new BasicPanel();
        treePanel.addStyleName("selector-mycollections-tree");
        add(treePanel);

        tree = new Tree();
        tree.addStyleName("browse-asset-tree");
        tree.addStyleName("selector-mycollections-tree-list");
        tree.setImageBase(Main.getTranslation("params.iconsurl"));
        treePanel.add(tree);
        CurrikiService.App.getInstance().getCollections(new displayCollections());
    }

    public AssetTreeItem getSelectedItem(){
        if (onlyItem != null){
            return onlyItem;
        }
        return (AssetTreeItem) tree.getSelectedItem();
    }

    public class displayCollections extends CurrikiAsyncCallback {
        public void onFailure(Throwable throwable) {
            super.onFailure(throwable);
        }

        public void onSuccess(Object object) {
            super.onSuccess(object);
            if (object == null){
                Window.alert(Main.getTranslation("mycollections.no_collections_found"));
                return;
            }

            AssetItem collections = (AssetItem) object;
            List l = collections.getItems();
            Iterator i = l.iterator();
            int cnt = 0;

            while (i.hasNext()){
                cnt++;
                AssetTreeItem t = new AssetTreeItem((AssetItem) i.next());
                onlyItem = t;
                tree.addItem(t);
            }

            if (cnt != 1){
                onlyItem = null;
            }

            // Callback with count value now
            if (countCallback != null){
                countCallback.onSuccess(new Integer(cnt));
            }
        }
    }
}
