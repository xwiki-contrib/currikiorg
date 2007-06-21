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

import com.google.gwt.user.client.ui.*;
import org.curriki.gwt.client.Main;

public class PaginationPanel extends HorizontalPanel {
    Hyperlink first = new Hyperlink();
    Hyperlink prev = new Hyperlink();
    Label page = new Label();
    Hyperlink next = new Hyperlink();
    Hyperlink last = new Hyperlink();
    boolean canPrevious = false;
    boolean canNext = false;
    Paginatable callback;
    int count = 0;
    int start = 0;
    int hitcount = 0;
    int lastindex = 0;

    public void init(String style, Paginatable callback){
        // setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        addStyleName(style);
        setVisible(false);

        this.callback = callback;

        ClickListener paginate = new ClickListener(){
            public void onClick(Widget sender){
                changePage(sender);
            }
        };
        first.setText(Main.getTranslation("find.first"));
        first.addStyleName("pagination-item-first");
        first.addStyleName("pagination-item-disabled");
        first.addClickListener(paginate);
        add(first);

        add(new Label(" | "));

        prev.setText(Main.getTranslation("find.prev"));
        prev.addStyleName("pagination-item-prev");
        prev.addStyleName("pagination-item-disabled");
        prev.addClickListener(paginate);
        add(prev);

        add(new Label(" | "));

        page.setText(Main.getTranslation("find.page")+" 1 / 1");
        page.addStyleName("pagination-item-page");
        add(page);

        add(new Label(" | "));

        next.setText(Main.getTranslation("find.next"));
        prev.addStyleName("pagination-item-next");
        next.addStyleName("pagination-item-disabled");
        next.addClickListener(paginate);
        add(next);

        add(new Label(" | "));

        last.setText(Main.getTranslation("find.last"));
        prev.addStyleName("pagination-item-last");
        last.addStyleName("pagination-item-disabled");
        last.addClickListener(paginate);
        add(last);
    }

    private void changePage(Widget sender) {
        int newstart = start;

        if (canPrevious && sender == first){
            newstart = 1;
        } else if (canPrevious && sender == prev){
            newstart = start - count;
        } else if (canNext && sender == next){
            newstart = start + count;
        } else if (canNext && sender == last){
            newstart = lastindex;
        }

        if (newstart != start){
            start = newstart;
            callback.paginate(start);
        }
    }

    public void adjust(int count, int start, int hitcount) {
        this.count = count;
        this.start = start;
        this.hitcount = hitcount;

        int pagecount = hitcount / count + (((hitcount % count) > 0) ? 1 : 0);

        int curpage = (start / count) + 1;

        lastindex = ((pagecount - 1)*count)+1;

        if (start > 1){
            // First and Prev should be enabled
            canPrevious = true;

            first.removeStyleName("pagination-item-disabled");
            prev.removeStyleName("pagination-item-disabled");

            first.addStyleName("pagination-item-enabled");
            prev.addStyleName("pagination-item-enabled");
        } else {
            // First and Prev should be disabled
            canPrevious = false;

            first.removeStyleName("pagination-item-enabled");
            prev.removeStyleName("pagination-item-enabled");

            first.addStyleName("pagination-item-disabled");
            prev.addStyleName("pagination-item-disabled");
        }

        page.setText(Main.getTranslation("find.page")+" "+curpage+" / "+pagecount);

        if (start < lastindex){
            // Next and Last should be enabled
            canNext = true;

            next.removeStyleName("pagination-item-disabled");
            last.removeStyleName("pagination-item-disabled");

            next.addStyleName("pagination-item-enabled");
            last.addStyleName("pagination-item-enabled");
        } else {
            // Next and Last should be disabled
            canNext = false;

            next.removeStyleName("pagination-item-enabled");
            last.removeStyleName("pagination-item-enabled");

            next.addStyleName("pagination-item-disabled");
            last.addStyleName("pagination-item-disabled");
        }

        if (hitcount > 0) {
            setVisible(true);
        } else {
            setVisible(false);
        }
    }
}
