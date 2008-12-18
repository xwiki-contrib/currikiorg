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
package org.curriki.xwiki.plugin.asset;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.curriki.xwiki.plugin.asset.attachment.*;
import org.curriki.xwiki.plugin.asset.composite.CollectionCompositeAsset;
import org.curriki.xwiki.plugin.asset.composite.FolderCompositeAsset;
import org.curriki.xwiki.plugin.asset.composite.RootCollectionCompositeAsset;
import org.curriki.xwiki.plugin.asset.external.ExternalAsset;
import org.curriki.xwiki.plugin.asset.external.VideoAsset;
import org.curriki.xwiki.plugin.asset.other.InvalidAsset;
import org.curriki.xwiki.plugin.asset.other.ProtectedAsset;
import org.curriki.xwiki.plugin.asset.text.TextAsset;
import org.curriki.xwiki.plugin.mimetype.MimeTypePlugin;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.plugin.image.ImagePlugin;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.api.Property;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.BaseStringProperty;
import com.xpn.xwiki.api.Object;

public class Asset extends CurrikiDocument {
    private static final Log LOG = LogFactory.getLog(Asset.class);

    public Asset(XWikiDocument doc, XWikiContext context) {
        super(doc, context);
    }

    public String getCategory() {
        if (hasA(Constants.ASSET_CLASS)) {
            Object obj = getObject(Constants.ASSET_CLASS);
            use(obj);
            String category = (String) getValue(Constants.ASSET_CLASS_CATEGORY);
            if (category==null)
                return Constants.ASSET_CATEGORY_UNKNOWN;
            else
                return category;
        } else {
            return Constants.ASSET_CATEGORY_UNKNOWN;
        }
    }

    public void setCategory(String category) {
        BaseObject obj = getDoc().getObject(Constants.ASSET_CLASS, true, context);
        obj.setStringValue(Constants.ASSET_CLASS_CATEGORY, category);
    }

    public void saveDocument(String comment) throws XWikiException {
        saveDocument(comment, false);
    }

    public static Asset createTempAsset(String parentAsset, XWikiContext context) throws XWikiException {
        return createTempAsset(parentAsset, null, context);
    }

    public static Asset createTempAsset(String parentAsset, String publishSpace, XWikiContext context) throws XWikiException {
        if (Constants.GUEST_USER.equals(context.getUser())) {
            throw new AssetException(AssetException.ERROR_ASSET_FORBIDDEN, "XWikiGuest cannot create assets.");
        }

        String pageName = context.getWiki().getUniquePageName(Constants.ASSET_TEMPORARY_SPACE, context);

        XWikiDocument newDoc = context.getWiki().getDocument(Constants.ASSET_TEMPORARY_SPACE, pageName, context);

        Asset assetDoc = new Asset(newDoc, context);
        assetDoc.init(parentAsset, publishSpace);
        assetDoc.saveDocument(context.getMessageTool().get("curriki.comment.createnewsourceasset"), true);

        return assetDoc;
    }

    protected void init(String parentAsset) throws XWikiException {
        init(parentAsset, null);
    }

    protected void init(String parentAsset, String publishSpace) throws XWikiException {
        assertCanEdit();
        getDoc().setCreator(context.getUser());

        inheritMetadata(parentAsset, publishSpace);

        getDoc().setCustomClass(getClass().getName());
        setDefaultContent();

        String rights = (String) getObject(Constants.ASSET_CLASS).get(Constants.ASSET_CLASS_RIGHT);

        applyRightsPolicy(rights);
    }

    protected void setDefaultContent() throws XWikiException {
        assertCanEdit();
        getDoc().setContent("");
    }

    protected void initSubType() throws XWikiException {
        assertCanEdit();
        // Empty for Superclass
    }

    public void addAttachment(InputStream iStream, String name) throws XWikiException, IOException {
        assertCanEdit();
        XWikiAttachment att = addAttachment(name, iStream);
        getDoc().saveAttachmentContent(att, context);
    }

    public String getDisplayTitle() {
        String title = getTitle();
        return (title == null || title.length() == 0) ? Constants.ASSET_DISPLAYTITLE_UNTITLED : title;
    }

    public String getDescription() {
        String className = getActiveClass();

        use(Constants.ASSET_CLASS);
        String description = (String) getValue(Constants.ASSET_CLASS_DESCRIPTION);

        if (className != null) {
            use(className);
        }

        return (description == null || description.length() == 0) ? "" : description;
    }

    public void changeOwnership(String newUser) {
        if (hasProgrammingRights()) {
            XWikiDocument assetDoc = getDoc();
            assetDoc.setCreator(newUser);
        }
    }

    /**
     * Set the rights objects based on the current right setting
     *
     * @throws XWikiException
     */
    public void applyRightsPolicy() throws XWikiException {
        applyRightsPolicy(null);
    }

