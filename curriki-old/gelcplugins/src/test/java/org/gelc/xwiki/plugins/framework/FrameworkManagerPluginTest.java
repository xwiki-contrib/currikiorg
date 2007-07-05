package org.gelc.xwiki.plugins.framework;

import com.xpn.xwiki.test.HibernateTestCase;
import com.xpn.xwiki.test.Utils;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.web.XWikiServletURLFactory;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;

import java.util.List;
import java.util.Vector;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;


public class FrameworkManagerPluginTest extends HibernateTestCase {
    public static String FRAMEWORK_NAME = "testFramework";

    protected void setUp() throws Exception {
        super.setUp();
        getXWiki().getPluginManager().addPlugin("framework_manager","org.gelc.xwiki.plugins.framework.FrameworkManagerPlugin", getXWikiContext());
        getXWikiContext().setURLFactory(new XWikiServletURLFactory(new URL("http://www.xwiki.org/"), "xwiki/" , "bin/"));

    }

    public void testInitPlugin() throws XWikiException {
        FrameworkManagerPluginAPI fmng = (FrameworkManagerPluginAPI) xwiki.getPluginApi("framework_manager", context);
        assertFalse(xwiki.getDocument(FrameworkConstant.FRAMEWORK_CLASS_FULLNAME, context).isNew());
        assertFalse(xwiki.getDocument(FrameworkConstant.FRAMEWORK_ITEM_CLASS_FULLNAME, context).isNew());
    }

    public void testImport() throws IOException, XWikiException {
        File xmlFile = new File(getClass().getResource("/framework_Arizona.xml").getFile());
        InputStream iStream = new FileInputStream(xmlFile);
        FrameworkManagerPluginAPI fmng = (FrameworkManagerPluginAPI) xwiki.getPluginApi("framework_manager", context);
        assertNotNull("The plugin is not on the list of plugins", fmng);
        assertTrue(fmng.importFramework(FRAMEWORK_NAME, iStream));
        assertTrue("We test if the homepage of the framework is created", xwiki.exists(FrameworkConstant.FRAMEWORK_PREFIX + FRAMEWORK_NAME + ".WebHome", context));

        Framework fmk = fmng.getFramework(FRAMEWORK_NAME);
        assertNotNull(fmk);

        List children = fmng.getChildren(fmk);
        assertNotNull(children);
        assertEquals(18, children.size());

        Vector obj;
        for(int i = 0; i < children.size(); i++){
            FrameworkItem item = (FrameworkItem) children.get(i);
            obj = item.getObjects(FrameworkConstant.FRAMEWORK_ITEM_CLASS_FULLNAME);
            assertNotNull(obj);
            assertEquals(obj.size(), 1);
        }

        obj = fmk.getObjects(FrameworkConstant.FRAMEWORK_CLASS_FULLNAME);
        assertNotNull(obj);
        assertEquals(obj.size(), 1);

        assertFalse("Should be false because of the missing of the PDF", fmng.testIntegrity(FRAMEWORK_NAME));
        List errors = (List) context.get("FrameworkManagerPlugin_Errors");
        List errorsCode = (List) context.get("FrameworkManagerPlugin_ErrorsCode");

        assertNotNull(errors);
        assertNotNull(errorsCode);

        attachPDFDoc(FrameworkConstant.FRAMEWORK_PREFIX + FRAMEWORK_NAME + ".WebHome");
        assertTrue(fmng.testIntegrity(FRAMEWORK_NAME));
    }

    private void attachPDFDoc(String docName) throws java.io.IOException, XWikiException {
        Utils.setStandardData();
        XWikiDocument doc1 = xwiki.getDocument(docName, context);
        XWikiAttachment attachment1 = new XWikiAttachment(doc1, FrameworkConstant.FRAMEWORK_REFERENCE_PDF_NAME);
        byte[] attachcontent1 = "plop".getBytes();
        attachment1.setContent(attachcontent1);
        doc1.saveAttachmentContent(attachment1, getXWikiContext());
        doc1.getAttachmentList().add(attachment1);
        getXWiki().getHibernateStore().saveXWikiDoc(doc1, getXWikiContext());
    }

    public void testAddChildItem(){

    }

    public void testRemoveItem(){

    }

    public void testGetPath() throws FileNotFoundException, XWikiException {
        File xmlFile = new File(getClass().getResource("/framework_Arizona.xml").getFile());
        InputStream iStream = new FileInputStream(xmlFile);
        FrameworkManagerPluginAPI fmng = (FrameworkManagerPluginAPI) xwiki.getPluginApi("framework_manager", context);
        assertNotNull("The plugin is not on the list of plugins", fmng);
        assertTrue(fmng.importFramework(FRAMEWORK_NAME, iStream));

        Framework fmk = fmng.getFramework(FRAMEWORK_NAME);
        assertNotNull(fmk);

        List children = fmng.getChildren(fmk);
        assertNotNull(children);
        assertEquals(18, children.size());

        for(int i = 0; i < children.size(); i++){
            FrameworkItem item = (FrameworkItem) children.get(i);
            Vector path = fmng.getPath(item);
            assertNotNull(path);
            assertEquals(2, path.size());
            assertEquals(((Document)path.get(0)).getFullName(), fmk.getFullName());
            assertEquals(((Document)path.get(1)).getFullName(), item.getFullName());
        }

        FrameworkItem item = ((FrameworkItem) children.get(0));
        children = fmng.getChildren(item);
        assertNotNull(children);
        assertTrue(children.size() > 0);
        for(int i = 0; i < children.size(); i++){
            FrameworkItem tmpItem = (FrameworkItem) children.get(i);
            Vector path = fmng.getPath(tmpItem);
            assertNotNull(path);
            assertEquals(3, path.size());
            assertEquals(((Document)path.get(0)).getFullName(), fmk.getFullName());
            assertEquals(((Document)path.get(1)).getFullName(), item.getFullName());
            assertEquals(((Document)path.get(2)).getFullName(), tmpItem.getFullName());
        }


    }

    public void testGetMetaDataList(){
        
    }

    public void testGetPDFReferenceURL(){
        
    }

}
