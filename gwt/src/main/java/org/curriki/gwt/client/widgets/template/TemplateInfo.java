package org.curriki.gwt.client.widgets.template;

import com.google.gwt.user.client.rpc.IsSerializable;

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
 * @author ludovic
 */

/**
 * Template Information used by the ChooseTemplateDialog
 */
public class TemplateInfo implements IsSerializable {
    private String pageName;
    private String title;
    private String description;
    private String imageURL;

    public TemplateInfo() {
        this("","","","");
    }

    public TemplateInfo(String templatePageName, String title, String description, String imageURL) {
        this.pageName = templatePageName;
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;                
    }

    /**
     * Full Page Name of the template
     * @return
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * Sets the full page name of the template
     * @param pageName
     */
    public void setPageName(String pageName) {
        this.pageName = pageName;
    }


    /**
     * Title of the template
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the template
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Detailed description of the template
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets a Detailed description of the template
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Image URL representing this template
     * @return
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * Sets the Image URL representing the template
     * @param imageURL
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
