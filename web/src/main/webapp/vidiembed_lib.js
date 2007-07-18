/**
 * SWFObject v1.4.4: Flash Player detection and embed - http://blog.deconcept.com/swfobject/
 *
 * SWFObject is (c) 2006 Geoff Stearns and is released under the MIT License:
 * http://www.opensource.org/licenses/mit-license.php
 *
 * **SWFObject is the SWF embed script formerly known as FlashObject. The name was changed for
 *   legal reasons.
 */
if(typeof deconcept == "undefined") var deconcept = new Object();
if(typeof deconcept.util == "undefined") deconcept.util = new Object();
if(typeof deconcept.SWFObjectUtil == "undefined") deconcept.SWFObjectUtil = new Object();
deconcept.SWFObject = function(swf, id, w, h, ver, c, useExpressInstall, quality, xiRedirectUrl, redirectUrl, detectKey){
	if (!document.getElementById) { return; }
	this.DETECT_KEY = detectKey ? detectKey : 'detectflash';
	this.skipDetect = deconcept.util.getRequestParameter(this.DETECT_KEY);
	this.params = new Object();
	this.variables = new Object();
	this.attributes = new Array();
	if(swf) { this.setAttribute('swf', swf); }
	if(id) { this.setAttribute('id', id); }
	if(w) { this.setAttribute('width', w); }
	if(h) { this.setAttribute('height', h); }
	if(ver) { this.setAttribute('version', new deconcept.PlayerVersion(ver.toString().split("."))); }
	this.installedVer = deconcept.SWFObjectUtil.getPlayerVersion();
	if(c) { this.addParam('bgcolor', c); }
	var q = quality ? quality : 'high';
	this.addParam('quality', q);
	this.setAttribute('useExpressInstall', useExpressInstall);
	this.setAttribute('doExpressInstall', false);
	var xir = (xiRedirectUrl) ? xiRedirectUrl : window.location;
	this.setAttribute('xiRedirectUrl', xir);
	this.setAttribute('redirectUrl', '');
	if(redirectUrl) { this.setAttribute('redirectUrl', redirectUrl); }
}
deconcept.SWFObject.prototype = {
	setAttribute: function(name, value){
		this.attributes[name] = value;
	},
	getAttribute: function(name){
		return this.attributes[name];
	},
	addParam: function(name, value){
		this.params[name] = value;
	},
	getParams: function(){
		return this.params;
	},
	addVariable: function(name, value){
		this.variables[name] = value;
	},
	getVariable: function(name){
		return this.variables[name];
	},
	getVariables: function(){
		return this.variables;
	},
	getVariablePairs: function(){
		var variablePairs = new Array();
		var key;
		var variables = this.getVariables();
		for(key in variables){
			variablePairs.push(key +"="+ variables[key]);
		}
		return variablePairs;
	},
	getSWFHTML: function() {
		var swfNode = "";
		if (navigator.plugins && navigator.mimeTypes && navigator.mimeTypes.length) { // netscape plugin architecture
			if (this.getAttribute("doExpressInstall")) { this.addVariable("MMplayerType", "PlugIn"); }
			swfNode = '<embed type="application/x-shockwave-flash" src="'+ this.getAttribute('swf') +'" width="'+ this.getAttribute('width') +'" height="'+ this.getAttribute('height') +'"';
			swfNode += ' id="'+ this.getAttribute('id') +'" name="'+ this.getAttribute('id') +'" ';
			var params = this.getParams();
			 for(var key in params){ swfNode += [key] +'="'+ params[key] +'" '; }
			var pairs = this.getVariablePairs().join("&");
			 if (pairs.length > 0){ swfNode += 'flashvars="'+ pairs +'"'; }
			swfNode += '/>';
		} else { // PC IE
			if (this.getAttribute("doExpressInstall")) { this.addVariable("MMplayerType", "ActiveX"); }
			swfNode = '<object id="'+ this.getAttribute('id') +'" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="'+ this.getAttribute('width') +'" height="'+ this.getAttribute('height') +'">';
			swfNode += '<param name="movie" value="'+ this.getAttribute('swf') +'" />';
			var params = this.getParams();
			for(var key in params) {
			 swfNode += '<param name="'+ key +'" value="'+ params[key] +'" />';
			}
			var pairs = this.getVariablePairs().join("&");
			if(pairs.length > 0) {swfNode += '<param name="flashvars" value="'+ pairs +'" />';}
			swfNode += "</object>";
		}
		return swfNode;
	},
	write: function(elementId){
		if(this.getAttribute('useExpressInstall')) {
			// check to see if we need to do an express install
			var expressInstallReqVer = new deconcept.PlayerVersion([6,0,65]);
			if (this.installedVer.versionIsValid(expressInstallReqVer) && !this.installedVer.versionIsValid(this.getAttribute('version'))) {
				this.setAttribute('doExpressInstall', true);
				this.addVariable("MMredirectURL", escape(this.getAttribute('xiRedirectUrl')));
				document.title = document.title.slice(0, 47) + " - Flash Player Installation";
				this.addVariable("MMdoctitle", document.title);
			}
		}
		if(this.skipDetect || this.getAttribute('doExpressInstall') || this.installedVer.versionIsValid(this.getAttribute('version'))){
			var n = (typeof elementId == 'string') ? document.getElementById(elementId) : elementId;
			n.innerHTML = this.getSWFHTML();
		/*	if(!(navigator.plugins && navigator.mimeTypes.length)) window[this.getAttribute('id')] = document.getElementById(this.getAttribute('id')); */
			return true;
		}else{
			if(this.getAttribute('redirectUrl') != "") {
				document.location.replace(this.getAttribute('redirectUrl'));
			}
		}
		return false;
	}
}

