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
package org.curriki.gwt.client.search.panels;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.search.queries.DoesSearch;
import org.curriki.gwt.client.search.queries.Paginator;

public class PaginationPanel extends VerticalPanel implements Paginator
{
    protected Hyperlink first = new Hyperlink();
    protected Hyperlink prev = new Hyperlink();
    protected Label page = new Label();
    protected Hyperlink next = new Hyperlink();
    protected Hyperlink last = new Hyperlink();
    protected boolean canPrevious = false;
    protected boolean canNext = false;
    protected DoesSearch searcher;
    protected int start = 1;
    protected int count = Constants.DIALOG_FIND_FETCH_COUNT;
    protected int hitcount = 0;
    protected int lastindex = 0;
    protected Paginator paginator;
    protected HorizontalPanel pResults = new HorizontalPanel();
    protected HorizontalPanel pNav = new HorizontalPanel();

    public PaginationPanel(){
        paginator = this;
        init("search-pagination");
    }

    public Paginator getPaginator()
    {
        return paginator;
    }

    public void init(String style){
        // setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        addStyleName(style);
        setVisible(false);
        pResults.setStyleName("search-pagination-results");

        pNav.setStyleName("search-pagination-nav");
        ClickListener paginate = new ClickListener(){
            public void onClick(Widget sender){
                changePage(sender);
            }
        };
        first.setText(Main.getTranslation("find.first"));
        first.addStyleName("pagination-item-first");
        first.addStyleName("pagination-item-disabled");
        first.addClickListener(paginate);
        pNav.add(first);

        pNav.add(new Label(" | "));

        prev.setText(Main.getTranslation("find.prev"));
        prev.addStyleName("pagination-item-prev");
        prev.addStyleName("pagination-item-disabled");
        prev.addClickListener(paginate);
        pNav.add(prev);

        pNav.add(new Label(" | "));

        page.setText(Main.getTranslation("find.page")+" 1 / 1");
        page.addStyleName("pagination-item-page");
        pNav.add(page);

        pNav.add(new Label(" | "));

        next.setText(Main.getTranslation("find.next"));
        prev.addStyleName("pagination-item-next");
        next.addStyleName("pagination-item-disabled");
        next.addClickListener(paginate);
        pNav.add(next);

        pNav.add(new Label(" | "));

        last.setText(Main.getTranslation("find.last"));
        prev.addStyleName("pagination-item-last");
        last.addStyleName("pagination-item-disabled");
        last.addClickListener(paginate);
        pNav.add(last);

        add(pResults);
        add(pNav);
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
            if (searcher != null){
                searcher.doSearch();
            }
        }
    }

    public void adjust(int count, int start, int hitcount) {
        this.count = count;
        this.start = start;
        this.hitcount = hitcount;

        if (hitcount < count){
            count = hitcount;
        }

        int pagecount = hitcount / count + (((hitcount % count) > 0) ? 1 : 0);

        int curpage = (start / count) + 1;

        lastindex = ((pagecount - 1)*count)+1;

        pResults.clear();
        pResults.add(new HTML(Main.getTranslation("Results")+" "+start+" - "+(start+count-1)+" of about "+hitcount));

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

    public int getLimit()
    {
        return count;
    }

    public void setLimit(int limit)
    {
        this.count = limit;
    }

    public int getFetchCount()
    {
        return getLimit();
    }

    public int getStart()
    {
        return start;
    }

    public void setSearcher(DoesSearch searcher)
    {
        this.searcher = searcher;
    }
}
