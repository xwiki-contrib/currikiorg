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

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import org.curriki.gwt.client.Main;

public class TermSelector extends TextInputSelector
{
    protected int selectorSize = 30;
    protected String selector_id = "__terms";

    public TermSelector() {
        super();
        setName(selector_id);
        setVisibleLength(selectorSize);
    }

    public TermSelector(String name){
        super(name);
        setVisibleLength(selectorSize);
    }

    public TermSelector(String name, String value){
        super(name, value);
        setVisibleLength(selectorSize);
    }

    public Widget getLabel()
    {
        HorizontalPanel p = new HorizontalPanel();
        p.add(new HTML(Main.getTranslation("search.selector.terms")));
        //p.add(getTooltip("terms"));
        return p;
    }
}
