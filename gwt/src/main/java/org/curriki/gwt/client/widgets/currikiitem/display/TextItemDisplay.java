/*
 * Copyright 2006, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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
 */
package org.curriki.gwt.client.widgets.currikiitem.display;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.jpavel.gwt.wysiwyg.client.Editor;
import com.xpn.xwiki.gwt.api.client.Document;
import com.xpn.xwiki.gwt.api.client.XObject;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.widgets.currikiitem.CurrikiItem;

import java.util.Map;
import java.util.HashMap;

public class TextItemDisplay extends AbstractItemDisplay implements WindowResizeListener {
    final static String TINYMCE_FIELDNAME = "contentfield";
    
    TextArea textarea = null;
    Editor editor = null;
    HTML viewLabel;
    String wysiwygtemplate = "XWiki.GWTTextAreaWyswiyg";
    String wysiwyghtml;

    public TextItemDisplay(Document doc, CurrikiItem item) {
        super(doc, item);
        initDisplay(doc);
        Window.addWindowResizeListener(this);
    }

    public String getType() {
        return Constants.TYPE_TEXT;
    }

    private native void addMCEControl(Element e, String imagepath, String attachpath) /*-{
          $wnd.tinyMCE.settings["wiki_images_path"] = imagepath;
          $wnd.tinyMCE.settings["wiki_attach_path"] = attachpath;
          $wnd.tinyMCE.addMCEControl(e, e.id);
          // $wnd.tinyMCE.settings["wiki_images_path"] = imagepath;
          // $wnd.tinyMCE.settings["wiki_attach_path"] = attachpath;
      }-*/;

    private native String getContent() /*-{
          return $wnd.tinyMCE.getContent();
      }-*/;

    private native String getContent(String elid) /*-{
          return $wnd.tinyMCE.getContent(elid);
      }-*/;

    private void initWYSIWYG(String text){
        editor = new Editor();
        // editor.setWidth("100%");
        // editor.setHeight("300px");
        editor.setHTML(text);
        editor.addStyleName("text-editor-wysiwyg");
        panel.add(editor);
        Main.getSingleton().getEditor().ensureVisibleWidget(editor);
    }

    private void initVerbatim(final String text){
        /*
        if (wysiwyghtml==null) {
            Map params = new HashMap();
            params.put("editor", "content");
            params.put("page", getDocumentFullName());
            CurrikiService.App.getInstance().getDocumentContent("XWiki.GWTTextArea", true, params, new CurrikiAsyncCallback() {

                public void onSuccess(Object object) {
                    super.onSuccess(object);
                    if (object!=null) {
                        wysiwyghtml = object.toString();
                        Window.alert(wysiwyghtml);
                        initVerbatim(text);
                    }
                }
            });
        } else {
        */
        textarea = new TextArea();
        textarea.setText((text==null) ? "" : text);
        // textarea.setHeight("300px");
        // textarea.setWidth("100%");
        textarea.setStyleName("text-editor");
        textarea.setName(TINYMCE_FIELDNAME);
        panel.add(textarea);
        textarea.setFocus(true);
        String imagepath = "/xwiki/bin/download/" + getDocumentFullName().replace('.', '/') + "/";
        String attachpath = "/xwiki/bin/view/" + getDocumentFullName().replace('.', '/');
        addMCEControl(textarea.getElement(), imagepath, attachpath);
        Main.getSingleton().getEditor().ensureVisibleWidget(textarea);
        // }
    }

    public void initDisplay(Document doc){
        panel.clear();

        if (viewLabel==null) {
            viewLabel = new HTML() {
                public void onBrowserEvent(Event event) {
                    item.onBrowserEvent(event);
                }
            };
            viewLabel.setWordWrap(true);
            viewLabel.sinkEvents(Event.ONCLICK | Event.ONDBLCLICK);
        }

        XObject obj = doc.getObject(Constants.TEXTASSET_CLASS);
        if (obj.get(Constants.TEXTASSET_TYPE_PROPERTY) == null || ((Long)obj.get(Constants.TEXTASSET_TYPE_PROPERTY)).longValue() == 0)
            viewLabel.setHTML(obj.getViewProperty(Constants.TEXTASSET_TEXT_PROPERTY));
        else
            viewLabel.setHTML((String) obj.getProperty(Constants.TEXTASSET_TEXT_PROPERTY));

        if(panel.getWidgetIndex(viewLabel) == -1)
            panel.add(viewLabel);
        panel.setStyleName("item-panel-text");
        item.refreshItemInfos();
        item.switchHeaderButtonsToViewMode();
    }

    public void changeToEditMode() {
        if(status != Constants.EDIT) {
            switchToEdit();
        }
    }

    public void cancelEditMode() {
        // we remove the previous widget
        panel.remove(0);
        CurrikiService.App.getInstance().unlockDocument(doc.getFullName(), new CurrikiAsyncCallback(){

            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);
                panel.add(viewLabel);
                status = Constants.VIEW;
            }

            public void onSuccess(Object object) {
                super.onSuccess(object);
                panel.add(viewLabel);
                status = Constants.VIEW;
            }
        });        
    }

    public boolean save() {
        XObject obj = doc.getObject(Constants.TEXTASSET_CLASS);
        String text;
        if (textarea != null) {
            text = getContent();
        }
        else {
            editor.getEditorWYSIWYG().toggleView();
            text = editor.getHTML();
        }
        obj.set(Constants.TEXTASSET_TEXT_PROPERTY, text);

        panel.remove(0);

       CurrikiService.App.getInstance().saveObject(obj, new CurrikiAsyncCallback(){
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);
                panel.add(viewLabel);
                status = Constants.VIEW;
            }

            public void onSuccess(Object object) {
                super.onSuccess(object);
                reloadDocument();
                status = Constants.VIEW;
            }
        });
        return true;
    }



    private void switchToEdit(){
        switchToEdit(false);
    }

    private void switchToEdit(boolean force){
        // we remove the previous widget
        panel.clear();
        
        status = Constants.EDIT;
        CurrikiService.App.getInstance().lockDocument(doc.getFullName(), force, new CurrikiAsyncCallback(){

            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);
                panel.add(viewLabel);
                status = Constants.VIEW;
            }

            public void onSuccess(Object object) {
                super.onSuccess(object);
                if (object != null && ((Boolean)object).booleanValue()) {
                    XObject obj = doc.getObject(Constants.TEXTASSET_CLASS);
                    if (obj.get(Constants.TEXTASSET_TYPE_PROPERTY) == null || ((Long)obj.get(Constants.TEXTASSET_TYPE_PROPERTY)).longValue() == 0)
                        initVerbatim((String) obj.get(Constants.TEXTASSET_TEXT_PROPERTY));
                    else
                        initWYSIWYG((String) obj.get(Constants.TEXTASSET_TEXT_PROPERTY));
                }
                else {
                    panel.add(viewLabel);
                    if (Window.confirm(Main.getTranslation("asset.asset_locked_force_edit"))){
                        switchToEdit(true);
                    }
                }
            }
        });

    }

    public void onView() {
        
    }



    public void onDocumentVersionChange() {
        if (status == Constants.VIEW){
            reloadDocument();
        }
    }

    public void onWindowResized(int i, int i1) {
        if (textarea!=null) {
            String text = getContent();
            panel.remove(0);
            initVerbatim(text);
        }
    }
}
