package org.curriki.gwt.client.pages;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.http.client.*;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.Main;
/**
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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

public class PreviewPage extends AbstractPage implements WindowResizeListener {
    private Frame frame;

    public PreviewPage(){
        panel.setStyleName("preview-page");
        initWidget(panel);
    }

    public void init() {
        super.init();
        panel.clear();
        loadPreview();
        Window.addWindowResizeListener(this);
    }

    public boolean isSourceAssetPage() {
        return true;
    }

    public void resizeWindow() {
        if (frame!=null)  {
            int absoluteTop = frame.getAbsoluteTop();
            frame.setHeight((Window.getClientHeight() - absoluteTop) + "px");
        }
    }

    private void loadPreview() {
        Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
        panel.clear();
        if (currentAsset!=null) {
            String previewURL = currentAsset.getViewURL() + "?xpage=assetpreview";
            frame = new Frame(previewURL);
            frame.setStyleName("preview-page-iframe");
            resizeWindow();
            panel.add(frame);
            /*
            RequestBuilder request = new RequestBuilder(RequestBuilder.GET, commentURL);
            try {
                Request response = request.sendRequest(null, new RequestCallback() {
                    public void onError(Request request, Throwable exception) {
                        panel.clear();
                        panel.add(new HTML(Main.getTranslation("comment.errorgettingpreview")));
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
                panel.add(new HTML(Main.getTranslation("comment.errorgettingpreview")));
            }
            */
        }
    }

    public void onWindowResized(int width, int height) {
        resizeWindow();
    }
}
