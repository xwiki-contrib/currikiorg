// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'group';

Ext.ns('Curriki.module.search.form.'+modName);

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function(){
	console.log('form.'+modName+': init');

	var comboWidth = 140;
	var comboListWidth = 250;

	form.termPanel = Search.util.createTermPanel(modName, form);
//	form.helpPanel = Search.util.createHelpPanel(modName, form);

	form.filterPanel = {
		xtype:'form'
		,labelAlign:'left'
		,id:'search-filterPanel-'+modName
		,formId:'search-filterForm-'+modName
		,border:false
		,items:[
			form.termPanel
//			,form.helpPanel
			,{
				xtype:'fieldset'
				,title:_('search.advanced.search.button')
				,id:'search-advanced-'+modName
				,autoHeight:true
				,collapsible:true
				,collapsed:true
				,animCollapse:false
				,border:true
				,stateful:true
				,stateEvents:['expand','collapse']
				,listeners:{
					'statesave':{
						fn:Search.util.fieldsetPanelSave
					}
					,'staterestore':{
						fn:Search.util.fieldsetPanelRestore
					}
					,'expand':{
						fn:function(panel){
							// CURRIKI-2989
							//  - Force a refresh of the grid view, as this
							//    seems to make the advanced search fieldset
							//    visible in IE7
							Ext.getCmp('search-results-'+modName).getView().refresh();

							Ext.select('.x-form-field-wrap', false, 'search-advanced-'+modName).setWidth(comboWidth);

							// CURRIKI-2873
							// - Force a repaint of the fieldset
							Ext.getCmp('search-termPanel-'+modName).el.repaint();
						}
					}
					,'collapse':{
						fn:function(panel){
							Ext.getCmp('search-results-'+modName).getView().refresh();
							Ext.getCmp('search-termPanel-'+modName).el.repaint();
						}
					}
				}
				,items:[{
					layout:'column'
					,border:false
					,defaults:{
						border:false
						,hideLabel:true
					}
					,items:[{
						columnWidth:0.33
						,layout:'form'
						,defaults:{
							hideLabel:true
						}
						,items:[{
							xtype:'combo'
							,fieldLabel:'Subject'
							,id:'combo-subject-'+modName
							,hiddenName:'subjectparent'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.subject
							,displayField:'subject'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('XWiki.CurrikiSpaceClass_topic_FW_masterFramework.WebHome.UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
							,listeners:{
								select:{
									fn:function(combo, value){
										var subSubject = Ext.getCmp('combo-subsubject-'+modName);
										if (combo.getValue() === '') {
											subSubject.clearValue();
											subSubject.hide();
										} else {
											subSubject.show();
											subSubject.clearValue();
											subSubject.store.filter('parentItem', combo.getValue());
											subSubject.setValue(combo.getValue());
										}
									}
								}
							}
						},{
							xtype:'combo'
							,fieldLabel:'Sub Subject'
							,id:'combo-subsubject-'+modName
							,hiddenName:'subject'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.subsubject
							,displayField:'subject'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
	//						,emptyText:'Select a Sub Subject...'
							,selectOnFocus:true
							,forceSelection:true
							,lastQuery:''
							,hidden:true
							,hideMode:'visibility'
						}]
					},{
						columnWidth:0.33
						,layout:'form'
						,defaults:{
							hideLabel:true
						}
						,items:[{
							xtype:'combo'
							,id:'combo-level-'+modName
							,fieldLabel:'Level'
							,mode:'local'
							,width:comboWidth
							,listWidth:comboListWidth
							,store:data.filter.store.level
							,hiddenName:'level'
							,displayField:'level'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('XWiki.CurrikiSpaceClass_educationLevel_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						},{
							xtype:'combo'
							,id:'combo-language-'+modName
							,fieldLabel:'Language'
							,hiddenName:'language'
							,mode:'local'
							,width:comboWidth
							,listWidth:comboListWidth
							,store:data.filter.store.language
							,displayField:'language'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('XWiki.CurrikiSpaceClass_language_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						}]
					},{
						columnWidth:0.34
						,layout:'form'
						,defaults:{
							hideLabel:true
						}
						,items:[{
							xtype:'combo'
							,id:'combo-policy-'+modName
							,fieldLabel:'Membership Policy'
							,hiddenName:'policy'
							,mode:'local'
							,width:comboWidth
							,listWidth:comboListWidth
							,store:data.filter.store.policy
							,displayField:'policy'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('search.XWiki.SpaceClass_policy_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						}]
					}]
				}]
			}
		]
	}

	form.columnModel = new Ext.grid.ColumnModel([{
			id: 'policy'
			,header: _('search.group.column.header.policy')
			,width: 62
			,dataIndex: 'policy'
			,sortable:true
			,renderer: data.renderer.policy
//			,tooltip: _('search.group.column.header.policy')
		},{
			id: 'title'
			,header: _('search.group.column.header.name')
			,width: 213
			,dataIndex: 'title'
			,sortable:true
			,hideable:false
			,renderer: data.renderer.title
//			,tooltip:_('search.group.column.header.name')
		},{
			id: 'description'
			,width: 225
			,header: _('search.group.column.header.description')
			,dataIndex:'description'
			,sortable:false
			,renderer: data.renderer.description
//			,tooltip: _('search.group.column.header.description')
		},{
			id: 'updated'
			,width: 96
			,header: _('search.group.column.header.updated')
			,dataIndex:'updated'
			,sortable:true
			,renderer: data.renderer.updated
//			,tooltip: _('search.group.column.header.updated')
	}]);

	form.resultsPanel = {
		xtype:'grid'
		,id:'search-results-'+modName
		//,title:'Results'
		,border:false
		,autoHeight:true
		,width:Search.settings.gridWidth
		,autoExpandColumn:'description'
		,stateful:true
		,frame:false
		,stripeRows:true
		,viewConfig: {
			forceFit:true
			,enableRowBody:true
			,showPreview:true
			// Remove the blank space on right of grid (reserved for scrollbar)
			,scrollOffset:0
		}
		,columnsText:_('search.columns.menu.columns')
		,sortAscText:_('search.columns.menu.sort_ascending')
		,sortDescText:_('search.columns.menu.sort_descending')
		,store: data.store.results
		,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
		,cm: form.columnModel
		,loadMask: false
		,plugins: form.rowExpander
		,bbar: new Ext.PagingToolbar({
			id: 'search-pager-'+modName
			,plugins:new Ext.ux.Andrie.pPageSize({
				 variations: [10, 25, 50]
				,beforeText: _('search.pagination.pagesize.before')
				,afterText: _('search.pagination.pagesize.after')
				,addBefore: _('search.pagination.pagesize.addbefore')
				,addAfter: _('search.pagination.pagesize.addafter')
			})
			,pageSize: 25
			,store: data.store.results
			,displayInfo: true
			,displayMsg: _('search.pagination.displaying.'+modName)
			,emptyMsg: _('search.find.no.results')
			,beforePageText: _('search.pagination.beforepage')
			,afterPageText: _('search.pagination.afterpage')
			,firstText: _('search.pagination.first')
			,prevText: _('search.pagination.prev')
			,nextText: _('search.pagination.next')
			,lastText: _('search.pagination.last')
			,refreshText: _('search.pagination.refresh')
		})
	};

	form.mainPanel = {
		xtype:'panel'
		,id:'search-panel-'+modName
		,autoHeight:true
		,items:[
			form.filterPanel
			,form.resultsPanel
		]
	};

	form.doSearch = function(){
		Search.util.doSearch(modName);
	};

	// Adjust title with count
	Search.util.registerTabTitleListener(modName);
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
	  form.init();
	});
});


// TODO:  Register this tab somehow with the main form

})();
