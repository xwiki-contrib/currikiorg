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
import java.util.*;

public class MimeTypePlugin  extends XWikiDefaultPlugin implements XWikiPluginInterface, MimeTypeConstant {
    private static Log mLogger =
            LogFactory.getFactory().getInstance(MimeTypePlugin.class);


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
        try {
            initClasses(context);
        } catch (XWikiException e) {

        }
    }

    public void init(XWikiContext context){
        try {
            initClasses(context);
        } catch (XWikiException e) {

        }
    }

    public void add(String mimeType, String category, String extension, XWikiContext context) throws XWikiException {
        add(mimeType, category, extension, "", context);
    }

    public void add(String mimeType, String category, String extension, String image, XWikiContext context) throws XWikiException {
        String pageName = context.getWiki().clearName(category, context);
        XWikiDocument xwDoc = context.getWiki().getDocument(MIMETYPE_SPACE, pageName, context);
        Object obj;
        MimeType doc;

        if (xwDoc.isNew()){
            doc = (MimeType) xwDoc.newDocument(MimeType.class.getName(), context);
            doc.setCustomClass(MimeType.class.getName());
            doc.setTitle(category);
        }
        else
            doc = (MimeType) xwDoc.newDocument(context);

        obj = doc.newObject(MIMETYPE_CLASS_FULLNAME);
        doc.use(obj);
        doc.set(MIMETYPE_ITEM_MIME_TYPE, mimeType);
        doc.set(MIMETYPE_ITEM_CATEGORY, category);
        doc.set(MIMETYPE_ITEM_ICON, image);
        doc.set(MIMETYPE_ITEM_EXTENSION, extension);
        doc.save();
    }

    public MimeType getCategoryByMimetype(String mimeType, XWikiContext context) throws XWikiException {
        String hql = ", BaseObject as obj, StringProperty as prop where obj.name=doc.fullName"
                        + " and obj.className='" + MIMETYPE_CLASS_FULLNAME + "' and prop.id.id = obj.id "
                        + "and prop.id.name = '" + MIMETYPE_ITEM_MIME_TYPE + "' and prop.value='" + mimeType + "'";
        List list = context.getWiki().getStore().searchDocumentsNames(hql, context);
        if (list.size() > 0)
            return (MimeType) context.getWiki().getDocument((String) list.get(0), context).newDocument(context);
        return null;
    }

    public MimeType getCategoryByExtension(String extension, XWikiContext context) throws XWikiException {
        String hql = ", BaseObject as obj, StringProperty as prop where obj.name=doc.fullName"
                        + " and obj.className='" + MIMETYPE_CLASS_FULLNAME + "' and prop.id.id = obj.id "
                        + "and prop.id.name = '" + MIMETYPE_ITEM_EXTENSION + "' and prop.value='" + extension + "'";
        List list = context.getWiki().getStore().searchDocumentsNames(hql, context);
        if (list.size() > 0)
            return (MimeType) context.getWiki().getDocument((String) list.get(0), context).newDocument(context);
        return null;
    }

    public String getCategoryPageName(String category, XWikiContext context) throws XWikiException {
        String hql = ", BaseObject as obj, StringProperty as prop where obj.name=doc.fullName"
                        + " and obj.className='" + MIMETYPE_CLASS_FULLNAME + "' and prop.id.id = obj.id "
                        + "and prop.id.name = '" + MIMETYPE_ITEM_CATEGORY + "' and prop.value='" + category + "'";
        List list = context.getWiki().getStore().searchDocumentsNames(hql, context);
        if (list.size() > 0)
            return (String) list.get(0);
        return null;
    }

    public List getCategories(XWikiContext context) throws XWikiException {
        String hql = ", BaseObject as obj where obj.name=doc.fullName"
                        + " and obj.className='" + MIMETYPE_CLASS_FULLNAME + "'";
        List list = context.getWiki().getStore().searchDocumentsNames(hql, context);
        List categs = new ArrayList();
        Iterator it = list.iterator();
        while(it.hasNext()){
            MimeType doc = (MimeType) context.getWiki().getDocument((String) it.next(), context).newDocument(context);
            categs.add(doc.getTitle());
        }
        return categs;       
    }

    public Map getCategoriesMap(XWikiContext context) throws XWikiException {
        String hql = ", BaseObject as obj where obj.name=doc.fullName"
                        + " and obj.className='" + MIMETYPE_CLASS_FULLNAME + "'";
        List list = context.getWiki().getStore().searchDocumentsNames(hql, context);
        Iterator it = list.iterator();
        Map categories = new HashMap();
        while(it.hasNext()){
            Document doc = context.getWiki().getDocument((String) it.next(), context).newDocument(context);
            doc.use(MIMETYPE_CLASS_FULLNAME);
            String key = (String) doc.get(MIMETYPE_ITEM_CATEGORY);
            List extList = new ArrayList();
            Vector mimetypes = doc.getObjects(MIMETYPE_CLASS_FULLNAME);
            Iterator itMime = mimetypes.iterator();
            while(itMime.hasNext()){
                Object obj = (Object) itMime.next();
                doc.use(obj);
                String ext = (String) doc.get(MIMETYPE_ITEM_EXTENSION);
                if (ext != null && ext.length() > 0)
                extList.add(ext);
            }
            categories.put(key, extList);
        }
        return categories;
    }

    private void initClasses(XWikiContext context) throws XWikiException {
        initMimeTypeClass(context);
    }


    public static String getCategoriesListHsql(){
        String hql = "select prop.value from BaseObject as obj, StringProperty as prop where "
                    + " obj.className='" + MIMETYPE_CLASS_FULLNAME + "' and prop.id.id = obj.id "
                    + " and prop.id.name = '" + MIMETYPE_ITEM_CATEGORY + "'"
                    + " group by prop.value";
        return hql;
    }

    private void initMimeTypeClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(MIMETYPE_CLASS_FULLNAME, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setSpace("XWiki");
            doc.setName(MIMETYPE_CLASS_NAME);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(MIMETYPE_CLASS_FULLNAME);
        needsUpdate |= bclass.addTextField(MIMETYPE_ITEM_MIME_TYPE, "Mime type", 50);
        needsUpdate |= bclass.addTextField(MIMETYPE_ITEM_EXTENSION, "extension", 50);        
        needsUpdate |= bclass.addTextField(MIMETYPE_ITEM_ICON, "icon", 50);


        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + MIMETYPE_CLASS_FULLNAME);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    public void importMimeType(String fileName, XWikiContext context) throws XWikiException, IOException {
        String[] lines = context.getWiki().getResourceContent(fileName).split("\n");
        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            String mimeType = values[3];
            String extension = values[2];
            String category = values[1];
            String image = "";
            if (values.length >= 6)
                image = values[5];
            add(mimeType, category, extension, image, context);           
        }
    }
}
