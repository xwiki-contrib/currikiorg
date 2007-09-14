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
package org.curriki.xwiki.plugin.framework;

public interface FrameworkConstant {

    public final static String PLUGIN_NAME = "framework_manager";

    public final static String FRAMEWORK_HOME = "WebHome";
    public final static String FRAMEWORK_PREFIX = "FW_";

    public final static String LEARNING_STANDARD_DOCUMENT_TITLE = "title";
    public final static String LEARNING_STANDARD_DOCUMENT_AUTHOR = "author";
    public final static String LEARNING_STANDARD_DOCUMENT_INDENTIFIER = "docIdentifier";
    public final static String LEARNING_STANDARD_DOCUMENT_ROOT = "LearningStandardDocument";
    public final static String LEARNING_STANDARD_DOCUMENT_ITEM_PARENT = "isChildOf";
    public final static String LEARNING_STANDARD_DOCUMENT_ITEM_IDENTIFIER = "itemIdentifier";
    public final static String LEARNING_STANDARD_DOCUMENT_ITEM = "LearningStandardItem";
    public final static String LEARNING_STANDARD_DOCUMENT_ITEM_DESCRIPTION = "itemDescription";
    public final static String LEARNING_STANDARD_DOCUMENT_ITEM_TEXT = "itemText";

    public final static String FRAMEWORK_ITEM_CLASS_FULLNAME = "XWiki.FrameworkItemClass";
    public final static String FRAMEWORK_ITEM_CLASS_NAME = "FrameworkItemClass";

    public final static String FRAMEWORK_CLASS_FULLNAME = "XWiki.FrameworkClass";
    public final static String FRAMEWORK_CLASS_NAME = "FrameworkClass";

    public final static String FRAMEWORK_REFERENCE_PDF_NAME = "reference.pdf";


    public final static String CLASS_ITEM_IDENTIFIER = "itemIdentifier";
    public final static String CLASS_ITEM_PARENT_IDENTIFIER = "itemParentIdentifier";


    public final static int ERROR_FRAMEWORK_ALREADY_EXIST = 1;
    public final static int ERROR_FRAMEWORK_CANNOT_IMPORT_DOCUMENT = 2;
    public final static int ERROR_FRAMEWORK_DOCUMENT_IS_NOT_A_FRAMEWORK = 3;
    public final static int ERROR_FRAMEWORK_ITEM_ALREADY_EXIST = 4;
    public final static int ERROR_FRAMEWORK_PATH_ERROR = 5;
    public final static int ERROR_FRAMEWORK_RECURSIVE_PATH = 6;
    public final static int ERROR_FRAMEWORK_INPUTSTREAM_MARK_NOT_IMPLEMENTED = 7;


    public final static int INTEGRITY_CHECK_LEVEL_WARNING = 0;
    public final static int INTEGRITY_CHECK_LEVEL_ERROR = 1;

    public final static Integer INTEGRITY_CHECK_ERROR_PDF = Integer.valueOf(0);
    public final static Integer INTEGRITY_CHECK_ERROR_PDF_LEVEL = Integer.valueOf(INTEGRITY_CHECK_LEVEL_ERROR);
    public final static String INTEGRITY_CHECK_ERROR_PDF_TEXT = "PDF Reference Missing";

    public final static Integer INTEGRITY_CHECK_ERROR_CANNOT_GET_FRAMEWORK_DOCUMENT = Integer.valueOf(0);
    public final static Integer INTEGRITY_CHECK_ERROR_CANNOT_GET_FRAMEWORK_DOCUMENT_LEVEL = Integer.valueOf(INTEGRITY_CHECK_LEVEL_ERROR);
    public final static String INTEGRITY_CHECK_ERROR_CANNOT_GET_FRAMEWORK_DOCUMENT_TEXT = "Cannot get the framework document";

    public final static String CONTEXT_KEY_ERRORS_MSG = "FrameworkManagerPlugin_Errors";
    public final static String CONTEXT_KEY_ERRORS_CODE = "FrameworkManagerPlugin_ErrorsCode";
    public final static String CONTEXT_KEY_IMPORT_FILTER = "FrameworkManagerPlugin_ImportFilter";

    public final static String CONTEXT_KEY_IMPORT_DOC_EL = "FrameworkManagerPlugin_DocumentElement";
}
