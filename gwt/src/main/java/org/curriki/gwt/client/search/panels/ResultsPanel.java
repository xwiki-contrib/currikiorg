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

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.search.Results;
import org.curriki.gwt.client.search.columns.ActionColumn;
import org.curriki.gwt.client.search.columns.ContributorColumn;
import org.curriki.gwt.client.search.columns.InstructionalTypeColumn;
import org.curriki.gwt.client.search.columns.ResultsColumnDisplayable;
import org.curriki.gwt.client.search.columns.ReviewColumn;
import org.curriki.gwt.client.search.columns.SortableColumnHeader;
import org.curriki.gwt.client.search.columns.TitleColumn;
import org.curriki.gwt.client.search.queries.DoesSearch;
import org.curriki.gwt.client.search.queries.LuceneAssetQuery;
import org.curriki.gwt.client.search.queries.Paginator;
import org.curriki.gwt.client.search.selectors.Selectable;

public class ResultsPanel extends FlowPanel implements DoesSearch, ResultsRenderer, TableListener
{
    protected Results results;
    protected LuceneAssetQuery query;
    protected Paginator paginator;
    protected FlexTable g;
    protected ResultsColumnDisplayable[] columns = new ResultsColumnDisplayable[5];
    protected int columnCount = 4;
    protected int curRow = 0;
    protected Selectable selector;
    protected String sortBy;

    public ResultsPanel(){
        init(false);
    }

    public ResultsPanel(boolean action){
        if (action){
            columnCount = 5;
        }
        init(action);
    }

    public void init(boolean action){
        results = new Results();
        results.setRederer(this);

        columns[0] = new TitleColumn();
        columns[1] = new InstructionalTypeColumn();
        columns[2] = new ContributorColumn();
        columns[3] = new ReviewColumn();
        columns[4] = new ActionColumn();

        g = new FlexTable();
        g.addTableListener(this);
        addHeadings();
        
        add(g);
    }

    public void doSearch()
    {
        int start = 1;
        int count = Constants.DIALOG_FIND_FETCH_COUNT;

        if (results != null) {
            query = new LuceneAssetQuery();
            query.setReceiver(results);

            if (selector != null){
                query.setCriteria(selector.getFilter());
            }

            query.setSortBy(sortBy);

            if (paginator != null){
                query.setPaginator(paginator);
                query.doSearch();
            } else {
                query.doSearch(start, count);
            }
            
            //TODO: Add to history
        }
    }

    public void setPaginator(Paginator paginator)
    {
        this.paginator = paginator;
        results.setPaginator(paginator);
    }

    public void clear(){
        g.clear();
        curRow = 0;
        addHeadings();
    }

    public void addHeadings()
    {
        curRow = 0;
        g.getRowFormatter().addStyleName(curRow, "find-results-table-title");
        for (int i=0; i<columnCount; i++){
            g.getFlexCellFormatter().addStyleName(curRow, i, "find-results-column-header");
            g.getFlexCellFormatter().addStyleName(curRow, i, columns[i].getHeaderColumnStyle());
            Widget w = columns[i].getHeaderWidget();
            if (w instanceof SortableColumnHeader){
                SortableColumnHeader sw = (SortableColumnHeader) w;

                if (sw.getSortBy().equals(sortBy)){
                    g.getFlexCellFormatter().addStyleName(curRow, i, "find-results-column-header-sorted");
                } else {
                    g.getFlexCellFormatter().removeStyleName(curRow, i, "find-results-column-header-sorted");
                }
            }
            g.setWidget(curRow, i, w);
        }
        curRow++;
    }
    public void addRow(Document doc)
    {
        g.getRowFormatter().addStyleName(curRow, "find-results-table-result");
        for (int i=0; i<columnCount; i++){
            g.getFlexCellFormatter().addStyleName(curRow, i, "find-results-cell");
            g.getFlexCellFormatter().addStyleName(curRow, i, columns[i].getColumnStyle());
            g.setWidget(curRow, i, columns[i].getDisplayWidget(doc));
        }
        curRow++;
    }

    public void setSelector(Selectable selector){
        this.selector = selector;
    }

    public String getSortBy(){
        return sortBy;
    }

    public void onCellClicked(SourcesTableEvents sourcesTableEvents, int row, int cell)
    {
        if (row == 0){ // Only accept clicks on headings
            Widget widget = g.getWidget(row, cell);
            if ((widget != null) && (widget instanceof SortableColumnHeader)){
                SortableColumnHeader pressed = (SortableColumnHeader) widget;

                if (pressed.isSortable()){
                    String oldSortBy = sortBy;
                    sortBy = pressed.getSortBy();

                    if (oldSortBy.equals(sortBy)){
                        // TODO: We will want to reverse the sort once XWiki supports that
                    } else {
                        doSearch();
                    }
                } else {
                    // Ignore click if not sortable
                }
            }
        }
    }
}
