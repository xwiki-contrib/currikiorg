// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

// Some variables need to be defined and passed to this script (start method)
//
// assetPage = Top level folder to organize
// title = Creator of the folder (for intention message)
// creator = Owner or Admin user
// creatorName = Owner or Admin user name

(function(){
Ext.ns('Curriki.module.organize');
Ext.ns('Curriki.data.organize');

var Organize = Curriki.module.organize;
var Data = Curriki.data.organize;
var UI = Curriki.ui;

Organize.init = function(){
	Data.removed = [];
	Data.moved = [];
	Data.changedFolders = [];
	Data.selected = null;
	Data.confirmedCallback = Ext.emptyFn;

	UI.treeLoader.Organize = function(config){
		UI.treeLoader.Organize.superclass.constructor.call(this);
	};
	Ext.extend(UI.treeLoader.Organize, UI.treeLoader.Base, {
		setFullRollover:true
		,setAllowDrag:true
	});

	Organize.logCancelled = function(){
		var page = Data.resource.replace('.', '/');
		Curriki.logView('/features/resources/organize/'+page+'/cancelled');
	}

	Organize.logCompleted = function(){
		var page = Data.resource.replace('.', '/');
		Curriki.logView('/features/resources/organize/'+page+'/completed');
	}

	Organize.getMovedList = function(node){
		if ('undefined' != typeof node.attributes.origLocation) {
			Data.moved.push(node);
		}

		if (node.hasChildNodes()) {
			node.eachChild(Organize.getMovedList);
		}
	};

	Organize.mainDlg = Ext.extend(UI.dialog.Messages, {
		  initComponent:function(){
			Ext.apply(this, {
				 id:'OrganizeDialogueWindow'
				,title:_('organize.dialog_header')
				,cls:'organize resource resource-edit'
				,autoScroll:false
				,width:634
				,bbar:[{
					 text:_('organize.dialog.remove_button')
					,id:'organize-remove-btn'
					,cls:'button remove'
					,disabled:true
					,listeners:{
						click:function(btn, e){
							if (Data.selected !== null) {
								Data.removed.push(Data.selected.remove());
								btn.disable();
							}
						}
					}
				},'->',{
					 text:_('organize.dialog.cancel_button')
					,id:'organize-cancel-btn'
					,cls:'button cancel'
					,listeners:{
						click:{
							 fn: function(btn, e){
								Organize.logCancelled();
								this.close();
								if (Ext.isIE) {
									window.location.reload();
								}
							}
							,scope:this
						}
					}
				},{
					 text:_('organize.dialog.done_button')
					,id:'organize-done-btn'
					,cls:'button done'
					,disabled:true
					,listeners:{
						click:{
							 fn: function(btn, e){
								var list = [];
								console.log('Organizing', list);

								Ext.getCmp('organize-tree-cmp').getRootNode().eachChild(Organize.getMovedList);

								Data.changedFolders = Data.moved.concat(Data.removed).collect(function(n){
									return n.attributes.origLocation.parentNode;
								}).concat(Data.moved.collect(function(n){
									return n.parentNode;
								})).uniq();

								var checkFolders = function(){
									var saveFolders = function(){
										Organize.logCompleted();
										Curriki.hideLoading();
//TODO: Reload page when completed
//										window.location.reload();
									}

									Data.changedFolders.each(function(n){
										var pSF = saveFolders;
										saveFolders = function() {
											var wanted = n.childNodes.collect(function(c){return c.id;});
											var logMessage = '';
console.log('saving folder', n.id, wanted, n);
											var added = [];
											if ('undefined' != typeof n.attributes.addedNodes) {
												n.attributes.addedNodes.uniq().each(function(n){
													added.push(n);
												});
											};
											if ('undefined' != typeof n.attributes.removedNodes) {
												n.attributes.removedNodes.uniq().each(function(n){
													if (added.indexOf(n) == -1) {
														logMessage += _('organize.history.removed_note', n.id, n.attributes.order+1)+' ';
													} else {
														added.remove(n);
													}
												});
											}
											if ('undefined' != typeof n.attributes.addedNodes) {
												added.each(function(n){
													logMessage += _('organize.history.inserted_note', n.id, wanted.indexOf(n.id)+1)+' ';
												});
											}
console.log('logging', logMessage);
//											Curriki.assets.SetSubassets(n.id, null, wanted, logMessage, function(o){
												if ("function" == typeof pSF) {
													pSF();
												}
//											});
										};
									});

									saveFolders();
								};

								Data.changedFolders.each(function(n){
									var pCF = checkFolders;
									checkFolders = function() {
										Curriki.assets.GetMetadata(n.id, function(o){
											if (o.revision != n.attributes.revision) {
												// Doesn't match
												Curriki.hideLoading();
												alert(_('organize.error.concurrency_text'));
												this.close();
												Ext.getCmp('OrganizeDialogueWindow').close();
												Organize.start(Data.startInfo);
											} else {
												// Matches -- check next
												if ("function" == typeof pCF) {
													pCF();
												}
											}
										});
									};
								});

								var afterCheck = checkFolders;
								checkFolders = function() {
										Curriki.showLoading();
										afterCheck();
								};

								Data.confirmedCallback = checkFolders;

								Data.confirmMsg = '';
								Data.removed.each(function(n){
									Data.confirmMsg += '<br />'+_('organize.confirmation.dialog_removed_listing', n.text, n.attributes.origLocation.parentNode.text);
								});
								Data.moved.uniq().each(function(n){
									if (Data.removed.indexOf(n) == -1) {
										Data.confirmMsg += '<br />'+_('organize.confirmation.dialog_moved_listing', n.text, n.attributes.origLocation.index, n.attributes.origLocation.parentNode.text, n.parentNode.indexOf(n)+1, n.parentNode.text);
									}
								});

								UI.show('confirmOrganizeDlg');
							 }
							,scope:this
						}
					}
				}]
				,items:[{
					 xtype:'panel'
					,id:'guidingquestion-container'
					,cls:'guidingquestion-container'
					,items:[{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,html:_('organize.dialog.guidingquestion_text')
							,cls:'guidingquestion'
						}
					},{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,html:_('organize.dialog.instruction_text')
							,cls:'instruction'
						}
					}]
				},{
					 xtype:'panel'
					,id:'organize-panel'
					,cls:'organize-panel'
					,items:[{
						 xtype:'treepanel'
						,loader: new UI.treeLoader.Organize()
						,id:'organize-tree-cmp'
						,autoScroll:true
						,maxHeight:390
						,useArrows:true
						,border:false
						,hlColor:'93C53C'
						,hlDrop:false
						,cls:'organize-tree'
						,animate:true
						,enableDD:true
						,ddScroll:true
						,containerScroll:true
						,rootVisible:true
						,listeners:{
							render:function(tPanel){
console.log('set up selectionchange', tPanel);
								tPanel.getSelectionModel().on('selectionchange', function(selMod, node){
console.log('selection change', node, selMod);
									Data.selected = node;
									if (node !== null) {
										Ext.getCmp('organize-remove-btn').enable();
									} else {
										Ext.getCmp('organize-remove-btn').disable();
									}
								});
							}
							,expandnode:{
								fn:function(node){
									var cmp = this.findById('organize-tree-cmp');
									if (!Ext.isEmpty(cmp)) {
										cmp.fireEvent('afterlayout', cmp, cmp.getLayout());
									}
								}
								,scope:this
							}
							,afterlayout:function(cmp, layout){
								if (this.afterlayout_maxheight) {
									// Don't collapse again
								} else {
									if (cmp.getBox().height > cmp.maxHeight){
										cmp.setHeight(cmp.maxHeight);
										cmp.findParentByType('organizeDlg').center();
										this.afterlayout_maxheight = true;
									} else {
										cmp.setHeight('auto');
									}
								}
							}
							,movenode:function(tree, node, oldParent, newParent, index){
								// Note that both a remove and insert have already occurred here for this node
console.log('moved node', node, oldParent, newParent, index, tree);
								if ('undefined' === typeof newParent.attributes.addedNodes) {
									newParent.attributes.addedNodes = [];
								}
								newParent.attributes.addedNodes.push(node);
								//node.select();
							}

							,beforeremove:function(tree, oldParent, node){
console.log('before remove node', node, oldParent, tree);
								if ('undefined' == typeof node.attributes.origLocation) {
									var removeFrom = oldParent.indexOf(node)+1;
									node.attributes.origLocation = { parentResource: oldParent.id, index: removeFrom, parentNode: oldParent };
								}
							}
							,remove:function(tree, oldParent, node){
console.log('removed node', node, oldParent, tree);
								if ('undefined' != typeof oldParent.attributes.addedNodes) {
									oldParent.attributes.addedNodes.remove(node);
								}
								if ('undefined' === typeof node.attributes.origLocation.parentNode.attributes.removedNodes) {
									node.attributes.origLocation.parentNode.attributes.removedNodes = [];
								}
								node.attributes.origLocation.parentNode.attributes.removedNodes.push(node);
								Ext.getCmp('organize-done-btn').enable();
							}
						}
						,root:Data.root
					}]
				}]
			});
			Organize.mainDlg.superclass.initComponent.call(this);
		}
	});
	Ext.reg('organizeDlg', Organize.mainDlg);

	Organize.confirmDlg = Ext.extend(UI.dialog.Messages, {
		  initComponent:function(){
			Ext.apply(this, {
				 id:'ConfirmOrganizeDialogueWindow'
				,title:_('organize.confirmation.dialog_header')
				,cls:'organize resource resource-edit'
				,autoScroll:false
				,bbar:['->',{
					 text:_('organize.dialog.cancel_button')
					,id:'organize-confirm-cancel-button'
					,cls:'button cancel'
					,listeners:{
						'click':{
							fn:function(e,evt){
								Organize.logCancelled();
								this.close();
							}
							,scope:this
						}
					}
				},{
					 text:_('organize.dialog.ok_button')
					,id:'organize-confirm-ok-button'
					,cls:'button next'
					,listeners:{
						'click':{
							fn:function(e,evt){
								Data.confirmedCallback();
								this.close();
							}
							,scope:this
						}
					}
				}]
				,items:[{
					xtype:'box'
					,autoEl:{
						tag:'div'
						,html:_('organize.confirmation.dialog_summary_text')
							+'<br />'
							+Data.confirmMsg
							+(Data.removed.size()>0?('<br /><br />'+_('organize.confirmation.dialog_note_text')):'')
							+'<br /><br />'
							+_('organize.confirmation.dialog_instruction_text')
					}
				}]
			});
			Organize.confirmDlg.superclass.initComponent.call(this);
		}
	});
	Ext.reg('confirmOrganizeDlg', Organize.confirmDlg);

	Organize.intentionDlg = Ext.extend(UI.dialog.Messages, {
		  initComponent:function(){
			Ext.apply(this, {
				 id:'IntentionOrganizeDialogueWindow'
				,title:_('organize.intention.dialog_title')
				,cls:'organize resource resource-edit'
				,autoScroll:false
				,bbar:[{
					 text:_('organize.intention.message.continue_button')
					,id:'organize-intention-continue-button'
					,cls:'button continue'
					,listeners:{
						'click':{
							fn:function(e,evt){
								UI.show('organizeDlg');
								this.close();
							}
							,scope:this
						}
					}
				},{
					 text:_('organize.dialog.cancel_button')
					,id:'organize-intention-cancel-button'
					,cls:'button cancel'
					,listeners:{
						'click':{
							fn:function(e,evt){
								Organize.logCancelled();
								this.close();
							}
							,scope:this
						}
					}
				}]
				,items:[{
					xtype:'box'
					,autoEl:{
						tag:'div'
						,html:_('organize.intention.message_text', Data.title, Data.creatorName)
					}
				}]
			});
			Organize.intentionDlg.superclass.initComponent.call(this);
		}
	});
	Ext.reg('intentOrganizeDlg', Organize.intentionDlg);

	Organize.display = function(){
		if (Data.creator == Curriki.global.username || Curriki.global.isAdmin) {
			UI.show('organizeDlg');
		} else {
			UI.show('intentOrganizeDlg');
		}
	};

	Organize.startMetadata = function(resource) {
		Curriki.assets.GetMetadata(resource, function(o){
console.log('returned', o);
			o.fwItems = o.fw_items;
			o.levels = o.educational_level;
			o.ict = o.instructional_component;
			o.displayTitle = o.title;
			o.rights = o.rightsList;

			o.leaf = false;
			o.allowDrag = false;
			o.allowDrop = true;
			o.expanded = true;

			var treeLoader = new UI.treeLoader.Organize();
			Data.root = treeLoader.createNode(o);
console.log('created', Data.root);
			Data.root.addListener('beforecollapse', function(){return false;});
			Data.resource = resource;

			Ext.onReady(function(){
				Organize.display();
			});
		});
	}


	return true;
};

