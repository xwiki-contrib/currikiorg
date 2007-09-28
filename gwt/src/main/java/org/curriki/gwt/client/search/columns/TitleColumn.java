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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.ClickListener;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.search.editor.Viewer;
import org.curriki.gwt.client.search.editor.ViewPanel;
import org.curriki.gwt.client.search.editor.ResourceAdder;
import org.curriki.gwt.client.utils.ClickListenerDocument;
import org.curriki.gwt.client.utils.ClickListenerString;
import org.curriki.gwt.client.widgets.metadata.TooltipMouseListener;

public class TitleColumn extends ResultsColumn implements Viewer
{
    protected int maxLength = 78;
    protected int maxDescLength = 78;
    protected Viewer viewer;
    protected ResourceAdder wizard;
    protected ClickListener cancelListener = null;

    public TitleColumn()
    {
        this.header = Main.getTranslation("search.results.col.title");
        this.columnStyle = "results-title-cell";
        this.sortBy = Constants.ASSET_CLASS+"."+Constants.ASSET_TITLE_PROPERTY;
    }

    public TitleColumn(String header, String columnStyle)
    {
        super(header, columnStyle);
        this.sortBy = Constants.ASSET_CLASS+"."+Constants.ASSET_TITLE_PROPERTY;
    }

    public TitleColumn(ResourceAdder wizard, Viewer viewer, ClickListener cancelListener){
        this();
        this.wizard = wizard;
        this.viewer = viewer;
        this.cancelListener = cancelListener;
    }

    public void setViewer(Viewer viewer){
        this.viewer = viewer;
    }

    public void setResourceAdder(ResourceAdder wizard){
        this.wizard = wizard;
    }

    public void setCancelListener(ClickListener cancelListener){
        this.cancelListener = cancelListener;
    }

    public String getDisplayString(Document value)
    {
        String name = (value.getTitle().length() > 0) ? value.getTitle() : value.getName();
        
        if (value.getObject(Constants.ASSET_CLASS) != null){
            value.use(Constants.ASSET_CLASS);
            if (value.get(Constants.ASSET_TITLE_PROPERTY) != null){
                name = value.get(Constants.ASSET_TITLE_PROPERTY);
                if (name.length() > maxLength){
                    name = name.substring(0, (maxLength-1))+"...";
                }
                if (name.length() > 0){
                }
            }
        }
        return name;
    }

    public Widget getDisplayWidget(Document value)
    {
        HTML nameCol = new HTML();
        String url = value.getViewURL();
        String name = (value.getTitle().length() > 0) ? value.getTitle() : value.getName();
        String desc = "";

        if (name.length() > maxLength){
            name = name.substring(0, (maxLength-1))+"...";
        }

        if (value.getObject(Constants.ASSET_CLASS) != null){
            value.use(Constants.ASSET_CLASS);
            if (value.get(Constants.ASSET_TITLE_PROPERTY) != null){
                String title = value.get(Constants.ASSET_TITLE_PROPERTY);
                title = title.replaceAll("^ *", "");
                title = title.replaceAll(" *$", "");
                if (title.length() > 0){
                    name = title;
                    if (name.length() > maxLength){
                        name = name.substring(0, (maxLength-1))+"...";
                    }
                }
            }

            if (value.get(Constants.ASSET_DESCRIPTION_PROPERTY) != null){
                desc = value.get(Constants.ASSET_DESCRIPTION_PROPERTY);
                if (desc.length() > maxDescLength){
                    desc = desc.substring(0, (maxDescLength-1))+"...";
                }
            }
        }

        if (name.length() < 1){
            name = Main.getTranslation("search.unknown_title");
        }
        
        if (desc.length() > 0){
            addTooltip(nameCol, "<b>Description:</b><br>"+desc);
        }

        if (viewer == null){
            // If in site
            nameCol.setHTML("<a href=\""+url+"\">"+name+"</a>");
        } else {
            // If in CB
            nameCol.setHTML(name);
            nameCol.addClickListener(new ClickListenerDocument(value) {
                public void onClick(Widget sender) {
                    viewer.displayView(doc);
                }
            });
        }

        return nameCol;
    }

    public void displayView(Document asset){
        String assetName = asset.getFullName();
        // View needs to put the rendered text in (this) window with ADD/BACK/CANCEL buttons above it

        // Create View Panel
        // Top has ADD/BACK/CANCEL buttons (RHS)
        ViewPanel panel = new ViewPanel(new AddAsset(assetName));

        // Bottom has the rendered version of the asset (unclickable)
        panel.displayResource(assetName);
    }

    private class AddAsset extends ClickListenerString
    {
        public AddAsset(String arg) {
            super(arg);
        }

        public void onClick(Widget sender) {
            if (wizard != null){
                if (cancelListener != null){
                    cancelListener.onClick(sender);
                }
                wizard.addExistingResource(arg);
            }
        }
    }

    protected void addTooltip(SourcesMouseEvents item, String text) {
        PopupPanel popup = new PopupPanel(true);
        popup.setStyleName("search-title-description-popup");
        popup.setWidth("300px");
        popup.add(new HTML(text));
        item.addMouseListener(new TooltipMouseListener(popup, 234, (Widget) item));
    }
}
