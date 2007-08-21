-- DB-Patch-CURRIKI-768.sql
--
-- Do fixups on bad AssetClass data
--
-- Should be applied during deployment of P1R2b (August Release)
--

-- **** Migrate description field from string to largestring

INSERT IGNORE INTO xwikilargestrings (XWL_ID, XWL_NAME, XWL_VALUE) SELECT XWS_ID, XWS_NAME, XWS_VALUE FROM xwikistrings WHERE XWS_NAME = 'description' AND XWS_ID IN (SELECT XWO_ID FROM xwikiobjects WHERE XWO_CLASSNAME = 'XWiki.AssetClass');

UPDATE xwikiproperties SET XWP_CLASSTYPE='com.xpn.xwiki.objects.LargeStringProperty' WHERE XWP_NAME='description' AND XWP_CLASSTYPE = 'com.xpn.xwiki.objects.StringProperty' AND XWP_ID IN (SELECT XWO_ID FROM xwikiobjects WHERE XWO_CLASSNAME = 'XWiki.AssetClass');

DELETE FROM xwikistrings WHERE XWS_NAME = 'description' AND XWS_ID IN (SELECT XWP_ID FROM xwikiproperties WHERE XWP_NAME = 'description' AND XWP_CLASSTYPE = 'com.xpn.xwiki.objects.LargeStringProperty' AND XWP_ID IN (SELECT XWO_ID FROM xwikiobjects WHERE XWO_CLASSNAME = 'XWiki.AssetClass'));


-- ***** Migrate licenseType2 field from listitem to string

INSERT IGNORE INTO xwikistrings (XWS_ID, XWS_NAME, XWS_VALUE) SELECT XWL_ID, XWL_NAME, XWL_VALUE FROM xwikilistitems WHERE XWL_NAME = 'licenseType2' AND XWL_ID IN (SELECT XWO_ID FROM xwikiobjects WHERE XWO_CLASSNAME = 'XWiki.AssetLicenseClass');

UPDATE xwikiproperties SET XWP_CLASSTYPE='com.xpn.xwiki.objects.StringProperty' WHERE XWP_NAME = 'licenseType2' AND XWP_CLASSTYPE != 'com.xpn.xwiki.objects.StringProperty' AND XWP_ID IN (SELECT XWO_ID FROM xwikiobjects WHERE XWO_CLASSNAME = 'XWiki.AssetLicenseClass');

DELETE FROM xwikilistitems WHERE XWL_NAME = 'licenseType2' AND XWL_ID IN (SELECT XWP_ID FROM xwikiproperties WHERE XWP_NAME = 'licenseType2' AND XWP_CLASSTYPE = 'com.xpn.xwiki.objects.StringProperty' AND XWP_ID IN (SELECT XWO_ID FROM xwikiobjects WHERE XWO_CLASSNAME = 'XWiki.AssetLicenseClass'));

DELETE FROM xwikilists WHERE XWL_NAME = 'licenseType2' AND XWL_ID IN (SELECT XWP_ID FROM xwikiproperties WHERE XWP_NAME = 'licenseType2' AND XWP_CLASSTYPE = 'com.xpn.xwiki.objects.StringProperty' AND XWP_ID IN (SELECT XWO_ID FROM xwikiobjects WHERE XWO_CLASSNAME = 'XWiki.AssetLicenseClass'));


-- ****** Migrate fw_items field from largestring to listitem

INSERT IGNORE INTO xwikilistitems (XWL_ID, XWL_NAME, XWL_VALUE, XWL_NUMBER) SELECT XWL_ID, XWL_NAME, XWL_VALUE, 0 FROM xwikilargestrings WHERE XWL_NAME = 'fw_items' AND XWL_ID IN (SELECT XWO_ID FROM xwikiobjects WHERE XWO_CLASSNAME = 'XWiki.AssetClass');

INSERT IGNORE INTO xwikilists (XWL_ID, XWL_NAME) SELECT XWL_ID, XWL_NAME FROM xwikilargestrings WHERE XWL_NAME = 'fw_items' AND XWL_ID IN (SELECT XWO_ID FROM xwikiobjects WHERE XWO_CLASSNAME = 'XWiki.AssetClass');

UPDATE xwikiproperties SET XWP_CLASSTYPE='com.xpn.xwiki.objects.DBStringListProperty' WHERE XWP_NAME='fw_items' AND XWP_CLASSTYPE = 'com.xpn.xwiki.objects.StringListProperty' AND XWP_ID IN (SELECT XWO_ID FROM xwikiobjects WHERE XWO_CLASSNAME = 'XWiki.AssetClass');

DELETE FROM xwikilargestrings WHERE XWL_NAME = 'fw_items' AND XWL_ID IN (SELECT XWP_ID FROM xwikiproperties WHERE XWP_NAME = 'fw_items' AND XWP_CLASSTYPE = 'com.xpn.xwiki.objects.DBStringListProperty' AND XWP_ID IN (SELECT XWO_ID FROM xwikiobjects WHERE XWO_CLASSNAME = 'XWiki.AssetClass'));

