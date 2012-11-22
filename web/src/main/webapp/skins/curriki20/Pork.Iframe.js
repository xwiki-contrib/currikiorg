/*
based on prototype's && moo.fx's ajax class
to be used with prototype.lite, in conjunction with moo.AJAX
this submits an iframe invisibly to the server, and expects a JSON object in return
handy, so that you do not have to care about posting forms, urlencoding, file uploads etc.
usage: <form method='post' onsubmit='new iframe(this); return false;'>

changelog:
17-01-06: initial release
18-01-06: added options initiator
          added IE5 support
		  added Opera support
01-03-06: added WORKING safari support! Major thanks to Charles Hinshaw and Phil Barrett!

*/
document.iframeLoaders = {};

iframe = Class.create();
iframe.prototype = {
	initialize: function(form, options){
		if (!options) options = {};
		this.form = form;
		this.uniqueId = new Date().getTime();
		document.iframeLoaders[this.uniqueId] = this;
		this.transport = this.getTransport();
		this.onComplete = options.onComplete || null;
		this.update = $(options.update) || null;
		this.updateMultiple = options.multiple || false;
		form.target= 'frame_'+this.uniqueId;
		form.setAttribute("target", 'frame_'+this.uniqueId); // in case the other one fails.
		form.submit();
	},

	onStateChange: function(){
		this.transport = $('frame_'+this.uniqueId);
		try {   var doc = this.transport.contentDocument.document.body.innerHTML; this.transport.contentDocument.document.close(); }	// For NS6
		catch (e){ 
			try{ var doc = this.transport.contentWindow.document.body.innerHTML; this.transport.contentWindow.document.close(); } // For IE5.5 and IE6
			 catch (e){
				 try { var doc = this.transport.document.body.innerHTML; this.transport.document.body.close(); } // for IE5
					catch (e) {
						try	{ var doc = window.frames['frame_'+this.uniqueId].document.body.innerText; } // for really nasty browsers
						catch (e) { } // forget it.
				 }
			}
		}
		this.transport.responseText = doc;
		if (this.onComplete) setTimeout(function(){this.onComplete(this.transport);}.bind(this), 10);
		if (this.update) setTimeout(function(){this.update.innerHTML = this.transport.responseText;}.bind(this), 10);
		if (this.updateMultiple){ setTimeout(function(){ // JSON support!
				try	{ var hasscript = false; eval("var inputObject = "+this.transport.responseText);	// we're expecting a JSON object, eval it to inputObject
					for (var i in inputObject) { if (i == 'script') { hasscript = true; } // check if we passed some javascript along too
						else {if ( elm = $(i)) { elm.innerHTML = inputObject[i]; } else { alert("element "+i+" not found!"); } } // if it's not script, update the corresponding div
					} if (hasscript) eval(inputObject['script']); // some on-the-fly-javascript exchanging support too
				} catch (e) { alert('There was an error processing: '+this.transport.responseText); } // in case of an error					
			}.bind(this), 10);
		}	
	},

	getTransport: function() 
	{
		var divElm = document.createElement('DIV');
	    divElm.style.position = "absolute";
        divElm.style.top = "0";
        divElm.style.marginLeft = "-10000px";
		if (navigator.userAgent.indexOf('MSIE') > 0 && navigator.userAgent.indexOf('Opera') == -1) {// switch to the crappy solution for IE
		 divElm.innerHTML = '<iframe name=\"frame_'+this.uniqueId+'\" id=\"frame_'+this.uniqueId+'\" src=\"about:blank\" onload=\"setTimeout(function(){document.iframeLoaders['+this.uniqueId+'].onStateChange()},20);"></iframe>';
		} else {
			var frame = document.createElement("iframe");
			frame.setAttribute("name", "frame_"+this.uniqueId);
			frame.setAttribute("id", "frame_"+this.uniqueId);
			frame.addEventListener("load", 	function(){	this.onStateChange(); }.bind(this), false);
			divElm.appendChild(frame);
		}
		document.getElementsByTagName("body").item(0).appendChild(divElm);
	}
};
