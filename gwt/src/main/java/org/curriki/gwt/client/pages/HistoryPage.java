package org.curriki.gwt.client.pages;

import com.xpn.xwiki.gwt.api.client.Document;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.http.client.*;
import org.curriki.gwt.client.Main;
/**
 * Copyright 2006,XpertNet SARL,and individual contributors as indicated
 * by the contributors.txt.
 *
 * This is free software;you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation;either version2.1of
 * the License,or(at your option)any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software;if not,write to the Free
 * Software Foundation,Inc.,51 Franklin St,Fifth Floor,Boston,MA
 * 02110-1301 USA,or see the FSF site:http://www.fsf.org.
 *
 * @author ldubost
 */

/**
 * Copyright 2006,XpertNet SARL,and individual contributors as indicated
 * by the contributors.txt.
 *
 * This is free software;you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation;either version2.1of
 * the License,or(at your option)any later version.
 *
 *This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software;if not,write to the Free
 * Software Foundation,Inc.,51 Franklin St,Fifth Floor,Boston,MA
 * 02110-1301 USA,or see the FSF site:http://www.fsf.org.
 *
 * @author ldubost
 */

public class HistoryPage extends AbstractPage {

    public HistoryPage(){
        panel.setStyleName("history-page");
        initWidget(panel);
    }

    public void init() {
        super.init();
        panel.clear();
        loadHistory();
    }

    public boolean isSourceAssetPage() {
        return true;
    }

    private void loadHistory() {
        Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
        panel.clear();
        panel.add(new HTML(Main.getTranslation("history.loadinghistory")));
        if (currentAsset!=null) {
            String commentURL = currentAsset.getViewURL() + "?xpage=assethistory";
            RequestBuilder request = new RequestBuilder(RequestBuilder.GET, commentURL);
            try {
                Request response = request.sendRequest(null, new RequestCallback() {
                    public void onError(Request request, Throwable exception) {
                        panel.clear();
                        panel.add(new HTML(Main.getTranslation("comment.errorgettinghistory")));
                    }
                    public void onResponseReceived(Request request, Response response) {
                        // Show the comments in the placeholder panel for it
                        panel.clear();
                        String content = Main.makeLinksExternal(response.getText());
                        panel.add(new HTML(content));
                    }
                });
            } catch (RequestException e) {
                panel.clear();
                panel.add(new HTML(Main.getTranslation("comment.errorgettinghistory")));
            }
        }
    }
}
