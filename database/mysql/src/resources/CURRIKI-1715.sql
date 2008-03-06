-- 
-- SQL script for CURRIKI-1715
--
-- Change all users with bounced email to be "inactive"
--

REPLACE
   INTO xwikiintegers (XWI_ID, XWI_NAME, XWI_VALUE)
 SELECT XWO_ID, 'active', 0
   FROM xwikiobjects
  WHERE XWO_CLASSNAME = 'XWiki.XWikiUsers'
    AND XWO_ID IN (SELECT XWI_ID
                     FROM xwikiintegers
                    WHERE XWI_NAME = 'email_undeliverable'
                      AND XWI_VALUE = 1
                  );

INSERT IGNORE
   INTO xwikiproperties (XWP_ID, XWP_NAME, XWP_CLASSTYPE)
 SELECT XWI_ID, 'active', 'com.xpn.xwiki.objects.IntegerProperty'
   FROM xwikiintegers
  WHERE XWI_NAME = 'active';
