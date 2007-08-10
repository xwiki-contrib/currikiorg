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
 * This class is originally from http://code.google.com/p/gwt-stuff/
 * licenced under the Apache Software licence:
 * See http://www.opensource.org/licenses/apachepl.php
 *
 *
 */


package org.curriki.gwt.client.widgets.loginpanel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.widgets.modaldialogbox.SizeDialogController;
import asquare.gwt.tk.client.ui.ModalDialog;
import asquare.gwt.tk.client.ui.behavior.TabFocusController;

public class LoginDialogBox extends ModalDialog {
    private LoginPanel loginPanel;
    private AsyncCallback loginCallback;
    private AsyncCallback loginPanelCallback;


    public LoginDialogBox() {
        super();
        removeController(getController(TabFocusController.class));
    }


    public void init(AsyncCallback loginCallback) {
        this.loginCallback = loginCallback;
        loginPanelCallback = new AsyncCallback() {
            public void onFailure(Throwable throwable) {
            }
            public void onSuccess(Object result) {
                onSubmit((LoginPanel) result);
            }
        };

        setCaption(Main.getTranslation("login.login"), false);
        loginPanel = new LoginPanel(loginPanelCallback);
        add(loginPanel);
    }

    public void login(String username, String password){
        CurrikiService.App.getInstance().login(username, password, true, new CurrikiAsyncCallback() {
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                Window.alert(Main.getTranslation("login.loginfailed"));
                loginCallback.onFailure(null);
            }

            public void onSuccess(Object result) {
                super.onSuccess(result);
                if (Constants.USER_XWIKI_GUEST.equals(result)) {
                    Window.alert(Main.getTranslation("login.loginfailed"));
                    loginCallback.onFailure(null);
                } else {
                    loginCallback.onSuccess(result);
                }
            }
        });

    }


    public void onSubmit(LoginPanel loginPanel) {
         login(loginPanel.getUsername(), loginPanel.getPassword());
    }
    
}