    /**
     * Set the rights object based on the right in param or the current right setting if null
     *
     * @param right
     * @throws XWikiException
     */
    public void applyRightsPolicy(String right) throws XWikiException {
        XWikiDocument assetDoc = getDoc();
        assetDoc.removeObjects("XWiki.XWikiRights");

        BaseObject assetObj = assetDoc.getObject(Constants.ASSET_CLASS);
        String rights;

        if (right == null) {
            // Use existing rights value
            rights = assetObj.getStringValue(Constants.ASSET_CLASS_RIGHT);
        } else {
            rights = right;
            assetObj.setStringValue(Constants.ASSET_CLASS_RIGHT, right);
        }

        // Make sure rights value is valid, default to PUBLIC if not
        if (rights == null
            || !(rights.equals(Constants.ASSET_CLASS_RIGHT_PUBLIC)
                 || rights.equals(Constants.ASSET_CLASS_RIGHT_MEMBERS)
                 || rights.equals(Constants.ASSET_CLASS_RIGHT_PRIVATE))) {
            rights = Constants.ASSET_CLASS_RIGHT_PUBLIC;
            assetObj.setStringValue(Constants.ASSET_CLASS_RIGHT, rights);
        }

        // Rights
        BaseObject rightObj;

        // If collection is user
        String usergroupfield = Constants.RIGHTS_CLASS_USER;
        String uservalue =  ("".equals(assetDoc.getCreator())) ? context.getUser() : assetDoc.getCreator();
        String usergroupvalue = uservalue;

        // If collection is group
        if (assetDoc.getSpace().startsWith(Constants.GROUP_COLLECTION_SPACE_PREFIX)) {
            usergroupfield = Constants.RIGHTS_CLASS_GROUP;
            usergroupvalue = assetDoc.getSpace().substring(5) + ".MemberGroup";
        }

        // Always let the admin group have edit access
        rightObj = assetDoc.newObject(Constants.RIGHTS_CLASS, context);
        rightObj.setLargeStringValue(Constants.RIGHTS_CLASS_GROUP, Constants.RIGHTS_ADMIN_GROUP);
        rightObj.setStringValue("levels", "edit");
        rightObj.setIntValue("allow", 1);

        // Always let the creator/group edit
        rightObj = assetDoc.newObject(Constants.RIGHTS_CLASS, context);
        // CURRIKI-2468 - allow creator to always edit/view their creations
        if (usergroupfield.equals(Constants.RIGHTS_CLASS_GROUP)) {
            rightObj.setLargeStringValue(Constants.RIGHTS_CLASS_USER, uservalue);
        }
        rightObj.setLargeStringValue(usergroupfield, usergroupvalue);
        rightObj.setStringValue("levels", "edit");
        rightObj.setIntValue("allow", 1);

        if (rights.equals(Constants.ASSET_CLASS_RIGHT_PUBLIC)) {
            // Viewable by all and any member can edit
            rightObj = assetDoc.newObject(Constants.RIGHTS_CLASS, context);
            rightObj.setLargeStringValue(Constants.RIGHTS_CLASS_GROUP, Constants.RIGHTS_ALL_GROUP);
            rightObj.setStringValue("levels", "edit");
            rightObj.setIntValue("allow", 1);
        } else if (rights.equals(Constants.ASSET_CLASS_RIGHT_MEMBERS)) {
            // Viewable by all, only user/group can edit
        } else {
            // rights == private, so only allow creator/group to view and edit
            rightObj = assetDoc.newObject(Constants.RIGHTS_CLASS, context);
            // CURRIKI-2468 - allow creator to always edit/view their creations
            if (usergroupfield.equals(Constants.RIGHTS_CLASS_GROUP)) {
                rightObj.setLargeStringValue(Constants.RIGHTS_CLASS_USER, uservalue);
            }
            rightObj.setLargeStringValue(usergroupfield, usergroupvalue);
            rightObj.setStringValue("levels", "view");
            rightObj.setIntValue("allow", 1);

            // TODO: something here caused CURRIKI-3100 so the rest of the metadata could not get saved
            //private assets must be removed from review queue
            removeFromReviewQueue();
        }
    }

    public Class<? extends Asset> determineAssetSubtype() {
        if (hasAccessLevel("view")) {
            if (isFolder()){
                if (isRootCollection()){
                    // Note that the Root Collection doesn't have an Asset Class
                    //  so it has to come before checking if no ASSET_CLASS
                    return RootCollectionCompositeAsset.class;
                } else if (isCollection()){
                    return CollectionCompositeAsset.class;
                } else {
                    return FolderCompositeAsset.class;
                }
            }

            String category = getCategory();
            if (category == null) {
                return InvalidAsset.class;
            }

            // Check specific objects to find displayer
            if (category.equals(Constants.ASSET_CATEGORY_TEXT)) {
                return TextAsset.class;
            } else  if (category.equals(Constants.ASSET_CATEGORY_EXTERNAL)) {
                return ExternalAsset.class;
            } else  if (category.equals(Constants.ASSET_CATEGORY_IMAGE)) {
                return ImageAsset.class;
            }  else  if (category.equals(Constants.ASSET_CATEGORY_AUDIO)) {
                return AudioAsset.class;
            } else  if (category.equals(Constants.ASSET_CATEGORY_VIDEO)) {
                return VideoAsset.class;
            } else  if (category.equals(Constants.ASSET_CATEGORY_INTERACTIVE)) {
                return InteractiveAsset.class;
            } else  if (category.equals(Constants.ASSET_CATEGORY_ARCHIVE)) {
                return ArchiveAsset.class;
            } else  if (category.equals(Constants.ASSET_CATEGORY_DOCUMENT)) {
                return DocumentAsset.class;
            } else {
                // Last is just an attachment item
                if (doc.getAttachmentList().size() > 0) {
                    return DocumentAsset.class;
                }
                return Asset.class;
            }
        } else {
            return ProtectedAsset.class;
        }
    }

