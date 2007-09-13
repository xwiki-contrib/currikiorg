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

public class EducationalLevelSelector extends DropdownSingleSelector
{
    public EducationalLevelSelector()
    {
        super();
        setFieldName("XWiki.AssetClass."+ Constants.ASSET_EDUCATIONAL_LEVEL_PROPERTY);
        
        addOption(Main.getTranslation("Any"), "");
        addOption(Main.getTranslation("Preschool / Ages 0-4"), "prek");
        addOption(Main.getTranslation("Gr. K-2 / Ages 5-7"), "gr-k-2");
        addOption(Main.getTranslation("Gr. 3-5 / Ages 8-10"), "gr-3-5");
        addOption(Main.getTranslation("Gr. 6-8 / Ages 11-13"), "gr-6-8");
        addOption(Main.getTranslation("Gr. 9-10 / Ages 14-16"), "gr-9-10");
        addOption(Main.getTranslation("Gr. 11-12 / Ages 16-18"), "gr-11-12");
        addOption(Main.getTranslation("College and Beyond"), "college_and_beyond");
        addOption(Main.getTranslation("Professional Development"), "professional_development");
        addOption(Main.getTranslation("Special Education"), "special_education");
        addOption(Main.getTranslation("Other"), "na");
    }

    public Widget getLabel()
    {
        return new Label(Main.getTranslation("Level"));
    }
}
