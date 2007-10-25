package org.curriki.gwt.client;

import java.util.HashSet;
import java.util.Set;

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

public class Constants {
    public static final String TRANSLATION_PAGE = "XWiki.CurrikiGWTTranslation";

    public static final String CURRIKI_SERVICE = "/xwiki/gwt/CurrikiService";
    public static final String USER_URL_PREFIX = "/xwiki/bin/view/XWiki/";

    public static final String SKIN_PATH = "/xwiki/skins/curriki8/";
    public static final String MIMETYPE_PATH = SKIN_PATH+"mimetypes/";
    public static final String MIMETYPE_CURIKULUM_ICON = "Currikulum_Icon";
    public static final String ICON_PATH = SKIN_PATH+"icons/";
    public static final String ICON_SPINNER = ICON_PATH+"spinner.gif";
    public static final String CURRIKI_LOGO = SKIN_PATH+"curriki-logo.png";

    public static final String DEFAULT_MP3PLAYER_URL = "/xwiki/bin/download/MimeType/audio_mp3/mini_player_mp3.swf";

    public static final String COMPOSITEASSET_CLASS = "XWiki.CompositeAssetClass";
    public static final String COMPOSITEASSET_TYPE_PROPERTY = "type";


    public static final String USER_XWIKI_GUEST = "XWiki.XWikiGuest";

    public static final String ASSET_CLASS = "XWiki.AssetClass";
    public static final String ASSET_CATEGORY_PROPERTY = "category";
    public static final String ASSET_FW_ITEMS_PROPERTY = "fw_items";
    public static final String ASSET_DESCRIPTION_PROPERTY = "description";
    public static final String ASSET_EDUCATIONAL_LEVEL_PROPERTY = "educational_level2";
    public static final String ASSET_LANGUAGE_PROPERTY = "language"; 
    public static final String ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY = "instructional_component2";
    public static final String ASSET_TITLE_PROPERTY = "title";
    public static final String ASSET_RIGHTS_PROPERTY = "rights";
    public static final String ASSET_TRACKING_PROPERTY = "tracking";
    public static final String ASSET_HIDE_FROM_SEARCH_PROPERTY = "hide_from_search";

    public static final String ASSET_CAPTION_DESCRIPTION_PROPERTY = "caption_desc";
    public static final String ASSET_ALT_DESCRIPTION_PROPERTY = "alt_desc";        

    public static final String EXTERNAL_ASSET_CLASS = "XWiki.ExternalAssetClass";
    public static final String EXTERNAL_ASSET_LINK_PROPERTY = "link";

    public static final String ASSET_LICENCE_CLASS = "XWiki.AssetLicenseClass";
    public static final String ASSET_LICENCE_RIGHT_HOLDER_PROPERTY = "rightsHolder";
    public static final String ASSET_LICENCE_TYPE_PROPERTY = "licenseType2";

    public static final String SUBASSET_CLASS = "XWiki.SubAssetClass";
    public static final String SUBASSET_ORDER_PROPERTY = "order";
    public static final String SUBASSET_ASSETPAGE_PROPERTY = "assetpage";

    public static final String MIMETYPE_CLASS = "XWiki.MimeTypeClass";
    public static final String MIMETYPE_CATEGORY_PROPERTY = "category";

    public static final String MIMETYPE_PICTURE_CLASS = "MimeType.picture";
    public static final String MIMETYPE_PICTURE_DISPLAY_SIZE_PROPERTY = "display_size";

    public static final String MIMETYPE_ARCHIVE_CLASS = "MimeType.etc compression files";
    public static final String MIMETYPE_ARCHIVE_DEFAULT_FILE_PROPERTY = "defaultfile";

    public static final String IMG_SMALL = "1";
    public static final String IMG_MEDIUM = "2";
    public static final String IMG_LARGE = "3";

    public static final String DIRECTION_CLASS = "XWiki.DirectionClass";
    public static final String DIRECTION_TEXT_PROPERTY = "text";

