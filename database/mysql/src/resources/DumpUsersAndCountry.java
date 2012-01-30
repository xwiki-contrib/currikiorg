import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.String;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DumpUsersAndCountry {
    public static void main(String[] args) throws Exception {
        String query = "select stringPropFirst.xws_value as first,\n" +
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
                "  ;";
                
        Connection conn = DriverManager.getConnection(args[0], args[1], args[2]);
        Statement s = conn.createStatement();
        s.execute(query);
        ResultSet rs = s.getResultSet();
        Writer out = new OutputStreamWriter(new FileOutputStream(args[3]),"utf-8");
        int n=0;
        out.write("\"first\",\"last\",\"country\",\"city\",\"xwikiName\"\n");
        while(rs.next()) {
            for(int i : new int[]{1,2,3,4,5}) {
                out.write("\"");
                out.write(rs.getString(i));
                out.write("\"");
                if(i<5) out.write(", ");
            }
            out.write("\n");
            n++;
            if(n%100==0) System.out.println("Outputting " + rs.getString(5) + " (" + n + ").");
        }
        out.flush(); out.close();
    }
}