-- xwikidoc "History (Local) " occurs in 2nd 3rd XWD_FULLNAME, XWD_NAME
update xwikidoc
    set xwd_fullname="FW_masterFramework.History Local",
        xwd_name="History Local"
        where xwd_fullname like"FW%istory Local%";
-- TODO: also update xwd_id as per
--   http://xwiki.475771.n2.nabble.com/how-to-rename-of-page-with-a-trailing-space-td5941324.html
        
-- proof        
select xwd_fullname from xwikidoc where xwd_name like"%istory Local%";

-- xwikilistitems "History (Local) " occurs in 3rd   XWL_VALUE
update xwikilistitems  set XWL_VALUE="FW_masterFramework.History Local"
        where xwl_value like"%istory Local%";
-- proof
select * from xwikilistitems where xwl_value like"%istory Local%";

-- xwikiobjects "History (Local) " occurs in 3rd XWO_NAME
update xwikiobjects  set XWO_NAME="FW_masterFramework.History Local"
        where xwo_name like"%istory Local%";
-- proof
select * from xwikiobjects where xwo_name like"%istory Local%";


