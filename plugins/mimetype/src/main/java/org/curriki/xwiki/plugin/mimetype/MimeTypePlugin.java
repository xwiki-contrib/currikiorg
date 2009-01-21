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

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.api.Object;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.*;

public class MimeTypePlugin  extends XWikiDefaultPlugin implements XWikiPluginInterface, MimeTypeConstant {

    private static Log mLogger =
            LogFactory.getFactory().getInstance(MimeTypePlugin.class);

    private Properties mimetypeConfig = new Properties();
    private String mimetypeConfigDocVersion = null;


    public MimeTypePlugin(String name, String className, XWikiContext context) {
        super(name, className, context);
    }

    public String getName() {
        return PLUGIN_NAME;
    }

    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
        return new MimeTypePluginAPI((MimeTypePlugin) plugin, context);
    }

    public void virtualInit(XWikiContext context){
    }

    public void init(XWikiContext context){
    }

    protected void loadMimeTypeConfigFromString(String content) throws IOException {
        mimetypeConfig = new Properties();
        ByteArrayInputStream is = new ByteArrayInputStream(content.getBytes());
        mimetypeConfig.load(is);
    }

    protected void loadMimeTypeConfig(XWikiContext context) {
        XWikiDocument doc = null;
        try {
            doc = context.getWiki().getDocument(MIMETYPE_CONFIG, context);
            if (doc.getVersion()!=mimetypeConfigDocVersion) {
                loadMimeTypeConfigFromString(doc.getContent());
                mimetypeConfigDocVersion = doc.getVersion();
            }
        } catch (Exception e) {
            if (mLogger.isErrorEnabled())
                mLogger.error("Error loading mime type configuration", e);
        }
    }

    public String getFileType(String extension, String mimetype, XWikiContext context) {
        loadMimeTypeConfig(context);
        String key;
        String filetype = null;

        if (mimetype!=null) {
            key = "mimetype_" + mimetype.toLowerCase();
            filetype = mimetypeConfig.getProperty(key);
        }

        if ((filetype==null||filetype.equals(""))&&(extension!=null)) {
            key = "extension_" + extension;
            filetype = mimetypeConfig.getProperty(key);
        }
        return filetype;
    }

    public String getFileType(String extension, XWikiContext context) {
        if (extension==null)
          return null;

        if (mLogger.isErrorEnabled())
         mLogger.error("Checking for extension " + extension);

        loadMimeTypeConfig(context);
        String   key = "extension_" + extension;
        String filetype = mimetypeConfig.getProperty(key);

        if (mLogger.isErrorEnabled())
         mLogger.error("Found filetype " + filetype);

        return filetype;
    }

    public String getCategory(String filetype, XWikiContext context) {
        String category = null;

        loadMimeTypeConfig(context);
        
        if (mLogger.isErrorEnabled())
         mLogger.error("Checking for filetype " + filetype);

        if (filetype!=null) {
            String key = "category_" + filetype.toLowerCase();
            category = mimetypeConfig.getProperty(key);
        }
        if (category==null||category.equals(""))
             category = MIMETYPE_CATEGORY_UNKNOWN;

        if (mLogger.isErrorEnabled())
         mLogger.error("Found category " + category);

        return category;
    }

    public String getDisplayer(String category, String filetype, XWikiContext context) {
        String key;
        String displayer = null;

        loadMimeTypeConfig(context);

        if (filetype!=null) {
            key = "displayer_" + filetype;
            displayer = mimetypeConfig.getProperty(key);
        }

        if ((displayer==null||displayer.equals(""))&&(category!=null)) {
            key = "displayer_" + category;
            displayer = mimetypeConfig.getProperty(key);
        }

        if ((displayer==null||displayer.equals(""))) {
            displayer = category;
        }

        return displayer;
    }

}
