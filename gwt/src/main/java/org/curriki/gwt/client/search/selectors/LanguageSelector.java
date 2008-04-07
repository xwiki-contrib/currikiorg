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
import com.google.gwt.user.client.ui.HTML;

public class LanguageSelector extends DropdownSingleSelector
{
    public LanguageSelector() {
        super();
        setFieldName("XWiki.AssetClass."+ Constants.ASSET_LANGUAGE_PROPERTY);
        
        addOption(Main.getTranslation("search.selector.language.any"), "");
        addOption(Main.getTranslation("XWiki.AssetClass_language_eng"), "eng");
        addOption(Main.getTranslation("XWiki.AssetClass_language_zho"), "zho");
        addOption(Main.getTranslation("XWiki.AssetClass_language_nld"), "nld");
        addOption(Main.getTranslation("XWiki.AssetClass_language_fra"), "fra");
        addOption(Main.getTranslation("XWiki.AssetClass_language_deu"), "deu");
        addOption(Main.getTranslation("XWiki.AssetClass_language_ita"), "ita");
        addOption(Main.getTranslation("XWiki.AssetClass_language_jpn"), "jpn");
        addOption(Main.getTranslation("XWiki.AssetClass_language_kor"), "kor");
        addOption(Main.getTranslation("XWiki.AssetClass_language_por"), "por");
        addOption(Main.getTranslation("XWiki.AssetClass_language_rus"), "rus");
        addOption(Main.getTranslation("XWiki.AssetClass_language_spa"), "spa");
    }

    public Widget getLabel()
    {
        return new HTML(Main.getTranslation("search.selector.language"));
    }
}
