package org.curriki.xwiki.plugin.asset;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.doc.XWikiDocument;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import org.curriki.xwiki.plugin.asset.composite.CollectionCompositeAsset;
import org.curriki.xwiki.plugin.asset.composite.RootCollectionCompositeAsset;

/**
 */
public class CollectionSpace {
    private String spaceName;
    private XWikiContext context;

    public CollectionSpace(String spaceName, XWikiContext context) throws AssetException {
        if (!spaceName.startsWith(Constants.COLLECTION_PREFIX)) {
            throw new AssetException("Space is not a collection space");
        }

        this.spaceName = spaceName;
        this.context = context;
    }

    public void ensureExists() throws XWikiException {
        if (!isPreferencesPageExists()) {
            protectSpace();
        }

        if (!isRootCollectionExists()) {
            createRootCollection();
        }

        if (isUserSpace() && !isFavoritesCollectionExists()) {
            createFavoritesCollection();
        }

    }

    static public void ensureExists(String space, XWikiContext context) throws XWikiException {
        CollectionSpace cSpace = new CollectionSpace(space, context);
        cSpace.ensureExists();
    }

    public boolean isExists(){
        return isPreferencesPageExists()
               && isRootCollectionExists()
               && (isGroupSpace() || isFavoritesCollectionExists());
    }

    static public boolean isExists(String space, XWikiContext context) {
        CollectionSpace cSpace;
        try {
            cSpace = new CollectionSpace(space, context);
        } catch (AssetException e) {
            return false;
        }
        return cSpace.isExists();
    }

    public boolean isPreferencesPageExists() {
        String prefsPage = spaceName+".WebPreferences";
        if (!context.getWiki().exists(prefsPage, context)){
            return false;
        }

        try {
            XWikiDocument ownerDoc = context.getWiki().getDocument(prefsPage, context);
            BaseObject userObj = ownerDoc.getObject("XWiki.XWikiGlobalRights");

            return userObj != null;
        } catch (XWikiException e) {
            return false;
        }
    }

    public boolean isRootCollectionExists() {
        String rootPage = spaceName+"."+Constants.ROOT_COLLECTION_PAGE;

        return context.getWiki().exists(rootPage, context);
    }

    public RootCollectionCompositeAsset getRootCollection() throws XWikiException {
        if (spaceName.equals(Constants.COLLECTION_PREFIX+Constants.GUEST_USER.replaceFirst(Constants.USER_PREFIX_REGEX, ""))) {
            return null;
        }
        
        String rootPage = spaceName+"."+Constants.ROOT_COLLECTION_PAGE;

        if (!isRootCollectionExists()) {
            throw new AssetException("Page "+rootPage+" does not exist");
        }

        RootCollectionCompositeAsset root;

        try {
            root = Asset.fetchAsset(rootPage, context).as(RootCollectionCompositeAsset.class);
        } catch (XWikiException ex) {
            // The page exists, but must not be a root collection -- fix it
            createRootCollection();
            root = Asset.fetchAsset(rootPage, context).as(RootCollectionCompositeAsset.class);
        }

        return root;
    }

    static public RootCollectionCompositeAsset getRootCollection(String space, XWikiContext context) throws XWikiException {
        CollectionSpace cSpace = new CollectionSpace(space, context);

        return cSpace.getRootCollection();
    }


    protected void createRootCollection() throws XWikiException {
        Map<String,String> ownerMap = getOwner();

        XWikiDocument doc = context.getWiki().getDocument(spaceName, Constants.ROOT_COLLECTION_PAGE, context);

        doc.setCustomClass(Asset.class.getName());
        doc.setCreator(context.getUser());
        doc.setContent("");
        doc.setParent(context.getUser());

        if (!doc.isNew()) {
            // Update page - Remove objects to re-create
            doc.removeObjects(Constants.ASSET_CLASS);
            doc.removeObjects(Constants.COMPOSITE_ASSET_CLASS);
            doc.removeObjects(Constants.RIGHTS_CLASS);
            doc.removeObjects(Constants.ASSET_LICENCE_CLASS);
        }

        BaseObject assetObj = doc.newObject(Constants.ASSET_CLASS, context);
        assetObj.setStringValue(Constants.ASSET_CLASS_RIGHT, Constants.ASSET_CLASS_RIGHT_MEMBERS);

        BaseObject compObj = doc.newObject(Constants.COMPOSITE_ASSET_CLASS, context);
        compObj.set(Constants.COMPOSITE_ASSET_CLASS_TYPE, Constants.COMPOSITE_ASSET_CLASS_TYPE_ROOT_COLLECTION, context);

        String owner = ownerMap.get("owner");
        String ownerType = ownerMap.get("ownerType");

        BaseObject obj = doc.newObject(Constants.RIGHTS_CLASS, context);
        obj.setLargeStringValue(ownerType, owner);
        obj.setStringValue("levels", "edit");
        obj.setIntValue("allow", 1);

        BaseObject newLicenceObj = doc.newObject(Constants.ASSET_LICENCE_CLASS, context);
        newLicenceObj.setStringValue(Constants.ASSET_LICENCE_ITEM_LICENCE_TYPE, Constants.ASSET_LICENCE_ITEM_LICENCE_TYPE_DEFAULT);

        context.getWiki().saveDocument(doc, context.getMessageTool().get("curriki.comment.createrootcollection"), context);
    }

