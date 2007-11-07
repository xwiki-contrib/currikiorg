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
 * @author jeremi
 */
package org.curriki.gwt.client.wizard;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.ClickListenerMetadata;
import org.curriki.gwt.client.widgets.metadata.MetadataEdit;


public class CreateCompositeAssetWizard extends Wizard {

    private TextBox fieldTitle = new TextBox();
    private Button bttFinish;
    private String space = null;
    private Document newDoc = null;
    private Button bttNext = null;

    public CreateCompositeAssetWizard(){
        this(null);
    }

    public CreateCompositeAssetWizard(String space){
        this.space = space;
        initDocument();

        initWidget(new ScrollPanel(panel));
    }




   private void initDocument(){
        if (space == null)
            space = Main.getSingleton().getEditor().getCurrentSpace();
        panel.clear();
        CurrikiService.App.getInstance().createCompositeAsset(space, new LoadDocumentAsyncCallBack());
   }


    private class LoadDocumentAsyncCallBack extends CurrikiAsyncCallback {

        public void onSuccess(Object result) {
            super.onSuccess(result);
            newDoc = (Document) result;
            MetadataEdit meta = new MetadataEdit(newDoc, false);

            // Add an event handler to the form.
            meta.addFormHandler(new FormHandler() {

                public void onSubmit(FormSubmitEvent formSubmitEvent) {
                    Main.getSingleton().startLoading();
                }

                public void onSubmitComplete(FormSubmitCompleteEvent event) {
                    Main.getSingleton().finishLoading();
                    closeParent();
                    String editURL = Main.getTranslation("params.gwturl") + "page=" + newDoc.getFullName() + "&new=1";
                    Main.changeWindowHref(editURL);
                }
            });

            meta.setResizeListener(resizeListener);

            panel.add(meta, DockPanel.CENTER);

            bttNext = new Button(Main.getTranslation("editor.btt_finish"), new ClickListenerMetadata(meta));
            bttNext.addStyleName("gwt-ButtonOrange");

            HorizontalPanel buttonPanel = new HorizontalPanel();
            buttonPanel.addStyleName("metadata-edit-buttons");
            buttonPanel.add(bttNext);
            
            panel.add(buttonPanel, DockPanel.SOUTH);
            onResize();
        }
    }

    public int  getOffsetHeight(){
        return panel.getOffsetHeight();   
    }

    public int  getOffsetWidth(){
        return panel.getOffsetWidth();
    }
    
}
