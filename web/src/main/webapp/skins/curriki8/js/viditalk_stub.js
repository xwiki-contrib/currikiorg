var global_components_domain,
    CONST_COMPONENTS_FLASH_FOLDER,
    IS_VIDICOMPONENTS_INITIATED;
if ("undefined" === typeof GLOBAL_FLAG_LOADED_VIDI_EMBED){
	// Stub functions for when VIDITalk cannot be reached.
	function viditalk_down(div_id){
		document.getElementById(div_id).innerHTML="VIDITalk's server is currently down. Please try again later.";
	}
	function embedVidiPlayback(div_id, sc, mediaId, autoplay, width, height, stretchPlayer, skinLoc, shareUrl, allowFullScreen){
		viditalk_down(div_id);
	}
	function embedVidiCapture(div_id, sc, captureType, autoPreview, allowImages, maxFilesizeMB, bypassPreview, allowPlaylists, bgFillColor, bgBorderColor, recLimit, horizontalPlaylist, allowActiveX){
		viditalk_down(div_id);
	}

	global_components_domain="components.viditalk.com";
	CONST_COMPONENTS_FLASH_FOLDER="flash";
	IS_VIDICOMPONENTS_INITIATED=false;
}
