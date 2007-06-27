/*
 * Copyright 2006, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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
package org.curriki.gwt.client.widgets.modaldialogbox;

import asquare.gwt.tk.client.ui.behavior.ControllerAdaptor;
import asquare.gwt.tk.client.ui.ModalDialog;
import asquare.gwt.tk.client.util.DomUtil;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.Window;
import org.curriki.gwt.client.wizard.Wizard;


    /**
	 * A controller which encapsulates dialog positioning logic.
	 */
public  class SizeDialogController extends ControllerAdaptor {
    private Widget widget = null;

    public SizeDialogController()
    {
        super(0, SizeDialogController.class);
    }

    public SizeDialogController(Widget widget) {
        this();
        this.widget = widget;
    }

    public void resize(ModalDialog dialog) {
        int contentWidth = dialog.getContentOffsetWidth();

        if (widget instanceof ScrollPanel){
           contentWidth = ((ScrollPanel)widget).getWidget().getOffsetWidth();
        }

        int maxContentWidth = Window.getClientWidth();
        if (contentWidth > maxContentWidth || contentWidth < dialog.getContentMinWidth())
        {
            if (contentWidth > maxContentWidth)
            {
                contentWidth = maxContentWidth;
            }
            if (contentWidth < dialog.getContentMinWidth())
            {
                contentWidth = dialog.getContentMinWidth();
            }
        }
        dialog.setContentWidth(contentWidth + "px");

        int contentHeight = dialog.getContentOffsetHeight();

        if (widget instanceof ScrollPanel){
           contentHeight = ((Wizard)((ScrollPanel)widget).getWidget()).getOffsetHeight();
        }

        if (contentHeight < dialog.getContentMinHeight())
        {
            contentHeight = dialog.getContentMinHeight();
            //dialog.setContentHeight(contentHeight + "px");
        }

        int maxContentHeight = Window.getClientHeight();
        if (contentHeight > maxContentHeight){
            contentHeight = maxContentHeight;
            //dialog.setHeight(contentHeight + "px");
           if (widget != null)
                widget.setHeight(contentHeight - 30 + "px");
        }

        dialog.setContentHeight(contentHeight - 30 + "px");


        int dialogHeight = dialog.getOffsetHeight();
        // calculate top last because maxContentWidth constraint may change height
        int top = DomUtil.getViewportScrollY() + DomUtil.getViewportHeight() / 2 - dialogHeight / 2;
        int left = DomUtil.getViewportScrollX() + DomUtil.getViewportWidth() / 2 - contentWidth / 2;
        dialog.setPopupPosition(left, top);
    }

    public void plugIn(Widget widget)
    {
        resize((ModalDialog) widget);
    }
}