    public static final String TEXTASSET_CLASS = "XWiki.TextAssetClass";
    public static final String TEXTASSET_TEXT_PROPERTY = "text";
    public static final String TEXTASSET_TYPE_PROPERTY = "type";
    public static final long TEXTASSET_TYPE_TEXT = 0;
    public static final long TEXTASSET_TYPE_HTML = 1;
    public static final long TEXTASSET_TYPE_DIRECTION = 2;

    public static final String VIDITALK_CLASS = "XWiki.VIDITalkAssetClass";
    public static final String VIDITALK_VIDEO_ID_PROPERTY = "video_id";
    public static final String VIDITALK_CAPTURE = "GWT.ViditalkCapture";
    public static final String VIDITALK_PLAYER = "GWT.ViditalkPlayback";
    public static final String VIDITALK_SITECODE_VAR = "viditalk.sitecode";
    public static final String VIDITALK_DOWNLOAD_DIR = "http://flashmedia.viditalk.com/10/active/"; 

    public static final String TEMPORARY_ASSET_SPACE = "AssetTemp";

    public static final String PAGE_BREAK = "PAGEBREAK";
    public static final String PROTECTED = "PROTECTED";
    public static final String DIRECTION = "DIRECTION";

    public static final String QUERY_GET_CATEGORY_PAGES = ", BaseObject as obj where " +
            "obj.className='"+ MIMETYPE_CLASS + "' " +
            "and doc.fullName=obj.name";

    public static final String DEFAULT_COLLECTION_PAGE = "Default";
    public static final String ROOT_COLLECTION_PAGE = "WebHome";


    public static final String TYPE_TEXT = "text";
    public static final String TYPE_DIRECTION = "direction";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_ATTACHMENT = "attachment";
    public static final String TYPE_ANIMATION = "animation";
    public static final String TYPE_ARCHIVE = "archive";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_LINK = "link";
    public static final String TYPE_PROTECTED = "protected";
    public static final String TYPE_COLLECTION = "composite";
    public static final String TYPE_UNDEFINED = "undefined";

    public static final String RIGHT_PUBLIC = "public";
    public static final String RIGHT_PROTECTED = "members";
    public static final String RIGHT_PRIVATE = "private";

    public static final String ASSET_LICENCE_TYPE_DEFAULT = "Licences.CurrikiLicense";
    public static final String ASSET_FW_ITEMS_DEFAULT = "FW_masterFramework.WebHome";

    public static final int PAGE_EDIT = 0;
    public static final int PAGE_PREVIEW = 1;
    public static final int PAGE_METADATA = 2;
    public static final int PAGE_COMMENT = 3;
    public static final int PAGE_HISTORY = 4;


    public static final String CATEGORY_IMAGE = "Image: GIF, JPG, PNG, BMP";
    public static final String CATEGORY_AUDIO = "Media: Audio/Video";
    public static final String CATEGORY_ANIMATION = "Media: Animation/Activity";
    public static final String CATEGORY_ARCHIVE = "Archive: Web Site/Zip/Stuffit File";
    public static final String CATEGORY_TEXT = "text";
    public static final String CATEGORY_COLLECTION = "collection";
    public static final String CATEGORY_LINK = "link";
    public static final String CATEGORY_UNKNOWN = "unknown";
    public static final String CATEGORY_VIDITALK_VIDEO = CATEGORY_AUDIO;

    public static final int EDIT = 1;
    public static final int VIEW = 0;
    public static final int UNKNOWN = -1;

    public static final String COMPOSITE_CURRIKI_DOCUMENT = "curriki_document";
    public static final String COMPOSITE_COLLECTION = "collection";
    public static final String COMPOSITE_ROOT_COLLECTION = "root_collection";
    public static final String COMPOSITE_ROOT_COLLECTION_CONTENT = "#includeForm(\"XWiki.MyCollectionsTemplate\")"; 

