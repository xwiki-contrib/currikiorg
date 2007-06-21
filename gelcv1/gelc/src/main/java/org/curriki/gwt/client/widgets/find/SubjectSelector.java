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
package org.curriki.gwt.client.widgets.find;

import com.google.gwt.user.client.ui.ListBox;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;

import java.util.Iterator;
import java.util.List;

public class SubjectSelector extends ListBox {
    private static final String FRAMEWORK_CUSTOM_QUERY_PAGE = "GWT.GetDefaultFrameworkQuery";

    public SubjectSelector() {
        // We need to make a call to customQuery() from XWikiServiceImpl to get the list using the following query
        //select doc.fullName, doc.title, doc.parent
        //  from XWikiDocument as doc, BaseObject as obj
        //  where doc.fullName=obj.name and obj.className='XWiki.FrameworkItemClass' order by doc.title
//        CurrikiService.App.getInstance().customQuery(FRAMEWORK_CUSTOM_QUERY_PAGE, new LoadSelectorCallback(this));
        CurrikiService.App.getInstance().getDocuments(", BaseObject as obj " +
                "where doc.fullName=obj.name and obj.className='XWiki.FrameworkItemClass'" +
                " and doc.parent = 'FW_masterFramework.WebHome'" +
                " order by doc.title", 100, 0, true, new LoadSelectorCallback(this));
    }

    public class LoadSelectorCallback extends CurrikiAsyncCallback {
        ListBox list;

        public LoadSelectorCallback(ListBox list) {
           this.list = list;
        }

        public void onSuccess(Object object) {
            super.onSuccess(object);
            List results = (List) object;
            Iterator i = results.iterator();

            list.addItem("Any", "FW_masterFramework.WebHome");
            
            while (i.hasNext()){
                Document doc = (Document) i.next();
                list.addItem(doc.getTitle(), doc.getFullName());
            }
        }
    }
}
