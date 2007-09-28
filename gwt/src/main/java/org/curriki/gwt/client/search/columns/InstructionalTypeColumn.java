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
package org.curriki.gwt.client.search.columns;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;

public class InstructionalTypeColumn extends ResultsColumn
{
    public InstructionalTypeColumn()
    {
        this.header = Main.getTranslation("search.results.col.ict");
        this.columnStyle = "results-ict-cell";
        this.sortBy = Constants.ASSET_CLASS+"."+Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY;
    }

    public InstructionalTypeColumn(String header, String columnStyle)
    {
        super(header, columnStyle);
    }

    public String getDisplayString(Document value)
    {
        String name = "";

        if (value.getObject(Constants.ASSET_CLASS) != null){
            value.use(Constants.ASSET_CLASS);
            if (value.get(Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY) != null){
                name = value.get(Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY);
            }
        }

        return name;
    }

    public Widget getDisplayWidget(Document value)
    {
        String name = "";
        FlowPanel ret = new FlowPanel();

        if (value.getObject(Constants.ASSET_CLASS) != null){
            value.use(Constants.ASSET_CLASS);
            if (value.get(Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY) != null){
                name = String.valueOf(value.getValue(Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY));
            }
        }

        if (name.indexOf("#--#") != -1){
            Image img = new Image(Constants.ICON_PATH+"ICTIcon-Multiple.gif");
            ret.add(img);
            ret.add(new Label(Main.getTranslation("search.results.col.ict.multiple")));
        } else  if (name.length() > 0){
            String icon = name.replaceFirst("_.*", "");
            if ((icon.length() > 0) && !icon.equals(name)){
                String iconTitle = Main.getTranslation("search.selector.ict."+icon);
                icon = icon.toUpperCase().substring(0, 1)+icon.substring(1);
                Image img = new Image(Constants.ICON_PATH+"ICTIcon-"+icon+".gif");
                img.setTitle(iconTitle);
                ret.add(img);
                
                name = Main.getTranslation("search.selector.ict."+name.replaceFirst("_", "."));
                ret.add(new Label(name));
            }
        }

        return ret;
    }
}
