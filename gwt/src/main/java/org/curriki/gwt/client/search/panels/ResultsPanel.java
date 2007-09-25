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
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.search.Results;
import org.curriki.gwt.client.search.columns.ActionColumn;
import org.curriki.gwt.client.search.columns.ContributorColumn;
import org.curriki.gwt.client.search.columns.InstructionalTypeColumn;
import org.curriki.gwt.client.search.columns.ResultsColumnDisplayable;
import org.curriki.gwt.client.search.columns.ReviewColumn;
import org.curriki.gwt.client.search.columns.TitleColumn;
import org.curriki.gwt.client.search.queries.DoesSearch;
import org.curriki.gwt.client.search.queries.LuceneAssetQuery;
import org.curriki.gwt.client.search.queries.Paginator;
import org.curriki.gwt.client.search.selectors.Selectable;
import org.curriki.gwt.client.Constants;

public class ResultsPanel extends FlowPanel implements DoesSearch, ResultsRenderer
{
    protected Results results;
    protected LuceneAssetQuery query;
    protected Paginator paginator;
    protected FlexTable g;
    protected ResultsColumnDisplayable[] columns = new ResultsColumnDisplayable[5];
    protected int columnCount = 4;
    protected int curRow = 0;
    protected Selectable selector; 

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

        //TODO: Set up empty layout
        g = new FlexTable();
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
            g.getFlexCellFormatter().addStyleName(curRow, i, columns[i].getHeaderColumnStyle());
            g.setWidget(curRow, i, columns[i].getHeaderWidget());
        }
        curRow++;
    }
    public void addRow(Document doc)
    {
        g.getRowFormatter().addStyleName(curRow, "find-results-table-result");
        for (int i=0; i<columnCount; i++){
            g.getFlexCellFormatter().addStyleName(curRow, i, columns[i].getColumnStyle());
            g.setWidget(curRow, i, columns[i].getDisplayWidget(doc));
        }
        curRow++;
    }

    public void setSelector(Selectable selector){
        this.selector = selector;
    }
}
