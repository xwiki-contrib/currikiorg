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

public class ReviewColumn extends ResultsColumn
{
    public ReviewColumn()
    {
        this.header = Main.getTranslation("search.results.col.review");
    }

    public ReviewColumn(String header, String columnStyle)
    {
        super(header, columnStyle);
    }

    public String getDisplayString(Document value)
    {
        String rating = "";

        if (value.getObject(Constants.CURRIKI_REVIEW_CLASS) != null){
            value.use(Constants.CURRIKI_REVIEW_CLASS);
            if (value.get(Constants.CURRIKI_REVIEW_RATING_PROPERTY) != null){
                rating = value.get(Constants.CURRIKI_REVIEW_RATING_PROPERTY);
            }
        }

        return rating;
    }

    public Widget getDisplayWidget(Document value)
    {
        final String url = value.getViewURL()+"?viewer=comments";

        String rating = getDisplayString(value);
        Image img = new Image();
        if (rating.length() > 0){
            if (!rating.equals("0")){
                img = new Image(Constants.ICON_PATH+"CRS"+rating+".png");
                img.addClickListener(new ClickListener() {
                    public void onClick(Widget sender) {
                        Main.changeWindowHref(url);
                    }
                });
            }
            this.addTooltip(img, Main.getTranslation("search.crs.tooltip."+rating));
        }

        return img;
    }
}
