	Ext.ns("Curriki.module.partner");
	Curriki.module.partner.confirm =	function(){
		Ext.Msg.show({
   			title: _('curriki.crs.review.partnerconfirmtitle'),
   			msg: _('curriki.crs.review.partnerconfirmmessage'),
   			buttons: Ext.Msg.OKCANCEL,
   			fn: function(btn){
				if (btn == "ok") {
					var page = Ext.get('assetFullName').dom.value;
					Curriki.assets.PartnerAsset(page, function(D){
						window.location.pathname = 'xwiki/bin/view/CRS/Reviews';
					});
				}
			},
   			icon: Ext.MessageBox.QUESTION
			});
	}