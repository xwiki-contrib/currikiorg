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
package org.curriki.xwiki.plugin.framework;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.plugin.PluginException;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class FrameworkManagerPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface, FrameworkConstant {
    private Map errorMessageMap = null;
    private Map errorLevelMap = null;

    public FrameworkManagerPlugin(String name, String className, XWikiContext context) {
        super(name, className, context);
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

    public String getName() {
        return PLUGIN_NAME;
    }

    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
        return new FrameworkManagerPluginAPI((FrameworkManagerPlugin) plugin, context);
    }

    public boolean importFramework(String name, byte[] xmlContent, XWikiContext context){
        return false;
    }

    public boolean importFramework(String frameworkName, InputStream iStream, XWikiContext context) throws XWikiException {

        Framework frameworkDoc = getFilter(context).readFramework(frameworkName, iStream, context);

        Collection items = getFilter(context).readFrameworkItems(frameworkDoc, iStream, context);

        frameworkDoc.save();

        saveAllItems(items, context);

        return true;
    }

    private void saveAllItems(Collection items, XWikiContext context) throws XWikiException {
        Iterator it = items.iterator();
        while(it.hasNext()){
            com.xpn.xwiki.api.Document doc = (com.xpn.xwiki.api.Document) it.next();
            doc.save();
        }
    }

    private ImportFilter getFilter(XWikiContext context){
        if (context.get(CONTEXT_KEY_IMPORT_FILTER) == null)
            context.put(CONTEXT_KEY_IMPORT_FILTER, new CSVImportFilterImpl());
        return (ImportFilter) context.get(CONTEXT_KEY_IMPORT_FILTER);
    }

    public boolean setParent(FrameworkItem itemChild, FrameworkItem itemParent, XWikiContext context){
        return false;
    }

    public boolean removeItem(FrameworkItem itemChild, XWikiContext context){
        return removeItem(itemChild, false, context);
    }


    /**
     * if forced is at true, it remove all the children
     * @param itemChild
     * @param forced
     * @return
     */
    public boolean removeItem(FrameworkItem itemChild, boolean forced, XWikiContext context){
        return false;
    }

    public boolean testIntegrity(Framework framework, XWikiContext context){
        return false;
    }


    public Framework getFramework(String frameworkName, XWikiContext context) throws PluginException {
        try {
            return (Framework)context.getWiki().getDocument(FRAMEWORK_PREFIX + frameworkName, "WebHome", context).newDocument(context);
        } catch (XWikiException e) {
            return null;
        }
        catch(ClassCastException e){
            throw new PluginException(getName(), ERROR_FRAMEWORK_DOCUMENT_IS_NOT_A_FRAMEWORK, frameworkName + " is not a valid framework");
        }
    }

    public List getChildrenName(com.xpn.xwiki.api.Document item, XWikiContext context) throws XWikiException {
        String wheresql = "where doc.parent = '" + item.getFullName() + "' order by doc.date desc";
        return context.getWiki().getStore().searchDocumentsNames(wheresql, context);
    }

    public List getChildren(com.xpn.xwiki.api.Document item, XWikiContext context) throws XWikiException {
        List docsName = getChildrenName(item, context);
        List childDocs = new ArrayList();
        if (docsName == null)
            return childDocs;
        Iterator it = docsName.iterator();
        while (it.hasNext()){
            String docName = (String) it.next();
            childDocs.add(context.getWiki().getDocument(docName, context).newDocument(context));
        }
        return childDocs;
    }

    private void initClasses(XWikiContext context) throws XWikiException {
        initFrameworkItemClass(context);
        initFrameworkClass(context);
    }

    private void initFrameworkItemClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(FRAMEWORK_ITEM_CLASS_FULLNAME, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setSpace("XWiki");
            doc.setName(FRAMEWORK_ITEM_CLASS_NAME);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(FRAMEWORK_ITEM_CLASS_FULLNAME);
        needsUpdate |= bclass.addTextField(CLASS_ITEM_IDENTIFIER, "Identifier", 200);
        needsUpdate |= bclass.addTextField(CLASS_ITEM_PARENT_IDENTIFIER, "Parent Identifier", 200);


        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + FRAMEWORK_ITEM_CLASS_NAME);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    private void initFrameworkClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(FRAMEWORK_CLASS_FULLNAME, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setSpace("XWiki");
            doc.setName(FRAMEWORK_CLASS_NAME);
            needsUpdate = true;
        }
        if (doc.isNew())
            needsUpdate = true;

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(FRAMEWORK_CLASS_FULLNAME);

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + FRAMEWORK_CLASS_FULLNAME);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    public boolean testIntegrity(String frameworkName, XWikiContext context) {
        boolean valid = true;

        Framework fmwk;
        try {
            fmwk = getFramework(frameworkName, context);

        } catch (PluginException e) {
            addIntegrityMessage(INTEGRITY_CHECK_ERROR_CANNOT_GET_FRAMEWORK_DOCUMENT, context);
            return false;
        }
        if (getPDFURLReference(fmwk, context) == null){
            valid = false;
            addIntegrityMessage(INTEGRITY_CHECK_ERROR_PDF, context);
        }
        return valid;
    }

    private void addIntegrityMessage(Integer code, XWikiContext context){
        
        int level = getErrorLevelCode(code);
        String msg = getErrorMessage(code);

        if (context.get(CONTEXT_KEY_ERRORS_MSG) == null){
            context.put(CONTEXT_KEY_ERRORS_MSG, new ArrayList());
            context.put(CONTEXT_KEY_ERRORS_CODE, new ArrayList());
        }
        List msgList = (List) context.get(CONTEXT_KEY_ERRORS_MSG);
        List codeList = (List) context.get(CONTEXT_KEY_ERRORS_CODE);

        Object[] errorMsg = {(level == INTEGRITY_CHECK_LEVEL_WARNING? "Warning": "Error"), msg};
        msgList.add(errorMsg);

        Object[] errorCode = {new Integer(level), code};
        codeList.add(errorCode);

    }

    private int getErrorLevelCode(Integer code){
        initErrorMap();
        return ((Integer)errorLevelMap.get(code)).intValue();
    }

    private String getErrorMessage(Integer code){
        initErrorMap();
        return (String)errorMessageMap.get(code);
    }


    private void initErrorMap(){
        if (errorMessageMap != null)
            return;
        errorMessageMap = new HashMap();
        errorLevelMap = new HashMap();
        errorMessageMap.put(INTEGRITY_CHECK_ERROR_PDF, INTEGRITY_CHECK_ERROR_PDF_TEXT);
        errorLevelMap.put(INTEGRITY_CHECK_ERROR_PDF, INTEGRITY_CHECK_ERROR_PDF_LEVEL);
    }

    public String getPDFURLReference(Framework fmwk, XWikiContext context){
        if (fmwk.getAttachment(FRAMEWORK_REFERENCE_PDF_NAME) != null)
            return fmwk.getAttachmentURL(FRAMEWORK_REFERENCE_PDF_NAME);
        return null;
    }

    public Vector getPath(FrameworkItem item, XWikiContext context) throws XWikiException {
        List parents = new ArrayList();
        Vector path = new Vector();
        path.add(item);
        com.xpn.xwiki.api.Document tmpItem = item;
        while(tmpItem instanceof FrameworkItem){
            String pageName = tmpItem.getParent();
            if (pageName != null && pageName.length() > 0){
                tmpItem = context.getWiki().getDocument(pageName, context).newDocument(context);
                if (parents.contains(pageName))
                    throw new PluginException(getName(), ERROR_FRAMEWORK_RECURSIVE_PATH, "recursive path");
                parents.add(pageName);
                path.add(0, tmpItem);
            }
            else
                throw new PluginException(getName(), ERROR_FRAMEWORK_PATH_ERROR, "Wrong path");
        }
        if (!(tmpItem instanceof Framework))
            throw new PluginException(getName(), ERROR_FRAMEWORK_PATH_ERROR, "Wrong path");
        return path;
    }



}
