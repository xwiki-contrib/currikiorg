
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
        if (window.name == 'curriki-identity-dialog-popup' && window.opener != window) {
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
