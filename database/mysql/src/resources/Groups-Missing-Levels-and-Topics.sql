select obj.xwo_name, obj.xwo_id
 from xwikiobjects obj,
    xwikiproperties pr
 where obj.xwo_id=pr.xwp_id  and obj.xwo_classname="XWiki.CurrikiSpaceClass"
   and (xwp_name='topic' or xwp_name='educationLevel')
   and obj.xwo_name LIKE "Group_%WebPreferences"
   and pr.xwp_id=obj.xwo_id
   and (select count(xwl_id) from xwikilistitems where xwl_id=pr.xwp_id) =0;


select xwl.*
 from xwikiobjects obj, xwikilistitems xwl,
    xwikiproperties pr
 where obj.xwo_id=pr.xwp_id  and obj.xwo_classname="XWiki.CurrikiSpaceClass"
   and (xwp_name='topic' or xwp_name='educationLevel')
   and obj.xwo_name LIKE "Group_%WebPreferences"
   and pr.xwp_id=obj.xwo_id and xwl.xwl_id=pr.xwp_id limit 50;

//    and obj.xwo_name="Group_CheckingCurrentRebuild.WebPreferences"

 insert into xwikilistitems (xwl_id, xwl_name, xwl_value, xwl_number)
   values  (-1810026864, "topic", "FW_masterFramework.WebHome",          0);
 insert into xwikilistitems (xwl_id, xwl_name, xwl_value, xwl_number)
    values  (-1810026864, "topic", "FW_masterFramework.Arts",            1);
 insert into xwikilistitems (xwl_id, xwl_name, xwl_value, xwl_number)
   values  (-1810026864, "educationLevel", "gr-k-2",  0);
    