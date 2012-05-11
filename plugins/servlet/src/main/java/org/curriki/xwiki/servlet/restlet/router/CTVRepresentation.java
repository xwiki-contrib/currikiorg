package org.curriki.xwiki.servlet.restlet.router;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.doc.XWikiDocument;
import org.apache.commons.httpclient.methods.GetMethod;
import org.curriki.plugin.spacemanager.impl.CurrikiSpaceManager;
import org.curriki.xwiki.plugin.curriki.CurrikiPlugin;
import org.restlet.data.MediaType;
import org.restlet.resource.StreamRepresentation;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

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
    private List userGroups;
    private String userName;
    private boolean userIsAdmin;
    private String childRightsS = null;
    private static ThreadLocal<Gson> gson = new ThreadLocal<Gson>() {
        protected Gson initialValue() { return new Gson(); }};


    public CTVRepresentation(String targetDocument, Type type, XWikiContext context)  throws IOException, XWikiException {
        super(jsonMediaType);
        this.xwiki =context.getWiki();
        this.userGroups = ((CurrikiSpaceManager) xwiki.getPlugin("csm",context)).getSpaceNames(context.getUser(), null, context);
        this.type = type;
        this.userName = context.getUser();
        this.userIsAdmin = xwiki.checkAccess("admin", xwiki.getDocument("XWiki.XWikiPreferences", context), context);
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
            childRightsS = currikiPlugin.solrGetSingleValue("fullname:" + docFullName, "childRights");
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

    private static String ghostSubAssetInfoJson = createPrivatisedSubAssetInfo();
    private static String createPrivatisedSubAssetInfo() {
        Map<String,Object> subInfo = new TreeMap<String,Object>();
        subInfo.put("displayTitle", "");
        subInfo.put("description", "");
        subInfo.put("revision", "");
        subInfo.put("fwItems", new String[]{});
        subInfo.put("levels", new String[]{});
        subInfo.put("category", "");
        subInfo.put("subcategory", "");
        subInfo.put("ict", "");
        //subInfo.put("assetType", assetType);

        Map<String,Boolean> rightsInfo = new HashMap<String, Boolean>(3);
        rightsInfo.put("view", false);
        rightsInfo.put("edit", false);
        rightsInfo.put("delete", false);
        subInfo.put("rights", rightsInfo);
        return new Gson().toJson(subInfo);
    }


    private boolean isUserAllowed(String rights) {
        if(userIsAdmin) return true;
        if(rights.startsWith("privateToGroup:"))
            return !userGroups.contains(rights.substring("privateToGroup:".length()));
        else if(rights.startsWith("privateToUser:"))
            return !userName.equals(rights.substring("privateToUser:".length()));
        else return true;
    }

    public void write(final Writer out) throws IOException {
        try {
            if(isBackEndStream) {
                final StringTokenizer childRights= type==Type.COLLECTION_CONTENT ?
                        new StringTokenizer(childRightsS, ",")
                        : null;
                currikiPlugin.startSolrMethod(get);
                if(type==Type.COLLECTION_CONTENT) {
                    out.write("[");
                    CurrikiPlugin.SolrResultCollector collector =new CurrikiPlugin.SolrResultCollector() {
                        public void addValue(String value) {
                            String right = childRights!=null ? childRights.nextToken() : null;
                            try {
                                if(childRights!=null && isUserAllowed(right)) out.write(value);
                                else out.write(ghostSubAssetInfoJson );
                                out.write(",\n");
                            } catch (Exception e) {
                                throw new IllegalStateException(e);
                            }
                        }
                    };
                    currikiPlugin.feedFieldFromXmlStream(get, collector, solrField);
                    out.write("]");
                } else {
                    currikiPlugin.feedFieldFromXmlStream(get, out, solrField);
                }
            } else {
                new Gson().toJson(objectToOutput,  out);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
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
