package org.curriki.xwiki.plugin.curriki;

import com.xpn.xwiki.XWikiException;

/**
 */
public class CurrikiException extends XWikiException {
    public static final int MODULE_PLUGIN_CURRIKI = 100;

    public static final int ERROR_CURRIKI_UNKNOWN = 100;

    public CurrikiException(int module, int code, String message, Throwable e, Object[] args) {
        super(module, code, message, e, args);
    }

    public CurrikiException(int module, int code, String message, Throwable e) {
        super(module, code, message, e);
    }

    public CurrikiException(int module, int code, String message) {
        super(module, code, message);
    }

    public CurrikiException(int code, String message, Throwable e) {
        super(MODULE_PLUGIN_CURRIKI, code, message, e);
    }

    public CurrikiException(int code, String message) {
        super(MODULE_PLUGIN_CURRIKI, code, message);
    }

    public CurrikiException(String message, Throwable e) {
        this(MODULE_PLUGIN_CURRIKI, ERROR_CURRIKI_UNKNOWN, message, e);
    }

    public CurrikiException(String message) {
        this(MODULE_PLUGIN_CURRIKI, ERROR_CURRIKI_UNKNOWN, message);
    }

    public CurrikiException(Throwable e) {
        this(MODULE_PLUGIN_CURRIKI, ERROR_CURRIKI_UNKNOWN, "Asset Plugin Exception", e);
    }

    public CurrikiException() {
        this(MODULE_PLUGIN_CURRIKI, ERROR_CURRIKI_UNKNOWN, "Curriki Plugin Exception");
    }
}
