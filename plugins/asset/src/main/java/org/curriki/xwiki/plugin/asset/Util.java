package org.curriki.xwiki.plugin.asset;

/**
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
