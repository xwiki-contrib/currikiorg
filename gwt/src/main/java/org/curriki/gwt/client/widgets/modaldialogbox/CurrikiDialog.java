package org.curriki.gwt.client.widgets.modaldialogbox;

import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.gwt.api.client.dialog.DefaultDialog;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 20 aožt 2008
 * Time: 09:58:42
 * To change this template use File | Settings | File Templates.
 */
public class CurrikiDialog extends DefaultDialog {

    public CurrikiDialog() {
        super();
        // Compatibility with older styles
        setStyleName("gwt-ModalDialog");
    }

    public void setCaption(String textOrHtml, boolean asHtml) {
        if (asHtml)
         setHTML(textOrHtml);
        else
         setText(textOrHtml);
    }
}
