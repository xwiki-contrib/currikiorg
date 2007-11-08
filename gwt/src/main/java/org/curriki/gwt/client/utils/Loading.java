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
 * @author dward
 *
 */
package org.curriki.gwt.client.utils;

import asquare.gwt.tk.client.ui.ModalDialog;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.Window;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;

public class Loading {
    private ModalDialog loadingPanel = null;
    private int currentRequest = 0;
    private boolean disable = false;

    public void disable(){
        disable = true;
    }

    public void enable(){
        disable = false;
    }

    public void startLoading() {
        if (loadingPanel == null && !disable){
            loadingPanel = new ModalDialog();
            loadingPanel.addStyleName("dialog-loading");
            loadingPanel.add(new HTML(Main.getTranslation("loading.loading_msg")));
            loadingPanel.add(new Image(Constants.ICON_SPINNER));
        }

        if (loadingPanel != null){
            loadingPanel.show();
        }
        currentRequest++;
    }

    public void finishLoading() {
        currentRequest--;
        if (currentRequest <= 0 && loadingPanel != null) {
            loadingPanel.hide();
            loadingPanel.removeFromParent();
            loadingPanel = null;
        }
        if (currentRequest < 0){
            Main.getSingleton().showError("finishedLoading() was called too many times " + currentRequest);
            currentRequest = 0;
        }
    }
}
