package org.gelc.xwiki.plugins.framework;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.plugin.PluginException;
import com.xpn.xwiki.api.Api;

import java.io.InputStream;
import java.util.List;
import java.util.Vector;

public class FrameworkManagerPluginAPI  extends Api {
    private FrameworkManagerPlugin frameworkManagerPlugin;

    public FrameworkManagerPluginAPI(FrameworkManagerPlugin frameworkManagerPlugin, XWikiContext context) {
        super(context);
        this.frameworkManagerPlugin = frameworkManagerPlugin;
    }

    public Framework getFramework(String frameworkName) throws PluginException {
        return frameworkManagerPlugin.getFramework(frameworkName, context);
    }

    public boolean importFramework(String name, byte[] xmlContent){
        return frameworkManagerPlugin.importFramework(name, xmlContent, context);
    }

    public boolean importFramework(String name, InputStream xmlStream) throws XWikiException {
        return frameworkManagerPlugin.importFramework(name, xmlStream, context);
    }

    public boolean setParent(FrameworkItem itemChild, FrameworkItem itemParent){
        return false;
    }

    public List getChildren(FrameworkItem item) throws XWikiException {
        return frameworkManagerPlugin.getChildren(item, context);
    }

    public List getChildren(Framework framework) throws XWikiException {
        return frameworkManagerPlugin.getChildren(framework, context);
    }

    public boolean removeItem(FrameworkItem itemChild){
        return removeItem(itemChild, false);
    }

    /**
     * return the path to access to the item
     * it starts by the Framework and finish by the specified FrameworkItem
     * @param item
     * @return
     * @throws XWikiException
     */
    public Vector getPath(FrameworkItem item) throws XWikiException {
        return frameworkManagerPlugin.getPath(item, context);
    }


    /**
     * if forced is at true, it remove all the children
     * @param itemChild
     * @param forced
     * @return
     */
    public boolean removeItem(FrameworkItem itemChild, boolean forced){
        return false;
    }

    public boolean testIntegrity(Framework framework){
        return false;
    }

    public boolean testIntegrity(String frameworkName){
        return frameworkManagerPlugin.testIntegrity(frameworkName, context);
    }

    
}
