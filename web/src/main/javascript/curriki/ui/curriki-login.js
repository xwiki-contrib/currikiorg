// This script is the set of pages used to command the UI for the login and registration steps
Ext.ns('Curriki.ui.login');
Curriki.ui.login.displayLoginDialog = function(url) {
    if(Curriki.ui.login.loginDialog && window.opener.top.Curriki.ui.login.loginDialog.isVisible())
        Curriki.ui.login.loginDialog.hide();
    var w = 700, h=400;
    //if(window.innerHeight && window.innerHeight <h) h = Math.round(window.innerHeight*0.9);
    if(window.innerWidth && window.innerWidth<w)   w = Math.round(window.innerWidth*0.95);
    if(url.indexOf('?')>=0) url = url+"&framed=true"; else url=url+"?framed=true";
    // the default header should be blue, not green as it is in AddPath, adjust the CSS live
    var rule = ".x-window .x-window-tl, .x-panel-ghost .x-window-tl";
    if(Ext && Ext.isIE) rule=".x-window .x-window-tl";
    Ext.util.CSS.updateRule(rule,
        "background-color", "#4E83C7");
    Curriki.ui.login.loginDialog = new Ext.Window({
        title:_("join.login.title"),
        border:false,
        id: 'loginDialogWindow',
        scrollbars: false
        ,modal:true
        ,width:634
        ,minWidth:400
        ,minHeight:100
        ,maxHeight:575
        ,autoScroll:false
        ,constrain:true
        ,collapsible:false
        ,closable:false
        ,resizable:false
        , monitorResize: true
        ,shadow:false
        ,defaults:{border:false},
         html: "<iframe style='border:none' frameBorder='0' name='curriki-login-dialog' id='loginIframe' src='"+url+"' width='"+(w-5)+"' height='"+(h-31)+"'/>" //
            });
    Curriki.ui.login.loginDialog.headerCls = "gistration-dialog-header";
    Curriki.ui.login.loginDialog.show();
    return Ext.get("loginIframe").dom.contentWindow; 
};

Curriki.ui.login.ensureProperBodyCssClass = function() {
    window.onload = function() {
        try {
            if (document.body) {
                var x = document.body.className;
                if (x) {
                    document.body.className = x + " insideIframe";
                } else if(!Ext.isIE()) {
                    document.body.className = "insideIframe";
                }

            }
        } catch(e) { if(console) console.log(e); }
    };
}


Curriki.ui.login.popupPopupAndIdentityAuthorization = function(provider, requestURL, xredirect) {
    try {
        if (console) console.log("Opening pop-up that will request authorization.");
        var dialog = Curriki.ui.login.displayLoginDialog("/xwiki/bin/view/Registration/RequestAuthorization?xpage=popup&provider=" + provider + "&to=" + encodeURIComponent(requestURL) + '&xredirect=' + encodeURIComponent(xredirect))
        Curriki.ui.login.popupIdentityAuthorization2(requestURL,dialog);
    } catch(e) { console.log(e); }
}
Curriki.ui.login.popupIdentityAuthorization = function(requestURL) {
    Curriki.ui.login.popupIdentityAuthorization2(requestURL, null);
}
Curriki.ui.login.popupIdentityAuthorization2 = function(requestURL, windowThatShouldNextGoTo) {
    // called from the login-or-register dialog or from the in-header-icons
    if(console) {console.log("Opening authorization to " + requestURL); }
    window.name='curriki-login-dialog';
    var otherWindow;
    if(window.frames['curriki_login_authorize']) {
        if(console) console.log("Re-using window.");
        otherWindow = window.frames['curriki_login_authorize'];
        otherWindow.location.href= requestURL;
        otherWindow.focus();
    } else {
        if(console) console.log("Creating window.");
        otherWindow = window.open(requestURL,'curriki_login_authorize',"toolbar=no,status=yes,menubar=no,resizable=yes,width=750,height=550");
    }
    window.Curriki.ui.login.authorizeDialog = otherWindow;
    if(windowThatShouldNextGoTo) window.Curriki.ui.login.windowThatShouldNextGoTo = windowThatShouldNextGoTo;
    return false;
};

 Curriki.ui.login.finishAuthorizationPopup = function(targetURL, openerWindow, openedWindow, toTop) {
    if(console) console.log("Finishing popup.");
    if(openerWindow && openerWindow.Curriki.ui.login.authorizeDialog &&
            openerWindow.Curriki.ui.login.authorizeDialog==window) {
        // we are in a popup relationship, can close and revert to that popup
        if(console) console.log("We are in popup, closing and opening popup.");
        var targetWindow = openerWindow;
        if(openerWindow.Curriki.ui.login.windowThatShouldNextGoTo)
            targetWindow = openerWindow.Curriki.ui.login.windowThatShouldNextGoTo;
        if(toTop) targetWindow = targetWindow.top;
        targetWindow.location.href = targetURL;
        //alert("Would go to " + targetURL + " from " + targetWindow);
        // schedule a close
        openedWindow.setInterval(function() {
           targetWindow.focus();
           openedWindow.close();
        },20);
        return false;
    } else {
        if(console) console.log("No popup parent found... ah well.");
        openedWindow.location.href = targetURL;
        //alert("Would go to " + targetURL + " from " + openedWindow);
    }
}




