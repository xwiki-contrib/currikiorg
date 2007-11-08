package org.curriki.gwt.client.widgets.currikiitem.display;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.gwt.api.client.Attachment;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.TreeListItem;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArchiveItemDisplay extends AttachementItemDisplay {
    protected Button bttShowHideTree;
    protected Button bttLaunchArchive;

    protected SimplePanel treePanel;
    protected Tree tree;
    protected boolean treeVisible = false;
    protected TreeListener treeListener;
    protected String launchFile = "";
    protected TreeItem launchFileTreeItem;
    protected boolean editMode = false;

    public ArchiveItemDisplay(Document doc, CurrikiItem item) {
        super(doc, item);
    }

    public String getType() {
        return Constants.TYPE_ATTACHMENT;
    }

    public void initDisplay(Document doc) {
        panel.clear();
        initDisplayDescription(doc);
        displayArchive(doc);
        item.refreshItemInfos();
        item.switchHeaderButtonsToViewMode();
    }

    private void displayArchive(Document doc) {
        editMode = false;
        HorizontalPanel hpanel = new HorizontalPanel();
        hpanel.add(new HTML(Main.getTranslation("metadata.description_title")));
        VerticalPanel vpanel = new VerticalPanel();
        hpanel.add(vpanel);
        String desc = "";
        XObject assetObj = doc.getObject(Constants.ASSET_CLASS);
        if (assetObj!=null) {
            desc = assetObj.getViewProperty(Constants.ASSET_DESCRIPTION_PROPERTY);
        }
        vpanel.add(new HTML(desc));

        if (doc.getAttachments().size() > 0) {
            initLaunchFile();
            initTreeButtons(vpanel);
            panel.add(hpanel);
            resetTreeState();
        }
        initDisplayAttachment(doc);
    }

    /**
     * This functions allows to reset the state of the tree in case
     * the editing was cancelled
     */
    private void resetTreeState() {
        if (tree!=null) {
            for (int i=0;i<tree.getItemCount();i++) {
                resetTreeState(tree.getItem(i));
            }
        }
    }

    private void resetTreeState(TreeItem treeItem) {
        if (launchFile.equals("/"+treeItem.getUserObject())) {
            treeItem.setSelected(true);
            tree.setSelectedItem(treeItem, false);
            launchFileTreeItem = treeItem;
        }
        else
            treeItem.setSelected(false);
        for (int i=0;i<treeItem.getChildCount();i++) {
            resetTreeState(treeItem.getChild(i));
        }
    }

    protected void initLaunchFile() {
        if ((launchFile==null)||(launchFile.equals(""))) {
            XObject archiveObj = doc.getObject(Constants.MIMETYPE_ARCHIVE_CLASS);
            if (archiveObj!=null) {
                launchFile = (String) archiveObj.get(Constants.MIMETYPE_ARCHIVE_DEFAULT_FILE_PROPERTY);
                if ((launchFile != null) && !launchFile.equals("") && !launchFile.startsWith("/")){
                    launchFile = "/" + launchFile;
                }
            }
    }
}
    protected void initEdit() {
        editMode = true;
        panel.clear();
        HorizontalPanel hpanel = new HorizontalPanel();
        panel.add(hpanel);
        hpanel.add(new HTML(Main.getTranslation("asset.description")));
        VerticalPanel vpanel = new VerticalPanel();
        hpanel.add(vpanel);
        String desc = "";
        XObject assetObj = doc.getObject(Constants.ASSET_CLASS);
        if (assetObj!=null) {
         desc = (String) assetObj.get(Constants.ASSET_DESCRIPTION_PROPERTY);
        }
        initLaunchFile();

        descBox = new TextArea();
        descBox.setText(desc);
        vpanel.add(descBox);

        if (doc.getAttachments().size() > 0) {
            initTreeButtons(vpanel);
        } else {
            initDisplayCorruptedAsset();
        }
        
        initEditAttachment();
    }

    private void initTreeButtons(VerticalPanel vpanel) {
        if (treePanel==null)
          treePanel = new SimplePanel();

        HorizontalPanel treeButtonsPanel = new HorizontalPanel();
        treeButtonsPanel.addStyleName("item-archive-treebuttons");
        vpanel.add(treeButtonsPanel);
        treePanel.addStyleName("item-archive-tree");
        vpanel.add(treePanel);

        String buttonText = (treeVisible) ? Main.getTranslation("asset.archive.hidetree") : Main.getTranslation("asset.archive.showtree"); 
        bttShowHideTree = new Button(buttonText, new ClickListener() {
            public void onClick(Widget widget) {
                if (!treeVisible) {
                    initDisplayFileTree();
                    treeVisible = true;
                    bttShowHideTree.setHTML(Main.getTranslation("asset.archive.hidetree"));
                } else {
                    treePanel.setVisible(false);
                    treeVisible = false;
                    bttShowHideTree.setHTML(Main.getTranslation("asset.archive.showtree"));
                }
            }
        });
        bttShowHideTree.addStyleName("gwt-ButtonGrey");
        bttShowHideTree.addStyleName("gwt-bttShowhide");
        treeButtonsPanel.add(bttShowHideTree);

        if (!editMode) {
            bttLaunchArchive = new Button(Main.getTranslation("asset.archive.launcharchive"), new ClickListener() {
                public void onClick(Widget widget) {
                    String url = getAttURL() + launchFile;
                    Window.open(url, "_blank", "");
                }
            });
            bttLaunchArchive.addStyleName("gwt-ButtonGrey");
            bttLaunchArchive.addStyleName("gwt-bttLaunchArchive");
            treeButtonsPanel.add(bttLaunchArchive);
        }else {
            HTML launchFileHelp = new HTML(Main.getTranslation("asset.archive.launchfilehelp"));
            launchFileHelp.setStyleName("item-archive-launchfilehelp");
            treeButtonsPanel.add(launchFileHelp);
        }
    }

    /**
     * Get the tree of files and displays it
     */
    private void initDisplayFileTree() {
        if (treePanel.getWidget()==null) {
            CurrikiService.App.getInstance().getFileTreeList(doc.getFullName(), ((Attachment)doc.getAttachments().get(0)).getFilename(), new CurrikiAsyncCallback() {
                public void onFailure(Throwable caught) {
                    super.onFailure(caught);
                    treePanel.setVisible(true);
                    treeVisible = true;
                }
                public void onSuccess(Object result) {
                    super.onSuccess(result);
                    List items = (List) result;
                    treePanel.add(makeTree(items));
                    treePanel.setVisible(true);
                    treeVisible = true;
                }
            });

        } else {
            treePanel.setVisible(true);
        }
    }

    private void initTreeListener() {
        // Only run if tree is loaded
        if (tree!=null) {
                if (treeListener==null) {
                    treeListener = new TreeListener() {
                        public void onTreeItemSelected(TreeItem treeItem) {
                            if (editMode) {
                                treeItem.setSelected(true);
                            } else {
                                treeItem.setSelected(false);
                                if (launchFileTreeItem!=null) {
                                  launchFileTreeItem.setSelected(true);
                                  tree.setSelectedItem(launchFileTreeItem, false);
                                }
                                Window.open(getAttURL() + "/" + treeItem.getUserObject(), "_blank", "");
                            }
                        }
                        public void onTreeItemStateChanged(TreeItem treeItem) {
                        }
                    };
                    tree.addTreeListener(treeListener);
                }
        }
    }

    private Widget makeTree(List items) {
        if (tree==null) {
            Map itemList = new HashMap();
            tree = new Tree();
            for (int i=0;i<items.size();i++) {
                TreeListItem item = (TreeListItem) items.get(i);
                boolean selected = launchFile.equals("/"+item.getId());
                TreeItem treeItem = new TreeItem(item.getValue());
                treeItem.setUserObject(item.getId());
                // Select the item that is the active launch file
                if (selected) {
                    treeItem.setSelected(true);
                    tree.setSelectedItem(treeItem, false);
                    launchFileTreeItem = treeItem;
                }
                itemList.put(item.getId(), treeItem);
                TreeItem parentItem = (TreeItem) itemList.get(item.getParent());
                if (parentItem!=null) {
                    parentItem.addItem(treeItem);
                }
                else {
                    tree.addItem(treeItem);
                }
            }
        }
        initTreeListener();
        tree.ensureSelectedItemVisible();
        return tree;
    }

    public boolean saveArchiveInformations(){
        XObject assetObj = doc.getObject(Constants.ASSET_CLASS);
        String desc = descBox.getText();
        assetObj.set(Constants.ASSET_DESCRIPTION_PROPERTY, desc);

        launchFileTreeItem = (tree==null) ? null : tree.getSelectedItem();
        launchFile = (launchFileTreeItem==null) ? "" : "/" + (String) launchFileTreeItem.getUserObject();

        XObject archiveObj = doc.getObject(Constants.MIMETYPE_ARCHIVE_CLASS);
        if (archiveObj==null) {
            archiveObj = new XObject();
            archiveObj.setName(doc.getFullName());
            archiveObj.setClassName(Constants.MIMETYPE_ARCHIVE_CLASS);
            archiveObj.setNumber(0);
        }
        archiveObj.set(Constants.MIMETYPE_ARCHIVE_DEFAULT_FILE_PROPERTY, launchFile);

        List objs = new ArrayList();
        objs.add(assetObj);
        objs.add(archiveObj);
        CurrikiService.App.getInstance().saveObjects(objs, new CurrikiAsyncCallback(){

            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);
            }

            public void onSuccess(Object object) {
                super.onSuccess(object);
                if (!saveUpload()) {
                    reloadDocument();
                    status = Constants.VIEW;
                }
            }
        });
        return true;
    }

    public boolean save() {
        return saveArchiveInformations();
    }

}
