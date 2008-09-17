--
--Update old status values by the new values
--
﻿SET AUTOCOMMIT=0;
START TRANSACTION;

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 10
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`='P' AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 20
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=3 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 40
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=2 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 60
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=1 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

UPDATE xwikistrings x
SET x.`XWS_VALUE`= 80
WHERE x.`XWS_NAME`='status' AND x.`XWS_VALUE`=0 AND x.`XWS_ID` IN
(SELECT xo.`XWO_ID`
FROM xwikiobjects xo
WHERE xo.`XWO_CLASSNAME` = 'CRS.CurrikiReviewStatusClass');

commit;