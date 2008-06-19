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
package org.curriki.gwt.client.search.panels;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.curriki.gwt.client.search.history.ClientState;
import org.curriki.gwt.client.search.history.KeepsState;
import org.curriki.gwt.client.search.selectors.Selectable;
import org.curriki.gwt.client.search.selectors.SelectorCollection;
import org.curriki.gwt.client.search.filters.CRSFilter;

import java.util.Iterator;

public class SelectorPanel extends VerticalPanel implements ChangeListener, ClickListener,
    Selectable, SourcesClickEvents, KeepsState
{
    protected SelectorMainPanel main;
    protected SelectorFilterPanel filters;
    protected SelectorTogglePanel bottom;
    protected SelectorCollection selectors = new SelectorCollection();
    protected String fieldName;

    public SelectorPanel()
    {
        main = new SelectorMainPanel();
        main.addStyleName("search-selector-main");
        main.addChangeListener(this);
        main.addClickListener(this);
        selectors.add(main);
        add(main);

        filters = new SelectorFilterPanel();
        filters.addStyleName("search-selector-filters");
        filters.addChangeListener(this);
        selectors.add(filters);
        add(filters);

        filters.setVisible(false);

        bottom = new SelectorTogglePanel();
        bottom.addStyleName("search-selector-bottom");
        bottom.setTogglePanel(filters);
        add(bottom);

        // Other selectors (non-displayed)
        selectors.add(new CRSFilter());
    }

    public Widget getLabel()
    {
        return null;
    }

    public void setFieldName(String name)
    {
        this.fieldName = name;
    }

    public String getFieldName()
    {
        String fieldName;
        if (this.fieldName != null){
            fieldName = this.fieldName;
        } else {
            fieldName = "";
        }
        return fieldName;
    }

    public String getFilter()
    {
        String filter = "";
        Iterator i = selectors.iterator();
        while (i.hasNext()){
            Selectable s = (Selectable) i.next();
            String f = s.getFilter();
            if (f != null && f.length() > 0){
                if (filter.length() > 0){
                    filter += " AND ";
                }
                filter += f;
            }
        }

        if (filter.length() > 0){
            filter = " "+filter+" ";
        }

        return filter;
    }

    public void loadState(ClientState state)
    {
        Iterator i = selectors.iterator();
        while (i.hasNext()){
            Object s = i.next();
            if (s instanceof KeepsState){
                ((KeepsState) s).loadState(state);
            }
        }
        bottom.loadState(state);
    }

    public void saveState(ClientState state)
    {
        Iterator i = selectors.iterator();
        while (i.hasNext()){
            Object s = i.next();
            if (s instanceof KeepsState){
                ((KeepsState) s).saveState(state);
            }
        }
        bottom.saveState(state);
    }

    public void onChange(Widget widget)
    {
        //TODO: Get changed values
        if (widget instanceof Selectable) {
            String filter = ((Selectable) widget).getFilter();

            //TODO: add filter change to list
        }
    }

    public void onClick(Widget widget)
    {
        clickListeners.fireClick(this);
    }

    private ClickListenerCollection clickListeners;

    public void addClickListener(ClickListener clickListener)
    {
        if (clickListeners == null) {
            clickListeners = new ClickListenerCollection();
        }
        clickListeners.add(clickListener);
    }

    public void removeClickListener(ClickListener clickListener)
    {
        if (clickListeners != null) {
            clickListeners.remove(clickListener);
        }
    }

    public void setCancelCallback(ClickListener cancelCallback)
    {
        main.setCancelCallback(cancelCallback);

        // If we have a cancel callback then we are in CB, so open selectors by default
        bottom.setToggleValue(true);
    }

    public void addResultsScrollPanel(ScrollPanel scrollPanel)
    {
        bottom.addResultsScrollPanel(scrollPanel);
    }
}
