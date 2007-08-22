-- DB-Patch-CURRIKI-772.sql
--
-- Do fixups due to change of class name
--
-- Should be applied during deployment of P1R2b (August Release)
--

--- **** Update org.gelc to org.curriki

UPDATE xwikidoc SET XWD_CUSTOM_CLASS = 'org.curriki.xwiki.plugin.asset.Asset' WHERE XWD_CUSTOM_CLASS = 'org.gelc.xwiki.plugins.assets.Asset';
update xwikidoc set XWD_CUSTOM_CLASS = 'org.curriki.xwiki.plugin.mimetype.MimeType' where XWD_CUSTOM_CLASS = 'org.gelc.xwiki.plugins.mime.MimeType';
update xwikidoc set XWD_CUSTOM_CLASS = 'org.curriki.xwiki.plugin.licence.Licence' where XWD_CUSTOM_CLASS = 'org.gelc.xwiki.plugins.licence.Licence';
update xwikidoc set XWD_CUSTOM_CLASS = 'org.curriki.xwiki.plugin.framework.Framework' where XWD_CUSTOM_CLASS = 'org.gelc.xwiki.plugins.framework.Framework';
