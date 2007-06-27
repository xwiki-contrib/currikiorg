/*
 * Copyright 2006-2007, XpertNet SARL, and individual contributors as indicated
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
 */
package org.curriki.xwiki.plugin.mimetype;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;

import java.util.List;
import java.util.Map;
import java.io.IOException;

public class MimeTypePluginAPI  extends Api {
    MimeTypePlugin plugin;

    public MimeTypePluginAPI(MimeTypePlugin plugin, XWikiContext context) {
        super(context);
        this.plugin = plugin;
    }

    public void add(String mimeType, String category, String extension) throws XWikiException {
        plugin.add(mimeType, category, extension, context);
    }

    public MimeType getCategoryByMimetype(String mimeType) throws XWikiException {
        return plugin.getCategoryByMimetype(mimeType, context);
    }

    public MimeType getCategoryByExtension(String mimeType) throws XWikiException {
        return plugin.getCategoryByExtension(mimeType, context);
    }

    public List getCategories() throws XWikiException {
        return plugin.getCategories(context);   
    }

    public void importMimeType(String fileName) throws XWikiException, IOException {
        plugin.importMimeType(fileName, context);
    }

    public String getCategoryPageName(String category) throws XWikiException {
        return plugin.getCategoryPageName(category, context);
    }

    public Map getCategoriesMap() throws XWikiException {
        return plugin.getCategoriesMap(context);
    }
}
