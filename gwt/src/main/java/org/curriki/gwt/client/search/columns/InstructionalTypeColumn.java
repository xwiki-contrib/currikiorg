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
        this.header = Main.getTranslation("Instructional Type");
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
        String name = getDisplayString(value);
        FlowPanel ret = new FlowPanel();

        if (name.matches("#--#")){
            name = "Multiple:";
        }

        if (name.length() > 0){
            String icon = name.replaceAll(":.*", "");
            name = name.replaceFirst(icon+":", "");
            if (icon.length() > 0) {
                Image img = new Image(Constants.ICON_PATH+"ICTIcon-"+icon+".png");
                img.setTitle(icon);
                ret.add(img);
            }
            ret.add(new Label(name));
        }

        return ret;
    }
}
