-- 
-- SQL script to fix CURRIKI-1362
--
-- Change all "collection" composite assets to be "curriki_document"
-- if the composite asset does not appear as a sub assetpage in a
-- "root_collection" composite asset.
--

UPDATE xwikistrings SET XWS_VALUE='curriki_document'
 WHERE XWS_ID IN
  (SELECT t3.id FROM
   (SELECT t2.id FROM
    (SELECT s.XWS_ID id
       FROM xwikistrings s, xwikiobjects o
      WHERE o.XWO_CLASSNAME = 'XWiki.CompositeAssetClass'
            AND o.XWO_ID = s.XWS_ID
            AND s.XWS_NAME = 'type'
            AND s.XWS_VALUE = 'collection'
            AND o.XWO_NAME NOT LIKE 'AssetTemp.%'
            AND o.XWO_NAME NOT LIKE 'Coll_Templates.%'
            AND o.XWO_NAME NOT IN
             (SELECT s2.XWS_VALUE
                FROM xwikistrings s2, xwikiobjects o2,
                     xwikistrings s3, xwikiobjects o3
               WHERE s2.XWS_ID = o2.XWO_ID
                     AND s2.XWS_NAME = 'assetpage'
                     AND o2.XWO_CLASSNAME = 'XWiki.SubAssetClass'
                     AND o3.XWO_NAME = o2.XWO_NAME
                     AND o3.XWO_ID = s3.XWS_ID
                     AND o3.XWO_CLASSNAME = 'XWiki.CompositeAssetClass'
                     AND s3.XWS_VALUE = 'root_collection'
             )
    ) t2
   ) t3
  )
