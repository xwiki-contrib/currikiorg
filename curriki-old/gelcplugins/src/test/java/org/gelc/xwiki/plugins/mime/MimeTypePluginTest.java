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
package org.gelc.xwiki.plugins.mime;

import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.test.HibernateTestCase;
import com.xpn.xwiki.web.XWikiServletURLFactory;

import java.io.IOException;
import java.net.URL;


public class MimeTypePluginTest extends HibernateTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        getXWiki().getPluginManager().addPlugin(MimeTypeConstant.PLUGIN_NAME,"org.gelc.xwiki.plugins.mime.MimeTypePlugin", getXWikiContext());

        getXWikiContext().setURLFactory(new XWikiServletURLFactory(new URL("http://www.xwiki.org/"), "xwiki/" , "bin/"));
    }

    public void testAddMimeType() throws XWikiException {
        MimeTypePluginAPI mtmng = (MimeTypePluginAPI) xwiki.getPluginApi(MimeTypeConstant.PLUGIN_NAME, context);
        mtmng.add("image/gif", "picture(photo)", "gif");
        mtmng.add("image/jpeg", "picture(photo)", "jpg");
        mtmng.add("application/msword", "document", "doc");

        assertEquals("document", mtmng.getCategoryByExtension("doc").getTitle());
        assertEquals("document", mtmng.getCategoryByMimetype("application/msword").getTitle());

        assertEquals("picture(photo)", mtmng.getCategoryByExtension("gif").getTitle());
        assertEquals("picture(photo)", mtmng.getCategoryByMimetype("image/jpeg").getTitle());

        assertEquals(2, mtmng.getCategories().size());
    }

   /* public void testImport() throws IOException, XWikiException {
        MimeTypePluginAPI mtmng = (MimeTypePluginAPI) xwiki.getPluginApi(MimeTypeConstant.PLUGIN_NAME, context);

        mtmng.importMimeType("/metadata.csv");
        assertEquals("picture(photo)", mtmng.getCategoryByExtension("gif").getTitle());
        assertEquals("document", mtmng.getCategoryByMimetype("application/msword").getTitle());

    }*/

}
