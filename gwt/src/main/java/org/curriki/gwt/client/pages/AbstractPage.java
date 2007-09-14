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
package org.curriki.gwt.client.pages;

import com.google.gwt.user.client.ui.*;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.*;
import org.curriki.gwt.client.editor.Editor;


public class AbstractPage extends Composite {
    protected Panel panel = new VerticalPanel();
    protected String assetLoaded = null;

    public void init() {
        assetLoaded = Main.getSingleton().getEditor().getCurrentAssetPageName();
    }

    public void initIfSelected() {
        if (isSelectedTab()) {
            initIfNotLoaded();
        }
    }

    public void initIfNotLoaded() {
            if ((assetLoaded==null)||(!assetLoaded.equals(Main.getSingleton().getEditor().getCurrentAssetPageName()))) {
                init();
            }
    }


    public boolean isSelectedTab() {
        Widget currentWidget = ComponentsPage.getSingleton().getSelectedTab();
        return (this==currentWidget);
    }


    public boolean isSourceAssetPage() {
        return false;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
        if (this.equals(ComponentsPage.getSingleton().getPage(tabIndex))) {
            /*
            Editor editor = Main.getSingleton().getEditor();
            if (isSourceAssetPage()) {
                String currentAssetPageName = editor.getCurrentAssetPageName();
                String selectedPageName = editor.getSelectedDocumentName();
                // If the current asset loaded is not the selected asset in the tree
                // then we want the selected asset loaded so that we can show it's comments and metadata
                if (selectedPageName!=null) {
                    if ((!currentAssetPageName.equals(selectedPageName))&&(!selectedPageName.equals(""))) {
                        editor.changeCurrentAsset(selectedPageName);
                        return;
                    }
                }
            } else {
                AssetDocument currentAsset = (AssetDocument) editor.getCurrentAsset();
                // If the current asset is not composite then we want to load it's parent asset and select the current one
                if (!currentAsset.isComposite()) {
                    editor.setSelectedDocumentName(currentAsset.getFullName());
                    return;
                }
            }
            */
            initIfNotLoaded();
        }
    }

    public void resetCache() {
        assetLoaded = null;
    }

    public void refreshState() {
        assetLoaded = null;
        initIfSelected();
    }

    public String getAssetLoaded() {
        return assetLoaded;
    }

    /**
     * If we are informed that this asset is invalid then we should clear our cached content
     * @param currentAssetPageName
     */
    public void setCurrentAssetInvalid(String currentAssetPageName) {
        if (assetLoaded!=null) {
            if (assetLoaded.equals(currentAssetPageName))
             assetLoaded = null;
        }
    }

    public void resizeWindow() {
    }
}
