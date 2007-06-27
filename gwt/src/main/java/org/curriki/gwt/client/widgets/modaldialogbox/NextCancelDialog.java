package org.curriki.gwt.client.widgets.modaldialogbox;

import asquare.gwt.tk.client.ui.ModalDialog;
import asquare.gwt.tk.client.ui.BasicPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.DOM;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;
import org.curriki.gwt.client.widgets.template.TemplateInfo;
import org.curriki.gwt.client.widgets.template.ProposeTemplateDuplicationDialog;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.AssetDocument;

import java.util.List;

/**
 * Copyright 2006,XpertNet SARL,and individual contributors as indicated
 * by the contributors.txt.
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

public class NextCancelDialog extends ModalDialog {
    AsyncCallback nextCallback;
    String titleText;
    String questionText;
    String cssDialogName;

    public NextCancelDialog(String titleText, String questionText, String cssDialogName, AsyncCallback nextCallback) {
        this.nextCallback = nextCallback;
        this.titleText = titleText;
        this.questionText = questionText;
        this.cssDialogName = cssDialogName;
        init();
    }

    public String getCSSName(String name) {
        if ((name==null)||name.equals(""))
            return "dialog-" + cssDialogName + "-" + name;
        else
            return "dialog-" + cssDialogName;
    }

    public void init(){
        addStyleName(getCSSName(""));
        setCaption(Main.getTranslation(titleText), false);

        BasicPanel main = new BasicPanel();
        main.addStyleName(getCSSName("content"));

        BasicPanel text = new BasicPanel();
        text.addStyleName(getCSSName("text"));
            text.add(new HTML(Main.getTranslation(questionText)));
        main.add(text);

        BasicPanel actions = new BasicPanel();
        actions.addStyleName(getCSSName("actions"));

        ClickListener cancelListener = new ClickListener(){
            public void onClick(Widget sender){
                nextCallback.onFailure(null);
            }
        };

        Button cancel = new Button(Main.getTranslation("editor.btt_cancel"), cancelListener);
        cancel.addStyleName("dialog-cancel");
        cancel.addStyleName(getCSSName("cancel"));
        cancel.addStyleName("gwt-bttCancel");
        cancel.addStyleName("gwt-ButtonGrey");
        actions.add(cancel);

        ClickListener nextListener = new ClickListener(){
                    public void onClick(Widget sender){
                        nextCallback.onSuccess(null);
                    }
                };

        Button next = new Button(Main.getTranslation("editor.btt_next"), nextListener);
        next.addStyleName("dialog-next");
        next.addStyleName(getCSSName("next"));
        next.addStyleName("gwt-ButtonOrange");
        next.addStyleName("gwt-bttNext");
        actions.add(next);

        main.add(actions);
        add(main);
        show();
    }
}
