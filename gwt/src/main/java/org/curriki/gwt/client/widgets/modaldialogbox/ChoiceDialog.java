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
package org.curriki.gwt.client.widgets.modaldialogbox;

import com.google.gwt.user.client.ui.*;
import org.curriki.gwt.client.widgets.basicpanel.BasicPanel;

public class ChoiceDialog extends CurrikiDialog {
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
            return "dialog-" + cssDialogName;
        else
            return "dialog-" + cssDialogName + "-" + name;
    }

    public void init(){
        addStyleName(getCSSName(""));
        setCaption(titleText, true);

        BasicPanel main = new BasicPanel();
        main.addStyleName(getCSSName("content"));

        BasicPanel text = new BasicPanel();
        text.addStyleName(getCSSName("text"));
        text.add(new HTML(questionText));
        main.add(text);

        HorizontalPanel actions = new HorizontalPanel();
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
