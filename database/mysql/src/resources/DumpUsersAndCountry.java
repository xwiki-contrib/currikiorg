import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.Class;
import java.lang.String;
import java.lang.System;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class DumpUsersAndCountry {
    
    public static String[] fieldNames = {"userName",
            "first_name","last_name", "country","city"};

    public static void main(String[] args) throws Exception {
        /* String query = "select stringPropFirst.xws_value as first,\n" +
                "        stringPropLast.xws_value as last,\n" +
                "        stringPropCountry.xws_value as country,\n" +
                "        stringPropCity.xws_value as city,\n" +
                "        userObject.xwo_name as xwikiName\n" +
                //"  INTO OUTFILE '/tmp/result.csv'\n" +
                //"  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' ESCAPED BY '\\\\' LINES TERMINATED BY '\\n'  \n" +
                "   from   xwikistrings as stringPropFirst,\n" +
                "          xwikistrings as stringPropLast,\n" +
                "          xwikistrings as stringPropCountry,\n" +
                "          xwikistrings as stringPropCity,\n" +
                "          xwikiobjects as userObject\n" +
                "   where\n" +
                "      xwo_classname=\"XWiki.XWikiUsers\" and\n" +
                "            userObject.xwo_id=stringPropFirst.xws_id\n" +
                "              and userObject.xwo_id=stringPropLast.xws_id\n" +
                "              and userObject.xwo_id=stringPropCountry.xws_id\n" +
                "              and userObject.xwo_id=stringPropCity.xws_id\n" +
                "\n" +
                "              and stringPropFirst.xws_name=\"first_name\"\n" +
                "              and stringPropLast.xws_name=\"last_name\"\n" +
                "              and stringPropCountry.xws_name=\"country\"\n" +
                "              and stringPropCity.xws_name=\"city\"\n" +
                "  ;"; */
        String query = "select stringProp.xws_value as prop,\n" +
                "        stringProp.xws_name as propName,\n" +
                "        userObject.xwo_name as xwikiName\n" +
                "   from   xwikistrings as stringProp,\n" +
                "          xwikiobjects as userObject\n" +
                "   where\n" +
                "      xwo_classname=\"XWiki.XWikiUsers\" and\n" +
                "            userObject.xwo_id=stringProp.xws_id\n" +
                "        and (    stringProp.xws_name =\"first_name\"\n" +
                "              or stringProp.xws_name =\"last_name\"\n" +
                "              or stringProp.xws_name =\"country\"\n" +
                "              or stringProp.xws_name =\"city\")  \n" +
                "           ORDER BY userObject.xwo_name";

        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(args[0], args[1], args[2]);
        Statement s = conn.createStatement();
        s.execute(query);
        ResultSet rs = s.getResultSet();
        Writer out = new OutputStreamWriter(new FileOutputStream(args[3]),"utf-8");
        int n=0;
        out.write("\"first\",\"last\",\"country\",\"city\",\"xwikiName\"\n");
        String userName = null, firstName, lastName, country, city;
        boolean tryMore = true;
        tryMore = rs.next();
        HashMap p = new HashMap(5);
        

        while(tryMore) {
            userName = rs.getString(3);
            p.put("userName", userName);
            while(userName.equals(rs.getString(3))) {
                //System.out.println(rs.getString(2) + " : " + rs.getString(1));
                p.put(rs.getString(2), rs.getString(1));
                tryMore = rs.next();
                if(!tryMore) break;
            }
            //System.out.println("Got map: " + p);

            int i=1;
            for(String fieldName: fieldNames) {
                out.write("\"");
                String value= (String) p.get(fieldName);
                if(value==null) value = "";
                out.write(value);
                out.write("\"");
                if(i<5) out.write(", ");
                i++;
            }
            out.write("\"");
            if(n%100==0) System.out.println("Outputting " + userName + " (" + n + ").");
            userName = null;
            p.clear();
            out.write("\n");
            n++;
        }
        out.flush(); out.close();
    }
}