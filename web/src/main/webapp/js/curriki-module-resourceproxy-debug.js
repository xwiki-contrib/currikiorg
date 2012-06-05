// vim: ts=4:sw=4
/*global Ext */
/*global _ */

(function(){
	Ext.ns('Curriki.module.resourceproxy');

	var ResourceProxy = Curriki.module.resourceproxy;

	ResourceProxy.settings = {
		proxyUrl: "http://current.dev.curriki.org"
	};

	ResourceProxy.run = function(){
		console.log("resourceproxy: starting");
		var url = ResourceProxy.getResourceUrlFromParams();

		ResourceProxy.renderPage(url);
	};

	ResourceProxy.getResourceUrlFromParams = function(){
		var params = Ext.urlDecode(location.search.substring(1));

		if(! (typeof params.resourceurl === "undefined") ){
			return params.resourceurl;
		} else {
			document.write("Proxy Error: No ressourceurl defined");
			throw "Proxy Error: No ressourceurl defined";
		}
	};

	ResourceProxy.renderPage = function(url){

		Ext.DomHelper.append(
	   		Ext.getBody(),
		    {tag: 'iframe', src: ResourceProxy.settings.proxyUrl + unescape(url), width:'100%', height:'100%', scrolling:"auto", frameborder:"0", allowtransparency:"true"},
		    false // this is required in order to return DOM node instead of Ext.Element
		);
	};

	Ext.onReady(function(){
		ResourceProxy.run();
	});

})();
