-- Sanity checks for xwiki database

-- PAGE NAMES
-- checking for data with bad characters in document name
-- NOTE: Removed for now -- Some pages are valid with spaces
--select XWD_FULLNAME 'Bad characters in XWD_FULLNAME' from xwikidoc where (XWD_FULLNAME like '% %' or XWD_FULLNAME like '%?%' or XWD_FULLNAME like '%+%' or XWD_FULLNAME like '%\%%' or XWD_FULLNAME like '%&%' or XWD_FULLNAME like '%#%'); 

-- OBJECTS
-- checking for objects where the document does not exist anymore
select XWO_NAME 'Object exists, but no document does - xwikiobjects.XWO_NAME' from xwikiobjects where XWO_NAME not in (select XWD_FULLNAME from xwikidoc);

-- ATTACHMENTS
-- checking for attachment where the document does not exist
select XWA_DOC_ID 'Attachment exists for no document - xwikiattachment.XWA_DOC_ID' from xwikiattachment where XWA_DOC_ID not in (select XWD_ID from xwikidoc);
-- checking for attachment_archive where the attachment does not exist anymore
select XWA_ID 'Attachment archive exists for no attachment - xwikiattachment_archive.XWA_ID' from xwikiattachment_archive where XWA_ID not in (select XWA_ID from xwikiattachment);
-- checking for attachment_content where the attachment does not exist anymore
select XWA_ID 'Attachment content exists for no attachment - xwikiattachment_content.XWA_ID' from xwikiattachment_content where XWA_ID not in (select XWA_ID from xwikiattachment);
-- checking for attachment with no attachment archive
select XWA_ID 'No archive for attachment - xwikiattachment.XWA_ID' from xwikiattachment where XWA_ID not in (select XWA_ID from xwikiattachment_archive);
-- checking for attachment with no attachment content
select XWA_ID 'No content for attachment - xwikiattachment.XWA_ID' from xwikiattachment where XWA_ID not in (select XWA_ID from xwikiattachment_content);

-- COMMENTS
-- checking for comments where the object does not exist anymore
select XWC_ID 'Comments where no object exists - xwikicomments.XWC_ID' from xwikicomments where XWC_ID not in (select XWO_ID from xwikiobjects);

-- LINKS
-- Cheking for data in links that does not exist in documents anymore
select XWL_DOC_ID 'Link exists where no document does - xwikilinks.XWL_DOC_ID' from xwikilinks where XWL_DOC_ID not in (select XWD_ID from xwikidoc);

-- PREFERENCES
-- checking for preferences where the object does not exist anymore
select XWP_ID 'Preferences where no object exists - xwikipreferences.XWP_ID' from xwikipreferences where XWP_ID not in (select XWO_ID from xwikiobjects);

-- PROPERTIES
-- This query should return no value. Returned values are properties left over for deleted objects
select XWP_ID 'Properties where no object exists - xwikiproperties.XWP_ID' from xwikiproperties where xwp_id not in (select xwo_id from xwikiobjects);


-- Data types:
--  Foreach data type:
--   1. data record with no object record (caught by checking for properties w/ no object record)
--   2. property record w/ no data record
--   3. data record with no property record
--   4. data record in another data table (wrong table as per property record)
-- All have XWx_ID, XWx_NAME columns to match with


-- Does the data record exist (property record does), in right table (item 2 above)
SELECT p.XWP_ID 'Property Record with no data record - xwikiproperties.XWP_ID', p.XWP_CLASSTYPE, p.XWP_NAME
  FROM xwikiproperties p
