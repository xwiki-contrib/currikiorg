package org.curriki.xwiki.servlet.restlet.router;

import com.google.gson.Gson;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.doc.XWikiDocument;
import org.apache.commons.httpclient.methods.GetMethod;
import org.curriki.xwiki.plugin.curriki.CurrikiPlugin;
import org.curriki.xwiki.plugin.curriki.CurrikiPluginApi;
import org.json.JSONException;
import org.json.JSONWriter;
import org.restlet.data.MediaType;
import org.restlet.resource.StreamRepresentation;

import java.io.*;
import java.nio.charset.Charset;

/**
 */
public class CTVRepresentation extends StreamRepresentation {

    private final static MediaType jsonMediaType = MediaType.register("application/json?charset=utf-8","JavaScript Object Notation");
    private final static Charset UTF8 = Charset.forName("UTF-8");
    private final static String SUFFIX = "[",
            PREFIX = "]"; // TODO: check if always so

    private final XWiki xwiki;
    private long totalSize = 0;
    private final byte[] byteBuff = new byte[512];
    private CurrikiPlugin currikiPlugin = null;
    private final String docFullName;
    private final String solrField;
    private final Type type;

    private boolean isBackEndStream = false;
    private GetMethod get;
    private Object objectToOutput;
    private static ThreadLocal<Gson> gson = new ThreadLocal<Gson>() {
        protected Gson initialValue() { return new Gson(); }};


    public CTVRepresentation(String targetDocument, Type type, XWikiContext context)  throws IOException, XWikiException {
        super(jsonMediaType);
        this.xwiki =context.getWiki();
        this.type = type;
        this.docFullName = targetDocument;
        this.currikiPlugin = (CurrikiPlugin) xwiki.getPlugin("curriki",context);

        boolean useBackEnd = true;

        if(!currikiPlugin.checkSolrIsUp()) useBackEnd = false;

        // identify docs to queries
        // TODO: check test fetchPage
        if(type==Type.USER_COLLECTIONS) {
            solrField = "userCollections";
        } else if(type==Type.USER_GROUPS) {
            solrField = "userGroups";
        } else if(type==Type.GROUP_COLLECTIONS) {
            solrField = "groupCollections";
        } else if(type==Type.COLLECTION_CONTENT) {
            solrField = "childInfo";
        } else {
            throw new UnsupportedEncodingException();
        }

        // TODO: check versions and rights: between solr and xwiki

        /* totalSize = PREFIX.length() + SUFFIX.length();
       for(SubRepresentation rep: reps) {
           rep.init();
           totalSize += rep.getLength();
       } */
        //
        //TODO: resume with size (need to convert to streams and SOLR to deliver raw value
        // super.setSize(totalSize);
    }

    public void init(XWikiContext context) throws XWikiException, IOException {
        // first check versions
        XWikiDocument doc = xwiki.getDocument(docFullName, context);
        String xwikiVersion = doc.getVersion();

        String fn = "fullname:" + docFullName;
        String solrRev = currikiPlugin.solrGetSingleValue(fn, "revisionNumber");
        if(xwikiVersion.equals(solrRev)) {
            isBackEndStream = true;
            get = currikiPlugin.solrCreateQueryGetMethod(fn, solrField, 0, 100);
            currikiPlugin.startMethod(get);
        } else {
            isBackEndStream = false;
            objectToOutput = currikiPlugin.fetchCollectionsInfo(docFullName, context);
        }


    }

    private long getLength() {
        if(isBackEndStream)
            return get.getResponseContentLength();
        else return -1;
        //return jsonBytes.length;
    }

    public void write(final Writer out) throws IOException {
        if(isBackEndStream) {
            if(type==Type.COLLECTION_CONTENT) out.write("[");
            Runnable sepWriter = null;
            if(type==Type.COLLECTION_CONTENT) sepWriter = new Runnable() { public void run() {
                try {
                    out.write(",\n");
                } catch (IOException e) { e.printStackTrace(); }
            }};
            currikiPlugin.feedFieldFromXmlStream(get, out,sepWriter, solrField);
            if(type==Type.COLLECTION_CONTENT) out.write("]");
        } else {
            try {
                new JSONWriter(out).value(objectToOutput);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public void write(OutputStream out) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, UTF8);
        this.write(writer);
        writer.flush();
    }

    @Override
    public InputStream getStream() throws IOException {
        throw new UnsupportedOperationException("Can't create a stream thus far.");
        // could do so by joining the input streams (and strings)... just... a bit of work
    }

    public static enum Type {
        USER_COLLECTIONS, USER_GROUPS, GROUP_COLLECTIONS, COLLECTION_CONTENT
    }
}
