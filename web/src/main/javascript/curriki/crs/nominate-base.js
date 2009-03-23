// vim: ts=4:sw=4
/* global Ext */
/* global Curriki */
/* global _ */

Ext.ns('Curriki.module.nominate');
Curriki.module.nominate.init = function() {
	if (Ext.isEmpty(Curriki.module.nominate.initialized)) {
		// Local alias
		var Nominate = Curriki.module.nominate;

		Nominate.ie_size_shift = 10;

		Nominate.EnableNext = function() {
			Ext.getCmp('nextbutton').enable();
		}
		Nominate.DisableNext = function() {
			Ext.getCmp('nextbutton').disable();
		}

		Nominate.NominateDialogueId = 'nominate-resource-dialogue';
		Nominate.NominateResource = Ext.extend(Curriki.ui.dialog.Actions, {
			initComponent : function() {
				Ext.apply(this, {
					title : _('curriki.crs.nominate'),
					cls : 'Caption', // cls: the css class to use
					id : Nominate.NominateDialogueId,
					items : [{// Begin Panel
						// xtype is a symbolic name given to a class.
						// Ext.reg('panel', Ext.Panel); defined in
						// ext-all-debug.js
						xtype : 'panel'
						,
						items : [{
							xtype : 'box',
							cls : 'crs_nominate_title',
							autoEl : { // autoEl: for inserting html code
								tag : 'div',
								html : _('curriki.crs.nominate.nominatefollowingresourceforreview'),
								cls : 'crs_nominate_title'
							}
						}
						,{
							xtype : 'box',
							autoEl : {
								tag : 'div',
								html : Curriki.current.assetTitle,
								cls : 'crs_nominate_pagename'
							}
						}]
					},		// End Panel
					{		// Begin Form
						xtype : 'form',
						id : 'nominateResourcePanel',
						formId : 'nominateResourceForm',
						cls : 'form-container',
						labelWidth : 25,
						autoScroll : true,
						border : false,
						defaults : {
							labelSeparator : '',
							hideLabel : true,
							name : 'nominateResource'
						},
						bbar : ['->', {
							text : _('curriki.crs.nominate.cancel'),
							id : 'cancelbutton',
							cls : 'button button-cancel',
							listeners : {
								click : {
									fn : function() {
										this.close();
										window.location.href = Curriki.current.cameFrom;
									},
									scope : this
								}
							}
						}, {
							text : _('curriki.crs.nominate.submit'),
							id : 'submitbutton',
							cls : 'submitbutton button button-confirm',
							listeners : {
								click : {
									fn : function() {
										var form = this.findByType('form')[0]
												.getForm();
										if (form.isValid()) {
											var comments = (form
													.getValues(false))['nominate-comments'];
											Nominate.Nominate(comments);
										} else {
											alert("Invalid Form");
										}

									},
									scope : this
								}
							}
						}],
						monitorValid : true,
						listeners : {
							render : function(fPanel) {
								// TODO: Try to generalize this (for different #
								// of panels)
								fPanel.ownerCt.on('bodyresize', function(
										wPanel, width, height) {
									if (height === 'auto') {
										fPanel.setHeight('auto');
									} else {
										fPanel
												.setHeight(wPanel
														.getInnerHeight()
														- (wPanel
																.findByType('panel')[0]
																.getBox().height + (Ext.isIE
																? Nominate.ie_size_shift
																: 0)));
									}
								});
							}
						},
						items : [{
							xtype : 'box',
							autoEl : {
								tag : 'div',
								html : _('curriki.crs.nominate.comments'),
								cls : 'crs_nominate_title'
							}
						}, {
							xtype : 'box',
							autoEl : {
								tag : 'div',
								html : _('curriki.crs.nominate.commentstext'),
								cls : 'crs_nominate_commentstext'
							}
						}, {
							xtype : 'textarea',
							id : 'nominate-comments',
							name : 'nominate-comments' // ,allowBlank:false
							// ,preventMark:true
							// ,hideLabel:true
							,
							width : '80%'
						}, {
							xtype : 'box',
							autoEl : {
								tag : 'div',
								html : _('curriki.crs.nominate.commentsfootertext'),
								cls : 'crs_nominate_commentstext'
							}
						}]
					}		// End Form
					]
				});
				Nominate.NominateResource.superclass.initComponent.call(this);
			}
		});
		Ext.reg('nominateResourceDialog', Nominate.NominateResource);

		Nominate.Nominate = function(comments) {
			Curriki.assets.NominateAsset(Curriki.current.assetName, comments,
					function(response) {
						window.location.href = Curriki.current.cameFrom;
					});
		};

		Curriki.module.nominate.initialized = true;
	}
};

Curriki.module.nominate.nominateResource = function(options) {
	Curriki.module.nominate.initAndStart(function() {
		Curriki.ui.show('nominateResourceDialog');
	}, options);
}

Curriki.module.nominate.initAndStart = function(fcn, options) {
	var current = Curriki.current;
	if (!Ext.isEmpty(options)) {
		current.assetName = options.assetName || current.assetName;
		current.parentAsset = options.parentAsset || current.parentAsset;
		current.publishSpace = options.publishSpace || current.publishSpace;
		current.cameFrom = window.location.href;

		current.assetTitle = options.assetTitle || current.assetTitle;
		current.assetType = options.assetType || current.assetType;
		current.parentTitle = options.parentTitle || current.parentTitle;
	}

	Curriki.init(function() {
		if (Ext.isEmpty(Curriki.data.user.me)
				|| 'XWiki.XWikiGuest' === Curriki.data.user.me.username) {
			window.location.href = '/xwiki/bin/login/XWiki/XWikiLogin?xredirect='
					+ window.location.href;
			return;
		}

		Curriki.module.nominate.init();

		var startFn = function() {
			fcn();
		}

		var parentFn;
		if (!Ext.isEmpty(current.parentAsset)
				&& Ext.isEmpty(current.parentTitle)) {
			// Get parent asset info
			parentFn = function() {
				Curriki.assets.GetAssetInfo(current.parentAsset,
						function(info) {
							Curriki.current.parentTitle = info.title;
							startFn();
						});
			}
		} else {
			parentFn = function() {
				startFn();
			};
		}

		var currentFn;
		if (!Ext.isEmpty(current.assetName)
				&& (Ext.isEmpty(current.assetTitle) || Ext
						.isEmpty(current.assetType))) {
			// Get asset info
			currentFn = function() {
				Curriki.assets.GetAssetInfo(current.assetName, function(info) {
					Curriki.current.assetTitle = info.title;
					Curriki.current.assetType = info.assetType;
					parentFn();
				});
			}
		} else {
			currentFn = function() {
				parentFn();
			};
		}

		currentFn();
	});
}

Curriki.module.nominate.loaded = true;

// Initialize "current" information
Ext.ns('Curriki.current');
Curriki.current = {
	init : function() {
		Ext.apply(this, {
			assetName : null,
			parentAsset : null,
			publishSpace : null,
			cameFrom : null,
			flow : null,
			flowFolder : '',
			assetTitle : null,
			assetType : null,
			parentTitle : null,
			asset : null,
			metadata : null,
			selected : null,
			fileName : null,
			videoId : null,
			linkUrl : null,
			sri1 : null,
			sri1fillin : null,
			sri2fillin : null,
			submitToTemplate : null,
			drop : null
		});
	}
}
Curriki.current.init();
