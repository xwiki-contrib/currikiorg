package org.curriki.xwiki.plugin.curriki;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.api.Property;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.lang.Object;
import java.lang.Class;

import org.curriki.xwiki.plugin.asset.Asset;

/**
 */
public class CurrikiPluginApi extends Api {
    private CurrikiPlugin plugin;

    public CurrikiPluginApi(XWikiContext context) {
        super(context);
    }

    public CurrikiPluginApi(CurrikiPlugin plugin, XWikiContext context) {
        this(context);
        this.plugin = plugin;
    }

    /* Hopefully we can make sure this is not needed
   public CurrikiPlugin getPlugin() {
       return (hasProgrammingRights() ? plugin : null);
   }
    */

    public List<String> fetchUserCollectionsList() {
        return plugin.fetchUserCollectionsList(context.getUser(), context);
    }

    public List<String> fetchUserCollectionsList(String forUser) {
        return plugin.fetchUserCollectionsList(forUser, context);
    }

    public Map<String,Object> fetchUserCollectionsInfo() {
        return plugin.fetchUserCollectionsInfo(context.getUser(), context);
    }

    public Map<String,Object> fetchUserCollectionsInfo(String forUser) {
        return plugin.fetchUserCollectionsInfo(forUser, context);
    }

    public Map<String,Object> fetchUserGroups() {
        return plugin.fetchUserGroups(context.getUser(), context);
    }

    public Map<String,Object> fetchUserGroups(String forUser) {
        return plugin.fetchUserGroups(forUser, context);
    }

    public List<String> fetchGroupCollectionsList(String forGroup) {
        return plugin.fetchGroupCollectionsList(forGroup, context);
    }

    public Map<String,Object> fetchGroupCollectionsInfo(String forGroup) {
        return plugin.fetchGroupCollectionsInfo(forGroup, context);
    }

    public Asset createAsset(String parentAsset) throws XWikiException {
        return plugin.createAsset(parentAsset, context);
    }

    public String createAssetName(String parentAsset) throws XWikiException {
        return createAsset(parentAsset).getFullName();
    }

    public Asset fetchAsset(String assetName) throws XWikiException {
        return plugin.fetchAsset(assetName, context);
    }

    public Asset fetchAssetAs(String assetName, Class<? extends Asset> classType) throws XWikiException {
        return plugin.fetchAssetAs(assetName, classType, context);
    }



    public List<Property> fetchAssetMetadata(String assetName) throws XWikiException {
        return plugin.fetchAssetMetadata(assetName, context);
    }

    public Map<String, Object> fetchUserInfo() {
        return plugin.fetchUserInfo(context);
    }
}
