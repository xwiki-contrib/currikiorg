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

import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.Constants;
import com.xpn.xwiki.gwt.api.client.Document;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTML;

public class ReviewColumn extends ResultsColumn
{
    public ReviewColumn()
    {
        this.header = Main.getTranslation("Review");
    }

    public ReviewColumn(String header, String columnStyle)
    {
        super(header, columnStyle);
    }

    public String getDisplayString(Document value)
    {
        //TODO: Where do we get this value?
        return "1";
    }

    public Widget getDisplayWidget(Document value)
    {
        //TODO: Where do we get this value?
        String icon = "1";
        icon = "<img src=\""+ Constants.ICON_PATH+"CRS"+icon+".png\" />";
        return new HTML(icon);
    }
}
