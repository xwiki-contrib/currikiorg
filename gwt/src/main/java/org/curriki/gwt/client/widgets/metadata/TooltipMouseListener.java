package org.curriki.gwt.client.widgets.metadata;

import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class TooltipMouseListener implements MouseListener {
    protected PopupPanel popup;
    protected int left = -1;
    protected Widget fromWidget;

    public void onMouseDown(Widget widget, int i, int i1) {
    }

    public void onMouseEnter(Widget widget) {
        Widget useWidget;
        if (fromWidget != null){
            useWidget = fromWidget;
        } else {
            useWidget = widget;
        }
        if (left == -1){
            popup.setPopupPosition(useWidget.getAbsoluteLeft() + 30, useWidget.getAbsoluteTop() + 5);
        } else {
            popup.setPopupPosition(useWidget.getAbsoluteLeft() + left, useWidget.getAbsoluteTop() + 5);
        }
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

    public TooltipMouseListener(PopupPanel popup, int left, Widget fromWidget){
        this(popup);
        setLeft(left);
        setFromWidget(fromWidget);
    }

    public void setLeft(int left){
        this.left = left;
    }

    public void setFromWidget(Widget fromWidget){
        this.fromWidget = fromWidget;
    }
}
