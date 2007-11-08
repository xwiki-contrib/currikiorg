package org.curriki.gwt.client.widgets.currikiitem.display;

import com.xpn.xwiki.gwt.api.client.Document;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.Event;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.Constants;

/**
 * Displayer for Protected Assets
 * These assets are not viewable by the user but it's still possible to remove them or move them
 * Edit and Information are not available
 */
public class ProtectedItemDisplay extends AbstractItemDisplay {

    public ProtectedItemDisplay(Document doc, CurrikiItem item) {
        super(doc, item);
        initDisplay(doc);
    }

    public String getType() {
        return Constants.TYPE_PROTECTED;
    }

    public void changeToEditMode() {
    }

    public void cancelEditMode() {
    }

    public boolean save() {
        return true;
    }

    public void onView() {
    }

    public void onDocumentVersionChange() {
    }

    /**
     * Shows what the users sees for this asset
     * @param doc
     */
    public void initDisplay(Document doc) {
        Label label = new HTML(Main.getTranslation("asset.assetprivate")){
            public void onBrowserEvent(Event event) {
                item.onBrowserEvent(event);
            }
        };
        label.sinkEvents(Event.ONCLICK | Event.ONDBLCLICK);

        panel.add(label);
        item.refreshItemInfos();
        item.switchHeaderButtonsToViewMode();
    }
}
