package org.curriki.xwiki.plugin.asset;

import com.xpn.xwiki.XWikiException;

/**
 */
public class AssetException extends XWikiException {
    public static final int MODULE_PLUGIN_ASSET = 101;

    public static final int ERROR_ASSET_UNKNOWN = 100;
    public static final int ERROR_ASSET_FORBIDDEN = 101;
    public static final int ERROR_ASSET_NOT_FOUND = 102;
    public static final int ERROR_ASSET_INCOMPATIBLE = 103;
    public static final int ERROR_ASSET_SUBASSET_NOTFOUND = 104;
    public static final int ERROR_ASSET_SUBASSET_EXISTS = 105;
    public static final int ERROR_ASSET_SUBASSET_RECURSION = 106;
    public static final int ERROR_ASSET_REORDER_NOTMATCH = 107;

    public AssetException(int module, int code, String message, Throwable e, Object[] args) {
        super(module, code, message, e, args);
    }

    public AssetException(int module, int code, String message, Throwable e) {
        super(module, code, message, e);
    }

    public AssetException(int module, int code, String message) {
        super(module, code, message);
    }

    public AssetException(int code, String message, Throwable e) {
        super(MODULE_PLUGIN_ASSET, code, message, e);
    }

    public AssetException(int code, String message) {
        super(MODULE_PLUGIN_ASSET, code, message);
    }

    public AssetException(String message, Throwable e) {
        this(MODULE_PLUGIN_ASSET, ERROR_ASSET_UNKNOWN, message, e);
    }

    public AssetException(String message) {
        this(MODULE_PLUGIN_ASSET, ERROR_ASSET_UNKNOWN, message);
    }

    public AssetException(Throwable e) {
        this(MODULE_PLUGIN_ASSET, ERROR_ASSET_UNKNOWN, "Asset Plugin Exception", e);
    }

    public AssetException() {
        this(MODULE_PLUGIN_ASSET, ERROR_ASSET_UNKNOWN, "Asset Plugin Exception");
    }
}
