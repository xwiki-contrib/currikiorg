// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
	Ext.ns('Curriki.module.search');

	var Search = Curriki.module.search;

	Search.stateProvider = new Ext.state.CookieProvider({
	});
	Ext.state.Manager.setProvider(Search.stateProvider);

	Search.sessionProvider = new Ext.state.CookieProvider({
		expires: null // Valid until end of browser session
	});
})();
