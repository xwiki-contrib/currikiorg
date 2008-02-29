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

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.ClickListener;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.ClickListenerString;
import org.curriki.gwt.client.utils.ClickListenerDocument;
import org.curriki.gwt.client.search.editor.ViewPanel;
import org.curriki.gwt.client.search.editor.Viewer;
import org.curriki.gwt.client.search.editor.ResourceAdder;

import java.util.Map;
import java.util.HashMap;

public class ReviewColumn extends ResultsColumn implements Viewer
{
    protected Viewer viewer;
    protected ResourceAdder wizard;
    protected ClickListener cancelListener = null;

    public ReviewColumn()
    {
        this.header = Main.getTranslation("search.results.col.review");
        this.columnStyle = "results-review-cell";
        this.sortBy = Constants.CURRIKI_REVIEW_STATUS_CLASS+"."+Constants.CURRIKI_REVIEW_STATUS_STATUS;
    }

    public ReviewColumn(String header, String columnStyle)
    {
        super(header, columnStyle);
    }

    public String getDisplayString(Document value)
    {
        String rating = "";

        if (value.getObject(Constants.CURRIKI_REVIEW_STATUS_CLASS) != null){
            value.use(Constants.CURRIKI_REVIEW_STATUS_CLASS);
            Object status = value.getValue(Constants.CURRIKI_REVIEW_STATUS_STATUS);
            if (status != null){
                rating = String.valueOf(status);
            }
        }

        return rating;
    }

    public Widget getDisplayWidget(Document value)
    {
        Image img = null;
        final String url = value.getViewURL()+"?viewer=comments";

        String rating = getDisplayString(value);
        if (rating.length() > 0){
            if (!rating.equals("0")){
                img = new Image(Constants.ICON_PATH+"CRS"+rating+".png");
                if (viewer == null){
                    // If in site
                    img.addClickListener(new ClickListener() {
                        public void onClick(Widget sender) {
                            Main.changeWindowHref(url);
                        }
                    });
                } else {
                    // If in CB
                    img.addClickListener(new ClickListenerDocument(value) {
                        public void onClick(Widget sender) {
                            viewer.displayView(doc);
                        }
                    });
                }
                this.addTooltip(img, Main.getTranslation("search.crs.tooltip."+rating));
            }
        }

        return img;
    }

    public void displayView(Document asset){
        String assetName = asset.getFullName();
        // View needs to put the rendered text in (this) window with ADD/BACK/CANCEL buttons above it

        // Create View Panel
        // Top has ADD/BACK/CANCEL buttons (RHS)
        Map args = new HashMap();
        args.put("viewer", "comments");
        ViewPanel panel = new ViewPanel(new AddAsset(assetName), args);

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
    
    public void setViewer(Viewer viewer){
        this.viewer = viewer;
    }

    public void setResourceAdder(ResourceAdder wizard){
        this.wizard = wizard;
    }

    public void setCancelListener(ClickListener cancelListener){
        this.cancelListener = cancelListener;
    }
}
