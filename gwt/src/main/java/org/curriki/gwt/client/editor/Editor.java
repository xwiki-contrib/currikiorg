package org.curriki.gwt.client.editor;

import asquare.gwt.tk.client.ui.ModalDialog;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.*;
import org.curriki.gwt.client.pages.ComponentsPage;
import org.curriki.gwt.client.pages.EditPage;
import org.curriki.gwt.client.pages.AbstractPage;
import org.curriki.gwt.client.utils.Location;
import org.curriki.gwt.client.utils.WindowUtils;
import org.curriki.gwt.client.widgets.design.Header;
import org.curriki.gwt.client.widgets.modaldialogbox.ModalMsgDialogBox;
import org.curriki.gwt.client.widgets.modaldialogbox.SizeDialogController;
import org.curriki.gwt.client.wizard.AddAssetWizard;
import org.curriki.gwt.client.wizard.CreateCompositeAssetWizard;
import org.curriki.gwt.client.wizard.Wizard;

import java.util.List;

public class Editor implements WindowResizeListener {
    // Current states
    private String rootAssetPageName = null;
    private String currentAssetPageName = null;
    private String selectedDocumentName = null;
    private boolean selectedDocumentEditMode = false;

    // Previous states
    private String previousCurrentAssetPageName = null;
    private String previousSelectedDocumentName = null;

    // Invalidation states
    private boolean isTreeContentInvalid =  true;
    private boolean isCurrentAssetInvalid =  true;

    // Data
    private Document currentAsset;
    private List currentSubAssets;


    // User Interface
    private MenuPanel menuPanel;
    private VerticalPanel appPanel = new VerticalPanel();
    private ScrollPanel scrollAppPanel = new ScrollPanel();
    private Location location;
    private ComponentsPage componentsPage;
    private ModalDialog dialog = null;

    private Timer windowSizeTimer;

    private HTML debugZone = new HTML();

    public void init() {
        // Main init
        location = WindowUtils.getLocation();
        Window.enableScrolling(false);

        VerticalPanel editorPanel = new VerticalPanel();
        Header header = new Header();
        editorPanel.add(header);
        editorPanel.setStyleName("editor-panel");

        FlowPanel mainPanel = new FlowPanel();
        mainPanel.add(debugZone);
        mainPanel.setStyleName("editor-mainpanel");

        menuPanel = new MenuPanel();
        menuPanel.setStyleName("editor-menupanel");
        mainPanel.add(menuPanel);

        scrollAppPanel.setWidget(appPanel);
        // scrollAppPanel.setWidth("100%");
        scrollAppPanel.setStyleName("editor-scrollpanel");
        mainPanel.add(scrollAppPanel);

        editorPanel.add(mainPanel);
        Window.addWindowResizeListener(this);

        componentsPage = new ComponentsPage();
        componentsPage.init();
        appPanel.add(componentsPage);
        RootPanel.get().add(editorPanel);
                                
        String action = getParameter("action");
        if (action != null && action.equals("createCompositeAsset")) {
            String space = getParameter("space");
            if (space == null || space.length() == 0) {
                new ModalMsgDialogBox(Main.getTranslation("editor.app_full_name") , Main.getTranslation("editor.specify_a_space") );
                return;
            }

            launchCreateCompositeAssetWizard(space);
        } else {
            rootAssetPageName = getParameter("page");
            currentAssetPageName = rootAssetPageName;
            // Set the start asset
            setCurrentAssetPageName(currentAssetPageName);
            // Refresh the state
            refreshState();
        }
        // set initial height
        resizeWindow();
        
        // set time to resize all viewports that need it..
        // Schedule the timer to run once every 10 seconds
        String notimer = getParameter("notimer");
        if ((notimer==null)||(notimer.equals("1"))) {
            windowSizeTimer = new Timer() {
                public void run() {
                    resizeWindow();
                    menuPanel.resizeWindow();
                    componentsPage.resizeWindow();
                }
            };
            windowSizeTimer.scheduleRepeating(1000);
        }
    }

    private void resizeWindow() {
        if ((scrollAppPanel!=null)&&(scrollAppPanel.getElement()!=null)) {
            int absoluteTop = Main.getAbsoluteTop(scrollAppPanel);
            if ("1".equals(getParameter("debugscroll"))) {
                if (debugZone!=null) {
                    debugZone.setHTML("Absolute:" + scrollAppPanel.getAbsoluteTop() + "-Scrollpos:" + scrollAppPanel.getScrollPosition() + "-HorScrollPos:" + scrollAppPanel.getHorizontalScrollPosition() + "-offsetheight:" + scrollAppPanel.getOffsetHeight() + "-AbsolutePlusScroll" + absoluteTop + "-Clientheight:" + Window.getClientHeight());
                }
            }
            int newHeight = (Window.getClientHeight() - absoluteTop);
            if (newHeight>0)  {
                try {
                    scrollAppPanel.setHeight(newHeight + "px");
                } catch (Exception e) {
                    // We need to catch this call since in IE7
                    // it seems to break when loading the initial screen                    
                }
            }
        }
    }

