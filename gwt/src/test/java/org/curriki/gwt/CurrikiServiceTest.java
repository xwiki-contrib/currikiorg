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
 */
package org.curriki.gwt;

import org.jmock.Mock;
import org.curriki.gwt.server.CurrikiServiceImpl;
import com.xpn.xwiki.web.*;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiConfig;
import com.xpn.xwiki.user.api.XWikiUser;
import com.xpn.xwiki.gwt.api.client.XWikiGWTException;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.StringProperty;
import com.xpn.xwiki.doc.XWikiDocument;

import javax.servlet.ServletException;
import java.util.Date;
import java.util.Vector;
import java.util.ArrayList;
import java.io.IOException;

public class CurrikiServiceTest extends org.jmock.cglib.MockObjectTestCase {

    private Mock mockXWiki;
    private Mock mockServletRequest;
    private Mock mockServletResponse;
    private Mock mockEngineContext;
    private Mock mockURLFactoryService;

    public void testGetTemplates() throws XWikiGWTException, ServletException, IOException {
        XWikiContext context = createXWikiContext();
        prepareTemplateTest();
        context.setWiki((XWiki) mockXWiki.proxy());

        ArrayList rlist = new ArrayList();
        rlist.add("Templates.WebHome");
        this.mockXWiki.stubs().method("search").will(returnValue(rlist));

        CurrikiServiceImpl cservice = new CurrikiServiceImpl();
        cservice.getTemplates();
    }

    public void prepareTemplateTest() {
        this.mockXWiki.stubs().method("getXWikiPreference").will(returnValue(null));
        this.mockXWiki.stubs().method("Param").will(returnValue(null));
        this.mockXWiki.stubs().method("getDocument").with(eq("Templates.WebHome"), ANYTHING)
                .will(returnValue(createMockTemplateHomeDocument(111111L, "Templates.WebHome")));
        this.mockXWiki.stubs().method("getDocument").with(eq("Templates.Page1"), ANYTHING)
                .will(returnValue(createMockTemplateDocument(111112L, "Templates.Page", "1")));
        this.mockXWiki.stubs().method("getDocument").with(eq("Templates.Page2"), ANYTHING)
                .will(returnValue(createMockTemplateDocument(111113L, "Templates.Page", "2")));
        this.mockXWiki.stubs().method("showViewAction").will(returnValue(true));
        this.mockXWiki.stubs().method("useDefaultWeb").will(returnValue(false));
        this.mockXWiki.stubs().method("useDefaultAction").will(returnValue(false));
        this.mockXWiki.stubs().method("getDefaultPage").will(returnValue("WebHome"));
        this.mockXWiki.stubs().method("setDatabase");
        this.mockXWiki.stubs().method("isVirtual").will(returnValue(false));

        this.mockURLFactoryService = mock(XWikiURLFactoryService.class);
        this.mockURLFactoryService.stubs().method("createURLFactory").will(returnValue(null));
        this.mockXWiki.stubs().method("getURLFactoryService").will(returnValue(this.mockURLFactoryService.proxy()));
        this.mockXWiki.stubs().method("prepareResources");
        this.mockXWiki.stubs().method("checkAuth").will(returnValue(new XWikiUser("XWiki.Toto")));

        this.mockEngineContext = mock(XWikiEngineContext.class);
        this.mockEngineContext.stubs().method("getAttribute").will(returnValue(mockXWiki.proxy()));

        this.mockServletRequest = mock(XWikiRequest.class);
        this.mockServletResponse = mock(XWikiResponse.class);
        this.mockServletRequest.stubs().method("getRequestURL").will(returnValue(new StringBuffer("http://localhost/xwiki/gwt/CurrikiService")));
        this.mockServletRequest.stubs().method("getQueryString").will(returnValue(""));
    }

