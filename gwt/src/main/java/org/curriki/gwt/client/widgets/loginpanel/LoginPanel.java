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
 *
 * This class is originally from http://code.google.com/p/gwt-stuff/
 * licenced under the Apache Software licence:
 * See http://www.opensource.org/licenses/apachepl.php
 *
 *
 */

package org.curriki.gwt.client.widgets.loginpanel;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.EventListener;

import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;

public class LoginPanel extends Composite {
    private final DockPanel dock = new DockPanel();

    private Widget banner = null;

    private final VerticalPanel vp = new VerticalPanel();

    private final Label message = new HTML();
    private final Label errorMessage = new HTML();
    private final Grid grid = new Grid(3, 2);

    private final TextBox username = new TextBox();
    private final Label usernameLabel = new HTML(Main.getTranslation("login.username"));

    private final PasswordTextBox password = new PasswordTextBox();
    private final Label passwordLabel = new HTML(Main.getTranslation("login.password"));

    private final Image wait = new Image(Constants.ICON_SPINNER);
    private final SimplePanel waitPanel = new SimplePanel();
    private final HorizontalPanel loginPanel = new HorizontalPanel();
    private final Button login = new Button(Main.getTranslation("login.login"));

    private final AsyncCallback callBack;
    
    /**
     * The current LoginValidator. defaults to a validator that simply checks that the username
     * and password are not empty.
     */
    private LoginValidator validator = new LoginValidator() {
        public boolean validateUsername(final String username) {
            return username.trim().length() > 0;
        }

        public boolean validatePassword(final String password) {
            return password.trim().length() > 0;
        }
    };

    /**
     * Create a new Login Panel.
     *
     * @param callBack the callback to be notifed of a login attempt.
     */
    public LoginPanel(AsyncCallback callBack) {
        this.callBack = callBack;
        initWidget(dock);

        setStyleNames();

        dock.add(vp, DockPanel.CENTER);

        vp.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        vp.add(message);
        vp.add(errorMessage);
        vp.add(grid);
        
        grid.setWidget(0, 0, usernameLabel);
        grid.setWidget(0, 1, username);

        grid.setWidget(1, 0, passwordLabel);
        grid.setWidget(1, 1, password);

        loginPanel.addStyleName("login-panel");
        // loginPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        final HorizontalPanel grouper = new HorizontalPanel();
        grouper.add(waitPanel);
        grouper.add(login);
        loginPanel.add(grouper);
        login.setEnabled(false);
        grid.setWidget(2, 1, loginPanel);

        final HTMLTable.CellFormatter cellFormatter = grid.getCellFormatter();
        cellFormatter.setWidth(2, 1, "1px");
        cellFormatter.setHorizontalAlignment(2, 1, HasAlignment.ALIGN_RIGHT);

        usernameLabel.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {
                username.setFocus(true);
            }
        });

        passwordLabel.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {
                password.setFocus(true);
            }
        });

        final LoginKeyboardListener keyboardListener = new LoginKeyboardListener();
        username.addKeyboardListener(keyboardListener);
        password.addKeyboardListener(keyboardListener);

        final LoginChangeListener loginChangeListener = new LoginChangeListener();
        username.addChangeListener(loginChangeListener);
        password.addChangeListener(loginChangeListener);

        login.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {
                login();
            }
        });
    }

    private void setStyleNames() {
        dock.setStyleName("login-LoginPanel");
        message.addStyleName("login-Message");
        errorMessage.addStyleName("login-ErrorMessage");
        username.addStyleName("login-Username");
        usernameLabel.addStyleName("login-UsernameLabel");
        password.addStyleName("login-Password");
        passwordLabel.addStyleName("login-PasswordLabel");
        login.addStyleName("login-Button");
    }

    /**
     * Grab the focus. This will put the cursor in the text field for the user.
     */
    public void focus() {
        if (username.getText().trim().length() > 0) {
            password.setFocus(true);
        } else {
            username.setFocus(true);
        }
    }

    /**
     * Check the username and password fields and enable/disable the login button.
     */
    private void updateLoginEnabled() {
        login.setEnabled(validator.validateUsername(username.getText())
                && validator.validatePassword(password.getText()));
    }

    /**
     * Trigger a login event. This is the equiviliant of the user clicking the Login button.
     */
    public void login() {
        login.setEnabled(false);
        waitPanel.add(wait);
        callBack.onSuccess(this);
    }

    /**
     * Reset the widget to the initial state.
     */
    public void reset() {
        setMessage(null);
        setErrorMessage(null);
        username.setText("");
        password.setText("");
        updateLoginEnabled();
        waitPanel.clear();
    }

    /**
     * Allow the widget to accept another login attempt.
     */
    public void reenable() {
        waitPanel.clear();
        updateLoginEnabled();
    }

    /**
     * Get the entered username.
     *
     * @return the username currently entered.
     */
    public String getUsername() {
        return username.getText();
    }

    /**
     * Get the entered password.
     *
     * @return the password currently entered.
     */
    public String getPassword() {
        return password.getText();
    }

    /**
     * Set the message to display to the user.
     *
     * @param message the message to display to the user.
     */
    public void setMessage(final String message) {
        this.message.setText(message != null ? message : "");
    }

    /**
     * Set the error message to display to the user.
     *
     * @param message the error message to display to the user.
     */
    public void setErrorMessage(final String message) {
        this.errorMessage.setText(message != null ? message : "");
    }

    /**
     * The widget displayed across the top of this LoginPanel.
     *
     * @return the widget above this panel or <code>null</code>.
     */
    public Widget getBanner() {
        return banner;
    }

    /**
     * Set the widget to be shown across the top of this panel.
     *
     * @param banner the widget to put above this login panel or <code>null</code> to clear.
     */
    public void setBanner(final Widget banner) {
        dock.remove(this.banner);
        if (banner != null) {
            dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
            dock.setVerticalAlignment(DockPanel.ALIGN_BOTTOM);
            dock.add(banner, DockPanel.NORTH);
        }
    }

    /**
     * Provide your own localized messages.
     *
     * @param messages custom localized messages.
     */
    //public void setMessages(final LoginMessages messages) {
    //    this.messages = messages;
    //}

    /**
     * Set the callback to validate the username and password fields. If these fields fail
     * validation the Login button will be disabled.
     *
     * @param validator callaback to check the username and password.
     */
    public void setLoginValidator(final LoginValidator validator) {
        if (validator == null) {
            throw new IllegalArgumentException("The LoginValidator cannot be set to null.");
        }
        this.validator = validator;
    }

    /**
     * Callback to check the acceptablity of the the username and password fields.
     * Use this to check minimum required length or required case of the user's crenditials.
     */
    public static interface LoginValidator extends EventListener {
        /**
         * Check the username for validity.
         *
         * @param username the currently entered username.
         * @return true if this is a possibly valid username, else false.
         */
        public boolean validateUsername(String username);

        /**
         * Check the password for validity.
         *
         * @param password the currently entered password.
         * @return true if this is a possibly valid password, else false.
         */
        public boolean validatePassword(String password);
    }

    /**
     * Listen for key presses and update the login button.
     */
    private class LoginKeyboardListener extends KeyboardListenerAdapter {
        public void onKeyUp(final Widget sender, final char keyCode, final int modifiers) {
            updateLoginEnabled();
            if (keyCode == KEY_ENTER) {
                if (login.isEnabled()) {
                    login.click();
                }
            }
        }
    }

    /**
     * Listen for changes and update the login button.
     */
    private class LoginChangeListener implements ChangeListener {
        public void onChange(final Widget sender) {
            updateLoginEnabled();
        }
    }
}
