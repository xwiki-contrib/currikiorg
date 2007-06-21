/*
 * Copyright 2006, XpertNet SARL, and individual contributors as indicated
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
 *
 * @author jeremi
 */
package org.gelc.xwiki.plugins.assets;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.api.Object;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.objects.classes.DBTreeListClass;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import com.xpn.xwiki.plugin.lucene.LucenePlugin;
import com.xpn.xwiki.web.XWikiRequest;
import org.gelc.xwiki.plugins.framework.FrameworkConstant;
import org.gelc.xwiki.plugins.mime.MimeTypePlugin;
import org.gelc.xwiki.plugins.mime.MimeTypePluginAPI;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class AssetManagerPlugin  extends XWikiDefaultPlugin implements XWikiPluginInterface, AssetConstant{

    public AssetManagerPlugin(String name, String className, XWikiContext context) {
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
        return new AssetManagerPluginApi((AssetManagerPlugin) plugin, context);
    }

    public Asset createAssetDocument(String assetName, XWikiContext context) throws XWikiException {
        String pageName = context.getWiki().getUniquePageName(ASSET_TEMPORARY_SPACE, context.getWiki().clearName(assetName, true, true, context), context).replaceAll("[/\\.]","");
        Asset asset = (Asset) context.getWiki().getDocument(ASSET_TEMPORARY_SPACE, pageName, context).newDocument(Asset.class.getName(), context);
        asset.setTitle(assetName);
        asset.setCustomClass(Asset.class.getName());
        asset.setContent("#includeForm(\"XWiki.AssetTemplate\")");
        XWikiDocument doc = asset.getDocument();
        doc.setAuthor(context.getUser());
        doc.setCreator(context.getUser());
        protectAssetPage(asset, context);
        return asset;
    }

    public Asset publishAsset(Asset asset, XWikiContext context) throws XWikiException {
        String space = getUserCollection(context);
        if (!context.getWiki().exists(space + ".WebHome", context))
            createCollection(getUserName(context), context);
        return publishAsset(asset, getUserCollection(context), context);
    }

    public Asset publishAsset(Asset asset, String collection, XWikiContext context) throws XWikiException {
        if (isComplet(asset, context) && asset.hasAccessLevel("edit") && hasPublishingRight(collection, context)){
            asset.use(ASSET_CLASS_FULLNAME);
            String title = (String) asset.get(ASSET_ITEM_TITLE);

            String distName = context.getWiki().getUniquePageName(collection, title.replaceAll("[/\\.]",""), context);
            distName = context.getWiki().clearName(distName, true, true, context);
            String fullName = collection + "." + distName;
            context.getWiki().renamePage(context.getWiki().getDocument(asset.getFullName(), context), fullName, context);
            asset = (Asset) context.getWiki().getDocument(fullName, context).newDocument(context);
            protectPublishedAsset(asset, context);
            asset.save();

            LucenePlugin lucene = (LucenePlugin) context.getWiki().getPlugin("lucene", context);
            XWikiDocument assetdoc = context.getWiki().getDocument(asset.getFullName(), context);
            // Workaround to make sure uploaded attachments are indexed.
            // Because the XWiki Attachment API does not call the lucene plugin
            if (lucene != null)
                lucene.queueAttachment(assetdoc, context);

            return asset;
        }
        else
            return null;
    }

    private void protectPublishedAsset(Asset asset, XWikiContext context) throws XWikiException {
        asset.removeObjects("XWiki.XWikiRights");

        asset.use(ASSET_CLASS_FULLNAME);
        String right = (String) asset.getValue(ASSET_ITEM_RIGHT);
        if (right==null)
         right = ASSET_ITEM_RIGHT_PRIVATE;

        Object obj = asset.newObject("XWiki.XWikiRights");
        asset.use(obj);
        if (right.equals(ASSET_ITEM_RIGHT_PUBLIC)){
            asset.set("users", "XWiki.XWikiGuest");
            asset.set("groups", "XWiki.XWikiAllGroup");
            asset.set("levels", "view");
            asset.set("allow", Integer.valueOf(1));
        }
        else if (right.equals(ASSET_ITEM_RIGHT_MEMBERS)){
            asset.set("groups", "XWiki.XWikiAllGroup");
            asset.set("levels", "view");
            asset.set("allow", Integer.valueOf(1));
        }
        else {
            asset.set("users", ("".equals(asset.getCreator())) ? context.getUser() : asset.getCreator());
            asset.set("levels", "view");
            asset.set("allow", Integer.valueOf(1));
        }
        obj = asset.newObject("XWiki.XWikiRights");
        asset.use(obj);
        asset.set("users", ("".equals(asset.getCreator())) ? context.getUser() : asset.getCreator());
        asset.set("levels", "edit");
        asset.set("allow", Integer.valueOf(1));
    }

    public boolean hasPublishingRight(String collection, XWikiContext context) throws XWikiException {
        Document doc = context.getWiki().getDocument(collection, "WebHome", context).newDocument(context);
        return doc.hasAccessLevel("edit");
    }

    private String getUserName(XWikiContext context) throws XWikiException {
        String userName = context.getUser();
        XWikiDocument doc = context.getWiki().getDocument(userName, context);
        return doc.getName();
    }

    public String getUserCollection(XWikiContext context) throws XWikiException {
        String userName = getUserName(context);
        return getCollectionSpace(userName, context);
    }

    public String createCollection(String name, XWikiContext context) throws XWikiException {
        String space = getCollectionSpace(context.getWiki().clearName(name, context), context);
        if (context.getWiki().exists(space + ".WebHome", context))
            return null;
        Document doc = context.getWiki().getDocument(space + ".WebHome", context).newDocument(context);
        doc.setContent("#includeForm(\"XWiki.CollectionTemplate\")");
        doc.setTitle(name);
        doc.save();
        protectSpace(space, context);
        return space;
    }

    public String getCollectionSpace(String name, XWikiContext context){
        return COLLECTION_PREFIX + name;
    }

    public void protectPage(Document doc, XWikiContext context) throws XWikiException {
        Object obj = doc.newObject("XWiki.XWikiRights");
        doc.use(obj);
        doc.set("groups", "XWiki.XWikiAdminGroup");
        doc.set("levels", "edit");
        doc.set("allow", Integer.valueOf(1));

        obj = doc.newObject("XWiki.XWikiRights");
        doc.use(obj);
        doc.set("users", ("".equals(doc.getCreator())) ? context.getUser() : doc.getCreator());
        doc.set("levels", "edit");
        doc.set("allow", Integer.valueOf(1));
    }

    public void protectSpace(String spaceName, XWikiContext context) throws XWikiException {
        XWikiDocument doc = context.getWiki().getDocument(spaceName, "WebPreferences", context);
        BaseObject obj = doc.newObject("XWiki.XWikiGlobalRights", context);

        obj.setStringValue("groups", "XWiki.XWikiAdminGroup, XWiki.EditorGroup");
        obj.setStringValue("levels", "admin, edit");
        obj.setIntValue("allow", 1);

        obj = doc.newObject("XWiki.XWikiGlobalRights", context);
        obj.setStringValue("users", context.getUser());
        obj.setStringValue("levels", "edit");
        obj.setIntValue("allow", 1);
        context.getWiki().saveDocument(doc, context);
    }

    private void protectAssetPage(Asset asset, XWikiContext context) throws XWikiException {
        protectPage(asset, context);
    }

    public Asset getTemporaryAssetDocument(String assetName, boolean create, XWikiContext context) throws XWikiException {
        XWikiDocument doc = context.getWiki().getDocument(ASSET_TEMPORARY_SPACE, assetName, context);
        if (doc.isNew() && create){
            return createAssetDocument(assetName, context);
        }
        else if (doc.isNew() && !create){
            return null;
        }
        return (Asset) doc.newDocument(context);
    }

    public String getAssetTemporarySpace(){
        return ASSET_TEMPORARY_SPACE;
    }

    public String createOrUpdateAssetFromRequest(XWikiContext context) throws XWikiException {
        XWikiRequest req = context.getRequest();
        XWiki xwiki = context.getWiki();
        String docName =  req.get(REQUEST_ASSET_FULLNAME);
        Asset asset;

        com.xpn.xwiki.api.Object assetObject;

        if (docName == null || docName.length() == 0){
            docName = xwiki.getUniquePageName(ASSET_TEMPORARY_SPACE, context);
            Document doc = xwiki.getDocument(ASSET_TEMPORARY_SPACE, docName, context).newDocument(context);
            assetObject = doc.updateObjectFromRequest(ASSET_CLASS_FULLNAME);
            String pageName = (String) assetObject.get(ASSET_ITEM_TITLE);
            if (pageName == null)
                pageName = docName;
            else {
                pageName = xwiki.clearName(pageName, true, true, context);
                pageName = xwiki.getUniquePageName(ASSET_TEMPORARY_SPACE, pageName, context);
            }
            asset = createAssetDocument(pageName, context);
            asset.save();
        }
        else {
            asset = (Asset) xwiki.getDocument(ASSET_TEMPORARY_SPACE, docName, context).newDocument(context);
        }
        if (!isAssetContentSetUp(asset, context)){
            if (asset.addAttachments() > 0) {
                LucenePlugin lucene = (LucenePlugin) context.getWiki().getPlugin("lucene", context);
                XWikiDocument assetdoc = xwiki.getDocument(asset.getFullName(), context);
                // Workaround to make sure uploaded attachments are indexed.
                // Because the XWiki Attachment API does not call the lucene plugin
                if (lucene != null)
                    lucene.queueAttachment(assetdoc, context);
                asset = (Asset) assetdoc.newDocument(context);
            }
            else
                asset.addObjectsFromRequest(EXTERNAL_ASSET_CLASS_FULLNAME);
        }
        asset.updateObjectFromRequest(ASSET_CLASS_FULLNAME);

        String[] classList = req.getParameterValues("classList");
        if (classList != null)
            for(int i = 0; i < classList.length; i++)
                asset.updateObjectFromRequest(classList[i]);
        asset.save();
        if (!asset.getSpace().equals(ASSET_TEMPORARY_SPACE)) {
            protectPublishedAsset(asset, context);
            asset.save();        
        }
        return asset.getFullName();
    }


    public boolean isComplet(Asset asset, XWikiContext context) {
        Map res = getStatusList(asset, context);
        if (!isAssetContentSetUp(asset, context))
            return false;
        if (res.get(ASSET_STATUS_FRAMEWORK_ITEM_SELECTED).equals(Integer.valueOf(0)))
            return false;
        if (res.get(ASSET_STATUS_RIGHTS).equals(Integer.valueOf(0)))
            return false;
        return true;
    }

    public boolean isAssetContentSetUp(Asset asset, XWikiContext context){
        Map res = getStatusList(asset, context);
        return !(res.get(ASSET_STATUS_ATTACHEMENT).equals(Integer.valueOf(0)) && res.get(ASSET_STATUS_EXTERNAL_LINK).equals(Integer.valueOf(0)));
    }

    public Map getStatusList(Asset asset, XWikiContext context) {
        Map res = new HashMap();
        if (asset.getAttachmentList().size() == 0)
            res.put(ASSET_STATUS_ATTACHEMENT, Integer.valueOf(0));
        else
            res.put(ASSET_STATUS_ATTACHEMENT, Integer.valueOf(1));

        if (getExternalAsset(asset, context).size() == 0)
            res.put(ASSET_STATUS_EXTERNAL_LINK, Integer.valueOf(0));
        else
            res.put(ASSET_STATUS_EXTERNAL_LINK, Integer.valueOf(1));

        res.put(ASSET_STATUS_ADDTIONAL_FRAMEWORK_SELECTED, Integer.valueOf(0));

        if (getMasterFrameworkItem(asset, context) == null)
            res.put(ASSET_STATUS_FRAMEWORK_ITEM_SELECTED, Integer.valueOf(0));
        else
            res.put(ASSET_STATUS_FRAMEWORK_ITEM_SELECTED, Integer.valueOf(1));

        res.put(ASSET_STATUS_LICENCE, Integer.valueOf(0));

        asset.use(ASSET_CLASS_FULLNAME);
        String right = (String) asset.get(ASSET_ITEM_RIGHT);
        if (right != null && right.length() > 0)
            res.put(ASSET_STATUS_RIGHTS, Integer.valueOf(1));
        else
            res.put(ASSET_STATUS_RIGHTS, Integer.valueOf(0));

        return res;
    }

    public boolean getStatus(Asset asset, Integer statusCode, XWikiContext context) {
        Map status = getStatusList(asset, context);
        return !status.get(statusCode).equals(Integer.valueOf(0));
    }

    public void addAttachment(InputStream iStream, String name, Asset asset, XWikiContext context) throws XWikiException, IOException {
        asset.addAttachment(iStream, name);
    }

    private void initClasses(XWikiContext context) throws XWikiException {
        initFrameworkItemAssetClass(context);
        initExternalAssetClass(context);
        initAssetClass(context);
        initLicenceClass(context);
    }

    private void initFrameworkItemAssetClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(FRAMEWORK_ASSET_CLASS_FULLNAME, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setSpace("XWiki");
            doc.setName(FRAMEWORK_ASSET_CLASS_NAME);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(FRAMEWORK_ASSET_CLASS_FULLNAME);
        needsUpdate |= bclass.addTextField(CLASS_FRAMEWORK_ITEM, "framework Item", 500);

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + FRAMEWORK_ASSET_CLASS_FULLNAME);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    private void initLicenceClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(ASSET_LICENCE_CLASS_FULLNAME, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setSpace("XWiki");
            doc.setName(ASSET_LICENCE_CLASS_NAME);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(ASSET_LICENCE_CLASS_FULLNAME);
        needsUpdate |= bclass.addTextField(ASSET_LICENCE_ITEM_RIGHTS_HOLDER, "Right holder", 60);
        needsUpdate |= bclass.addDateField(ASSET_LICENCE_ITEM_EXPIRY_DATE, "Expiry date");
        needsUpdate |= bclass.addTextField(ASSET_LICENCE_ITEM_EXTERNAL_RIGHTS_HOLDER, "External right holder", 60);
        needsUpdate |= bclass.addTextField(ASSET_LICENCE_ITEM_LICENCE_TYPE, "Licence type", 60);

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + ASSET_LICENCE_CLASS_FULLNAME);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }



    private void initAssetClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(ASSET_CLASS_FULLNAME, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setSpace("XWiki");
            doc.setName(ASSET_CLASS_NAME);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(ASSET_CLASS_FULLNAME);

        needsUpdate |= bclass.addTextField(ASSET_ITEM_TITLE, "title", 60);
        needsUpdate |= bclass.addTextAreaField(ASSET_ITEM_DESCRIPTION, "description", 60, 10);
        needsUpdate |= bclass.addStaticListField(ASSET_ITEM_KEYWORDS, "keywords", 5, true, "");
        needsUpdate |= bclass.addStaticListField(ASSET_ITEM_AGGREGATION_LEVEL, "Aggregation level", 5, false, ASSET_ITEM_AGGREGATION_LEVEL_VALUES);
        needsUpdate |= bclass.addStaticListField(ASSET_ITEM_STATUS, "Status", 5, false, ASSET_ITEM_STATUS_VALUES);

        String hql = "select doc.fullName, doc.title, doc.parent from XWikiDocument as doc, BaseObject as obj where doc.fullName=obj.name and obj.className='" + FrameworkConstant.FRAMEWORK_CLASS_FULLNAME + "' order by doc.title";
        needsUpdate |= bclass.addDBTreeListField(ASSET_ITEM_FRAMEWORK_ITEMS, "Framework Items", 10, true, hql);
        ((DBTreeListClass)bclass.get(ASSET_ITEM_FRAMEWORK_ITEMS)).setPicker(true);        

        needsUpdate |= bclass.addStaticListField(ASSET_ITEM_INSTRUCTIONAL_COMPONENT, "Instructional Component", ASSET_ITEM_INSTRUCTIONAL_COMPONENT_VALUES);
        // needsUpdate |= bclass.addStaticListField(ASSET_ITEM_DIFFICULTY, "Difficulty", ASSET_ITEM_DIFICULTY_VALUES);
        needsUpdate |= bclass.addStaticListField(ASSET_ITEM_EDUCATIONAL_LEVEL, "Educational Level", ASSET_ITEM_EDUCATIONAL_LEVEL_VALUES);
        needsUpdate |= bclass.addStaticListField(ASSET_ITEM_RIGHT, "Right", ASSET_ITEM_RIGHT_VALUES);



        needsUpdate |= bclass.addDBListField(ASSET_ITEM_CATEGORY, "category", MimeTypePlugin.getCategoriesListHsql());

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + ASSET_CLASS_FULLNAME);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }

    private void initExternalAssetClass(XWikiContext context) throws XWikiException {
        XWikiDocument doc;
        XWiki xwiki = context.getWiki();
        boolean needsUpdate = false;

        try {
            doc = xwiki.getDocument(EXTERNAL_ASSET_CLASS_FULLNAME, context);
        } catch (Exception e) {
            doc = new XWikiDocument();
            doc.setSpace("XWiki");
            doc.setName(EXTERNAL_ASSET_CLASS_NAME);
            needsUpdate = true;
        }

        BaseClass bclass = doc.getxWikiClass();
        bclass.setName(EXTERNAL_ASSET_CLASS_FULLNAME);
        needsUpdate |= bclass.addTextField(EXTERNAL_ASSET_LINK, "external Link", 60);

        String content = doc.getContent();
        if ((content==null)||(content.equals(""))) {
            needsUpdate = true;
            doc.setContent("1 " + EXTERNAL_ASSET_CLASS_FULLNAME);
        }

        if (needsUpdate)
            xwiki.saveDocument(doc, context);
    }


    public String getMasterFrameworkItem(Asset asset, XWikiContext context) {
        asset.use(ASSET_CLASS_FULLNAME);
        return (String) asset.get(ASSET_ITEM_FRAMEWORK_ITEMS);
    }

    public List getExternalAsset(Asset asset, XWikiContext context) {
        Vector v = asset.getObjects(EXTERNAL_ASSET_CLASS_FULLNAME);
        List items = new ArrayList();
        Iterator it = v.iterator();
        while(it.hasNext())
        {
            com.xpn.xwiki.api.Object obj = (com.xpn.xwiki.api.Object) it.next();
            if(obj.get(EXTERNAL_ASSET_LINK) != null)
                items.add(obj.get(EXTERNAL_ASSET_LINK));
        }
        return items;
    }

    public boolean addExternalAsset(Asset asset, String link, XWikiContext context) throws XWikiException {
        List items = getExternalAsset(asset, context);
        if (items.contains(link))
            return false;
        com.xpn.xwiki.api.Object obj = asset.newObject(EXTERNAL_ASSET_CLASS_FULLNAME);
        obj.set(EXTERNAL_ASSET_LINK, link);
        return true;
    }

    public String getTechnicalMetaDataClassName(Asset asset, XWikiContext context) throws XWikiException {
        asset.use(ASSET_CLASS_FULLNAME);
        String category = (String) asset.get(ASSET_ITEM_CATEGORY);
        MimeTypePluginAPI mtmng = (MimeTypePluginAPI) context.getWiki().getPluginApi(MimeTypePlugin.PLUGIN_NAME, context);
        String docName = mtmng.getCategoryPageName(category);
        return docName;
    }

    private List WrapDocuments(List xdocs, XWikiContext context){
        List docs = new ArrayList();
        Iterator it  = xdocs.iterator();
        while(it.hasNext()){
            docs.add(((XWikiDocument)it.next()).newDocument(context));
        }
        return docs;
    }

    public List getUnpublishedCollection(XWikiContext context) throws XWikiException {
        return getUnpublishedCollection(context.getUser(), context);
    }

    public List getUnpublishedCollection(String userName, XWikiContext context) throws XWikiException {
        CollectionQuery colQ = new CollectionQuery();
        colQ.author = userName;
        colQ.published = false;
        return getCollection(colQ, context);
    }

    public List getPublishedCollection(XWikiContext context) throws XWikiException {
        return getPublishedCollection(context.getUser(), context);
    }

    public List getPublishedCollection(String userName, XWikiContext context) throws XWikiException {
        CollectionQuery colQ = new CollectionQuery();
        colQ.author = userName;
        colQ.collectionName = context.getWiki().getDocument(userName, context).getName();
        colQ.published = true;
        return getCollection(colQ, context);
    }

    public List getCollection(CollectionQuery colQ, XWikiContext context) throws XWikiException {
        String hql = ", BaseObject as obj where " +
                "doc.fullName=obj.name and obj.className='" + ASSET_CLASS_FULLNAME + "'";
        if (colQ.published)
               hql +=  " and doc.web = '" + getCollectionSpace(colQ.collectionName, context) + "'";
        else
               hql +=  " and doc.web='" + ASSET_TEMPORARY_SPACE + "'";
        if (colQ.author != null && colQ.author.length() > 0)
             hql += " and doc.author = '" + colQ.author + "'";
        hql +=  " order by " + colQ.order;
        List xDocs = context.getWiki().getStore().searchDocuments(hql, context);
        return WrapDocuments(xDocs, context);
    }

    public CollectionQuery getCollectionQuery(){
        return new CollectionQuery();
    }

    public class CollectionQuery{
        public boolean published = true;
        public String collectionName = "";
        public String order = "doc.date";
        public String author = "";
        public int begining = 0;
        public int end = 10;
    }
}

