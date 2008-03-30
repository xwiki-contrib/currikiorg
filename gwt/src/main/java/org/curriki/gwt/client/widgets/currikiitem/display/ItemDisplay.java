package org.curriki.gwt.client.widgets.currikiitem.display;

import java.util.ArrayList;
import java.util.List;

import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.Attachment;
import com.xpn.xwiki.gwt.api.client.XObject;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;
import org.curriki.gwt.client.widgets.upload.UploadWidget;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.Translator;
import org.curriki.gwt.client.utils.URLUtils;
import org.curriki.gwt.client.utils.Loading;

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

public abstract class ItemDisplay  extends AbstractItemDisplay {
    UploadWidget upload = null;
    TextBox textbox = null;
    Label link = null;
    Image icon = null;
    TextArea descBox = null;


    public ItemDisplay(Document doc, CurrikiItem item) {
        super(doc, item);
        // panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        panel.addStyleName("item-panel-display");
        initDisplay(doc);
    }

    public void changeToEditMode() {
        if(status != Constants.EDIT) {
            switchToEdit();
        }
    }

    public void cancelEditMode() {
       status = Constants.VIEW;
       initDisplay(doc);

    }

    protected void initEditLink(){
        XObject obj = doc.getObject(Constants.EXTERNAL_ASSET_CLASS);

        textbox = new TextBox();
        textbox.setText((String) obj.get(Constants.EXTERNAL_ASSET_LINK_PROPERTY));
        panel.add(textbox);
        textbox.setFocus(true);
    }

    protected void initEditDescription() {

        XObject obj = doc.getObject(Constants.ASSET_CLASS);

        String desc = (String) obj.get(Constants.ASSET_DESCRIPTION_PROPERTY);

        descBox = new TextArea();
        if (desc != null)
            descBox.setText(desc);

        Grid grid = new Grid(1, 2);

        Label label = getSubtitleLabel("asset.description");
        grid.setWidget(0, 0, label);
        grid.setWidget(0, 1, descBox);

        panel.add(grid);
    }

    protected void initEditAttachment(){
        HTML uploadTitle = new HTML(Main.getTranslation("asset.archive.uploadfile") + ":");
        uploadTitle.addStyleName("attachment-upload-title");
        panel.add(uploadTitle);

        upload = new UploadWidget(doc.getUploadURL(), false);
        upload.addStyleName("attachment-upload-field");
        if (doc.getAttachments().size() > 0)
            upload.setFilename(((Attachment)doc.getAttachments().get(0)).getFilename());

        upload.addFormHandler(new FormHandler(){
            public void onSubmit(FormSubmitEvent formSubmitEvent) {
                Main.getSingleton().startLoading();
            }

            public void onSubmitComplete(FormSubmitCompleteEvent formSubmitCompleteEvent) {
                Main.getSingleton().finishLoading();
                reloadDocument();
                status = Constants.VIEW;
            }
        });

        
        panel.add(upload);
    }

    public boolean saveUpload() {
        return upload.sendFile();
    }