WHERE (
  CASE p.XWP_CLASSTYPE
   WHEN 'com.xpn.xwiki.objects.StringProperty'
    THEN (SELECT 1 FROM xwikistrings d1 WHERE d1.XWS_ID = p.XWP_ID AND d1.XWS_NAME = p.XWP_NAME)
   WHEN 'com.xpn.xwiki.objects.IntegerProperty'
    THEN (SELECT 1 FROM xwikiintegers d2 WHERE d2.XWI_ID = p.XWP_ID AND d2.XWI_NAME = p.XWP_NAME)
   WHEN 'com.xpn.xwiki.objects.LargeStringProperty'
    THEN (SELECT 1 FROM xwikilargestrings d3 WHERE d3.XWL_ID = p.XWP_ID AND d3.XWL_NAME = p.XWP_NAME)
   WHEN 'com.xpn.xwiki.objects.DBStringListProperty'
    THEN (SELECT 1 FROM xwikilists d4 WHERE d4.XWL_ID = p.XWP_ID AND d4.XWL_NAME = p.XWP_NAME)
   WHEN 'com.xpn.xwiki.objects.LongProperty'
    THEN (SELECT 1 FROM xwikilongs d5 WHERE d5.XWL_ID = p.XWP_ID AND d5.XWL_NAME = p.XWP_NAME)
   WHEN 'com.xpn.xwiki.objects.FloatProperty'
    THEN (SELECT 1 FROM xwikifloats d6 WHERE d6.XWF_ID = p.XWP_ID AND d6.XWF_NAME = p.XWP_NAME)
   WHEN 'com.xpn.xwiki.objects.StringListProperty'
    THEN (SELECT 1 FROM xwikilargestrings d7 WHERE d7.XWL_ID = p.XWP_ID AND d7.XWL_NAME = p.XWP_NAME)
   WHEN 'com.xpn.xwiki.objects.DateProperty'
    THEN (SELECT 1 FROM xwikidates d8 WHERE d8.XWS_ID = p.XWP_ID AND d8.XWS_NAME = p.XWP_NAME)
   WHEN 'com.xpn.xwiki.objects.DoubleProperty'
    THEN (SELECT 1 FROM xwikidoubles d9 WHERE d9.XWD_ID = p.XWP_ID AND d9.XWD_NAME = p.XWP_NAME)
   ELSE
     NULL
   END
) IS NULL;

--                                       AND EXISTS (SELECT 1 FROM xwikilistitems d42 WHERE d42.XWL_ID = p.XWP_ID AND d42.XWL_NAME = p.XWP_NAME))

-- Does a data record exist where a property record does not (item 3)
SELECT id, name, class FROM
(
SELECT XWS_ID id, XWS_NAME name, '^com.xpn.xwiki.objects.StringProperty$' class FROM xwikistrings
UNION
SELECT XWI_ID id, XWI_NAME name, '^com.xpn.xwiki.objects.IntegerProperty$' class FROM xwikiintegers
UNION
SELECT XWL_ID id, XWL_NAME name, '^(com.xpn.xwiki.objects.LargeStringProperty|com.xpn.xwiki.objects.StringListProperty)$' class FROM xwikilargestrings
UNION
SELECT XWL_ID id, XWL_NAME name, '^com.xpn.xwiki.objects.DBStringListProperty$' class FROM xwikilists
UNION
SELECT XWL_ID id, XWL_NAME name, '^com.xpn.xwiki.objects.LongProperty$' class FROM xwikilongs
UNION
SELECT XWF_ID id, XWF_NAME name, '^com.xpn.xwiki.objects.FloatProperty$' class FROM xwikifloats
UNION
SELECT XWS_ID id, XWS_NAME name, '^com.xpn.xwiki.objects.DateProperty$' class FROM xwikidates
UNION
SELECT XWD_ID id, XWD_NAME name, '^com.xpn.xwiki.objects.DoubleProperty$' class FROM xwikidoubles
) vtbl
WHERE NOT EXISTS (SELECT 1 FROM xwikiproperties p WHERE p.XWP_ID = id AND p.XWP_NAME = name AND p.XWP_CLASSTYPE REGEXP class);


-- Data exists in more than one table for a field
SELECT id, name FROM
(
SELECT XWS_ID id, XWS_NAME name FROM xwikistrings
UNION
SELECT XWI_ID id, XWI_NAME name FROM xwikiintegers
UNION
SELECT XWL_ID id, XWL_NAME name FROM xwikilargestrings
UNION
SELECT XWL_ID id, XWL_NAME name FROM xwikilists
UNION
SELECT XWL_ID id, XWL_NAME name FROM xwikilongs
UNION
SELECT XWF_ID id, XWF_NAME name FROM xwikifloats
UNION
SELECT XWS_ID id, XWS_NAME name FROM xwikidates
UNION
SELECT XWD_ID id, XWD_NAME name FROM xwikidoubles
) vtbl
GROUP BY id, name
HAVING count(*) > 1;

-- Checking for value in xwikilistitems and not in xwikilists
select XWL_ID 'Exists in xwikilistitems and not in xwikilists - xwikilistitems.XWL_ID' from xwikilistitems where XWL_ID not in (select XWL_ID from xwikilists);
--  xwikilistsitems is more tricky.. invalid data would be a missing item like data for number 1 and not for number 0
SELECT XWL_ID 'Bad order for list items - xwikilistitems.XWL_ID' FROM `xwikilistitems` x1 WHERE x1.XWL_NUMBER != 0 and not exists (select 1 from `xwikilistitems` x2 WHERE x2.XWL_ID = x1.XWL_ID AND x2.XWL_NAME=x1.XWL_NAME AND x2.XWL_NUMBER = (x1.XWL_NUMBER - 1));

