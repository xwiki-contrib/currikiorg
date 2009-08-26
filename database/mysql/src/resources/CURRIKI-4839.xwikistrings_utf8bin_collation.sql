-- CURRIKI-4839
--   Change xwikistrings to use utf8_bin collation instead of utf8_general_ci

ALTER TABLE xwikistrings MODIFY
  XWS_VALUE VARCHAR(255)
   CHARACTER SET utf8
   COLLATE utf8_bin
;
