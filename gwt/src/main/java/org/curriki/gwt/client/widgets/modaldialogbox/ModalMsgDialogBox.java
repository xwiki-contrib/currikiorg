package org.curriki.gwt.client.widgets.modaldialogbox;

import asquare.gwt.tk.client.ui.ModalDialog;
import com.google.gwt.user.client.ui.*;

import org.curriki.gwt.client.Main;
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

public class ModalMsgDialogBox {
    final ModalDialog dialog = new ModalDialog();

    public ModalMsgDialogBox(String title, String msg){
        this(title, msg, null);
    }

    public ModalMsgDialogBox(String title, String msg, String styleName){
        dialog.setCaption(title, false);
        if (styleName!=null) {
            ScrollPanel scroll = new ScrollPanel();
            scroll.add(new Label(msg));
            scroll.addStyleName(styleName);
            dialog.add(scroll);
        } else {
            dialog.add(new Label(msg));
        }
        dialog.add(new CloseButton(dialog, Main.getTranslation("Ok")));
        dialog.show();
    }

    class CloseListener implements ClickListener {
        private final ModalDialog m_dialog;

        public CloseListener(ModalDialog dialog)
        {
            m_dialog = dialog;
        }

        public void onClick(Widget sender)
        {
            m_dialog.hide();
            m_dialog.removeFromParent();
        }
    }

    class CloseButton extends Button {
        public CloseButton(ModalDialog dialog, String msg)
        {
            super(msg);
            addClickListener(new CloseListener(dialog));
        }
    }

    public static void show(String txt){
        new ModalMsgDialogBox(Main.getTranslation("editor.app_full_name"), txt);
    }


}
