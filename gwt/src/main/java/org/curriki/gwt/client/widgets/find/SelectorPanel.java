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
 * @author dward
 *
 */
package org.curriki.gwt.client.widgets.find;

import com.google.gwt.user.client.ui.*;
import org.curriki.gwt.client.utils.Translator;
import org.curriki.gwt.client.Main;

public class SelectorPanel extends HorizontalPanel implements Selector {
//    FlexTable g = new FlexTable();
    TermSelector terms = new TermSelector();
    SubjectSelector subject = new SubjectSelector();
    LevelSelector level = new LevelSelector();
    TypeSelector type = new TypeSelector();
    Searchable resultPanel = null;
    
    public SelectorPanel() {
    }

    public void init(Searchable results, ClickListener cancelListener) {
        resultPanel = results;

        addStyleName("find-selector");

//        Document doc = new Document();
//        XObject obj = doc.getObject(Constants.ASSET_CLASS);

        KeyboardListener enterPressed = new KeyboardListenerAdapter() {
            public void onKeyPress(Widget sender, char keyCode, int modifiers){
                if (keyCode == KeyboardListener.KEY_ENTER){
                    findPressed(sender);
                }
            }
        };
        VerticalPanel termPanel = new VerticalPanel();
        termPanel.addStyleName("find-matchterms");
        // termPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        termPanel.add(new Label(Main.getTranslation("find.matchterms")+":"));
        termPanel.add(terms);
        terms.addKeyboardListener(enterPressed);

        VerticalPanel subjectPanel = new VerticalPanel();
        termPanel.addStyleName("find-subject");
        // subjectPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        subjectPanel.add(new Label(Main.getTranslation("find.subject")+":"));
        subjectPanel.add(subject);
//        subjectPanel.add(createEditor(obj, "fw_items", null));
        subject.addKeyboardListener(enterPressed);

        VerticalPanel levelPanel = new VerticalPanel();
        termPanel.addStyleName("find-level");
        // levelPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        levelPanel.add(new Label(Main.getTranslation("find.level")+":"));
        levelPanel.add(level);
//        levelPanel.add(createEditor(obj, "educational_level", null));
        level.addKeyboardListener(enterPressed);

        VerticalPanel typePanel = new VerticalPanel();
        termPanel.addStyleName("find-type");
        // typePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        typePanel.add(new Label(Main.getTranslation("find.type")+":"));
        typePanel.add(type);
//        typePanel.add(createEditor(obj, Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY, null));
        type.addKeyboardListener(enterPressed);

        Button find = new Button(Main.getTranslation("editor.btt_find"), new ClickListener() {
            public void onClick(Widget sender) {
                findPressed(sender);
            }
        });
        find.addStyleName("find-selector-find");

        Button cancel = new Button(Main.getTranslation("editor.btt_cancel"), cancelListener);
        cancel.addStyleName("find-selector-cancel");

        setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        add(termPanel);
        add(subjectPanel);
        add(levelPanel);
        add(typePanel);
        add(find);
        add(cancel);
    }

    public void findPressed(Widget sender){
        resultPanel.search(this);
    }

    public String[] getChosen(){
        String[] chosen = new String[4];

        chosen[0] = terms.getText();
        chosen[1] = subject.getValue(subject.getSelectedIndex());
        chosen[2] = level.getValue(level.getSelectedIndex());
        chosen[3] = type.getValue(type.getSelectedIndex());

        return chosen;
    }
}
