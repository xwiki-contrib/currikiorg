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

import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.Attachment;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;

import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.Main;

public class AttachementItemDisplay extends ItemDisplay {


    public AttachementItemDisplay(Document doc, CurrikiItem item){
        super(doc, item);
    }


    public String getType() {
        return Constants.TYPE_ATTACHMENT;
    }

//    public boolean isDirty() {
//        return (upload.getFileName() != null && upload.getFileName().length() > 0);
//    }

    public void initDisplay(Document doc) {
        panel.clear();
        initDisplayDescription(doc);
        initDisplayAttachment(doc);
    }

    public boolean save() {
        return saveDescription();
    }

    protected void initEdit(){
        initEditDescription();
        initEditAttachment();
    }

    protected String getURL() {
        return getAttURL();
    }

    public void initDisplayAttachment(Document doc) {

        if (upload != null && panel.getWidgetIndex(upload) != -1) {
            panel.remove(upload);
            upload = null;
        }

        if (icon != null && panel.getWidgetIndex(icon) != -1)
            panel.remove(icon);

        if (doc.getAttachments().size() > 0) {
            // Get Attachment Info
            Attachment att = (Attachment) doc.getAttachments().get(0);
            String attName = att.getFilename();
            String extension = getFileExtension(attName);

            // Create click listener
            ClickListener openClick = new ClickListener() {
                public void onClick(Widget widget) {
                    // Let's open a new window
                    Window.open(getAttURL(), "_blank", "");
                }
            };

            // Create click listener
            ClickListener downloadClick = new ClickListener() {
                public void onClick(Widget widget) {
                    // Let's open a new window
                    String url = getAttURL() + "?force_download=1";
                    Window.open(url, "_blank", "");
                }
            };

            // Create branched icon
            icon = new Image();
            icon.addStyleName("item-download-link");
            icon.setUrl(Constants.MIMETYPE_PATH + extension + ".png");
            icon.addClickListener(openClick);

            if(panel.getWidgetIndex(icon) == -1)
                panel.add(icon);

            initDisplayAttachmentDownloadZone(attName, openClick, downloadClick);
        }
        else
        {
            initDisplayCorruptedAsset();
        }

        item.refreshItemInfos();
        item.switchHeaderButtonsToViewMode();
    }

    protected String getFileExtension(String fileName) {
        String extension = (fileName.lastIndexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase(): null);
        if (extension == null || !Constants.SUPPORTED_MIMETYPES_ICONS.contains(extension)){
            extension = "Unknown";
        }
        return extension;
    }

    protected void initDisplayCorruptedAsset() {
        panel.add(new HTML(doc.getFullName() + Main.getTranslation("asset.asset_corrupted")));
    }

    protected void initDisplayAttachmentDownloadZone(String attName, ClickListener openClick, ClickListener downloadClick) {
        // Download Zone with link and button
        HorizontalPanel downloadZone = new HorizontalPanel();
        downloadZone.addStyleName("item-download-zone");
        Hyperlink link = new Hyperlink(attName, "");
        link.addStyleName("item-download-zone-text");
        link.addClickListener(openClick);
        downloadZone.add(link);
        Button button = new Button(Main.getTranslation("asset.download"), downloadClick);
        button.addStyleName("gwt-ButtonGrey");
        button.addStyleName("item-download-zone-button");
        downloadZone.add(button);
        panel.add(downloadZone);
    }
}
