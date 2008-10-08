package org.curriki.gwt.client.widgets.moveasset;

import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.*;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.widgets.modaldialogbox.CurrikiDialog;
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

public class MoveModalBox extends CurrikiDialog {
    private String assetPageName;
    private VerticalPanel panel;
    private CurrikiDialog dialog;

    public MoveModalBox(String rootAsset, String assetPageName, String parentName, long pos){
        super();
        addStyleName("move-modal-box");
        this.dialog = this;
        setCaption(Main.getTranslation("moveasset.move"), true);
        MoveAsset moveAsset = new MoveAsset(rootAsset, assetPageName, parentName, pos, new BoxWindowResizeListener());
        moveAsset.setParentDialog(this);
        panel = new VerticalPanel();
        panel.add(new HTML(Main.getTranslation("moveasset.select_asset_to_move")));

        ScrollPanel movePanel = new ScrollPanel();
        movePanel.addStyleName("move-modal-box-scroller");
        movePanel.add(moveAsset);

        panel.add(movePanel);
        panel.add(new Button(Main.getTranslation("editor.btt_cancel"), new CancelClickListener()));
        add(panel);
        this.assetPageName = assetPageName;
    }



    private class CancelClickListener implements ClickListener{

        public void onClick(Widget sender) {
            hide();
            removeFromParent();
        }
    }

    private class BoxWindowResizeListener  implements WindowResizeListener {

        public void onWindowResized(int width, int height) {
        	dialog.center();
            // getController(ModalDialog.PositionDialogController.class).plugIn(dialog);
        }
    }

    /*
    protected List createCaptionControllers() {
        List result = new Vector();
        result.add(PreventSelectionController.INSTANCE);
        return result;
    }
    */

}
