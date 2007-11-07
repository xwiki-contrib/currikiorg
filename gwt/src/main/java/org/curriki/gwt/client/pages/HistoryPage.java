package org.curriki.gwt.client.pages;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.VersionInfo;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.utils.WindowUtils;
import org.curriki.gwt.client.editor.Editor;
import org.curriki.gwt.client.widgets.modaldialogbox.ChoiceDialog;
import org.curriki.gwt.client.widgets.preview.PreviewDialog;
import org.gwtwidgets.client.util.SimpleDateFormat;

import java.util.List;
import java.util.Date;
/**
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software;you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation;either version2.1of
 * the License,or(at your option)any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software;if not,write to the Free
 * Software Foundation,Inc.,51 Franklin St,Fifth Floor,Boston,MA
 * 02110-1301 USA,or see the FSF site:http://www.fsf.org.
 *
 * @author ldubost
 */

/**
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software;you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation;either version2.1of
 * the License,or(at your option)any later version.
 *
 *This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software;if not,write to the Free
 * Software Foundation,Inc.,51 Franklin St,Fifth Floor,Boston,MA
 * 02110-1301 USA,or see the FSF site:http://www.fsf.org.
 *
 * @author ldubost
 */

public class HistoryPage extends AbstractPage {
    private int start = 0;
    private String toSelection = "";
    private String fromSelection = "";
    private boolean isComposite = false;

    public HistoryPage(){
        panel.setStyleName("history-page");
        initWidget(panel);
    }

    public void init() {
        super.init();
        panel.clear();
        loadHistory(0);
    }

    public boolean isSourceAssetPage() {
        return true;
    }