    public boolean isFavoritesCollectionExists() {
        String favPage = spaceName+"."+Constants.FAVORITES_COLLECTION_PAGE;

        return context.getWiki().exists(favPage, context);
    }

    protected void createFavoritesCollection() throws XWikiException {
        if (isGroupSpace()) {
            // Do not create for group collection spaces
            return;
        }

        Map<String,String> ownerMap = getOwner();

        if (!ownerMap.get("owner").equals(context.getUser())) {
            // Only create the favorites collection for the user
            return;
        }

        Asset asset = Asset.createTempAsset(null, context);

        CollectionCompositeAsset fav = asset.makeCollection();
        fav.setTitle(Constants.FAVORITES_COLLECTION_TITLE);
        fav.setTitle(Constants.FAVORITES_COLLECTION_TITLE);
        fav.set(Constants.ASSET_CLASS_DESCRIPTION, Constants.FAVORITES_COLLECTION_TITLE);

        // we select the "Resource: Reference Collection" ICT value
        List<String> ictList = new ArrayList<String>();
        ictList.add("resource_collection");
        fav.set(Constants.ASSET_CLASS_INSTRUCTIONAL_COMPONENT, ictList);

        // we select the "Other" the education level
        List<String> eduList = new ArrayList<String>();
        eduList.add("na");
        fav.set(Constants.ASSET_CLASS_EDUCATIONAL_LEVEL, eduList);

        // we select the root of the Master framework
        List<String> fwList = new ArrayList<String>();
        fwList.add(Constants.ASSET_CLASS_FRAMEWORK_ITEMS_DEFAULT);
        fav.set(Constants.ASSET_CLASS_FRAMEWORK_ITEMS, fwList);

        fav.publish(spaceName, Constants.FAVORITES_COLLECTION_PAGE, false);
    }

    public boolean isGroupSpace() {
        return spaceName.startsWith(Constants.GROUP_COLLECTION_SPACE_PREFIX);
    }

    public boolean isUserSpace() {
        return spaceName.startsWith(Constants.COLLECTION_PREFIX);
    }

    protected Map<String,String> getOwner() throws XWikiException {
        Map<String,String> ownerMap = new HashMap<String,String>(2);

        String owner;
        String ownerType;

        if (isGroupSpace()){
            owner = spaceName.replaceFirst("^"+Constants.GROUP_COLLECTION_PREFIX_SPACE_PREFIX, "") + ".MemberGroup";
        } else if (isUserSpace()){
            owner = "XWiki."+spaceName.replaceFirst("^"+Constants.COLLECTION_PREFIX, "");
        } else {
            throw new AssetException("Cannot determine owner for collection space: "+spaceName);
        }

        if (context.getWiki().exists(owner, context)){
            XWikiDocument ownerDoc = context.getWiki().getDocument(owner, context);
            BaseObject userObj = ownerDoc.getObject("XWiki.XWikiUsers");
            if (userObj != null){
                ownerType = "users";
            } else {
                BaseObject groupObj = ownerDoc.getObject("XWiki.XWikiGroups");
                if (groupObj != null){
                    ownerType = "groups";
                } else {
                    throw new XWikiException(XWikiException.MODULE_XWIKI_GWT_API, XWikiException.ERROR_XWIKI_DOES_NOT_EXIST, "Cannot set owner for "+spaceName+". No user or group exists.");
                }
            }
        } else {
            throw new AssetException("Cannot determine owner for collection space: "+spaceName);
        }

        ownerMap.put("owner", owner);
        ownerMap.put("ownerType", ownerType);

        return ownerMap;
    }

    protected void protectSpace() throws XWikiException {
        Map<String,String> ownerMap = getOwner();

        String owner = ownerMap.get("owner");
        String ownerType = ownerMap.get("ownerType");

        XWikiDocument doc = context.getWiki().getDocument(spaceName, "WebPreferences", context);
        doc.removeObjects("XWiki.XWikiGlobalRights");

        BaseObject obj = doc.newObject("XWiki.XWikiGlobalRights", context);

        obj.setLargeStringValue("groups", "XWiki.XWikiAllGroup, XWiki.EditorGroup");
        obj.setStringValue("levels", "edit");
        obj.setIntValue("allow", 1);

        obj = doc.newObject("XWiki.XWikiGlobalRights", context);
        obj.setLargeStringValue(ownerType, owner);
        obj.setStringValue("levels", "edit");
        obj.setIntValue("allow", 1);

        if (isGroupSpace()){
            doc.setStringValue("XWiki.XWikiPreferences", "parent", spaceName.replaceFirst("^"+Constants.GROUP_COLLECTION_PREFIX_SPACE_PREFIX, ""));
            obj = doc.newObject("XWiki.XWikiGlobalRights", context);
            obj.setLargeStringValue("groups", spaceName.replaceFirst("^"+Constants.GROUP_COLLECTION_PREFIX_SPACE_PREFIX, "") + ".AdminGroup");
            obj.setStringValue("levels", "admin");
            obj.setIntValue("allow", 1);
        }

        context.getWiki().saveDocument(doc, context.getMessageTool().get("curriki.comment.protectspace"), true, context);
    }
}
