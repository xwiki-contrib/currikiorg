Ext.ns("Curriki.module.asterixReview");
Curriki.module.asterixReview.selectedConfirm = function(asterixReviewValue) {
	Ext.Msg.show({
		title : _('curriki.crs.review.information'),
		msg : _('curriki.crs.review.asterixReview.selectedConfirm'+asterixReviewValue),
		buttons : Ext.Msg.OKCANCEL,
		fn : function(btn) {
			if (btn == "ok") {
				var page = Ext.get('assetFullName').dom.value;
				Curriki.assets.SetAsterixReview(page, function(D) {
					document.getElementById('currentStatus').value=asterixReviewValue;
	   				document.getElementById('asterixReview').style.display = 'none';
					document.getElementById('isChild').checked = true;
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

Curriki.module.asterixReview.removedConfirm = function() {
	if (document.getElementById('currentStatus').value != 80) {
		Ext.Msg.show({
			title: _('curriki.crs.review.information'),
			msg: _('curriki.crs.review.asterixReview.removedConfirm' + document.getElementById('currentStatus').value),
			buttons: Ext.Msg.OKCANCEL,
			fn: function(btn){
				if (btn == "ok") {
					var page = Ext.get('assetFullName').dom.value;
					Curriki.assets.RemoveAsterixReview(page, function(D){
						document.getElementById('asterixReview').style.display = 'none';
						document.getElementById('currentStatus').value = 80;
						document.getElementById('asterixReview').value = "";
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