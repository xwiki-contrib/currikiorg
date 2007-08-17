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
package org.curriki.gwt.server;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XWikiGWTException;
import com.xpn.xwiki.gwt.api.server.XWikiServiceImpl;
import com.xpn.xwiki.objects.BaseElement;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.ListProperty;
import com.xpn.xwiki.objects.PropertyInterface;
import com.xpn.xwiki.objects.classes.ListItem;
import com.xpn.xwiki.plugin.image.ImagePlugin;
import com.xpn.xwiki.plugin.lucene.LucenePlugin;
import com.xpn.xwiki.plugin.lucene.LucenePluginApi;
import com.xpn.xwiki.plugin.lucene.SearchResult;
import com.xpn.xwiki.plugin.lucene.SearchResults;
import com.xpn.xwiki.plugin.zipexplorer.ZipExplorerPlugin;
import com.xpn.xwiki.web.XWikiEngineContext;
import com.xpn.xwiki.web.XWikiMessageTool;
import com.xpn.xwiki.web.XWikiRequest;
import com.xpn.xwiki.web.XWikiResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.curriki.gwt.client.AssetDocument;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.TreeListItem;
import org.curriki.gwt.client.widgets.browseasset.AssetItem;
import org.curriki.gwt.client.widgets.template.TemplateInfo;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.mimetype.MimeType;
import org.curriki.xwiki.plugin.mimetype.MimeTypePlugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class CurrikiServiceImpl extends XWikiServiceImpl implements CurrikiService {
    private static final Log log = LogFactory.getLog(CurrikiServiceImpl.class);

    public CurrikiServiceImpl() {
        super();
    }

    public CurrikiServiceImpl(XWikiRequest request, XWikiResponse response, XWikiEngineContext engine) {
        super(request, response, engine);
    }

    public boolean isDefaultCollectionExists(String space) throws XWikiGWTException {
        return isCollectionExists(space, Constants.DEFAULT_COLLECTION_PAGE);
    }

    private boolean isCollectionExists(String space, String pageName) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();

            XWikiDocument doc = context.getWiki().getDocument(space, pageName, context);
            if (doc.isNew())
                return false;
            if (doc.getObjectNumbers(Constants.COMPOSITEASSET_CLASS) == 0){
                return false;
            } else {
                // Work around a bug XWIKI-1624
                // TODO: Remove the work-around once XWIKI-1624 is fixed
                List subAssets = doc.getObjects(Constants.COMPOSITEASSET_CLASS);
                Iterator i = subAssets.iterator();
                int count = 0;
                while (i.hasNext() && count == 0){
                    if (i.next() != null){
                        count++;
                    }
                }
                if (count == 0){
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    private boolean createDefaultCollection(String space) throws XWikiGWTException {
        return createCollection(space, Constants.DEFAULT_COLLECTION_PAGE);
    }

    public boolean createCollection(String space, String pageName) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();

            XWikiMessageTool msg = (XWikiMessageTool) context.get("msg");
            String pageTitle = msg.get("Untitled");
            return (createCollection(space, pageName, pageTitle) != null);
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    public String createCollection(String space, String pageName, String pageTitle) throws XWikiGWTException {
        Document doc = createCollectionDocument(space, pageName, pageTitle);
        if (doc != null){
            return doc.getFullName();
        } else {
            return null;
        }
    }

    public Document createCollectionDocument(String space, String pageName, String pageTitle) throws XWikiGWTException {

        try {
            XWikiContext context = getXWikiContext();
            if (space == null){
                // Use default space for user
                space = "Coll_" + context.getUser().replaceFirst("XWiki.", "");
            }

            if (pageName == null){
                // Generate a random page name
                pageName = context.getWiki().getUniquePageName(space, context);
            }

            if (pageTitle == null){
                XWikiMessageTool msg = (XWikiMessageTool) context.get("msg");
                pageTitle = msg.get("Untitled");
            }

            // TODO: Why in createCollection we don't raise an error if the collection exist instead of returning it ?
            if (isCollectionExists(space, pageName)){
                try {
                    XWikiDocument doc = context.getWiki().getDocument(space, pageName, context);
                    return newDocument(new Document(), doc, true, false, true, false, context);
                } catch (Exception e) {
                    throw getXWikiGWTException(e);
                }
            }


            if (!isRootCollectionExists(space, context)){
                createRootCollection(space, context);
            }

            /* CURRIKI-816 - No longer create DEFAULT collection when creating another collection
            if (!pageName.equals(Constants.DEFAULT_COLLECTION_PAGE)){
                if (!isDefaultCollectionExists(space)){
                    createDefaultCollection(space);
                }
            }
            */

            Document doc;
            if (pageName.equals(Constants.DEFAULT_COLLECTION_PAGE)){
                // Just create the Default collection
                doc = createCompositeAsset(space, space+"."+Constants.ROOT_COLLECTION_PAGE, pageName, Constants.COMPOSITE_COLLECTION, -1);
            } else {
                doc = createTempCompositeAsset(space+"."+Constants.ROOT_COLLECTION_PAGE, Constants.COMPOSITE_COLLECTION);
            }

            XWikiDocument xDoc = initCollectionSettings(doc.getFullName(), pageTitle, context);

            return newDocument(new Document(), xDoc, true, false, true, false, context);
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    private XWikiDocument initCollectionSettings(String fullName, String pageTitle, XWikiContext context) throws XWikiException {
        XWikiDocument doc = context.getWiki().getDocument(fullName, context);

        BaseObject obj = doc.getObject(Constants.ASSET_CLASS);
        obj.set(Constants.ASSET_RIGHTS_PROPERTY, Constants.RIGHT_PUBLIC, context);
        obj.set(Constants.ASSET_CATEGORY_PROPERTY, Constants.CATEGORY_COLLECTION, context);
        obj.set(Constants.ASSET_TITLE_PROPERTY, pageTitle, context);
        if (doc.getName().equals(Constants.DEFAULT_COLLECTION_PAGE)){
            String username = context.getUser();
            username = username.substring(username.indexOf(".")+1);
            pageTitle = username + "'s " + context.getMessageTool().get("default_collection");
            obj.setStringValue(Constants.ASSET_TITLE_PROPERTY, pageTitle);
            obj.setStringValue(Constants.ASSET_DESCRIPTION_PROPERTY, pageTitle);

            // we select the "Other" the education level
            List eduList = new ArrayList();
            eduList.add("na");
            obj.setDBStringListValue(Constants.ASSET_EDUCATIONAL_LEVEL_PROPERTY, eduList);
        }

        // we select the root of the Master framework
        List fwList = new ArrayList();
        fwList.add("FW_masterFramework.WebHome");
        obj.setStringListValue(Constants.ASSET_FW_ITEMS_PROPERTY, fwList);

        context.getWiki().saveDocument(doc, context.getMessageTool().get("curriki.comment.initmetadatafornewcollection"), context);
        return doc;
    }

    public Document updateMetadata(String fullName, boolean fromTemplate) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();

            XWikiDocument doc = context.getWiki().getDocument(fullName, context);

            BaseObject assetObj = doc.getObject(Constants.ASSET_CLASS);

            // Here we discover the type of file attached
            if (doc.getAttachmentList().size() > 0 || doc.getObject(Constants.EXTERNAL_ASSET_CLASS) != null){
                String category = discoverTechnicalMetadata(doc, context);
                assetObj.setStringValue(Constants.ASSET_CATEGORY_PROPERTY, category);
            }

            //if it's a file uploaded or a link we derive the title of the asset from the name of the file
            String title = assetObj.getStringValue(Constants.ASSET_TITLE_PROPERTY);
            if ((title==null)||(title.equals(""))) {
                title = discoverAssetTitle(doc);
                if (title != null){
                    assetObj.setStringValue(Constants.ASSET_TITLE_PROPERTY, title);
                }
            }

            context.getWiki().saveDocument(doc, context.getMessageTool().get("curriki.comment.initmetadatafornewasset"), context);

            return newDocument(new Document(), doc, true, false, true, false, context);
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    private String discoverAssetTitle(XWikiDocument doc){

        String title = null;
        if (doc.getAttachmentList().size() > 0){
            title  = ((XWikiAttachment)doc.getAttachmentList().get(0)).getFilename();


        }
        else if (doc.getObject(Constants.EXTERNAL_ASSET_CLASS) != null){
            title = doc.getObject(Constants.EXTERNAL_ASSET_CLASS).getStringValue(Constants.EXTERNAL_ASSET_LINK_PROPERTY);
            if (title.lastIndexOf("/") == title.length() - 1){
                title = title.substring(0, title.length() - 1);
            }
            if (title.contains("/")){
                title = title.substring(title.lastIndexOf("/") + 1, title.length());
            }
        }

        if (title == null)
            return null;

        if (title.contains("."))
            title = title.substring(0, title.lastIndexOf("."));

        title = title.replace("_", " ");

        return title;

    }

    public boolean isRootCollectionExists(String space, XWikiContext context) throws XWikiGWTException {
        try {
            XWikiDocument doc = context.getWiki().getDocument(space, Constants.ROOT_COLLECTION_PAGE, context);
            if (doc.isNew())
                return false;
            if (doc.getObjectNumbers(Constants.COMPOSITEASSET_CLASS) == 0){
                return false;
            } else {
                // Work around a bug XWIKI-1624
                // TODO: Remove the work-around once XWIKI-1624 is fixed
                List subAssets = doc.getObjects(Constants.COMPOSITEASSET_CLASS);
                Iterator i = subAssets.iterator();
                int count = 0;
                while (i.hasNext() && count == 0){
                    if (i.next() != null){
                        count++;
                    }
                }
                if (count == 0){
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    public boolean createRootCollection(String space, XWikiContext context) throws XWikiGWTException {
        try {
            protectSpace(space, context);

            XWikiDocument doc = context.getWiki().getDocument(space, Constants.ROOT_COLLECTION_PAGE, context);

            // We do not check the right when we create a collection we only check if the collection start by Coll_
            // and does not exist
            //if (!doc.isNew() || !space.startsWith("Coll_"))
            //    return false;

            BaseObject CompObj = doc.newObject(Constants.COMPOSITEASSET_CLASS, context);
            CompObj.set(Constants.COMPOSITEASSET_TYPE_PROPERTY, Constants.COMPOSITE_ROOT_COLLECTION, context);

            doc.setCreator(context.getUser());
            doc.setContent(Constants.COMPOSITE_ROOT_COLLECTION_CONTENT);
            doc.setParent(context.getUser());

            protectEditPage(doc, context);

            context.getWiki().saveDocument(doc, context.getMessageTool().get("curriki.comment.createrootcollection"), context);
            return true;
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }



    private boolean addCompositeAssetToDefaultCollection(String assetPageName, String space, XWikiContext context) throws XWikiGWTException {
        if (!isDefaultCollectionExists(space)) {
            createDefaultCollection(space);
        }

        return insertSubAsset(space + "." + Constants.DEFAULT_COLLECTION_PAGE, assetPageName, -1);
    }

    public boolean addCompositeAssetToCollection(String assetPageName, String collectionName) throws XWikiGWTException {
        return insertSubAsset(collectionName, assetPageName, -1);
    }

    public AssetItem getCollections() throws XWikiGWTException {
        // Get list of all collections for the user
        try {
            XWikiContext context = getXWikiContext();
            String user = context.getUser();
            String space = "Coll_"+user.replaceFirst("XWiki.", "");

            /* CURRIKI-816
             * No longer create DEFAULT collection automatically if any other collection exists
             * but create it if one does not exist
             */
            if (!isRootCollectionExists(space, context)){
                createDefaultCollection(space);
            } else {
                XWikiDocument doc = context.getWiki().getDocument(space+"."+Constants.ROOT_COLLECTION_PAGE, context);
                if (doc.getObjectNumbers(Constants.SUBASSET_CLASS) == 0){
                    createDefaultCollection(space);
                } else {
                    // Work around a bug XWIKI-1624
                    // TODO: Remove the work-around once XWIKI-1624 is fixed
                    List subAssets = doc.getObjects(Constants.SUBASSET_CLASS);
                    Iterator i = subAssets.iterator();
                    int count = 0;
                    while (i.hasNext() && count == 0){
                        if (i.next() != null){
                            count++;
                        }
                    }
                    if (count == 0){
                        createDefaultCollection(space);
                    }
                }
            }

            return getCollectionTreeItem(space+"."+Constants.ROOT_COLLECTION_PAGE);
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    public Document createCompositeAsset(String space) throws XWikiGWTException {
        return createCompositeAsset(space, null, -1);
    }

    /**
     * @param space
     * @return the Document of the newly created composite asset
     */
    public Document createCompositeAsset(String space, String parent, long position) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            String pageName = context.getWiki().getUniquePageName(space, context);
            return createCompositeAsset(space, parent, pageName, position);
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    /**
     * @param space
     * @return the Document of the newly created composite asset
     */
    public Document createCompositeAsset(String space, String parent, String pageName, long position) throws XWikiGWTException {
        return createCompositeAsset(space, parent, pageName, Constants.COMPOSITE_CURRIKI_DOCUMENT, position);
    }

    private void assertIfCompositeAssetDoesNotExist(String fullName, XWikiContext context) throws XWikiException {
        XWikiDocument doc = context.getWiki().getDocument(fullName, context);
        if (doc.getObject(Constants.COMPOSITEASSET_CLASS) == null)
            throw new XWikiException(XWikiException.MODULE_XWIKI_GWT_API, -1, "Parent composite asset does not exist");
    }

    /**
     * @param space
     * @return the Document of the newly created composite asset
     */
    public Document createCompositeAsset(String space, String parent, String pageName, String compositeAssetType, long position) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();

            if (!isRootCollectionExists(space, context)){
                createRootCollection(space, context);
            }
            if (parent == null){
                parent = space + "." + Constants.DEFAULT_COLLECTION_PAGE;
            }

            // we create the default collection if the parent is the default one and does not already exist
            if (parent.endsWith("." + Constants.DEFAULT_COLLECTION_PAGE) && !isDefaultCollectionExists(space)){
                createDefaultCollection(space);
            }

            assertIfCompositeAssetDoesNotExist(parent, context);

                
            XWikiDocument doc = createSourceAsset(parent, space, pageName, context);//context.getWiki().getDocument(space + "." + pageName, context);
            //assertEditRight(doc, context);

            doc.setContent("#includeForm(\"XWiki.CompositeAssetTemplate\")");

            BaseObject CompObj = doc.newObject(Constants.COMPOSITEASSET_CLASS, context);
            CompObj.set(Constants.COMPOSITEASSET_TYPE_PROPERTY, compositeAssetType, context);

            if (compositeAssetType.equals(Constants.COMPOSITE_COLLECTION)){
                protectEditPage(doc, context);
            }

            //doc.setCreator(context.getUser());

            //protectPage(doc, context);

            context.getWiki().saveDocument(doc, context.getMessageTool().get("curriki.comment.startcreatingcompositeasset"), context);

            insertSubAsset(parent, doc.getFullName(), position);

            return newDocument(new Document(), doc, true, false, true, false, context);
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    public Document createTempCompositeAsset(String parent) throws XWikiGWTException {
        return createTempCompositeAsset(parent, Constants.COMPOSITE_CURRIKI_DOCUMENT);
    }

    public Document createTempCompositeAsset(String parent, String compositeAssetType) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();

            XWikiDocument doc = createTempSourceAsset(parent, context);

            BaseObject compObj = doc.newObject(Constants.COMPOSITEASSET_CLASS, context);
            compObj.set(Constants.COMPOSITEASSET_TYPE_PROPERTY, compositeAssetType, context);

            doc.setContent("#includeForm(\"XWiki.CompositeAssetTemplate\")");
            
            doc.setCreator(context.getUser());

            protectPage(doc, context);

            context.getWiki().saveDocument(doc, context.getMessageTool().get("curriki.comment.startcreatingcompositeasset"), context);

            return newDocument(new Document(), doc, true, false, true, false, context);
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    private boolean insertDirectionBlock(String compositeAssetPage, long position) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            XWikiDocument compositeAssetDoc = context.getWiki().getDocument(compositeAssetPage, context);

            BaseObject directionObj = compositeAssetDoc.newObject(Constants.DIRECTION_CLASS, context);

            addSubAsset(compositeAssetDoc, Constants.DIRECTION + directionObj.getNumber(), position, context);

            context.getWiki().saveDocument(compositeAssetDoc, context.getMessageTool().get("curriki.comment.adddirectionblock"), context);

        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
        return false;
    }


    public boolean insertSubAsset(String compositeAssetPage, String assetPageName, long position) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            XWikiDocument compositeAssetDoc = context.getWiki().getDocument(compositeAssetPage, context);

            position = addSubAsset(compositeAssetDoc, assetPageName, position, context);

            XWikiDocument assetDoc = context.getWiki().getDocument(assetPageName, context);
            List params = new ArrayList();
            params.add(assetDoc.getStringValue(Constants.ASSET_TITLE_PROPERTY));
            params.add(assetDoc.getFullName());
            params.add(assetDoc.getStringValue(Constants.ASSET_CATEGORY_PROPERTY));
            params.add("" + position);

            String comment = context.getMessageTool().get("curriki.comment.insertsubassetincompositeasset", params);
            context.getWiki().saveDocument(compositeAssetDoc, comment, context);

            return true;

        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    private long addSubAsset(XWikiDocument compositeAssetDoc, String assetPageName, long position, XWikiContext context) throws XWikiException, XWikiGWTException {
        assertEditRight(compositeAssetDoc, context);

        // CURRIKI-314 -- Do not allow an ancestor to be added as a sub-asset
        XWikiMessageTool msg = (XWikiMessageTool) context.get("msg");

        boolean done = false;
        List searchFor = new ArrayList();
        searchFor.add(compositeAssetDoc.getFullName());

        while (!done){
            String sql = null;
            for (int i=0;i<searchFor.size();i++) {
                String item = (String) searchFor.get(i);
                if (item.equals(assetPageName)){
                    throw new XWikiGWTException(msg.get("addsubasset.recursive_add"), msg.get("addsubasset.recursive_add_message"), XWikiException.ERROR_XWIKI_CONTENT_LINK_INVALID_TARGET, XWikiException.MODULE_XWIKI_GWT_API);
                }
                if (sql != null){
                    sql = sql + ", '" + item + "'";
                } else {
                    sql = "'" + item + "'";
                }
            }

            sql = ", BaseObject as obj, StringProperty as prop where obj.name=doc.fullName and obj.className='XWiki.SubAssetClass' and prop.id.id = obj.id and prop.name='assetpage' and prop.value in (" + sql + ")";
            List list = context.getWiki().getStore().searchDocumentsNames(sql, context);
            if ((list==null)||(list.size()==0)){
                done = true;
            } else {
                searchFor = list;
            }
        }

        if (position!=-1)
         relocateAssets(compositeAssetDoc, position, 1, context);
        else
         position = getEndPosition(compositeAssetDoc, context);

        int index = compositeAssetDoc.createNewObject(Constants.SUBASSET_CLASS, context);
        BaseObject obj = compositeAssetDoc.getObject(Constants.SUBASSET_CLASS, index);

        obj.set(Constants.SUBASSET_ASSETPAGE_PROPERTY, assetPageName, context);
        obj.set(Constants.SUBASSET_ORDER_PROPERTY, new Long(position), context);
        return position;
    }

    public Document createTempSourceAsset(String compositeAssetPage) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            XWikiDocument assetDoc = createTempSourceAsset(compositeAssetPage, context);

            return newDocument(new Document(), assetDoc, true, false, true, false, context);

        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    public List removeSubAsset(String compositeAssetPage, long position) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            XWikiDocument compositeAssetDoc = context.getWiki().getDocument(compositeAssetPage, context);

            assertEditRight(compositeAssetDoc, context);

            List objs = compositeAssetDoc.getObjects(Constants.SUBASSET_CLASS);

            Iterator it = objs.iterator();
            while(it.hasNext()){
                BaseObject obj = (BaseObject) it.next();
                if(obj == null)
                    continue;
                long currPos = obj.getLongValue(Constants.SUBASSET_ORDER_PROPERTY);
                if (currPos == position){
                    compositeAssetDoc.removeObject(obj);
                    relocateAssets(compositeAssetDoc, position, -1, context);

                    XWikiDocument subassetDoc = context.getWiki().getDocument(obj.getStringValue(Constants.SUBASSET_ASSETPAGE_PROPERTY), context);
                    List params = new ArrayList();
                    params.add(subassetDoc.getDisplayTitle(context));
                    params.add(subassetDoc.getFullName());
                    params.add("" + position);
                    String comment = context.getMessageTool().get("curriki.comment.removesubassetincompositeasset", params);

                    context.getWiki().saveDocument(compositeAssetDoc, comment, context);
                    break;
                }
            }

            return getCompositeAsset(compositeAssetPage);

        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    public void applyRightsPolicy(String assetName) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            XWikiDocument assetDoc = context.getWiki().getDocument(assetName, context);
            applyRightsPolicy(assetDoc, context);
            context.getWiki().saveDocument(assetDoc, context.getMessageTool().get("curriki.comment.applyrights"), context);
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    private void applyRightsPolicy(XWikiDocument assetDoc, XWikiContext context) throws XWikiException {

        assetDoc.removeObjects("XWiki.XWikiRights");

        BaseObject assetObj = assetDoc.getObject(Constants.ASSET_CLASS);
        String rights = assetObj.getStringValue(Constants.ASSET_RIGHTS_PROPERTY);

        BaseObject rightObj = assetDoc.newObject("XWiki.XWikiRights", context);
        rightObj.setStringValue("groups", "XWiki.XWikiAdminGroup");
        rightObj.setStringValue("levels", "edit");
        rightObj.setIntValue("allow", 1);

        rightObj = assetDoc.newObject("XWiki.XWikiRights", context);
        rightObj.setStringValue("users", ("".equals(assetDoc.getCreator())) ? context.getUser() : assetDoc.getCreator());
        rightObj.setStringValue("levels", "edit");
        rightObj.setIntValue("allow", 1);

        if (rights != null && rights.equals(Constants.RIGHT_PUBLIC)) {
            rightObj = assetDoc.newObject("XWiki.XWikiRights", context);
            rightObj.setStringValue("groups", "XWiki.XWikiAllGroup");
            rightObj.setStringValue("levels", "edit");
            rightObj.setIntValue("allow", 1);       
        }
        else if (rights != null && rights.equals(Constants.RIGHT_PROTECTED)) {

        }
        else {
            rightObj = assetDoc.newObject("XWiki.XWikiRights", context);
            rightObj.setStringValue("users", ("".equals(assetDoc.getCreator())) ? context.getUser() : assetDoc.getCreator());
            rightObj.setStringValue("levels", "view");
            rightObj.setIntValue("allow", 1);
        }
    }


    private void protectPage(XWikiDocument assetDoc, XWikiContext context) throws XWikiException {
        assetDoc.removeObjects("XWiki.XWikiRights");

        BaseObject obj = assetDoc.newObject("XWiki.XWikiRights", context);
        obj.setStringValue("groups", "XWiki.XWikiAdminGroup");
        obj.setStringValue("levels", "edit");
        obj.setIntValue("allow", 1);

        obj = assetDoc.newObject("XWiki.XWikiRights", context);
        obj.setStringValue("groups", "XWiki.XWikiAllGroup");
        obj.setStringValue("levels", "edit");
        obj.setIntValue("allow", 1);

        obj = assetDoc.newObject("XWiki.XWikiRights", context);
        obj.setStringValue("users", ("".equals(assetDoc.getCreator())) ? context.getUser() : assetDoc.getCreator());
        obj.setStringValue("levels", "edit");
        obj.setIntValue("allow", 1);
    }

    private void protectEditPage(XWikiDocument doc, XWikiContext context) throws XWikiException {
            BaseObject obj = doc.newObject("XWiki.XWikiRights", context);
            obj.setStringValue("users", ("".equals(doc.getCreator())) ? context.getUser() : doc.getCreator());
            obj.setStringValue("levels", "edit");   
            obj.setIntValue("allow", 1);
    }

    private void protectSpace(String spaceName, XWikiContext context) throws XWikiException {
        String owner = context.getUser();
        boolean ownerIsUser = true;
        if (spaceName.startsWith("Coll_")){
            String spaceOwner = "XWiki."+spaceName.replaceFirst("Coll_", "");
            if (context.getWiki().exists(spaceOwner, context)){
                XWikiDocument ownerDoc = context.getWiki().getDocument(spaceOwner, context);
                BaseObject userObj = ownerDoc.getObject("XWiki.XWikiUsers");
                if (userObj != null){
                    owner = spaceOwner;
                    ownerIsUser = true;
                } else {
                    BaseObject groupObj = ownerDoc.getObject("XWiki.XWikiGroups");
                    if (groupObj != null){
                        owner = spaceOwner;
                        ownerIsUser = false;
                    } else {
                        throw new XWikiException(XWikiException.MODULE_XWIKI_GWT_API, XWikiException.ERROR_XWIKI_DOES_NOT_EXIST, "Cannot set owner for "+spaceName+". No user or group exists.");
                    }
                }
            }
        }

        XWikiDocument doc = context.getWiki().getDocument(spaceName, "WebPreferences", context);
        doc.removeObjects("XWiki.XWikiGlobalRights");
        BaseObject obj = doc.newObject("XWiki.XWikiGlobalRights", context);

        obj.setStringValue("groups", "XWiki.XWikiAllGroup, XWiki.EditorGroup");
        obj.setStringValue("levels", "edit");
        obj.setIntValue("allow", 1);

        obj = doc.newObject("XWiki.XWikiGlobalRights", context);
        if (ownerIsUser){
            obj.setStringValue("users", owner);
        } else {
            obj.setStringValue("groups", owner);
        }
        obj.setStringValue("levels", "edit");
        obj.setIntValue("allow", 1);

        context.getWiki().saveDocument(doc, context.getMessageTool().get("curriki.comment.protectspace"), context);
    }


     private XWikiDocument createTempSourceAsset(String compositeAssetPage, XWikiContext context) throws XWikiException, XWikiGWTException {
         return createSourceAsset(compositeAssetPage, Constants.TEMPORARY_ASSET_SPACE, null, context);
     }

    private boolean copyProperty(BaseObject fromObj, BaseObject destObj, String key) throws XWikiException {
        PropertyInterface prop = fromObj.get(key);

        if (prop == null)
            return false;

        PropertyInterface newProp = (PropertyInterface) ((BaseElement)prop).clone();

        newProp.setObject(destObj);

        destObj.safeput(key, newProp);

        return true;
    }

    /**
     * Merging properties (in the case of ListProperties)
     * @param fromObj
     * @param destObj
     * @param key
     * @return
     * @throws XWikiException
     */
    private boolean mergeProperty(BaseObject fromObj, BaseObject destObj, String key) throws XWikiException {
        PropertyInterface prop = fromObj.get(key);

        if (prop == null)
            return false;

        PropertyInterface newProp = destObj.get(key);
        if (newProp==null) {
            newProp = (PropertyInterface) ((BaseElement)prop).clone();
            newProp.setObject(destObj);
            destObj.safeput(key, newProp);
            return true;
        } else {
           if (newProp instanceof ListProperty) {
               List list1 = ((ListProperty)newProp).getList();
               List list2 = ((ListProperty)prop).getList();
               for(int i=0;i<list2.size();i++) {
                   Object item = list2.get(i);
                   if (!list1.contains(item))
                    list1.add(item);
               }
               ((ListProperty)newProp).setList(list1);
               newProp.setObject(destObj);
               destObj.safeput(key, newProp);
               return true;
           } else {
               return false;
           }
        }
    }

    private XWikiDocument createSourceAsset(String compositeAssetPage, String space, String pageName, XWikiContext context) throws XWikiException, XWikiGWTException {
        XWikiDocument compositeAssetDoc = context.getWiki().getDocument(compositeAssetPage, context);
        assertEditRight(compositeAssetDoc, context);

        if (pageName == null){
            pageName = context.getWiki().getUniquePageName(space, context);
        }

        XWikiDocument assetDoc = context.getWiki().getDocument(space, pageName, context);

        BaseObject newObjAsset = assetDoc.newObject(Constants.ASSET_CLASS, context);

        // the Root collection does not have an asset class
        if (compositeAssetDoc.getObject(Constants.ASSET_CLASS) != null) {
            BaseObject parentObjAsset = (BaseObject) compositeAssetDoc.getObject(Constants.ASSET_CLASS).clone();

            copyProperty(parentObjAsset, newObjAsset, Constants.ASSET_EDUCATIONAL_LEVEL_PROPERTY);

            copyProperty(parentObjAsset, newObjAsset, Constants.ASSET_FW_ITEMS_PROPERTY);

            copyProperty(parentObjAsset, newObjAsset, Constants.ASSET_RIGHTS_PROPERTY);
        }

        // let's make sure default value is not empty
        String rights = newObjAsset.getStringValue(Constants.ASSET_RIGHTS_PROPERTY);
        if ((rights==null)||(rights.equals("")))
            newObjAsset.setStringValue(Constants.ASSET_RIGHTS_PROPERTY, Constants.RIGHT_PUBLIC);

        BaseObject newLicenceObj = assetDoc.newObject(Constants.ASSET_LICENCE_CLASS, context);
        // the Root collection does not have an asset Licence class
        if (compositeAssetDoc.getObject(Constants.ASSET_LICENCE_CLASS) != null) {

            BaseObject parentLicenceObjAsset = compositeAssetDoc.getObject(Constants.ASSET_LICENCE_CLASS);

            copyProperty(parentLicenceObjAsset, newLicenceObj, Constants.ASSET_LICENCE_TYPE_PROPERTY);

        }
        else {
            List license = new ArrayList();
            license.add(Constants.ASSET_LICENCE_TYPE_DEFAULT);
            newLicenceObj.setDBStringListValue(Constants.ASSET_LICENCE_TYPE_PROPERTY, license);
        }

        // User should be by default the pretty name of the user
        newLicenceObj.setStringValue(Constants.ASSET_LICENCE_RIGHT_HOLDER_PROPERTY, context.getWiki().getLocalUserName(context.getUser(), null, false, context));

        assetDoc.setCustomClass(Asset.class.getName());
        assetDoc.setContent("#includeForm(\"XWiki.AssetTemplate\")");
        assetDoc.setCreator(context.getUser());

        protectPage(assetDoc, context);

        context.getWiki().saveDocument(assetDoc, context.getMessageTool().get("curriki.comment.createnewsourceasset"), context);

        return assetDoc;
    }

    /**
     * Creates a Source Asset from a template by copying all objects and merging the meta data from the parent composite asset
     * @param templatePageName
     * @param compositeAssetPage
     * @return AssetDocument stored in the temporary space
     * @throws XWikiGWTException
     */
    public Document createTempSourceAssetFromTemplate(String templatePageName, String compositeAssetPage, boolean clearattachments) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            XWikiDocument assetDoc = createTempSourceAssetFromTemplate(templatePageName, compositeAssetPage, clearattachments, context);

            return newDocument(new Document(), assetDoc, true, false, true, false, context);

        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    private XWikiDocument createTempSourceAssetFromTemplate(String templatePageName, String compositeAssetPage, boolean clearattachments, XWikiContext context) throws XWikiException, XWikiGWTException {
        return createSourceAssetFromTemplate(templatePageName, compositeAssetPage, Constants.TEMPORARY_ASSET_SPACE, null, clearattachments, context);
    }

    private XWikiDocument createSourceAssetFromTemplate(String templatePageName, String compositeAssetPage, String space, String pageName, boolean clearattachments, XWikiContext context) throws XWikiException, XWikiGWTException {
        XWikiDocument compositeAssetDoc = context.getWiki().getDocument(compositeAssetPage, context);
        assertEditRight(compositeAssetDoc, context);

        XWikiDocument templateAssetDoc = context.getWiki().getDocument(templatePageName, context);
        assertViewRight(templateAssetDoc, context);
        if (assertDuplicateRight(templateAssetDoc, context)==false)
          return null;

        if (pageName == null){
            pageName = context.getWiki().getUniquePageName(space, context);
        }

        XWikiDocument assetDoc = context.getWiki().getDocument(space, pageName, context);

        // Let's make a copy of the template
        assetDoc = templateAssetDoc.copyDocument(space + "." + pageName, context);
        // Remove comments from copied asset
        if (assetDoc.getObjects("XWiki.XWikiComments")!=null)
            assetDoc.getObjects("XWiki.XWikiComments").clear();
        // Let's remove all attachements
        if (clearattachments) {
           assetDoc.setAttachmentList(new ArrayList());
        }

        BaseObject newObjAsset = assetDoc.getObject(Constants.ASSET_CLASS);
        if (newObjAsset==null)
                 newObjAsset = assetDoc.newObject(Constants.ASSET_CLASS, context);

        // the Root collection does not have an asset class
        if (compositeAssetDoc.getObject(Constants.ASSET_CLASS) != null) {
            BaseObject parentObjAsset = (BaseObject) compositeAssetDoc.getObject(Constants.ASSET_CLASS).clone();
            mergeProperty(parentObjAsset, newObjAsset, Constants.ASSET_EDUCATIONAL_LEVEL_PROPERTY);
            mergeProperty(parentObjAsset, newObjAsset, Constants.ASSET_FW_ITEMS_PROPERTY);
            copyProperty(parentObjAsset, newObjAsset, Constants.ASSET_RIGHTS_PROPERTY);
        }

        // let's make sure default value is not empty
        String rights = newObjAsset.getStringValue(Constants.ASSET_RIGHTS_PROPERTY);
        if ((rights==null)||(rights.equals("")))
            newObjAsset.setStringValue(Constants.ASSET_RIGHTS_PROPERTY, Constants.RIGHT_PUBLIC);

        BaseObject newLicenceObj = assetDoc.getObject(Constants.ASSET_LICENCE_CLASS);
        if (newLicenceObj==null)
            newLicenceObj = assetDoc.newObject(Constants.ASSET_LICENCE_CLASS, context);

        // the Root collection does not have an asset Licence class
        if (compositeAssetDoc.getObject(Constants.ASSET_LICENCE_CLASS) != null) {
            BaseObject parentLicenceObjAsset = compositeAssetDoc.getObject(Constants.ASSET_LICENCE_CLASS);
            copyProperty(parentLicenceObjAsset, newLicenceObj, Constants.ASSET_LICENCE_TYPE_PROPERTY);
        }
        else {
            List license = new ArrayList();
            license.add(Constants.ASSET_LICENCE_TYPE_DEFAULT);
            newLicenceObj.setDBStringListValue(Constants.ASSET_LICENCE_TYPE_PROPERTY, license);
        }

        // User should be by default the pretty name of the user added with the current template rights holder
        String newRightsHolder = context.getWiki().getLocalUserName(context.getUser(), null, false, context);
        String templateRightsHolder = templateAssetDoc.getStringValue(Constants.ASSET_LICENCE_CLASS, Constants.ASSET_LICENCE_RIGHT_HOLDER_PROPERTY);
        if (!newRightsHolder.equals(templateRightsHolder))
         newRightsHolder += ", " + templateRightsHolder;
        newLicenceObj.setStringValue(Constants.ASSET_LICENCE_RIGHT_HOLDER_PROPERTY, newRightsHolder);

        // Keep the information allowing to track where that asset came from
        newObjAsset.setStringValue(Constants.ASSET_TRACKING_PROPERTY, templatePageName);
        
        assetDoc.setCreator(context.getUser());
        // Clear rights objects otherwise this will trigger a remove object although these have never been saved
        assetDoc.setObjects("XWiki.XWikiRights", new Vector());
        protectPage(assetDoc, context);
        context.getWiki().saveDocument(assetDoc, context.getMessageTool().get("curriki.comment.createnewsourceassetfromtemplate"), context);
        return assetDoc;
    }

    public String duplicateTemplateAsset(String parentAsset, String templatePageName, long index) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            XWikiDocument compositeAssetDoc = context.getWiki().getDocument(parentAsset, context);
            XWikiDocument templateAssetDoc = context.getWiki().getDocument(templatePageName, context);
            String space = compositeAssetDoc.getSpace();
            // Let's choose a nice name for the page
            String prettyName = context.getWiki().clearName(templateAssetDoc.getStringValue(Constants.ASSET_TITLE_PROPERTY), true, true, context);

            XWikiDocument newAssetDoc = createSourceAssetFromTemplate(templatePageName, parentAsset, space, prettyName, false, context);
            if (newAssetDoc==null) {
                throw new XWikiException(XWikiException.MODULE_XWIKI_GWT_API, -1, "Asset does not allow derivatives");                
            }

            if (replaceSubAsset(compositeAssetDoc, templatePageName, newAssetDoc.getFullName(), index)==false) {
                throw new XWikiException(XWikiException.MODULE_XWIKI_GWT_API, -1, "Could not find page " + templatePageName + " at position " + index + " in composite asset " + parentAsset);
            } else {
                List params = new ArrayList();
                params.add("" + index);
                params.add(newAssetDoc.getFullName());
                params.add(prettyName);
                String comment = context.getMessageTool().get("curriki.comment.duplicatetemplatesourceasset", params);
                context.getWiki().saveDocument(compositeAssetDoc, comment, context);
                return newAssetDoc.getFullName();
            }
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    // Zip Assets
    public List getFileTreeList(String pageName, String fileName) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            ZipExplorerPlugin zipe = (ZipExplorerPlugin) context.getWiki().getPlugin("zipexplorer", context);
            if (zipe==null)
                throw new XWikiException(XWikiException.MODULE_XWIKI_GWT_API, -1, "Zip Explorer is not loaded");

            XWikiDocument doc = context.getWiki().getDocument(pageName, context);
            assertViewRight(doc , context);
            List treeListItems = zipe.getFileTreeList(new com.xpn.xwiki.api.Document(doc, context), fileName, context);
            List tree = new ArrayList();
            if ((treeListItems==null)||(treeListItems.size()==0))
             return tree;

            for (int i=0;i<treeListItems.size();i++) {
                ListItem item = (ListItem) treeListItems.get(i);
                tree.add(new TreeListItem(item.getId(), item.getValue(), item.getParent()));
            }
            return tree;
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    private boolean replaceSubAsset(XWikiDocument compositeAssetDoc, String previousAssetPageName, String newAssetPageName, long position) throws XWikiGWTException {
        List objs = compositeAssetDoc.getObjects(Constants.SUBASSET_CLASS);
        if (objs==null)
         return false;

        Iterator it = objs.iterator();
        while(it.hasNext()){
            BaseObject obj = (BaseObject) it.next();
            if(obj == null)
                continue;
            long currPos = obj.getLongValue(Constants.SUBASSET_ORDER_PROPERTY);
            if (currPos == position){
                String currentAssetPageName = obj.getStringValue(Constants.SUBASSET_ASSETPAGE_PROPERTY);
                if (!currentAssetPageName.equals(previousAssetPageName))
                 return false;
                else {
                    obj.setStringValue(Constants.SUBASSET_ASSETPAGE_PROPERTY, newAssetPageName);
                    return true;
                }
            }
        }
        return false;
    }


    private boolean assertDuplicateRight(XWikiDocument templateAssetDoc, XWikiContext context) throws XWikiGWTException {
        if (templateAssetDoc.getCreator().equals(context.getUser()))
         return true;

        BaseObject obj = templateAssetDoc.getObject(Constants.ASSET_LICENCE_CLASS);
        if (obj != null){
            String licence = obj.getStringValue(Constants.ASSET_LICENCE_TYPE_PROPERTY);
            if (licence.contains("NoDerivatives"))
              return false;
        }

        return true;
    }

    public void finishUpdateMetaData(String assetPage) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();

            XWikiDocument doc = context.getWiki().getDocument(assetPage, context);

            applyRightsPolicy(doc, context);

            context.getWiki().saveDocument(doc, context.getMessageTool().get("curriki.comment.applyrights"), context);
            
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }

    }

    public Document finalizeAssetCreation(String assetPage, String compositeAssetPage, long position) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();

            if (compositeAssetPage.equals(Constants.ROOT_COLLECTION_PAGE)){
                String space = "Coll_"+context.getUser().replaceFirst("XWiki.", "");
                compositeAssetPage = space+"."+Constants.ROOT_COLLECTION_PAGE;
            }

            XWikiDocument compositeAssetDoc = context.getWiki().getDocument(compositeAssetPage, context);

            assertEditRight(compositeAssetDoc, context);

            String space = compositeAssetDoc.getSpace();

            XWikiDocument assetDoc = context.getWiki().getDocument(assetPage, context);
            assertEditRight(assetDoc, context);

            // Let's choose a nice name for the page
            String prettyName = context.getWiki().clearName(assetDoc.getStringValue(Constants.ASSET_TITLE_PROPERTY), true, true, context);
            assetDoc.rename(space + "." + getUniquePageName(space, prettyName), new ArrayList(), context);

            position = addSubAsset(compositeAssetDoc, space + "." + assetDoc.getName(), position, context);

            List params = new ArrayList();
            params.add(assetDoc.getStringValue(Constants.ASSET_TITLE_PROPERTY));
            params.add(assetDoc.getFullName());
            params.add(assetDoc.getStringValue(Constants.ASSET_CATEGORY_PROPERTY));
            params.add("" + position);

            String comment = context.getMessageTool().get("curriki.comment.addingsubassettocompositeasset", params);
            context.getWiki().saveDocument(compositeAssetDoc, comment, context);

            applyRightsPolicy(assetDoc, context);

            params = new ArrayList();
            params.add(assetDoc.getStringValue(Constants.ASSET_CATEGORY_PROPERTY));
            context.getWiki().saveDocument(assetDoc, context.getMessageTool().get("curriki.comment.finishcreatingsubasset", params), context);

            LucenePlugin lucene = (LucenePlugin) context.getWiki().getPlugin("lucene", context);
            // Workaround to make sure assets are indexed.
            // Because the XWiki Attachment API does not call the lucene plugin
            if (lucene != null) {
                lucene.queueDocument(assetDoc, context);
                if (assetDoc.getAttachmentList() != null && assetDoc.getAttachmentList().size() > 0)
                    lucene.queueAttachment(assetDoc, context);
            }

            return getDocument(space + "." + assetDoc.getName());

        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }


    public Document createTextSourceAsset(String compositeAssetPage, long type) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            XWikiDocument assetDoc = createTempSourceAsset(compositeAssetPage, context);
            BaseObject obj = assetDoc.getObject(Constants.TEXTASSET_CLASS,assetDoc.createNewObject(Constants.TEXTASSET_CLASS, context));
            obj.setLongValue(Constants.TEXTASSET_TYPE_PROPERTY, type);

            context.getWiki().saveDocument(assetDoc, context.getMessageTool().get("curriki.comment.createtextsourceasset"), context);

            return newDocument(new Document(), assetDoc, true, false, true, false, context);

        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    public Document createLinkAsset(String compositeAssetPage, String link) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            XWikiDocument assetDoc = createTempSourceAsset(compositeAssetPage, context);

            BaseObject obj = assetDoc.getObject(Constants.EXTERNAL_ASSET_CLASS, assetDoc.createNewObject(Constants.EXTERNAL_ASSET_CLASS, context));
            obj.setStringValue(Constants.EXTERNAL_ASSET_LINK_PROPERTY, link);

            context.getWiki().saveDocument(assetDoc, context.getMessageTool().get("curriki.comment.createlinksourceasset"), context);

            return newDocument(new Document(), assetDoc, true, false, true, false, context);

        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    public void moveAsset(String assetName, String fromParent, long fromPosition, String toParent, long toPosition) throws XWikiGWTException {
        if ((fromParent!=null) && fromParent.equals(toParent) && fromPosition < toPosition){
            toPosition--;
        }
        if (removeSubAsset(fromParent, fromPosition) != null)
            insertSubAsset(toParent, assetName, toPosition);
    }

    /**
     * Relocates subasset to prepare a space to put a new subasset at position startPos
     * @param doc
     * @param startPos
     * @param move
     * @param context
     * @throws XWikiException
     */
    private void relocateAssets(XWikiDocument doc, long startPos, long move, XWikiContext context) throws XWikiException {
        List subassets = doc.getObjects(Constants.SUBASSET_CLASS);
        if (subassets == null)
            return ;
        Iterator it = subassets.iterator();
        while (it.hasNext()) {
            BaseObject obj = (BaseObject) it.next();
            if(obj == null)
                continue;
            long obj_pos = obj.getLongValue(Constants.SUBASSET_ORDER_PROPERTY);
            if (obj_pos >= startPos) {
                obj.set(Constants.SUBASSET_ORDER_PROPERTY, new Long(obj_pos + move), context);
            }
        }
        if(log.isWarnEnabled())
            checkSubAssetPosition(doc);
    }

    /**
     * Get the maximum order in the subasset list
     * @param doc
     * @param context
     * @throws XWikiException
     */
    private long getEndPosition(XWikiDocument doc, XWikiContext context) throws XWikiException {
        long position = 0;
        List subassets = doc.getObjects(Constants.SUBASSET_CLASS);
        if (subassets == null)
            return position;
        Iterator it = subassets.iterator();
        while (it.hasNext()) {
            BaseObject obj = (BaseObject) it.next();
            if(obj == null)
                continue;
            long obj_pos = obj.getLongValue(Constants.SUBASSET_ORDER_PROPERTY);
            if (obj_pos >= position) {
                position = obj_pos + 1;
            }
        }
        return position;
    }

    /**
     * this function is just for debug purpose. it log problems in the order of asset inside a collecttion
     * @param doc
     */
    private void checkSubAssetPosition(XWikiDocument doc){
        List subassets = doc.getObjects(Constants.SUBASSET_CLASS);
        Map orderedList = new HashMap();


        Iterator it = subassets.iterator();
        while(it.hasNext()){
            BaseObject obj = (BaseObject) it.next();
            if(obj == null)
                continue;
            long pos = obj.getLongValue(Constants.SUBASSET_ORDER_PROPERTY);
            if (pos >= subassets.size()){
                log.warn("asset " + obj.getStringValue(Constants.SUBASSET_ASSETPAGE_PROPERTY) + " position is not right.");
                continue;
            }
            if(orderedList.size() > pos && orderedList.get(new Long(pos)) != null) {
                log.warn("we are trying to add " + obj.getStringValue(Constants.SUBASSET_ASSETPAGE_PROPERTY) +
                        " but there is already an asset (" + ((BaseObject)orderedList.get(new Long(pos))).getStringValue(Constants.SUBASSET_ASSETPAGE_PROPERTY)+  ") at the position " + pos + ".");
                continue;
            }
            orderedList.put(new Long(pos), obj);
        }


        for(long i = 0; i < orderedList.size(); i++){
            if (orderedList.get(new Long(i)) == null){
                log.warn("there is no asset at the position " + i);    
            }
        }
    }



    public AssetItem getFullTreeItem(String rootAssetPage) throws XWikiGWTException {
        AssetItem root = new AssetItem(rootAssetPage, 0);

        try {
            return getFullTreeItem(root, getXWikiContext());
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    public AssetItem getFullTreeItem(AssetItem parent, XWikiContext context) throws XWikiException, XWikiGWTException {

        XWikiDocument doc = context.getWiki().getDocument(parent.getAssetPage(), context);

        if (doc.getObject(Constants.COMPOSITEASSET_CLASS) != null){
            parent.setType(Constants.CATEGORY_COLLECTION);
            parent.setProtected(true);
        }

        try {
            assertViewRight(doc, context);
        } catch (Exception e) {
            parent.setText(context.getMessageTool().get("assetprivate"));
            return parent;
        }

        BaseObject obj = doc.getObject(Constants.ASSET_CLASS);
        String title = (obj==null) ? null : obj.getStringValue(Constants.ASSET_TITLE_PROPERTY);
        if (title == null || title.length() == 0) {
            title = context.getMessageTool().get("assetuntitled");
        }
        parent.setText(title);
        List objs_old = doc.getObjects(Constants.SUBASSET_CLASS);
        if (objs_old == null || objs_old.size() == 0)
            return parent;

        List objs = new ArrayList(objs_old);
        List items = new ArrayList();
        while(objs.size() > 0) {
            BaseObject smallerObj = null;
            long smaller_pos = -100;
            Iterator it = objs.iterator();
            while(it.hasNext()) {
                BaseObject currObj = (BaseObject) it.next();
                if(currObj == null)
                    continue;
                long currPos = currObj.getLongValue("order");
                if ((smaller_pos == -100) || (currPos < smaller_pos)) {
                    smaller_pos = currPos;
                    smallerObj = currObj;
                }
            }
            if (smallerObj == null)
                break;
            objs.remove(smallerObj);
            String assetPage = smallerObj.getStringValue(Constants.SUBASSET_ASSETPAGE_PROPERTY);
            long index = smallerObj.getLongValue(Constants.SUBASSET_ORDER_PROPERTY);

            AssetItem currItem = new AssetItem(assetPage, index);
            items.add(currItem);
            if (assetPage.equals(Constants.PAGE_BREAK)){
                currItem.setText("-------------");
            }
            else
                getFullTreeItem(currItem, context);
        }
        parent.setItems(items);
        return parent;

    }



    private String discoverTechnicalMetadata(XWikiDocument doc, XWikiContext context) throws XWikiGWTException {
        String category = null;
        try {
            assertEditRight(doc, context);

            String fileName = null;
            if (doc.getAttachmentList().size() > 0)
            {
                XWikiAttachment attach = (XWikiAttachment) doc.getAttachmentList().get(0);

                fileName = attach.getFilename();

                category = Constants.CATEGORY_UNKNOWN;
            }
            else
            {
                BaseObject obj = doc.getObject(Constants.EXTERNAL_ASSET_CLASS);
                fileName = obj.getStringValue(Constants.EXTERNAL_ASSET_LINK_PROPERTY);
                category = Constants.CATEGORY_LINK;
                if (fileName == null)
                    return null;
                try {
                    URL url = new URL(fileName);

                    fileName = url.getFile();

                } catch (MalformedURLException e) {
                    return category;
                }
            }

            String extension = (fileName.lastIndexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(): null);

            MimeTypePlugin mimePlugin = (MimeTypePlugin) context.getWiki().getPlugin(MimeTypePlugin.PLUGIN_NAME, context);
            MimeType mime = mimePlugin.getCategoryByExtension(extension, context);
            if (mime != null)
                category = mime.getCategoryName();
            if (category.equals(Constants.CATEGORY_IMAGE)){
                ImagePlugin imgPlugin = (ImagePlugin) context.getWiki().getPlugin(ImagePlugin.PLUGIN_NAME, context);

                BaseObject obj = doc.getObject(mime.getFullName());
                if (obj == null){
                    doc.createNewObject(mime.getFullName(), context);
                    obj = doc.getObject(mime.getFullName());
                }


                if (imgPlugin != null && doc.getAttachmentList().size() > 0) {
                    XWikiAttachment att = (XWikiAttachment) doc.getAttachmentList().get(0);
                    int height = imgPlugin.getHeight(att, context);
                    int width = imgPlugin.getWidth(att, context);
                    obj.setIntValue("height", height);
                    obj.setIntValue("width", width);
                }
            }

        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
        return category;
    }

    /**
     * return in the first element the Composite asset and in the second the assets
     *
     * @param compositeAssetPage
     * @return
     */
    public List getCompositeAsset(String compositeAssetPage) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            XWikiDocument assetDoc = context.getWiki().getDocument(compositeAssetPage, context);
            assertViewRight(assetDoc, context);
            assetDoc = (XWikiDocument) assetDoc.clone();

            List objs = assetDoc.getObjects(Constants.SUBASSET_CLASS);

            List res = new ArrayList();
            AssetDocument compositeDoc = (AssetDocument) newCurrikiDocument(assetDoc, true, true, true, false, null, context);
            res.add(compositeDoc);

            List assets = new ArrayList();
            res.add(assets);

            if (objs != null) {
                while (objs.size() > 0) {
                    BaseObject smallerObj = null;
                    long smaller_pos = -1;
                    Iterator it = objs.iterator();
                    while (it.hasNext()) {
                        BaseObject currObj = (BaseObject) it.next();
                        if (currObj == null)
                            continue;
                        long currPos = currObj.getLongValue(Constants.SUBASSET_ORDER_PROPERTY);
                        if ((smaller_pos == -1) || (currPos < smaller_pos)) {
                            smaller_pos = currPos;
                            smallerObj = currObj;
                        }
                    }
                    if (smallerObj == null)
                        break;
                    objs.remove(smallerObj);
                    String assetPage = smallerObj.getStringValue(Constants.SUBASSET_ASSETPAGE_PROPERTY);
                    if (!assetPage.equals(Constants.PAGE_BREAK)) {
                        Document doc = getDocument(assetPage, true, true, false, true);
                        if (doc!=null) {
                            if (doc instanceof AssetDocument) {
                                AssetDocument doc2 = (AssetDocument) doc;
                                doc2.setParentEditable(compositeDoc.hasEditRight());
                                doc2.setParentCurrikiTemplate(compositeDoc.isCurrikiTemplate());
                                doc2.setParent(assetDoc.getFullName());
                                assets.add(doc2);
                            } else {
                                // Document is not corrupted. Let's create new AssetDocument with allowed info
                                XWikiDocument origdoc = context.getWiki().getDocument(assetPage, context);
                                AssetDocument corruptedDoc = new AssetDocument();
                                corruptedDoc.setFullName(origdoc.getFullName());
                                corruptedDoc.setName(origdoc.getName());
                                corruptedDoc.setWeb(origdoc.getSpace());
                                corruptedDoc.setAuthor(origdoc.getAuthor());
                                corruptedDoc.setCreator(origdoc.getCreator());
                                corruptedDoc.setDate(origdoc.getDate().getTime());
                                corruptedDoc.setCreationDate(origdoc.getCreationDate().getTime());
                                corruptedDoc.setParent(assetDoc.getFullName());
                                corruptedDoc.setEditRight(false);
                                corruptedDoc.setViewRight(true);
                                corruptedDoc.setParentEditable(compositeDoc.hasEditRight());
                                corruptedDoc.setParentCurrikiTemplate(compositeDoc.isCurrikiTemplate());
                                assets.add(corruptedDoc);
                            }
                        } else {
                            // Document is not viewable. Let's create new AssetDocument with allowed info
                            XWikiDocument origdoc = context.getWiki().getDocument(assetPage, context);
                            AssetDocument protectedDoc = new AssetDocument();
                            protectedDoc.setFullName(origdoc.getFullName());
                            protectedDoc.setName(origdoc.getName());
                            protectedDoc.setWeb(origdoc.getSpace());
                            protectedDoc.setAuthor(origdoc.getAuthor());
                            protectedDoc.setCreator(origdoc.getCreator());
                            protectedDoc.setDate(origdoc.getDate().getTime());
                            protectedDoc.setCreationDate(origdoc.getCreationDate().getTime());
                            protectedDoc.setParent(assetDoc.getFullName());
                            protectedDoc.setEditRight(false);
                            protectedDoc.setViewRight(false);
                            protectedDoc.setParentEditable(compositeDoc.hasEditRight());
                            protectedDoc.setParentCurrikiTemplate(compositeDoc.isCurrikiTemplate());
                            assets.add(protectedDoc);
                        }
                    }
                    else
                        assets.add(Constants.PAGE_BREAK);
                }
            }
            return res;
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }


    protected Document newCurrikiDocument(XWikiDocument xdoc, boolean withObjects, boolean withViewDisplayers,
                         boolean withEditDisplayers, boolean withRenderedContent, String parent, XWikiContext context)  throws XWikiGWTException {
        AssetDocument doc = (AssetDocument) newDocument(null, xdoc, withObjects, withViewDisplayers, withEditDisplayers, withRenderedContent, context);
        doc.setParent(parent);
        return doc;
    }

    protected Document newDocument(Document doc, XWikiDocument xdoc, boolean withObjects, boolean withViewDisplayers,
                             boolean withEditDisplayers, boolean withRenderedContent, XWikiContext context)  throws XWikiGWTException {
        XWikiDocument cdoc = context.getDoc();
        try {
            context.setDoc(xdoc);
            if(xdoc.getObject(Constants.ASSET_CLASS) != null)
                doc = new AssetDocument();
            else if (doc == null)
                doc = new Document();
            super.newDocument(doc, xdoc, withObjects, withViewDisplayers, withEditDisplayers, withRenderedContent, context);

            BaseObject obj = xdoc.getObject(Constants.ASSET_LICENCE_CLASS);
            if (obj != null){
                String licence = obj.getStringValue(Constants.ASSET_LICENCE_TYPE_PROPERTY);
                boolean licenceProtected = licence.contains("NoDerivatives");
                ((AssetDocument)doc).setLicenceProtected(licenceProtected);
                ((AssetDocument)doc).setDuplicatable((context.getUser().equals(doc.getCreator()))||(!licenceProtected));
                if (doc.getWeb().startsWith("Templates_")||doc.getWeb().equals(Constants.TEMPLATES_SPACE))
                    ((AssetDocument)doc).setCurrikiTemplate(true);
            }
            BaseObject cObj = xdoc.getObject(Constants.COMPOSITEASSET_CLASS);
            if (cObj != null){
                ((AssetDocument)doc).setComposite(true);
            }
            return doc;
        } finally {
            context.setDoc(cdoc);
        }    }

    /* Lucene Searching */

    public List luceneSearch(String terms, int start, int nb) throws XWikiGWTException {
        List docs = new ArrayList();

        try {
            XWikiContext context = getXWikiContext();
            LucenePluginApi lucene = (LucenePluginApi) context.getWiki().getPluginApi("lucene", context);

            // Need to add sorting
            SearchResults search = lucene.getSearchResults(terms, "name", "default,en");

            List results = search.getResults(start, nb);

            // First element in list is the hit count
            docs.add(Integer.valueOf(search.getTotalHitcount()));

            Iterator i = results.iterator();

            while (i.hasNext()) {
                SearchResult r = (SearchResult) i.next();
                XWikiDocument xd = context.getWiki().getDocument(r.getWeb()+"."+r.getName(), context);
                Document doc = newDocument(new Document(), xd, true, true, false, false, context);

                // TODO: We really should create a sub-class of Document for this
                doc.setCreator(context.getWiki().getUserName(xd.getCreator(), null, false, context));
                BaseObject obj = xd.getObject(Constants.COMPOSITEASSET_CLASS);
                if (obj != null){
                    doc.setFormat("composite");
                } else {
                    List attachments = xd.getAttachmentList();
                    if (!attachments.isEmpty()){
                        XWikiAttachment attach = (XWikiAttachment) attachments.get(0);

                        String attName = attach.getFilename();

                        String extension = (attName.lastIndexOf(".") != -1 ? attName.substring(attName.lastIndexOf(".") + 1).toUpperCase(): null);

                        if (extension == null){
                            extension = "Unknown";
                        }

                        doc.setFormat(extension);
                    } else {
                        obj = xd.getObject(Constants.EXTERNAL_ASSET_CLASS);
                        if (obj != null){
                            doc.setFormat("WWW");
                        } else {
                            doc.setFormat("block");
                        }
                    }
                }

                docs.add(doc);
            }
        } catch (Exception e){
            throw getXWikiGWTException(e);
        }

        return docs;
    }

    public AssetItem getCollectionTreeItem(String rootAssetPage) throws XWikiGWTException {
        AssetItem root = new AssetItem(rootAssetPage, 0);

        try {
            return getCollectionTreeItem(root, getXWikiContext());
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }

    public AssetItem getCollectionTreeItem(AssetItem parent, XWikiContext context) throws XWikiGWTException {
        XWikiDocument doc;
        try {
            doc = context.getWiki().getDocument(parent.getAssetPage(), context);
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
        if (doc == null){
            //return new AssetItem("ERROR_NO_SUCH_DOCUMENT_2-"+parent.getAssetPage(), -1);
            return null;
        }

        BaseObject composite = doc.getObject(Constants.COMPOSITEASSET_CLASS);
        if (composite == null){
            // This is not a composite asset
            //return parent;
            return null;
        }

        BaseObject obj = doc.getObject(Constants.ASSET_CLASS);
        String title;
        if (obj == null){
            // The root collection does not have an ASSET_CLASS object
            if (composite.getStringValue(Constants.COMPOSITEASSET_TYPE_PROPERTY).equals(Constants.COMPOSITE_ROOT_COLLECTION)){
                XWikiMessageTool msg = (XWikiMessageTool) context.get("msg");
                title = msg.get("Root");
            } else {
                // This Composite Asset is invalid
                return null;
            }
        } else {
            // The root collection does not have an ASSET_CLASS object
            title = obj.getStringValue(Constants.ASSET_TITLE_PROPERTY);
            if (title == null || title.length() == 0) {
                title = doc.getTitle();
                if (title == null || title.length() == 0){
                    title = doc.getFullName();
                    if (title == null || title.length() == 0){
                        XWikiMessageTool msg = (XWikiMessageTool) context.get("msg");
                        title = msg.get("Untitled");
                    }
                }
            }
        }
        parent.setText(title);

        List objs_old = doc.getObjects(Constants.SUBASSET_CLASS);
        if (objs_old == null || objs_old.size() == 0){
            return parent;
        }

        List objs = new ArrayList(objs_old);
        List items = new ArrayList();
        while(objs.size() > 0) {
            BaseObject smallerObj = null;
            long smaller_pos = -100;
            Iterator it = objs.iterator();
            while(it.hasNext()) {
                BaseObject currObj = (BaseObject) it.next();
                if(currObj == null){
                    continue;
                }
                long currPos = currObj.getLongValue("order");
                if ((smaller_pos == -100) || (currPos < smaller_pos)) {
                    smaller_pos = currPos;
                    smallerObj = currObj;
                }
            }
            if (smallerObj == null){
                break;
            }
            objs.remove(smallerObj);
            String assetPage = smallerObj.getStringValue(Constants.SUBASSET_ASSETPAGE_PROPERTY);
            long index = smallerObj.getLongValue(Constants.SUBASSET_ORDER_PROPERTY);

            if (assetPage == null || assetPage.length() == 0){
                continue;
            } else if (assetPage.equals(Constants.PAGE_BREAK)){
                continue;
            } else {
                AssetItem currItem = new AssetItem(assetPage, index);
                currItem = getCollectionTreeItem(currItem, context);
                if (currItem != null){
                    items.add(currItem);
                }
            }
        }
        if (!items.isEmpty()){
            parent.setItems(items);
        }
        return parent;
    }


    // Templates
    public List getTemplates() throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();
            String hsql = "where doc.name='WebHome' and (doc.web='" + Constants.TEMPLATES_SPACE + "' or doc.web like 'Templates_%') order by doc.fullName";
            List templateList = new ArrayList();
            List collectionList = context.getWiki().getStore().searchDocumentsNames(hsql, context);
            for (int i=0;i<collectionList.size();i++) {
                String docname = (String) collectionList.get(0);
                XWikiDocument doc = context.getWiki().getDocument(docname, context);
                Vector subassets = doc.getObjects(Constants.SUBASSET_CLASS);
                if (subassets!=null) {
                        for (int j=0;j<subassets.size();j++) {
                            BaseObject subassetobj = (BaseObject) subassets.get(j);
                            if (subassetobj!=null) {
                                String templatePageName = subassetobj.getStringValue(Constants.SUBASSET_ASSETPAGE_PROPERTY);
                                XWikiDocument templateDoc = context.getWiki().getDocument(templatePageName, context);
                                String title = templateDoc.getStringValue(Constants.ASSET_CLASS, Constants.ASSET_TITLE_PROPERTY);
                                String desc = templateDoc.getStringValue(Constants.ASSET_CLASS, Constants.ASSET_DESCRIPTION_PROPERTY);
                                String imageURL = "";
                                // We look for the first attached file in the template collection
                                List attachmentList = templateDoc.getAttachmentList();
                                if ((attachmentList!=null)&&(attachmentList.size()>0)) {
                                    XWikiAttachment attachment = (XWikiAttachment) attachmentList.get(0);
                                    if (attachment!=null)
                                     imageURL = templateDoc.getAttachmentURL(attachment.getFilename(), context);
                                }
                                templateList.add(new TemplateInfo(templatePageName, title, desc, imageURL));
                            }
                        }
                }
            }
            return templateList;
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }
    
    public Document updateViditalk(String fullName, String videoId) throws XWikiGWTException {
        try {
            XWikiContext context = getXWikiContext();

            XWikiDocument doc = context.getWiki().getDocument(fullName, context);

            BaseObject assetObj = doc.getObject(Constants.ASSET_CLASS);
            BaseObject videoObj = doc.getObject(Constants.VIDITALK_CLASS);

            if (videoObj == null){
                // Create a Viditalk Asset Object
                doc.createNewObject(Constants.VIDITALK_CLASS, context);
                videoObj = doc.getObject(Constants.VIDITALK_CLASS);
            }

            videoObj.setStringValue("video_id", videoId);

            context.getWiki().saveDocument(doc, context.getMessageTool().get("curriki.comment.updatedviditalkid"), context);

            return newDocument(new Document(), doc, true, false, true, false, context);
        } catch (Exception e) {
            throw getXWikiGWTException(e);
        }
    }


}
