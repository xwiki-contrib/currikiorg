package org.curriki.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XWikiService;
import com.xpn.xwiki.gwt.api.client.XWikiGWTException;
import org.curriki.gwt.client.widgets.browseasset.AssetItem;

import java.util.List;

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

public interface CurrikiService extends XWikiService {


    // Asset creation
    public Document createTempSourceAsset(String compositeAssetPage) throws XWikiGWTException;
    public boolean insertSubAsset(String compositeAssetPage, String assetPageName, long position) throws XWikiGWTException;
    public Document createTextSourceAsset(String compositeAssetPage, long html) throws XWikiGWTException;
    public Document createLinkAsset(String compositeAssetPage, String link) throws XWikiGWTException;
    public Document createTempCompositeAsset(String parent) throws XWikiGWTException;
    public Document createCompositeAsset(String space) throws XWikiGWTException;
    public void finishUpdateMetaData(String assetPage) throws XWikiGWTException;
    public Document updateMetadata(String fullName, boolean fromTemplate) throws XWikiGWTException;
    public Document finalizeAssetCreation(String assetPage, String compositeAssetPage, long position) throws XWikiGWTException;
    public Document updateViditalk(String fullName, String videoId) throws XWikiGWTException; 

    // Collections
    public boolean isDefaultCollectionExists(String space) throws XWikiGWTException;
    public boolean addCompositeAssetToCollection(String assetPageName, String collectionName) throws XWikiGWTException;
    public AssetItem getCollections() throws XWikiGWTException;
    public String createCollection(String space, String pageName, String pageTitle) throws XWikiGWTException;
    public Document createCollectionDocument(String space, String pageName, String pageTitle) throws XWikiGWTException;


    // Composite Assets and SubAssets
    public List getCompositeAsset(String compositeAssetPage) throws XWikiGWTException;
    public List removeSubAsset(String compositeAssetPage, long position) throws XWikiGWTException;
    public void moveAsset(String assetName, String fromParent, long fromPosition, String toParent, long toPosition) throws XWikiGWTException;
    public AssetItem getFullTreeItem(String rootAssetPage) throws XWikiGWTException, XWikiGWTException;

    // Lucene search
    public List luceneSearch(String terms, int start, int nb) throws XWikiGWTException;
    public List luceneSearch(String terms, int start, int nb, String sortBy) throws XWikiGWTException;

    // Templates
    public List getTemplates() throws XWikiGWTException;
    public Document createTempSourceAssetFromTemplate(String templatePageName, String compositeAssetPage, boolean clearattachments) throws XWikiGWTException;
    public Document createTempSourceAssetFromTemplate(String templatePageName, String compositeAssetPage, boolean clearattachments, boolean clearTitle) throws XWikiGWTException;
    public String duplicateTemplateAsset(String parentAsset, String documentFullName, long index) throws XWikiGWTException;

    // Zip Assets
    public List getFileTreeList(String pageName, String fileName) throws XWikiGWTException;

    /**
     * Utility/Convinience class.
     * Use CurrikiService.App.getInstance() to access static instance of CurrikiServiceAsync
     */
    public static class App {
        private static CurrikiServiceAsync ourInstance = null;

        public static synchronized CurrikiServiceAsync getInstance() {
            if (ourInstance == null) {
                ourInstance = (CurrikiServiceAsync) GWT.create(CurrikiService.class);
                ((ServiceDefTarget) ourInstance).setServiceEntryPoint(Constants.CURRIKI_SERVICE);
            }
            return ourInstance;
        }
    }
}
