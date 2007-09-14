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
package org.curriki.gwt.client.search;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import org.curriki.gwt.client.search.panels.SearcherPanel;

public class Searcher
{
    private org.curriki.gwt.client.search.SearcherHistory history;

    public void init()
    {
        history = new org.curriki.gwt.client.search.SearcherHistory();

        String initToken = History.getToken();
        boolean doSearch = false;
        if (initToken.length() == 0){
            /* An empty token can be a state -- just "search page" */
            //initToken = "_";
            doSearch = true;
        }

        // History needs to track search parameters and what page we are on for results
        
        // Selector Panel
        // Needs Search Terms (always Open) + Search button
        // Sub-panel (with open/close)
        //  Subject, Level, Instructional Type
        //  File Type, Language, Special Filters

        // Results Panel
        // Column Headings (each sortable)
        //  Title, Instructional Type, Contributor, Review, + Action (in CB)
        // Paginatable -- with setting on how many items show per page
        //  Show which results are displayed (Results 110 - 120 of about 255)
        //  Have buttons "Previous" 11 12 13 ... 19 20 "Next"

        SearcherPanel main = new SearcherPanel();
        history.addSearcher(main.getSearcher());

        // Sets up defaults based on history
        history.onHistoryChanged(initToken);

        if (RootPanel.get("searchElement") != null){
            RootPanel.get("searchElement").add(main);
        } else {
            RootPanel.get().add(main);
        }

        History.addHistoryListener(history);

        if (doSearch){
            main.getSearcher().doSearch();
        }
    }
}
