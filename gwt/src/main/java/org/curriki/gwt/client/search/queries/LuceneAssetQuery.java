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
package org.curriki.gwt.client.search.queries;

import org.curriki.gwt.client.Constants;

public class LuceneAssetQuery extends LuceneQuery
{
    public void doSearch(int start, int count)
    {
        searchTerms = "XWiki.AssetClass."+ Constants.ASSET_FW_ITEMS_PROPERTY+":FW_masterFramework.WebHome AND " + searchTerms;
        searchTerms += " AND NOT web:AssetTemp AND NOT web:Coll_Templates AND NOT name:WebHome AND NOT name:WebPreferences AND NOT name:MyCollections AND NOT name:SpaceIndex";
        super.doSearch(start, count);
    }
}
