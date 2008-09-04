// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'curriki';

Ext.ns('Curriki.module.search.form.'+modName);

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function(){
	console.log('form.'+modName+': init');

	form.termPanel = Search.util.createTermPanel(modName, form);

	form.filterPanel = {
		xtype:'form'
		,labelAlign:'left'
		,id:'search-filterPanel-'+modName
		,formId:'search-filterForm-'+modName
		,border:false
		,items:[
			form.termPanel
		]
	};

	form.columnModel = new Ext.grid.ColumnModel([
		{
			id: 'name'
			,header:_('search.curriki.column.header.name')
			//,width: 17
			,dataIndex: 'name'
			,renderer: data.renderer.name
			,tooltip:_('search.curriki.column.header.name')
/*
		},{
			id: 'text'
			,header: _('search.curriki.column.header.text')
			//,width: 168
			,dataIndex: 'text'
			,sortable:false
			,renderer: data.renderer.text
			,tooltip:_('search.curriki.column.header.text')
*/
		},{
			id: 'updated'
			//,width: 169
			,header: _('search.curriki.column.header.updated')
			,dataIndex:'updated'
			,sortable:true
			,renderer: data.renderer.updated
			,tooltip: _('search.curriki.column.header.updated')
	}]);

	form.resultsPanel = {
		xtype:'grid'
		,id:'search-results-'+modName
		//,title:'Results'
		,border:false
		,autoHeight:true
		,autoExpandColumn:'name'
		,frame:false
		,viewConfig: {
			forceFit:true
			,enableRowBody:true
			,showPreview:true
		}
		,store: data.store.results
		,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
		,cm: form.columnModel
		,loadMask: false
		,bbar: new Ext.PagingToolbar({
			id: 'search-pager-'+modName
			,plugins:new Ext.ux.Andrie.pPageSize({variations: [10, 25, 50]})
			,pageSize: 25
			,store: data.store.results
			,displayInfo: true
			,displayMsg: _('search.pagination.count.displayed')
			,emptyMsg: _('search.find.no.results')
			,afterPageText: _('search.pagination.count.total')
		})
	};

	form.mainPanel = {
		xtype:'panel'
		,id:'search-panel-'+modName
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
	form.init();
});


// TODO:  Register this tab somehow with the main form

})();
