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
	UI.treeLoader.Organize = function(config){
		UI.treeLoader.Organize.superclass.constructor.call(this);
	};
	Ext.extend(UI.treeLoader.Organize, UI.treeLoader.Base, {
		setFullRollover:true
		,setAllowDrag:true
	});

	Organize.mainDlg = Ext.extend(UI.dialog.Messages, {
		  initComponent:function(){
			Ext.apply(this, {
				 id:'OrganizeDialogueWindow'
				,title:_('organize.dialog_header')
				,cls:'organize resource resource-edit'
				,autoScroll:false
				,width:634
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
					,bbar:[{
						 text:_('organize.dialog.remove_button')
						,id:'removebutton'
						,cls:'button remove'
						,listeners:{
							click:{
								 fn: function(){
//TODO: Remove items...
									this.close();
									if (Ext.isIE) {
										window.location.reload();
									}
								}
								,scope:this
							}
						}
					},'->',{
						 text:_('organize.dialog.cancel_button')
						,id:'cancelbutton'
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
						,id:'donebutton'
						,cls:'button done'
						,listeners:{
							click:{
								 fn: function(){
//TODO: Display confirm message, then save changes
								 	var list = [];
//									Ext.getCmp('reorderCollectionsMS').store.each(function(rec){list.push(rec.data.collectionPage);});
									console.log('Organizing', list);

									var dlg = this;
									var callback = function(o){
										console.log('Organize callback', o);

										dlg.close();
//										Organize.msgComplete();

										window.location.reload();
									};

//									Curriki.assets.ReorderRootCollection(Data.place, Data.which, Data.orig, list, callback);
								 }
								,scope:this
							}
						}
					}]
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
/*
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
*/
							,expandnode:{
								fn: function(node){
									var wnd = this;
									wnd.fireEvent('afterlayout', wnd, wnd.getLayout());
								}
								,scope:this
							}
							,beforeclick:function(node, e){
									node.toggle();
									return false;
							}
						}
						,root:Data.root
					}]
				}]
			});
			Organize.mainDlg.superclass.initComponent.call(this);
		}
	});
	Ext.reg('organizeDialog', Organize.mainDlg);

	Organize.msgComplete = Ext.extend(UI.dialog.Messages, {
//TODO: Fill in
	});

	Organize.confirmDlg = Ext.extend(UI.dialog.Messages, {
		  initComponent:function(){
			Ext.apply(this, {
				 id:'ConfirmOrganizeDialogueWindow'
				,title:_('organize.intention.dialog_title')
				,cls:'organize resource resource-edit'
				,autoScroll:false
				,bbar:[{
					 text:_('organize.intention.message.continue_button')
					,id:'continuebutton'
					,cls:'button continue'
					,listeners:{
						'click':{
							fn:function(e,evt){
								UI.show('organizeDialog');
								this.close();
							}
							,scope:this
						}
					}
				},{
					 text:_('organize.dialog.cancel_button')
					,id:'closebutton'
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
			Organize.confirmDlg.superclass.initComponent.call(this);
		}
	});
	Ext.reg('confirmOrganizeDialog', Organize.confirmDlg);

	Organize.display = function(){
		if (Data.creator == Curriki.global.username || Curriki.global.isAdmin) {
			UI.show('organizeDialog');
		} else {
			UI.show('confirmOrganizeDialog');
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
		if (undefined === resourceInfo || undefined === resourceInfo.assetPage) {
			alert('No resource to organize given.');
			return false;
		}
		Data.resource = resourceInfo.assetPage;

		if(undefined === resourceInfo.title
		   || undefined == resourceInfo.creator
		   || undefined == resourceInfo.creatorName) {
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
// Organize.start({assetPage:'Coll_dward.Collection20080624c-IE', title:'Testing Resource Title', creator:'XWiki.dward', creatorName:'Testing Creator'});
Organize.start({assetPage:'Coll_jmarks.UsingtheCurrikulumBuildertomakeLearningResourceCollections'});
});
*/

})();
