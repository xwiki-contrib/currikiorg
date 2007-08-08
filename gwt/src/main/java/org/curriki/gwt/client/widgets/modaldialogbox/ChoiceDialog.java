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
package org.curriki.gwt.client.widgets.modaldialogbox;

import asquare.gwt.tk.client.ui.BasicPanel;
import asquare.gwt.tk.client.ui.ModalDialog;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ChoiceDialog extends ModalDialog {
    String titleText;
    String questionText;
    String cssDialogName;
    FocusWidget[] buttons;

    public ChoiceDialog(String titleText, String questionText, FocusWidget[] buttons, String cssDialogName) {
        this.titleText = titleText;
        this.questionText = questionText;
        this.cssDialogName = cssDialogName;
        this.buttons = buttons;

        init();
    }

    public ChoiceDialog()
    {
    }

    public String getCSSName(String name) {
        if ((name==null)||name.equals(""))
            return "dialog-" + cssDialogName + "-" + name;
        else
            return "dialog-" + cssDialogName;
    }

    public void init(){
        addStyleName(getCSSName(""));
        setCaption(titleText, false);

        BasicPanel main = new BasicPanel();
        main.addStyleName(getCSSName("content"));

        BasicPanel text = new BasicPanel();
        text.addStyleName(getCSSName("text"));
        text.add(new HTML(questionText));
        main.add(text);

        BasicPanel actions = new BasicPanel();
        actions.addStyleName(getCSSName("actions"));

        final ChoiceDialog dialog = this;

        ClickListener closeDialog = new ClickListener(){
            public void onClick(Widget sender){
                if ((dialog != null) && dialog.isVisible()){
                    dialog.hide();
                }
            }
        };

        for (int i=0; i<buttons.length; i++){
            buttons[i].addClickListener(closeDialog);
            actions.add(buttons[i]);
        }

        main.add(actions);
        add(main);
        show();
    }
}
