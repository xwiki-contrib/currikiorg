package org.curriki.gwt.client.widgets.modaldialogbox;

import asquare.gwt.tk.client.ui.ModalDialog;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.gwtwidgets.client.util.SimpleDateFormat;

import java.util.Date;

/**
 * Copyright 2006,XpertNet SARL,and individual contributors as indicated
 * by the contributors.txt.
 * <p/>
 * This is free software;you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation;either version2.1of
 * the License,or(at your option)any later version.
 * <p/>
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software;if not,write to the Free
 * Software Foundation,Inc.,51 Franklin St,Fifth Floor,Boston,MA
 * 02110-1301 USA,or see the FSF site:http://www.fsf.org.
 *
 * @author ldubost
 */

public class NominateDialog extends ModalDialog {
    private Document asset;
    private TextArea commentTextArea;
    private AsyncCallback cb;

    public NominateDialog(Document asset, AsyncCallback cb) {
        this.asset = asset;
        this.cb = cb;
        init();
    }

    public NominateDialog(String assetName, AsyncCallback cb) {
        this.cb = cb;
        CurrikiService.App.getInstance().getDocument(assetName, true, false, new CurrikiAsyncCallback() {
            public void onSuccess(Object result) {
                super.onSuccess(result);
                asset = (Document) result;
                init();
            }
        });
    }

    public NominateDialog() {
    }

    public String getCSSName(String name) {
        if ((name==null)||name.equals(""))
            return "crs_nominate_" + name;
        else
            return "crs_nominate";
    }

    public void init(){
        addStyleName(getCSSName(""));
        setCaption(Main.getTranslation("curriki.crs.nominate"), false);

        FlowPanel introTitlePanel = new FlowPanel();
        introTitlePanel.addStyleName(getCSSName("subtitle"));
        introTitlePanel.add(new HTML(Main.getTranslation("curriki.crs.nominate.nominatefollowingresourceforreview")));
        add(introTitlePanel);

        FlowPanel pagenamePanel = new FlowPanel();
        pagenamePanel.addStyleName(getCSSName("pagename"));
        XObject assetObj = asset.getObject(Constants.ASSET_CLASS);
        pagenamePanel.add(new HTML((assetObj==null) ? "" : (String) assetObj.get(Constants.ASSET_TITLE_PROPERTY)));
        add(pagenamePanel);

        FlowPanel commentTitlePanel = new FlowPanel();
        commentTitlePanel.addStyleName(getCSSName("subtitle"));
        commentTitlePanel.add(new HTML(Main.getTranslation("curriki.crs.nominate.comments")));
        add(commentTitlePanel);

        FlowPanel commentTextPanel = new FlowPanel();
        commentTextPanel.addStyleName(getCSSName("commentstext"));
        commentTextPanel.add(new HTML(Main.getTranslation("curriki.crs.nominate.commentstext")));
        add(commentTextPanel);

        commentTextArea = new TextArea();
        commentTextArea.addStyleName(getCSSName("commentstextarea"));
        commentTextArea.setCharacterWidth(80);
        commentTextArea.setVisibleLines(8);
        add(commentTextArea);

        FlowPanel commentFooterPanel = new FlowPanel();
        commentFooterPanel.addStyleName(getCSSName("commentstext"));
        commentFooterPanel.add(new HTML(Main.getTranslation("curriki.crs.nominate.commentsfootertext")));
        add(commentFooterPanel);

        Button submitButton = new Button();
        submitButton.addStyleName(getCSSName("submitbutton"));
        submitButton.setText(Main.getTranslation("curriki.crs.nominate.submit"));
        submitButton.addStyleName("gwt-bttNext");
        submitButton.addStyleName("gwt-ButtonOrange");
        submitButton.addClickListener(new ClickListener(){
            public void onClick(Widget sender){
                if (commentTextArea.getText().equals("")) {
                    Window.alert(Main.getTranslation("curriki.crs.nominate.cannotnominatewithoutcomment"));
                } else {
                    XObject obj = asset.getObject(Constants.CURRIKI_REVIEW_STATUS_CLASS);
                    CurrikiAsyncCallback saveCallback = new CurrikiAsyncCallback() {
                        public void onFailure(Throwable caught) {
                            super.onFailure(caught);
                            Main.getSingleton().showError(caught);
                            hide();
                        }

                        public void onSuccess(Object result) {
                            super.onSuccess(result);
                            cb.onSuccess("");
                            hide();
                        }
                    };

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String sdate = formatter.format(new Date());
                    String suser =  Main.getSingleton().getUser().getFullName();

                    if (obj==null) {
                        obj = new XObject();
                        obj.setClassName(Constants.CURRIKI_REVIEW_STATUS_CLASS);
                        obj.setName(asset.getFullName());
                        obj.setNumber(0);
                        obj.set("nomination_user", suser);
                        obj.set("nomination_date", sdate);
                        obj.set("nomination_comment", commentTextArea.getText());
                        obj.set("reviewpending", "1");
                        CurrikiService.App.getInstance().addObject(asset.getFullName(), obj, saveCallback);
                    } else {
                        obj.set("nomination_user", suser);
                        obj.set("nomination_date", sdate);
                        obj.set("nomination_comment", commentTextArea.getText());
                        obj.set("reviewpending", "1");
                        CurrikiService.App.getInstance().saveObject(obj, saveCallback);
                    }
                }
            }
        });
        add(submitButton);


        Button cancelButton = new Button();
        cancelButton.setText(Main.getTranslation("curriki.crs.nominate.cancel"));
        cancelButton.addStyleName(getCSSName("cancelbutton"));
        cancelButton.addStyleName("gwt-bttCancel");
        cancelButton.addStyleName("gwt-ButtonGrey");
        cancelButton.addClickListener(new ClickListener(){
            public void onClick(Widget sender){
                cb.onFailure(null);
                hide();
            }
        });
        add(cancelButton);
        show();
    }
}