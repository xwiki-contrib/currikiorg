package org.curriki.gwt.client.widgets.currikiitem;

import com.google.gwt.user.client.Event;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.widgets.currikiitem.display.AbstractItemDisplay;
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

public interface CurrikiItem {
    void onBrowserEvent(Event event);

    void refreshItemInfos();

    void refreshItemInfos(AbstractItemDisplay item);

    int getStatus();

    void onEditClick();

    void onSelectClick();

    void setSelected(boolean flag);

    boolean isSelected();

    void onCancelClick();

    void onSaveClick();

    void onDuplClick();

     public boolean isDirty();

    void switchHeaderButtonsToViewMode();

    void loadItemDisplay(Document doc);

    void loadItemDisplay(String fullName);

    String getDocumentFullName();

    AbstractItemDisplay getItem();

    void setItem(AbstractItemDisplay item);

    void setTitle(String title);

    long getIndex();

    void setIndex(long index);

    void onRemoveClick();

    void onHideClick();

    void onShowClick();

    String getType();

    public void onEditMetadataClick();

    void onMoveClick();

    void init();

    void onCommentClick();

    void refreshHeader();
}
