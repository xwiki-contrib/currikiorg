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
        'outerResource':['subject', 'level', 'language', 'ict', 'review', 'special', 'other', 'sort', 'dir'],
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
				var overmax = false;
				var totalCount = 0;
				var resultCount = store.getTotalCount();
				if (!Ext.isEmpty(store.reader.jsonData) && !Ext.isEmpty(store.reader.jsonData.totalResults)) {
					totalCount = parseInt(store.reader.jsonData.totalResults);
				}
				if (totalCount > resultCount) {
					overmax = true;
				}

				var tab = Ext.getCmp('search-'+modName+'-tab');
				if (!Ext.isEmpty(tab)) {
					var titleMsg = _('search.tab.title.results');
					if (overmax && (_('search.tab.title.resultsmax_exceeds') !== 'search.tab.title.resultsmax_exceeds')) {
						titleMsg = _('search.tab.title.resultsmax_exceeds');
					}

					tab.setTitle(String.format(titleMsg, _('search.'+modName+'.tab.title'), resultCount, totalCount));

				}

				var pager = Ext.getCmp('search-pager-'+modName);
				if (!Ext.isEmpty(pager)) {
					var afterPageText = _('search.pagination.afterpage');
					if (overmax && (_('search.pagination.afterpage_resultsmax_exceeds') !== 'search.pagination.afterpage_resultsmax_exceeds')) {
						afterPageText = _('search.pagination.afterpage_resultsmax_exceeds');
					}
					pager.afterPageText = String.format(afterPageText, '{0}', totalCount);

					var displayMsg = _('search.pagination.displaying.'+modName);
					if (overmax && (_('search.pagination.displaying.'+modName+'_resultsmax_exceeds') !== 'search.pagination.displaying.'+modName+'_resultsmax_exceeds')) {
						displayMsg = _('search.pagination.displaying.'+modName+'_resultsmax_exceeds');
					}
					pager.displayMsg = String.format(displayMsg, '{0}', '{1}', '{2}', totalCount);
				}
			}
		);

        Ext.StoreMgr.lookup('search-store-'+modName).addListener(
                'beforeload'
                ,function(s, o) {
                    var store = Ext.StoreMgr.lookup('search-store-'+modName);
                    var pager = Ext.getCmp('search-pager-'+modName);
                    store.baseParams.rows = pager.pageSize;
                    return true;
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

				if(Curriki.module.search.util.isInEmbeddedMode()){
					Curriki.module.search.util.sendResizeMessageToEmbeddingWindow();
                    Curriki.logView('/features/embeddedsearch/'+tab+'/'+terms+'/'+advanced+filters+page);
                }else {
                    Curriki.logView('/features/search/'+tab+'/'+terms+'/'+advanced+filters+page);
                }


				// Add to history
                    // TODO: MSIE misery... have commented this out
				//Search.doSearch(tab, false, true);

                // stop blocking other searches
                // TODO: MSIE misery here
                // Search['runningSearch' + modName] = false;

			}
		);

        Ext.StoreMgr.lookup('search-store-'+modName).addListener(
            'exception'
            ,Curriki.notifyException
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
        var modName2 = modName;
        if(modName2=='otherResource') modName2='resource';
		Ext.apply(filters, {module: modName2});

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
			,cls:'term-panel'
			,border:false
			,items:[{
				layout:'column'
				,border:false
				,defaults:{border:false}
				,items:[{
					layout:'form'
					,id:'search-termPanel-'+modName+'-form'
					,cls:'search-termPanel-form'
					,items:[{
						xtype:'textfield'
						,id:'search-termPanel-'+modName+'-terms'
						,cls:'search-termPanel-terms'
						,fieldLabel:_('search.text.entry.label')
						,name:'terms'
						,hideLabel:true
						,emptyText:_('search.text.entry.label')
						,listeners:{
							specialkey:{
								fn:function(field, e){
									if (e.getKey() === Ext.EventObject.ENTER) {
										e.stopEvent();
                                        if('resource'==modName && Ext.StoreMgr.lookup('search-store-resource').sortInfo) {
                                            Ext.StoreMgr.lookup('search-store-resource').sortInfo.field = 'score';
                                            Ext.StoreMgr.lookup('search-store-resource').sortInfo.direction = 'DESC';
                                        }
										Search.doSearch(modName, true);
									}
								}
							}
						}
					}]
				},{
					layout:'form'
					,id:'search-termPanel-buttonColumn-'+modName
					,cls:'search-termPanel-buttonColumn'
					,items:[{
						xtype:'button'
						,id:'search-termPanel-button-'+modName
						,cls:'search-termPanel-button'
						,text:_('search.text.entry.button')
						,listeners:{
							click:{
								fn: function(){
                                    if('resource'==modName && Ext.StoreMgr.lookup('search-store-resource').sortInfo) {
                                        Ext.StoreMgr.lookup('search-store-resource').sortInfo.field = 'score';
                                        Ext.StoreMgr.lookup('search-store-resource').sortInfo.direction = 'DESC';
                                    }
									Search.doSearch(modName, true);
								}
							}
						}
					}]
				},{
					xtype:'box'
					,id:'search-termPanel-tips-'+modName
					,cls:'search-termPanel-tips'
					,autoEl:{html:'<a href="/xwiki/bin/view/Search/Tips?xpage=popup" target="search_tips" onclick="{var popup=window.open(this.href, \'search_tips\', \'width=725,height=400,status=no,toolbar=no,menubar=no,location=no,resizable=yes\'); popup.focus();} return false;">'+_('search.text.entry.help.button')+'</a>'}
				}]
			},{
				xtype:'hidden'
				,name:'other'
				,id:'search-termPanel-other-'+modName
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

	module.isInEmbeddedMode = function(){
		return !(	//If this attribute is not set, we can are not in embedded mode
					typeof Curriki.module.search.embeddingPartnerUrl === "undefined");
	};

    module.sendResizeMessageToEmbeddingWindow = function() {
		var height = document.body.scrollHeight + 25;
		console.log("search: sending resource view height to embedding window (" + height + "px)");
		var data = "resize:height:"+ height + "px;"
		window.parent.postMessage(data,'*');
	};

	module.sendResourceUrlToEmbeddingWindow = function(url) {
		console.log("search: sending resource url to embedding window (" + url + ")");
		var data = "resourceurl:"+url;
		window.parent.postMessage(data,'*');
	};
	
	module.registerSearchLogging = function(tab){
	};


};

Ext.onReady(function(){
	module.init();
});
})();
