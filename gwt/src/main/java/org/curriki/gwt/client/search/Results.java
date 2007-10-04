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

import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.search.panels.ResultsRenderer;
import org.curriki.gwt.client.search.queries.ResultsReceiver;
import org.curriki.gwt.client.search.queries.Paginator;

import java.util.Iterator;
import java.util.List;

public class Results implements ResultsReceiver
{
    protected ResultsRenderer renderer;
    protected Paginator paginator;

    public void setRederer(ResultsRenderer renderer){
        this.renderer = renderer;
    }
    
    public void setResults(int hitcount, List documentList)
    {
        int shownResults = 0;

        renderer.clear();
        
        Iterator i = documentList.iterator();
        while (i.hasNext()){
            Document doc = (Document) i.next();
            renderer.addRow(doc);
            shownResults++;
        }

        paginator.adjust(paginator.getFetchCount(), paginator.getStart(), hitcount);

        if (shownResults == 0 && hitcount > 0){
            renderer.addRowNoShowableResults();
        }

        if (hitcount == 0){
            renderer.addRowNoResults();
        }
    }

    public void setPaginator(Paginator paginator){
        this.paginator = paginator;
    }
}
