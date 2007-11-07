package org.curriki.gwt.client;

import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
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

public class AssetDocument extends Document {
    private boolean licenceProtected = false;
    private boolean duplicatable = false;
    private boolean currikiTemplate = false;
    private boolean parentEditable = false;
    private boolean parentCurrikiTemplate = false;
    private boolean composite = false;
    private String parent = null;

    public boolean isLicenceProtected() {
        return licenceProtected;
    }

    public void setLicenceProtected(boolean licenceProtected) {
        this.licenceProtected = licenceProtected;
    }

    public boolean isDuplicatable() {
        return duplicatable && !isDirectionBlock();
    }

    public void setDuplicatable(boolean duplicatable) {
        this.duplicatable = duplicatable;
    }

    public boolean isParentEditable() {
        return parentEditable;
    }

    public void setParentEditable(boolean parentEditable) {
        this.parentEditable = parentEditable;
    }

    public boolean isCurrikiTemplate() {
        return currikiTemplate;
    }

    public void setCurrikiTemplate(boolean currikiTemplate) {
        this.currikiTemplate = currikiTemplate;
    }


    public boolean isParentCurrikiTemplate() {
        return parentCurrikiTemplate;
    }

    public void setParentCurrikiTemplate(boolean parentCurrikiTemplate) {
        this.parentCurrikiTemplate = parentCurrikiTemplate;
    }


    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isComposite() {
        return composite;
    }

    public void setComposite(boolean composite) {
        this.composite = composite;
    }

    public boolean isDirectionBlock() {
        XObject obj = getObject(Constants.TEXTASSET_CLASS);
        if (obj==null)
            return false;
        if (obj.get(Constants.TEXTASSET_TYPE_PROPERTY) == null)
            return false;
        if (((Long)obj.get(Constants.TEXTASSET_TYPE_PROPERTY)).longValue() == Constants.TEXTASSET_TYPE_DIRECTION)
            return true;
        else
            return false;
    }
}
