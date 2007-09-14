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
package org.curriki.gwt.client.pages;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window;
import org.curriki.gwt.client.*;
import org.curriki.gwt.client.editor.Editor;
import org.curriki.gwt.client.widgets.metadata.MetadataEdit;

public class MetadataPage extends AbstractPage {
    MetadataEdit meta = new MetadataEdit(true);
    Button bttSend = new Button(Main.getTranslation("editor.btt_save"));

    public MetadataPage(){
        panel.add(meta);
        panel.setStyleName("metadata-page");

        bttSend.addClickListener(new ClickListener(){
            public void onClick(Widget sender) {
                meta.submit();
            }
        });

        bttSend.addStyleName("metadata-save");

        meta.addFormHandler(new FormHandler(){
            public void onSubmit(FormSubmitEvent event) {
                Main.getSingleton().startLoading();
            }

            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                CurrikiService.App.getInstance().finishUpdateMetaData(Main.getSingleton().getEditor().getCurrentAssetPageName(), new CurrikiAsyncCallback(){
                    public void onSuccess(Object result) {
                        super.onSuccess(result);
                        Editor editor = Main.getSingleton().getEditor();
                        editor.setCurrentAssetInvalid(true);
                        editor.refreshState();
                    }
                });
                Main.getSingleton().finishLoading();
            }
        });

        panel.add(bttSend);


        initWidget(panel);
    }

    public boolean isSourceAssetPage() {
        return true;
    }
    
    public void init() {
        super.init();
        panel.remove(bttSend);
        if (Main.getSingleton().getEditor().getCurrentAsset().hasEditRight())
         panel.add(bttSend);
        meta.init(Main.getSingleton().getEditor().getCurrentAsset(), true);
    }
}
