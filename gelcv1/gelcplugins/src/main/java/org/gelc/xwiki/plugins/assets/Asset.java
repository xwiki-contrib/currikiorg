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
package org.gelc.xwiki.plugins.assets;

import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;

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
}
