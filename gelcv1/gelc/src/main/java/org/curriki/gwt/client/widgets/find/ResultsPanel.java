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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.ClickListenerDocument;
import org.curriki.gwt.client.utils.ClickListenerString;
import org.gwtwidgets.client.util.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ResultsPanel extends VerticalPanel implements Searchable, Paginatable, Viewer {
    protected FlexTable g = new FlexTable();
    protected ResourceAdder wizard = null;
    protected PaginationPanel p = new PaginationPanel();
    protected ClickListener cancelListener = null;
    protected Viewer viewer = null;
    protected int start = 1;
    protected int firstLine = 0;
    protected int line = 0;
    protected int hitcount = 0;
    protected String[] chosen;

    public ResultsPanel() {
    }

    public void init(ResourceAdder wizardPanel, ClickListener cancelListener) {
        init(wizardPanel, cancelListener, this);
    }

    public void init(ResourceAdder wizardPanel, ClickListener cancelListener, Viewer viewer) {
        wizard = wizardPanel;
        this.cancelListener = cancelListener;
        if (viewer == null){
            this.viewer = this;
        } else {
            this.viewer = viewer;
        }

        // setWidth("100%");
        addStyleName("find-results-panel");
        //setVisible(false);

        // Above main results panel
        // Title
        Label title = new Label(Main.getTranslation("find.results"));
        title.addStyleName("find-results-title");
        add(title);

        // In main results panel
        /*
        RoundedPanel rp = new RoundedPanel();
        rp.addStyleName("find-results-container");
        rp.setWidth("100%");
        */

        VerticalPanel main = new VerticalPanel();
        main.addStyleName("find-results");
        // main.setWidth("100%");
        //rp.add(main);

        setupResultsHeader();

        ScrollPanel scroller = new ScrollPanel();
        //scroller.setAlwaysShowScrollBars(true);
        //scroller.setHeight("400px");
        scroller.addStyleName("find-results-scroller");
        scroller.add(g);
        main.add(scroller);

        line++;
        firstLine = line;

        // Pagination
        // main.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        p.init("find-results-pagination", this);
        //p.setVisible(false);
        FlowPanel pagination = new FlowPanel();
        pagination.addStyleName("find-results-pagination-wrapper");
        pagination.add(p);

        main.add(pagination);

        add(main);
    }

    private void setupResultsHeader() {
        // Results Title bar

        // Learning Resource, Type, Published Date, Contributor, Action

        // g.setWidth("100%");
        g.addStyleName("find-results-table");
        g.getRowFormatter().addStyleName(line, "find-results-table-title");

        // TODO: Add sorting
        /*
        Hyperlink link = new Hyperlink();
        link.setText(Main.getTranslation("Learning Resource"));
        link.addClickListener(new ClickListenerString(link.getText()) {
            public void onClick(Widget sender) {
                // TODO: Sort by Name
                Window.alert("TODO: Sort by "+arg);
            }
        });
        */
        Label col = new Label(Main.getTranslation("find.learning_resource"));
        g.getColumnFormatter().addStyleName(0, "results-resource-col");
        g.setWidget(line, 0, col);

        /*
        link = new Hyperlink();
        link.setText(Main.getTranslation("Type"));
        link.addClickListener(new ClickListenerString(link.getText()) {
            public void onClick(Widget sender) {
                // TODO: Sort by Type
                Window.alert("TODO: Sort by "+arg);
            }
        });
        */
        col = new Label(Main.getTranslation("find.type"));
        g.getColumnFormatter().addStyleName(1, "results-type-col");
        g.setWidget(line, 1, col);

        col = new Label(Main.getTranslation("find.published_date"));
        g.getColumnFormatter().addStyleName(2, "results-published-col");
        g.setWidget(line, 2, col);

        col = new Label(Main.getTranslation("find.contributor"));
        g.getColumnFormatter().addStyleName(3, "results-contributor-col");
        g.setWidget(line, 3, col);

        col = new Label(Main.getTranslation("find.action"));
        g.getColumnFormatter().addStyleName(4, "results-action-col");
        g.setWidget(line, 4, col);

    }

    public void search(Selector sender){
        // chosen[] == terms, framework item, level, type
        chosen = sender.getChosen();

        setVisible(true);
        paginate(1);
    }

    public void paginate(int firstItem){
        start = firstItem;

        getResults(chosen);
    }

    public void getResults(String[] chosen){
        // chosen[] == terms, framework item, level, type

        String searchTerm = "XWiki.AssetClass."+Constants.ASSET_FW_ITEMS_PROPERTY+":FW_masterFramework.WebHome";
        if (chosen[0].length() > 0){
            searchTerm += " AND "+chosen[0];
        }
        if (chosen[1].length() > 0){
            searchTerm += " AND XWiki.AssetClass."+Constants.ASSET_FW_ITEMS_PROPERTY+":"+chosen[1];
        }
        if (chosen[2].length() > 0){
            searchTerm += " AND XWiki.AssetClass."+Constants.ASSET_EDUCATIONAL_LEVEL_PROPERTY+":"+chosen[2];
        }
        if (chosen[3].length() > 0){
            searchTerm += " AND XWiki.AssetClass." + Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY + ":"+chosen[3];
        }
        searchTerm += " AND NOT web:AssetTemp AND NOT web:Coll_Templates AND NOT name:WebHome AND NOT name:WebPreferences AND NOT name:MyCollections AND NOT name:SpaceIndex";
        CurrikiService.App.getInstance().luceneSearch(searchTerm, start, Constants.DIALOG_FIND_FETCH_COUNT, new populateResultsCallback(this));
    }

    public class populateResultsCallback extends CurrikiAsyncCallback {
        ResultsPanel list;

        public populateResultsCallback(ResultsPanel list) {
           this.list = list;
        }

        public void onFailure(Throwable throwable) {
            super.onFailure(throwable);

            hitcount = 0;
            p.adjust(Constants.DIALOG_FIND_FETCH_COUNT, start, hitcount);
        }

        public void onSuccess(Object object) {
            super.onSuccess(object);
            List results = (List) object;
            Iterator i = results.iterator();

            list.clear();

            // First item in results is hitcount
            hitcount = ((Integer) i.next()).intValue();

            if (hitcount == 0){
                g.getRowFormatter().addStyleName(line, "find-results-table-result");
                g.getFlexCellFormatter().addStyleName(line, 0, "find-results-table-noresults");
                g.getFlexCellFormatter().setColSpan(line, 0, 5);
                g.setText(line, 0, Main.getTranslation("find.no_results"));
            } else {
                g.getFlexCellFormatter().setColSpan(line, 0, 1);
                g.getFlexCellFormatter().removeStyleName(line, 0, "find-results-table-noresults");
            }

            while (i.hasNext()){
                Document item = (Document) i.next();
                list.addItem(item);
            }

            p.adjust(Constants.DIALOG_FIND_FETCH_COUNT, start, hitcount);
        }
    }

    public void clear(){
        line = firstLine;

        int cols = g.getCellCount(0);
        int rows = g.getRowCount();
        for (int i=line;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                g.setText(i, j, "");
            }
        }
    }

    public void addItem(Document value){
        int colNum = 0;

        // Row has: Learning Resource, Type, Published Date, Contributor, Action
        g.getRowFormatter().addStyleName(line, "find-results-table-result");

        // Learning Resource
        Label nameCol = new Label();
        String name = (value.getTitle().length() > 0) ? value.getTitle() : value.getName();
        if (name.length() > 40){
            nameCol.setTitle(name);
            name = name.substring(0, 39)+"...";
        }
        nameCol.setText(name);
        g.getFlexCellFormatter().addStyleName(line, colNum, "results-resource-cell");
        g.setWidget(line, colNum++, nameCol);
        if (value.getObject(Constants.ASSET_CLASS) != null){
            Document assetDoc = value;
            assetDoc.use(Constants.ASSET_CLASS);
            if (assetDoc.get("title") != null){
                name = assetDoc.get("title");
                if (name.length() > 40){
                    nameCol.setTitle(name);
                    name = name.substring(0, 39)+"...";
                }
                if (name.length() > 0){
                    nameCol.setText(name);
                }
            }
        }

        // Type
        String icon = value.getFormat();
        if (icon.equals("composite") || icon.equals("block")){
            icon = Constants.MIMETYPE_CURIKULUM_ICON;
        }
        if (icon != null){
            icon = "<IMG SRC=\""+Constants.MIMETYPE_PATH+icon+".png\" />";
        }
        g.getFlexCellFormatter().addStyleName(line, colNum, "results-type-cell");
        g.setHTML(line, colNum++, icon);

        // Published Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd-yy");
        g.getFlexCellFormatter().addStyleName(line, colNum, "results-published-cell");
        g.setText(line, colNum++, dateFormat.format(new Date(value.getDate())));

        // Contributor -- creator
        g.getFlexCellFormatter().addStyleName(line, colNum, "results-contributor-cell");
        if (value.getCreator() != null){
            g.setText(line, colNum++, value.getCreator());
        } else {
            g.setText(line, colNum++, "");
        }

        // Action panel
        HorizontalPanel actions = new HorizontalPanel();
        actions.addStyleName("find-result-actions");

        // TODO: Inserting "Add" action/listener still needs to be worked out better
        if (wizard != null){
            Button a = new Button(Main.getTranslation("editor.btt_add"), new AddAsset(value.getFullName()));
            actions.add(a);
        }

        Button v = new Button(Main.getTranslation("editor.btt_view"), new ClickListenerDocument(value) {
            public void onClick(Widget sender) {
                viewer.displayView(doc);
            }
        });
        //HTML v = new HTML("<a href=\""+value.getViewURL()+"\">View</a>");
        actions.add(v);
        g.getFlexCellFormatter().addStyleName(line, colNum, "results-action-cell");
        g.setWidget(line, colNum++, actions);
        
        line++;
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

    private class AddAsset extends ClickListenerString {
        public AddAsset(String arg) {
            super(arg);
        }

        public void onClick(Widget sender) {
            cancelListener.onClick(sender);
            wizard.addExistingResource(arg);
        }
    }
}
