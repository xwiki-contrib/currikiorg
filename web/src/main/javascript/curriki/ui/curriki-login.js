
Ext.ns('Curriki.ui.login');
Curriki.ui.login.displayIntroStep = function() {
    var w = 500, h=250;
    Curriki.ui.login.loginDialog = new Ext.Window({
                width:w,
                height:h,
                modal:true,
                closable:true,
                title:"Login or Register",
                html: "<iframe src='/xwiki/bin/loginsubmit/XWiki/XWikiLogin' width='"+w+"' height='"+h+"'/>"
            });
    Curriki.ui.login.loginDialog.show();
};

