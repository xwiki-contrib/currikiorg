if(typeof Curriki == 'undefined') Curriki = {}

Curriki.logEvent = function(eventParams, followup) {
  var gaqParams=eventParams.reverse();
  gaqParams.push("_trackEvent"); gaqParams = gaqParams.reverse();
  if(window._gaq) {
    if(followup) {
      _gaq.push(gaqParams).push(followup);
    } else {
      _gaq.push(gaqParams);
    }
  } else {
    try{
      if(followup) {
        window.top._gaq.push(gaqParams).push(followup);
      } else {
        window.top._gaq.push(gaqParams);
      }
      if(console) console.info('Would track: ', page);
    }catch(e){
      try{
        if(console) console.info('Failed to track: ', page);
      }catch(e){

      }
    }
  }
}

Curriki.logView = function(page, callback){
  // Usage in site example:
  // <a onClick="javascript:Curriki.logView('/Download/attachment/${space}/${name}/${attach.filename}');"> .. </a>
  if (window.pageTracker) {
      if(typeof(callback)=="function") {
          pageTracker.push("_trackPageview", page);
          pageTracker.push(callback);
      } else pageTracker._trackPageview(page);
  } else if (_gaq) {
      if(typeof(callback)=="function") {
          _gaq.push("_trackPageview", page);
          _gaq.push(callback);
      } else _gaq._trackPageview(page);
  } else {

    // Double try catch for CURRIKI-5828
    // This is needed because we can not define if we
    // are coming from an embedded search in a resource proxy.
    // So we need to try to address not the top frame if thats fails.
    try{
      if (window.top._gaq) {
        window.top._gaq.push(["_trackPageview", page]);
      } else {
        window.top.pageTrackerQueue = window.top.pageTrackerQueue || new Array();
        window.top.pageTrackerQueue.push(page);
      }
      if(console) console.info('Would track: ', page);
    }catch(e){
      try{
        window.pageTrackerQueue = window.pageTrackerQueue || new Array();
        window.pageTrackerQueue.push(page);
        if(console) console.info('Would track: ', page);
      }catch(e){

      }
    }
  }
}