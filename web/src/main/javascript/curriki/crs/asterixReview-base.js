Ext.ns("Curriki.module.asterixReview");
Curriki.module.asterixReview.selectedConfirm = function(asterixReviewValue,redirectURL) {
	Ext.Msg.show({
		title : _('curriki.crs.review.form.dialog.confirm_header'),
		msg : _('curriki.crs.review.asterixReview.selectedConfirm_'+asterixReviewValue),
		buttons : Ext.Msg.OKCANCEL,
		fn : function(btn) {
			if (btn == "ok") {
				var page = Ext.get('assetFullName').dom.value;
				Curriki.assets.SetAsterixReview(page, function(D) {
					window.location.pathname = redirectURL;
				},asterixReviewValue);
			}
			else{
				document.getElementById('asterixReview').style.display = 'none';
				document.getElementById('isChild').checked = false;
				document.getElementById('asterixReview').value = "";
			}
		},
		icon : Ext.MessageBox.QUESTION
	});
}

Curriki.module.asterixReview.removedConfirm = function(redirectURL) {
	if (document.getElementById('currentStatus').value != 80) {
		Ext.Msg.show({
			title: _('curriki.crs.review.form.dialog.confirm_header'),
			msg: _('curriki.crs.review.asterixReview.removedConfirm_' + document.getElementById('currentStatus').value),
			buttons: Ext.Msg.OKCANCEL,
			fn: function(btn){
				if (btn == "ok") {
					var page = Ext.get('assetFullName').dom.value;
					Curriki.assets.RemoveAsterixReview(page, function(D){
						window.location.pathname = redirectURL;
					});
				}
				else {
					document.getElementById('asterixReview').style.display = 'none';
					document.getElementById('isChild').checked = true;
				}
			},
			icon: Ext.MessageBox.QUESTION
		});
	}
	else {
		document.getElementById('asterixReview').style.display = 'none';
	}
}
