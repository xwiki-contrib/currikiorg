	Ext.ns("Curriki.module.unnominate");
	Curriki.module.unnominate.confirm =	function(){
		Ext.MessageBox.confirm(_('curriki.crs.review.information'), _('curriki.crs.review.unnominateconfirm'), function(btn){
			if (btn == "yes") {
				var page = Ext.get('assetFullName').dom.value;
				Curriki.assets.UnnominateAsset(page, function(D){
					window.location.pathname = 'xwiki/bin/view/CRS/Reviews';
				});
			}
		});
	}