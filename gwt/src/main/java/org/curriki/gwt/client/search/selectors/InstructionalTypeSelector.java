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
        
        addOption(Main.getTranslation("search.selector.ict.any"), "");

        addGroup(Main.getTranslation("search.selector.ict.activity"));
        addGroupOption(Main.getTranslation("search.selector.ict.activity"), Main.getTranslation("search.selector.ict.activity.exercise"), "activity_exercise");
        addGroupOption(Main.getTranslation("search.selector.ict.activity"), Main.getTranslation("search.selector.ict.activity.lab"), "activity_lab");
        addGroupOption(Main.getTranslation("search.selector.ict.activity"), Main.getTranslation("search.selector.ict.activity.worksheet"), "activity_worksheet");
        addGroupOption(Main.getTranslation("search.selector.ict.activity"), Main.getTranslation("search.selector.ict.activity.problemset"), "activity_problemset");

        addGroup(Main.getTranslation("search.selector.ict.book"));
        addGroupOption(Main.getTranslation("search.selector.ict.book"), Main.getTranslation("search.selector.ict.book.fiction"), "book_fiction");
        addGroupOption(Main.getTranslation("search.selector.ict.book"), Main.getTranslation("search.selector.ict.book.nonfiction"), "book_nonfiction");
        addGroupOption(Main.getTranslation("search.selector.ict.book"), Main.getTranslation("search.selector.ict.book.readings"), "book_readings");
        addGroupOption(Main.getTranslation("search.selector.ict.book"), Main.getTranslation("search.selector.ict.book.textbook"), "book_textbook");

        addGroup(Main.getTranslation("search.selector.ict.curriculum"));
        addGroupOption(Main.getTranslation("search.selector.ict.curriculum"), Main.getTranslation("search.selector.ict.curriculum.assessment"), "curriculum_assessment");
        addGroupOption(Main.getTranslation("search.selector.ict.curriculum"), Main.getTranslation("search.selector.ict.curriculum.course"), "curriculum_course");
        addGroupOption(Main.getTranslation("search.selector.ict.curriculum"), Main.getTranslation("search.selector.ict.curriculum.unit"), "curriculum_unit");
        addGroupOption(Main.getTranslation("search.selector.ict.curriculum"), Main.getTranslation("search.selector.ict.curriculum.lp"), "curriculum_lp");
        addGroupOption(Main.getTranslation("search.selector.ict.curriculum"), Main.getTranslation("search.selector.ict.curriculum.scope"), "curriculum_scope");
        addGroupOption(Main.getTranslation("search.selector.ict.curriculum"), Main.getTranslation("search.selector.ict.curriculum.standards"), "curriculum_standards");
        addGroupOption(Main.getTranslation("search.selector.ict.curriculum"), Main.getTranslation("search.selector.ict.curriculum.syllabus"), "curriculum_syllabus");

        addGroup(Main.getTranslation("search.selector.ict.resource"));
        addGroupOption(Main.getTranslation("search.selector.ict.resource"), Main.getTranslation("search.selector.ict.resource.animation"), "resource_animation");
        addGroupOption(Main.getTranslation("search.selector.ict.resource"), Main.getTranslation("search.selector.ict.resource.diagram"), "resource_diagram");
        addGroupOption(Main.getTranslation("search.selector.ict.resource"), Main.getTranslation("search.selector.ict.resource.index"), "resource_index");
        addGroupOption(Main.getTranslation("search.selector.ict.resource"), Main.getTranslation("search.selector.ict.resource.photograph"), "resource_photograph");
        addGroupOption(Main.getTranslation("search.selector.ict.resource"), Main.getTranslation("search.selector.ict.resource.presentation"), "resource_presentation");
        addGroupOption(Main.getTranslation("search.selector.ict.resource"), Main.getTranslation("search.selector.ict.resource.collection"), "resource_collection");
        addGroupOption(Main.getTranslation("search.selector.ict.resource"), Main.getTranslation("search.selector.ict.resource.script"), "resource_script");
        addGroupOption(Main.getTranslation("search.selector.ict.resource"), Main.getTranslation("search.selector.ict.resource.speech"), "resource_speech");
        addGroupOption(Main.getTranslation("search.selector.ict.resource"), Main.getTranslation("search.selector.ict.resource.table"), "resource_table");

        addOption(Main.getTranslation("search.selector.ict.other"), "other");
    }

    public Widget getLabel()
    {
        HorizontalPanel p = new HorizontalPanel();
        p.add(new Label(Main.getTranslation("search.selector.ict")));
        //p.add(getTooltip("ict"));
        return p;
    }
}
