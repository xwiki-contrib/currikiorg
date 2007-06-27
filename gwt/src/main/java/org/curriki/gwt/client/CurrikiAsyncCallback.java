package org.curriki.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;
import org.curriki.gwt.client.utils.Loading;
import org.curriki.gwt.client.utils.Translator;
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

public class CurrikiAsyncCallback implements AsyncCallback {
    public CurrikiAsyncCallback(){
        if (Main.getSingleton() != null){
            Main.getSingleton().startLoading();
        }
    }
    
    public void onFailure(Throwable caught) {
        if (Main.getSingleton() != null){
            Main.getSingleton().finishLoading();
        }

        if (caught.toString().startsWith("com.google.gwt.user.client.rpc.InvocationException")){
            // TODO: Ignore this error ONLY when changing pages -- otherwise display it
        } else {
            Main.getSingleton().showError(caught);
        }
    }

    public void onSuccess(Object result) {
        if (Main.getSingleton() != null)
            Main.getSingleton().finishLoading();
    }
}