/* ---- detection functions ---- */
deconcept.SWFObjectUtil.getPlayerVersion = function(){
	var PlayerVersion = new deconcept.PlayerVersion([0,0,0]);
	if(navigator.plugins && navigator.mimeTypes.length){
		var x = navigator.plugins["Shockwave Flash"];
		if(x && x.description) {
			PlayerVersion = new deconcept.PlayerVersion(x.description.replace(/([a-zA-Z]|\s)+/, "").replace(/(\s+r|\s+b[0-9]+)/, ".").split("."));
		}
	}else{
		// do minor version lookup in IE, but avoid fp6 crashing issues
		// see http://blog.deconcept.com/2006/01/11/getvariable-setvariable-crash-internet-explorer-flash-6/
		try{
			var axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
		}catch(e){
			try {
				var axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");
				PlayerVersion = new deconcept.PlayerVersion([6,0,21]);
				axo.AllowScriptAccess = "always"; // throws if player version < 6.0.47 (thanks to Michael Williams @ Adobe for this code)
			} catch(e) {
				if (PlayerVersion.major == 6) {
					return PlayerVersion;
				}
			}
			try {
				axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
			} catch(e) {}
		}
		if (axo != null) {
			PlayerVersion = new deconcept.PlayerVersion(axo.GetVariable("$version").split(" ")[1].split(","));
		}
	}
	return PlayerVersion;
}
deconcept.PlayerVersion = function(arrVersion){
	this.major = arrVersion[0] != null ? parseInt(arrVersion[0]) : 0;
	this.minor = arrVersion[1] != null ? parseInt(arrVersion[1]) : 0;
	this.rev = arrVersion[2] != null ? parseInt(arrVersion[2]) : 0;
}
deconcept.PlayerVersion.prototype.versionIsValid = function(fv){
	if(this.major < fv.major) return false;
	if(this.major > fv.major) return true;
	if(this.minor < fv.minor) return false;
	if(this.minor > fv.minor) return true;
	if(this.rev < fv.rev) return false;
	return true;
}
/* ---- get value of query string param ---- */
deconcept.util = {
	getRequestParameter: function(param) {
		var q = document.location.search || document.location.hash;
		if(q) {
			var pairs = q.substring(1).split("&");
			for (var i=0; i < pairs.length; i++) {
				if (pairs[i].substring(0, pairs[i].indexOf("=")) == param) {
					return pairs[i].substring((pairs[i].indexOf("=")+1));
				}
			}
		}
		return "";
	}
}
/* fix for video streaming bug */
deconcept.SWFObjectUtil.cleanupSWFs = function() {
	if (window.opera || !document.all) return;
	var objects = document.getElementsByTagName("OBJECT");
	for (var i=0; i < objects.length; i++) {
		objects[i].style.display = 'none';
		for (var x in objects[i]) {
			if (typeof objects[i][x] == 'function') {
				objects[i][x] = function(){};
			}
		}
	}
}
// fixes bug in fp9 see http://blog.deconcept.com/2006/07/28/swfobject-143-released/
deconcept.SWFObjectUtil.prepUnload = function() {
	__flash_unloadHandler = function(){};
	__flash_savedUnloadHandler = function(){};
	if (typeof window.onunload == 'function') {
		var oldUnload = window.onunload;
		window.onunload = function() {
			deconcept.SWFObjectUtil.cleanupSWFs();
			oldUnload();
		}
	} else {
		window.onunload = deconcept.SWFObjectUtil.cleanupSWFs;
	}
}
if (typeof window.onbeforeunload == 'function') {
	var oldBeforeUnload = window.onbeforeunload;
	window.onbeforeunload = function() {
		deconcept.SWFObjectUtil.prepUnload();
		oldBeforeUnload();
	}
} else {
	window.onbeforeunload = deconcept.SWFObjectUtil.prepUnload;
}
/* add Array.push if needed (ie5) */
if (Array.prototype.push == null) { Array.prototype.push = function(item) { this[this.length] = item; return this.length; }}

