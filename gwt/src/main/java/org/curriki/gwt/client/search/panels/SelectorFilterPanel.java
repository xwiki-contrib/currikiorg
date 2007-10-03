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
package org.curriki.gwt.client.search.panels;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.curriki.gwt.client.search.history.ClientState;
import org.curriki.gwt.client.search.history.KeepsState;
import org.curriki.gwt.client.search.selectors.EducationalLevelSelector;
import org.curriki.gwt.client.search.selectors.FileTypeSelector;
import org.curriki.gwt.client.search.selectors.InstructionalTypeSelector;
import org.curriki.gwt.client.search.selectors.Selectable;
import org.curriki.gwt.client.search.selectors.SelectorCollection;
import org.curriki.gwt.client.search.selectors.SpecialFilterSelector;
import org.curriki.gwt.client.search.selectors.SubjectSelector;

import java.util.Iterator;

public class SelectorFilterPanel extends VerticalPanel implements ChangeListener, Selectable,
    SourcesChangeEvents, KeepsState
{
    protected SelectorCollection selectors = new SelectorCollection();
    protected String fieldName;
    protected Grid g;

    public SelectorFilterPanel()
    {
        SubjectSelector sSubject = new SubjectSelector();
        sSubject.addChangeListener(this);
        selectors.add(sSubject);
        EducationalLevelSelector sLevel = new EducationalLevelSelector();
        sLevel.addChangeListener(this);
        selectors.add(sLevel);
        InstructionalTypeSelector sInstructionType = new InstructionalTypeSelector();
        sInstructionType.addChangeListener(this);
        selectors.add(sInstructionType);
        FileTypeSelector sFileType = new FileTypeSelector();
        sFileType.addChangeListener(this);
        selectors.add(sFileType);
//        LanguageSelector sLanguage = new LanguageSelector();
//        sLanguage.addChangeListener(this);
//        selectors.add(sLanguage);
        SpecialFilterSelector sSpecial = new SpecialFilterSelector();
        sSpecial.addChangeListener(this);
        selectors.add(sSpecial);

        g = new Grid(4, 3);

        g.setWidget(0, 0, sSubject.getLabel());
        g.setWidget(0, 1, sLevel.getLabel());
        g.setWidget(0, 2, sInstructionType.getLabel());

        g.setWidget(1, 0, sSubject);
        g.setWidget(1, 1, sLevel);
        g.setWidget(1, 2, sInstructionType);

        g.setWidget(2, 0, sFileType.getLabel());
//        g.setWidget(2, 1, sLanguage.getLabel());
        g.setWidget(2, 1, sSpecial.getLabel());

        g.setWidget(3, 0, sFileType);
//        g.setWidget(3, 1, sLanguage);
        g.setWidget(3, 1, sSpecial);

        add(g);

        //TODO: Need open/close bar
    }

    public Widget getLabel()
    {
        return null;
    }

    public void setFieldName(String name)
    {
        this.fieldName = name;
    }

    public String getFieldName()
    {
        String fieldName;
        if (this.fieldName != null){
            fieldName = this.fieldName;
        } else {
            fieldName = "";
        }
        return fieldName;
    }

    public String getFilter()
    {
        String filter = "";
        Iterator i = selectors.iterator();
        while (i.hasNext()){
            Selectable s = (Selectable) i.next();
            String f = s.getFilter();
            if (f != null && f.length() > 0){
                if (filter.length() > 0){
                    filter += " AND ";
                }
                filter += f;
            }
        }

        if (filter.length() > 0){
            filter = " "+filter+" ";
        }

        return filter;
    }

    public void loadState(ClientState state)
    {
        Iterator i = selectors.iterator();
        while (i.hasNext()){
            Object s = i.next();
            if (s instanceof KeepsState){
                ((KeepsState) s).loadState(state);
            }
        }
    }

    public void saveState(ClientState state)
    {
        Iterator i = selectors.iterator();
        while (i.hasNext()){
            Object s = i.next();
            if (s instanceof KeepsState){
                ((KeepsState) s).saveState(state);
            }
        }
    }

    public void onChange(Widget widget)
    {
        if (widget instanceof Selectable) {
            changeListeners.fireChange(this);
        }
    }

    private ChangeListenerCollection changeListeners;
    
    public void addChangeListener(ChangeListener changeListener)
    {
        if (changeListeners == null) {
            changeListeners = new ChangeListenerCollection();
        }
        changeListeners.add(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener)
    {
        if (changeListeners != null) {
            changeListeners.remove(changeListener);
        }
    }
}
