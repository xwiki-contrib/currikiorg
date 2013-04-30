// needs two configurations:
// -- window.jwplayer.key= "---"; (obtained when you download jwplayer)
// -- window.videoPrefixToDownload = "http://media.dev.curriki.org/---"; (the video hosting server, including jwplayer code and videos)


function videoInsert(videoId, title) {
    // insert script
    var sizeScript = document.createElement('script'); sizeScript.type = 'text/javascript';
    sizeScript.src = window.videoPrefixToDownload + videoId + "-sizes.js";
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(sizeScript, s);

    if(typeof(window.videoTitles)!="object") window.videoTitles = new Object();
    window.videoTitles[videoId] = title;
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
    var im = Ext.get("video_img_" + videoId+"_image");
    if(typeof(sources)=="string") {
        if(console) console.log("size is still a string, display it: " + sources);
        if(im) {
            im=im.parent();
            im.setSize(320, 80);
            var m = _(sources);
            if(sources.startsWith("video.errors."));
                m = m + "</p><p style='font-size:small'>" + _(sources + ".details");
            im.update("<div width='320' height='240'><p>"+m+"</p></div>")
        }
    } else if (typeof(sources)=="object") {
        if(im) {
            im.setSize(sources[0].width, sources[0].height);
            im.dom.setAttribute("src",   window.videoPrefixToDownload +  sources[0].image);
        }
        for(var i=0; i<sources.length; i++) {
            var s = sources[i];
            s.file = window.videoPrefixToDownload + s.file;
        }
        jwplayer("video_div_" + videoId).setup({
            playlist: [{
                image: window.videoPrefixToDownload + sources[0].image,
                sources: sources,
                //title: window.videoTitles[videoId],
                width: sources[0].width,
                height: sources[0].height
            }]
        });
    }
    var origPath = window['video_' + videoId + "_originalName"];
    if(origPath) {
        Ext.get("download_original_"+videoId+"_div").setVisible(true);
        var extension = origPath.substring(origPath.lastIndexOf('.')+1);
        Ext.get("download_original_"+videoId+"_div").addClass("filetype-" + extension)
        Ext.get("video_download_link_" + videoId).dom.setAttribute("href",
            window.videoPrefixToDownload.replace('/deliver/', '/original/') + origPath + "?forceDownload=1");
        Ext.get("video_download_link_" + videoId + "_text").dom.setAttribute("href",
            window.videoPrefixToDownload.replace('/deliver/', '/original/') + origPath + "?forceDownload=1");
    }
    //jwplayer("video_div_" + videoId).onQualityChange(videoQualityChange);

}

/* Currently ignored: was used to pop up the video when the quality change is requested.
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
*/

function videoDownloadOriginal(videoId) {
    var p = window['video_' + videoId + "_originalName"];
    location.href= window.videoPrefixToDownload.replace('/deliver/', '/original/') + p + "?forceDownload=1";
    return false;
}

function videoDisplayEmbedCode(rsrcName) {
    var code="  <iframe width='558' height='490' \n src='http://"+ location.host + "/xwiki/bin/view/" + rsrcName.replace('\\.','/')  +"?viewer=embed'></iframe>";
    code = "<div style='margin:1em'><h1>"+_("video.embed.title")+"</h1><p>"+_("video.embed.intro")+ "</p>" +
        "<code>\n" + code.replace(/&/g,"&amp;").replace(/</g, "&lt;").replace(/>/g,"&gt;")+
        "</code>" +
        "<p align='right'><span><input type='button' class='button-grey' value='" + _("video.embed.okButton") + "' style='padding: 3pt 6pt; font-size: 11px;' onclick='window.embedDialog.close()'/></span></span></p></div>";
    window.embedDialog = new Ext.Window({
        title:_("video.embed.title"),
        border:false,
        id: 'embedDialog',
        scrollbars: false
        ,modal:true
        ,width: 720
        ,minWidth:500
        ,minHeight:400
        ,maxHeight:575
        ,autoScroll:false
        ,constrain:true
        ,collapsible:false
        ,closable:true
        ,resizable:true
        ,shadow:false
        ,defaults:{border:false}
        ,html: code
    });
    window.embedDialog.show();
    return false;
}