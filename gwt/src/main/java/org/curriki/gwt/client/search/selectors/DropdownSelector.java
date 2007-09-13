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
package org.curriki.gwt.client.search.selectors;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.widgets.metadata.TooltipMouseListener;

import java.util.HashMap;
import java.util.Map;

abstract public class DropdownSelector extends ListBox implements Selectable
{
    protected Element selectbox;
    protected Map groups = new HashMap();
    protected String fieldName;

    public DropdownSelector(){
        this.selectbox = this.getElement();
    }

    public DropdownSelector(boolean isMultipleSelect){
        // super(isMultipleSelect); //TODO: Use this is for GWT 1.4
        setMultipleSelect(isMultipleSelect); //TODO: This should not be used for GWT 1.4
        
        this.selectbox = this.getElement();
    }

    public void addOption(String text, String value){
        DOM.appendChild(selectbox, createOption(text, value));
    }

    public void addGroup(String text){
        DOM.appendChild(selectbox, createGroup(text));
    }

    public void addGroupOption(String group, String text, String value){
        Element groupElement = (Element) this.groups.get(group);
        if (groupElement != null){
            DOM.appendChild(groupElement, createOption(text, value));
        } else {
            addOption(text, value);
        }
    }

    public Element createOption(String text, String value) {
        Element option = DOM.createElement("option");
        DOM.setInnerText(option, text);
        DOM.setAttribute(option, "value", value);
        return option;
    }

    public Element createGroup(String text) {
        Element group = DOM.createElement("optgroup");
        DOM.setAttribute(group, "label", text);
        this.groups.put(text, group);
        return group;
    }

    /**
     * Override getValue() so that we get the n'th item in the options array instead of the n'th child.
     */
    public String getValue(int index){
        return getOptionValue(this.getElement(), index);
    }

    public native String getOptionValue(Element elem, int index) /*-{
        return elem.options[index].value;
    }-*/;

    /**
     * Override isItemSelected() so that we get the n'th item in the options array instead of the n'th child
     */
    public boolean isItemSelected(int index) {
        return getItemSelected(this.getElement(), index);
    }

    public native boolean getItemSelected(Element elem, int index) /*-{
        return elem.options[index].selected;
    }-*/;

    public void setFieldName(String name)
    {
        this.fieldName = name;
    }

    public String getFieldName()
    {
        String filterName;
        if (this.fieldName != null){
            filterName = this.fieldName;
        } else {
            filterName = "";
        }
        return filterName;
    }

    public String getFilter()
    {
        String filter = "";
        String value = getValue(getSelectedIndex());
        if (value.length() > 0){
            if (getFieldName() != null){
                if (getFieldName().length() == 0 || getFieldName().startsWith("__")){
                    filter = value;
                } else {
                    filter = getFieldName()+":"+value;
                }
            }
        }

        return filter;
    }

    public SelectionCollection getSelected()
    {
        //TODO: get list of selected items
        return new SelectionCollection();
    }

    public void setSelected(SelectionCollection selection)
    {
        //TODO: set selected items
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Widget getTooltip(String name) {
        String txt = Main.getTranslation("search." + name + "_tooltip");
        Image image = new Image(Constants.ICON_PATH+"exclamation.png");
        PopupPanel popup = new PopupPanel(true);
        popup.setStyleName("search-tooltip-popup");
        // popup.setWidth("300px");
        popup.add(new HTML(txt));
        image.addMouseListener(new TooltipMouseListener(popup));
        return image;
    }
}
