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
    String USER_PREFIX = "XWiki.";
    String USER_PREFIX_REGEX = "^XWiki\\.";

    String GUEST_USER = "XWiki.XWikiGuest";

    String ASSET_TEMPORARY_SPACE = "AssetTemp";
    String ASSET_DISPLAYTITLE_UNTITLED = "Untitiled";

    String COLLECTION_PREFIX = "Coll_";
    String FAVORITES_COLLECTION_TITLE = "Favorites";

    String FAVORITES_COLLECTION_PAGE = "Favorites";
    String ROOT_COLLECTION_PAGE = "WebHome";

    /**
     *     Class FrameworkItemAssetClass
     */
    String FRAMEWORK_ITEM_ASSET_CLASS = "XWiki.FrameworkItemAssetClass";

    /**
     *     Class AssetClass
     */
    String ASSET_CLASS = "CurrikiCode.AssetClass";

    String ASSET_CLASS_DESCRIPTION = "description";
    String ASSET_CLASS_KEYWORDS = "keywords";
    String ASSET_CLASS_CATEGORY = "category";
    String ASSET_CLASS_FRAMEWORK_ITEMS = "fw_items";
    String ASSET_CLASS_FRAMEWORK_ITEMS_DEFAULT = "FW_masterFramework.WebHome";
    String ASSET_CLASS_FRAMEWORK_ITEMS_QUERY = "select doc.fullName, doc.title, doc.parent from XWikiDocument as doc, BaseObject as obj where doc.web in ('FW_masterFramework') and doc.fullName=obj.name and obj.className='XWiki.FrameworkItemClass' order by doc.title";

    String ASSET_CLASS_RIGHT = "rights";
    String ASSET_CLASS_RIGHT_PUBLIC = "public";
    String ASSET_CLASS_RIGHT_MEMBERS = "members";
    String ASSET_CLASS_RIGHT_PRIVATE = "private";
    String ASSET_CLASS_RIGHT_VALUES = "public=Public: Available to anyone and any member can edit.|members=Protected: Available to anyone but only you (or your group members) can edit this copy.|private=Private: Only you (or your group members) can view or edit.";

    String ASSET_CLASS_EDUCATIONAL_LEVEL = "educational_level";
    String ASSET_CLASS_EDUCATIONAL_LEVEL_VALUES = "prek=Preschool / Ages 0-4|gr-k-2=Gr. K-2 / Ages 5-7|gr-3-5=Gr. 3-5 / Ages 8-10|gr-6-8=Gr. 6-8 / Ages 11-13|gr-9-10=Gr. 9-10 / Ages 14-16|gr-11-12=Gr. 11-12 / Ages 16-18|college_and_beyond=College and Beyond|professional_development=Professional Development|special_education=Special Education|na=Other";

    String ASSET_CLASS_INSTRUCTIONAL_COMPONENT = "instructional_component";
    String ASSET_CLASS_INSTRUCTIONAL_COMPONENT_VALUES = "choose=Choose from list...|activity=Activity|animation=Animation/Simulation|application=Application|assesment=Assessment/Test|audio=Audio/Voice/Song|book=Book|textbook=Book Text Book|course=Course/Curriculum|collection=Collection (of related assets)|diagram=Diagram/Illustration|exercise=Exercise/Problem Set|experiment=Experiment/Lab|graph=Graph/Table|graphic=Graphic/Image|index=Index|image=Image/Photograph|learningobject=Learning Object (SCORM)|lessonplan=Lesson Plan|lecture=Lecture|presentation=Presentation/Slide Show|professionaldevelopment=Professional Development|resourceslist=Resource List/Link(s)|text=Text/Notes/Transcript|video=Video|website=Web Site|worksheet=Worksheet/Graphic Organizer|other=Other";
 
    String ASSET_CLASS_LANGUAGE = "language";
    String ASSET_CLASS_LANGUAGE_VALUES = "eng=English|ind=Bahasa Indonesia|zho=Chinese|nld=Dutch|fra=French|deu=German|hin=Hindi|ita=Italian|jpn=Japanese|kor=Korean|nep=Nepali|por=Portuguese|rus=Russian|sin=Sinhalese|spa=Spanish|tam=Tamil|---=Other";
    String ASSET_CLASS_HIDDEN_FROM_SEARCH = "hidden_from_search";

    String ASSET_CATEGORY_UNKNOWN = "unknown";
    String ASSET_CATEGORY_TEXT = "text";
    String ASSET_CATEGORY_DOCUMENT = "document";
    String ASSET_CATEGORY_IMAGE = "image";
    String ASSET_CATEGORY_AUDIO= "audio";
    String ASSET_CATEGORY_VIDEO = "video";
    String ASSET_CATEGORY_INTERACTIVE = "interactive";
    String ASSET_CATEGORY_ARCHIVE = "archive";
    String ASSET_CATEGORY_EXTERNAL = "external";
    String ASSET_CATEGORY_COLLECTION = "collection";

    String ASSET_CLASS_TRACKING = "tracking";

    // File check fields
    String ASSET_CLASS_FCSTATUS = "fcstatus";
    String ASSET_CLASS_FCSTATUS_VALUES = "0=none|1=ok|2=specialcheck|3=improvementrequested|4=deleted";
    String ASSET_CLASS_FCREVIEWER = "fcreviewer";
    String ASSET_CLASS_FCDATE = "fcdate";
    String ASSET_CLASS_FCDATE_FORMAT =  "MM/dd/yyyy HH:mm";
    String ASSET_CLASS_FCNOTES = "fcnotes";

    /**
     *    Class AssetLicenseClass
     */
    String ASSET_LICENCE_CLASS = "CurrikiCode.AssetLicenseClass";

    String ASSET_LICENCE_ITEM_RIGHTS_HOLDER = "rightsHolder";
    String ASSET_LICENCE_ITEM_EXTERNAL_RIGHTS_HOLDER = "externalRightsHolder";
    String ASSET_LICENCE_ITEM_LICENCE_TYPE = "licenseType";
    String ASSET_LICENCE_ITEM_LICENCE_TYPE_VALUES = "Licences.CurrikiLicense=Curriki License (Creative Commons Attributions 3.0)|Licences.PublicDomain=Public Domain|Licences.CreativeCommonsAttributionNon-Commercial=Creative Commons Attribution Non-Commercial|Licences.CreativeCommonsAttributionNoDerivatives=Creative Commons Attribution No Derivatives|Licences.CreativeCommonsAttributionNon-CommercialNoDerivatives|Licences.CreativeCommonsAttributionSharealike=Creative Commons Attribution Share Alike|Licences.CreativeCommonsAttributionNon-CommercialShareAlike=Creative Commons Attribution Non-Commercial Share Alike";
    // String ASSET_LICENCE_ITEM_LICENCE_TYPE_QUERY = "select obj.name, prop.value from BaseObject as obj, StringProperty as prop, IntegerProperty as oprop where  obj.className='XWiki.LicenceClass' and prop.id.id = obj.id  and prop.id.name = 'name' and prop.value not like 'DEPRECATED:%' and oprop.id.id = obj.id and oprop.id.name = 'order' order by oprop.value";
    String ASSET_LICENCE_ITEM_LICENCE_TYPE_DEFAULT = "Licences.CurrikiLicense";
    String ASSET_LICENCE_ITEM_EXPIRY_DATE = "expiryDate";
    String ASSET_LICENCE_ITEM_EXPIRY_DATE_FORMAT = "dd/MM/yyyy";


    String SUBASSET_CLASS = "CurrikiCode.SubAssetClass";
    String SUBASSET_CLASS_PAGE = "assetpage";
    String SUBASSET_CLASS_ORDER = "order";

    /**
      * Class Composite Asset Class
      */
    String COMPOSITE_ASSET_CLASS = "CurrikiCode.CompositeAssetClass";
    String COMPOSITE_ASSET_CLASS_TYPE = "type";
    String COMPOSITE_ASSET_CLASS_TYPE_SUBFOLDER = "curriki_document";
    String COMPOSITE_ASSET_CLASS_TYPE_COLLECTION = "collection";
    String COMPOSITE_ASSET_CLASS_TYPE_ROOT_COLLECTION = "root_collection";

    /**
     * Class Collection Reordered Class
     */
    String COLLECTION_REORDERED_CLASS = "CurrikiCode.CollectionReorderedClass";
    String COLLECTION_REORDERED_CLASS_REORDERD = "reordered";

    /**
     * Class TextAssetClass
     */
    String TEXT_ASSET_CLASS = "CurrikiCode.TextAssetClass";

    String TEXT_ASSET_SYNTAX = "syntax";
    String TEXT_ASSET_SYNTAX_TEXT = "text";
    String TEXT_ASSET_SYNTAX_CBOE = "cboe";
    String TEXT_ASSET_SYNTAX_XWIKI1 = "xwiki/1.0";
    String TEXT_ASSET_SYNTAX_XWIKI2 = "xwiki/2.0";
    String TEXT_ASSET_SYNTAX_XHTML1 = "xhtml/1.0";

    /**
     * Class TextAssetClass
     */
    String ATTACHMENT_ASSET_CLASS = "CurrikiCode.AttachmentAssetClass";
    String ATTACHMENT_ASSET_ALT_TEXT = "alt_text";
    String ATTACHMENT_ASSET_CAPTION_TEXT = "caption_text";
    String ATTACHMENT_ASSET_FILE_TYPE = "file_type";
    String ATTACHMENT_ASSET_FILE_SIZE = "file_size";


    /**
     * Class ImageAssetClass
     */
    String IMAGE_ASSET_CLASS = "CurrikiCode.ImageAssetClass";
    String IMAGE_ASSET_WIDTH = "width";
    String IMAGE_ASSET_HEIGHT = "height";

    /**
     * Class AudioAssetClass
     */
    // No data in class, so no class needed
    // String AUDIO_ASSET_CLASS = "CurrikiCode.AudioAssetClass";

    /**
     * Class VideoAssetClass
     */
    String VIDEO_ASSET_CLASS = "CurrikiCode.VideoAssetClass";
    String VIDEO_ASSET_ID = "id";

    /**
     * Class ArchiveAssetClass
     */
    String ARCHIVE_ASSET_CLASS = "CurrikiCode.ArchiveAssetClass";
    String ARCHIVE_ASSET_START_FILE = "startfile";

    /**
     * Class InteractiveAssetClass
     */
    // No date in class, so no class needed
    // String INTERACTIVE_ASSET_CLASS = "CurrikiCode.InteractiveAssetClass";

    /**
     *    Class ExternalAssetClass
     */
    String EXTERNAL_ASSET_CLASS = "CurrikiCode.ExternalAssetClass";
    String EXTERNAL_ASSET_LINK = "link";
 
    /**
     * Groups
     */
    String GROUP_SPACE_PREFIX = "Group_";
    String GROUP_COLLECTION_PREFIX_SPACE_PREFIX = COLLECTION_PREFIX;
    String GROUP_COLLECTION_SPACE_PREFIX = GROUP_COLLECTION_PREFIX_SPACE_PREFIX + GROUP_SPACE_PREFIX;
    String GROUP_RIGHTS_PAGE = "WebPreferences";
    String GROUP_RIGHTS_CLASS = "XWiki.CurrikiSpaceClass";
    String GROUP_RIGHTS_PROPERTY = "accessprivileges";
    String GROUP_RIGHT_PUBLIC = "open";
    String GROUP_RIGHT_PROTECTED = "protected";
    String GROUP_RIGHT_PRIVATE = "private";

    /**
     * Rights
     */
    String RIGHTS_CLASS = "XWiki.XWikiRights";
    String RIGHTS_CLASS_GROUP = "groups";
    String RIGHTS_CLASS_USER = "users";
    String RIGHTS_ADMIN_GROUP = "XWiki.XWikiAdminGroup";
    String RIGHTS_ALL_GROUP = "XWiki.XWikiAllGroup";

    /**
     * CRS
     */
    String ASSET_CURRIKI_REVIEW_CLASS = "CRS.CurrikiReviewClass";
    String ASSET_CURRIKI_REVIEW_CLASS_STATUS = "status";
    String ASSET_CURRIKI_REVIEW_CLASS_STATUS_PARTNER="200";
    
    String ASSET_CURRIKI_REVIEW_STATUS_CLASS="CRS.CurrikiReviewStatusClass";
    String ASSET_CURRIKI_REVIEW_STATUS_CLASS_STATUS = "status";
    String ASSET_CURRIKI_REVIEW_STATUS_CLASS_STATUS_PARTNER="200";


    /**
     * BASIC FILE CHECK STATUS
     */
    String ASSET_BFCS_STATUS="fcstatus";
    String ASSET_BFCS_STATUS_NONE="0";
    String ASSET_BFCS_STATUS_OK="1";
    String ASSET_BFCS_STATUS_SPECIALCHECKREQUIRED="2";
    String ASSET_BFCS_STATUS_IMPROVEMENTREQUIRED="3";
    String ASSET_BFCS_STATUS_DELETEDFROMMEMBERACCESS="4";

    /**
     * Old Asset Class
     */
    String OLD_ASSET_CLASS = "XWiki.AssetClass";
    String OLD_ASSET_CLASS_TITLE = "title";
    String OLD_ASSET_CLASS_EDUCATIONAL_LEVEL = "educational_level2";
    String OLD_ASSET_CLASS_INSTRUCTIONAL_COMPONENT = "instructional_component2";

    String OLD_ASSET_CLASS_ALT_TEXT = "alt_text";
    String OLD_ASSET_CLASS_CAPTION_TEXT = "caption_text";

    String OLD_COMPOSITE_ASSET_CLASS = "XWiki.CompositeAssetClass";

    String OLD_SUBASSET_CLASS = "XWiki.SubAssetClass";

    String OLD_COLLECTION_REORDERED_CLASS = "XWiki.CollectionReorderedClass";

    String OLD_ASSET_LICENCE_CLASS = "XWiki.AssetLicenseClass";
    String OLD_ASSET_LICENCE_ITEM_LICENCE_TYPE = "licenseType2";


    String OLD_TEXT_ASSET_CLASS = "XWiki.TextAssetClass";
    String OLD_TEXT_ASSET_CLASS_TEXT = "text";
    String OLD_TEXT_ASSET_CLASS_TYPE = "type";
    long OLD_TEXT_ASSET_CLASS_TYPE_TEXT = 0;
    long OLD_TEXT_ASSET_CLASS_TYPE_WIKITEXT = 0;
    long OLD_TEXT_ASSET_CLASS_TYPE_HTML = 1;
    long OLD_TEXT_ASSET_CLASS_TYPE_DIRECTION = 2;

    String OLD_EXTERNAL_ASSET_CLASS = "XWiki.ExternalAssetClass";
    String OLD_EXTERNAL_ASSET_LINK = "link";

    String OLD_MIMETYPE_PICTURE_CLASS = "MimeType.picture";
    String OLD_MIMETYPE_PICTURE_CLASS_DISPLAY_SIZE = "display_size";

    String OLD_MIMETYPE_ARCHIVE_CLASS = "MimeType.etc compression files";
    String OLD_MIMETYPE_ARCHIVE_CLASS_DEFAULT_FILE = "defaultfile";

    String OLD_VIDITALK_CLASS = "XWiki.VIDITalkAssetClass";
    String OLD_VIDITALK_CLASS_VIDEO_ID = "video_id";
    String OLD_VIDITALK_CAPTURE = "GWT.ViditalkCapture";
    String OLD_VIDITALK_PLAYER = "GWT.ViditalkPlayback";
    String OLD_VIDITALK_SITECODE_VAR = "viditalk.sitecode";
    String OLD_VIDITALK_DOWNLOAD_DIR = "http://flashmedia.viditalk.com/10/active/";

    String OLD_CATEGORY_DOCUMENT = "Document: .Doc, .XLS, PDF";
    String OLD_CATEGORY_IMAGE = "Image: GIF, JPG, PNG, BMP";
    String OLD_CATEGORY_AUDIO = "Media: Audio/Video";
    String OLD_CATEGORY_ANIMATION = "Media: Animation/Activity";
    String OLD_CATEGORY_ARCHIVE = "Archive: Web Site/Zip/Stuffit File";
    String OLD_CATEGORY_TEXT = "text";
    String OLD_CATEGORY_COLLECTION = "collection";
    String OLD_CATEGORY_LINK = "link";
    String OLD_CATEGORY_UNKNOWN = "unknown";
    String OLD_CATEGORY_VIDITALK_VIDEO = OLD_CATEGORY_AUDIO;
    // TODO: What about the list used by GWT:  audio, text, image, link ?


}
