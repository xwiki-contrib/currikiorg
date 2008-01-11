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
package org.curriki.gwt.client.wizard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.search.editor.ResourceAdder;
import org.curriki.gwt.client.utils.CompletionCallback;
import org.curriki.gwt.client.widgets.siteadd.ChooseCollectionDialog;
import org.curriki.gwt.client.widgets.siteadd.ThankYouDialog;

public class AddExistingResourceWizard implements CompletionCallback, ResourceAdder {
    private static ChooseCollectionDialog collections;
    private static ThankYouDialog thankYouDialog;
    private String resource;
    private String collectionName;
    private Command callbackCommand;

    public AddExistingResourceWizard(){
        this.collectionName = null;
    }

    public AddExistingResourceWizard(String collectionName)
    {
        this.collectionName = collectionName;
    }

    public void addExistingResource(String resourceName){
        // 1. Choose a collection to add it to (if more than just the default)
        // 2. Add the resource to the collection
        // 3. "Thank You" dialog
        this.resource = resourceName;

        if (collectionName != null){
            CurrikiService.App.getInstance().addCompositeAssetToCollection(resource, collectionName, new addedAssetToCollection());
        } else {
            ClickListener next = new ClickListener(){
                public void onClick(Widget sender){
                    if (collections == null || collections.getSelectedItem() == null || collections.getSelectedItem().getPageName() == null || collections.getSelectedItem().getPageName().equals("__NOSELECT__")){
                        Window.alert(Main.getTranslation("addexistingasset.selectcollection"));
                    } else{
                        CurrikiService.App.getInstance().addCompositeAssetToCollection(resource, collections.getSelectedItem().getPageName(), new addedAssetToCollection());
                    }
                }
            };
            ClickListener cancel =  new ClickListener(){
                public void onClick(Widget sender){
                    if (collections.isAttached()){
                        collections.hide();
                    }
                }
            };
            collections = new ChooseCollectionDialog(next, cancel);
        }
    }

    public void setCompletionCallback(Command cmd)
    {
        callbackCommand = cmd;
    }

    public class addedAssetToCollection extends CurrikiAsyncCallback {
        public void onFailure(Throwable throwable) {
            super.onFailure(throwable);
            //Window.alert(Main.getSingleton().getTranslator().getTranslation("addexistingasset.couldnotaddtocollection", new String[] {resource, throwable.getMessage()}));
            if (collections != null && collections.isAttached()){
                collections.hide();
            }
            collections = null;
        }

        public void onSuccess(Object object) {
            super.onSuccess(object);
            if (!((Boolean) object).booleanValue()){
                Window.alert(Main.getSingleton().getTranslator().getTranslation("addexistingasset.failedtoaddtocollection", new String[] {resource}));
            }
            if (collections != null && collections.isAttached()){
                collections.hide();
            }
            collections = null;

            if (((Boolean) object).booleanValue()){
                ClickListener cancel =  new ClickListener(){
                    public void onClick(Widget sender){
                        thankYouDialog.hide();
                        thankYouDialog = null;
                        if (callbackCommand != null){
                            callbackCommand.execute();
                        }
                    }
                };

                thankYouDialog = new ThankYouDialog(Constants.DIALOG_THANKYOU_ADD_COLLECTION, cancel);
            }
        }
    }
}