    private BaseObject getSubAssetBaseObject(String name, String nb) {
        BaseObject subasset = new BaseObject();
        subasset.setName(name);
        subasset.setClassName("XWiki.SubAssetClass");
        StringProperty assetPage = new StringProperty();
        assetPage.setName("assetpage");
        assetPage.setValue("Templates.Page" + nb);
        subasset.addField("assetpage", assetPage);
        return subasset;
    }

    /*
    private BaseObject getAssetBaseObject(String name, String nb) {
        BaseObject subasset = new BaseObject();
        subasset.setName(name);
        subasset.setClassName("XWiki.AssetClass");
        StringProperty titleProp = new StringProperty();
        titleProp.setName("title");
        titleProp.setValue("Test Title " + nb);
        subasset.addField("title", titleProp);
        StringProperty descProp = new StringProperty();
        descProp.setName("description");
        descProp.setValue("Test Desc " + nb);
        subasset.addField("description", descProp);
        return subasset;
    }
    */

    private XWikiDocument createMockTemplateHomeDocument(long id, String name)
    {
        BaseObject subasset1 = getSubAssetBaseObject(name, "1");
        BaseObject subasset2 = getSubAssetBaseObject(name, "2");
        Vector objects = new Vector();
        objects.add(subasset1);
        objects.add(subasset2);

        Mock mockDocument = mock(XWikiDocument.class);
        XWikiDocument document = (XWikiDocument) mockDocument.proxy();
        mockDocument.stubs().method("getTranslatedDocument").will(returnValue(document));
        mockDocument.stubs().method("isNew").will(returnValue(false));
        mockDocument.stubs().method("getId").will(returnValue(new Long(id)));
        mockDocument.stubs().method("getDate").will(returnValue(new Date()));
        mockDocument.stubs().method("getContent").will(returnValue(""));
        mockDocument.stubs().method("getFullName").will(returnValue(name));
        mockDocument.stubs().method("getVersion").will(returnValue("1.1"));
        mockDocument.stubs().method("getObjects").with(eq("XWiki.SubAssetClass"))
                .will(returnValue(objects));
        return (XWikiDocument) mockDocument.proxy();
    }

    private XWikiDocument createMockTemplateDocument(long id, String name, String nb)
    {
        String pageName = name + nb;
        // BaseObject subasset = getAssetBaseObject(pageName, nb);

        Mock mockDocument = mock(XWikiDocument.class);
        XWikiDocument document = (XWikiDocument) mockDocument.proxy();
        mockDocument.stubs().method("getTranslatedDocument").will(returnValue(document));
        mockDocument.stubs().method("isNew").will(returnValue(false));
        mockDocument.stubs().method("getId").will(returnValue(new Long(id)));
        mockDocument.stubs().method("getDate").will(returnValue(new Date()));
        mockDocument.stubs().method("getContent").will(returnValue(""));
        mockDocument.stubs().method("getFullName").will(returnValue(pageName));
        mockDocument.stubs().method("getVersion").will(returnValue("1.1"));
        // mockDocument.stubs().method("getObject").with(eq("XWiki.AssetClass"))
        //        .will(returnValue(subasset));
        mockDocument.stubs().method("getStringValue").with(eq("XWiki.AssetClass"), eq("title"))
                .will(returnValue("Test Title " + nb));
        mockDocument.stubs().method("getStringValue").with(eq("XWiki.AssetClass"), eq("description"))
                .will(returnValue("Test Desc " + nb));
        mockDocument.stubs().method("getAttachmentList").will(returnValue(null));
        return (XWikiDocument) mockDocument.proxy();
    }

    private XWikiContext createXWikiContext() {
        Mock mockRequest = mock(XWikiRequest.class);
        XWikiContext context = new XWikiContext();
        XWikiConfig config = new XWikiConfig();
        context.setRequest((XWikiRequest) mockRequest.proxy());
        this.mockXWiki = mock(XWiki.class,
            new Class[]{XWikiConfig.class, XWikiContext.class}, new Object[]{config, context});
        context.setWiki((XWiki) this.mockXWiki.proxy());
        context.setMode(XWikiContext.MODE_GWT);
        return context;
    }
}