/* add some aliases for ease of use/backwards compatibility */
var getQueryParamValue = deconcept.util.getRequestParameter;
var FlashObject = deconcept.SWFObject; // for legacy support
var SWFObject = deconcept.SWFObject;

var componentVer="1.79";
var fo;
var flashLoaded = false;
var called_once=false;
var capture_sitecode=-1;
var capture_div = "";
var capture_type;
var auto_preview;
var allow_images;
var max_filesize_mb;
var bypass_preview;
var allow_playlists;
var bg_fill_color;
var bg_border_color;
var rec_limit;
var initial_playlist = null;
var horizontal_playlist = null;

function ge()
{var ea;for(var i=0;i<arguments.length;i++){var e=arguments[i];if(typeof e=='string')
e=document.getElementById(e);if(arguments.length==1)
return e;if(!ea)
ea=new Array();ea[ea.length]=e;}
return ea;}


function thisMovie(movieName) {

 if (window.document[movieName]) 
  {
      return window.document[movieName];
  }
  if (navigator.appName.indexOf("Microsoft Internet")==-1)
  {
    if (document.embeds && document.embeds[movieName])
      return document.embeds[movieName]; 
  }
  else
  {
    return document.getElementById(movieName);
  } 	
}

function ReloadPage(id, initialPlaylist)
{
	initial_playlist = initialPlaylist;
	try { captureReloaded(); } catch(e) {}
	embedVidiCapture(capture_div, capture_sitecode, capture_type, auto_preview, allow_images, max_filesize_mb, bypass_preview, allow_playlists, bg_fill_color, bg_border_color, rec_limit, horizontal_playlist);
}
function PrePost()
{
	thisMovie("VIDITalkPublishControlv1").SetVariable("prePostVar", "True");
}
function PostVideo(id)
{
	if (!called_once)
	{
		uploadComplete(id);
	}
}
function addToPlaylist(videoid)
{
	thisMovie("VIDITalkPublishControlv1").SetVariable("nextPlaylistVideoId", videoid);

}
function playlistEmpty()
{
	thisMovie("VIDITalkPublishControlv1").SetVariable("playlistEmptyVar", "True");
}
function restartCapture()
{
	thisMovie("VIDITalkPublishControlv1").SetVariable("restartCaptureVar", "True");
}
function embedVidiCapture(div_id, sc, captureType, autoPreview, allowImages, maxFilesizeMB, bypassPreview, allowPlaylists, bgFillColor, bgBorderColor, recLimit, horizontalPlaylist)
{
	//window["VIDITalkPublishControlv1"] = new Object();
	if ((capture_div.length > 0) && (capture_div != div_id))
		alert("error: only one capture component per html page is supported.");

	capture_sitecode = sc;
	capture_div = div_id;
	capture_type = captureType;
	auto_preview = autoPreview;
	allow_images = allowImages;
	max_filesize_mb = maxFilesizeMB;
	bypass_preview = bypassPreview;
	allow_playlists = allowPlaylists;
	bg_fill_color = bgFillColor;
	bg_border_color = bgBorderColor;
	rec_limit = recLimit;
	horizontal_playlist = horizontalPlaylist;

	var panelWidth = "340";
	var panelHeight = "313";
	if ((allowPlaylists != null) && (allowPlaylists))
	{
	        if ((horizontalPlaylist != null) && (horizontalPlaylist))
		{
			panelWidth = "340";
			panelHeight = "505";
		}
		else
		{
			panelWidth = "490";
			panelHeight = "313";

		}
	}

	fo = new SWFObject( "http://components.viditalk.com/flash/vidipublish.swf?componentVer="+componentVer,"VIDITalkPublishControlv1", panelWidth, panelHeight, "8", "", false, "best" );
	fo.addParam("allowScriptAccess", "always");
	fo.addParam("wmode", "transparent");
	fo.addParam("recTime", "600");
	fo.addVariable("sitecode", sc);

        if (captureType != null)
		fo.addVariable("captureType", captureType);
        if ((autoPreview != null) && (autoPreview))
		fo.addVariable("autoPreview", "True");
        if ((allowImages != null) && (!allowImages))
		fo.addVariable("allowImages", "False");
        if (maxFilesizeMB != null)
		fo.addVariable("maxFilesizeMB", maxFilesizeMB);
        if ((bypassPreview != null) && (bypassPreview))
		fo.addVariable("bypassPreview", "True");
        if ((allowPlaylists != null) && (allowPlaylists))
		fo.addVariable("allowPlaylists", "True");
        if (bgFillColor != null)
		fo.addVariable("bgFill", "0x"+bgFillColor);
        if (bgBorderColor != null)
		fo.addVariable("bgBorder", "0x"+bgBorderColor);
        if (recLimit != null)
		fo.addVariable("recLimit", recLimit);
        if ((horizontalPlaylist != null) && (horizontalPlaylist))
	        fo.addVariable("horizontalPlaylist", "True");
        if (initial_playlist != null)
		fo.addVariable("initialPlaylist", initial_playlist);

	if (!flashLoaded)
	{
		var htmlCode = 	"<div style=\"position:relative;width:"+panelWidth+"px\">";
		htmlCode +=    		"<div id=\"html_upload\" style=\"position:absolute;height:270px;width:313px;background-color:#CCCCCC;left:15px;overflow:hidden;top:22px;z-index:123456789;\">";
		htmlCode += 			"<iframe id=\"uploadIFrame\"  width=\"313\" frameborder=\"0\" scrolling=\"no\" allowtransparency=\"yes\" height=\"270\" src=\"\"></iframe>";
		htmlCode += 		"</div>";
		htmlCode +=	 "<div><table cols=2><tr><td>";
		htmlCode += 		"<div id=\"leftflash\" style=\"z-index:1\">&nbsp;";
		htmlCode += 			"<div><a href=\"http://www.macromedia.com/go/getflashplayer\" target=\"_blank\"><img src=\"http://components.viditalk.com/flash/images/noflash.jpg\" border=\"0\" alt=\"The content presented here requires JavaScript to be enabled and the latest version of the Macromedia Flash Player. If you are you using a browser with JavaScript disabled please enable it now. Otherwise, please update your version of the free Flash Player\"></a></div>";
		htmlCode += 			"</div></td><td>";
		htmlCode +=			"<div id=\"rightflash\"></div>";
		htmlCode +=			"</td></tr></table>";
		htmlCode +=		"</div>";
		htmlCode += 	"</div>";
		document.getElementById(div_id).innerHTML = htmlCode;
	}

	fo.write("leftflash"); 	
	hideHtmlUpload();

	flashLoaded = true;
}

