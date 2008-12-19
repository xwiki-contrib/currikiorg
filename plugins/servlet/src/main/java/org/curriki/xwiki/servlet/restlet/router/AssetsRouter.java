package org.curriki.xwiki.servlet.restlet.router;

import org.curriki.xwiki.servlet.restlet.resource.assets.AssetManagerResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.AssetResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.AssetsResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.ExternalResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.ExternalsResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.MetadataResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.NominateResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.PartnerResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.PublishedResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.SubassetResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.SubassetsResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.TextassetResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.TextassetsResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.UnnominateResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.VideoResource;
import org.curriki.xwiki.servlet.restlet.resource.assets.VideosResource;
import org.restlet.Context;
import org.restlet.Router;
import org.restlet.util.Template;

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
        attach("/{assetName}/viditalks", VideosResource.class);
        attach("/{assetName}/viditalks/{viditalkId}", VideoResource.class);
        attach("/{assetName}/textassets", TextassetsResource.class);
        attach("/{assetName}/textassets/{textId}", TextassetResource.class);
        attach("/{assetName}/published", PublishedResource.class);
        attach("/{assetName}/nominate", NominateResource.class);
        attach("/{assetName}/unnominate", UnnominateResource.class);
        attach("/{assetName}/partner", PartnerResource.class);
        attach("/{assetName}/assetManager", AssetManagerResource.class);
    }
}
