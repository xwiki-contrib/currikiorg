/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * @author jeremi
 *
 */
package org.curriki.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.i18n.client.Dictionary;
import com.xpn.xwiki.gwt.api.client.User;
import com.xpn.xwiki.gwt.api.client.XWikiGWTException;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.widgets.modaldialogbox.ModalMsgDialogBox;
import org.curriki.gwt.client.widgets.modaldialogbox.NominateDialog;
import org.curriki.gwt.client.widgets.loginpanel.LoginDialogBox;
import org.curriki.gwt.client.utils.WindowUtils;
import org.curriki.gwt.client.utils.Loading;
import org.curriki.gwt.client.utils.Translator;
import org.curriki.gwt.client.wizard.*;
import org.curriki.gwt.client.editor.Editor;
import org.curriki.gwt.client.search.Searcher;
import org.curriki.gwt.client.search.editor.Viewer;
import org.curriki.gwt.client.search.editor.SearchPanel;

import java.util.Map;
import java.util.HashMap;

public class Main implements EntryPoint
{
    private static Main singleton;
    private Translator translator;
    private Loading loading;

    /*************************** Editor *********************/
    private Editor editor;
    private Searcher searcher;

    /*************************** Wizards ********************/
    private AddExistingResourceWizard addExistingWizard;
    private AddFileWizard addFileWizard;
    private AddResourceWizard addResourceWizard;
    private CreateCollectionWizard createCollWizard;
    private CreateCollectionWizard addCollectionWizard;
    private SearchPanel findPopup;
    private AddFromTemplateWizard addFromTemplateWizard;


    private static Map _metaPropertiesMap = null;


    public User getUser() {
        return user;
    }

    private User user = null;

    public static Main getSingleton() {
        return singleton;
    }

    public Editor getEditor() {
        return editor;
    }

    public Translator getTranslator() {
        return translator;
    }
                                                         
    public static String getTranslation(String key) {
        return getSingleton().translator.getTranslation(key);
    }

    public static String getTranslation(String key, String[] args) {
        return getSingleton().translator.getTranslation(key, args);
    }

    public void onModuleLoad() {
        singleton = this;
        String action = WindowUtils.getLocation().getParameter("action");
        String page = WindowUtils.getLocation().getParameter("page");
        String dosearch = WindowUtils.getLocation().getParameter("search");

        if (dosearch == null) {
            // Check if started with search set using javascript like
            // var GWTArguments = { search: "" };
            try {
                Dictionary arguments = Dictionary.getDictionary("GWTArguments");
                if (arguments != null && arguments.get("search") != null){
                    dosearch = arguments.get("search");
                }
            } catch (Exception e){
                // Ignore any exception
            }
        }

        hideLoadingMessage();

        if (dosearch != null){
            // Bring up Site search app
            callSiteAddJSAPI(singleton); // Need to makes sure other GWT links still work

            final String searchArgs = dosearch; // Need a final value for inner class below
            searcher = new Searcher();
            checkTranslator(new AsyncCallback() {
                public void onFailure(Throwable throwable) {
                }
                public void onSuccess(Object object) {
                    Command loadSearcher = new Command(){
                        public void execute(){
                            searcher.init(searchArgs);
                        }
                    };
                    fetchUser(loadSearcher);
                }
            });
        } else  if ((action!=null)||(page!=null)) {
            editor = new Editor();
            checkTranslator(new AsyncCallback() {
                public void onFailure(Throwable throwable) {
                }
                public void onSuccess(Object object) {
                    loadDesignCSS();
                    // We check if user is logged in before lauching the editor
                    checkAnonymous();
                    // initCurrikiLogger();
                }
            });
        } else {
            callSiteAddJSAPI(singleton);
        }
    }

    private void loadDesignCSS(){
        if (WindowUtils.getLocation().getParameter("nodesign") == null){
            Element el = DOM.createElement("link");
            DOM.setAttribute(el, "type", "text/css");
            DOM.setAttribute(el, "rel", "stylesheet");
            DOM.setAttribute(el, "media", "all");
            DOM.setAttribute(el, "href", "/xwiki/bin/view/XWiki/style?xpage=plain");
            DOM.appendChild(RootPanel.get().getElement(), el);
        }

    }

