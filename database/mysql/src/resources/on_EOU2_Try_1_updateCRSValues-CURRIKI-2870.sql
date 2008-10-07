SET AUTOCOMMIT=0;
START TRANSACTION;

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 200
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=10 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 800
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=20 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 600
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=40 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 400
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=60 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 100
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=80 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 700
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=30 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 500
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=50 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 300
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=70 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');


commit;