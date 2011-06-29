
Ext.ns('Curriki.ui.login');
Curriki.ui.login.displayIntroStep = function() {
    var w = 700, h=300;
    Curriki.ui.login.loginDialog = new Ext.Window({
                width:w,
                height:h,
                modal:true,
                closable:true,
                monitorResize: true,
                title:"Login or Register",
                html: "<iframe id='loginIframe' src='/xwiki/bin/loginsubmit/XWiki/XWikiLogin' width='"+w+"' height='"+h+"'/>"
            });
    Curriki.ui.login.loginDialog.show();
};

