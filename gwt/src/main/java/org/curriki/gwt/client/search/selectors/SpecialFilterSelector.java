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

import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.Constants;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;

public class SpecialFilterSelector extends DropdownSingleSelector
{
    public SpecialFilterSelector() {
        super();
        setFieldName("__special");

        addOption(Main.getTranslation("search.selector.filters.none"), "");
        if (!Main.getSingleton().getUser().getFullName().equals(Constants.USER_XWIKI_GUEST)) {
            addOption(Main.getTranslation("search.selector.filters.mine"), "mine");
        }
        addOption(Main.getTranslation("search.selector.filters.collections"), "collections");
        addOption(Main.getTranslation("search.selector.filters.reviewed"), "reviewed");
    }

    public Widget getLabel()
    {
        HorizontalPanel p = new HorizontalPanel();
        p.add(new HTML(Main.getTranslation("search.selector.filters")));
        //p.add(getTooltip("filters"));
        return p;
    }

    public String getFilter()
    {
        String filter = "";
        String value = getValue(getSelectedIndex());
        if (value.length() > 0){
            if (value.equals("collections")){
                filter = "XWiki.CompositeAssetClass.type:collection";
            }
            if (value.equals("reviewed")){
                filter = "CRS.CurrikiReviewStatusClass.status:(60 OR 40 OR 20 OR 10)";
            }
            if (value.equals("mine")){
                filter = "creator:"+Main.getSingleton().getUser().getFullName(); // This is the full USERNAME, not realname
            }
        }

        return filter;
    }
}
