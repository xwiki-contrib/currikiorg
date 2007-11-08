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
package org.curriki.gwt.client.widgets.siteadd;

import asquare.gwt.tk.client.ui.BasicPanel;
import asquare.gwt.tk.client.ui.ModalDialog;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.XWikiGWTPanelLoader;
import org.curriki.gwt.client.widgets.browseasset.AssetTreeItem;

/*
 * ChooseCollectionDialog
 *
 * Contains
 *   Text at top describing why we are here (if more than 1 collection) - from a wiki page
 *   Tree selector of collections to select where to place the new resource
 *   Cancel and Next buttons at bottom right.
 */
public class ChooseCollectionDialog extends ModalDialog {
    ClickListener nextCallback;
    ClickListener cancelCallback;
    MyCollectionsPanel myCollections;
    String wikiPage;
    String titleTranslation;
    public int collectionsCount = -1;

    public ChooseCollectionDialog(ClickListener nextCallback, ClickListener cancelCallback){
        this.nextCallback = nextCallback;
        this.cancelCallback = cancelCallback;
        this.wikiPage = Constants.DIALOG_CHOOSE_COLLECTION;
        this.titleTranslation = "choosecollection.add_a_learning_resource";
        init();
    }

    public ChooseCollectionDialog(String titleTranslation, String wikiPage, ClickListener nextCallback, ClickListener cancelCallback){
        this.nextCallback = nextCallback;
        this.cancelCallback = cancelCallback;
        this.wikiPage = wikiPage;
        this.titleTranslation = titleTranslation;
        init();
    }

    public void init(){
        addStyleName("collections-dialog");
        //addController(new ModalDialog.DragStyleController(this));
        setCaption(Main.getTranslation(titleTranslation), true);
        setContentMinWidth(634);
        setContentMinHeight(450);

        BasicPanel main = new BasicPanel();
        main.addStyleName("collections-dialog-content");

        BasicPanel text = new BasicPanel();
        text.addStyleName("collections-dialog-text");
        XWikiGWTPanelLoader.loadWikiPage(wikiPage, text);

        Grid bottom = new Grid(1, 3);
        bottom.addStyleName("collections-dialog-bottom");
        bottom.getColumnFormatter().addStyleName(0, "collections-dialog-col1");
        bottom.getColumnFormatter().addStyleName(1, "collections-dialog-col2");
        bottom.getColumnFormatter().addStyleName(2, "collections-dialog-col3");


        BasicPanel chooser = new BasicPanel();
        chooser.addStyleName("collections-dialog-chooser");
        myCollections = new MyCollectionsPanel(new collectionCount(this));
        chooser.add(myCollections);
        

        BasicPanel actions = new BasicPanel();
        actions.addStyleName("collections-dialog-actions");

        Button cancel = new Button(Main.getTranslation("editor.btt_cancel"), cancelCallback);
        cancel.addStyleName("dialog-cancel");
        cancel.addStyleName("collections-dialog-cancel");
        actions.add(cancel);

        Button next = new Button(Main.getTranslation("editor.btt_next"), nextCallback);
        next.addStyleName("dialog-next");
        next.addStyleName("collections-dialog-next");
        actions.add(next);


        bottom.setText(0, 0, " ");
        bottom.setWidget(0, 1, chooser);
        bottom.setWidget(0, 2, actions);

        main.add(text);
        main.add(bottom);

        //while (collectionsCount == -1){
        // sleep();
        //}
        add(main);
    }

    public AssetTreeItem getSelectedItem(){
        return myCollections.getSelectedItem();
    }


    public class collectionCount extends CurrikiAsyncCallback {
        ModalDialog panel;

        public collectionCount(ModalDialog panel){
            this.panel = panel;
        }

        public void onSuccess(Object object) {
            super.onSuccess(object);
            collectionsCount = ((Integer) object).intValue();
            if (collectionsCount == 1){
                // Just take the default then, no choice
                nextCallback.onClick(null);
            } else if (panel != null){
                panel.show();
            }
        }
    }
}
