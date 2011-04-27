// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'member';
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
							,id:'combo-subject-'+modName
							,fieldLabel:'Subject'
							,hiddenName:'subjectparent'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.subject
							,displayField:'subject'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('XWiki.XWikiUsers_topics_FW_masterFramework.WebHome.UNSPECIFIED')
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
							,id:'combo-member_type-'+modName
							,fieldLabel:'Member Type'
							,mode:'local'
							,width:comboWidth
							,listWidth:comboListWidth
							,store:data.filter.store.member_type
							,hiddenName:'member_type'
							,displayField:'member_type'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('XWiki.XWikiUsers_member_type_UNSPECIFIED')
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
							,id:'combo-country-'+modName
							,fieldLabel:'Country'
							,hiddenName:'country'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.country
							,displayField:'country'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('XWiki.XWikiUsers_country_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						}]
					}]
				}]
			}
		]
	}

	form.columnModelList = [{
			id: 'picture'
			,header: _('search.member.column.header.picture')
			,width: 116
			,dataIndex: 'picture'
			,sortable:false
			,resizable:false
			,menuDisabled:true
			,renderer: data.renderer.picture
//			,tooltip:_('search.member.column.header.picture')
		},{
			id: 'name1'
			,header: _('search.member.column.header.name1')
			,width: 120
			,dataIndex: 'name1'
			,sortable:true
			,hideable:false
			,renderer: data.renderer.name1
//			,tooltip:_('search.member.column.header.name1')
		},{
			id: 'name2'
			,width: 120
			,header: _('search.member.column.header.name2')
			,dataIndex:'name2'
			,sortable:true
			,hideable:false
			,renderer: data.renderer.name2
//			,tooltip: _('search.member.column.header.name2')
		},{
			id: 'bio'
			,width: 120
			,header: _('search.member.column.header.bio')
			,dataIndex:'bio'
			,sortable:false
			,renderer: data.renderer.bio
//			,tooltip: _('search.member.column.header.bio')
		},{
			id: 'contributions'
			,width: 120
			,header: _('search.member.column.header.contributions')
			,dataIndex:'contributions'
			,sortable:false
			,renderer: data.renderer.contributions
//			,tooltip: _('search.member.column.header.contributions')
	}];

	form.columnModel = new Ext.grid.ColumnModel(form.columnModelList);

	form.resultsPanel = {
		xtype:'grid'
		,id:'search-results-'+modName
		//,title:'Results'
		,border:false
		,autoHeight:true
		,width:Search.settings.gridWidth
		,autoExpandColumn:'bio'
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
