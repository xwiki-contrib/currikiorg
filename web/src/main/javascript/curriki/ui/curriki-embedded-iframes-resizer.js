
function postMessageHandler(event){ // Event having data of the form "eventtype:value"
    console.log("postMessage: ",event);
    var eventData = event.data;
    var eventType = eventData.substring(0,eventData.indexOf(":")); // Get the "eventtype"
    var p = eventData.indexOf(":"); var q = eventData.indexOf(":",p+1);
    var target = eventData.substring(p+1, q); // Get the "target"
    var value = eventData.substring(q+1); // Get the "value"
    //console.log("eventData: \"" + eventData + "\", p=" +p + ", q=" + q + " target: \"" + target + "\".");
    switch(eventType){
        case 'resize':
            console.log("received resize event (resize " + target + " to " + value + ")");
            window.resizeThatCurrikiIframe(target, value);
            break
    }
}

function resizeThatCurrikiIframe(target, styleString){
    var frameName = "currikiIFrame_" + target;
    if(target.startsWith("currikiIFrame")) frameName = target;
    var frame = document.getElementById(frameName);
    if(frame) {
        frame.setAttribute("style", styleString);
        frame.parentNode.setAttribute("style", styleString);
    } else
        console.log("No frame found for " + frameName);
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