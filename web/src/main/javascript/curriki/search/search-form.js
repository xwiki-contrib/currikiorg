// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
Ext.ns('Curriki.module.search.form');

var Search = Curriki.module.search;
var forms = Search.form;

Search.init = function(){
	console.log('search: init');
	if (Ext.isEmpty(Search.initialized)) {
		Search.ignoreNextHistoryChange = false;
		Search.ignoreNextSearchForHistory = false;

		if (Ext.isEmpty(Search.tabList)) {
			Search.tabList = ['resource', 'group', 'member', 'blog', 'curriki'];
		}

		Search.doSearch = function(searchTab){
			var filterValues = {};
			//filterValues['all'] = Ext.getCmp('search-termPanel').getForm().getValues(false);

			var pagerValues = {};

			var panelSettings = {};

			Ext.each(
				Search.tabList
				,function(tab){
					var module = forms[tab];
					if (!Ext.isEmpty(module) && !Ext.isEmpty(module.doSearch)) {
						var filterPanel = Ext.getCmp('search-filterPanel-'+tab);
						if (!Ext.isEmpty(filterPanel)) {
							var filterForm = filterPanel.getForm();
							if (!Ext.isEmpty(filterForm)) {
								filterValues[tab] = filterForm.getValues(false);
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
							pagerInfo.c = 0;
							pagerInfo.s = pagerPanel.pageSize;
							pagerValues[tab] = pagerInfo;
						}
						if (Ext.isEmpty(searchTab) || searchTab === tab) {
							module.doSearch();
						}
					}
				}
			);

			if (!Search.ignoreNextSearchForHistory) {
				var token = {};
				token['s'] = Ext.isEmpty(searchTab)?'all':searchTab;
				token['f'] = filterValues;
				token['p'] = pagerValues;
				if (Ext.getCmp('search-tabPanel').getActiveTab) {
					token['t'] = Ext.getCmp('search-tabPanel').getActiveTab().id;
				}
				token['a'] = panelSettings;
				var provider = new Ext.state.Provider();
				Search.ignoreNextHistoryChange = true;
				console.log('Saving History', {values: token});
				Ext.History.add(provider.encodeValue(token));
			} else {
				Search.ignoreNextSearchForHistory = false;
			}
		};

/*
		Search.termPanel = {
			xtype:'form'
			,labelAlign:'top'
			,id:'search-termPanel'
			,formId:'search-termForm'
			,items:[{
				layout:'column'
				,border:false
				,defaults:{border:false}
				,items:[{
					columnWidth:0.8
					,layout:'form'
					,items:[{
						xtype:'textfield'
						,fieldLabel:_('search.text.entry.label')
						,name:'terms'
						,listeners:{
							specialkey:{
								fn:function(field, e){
									if (e.getKey() === Ext.EventObject.ENTER) {
										Search.doSearch();
									}
								}
							}
						}
					}]
				},{
					columnWidth:0.15
					,layout:'form'
					,items:[{
						xtype:'button'
						,id:'search-termPanel-searchButton'
						,text:_('search.text.entry.button')
						,listeners:{
							click:{
								fn: function(){
									Search.doSearch();
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
				,value:''
			}]
		};
*/

/*
		Search.helpPanel = {
			xtype:'panel'
			,id:'search-helpPanel'
			,title:_('search.text.entry.help.button')
			,collapsible:true
			,collapsed:((Search.sessionProvider.get('search_help', 0)===0)?true:false)
			,listeners:{
				collapse:{
					fn:function(panel){
						Search.sessionProvider.clear('search_help');
					}
				}
				,expand:{
					fn:function(panel){
						Search.sessionProvider.set('search_help', 1);
					}
				}
			}
			,html:_('search.text.entry.help.text')
		};
*/

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
/*
				tabchange:function(tabPanel, tab){
					var URLtoken = Ext.History.getToken();
					var provider = new Ext.state.Provider();
					var token = provider.decodeValue(URLtoken);
					token['t'] = tabPanel.getActiveTab().id;
					Search.ignoreNextHistoryChange = true;
					console.log('Saving History', {values: token});
					Ext.History.add(provider.encodeValue(token));
				}
*/
			}
			,items:[] // Filled in based on tabs available
		};
		Ext.each(
			Search.tabList
			,function(tab){
				panel = {
					title: _('search.'+tab+'.tab.title')
					,id:'search-'+tab+'-tab'
					,autoHeight:true
//					,closable:true
//					,listeners:{
//						close:{
//							fn:function(tabPanel){
//								// TODO: Mark tab as not shown in future
//							}
//						}
//					}
				};
				module = forms[tab];
				if (!Ext.isEmpty(module) && !Ext.isEmpty(module.mainPanel)) {
					panel.items = [module.mainPanel];
					Search.tabPanel.items.push(panel);
				}
			}
		);

		Search.mainPanel = {
			el:'search-div'
			//,title:_('search.top_titlebar')
			,border:false
			,height:'600px'
			,defaults:{border:false}
			,items:[
//				Search.termPanel
//				,Search.helpPanel
				Search.tabPanel
			]
		};

		Search.initialized = true;
		console.log('search: init done');
	}
};

