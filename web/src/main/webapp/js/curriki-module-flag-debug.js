// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
Ext.ns('Curriki.module.flag');
Ext.ns('Curriki.data.flag');

var Flag = Curriki.module.flag;
var Data = Curriki.data.flag;
var UI = Curriki.ui;

Flag.init = function(){
	/*
	 * We need to get the list of reasons to display
	 */
	var pfx = 'flag.dialog.reason.';
	Data.list = [];
	var i = 1;
	while (_(pfx+i) !== pfx+i){
		Data.list.push([i, _(pfx+i), i]);
		++i;
	}
	Data.list.push(['alt', _(pfx+'alt'), i]);

	Flag.store = new Ext.data.SimpleStore({
			fields: ['id', 'text', 'sortValue']
			,sortInfo: {field:'sortValue', direction:'ASC'}
			,data: Data.list
			,id: 0
	});

	Flag.mainDlg = Ext.extend(UI.dialog.Actions, {
		  initComponent:function(){

			Ext.apply(this, {
				 id:'FlagDialogueWindow'
				,title:_('flag.dialog.header')
				,cls:'flag resource'
				,autoScroll:false
				,items:[{
					 xtype:'form'
					,id:'FlagDialoguePanel'
					,formId:'FlagDialogueForm'
					,labelWidth:200
					,labelAlign:'top'
					,autoScroll:false
					,border:false
					,defaults:{
						 labelSeparator:''
					}
					,monitorValid:true
					,buttonAlign:'right'
					,buttons:[{
						 text:_('flag.dialog.cancel.btt')
						,id:'cancelbutton'
						,cls:'button button-cancel'
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
						 text:_('flag.dialog.submit.btt')
						,id:'nextbutton'
						,cls:'button button-confirm'
						,formBind:true
						,listeners:{
							click:{
								 fn: function(){
									console.log('Flaging', Data.resourcePage);

									var dlg = this;
									var callback = function(o){
										console.log('Flag callback', o);

										dlg.close();
										Flag.msgComplete();
									};

									var reason = Ext.getCmp('flag-reason').getValue();
									var altReason = Ext.getCmp('flag-alt-reason')?Ext.getCmp('flag-alt-reason').getValue():'';
//									Curriki.assets.Flag(Data.resourcePage, reason, altReason, callback);
									callback('TODO: REMOVE');
								 }
								,scope:this
							}
						}
					}]
					,items:[{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,html:_('flag.dialog.guidingquestion1.txt')
							,cls:'guidingquestion'
						}
					},{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,html:_('flag.dialog.instruction1.txt')
							,cls:'instruction'
						}
					},{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,html:_('flag.dialog.guidingquestion2.txt')
							,cls:'guidingquestion'
						}
					},{
						xtype:'combo'
						,id:'flag-reason'
						,hiddenName:'flagReason'
						,mode:'local'
						,store:Flag.store
						,displayField:'text'
						,valueField:'id'
						,emptyText:_('flag.dialog.reason.unselected') // Shown in spec, but nothing in translations
						,width:400
						,selectOnFocus:true
						,forceSelection:true
						,triggerAction:'all'
						,allowBlank:false
						,editable:false
						,typeAhead:false
						,hideLabel:false
						,labelStyle:'instruction'
						,labelClass:'instructionClass'
						,labelCls:'instructionCls'
						,fieldLabel:_('flag.dialog.required.field.icon')+_('flag.dialog.instruction2.txt') // The ! should really have the required-indicator item in it
						,clearCls:''
						,listeners:{
							select:{
								fn:function(combo, record, index){
									var reason = record.id;
									var field = Ext.getCmp('FlagDialoguePanel').getForm().findField('flag-alt-reason');
									if (reason === 'alt') {
										field.show();
									} else {
										field.hide();
									}
								}
							}
							,render:{
								fn:function(cmp){
									cmp.getEl().up('.x-form-item').down('label').addClass('instruction');
								}
							}
						}
					},{
						xtype:'textarea'
						,id:'flag-alt-reason'
						,hiddenName:'flagAltReason'
						,width:580
						,minLength:5
						,maxLength:300
						,allowBlank:false
						,hidden:true
						,hideLabel:false
						,labelStyle:'instruction'
						,labelClass:'instructionClass'
						,fieldLabel:_('flag.dialog.required.field.icon')+_('flag.dialog.instruction4.txt')
						,listeners:{
							hide:{
								fn:function(cmp){
									cmp.disable();
									cmp.getEl().up('.x-form-item').setDisplayed(false); // hide label
								}
							}
							,show:{
								fn:function(cmp){
									cmp.enable();
									cmp.getEl().up('.x-form-item').setDisplayed(true); // true label
								}
							}
							,render:{
								fn:function(cmp){
									cmp.getEl().up('.x-form-item').down('label').addClass('instruction');
								}
							}
						}
					},{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,html:_('flag.dialog.instruction5.txt')
							,cls:'instruction'
						}
					}]
				}]
			});
			Flag.mainDlg.superclass.initComponent.call(this);
		}
	});
	Ext.reg('flagDialog', Flag.mainDlg);

	Flag.completeDlg = Ext.extend(UI.dialog.Messages, {
		  initComponent:function(){
			Ext.apply(this, {
				 id:'FlagDialogueWindow'
				,title:_('flag.success.dialog.header')
				,cls:'flag resource'
				,autoScroll:false
				,buttonAlign:'right'
				,buttons:[{
					 text:_('flag.success.dialog.button')
					,id:'closebutton'
					,cls:'button button-confirm'
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
				}]
				,items:[{
					 xtype:'box'
					,autoEl:{
						 tag:'div'
						,html:_('flag.success.dialog.message')
						//,cls:'instruction'
					}
				}]
			});
			Flag.completeDlg.superclass.initComponent.call(this);
		}
	});
	Ext.reg('flagCompleteDialog', Flag.completeDlg);

	Flag.msgComplete = function(){
//		Curriki.logView('/features/flag/'+'saved');
		UI.show('flagCompleteDialog');
	};

	Flag.initialized = true;

	return true;
};

Flag.display = function(options){
	if (!Ext.isEmpty(options) && (/* Flag.initialized || */ Flag.init())) {
		Data.resourcePage = options.resourcePage||null;
		if (Data.resourcePage) {
			UI.show('flagDialog');
//			Curriki.logView('/features/flag/'+'started');
		}
	}
};

Flag.start = function(options){
	Ext.onReady(function(){
		Flag.display(options);
	});
};

Ext.onReady(function(){
	// Add Flag link to page (with separator)
	// Currently both pages use a headerbar with a "Hide Descriptions" link
	// using 'hider-link' as the id where we want this link added in front of
	// - Groups.CurriculumSheet
	// - MyCurriki.Collections
/*
	var hiderNode = Ext.DomQuery.selectNode('#hider-link');
	if (hiderNode) {
		Ext.DomHelper.insertBefore(hiderNode, {id: 'flag-link', tag: 'a', cls: 'flag-link', onclick:'Curriki.module.flag.start(); return false;', html:_('flag.dialog.link')})
		Ext.DomHelper.insertBefore(hiderNode, {id: 'flag-sep', tag:'span', cls:'separator', html:'|'})
	}
*/
});
})();

//Curriki.module.flag.start({resourcePage: 'test.test'});
