package org.curriki.xwiki.plugin.asset;

/**
 * User: ludovic
 * Date: 10 févr. 2009
 * Time: 19:39:18
 */
public class Util {
    /**
     * Escape text to be used in a JS call in an HTML tag
     * @param origtext
     * @return
     */
    public static String escapeForJS(String origtext) {
        String text = origtext.replaceAll("\\\\", "\\\\");
        text = text.replaceAll("'", "\\\\'");
        text = text.replaceAll("\"", "\\\\x22");
        return text;
    }    
}
