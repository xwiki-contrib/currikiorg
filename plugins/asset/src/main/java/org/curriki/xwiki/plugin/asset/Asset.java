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
 */
package org.curriki.xwiki.plugin.asset;

import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;

import java.io.InputStream;
import java.io.IOException;


public class Asset extends Document {
    public Asset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }


    public void addAttachment(InputStream iStream, String name) throws XWikiException, IOException {
        XWikiAttachment att = addAttachment(name, iStream);
        doc.saveAttachmentContent(att, context);
    }

    public String getDisplayTitle() {
        String className = getActiveClass();

        use("XWiki.AssetClass");
        String title = (String) getValue("title");

        if (className != null)
            use(className);
        
        return (title == null || title.length() == 0) ? "Untitled" : title;
    }

    public void changeOwnership(String newUser) {
        if (hasProgrammingRights()) {
            XWikiDocument assetDoc = getDoc();
            assetDoc.setCreator(newUser);
        }
    }

    /**
     * Set the rights objects based on the current right setting
     *
     * @throws XWikiException
     */
    public void applyRightsPolicy() throws XWikiException {
        applyRightsPolicy(null);
    }

    /**
     * Set the rights object based on the right in param or the current right setting if null
     *
     * @param right
     * @throws XWikiException
     */
    public void applyRightsPolicy(String right) throws XWikiException {
        XWikiDocument assetDoc = getDoc();
        assetDoc.removeObjects("XWiki.XWikiRights");

        BaseObject assetObj = assetDoc.getObject("XWiki.AssetClass");
        String rights;

        if (right == null)
            rights = assetObj.getStringValue("rights");
        else {
            rights = right;
            assetObj.setStringValue("rights", right);
        }

        BaseObject rightObj = assetDoc.newObject("XWiki.XWikiRights", context);
        rightObj.setStringValue("groups", "XWiki.XWikiAdminGroup");
        rightObj.setStringValue("levels", "edit");
        rightObj.setIntValue("allow", 1);

        rightObj = assetDoc.newObject("XWiki.XWikiRights", context);
        rightObj.setStringValue("users", ("".equals(assetDoc.getCreator())) ? context.getUser() : assetDoc.getCreator());
        rightObj.setStringValue("levels", "edit");
        rightObj.setIntValue("allow", 1);

        if (rights != null && rights.equals("public")) {
            rightObj = assetDoc.newObject("XWiki.XWikiRights", context);
            rightObj.setStringValue("groups", "XWiki.XWikiAllGroup");
            rightObj.setStringValue("levels", "edit");
            rightObj.setIntValue("allow", 1);
        } else if (rights != null && rights.equals("members")) {

        } else {
            rightObj = assetDoc.newObject("XWiki.XWikiRights", context);
            rightObj.setStringValue("users", ("".equals(assetDoc.getCreator())) ? context.getUser() : assetDoc.getCreator());
            rightObj.setStringValue("levels", "view");
            rightObj.setIntValue("allow", 1);
        }
    }
}
