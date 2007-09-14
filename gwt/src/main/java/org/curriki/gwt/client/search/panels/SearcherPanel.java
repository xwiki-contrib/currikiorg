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
package org.curriki.gwt.client.search.panels;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.search.queries.DoesSearch;

public class SearcherPanel extends VerticalPanel implements ClickListener
{
    protected SelectorPanel selector;
    protected ResultsPanel results;
    protected PaginationPanel pagination;

    public SearcherPanel()
    {
        VerticalPanel pTitle = new VerticalPanel();
        pTitle.addStyleName("search-top-titlebar");
        pTitle.add(new Label(Main.getTranslation("search.top_titlebar")));
        add(pTitle);

        selector = new SelectorPanel();
        selector.addClickListener(this);
        add(selector);

        VerticalPanel rTitle = new VerticalPanel();
        rTitle.addStyleName("search-results-title");
        rTitle.add(new Label(Main.getTranslation("search.results_title")));
        add(rTitle);

        results = new ResultsPanel();
        results.addSelector(selector);
        add(results);

        pagination = new PaginationPanel();
        results.addPaginator(pagination);
        add(pagination);
    }

    public DoesSearch getSearcher()
    {
        return results;
    }

    public void onClick(Widget widget)
    {
        if (widget instanceof SelectorPanel){
            getSearcher().doSearch();
        }
    }
}
