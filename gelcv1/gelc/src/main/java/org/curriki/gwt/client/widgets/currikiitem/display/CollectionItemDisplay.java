package org.curriki.gwt.client.widgets.currikiitem.display;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.editor.Editor;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;
/*
 * Copyright 2006, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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
 * @author jeremi
 */

public class CollectionItemDisplay  extends AbstractItemDisplay {

    public CollectionItemDisplay(Document doc, CurrikiItem item) {
        super(doc, item);
        initDisplay(doc);
    }

    public String getType() {
        return Constants.TYPE_COLLECTION;
    }

    public void changeToEditMode() {
        Editor editor = Main.getSingleton().getEditor();
        editor.setCurrentAssetPageName(getDocumentFullName());
        editor.refreshState();
    }

    public void cancelEditMode() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean save() {
        return true;
    }

    public void onView() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onDocumentVersionChange() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void initDisplay(Document doc) {
//        Label label = new Label(Main.getTranslation("collection")){
//            public void onBrowserEvent(Event event) {
//                item.onBrowserEvent(event);
//            }
//        };
//        label.sinkEvents(Event.ONCLICK | Event.ONDBLCLICK);
//
//        panel.add(label);
        item.refreshItemInfos();
        item.switchHeaderButtonsToViewMode();
    }
}
