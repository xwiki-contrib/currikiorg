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
import org.curriki.gwt.client.search.history.KeepsState;
import org.curriki.gwt.client.search.history.ClientState;

public class SpaceFilterSelector implements Selectable, KeepsState
{
    protected String selector_id = "__space";
    protected String value = "";

    public SpaceFilterSelector() {
    }

    public Widget getLabel()
    {
        return null; // Not a displayable filter
    }

    public void setFieldName(String name)
    {
        selector_id = name;
    }

    public String getFieldName()
    {
        return selector_id;
    }

    public String getFilter()
    {
        String filter = "";
        if (!value.equals("")){
            filter = "web:"+value;
        }
        return filter;
    }

    public void loadState(ClientState state)
    {
        if (getFieldName().length() > 0){
            value = state.getValue(getFieldName());
        }
    }

    public void saveState(ClientState state)
    {
        if (getFieldName().length() > 0){
            state.setValue(getFieldName(), value);
        }
    }
}