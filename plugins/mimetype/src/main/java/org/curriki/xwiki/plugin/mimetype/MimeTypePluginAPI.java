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

    /**
     * Returns the filetype corresponding to the mimetype or the extension
     * The mimetype is checked first, then the extension
     * @param extension
     * @param mimetype
     * @return filetype code
     */
    public String getFileType(String extension, String mimetype) {
         return plugin.getFileType(extension, mimetype, context);
    }

    /**
       * Returns the filetype corresponding to the extension
       * @param extension
       * @return filetype code
       */
      public String getFileType(String extension) {
           return plugin.getFileType(extension, context);
      }

    /**
     * Returns the category corresponding to the filetype
     * @param filetype
     * @return category
     */
    public String getCategory(String filetype) {
        return plugin.getCategory(filetype, context);
    }

    /**
     * Returns the displayer corresponding to the filetype or the category
     * @param filetype
     * @param category
     * @return displayer
     */
    public String getDisplayer(String category, String filetype) {
        return plugin.getDisplayer(category, filetype, context);
    }

}
