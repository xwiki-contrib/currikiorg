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
package org.curriki.gwt.client.search.queries;

import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class LuceneQuery implements DocumentSearcher
{
    protected String searchTerms = "";
    protected String sortBy;
    protected List results;
    protected int hitcount;
    protected ResultsReceiver receiver;
    protected Paginator paginator;
    protected int limit = Constants.DIALOG_FIND_FETCH_COUNT;

    public int getLimit(){
        return limit;
    }

    public void setLimit(int limit){
        this.limit = limit;
    }

    public void setCriteria(String criteria)
    {
        searchTerms = criteria;
    }

    public void setSortBy(String sortBy){
        this.sortBy = sortBy;
    }

    public void setReceiver(ResultsReceiver receiver)
    {
        this.receiver = receiver;
    }

    public void doSearch()
    {
        if (paginator != null){
            doSearch(paginator.getStart(), paginator.getFetchCount());
        } else {
            doSearch(1, limit);
        }
    }
    
    public void doSearch(int start, int count)
    {
        if ((sortBy == null) || (sortBy.length() == 0)){
            sortBy = "name";
        }
        CurrikiService.App.getInstance().luceneSearch(searchTerms, start, limit, sortBy, new LuceneQuery.populateResultsCallback());
    }

    public int getHitcount()
    {
        return hitcount;
    }

    public void setHitcount(int hitcount){
        this.hitcount = hitcount;
    }

    public List getResults()
    {
        return results;
    }

    public void setResults(List results){
        this.results = results;
    }

    public class populateResultsCallback extends CurrikiAsyncCallback
    {

        public populateResultsCallback() {
        }

        public void onFailure(Throwable throwable) {
            super.onFailure(throwable);

            setHitcount(0);
            setResults(null);

            if (receiver != null){
                receiver.setResults(hitcount, results);
            }
        }

        public void onSuccess(Object object) {
            super.onSuccess(object);
            List resultSet = new Vector();

            List results = (List) object;
            Iterator i = results.iterator();

            // First item in results is hitcount
            setHitcount(((Integer) i.next()).intValue());

            while (i.hasNext()){
                AssetDocumentWithOwnerName item = (AssetDocumentWithOwnerName) i.next();
                resultSet.add(item);
            }

            setResults(resultSet);

            if (receiver != null){
                receiver.setResults(getHitcount(), getResults());
            }
        }
    }

    public void setPaginator(Paginator paginator)
    {
        this.paginator = paginator;
    }
}
