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

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.web.XWikiRequest;
import org.curriki.xwiki.plugin.licence.Licence;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


public class AssetManagerPluginApi extends Api {
    private AssetManagerPlugin assetManagerPlugin;
    public AssetManagerPluginApi(XWikiContext context) {
        super(context);
    }

    public AssetManagerPluginApi(AssetManagerPlugin assetManagerPlugin, XWikiContext context) {
        super(context);
        this.assetManagerPlugin = assetManagerPlugin;
    }

    public Asset createAssetDocument(String assetName) throws XWikiException {
        return assetManagerPlugin.createAssetDocument(assetName, context);
    }

    public Asset getTemporaryAssetDocument(String assetName) throws XWikiException {
        return assetManagerPlugin.getTemporaryAssetDocument(assetName, true, context);
    }

    public Asset getTemporaryAssetDocument(String assetName, boolean create) throws XWikiException {
        return assetManagerPlugin.getTemporaryAssetDocument(assetName, create, context);    
    }

    public void addAttachment(InputStream iStream, String name, Asset asset) throws IOException, XWikiException {
        assetManagerPlugin.addAttachment(iStream, name, asset, context);        
    }

    public boolean isComplet(Asset asset){
         return assetManagerPlugin.isComplet(asset, context);
    }

    public Map getStatusList(Asset asset){
       return assetManagerPlugin.getStatusList(asset, context);
    }

    public boolean copyAsset(Asset fromAsset, Asset toAsset){
        return false;   
    }

    public String fromRequest(XWikiRequest request){
        return null;
    }

    public boolean getStatus(Asset asset, int status){
        return assetManagerPlugin.getStatus(asset,  new Integer(status), context);
    }

    public void setLicence(Licence licence){

    }

    public String getMasterFrameworkItem(Asset asset){
        return assetManagerPlugin.getMasterFrameworkItem(asset, context);
    }

    public List getAdditionalFrameworkItems(){
        return null;
    }

    public boolean addExternalAsset(Asset asset, String link) throws XWikiException {
        return assetManagerPlugin.addExternalAsset(asset, link, context);
    }

    public List getExternalAsset(Asset asset){
        return assetManagerPlugin.getExternalAsset(asset, context);    
    }

    public String getTechnicalMetaDataClassName(Asset asset) throws XWikiException {
        return assetManagerPlugin.getTechnicalMetaDataClassName(asset, context);
    }

    public String getAssetTemporarySpace(){
        return assetManagerPlugin.getAssetTemporarySpace();
    }

    public String createOrUpdateAssetFromRequest() throws XWikiException {
        return assetManagerPlugin.createOrUpdateAssetFromRequest(context);
    }

    public Asset publishAsset(Asset asset) throws XWikiException {
        return assetManagerPlugin.publishAsset(asset, context);     
    }

     public Asset publishAsset(Asset asset, String collection) throws XWikiException {
        return assetManagerPlugin.publishAsset(asset, collection, context);   
     }

    public List getPublishedCollection() throws XWikiException {
        return assetManagerPlugin.getPublishedCollection(context);
    }

    public List getPublishedCollection(String userName) throws XWikiException {
        return assetManagerPlugin.getPublishedCollection(userName, context);
    }

    public List getUnpublishedCollection() throws XWikiException {
        return assetManagerPlugin.getUnpublishedCollection(context);
    }

    public List getUnpublishedCollection(String userName) throws XWikiException {
        return assetManagerPlugin.getUnpublishedCollection(userName, context);
    }

    public AssetManagerPlugin.CollectionQuery getCollectionQueryObject(){
        return assetManagerPlugin.getCollectionQuery();
    }

    public List getCollection(AssetManagerPlugin.CollectionQuery colQ) throws XWikiException {
        return assetManagerPlugin.getCollection(colQ, context);
    }
}
