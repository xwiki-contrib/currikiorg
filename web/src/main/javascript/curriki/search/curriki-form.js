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
			,width: 500
			,dataIndex: 'name'
			,sortable:true
			,renderer: data.renderer.name
//			,tooltip:_('search.curriki.column.header.name')
		},{
			id: 'updated'
			,width: 96
			,header: _('search.curriki.column.header.updated')
			,dataIndex:'updated'
			,sortable:true
			,renderer: data.renderer.updated
//			,tooltip: _('search.curriki.column.header.updated')
	}]);

	form.resultsPanel = {
		xtype:'grid'
		,id:'search-results-'+modName
		//,title:'Results'
		,border:false
		,autoHeight:true
		,width:Search.settings.gridWidth
		,autoExpandColumn:'name'
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
