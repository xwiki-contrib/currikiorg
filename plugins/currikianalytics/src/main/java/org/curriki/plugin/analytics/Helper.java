package org.curriki.plugin.analytics;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class provides some static helpers which are used across the modules
 * mainly providing functionality that is needed here and there.
 */
public class Helper {

    private static final Logger LOG = LoggerFactory.getLogger(Helper.class);

    /**
     * Read lines from a XWiki Page
     *
     * @param path the path to a resource (e.g.: Coll_Admin/resource1)
     * @param context the XWikiContext to be able to fetch documents.
     * @return a List of Strings with one entry for each line (ignoring empty lines and lines beginning with ##)
     */
    public static List<String> getLinesOfPage(String path, XWikiContext context){
        List<String> lines = new LinkedList<String>();
        try {
            XWikiDocument doc = context.getWiki().getDocumentFromPath(path, context);
            LOG.warn("Loaded lines from page " + doc.toString());
            String[] readLines = doc.getContent().split("\n");
            for(String line: readLines){
                if(line != null){
                    line = line.trim();
                    if(!("".equals(line)) && !(line.startsWith("##"))){
                        lines.add(line);
                        LOG.warn(line);
                    }
                }
            }
        } catch (XWikiException e) {
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * Produce a list of pattern for matching of regular expression
     * from a given list of strings.
     *
     * @param strings A list of strings
     * @return A list if Patterns generated from teh given strings.
     */
    public static List<Pattern> compileStringsToPatterns(List<String> strings){
        List<Pattern> patterns = new LinkedList<Pattern>();
        if(strings!= null){
            for(String string: strings){
                patterns.add(Pattern.compile(string));
            }
        }
        return patterns;
    }

    /**
     * Read a config from a xwiki page. Some modules in this plugin will need
     * configurable options. We current store these options in pages looking like that
     *
     * ## comment
     * key1=value1
     * key2=value2
     *
     * @param path the path to the xwiki page to read the config from
     * @param context the XWikiContext to be able to fetch documents.
     * @return A map of key value pairs with all read configuration entries
     */
    public static Map<String, String> loadConfigFromPage(String path, XWikiContext context){
        Map<String, String> configValues = new HashMap<String, String>();
        List<String> lines = getLinesOfPage(path,context);
        for(String line : lines){
            String[] keyValuePair = line.split("=");
            if(keyValuePair.length == 2 && keyValuePair[0] != null && keyValuePair[1] != null){
                configValues.put(keyValuePair[0],keyValuePair[1]);
            }
        }
        return configValues;
    }
}