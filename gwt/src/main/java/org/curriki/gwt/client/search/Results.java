/*
 * Copyright 2007, The Global Education and Learning Community,
 * and individual contributors as indicated by the contributors.txt.
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

import java.util.Iterator;
import java.util.List;

public class Results implements ResultsReceiver
{
    protected ResultsRenderer renderer;

    public void setRederer(ResultsRenderer renderer){
        this.renderer = renderer;
    }
    
    public void setResults(int hitcount, List documentList)
    {
        renderer.clear();
        
        Iterator i = documentList.iterator();
        while (i.hasNext()){
            Document doc = (Document) i.next();
            renderer.addRow(doc);
        }

        //TODO: Set pagination info
    }
}
