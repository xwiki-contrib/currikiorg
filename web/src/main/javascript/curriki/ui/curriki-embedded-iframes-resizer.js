
function postMessageHandler(event){ // Event having data of the form "eventtype:value"
    var eventData = event.data;
    var eventType = eventData.substr(0,eventData.indexOf(":")); // Get the "eventtype"
    var p = eventData.indexOf(":"), q = eventData.indexOf(":",p+1);
    var target = eventData.substr(p, q-p); // Get the "target"
    var value = eventData.substr(q); // Get the "value"
    switch(eventType){
        case 'resize':
            console.log("received resize event");
            resizeThatCurrikiIframe(target, value);
            break
    }
}

function resizeThatCurrikiIframe(target, styleString){
    document.getElementById("currikiIFrame_" + target).setAttribute("style", styleString)
}

///  postMessage Processing
if(typeof window.attachEvent === "function" || typeof window.attachEvent === "object"){ // Firefox
    console.log("attached Listener to event via window.attachEvent");
    window.attachEvent('onmessage',postMessageHandler);
}else if (typeof window.addEventListener === "function"){
    console.log("attached Listener to event via window.addEvenListener");
    window.addEventListener("message", postMessageHandler, false);
}else if(typeof document.attachEvent === "function"){
    console.log("cors iframe communication is not possible");
    document.attachEvent('onmessage',postMessageHandler);
}else{
    console.log("Frame communication not possible");
}