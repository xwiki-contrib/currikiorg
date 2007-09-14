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
import com.xpn.xwiki.plugin.PluginException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DefaultImportFilterImpl implements ImportFilter, FrameworkConstant{

    public String getPageName(Object item, XWikiContext context) {
        Element itemEl = (Element) item;
        return itemEl.element(LEARNING_STANDARD_DOCUMENT_ITEM_IDENTIFIER).getText();
    }    

    public Framework readFramework(String frameworkName, InputStream iStream, XWikiContext context) throws XWikiException {
        SAXReader reader = new SAXReader();
        Document domdoc;

        try {
            domdoc = reader.read(iStream);
        } catch (DocumentException e) {
            throw new PluginException(PLUGIN_NAME, ERROR_FRAMEWORK_CANNOT_IMPORT_DOCUMENT, "Cannont read the xml file of the framework to import", e);
        }
        Element docEl = domdoc.getRootElement().element(LEARNING_STANDARD_DOCUMENT_ROOT);
        context.put(CONTEXT_KEY_IMPORT_DOC_EL, docEl);
        Framework doc = (Framework) context.getWiki().getDocument(FRAMEWORK_PREFIX + frameworkName, FRAMEWORK_HOME, context).newDocument(Framework.class.getName(), context);
        if (!doc.isNew())
            throw new PluginException(PLUGIN_NAME, ERROR_FRAMEWORK_ALREADY_EXIST, "the framework " + frameworkName + " already exist, choose another name");
        doc.setTitle(docEl.element(LEARNING_STANDARD_DOCUMENT_TITLE).getText());
        doc.setCustomClass(Framework.class.getName());
        doc.newObject(FRAMEWORK_CLASS_FULLNAME);
        return doc;
    }

    public Collection readFrameworkItems(Framework framework, InputStream iStream, XWikiContext context) throws XWikiException {
        Element el = (Element) context.get(CONTEXT_KEY_IMPORT_DOC_EL);
        List items = el.elements(LEARNING_STANDARD_DOCUMENT_ITEM);
        Map itemsDoc = new HashMap();
        Iterator it = items.iterator();
        while(it.hasNext()){
            Element itemEl = (Element) it.next();
            FrameworkItem item = readFrameworkItem(itemEl, this, framework.getWeb(), context);
            itemsDoc.put(item.getIdentifier(context), item);
        }

        resetItemsParents(itemsDoc, context);

        return itemsDoc.values();
    }

    private FrameworkItem readFrameworkItem(Element itemEl, ImportFilter filter, String space, XWikiContext context) throws XWikiException {
        FrameworkItem doc = new FrameworkItem(context.getWiki().getDocument(space, filter.getPageName(itemEl, context), context), context);
        if (!doc.isNew())
            throw new PluginException(PLUGIN_NAME, ERROR_FRAMEWORK_ITEM_ALREADY_EXIST, "the framework item " + itemEl + " already exist, choose another name");

        Element descEl = itemEl.element(LEARNING_STANDARD_DOCUMENT_ITEM_DESCRIPTION);
        if (descEl != null && descEl.element(LEARNING_STANDARD_DOCUMENT_ITEM_TEXT) != null){

            String title = descEl.element(LEARNING_STANDARD_DOCUMENT_ITEM_TEXT).getText();
            if (title.length() > 250)
                title = title.substring(0, 250);
            doc.setTitle(title);
        }

        doc.setIdentifier(itemEl.element(LEARNING_STANDARD_DOCUMENT_ITEM_IDENTIFIER).getText());

        if (itemEl.element(LEARNING_STANDARD_DOCUMENT_ITEM_PARENT) != null)
            doc.setParentIdentifier(itemEl.element(LEARNING_STANDARD_DOCUMENT_ITEM_PARENT).getText());

        doc.setParent(space + "." + FRAMEWORK_HOME);
        doc.setCustomClass(FrameworkItem.class.getName());
        return doc;
    }

    private void resetItemsParents(Map items, XWikiContext context) {
        Iterator it = items.values().iterator();
        while(it.hasNext()){
            FrameworkItem doc = (FrameworkItem) it.next();
            String id = doc.getParentIdentifier(context);
            if (id != null && id.length() > 0)
            {
                FrameworkItem parentItem = (FrameworkItem) items.get(id);
                doc.setParent(parentItem.getFullName());
            }
        }
    }
}
