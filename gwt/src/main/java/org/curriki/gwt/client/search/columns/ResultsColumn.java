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
package org.curriki.gwt.client.search.columns;

import com.xpn.xwiki.gwt.api.client.Document;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

abstract public class ResultsColumn implements ResultsColumnDisplayable
{
    protected String header = "";
    protected String columnStyle = "results-column";

    protected ResultsColumn()
    {
    }

    protected ResultsColumn(String header, String columnStyle)
    {
        this.header = header;
        this.columnStyle = columnStyle;
    }

    public String getHeaderString()
    {
        return this.header;
    }

    public Widget getHeaderWidget()
    {
        return new Label(this.header);
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
}
