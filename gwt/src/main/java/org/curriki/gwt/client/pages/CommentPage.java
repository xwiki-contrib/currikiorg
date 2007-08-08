package org.curriki.gwt.client.pages;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.http.client.*;
import com.xpn.xwiki.gwt.api.client.Document;
import org.curriki.gwt.client.*;
import org.curriki.gwt.client.editor.Editor;

import java.util.List;

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

public class CommentPage extends AbstractPage {
    SimplePanel commentsPanel = new SimplePanel();
    Button bttAddComment = new Button(Main.getTranslation("editor.btt_addcomment"));
    Button bttAddComment2 = new Button(Main.getTranslation("editor.btt_addcomment"));
    Button bttSaveComment = new Button(Main.getTranslation("editor.btt_savecomment"));
    VerticalPanel addCommentPanel = new VerticalPanel();
    TextArea commentTextArea = new TextArea();

    public CommentPage(){
        panel.add(commentsPanel);
        panel.setStyleName("comment-page");

        ClickListener click = new ClickListener(){
            public void onClick(Widget sender) {
                commentTextArea.setText("");
                addCommentPanel.setVisible(!addCommentPanel.isVisible());
            }
        };
        bttAddComment.addClickListener(click);
        bttAddComment.addStyleName("bbt-AddComment");
        bttAddComment2.addClickListener(click);
        bttAddComment2.addStyleName("bbt-AddComment");

        addCommentPanel.addStyleName("comment-addcomment");
        commentTextArea.addStyleName("comment-textarea");
        bttSaveComment.addClickListener(new ClickListener() {
            public void onClick(Widget widget) {
                // Let's send the comment
                String comment = commentTextArea.getText();
                if ((comment==null)||(comment.trim().equals(""))) {
                    Window.alert(Main.getTranslation("comment.commentcannotbeempty"));
                } else {
                    CurrikiService.App.getInstance().addComment(Main.getSingleton().getEditor().getCurrentAssetPageName(), comment, new CurrikiAsyncCallback() {
                        public void onFailure(Throwable caught) {
                            super.onFailure(caught);
                        }

                        public void onSuccess(Object result) {
                            super.onSuccess(result);
                            // A comment has been added we should reload
                            Editor editor = Main.getSingleton().getEditor();
                            editor.getCurrentAsset().setCommentsNumber(editor.getCurrentAsset().getCommentsNumber()+1);
                            editor.resetCache();
                            editor.refreshState();
                        }
                    });
                }
            }
        });
        bttSaveComment.addStyleName("bbt-SaveComment");
        initWidget(panel);

    }

    private void initCommentForm() {
        addCommentPanel.clear();
        addCommentPanel.setVisible(false);
        addCommentPanel.add(commentTextArea);
        addCommentPanel.add(bttSaveComment);
        panel.add(addCommentPanel);
    }

    public void init() {
        super.init();
        panel.clear();
        Document assetDoc = Main.getSingleton().getEditor().getCurrentAsset();
        boolean hasComments = assetDoc.hasCommentRight();
        if (hasComments)
            panel.add(bttAddComment);
        panel.add(commentsPanel);
        commentsPanel.clear();
        if (hasComments) {
            panel.add(bttAddComment2);
            initCommentForm();
        }
        loadComments();
    }

    public boolean isSourceAssetPage() {
        return true;
    }
    
    private void loadComments() {
        Document currentAsset = Main.getSingleton().getEditor().getCurrentAsset();
        commentsPanel.clear();
        if (currentAsset.getCommentsNumber()>0) {
            commentsPanel.add(new HTML(Main.getTranslation("comment.loadingcomments")));
            if (currentAsset!=null) {
                String commentURL = currentAsset.getViewURL() + "?xpage=commentsinline";
                RequestBuilder request = new RequestBuilder(RequestBuilder.GET, commentURL);
                try {
                    Request response = request.sendRequest(null, new RequestCallback() {
                        public void onError(Request request, Throwable exception) {
                            commentsPanel.clear();
                            commentsPanel.add(new HTML(Main.getTranslation("comment.errorgettingcomments")));
                        }
                        public void onResponseReceived(Request request, Response response) {
                            // Show the comments in the placeholder panel for it
                            commentsPanel.clear();
                            String content = Main.makeLinksExternal(response.getText());
                            commentsPanel.add(new HTML(content));
                        }
                    });
                } catch (RequestException e) {
                    commentsPanel.clear();
                    commentsPanel.add(new HTML(Main.getTranslation("comment.errorgettingcomments")));
                }
            }
        }
    }
}
