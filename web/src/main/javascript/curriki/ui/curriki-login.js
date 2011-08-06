
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
};

Curriki.ui.login.makeSureWeAreFramed = function(framedContentURL) {
    try { // we are in the same protocol as window.opener
        // we need to satisfy window.name='curriki-identity-dialog-popup' and
        //    window.parent.name='curriki-identity-dialog'
        if (window.name != 'curriki-identity-dialog-popup' || window.parent == window) {
            if (console) console.log("Redirecting to " + framedContentURL)
            window.opener.location.replace(framedContentURL);
            window.setInterval("window.close();", 20);
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
                if(console) console.log("failed validation: ", response, options);
            }
		    ,success:function(response, options){
                var t = response.responseText;
                if(t) t = t.trim();
                if(console) console.log("Response: " + t);
                Curriki.ui.login.liveValidation.notifyValidationResult(field, "true" == t);
                queue.remove(queueEntry);
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
            i.addListener("blur", function(evt) {
                console.log("Focus-out...")
                Curriki.ui.login.liveValidation.queueQueryNow(i);
            });
            j.addListener("blur", function(evt) {
                console.log("Focus-out...")
                Curriki.ui.login.liveValidation.queueQueryNow(j);
            });
            i.addListener("change", function() { console.log("change"); })
            // TODO: logic to activate after some timeout after value change
        }
        , queueQueryNow: function(inputElt) {
                var q = new Object();
                q.value = inputElt.getValue();
                if(typeof(q.value)=="undefined" || q.value==null) {
                    if(console) console.log("Undefined value, stop.");
                    return;
                }
                // scan the queue if there's a query with same value, bring it to front
                for(x in queue) {
                    if(x.value == q.value) {
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
                q.request = this.launchCheckFieldRequest(q.value, inputElt, q);
                // add to queue
                queue[queue.length] = q;
                // cancel any other? not now
        }

    };
}();

// TODO: only in 