    /**
     * State information
     * @return
     */
    public String getRootAssetPageName() {
        return rootAssetPageName;
    }

    /**
     * State information
     * @return
     */
    public String getCurrentAssetPageName() {
        return currentAssetPageName;
    }

    /**
     * State information
     * @return
     */
    public void setCurrentAssetPageName(String currentAssetPageName) {
        this.previousCurrentAssetPageName = this.currentAssetPageName;
        this.currentAssetPageName = currentAssetPageName;
    }

    /**
     * State information
     * @return
     */
    public String getSelectedDocumentName() {
        return selectedDocumentName;
    }

    /**
     * State information
     * @return
     */
    public String getPreviousSelectedDocumentName() {
        return previousSelectedDocumentName;
    }


    /**
     * State information
     * @return
     */
    public void setSelectedDocumentName(String selectedDocumentName) {
        this.previousSelectedDocumentName = this.selectedDocumentName;                
        this.selectedDocumentName = selectedDocumentName;
    }


    /**
     * Checks if the current asset need to be reloaded
     * @return
     */
    public boolean isCurrentAssetInvalid() {
        if (this.isCurrentAssetInvalid)
           return true;
        if (this.currentAssetPageName==null)
           return true;
        if (this.previousCurrentAssetPageName==null)
           return false;
        if (this.previousCurrentAssetPageName.equals(this.currentAssetPageName)) {
           return false;
        }
        return true;
    }

    /**
     * Checks if the tree needs to be reloaded
     * @return
     */
    public boolean isTreeContentInvalid() {
        return this.isTreeContentInvalid;
    }

    /**
     * Forces invalidation of the tree content
     * @param treeContentInvalid
     */
    public void setTreeContentInvalid(boolean treeContentInvalid) {
        this.isTreeContentInvalid = treeContentInvalid;
    }

    /**
     * Forces invalidation of the current asset content
     * @param currentAssetInvalid
     */
    public void setCurrentAssetInvalid(boolean currentAssetInvalid) {
        componentsPage.setCurrentAssetInvalid(getCurrentAssetPageName());
        this.isCurrentAssetInvalid = currentAssetInvalid;
    }

    /**
     * Forces the selected document to go in edit mode
     * @param editMode
     */
    public void setSelectedDocumentEditMode(boolean editMode) {
        this.selectedDocumentEditMode = editMode;
    }

    /**
     * Checks if the selected documents needs to be opened in edit mode
     * @return
     */
    public boolean getSelectedDocumentEditMode() {
        return this.selectedDocumentEditMode;
    }

    /**
     * State helper function
     * @return
     */
    public AbstractPage getSelectedTab() {
        return componentsPage.getSelectedTab();
    }


    /**
     * State helper function
     * @return
     */
    public Document getCurrentAsset() {
        return currentAsset;
    }

    /**
     * State helper function
     * @return
     */
    public List getCurrentSubAssets() {
        return currentSubAssets;
    }

    /**
     * State helper function
     * @return
     */
    public boolean isCurrentAsset(String docName){
        return docName.equals(currentAsset.getFullName());
    }



    /**
     * This function will refresh the state of the editor
     * Launch the necessary reloads and select the appropriate tree items and assets in the view
     */
    public void refreshState() {
        // We need to check if the currently loaded asset is the right one
        // If not we reload it and continue
        if (isCurrentAssetInvalid()) {
            reloadCurrentAsset(new CurrikiAsyncCallback() {
                public void onSuccess(Object result) {
                    super.onSuccess(result);
                    refreshState();
                }
            });
            return;
        }

        // We need to check if the tree content is currently valid
        // If not we reload it
        if (isTreeContentInvalid()) {
            menuPanel.refreshTree(new CurrikiAsyncCallback() {
                public void onSuccess(Object result) {
                    super.onSuccess(result);
                    setTreeContentInvalid(false);
                    refreshState();
                }
            });
        }

        // if current asset is invalid maybe the title has change. let's force updating it.
        try {
            Main.getSingleton().getEditor().updateAssetTitle(getCurrentAssetPageName(), (String) getCurrentAsset().getObject(Constants.ASSET_CLASS).get(Constants.ASSET_TITLE_PROPERTY));
        } catch (Exception e) {
            // silent failure
        }

        // We now refresh the add button and tree state
        menuPanel.refreshState();
        componentsPage.refreshState();
    }

