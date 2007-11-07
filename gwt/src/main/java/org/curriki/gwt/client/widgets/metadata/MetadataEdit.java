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
 * @author jeremi
 */
package org.curriki.gwt.client.widgets.metadata;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.xpn.xwiki.gwt.api.client.DOMUtils;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.User;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.*;
import org.curriki.gwt.client.pages.ComponentsPage;
import org.curriki.gwt.client.editor.Editor;
import org.curriki.gwt.client.widgets.modaldialogbox.NextCancelDialog;
import org.curriki.gwt.client.widgets.modaldialogbox.NominateDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MetadataEdit extends Composite implements MouseListener, ClickListener
{
    private VerticalPanel panel;
    private Document doc;
    private FormPanel form = new FormPanel();
    private boolean fullMode;
    private boolean startFullMode;
    private HashMap fieldMap = new HashMap();
    private Hyperlink moreInfoLabel = null;
    private HTML moreInfoText = null;
    private WindowResizeListener resizeListener = null;
    private List mandatoryFields = new ArrayList();
    private NextCancelDialog ncDialog;
    // CRS
    private String currentCRSStatus;
    private int step = 1;
    private VerticalPanel panelStep1;
    private VerticalPanel panelStep2;

    public MetadataEdit(boolean fullMode){
        this(null, fullMode);
    }

    public MetadataEdit(Document doc, boolean fullMode){
        panel = new VerticalPanel();
        panel.addStyleName("metadata-edit-panel");

        form.setWidget(panel);

        form.setMethod(FormPanel.METHOD_POST);
        form.setEncoding(FormPanel.ENCODING_URLENCODED);

        if(doc != null)
            init(doc, fullMode);

        if (!fullMode) {
            ScrollPanel sPanel = new ScrollPanel(form);

            int width = (Window.getClientWidth() < 600 ? Window.getClientWidth() - 20 : 600);
            int height = (Window.getClientHeight() < 500 ? Window.getClientHeight() - 50 : 500);

            sPanel.setWidth(width + "px");
            sPanel.setHeight(height + "px");
            initWidget(sPanel);
        }
        else
            initWidget(form);
        addStyleName("metadata-edit");
    }

    public void init(Document doc, boolean fullMode){
        if (doc == null)
            return;
        this.doc = doc;
        this.fullMode = fullMode;
        this.startFullMode = fullMode;

        if (((AssetDocument) doc).isDirectionBlock()) {
            panel.clear();
            panel.add(new HTML(Main.getTranslation("metadata.directionblocks_have_no_metadata")));
            startFullMode = true; // So that "Next" will work correctly
        } else {
            form.setAction(doc.getSaveURL());
            showEdit();

            // WARNING: This seems to break in certain cases in IE because of changes in the resizing algorythm
            // Send resize message if we are in the editor
            // Editor editor = Main.getSingleton().getEditor();
            // if (editor!=null)
            //    editor.onWindowResized(Window.getClientWidth(), Window.getClientHeight());
        }
    }

    public void addFormHandler(FormHandler formHandler){
        form.addFormHandler(formHandler);    
    }

    protected void showEdit(){
        panel.clear();

        panel.add(new Hidden("comment", Main.getTranslation("curriki.comment.updatemetadata")));

        if (startFullMode){
            showTabEdit();
        } else {
            showDialogEdit();
        }
    }

    protected void showTabEdit(){
        addSectionTitle("required_information", true);

        XObject assetObj = doc.getObject(Constants.ASSET_CLASS);
        XObject rightObj = doc.getObject(Constants.ASSET_LICENCE_CLASS);

        addEditor(assetObj, "title", "title", true);
        addEditor(assetObj, "description", "description", true);


        //addSectionTitle("educational_metadata", fullMode);
        HorizontalPanel hPanel = new HorizontalPanel();

        hPanel.addStyleName("subject-level-table");
        addEditor(assetObj, "fw_items", "fw_items", hPanel, true, true);
        addEditor(assetObj, "educational_level2", "educational_level", hPanel, true, true);
        hPanel.setCellWidth(hPanel.getWidget(0), "50%");
        hPanel.setCellWidth(hPanel.getWidget(1), "50%");
        panel.add(hPanel);

        addEditor(assetObj, Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY, Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY, panel, true, fullMode);

        addSectionTitle("general_information", fullMode);
        addEditor(assetObj, "keywords", "keywords", panel, false, fullMode);
        addEditor(assetObj, "language", "language", panel, false, fullMode);

        String rating = "";
        if (doc.getObject(Constants.CURRIKI_REVIEW_STATUS_CLASS) != null){
            doc.use(Constants.CURRIKI_REVIEW_STATUS_CLASS);
            if (doc.get(Constants.CURRIKI_REVIEW_STATUS_STATUS) != null){
                rating = String.valueOf(doc.getValue(Constants.CURRIKI_REVIEW_STATUS_STATUS));
            }
        }
        if (!(rating.equals("P") || rating.equals("3"))){
            // Only show Hide From Search if the CRS rating is not a P or 3
            addEditor(assetObj, Constants.ASSET_HIDE_FROM_SEARCH_PROPERTY, Constants.ASSET_HIDE_FROM_SEARCH_PROPERTY, panel, false, fullMode);
        }

        if (fullMode)
            addCRS(doc, panel, "private".equals(assetObj.get("rights")));


        User user = Main.getSingleton().getUser();
        boolean forceViewMode = !(user == null || (doc.getCreator().equals(user.getFullName()) || user.isAdmin()));
        addSectionTitle("right_section", fullMode);
        addEditor(rightObj, Constants.ASSET_LICENCE_RIGHT_HOLDER_PROPERTY, "right_holder", panel, false, fullMode, forceViewMode);
        addEditor(assetObj, "rights", "rights", panel, false, fullMode, forceViewMode);
        addEditor(rightObj, "licenseType2", "license_type", panel, false, fullMode, forceViewMode);

        moreInfoText = new HTML(Main.getTranslation("metadata.more_info_text"));
        moreInfoText.addStyleName("more-info-text");
        moreInfoText.setVisible(!fullMode);
        panel.add(moreInfoText);

        if (!fullMode) {
            moreInfoLabel = new Hyperlink();
            moreInfoLabel.setText(Main.getTranslation("metadata.more_info_"+fullMode));
            moreInfoLabel.addClickListener(this);
            moreInfoLabel.addStyleName("more-info");
            //moreInfoLabel.addStyleName("more-info-"+fullMode);
            panel.add(moreInfoLabel);
        }
    }

    protected void showDialogEdit(){
        panelStep1 = new VerticalPanel();
        panelStep2 = new VerticalPanel();
        panelStep2.setVisible(false);

        addSectionTitle(panelStep1, "when_you_add_a_resource", true);
        panelStep1.add(new HTML(Main.getTranslation("metadata.add_resource_metadata_desc")));
        addSubTitle(panelStep1, "complete_required_fields");

        XObject assetObj = doc.getObject(Constants.ASSET_CLASS);
        XObject rightObj = doc.getObject(Constants.ASSET_LICENCE_CLASS);

        addEditor(assetObj, "title", "title", panelStep1, true, true);
        addEditor(assetObj, "description", "description", panelStep1, true, true);

        HorizontalPanel hPanel = new HorizontalPanel();

        hPanel.addStyleName("subject-level-table");
        addEditor(assetObj, "fw_items", "fw_items", hPanel, true, true);
        addEditor(assetObj, "educational_level2", "educational_level", hPanel, true, true);
        hPanel.setCellWidth(hPanel.getWidget(0), "50%");
        hPanel.setCellWidth(hPanel.getWidget(1), "50%");
        panelStep1.add(hPanel);

        addEditor(assetObj, Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY, Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY, panelStep1, true, true);

        panel.add(panelStep1);


        addSubTitle(panelStep2, "review_and_update_fields");
        User user = Main.getSingleton().getUser();
        boolean forceViewMode = !(user == null || (doc.getCreator().equals(user.getFullName()) || user.isAdmin()));
        addEditor(assetObj, "rights", "rights", panelStep2, false, true, forceViewMode);

        String rating = "";
        if (doc.getObject(Constants.CURRIKI_REVIEW_STATUS_CLASS) != null){
            doc.use(Constants.CURRIKI_REVIEW_STATUS_CLASS);
            if (doc.get(Constants.CURRIKI_REVIEW_STATUS_STATUS) != null){
                rating = String.valueOf(doc.getValue(Constants.CURRIKI_REVIEW_STATUS_STATUS));
            }
        }
        if (!(rating.equals("P") || rating.equals("3"))){
            // Only show Hide From Search if the CRS rating is not a P or 3
            addEditor(assetObj, Constants.ASSET_HIDE_FROM_SEARCH_PROPERTY, Constants.ASSET_HIDE_FROM_SEARCH_PROPERTY, panelStep2, false, true);
        }

        addEditor(assetObj, "keywords", "keywords", panelStep2, false, true);
        addEditor(assetObj, "language", "language", panelStep2, false, true);

        addEditor(rightObj, Constants.ASSET_LICENCE_RIGHT_HOLDER_PROPERTY, "right_holder", panelStep2, false, true, forceViewMode);
        addEditor(rightObj, "licenseType2", "license_type", panelStep2, false, true, forceViewMode);

        panel.add(panelStep2);
    }

    private void addCRS(Document doc, Panel panel, boolean isPrivate) {
        String role = Main.getProperty("role");
        XObject crsObj = doc.getObject(Constants.CURRIKI_REVIEW_STATUS_CLASS);
        Integer reviewpending = (crsObj==null) ? null : (Integer) crsObj.get(Constants.CURRIKI_REVIEW_STATUS_REVIEWPENDING);
        String status = (crsObj==null) ? null : (String) crsObj.get(Constants.CURRIKI_REVIEW_STATUS_STATUS);
        currentCRSStatus = status;
        String lastReviewDate = (crsObj==null) ? "" : crsObj.getViewProperty(Constants.CURRIKI_REVIEW_STATUS_LASTTREVIEWDATE);
        FlowPanel crsPanel = new FlowPanel();
        crsPanel.setStyleName("crs_review");
        HTMLPanel crsPanelTitle = new HTMLPanel(Main.getTranslation("curriki.crs.currikireview"));
        crsPanelTitle.setStyleName("crs_reviewtitle");
        crsPanel.add(crsPanelTitle);
        String txt = Main.getTranslation("curriki.crs.review.tooltip");
        Image image = new Image(Constants.ICON_PATH+"exclamation.png");
        PopupPanel popup = new PopupPanel(true);
        popup.setStyleName("metadata-tooltip-popup");
        // popup.setWidth("300px");
        popup.add(new HTML(txt));
        image.addMouseListener(new TooltipMouseListener(popup));
        crsPanel.add(image);
        FlowPanel crsRatingPanel = new FlowPanel();
        crsRatingPanel.setStyleName("crs_reviewrating");
        if ((status==null)||(status.equals("0"))||(status.equals(""))) {
            crsRatingPanel.setStyleName("crs_reviewnorating");
            HTMLPanel crsRatingTextPanel = new HTMLPanel(Main.getTranslation("curriki.crs.unrated"));
            crsRatingTextPanel.setStyleName("crs_reviewratingtext");
            crsRatingPanel.add(crsRatingTextPanel);
        } else {
            HTMLPanel crsRatingTextPanel = new HTMLPanel(Main.getTranslation("curriki.crs.rating"  + status));
            crsRatingTextPanel.setStyleName("crs_reviewratingtext");
            crsRatingPanel.add(crsRatingTextPanel);
            if ((lastReviewDate!=null)&&(!lastReviewDate.equals(""))) {
                String sreviewDate = (lastReviewDate.length()>=8) ? lastReviewDate.substring(0,8) : lastReviewDate;
                HTMLPanel crsRatingDatePanel = new HTMLPanel(Main.getTranslation("curriki.crs.asof") + " " + sreviewDate);
                crsRatingDatePanel.setStyleName("crs_reviewratingdate");
                crsRatingPanel.add(crsRatingDatePanel);
            }
            Image crsRatingImage = new Image(Constants.SKIN_PATH + "crs" + status + ".gif");
            crsRatingImage.addClickListener(new ClickListener() {
                public void onClick(Widget widget) {
                    ComponentsPage.getSingleton().switchPage("comment");
                }
            });
            crsRatingImage.setStyleName("crs_reviewratingtext");
            crsRatingPanel.add(crsRatingImage);
        }
        crsPanel.add(crsRatingPanel);
        if (!"P".equals(status)&&!isPrivate) {
            if ((reviewpending!=null)&&reviewpending.intValue()==1)  {
                HTMLPanel crsReviewPendingPanel = new HTMLPanel(Main.getTranslation("curriki.crs.reviewpending"));
                crsReviewPendingPanel.setStyleName("crs_reviewpending");
                crsPanel.add(crsReviewPendingPanel);
            } else {
                Hyperlink crsReviewNominateLink = new Hyperlink();
                crsReviewNominateLink.setText(Main.getTranslation("curriki.crs.reviewnominate"));
                crsReviewNominateLink.setStyleName("crs_reviewnominate");
                crsReviewNominateLink.addClickListener(new ClickListener() {
                    public void onClick(Widget widget) {
                        Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
                        new NominateDialog(currentAsset, new AsyncCallback() {
                            public void onFailure(Throwable throwable) {
                                // nothing to do this was a cancel
                            }

                            public void onSuccess(Object object) {
                                // Refresh the page
                                Editor editor = Main.getSingleton().getEditor();
                                editor.setCurrentAssetInvalid(true);
                                editor.refreshState();
                            }
                        });

                        // String url = Main.getTranslation("params.crs.nominateurl") + "?fromgwt=1&page=" + currentAsset.getFullName();
                        // Window.open(url, "_blank", "");
                    }
                });
                crsPanel.add(crsReviewNominateLink);
            }

            // if the reviewer mode is set to one then we show the review link
            if (("reviewer".equals(role))||("admin".equals(role))) {
                Hyperlink crsReviewReviewLink = new Hyperlink();
                crsReviewReviewLink.setText(Main.getTranslation("curriki.crs.review"));
                crsReviewReviewLink.setStyleName("crs_reviewreview");
                crsReviewReviewLink.addClickListener(new ClickListener() {
                    public void onClick(Widget widget) {
                        Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
                        String url = Main.getTranslation("params.crs.reviewurl") + "?page=" + currentAsset.getFullName();
                        Window.open(url, "_blank", "");
                    }
                });
                crsPanel.add(crsReviewReviewLink);
            }
        }

        if ("admin".equals(role)) {
            final CheckBox setToPCheckBox = new CheckBox(Main.getTranslation("curriki.crs.settopartner"));
            setToPCheckBox.setChecked("P".equals(currentCRSStatus));
            setToPCheckBox.addClickListener(new ClickListener() {
                public void onClick(Widget widget) {
                    String questionText;
                    String titleText;
                    if ("P".equals(currentCRSStatus)) {
                        questionText = Main.getTranslation("curriki.crs.confirmunsettopartner");
                        titleText = "curriki.crs.unsettopartner";
                    } else {
                        questionText = Main.getTranslation("curriki.crs.confirmsettopartner");
                        titleText = "curriki.crs.settopartner";
                    }
                    ncDialog = new NextCancelDialog(titleText, questionText, "crsconfirmsettopartner", new AsyncCallback() {
                        public void onFailure(Throwable throwable) {
                            setToPCheckBox.setChecked("P".equals(currentCRSStatus));
                            ncDialog.hide();
                        }
                        public void onSuccess(Object object) {
                            Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
                            final String newCRSStatus =  "P".equals(currentCRSStatus) ? "0" : "P";
                            ncDialog.hide();
                            CurrikiService.App.getInstance().updateProperty(currentAsset.getFullName(), Constants.CURRIKI_REVIEW_STATUS_CLASS, "status", newCRSStatus, new CurrikiAsyncCallback() {
                                public void onFailure(Throwable caught) {
                                    super.onFailure(caught);
                                    setToPCheckBox.setChecked("P".equals(currentCRSStatus));
                                }

                                public void onSuccess(Object result) {
                                    super.onSuccess(result);
                                    setToPCheckBox.setChecked("P".equals(newCRSStatus));
                                    currentCRSStatus = newCRSStatus;
                                }
                            });
                        }
                    });

                }
            });
            crsPanel.add(setToPCheckBox);
        }
        panel.add(crsPanel);

    }
    
    public void addSectionTitle(String titleKey, boolean visible){
        addSectionTitle(panel, titleKey, visible);
    }

    public void addSectionTitle(Panel panel, String titleKey, boolean visible){
        Label label = new Label(Main.getTranslation("metadata." + titleKey));
        label.addStyleName("curriki-title");
        label.setVisible(visible);

        fieldMap.put(titleKey, label);
        panel.add(label);
    }

    public void addSubTitle(String titleKey){
        addSubTitle(panel, titleKey);
    }

    public void addSubTitle(Panel panel, String titleKey){
        HTML title = new HTML(Main.getTranslation("metadata." + titleKey));
        title.addStyleName("curriki-subtitle");
        panel.add(title);
    }

    public void SetHiddenCategoryValue(String value){
        SetHiddenCategoryValue(panel, value);
    }

    public void SetHiddenCategoryValue(Panel panel, String value){
        XObject obj = doc.getObject(Constants.ASSET_CLASS);
        if (obj != null){
            Hidden hidden = new Hidden(obj.getEditPropertyFieldName("category"), value);
            panel.add(hidden);
        }
    }
    

    public void addEditor(XObject obj, String name, String keyValue){
        addEditor(obj, name, keyValue, panel, false, true);
    }

    public void addEditor(XObject obj, String name, String keyValue, boolean mandatory){
        addEditor(obj, name, keyValue, panel, mandatory, true);
    }

    public void addEditor(XObject obj, String name, String keyValue, Panel panel, boolean mandatory, boolean visible){
        addEditor(obj, name, keyValue, panel, mandatory, visible, false);

    }
    public void addEditor(XObject obj, String name, String keyValue, Panel panel, boolean mandatory, boolean visible, boolean forceViewMode){
        String divid = null;
        String script = null;
        String html = null;
        if (obj != null){
            if (doc.hasEditRight()&&!forceViewMode)
             html = obj.getEditProperty(name);
            else
             html = obj.getViewProperty(name);
        }
        HTML htmlblock;

        if (name.equals("fw_items")&&doc.hasEditRight()) {
            // We need to workaround the fact the Internet Explorer does not executre Javascript
            // when inserted using an HTMLPanel
            try {
                String findstr1 = "<div id=\"";
                int i1 = html.indexOf(findstr1);
                int i2 = html.indexOf("\"", i1 + findstr1.length() + 1);
                divid = html.substring(i1 + findstr1.length(),i2);

                String findstr2 = "<input";
                int i5 = html.indexOf(findstr2);
                int i6 = html.indexOf("/>", i5 + findstr2.length() + 1);
                String inputstr = html.substring(i5,i6+2);
                htmlblock = new HTML(inputstr);

                String findstr3 = "<script type=\"text/javascript\">";
                int i3 = html.indexOf(findstr3);
                int i4 = html.indexOf("</script>", i3 + 1);
                script = html.substring(i3+findstr3.length(), i4);
            } catch (Exception e) {
                html = Main.getTranslation("metadata.error_displaying_subject_field");
                htmlblock = new HTML(html);
            }
        }
        else {
            htmlblock = new HTML(html);
        }

        VerticalPanel fieldPanel = new VerticalPanel();
        fieldPanel.addStyleName("field-panel");
        fieldPanel.setVisible(visible);
        panel.add(fieldPanel);
        fieldMap.put(name, fieldPanel);

        FlowPanel hPanel = new FlowPanel();
        hPanel.addStyleName("field-panel-field");
        fieldPanel.add(hPanel);
        if(mandatory){
            HTML mandatoryHTML = new HTML("!&nbsp;");
            mandatoryHTML.addStyleName("required_fields");
            hPanel.add(mandatoryHTML);
            mandatoryFields.add(htmlblock);
        }

        if (keyValue != null) {
            String txt = Main.getTranslation("metadata." + keyValue + "_title");
            if (txt.length() > 0 && !txt.equals("metadata." + keyValue + "_title")) {
                Label title = new Label(txt + ":");
                title.addStyleName("curriki-subtitle");
                hPanel.add(title);
            }

            hPanel.add(getTooltip(name));

            txt = Main.getTranslation("metadata." + keyValue + "_txt");
            if (txt.length() > 0 && !txt.equals("metadata." + keyValue + "_txt")) {
                HTML text = new HTML(txt);
                text.addStyleName("curriki-txt");
                fieldPanel.add(text);
            }

            txt = Main.getTranslation("metadata." + keyValue + "_content");
            if (txt.equals("metadata." + keyValue + "_content")) {
                txt = "";
            }


            Element el = htmlblock.getElement();
            Element resEl = DOMUtils.getFirstElementByTagName(el, "input");
            if (resEl != null) {
                String origValue = DOM.getAttribute(resEl, "value");
                if (origValue == null || origValue.length() == 0)
                    DOM.setAttribute(resEl, "value", txt);
            }
            else {
                resEl = DOMUtils.getFirstElementByTagName(el, "textarea");
                if (resEl != null) {
                    String origValue = DOM.getInnerText(resEl);
                    if (origValue == null || origValue.length() == 0)
                        DOM.setInnerText(resEl, txt);
                }
            }

            txt = Main.getTranslation("metadata." + keyValue + "_after");
            if (txt.length() > 0 && !txt.equals("metadata." + keyValue + "_after")) {
                htmlblock.setHTML(htmlblock.getHTML()+txt);
            }
        }

        if (script != null) {
            SimplePanel hpanel = new SimplePanel();
            fieldPanel.add(hpanel);
            addScript(hpanel, divid, script);
        }

        if (htmlblock!=null) {
            fieldPanel.add(htmlblock);
        }
    }

    private Widget getTooltip(String name) {
        String txt = Main.getTranslation("metadata." + name + "_tooltip");
        Image image = new Image(Constants.ICON_PATH+"exclamation.png");
        PopupPanel popup = new PopupPanel(true);
        popup.setStyleName("metadata-tooltip-popup");
        // popup.setWidth("300px");
        popup.add(new HTML(txt));
        image.addMouseListener(new TooltipMouseListener(popup));
        return image;
    }

    private native void initTooltip() /*-{
        tt_Init();
    }-*/;

    private native void setText(Element e, String script) /*-{
          e.text = script;
    }-*/;

    public void addScript(Panel panel, String divid, String script) {
        if (!Main.isSafari()) {
            Element e1 = DOM.createElement("div");
            DOM.setAttribute(e1, "id", divid);
            // Let's append the child element to an already visible element
            // Otherwise it is not found
            DOM.appendChild(DOM.getElementById("treeviewtemp"), e1);

            Element e = DOM.createElement("script");
            DOM.setAttribute(e, "language", "JavaScript");
            DOM.appendChild(panel.getElement(), e);
            // We need to do it this way because of Internet Explorer
            setText(e, script);

            // One generated we can append it to the panel
            DOM.removeChild(DOM.getElementById("treeviewtemp"), e1);
            DOM.appendChild(panel.getElement(), e1);
        } else {
            Element e1 = DOM.createElement("div");
            DOM.setAttribute(e1, "id", divid);
            DOM.appendChild(panel.getElement(), e1);

            Element e = DOM.createElement("script");
            DOM.setAttribute(e, "language", "JavaScript");
            DOM.appendChild(panel.getElement(), e);

            // We need to do it this way because of Internet Explorer
            // This also works with Gecko and Safari
            DOM.setInnerHTML(e, script);
        }
    }

    public void submit(){
        if (!hasMissing()){
            form.submit();
        }
    }

    public boolean hasMissing(){
        String missing = isFormValid();
        if (missing.equals("")){
            return false;
        } else {
            String[] missings = missing.split(",");
            String text;
            text = Main.getTranslation("metadata.fields_missing") + ":\n";
            for (int i=0;i<missings.length;i++) {
                text += "  - "+Main.getTranslation("metadata." + missings[i] + "_title");
            }
            Window.alert(text);
            return true;
        }
    }

    private String isFormValid() {
        return isFormValid(form.getElement());
    }

    private native String isFormValid(Element form) /*-{
        var missing = "";
        var first = true;

        if (form["XWiki.AssetClass_0_title"].value == "") {
            if (!first)
                missing += ",";
            first = false;
            missing += "title";
        }

       if (form["XWiki.AssetClass_0_description"].value == "") {
            if (!first)
                missing += ",";
            first = false;
            missing += "description";
        }

        // There are 2 fields of this name, the first is the selector, second is a hidden input
        if (form["XWiki.AssetClass_0_instructional_component2"][0].selectedIndex == -1) {
            if (!first)
                missing += ",";
            first = false;
            missing += "instructional_component2";
        }

        var checkboxes  = form["XWiki.AssetClass_0_educational_level2"];

        var selected = false;
        for (i=0; i < checkboxes.length; i++) {
            if (checkboxes[i].checked){
                selected = true;
                break;
            }
        }
        if (!selected) {
            if (!first)
                missing += ",";
            first = false;
            missing += "educational_level";
        }
        return missing;
    }-*/;


    public void showView(){
        panel.clear();
    }

    private void switchVisibility(String key, boolean visible){
        Widget widget = (Widget) fieldMap.get(key);
        if (widget != null){
            widget.setVisible(visible);
        }
    }

    private void switchMode(){
        fullMode = !fullMode;
        switchVisibility("educational_metadata", fullMode);
        switchVisibility(Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY, fullMode);
        switchVisibility("right_section", fullMode);
        switchVisibility("general_information", fullMode);
        switchVisibility("educational_information", fullMode);
        switchVisibility(Constants.ASSET_LICENCE_RIGHT_HOLDER_PROPERTY, fullMode);
        switchVisibility("rights", fullMode);
        switchVisibility("licenseType2", fullMode);
        switchVisibility("keywords", fullMode);
        switchVisibility("language", fullMode);
        switchVisibility(Constants.ASSET_HIDE_FROM_SEARCH_PROPERTY, fullMode);
        moreInfoText.setVisible(!fullMode);
        if (resizeListener != null) {
            //Window.alert("plop");
            resizeListener.onWindowResized(panel.getOffsetHeight(), panel.getOffsetWidth());
        }
    }

    private void set_view_step(int nextStep){
        if (nextStep == 1){
            panelStep1.setVisible(true);
            panelStep2.setVisible(false);
        }
        if (nextStep == 2){
            panelStep1.setVisible(false);
            panelStep2.setVisible(true);
        }
    }

    public void onMouseDown(Widget sender, int x, int y) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onMouseEnter(Widget sender) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onMouseLeave(Widget sender) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onMouseMove(Widget sender, int x, int y) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onMouseUp(Widget sender, int x, int y) {
        onClick(sender);
    }

    public void onClick(Widget sender){
        //moreInfoLabel.removeStyleName("more-info-" + fullMode);
        switchMode();
        moreInfoLabel.setText(Main.getTranslation("metadata.more_info_" + fullMode));
        //moreInfoLabel.addStyleName("more-info-" + fullMode);
    }

    public void setResizeListener(WindowResizeListener resizeListener) {
        this.resizeListener = resizeListener;
    }

    public boolean doNext(){
        if (startFullMode){
            return true;
        }
        if (step == 1){
            if (!hasMissing()){
                // Go to step 2
                step = 2;
                set_view_step(step);
            }
            return false;
        } else if (step == 2){
            if (hasMissing()){
                return false;
            }
        }
        return true;
    }
}
