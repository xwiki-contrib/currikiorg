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
import org.curriki.gwt.client.search.queries.Paginator;
import org.curriki.gwt.client.search.selectors.Selectable;
import org.curriki.gwt.client.search.history.SearcherHistory;
import org.curriki.gwt.client.search.editor.Viewer;
import org.curriki.gwt.client.search.editor.ResourceAdder;

public class SearcherPanel extends VerticalPanel implements ClickListener
{
    protected SelectorPanel selector;
    protected ResultsPanel results;
    protected PaginationPanel pagination;

    public SearcherPanel()
    {
        init(false);
    }

    public SearcherPanel(boolean fromCB)
    {
        init(fromCB);
    }

    public void init(boolean fromCB)
    {
        addStyleName("search-panel");
        if (!fromCB){
            VerticalPanel pTitle = new VerticalPanel();
            pTitle.addStyleName("search-top-titlebar");
            pTitle.add(new Label(Main.getTranslation("search.top_titlebar")));
            add(pTitle);
        }

        selector = new SelectorPanel();
        selector.addStyleName("search-selector");
        selector.addClickListener(this);
        add(selector);

        VerticalPanel rTitle = new VerticalPanel();
        rTitle.addStyleName("search-results-title");
        rTitle.add(new Label(Main.getTranslation("search.results_title")));
        add(rTitle);

        results = new ResultsPanel(fromCB);
        results.addStyleName("search-results");
        results.setSelector(selector);
        add(results);
        selector.addResultsScrollPanel(results.getScrollPanel());

        pagination = new PaginationPanel();
        pagination.addStyleName("search-pagination");
        results.setPaginator(pagination.getPaginator());
        pagination.setSearcher(results);
        add(pagination);
    }

    public void addHistory(SearcherHistory history){
        results.addHistory(history);
    }

    public DoesSearch getSearcher()
    {
        return results;
    }

    public Selectable getSelector(){
        return selector;
    }

    public Paginator getPaginator(){
        return pagination;
    }

    public void onClick(Widget widget)
    {
        if (widget instanceof SelectorPanel){
            pagination.setStart(1); // Reset to first page
            getSearcher().doSearch();
        }
    }

    public void setCancelCallback(ClickListener cancelCallback)
    {
        selector.setCancelCallback(cancelCallback);
        results.setCancelCallback(cancelCallback);
    }

    public void setViewer(Viewer viewer)
    {
        results.setViewer(viewer);
    }

    public void setResourceAdder(ResourceAdder wizard)
    {
        results.setResourceAdder(wizard);
    }
}
