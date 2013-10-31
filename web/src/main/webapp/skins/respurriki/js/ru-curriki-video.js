// needs two configurations:
// -- window.jwplayer.key= "---"; (obtained when you download jwplayer)
// -- window.videoPrefixToDownload = "http://media.dev.curriki.org/---"; (the video hosting server, including jwplayer code and videos)


function videoInsert(videoId, title, rsrcName) {
    // insert script
    var sizeScript = document.createElement('script'); sizeScript.type = 'text/javascript';
    sizeScript.src = window.videoPrefixToDownload + videoId + "-sizes.js";
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(sizeScript, s);

    if(typeof(window.videoTitles)!="object") window.videoTitles = new Object();
    if(typeof(window.videoFullNames)!="object") window.videoFullNames= new Object();
    window.videoTitles[videoId] = title;
    window.videoFullNames[videoId] = rsrcName;
    window.setTimeout("videoWatchSizesArrived('"+videoId+"');", 50)
}


function videoWatchSizesArrived(videoId) {
    var candidateValue = window["video_" + videoId + "_sizes"];
    window.numWatches = window.numWatchers || new Object();
    if(typeof(window.numWatches[videoId])!="number") window.numWatches[videoId] = 0;
    if(candidateValue) {
        videoNotifyVideoSizeArrived(videoId, candidateValue);
    } else {
        if(window.numWatches[videoId]<500) {
            var timeout = 50;
            window.numWatches[videoId] = window.numWatches[videoId] + 1;
            if(window.numWatches[videoId]>200) timeout = timeout*5;
            window.setTimeout("videoWatchSizesArrived('"+videoId+"');", timeout);
        }
    }
}

function videoNotifyVideoSizeArrived(videoId, sources) {
    var im = jQuery("#video_img_" + videoId+"_image");
    if(typeof(sources)=="string") {
        if(console) console.log("size is still a string, display it: " + sources);
        if(im) {
            im=im.parent();
            im.width(320); im.height(80);
            var m = _(sources);
            var mailTo = _('video.errors.reportErrorsToEmail');
            mailTo = "mailto:" + _('video.errors.reportErrorsToEmail') + '?subject=' + encodeURI(_(m)) + '&body=' + encodeURI(_(sources + ".details", [mailTo, videoId]));
            if(sources.startsWith("video.errors.") || sources.startsWith("video.processingMessages"));
                m = m + "</p><p style='font-size:small'>" + _(sources + ".details", [mailTo, videoId]);
            im.html("<div width='320' height='240'><p>"+m+"</p></div>")
        }
    } else if (typeof(sources)=="object") {
        // what space do we have around?
        var maxWidth = jQuery(window).width(),
            maxHeight = jQuery(window).height();

        // find the sizes that's most appropriate
        var distance = 1000000, chosen = 0;
        for(i=0; i<sources.length; i++) {
            var d = Math.max(Math.max(Math.abs(maxWidth-sources[i].width), Math.abs(maxHeight-sources[i].height)));
            if(console) console.log("Distance: " + d + " for position " + i, sources[i]);
            if(d<distance) {
                distance = d; chosen = i;
            }
        }

        if(im) {
            im.width(sources[chosen].width); im.height(sources[chosen].height);
            im.attr("src",   window.videoPrefixToDownload +  sources[chosen].image);
            if(console) console.log("resized img to that of "+chosen+" " + sources[chosen].width + ":" + sources[chosen].height );
        }
        var div = jQuery("#video_div_" + videoId);
        if(div) {
            div.width(sources[chosen].width+4);
            div.height(sources[chosen].height+4);
            if(console) console.log("resized div to "+ jQuery("#video_div_" + videoId).width() + ":"
                + jQuery("#video_div_" + videoId).height() );
        }
        for(i=0; i<sources.length; i++) {
            var s = sources[i];
            s.file = window.videoPrefixToDownload + s.file;
        }
        var rsrcName = window.videoFullNames[videoId];
        var sharingURL = "http://"+ location.host + "/xwiki/bin/view/" + rsrcName.replace('.','/')  +"?viewer=embed";
        var sharingCode = "<iframe width='558' height='490' \n src='"+sharingURL+"'></iframe>";

        if(chosen!=0) {
            var temp = sources[chosen];
            sources[chosen] = sources[0];
            sources[0] = temp;
            window.videoSources = sources;
        }
        jwplayer("video_div_" + videoId).setup({
            playlist: [{
                image: window.videoPrefixToDownload + sources[chosen].image,
                sources: sources,
                //title: window.videoTitles[videoId],
                width: sources[chosen].width,
                height: sources[chosen].height
            }],
            ga: {},
            sharing: {
                code: encodeURI(sharingCode),
                link: sharingURL,
                title: _('video.sharing.title')
            }
        });
    }
    var origPath = window['video_' + videoId + "_originalName"];
    if(origPath) {
        jQuery("#download_original_"+videoId+"_div").show();
        var extension = origPath.substring(origPath.lastIndexOf('.')+1);
        jQuery("#download_original_"+videoId+"_div").addClass("filetype-" + extension);
        jQuery("#video_download_link_" + videoId).attr("href",
            window.videoPrefixToDownload.replace('/deliver/', '/original/') + origPath + "?forceDownload=1");
        jQuery("video_download_link_" + videoId + "_text").attr("href",
            window.videoPrefixToDownload.replace('/deliver/', '/original/') + origPath + "?forceDownload=1");
    }

}

function videoDownloadOriginal(videoId) {
    var p = window['video_' + videoId + "_originalName"];
    location.href= window.videoPrefixToDownload.replace('/deliver/', '/original/') + p + "?forceDownload=1";
    return false;
}
