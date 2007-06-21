/*
 * Copyright 2006, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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
package org.curriki.gwt.client.widgets.currikiitem.display;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.AssetDocument;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;


public abstract class AbstractItemDisplay extends Composite {
    VerticalPanel panel = new VerticalPanel();
    Document doc;
    XObject assetObj;
    final Image loadingImg = new Image(Constants.ICON_SPINNER);
    int status = Constants.VIEW;
    CurrikiItem item;


    public AbstractItemDisplay(Document doc, CurrikiItem item) {
        this.item = item;
        setDocument(doc);
        // panel.setWidth("100%");
        if (doc.getObject(Constants.COMPOSITEASSET_CLASS) != null) {
            panel.addStyleName("item-composite-display");
        } else {
            panel.addStyleName("item-display");
        }
        item.setItem(this);
        initWidget(panel);
    }

    protected void setDocument(Document doc){
        this.doc = doc;
        this.assetObj = doc.getObject(Constants.ASSET_CLASS);
    }

    public AssetDocument getDocument() {
        return (AssetDocument) doc;
    }

    public String getDocumentFullName(){
        if (doc != null)
            return doc.getFullName();
        else
            return null;
    }

    public String getTitle() {
        if ((doc != null)&&(assetObj != null))
            return (String) assetObj.get(Constants.ASSET_TITLE_PROPERTY);
        else
            return null;
    }

    public String getCurrentVersion(){
        return doc.getVersion();
    }

    public abstract void changeToEditMode();

    public abstract void cancelEditMode();

    public abstract boolean save();

    public abstract void onView();

    public abstract void onDocumentVersionChange();

    public abstract void initDisplay(Document doc);

    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
    }

    public static AbstractItemDisplay loadItemDisplay(Document doc, CurrikiItem item){
        if (doc.hasViewRight()) {

            XObject obj = doc.getObject(Constants.COMPOSITEASSET_CLASS);
            if (obj!=null)
                return new CollectionItemDisplay(doc, item);

            obj = doc.getObject(Constants.ASSET_CLASS);
            String category = (obj==null) ? null : (String) obj.get(Constants.ASSET_CATEGORY_PROPERTY);
            if (category == null)
                return new AttachementItemDisplay(doc, item);
            if (category.equals(Constants.CATEGORY_IMAGE))
                return new ImageItemDisplay(doc, item);
            if (category.equals(Constants.CATEGORY_AUDIO))
                return new AudioItemDisplay(doc, item);
            if (category.equals(Constants.CATEGORY_ANIMATION))
                return new AnimationItemDisplay(doc, item);
            if (category.equals(Constants.CATEGORY_TEXT))
                return new TextItemDisplay(doc, item);
            if (category.equals(Constants.CATEGORY_COLLECTION))
                return new CollectionItemDisplay(doc, item);
            if (category.equals(Constants.CATEGORY_LINK))
                return new LinkItemDisplay(doc, item);
            if (category.equals(Constants.CATEGORY_ARCHIVE))
                return new ArchiveItemDisplay(doc, item);

            // Last is item
            return new AttachementItemDisplay(doc, item);
        } else {
            // Document is non viewable with need a generic displayer
            return new ProtectedItemDisplay(doc, item);
        }

    }


    public void reloadDocument(){
        loadDocument(doc.getFullName());
    }

    public void loadDocument(String fullName){
        CurrikiService.App.getInstance().getDocument(fullName, true, true, false, false, new CurrikiAsyncCallback(){
            public void onSuccess(Object result) {
                super.onSuccess(result);
                AssetDocument newDoc = (AssetDocument) result;
                newDoc.setParentEditable(getDocument().hasEditRight());
                newDoc.setParentCurrikiTemplate(getDocument().isCurrikiTemplate());
                newDoc.setParent(getDocument().getParent());
                setDocument(newDoc);
                initDisplay(doc);
            }
        });
    }

    public void lockDocument(){
        
    }

    public void hide() {
        panel.setVisible(false);
    }

    public void show() {
        panel.setVisible(true);
    }

    public boolean isVisible(){
        return panel.isVisible();
    }

    public boolean isDirty() {
        return true;
    }

    public abstract String getType();
}