    private void loadHistory(int newStart) {
        start = newStart;
        Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
        isComposite = (currentAsset.getObject(Constants.COMPOSITEASSET_CLASS) != null);

        CurrikiService.App.getInstance().getDocumentVersions(currentAsset.getFullName(), Constants.DEFAULT_NB_VERSIONS, start, new CurrikiAsyncCallback() {
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                panel.clear();
                panel.add(new HTML(Main.getTranslation("history.errorgettinghistory")));
            }

            public void onSuccess(Object result) {
                super.onSuccess(result);
                panel.clear();
                refreshHistory((List) result);
            }
        });
    }


    class MyRadioButton extends RadioButton {
        private String value;

        public MyRadioButton(String name, String text, String value) {
            super(name, text);
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private void refreshHistory(List versionsList) {
        String dateFormat = "MMM-dd-yy hh:mm a";
        panel.clear();
        FlexTable table = new FlexTable();
        panel.add(table);
        table.setStyleName("history-versions");
        int startCol = 0;
        if (!isComposite) {
            table.setText(0, 0, Main.getTranslation("history.to"));
            table.setText(0, 1, Main.getTranslation("history.from"));
            table.getCellFormatter().setStyleName(0, 0, "to");
            table.getCellFormatter().setStyleName(0, 1, "from");
            startCol = 2;
        }
        table.getRowFormatter().setStyleName(0, "history-versions-title");
        table.setText(0, startCol, Main.getTranslation("history.version"));
        table.setText(0, startCol + 1, Main.getTranslation("history.author"));
        table.setText(0, startCol + 2, Main.getTranslation("history.date"));
        table.setText(0, startCol + 3, Main.getTranslation("history.comment"));
        table.setText(0, startCol + 4, Main.getTranslation("history.rollback"));
        table.getCellFormatter().setStyleName(0, startCol, "version");
        table.getCellFormatter().setStyleName(0, startCol + 1, "author");
        table.getCellFormatter().setStyleName(0, startCol + 2, "date");
        table.getCellFormatter().setStyleName(0, startCol + 3, "comment");
        table.getCellFormatter().setStyleName(0, startCol + 4, "rollback");

        for (int row = 0;row < versionsList.size(); row++) {
            final VersionInfo vinfo = (VersionInfo) versionsList.get(row);
            final String version = vinfo.getVersion();
            startCol = 0;
            if (!isComposite) {
                MyRadioButton toButton = new MyRadioButton("to", "", version);
                toButton.addClickListener(new ClickListener() {
                    public void onClick(Widget widget) {
                        toSelection = ((MyRadioButton) widget).getValue();
                    }
                });
                table.setWidget(row+1, 0, toButton);
                MyRadioButton fromButton = new MyRadioButton("from", "", version);
                fromButton.addClickListener(new ClickListener() {
                    public void onClick(Widget widget) {
                        fromSelection = ((MyRadioButton) widget).getValue();
                    }
                });
                table.setWidget(row+1, 1, fromButton);
                startCol = 2;
                table.getCellFormatter().setStyleName(row+1, 0, "to");
                table.getCellFormatter().setStyleName(row+1, 1, "from");
            }
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            String sdate = formatter.format(new Date(vinfo.getDate()));
            Hyperlink link = new Hyperlink();
            link.setStyleName("history-versionlink");
            link.setText(version);
            link.addClickListener(new ClickListener() {
                public void onClick(Widget widget) {
                    // viewing older version of a document.
                    Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
                    String url = currentAsset.getViewURL() + "?xpage=viewrev&rev=" + version;
                    if ("dialog".equals(WindowUtils.getLocation().getParameter("preview")))
                        PreviewDialog.show(url);
                    else
                        Window.open(url, "_blank", "");
                }
            });
            table.setWidget(row+1, startCol, link);
            if (!vinfo.getAuthor().equals("WebHome"))  {
                Hyperlink authorLink = new Hyperlink();
                authorLink.setStyleName("history-authorlink");
                authorLink.setText(vinfo.getAuthor());
                authorLink.addClickListener(new ClickListener() {
                    public void onClick(Widget widget) {
                        // Rollback has been accepted
                        Window.open(vinfo.getAuthorLink(), "_blank", "");
                    }
                });                
                table.setWidget(row+1, startCol + 1, authorLink);
            }

            table.setText(row+1, startCol + 2, sdate);
            String comment = vinfo.getComment();
            comment = comment.replaceAll("<a", "<a target=\"_blank\"");
            table.setHTML(row+1, startCol + 3, comment);

            table.getCellFormatter().setStyleName(row+1, startCol, "version");
            table.getCellFormatter().setStyleName(row+1, startCol + 1, "author");
            table.getCellFormatter().setStyleName(row+1, startCol + 2, "date");
            table.getCellFormatter().setStyleName(row+1, startCol + 3, "comment");

            if (row!=0) {
                Button button = new Button();
                button.setText(Main.getTranslation("history.rollback"));
                button.setStyleName("history-rollback-button");
                button.addClickListener(new ClickListener() {
                    public void onClick(Widget widget) {
                        Button cancelRollback = new Button(Main.getTranslation("history.rollback.cancel"));
                        Button confirmRollback = new Button(Main.getTranslation("history.rollback.confirm"), new ClickListener(){
                            public void onClick(Widget widget)
                            {
                                // Rollback has been accepted
                                Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
                                String url = currentAsset.getViewURL().replaceAll("/view/", "/rollback/") + "?rev=" + version + "&confirm=1";
                                HTTPRequest.asyncGet(url, new ResponseTextHandler() {
                                    public void onCompletion(String string) {
                                        if (string.toLowerCase().indexOf("exception")!=-1) {
                                            Window.alert(Main.getTranslation("history.rollback.exception"));
                                        } else {
                                            Editor editor = Main.getSingleton().getEditor();
                                            editor.setCurrentAssetInvalid(true);
                                            // Tree is invalid when it is a composite asset that is rolledback
                                            editor.setTreeContentInvalid(true);
                                            editor.resetCache();
                                            editor.refreshState();
                                        }
                                    }
                                });
                            }
                        });
                        Button[] buttons = { cancelRollback, confirmRollback };
                        String[] args = { version };
                        ChoiceDialog rollbackProposal = new ChoiceDialog(Main.getTranslation("history.rollback.confirm"),
                                Main.getSingleton().getTranslator().getTranslation("history.rollback.confirm.message", args),
                                buttons, "history-rollback-confirm-dialog");
                    }
                });
                table.setWidget(row+1, startCol + 4, button);
                table.getCellFormatter().setStyleName(row+1, startCol + 4, "rollback");
            }
        }

        // Adding compare button
        if (!isComposite) {
            Button button = new Button();
            button.setText(Main.getTranslation("history.compare"));
            button.setStyleName("history-compare-button");
            button.addClickListener(new ClickListener() {
                public void onClick(Widget widget)
                {
                    // Rollback has been accepted
                    Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
                    String url = currentAsset.getViewURL() + "?xpage=compare&rev1=" + fromSelection + "&rev2=" + toSelection;
                    Window.open(url, "_blank", "");
                }
            });
            panel.add(button);
        }
    }
}
