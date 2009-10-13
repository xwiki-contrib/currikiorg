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
	var flashvars = {};
	var params = {
		'wmode': 'transparent'
		,'play': 'false'
		,'quality': 'autohigh'
		,'bgcolor': '#FFFFFF'
	};
	var attributes = {};

	swfobject.embedSWF(flashURL, 'flashItem'+flashNum, '450', '450', '6', 'expressInstall.swf', flashvars, params, attributes);
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
  button.className="button-link button-link-pause";
}

function stopFlash(flashItem, button){
  var mObj = getFlashMovieObject("flashItem"+flashItem);
  mObj.StopPlay();
  button.onclick=function(){playFlash(flashItem, button);};
  button.title="$msg.get('rve.content.view.play_button')";
  button.innerHTML="$msg.get('rve.content.view.play_button')";
  button.className="button-link button-link-play";
}
