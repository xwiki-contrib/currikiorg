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
package org.curriki.gwt.client.widgets.find;

import asquare.gwt.tk.client.ui.ModalDialog;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.ClickListenerListener;

public class ViewPanel extends VerticalPanel {
    ScrollPanel view;
    ModalDialog dialog;

    public ViewPanel(ClickListener act_add) {
        initPanel(act_add);
    }

    private void initPanel(ClickListener act_add) {
        VerticalPanel main = new VerticalPanel();
        main.addStyleName("view-main");
        // main.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        // Top has ADD/BACK/CANCEL buttons (RHS)
        HorizontalPanel actions = new HorizontalPanel();
        // actions.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        actions.addStyleName("view-actions");
        Button a = new Button(Main.getTranslation("editor.btt_add"), new ClickListenerListener(act_add) {
            public void onClick(Widget sender) {
                dialog.hide();
                act.onClick(sender);
            }
        });
        actions.add(a);
        Button b = new Button(Main.getTranslation("editor.btt_back"), new ClickListener() {
            public void onClick(Widget sender) {
                dialog.hide();
            }
        });
        actions.add(b);

        main.add(actions);

        // The rendered view of the asset -- non-clickable
        view = new ScrollPanel();
        view.sinkEvents(Event.ONCLICK | Event.ONDBLCLICK | Event.BUTTON_LEFT | Event.MOUSEEVENTS);
        view.addStyleName("view-rendered");
        //view.setAlwaysShowScrollBars(true);
        // view.setWidth("600px");
        // view.setHeight("500px");
        main.add(view);

        add(main);

        dialog = new ModalDialog();
        dialog.addController(new ModalDialog.DragStyleController(dialog));
        dialog.setCaption(Main.getTranslation("find.preview"), false);
        dialog.add(this);

        //RootPanel.get().add(dialog);
    }

    public void displayResource(String resourceName){
        // Fetch resource to display
        CurrikiService.App.getInstance().getDocumentContent(resourceName, true, new displayRenderedContent(view));
    }

    public class displayRenderedContent extends CurrikiAsyncCallback {
        Panel view;

        public displayRenderedContent(Panel view){
            this.view = view;
        }

        public void onFailure(Throwable throwable) {
            super.onFailure(throwable);
        }

        public void onSuccess(Object object) {
            super.onSuccess(object);
            String content = (String) object;

            HTML contentHTML = new HTML(content);
            contentHTML.addStyleName("find-preview-html");

            view.add(contentHTML);

            dialog.show();

            // Now cover the content
            // Note that the value given by getOffsetHeight() DOES NOT include margin + offset like the javadoc says
            // so we are accounting for an extra 23 (13 Top Offset, 10 Bottom Margin) here
            Element cover = DOM.createDiv();
            DOM.setStyleAttribute(cover, "position", "relative");
            DOM.setStyleAttribute(cover, "top", "-"+Integer.toString(contentHTML.getOffsetHeight()+23)+"px");
            DOM.setStyleAttribute(cover, "height", Integer.toString(contentHTML.getOffsetHeight()+23)+"px");
            DOM.appendChild(view.getElement(), cover);
        }
    }
}
