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
package org.gelc.xwiki.plugins.assets;

import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.test.HibernateTestCase;
import com.xpn.xwiki.web.XWikiServletURLFactory;
import org.gelc.xwiki.plugins.framework.FrameworkManagerPluginAPI;
import org.gelc.xwiki.plugins.framework.FrameworkItem;
import org.gelc.xwiki.plugins.framework.Framework;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;


public class AssetManagerPluginTest extends HibernateTestCase {
    public static String FRAMEWORK_NAME = "testFramework";

    protected void setUp() throws Exception {
        super.setUp();
        getXWiki().getPluginManager().addPlugin("framework_manager","org.gelc.xwiki.plugins.framework.FrameworkManagerPlugin", getXWikiContext());
        getXWiki().getPluginManager().addPlugin("assets_manager","org.gelc.xwiki.plugins.assets.AssetManagerPlugin", getXWikiContext());

        getXWikiContext().setURLFactory(new XWikiServletURLFactory(new URL("http://www.xwiki.org/"), "xwiki/" , "bin/"));

    }

    public void testAddAsset() throws IOException, XWikiException {
        AssetManagerPluginApi amng = (AssetManagerPluginApi) xwiki.getPluginApi(AssetConstant.PLUGIN_NAME, context);
        assertNotNull("The plugin is not on the list of plugins", amng);
        Asset asset = amng.createAssetDocument("test Asset");
        assertNotNull(asset);
        assertEquals("test Asset", asset.getTitle());

        assertEquals(AssetConstant.ASSET_TEMPORARY_SPACE, asset.getSpace());

        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_ATTACHEMENT));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_EXTERNAL_LINK));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_ADDTIONAL_FRAMEWORK_SELECTED));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_FRAMEWORK_ITEM_SELECTED));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_LICENCE));
        assertFalse(amng.isComplet(asset));

        byte[] content = "content test".getBytes();
        amng.addAttachment(new ByteArrayInputStream(content), "att1", asset);

        assertTrue(amng.getStatus(asset, AssetConstant.ASSET_STATUS_ATTACHEMENT));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_EXTERNAL_LINK));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_ADDTIONAL_FRAMEWORK_SELECTED));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_FRAMEWORK_ITEM_SELECTED));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_LICENCE));
        assertFalse(amng.isComplet(asset));

        importFramework();

        FrameworkItem item = getFrameworkItem();
        assertNotNull(item);

        asset.set(AssetConstant.ASSET_ITEM_FRAMEWORK_ITEMS, item.getFullName());

        String itemName = amng.getMasterFrameworkItem(asset);
        assertNotNull(itemName);
        assertEquals(item.getFullName(), itemName);

        assertTrue(amng.getStatus(asset, AssetConstant.ASSET_STATUS_ATTACHEMENT));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_EXTERNAL_LINK));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_ADDTIONAL_FRAMEWORK_SELECTED));
        assertTrue(amng.getStatus(asset, AssetConstant.ASSET_STATUS_FRAMEWORK_ITEM_SELECTED));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_LICENCE));
        assertTrue(amng.isComplet(asset));

        amng.addExternalAsset(asset, "test");
        amng.addExternalAsset(asset, "test2");

        List links = amng.getExternalAsset(asset);
        assertNotNull(links);
        assertEquals(2, links.size());

        assertTrue(amng.getStatus(asset, AssetConstant.ASSET_STATUS_ATTACHEMENT));
        assertTrue(amng.getStatus(asset, AssetConstant.ASSET_STATUS_EXTERNAL_LINK));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_ADDTIONAL_FRAMEWORK_SELECTED));
        assertTrue(amng.getStatus(asset, AssetConstant.ASSET_STATUS_FRAMEWORK_ITEM_SELECTED));
        assertFalse(amng.getStatus(asset, AssetConstant.ASSET_STATUS_LICENCE));
        assertTrue(amng.isComplet(asset));

    }

    public FrameworkItem getFrameworkItem() throws XWikiException {
        FrameworkManagerPluginAPI fmng = (FrameworkManagerPluginAPI) xwiki.getPluginApi("framework_manager", context);
        Framework fmk = fmng.getFramework(FRAMEWORK_NAME);
        assertNotNull(fmk);

        List children = fmng.getChildren(fmk);
        assertNotNull(children);

        return (FrameworkItem) children.get(1);

    }

    public void testAddAssetSameName() throws XWikiException {
        AssetManagerPluginApi amng = (AssetManagerPluginApi) xwiki.getPluginApi(AssetConstant.PLUGIN_NAME, context);
        assertNotNull("The plugin is not on the list of plugins", amng);
        Asset asset = amng.createAssetDocument("test Asset");
        assertNotNull(asset);
        asset.save();


        Asset asset2 = amng.createAssetDocument("test Asset");
        assertNotNull(asset2);

        assertTrue(asset.getSpace().equals(asset2.getSpace()));
        assertFalse(asset.getName().equals(asset2.getName()));
    }

    private void importFramework() throws FileNotFoundException, XWikiException {
        File xmlFile = new File(getClass().getResource("/framework_Arizona.xml").getFile());
        InputStream iStream = new FileInputStream(xmlFile);
        FrameworkManagerPluginAPI fmng = (FrameworkManagerPluginAPI) xwiki.getPluginApi("framework_manager", context);
        assertNotNull("The plugin is not on the list of plugins", fmng);
        assertTrue(fmng.importFramework(FRAMEWORK_NAME, iStream));
    }
}
