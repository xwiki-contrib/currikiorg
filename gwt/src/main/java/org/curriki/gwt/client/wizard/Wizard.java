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
package org.curriki.gwt.client.wizard;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import org.curriki.gwt.client.utils.Translator;
import org.curriki.gwt.client.Main;
import asquare.gwt.tk.client.ui.ModalDialog;

public class Wizard extends Composite {
    protected final DockPanel panel = new DockPanel();
    private ClickListener closeListener = null;
    private AsyncCallback parentCaptionCallback = null;
    protected WindowResizeListener resizeListener = null;
    protected ModalDialog parentDialog;


    public void setParentDialog(ModalDialog parentDialog) {
        this.parentDialog = parentDialog;
    }

    public ModalDialog getParentDialog() {
        return parentDialog;
    }

    public void hideParentDialog() {
       if (parentDialog!=null)
            parentDialog.setVisible(false);
    }

    public void showParentDialog() {
       if (parentDialog!=null)
            parentDialog.setVisible(true);
    }

    protected void setParentCaption(String text){
        if (parentCaptionCallback != null){
            parentCaptionCallback.onSuccess(text);
        }
    }

    protected void closeParent(){
        if (closeListener != null)
            closeListener.onClick(this);
        else
            Window.alert(Main.getTranslation("error.no_close_listener_set"));
    }

    public void setCloseListener(ClickListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setParentCaptionListener(AsyncCallback callback) {
        this.parentCaptionCallback = callback;
    }

    public void setResizeListener(WindowResizeListener callback) {
        this.resizeListener = callback;
    }

    protected void onResize(){
        if (resizeListener != null)
            resizeListener.onWindowResized(panel.getOffsetHeight(), panel.getOffsetWidth());
    }
}
