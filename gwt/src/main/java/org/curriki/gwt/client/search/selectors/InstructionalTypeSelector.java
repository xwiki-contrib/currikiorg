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
package org.curriki.gwt.client.search.selectors;

import org.curriki.gwt.client.Main;
import org.curriki.gwt.client.Constants;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class InstructionalTypeSelector extends DropdownSingleSelector
{
    public InstructionalTypeSelector() {
        super();
        setFieldName("XWiki.AssetClass."+Constants.ASSET_INSTRUCTIONAL_COMPONENT_PROPERTY);
        
        addOption(Main.getTranslation("Any"), "");

        addGroup(Main.getTranslation("Activity"));
        addGroupOption(Main.getTranslation("Activity"), Main.getTranslation("Excercise"), "activity_exercise");
        addGroupOption(Main.getTranslation("Activity"), Main.getTranslation("Exercise"), "activity_exercise");
        addGroupOption(Main.getTranslation("Activity"), Main.getTranslation("Experiment/Lab"), "activity_lab");
        addGroupOption(Main.getTranslation("Activity"), Main.getTranslation("Graphic Organizer/Worksheet"), "activity_worksheet");
        addGroupOption(Main.getTranslation("Activity"), Main.getTranslation("Problem Set"), "activity_problemset");

        addGroup(Main.getTranslation("Book"));
        addGroupOption(Main.getTranslation("Book"), Main.getTranslation("Fiction"), "book_fiction");
        addGroupOption(Main.getTranslation("Book"), Main.getTranslation("Non-Fiction"), "book_nonfiction");
        addGroupOption(Main.getTranslation("Book"), Main.getTranslation("Readings/Excerpts"), "book_readings");
        addGroupOption(Main.getTranslation("Book"), Main.getTranslation("Text Book"), "book_textbook");

        addGroup(Main.getTranslation("Curriculum"));
        addGroupOption(Main.getTranslation("Curriculum"), Main.getTranslation("Assessment/Test"), "curriculum_assessment");
        addGroupOption(Main.getTranslation("Curriculum"), Main.getTranslation("Full Course"), "curriculum_course");
        addGroupOption(Main.getTranslation("Curriculum"), Main.getTranslation("Lesson Plan"), "curriculum_lp");
        addGroupOption(Main.getTranslation("Curriculum"), Main.getTranslation("Scope & Sequence"), "curriculum_scope");
        addGroupOption(Main.getTranslation("Curriculum"), Main.getTranslation("Standards"), "curriculum_standards");
        addGroupOption(Main.getTranslation("Curriculum"), Main.getTranslation("Syllabus"), "curriculum_syllabus");

        addGroup(Main.getTranslation("Resource"));
        addGroupOption(Main.getTranslation("Resource"), Main.getTranslation("Animation/Simulation"), "resource_animation");
        addGroupOption(Main.getTranslation("Resource"), Main.getTranslation("Diagram/Illustration"), "resource_diagram");
        addGroupOption(Main.getTranslation("Resource"), Main.getTranslation("Index/List"), "resource_index");
        addGroupOption(Main.getTranslation("Resource"), Main.getTranslation("Photograph"), "resource_photograph");
        addGroupOption(Main.getTranslation("Resource"), Main.getTranslation("Presentation/Slide Show"), "resource_presentation");
        addGroupOption(Main.getTranslation("Resource"), Main.getTranslation("Reference Collection"), "resource_collection");
        addGroupOption(Main.getTranslation("Resource"), Main.getTranslation("Script/Transcript"), "resource_script");
        addGroupOption(Main.getTranslation("Resource"), Main.getTranslation("Speech/Lecture/Song"), "resource_speech");
        addGroupOption(Main.getTranslation("Resource"), Main.getTranslation("Table/Graph/Chart"), "resource_table");

        addOption(Main.getTranslation("Other"), "other");
    }

    public Widget getLabel()
    {
        HorizontalPanel p = new HorizontalPanel();
        p.add(new Label(Main.getTranslation("Instructional Type")));
        p.add(getTooltip("ict"));
        return p;
    }
}
