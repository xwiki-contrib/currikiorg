// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */ 
/*global _ */

(function(){
Ext.ns('Curriki.module.search.form');

var Search = Curriki.module.search;
var forms = Search.form;
var NO_CONCURRENT_SEARCH_ERRMSG = "No concurrent searches on same tab.";

Search.init = function(){
	console.log('search: init');
	if (Ext.isEmpty(Search.initialized)) {
		if (Ext.isEmpty(Search.tabList)) {
			Search.tabList = ['resource','outerResource', 'group', 'member', 'curriki']; //
		}

		var comboWidth = 140;

		Search.doSearch = function(searchTab, resetPage /* default false */, onlyHistory /* default false */){
			var filterValues = {};
			if (Ext.getCmp('search-termPanel')
			    && Ext.getCmp('search-termPanel').getForm) {
				filterValues['all'] = Ext.getCmp('search-termPanel').getForm().getValues(false);
			}

            var t= $('search-termPanel-'+searchTab+'-terms').getValue();
            if(t==_('search.text.entry.label')) t= "";
            if(document.savedTitle && t!="") {
                document.title = document.savedTitle;
            } else {
                if(typeof(document.savedTitle)=="undefined") document.savedTitle = document.title;
                document.title = _("search.window.title." + searchTab, [t]);
            }
            try {
	            var box = $('curriki-searchbox');
	            if(typeof(box)=="object" && typeof(box.style)=="object") {
	                box.style.color='lightgrey';
	                box.value = t;
	            }
            } catch(e) {
            	console.log('search: curriki-searchbox not found. (Ok in embedded mode)');
            	console.log('EmbeddedMode: ' + Curriki.module.search.util.isInEmbeddedMode());
            }

			var pagerValues = {};

			var panelSettings = {};

			Ext.each(
				Search.tabList
				,function(tab){
					var module = forms[tab];
					if (!Ext.isEmpty(module) && !Ext.isEmpty(module.doSearch)) {
						// Get current values
						var filterPanel = Ext.getCmp('search-filterPanel-'+tab);
						if (!Ext.isEmpty(filterPanel)) {
							var filterForm = filterPanel.getForm();
							if (!Ext.isEmpty(filterForm)) {
								filterValues[tab] = filterForm.getValues(false);
								if ("undefined" !== typeof filterValues[tab]["terms"] && filterValues[tab]["terms"] === _('search.text.entry.label')) {
									delete(filterValues[tab]["terms"]);
								}
								if ("undefined" !== typeof filterValues[tab]["other"] && filterValues[tab]["other"] === '') {
									delete(filterValues[tab]["other"]);
								}
                                // CURRIKI-5404: almost there: store sort in URL
                                if (Ext.StoreMgr.lookup('search-store-' + tab).sortInfo) {
                                    filterValues[tab]['sort'] = new Object();
                                    filterValues[tab]['sort'].field = Ext.StoreMgr.lookup('search-store-' + tab).sortInfo.field;
                                    filterValues[tab]['sort'].dir= Ext.StoreMgr.lookup('search-store-' + tab).sortInfo.direction;
                                }

							}
						}

						var advancedSearch = Ext.getCmp('search-advanced-'+tab);
						if (!Ext.isEmpty(advancedSearch)) {
							if (!advancedSearch.collapsed) {
								panelSettings[tab] = {a:true}; // Advanced open
							}
						}

						var pagerPanel = Ext.getCmp('search-pager-'+tab);
						if (!Ext.isEmpty(pagerPanel)) {
							var pagerInfo = {};
							pagerInfo.c = (("undefined" === typeof resetPage) || (resetPage !== true))?pagerPanel.cursor:0;
							pagerInfo.s = pagerPanel.pageSize;
							pagerValues[tab] = pagerInfo;
						}

						// Do the search
						if ((("undefined" === typeof onlyHistory) || (onlyHistory = false)) && (Ext.isEmpty(searchTab) || searchTab === tab)) {
                            // TODO: MSIE misery here
                            // if(Search['runningSearch' + tab]) throw NO_CONCURRENT_SEARCH_ERRMSG;
                            // Search['runningSearch' + tab] = true;
                            if(Curriki.numSearches>10) return; Curriki.numSearches++;
                            console.log('now util.doSearch (' + window.numSearches + ")", tab, pagerValues);
							Search.util.doSearch(tab, (("undefined" !== typeof pagerValues[tab])?pagerValues[tab].c:0));
						}
					}
				}
			);
Curriki.numSearches = 0;
			var stateObject = {};
			stateObject['s'] = Ext.isEmpty(searchTab)?'all':searchTab;
			stateObject['f'] = filterValues;
			stateObject['p'] = pagerValues;
			if (Ext.getCmp('search-tabPanel').getActiveTab) {
				stateObject['t'] = Ext.getCmp('search-tabPanel').getActiveTab().id;
			}
			stateObject['a'] = panelSettings;

				var provider = new Ext.state.Provider();
				var encodedToken = provider.encodeValue(stateObject);
				console.log('Saving History: '+ encodedToken );
	            if(Search.history.lastHistoryToken || window.currikiHistoryStarted) {
	                Search.history.setLastToken(encodedToken);
	                var created = Ext.History.add(encodedToken,true);
	                if(created) console.log("-- created a new history frame.");
	            } else {
	                window.currikiHistoryStarted = true;
	                Search.history.setLastToken(encodedToken);
	                if(!Curriki.module.search.util.isInEmbeddedMode()){
	                	window.top.location.replace(window.location.pathname + "#" + encodedToken);
	                }else{
	                	location.replace(window.location.pathname + location.search + "#" + encodedToken);
	                }
	                console.log("-- rather replaced history.");
	            }
		};

		Search.tabPanel = {
			xtype:(Search.tabList.size()>1?'tab':'')+'panel'
			,id:'search-tabPanel'
			,activeTab:0
			,deferredRender:false
			,autoHeight:true
			,layoutOnTabChange:true
			,frame:false
			,border:false
			,plain:true
			,defaults:{
				autoScroll:false
				,border:false
			}
			,listeners:{
				tabchange:function(tabPanel, tab){
					// Log changing to view a tab
					var tabId = tab.id.replace(/(^search-|-tab$)/g, '');
					Curriki.logView('/features/search/'+tabId);

					var advancedPanel = Ext.getCmp('search-advanced-'+tabId);
					if (!Ext.isEmpty(advancedPanel)) {
						if (!advancedPanel.collapsed) {
							Ext.select('.x-form-field-wrap', false, 'search-advanced-'+tabId).setWidth(comboWidth);
						}
					}
/*
					var URLtoken = Ext.History.getToken();
					var provider = new Ext.state.Provider();
					var token = provider.decodeValue(URLtoken);
					token['t'] = tabPanel.getActiveTab().id;
					console.log('Saving History', {values: token});
					Ext.History.add(provider.encodeValue(token));
*/
				}
			}
			,items:[] // Filled in based on tabs available
		};
		Ext.each(
			Search.tabList
			,function(tab){
				panel = {
					title: _('search.'+tab+'.tab.title')
					,id:'search-'+tab+'-tab'
					,cls:'search-'+tab
					,autoHeight:true
				};
				module = forms[tab];
				if (!Ext.isEmpty(module) && !Ext.isEmpty(module.mainPanel)) {
					panel.items = [module.mainPanel];
					Search.tabPanel.items.push(panel);
				} else {
                    console.log("Dropping " + tab + " (module Curriki.module.search.form[" + tab + "] is empty).")
                }
			}
		);

		Search.mainPanel = {
			el:'search-div'
			//,title:_('search.top_titlebar')
			,border:false
			,height:'600px'
			,defaults:{border:false}
			,cls:'search-module'
			,items:[
				Search.tabPanel
			]
		};




		Ext.ns('Curriki.module.search.history');
		var History = Search.history;
		History.lastHistoryToken = false;

		// Handle this change event in order to restore the UI
		// to the appropriate history state
		History.historyChange = function(token){
			if(token){
				if(token == History.lastHistoryToken){
					// Ignore duplicate tokens
				} else {
					History.updateFromHistory(token);
				}
			} else {
				// This is the initial default state.
				// Necessary if you navigate starting from the
				// page without any existing history token params
				// and go back to the start state.
			}
		};

		History.setLastToken = function(token){
			History.lastHistoryToken = token;
		};

		History.updateFromHistory = function(token){
			var provider = new Ext.state.Provider();
			var values = provider.decodeValue(token);
            if(History.lastHistoryToken==token || escape(History.lastHistoryToken)==token) return;

			console.log('Got History: ' + token, {values: values});

			if (!Ext.isEmpty(values)) {
				var filterValues = values['f'];
				if (!Ext.isEmpty(filterValues) && filterValues['all'] && Ext.getCmp('search-termPanel') && Ext.getCmp('search-termPanel').getForm) {
					Ext.getCmp('search-termPanel').getForm().setValues(filterValues['all']);
				}

				var pagerValues = values['p'];

				var panelSettings = values['a'];

				if (values['t']) {
					if (Ext.getCmp('search-tabPanel').setActiveTab) {
						Ext.getCmp('search-tabPanel').setActiveTab(values['t']);
					}
				}

				Ext.each(
					Search.tabList
					,function(tab){
						console.log('Updating '+tab);
						var module = Search.form[tab];
                        // TODO: MSIE misery here
                        // if(!Ext.isEmpty(Search) && Search['runningSearch' + tab]) throw NO_CONCURRENT_SEARCH_ERRMSG;
						if (!Ext.isEmpty(module) && !Ext.isEmpty(module.doSearch) && !Ext.isEmpty(filterValues) && !Ext.isEmpty(filterValues[tab])) {
							var filterPanel = Ext.getCmp('search-filterPanel-'+tab);
							if (!Ext.isEmpty(filterPanel)) {
								var filterForm = filterPanel.getForm();
								if (!Ext.isEmpty(filterForm)) {
									try {
										filterForm.setValues(filterValues[tab]);

										// setValues does not trigger the visiblity change of the sub-lists
										var list = Ext.getCmp('combo-subject-'+tab);
										if (list) {
											list.fireEvent("select", list, list.getValue());
											if (!Ext.isEmpty(filterValues[tab].subject)) {
												if (Ext.getCmp('combo-subsubject-'+tab)) {
													Ext.getCmp('combo-subsubject-'+tab).setValue(filterValues[tab].subject);
												}
											}
										}
										list = Ext.getCmp('combo-ictprfx-'+tab);
										if (list) {
											list.fireEvent("select", list, list.getValue());
											if (!Ext.isEmpty(filterValues[tab].ict)) {
												if (Ext.getCmp('combo-subICT-'+tab)) {
													Ext.getCmp('combo-subICT-'+tab).setValue(filterValues[tab].ict);
												}
											}
										}
                                        // attempt at CURRIKI-5404
                                        if (filterValues[tab]['sort']) {
                                            var sortInfo = filterValues[tab]['sort'];
                                            if(sortInfo.field) Ext.StoreMgr.lookup('search-store-' + tab).sortInfo.field = sortInfo['field'];
                                            if(sortInfo.dir)   Ext.StoreMgr.lookup('search-store-' + tab).sortInfo.direction = sortInfo['dir'];
                                        }
									} catch(e) {
										console.log('ERROR Updating '+tab, e);
									}
								}
							}

							// Open advanced panel if specified
							if (!Ext.isEmpty(panelSettings) && !Ext.isEmpty(panelSettings[tab]) && panelSettings[tab].a) {
								var advancedPanel = Ext.getCmp('search-advanced-'+tab);
								if (!Ext.isEmpty(advancedPanel)) {
									advancedPanel.expand(false);
								}
							}

							// Set pager values
							var pagerPanel = Ext.getCmp('search-pager-'+tab);
							if (!Ext.isEmpty(pagerPanel) && !Ext.isEmpty(pagerValues)) {
								if (pagerValues[tab]) {
									try {
										if (pagerValues[tab]['c']) {
											pagerPanel.cursor = pagerValues[tab]['c'];
										}
										if (pagerValues[tab]['s']) {
											if (pagerPanel.pageSize != pagerValues[tab]['s']) {
												pagerPanel.setPageSize(pagerValues[tab]['s']);
											}
										}
									} catch(e) {
										console.log('ERROR Updating '+tab, e);
									}
								}
							}
						}
					}
				);

				if (values['s']) {
					console.log('Starting search');
					if (values['s'] === 'all') {
						Search.doSearch();
					} else {
						Search.doSearch(values['s']);
					}
				}

				History.setLastToken(token);
			}
		};


		History.init = function(){
			if (Ext.isEmpty(History.initialized)) {
				var URLtoken = Ext.History.getToken(); // Get BEFORE init'd
				Ext.History.init(
					function(){
						Ext.History.on('change', History.historyChange);

						if (URLtoken) {
							History.historyChange(URLtoken);
						}
					}
				);

				History.initialized = true;
			};
		};

		Search.initialized = true;
		console.log('search: init done');
	}
};


Search.display = function(){
	Search.init();

	var s = new Ext.Panel(Search.mainPanel);
	s.render();

	if(Curriki.module.search.util.isInEmbeddedMode()){
		Curriki.module.search.util.sendResizeMessageToEmbeddingWindow(); // Initial resizement of the embedding iframe
	}

	Search.history.init();
	
};


Search.start = function(){
	Ext.onReady(function(){
		Search.display();
	});
};
})();