    public void fetchUser(final Command cmd){
        CurrikiService.App.getInstance().getUser(new CurrikiAsyncCallback(){
            public void onSuccess(Object result) {
                super.onSuccess(result);
                user = (User) result;
                if (cmd != null){
                    cmd.execute();
                }
            }
        });
    }

    public void checkAnonymous(){
        Command loadEditor = new Command(){
            public void execute(){
                if (user.getFullName().equals(Constants.USER_XWIKI_GUEST)) {
                    final LoginDialogBox login = new LoginDialogBox();
                    login.init(new AsyncCallback() {
                        public void onFailure(Throwable caught) {
                            login.hide();
                            checkAnonymous();
                        }

                        public void onSuccess(Object result) {
                            login.hide();
                            checkAnonymous();
                        }
                    });
                    login.show();
                    return;
                }
                editor.init();
            }
        };
        fetchUser(loadEditor);
    }

    /*
    private void initCurrikiLogger(){
        GWT.setUncaughtExceptionHandler(new LogError(GWT.getUncaughtExceptionHandler()));
    }

    private class LogError  implements GWT.UncaughtExceptionHandler {
        GWT.UncaughtExceptionHandler uncaughtExceptionHandler;

        public LogError(GWT.UncaughtExceptionHandler e){
            uncaughtExceptionHandler = e;
        }

        public void onUncaughtException(Throwable e) {
            Map errorInfos = new HashMap();
            errorInfos.put("module", GWT.getModuleName());
            errorInfos.put("useragent", Navigator.getUserAgent());
//            errorInfos.put("stacktrace", e.getStackTrace());

            CurrikiService.App.getInstance().logJSError(errorInfos, null);

            uncaughtExceptionHandler.onUncaughtException(e);
        }
    }
    */

    private Loading getLoading() {
        if (loading == null){
               loading = new Loading();
           }
        return loading;
    }

    public void startLoading() {
        getLoading().startLoading();
    }

    public void finishLoading() {
        getLoading().finishLoading();
    }


    public void showError(Throwable caught) {
        if (caught instanceof XWikiGWTException) {
            XWikiGWTException exp = ((XWikiGWTException)caught);
            if (exp.getCode()== 9002) {
                // This is a login error
                new ModalMsgDialogBox("main.curriki", getTranslation("main.login_first"));
            }
            else if (exp.getCode()== 9001) {
                // This is a right error
                new ModalMsgDialogBox("main.curriki", getTranslation("main.missing_rights"));

            } else
                showError("" + exp.getCode(), exp.getFullMessage());
        }
        else
         showError("", caught.getMessage());
    }

    public void showError(String text) {
        showError("", text);
    }

    public void showError(String code, String text) {
        new ModalMsgDialogBox(getTranslation("curriki") + " " + getTranslation("error") + " " + code, text, "error-panel");
    }
           
    /*************************** Site Add code ********************/

    /**
     * Creates the Javascript API interface that can be used by web pages.
     *
     * @param x  An instance of "this" for the callbacks to be made to.
     */
    public native void callSiteAddJSAPI(Main x) /*-{
        $wnd.addCollection = function() {
            x.@org.curriki.gwt.client.Main::addCollection()();
        };
        $wnd.createCollection = function(space, pageName, pageTitle) {
            x.@org.curriki.gwt.client.Main::createCollection(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(space, pageName, pageTitle);
        };
        $wnd.addExistingResource = function(resource) {
            x.@org.curriki.gwt.client.Main::addExistingResource(Ljava/lang/String;)(resource);
        };
        $wnd.addFile = function() {
            x.@org.curriki.gwt.client.Main::addFile()();
        };
        $wnd.addFileToCollection = function(collection) {
            x.@org.curriki.gwt.client.Main::addFile(Ljava/lang/String;)(collection);
        };
        $wnd.addFromTemplate = function() {
            x.@org.curriki.gwt.client.Main::addFromTemplate()();
        };
        $wnd.findPopup = function() {
            x.@org.curriki.gwt.client.Main::findPopup()();
        };
        $wnd.addResourceToCollection = function(collection) {
            x.@org.curriki.gwt.client.Main::addResourceToCollection(Ljava/lang/String;)(collection);
        };
        $wnd.nominateAsset = function(assetName) {
            x.@org.curriki.gwt.client.Main::nominateAsset(Ljava/lang/String;)(assetName);
        };

        $wnd.currikiGWTLoaded = true;
    }-*/;

