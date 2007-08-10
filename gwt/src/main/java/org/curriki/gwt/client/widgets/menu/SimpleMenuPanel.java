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
 *
 */
package org.curriki.gwt.client.widgets.menu;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class SimpleMenuPanel extends ComplexPanel implements IndexedPanel {
    private boolean hiddingEnable = true;
    private Element panelDiv;

    /**
     * Creates an empty simple menu panel.
     */
    public SimpleMenuPanel() {
        panelDiv = DOM.createDiv();
        setElement(panelDiv);

        DOM.sinkEvents(panelDiv, Event.ONDBLCLICK);
        setStyleName("simplePanel");
    }

    /**
     * Adds a new child with the given widget.
     *
     * @param w the widget to be added
     */
    public void add(Widget w) {
        // Call this early to ensure that the table doesn't end up partially
        // constructed when an exception is thrown from adopt().
        w.removeFromParent();

        int index = getWidgetCount();

        Element itemDiv = DOM.createDiv();
        DOM.appendChild(panelDiv, itemDiv);
        setPanelItemID(itemDiv, "" + w.hashCode());

        Element itemTitleDiv = DOM.createDiv();
        DOM.appendChild(itemDiv, itemTitleDiv);
        setStyleName(itemTitleDiv, "simplePanelItemTitle", true);
        DOM.setIntAttribute(itemTitleDiv, "__index", index);
        DOM.setAttribute(itemTitleDiv, "height", "1px");

        Element itemContentDiv = DOM.createDiv();
        setStyleName(itemTitleDiv, "simplePanelItemContent", true);
        DOM.appendChild(itemDiv, itemContentDiv);
        DOM.setAttribute(itemContentDiv, "vAlign", "top");

        super.add(w, itemContentDiv);

    }

    private void setPanelItemID(Element el, String id){
        DOM.setAttribute(el, "id", "panel_" + id);
    }

    private Element getPanelItemByID(String id){
        return DOM.getElementById("panel_" + id);
    }

    private boolean isPanelItemExistByID(String id){
        return getPanelItemByID(id) != null;
    }

    /**
     * Adds a new child with the given widget and header.
     *
     * @param w         the widget to be added
     * @param stackText the header text associated with this widget
     */
    public boolean add(String id, Widget w, String stackText) {
        return add(id, w, stackText, false);
    }

    /**
     * Adds a new child with the given widget and header, optionally interpreting
     * the header as HTML.
     *
     * @param w         the widget to be added
     * @param stackText the header text associated with this widget
     * @param asHTML    <code>true</code> to treat the specified text as HTML
     */
    public boolean add(String id, Widget w, String stackText, boolean asHTML) {
        if (isPanelItemExistByID(id))
            return false;
        add(w);
        int index = getWidgetCount() - 1;
        setPanelItemID(DOM.getChild(panelDiv, index), id);
        setStackText(index, stackText, asHTML);
        return true;
    }

    public Widget getWidget(int index) {
        return getChildren().get(index);
    }

    public int getWidgetCount() {
        return getChildren().size();
    }

    public int getWidgetIndex(Widget child) {
        return getChildren().indexOf(child);
    }

    public void onBrowserEvent(Event event) {
        if (DOM.eventGetType(event) == Event.ONDBLCLICK) {
            int index = getDividerIndex(DOM.eventGetTarget(event));
            if (index != -1) {
                showHideItem(index);
            }
        }
    }

    public void showHideItem(int index) {
        if (!hiddingEnable)
            return ;
        Element itemEl = DOM.getChild(panelDiv, index);
        Element itemContentEl = DOM.getChild(itemEl, 1);
        setVisible(itemContentEl, !isVisible(itemContentEl));
    }

    public boolean remove(int index) {
        return remove(getWidget(index), index);
    }

    public boolean remove(Widget child) {
        return remove(child, getWidgetIndex(child));
    }

    /**
     * Sets the text associated with a child by its index.
     *
     * @param index the index of the child whose text is to be set
     * @param text  the text to be associated with it
     */
    public void setStackText(int index, String text) {
        setStackText(index, text, false);
    }

    /**
     * Sets the text associated with a child by its index.
     *
     * @param index  the index of the child whose text is to be set
     * @param text   the text to be associated with it
     * @param asHTML <code>true</code> to treat the specified text as HTML
     */
    public void setStackText(int index, String text, boolean asHTML) {
        if (index >= getWidgetCount()) {
            return;
        }
        // The index is hardcoded, can be find using the className simplePanelItemTitle
        Element itemContentDiv = DOM.getChild(DOM.getChild(panelDiv, index), 0);

        if (asHTML) {
            DOM.setInnerHTML(itemContentDiv, text);
        } else {
            DOM.setInnerText(itemContentDiv, text);
        }
    }

    private int getDividerIndex(Element elem) {
        while ((elem != null) && !DOM.compare(elem, getElement())) {
            String expando = DOM.getAttribute(elem, "__index");
            if (expando != null) {
                return Integer.parseInt(expando);
            }

            elem = DOM.getParent(elem);
        }

        return -1;
    }

    private boolean remove(Widget child, int index) {
        if (child.getParent() != this) {
            return false;
        }

        Element itemDiv = DOM.getChild(panelDiv, index);
        DOM.removeChild(panelDiv, itemDiv);
        super.remove(child);
        int rows = getWidgetCount();

        // Update all the indexes.
        for (int i = index; i < rows; i++) {
            itemDiv = DOM.getChild(panelDiv, i);
            Element itemTitle = DOM.getFirstChild(itemDiv);
            int curIndex = DOM.getIntAttribute(itemTitle, "__index");
            // assert (curIndex == i - 1);
            DOM.setIntAttribute(itemTitle, "__index", index);
            ++index;
        }

        return true;
    }

    public void showPanelItem(String id){
        Element itemEl = getPanelItemByID(id);
        if (!isVisible(itemEl)){
            switchItemVisibility(itemEl);
        }
    }

    public void hidePanelItem(String id){
        Element itemEl = getPanelItemByID(id);
        if (isVisible(itemEl)){
            switchItemVisibility(itemEl);
        }
    }

    public void showPanelItem(int index){
        Element itemEl = DOM.getChild(panelDiv, index);
        if (!isVisible(itemEl)){
            switchItemVisibility(itemEl);
        }
    }

    public void hidePanelItem(int index){
        Element itemEl = DOM.getChild(panelDiv, index);
        if (isVisible(itemEl)){
            switchItemVisibility(itemEl);
        }
    }

    private void switchItemVisibility(Element itemEl){
        setVisible(itemEl, !isVisible(itemEl));
    }

    public void onModuleLoad() {
        RootPanel.get().add(this);
        this.add("menu1", new Button("test"), "test");
        this.add("menu2", new Label("test"), "test 2");
        hidePanelItem("menu2");
    }
}