Curriki.ui.login.makeSureWeAreFramed = function(framedContentURL) {
    if(window==window.top) {
        if(!framedContentURL || framedContentURL==null) framedContentURL = window.location.href;
        Curriki.ui.login.displayLoginDialog(framedContentURL);
    } else if (window.name != 'curriki-login-dialog' && framedContentURL && framedContentURL!=null) {
        if (console) console.log("Redirecting to " + framedContentURL)
        window.opener.location.replace(framedContentURL);
        window.setInterval("window.close();", 50);
        return;
    }

};




Ext.namespace("Curriki.ui.login.liveValidation");
Curriki.ui.login.liveValidation = function() {
    var queue = new Array();

    return {
        queue: queue,


        launchCheckFieldRequest: function(value, field, queueEntry) {
            Curriki.ui.login.liveValidation.notifyValidationResult(field, "waiting");
            var r = Ext.Ajax.request({
                url: "/xwiki/bin/view/Registration/CheckValid"
                ,headers: {'Accept':'application/json'}
                ,method: "GET"
                ,failure:function(response, options) {
                    Curriki.ui.login.liveValidation.queriedValue=queueEntry.value;
                    Curriki.ui.login.liveValidation.notifyValidationResult(field, null);
                    if(console) console.log("failed validation: ", response, options);
                }
                ,success:function(response, options){
                    var t = response.responseText;
                    if(t) t = t.trim();
                    if(console) console.log("Response: " + t);
                    queue.remove(queueEntry);
                    if(queueEntry.value!=field.getValue()) return;
                    Curriki.ui.login.liveValidation.notifyValidationResult(field, "true" == t);
                }
                , params: { what: field.dom.name,
                    value: value,
                    xpage: "plain"
                }
                , scope: this
            });
            return r;
        },

        notifyValidationResult:function(field, res) {
            /*
             Ext.get("loginIframe").dom.contentWindow.Ext.get("username_input").parent().addClass("warningField")
             */
            try {
                if (field) {
                } else {
                    if(console) console.log("Warning: missing field.");
                    return;
                }
                var pElt = field.parent();
                if (null == res) {
                    pElt.removeClass("okField");
                    pElt.removeClass("waiting");
                    pElt.removeClass("warningField");
                } else if("waiting" == res) {
                    pElt.addClass("waiting");
                } else if (true == res || "true" == res) {
                    pElt.removeClass("waiting");
                    pElt.removeClass("warningField");
                    pElt.addClass("okField");
                } else if (false == res || "false" == res) {
                    pElt.removeClass("waiting");
                    pElt.removeClass("okField");
                    pElt.addClass("warningField");
                }
            } catch(e) {
                if(console) console.log("Error: ", e)
            }
        },




        activate:function(ids) {
            // disable flashy XHR witness
            Ext.Ajax.purgeListeners();

            Ext.each(ids, function(name) {
                console.log("Registering on " + name);
                var x = Ext.get(name);
                if(x) {} else {
                    console.log("Not found: " + name);
                    return;
                }
                if(x.purgeListeners) x.purgeListeners();
                x.addListener("blur", function(evt) {
                    console.log("Focus-out...");
                    Curriki.ui.login.liveValidation.queueQueryNow(x);
                    Curriki.ui.login.liveValidation.stopPolling();
                });
                x.addListener("focus", function(evt) {
                    console.log("Focus-in...");
                    var handle=window.setInterval(function() {
                        clearInterval(handle);
                        Curriki.ui.login.liveValidation.startPollingTextField(x);
                    }, 50);
                });
            });
        }
        , queueQueryNow: function(inputElt) {
            // this is the main function to call the validation
            var fieldName = inputElt.dom.name;
            var fieldValue = inputElt.dom.value;
            console.log("Validation on field " + fieldName + " with value '" + fieldValue + "'.");
            var min_length=3;
            if(fieldName=="firsName" || fieldName=="lastName" || fieldName=="agree" || fieldName=="member_type")
                min_length=1;
            if(typeof(fieldValue)!="undefined" && fieldValue.length<=min_length) return;
            if(fieldName!="email" && fieldName!="username") {
                var passed = false;
                var silentFailure = fieldName=="firstName" || fieldName=="lastName" || fieldName=="password";
                if(fieldName=="agree") passed = fieldValue!="0";
                if(fieldName=="member_type") passed = fieldValue!="-";
                if(fieldName=="firstName" || fieldName=="lastName") passed = fieldValue.length>=1;
                if(fieldName=="password") passed = fieldValue.length>5;
                console.log("passed? " + passed + ".");
                // manual check here, just long enough
                if(passed==false)
                    if(!silentFailure) Curriki.ui.login.liveValidation.notifyValidationResult(inputElt, false);
                if(passed==true)
                    Curriki.ui.login.liveValidation.notifyValidationResult(inputElt, true);
                // missed case: silentFailure == true && passed == false (no need to bother folks for too short names)
                return;
            }
            // we're left with email and username, only check if longer than 3
            var queueEntry = new Object();
            queueEntry.value = inputElt.getValue();
            Curriki.ui.login.liveValidation.queriedValue = inputElt.getValue();
            console.log("Queuing query for " + queueEntry.value);
            if(typeof(queueEntry.value)=="undefined" || queueEntry.value==null) {
                if(console) console.log("Undefined value, stop.");
                return;
            }
            // scan the queue if there's a query with same value, bring it to front
            for(x in queue) {
                if(x.value == queueEntry.value) {
                    var i = queue.indexOf(x);
                    if(i>0) for(j=i-1; j>=0; j--) {
                        queue[j+1] = queue[j];
                    }
                    if(console) console.log("Swapping existing queue entries.")
                    queue[0] = x;
                    return;
                }
            }
            // otherwise launch request
            if(console) console.log("Launching in queue.")
            queueEntry.request = this.launchCheckFieldRequest(queueEntry.value, inputElt, queueEntry);
            // add to queue
            queue[queue.length] = queueEntry;
            // cancel any other? not now
        }

        , intervalPointer: null
        , startedPollingTime: null
        , inputFieldBeingPolled: null
        , queriedValue: null
        , lastValue: null

        , startPollingTextField: function(inputField) {
            var t = Curriki.ui.login.liveValidation;
            if(inputField) {} else {return;}
            if(t.intervalPointer && t.intervalPointer!=null)
                t.stopPolling();
            console.log("Start polling on " + t);
            t.inputFieldBeingPolled = inputField;
            t.startedPollingTime = new Date().getTime();
            t.intervalPointer = window.setInterval(t.inputFieldPoll, 50);
        }
        , stopPolling: function() {
            console.log("Stop polling.");
            try {
                var t = Curriki.ui.login.liveValidation;
                if (t.intervalPointer && t.intervalPointer != null)
                    window.clearInterval(t.intervalPointer);
                t.startedPollingTime = null;
                t.inputFieldBeingPolled = null;
            } catch(e) { console.log(e); }
        }
        , inputFieldPoll: function() {
            //console.log("poll");
            var t = Curriki.ui.login.liveValidation;
            var input = t.inputFieldBeingPolled;
            if(input) {} else {return;}
            var now = new Date().getTime();
            if(t.startedPollingTime && t.startedPollingTime==null)
                t.startedPollingTime = now;
            /* if(now - t.startedPollingTime > 30000) {
                t.stopPolling(); return;
            }*/
            var value = input.dom.value;
            // Evaluating value=asdasd@i2go.e wrt t.lastValue=asdasd@i2go.e and t.lastChanged=1314641854765 with now 1314641858380
            if(value) {
                if(t.lastValue) {
                    if(! (value==t.lastValue)) {
                        t.lastValue = value;
                        t.lastChanged = now;
                    } else { // same value: act if nothing happened since 200ms
                        if(t.lastChanged && now-t.lastChanged>200 && (t.lastChanged > t.lastChecked || t.lastChecked===undefined) &&
                                (typeof(t.queriedValue)=="undefined" || t.queriedValue!=value)) {
                            t.lastChecked = now;
                            t.queueQueryNow(input);
                        }
                    }
                } else {
                    t.lastValue = value;
                    t.lastChanged = now;
                }
            }

        }

    };
}();
