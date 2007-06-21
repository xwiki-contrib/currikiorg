/*
 * Copyright 2007, The Global Education and Learning Community,
 * and individual contributors as indicated by the contributors.txt.
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
package org.curriki.gwt.client.widgets.template;

import org.curriki.gwt.client.AssetDocument;
import org.curriki.gwt.client.CurrikiAsyncCallback;
import org.curriki.gwt.client.CurrikiService;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TemplateCopier
{
    private AsyncCallback next;

    public void copyTemplate(TemplateInfo selectedTemplate, String parentAsset, AsyncCallback nextCallback) {
        copyTemplate(selectedTemplate.getPageName(), parentAsset, nextCallback);
    }

    public void copyTemplate(String selectedTemplate, String parentAsset, AsyncCallback nextCallback) {
        this.next = nextCallback;

        CurrikiService.App.getInstance().createTempSourceAssetFromTemplate(selectedTemplate, parentAsset, true, new CurrikiAsyncCallback() {
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
            }

            public void onSuccess(Object result) {
                super.onSuccess(result);
                AssetDocument newDoc = (AssetDocument) result;
                next.onSuccess(newDoc);
            }
        });
    }
}
