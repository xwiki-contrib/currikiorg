-- DB-Patch-CURRIKI-772.sql
--
-- Do fixups due to change of class name
--
-- Should be applied during deployment of P1R2b (August Release)
--

--- **** Update org.gelc to org.curriki

UPDATE xwikidoc SET XWD_CUSTOM_CLASS = 'org.curriki.xwiki.plugin.asset.Asset' WHERE XWD_CUSTOM_CLASS = 'org.gelc.xwiki.plugins.assets.Asset';