    public boolean isFolder() {
        if (doc.getObjectNumbers(Constants.COMPOSITE_ASSET_CLASS) == 0){
            return false;
        } else {
            // Work around a bug XWIKI-1624
            // TODO: Remove the work-around once XWIKI-1624 is fixed
            List<BaseObject> compObjs = doc.getObjects(Constants.COMPOSITE_ASSET_CLASS);
            for (BaseObject compObj : compObjs){
                if (compObj != null){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCollection() {
        if (isFolder()) {
            com.xpn.xwiki.api.Object obj = getObject(Constants.COMPOSITE_ASSET_CLASS);
            if (getValue(Constants.COMPOSITE_ASSET_CLASS_TYPE, obj).equals(Constants.COMPOSITE_ASSET_CLASS_TYPE_COLLECTION)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRootCollection() {
        if (isFolder()) {
            com.xpn.xwiki.api.Object obj = getObject(Constants.COMPOSITE_ASSET_CLASS);
            if (getValue(Constants.COMPOSITE_ASSET_CLASS_TYPE, obj).equals(Constants.COMPOSITE_ASSET_CLASS_TYPE_ROOT_COLLECTION)) {
                return true;
            }
        }
        return false;
    }


    static public Asset fetchAsset(String assetName, XWikiContext context) throws XWikiException {
        com.xpn.xwiki.api.XWiki xwikiApi = new com.xpn.xwiki.api.XWiki(context.getWiki(), context);
        Document doc = xwikiApi.getDocument(assetName);

        if (doc instanceof Asset){
            return ((Asset) doc).as(null);
        } else {
            throw new AssetException(AssetException.ERROR_ASSET_NOT_FOUND, "Asset "+assetName+" could not be found");
        }
    }

    static public Asset fetchAsset(String web, String page, XWikiContext context) throws XWikiException {
        return Asset.fetchAsset(web+"."+page, context);
    }

    public List<Property> getMetadata() {
        List<Property> md = new ArrayList<Property>();

        com.xpn.xwiki.api.Object assetObj = getObject(Constants.ASSET_CLASS);
        for (java.lang.Object prop : assetObj.getPropertyNames()) {
            LOG.debug("Adding "+prop+" to metadata list");
            md.add(assetObj.getProperty((String) prop));
        }

        com.xpn.xwiki.api.Object licenseObj = getObject(Constants.ASSET_LICENCE_CLASS);
        for (java.lang.Object prop : licenseObj.getPropertyNames()) {
            LOG.debug("Adding "+prop+" to metadata list");
            md.add(licenseObj.getProperty((String) prop));
        }

        // Add assetType to metadata
        Class assetType = determineAssetSubtype();
        String fullAssetType = assetType.getCanonicalName();
        BaseStringProperty baseProp = new BaseStringProperty();
        baseProp.setName("fullAssetType");
        baseProp.setValue(fullAssetType);
        Property prop = new Property(baseProp, context);
        md.add(prop);

        // And add shortAssetType to metadata
        String shortAssetType = assetType.getSimpleName();
        if (!shortAssetType.equals("Asset")) {
            shortAssetType = shortAssetType.replaceAll("Asset$", "");
        }
        baseProp = new BaseStringProperty();
        baseProp.setName("assetType");
        baseProp.setValue(shortAssetType);
        prop = new Property(baseProp, context);
        md.add(prop);

        return md;
    }

    public <A extends Asset> A as(Class<? extends Asset> wantedClass) throws XWikiException {
        return (A) as(wantedClass, false);
    }

    public <A extends Asset> A subclassAs(Class<? extends Asset> wantedClass) throws XWikiException {
        return (A) this.as(wantedClass, true);
    }

    public <A extends Asset> A as(Class<? extends Asset> wantedClass, boolean moreSpecific) throws XWikiException {
        // If this is already a subtype of the wanted class then just return this
        if (wantedClass != null && wantedClass.isAssignableFrom(this.getClass())) {
            return (A) this;
        }

        Class returnClass;

        // Make sure the determined type is a subclass of the wanted type
        Class<? extends Asset> assetType = determineAssetSubtype();
        if (!moreSpecific) {
            if (wantedClass == null) {
                // Return as whatever subtype it is
                returnClass = assetType;
            } else if (wantedClass.isAssignableFrom(assetType)) {
                // Return as the desired type
                returnClass = wantedClass;
            } else {
                throw new AssetException(AssetException.ERROR_ASSET_INCOMPATIBLE, "Document of type "+assetType+" cannot become "+wantedClass);
            }
        } else {
            // Subclass an Asset
            if (assetType.equals(wantedClass)) {
                // Already is that type, so don't initialize
                moreSpecific = false;
                returnClass = wantedClass;
            } else if (assetType.equals(Asset.class) && assetType.isAssignableFrom(wantedClass)) {
                // Turn into a specified subtype of Asset (initializing the subtype)
                returnClass = wantedClass;
            } else {
                throw new AssetException(AssetException.ERROR_ASSET_INCOMPATIBLE, "Document of type "+assetType+" cannot become "+wantedClass);
            }
        }

        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = XWikiDocument.class;
        parameterTypes[1] = XWikiContext.class;

        java.lang.Object[] initargs = new java.lang.Object[2];
        initargs[0] = doc;
        initargs[1] = context;

        A subtyped;
        try {
            Constructor<A> constructor = returnClass.getConstructor(parameterTypes);
            subtyped = constructor.newInstance(initargs);
            if (moreSpecific) {
                subtyped.initSubType();
            }
        } catch (Exception e) {
            throw new AssetException(AssetException.ERROR_ASSET_INCOMPATIBLE, "Document of type "+assetType+" cannot become "+wantedClass, e);
        }

        return subtyped;
    }

    public void inheritMetadata() throws XWikiException {
        inheritMetadata(null, null);
    }

    public void inheritMetadata(String parentAsset) throws XWikiException {
        inheritMetadata(parentAsset, null);
    }

    public void inheritMetadata(String parentAsset, String publishSpace) throws XWikiException {
        assertCanEdit();
        XWikiDocument assetDoc = getDoc();

        BaseObject assetObj = assetDoc.getObject(Constants.ASSET_CLASS);
        if (assetObj == null) {
            assetObj = assetDoc.getObject(Constants.ASSET_CLASS, true, context);
        }

        // CURRIKI-2451 - Make sure group rights are used by default
        if (publishSpace != null && publishSpace.startsWith(Constants.GROUP_COLLECTION_SPACE_PREFIX)) {
            String groupSpace = publishSpace.replaceFirst("^"+Constants.GROUP_COLLECTION_SPACE_PREFIX, Constants.GROUP_SPACE_PREFIX);
            String rights = Constants.ASSET_CLASS_RIGHT_PUBLIC;

            // TODO: This should probably be using the SpaceManager extension
            XWikiDocument groupSpaceDoc = context.getWiki().getDocument(groupSpace+"."+Constants.GROUP_RIGHTS_PAGE, context);
            if (groupSpaceDoc != null){
                // Note that the values for the group access defaults
                //  DO NOT MATCH the values that need to be applied to the collection
                BaseObject rObj = groupSpaceDoc.getObject(Constants.GROUP_RIGHTS_CLASS);
                if (rObj != null){
                    String groupDefaultPrivs = rObj.getStringValue(Constants.GROUP_RIGHTS_PROPERTY);
                    if (groupDefaultPrivs.equals(Constants.GROUP_RIGHT_PRIVATE)){
                        rights = Constants.ASSET_CLASS_RIGHT_PRIVATE;
                    } else if (groupDefaultPrivs.equals(Constants.GROUP_RIGHT_PROTECTED)){
                        rights = Constants.ASSET_CLASS_RIGHT_MEMBERS;
                    } else if (groupDefaultPrivs.equals(Constants.GROUP_RIGHT_PUBLIC)){
                        rights = Constants.ASSET_CLASS_RIGHT_PUBLIC;
                    }
                }
            }

            assetObj.setStringValue(Constants.ASSET_CLASS_RIGHT, rights);
        }

        XWikiDocument parentDoc = null;
        if (parentAsset != null && parentAsset.length() > 0) {
            parentDoc = context.getWiki().getDocument(parentAsset, context);
            if (parentDoc.isNew()) {
                throw new AssetException(AssetException.MODULE_PLUGIN_ASSET, AssetException.ERROR_ASSET_NOT_FOUND, "Parent asset not found");
            }
        }
        // the Root collection does not have a proper asset class so we can't inherit from it
        if (parentDoc != null && parentDoc.getObject(Constants.ASSET_CLASS) != null && !parentDoc.getName().equals(Constants.ROOT_COLLECTION_PAGE)) {
            BaseObject parentAssetObj = (BaseObject) parentDoc.getObject(Constants.ASSET_CLASS).clone();

            copyProperty(parentAssetObj, assetObj, Constants.ASSET_CLASS_EDUCATIONAL_LEVEL);
            copyProperty(parentAssetObj, assetObj, Constants.ASSET_CLASS_FRAMEWORK_ITEMS);
            copyProperty(parentAssetObj, assetObj, Constants.ASSET_CLASS_RIGHT);
            copyProperty(parentAssetObj, assetObj, Constants.ASSET_CLASS_KEYWORDS);
            copyProperty(parentAssetObj, assetObj, Constants.ASSET_CLASS_LANGUAGE);
        }

        // make sure default rights value is not empty (default to public)
        String rights = assetObj.getStringValue(Constants.ASSET_CLASS_RIGHT);
        if ((rights==null)||(rights.equals(""))) {
            assetObj.setStringValue(Constants.ASSET_CLASS_RIGHT, Constants.ASSET_CLASS_RIGHT_PUBLIC);
        }

        BaseObject newLicenceObj = doc.getObject(Constants.ASSET_LICENCE_CLASS);
        if (newLicenceObj == null) {
            newLicenceObj = doc.newObject(Constants.ASSET_LICENCE_CLASS, context);
        }
        // the Root collection does not have an asset Licence class
        if (parentDoc != null && parentDoc.getObject(Constants.ASSET_LICENCE_CLASS) != null) {
            BaseObject parentLicenceObjAsset = parentDoc.getObject(Constants.ASSET_LICENCE_CLASS);
            copyProperty(parentLicenceObjAsset, newLicenceObj, Constants.ASSET_LICENCE_ITEM_LICENCE_TYPE);
        } else {
            newLicenceObj.setStringValue(Constants.ASSET_LICENCE_ITEM_LICENCE_TYPE, Constants.ASSET_LICENCE_ITEM_LICENCE_TYPE_DEFAULT);
        }

        // Rights holder should be by default the pretty name of the user
        newLicenceObj.setStringValue(Constants.ASSET_LICENCE_ITEM_RIGHTS_HOLDER, context.getWiki().getLocalUserName(context.getUser(), null, false, context));
    }

    public FolderCompositeAsset makeFolder() throws XWikiException {
        return makeFolder(null);
    }

    public FolderCompositeAsset makeFolder(String page) throws XWikiException {
        assertCanEdit();
        FolderCompositeAsset asset = subclassAs(FolderCompositeAsset.class);

        if (page != null) {
            asset.addSubasset(page);
        }
        saveDocument(context.getMessageTool().get("curriki.comment.createfoldersourceasset"), true);
        return asset;
    }

    public CollectionCompositeAsset makeCollection() throws XWikiException {
        return makeCollection(null);
    }

    public CollectionCompositeAsset makeCollection(String page) throws XWikiException {
        assertCanEdit();
        CollectionCompositeAsset asset = subclassAs(CollectionCompositeAsset.class);

        if (page != null) {
            asset.addSubasset(page);
        }
        saveDocument(context.getMessageTool().get("curriki.comment.createfoldersourceasset"), true);
        return asset;
    }

    public ExternalAsset makeExternal(String link) throws XWikiException {
        assertCanEdit();
        ExternalAsset asset = subclassAs(ExternalAsset.class);
        asset.addLink(link);
        saveDocument(context.getMessageTool().get("curriki.comment.createlinksourceasset"), true);
        return asset;
    }

    public VideoAsset makeVIDITalk(String videoId) throws XWikiException {
        assertCanEdit();
        VideoAsset asset = subclassAs(VideoAsset.class);

        asset.addVideoId("viditalk:" + videoId);
        saveDocument(context.getMessageTool().get("curriki.comment.createviditalksourceasset"), true);
        return asset;
    }

    public TextAsset makeTextAsset(String category, String syntax, String content) throws XWikiException {
        assertCanEdit();
        TextAsset asset = subclassAs(TextAsset.class);

        asset.addText(syntax, content);
        asset.setCategory(category);
        saveDocument(context.getMessageTool().get("curriki.comment.createtextsourceasset"), true);
        return asset;
    }

    public DocumentAsset processAttachment() throws XWikiException {
        assertCanEdit();
        DocumentAsset asset = subclassAs(DocumentAsset.class);

        if (doc.getAttachmentList().size() > 0) {
            XWikiAttachment attach = doc.getAttachmentList().get(0);
            determineFileTypeAndCategory(attach);
            saveDocument(context.getMessageTool().get("curriki.comment.createtextsourceasset"), true);
        }

        return asset;
    }

    protected void determineFileTypeAndCategory(XWikiAttachment attachment) throws XWikiException {
        String filename = attachment.getFilename();
        String extension = (filename.lastIndexOf(".") != -1 ? filename.substring(filename.lastIndexOf(".") + 1).toLowerCase(): null);
        MimeTypePlugin mimePlugin = (MimeTypePlugin) context.getWiki().getPlugin(MimeTypePlugin.PLUGIN_NAME, context);
        String filetype =  mimePlugin.getFileType(extension, context);
        String category = mimePlugin.getCategory(filetype, context);
        XWikiDocument assetDoc = getDoc();

        BaseObject documentObject = assetDoc.getObject(Constants.ATTACHMENT_ASSET_CLASS, true, context);
        documentObject.setStringValue(Constants.ATTACHMENT_ASSET_FILE_TYPE, filetype);
        documentObject.setLongValue(Constants.ATTACHMENT_ASSET_FILE_SIZE, attachment.getFilesize());

        // We need to add the class for certain asset types if they do not exist yet
        if (category.equals(Constants.ASSET_CATEGORY_ARCHIVE))
                getObject(Constants.ARCHIVE_ASSET_CLASS, true);


        if (category.equals(Constants.ASSET_CATEGORY_IMAGE)) {
            getObject(Constants.IMAGE_ASSET_CLASS, true);

            ImagePlugin imgPlugin = (ImagePlugin) context.getWiki().getPlugin(ImagePlugin.PLUGIN_NAME, context);

            BaseObject imageObject = assetDoc.getObject(Constants.IMAGE_ASSET_CLASS, true, context);

            if (imgPlugin != null) {
                try {
                    int height = imgPlugin.getHeight(attachment, context);
                    int width = imgPlugin.getWidth(attachment, context);
                    imageObject.setIntValue("height", height);
                    imageObject.setIntValue("width", width);
                } catch (InterruptedException ie) {
                    // Ignore exception
                }
            }
        }


        BaseObject assetObj = assetDoc.getObject(Constants.ASSET_CLASS, true, context);
        assetObj.setStringValue(Constants.ASSET_CLASS_CATEGORY, category);
    }

    public Boolean isPublished() {
        return !getSpace().equals("AssetTemp");
    }

    public Asset publish(String space) throws XWikiException {
        return publish(space, true);
    }

    public Asset publish(String space, String name) throws XWikiException {
        return publish(space, name, true);
    }

    public Asset publish(String space, boolean checkSpace) throws XWikiException {
        String prettyName = context.getWiki().clearName(getTitle(), true, true, context);

        return publish(space, prettyName, checkSpace);
    }

    public Asset publish(String space, String name, boolean checkSpace) throws XWikiException {
        if (isPublished()) {
            throw new AssetException("This resource is already published");
        }

        assertCanEdit();

        XWikiDocument assetDoc = getDoc();
        // Be sure we have a category first -- attachments can't get them automatically yet
        BaseObject obj = assetDoc.getObject(Constants.ASSET_CLASS);
        if (obj != null) {
            String category = obj.getStringValue(Constants.ASSET_CLASS_CATEGORY);
            if (category == null || category.length() == 0 || category.equals(Constants.ASSET_CATEGORY_UNKNOWN)) {
                processAttachment();
            }
        }

        if (!space.startsWith(Constants.COLLECTION_PREFIX)) {
            throw new AssetException("You cannot publish to the space "+space);
        }

        if (!validate()) {
            throw new AssetException("Validation failed.");
        }

        if (checkSpace) {
            CollectionSpace.ensureExists(space, context);
        }

        // Let's choose a nice name for the page
        String prettyName = context.getWiki().clearName(name, true, true, context);
        assetDoc.rename(space + "." + context.getWiki().getUniquePageName(space, prettyName.trim(), context), new ArrayList<String>(), context);

        applyRightsPolicy();

        List<String> params = new ArrayList<String>();
        params.add(assetDoc.getStringValue(Constants.ASSET_CLASS_CATEGORY));
        save(context.getMessageTool().get("curriki.comment.finishcreatingsubasset", params));

        if (isCollection()) {
            RootCollectionCompositeAsset root = CollectionSpace.getRootCollection(space, context);
            root.addSubasset(this.getFullName());
            root.saveDocument(context.getMessageTool().get("curriki.comment.addtocollectionasset"));
        }

        return this;
    }

    public boolean validate() throws XWikiException {
        // Has the asset been subtyped ?
        if (determineAssetSubtype().equals(Asset.class)) {
            throw new AssetException("This asset is not complete.");
        }

        // Other validation

        // Super's validate
        return super.validate();
    }

	/**
	 *
	 * @param appropriatePedagogy
	 * @param contentAccuracy
	 * @param technicalCompletness
	 * @return the calculated rating
	 * @throws XWikiException
	 */

    public String calculateRating(String appropriatePedagogy, String contentAccuracy,String technicalCompletness)throws XWikiException{

    	//validate that a value was selected in all categories
    	if(appropriatePedagogy==null || "".equals(appropriatePedagogy)  ||
    			contentAccuracy==null || "".equals(contentAccuracy) ||
    			technicalCompletness==null || "".equals(technicalCompletness)){
    		throw new AssetException(context.getMessageTool().get("curriki.crs.review.mustSelectAValueInAllCategories"));
    	}
    	//validate that only appropriatePedagogy is not rated
    	//or all criteria are not rated
    	if (!appropriatePedagogy.equals("0") && (contentAccuracy.equals("0") || technicalCompletness.equals("0"))) {
    		throw new AssetException(context.getMessageTool().get("curriki.crs.review.notValidNotRatedCategorySelection"));
    	}
    	if (appropriatePedagogy.equals("0") &&
    			((contentAccuracy.equals("0") && !technicalCompletness.equals("0"))||(!contentAccuracy.equals("0") && technicalCompletness.equals("0")))) {
    		throw new AssetException(context.getMessageTool().get("curriki.crs.review.notValidNotRatedCategorySelection"));
    	}

    	int weightAppropriatePedagogy=2;
    	int weightContentAccuracy=2;
    	int weightTechnicalCompletness=1;

    	int valueAppropriatePedagogy=Integer.parseInt(appropriatePedagogy);
    	int valueContentAccuracy=Integer.parseInt(contentAccuracy);
    	int valueTechnicalCompletness=Integer.parseInt(technicalCompletness);

    	//weights are zero if the criteria value is zero
    	weightAppropriatePedagogy=valueAppropriatePedagogy==0?0:weightAppropriatePedagogy;
    	weightContentAccuracy=valueContentAccuracy==0?0:weightContentAccuracy;
    	weightTechnicalCompletness=valueTechnicalCompletness==0?0:weightTechnicalCompletness;

    	float numerator = weightAppropriatePedagogy*valueAppropriatePedagogy+weightContentAccuracy*valueContentAccuracy+weightTechnicalCompletness*valueTechnicalCompletness;
    	float denominator= weightAppropriatePedagogy+weightContentAccuracy+weightTechnicalCompletness;


    	int rating;
    	if(weightAppropriatePedagogy==0 && weightContentAccuracy==0 && weightTechnicalCompletness==0){
    		rating=0;
    	}else{
    		if(weightAppropriatePedagogy!=0){
	    			rating=Math.round(numerator/denominator);
	    		}else{
	    			rating=(int)Math.floor(numerator/denominator);
	    		}
    		}


    	//this is a special case, if pedagogy=N/R, CAccuracy=3 and TCompleteness=2
    	// return 3
    	if(weightAppropriatePedagogy==0 && valueContentAccuracy==3 && valueTechnicalCompletness==2){
    		rating=3;
    	}

    	return String.valueOf(rating);
    }

    /**
     * get the numbers of comments, the reviews are counted as a comment
     * @return comment numbers
     */
    public Integer getCommentNumbers()
    {
    	return getComments().size() + getObjectNumbers(Constants.ASSET_CURRIKI_REVIEW_CLASS);
    }


    public boolean canBeNominatedOrReviewed(){
    	use(Constants.ASSET_CLASS);
        String rights = (String)getValue(Constants.ASSET_CLASS_RIGHT);

        //false if access privilege is PRIVATE
        if (rights!=null && rights.equals(Constants.ASSET_CLASS_RIGHT_PRIVATE)) {
         return false;
        }
        //false if CRS.CurrikiReviewStatusClass.status == P (Partner)
    	use(Constants.ASSET_CURRIKI_REVIEW_STATUS_CLASS);
        String status = (String)getValue(Constants.ASSET_CURRIKI_REVIEW_STATUS_CLASS_STATUS);

        if (status!=null && status.equals(Constants.ASSET_CURRIKI_REVIEW_STATUS_CLASS_STATUS_PARTNER)) {
            return false;
        }

        //false if BFCS is SPECIALCHECKREQUIRED,IMPROVEMENTREQUIRED or DELETEDFROMMEMBERACCESS
        use(Constants.ASSET_CLASS);
        String fcStatus = (String)getValue(Constants.ASSET_BFCS_STATUS);
        if(fcStatus!=null && !fcStatus.equals("")){
        	if(fcStatus.equals(Constants.ASSET_BFCS_STATUS_SPECIALCHECKREQUIRED) ||
        		fcStatus.equals(Constants.ASSET_BFCS_STATUS_IMPROVEMENTREQUIRED) ||
        		fcStatus.equals(Constants.ASSET_BFCS_STATUS_DELETEDFROMMEMBERACCESS)){
        		return false;
        	}
        }

    	return true;
    }

    /**
     * If BFCS other than â€œOKâ€� was applied or the resource is set to Private,
     * the resource should be removed from the Review Queue and no longer show Nominate or Review links.
     */
    public void checkReviewQueue(){
    	use(Constants.ASSET_CLASS);
        String fcStatus = (String)getValue(Constants.ASSET_BFCS_STATUS);
        if(fcStatus!=null && !fcStatus.equals("")){
        	if(fcStatus.equals(Constants.ASSET_BFCS_STATUS_SPECIALCHECKREQUIRED) ||
        		fcStatus.equals(Constants.ASSET_BFCS_STATUS_IMPROVEMENTREQUIRED) ||
        		fcStatus.equals(Constants.ASSET_BFCS_STATUS_DELETEDFROMMEMBERACCESS)){
        		removeFromReviewQueue();
        		return;
        	}
        }

        String rights = (String)getValue(Constants.ASSET_CLASS_RIGHT);

        //false if access privilege is PRIVATE
        if (rights!=null && rights.equals(Constants.ASSET_CLASS_RIGHT_PRIVATE)) {
        	removeFromReviewQueue();
        }

    }

    /**
     * Remove the asset from the review queue
     */
    public void removeFromReviewQueue(){
        // TODO:  Something here caused CURRIKI-3100 making the rest of the metadata not get saved
        //        Could be the use of "use" as that is not being used elsewhere here
        use(Constants.ASSET_CURRIKI_REVIEW_CLASS);
        Integer reviewpending = (Integer)getValue("reviewpending");
		if(reviewpending!=null && reviewpending.equals(1)){
			set("reviewpending", "0");
		}

    }

    /**
     * Nominate a resource for review
     * @param comments
     * @return
     * @throws XWikiException
     */
    public Asset nominate(String comments) throws XWikiException {
        XWikiDocument assetDoc = getDoc();
        BaseObject obj = assetDoc.getObject(Constants.ASSET_CURRIKI_REVIEW_STATUS_CLASS);

        String suser =  context.getUser();
        if (obj==null) {
            obj = assetDoc.newObject(Constants.ASSET_CURRIKI_REVIEW_STATUS_CLASS, context);
            obj.setIntValue("number",0);
        }
        obj.setStringValue("nomination_user", suser);
        obj.setDateValue("nomination_date", new Date());
        obj.setLargeStringValue("nomination_comment", comments);

        obj.setIntValue("reviewpending", 1);

        saveWithProgrammingRights("save CRS nomination");

    	return this;
    }


   /** 
    * Determines if an asset is in the latest format 
    * @return boolean true if latest format
    */
   public boolean isLatestVersion() throws XWikiException {     
     if (getObject(Constants.OLD_ASSET_CLASS)!=null)
      return false;

     return true;
   }


    private void updateObject(Object newAssetObject, Object oldAssetObject, String propname) {
        updateObject(newAssetObject, oldAssetObject, propname, propname);
    }

    private void updateObject(Object newAssetObject, Object oldAssetObject, String newpropname, String oldpropname) {
        if (LOG.isDebugEnabled())
         LOG.debug("CURRIKI CONVERTER: updating property " + newpropname);

        use(oldAssetObject);
        java.lang.Object value = getValue(oldpropname);
        use(newAssetObject);
        set(newpropname, value);

        if (LOG.isDebugEnabled())
         LOG.debug("CURRIKI CONVERTER: updated property value is " + newAssetObject.get(newpropname));

    }

    /**
     * Check if the asset in old format is a video
     * @return true if video
     */
    private boolean isOldAssetVideo() {
       if (getObject(Constants.OLD_VIDITALK_CLASS)!=null)
            return true;
       else
            return false;
    }

    private void setNewCategoryAndClass() throws XWikiException {


        if (LOG.isDebugEnabled())
         LOG.debug("CURRIKI CONVERTER: running setNewCategoryAndClass");

        // Get Asset objects
        Object oldAssetObject = getObject(Constants.OLD_ASSET_CLASS);

        // get old category
        use(oldAssetObject);
        String oldCategory = (String) getValue(Constants.ASSET_CLASS_CATEGORY);
        String newCategory = "";

        if (oldCategory==null)
           oldCategory = "";
        
        // transforming category of type text
        if (oldCategory.equals(Constants.OLD_CATEGORY_COLLECTION)||(getObject(Constants.OLD_COMPOSITE_ASSET_CLASS)!=null)) {

            newCategory = Constants.ASSET_CATEGORY_COLLECTION;
            // we don't need the composite asset object
            Object oldCompositeAssetObject = getObject(Constants.OLD_COMPOSITE_ASSET_CLASS);
            Object newCompositeAssetObject = getObject(Constants.COMPOSITE_ASSET_CLASS, true);

            if (oldCompositeAssetObject!=null) {
                removeObject(oldCompositeAssetObject);
                updateObject(newCompositeAssetObject, oldCompositeAssetObject, Constants.COMPOSITE_ASSET_CLASS_TYPE);
                use(newCompositeAssetObject);
                if (Constants.OLD_COMPOSITE_ASSET_CLASS_TYPE_SUBFOLDER.equals(getValue(Constants.COMPOSITE_ASSET_CLASS_TYPE)))
                    set(Constants.COMPOSITE_ASSET_CLASS_TYPE, Constants.COMPOSITE_ASSET_CLASS_TYPE_SUBFOLDER);
            } else {
                if (LOG.isErrorEnabled())
                    LOG.error("CURRIKI ASSET CONVERTER ERROR: asset declared collection has no composite class for asset " + getFullName());
            }

            // convert sub-assets
            List subassets = getObjects(Constants.OLD_SUBASSET_CLASS);
            if ((subassets!=null) && (subassets.size()>0)) {
                for (int i=0; i<subassets.size();i++) {
                    Object oldSubasset = (Object) subassets.get(i);
                    Object newSubasset = newObject(Constants.SUBASSET_CLASS);
                    updateObject(newSubasset, oldSubasset, Constants.SUBASSET_CLASS_ORDER);
                    updateObject(newSubasset, oldSubasset, Constants.SUBASSET_CLASS_PAGE);
                }
                removeObjects(Constants.OLD_SUBASSET_CLASS);
            }

            // convert reorder info
            List reorderedlist = getObjects(Constants.OLD_COLLECTION_REORDERED_CLASS);
            if ((reorderedlist!=null) && (reorderedlist.size()>0)) {
                for (int i=0; i<reorderedlist.size();i++) {
                    Object oldReorderdObject = (Object) reorderedlist.get(i);
                    Object newReorderdObject = newObject(Constants.COLLECTION_REORDERED_CLASS);
                    updateObject(newReorderdObject, oldReorderdObject, Constants.COLLECTION_REORDERED_CLASS_REORDERD);
                }
                removeObjects(Constants.OLD_COLLECTION_REORDERED_CLASS);
            }

        } else if (oldCategory.equals(Constants.OLD_CATEGORY_TEXT)||(getObject(Constants.OLD_TEXT_ASSET_CLASS)!=null)) {
            Object newTextAssetObject = getObject(Constants.TEXT_ASSET_CLASS, true);
            set(Constants.TEXT_ASSET_SYNTAX, Constants.TEXT_ASSET_SYNTAX_TEXT);
            Object oldTextAssetObject = getObject(Constants.OLD_TEXT_ASSET_CLASS);
            if (oldTextAssetObject!=null) {
                newCategory = Constants.ASSET_CATEGORY_TEXT;
                use(oldTextAssetObject);
                Long type = (Long) getValue(Constants.OLD_TEXT_ASSET_CLASS_TYPE);
                use(oldTextAssetObject);
                String content = (String) getValue(Constants.OLD_TEXT_ASSET_CLASS_TEXT);
                if (content==null)
                 setContent("");
                else
                 setContent(content);
                use(newTextAssetObject);

                if ((type==null)||type==0) {
                    set(Constants.TEXT_ASSET_SYNTAX, Constants.TEXT_ASSET_SYNTAX_XWIKI1);
                } else if (type==1) {
                    set(Constants.TEXT_ASSET_SYNTAX, Constants.TEXT_ASSET_SYNTAX_XHTML1);
                } else if (type==2) {
                    set(Constants.TEXT_ASSET_SYNTAX, Constants.TEXT_ASSET_SYNTAX_CBOE);
                } else {
                    set(Constants.TEXT_ASSET_SYNTAX, Constants.TEXT_ASSET_SYNTAX_TEXT);
                }
                removeObject(oldTextAssetObject);
            }
        } else if (oldCategory.equals(Constants.OLD_CATEGORY_ARCHIVE)||(getObject(Constants.OLD_MIMETYPE_ARCHIVE_CLASS)!=null)) {
            newCategory = Constants.ASSET_CATEGORY_ARCHIVE;
            getObject(Constants.ATTACHMENT_ASSET_CLASS, true);
            Object oldArchiveAssetObject = getObject(Constants.OLD_MIMETYPE_ARCHIVE_CLASS);
            Object newArchiveAssetObject = getObject(Constants.ARCHIVE_ASSET_CLASS, true);
            updateObject(newArchiveAssetObject, oldArchiveAssetObject, Constants.ARCHIVE_ASSET_START_FILE, Constants.OLD_MIMETYPE_ARCHIVE_CLASS_DEFAULT_FILE);
            // we need to remove the old archive object
            removeObject(oldArchiveAssetObject);
        } else if (oldCategory.equals(Constants.OLD_CATEGORY_ANIMATION)) {
            newCategory = Constants.ASSET_CATEGORY_INTERACTIVE;
            getObject(Constants.ATTACHMENT_ASSET_CLASS, true);
        } else if (oldCategory.equals(Constants.OLD_CATEGORY_IMAGE)) {
            newCategory = Constants.ASSET_CATEGORY_IMAGE;
            getObject(Constants.ATTACHMENT_ASSET_CLASS, true);
            Object oldImageAssetObject = getObject(Constants.OLD_MIMETYPE_PICTURE_CLASS);
            Object newImageAssetObject = getObject(Constants.IMAGE_ASSET_CLASS, true);
            if (oldImageAssetObject!=null) {
                updateObject(newImageAssetObject, oldImageAssetObject, Constants.IMAGE_ASSET_WIDTH);
                updateObject(newImageAssetObject, oldImageAssetObject, Constants.IMAGE_ASSET_HEIGHT);
            }
            // we need to remove the old image object

            removeObject(oldImageAssetObject);
        } else if (getObject(Constants.OLD_VIDITALK_CLASS)!=null) {
            newCategory = Constants.ASSET_CATEGORY_VIDEO;
            getObject(Constants.ATTACHMENT_ASSET_CLASS, true);

            // transfer video id
            Object newVideoAssetObject = getObject(Constants.VIDEO_ASSET_CLASS, true);
            Object oldVideoAssetObject = getObject(Constants.OLD_VIDITALK_CLASS);
            if (oldVideoAssetObject!=null) {
                use(oldVideoAssetObject);
                String videoId = (String) getValue(Constants.OLD_VIDITALK_CLASS_VIDEO_ID);
                use(newVideoAssetObject);
                if (videoId!=null) {
                    set(Constants.VIDEO_ASSET_ID, videoId);
                    set(Constants.VIDEO_ASSET_PARTNER, Constants.VIDEO_ASSET_PARTNER_VIDITALK);
                }          
                removeObject(oldVideoAssetObject);
            }
        } else if (oldCategory.equals(Constants.OLD_CATEGORY_AUDIO)) {
            // we cannot determine type between audio and video
            newCategory = null;
            getObject(Constants.ATTACHMENT_ASSET_CLASS, true);
        } else if (oldCategory.equals(Constants.OLD_CATEGORY_LINK)||(getObject(Constants.OLD_EXTERNAL_ASSET_CLASS)!=null)) {
            newCategory = Constants.ASSET_CATEGORY_EXTERNAL;
            Object newExternalAssetObject = getObject(Constants.EXTERNAL_ASSET_CLASS, true);
            Object oldExternalAssetObject = getObject(Constants.OLD_EXTERNAL_ASSET_CLASS);
            if (oldExternalAssetObject!=null) {
                updateObject(newExternalAssetObject, oldExternalAssetObject, Constants.EXTERNAL_ASSET_LINK, Constants.OLD_EXTERNAL_ASSET_LINK);
                removeObject(oldExternalAssetObject);
            }
        } else if (oldCategory.equals(Constants.OLD_CATEGORY_DOCUMENT)) {
            newCategory = Constants.ASSET_CATEGORY_DOCUMENT;
            getObject(Constants.ATTACHMENT_ASSET_CLASS, true);
        }

        // update document (attachment) assets.
        // We need to do this either because the document use to be declared as a Document
        // We also need to do this if the document is not a text asset and there is an attachment
        Object newDocumentAssetObject = getObject(Constants.ATTACHMENT_ASSET_CLASS);
        if ((!Constants.ASSET_CATEGORY_TEXT.equals(newCategory)
                && (getAttachmentList().size() > 0)) || (newDocumentAssetObject!=null))
        {
            updateObject(newDocumentAssetObject, oldAssetObject, Constants.ATTACHMENT_ASSET_ALT_TEXT, Constants.OLD_ASSET_CLASS_ALT_TEXT);
            updateObject(newDocumentAssetObject, oldAssetObject, Constants.ATTACHMENT_ASSET_CAPTION_TEXT, Constants.OLD_ASSET_CLASS_CAPTION_TEXT);
            XWikiAttachment attachment = (doc.getAttachmentList().size()>0) ? doc.getAttachmentList().get(0) : null;
            if (attachment!=null)
             determineFileTypeAndCategory(attachment);
            else {
                if (LOG.isErrorEnabled())
                   LOG.error("CURRIKI ASSET CONVERTER ERROR: attachment missing in document asset " + getFullName());
            }

            // temporary log for debugging
            if (LOG.isInfoEnabled()) {
                LOG.info("CURRIKI ASSET CONVERTER: detected category is " + getCategory());
            }

            // we should have found the same category through conversion
            if (newCategory!=null) {
                if (!newCategory.equals(getCategory())) {
                    if (LOG.isErrorEnabled())
                        LOG.error("CURRIKI ASSET CONVERTER ERROR: newCategory " + getCategory() + " different than converted category " + newCategory + " for asset " + getFullName());
                }

                // let's fallback to the new Category
                if (getCategory()==Constants.ASSET_CATEGORY_UNKNOWN)
                     setCategory(newCategory);                
            }


            // make sure we don't have this object anymore
            Object oldArchiveAssetObject = getObject(Constants.OLD_MIMETYPE_ARCHIVE_CLASS);
            if (oldArchiveAssetObject!=null)
             removeObject(oldArchiveAssetObject);
        } else {
            // for text, collection, external, collection assets
            setCategory(newCategory);
        }
    }

    /**
     * Convert an asset from the old format to the new format
     *
     */
    public boolean convert() throws Exception {
        if (!hasProgrammingRights()) {
            java.lang.Object[] args = { getFullName() };
            throw new XWikiException(XWikiException.MODULE_XWIKI_ACCESS, XWikiException.ERROR_XWIKI_ACCESS_DENIED,
                    "Access denied with no programming rights document {0}", null, args);
        }
        
        if (convertWithoutSave()) {
            try {
                // Saving as a minor edit
                // We bypass the public api so that the author is not updated
                // We reset setContentDirty so that the content date does not change
                getDoc().setContentDirty(false);
                context.getWiki().saveDocument(getDoc(), context.getMessageTool().get("curriki.comment.datamodelmigration"), true, context);
                return true;
            } catch (Exception e) {
                if (LOG.isErrorEnabled())
                    LOG.error("CURRIKI ASSET CONVERTER ERROR: error saving converted asset " + getFullName(), e);
                throw e;
            }
        }

        return false;
    }

    /**
     * Hacked function needed by the Curriki Plugin
     */
    protected void setAlreadyCloned() {
        cloned = true;
    }
    
    /**
     * Convert an asset from the old format to the new format
     *
     */
    public boolean convertWithoutSave() throws Exception {
        if (isLatestVersion())
            return false;

        try {
            // Convert Asset Class
            // we need to make sure we are working on the modified object
            // because getObject(classname, boolean) does not do the job right
            getDoc();

            Object oldAssetObject = getObject(Constants.OLD_ASSET_CLASS);
            Object newAssetObject = getObject(Constants.ASSET_CLASS, true);

            if (LOG.isDebugEnabled())
             LOG.debug("CURRIKI CONVERTER: running convert on asset " + getFullName());

            // set title
            use(oldAssetObject);
            String title = (String) getValue(Constants.OLD_ASSET_CLASS_TITLE);
            if (title!=null)
               setTitle(title);
            setContent("");

            // transfer unchanged fields
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_DESCRIPTION);
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_FRAMEWORK_ITEMS);
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_HIDDEN_FROM_SEARCH);
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_KEYWORDS);
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_LANGUAGE);
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_RIGHT);

            // transfer tracking field
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_TRACKING);
            // transfer file check fields
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_FCSTATUS);
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_FCREVIEWER);
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_FCDATE);
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_FCNOTES);

            // transfer changed fields
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_INSTRUCTIONAL_COMPONENT, Constants.OLD_ASSET_CLASS_INSTRUCTIONAL_COMPONENT);
            updateObject(newAssetObject, oldAssetObject, Constants.ASSET_CLASS_EDUCATIONAL_LEVEL, Constants.OLD_ASSET_CLASS_EDUCATIONAL_LEVEL);

            // create new category field
            setNewCategoryAndClass();

            // remove old asset object
            removeObject(oldAssetObject);

            // Convert License Class
            Object oldLicenseObject = getObject(Constants.OLD_ASSET_LICENCE_CLASS);
            Object newLicenseObject = getObject(Constants.ASSET_LICENCE_CLASS, true);
            if (oldLicenseObject!=null) {
                updateObject(newLicenseObject, oldLicenseObject, Constants.ASSET_LICENCE_ITEM_LICENCE_TYPE, Constants.OLD_ASSET_LICENCE_ITEM_LICENCE_TYPE);
                updateObject(newLicenseObject, oldLicenseObject, Constants.ASSET_LICENCE_ITEM_RIGHTS_HOLDER);
                updateObject(newLicenseObject, oldLicenseObject, Constants.ASSET_LICENCE_ITEM_EXTERNAL_RIGHTS_HOLDER);
                updateObject(newLicenseObject, oldLicenseObject, Constants.ASSET_LICENCE_ITEM_EXPIRY_DATE);

                // remove old asset license object
                removeObject(oldLicenseObject);
            }

            return true;
        } catch (Exception e) {
            if (LOG.isErrorEnabled())
                LOG.error("CURRIKI ASSET CONVERTER ERROR: error converting asset " + getFullName(), e);
            throw new XWikiException(XWikiException.MODULE_XWIKI_PLUGINS, XWikiException.ERROR_XWIKI_UNKNOWN, "Curriki asset conversion exception", e);
        }
    }

}
