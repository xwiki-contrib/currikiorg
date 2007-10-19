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
package org.curriki.gwt.client.search.columns;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.search.editor.ResourceAdder;
import org.curriki.gwt.client.utils.ClickListenerString;
import org.curriki.gwt.client.utils.StyleOnMouseover;

public class ActionColumn extends ResultsColumn
{
    protected ResourceAdder resourceAdder;
    protected ClickListener cancelListener;
    protected StyleOnMouseover hoverMarker = new StyleOnMouseover();

    public ActionColumn()
    {
        this.header = Main.getTranslation("search.results.col.action");
        this.columnStyle = "results-action-cell";
    }

    public ActionColumn(String header, String columnStyle)
    {
        super(header, columnStyle);
    }

    public void setResourceAdder(ResourceAdder resourceAdder){
        this.resourceAdder = resourceAdder;
    }

    public void setCancelListener(ClickListener cancelListener){
        this.cancelListener = cancelListener;
    }

    public Widget getDisplayWidget(Document value)
    {
        if ((resourceAdder != null) && (cancelListener != null) &&
            (Main.getSingleton().getUser() != null) &&
            !(Main.getSingleton().getUser().getFullName().equals(Constants.USER_XWIKI_GUEST))){
            HTML a = new HTML();
            a.addStyleName("results-action-cell-link");
            a.setHTML(Main.getTranslation("editor.btt_add"));
            a.addClickListener(new AddAsset(value.getFullName()));
            a.addMouseListener(hoverMarker);
            return a;
        }

        return null;
    }

    private class AddAsset extends ClickListenerString
    {
        public AddAsset(String arg) {
            super(arg);
        }

        public void onClick(Widget sender) {
            resourceAdder.addExistingResource(arg);
            if (cancelListener != null){
                cancelListener.onClick(sender);
            }
        }
    }
}
