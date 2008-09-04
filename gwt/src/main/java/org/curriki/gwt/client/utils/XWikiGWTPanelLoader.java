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
package org.curriki.gwt.client.utils;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.widgets.basicpanel.BasicPanel;

public class XWikiGWTPanelLoader extends CurrikiAsyncCallback {
    Panel view;
    String wikiPage;

    public XWikiGWTPanelLoader(String wikiPage, Panel view){
        this.view = view;
        this.wikiPage = wikiPage;
    }

    public void onFailure(Throwable throwable) {
        super.onFailure(throwable);
        view.add(new HTML(Main.getTranslation("error.could_not_get_wiki_document") + " " + wikiPage + "."));
    }

    public void onSuccess(Object object) {
        super.onSuccess(object);
        String content = (String) object;

        if (content.length() == 0){
            view.add(new HTML(Main.getTranslation("error.could_not_get_wiki_document") + " " + wikiPage + "."));
        } else {
            view.add(new HTML(content));
        }
    }

    public static void loadWikiPage(String wikiPage, Panel view){
        CurrikiService.App.getInstance().getDocumentContent(wikiPage, true, new XWikiGWTPanelLoader(wikiPage, view));
    }
}
