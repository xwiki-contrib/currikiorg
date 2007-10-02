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
package org.curriki.gwt.client.widgets.currikiitem;

import com.google.gwt.user.client.ui.*;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.Constants;

public class CurrikiItemHeader extends Composite implements ClickListener {
    public int MODE_EMPTY = 0;
    public int MODE_VIEW = 1;
    public int MODE_EDIT = 2;

    private int mode = 1;
    private boolean show = true;

    private HorizontalPanel panel = new HorizontalPanel();
    private FlowPanel buttonPanel = new FlowPanel();
    private FlowPanel collapsePanel = new FlowPanel();
    private FlowPanel titlePanel = new FlowPanel();
    private Button editBt;
    private Button editCompBt;
    private Button duplBt;
    private Button removeBt;
    private Button moveBt;
    private Button commentBt;
    private Image hideBt;
    private Image showBt;

    private Button cancelBt;
    private Button saveBt;
    private Button metaBt;
    private Button openBt;

    private HTML title;
    private CurrikiItem item;

    /**
     * Verified is document is licence protected
     * @return
     */
    public boolean isLicenceProtected() {
        try {
            return item.getItem().getDocument().isLicenceProtected();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verified if document is viewable
     * @return
     */
    public boolean isViewable() {
        try {
            return item.getItem().getDocument().hasViewRight();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verified if document is editable
     * @return
     */
    public boolean isEditable() {
        try {
            return item.getItem().getDocument().hasEditRight();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifies if document is duplicatable
     * Either the user is the owner
     * Or the document is viewable and doesn't have the No Derivatives licence
     * @return
     */
    public boolean isDuplicatable() {
        try {
            return item.getItem().getDocument().isDuplicatable();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verified if document is editable
     * @return
     */
    public boolean isParentEditable() {
        try {
            return item.getItem().getDocument().isParentEditable();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verified if document is viewable
     * @return
     */
    public boolean isComposite() {
        try {
            return item.getItem().getDocument().isComposite();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verified if document is viewable
     * @return
     */
    public boolean isDirectionBlock() {
        try {
            return item.getItem().getDocument().isDirectionBlock();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sets the mode (0 VIEW 1 EDIT)
     * @param mode
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * Gets the mode (0 VIEW 1 EDIT)
     */
    public int getMode() {
        return mode;
    }


    /**
     * Sets the display status (True Shown False Hidden)
     * @param show
     */
    public void setShow(boolean show) {
        this.show = show;
    }

    /**
     * Gets the display status
     */
    public boolean getShow() {
        return show;
    }

    public CurrikiItemHeader(CurrikiItem item){
        this.item = item;
        initHeader();
    }

    public void initHeader() {
        title = new HTML(Main.getTranslation("loading.loading_msg"));
        title.addStyleName("item-header-title");
        titlePanel.add(title);
        panel.add(collapsePanel);
        panel.add(titlePanel);
        panel.add(buttonPanel);

        collapsePanel.setStyleName("item-header-collapse");
        titlePanel.setStyleName("item-header-title");
        buttonPanel.setStyleName("item-header-buttons");

        cancelBt = new Button(Main.getTranslation("editor.btt_cancel"));
        cancelBt.addClickListener(this);
        cancelBt.addStyleName("edit-btt-cancel");

        saveBt = new Button(Main.getTranslation("editor.btt_save"));
        saveBt.addClickListener(this);
        saveBt.addStyleName("edit-btt-save");

        editBt = new Button(Main.getTranslation("editor.btt_edit"));
        editBt.addClickListener(this);
        editBt.addStyleName("edit-btt-edit");
        editBt.setTitle(Main.getTranslation("editor.btt_edit_tt"));

        editCompBt = new Button(Main.getTranslation("editor.btt_compEdit"));
        editCompBt.addClickListener(this);
        editCompBt.addStyleName("edit-btt-compEdit");

        duplBt = new Button(Main.getTranslation("editor.btt_dupl"));
        duplBt.addClickListener(this);
        duplBt.addStyleName("edit-btt-dupl");
        duplBt.setTitle(Main.getTranslation("editor.btt_dupl_tt"));

        removeBt = new Button(Main.getTranslation("editor.btt_remove"));
        removeBt.addClickListener(this);
        removeBt.addStyleName("edit-btt-remove");
        removeBt.setTitle(Main.getTranslation("editor.btt_remove_tt"));

        moveBt = new Button(Main.getTranslation("editor.btt_move"));
        moveBt.addClickListener(this);
        moveBt.setTitle(Main.getTranslation("editor.btt_move_tt"));

        metaBt = new Button(Main.getTranslation("editor.btt_metadata"));
        metaBt.addClickListener(this);
        metaBt.addStyleName("edit-btt-metadata");
        metaBt.setTitle(Main.getTranslation("editor.btt_metadata_tt"));

        commentBt = new Button(Main.getTranslation("editor.btt_comment") + " (" + getCurrentCommentNumber() + ")");
        commentBt.addClickListener(this);
        commentBt.addStyleName("edit-btt-comment");

        openBt = new Button(Main.getTranslation("editor.btt_open"));
        openBt.addClickListener(this);
        openBt.addStyleName("edit-btt-open");

        String imgSuffix = ".png";
        if (isDirectionBlock())
            imgSuffix = "_white.gif";

        hideBt = new Image(Constants.ICON_PATH+"collapse_down" + imgSuffix);
        hideBt.addClickListener(this);

        showBt = new Image(Constants.ICON_PATH+"collapse" + imgSuffix);
        showBt.addClickListener(this);

        // panel.setWidth("100%");
        panel.setStyleName("item-header-panel");
        showHideButton();
        initWidget(panel);
        addStyleName("item-header-panel");

        // Hardcoded styles
        panel.setCellWidth(collapsePanel, "18px");
        panel.setCellHorizontalAlignment(collapsePanel, HorizontalPanel.ALIGN_LEFT);
        panel.setCellWidth(buttonPanel, "300px");
    }

    private int getCurrentCommentNumber() {
        if ((item==null)||(item.getItem()==null)||(item.getItem().getDocument()==null))
          return 0;
        else
          return item.getItem().getDocument().getCommentsNumber();
    }

    public void setTitle(String title){
        if (isDirectionBlock())
            this.title.setText(Main.getTranslation("editor.cbdirections"));
        else
            this.title.setText(title);
    }

    public void onClick(Widget widget) {
        if (widget.equals(editBt) || widget.equals(editCompBt))
            item.onEditClick();
        if (widget.equals(cancelBt))
            item.onCancelClick();
        if (widget.equals(duplBt))
            item.onDuplClick();
        if (widget.equals(saveBt))
            item.onSaveClick();
        if (widget.equals(removeBt))
            item.onRemoveClick();
        if (widget.equals(hideBt))
            item.onHideClick();
        if (widget.equals(showBt))
            item.onShowClick();
        if (widget.equals(metaBt))
            item.onEditMetadataClick();
        if (widget.equals(moveBt))
            item.onMoveClick();
        if (widget.equals(commentBt))
            item.onCommentClick();
    }

    public void refreshHeader() {
        if (getMode()==MODE_VIEW)
         setViewButtons();
        else if (getMode()==MODE_EDIT)
         setEditButtons();
        else
         clearButtons();

        if (getShow()) {
         showHideButton();
        }
        else {
         showShowButton();
        }
    }

    protected void setEditButtons(){
        clearButtons();
        buttonPanel.add(cancelBt);
        buttonPanel.add(saveBt);
    }

    protected void setViewButtons(){
        clearButtons();


        // We show the edit button either if document is editable or if asset is a template and the parent one isn't (which means we should automatically duplicate
        if (isEditable()||(item.getItem().getDocument().isCurrikiTemplate()&&!item.getItem().getDocument().isParentCurrikiTemplate())) {
            if (isComposite()) {
                buttonPanel.add(editCompBt);
            } else {
                buttonPanel.add(editBt);
            }
        }

        if (isDuplicatable())
            buttonPanel.add(duplBt);

        if (isParentEditable()) {
            buttonPanel.add(removeBt);
            buttonPanel.add(moveBt);
        }

        if (isViewable()&&!isDirectionBlock()) {
            buttonPanel.add(metaBt);
        }

        if (isViewable()&&!isDirectionBlock()) {
            buttonPanel.add(commentBt);
            commentBt.setText(Main.getTranslation("editor.btt_comment") + " (" + getCurrentCommentNumber() + ")");
        }
    }

    protected void clearButtons() {
        buttonPanel.remove(cancelBt);
        buttonPanel.remove(saveBt);
        buttonPanel.remove(editBt);
        buttonPanel.remove(editCompBt);
        buttonPanel.remove(duplBt);
        buttonPanel.remove(removeBt);
        buttonPanel.remove(metaBt);
        buttonPanel.remove(commentBt);
        buttonPanel.remove(moveBt);
    }

    protected void showShowButton() {
        collapsePanel.remove(hideBt);
        collapsePanel.remove(showBt);
        if (!isComposite())
            collapsePanel.add(showBt);
    }

    protected void showHideButton() {
        collapsePanel.remove(hideBt);
        collapsePanel.remove(showBt);
        if (!isComposite())
            collapsePanel.add(hideBt);
    }
}
