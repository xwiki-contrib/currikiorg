
Ext.ns('Curriki.ui.login');
Curriki.ui.login.displayLoginDialog = function(url) {
    if(Curriki.ui.login.loginDialog && window.opener.top.Curriki.ui.login.loginDialog.isVisible())
        Curriki.ui.login.loginDialog.hide();
    var w = 700, h=500;
    if(window.innerHeight && window.innerHeight <h) h = Math.round(window.innerHeight*0.9);
    if(window.innerWidth && window.innerWidth<w)   w = Math.round(window.innerWidth*0.95);
    if(url.indexOf('?')>=0) url = url+"&framed=true"; else url=url+"?framed=true";
    Curriki.ui.login.loginDialog = new Ext.Window({
                width:w,
                height:h,
                headerCls: "registration-dialog-header",
                modal:true,
                closable:true,
                monitorResize: true,
                scrollbars: true,
                title:_("join.login.title"),
                html: "<iframe id='loginIframe' src='"+url+"' width='"+w+"' height='"+h+"'/>"
            });
    Curriki.ui.login.loginDialog.show();
    return Ext.get("loginIframe").dom.contentWindow;
};

Curriki.ui.login.popupPopupAndIdentityAuthorization = function(provider, requestURL) {
    try {
        if (console) console.log("Opening pop-up that will request authorization.");
        var dialog = Curriki.ui.login.displayLoginDialog("/xwiki/bin/view/Registration/RequestAuthorization?xpage=popup&provider=" + provider + "&to=" + encodeURIComponent(requestURL))
        Curriki.ui.login.popupIdentityAuthorization2(requestURL,dialog);
    } catch(e) { console.log(e); }
}
Curriki.ui.login.popupIdentityAuthorization = function(requestURL) {
    Curriki.ui.login.popupIdentityAuthorization2(requestURL, null);
}
Curriki.ui.login.popupIdentityAuthorization2 = function(requestURL, windowThatShouldNextGoTo) {
    // called from the login-or-register dialog or from the in-header-icons
    if(console) console.log("Opening authorization.");
    window.name='curriki-login-dialog';
    var otherWindow = window.open(requestURL,'curriki-login-authorize');
    window.Curriki.ui.login.authorizeDialog = otherWindow;
    if(windowThatShouldNextGoTo) window.Curriki.ui.login.windowThatShouldNextGoTo = windowThatShouldNextGoTo;
    return false;
};

 Curriki.ui.login.finishAuthorizationPopup = function(targetURL, openerWindow, openedWindow) {
    if(console) console.log("Finishing popup.");
    if(openerWindow && openerWindow.Curriki.ui.login.authorizeDialog &&
            openerWindow.Curriki.ui.login.authorizeDialog==window) {
        // we are in a popup relationship, can close and revert to that popup
        if(console) console.log("We are in popup, closing and opening popup.");
        var targetWindow = openerWindow;
        if(openerWindow.Curriki.ui.login.windowThatShouldNextGoTo)
            targetWindow = openerWindow.Curriki.ui.login.windowThatShouldNextGoTo;
        targetWindow.location.href = targetURL;
        // schedule a close
        openedWindow.setInterval(function() {
           targetWindow.focus();
           openedWindow.close();
        },20);
        return false;
    } else {
        if(console) console.log("No popup parent found... ah well.");
        openedWindow.location.href = targetURL;
    }
}




Curriki.ui.login.makeSureWeAreFramed = function(framedContentURL) {
    try { // we are in the same protocol as window.opener
        // we need to satisfy window.name='curriki-identity-dialog-popup' and
        //    window.parent.name='curriki-identity-dialog'
        if (window.name != 'curriki-identity-dialog-popup' || window.parent == window) {
            if (console) console.log("Redirecting to " + framedContentURL)
            window.opener.location.replace(framedContentURL);
            window.setInterval("window.close();", 50);
            return;
        }
    } catch(e) {
        console.log("Failed evaluating: " + e);
    }

    try { // we are in a different protocol as window.opener but the parent is probably good
        if (window.opener!=window &&
              (window.opener.name == 'curriki-identity-dialog' || window.opener.top.Curriki.ui.login.loginDialog.isVisible())) {
            if (console) console.log("Sending to the right frame " + framedContentURL)
            window.opener.Curriki.ui.login.displayLoginDialog(framedContentURL);
            window.setInterval("window.close();", 20);
            return;
        }
    } catch(e) {
        console.log("Failed evaluating: " + e);
    }
    if(window.top!=window) {
        window.open("/xwiki/bin/view/Registration/JumpToFramed?redir=" + encodeURIComponent(framedContentURL),"_top")
    } else {
        Ext.onReady(function() {
            Curriki.ui.login.displayLoginDialog(framedContentURL);
        });
    }
};


Ext.namespace("Curriki.ui.login.liveValidation");
Curriki.ui.login.liveValidation = function() {
    var idOfUsername = null, idOfEmail = null;
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




        activate:function(idOfUsernameInput, idOfEmailInput) {
            // disable flashy XHR witness
            Ext.Ajax.purgeListeners();

            idOfUsername = idOfUsernameInput;
            idOfEmail = idOfEmailInput;
            var i=Ext.get(idOfEmailInput), j = Ext.get(idOfUsername);
            Ext.each([i,j], function(x) {
                console.log("Registering on " + x);
                if(x.purgeListeners) x.purgeListeners();
                x.addListener("blur", function(evt) {
                    console.log("Focus-out...")
                    Curriki.ui.login.liveValidation.queueQueryNow(x);
                    Curriki.ui.login.liveValidation.stopPolling();
                });
                x.addListener("focus", function(evt) {
                    console.log("Focus-in...")
                    var handle=window.setInterval(function() {
                        clearInterval(handle);
                        Curriki.ui.login.liveValidation.startPollingTextField(x);
                    }, 50);
                });
            });
        }
        , queueQueryNow: function(inputElt) {
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
            console.log("Start polling.");
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
            var value = input.getValue();
            if(value) {
                if(t.lastValue) {
                    if(value!=t.lastValue) {
                        t.lastValue = value;
                        t.lastChanged = now;
                    } else { // same value: act if nothing happened since 200ms
                        if(t.lastChanged && now-t.lastChanged>200 &&
                                (typeof(t.queriedValue)=="undefined" || t.queriedValue!=value))
                            t.queueQueryNow(input);
                    }
                } else {
                    t.lastValue = value;
                    t.lastChanged = now;
                }
            } // otherwise can't do much... no value

        }

    };
}();
