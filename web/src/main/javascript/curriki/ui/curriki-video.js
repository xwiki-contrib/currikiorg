// needs two configurations:
// -- window.jwplayer.key= "---"; (obtained when you download jwplayer)
// -- window.videoPrefixToDownload = "http://media.dev.curriki.org/---"; (the video hosting server, including jwplayer code and videos)


function videoInsert(videoId, title) {
    // insert script
    var sizeScript = document.createElement('script'); sizeScript.type = 'text/javascript';
    sizeScript.src = window.videoPrefixToDownload + videoId + "-sizes.js";
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(sizeScript, s);

    if(typeof(window.videoTitles)!="object") window.videoTitles = new Object();
    window.videoTitles[videoId] = title;
    window.setTimeout("videoWatchSizesArrived('"+videoId+"');", 50)
}


function videoWatchSizesArrived(videoId) {
    var candidateValue = window[videoId + "_sizes"];
    window.numWatches = window.numWatchers || new Object();
    if(typeof(window.numWatches[videoId])!="number") window.numWatches[videoId] = 0;
    if(candidateValue) {
        videoNotifyVideoSizeArrived(videoId, candidateValue);
    } else {
        if(window.numWatches[videoId]<500) {
            var timeout = 50;
            window.numWatches[videoId] = window.numWatches[videoId] + 1;
            if(window.numWatches[videoId]>200) timeout = timeout*5;
            window.setTimeout("videoWatchSizesArrived('"+videoId+"');", 50);
        }
    }
}

function videoNotifyVideoSizeArrived(videoId, sources) {
    for(var i=0; i<sources.length; i++) {
        var s = sources[i];
        s.file = window.videoPrefixToDownload + s.file;
    }
    jwplayer(videoId).setup({
        playlist: [{
            image: window.videoPrefixToDownload + sources[0].image,
            sources: sources,
            title: window.videoTitles[videoId],
            width: sources[0].width,
            height: sources[0].height
        }]
    });
    var im = Ext.get(videoId+"_image");
    if(im) {
        im.setSize(sources[0].width, sources[0].height);
        im.dom.setAttribute("src",    sources[0].image);
    }

    jwplayer(videoId).onQualityChange(videoQualityChange);

}

function videoQualityChange(evt) {
    var quality = evt.levels[evt.currentQuality];
    if(typeof(quality)!="object") return;
    var width= quality.width; var  height=quality.height;
    if(width>window.innerWidth || height>window.innerHeight) {
        var factor = Math.min(window.innerWidth/width, window.innerHeight/height);
        width = factor*width; height=factor*height;
    }
    var divElt = Ext.get(evt.id);
    if(quality.label=='hq') {
        // detach from tree, make floating
        divElt.setStyle("position","absolute");
        divElt.setBounds((window.innerWidth-width)/2, (window.innerHeight-height)/2, width, height, true);
        jwplayer(evt.id).resize(quality.width, quality.height);
    } else {
        // come back, make
        divElt.setBounds(0,0);
        divElt.setStyle("position","relative");
        jwplayer(evt.id).resize(quality.width, quality.height);
    }
}
