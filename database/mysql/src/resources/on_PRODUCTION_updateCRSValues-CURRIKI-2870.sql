SET AUTOCOMMIT=0;
START TRANSACTION;

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 200
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`='P' AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 800
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=3 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 600
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=2 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 400
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=1 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 100
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=0 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

commit;