--
-- SQL script to fix CURRIKI-2519
--
-- Make document bundle column into a MEDIUMTEXT instead of a VARCHAR(255)
-- so that our large list of translation pages can be listed
--
ALTER TABLE `xwikipreferences`
 CHANGE `XWP_DOCUMENT_BUNDLES`
  `XWP_DOCUMENT_BUNDLES` MEDIUMTEXT
                         CHARACTER SET utf8
                         COLLATE utf8_general_ci
                         NULL
                         DEFAULT NULL;