    /**
     * check if translator is loaded
     *
     * @param cback Where to call back after the translator is loaded.
     */
    public void checkTranslator(AsyncCallback cback) {
        if (translator==null) {
            // We need to disable the loading box
            // otherwise it shows without translations
            getLoading().disable();
            translator = new Translator();
            translator.init(cback);
            getLoading().enable();
        } else {
            // We need to make sure call back is sent
            if (cback!=null)
             cback.onSuccess(null);
        }
    }

    /**
     * Start the add wizard to add an existing resource to a collection.
     *
     * @param resourceName  Full wikiname of page to add.
     */
    public void addExistingResource(final String resourceName){
        checkTranslator(new AsyncCallback() {
            public void onFailure(Throwable throwable) {
            }
            public void onSuccess(Object object) {
                addExistingWizard = new AddExistingResourceWizard();
                addExistingWizard.setCompletionCallback(new Command() {
                    public void execute(){
                        reloadWindow();
                    }
                });
                addExistingWizard.addExistingResource(resourceName);
            }
        });
    }

    /**
     * Create a collection in the specified space, with the specified pageName and pageTitle.
     *
     * @param space      Space to create the collection in.
     * @param pageName   Page name of the collection.
     * @param pageTitle  Title of the collection.
     */
    public void createCollection(final String space, final String pageName, final String pageTitle){
        checkTranslator(new AsyncCallback() {
            public void onFailure(Throwable throwable) {
            }
            public void onSuccess(Object object) {
                createCollWizard = new CreateCollectionWizard();
                createCollWizard.createCollection(space, pageName, pageTitle);
            }
        });
    }

    /**
     * Start the add wizard to add a web link or file to a collection.
     */
    public void addFile(){
        checkTranslator(new AsyncCallback() {
            public void onFailure(Throwable throwable) {
            }
            public void onSuccess(Object object) {
                addFileWizard = new AddFileWizard();
                addFileWizard.setCompletionCallback(new Command() {
                    public void execute(){
                        reloadWindow();
                    }
                });
                addFileWizard.addFile();
            }
        });
    }

    /**
     * Start the add wizard to add a some resource to a collection.
     *
     * @param collection Full page name of the collection
     */
    public void addResourceToCollection(final String collection){
        checkTranslator(new AsyncCallback() {
            public void onFailure(Throwable throwable) {
            }
            public void onSuccess(Object object) {
                addResourceWizard = new AddResourceWizard();
                addResourceWizard.addResource(collection);
            }
        });
    }

    /**
     * Start nomination dialog
     * @param assetName asset full name
     */
    public void nominateAsset(final String assetName) {
        checkTranslator(new AsyncCallback() {
            public void onFailure(Throwable throwable) {
            }
            public void onSuccess(Object object) {
                new NominateDialog(assetName, new AsyncCallback() {
                    public void onFailure(Throwable throwable) {
                    }
                    public void onSuccess(Object object) {
                        reloadWindow();
                    }
                });

                CurrikiService.App.getInstance().getUser(new CurrikiAsyncCallback(){
                    public void onSuccess(Object result) {
                        super.onSuccess(result);
                        user = (User) result;
                    }
                });

            }
        });
    }

    /**
     * Start the add wizard to add a web link or file to a collection.
     * 
     * @param collection Full page name of the collection
     */
    public void addFile(final String collection){
        checkTranslator(new AsyncCallback() {
            public void onFailure(Throwable throwable) {
            }
            public void onSuccess(Object object) {
                addFileWizard = new AddFileWizard();
                addFileWizard.setCompletionCallback(new Command() {
                    public void execute(){
                        reloadWindow();
                    }
                });
                addFileWizard.addFile(collection);
            }
        });
    }

    /**
     * Start the add wizard to add a collection.
     */
    public void addCollection(){
        checkTranslator(new AsyncCallback() {
            public void onFailure(Throwable throwable) {
            }
            public void onSuccess(Object object) {
                addCollectionWizard = new CreateCollectionWizard();
                addCollectionWizard.addCollection();
            }
        });
    }

