/*
 * Copyright 2007, The Global Education and Learning Community,
 * and individual contributors as indicated by the contributors.txt.
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

import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.core.client.GWT;
import com.xpn.xwiki.gwt.api.client.Dictionary;

public class Translator {
    private Dictionary dictionary = null;
    private AsyncCallback callback;
    private TextArea missingTranslation = null;

    public Translator(){
    }

    public void addToMissingTranslation(String key){
        if (missingTranslation == null) {
            missingTranslation = new TextArea();
            missingTranslation.setVisible(false);
            RootPanel.get().add(missingTranslation);
            missingTranslation.addStyleName("missingTranslation");
        }
        String txt = missingTranslation.getText();
        if (txt == null){
            txt = "";
        }
        txt = txt + "\n" + key + ":";
        missingTranslation.setText(txt);
    }

    public String getTranslation(String key) {
        if (dictionary == null){
            return key;
        }
        if (dictionary.get(key) == null){
            addToMissingTranslation(key);
        }
        return (dictionary.get(key) != null) ? dictionary.get(key) : key ;
    }

    public String getTranslation(String key, String[] args) {
        String oStr = getTranslation(key);

        for (int i = 0; i<args.length; i++){
            oStr = oStr.replaceAll("{"+i+"}", args[i]);
        }

        return oStr;
    }

    public void init() {
        CurrikiService.App.getInstance().getTranslation(Constants.TRANSLATION_PAGE, "en_US", new CurrikiAsyncCallback(){
            public void onSuccess(Object result) {
                super.onSuccess(result);
                dictionary = (Dictionary) result;
                if (callback != null){
                    callback.onSuccess(result);
                }
            }
        });
    }

    public void init(AsyncCallback callback){
        this.callback = callback;
        init();
    }
}
