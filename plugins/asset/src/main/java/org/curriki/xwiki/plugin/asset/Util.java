package org.curriki.xwiki.plugin.asset;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 10 févr. 2009
 * Time: 19:39:18
 * To change this template use File | Settings | File Templates.
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
        text = text.replaceAll("\"", "\\x22");
        return text;
    }    
}
