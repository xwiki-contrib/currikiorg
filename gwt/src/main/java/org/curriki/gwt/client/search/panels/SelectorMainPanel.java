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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.search.selectors.Selectable;
import org.curriki.gwt.client.search.selectors.SelectionCollection;
import org.curriki.gwt.client.search.selectors.SelectorCollection;
import org.curriki.gwt.client.search.selectors.TermSelector;
import org.curriki.gwt.client.search.selectors.TextInputSelector;

import java.util.Iterator;

public class SelectorMainPanel extends HorizontalPanel implements ChangeListener, ClickListener,
    Selectable, SourcesChangeEvents, SourcesClickEvents
{
    protected TextInputSelector terms;
    protected Button search;
    protected SelectorCollection selectors = new SelectorCollection();
    protected String fieldName;

    public SelectorMainPanel()
    {
        // Has Term selector and Search button
        terms = new TermSelector();
        terms.setFieldName("");
        terms.addChangeListener(this);
        selectors.add(terms);

        VerticalPanel pTerms = new VerticalPanel();
        pTerms.add(terms.getLabel());
        pTerms.add(terms);

        add(pTerms);

        search = new Button(Main.getTranslation("Search"));
        search.addClickListener(this);
        add(search);
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
            filter = " ("+filter+") ";
        }

        return filter;
    }

    public SelectionCollection getSelected()
    {
        SelectionCollection selected = new SelectionCollection();
        Iterator i = selectors.iterator();
        while (i.hasNext()){
            Selectable s = (Selectable) i.next();
            Iterator j = s.getSelected().keySet().iterator();
            while (j.hasNext()){
                String key = (String) j.next();
                selected.put(key, s.getSelected().get(key));
            }
        }

        return selected;
    }

    public void setSelected(SelectionCollection selection)
    {
        Iterator i = selectors.iterator();
        while (i.hasNext()){
            ((Selectable) i.next()).setSelected(selection);
        }
    }

    public void onChange(Widget widget)
    {
        if (widget instanceof Selectable) {
            changeListeners.fireChange(this);
        }
    }

    public void onClick(Widget widget)
    {
        clickListeners.fireClick(this);
    }

    private ChangeListenerCollection changeListeners;

    public void addChangeListener(ChangeListener changeListener)
    {
        if (changeListeners == null) {
            changeListeners = new ChangeListenerCollection();
        }
        changeListeners.add(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener)
    {
        if (changeListeners != null) {
            changeListeners.remove(changeListener);
        }
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
}
