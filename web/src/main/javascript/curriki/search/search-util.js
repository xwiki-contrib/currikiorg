// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
Ext.ns('Curriki.module.search.util');

var Search = Curriki.module.search;
var module = Search.util;

module.init = function(){
	console.log('search util: init');

	module.logFilterList = {
		'resource':['subject', 'level', 'language', 'ict', 'review', 'special', 'other', 'sort', 'dir']
		,'group':['subject', 'level', 'language', 'policy', 'other', 'sort', 'dir']
		,'member':['subject', 'member_type', 'country', 'other', 'sort', 'dir']
		,'blog':['other', 'sort', 'dir']
		,'curriki':['other', 'sort', 'dir']
	};

	// Register a listener that will update counts on the tab
	module.registerTabTitleListener = function(modName){
		// Adjust title with count
		Ext.StoreMgr.lookup('search-store-'+modName).addListener(
			'datachanged'
			,function(store) {
				var tab = Ext.getCmp('search-'+modName+'-tab');
				if (!Ext.isEmpty(tab)) {
					tab.setTitle(_('search.'+modName+'.tab.title')+' ('+store.getTotalCount()+')');
				}
			}
		);

		Ext.StoreMgr.lookup('search-store-'+modName).addListener(
			'load'
			,function(store, data, options) {
				var params = options.params||{};
				var tab = params.module;
				var terms = escape(params.terms||'');
				var advancedPanel = Ext.getCmp('search-advanced-'+tab);
				var advanced = (advancedPanel&&!advancedPanel.collapsed)
				               ?'advanced'
				               :'simple';
				var page = ''; // Only if not first page
				if (params.start) {
					if (params.start !== '0') {
						page = '/start/'+params.start;
					}
				}
				var filters = ''; // Need to construct
				Ext.each(
					module.logFilterList[tab]
					,function(filter){
						if (!Ext.isEmpty(params[filter], false)){
							filters += '/'+filter+'/'+escape(params[filter]);
						}
					}
				);

				Curriki.logView('/features/search/'+tab+'/'+terms+'/'+advanced+filters+page);

				// Add to history
				Search.doSearch(tab, false, true);
			}
		);

	};

	// Perform a search for a module
	module.doSearch = function(modName, start){
		console.log('Doing search', modName, start);
		var filters = {};

		// Global panel (if exists)
		var filterPanel = Ext.getCmp('search-termPanel');
		if (!Ext.isEmpty(filterPanel)) {
			var filterForm = filterPanel.getForm();
			if (!Ext.isEmpty(filterForm)) {
				Ext.apply(filters, filterForm.getValues(false));
			}
		}
		Ext.apply(filters, {module: modName});

		// Module panel
		filterPanel = Ext.getCmp('search-filterPanel-'+modName);
		if (!Ext.isEmpty(filterPanel)) {
			var filterForm = filterPanel.getForm();
			if (!Ext.isEmpty(filterForm)) {
				Ext.apply(filters, filterForm.getValues(false));
			}
		}

		// Check for emptyText value in terms field
		if (filters.terms && filters.terms === _('search.text.entry.label')){
			filters.terms = '';
		}

		console.log('Applying search filters', filters);

		Ext.apply(Ext.StoreMgr.lookup('search-store-'+modName).baseParams || {}, filters);

		var pager = Ext.getCmp('search-pager-'+modName)
		if (!Ext.isEmpty(pager)) {
			console.log('Searching', filters);
			pager.doLoad(Ext.num(start, 0)); // Reset to first page if the tab is shown
		}
		console.log('Done util.doSearch', filters);
	};

	// General term panel (terms and search button)
	module.createTermPanel = function(modName, form){
		return {
			xtype:'panel'
			,labelAlign:'left'
			,id:'search-termPanel-'+modName
			,border:false
			,items:[{
				layout:'column'
				,border:false
				,defaults:{border:false}
				,items:[{
					columnWidth:0.7
					,layout:'form'
					,items:[{
						xtype:'textfield'
						,fieldLabel:_('search.text.entry.label')
						,name:'terms'
						,hideLabel:true
						,emptyText:_('search.text.entry.label')
						,listeners:{
							specialkey:{
								fn:function(field, e){
									if (e.getKey() === Ext.EventObject.ENTER) {
										Search.doSearch(modName, true);
									}
								}
							}
						}
					}]
				},{
					columnWidth:0.15
					,xtype:'box'
					,autoEl:{html:'<a href="/xwiki/bin/view/Search/Tips?xpage=popup" target="search_tips">'+_('search.text.entry.help.button')+'</a>'}
				},{
					columnWidth:0.10
					,layout:'form'
					,items:[{
						xtype:'button'
						,text:_('search.text.entry.button')
						,listeners:{
							click:{
								fn: function(){
									Search.doSearch(modName, true);
								}
							}
						}
					}]
				}]
			},{
				xtype:'hidden'
				,name:'since'
				,value:(new Date()).add(Date.DAY, -_('search.resource.special.selector.updated.days')).format('Ymd')
			},{
				xtype:'hidden'
				,name:'other'
				,value:(!Ext.isEmpty(Search.restrictions)?Search.restrictions:'')
			}]
		};
	};

/*
	// General help panel
	module.createHelpPanel = function(modName, form){
		var cookie = 'search_help_'+modName;
		return {
			xtype:'fieldset'
			,id:'search-helpPanel-'+modName
			,title:_('search.text.entry.help.button')
			,collapsible:true
			,collapsed:((Search.sessionProvider.get(cookie, 0)===0)?true:false)
			,listeners:{
				collapse:{
					fn:function(panel){
						Search.sessionProvider.clear(cookie);
					}
				}
				,expand:{
					fn:function(panel){
						Search.sessionProvider.set(cookie, 1);
					}
				}
			}
			,border:true
			,autoHeight:true
			,items:[{
				xtype:'box'
				,autoEl:{
					tag:'div'
					,html:_('search.text.entry.help.text')
					,cls:'help-text'
				}
			}]
		};
	};
*/

	module.fieldsetPanelSave = function(panel, state){
		if (Ext.isEmpty(state)) {
			state = {};
		}
		if (!panel.collapsed) {
			state.collapsed = panel.collapsed;
		} else {
			state = null;
		}
		console.log('fieldset Panel Save state:', state);
		Search.sessionProvider.set(panel.stateId || panel.id, state);
	};

	module.fieldsetPanelRestore = function(panel, state){
		if (!Ext.isEmpty(state)
		    && !Ext.isEmpty(state.collapsed)
		    && !state.collapsed) {
			panel.expand(false);
		}
	};

	module.registerSearchLogging = function(tab){
	};

};

Ext.onReady(function(){
	module.init();
});
})();
