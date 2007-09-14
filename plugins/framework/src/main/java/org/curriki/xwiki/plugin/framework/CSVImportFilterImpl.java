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
package org.curriki.xwiki.plugin.framework;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class CSVImportFilterImpl  implements ImportFilter, FrameworkConstant{

    public String getPageName(Object item, XWikiContext context) {
        return (String) item;
    }

    public Framework readFramework(String frameworkName, InputStream iStream, XWikiContext context) throws XWikiException {
        Framework doc = (Framework) context.getWiki().getDocument(FRAMEWORK_PREFIX + frameworkName, FRAMEWORK_HOME, context).newDocument(Framework.class.getName(), context);
        doc.setTitle("Master Framework");
        doc.setContent("#includeForm(\"XWiki.FrameworkTemplate\")");
        doc.setCustomClass(Framework.class.getName());
        doc.getObject(FRAMEWORK_CLASS_FULLNAME, true);
        doc.getObject(FRAMEWORK_ITEM_CLASS_FULLNAME, true);
        return doc;
    }

    public Collection readFrameworkItems(Framework framework, InputStream iStream, XWikiContext context) throws XWikiException {
        BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
        List frameworkItems = new ArrayList();
        String line;
        String parent = "";
        FrameworkItem parentItem = null;
        String space = framework.getSpace();
        try {
            while ((line = br.readLine()) != null) {
                String[] item = line.split(",");
                if (item.length != 2)
                    continue;
                if (!parent.equals(item[0])){
                    FrameworkItem fItem = createFrameworkItem(space + "." + FRAMEWORK_HOME, space, item[0], context);
                    frameworkItems.add(fItem);
                    parent = item[0];
                    parentItem = fItem;
                }
                FrameworkItem fItem = createFrameworkItem(space + "." + parentItem.getName(), space, item[1], context);
                frameworkItems.add(fItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return frameworkItems;
    }

    private FrameworkItem createFrameworkItem(String parentName, String space, String title, XWikiContext context) throws XWikiException {
        FrameworkItem item = (FrameworkItem) context.getWiki().getDocument(space, context.getWiki().getUniquePageName(space, title, context), context).newDocument(FrameworkItem.class.getName(), context);
        item.setParent(parentName);
        item.setTitle(title);
        item.setContent("#includeForm(\"XWiki.FrameworkItemTemplate\")");
        item.getObject(FRAMEWORK_ITEM_CLASS_FULLNAME, true);
        item.save();
        return item;
    }
}

