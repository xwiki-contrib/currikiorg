package org.curriki.gwt.client.widgets.template;

import asquare.gwt.tk.client.ui.BasicPanel;
import asquare.gwt.tk.client.ui.ModalDialog;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.Constants;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.utils.XWikiGWTPanelLoader;
import org.curriki.gwt.client.utils.Translator;

import java.util.List;

public class ChooseTemplateDialog extends ModalDialog {
    AsyncCallback nextCallback;
    String parentAsset;
    VerticalPanel templateButtonPanel = new VerticalPanel();
    VerticalPanel help = new VerticalPanel();
    SimplePanel helpHeader = new SimplePanel();
    SimplePanel helpContent = new SimplePanel();

    public ChooseTemplateDialog(String parentAsset, AsyncCallback nextCallback) {
        this.nextCallback = nextCallback;
        this.parentAsset = parentAsset;
        // We load the templates
        CurrikiService.App.getInstance().getTemplates(new CurrikiAsyncCallback() {
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                init(null);
            }

            public void onSuccess(Object result) {
                super.onSuccess(result);
                init((List) result);
            }
        });
    }

    public void init(List templates){
        addStyleName("dialog-choosetemplate");
        setCaption(Main.getTranslation("template.create_a_learning_resource"), false);

        BasicPanel main = new BasicPanel();
        main.addStyleName("dialog-choosetemplate-content");

        BasicPanel text = new BasicPanel();
        text.addStyleName("dialog-choosetemplate-text");
        XWikiGWTPanelLoader.loadWikiPage(Constants.DIALOG_CHOOSE_TEMPLATE, text);

        Grid bottom = new Grid(3, 2);
        bottom.addStyleName("dialog-choosetemplate-bottom");
        bottom.getColumnFormatter().addStyleName(0, "choosetemplate-dialog-col1");
        bottom.getColumnFormatter().addStyleName(1, "choosetemplate-dialog-col2");

        BasicPanel selectText = new BasicPanel();
        selectText.addStyleName("dialog-choosetemplate-chooser");
        HTML sText = new HTML(Main.getTranslation("template.select_a_template_style"));
        sText.addStyleName("dialog-choosetemplate-chooser-title");
        selectText.add(sText);

        HTML sText2 = new HTML(Main.getTranslation("template.select_a_template_style_subtitle"));
        sText.addStyleName("dialog-choosetemplate-chooser-subtitle");
        selectText.add(sText2);

        helpHeader.addStyleName("help-head");
        helpContent.addStyleName("help-content");
        help.addStyleName("help");
        bottom.setWidget(1,1, help);
        
        // Here we should add the different templates buttons..
        // We need to read this from the server

        if (templates!=null) {
            bottom.setWidget(1,0, initTemplates(templates));
        }
        else {
            Window.alert(Main.getTranslation("template.error_while_getting_templates"));
        }

        VerticalPanel actions = new VerticalPanel();
        actions.addStyleName("dialog-choosetemplate-actions");

        ClickListener cancelListener = new ClickListener(){
            public void onClick(Widget sender){
                nextCallback.onFailure(null);
            }
        };
        Button cancel = new Button(Main.getTranslation("editor.btt_cancel"), cancelListener);
        cancel.addStyleName("dialog-cancel");
        cancel.addStyleName("dialog-choosetemplate-cancel");
        actions.add(cancel);

        bottom.setWidget(0, 0, selectText);
        bottom.setWidget(2, 1, actions);
        main.add(text);
        main.add(bottom);
        add(main);
        show();
    }

    private Panel initTemplates(List templates) {
         for (int i=0;i<templates.size();i++) {
            TemplateInfo template = (TemplateInfo) templates.get(i);
            // New resource: File of Link
            WhichTemplateButton button = new WhichTemplateButton(template, new ClickListener() {
                public void onClick(Widget widget) {
                    onTemplateClick(widget);
                }
            });
            templateButtonPanel.add(button);
            if (i==0)
             button.showDescription();
        }  
        return templateButtonPanel;
    }

    public void onTemplateClick(Widget widget) {
        if (widget instanceof WhichTemplateButton) {
            WhichTemplateButton button = (WhichTemplateButton) widget;
            insertTemplate(button.getTemplate());
        }

    }

    private void insertTemplate(TemplateInfo template) {
        TemplateCopier copier = new TemplateCopier();
        copier.copyTemplate(template, parentAsset, nextCallback);
    }

    private class WhichTemplateButton extends Button {
        TemplateInfo template;

        public WhichTemplateButton(TemplateInfo template, ClickListener callback){
            super(template.getTitle(), callback);
            this.template = template;
            addStyleName("gwt-ButtonNav");
            sinkEvents(Event.ONMOUSEOVER);
        }

        public TemplateInfo getTemplate() {
            return template;
        }

        public void onBrowserEvent(Event event) {
            switch (DOM.eventGetType(event)) {
                case Event.ONMOUSEOVER:
                    setActive(this);
                    showDescription();
                    break;
            }

            super.onBrowserEvent(event);
        }

        public void showDescription(){
            help.clear();
            HTMLPanel textPanel = new HTMLPanel(template.getTitle());
            textPanel.setStyleName("help-content-text");
            help.add(textPanel);
            String imageURL = template.getImageURL();
            Image image = new Image();
            image.setStyleName("help-content-image");
            image.setTitle(template.getTitle());
            // image.setWidth("200px");
            // image.setHeight("175px");
            if ((imageURL!=null)&&(!imageURL.equals(""))) {
             image.setUrl(imageURL);
            } else {
             image.setUrl(Main.getTranslation("params.iconsurl") + Main.getTranslation("params.template.defaultimageurl"));   
            }
            help.add(image);
            HTMLPanel descPanel = new HTMLPanel(template.getDescription());
            descPanel.setStyleName("help-content-description");
            help.add(descPanel);
        }
    }

    private void setActive(Button button) {
        for (int i=0;i<templateButtonPanel.getWidgetCount();i++) {
            Widget widget = templateButtonPanel.getWidget(i);
            if (widget instanceof WhichTemplateButton)
                widget.removeStyleName("gwt-ButtonNav-active");
        }
        button.addStyleName("gwt-ButtonNav-active");
    }
}
