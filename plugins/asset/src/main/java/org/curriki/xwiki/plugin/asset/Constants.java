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

public interface Constants {
    String GUEST_USER = "XWiki.XWikiGuest";

    String ASSET_TEMPORARY_SPACE = "AssetTemp";
    String ASSET_DISPLAYTITLE_UNTITLED = "Untitiled";

    String COLLECTION_PREFIX = "Coll_";
    String FAVORITES_COLLECTION_NAME = "Favorites";

    String FAVORITES_COLLECTION_PAGE = "Favorites";
    String ROOT_COLLECTION_PAGE = "WebHome";

    /**
     *     Class FrameworkItemAssetClass
     */
    String FRAMEWORK_ITEM_ASSET_CLASS = "XWiki.FrameworkItemAssetClass";

    /**
     *     Class AssetClass
     */
    String ASSET_CLASS = "XWiki.AssetClass";

    String ASSET_CLASS_TITLE = "title";
    String ASSET_CLASS_DESCRIPTION = "description";
    String ASSET_CLASS_KEYWORDS = "keywords";
    String ASSET_CLASS_CATEGORY = "category";
    String ASSET_CLASS_FRAMEWORK_ITEMS = "fw_items";
    String ASSET_CLASS_FRAMEWORK_ITEMS_DEFAULT = "FW_masterFramework.WebHome";
    String ASSET_CLASS_STATUS = "status";

    String ASSET_CLASS_RIGHT = "rights";
    String ASSET_CLASS_RIGHT_PUBLIC = "public";
    String ASSET_CLASS_RIGHT_MEMBERS = "members";
    String ASSET_CLASS_RIGHT_PRIVATE = "private";

    String ASSET_CLASS_EDUCATIONAL_LEVEL = "educational_level2";

    String ASSET_CLASS_INSTRUCTIONAL_COMPONENT = "instructional_component2";

    /**
     *    Class ExternalAssetClass
     */
    String EXTERNAL_ASSET_CLASS = "XWiki.ExternalAssetClass";
    String EXTERNAL_ASSET_LINK = "link";

    /**
     *    Class AssetLicenseClass
     */
    String ASSET_LICENCE_CLASS = "XWiki.AssetLicenseClass";

    String ASSET_LICENCE_ITEM_RIGHTS_HOLDER = "rightsHolder";
    String ASSET_LICENCE_ITEM_EXTERNAL_RIGHTS_HOLDER = "externalRightsHolder";
    String ASSET_LICENCE_ITEM_LICENCE_TYPE = "licenseType";
    String ASSET_LICENCE_ITEM_LICENCE_TYPE_DEFAULT = "Licences.CurrikiLicense";
    String ASSET_LICENCE_ITEM_EXPIRY_DATE = "expiryDate";

    /**
     *  Class CompositeAssetClass
     */
    String COMPOSITE_ASSET_CLASS = "XWiki.CompositeAssetClass";
    String COMPOSITE_ASSET_CLASS_TYPE = "type";
    String COMPOSITE_ASSET_CLASS_TYPE_SUBFOLDER = "curriki_document";
    String COMPOSITE_ASSET_CLASS_TYPE_COLLECTION = "collection";
    String COMPOSITE_ASSET_CLASS_TYPE_ROOT_COLLECTION = "root_collection";

    String SUBASSET_CLASS = "XWiki.SubAssetClass";
    String SUBASSET_CLASS_PAGE = "assetpage";
    String SUBASSET_CLASS_ORDER = "order";

    /**
     * Class TextAssetClass
     */
    String TEXT_ASSET_CLASS = "XWiki.TextAssetClass";
    String TEXT_ASSET_CLASS_TEXT = "text";
    String TEXT_ASSET_CLASS_TYPE = "type";
    long TEXT_ASSET_CLASS_TYPE_TEXT = 0;
    long TEXT_ASSET_CLASS_TYPE_HTML = 1;
    long TEXT_ASSET_CLASS_TYPE_DIRECTION = 2;

    String MIMETYPE_PICTURE_CLASS = "MimeType.picture";
    String MIMETYPE_PICTURE_CLASS_DISPLAY_SIZE = "display_size";

    String MIMETYPE_ARCHIVE_CLASS = "MimeType.etc compression files";
    String MIMETYPE_ARCHIVE_CLASS_DEFAULT_FILE = "defaultfile";

    String VIDITALK_CLASS = "XWiki.VIDITalkAssetClass";
    String VIDITALK_CLASS_VIDEO_ID = "video_id";
    String VIDITALK_CAPTURE = "GWT.ViditalkCapture";
    String VIDITALK_PLAYER = "GWT.ViditalkPlayback";
    String VIDITALK_SITECODE_VAR = "viditalk.sitecode";
    String VIDITALK_DOWNLOAD_DIR = "http://flashmedia.viditalk.com/10/active/";
    
    String CATEGORY_IMAGE = "Image: GIF, JPG, PNG, BMP";
    String CATEGORY_AUDIO = "Media: Audio/Video";
    String CATEGORY_ANIMATION = "Media: Animation/Activity";
    String CATEGORY_ARCHIVE = "Archive: Web Site/Zip/Stuffit File";
    String CATEGORY_TEXT = "text";
    String CATEGORY_COLLECTION = "collection";
    String CATEGORY_LINK = "link";
    String CATEGORY_UNKNOWN = "unknown";
    String CATEGORY_VIDITALK_VIDEO = CATEGORY_AUDIO;
    // TODO: What about the list used by GWT:  audio, text, image, link ?

    /**
     * Groups
     */
    String GROUP_SPACE_PREFIX = "Group_";
    String GROUP_COLLECTION_PREFIX_SPACE_PREFIX = COLLECTION_PREFIX;
    String GROUP_COLLECTION_SPACE_PREFIX = GROUP_COLLECTION_PREFIX_SPACE_PREFIX +GROUP_SPACE_PREFIX;
}
