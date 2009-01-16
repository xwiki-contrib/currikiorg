// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

// Some variables need to be defined before this script is loaded
//
// ? = Top level folder to organize
// ? = Creator of the folder (for intention message)
// ? = Owner or Admin user

(function(){
Ext.ns('Curriki.module.organize');
Ext.ns('Curriki.data.organize');

var Organize = Curriki.module.organize;
var Data = Curriki.data.organize;
var UI = Curriki.ui;

Organize.init = function(){
	Data.isMoving = false;
	Data.removed = [];
	Data.moved = [];
	Data.changedFolders = [];
	Data.selected = false;
	Data.confirmedCallback = Ext.emptyFn;

	UI.treeLoader.Organize = function(config){
		UI.treeLoader.Organize.superclass.constructor.call(this);
	};
	Ext.extend(UI.treeLoader.Organize, UI.treeLoader.Base, {
		setFullRollover:true
		,setAllowDrag:true
	});

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
						click:function(){
							if (Data.selected !== false) {
								Data.selected.remove();
								Data.selected = false;
							}
						}
					}
				},'->',{
					 text:_('organize.dialog.cancel_button')
					,id:'organize-cancel-btn'
					,cls:'button cancel'
					,listeners:{
						click:{
							 fn: function(){
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
							 fn: function(){
								var list = [];
//									Ext.getCmp('reorderCollectionsMS').store.each(function(rec){list.push(rec.data.collectionPage);});
								console.log('Organizing', list);

								Ext.getCmp('organize-tree-cmp').getRootNode().eachChild(Organize.getMovedList);

console.log('Removed Items', Data.removed);
console.log('Moved Items', Data.moved);

//TODO: Display confirm message, then save changes on acceptance
//TODO: Now, it is really the parents of these items that are changing (new and old)
// so, get the list of changing parents
// - then check revisions of them (one step or many?)
// - then save changes to them (one step or many?)
//   - Need to log message appropriately for the changes on that folder

								Data.changedFolders = Data.moved.concat(Data.removed).collect(function(n){
									return n.attributes.origLocation.parentNode;
								}).concat(Data.moved.collect(function(n){
									return n.parentNode;
								})).uniq();
console.log('Changed folders', Data.changedFolders);

								var checkFolders = function(){
console.log('All folders checked');
									var saveFolders = function(){
//TODO: Show final dialogue
console.log('save folders complete');
										Curriki.hideLoading();
//TODO: Reload the page
										window.location.reload();
									}

									Data.changedFolders.each(function(n){
										var pSF = saveFolders;
										saveFolders = function() {
											var wanted = n.childNodes.collect(function(c){return c.id;});
console.log('saving folder', n.id, wanted);
//Curriki.assets.SetSubassets(n.id, n.attributes.revision, wanted, function(o){
											if ("function" == typeof pSF) {
												pSF();
											}
//});
										};
									});

									saveFolders();
								};

								Data.changedFolders.each(function(n){
									var pCF = checkFolders;
									checkFolders = function() {
console.log('checking folder', n);
										Curriki.assets.GetMetadata(n.id, function(o){
console.log('checking folder in callback', n.id);
											if (o.revision != n.attributes.revision) {
												// Doesn't match
console.log('revision didnt match', n, o);
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
/*
							nodedragover:{
								fn: function(dragOverEvent){
									var draggedNodeId = dragOverEvent.dropNode.attributes.assetName;
									var parentNode = dragOverEvent.target;
									if (dragOverEvent.point !== 'append') {
										parentNode = parentNode.parentNode;
										if (Ext.isEmpty(parentNode)) {
											return false;
										}
									}

									if (!Ext.isEmpty(parentNode.attributes.disallowDropping) && (parentNode.attributes.disallowDropping === true)) {
										dragOverEvent.cancel = true;
										return false;
									}

									var cancel = false;
									while (!Ext.isEmpty(parentNode) && !cancel){
										if (parentNode.id === draggedNodeId) {
											cancel = true;
											dragOverEvent.cancel = true;
											return false;
										} else {
											parentNode = parentNode.parentNode;
										}
									}
								}
								,scope:this
							}
							,nodedrop:{
								fn: function(dropEvent){
									var targetNode = Ext.getCmp('ctv-to-tree-cmp').getNodeById('ctv-target-node');
									var parentNode = targetNode.parentNode;
									var parentNodeId = parentNode.id;
									var nextSibling = targetNode.nextSibling;
									var targetIndex = -1;
									if (nextSibling){
										targetIndex = nextSibling.attributes.order||-1;
									}
									Curriki.current.drop = {
										 parentPage:parentNodeId
										,targetIndex:targetIndex
									};
									Curriki.current.parentTitle = parentNode.text;
									AddPath.EnableNext();
								}
								,scope:this
							}
							,render:function(tPanel){
//TODO: Try to generalize this (for different # of panels)
								tPanel.ownerCt.ownerCt.on(
									'bodyresize'
									,function(wPanel, width, height){
										if (height === 'auto') {
											tPanel.setHeight('auto');
										} else {
											tPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+wPanel.findByType('panel')[0].el.getMargins('tb')+wPanel.findByType('panel')[1].getBox().height+wPanel.items.get(1).getFrameHeight()+wPanel.findByType('panel')[1].el.getMargins('tb')+(Ext.isIE?AddPath.ie_size_shift:0)+(Ext.isMac?(2*AddPath.ie_size_shift):0)));
										}
									}
								);
							}
							,expandnode:{
								fn: function(node){
									var wnd = this;
									wnd.fireEvent('afterlayout', wnd, wnd.getLayout());
								}
								,scope:this
							}
*/

/*
							render:function(tPanel){
								tPanel.getSelectionModel().on('selectionchange', function(tree, node){
console.log('selection change', node, e);
									Data.selected = node;
									Ext.getCmp('organize-remove-btn').enable();
								});
							}
*/

							beforeclick:function(node, e){
console.log('before click', node, e);
								node.select();
								Data.selected = node;
								Ext.getCmp('organize-remove-btn').enable();

								if (!node.isLeaf()) {
									node.toggle();
								}

								e.cancel = true;
								e.stopEvent();
								e.preventDefault();

								return false;
							}
							,click:function(node, e){
console.log('click', node, e);

								return false;
							}

							,dblclick:function(node, e){
console.log('dblclick', node, e);
								return false;
							}

							,beforemovenode:function(tree, node, oldParent, newParent, index){
console.log('beforemove node', node, oldParent, newParent, index, tree);
								Data.isMoving = true;
							}
							,movenode:function(tree, node, oldParent, newParent, index){
								// Note that both a remove and insert have already occurred here for this node
console.log('moved node', node, oldParent, newParent, index, tree);
								Data.isMoving = false;
							}

							,beforeremove:function(tree, oldParent, node){
console.log('before remove node', node, oldParent, tree);
								Data.removeFrom = oldParent.indexOf(node)+1;
								if (node.isSelected()){
									node.unselect();
								}
								if ('undefined' == typeof node.attributes.origLocation) {
									node.attributes.origLocation = { parentResource: oldParent.id, index: Data.removeFrom, parentNode: oldParent };
								}
							}
							,remove:function(tree, oldParent, node){
console.log('removed node', node, oldParent, tree);
								if (!Data.isMoving) {
									Data.removed.push(node);

									Data.selected = false;
									Ext.getCmp('organize-remove-btn').disable();
								}

								Ext.getCmp('organize-done-btn').enable();
							}

							,beforeinsert:function(tree, newParent, node, refNode){
console.log('before insert node', node, newParent, tree, refNode);
							}
							,insert:function(tree, newParent, node, refNode){
console.log('inserted node', node, newParent, tree, refNode);
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

	Organize.msgComplete = Ext.extend(UI.dialog.Messages, {
//TODO: Fill in
	});

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

		if('undefined' == typeof resourceInfo.title
		   || 'undefined' == typeof resourceInfo.creator
		   || 'undefined' == typeof resourceInfo.creatorName) {
			// Fetch resource info if not provided
			Curriki.assets.GetAssetInfo(Data.resource, function(cbInfo){
				Organize.start(cbInfo);
			});
			return false;
		} else {
			Data.title = resourceInfo.title;
			Data.creator = resourceInfo.creator;
			Data.creatorName = resourceInfo.creatorName;

			Organize.startMetadata(Data.resource);
		}
	} else {
		alert('ERROR: Could not start Organize.');
	}
};

/*
Ext.onReady(function(){
// Curriki.module.organize.start({assetPage:'Coll_dward.Collection20080624c-IE', title:'Testing Resource Title', creator:'XWiki.dward', creatorName:'Testing Creator'});
Curriki.module.organize.start({assetPage:'Coll_jmarks.UsingtheCurrikulumBuildertomakeLearningResourceCollections'});
});
*/

})();
