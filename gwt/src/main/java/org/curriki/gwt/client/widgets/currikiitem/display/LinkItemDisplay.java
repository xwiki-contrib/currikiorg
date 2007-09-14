package org.curriki.gwt.client.widgets.currikiitem.display;

import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;
import org.curriki.gwt.client.Constants;
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

public class LinkItemDisplay  extends ItemDisplay {

    public LinkItemDisplay(Document doc, CurrikiItem item){
        super(doc, item);
    }

    public String getType() {
        return Constants.TYPE_LINK;
    }

    public boolean save() {
        return saveLink();
    }

    protected String getURL() {
        return getLinkURL();
    }

    protected void initEdit(){
        initEditLink();
    }

    public void initDisplay(Document doc) {
        initDisplayLink(doc);
    }

    
}
