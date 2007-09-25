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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.widgets.metadata.TooltipMouseListener;

public class TitleColumn extends ResultsColumn
{
    protected int maxLength = 78;

    public TitleColumn()
    {
        this.header = Main.getTranslation("search.results.col.title");
        this.columnStyle = "results-title-cell";
        this.sortBy = Constants.ASSET_CLASS+"."+Constants.ASSET_TITLE_PROPERTY;
    }

    public TitleColumn(String header, String columnStyle)
    {
        super(header, columnStyle);
    }

    public String getDisplayString(Document value)
    {
        String name = (value.getTitle().length() > 0) ? value.getTitle() : value.getName();
        
        if (value.getObject(Constants.ASSET_CLASS) != null){
            value.use(Constants.ASSET_CLASS);
            if (value.get(Constants.ASSET_TITLE_PROPERTY) != null){
                name = value.get(Constants.ASSET_TITLE_PROPERTY);
                if (name.length() > maxLength){
                    name = name.substring(0, (maxLength-1))+"...";
                }
                if (name.length() > 0){
                }
            }
        }
        return name;
    }

    public Widget getDisplayWidget(Document value)
    {
        HTML nameCol = new HTML();
        String url = value.getViewURL();
        String name = (value.getTitle().length() > 0) ? value.getTitle() : value.getName();
        String desc = "";

        if (name.length() > maxLength){
            name = name.substring(0, (maxLength-1))+"...";
        }

        if (value.getObject(Constants.ASSET_CLASS) != null){
            value.use(Constants.ASSET_CLASS);
            if (value.get(Constants.ASSET_TITLE_PROPERTY) != null){
                name = value.get(Constants.ASSET_TITLE_PROPERTY);
                if (name.length() > maxLength){
                    name = name.substring(0, (maxLength-1))+"...";
                }
            }

            if (value.get(Constants.ASSET_DESCRIPTION_PROPERTY) != null){
                desc = value.get(Constants.ASSET_DESCRIPTION_PROPERTY);
            }
        }

        nameCol.setHTML("<a href=\""+url+"\">"+name+"</a>");
        if (desc.length() > 0){
            addTooltip(nameCol, "<b>Description:</b><br>"+desc);
        }

        return nameCol;
    }

    protected void addTooltip(SourcesMouseEvents item, String text) {
        PopupPanel popup = new PopupPanel(true);
        popup.setStyleName("search-title-description-popup");
        popup.setWidth("300px");
        popup.add(new HTML(text));
        item.addMouseListener(new TooltipMouseListener(popup, 234, (Widget) item));
    }
}