Organize.start = function(resourceInfo){
	if (Organize.init()) {
		if ('undefined' == typeof resourceInfo || 'undefined' == typeof resourceInfo.assetPage) {
			alert('No resource to organize given.');
			return false;
		}
		Data.resource = resourceInfo.assetPage;

		if('undefined' == typeof resourceInfo.creator
		   || ((resourceInfo.creator != Curriki.global.username
		        && !Curriki.global.isAdmin)
		       && ('undefined' == typeof resourceInfo.title
		           || 'undefined' == typeof resourceInfo.creatorName))) {
			// Fetch resource info if not provided
			Curriki.assets.GetAssetInfo(Data.resource, function(cbInfo){
				Organize.start(cbInfo);
			});
		} else {
			Data.startInfo = resourceInfo;
			Data.creator = resourceInfo.creator;
			Data.title = resourceInfo.title||'No Title Given';
			Data.creatorName = resourceInfo.creatorName||'No Username Given';

			Organize.startMetadata(Data.resource);
		}
	} else {
		alert('ERROR: Could not start Organize.');
		return false;
	}
};

/*
 * Usage:
 *
 * Curriki.module.organize.start({assetPage:'web.page', title:'Resource Title', creator:'creator.id', creatorName:'Creator Name'});
*/

})();
