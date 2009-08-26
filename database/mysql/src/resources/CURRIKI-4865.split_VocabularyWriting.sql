-- CURRIKI-4865
--  Split FW_masterFramework.VocabularyWriting into two
--  and giving all items with it both the new items

-- xwikilistitems contains the value
--  - need to add the additional value with XWL_NUMBER being 1 greater than
--    the highest used for that XWL_ID,XWL_NAME combination

INSERT INTO xwikilistitems (XWL_ID, XWL_NAME, XWL_VALUE, XWL_NUMBER)
  SELECT XWL_ID,
         XWL_NAME,
        'FW_masterFramework.FLWriting' AS 'XWL_VALUE',
        ( SELECT MAX(XWL_NUMBER)+1
            FROM xwikilistitems s
           WHERE s.XWL_ID = l.XWL_ID
             AND s.XWL_NAME = l.XWL_NAME ) AS 'XWL_NUMBER'
    FROM xwikilistitems l
   WHERE XWL_VALUE = 'FW_masterFramework.VocabularyWriting';
