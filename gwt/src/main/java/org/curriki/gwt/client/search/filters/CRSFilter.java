package org.curriki.gwt.client.search.filters;

import org.curriki.gwt.client.search.history.KeepsState;
import org.curriki.gwt.client.search.history.ClientState;
import org.curriki.gwt.client.search.selectors.Selectable;
import com.google.gwt.user.client.ui.Widget;

public class CRSFilter implements Selectable, KeepsState {
    protected String value = "";
    protected String fieldName = "__crs";

    public Widget getLabel() {
        // A hidden filter with no label
        return null;
    }

    public void setFieldName(String name) {
        fieldName = name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFilter() {
        String filter = "";

        if (value.length() > 0){
            if (value.equals("partners")){
                filter = "CRS.CurrikiReviewStatusClass.status:10";
            }
            if (value.equals("topreviewed")){
                filter = "CRS.CurrikiReviewStatusClass.status:(40 OR 20)";
            }
        }

        return filter;
    }

    public void saveState(ClientState state)
    {
        if (getFieldName().length() > 0){
            if (value.length() > 0){
                state.setValue(getFieldName(), value);
            } else {
                state.setValue(getFieldName(), "");
            }
        }
    }

    public void loadState(ClientState state)
    {
        if (getFieldName().length() > 0){
            String loadValue = state.getValue(getFieldName());
            if (loadValue.length() > 0){
                value = loadValue;
            }
        }
    }
}
