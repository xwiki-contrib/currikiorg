/*
 * Copyright 2007, The Global Education and Learning Community,
 * and individual contributors as indicated by the contributors.txt.
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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ListBox;

public class TypeSelector extends ListBox {
    public TypeSelector() {
        Element select = this.getElement();

        Element option = createOption("Any", "");
        DOM.appendChild(select, option);


        Element optgroup = createGroup("Activity");
        option = createOption("Exercise", "activity_exercise");
        DOM.appendChild(optgroup, option);
        option = createOption("Experiment/Lab", "activity_lab");
        DOM.appendChild(optgroup, option);
        option = createOption("Graphic Organizer/Worksheet", "activity_worksheet");
        DOM.appendChild(optgroup, option);
        option = createOption("Problem Set", "activity_problemset");
        DOM.appendChild(optgroup, option);
        DOM.appendChild(select, optgroup);

        optgroup = createGroup("Book");
        option = createOption("Fiction", "book_fiction");
        DOM.appendChild(optgroup, option);
        option = createOption("Non-Fiction", "book_nonfiction");
        DOM.appendChild(optgroup, option);
        option = createOption("Readings/Excerpts", "book_readings");
        DOM.appendChild(optgroup, option);
        option = createOption("Text Book", "book_textbook");
        DOM.appendChild(optgroup, option);
        DOM.appendChild(select, optgroup);

        optgroup = createGroup("Curriculum");
        option = createOption("Assessment/Test", "curriculum_assessment");
        DOM.appendChild(optgroup, option);
        option = createOption("Full Course", "curriculum_course");
        DOM.appendChild(optgroup, option);
        option = createOption("Lesson Plan", "curriculum_lp");
        DOM.appendChild(optgroup, option);
        option = createOption("Scope & Sequence", "curriculum_scope");
        DOM.appendChild(optgroup, option);
        option = createOption("Standards", "curriculum_standards");
        DOM.appendChild(optgroup, option);
        option = createOption("Syllabus", "curriculum_syllabus");
        DOM.appendChild(optgroup, option);
        DOM.appendChild(select, optgroup);

        optgroup = createGroup("Resource");
        option = createOption("Animation/Simulation", "resource_animation");
        DOM.appendChild(optgroup, option);
        option = createOption("Diagram/Illustration", "resource_diagram");
        DOM.appendChild(optgroup, option);
        option = createOption("Index/List", "resource_index");
        DOM.appendChild(optgroup, option);
        option = createOption("Photograph", "resource_photograph");
        DOM.appendChild(optgroup, option);
        option = createOption("Presentation/Slide Show", "resource_presentation");
        DOM.appendChild(optgroup, option);
        option = createOption("Reference Collection", "resource_collection");
        DOM.appendChild(optgroup, option);
        option = createOption("Script/Transcript", "resource_script");
        DOM.appendChild(optgroup, option);
        option = createOption("Speech/Lecture/Song", "resource_speech");
        DOM.appendChild(optgroup, option);
        option = createOption("Table/Graph/Chart", "resource_table");
        DOM.appendChild(optgroup, option);
        DOM.appendChild(select, optgroup);

        option = createOption("Other", "other");
        DOM.appendChild(select, option);
    }

    public Element createOption(String text, String value) {
        Element option = DOM.createElement("option");
        DOM.setInnerText(option, text);
        DOM.setAttribute(option, "value", value);
        return option;
    }

    public Element createGroup(String text) {
        Element group = DOM.createElement("optgroup");
        DOM.setAttribute(group, "label", text);
        return group;
    }

    /**
     * Override getValue() so that we get the n'th item in the options array instead of the n'th child.
     */
    public String getValue(int index){
        return getOptionValue(this.getElement(), index);
    }

    public native String getOptionValue(Element elem, int index) /*-{
        return elem.options[index].value;
    }-*/;
}
