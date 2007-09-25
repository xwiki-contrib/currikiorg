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

public class EducationalLevelSelector extends DropdownSingleSelector
{
    public EducationalLevelSelector()
    {
        super();
        setFieldName("XWiki.AssetClass."+ Constants.ASSET_EDUCATIONAL_LEVEL_PROPERTY);
        
        addOption(Main.getTranslation("search.selector.level.any"), "");
        addOption(Main.getTranslation("search.selector.level.prek"), "prek");
        addOption(Main.getTranslation("search.selector.level.gr-k-2"), "gr-k-2");
        addOption(Main.getTranslation("search.selector.level.gr-3-5"), "gr-3-5");
        addOption(Main.getTranslation("search.selector.level.gr-6-8"), "gr-6-8");
        addOption(Main.getTranslation("search.selector.level.gr-9-10"), "gr-9-10");
        addOption(Main.getTranslation("search.selector.level.gr-11-12"), "gr-11-12");
        addOption(Main.getTranslation("search.selector.level.college_and_beyond"), "college_and_beyond");
        addOption(Main.getTranslation("search.selector.level.professional_development"), "professional_development");
        addOption(Main.getTranslation("search.selector.level.special_education"), "special_education");
        addOption(Main.getTranslation("search.selector.level.na"), "na");
    }

    public Widget getLabel()
    {
        return new Label(Main.getTranslation("search.selector.level"));
    }
}
