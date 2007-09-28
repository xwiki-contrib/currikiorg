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
package org.curriki.gwt.client.search.editor;

import asquare.gwt.tk.client.ui.ModalDialog;
import asquare.gwt.tk.client.ui.behavior.TabFocusController;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.search.panels.SearcherPanel;

/**
 *  This is the main panel for the "Search" tool
 */
public class SearchPanel extends ModalDialog {
    protected ResourceAdder wizard;
    protected Viewer viewer = null;

    public SearchPanel() {
        initPanel();
        removeController(getController(TabFocusController.class));
    }

    public SearchPanel(ResourceAdder addAssetWizard) {
        removeController(getController(TabFocusController.class));
        wizard = addAssetWizard;
        initPanel();
    }

    public SearchPanel(ResourceAdder addAssetWizard, Viewer viewer) {
        removeController(getController(TabFocusController.class));
        wizard = addAssetWizard;
        this.viewer = viewer;
        initPanel();
    }

    private void initPanel() {
        setCaption(Main.getTranslation("search.top_titlebar"), false);
        setWidth("699");
        setHeight("484");
        addStyleName("search-cb-panel");

        ClickListener cancelCallback = new ClickListener(){
            public void onClick(Widget sender){
                hide();
            }
        };

        SearcherPanel search = new SearcherPanel(true);
        search.setViewer(viewer);
        search.setCancelCallback(cancelCallback);
        search.setResourceAdder(wizard);
        add(search);
    }
}