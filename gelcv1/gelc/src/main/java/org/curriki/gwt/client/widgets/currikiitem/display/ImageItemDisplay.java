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

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LoadListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import com.xpn.xwiki.gwt.api.client.Attachment;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.Translator;

import java.util.ArrayList;
import java.util.List;


public class ImageItemDisplay  extends AttachementItemDisplay {
    private Image image;
    private boolean isLink;
    TextArea altDescBox = null;
    TextArea captionBox = null;
    RadioButton rbSmallSize = null;
    RadioButton rbMediumSize = null;
    RadioButton rbLargeSize = null;
    int maxWidth;

    
    public ImageItemDisplay(Document doc, CurrikiItem item) {
        super(doc, item);
        // panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    }

    public String getType() {
        return Constants.TYPE_IMAGE;
    }

    public void initDisplay(Document doc) {
        upload = null;
        panel.clear();
        panel.addStyleName("item-image-panel");

        displayImage(doc, false);
    }


    public void displayImage(Document doc, boolean editMode) {
        String url = getURL();

        // Is our image an external link or a local attachment
        boolean isLink = (doc.getAttachments().size() == 0);

        String extension = getFileExtension();
        if ((!extension.equals("PSD"))&&(!extension.equals("TIF"))) {

            image = new Image(){
                public void onBrowserEvent(Event event) {
                    super.onBrowserEvent(event);
                    item.onBrowserEvent(event);
                }
            };

            //image.sinkEvents(Event.ONCLICK | Event.ONDBLCLICK | Event.ONLOAD);
            image.addStyleName("item-img");
            XObject assetImg = doc.getObject(Constants.MIMETYPE_PICTURE_CLASS);
            String size = (String) assetImg.get(Constants.MIMETYPE_PICTURE_DISPLAY_SIZE_PROPERTY);

            if (size == null)
                maxWidth = 450;
            else if (size.equals(Constants.IMG_SMALL))
                maxWidth = 300;
            else if (size.equals(Constants.IMG_MEDIUM))
                maxWidth = 450;
            else
                maxWidth = 600;


            image.addLoadListener(new LoadListener() {
                public void onError(Widget sender) {
                    Main.getSingleton().showError(Main.getTranslation("asset.image.broken_image_link"));
                }

                public void onLoad(Widget sender) {
                    int width = image.getOffsetWidth();
                    if (width > maxWidth){
                        image.setWidth(maxWidth + "px");
                    }
                }
            });

            image.setUrl(url);
            panel.add(image);
        }

        // Let's put the description for all
        Label caption = new Label();
        XObject obj = doc.getObject(Constants.ASSET_CLASS);
        String captionTxt = obj.getViewProperty(Constants.ASSET_CAPTION_DESCRIPTION_PROPERTY);
        if (captionTxt == null || captionTxt.trim().length() == 0) {
            captionTxt = obj.getViewProperty(Constants.ASSET_DESCRIPTION_PROPERTY);
        }
        caption.setText(captionTxt);
        caption.setStyleName("item-description");
        // caption.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        if(panel.getWidgetIndex(caption) == -1)
            panel.add(caption);

        // Also display the file type icon and download link
        if (!editMode) {
            if (!isLink)
                initDisplayAttachment(doc);
        }
    }

    private String getFileExtension() {
        if (isLink) {
            return getFileExtension(getURL());
        } else {
            return getFileExtension(getAttURL());
        }
    }


    public boolean saveImageInformations(){
        XObject assetObj = doc.getObject(Constants.ASSET_CLASS);

        String altDesc = altDescBox.getText();
        assetObj.set(Constants.ASSET_ALT_DESCRIPTION_PROPERTY, altDesc);

        String caption = captionBox.getText();
        assetObj.set(Constants.ASSET_CAPTION_DESCRIPTION_PROPERTY, caption);

        XObject assetImg = doc.getObject(Constants.MIMETYPE_PICTURE_CLASS);

        if (rbSmallSize.isChecked())
            assetImg.set(Constants.MIMETYPE_PICTURE_DISPLAY_SIZE_PROPERTY, Constants.IMG_SMALL);
        else if (rbMediumSize.isChecked())
            assetImg.set(Constants.MIMETYPE_PICTURE_DISPLAY_SIZE_PROPERTY, Constants.IMG_MEDIUM);
        else if (rbLargeSize.isChecked())
            assetImg.set(Constants.MIMETYPE_PICTURE_DISPLAY_SIZE_PROPERTY, Constants.IMG_LARGE);

        List objs = new ArrayList();
        objs.add(assetObj);
        objs.add(assetImg);



        CurrikiService.App.getInstance().saveObjects(objs, new CurrikiAsyncCallback(){

            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);
            }

            public void onSuccess(Object object) {
                super.onSuccess(object);

                rbSmallSize = null;
                rbMediumSize = null;
                rbLargeSize = null;
                altDescBox = null;
                captionBox = null;

                if (isLink)
                    saveLink();
                else
                    if (!saveUpload()) {
                        reloadDocument();
                        status = Constants.VIEW;
                    }
            }
        });
        return true;
    }

    public boolean save() {
        return saveImageInformations();
    }

    protected void initEdit(){

        //We display the image
        displayImage(doc, true);

        XObject obj = doc.getObject(Constants.ASSET_CLASS);

        String altDesc = (String) obj.get(Constants.ASSET_ALT_DESCRIPTION_PROPERTY);
        String caption = (String) obj.get(Constants.ASSET_CAPTION_DESCRIPTION_PROPERTY);

        altDescBox = new TextArea();
        if (altDesc != null)
            altDescBox.setText(altDesc);

        captionBox = new TextArea();
        if (caption != null)
            captionBox.setText(caption);

        Grid grid = new Grid(3, 2);

        Label label = getSubtitleLabel("asset.image.caption_text");
        grid.setWidget(0, 0, label);
        grid.setWidget(0, 1, captionBox);


        label = getSubtitleLabel("asset.image.alt_desc_text");
        grid.setWidget(1, 0, label);
        grid.setWidget(1, 1, altDescBox);

        VerticalPanel vPanel = new VerticalPanel();
        label = getSubtitleLabel("asset.image.resize_image_text");

        grid.setWidget(2, 0, label);
        grid.setWidget(2, 1, vPanel);

        XObject assetImg = doc.getObject(Constants.MIMETYPE_PICTURE_CLASS);
        String size = (String) assetImg.get(Constants.MIMETYPE_PICTURE_DISPLAY_SIZE_PROPERTY);

        rbSmallSize = new RadioButton("resizeImageGroup", Main.getTranslation("asset.image.small_size"));
        rbMediumSize = new RadioButton("resizeImageGroup", Main.getTranslation("asset.image.medium_size"));
        rbLargeSize = new RadioButton("resizeImageGroup", Main.getTranslation("asset.image.large_size"));
        vPanel.add(rbSmallSize);
        vPanel.add(rbMediumSize);
        vPanel.add(rbLargeSize);

        if (size == null || size.equals(Constants.IMG_MEDIUM))
            rbMediumSize.setChecked(true);
        else if (size.equals(Constants.IMG_SMALL))
            rbSmallSize.setChecked(true);
        else
            rbLargeSize.setChecked(true);

        panel.add(grid);




        if (isLink)
            initEditLink();
        else
            initEditAttachment();
    }

    protected String getURL() {
        if (isLink)
            return getLinkURL();
        else
            return getAttURL() + "?"+  Random.nextInt();// + "?width=400&" +  Random.nextInt();
    }

}
