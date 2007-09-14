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
package org.curriki.gwt.client.search.selectors;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.HTML;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.widgets.metadata.TooltipMouseListener;

abstract public class TextInputSelector extends TextBox implements Selectable
{
    protected String fieldName;

    public TextInputSelector(){
        setMaxLength(255);
        setVisibleLength(25);
    }

    public TextInputSelector(String name)
    {
        this();
        setName(name);
    }

    public TextInputSelector(String name, String value)
    {
        this(name);
        this.setText(value);
    }

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
            filterName = getName();
        }
        return filterName;
    }

    public String getFilter(){
        String filter = "";
        if (getText().length() > 0){
            if (getFieldName() != null){
                if (getFieldName().length() == 0 || getFieldName().startsWith("__")){
                    filter = getText();
                } else {
                    filter = getFieldName()+":"+getText();
                }
            }
        }
        return filter;
    }

    public SelectionCollection getSelected(){
        SelectionCollection s = new SelectionCollection();
        s.put(getName(), getText());

        return s;
    }

    public void setSelected(SelectionCollection selection){
        if (selection.containsKey(getName())){
            setText(selection.get(getName()));
        }
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
