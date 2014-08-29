// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

// Some variables need to be defined before this script is loaded
//
// Set location the dialogue is being used (MyCurriki or Groups)
// Curriki.data.reorder.place = {'users', 'groups'}
//
// Set user or group to be used
// Curriki.data.reorder.which = {'XWiki.user', 'Group_spacename'}
//
// Set variable to state if a reorder has already been done
// Curriki.data.reorder.reordered = {true, false}

(function(){
Ext.ns('Curriki.module.reorder');
Ext.ns('Curriki.data.reorder');

var Reorder = Curriki.module.reorder;
var Data = Curriki.data.reorder;
var UI = Curriki.ui;

// Set message prefix depending on where the dialogue is being displayed
var msgPfx = 'mycurriki.collections.reorder.';
if (Data.place && Data.place === 'groups'){
	msgPfx = 'groups_curriculum_collections_reorder.';
}

Reorder.init = function(){
	if (!Data.place || !Data.which) {
		// Required values not passed
		return false;
	}

	Data.orig = [];

	Reorder.store = new Ext.data.JsonStore({
		storeId:'CollectionsStore'
		,url: '/xwiki/curriki/'+Data.place+'/'+Data.which+'/collections?_dc='+(new Date().getTime())
		,fields: ['displayTitle', 'assetpage']
		,autoLoad:true
		,listeners: {
			load:{
				fn: function(store, records, options){
					var list = [];
					store.each(function(rec){list.push(rec.data.assetpage);});
					Data.orig = list;
					console.log('Fetched list', list);
				}
			}
		}
	});

	Reorder.mainDlg = Ext.extend(UI.dialog.Messages, {
		  initComponent:function(){

			Ext.apply(this, {
				 id:'ReorderDialogueWindow'
				,title:_(msgPfx+'dialog_title')
				,cls:'reorder resource'
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
							,html:_(msgPfx+'guidingquestion')
							,cls:'guidingquestion'
						}
					},{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,html:_(msgPfx+'instruction')
							,cls:'instruction'
						}
					}]
				},{
					 xtype:'panel'
					,id:'collections-list-panel'
					,cls:'collections-list'
					,bbar:['->',{
						 text:_(msgPfx+'cancel.btt')
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
						 text:_(msgPfx+'next.btt')
						,id:'nextbutton'
						,cls:'button next'
						,listeners:{
							click:{
								 fn: function(){
								 	var list = [];
									Ext.getCmp('reorderCollectionsMS').store.each(function(rec){list.push(rec.data.assetpage);});
									console.log('Reordering', list);

									var dlg = this;
									var callback = function(o){
										console.log('Reorder callback', o);

										dlg.close();
										Reorder.msgComplete();

										window.location.reload();
									};

									Curriki.assets.ReorderRootCollection(Data.place, Data.which, Data.orig, list, callback);
								 }
								,scope:this
							}
						}
					}]
					,items:[{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,html:_(msgPfx+'listheader')
							,cls:'listheader'
						}
					},{
						 xtype:'form'
						,id:'ReorderDialoguePanel'
						,formId:'ReorderDialogueForm'
						,labelWidth:25
						,autoScroll:false
						,border:false
						,defaults:{
							 labelSeparator:''
						}
						,listeners:{
						}
						,items:[{
								 xtype:'multiselect'
								,id:'reorderCollectionsMS'
								,name:'reorderCollections'
								,hideLabel:true
								,border:false
								,enableToolbar:false
								//,legend:_('sri.instructional_component2_title')
								,legend:' '
								,store:Reorder.store
								,valueField:'assetpage'
								,displayField:'[""]}<span class="resource-CollectionComposite"><img class="x-tree-node-icon assettype-icon" src="'+Ext.BLANK_IMAGE_URL+'" /></span> {displayTitle'
								,width:600
								,height:200
								,allowBlank:false
								,preventMark:true
								,minLength:1
								,isFormField:true

								,dragGroup:'reorderMSGroup'
								,dropGroup:'reorderMSGroup'
						}]
					}]
				}]
			});
			Reorder.mainDlg.superclass.initComponent.call(this);
		}
	});
	Ext.reg('reorderDialog', Reorder.mainDlg);

	Reorder.msgComplete = function(){
		Curriki.logView('/features/reorder/'+(Data.place==='groups'?'groups':'mycurriki')+'/saved');
		alert(_(msgPfx+'set.confirm'));
	};

	Reorder.initialized = true;

	return true;
};

Reorder.confirmMsg = {
	first:function(){
		return confirm(_(msgPfx+'checkfirst.dialog'));
	}

	,after:function(){
		return confirm(_(msgPfx+'checkafter.dialog'));
	}

	,display:function(){
		if (!Data.reordered) {
			return Reorder.confirmMsg['first']();
		} else {
			return Reorder.confirmMsg['after']();
		}
	}
};

Reorder.display = function(){
	if (Reorder.init()) {
		UI.show('reorderDialog');
		Curriki.logView('/features/reorder/'+(Data.place==='groups'?'groups':'mycurriki')+'/started');
	}
};

Reorder.start = function(){
	Ext.onReady(function(){
		if (Reorder.confirmMsg.display()) {
			Reorder.display();
		}
	});
};

Ext.onReady(function(){
	if (!Data.place || !Data.which) {
		// Required values not passed
		return;
	}

	// Add Reorder link to page (with separator)
	// Currently both pages use a headerbar with a "Hide Descriptions" link
	// using 'hider-link' as the id where we want this link added in front of
	// - Groups.CurriculumSheet
	// - MyCurriki.Collections
	var hiderNode = Ext.DomQuery.selectNode('#hider-link');
	if (hiderNode) {
		Ext.DomHelper.insertBefore(hiderNode, {id: 'reorder-link', tag: 'a', cls: 'reorder-link', onclick:'Curriki.module.reorder.start(); return false;', html:_(msgPfx+'link')})
		Ext.DomHelper.insertBefore(hiderNode, {id: 'reorder-sep', tag:'span', cls:'separator', html:'|'})
	}
});
})();
