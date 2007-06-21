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
package org.curriki.gwt.client.widgets.siteadd;

import asquare.gwt.tk.client.ui.BasicPanel;
import asquare.gwt.tk.client.ui.ModalDialog;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.ClickListenerInt;

public class AddResourceDialog extends ModalDialog {
    ClickListenerInt nextCallback;
    ClickListener cancelCallback;
    String collectionName;

    private RadioButton fRepository;
    private RadioButton fFile;
    private RadioButton fTemplate;

    public AddResourceDialog(String collectionName, ClickListenerInt nextCallback, ClickListener cancelCallback) {
        this.nextCallback = nextCallback;
        this.collectionName = collectionName;
        this.cancelCallback = cancelCallback;
        init();
    }

    public void init(){
        addStyleName("dialog-addresource");
        //addController(new ModalDialog.DragStyleController(this));
        setCaption(Main.getTranslation("addresource.add_a_learning_resource"), false);

        BasicPanel main = new BasicPanel();
        main.addStyleName("dialog-addresource-content");

        Grid bottom = new Grid(2, 2);
        bottom.addStyleName("dialog-addresource-bottom");
        bottom.getColumnFormatter().addStyleName(0, "addresource-dialog-col1");
        bottom.getColumnFormatter().addStyleName(1, "addresource-dialog-col2");


        VerticalPanel chooser = new VerticalPanel();
        chooser.addStyleName("dialog-addresource-chooser");
        HTML sText = new HTML(Main.getTranslation("addresource.what_type_of_resource"));
        sText.addStyleName("dialog-addresource-heading");
        sText.addStyleName("curriki-title");
        fRepository = new RadioButton("from", Main.getTranslation("addresource.from_repository"));
        fFile = new RadioButton("from", Main.getTranslation("addresource.from_file_or_link"));
        fTemplate = new RadioButton("from", Main.getTranslation("addresource.from_template"));

        fRepository.setChecked(true);

        chooser.add(sText);
        chooser.add(fRepository);
        chooser.add(fFile);
        chooser.add(fTemplate);

        BasicPanel actions = new BasicPanel();
        actions.addStyleName("addresource-dialog-actions");

        ClickListener nextListener = new ClickListener(){
            public void onClick(Widget sender){
                if (fRepository.isChecked()){
                    nextCallback.setArg(Constants.DIALOG_RESOURCE_TYPE_EXISTING_RESOURCE);
                    nextCallback.onClick(sender);
                } else if (fFile.isChecked()) {
                    nextCallback.setArg(Constants.DIALOG_RESOURCE_TYPE_FILE);
                    nextCallback.onClick(sender);
                } else if (fTemplate.isChecked()) {
                    nextCallback.setArg(Constants.DIALOG_RESOURCE_TYPE_TEMPLATE);
                    nextCallback.onClick(sender);
                } else {
                    Window.alert(Main.getTranslation("addresource.please_make_a_selection"));
                }
            }
        };

        Button cancel = new Button(Main.getTranslation("editor.btt_cancel"), cancelCallback);
        cancel.addStyleName("dialog-cancel");
        cancel.addStyleName("addresource-dialog-cancel");
        actions.add(cancel);

        Button next = new Button(Main.getTranslation("editor.btt_next"), nextListener);
        next.addStyleName("dialog-next");
        next.addStyleName("addresource-dialog-next");
        actions.add(next);


        bottom.setWidget(0, 0, chooser);
        bottom.setWidget(1, 1, actions);

        main.add(bottom);

        add(main);
        show();
    }
}