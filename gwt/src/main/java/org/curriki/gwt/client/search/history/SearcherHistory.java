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
package org.curriki.gwt.client.search.history;

import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Timer;
import org.curriki.gwt.client.search.queries.DoesSearch;
import org.curriki.gwt.client.search.queries.Paginator;
import org.curriki.gwt.client.search.selectors.Selectable;
import org.curriki.gwt.client.search.exceptions.InitializationNotReadyException;

public class SearcherHistory implements HistoryListener
{
    protected DoesSearch searcher;
    protected Selectable selector;
    protected Paginator paginator;
    protected ClientState state;
    protected boolean ignoreNextChange = false;
    protected String currentToken;

    public boolean isIgnoreNextChange()
    {
        return ignoreNextChange;
    }

    public void setIgnoreNextChange(boolean ignoreNextChange)
    {
        this.ignoreNextChange = ignoreNextChange;
    }

    public void onHistoryChanged(String token)
    {
        currentToken = token;
        if (!isIgnoreNextChange()){
            state.InitFromToken(token);
            try {
                loadState();
                if ((state.getValue("__terms").length() > 0) || state.getValue("__go").equals("1")) {
                    // Only do a search if terms are passed
                    searcher.doSearchFromHistory();
                }
            } catch (InitializationNotReadyException e){
                // Try again in a bit if we are not ready to initialize
                Timer t = new Timer(){
                    public void run(){
                        onHistoryChanged(currentToken);
                    }
                };
                t.schedule(500);
            }
        } else {
            setIgnoreNextChange(false);
        }
    }

    public String createToken(){
        saveState();
        return state.getHistoryToken();
    }

    public void addSearcher(DoesSearch searcher)
    {
        this.searcher = searcher;
    }

    public void addSelector(Selectable selector)
    {
        this.selector = selector;
    }

    public void addPaginator(Paginator paginator)
    {
        this.paginator = paginator;
    }

    public void addState(ClientState state)
    {
        this.state = state;
    }

    public void saveState(){
        if (searcher instanceof KeepsState){
            ((KeepsState) searcher).saveState(state);
        }
        if (selector instanceof KeepsState){
            ((KeepsState) selector).saveState(state);
        }
        if (paginator instanceof KeepsState){
            ((KeepsState) paginator).saveState(state);
        }
    }

    public void loadState() throws InitializationNotReadyException
    {
        if (searcher instanceof KeepsState){
            ((KeepsState) searcher).loadState(state);
        }
        if (selector instanceof KeepsState){
            ((KeepsState) selector).loadState(state);
        }
        if (paginator instanceof KeepsState){
            ((KeepsState) paginator).loadState(state);
        }
    }
}