    /**
     * This function will reload the current asset
     */
    public void reloadCurrentAsset(final CurrikiAsyncCallback callback) {
        CurrikiService.App.getInstance().getCompositeAsset(currentAssetPageName,  new CurrikiAsyncCallback() {
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                callback.onFailure(caught);
            }

            public void onSuccess(Object result) {
                super.onSuccess(result);
                // let's mark the asset reloaded
                previousCurrentAssetPageName = null;
                isCurrentAssetInvalid = false;
                List res = (List) result;
                currentAsset = (Document) res.get(0);
                currentSubAssets = (List) res.get(1);
                callback.onSuccess(result);
            }
        });
    }

    /**
     * Make sure a widget in the scroll panel is visible
     * @param obj
     */
    public void ensureVisibleWidget(UIObject obj){
        scrollAppPanel.ensureVisible(obj);
    }

    /**
     * Create Composite Asset Wizard
     * @param space
     */
    public void launchCreateCompositeAssetWizard(String space) {
        CreateCompositeAssetWizard wizard = new CreateCompositeAssetWizard(space);
        wizard.setCloseListener(new CloseDialogListener());
        initDialogBox(wizard);
        dialog.setCaption("Insert", false);
        wizard.setParentCaptionListener(new AsyncCallback() {
            public void onFailure(Throwable caught) {
            }
            public void onSuccess(Object result) {
                dialog.setCaption((String) result, false);
            }
        });

        wizard.setResizeListener(new WindowResizeListener() {
            public void onWindowResized(int width, int height) {
                dialog.getController(SizeDialogController.class).plugIn(dialog);
            }
        });
        dialog.show();
    }

    /**
     * Launch add item wizard
     */
    public void launchInsertWizard() {
        AddAssetWizard wizard = new AddAssetWizard();
        wizard.setCloseListener(new CloseDialogListener());

        initDialogBox(wizard);
        wizard.setParentCaptionListener(new AsyncCallback(){
            public void onFailure(Throwable caught) {
            }
            public void onSuccess(Object result) {
                dialog.setCaption((String) result, false);
            }
        });

        wizard.setResizeListener(new WindowResizeListener() {
            public void onWindowResized(int width, int height) {
                dialog.getController(SizeDialogController.class).plugIn(dialog);
            }
        });

        dialog.setCaption(Main.getTranslation("addasset.title"), false);

        dialog.show();
    }

    private void initDialogBox(Wizard wizard) {
        dialog = new ModalDialog();
        dialog.removeController(dialog.getController(ModalDialog.PositionDialogController.class));
        Panel panel = new ScrollPanel(wizard);
        dialog.addController(new SizeDialogController(panel));
        if (wizard != null)
            dialog.add(panel);
    }

    public String findParent(String currentAssetPageName) {
        return menuPanel.findParent(currentAssetPageName);
    }

    public boolean isAssetInTree(String currentAssetPageName) {
        return menuPanel.isAssetInTree(currentAssetPageName);
    }

    public void updateAssetTitle(String fullName, String title) {
        menuPanel.updateAssetTitle(fullName, title);
    }


    public class CloseDialogListener implements ClickListener {
        public void onClick(Widget sender) {
            dialog.hide();
        }
    }
    
    /**
     * Window display functions
     * @param width
     * @param height
     */
    public void onWindowResized(int width, int height) {
        resizeWindow();
    }


    public void removeAsset(long index) {
        CurrikiService.App.getInstance().removeSubAsset(currentAssetPageName, index, new CurrikiAsyncCallback() {
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                // The action failed but we want to reload anyway in case something happened
                setCurrentAssetInvalid(true);
                setTreeContentInvalid(true);
                refreshState();
            }

            public void onSuccess(Object result) {
                super.onSuccess(result);
                // The action worked we want to reload the current asset and the tree
                setCurrentAssetInvalid(true);
                setTreeContentInvalid(true);
                refreshState();
            }
        });
    }

    /**
     * Allows to reset all the loaded asset info to make sure every tab will reload the info
     */
    public void resetCache() {
        componentsPage.resetCache();
    }

    public String getParameter(String param) {
        return location.getParameter(param);
    }


    public String getCurrentSpace() {
        return currentAssetPageName.substring(0, currentAssetPageName.indexOf("."));
    }

    public boolean isInEditMode(){
        return EditPage.getSingleton().isInEditMode();
    }

}
