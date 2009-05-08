 -- Counts of resources by type (and subtype?)

 -- Need re-usable query to get all counts of assets of each type, and if possible by sub-types e.g. all videos and within that Viditalk vs. .mov vs. .mpg

SELECT a.c as 'Category', a.t as 'Sub-Type', a.count as 'Count'
FROM (

SELECT c1.XWS_VALUE c, '~Total~' as t, count(*) 'count'
  FROM
    xwikistrings c1,
    xwikiobjects obj,
    xwikidoc doc
 WHERE
        obj.XWO_CLASSNAME = 'CurrikiCode.AssetClass'
    AND obj.XWO_NAME = doc.XWD_FULLNAME
    AND doc.XWD_WEB NOT IN ('AssetTemp', 'Coll_Templates')
    AND doc.XWD_NAME NOT IN ('WebHome', 'WebPreferences', 'SpaceIndex', 'MyCollectios', 'Favorites')
    AND c1.XWS_ID = obj.XWO_ID
    AND c1.XWS_NAME = 'category'
 GROUP BY
    c1.XWS_VALUE

UNION

-- Attachments (all but collection, text)
SELECT c1.XWS_VALUE c, a2.XWS_VALUE t, count(*) 'count'
  FROM
    xwikistrings c1,
    xwikiobjects obj,
    xwikiobjects a1,
    xwikistrings a2,
    xwikidoc doc
 WHERE
        obj.XWO_CLASSNAME = 'CurrikiCode.AssetClass'
    AND obj.XWO_NAME = doc.XWD_FULLNAME
    AND doc.XWD_WEB NOT IN ('AssetTemp', 'Coll_Templates')
    AND doc.XWD_NAME NOT IN ('WebHome', 'WebPreferences', 'SpaceIndex', 'MyCollectios', 'Favorites')
    AND c1.XWS_ID = obj.XWO_ID
    AND c1.XWS_NAME = 'category'
    AND c1.XWS_VALUE NOT IN ('collection', 'text')
    AND a1.XWO_NAME = obj.XWO_NAME
    AND a1.XWO_CLASSNAME = 'CurrikiCode.AttachmentAssetClass'
    AND a2.XWS_ID = a1.XWO_ID
    AND a2.XWS_NAME = 'file_type'
 GROUP BY
    c1.XWS_VALUE, a2.XWS_VALUE

UNION

-- Collections
SELECT 'collection' as c, a2.XWS_VALUE t, count(*) 'count'
  FROM
    xwikistrings c1,
    xwikiobjects obj,
    xwikiobjects a1,
    xwikistrings a2,
    xwikidoc doc
 WHERE
        obj.XWO_CLASSNAME = 'CurrikiCode.AssetClass'
    AND obj.XWO_NAME = doc.XWD_FULLNAME
    AND doc.XWD_WEB NOT IN ('AssetTemp', 'Coll_Templates')
    AND doc.XWD_NAME NOT IN ('WebHome', 'WebPreferences', 'SpaceIndex', 'MyCollectios', 'Favorites')
    AND c1.XWS_ID = obj.XWO_ID
    AND c1.XWS_NAME = 'category'
    AND c1.XWS_VALUE = 'collection'
    AND a1.XWO_NAME = obj.XWO_NAME
    AND a1.XWO_CLASSNAME = 'CurrikiCode.CompositeAssetClass'
    AND a2.XWS_ID = a1.XWO_ID
    AND a2.XWS_NAME = 'type'
 GROUP BY
    a2.XWS_VALUE

UNION

-- Text
SELECT 'text' as c, a2.XWS_VALUE t, count(*) 'count'
  FROM
    xwikistrings c1,
    xwikiobjects obj,
    xwikiobjects a1,
    xwikistrings a2,
    xwikidoc doc
 WHERE
        obj.XWO_CLASSNAME = 'CurrikiCode.AssetClass'
    AND obj.XWO_NAME = doc.XWD_FULLNAME
    AND doc.XWD_WEB NOT IN ('AssetTemp', 'Coll_Templates')
    AND doc.XWD_NAME NOT IN ('WebHome', 'WebPreferences', 'SpaceIndex', 'MyCollectios', 'Favorites')
    AND c1.XWS_ID = obj.XWO_ID
    AND c1.XWS_NAME = 'category'
    AND c1.XWS_VALUE = 'text'
    AND a1.XWO_NAME = obj.XWO_NAME
    AND a1.XWO_CLASSNAME = 'CurrikiCode.TextAssetClass'
    AND a2.XWS_ID = a1.XWO_ID
    AND a2.XWS_NAME = 'syntax'
 GROUP BY
    a2.XWS_VALUE

UNION

-- Video (partner - viditalk)
SELECT 'video' as c, a2.XWS_VALUE t, count(*) 'count'
  FROM
    xwikistrings c1,
    xwikiobjects obj,
    xwikiobjects a1,
    xwikistrings a2,
    xwikidoc doc
 WHERE
        obj.XWO_CLASSNAME = 'CurrikiCode.AssetClass'
    AND obj.XWO_NAME = doc.XWD_FULLNAME
    AND doc.XWD_WEB NOT IN ('AssetTemp', 'Coll_Templates')
    AND doc.XWD_NAME NOT IN ('WebHome', 'WebPreferences', 'SpaceIndex', 'MyCollectios', 'Favorites')
    AND c1.XWS_ID = obj.XWO_ID
    AND c1.XWS_NAME = 'category'
    AND c1.XWS_VALUE = 'video'
    AND a1.XWO_NAME = obj.XWO_NAME
    AND a1.XWO_CLASSNAME = 'CurrikiCode.VideoAssetClass'
    AND a2.XWS_ID = a1.XWO_ID
    AND a2.XWS_NAME = 'partner'
 GROUP BY
    a2.XWS_VALUE
) a
ORDER BY
  a.c, a.t
