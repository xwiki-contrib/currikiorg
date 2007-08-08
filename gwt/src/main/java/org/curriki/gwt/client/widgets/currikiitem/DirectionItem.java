package org.curriki.gwt.client.widgets.currikiitem;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.DOM;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.pages.EditPage;
import org.curriki.gwt.client.widgets.currikiitem.display.AbstractItemDisplay;
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

public class DirectionItem extends Composite implements CurrikiItem {
    TextArea textarea = null;
    private Label directions;
    private Button bttDelete = new Button(Main.getTranslation("editor.btt_remove"));
    private Button bttEdit = new Button(Main.getTranslation("editor.btt_edit"));
    private Button bttSave = new Button(Main.getTranslation("editor.btt_save"));

    private DockPanel panel = new DockPanel();
    private long index = -1;
    private boolean selected = false;

    public DirectionItem(Document doc, String key){
        int obj_id = Integer.valueOf(key.substring(Constants.DIRECTION.length())).intValue();
        XObject obj = doc.getObject(Constants.DIRECTION_CLASS, obj_id);
        String text = (String) obj.get(Constants.DIRECTION_TEXT_PROPERTY);
        directions = new Label(text){
            public void onBrowserEvent(Event event) {
                if (DOM.eventGetType(event) == Event.ONCLICK)
                    onSelectClick();
            }
        };
        directions.sinkEvents(Event.ONCLICK);
        init();
    }

    public void init() {
        bttDelete.addClickListener(new ClickListener(){
            public void onClick(Widget sender) {
                onRemoveClick();
            }
        });

        bttEdit.addClickListener(new ClickListener(){
            public void onClick(Widget sender) {
                onEditClick();
            }
        });

        bttSave.addClickListener(new ClickListener(){
            public void onClick(Widget sender) {
                onSaveClick();
            }
        });

        panel.add(directions, DockPanel.CENTER);

        panel.add(bttDelete, DockPanel.NORTH);
        panel.add(bttEdit, DockPanel.NORTH);

        initWidget(panel);
        panel.addStyleName("item-direction");
    }

    public void refreshHeader() {
    }

    public void onBrowserEvent(Event event) {
    }

    public void refreshItemInfos(){}
    public void refreshItemInfos(AbstractItemDisplay item){}
    public int getStatus(){
        return Constants.VIEW;
    }

    public void onEditClick() {
        textarea = new TextArea();
        textarea.setText(directions.getText());
        // textarea.setHeight("300px");
        // textarea.setWidth("100%");
        textarea.setStyleName("text-editor");
        panel.add(bttSave, DockPanel.SOUTH);
    }

    public void onSelectClick() {
        Main.getSingleton().getEditor().setSelectedDocumentName(getDocumentFullName());
    }

    public void setSelected(boolean flag){
        if (flag) {
            panel.setStyleName("item-direction");
            panel.addStyleName("item-direction-selected");
            directions.setStyleName("item-direction-content");
            directions.addStyleName("item-direction-content-selected");
        } else
            panel.setStyleName("item-direction");
            directions.setStyleName("item-direction-content");
        selected = flag;
    }

    public boolean isSelected() {
        return selected;
    }

    public void onCancelClick() {
    }

    public void onSaveClick() {
    }

    public void onDuplClick() {
    }

    public boolean isDirty(){
        return false;    
     }

    public void switchHeaderButtonsToViewMode() {
    }

    public void loadItemDisplay(Document doc) {
    }

    public void loadItemDisplay(String fullName) {
    }

    public String getDocumentFullName(){
        return Constants.PAGE_BREAK;
    }

    public AbstractItemDisplay getItem() {
        return null;
    }

    public void setItem(AbstractItemDisplay item) {
    }

    public void setTitle(String title) {
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public void onRemoveClick() {
        Main.getSingleton().getEditor().removeAsset(getIndex());
    }

    public void onHideClick() {
    }

    public void onShowClick() {
    }

    public String getType() {
        return Constants.PAGE_BREAK;
    }

    public void setNoDerivative(boolean value) {

    }

    public void onEditMetadataClick() {
    }

    public void onCommentClick() {
    }

    public void onMoveClick() {
    }
}
