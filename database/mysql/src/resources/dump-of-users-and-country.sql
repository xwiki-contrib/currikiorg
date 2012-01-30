select stringPropFirst.xws_value as first,
        stringPropLast.xws_value as last,
        stringPropCountry.xws_value as country,
        stringPropCity.xws_value as city,
        userObject.xwo_name as xwikiName
  INTO OUTFILE '/tmp/result.csv'
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' ESCAPED BY '\\' LINES TERMINATED BY '\n'  
   from   xwikistrings as stringPropFirst,
          xwikistrings as stringPropLast,
          xwikistrings as stringPropCountry,
          xwikistrings as stringPropCity,
          xwikiobjects as userObject
   where
      xwo_classname="XWiki.XWikiUsers" and
            userObject.xwo_id=stringPropFirst.xws_id
              and userObject.xwo_id=stringPropLast.xws_id
              and userObject.xwo_id=stringPropCountry.xws_id
              and userObject.xwo_id=stringPropCity.xws_id

              and stringPropFirst.xws_name="first_name"
              and stringPropLast.xws_name="last_name"
              and stringPropCountry.xws_name="country"
              and stringPropCity.xws_name="city"
  order by xwo_name;