package org.curriki.gwt.client.widgets.metadata;

import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by IntelliJ IDEA.
 * User: ldubost
 * Date: 11 mai 2007
 * Time: 11:19:53
 * To change this template use File | Settings | File Templates.
 */
public class TooltipMouseListener implements MouseListener {
    protected PopupPanel popup;

    public void onMouseDown(Widget widget, int i, int i1) {
    }

    public void onMouseEnter(Widget widget) {
        popup.setPopupPosition(widget.getAbsoluteLeft() + 30, widget.getAbsoluteTop() + 5);
        popup.show();
    }

    public void onMouseLeave(Widget widget) {
        popup.hide();
    }

    public void onMouseMove(Widget widget, int i, int i1) {
    }

    public void onMouseUp(Widget widget, int i, int i1) {
    }

    public TooltipMouseListener(PopupPanel popup) {
        this.popup = popup;
    }
}
