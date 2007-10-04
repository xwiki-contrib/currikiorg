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

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.search.Results;
import org.curriki.gwt.client.search.columns.ActionColumn;
import org.curriki.gwt.client.search.columns.ContributorColumn;
import org.curriki.gwt.client.search.columns.InstructionalTypeColumn;
import org.curriki.gwt.client.search.columns.ResultsColumnDisplayable;
import org.curriki.gwt.client.search.columns.ReviewColumn;
import org.curriki.gwt.client.search.columns.SortableColumnHeader;
import org.curriki.gwt.client.search.columns.TitleColumn;
import org.curriki.gwt.client.search.editor.ResourceAdder;
import org.curriki.gwt.client.search.editor.Viewer;
import org.curriki.gwt.client.search.history.ClientState;
import org.curriki.gwt.client.search.history.KeepsState;
import org.curriki.gwt.client.search.history.SearcherHistory;
import org.curriki.gwt.client.search.queries.DoesSearch;
import org.curriki.gwt.client.search.queries.LuceneAssetQuery;
import org.curriki.gwt.client.search.queries.Paginator;
import org.curriki.gwt.client.search.selectors.Selectable;

public class ResultsPanel extends FlowPanel implements DoesSearch, ResultsRenderer, TableListener,
    KeepsState
{
    protected SearcherHistory history;
    protected Results results;
    protected LuceneAssetQuery query;
    protected Paginator paginator;
    protected FlexTable g;
    protected ScrollPanel s;
    protected ResultsColumnDisplayable[] columns = new ResultsColumnDisplayable[5];
    protected int columnCount = 4;
    protected int curRow = 0;
    protected Selectable selector;
    protected String sortBy = "";
    protected ClickListener cancelCallback;
    protected Viewer viewer;
    protected ResourceAdder resourceAdder;
    protected hoverOnMouseover hoverMarker = new hoverOnMouseover();

    public ResultsPanel(){
        init(false);
    }

    public ResultsPanel(boolean fromCB){
        if (fromCB){
            columnCount = 5;
        }
        init(fromCB);
    }

    public void init(boolean fromCB){
        results = new Results();
        results.setRederer(this);

        columns[0] = new TitleColumn();
        columns[1] = new InstructionalTypeColumn();
        columns[2] = new ContributorColumn();
        columns[3] = new ReviewColumn();
        columns[4] = new ActionColumn();

        g = new FlexTable();
        g.addStyleName("find-results-table");
        g.addTableListener(this);
        addHeadings();

        if (fromCB){
            s = new ScrollPanel();
            s.addStyleName("find-results-scroller");

            s.add(g);
            add(s);
        } else {
            add(g);
        }
    }

    public void addHistory(SearcherHistory history){
        this.history = history;
    }

    public void doSearch() {
        if (history != null){
            history.setIgnoreNextChange(true);
            History.newItem(history.createToken());
        }
        doRealSearch();
    }

    public void doSearchFromHistory(){
        doRealSearch();
    }

    public void doRealSearch(){
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
                query.doSearch(1, Constants.DIALOG_FIND_FETCH_COUNT);
            }
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

                if (sw != null && sw.getSortBy() != null && !sw.getSortBy().equals("")){
                    sw.addMouseListener(hoverMarker);

                    if (sortBy != null && !sortBy.equals("") && sw.getSortBy().equals(sortBy)){
                        g.getFlexCellFormatter().addStyleName(curRow, i, "find-results-column-header-sorted");
                    } else {
                        g.getFlexCellFormatter().removeStyleName(curRow, i, "find-results-column-header-sorted");
                    }
                }
            }
            g.setWidget(curRow, i, w);
        }
        curRow++;
    }

    public void addRow(Document doc)
    {
        g.getRowFormatter().addStyleName(curRow, "find-results-table-result");
        if ((curRow % 2) == 0){
            g.getRowFormatter().addStyleName(curRow, "find-results-table-result-odd");
        }
        g.getFlexCellFormatter().setColSpan(curRow, 0, 1);
        for (int i=0; i<columnCount; i++){
            g.getFlexCellFormatter().addStyleName(curRow, i, "find-results-cell");
            g.getFlexCellFormatter().addStyleName(curRow, i, columns[i].getColumnStyle());
            g.setWidget(curRow, i, columns[i].getDisplayWidget(doc));
        }
        curRow++;
    }

    public void addRowNoResults()
    {
        g.getRowFormatter().addStyleName(curRow, "find-results-table-result");
        g.getFlexCellFormatter().setColSpan(curRow, 0, 4);
        HTML msg = new HTML(Main.getTranslation("search.noresults"));
        msg.addStyleName("search-noresults");
        g.setWidget(curRow, 0, msg);
        curRow++;
    }

    public void setSelector(Selectable selector){
        this.selector = selector;
    }

    public String getSortBy(){
        return sortBy;
    }

    public void loadState(ClientState state)
    {
        if (state.getValue(Constants.HISTORY_FIELD_SORTBY).length() > 0){
            sortBy = state.getValue(Constants.HISTORY_FIELD_SORTBY);
        } else {
            sortBy = "";
        }
    }

    public void saveState(ClientState state)
    {
        if (sortBy.length() > 0){
            state.setValue(Constants.HISTORY_FIELD_SORTBY, sortBy);
        } else {
            state.setValue(Constants.HISTORY_FIELD_SORTBY, "");
        }
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

    public void setCancelCallback(ClickListener cancelCallback)
    {
        this.cancelCallback = cancelCallback;
        if (columns[0] instanceof TitleColumn){
            TitleColumn title = (TitleColumn) columns[0];
            title.setCancelListener(cancelCallback);
        }
        if (columns[3] instanceof ReviewColumn){
            ReviewColumn review = (ReviewColumn) columns[3];
            review.setCancelListener(cancelCallback);
        }
        if (columns[4] instanceof ActionColumn){
            ActionColumn act = (ActionColumn) columns[4];
            act.setCancelListener(cancelCallback);
        }
    }

    public void setViewer(Viewer viewer)
    {
        this.viewer = viewer;
        if (columns[0] instanceof TitleColumn){
            TitleColumn title = (TitleColumn) columns[0];
            title.setViewer(viewer);
        }
        if (columns[3] instanceof ReviewColumn){
            ReviewColumn review = (ReviewColumn) columns[3];
            review.setViewer(viewer);
        }
    }

   public void setResourceAdder(ResourceAdder resourceAdder)
    {
        this.resourceAdder = resourceAdder;
        if (columns[0] instanceof TitleColumn){
            TitleColumn title = (TitleColumn) columns[0];
            title.setResourceAdder(resourceAdder);
            if (viewer == null){
                setViewer(title);
            }
        }
        if (columns[2] instanceof ContributorColumn){
            ContributorColumn creator = (ContributorColumn) columns[2];
            creator.setUseNewWindow(true);
        }
        if (columns[3] instanceof ReviewColumn){
            ReviewColumn review = (ReviewColumn) columns[3];
            review.setResourceAdder(resourceAdder);
            if (viewer == null){
                setViewer(review);
            }
        }
        if (columns[4] instanceof ActionColumn){
            ActionColumn act = (ActionColumn) columns[4];
            act.setResourceAdder(resourceAdder);
            columnCount = 5;
        }
    }

    public ScrollPanel getScrollPanel()
    {
        return s;
    }

    public class hoverOnMouseover extends MouseListenerAdapter {
        public void onMouseEnter(Widget w){
            w.addStyleName("mouse-over");
        }

        public void onMouseLeave(Widget w){
            w.removeStyleName("mouse-over");
        }
    }
}