    public boolean saveDescription() {
        XObject assetObj = doc.getObject(Constants.ASSET_CLASS);

        String desc = descBox.getText();
        assetObj.set(Constants.ASSET_DESCRIPTION_PROPERTY, desc);

        List objs = new ArrayList();
        objs.add(assetObj);

        CurrikiService.App.getInstance().saveObjects(objs, new CurrikiAsyncCallback(){

            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);
            }

            public void onSuccess(Object object) {
                super.onSuccess(object);
                descBox = null;
                if (!saveUpload()) {
                    reloadDocument();
                    status = Constants.VIEW;
                }
            }
        });
        return true;
    }

    public boolean saveLink() {
        XObject obj = doc.getObject(Constants.EXTERNAL_ASSET_CLASS);
        String text;
        text = textbox.getText();
        if (!URLUtils.isValidUrl(text)){
            Window.alert(Main.getTranslation("asset.invalid_url"));
            return false;
        }


        obj.set(Constants.EXTERNAL_ASSET_LINK_PROPERTY, text);

        CurrikiService.App.getInstance().saveObject(obj, new CurrikiAsyncCallback(){

            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);
                panel.add(textbox);
            }

            public void onSuccess(Object object) {
                super.onSuccess(object);
                panel.remove(0);
                reloadDocument();
                status = Constants.VIEW;
            }
        });
        return true;
    }


    protected void switchToEdit(){
        switchToEdit(false);
    }

    protected void switchToEdit(boolean force){
        panel.clear();
        status = Constants.EDIT;
        CurrikiService.App.getInstance().lockDocument(doc.getFullName(), force, new CurrikiAsyncCallback(){

            public void onFailure(Throwable caught){
                super.onFailure(caught);
                status = Constants.VIEW;
            }

            public void onSuccess(Object object) {
                super.onSuccess(object);
                if (object != null && ((Boolean)object).booleanValue()) {
                    initEdit();
                }
                else {
                    status = Constants.VIEW;
                    if (Window.confirm(Main.getTranslation("asset.asset_locked_force_edit"))){
                        switchToEdit(true);
                    }
                    else
                    {
                    	cancelEditMode();
                    }
                }

            }
        });
    }

    protected void initDisplayDescription(Document doc) {
        Label caption = new HTML(){
            public void onBrowserEvent(Event event) {
                item.onBrowserEvent(event);
            }
        };
        XObject obj = doc.getObject(Constants.ASSET_CLASS);
        caption.setText(obj.getViewProperty(Constants.ASSET_DESCRIPTION_PROPERTY));
        caption.setStyleName("item-description");
        // caption.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        if(panel.getWidgetIndex(caption) == -1)
            panel.add(caption);
    }

    public void initDisplay(Document doc) {
        panel.clear();

        link = new HTML(){
            public void onBrowserEvent(Event event) {
                item.onBrowserEvent(event);
            }
        };
        link.sinkEvents(Event.ONCLICK | Event.ONDBLCLICK);
        link.addStyleName("item-link");

        link.setText(getURL());

        panel.add(link);

        item.refreshItemInfos();
        item.switchHeaderButtonsToViewMode();
    }

    protected abstract String getURL();

    public void initDisplayLink(Document doc) {
        if (textbox != null && panel.getWidgetIndex(textbox) != -1) {
            panel.remove(textbox);
            textbox = null;
        }

        if (link != null && panel.getWidgetIndex(link) != -1)
            panel.remove(link);
        link = new HTML(){
            public void onBrowserEvent(Event event) {
                item.onBrowserEvent(event);
            }
        };
        link.sinkEvents(Event.ONCLICK | Event.ONDBLCLICK);
        link.addStyleName("item-link");

        Label caption = new HTML();
        caption.setText(Main.getTranslation("asset.externallink"));
        caption.setStyleName("item-description");
        if(panel.getWidgetIndex(caption) == -1)
            panel.add(caption);        
        
        XObject obj = doc.getObject(Constants.EXTERNAL_ASSET_CLASS);
        String text = (String) obj.get(Constants.EXTERNAL_ASSET_LINK_PROPERTY);
        text = URLUtils.breakLinkText(text, 100);
        link.setText(text);

        if(panel.getWidgetIndex(link) == -1)
            panel.add(link);
        item.refreshItemInfos();
        item.switchHeaderButtonsToViewMode();
    }


    protected String getLinkURL() {
        XObject obj = doc.getObject(Constants.EXTERNAL_ASSET_CLASS);
        return (String) obj.get(Constants.EXTERNAL_ASSET_LINK_PROPERTY);
    }

    protected String getAttURL() {
        if (doc.getAttachments().size()>0) {
            Attachment att = (Attachment) doc.getAttachments().get(0);
            return att.getDownloadUrl();
        } else {
            return "";
        }
    }

    public void onView() {}

    public void onDocumentVersionChange() {}

    /**
     * Edit mode
     */
    protected abstract void initEdit();

    protected Label getSubtitleLabel(String titleKey){
        Label label = new HTML(Main.getTranslation(titleKey));
        label.addStyleName("curriki-subtitle");
        return label;
    }

}
