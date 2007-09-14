package org.curriki.gwt.client.widgets.currikiroundedpanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.widgets.roundedpanel.RoundedPanel;
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

public class CurrikiRoundedPanel extends Composite {
    private String title;
    private Panel widget;
    private RoundedPanel rndPanel;
    private HorizontalPanel panel;

    public CurrikiRoundedPanel(String title, Widget widget){
        panel = new HorizontalPanel();
        panel.add(new HTML(title));

        panel.add(widget);

        rndPanel = new RoundedPanel(panel);
        initWidget(rndPanel); 
    }
}
