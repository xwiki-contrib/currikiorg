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
 *
 */
package org.curriki.gwt.client.widgets.upload;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.Constants;


public class UploadWidget  extends Composite {
    private HorizontalPanel panel = new HorizontalPanel();

    private FileUpload upload = new FileUpload();

    private Button bttSend;

    private FormPanel uploadForm = new FormPanel();

    Image loadingImg = new Image(Constants.ICON_SPINNER);

    public UploadWidget(String destination, boolean showUploadBtt){
        this(destination);
        bttSend.setVisible(showUploadBtt);
    }

    public UploadWidget(String destination) {
        uploadForm.setAction(destination);
        // Set the form encoding to multipart to indicate a file upload
        uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
        // Set the method to Post
        uploadForm.setMethod(FormPanel.METHOD_POST);

        uploadForm.setWidget(panel);

        upload.setName("filepath");

        bttSend = new Button(Main.getTranslation("editor.btt_send"), new ClickListener() {
            public void onClick(Widget sender) {
                sendFile();
            }
        });

        panel.add(upload);
        panel.add(bttSend);


        // Add an event handler to the form.
        uploadForm.addFormHandler(new FormHandler() {
            public void onSubmit(FormSubmitEvent formSubmitEvent) {
                setLoading();                        
            }

            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                setLoaded();
            }
        });

        initWidget(uploadForm);

    }

    private void setLoaded() {
        panel.remove(loadingImg);
        bttSend.setEnabled(true);
    }

    public boolean sendFile() {
        if (!upload.getFilename().equals("")) {
            uploadForm.submit();
            return true;
        }
        return false;
    }

    private void setLoading() {
        panel.add(loadingImg);
        bttSend.setEnabled(false);
    }

    public void setFilename(String filename){
        panel.add(new Hidden("filename", filename));
    }

    public void setAction(String destination){
        uploadForm.setAction(destination);
    }

    public void addFormHandler(FormHandler handler){
        uploadForm.addFormHandler(handler);    
    }

    public String getFileName(){
        return upload.getFilename();
    }

    public String getFileExtension(){
        String fileName = upload.getFilename();
        
        return (fileName.lastIndexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(): null);
    }

}