function hideHtmlUpload()
{
	document.getElementById("html_upload").style.display='none';
}

function showHtmlUpload()
{
	document.getElementById("html_upload").style.display='block';
}

function httpUpload(id)
{
	document.getElementById("uploadIFrame").src = "http://transcode.viditalk.com/fileupload/upload_asp.asp?id="+id+"&sitecode="+capture_sitecode;
	showHtmlUpload();
}

function htmlUploadStatus(success)
{
	hideHtmlUpload();
}


function playbackStarting(videoid){
	
}

function captureLoaded(){

}

function embedVidiPlayback(div_id, sc, mediaId, autoplay, width, height, stretchPlayer, skinLoc, shareUrl)
{
	document.getElementById(div_id).innerHTML = "&nbsp;<div><a href=\"http://www.macromedia.com/go/getflashplayer\" target=\"_blank\"><img src=\"http://components.viditalk.com/flash/images/noflash.jpg\" border=\"0\" alt=\"The content presented here requires JavaScript to be enabled and the latest version of the Macromedia Flash Player. If you are you using a browser with JavaScript disabled please enable it now. Otherwise, please update your version of the free Flash Player\"></a></div>";
	var defwidth=320
	var defheight=280
	var skinHeight = 40;
	 if ((width != null) && (width)) defwidth=width
	 if ((height != null) && (height)) defheight=height

	fo = new SWFObject( "http://components.viditalk.com/flash/vidiplayer.swf?componentVer="+componentVer, "VIDITalkPlayerControlv1", defwidth, defheight, "7", "", false, "best" );
	fo.addParam("allowScriptAccess", "always");
	fo.addParam("wmode", "transparent");
	fo.addVariable("sitecode", sc);


	fo.addVariable("myurl", mediaId);
        if ((autoplay != null) && (autoplay))
		fo.addVariable("autoStart", "True");
        if (skinLoc != null)
	{
		fo.addVariable("skinLoc", skinLoc);
		var pos1 = skinLoc.lastIndexOf("_");
		var pos2 = skinLoc.lastIndexOf(".");
		if ((pos1 >= 0) && (pos2 > pos1))
    			skinHeight = parseInt(skinLoc.substring(pos1+1, pos2));
		if (isNaN(skinHeight))
			skinHeight = 40;
	}
	if ((stretchPlayer != null) && (!stretchPlayer))
	{
		fo.addVariable("stretchPlayer", "False");
		if ((width != null) && (width))
			fo.addVariable("videoWidth", width);
		if ((height != null) && (height)) 
			fo.addVariable("videoHeight", height-skinHeight);
	}
	if (shareUrl != null)
	{
		fo.addVariable("allowShare", "True");
		fo.addVariable("htmlurl", window.location);	
	}
	fo.write(div_id); 	
}