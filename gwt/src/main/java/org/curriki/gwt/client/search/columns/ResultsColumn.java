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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.widgets.metadata.TooltipMouseListener;

abstract public class ResultsColumn implements ResultsColumnDisplayable
{
    protected String header = "";
    protected String columnStyle = "results-column";
    protected String sortBy;

    protected ResultsColumn()
    {
    }

    protected ResultsColumn(String header, String columnStyle)
    {
        this.header = header;
        this.columnStyle = columnStyle;
    }

    protected ResultsColumn(String header, String columnStyle, String sortBy)
    {
        this.header = header;
        this.columnStyle = columnStyle;
        this.sortBy = sortBy;
    }

    public void setSortBy(String sortBy){
        this.sortBy = sortBy;
    }

    public String getSortBy(){
        return sortBy;
    }

    public String getHeaderString()
    {
        return this.header;
    }

    public Widget getHeaderWidget()
    {
        if (sortBy != null && !sortBy.equals("")){
            return new SortableColumnHeader(header, sortBy);
        } else {
            return new Label(header);
        }
    }

    public String getColumnStyle()
    {
        return this.columnStyle;
    }

    public String getHeaderColumnStyle()
    {
        return this.columnStyle;
    }

    public String getDisplayString(Document value)
    {
        String content = "";
        return content;
    }

    public Widget getDisplayWidget(Document value)
    {
        return new Label(getDisplayString(value));
    }

    protected void addTooltip(SourcesMouseEvents item, String text) {
        PopupPanel popup = new PopupPanel(true);
        popup.setStyleName("search-description-popup");
        // popup.setWidth("300px");
        popup.add(new HTML(text));
        item.addMouseListener(new TooltipMouseListener(popup));
    }
}
