(function(){Ext.ns("Curriki.module.resourceproxy");var a=Curriki.module.resourceproxy;a.settings={proxyUrl:"http://current.dev.curriki.org"};a.run=function(){console.log("resourceproxy: starting");var b=a.getResourceUrlFromParams();a.renderPage(b)};a.getResourceUrlFromParams=function(){var b=Ext.urlDecode(location.search.substring(1));if(!(typeof b.resourceurl==="undefined")){return b.resourceurl}else{document.write("Please provide a resource to display");throw"EmbeddedDisplay Error: No ressourceurl defined"}};a.renderPage=function(c){var b=document.getElementById("curriki_resource_frame");b.setAttribute("src",a.settings.proxyUrl+unescape(c))};Ext.onReady(function(){a.run()})})();