Search.history = function(){
	if (!Ext.isEmpty(Ext.History)) {
		// Handle this change event in order to restore the UI
		// to the appropriate history state
		Search.historyChange = function(token){
			if (!Search.ignoreNextHistoryChange) {
				if(token){
					var provider = new Ext.state.Provider();
					var values = provider.decodeValue(token);
					console.log('Got History', {token: token, values: values});

					var filterValues = values['f'];
					//Ext.getCmp('search-termPanel').getForm().setValues(filterValues['all']);

					var pagerValues = values['p'];

					var panelSettings = values['a'];

					Ext.each(
						Search.tabList
						,function(tab){
							console.log('Updating '+tab);
							var module = Search.form[tab];
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
											}
											list = Ext.getCmp('combo-ictprfx-'+tab);
											if (list) {
												list.fireEvent("select", list, list.getValue());
											}
										} catch(e) {
											console.log('ERROR Updating '+tab, e);
										}
									}
								}

								if (!Ext.isEmpty(panelSettings) && !Ext.isEmpty(panelSettings[tab]) && !Ext.isEmpty(panelSettings[tab].a)) {
									var advancedPanel = Ext.getCmp('search-advanced-'+tab);
									if (!Ext.isEmpty(advancedPanel)) {
										advancedPanel.expand(false);
									}
								}

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
					if (values['t']) {
						if (Ext.getCmp('search-tabPanel').setActiveTab) {
							Ext.getCmp('search-tabPanel').setActiveTab(values['t']);
						}
					}
					if (values['s']) {
						console.log('Starting search');
						Search.ignoreNextSearchForHistory = true;
						if (values['s'] === 'all') {
							Search.doSearch();
						} else {
							var module = Search.form[values['s']];
							if (!Ext.isEmpty(module) && !Ext.isEmpty(module.doSearch)) {
								module.doSearch();
							}
						}
					}
				} else {
					// TODO:
					// This is the initial default state.
					// Necessary if you navigate starting from the
					// page without any existing history token params
					// and go back to the start state.
				}
			} else {
				Search.ignoreNextHistoryChange = false;
			}
		};

		var URLtoken = Ext.History.getToken(); // Get BEFORE init'd
		Ext.History.init(
			function(){
				Ext.History.on('change', Search.historyChange);

				if (URLtoken) {
					Search.historyChange(URLtoken);
				}
			}
		);
	}
};

Search.display = function(){
	//Init the qtip singleton
	Ext.QuickTips.init();

	Search.init();

	var s = new Ext.Panel(Search.mainPanel);
	s.render();

	Search.history();
};

Search.start = function(){
	Ext.onReady(function(){
		Search.display();
	});
};
})();
