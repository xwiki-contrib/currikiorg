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
package org.gelc.xwiki.plugins.assets;

public interface AssetConstant {
    public final static String PLUGIN_NAME = "asset_manager";

    public final static Integer ASSET_STATUS_ADDTIONAL_FRAMEWORK_SELECTED = Integer.valueOf(0);
    public final static Integer ASSET_STATUS_FRAMEWORK_ITEM_SELECTED = Integer.valueOf(1);
    public final static Integer ASSET_STATUS_ATTACHEMENT = Integer.valueOf(2);
    public final static Integer ASSET_STATUS_EXTERNAL_LINK = Integer.valueOf(3);
    public final static Integer ASSET_STATUS_LICENCE = Integer.valueOf(4);
    public final static Integer ASSET_STATUS_SIZE = Integer.valueOf(5);
    public final static Integer ASSET_STATUS_RIGHTS = Integer.valueOf(6);

    public final static String ASSET_TEMPORARY_SPACE = "AssetTemp";

    public final static String COLLECTION_PREFIX = "Coll_";

    /**
     *     Class FrameworkItemAssetClass
     */
    public final static String FRAMEWORK_ASSET_CLASS_FULLNAME = "XWiki.FrameworkItemAssetClass";
    public final static String FRAMEWORK_ASSET_CLASS_NAME = "FrameworkItemAssetClass";

    public final static String CLASS_FRAMEWORK_ITEM = "framework_item";


    /**
     *     Class AssetClass
     */
    public static final String ASSET_CLASS_FULLNAME = "XWiki.AssetClass";
    public static final String ASSET_CLASS_NAME = "AssetClass";

    public static final String ASSET_ITEM_TITLE = "title";
    public static final String ASSET_ITEM_DESCRIPTION = "description";
    public static final String ASSET_ITEM_KEYWORDS = "keywords";
    public static final String ASSET_ITEM_AGGREGATION_LEVEL = "aggregation_level";
    public static final String ASSET_ITEM_AGGREGATION_LEVEL_VALUES = "choose=Choose from list...|asset=Individual Asset(s)|resource=Learning Resource|lession=Lesson Collection|course=Course/Curriculum"; 
    public static final String ASSET_ITEM_CATEGORY = "category";
    public static final String ASSET_ITEM_FRAMEWORK_ITEMS = "fw_items";

    public static final String ASSET_ITEM_STATUS = "status";
    public static final String ASSET_ITEM_STATUS_VALUES = "choose=Choose from list...|draft=Draft|final=Final|revised=Revised";

    public static final String ASSET_ITEM_INSTRUCTIONAL_COMPONENT = "instructional_component";
    public static final String ASSET_ITEM_INSTRUCTIONAL_COMPONENT_VALUES = "choose=Choose from list...|activity=Activity|animation=Animation/Simulation|application=Application|assesment=Assessment/Test|audio=Audio/Voice/Song|book=Book|textbook=Book Text Book|course=Course/Curriculum|collection=Collection (of related assets)|diagram=Diagram/Illustration|exercise=Exercise/Problem Set|experiment=Experiment/Lab|graph=Graph/Table|graphic=Graphic/Image|index=Index|image=Image/Photograph|learningobject=Learning Object (SCORM)|lessonplan=Lesson Plan|lecture=Lecture|presentation=Presentation/Slide Show|professionaldevelopment=Professional Development|resourceslist=Resource List/Link(s)|text=Text/Notes/Transcript|video=Video|website=Web Site|worksheet=Worksheet/Graphic Organizer|other=Other�";

    public static final String ASSET_ITEM_DIFFICULTY = "difficulty";
    public static final String ASSET_ITEM_DIFFICULTY_VALUES = "Very easy|Easy|Medium|Difficult|Very difficult";

    public static final String ASSET_ITEM_EDUCATIONAL_LEVEL = "educational_level";
    public static final String ASSET_ITEM_EDUCATIONAL_LEVEL_VALUES = "choose=Choose from list...|preschool=Preschool|earlyelementary=Early Elementary|upperelementary=Upper Elementary|middleschool=Middle School|highschool=High School|highered=Higher Ed|professional=Professional|na=NA";

    public static final String ASSET_ITEM_RIGHT = "rights";
    public static final String ASSET_ITEM_RIGHT_PUBLIC = "public";
    public static final String ASSET_ITEM_RIGHT_MEMBERS = "members";
    public static final String ASSET_ITEM_RIGHT_PRIVATE = "private";
    public static final String ASSET_ITEM_RIGHT_VALUES = "public=Public: Available to anyone who visits Curriki.|members=Members: Available only to Curriki community members.|private=Private: Available only to you";
    

    /**
     *    Class ExternalAssetClass
     */
    public static final String EXTERNAL_ASSET_CLASS_FULLNAME = "XWiki.ExternalAssetClass";
    public static final String EXTERNAL_ASSET_CLASS_NAME = "ExternalAssetClass";

    public static final String EXTERNAL_ASSET_LINK = "link";
    public static final String REQUEST_ASSET_FULLNAME = "assetName";


    /**
     *    Class AssetLicenseClass
     */
    public static final String ASSET_LICENCE_CLASS_FULLNAME = "XWiki.AssetLicenseClass";
    public static final String ASSET_LICENCE_CLASS_NAME = "AssetLicenseClass";

    public static final String ASSET_LICENCE_ITEM_RIGHTS_HOLDER = "rightsHolder";
    public static final String ASSET_LICENCE_ITEM_EXTERNAL_RIGHTS_HOLDER = "externalRightsHolder";
    public static final String ASSET_LICENCE_ITEM_LICENCE_TYPE = "licenseType";
    public static final String ASSET_LICENCE_ITEM_EXPIRY_DATE = "expiryDate";



    
}
