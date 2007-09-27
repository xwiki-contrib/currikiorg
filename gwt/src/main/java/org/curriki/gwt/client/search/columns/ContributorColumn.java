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
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.Constants;

public class ContributorColumn extends ResultsColumn
{
    protected boolean useNewWindow = false;

    public ContributorColumn()
    {
        this.header = Main.getTranslation("search.results.col.creator");
        this.columnStyle = "results-creator-cell";
        this.sortBy = "creator";
    }

    public ContributorColumn(String header, String columnStyle)
    {
        super(header, columnStyle);
    }

    public String getDisplayString(Document value)
    {
        String creator = "";
        if (value.getCreator() != null){
            creator = value.getCreator();
        }
        return creator;
    }

    public Widget getDisplayWidget(Document value)
    {
        HTML nameCol = new HTML();
        String name = getDisplayString(value);
        if (name.length() > 0){
            name = name.replaceFirst("XWiki.", "");
            String url = Constants.USER_URL_PREFIX+name;
            String target = "";
            if (useNewWindow){
                target = " target=\"AssetContributor\" ";
            }
            nameCol.setHTML("<a href=\""+url+"\""+target+">"+name+"</a>");
        }

        return nameCol;
    }

    public void setUseNewWindow(boolean useNewWindow){
        this.useNewWindow = useNewWindow;
    }
}
