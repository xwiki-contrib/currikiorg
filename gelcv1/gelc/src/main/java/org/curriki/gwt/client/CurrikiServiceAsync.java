package org.curriki.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.xpn.xwiki.gwt.api.client.XWikiServiceAsync;

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

public interface CurrikiServiceAsync extends XWikiServiceAsync {

    // Asset creation
    void createTempSourceAsset(String compositeAssetPage, AsyncCallback async);

    void insertSubAsset(String compositeAssetPage, String assetPageName, long position, AsyncCallback async);

    void createTextSourceAsset(String compositeAssetPage, long html, AsyncCallback async);

    void createLinkAsset(String compositeAssetPage, String link, AsyncCallback async);

    void createTempCompositeAsset(String parent, AsyncCallback async);

    void createCompositeAsset(String space, AsyncCallback async);

    void finishUpdateMetaData(String assetPage, AsyncCallback async);

    void updateMetadata(String fullName, boolean fromTemplate, AsyncCallback async);

    void finalizeAssetCreation(String assetPage, String compositeAssetPage, long position, AsyncCallback async);

    // Collections
    void isDefaultCollectionExists(String space, AsyncCallback async);

    void addCompositeAssetToCollection(String assetPageName, String collectionName, AsyncCallback async);

    void getCollections(AsyncCallback async);

    void createCollection(String space, String pageName, String pageTitle, AsyncCallback async);

    void createCollectionDocument(String space, String pageName, String pageTitle, AsyncCallback async);

    // Composite Assets and SubAssets
    void getCompositeAsset(String compositeAssetPage, AsyncCallback async);

    void removeSubAsset(String compositeAssetPage, long position, AsyncCallback async);

    void moveAsset(String assetName, String fromParent, long fromPosition, String toParent, long toPosition, AsyncCallback async);

    void getFullTreeItem(String rootAssetPage, AsyncCallback async);

    // Lucene search
    void luceneSearch(String terms, int start, int nb, AsyncCallback async);

    // Templates
    void getTemplates(AsyncCallback async);

    void createTempSourceAssetFromTemplate(String templatePageName, String compositeAssetPage, boolean clearattachments, AsyncCallback async);

    void duplicateTemplateAsset(String parentAsset, String documentFullName, long index, AsyncCallback async);

    // Zip Assets
    void getFileTreeList(String pageName, String fileName, AsyncCallback async);
}
