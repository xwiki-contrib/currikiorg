/*
 * Copyright 2007, The Global Education and Learning Community,
 * and individual contributors as indicated by the contributors.txt.
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
package org.curriki.gwt.client.widgets.find;

import asquare.gwt.tk.client.ui.ModalDialog;
import asquare.gwt.tk.client.ui.behavior.TabFocusController;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.widgets.roundedpanel.RoundedPanel;

/**
 *  This is the main panel for the "Find" tool
 */
public class FindPanel extends ModalDialog {
    protected ResourceAdder wizard;
    protected Viewer viewer = null;

    public FindPanel() {
        initPanel();
        removeController(getController(TabFocusController.class));
    }

    public FindPanel(ResourceAdder addAssetWizard) {
        removeController(getController(TabFocusController.class));
        wizard = addAssetWizard;
        initPanel();
    }

    public FindPanel(ResourceAdder addAssetWizard, Viewer viewer) {
        wizard = addAssetWizard;
        this.viewer = viewer;
        initPanel();
    }

    private void initPanel() {
        setCaption(Main.getTranslation("find.find_resource_by"), false);
        setHeight("572");
        setContentMinWidth(785);
        addStyleName("find-panel");

        VerticalPanel selectorPanel = new VerticalPanel();
        SelectorPanel selector = new SelectorPanel();
        ResultsPanel results = new ResultsPanel();

        ClickListener cancelCallback = new ClickListener(){
            public void onClick(Widget sender){
                hide();
            }
        };
        results.init(wizard, cancelCallback, viewer);
        selector.init(results, cancelCallback);

        selectorPanel.addStyleName("find-selector");
        // selectorPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        selectorPanel.add(selector);

        RoundedPanel rp = new RoundedPanel();
        rp.addStyleName("find-selector-container");
        rp.add(selectorPanel);

        add(rp);
        add(results);
    }
}
