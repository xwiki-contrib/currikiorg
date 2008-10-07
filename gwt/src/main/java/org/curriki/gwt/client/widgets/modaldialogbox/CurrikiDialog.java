package org.curriki.gwt.client.widgets.modaldialogbox;

import asquare.gwt.tk.client.ui.GlassPanel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.xpn.xwiki.gwt.api.client.dialog.DefaultDialog;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 20 ao�t 2008
 * Time: 09:58:42
 * To change this template use File | Settings | File Templates.
 */
public class CurrikiDialog extends DefaultDialog {
	private static GlassPanel glassPanel = new GlassPanel();
	// since we don't want more than one glassPanel displayed at one time and 
	//multiple dialogs can be displayed at one time, we'll remove the glasspanel 
	//only when no dialog needs it anymore
	private static int gpCount = 0;
	//know if this dialog is shown or not
	private boolean isShown = false;
	
    public CurrikiDialog() {
    	// create a non-autohide dialog, modal
        super(false, true);
        // Compatibility with older styles
        setStyleName("gwt-ModalDialog");
    }

    public void setCaption(String textOrHtml, boolean asHtml) {
        if (asHtml)
         setHTML(textOrHtml);
        else
         setText(textOrHtml);
    }
    
    public void show() {
    	if (!isShown) {
	    	if (glassPanel != null) {
	    		if (gpCount == 0) {
	    			glassPanel.show();
	    		}
	        	gpCount++;
	    	}
    	}
    	super.show();
    	isShown = true;
    }
    
    public void hide() {
    	super.hide();
    	if (isShown) {
	    	if (glassPanel != null) {
	    		gpCount--;
	    		if (gpCount == 0) {
	    			glassPanel.hide();	
	    		}
	    	}
    	}
    	isShown = false;
    }
}
