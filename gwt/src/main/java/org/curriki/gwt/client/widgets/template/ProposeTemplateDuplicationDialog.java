package org.curriki.gwt.client.widgets.template;

import asquare.gwt.tk.client.ui.ModalDialog;
import asquare.gwt.tk.client.ui.BasicPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.curriki.gwt.client.*;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;

import java.util.List;
/**
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software;you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation;either version2.1of
 * the License,or(at your option)any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software;if not,write to the Free
 * Software Foundation,Inc.,51 Franklin St,Fifth Floor,Boston,MA
 * 02110-1301 USA,or see the FSF site:http://www.fsf.org.
 *
 * @author ldubost
 */

public class ProposeTemplateDuplicationDialog extends ModalDialog {
    AsyncCallback nextCallback;
    CurrikiItem item;

    public ProposeTemplateDuplicationDialog(CurrikiItem item, AsyncCallback nextCallback) {
        this.nextCallback = nextCallback;
        this.item = item;
    }

    public void init(List templates){
        addStyleName("dialog-proposeduplication");
        setCaption(Main.getTranslation("Template Editing"), false);

        BasicPanel main = new BasicPanel();
        main.addStyleName("dialog-proposeduplication-content");

        BasicPanel text = new BasicPanel();
        text.addStyleName("dialog-proposeduplication-text");
        if (item.getItem().getDocument().hasEditRight()) {
            text.add(new HTML(Main.getTranslation("template.propose_duplication_text_editable")));
        } else {
            text.add(new HTML(Main.getTranslation("template.propose_duplication_text_noneditable")));
        }
        main.add(text);

        VerticalPanel actions = new VerticalPanel();
        actions.addStyleName("dialog-choosetemplate-actions");

        ClickListener cancelListener = new ClickListener(){
            public void onClick(Widget sender){
                nextCallback.onFailure(null);
            }
        };

        Button cancel = new Button(Main.getTranslation("editor.btt_Cancel"), cancelListener);
        cancel.addStyleName("dialog-cancel");
        cancel.addStyleName("dialog-proposeduplication-cancel");
        actions.add(cancel);

        ClickListener nextListener = new ClickListener(){
                    public void onClick(Widget sender){
                        nextCallback.onSuccess(null);
                    }
                };

        Button next = new Button(Main.getTranslation("editor.btt_next"), nextListener);
        cancel.addStyleName("dialog-next");
        cancel.addStyleName("dialog-proposeduplication-next");
        actions.add(next);

        main.add(actions);
        add(main);
        show();
    }
}
