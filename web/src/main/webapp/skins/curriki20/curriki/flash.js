function getFlashMovieObject(movieName) {
  if (window.document[movieName]) {
      return window.document[movieName];
  }
  if (navigator.appName.indexOf("Microsoft Internet")==-1) {
    if (document.embeds && document.embeds[movieName]) {
      return document.embeds[movieName]; 
    }
  } else { // if (navigator.appName.indexOf("Microsoft Internet")!=-1)
    return document.getElementById(movieName);
  }
}

function displayFlash(flashURL, flashNum){
  var so = new SWFObject(flashURL, "flashItem"+flashNum, "450", "450", "6", "#ffffff");
  so.addParam("wmode", "transparent");
  so.addParam("play", "false");
  so.addParam("quality", "autohigh");
  so.write("flashContent"+flashNum);
}

function initialStopFlashMovie(flashNum){
  var mObj = getFlashMovieObject("flashItem"+flashNum);
  mObj.StopPlay();
}

function playFlash(flashItem, button){
  var mObj = getFlashMovieObject("flashItem"+flashItem);
  mObj.Play();
  button.onclick=function(){stopFlash(flashItem, button);};
  button.title="$msg.get('rve.content.view.pause_button')";
  button.innerHTML="$msg.get('rve.content.view.pause_button')";
  button.className="button button-confirm mgn-rt";
}

function stopFlash(flashItem, button){
  var mObj = getFlashMovieObject("flashItem"+flashItem);
  mObj.StopPlay();
  button.onclick=function(){playFlash(flashItem, button);};
  button.title="$msg.get('rve.content.view.play_button')";
  button.innerHTML="$msg.get('rve.content.view.play_button')";
  button.className="button button-confirm mgn-rt";
}
