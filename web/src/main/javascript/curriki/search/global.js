// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
	Ext.ns('Curriki.module.search');

	var Search = Curriki.module.search;

	Search.settings = {
		gridWidth:(Ext.isIE6?620:'auto')
	};

	Search.stateProvider = new Ext.state.CookieProvider({
	});
	Ext.state.Manager.setProvider(Search.stateProvider);

	Search.sessionProvider = new Ext.state.CookieProvider({
		expires: null // Valid until end of browser session
	});


    Curriki.module.search.outerResources = {
        prefix: "http://www.curriki.org/xwiki/bin/view/",
        suffix: "?viewer=embed",
        target: "currikiResources",
        ratingsPrefix: "http://www.curriki.org/xwki/bin/view/",
        ratingsSuffix : "?viewer=comments"
        };
})();
