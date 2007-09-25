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
package org.curriki.gwt.client.search.panels;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.Main;

public class SelectorTogglePanel extends VerticalPanel implements ClickListener
{
    protected SelectorFilterPanel filters;
    protected HTML toggleWidget;

    public SelectorTogglePanel()
    {
        toggleWidget = new HTML();
        toggleWidget.addClickListener(this);

        add(toggleWidget);
    }

    public void setTogglePanel(SelectorFilterPanel filters)
    {
        this.filters = filters;
        doSetToggleWidget();
    }

    public void doToggle()
    {
        if (filters == null){
            return;
        }

        if (filters.isVisible()){
            filters.setVisible(false);
        } else {
            filters.setVisible(true);
        }

        doSetToggleWidget();
    }

    public void doSetToggleWidget()
    {
        if (filters == null){
            return;
        }
        
        if (filters.isVisible()){
            toggleWidget.setHTML(Main.getTranslation("search.instruction.adv.close"));
        } else {
            toggleWidget.setHTML(Main.getTranslation("search.instruction.adv.open"));
        }
    }

    public void onClick(Widget widget)
    {
        doToggle();
    }
}
