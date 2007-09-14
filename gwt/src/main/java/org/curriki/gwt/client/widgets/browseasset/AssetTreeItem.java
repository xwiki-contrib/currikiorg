package org.curriki.gwt.client.widgets.browseasset;

import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.Element;
import com.xpn.xwiki.gwt.api.client.DOMUtils;

import java.util.Iterator;
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

public class AssetTreeItem extends TreeItem {
    private String pageName;
    private String type;

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    private long index;


    public AssetTreeItem(String pageName, long index){
        this.pageName = pageName;
        this.index = index;
        setText(this.pageName);
        setIndex(this.index);
        addStyleName("asset-tree-item");
    }

    public AssetTreeItem(AssetItem currItem){
        pageName = currItem.getAssetPage();
        setText(currItem.getText());
        setIndex(currItem.getIndex());
        setType(currItem.getType());
        setState(true, false);
        if (currItem.getItems() != null) {
            Iterator it = currItem.getItems().iterator();
            while(it.hasNext()){
                AssetItem item = (AssetItem) it.next();
                addItem(new AssetTreeItem(item));
            }
        }
        addStyleName("asset-tree-item");
    }

    public String getPageName() {
        return pageName;
    }

    public long getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCurrentItem(boolean selected){
        setStyleName(getContentElem(), "gwt-CurrentItem-selected", selected);
    }

    protected Element getContentElem(){
        return DOMUtils.getElementByClassName(getElement(), "gwt-TreeItem");

    }

}
