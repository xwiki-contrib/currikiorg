package org.curriki.xwiki.servlet.restlet.router;

import org.restlet.Router;
import org.restlet.Context;
import org.restlet.util.Template;
import org.curriki.xwiki.servlet.restlet.resource.assets.SubassetsResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.MetadataResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.AssetsResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.AssetResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.ExternalsResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.ExternalResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.SubassetResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.ViditalksResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.ViditalkResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.TextassetsResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.TextassetResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.PublishedResource;

/**
 */
public class AssetsRouter extends Router {
    public AssetsRouter(Context context) {
        super(context);
        attach("", AssetsResource.class).getTemplate().setMatchingMode(Template.MODE_EQUALS);
        attach("/{assetName}", AssetResource.class);
        attach("/{assetName}/metadata", MetadataResource.class);
        attach("/{assetName}/subassets", SubassetsResource.class);
        attach("/{assetName}/subassets/{subassetId}", SubassetResource.class);
        attach("/{assetName}/externals", ExternalsResource.class);
        attach("/{assetName}/externals/{externalId}", ExternalResource.class);
        attach("/{assetName}/viditalks", ViditalksResource.class);
        attach("/{assetName}/viditalks/{viditalkId}", ViditalkResource.class);
        attach("/{assetName}/textassets", TextassetsResource.class);
        attach("/{assetName}/textassets/{textId}", TextassetResource.class);
        attach("/{assetName}/published", PublishedResource.class);
    }
}
