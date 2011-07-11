
Ext.ns('Curriki.ui.login');
Curriki.ui.login.displayLoginDialog = function(url) {
    var w = 700, h=500;
    if(url.indexOf('?')>=0) url = url+"&framed=true"; else url=url+"?framed=true";
    Curriki.ui.login.loginDialog = new Ext.Window({
                width:w,
                height:h,
                modal:true,
                closable:true,
                monitorResize: true,
                scrollbars: true,
                title:"Login or Register",
                html: "<iframe id='loginIframe' src='"+url+"' width='"+w+"' height='"+h+"'/>"
            });
    Curriki.ui.login.loginDialog.show();
};
