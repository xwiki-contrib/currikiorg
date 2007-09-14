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

import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.search.selectors.SelectionCollection;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class LuceneQuery implements DocumentSearcher
{
    protected String searchTerms = "";
    protected List results;
    protected int hitcount;
    protected ResultsReceiver receiver;
    protected int limit = Constants.DIALOG_FIND_FETCH_COUNT;

    public int getLimit(){
        return limit;
    }

    public void setLimit(int limit){
        this.limit = limit;
    }

    public void setCriteria(SelectionCollection criteria)
    {
        searchTerms = "";
        Iterator i = criteria.keySet().iterator();
        while (i.hasNext()){
            String key = (String) i.next();
            String value = criteria.get(key);

            if (value.length() > 0){
                if (key.length() > 0){
                    searchTerms += " AND "+key+":"+value;
                } else {
                    searchTerms += " AND "+value;
                }
            }
        }
    }

    public void setCriteria(String criteria)
    {
        searchTerms = criteria;
    }

    public void setReceiver(ResultsReceiver receiver)
    {
        this.receiver = receiver;
    }

    public void doSearch(int start, int count)
    {
        CurrikiService.App.getInstance().luceneSearch(searchTerms, start, limit, new LuceneQuery.populateResultsCallback());
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

        public void onSuccess(Object
            object) {
            super.onSuccess(object);
            List resultSet = new Vector();

            List results = (List) object;
            Iterator i = results.iterator();

            // First item in results is hitcount
            setHitcount(((Integer) i.next()).intValue());

            while (i.hasNext()){
                Document item = (Document) i.next();
                resultSet.add(item);
            }

            setResults(resultSet);

            if (receiver != null){
                receiver.setResults(getHitcount(), getResults());
            }
        }
    }
}