    public static final String DIALOG_CHOOSE_COLLECTION = "GWT.ChooseCollectionDialog";
    public static final String DIALOG_CHOOSE_COLLECTION_CREATE = "GWT.ChooseCollectionWhenCreatingDialog";
    public static final String DIALOG_ADD_FILE = "GWT.AddFileDialog";
    public static final String DIALOG_THANKYOU_ADD_CURRIKI = "GWT.ThankYouAddCurrikiDialog";
    public static final String DIALOG_THANKYOU_ADD_COLLECTION = "GWT.ThankYouAddCollectionDialog";
    public static final String DIALOG_THANKYOU_CREATE_COLLECTION = "GWT.ThankYouCreateCollectionDialog";
    public static final String DIALOG_CHOOSE_TEMPLATE = "GWT.ChooseTemplateDialog";
    public static final String DIALOG_PROPOSE_TEMPLATE_DUPLICATION = "GWT.ProposeTemplateDuplicationDialog";
    public static final String DIALOG_ADD_VIDITALK_VIDEO = VIDITALK_CAPTURE;

    public static final String TEMPLATES_SPACE = "Coll_Templates";
    public static final String TEMPLATES_USER = "Templates";

    public static final String TEMPLATE_ABOUT_COLLECTION = "Coll_Templates.AboutyourCollection";

    public static final int DIALOG_RESOURCE_TYPE_UNKNOWN = 0;
    public static final int DIALOG_RESOURCE_TYPE_EXISTING_RESOURCE = 1;
    public static final int DIALOG_RESOURCE_TYPE_FILE = 2;
    public static final int DIALOG_RESOURCE_TYPE_TEMPLATE = 3;
    public static final int DIALOG_FIND_FETCH_COUNT = 20;

    public static final Set SUPPORTED_MIMETYPES_ICONS;
    public static final String SUPPORTED_MIMETYPES_ICONS_STRING = 
        "ASF;AVI;BIN;BMP;CLASS;CSS;DCR;DIR;DOC;DSR;EXE;GIF;GPE;GTAR;GZ;HTM" +
        ";HTTP;IHA;IZH;JPG;MOV;MP3;MPG;PDF;PHP;PNG;PPT;PSD;RTF;SCO;SOS;SWF" +
        ";TIF;TXT;URL;WAV;WMV;WWW;XLS;XML;ZIP";

    static {
        SUPPORTED_MIMETYPES_ICONS = new HashSet();
        String[] types = SUPPORTED_MIMETYPES_ICONS_STRING.split(";");
        for (int i=0; i<types.length; i++) {
            if (types[i] != null && !"".equals(types[i])) {
                SUPPORTED_MIMETYPES_ICONS.add(types[i]);
            }
                
        }
    }

    // What type of message do we want when we propose to duplicate a resource
    public static final int PROPOSE_DUPLICATE_TEMPLATE = 1;
    public static final int PROPOSE_DUPLICATE_EDIT = 2;
    public static final int DEFAULT_NB_VERSIONS = 0;

    // CRS constants
    public static final String CURRIKI_REVIEW_STATUS_CLASS = "CRS.CurrikiReviewStatusClass";
    public static final String CURRIKI_REVIEW_STATUS_STATUS = "status";
    public static final String CURRIKI_REVIEW_STATUS_LASTTREVIEWDATE = "lastreview_date";
    public static final String CURRIKI_REVIEW_STATUS_REVIEWPENDING = "reviewpending";
    public static final String CURRIKI_REVIEW_CLASS = "CRS.CurrikiReviewClass";
    public static final String CURRIKI_REVIEW_TECHNICALCOMPLETNESS_PROPERTY = "technicalcompletness";
    public static final String CURRIKI_REVIEW_CONTENTACCURACY_PROPERTY = "contentaccuracy";
    public static final String CURRIKI_REVIEW_APPROPRIATEPEDAGOGY_PROPERTY = "appropriatepedagogy";
    public static final String CURRIKI_REVIEW_RATING_PROPERTY = "rating";
    public static final String CURRIKI_REVIEW_DATE_PROPERTY = "date";
    public static final String CURRIKI_REVIEW_COMMENT_PROPERTY = "comment";

    public static final String HISTORY_FIELD_SORTBY = "__sortBy";
    public static final String HISTORY_FIELD_ATITEM = "__atItem";
    public static final String HISTORY_FIELD_ADV_TOGGLE = "__advShow"; 
}
