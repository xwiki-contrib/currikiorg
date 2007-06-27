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
package org.curriki.gwt.client.wizard;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.ClickListenerInt;
import org.curriki.gwt.client.widgets.siteadd.AddResourceDialog;
import org.curriki.gwt.client.widgets.siteadd.ChooseCollectionDialog;

public class AddResourceWizard
{
    private static ChooseCollectionDialog collections;
    private static AddResourceDialog resource;
    private String collectionName;

    public void addResource(){
        // 1. Choose a collection to add it to (if more than just the default)
        // 2. Choose type of resource
        ClickListener next = new ClickListener(){
            public void onClick(Widget sender){
                if (collections == null || collections.getSelectedItem() == null || collections.getSelectedItem().getPageName() == null || collections.getSelectedItem().getPageName().equals("__NOSELECT__")){
                    Window.alert(Main.getTranslation("addresource.need_to_select_collection"));
                } else{
                    collections.hide();
                    addResource(collections.getSelectedItem().getPageName());
                }
            }
        };
        ClickListener cancel =  new ClickListener(){
            public void onClick(Widget sender){
                collections.hide();
            }
        };
        collections = new ChooseCollectionDialog(next, cancel);

    }

    public void addResource(String collection) {
        this.collectionName = collection;

        // We have a collection now
        // 2. Choose type of resource and pass to proper wizard
        final ClickListener cancel = new ClickListener(){
            public void onClick(Widget sender){
                resource.hide();
            }
        };

        ClickListenerInt next = new ClickListenerInt(Constants.DIALOG_RESOURCE_TYPE_UNKNOWN){
            public void onClick(Widget sender){
                // We should now know what kind of item it is
                resource.hide();

                switch(this.arg) {
                    case Constants.DIALOG_RESOURCE_TYPE_EXISTING_RESOURCE: // Existing Resource
                        Main.getSingleton().findPopup(collectionName);
                        break;
                    case Constants.DIALOG_RESOURCE_TYPE_FILE: // File
                        Main.getSingleton().addFile(collectionName);
                        break;
                    case Constants.DIALOG_RESOURCE_TYPE_TEMPLATE: // Template
                        Main.getSingleton().addFromTemplate(collectionName);
                        break;
                    default:
                        // Unknown type
                        cancel.onClick(sender);
                        break;
                }
            }
        };

        resource = new AddResourceDialog(collectionName, next, cancel);
    }
}