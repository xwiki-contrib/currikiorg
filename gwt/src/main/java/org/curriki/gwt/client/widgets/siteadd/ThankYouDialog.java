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
import org.curriki.gwt.client.utils.Translator;
import org.curriki.gwt.client.utils.XWikiGWTPanelLoader;
import org.curriki.gwt.client.Main;

public class ThankYouDialog extends ModalDialog {
    public ThankYouDialog(String thankYouPage, ClickListener continueCallback) {
        addStyleName("thankyou-dialog");
        //addController(new ModalDialog.DragStyleController(this));
        setCaption(Main.getTranslation("dialog."+thankYouPage+".caption"), false);
        setContentMinWidth(400);
        setContentMinHeight(100);

        BasicPanel main = new BasicPanel();
        main.addStyleName("thankyou-dialog-content");

        BasicPanel text = new BasicPanel();
        text.addStyleName("thankyou-dialog-text");
        XWikiGWTPanelLoader.loadWikiPage(thankYouPage, text);

        Button next = new Button(Main.getTranslation("dialog."+thankYouPage+".continue"), continueCallback);
        next.addStyleName("dialog-continue");
        next.addStyleName("thankyou-dialog-continue");

        main.add(text);
        main.add(next);

        add(main);
        show();
    }
}
