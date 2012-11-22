// vim: ts=4:sw=4
/*global Ext */
/*global _ */

(function(){
	Ext.ns('Curriki.module.resourceproxy');

	var ResourceProxy = Curriki.module.resourceproxy;

	ResourceProxy.settings = {
		proxyUrl: "http://current.dev.curriki.org" //Working on current util release
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
			document.write("Please provide a resource to display");
			throw "EmbeddedDisplay Error: No ressourceurl defined";
		}
	};

	ResourceProxy.renderPage = function(url){
		var resourceFrame = document.getElementById("curriki_resource_frame");
		resourceFrame.setAttribute("src", ResourceProxy.settings.proxyUrl + unescape(url));
	};

	Ext.onReady(function(){
		ResourceProxy.run();
	});

})();