    /**
     * Start the add wizard to add a template to a collection.
     */
    public void addFromTemplate(){
        checkTranslator(new AsyncCallback() {
            public void onFailure(Throwable throwable) {
            }
            public void onSuccess(Object object) {
                addFromTemplateWizard = new AddFromTemplateWizard();
                addFromTemplateWizard.addFromTemplate();
            }
        });
    }

    /**
     * Start the add wizard to add a template to a collection.
     * 
     * @param collection Full page name for Collection to add the template to
     */
    public void addFromTemplate(final String collection){
        checkTranslator(new AsyncCallback() {
            public void onFailure(Throwable throwable) {
            }
            public void onSuccess(Object object) {
                addFromTemplateWizard = new AddFromTemplateWizard();
                addFromTemplateWizard.addFromTemplate(collection);
            }
        });
    }

    /**
     * Start the "advanced" find
     */
    public void findPopup(final String collectionName){
        checkTranslator(new AsyncCallback() {
            public void onFailure(Throwable throwable) {
            }
            public void onSuccess(Object object) {
                Command showSearch = new Command(){
                    public void execute(){
                        Viewer viewer = new Viewer(){
                            public void displayView(Document asset)
                            {
                                changeWindowHref(asset.getViewURL());
                            }
                        };

                        AddExistingResourceWizard addWizard = new AddExistingResourceWizard(collectionName);
                        addWizard.setCompletionCallback(new Command() {
                            public void execute(){
                                reloadWindow();
                            }
                        });
                        findPopup = new SearchPanel(addWizard, viewer);
                        findPopup.show();
                    }
                };
                fetchUser(showSearch);
            }
        });
    }

    public void findPopup() {
        findPopup(null);
    }

    public static native void changeWindowHref(String url) /*-{
        $wnd.location.href = url;
    }-*/;

    public static native void reloadWindow() /*-{
        $wnd.location.reload();
    }-*/;

    public static String makeLinksExternal(String content) {
        content = content.replaceAll("<([aA].*[hH][rR][eE][fF].*)>", "<$1 target=\"blank\">");
        return content;
    }

    private static native String getUserAgent() /*-{
        return navigator.userAgent.toString();
    }-*/;

    public static boolean isMSIE() {
        return (getUserAgent().indexOf("MSIE")!=-1);
    }

    public static boolean isGecko() {
        return (getUserAgent().indexOf("Gecko")!=-1)&&(!isSafari());
    }

    public static boolean isSafari() {
        return (getUserAgent().indexOf("AppleWebKit")!=-1);
    }

    public static int getAbsoluteTop(ScrollPanel panel) {
       return panel.getAbsoluteTop();
       /*  if (isMSIE())
            return panel.getAbsoluteTop();
        else
            return panel.getAbsoluteTop() + panel.getScrollPosition();
       */
    }

    private static native Map getMetaProperties(Map map) /*-{
      var metas = $wnd.document.getElementsByTagName("meta");
      var n = metas.length;
      for (var i = 0; i < n; ++i) {
         var meta = metas[i];
         var name = meta.getAttribute("name");
         if (name && name == "gwt:property") {
            var content = meta.getAttribute("content");
            if (content) {
                var name = content;
                var value = "";
                var eq = content.indexOf("=");
                if (eq != -1) {
                    name  = content.substring(0, eq);
                    value = content.substring(eq+1);
                }
                map.@java.util.Map::put(Ljava/lang/Object;Ljava/lang/Object;)(name,value);
            }
         }
      }
      return map;
   }-*/;

    /**
     * Native method in JavaScript to access gwt:property
     */
    public static String getProperty(String name) {
      if (_metaPropertiesMap == null) {
          _metaPropertiesMap = getMetaProperties(new HashMap());
      }
      return (String) _metaPropertiesMap.get(name);
    };


    /**
     * Native method in JavaScript to turn off the pre-GWT loading message
     */
    public static native void hideLoadingMessage() /*-{
        if ($wnd.hideLoadingMsg) $wnd.hideLoadingMsg();
    }-*/;

    public static String getHelpURL() {
        String helpURL = getProperty("helpurl");
        if ((helpURL==null)||(helpURL.equals("")))
         helpURL = Constants.HELP_URL;
        return helpURL;
    }
}
