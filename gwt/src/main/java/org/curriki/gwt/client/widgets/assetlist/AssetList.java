package org.curriki.gwt.client.widgets.assetlist;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;

import java.util.List;

import org.curriki.gwt.client.Main;
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
 * @author jeremi
 */

public class AssetList  extends Composite {
    Grid grid;
    public final int MAX_COL = 5;

    public AssetList(List docs){
        grid = new Grid(docs.size() + 1, MAX_COL);

        grid.setText(0, 1, Main.getTranslation("asset.title"));
        grid.setText(0, 2, Main.getTranslation("asset.subject"));
        grid.setText(0, 3, Main.getTranslation("asset.level"));
        grid.setText(0, 4, Main.getTranslation("asset.type"));
        
        initWidget(grid);
    }

}
