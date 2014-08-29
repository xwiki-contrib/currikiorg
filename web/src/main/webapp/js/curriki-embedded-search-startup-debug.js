// typeof currikiResourceProxyWindow;

function postMessageHandler(event){ // Event having data of the form "eventtype:value"
	var eventData = event.data;
	var eventType = eventData.substr(0,eventData.indexOf(":")); // Get the "eventtype"
	var value = eventData.substr(eventData.indexOf(":")+1); // Get the "value"
	switch(eventType){
		case 'resize':
			console.log("embedded search: recieved resize event");
			resizeCurrikiIframe(value);
		break

		// case 'resourceurl':
		// 	console.log("embedded search: recieved resource url event");
		// 	openResourceUrl(value);
		// break
	}
}

function resizeCurrikiIframe(styleString){
  document.getElementById("curriki_search_frame").setAttribute("style", styleString)
}

// function openResourceUrl(resourceUrl){
// 	if(currikiResourceProxyWindow == null || typeof currikiResourceProxyWindow === "undefined" || currikiResourceProxyWindow.closed){
// 		currikiResourceProxyWindow = window.open("currikiResourceProxy.html?resourceurl=" + resourceUrl);
// 	}else{
// 		currikiResourceProxyWindow.location.href = ("currikiResourceProxy.html?resourceurl=" + resourceUrl);
// 	}
// }

function setCurrikiIFrameSrc(){
	//Please do not change
  var SEARCH_FRAME_PATH = "/xwiki/bin/view/EmbeddedSearch/AdvancedSearchFrame?xpage=plain"
	var currikiSearchFrame = document.getElementById("curriki_search_frame");
	var iFrameSrc = CURRIKI_HOST + SEARCH_FRAME_PATH + "&embeddingPartnerUrl=" + PARTNER_HOST + "&" + "resourceDisplay=" + RESOURCE_DISPLAYER + "&" + "embedViewMode=" + EMBED_VIEW_MODE;
	currikiSearchFrame.setAttribute("src", iFrameSrc);
}


if(typeof window.attachEvent === "function" || typeof window.attachEvent === "object"){ // Firefox 
	console.log("search: attached Listener to evenet via window.attachEvent");
	window.attachEvent('onmessage',postMessageHandler);
}else if (typeof window.addEventListener === "function"){
	console.log("search: attached Listener to evenet via window.addEvenListener");
  	window.addEventListener("message", postMessageHandler, false);
}else if(typeof document.attachEvent === "function"){
	console.log("search: cors iframe communication is not possible");
	document.attachEvent('onmessage',postMessageHandler);
}else{
	console.log("Frame communication not possible");
}