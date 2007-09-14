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
package org.curriki.xwiki.plugin.licence;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class LicenceManagerPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface, LicenceManagerConstant {
    private static Log log = LogFactory.getFactory().getInstance(LicenceManagerPlugin.class);

    public LicenceManagerPlugin(String name, String className, XWikiContext context) {
        super(name, className, context);
    }

    public String getName() {
        return PLUGIN_NAME;
    }

    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
        return new LicenceManagerPluginAPI((LicenceManagerPlugin) plugin, context);
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

    private void initClasses(XWikiContext context) throws XWikiException {
        initLicenceClass(context);
    }

    public String addLicence(String name, boolean compatible, XWikiContext context) throws XWikiException {
        String pageName = context.getWiki().getUniquePageName(LICENCE_SPACE, name, context);
        XWikiDocument xwDoc = context.getWiki().getDocument(LICENCE_SPACE, pageName, context);
        com.xpn.xwiki.api.Object obj;
        Licence doc;

        if (xwDoc.isNew()){
            doc = (Licence) xwDoc.newDocument(Licence.class.getName(), context);
            doc.setCustomClass(Licence.class.getName());
            doc.setTitle(name);
        }
        else
            doc = (Licence) xwDoc.newDocument(context);

        obj = doc.newObject(LICENCE_CLASS_FULLNAME);
        doc.use(obj);
        doc.set(LICENCE_ITEM_NAME, name);
        doc.set(LICENCE_ITEM_CURIKI_COMPATIBLE, Integer.valueOf(compatible?1:0));
        doc.save();
        return doc.getFullName();
    }

    public List getCompatibleLicences(XWikiContext context) throws XWikiException {
        return getLicences(true, context);
    }

    public List getNotCompatibleLicences(XWikiContext context) throws XWikiException {
        return getLicences(false, context);
    }

    public List getLicences(boolean compatible, XWikiContext context) throws XWikiException {
        String hql = ", BaseObject as obj, IntegerProperty as prop where obj.name=doc.fullName"
                + " and obj.className='" + LICENCE_CLASS_FULLNAME + "' and prop.id.id = obj.id "
                + "and prop.id.name = '" + LICENCE_ITEM_CURIKI_COMPATIBLE + "' and prop.value=" + (compatible?1:0);
        return context.getWiki().getStore().searchDocumentsNames(hql, context);
    }

    public String getLicenceName(String name, XWikiContext context) throws XWikiException {
        Licence lic = (Licence) context.getWiki().getDocument(LICENCE_SPACE, name, context).newDocument(context);
        lic.use(LICENCE_CLASS_FULLNAME);
        return (String) lic.get(LICENCE_ITEM_NAME);
    }

    private void initLicenceClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(LICENCE_CLASS_FULLNAME, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setSpace("XWiki");
            doc.setName(LICENCE_CLASS_NAME);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(LICENCE_CLASS_FULLNAME);
        needsUpdate |= bclass.addBooleanField(LICENCE_ITEM_CURIKI_COMPATIBLE, "compatible", "radio");
        needsUpdate |= bclass.addTextField(LICENCE_ITEM_NAME, "name", 50);

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + LICENCE_CLASS_FULLNAME);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }


}
