package org.curriki.gwt.client.widgets.preview;

import org.curriki.gwt.client.widgets.modaldialogbox.ModalMsgDialogBox;
import org.curriki.gwt.client.Main;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;
import asquare.gwt.tk.client.ui.ModalDialog;

/**
 * Copyright 2006,XpertNet SARL,and individual contributors as indicated
 * by the contributors.txt.
 * <p/>
 * This is free software;you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation;either version2.1of
 * the License,or(at your option)any later version.
 * <p/>
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software;if not,write to the Free
 * Software Foundation,Inc.,51 Franklin St,Fifth Floor,Boston,MA
 * 02110-1301 USA,or see the FSF site:http://www.fsf.org.
 *
 * @author ldubost
 */

public class PreviewDialog  {
    final ModalDialog dialog = new ModalDialog();

    public PreviewDialog(String title, String msg, String url, String styleName){
        dialog.setCaption(title, false);
        dialog.setStyleName(styleName);
        dialog.add(new Label(msg));
        int height = Window.getClientHeight() - 100;
        if (height<100)
         height = 100;
        dialog.setHeight(height + "px");
        ScrollPanel scroll = new ScrollPanel();
        scroll.setWidth("100%");
        scroll.setHeight("80%");
        scroll.addStyleName(styleName + "-scroll");
        Frame frame = new Frame(url);
        frame.setStyleName(styleName + "-iframe");
        frame.setWidth("100%");
        // frame.setHeight((height-60) + "px");
        scroll.add(frame);
        dialog.add(scroll);
        dialog.add(new CloseButton(dialog, Main.getTranslation("preview.close")));
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

    public static void show(String url){
        new PreviewDialog(Main.getTranslation("preview.title"), Main.getTranslation("preview.message"), url, "preview");
    }

}
