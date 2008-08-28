package org.curriki.gwt.client.widgets.modaldialogbox;

import org.curriki.gwt.client.widgets.modaldialogbox.CurrikiDialog;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;

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

public class WizardBox {
    public static final int NEXT_BTT = 1;
    public static final int FINISH_BTT = 2;
    public static final int CANCEL_BTT = 4;

    private Button bttNext = new Button(Main.getTranslation("editor.btt_next"));
    private Button bttFinish = new Button(Main.getTranslation("editor.btt_finish"));
    private Button bttCancel = new Button(Main.getTranslation("editor.btt_cancel"));

    private Widget currWidget = null;
    private DockPanel bttPanel = new DockPanel();

    final CurrikiDialog dialog = new CurrikiDialog();

    public WizardBox(String title){
        dialog.addStyleName("wizard");
        dialog.setCaption(title, true);
        bttPanel.add(bttFinish, DockPanel.EAST);
    }

    public Widget getCurrWidget() {
        return currWidget;
    }

    public void show(){
        dialog.show();
    }

    public void hide(){
        dialog.hide();
    }

    public void setCurrWidget(Widget currWidget) {
        if (this.currWidget != null) {
            dialog.clear();
        }
        this.currWidget = currWidget;
        dialog.add(currWidget);
        dialog.add(bttPanel);
    }

    public void addClickListner(ClickListener clicklistener, int btt){
        switch(btt) {
            case NEXT_BTT: bttNext.addClickListener(clicklistener); break;
            case FINISH_BTT: bttFinish.addClickListener(clicklistener); break;
            case CANCEL_BTT: bttCancel.addClickListener(clicklistener); break;
        }
    }

    /**
     *
     * @param btt is a combination of the list of button you want to have
     */
    public void setButtons(int btt){
        bttPanel.clear();
        if ((btt & NEXT_BTT) == 1){
            bttPanel.add(bttNext, DockPanel.EAST);
        }
        if ((btt & FINISH_BTT) == 1){
            bttPanel.add(bttFinish, DockPanel.EAST);
        }
        if ((btt & CANCEL_BTT) == 1){
            bttPanel.add(bttCancel, DockPanel.EAST);    
        }
    }

    class CloseListener implements ClickListener {
        private final CurrikiDialog m_dialog;

        public CloseListener(CurrikiDialog dialog)
        {
            m_dialog = dialog;
        }

        public void onClick(Widget sender)
        {
            m_dialog.hide();
        }
    }

    class CloseButton extends Button {
        public CloseButton(CurrikiDialog dialog, String msg)
        {
            super(msg);
            addClickListener(new CloseListener(dialog));
        }
    }
}
