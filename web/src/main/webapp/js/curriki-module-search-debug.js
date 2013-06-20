// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
	Ext.ns('Curriki.module.search');

	var Search = Curriki.module.search;

	Search.settings = {
		gridWidth:(Ext.isIE6?620:'auto')
	};

	Search.stateProvider = new Ext.state.CookieProvider({
	});
	Ext.state.Manager.setProvider(Search.stateProvider);

	Search.sessionProvider = new Ext.state.CookieProvider({
		expires: null // Valid until end of browser session
	});


    Curriki.module.search.outerResources = {
        prefix: "http://www.curriki.org/xwiki/bin/view/",
        suffix: "?comingFrom=" + location.host,
        target: "currikiResources",
        ratingsPrefix: "http://www.curriki.org/xwiki/bin/view/",
        ratingsSuffix : "?viewer=comments"
        };
})();
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
        if(modName2=='outerResource') modName2='resource';
        if(modName2=='curriki') modName2='discussions';
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
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modNames = ['outerResource', 'resource'];
for(var i=0; i<2; i++) {
    var modName = modNames[i];
    Ext.ns('Curriki.module.search.data.'+modName);

Curriki.module.search.data[modName].init = function(modName){
    var data = Curriki.module.search.data[modName];
    console.log('data.'+modName+': init');

	// Set up filters
	data.filter = {};
	var f = data.filter; // Alias

	f.data = {};

	f.data.subject =  {
		mapping: Curriki.data.fw_item.fwMap['FW_masterFramework.WebHome']
		,list: []
		,data: [
			['', _('CurrikiCode.AssetClass_fw_items_FW_masterFramework.UNSPECIFIED')]
		]
	};
	f.data.subject.mapping.each(function(value){
		f.data.subject.list.push(value.id);
	});

	// CURRIKI-2872
	f.data.subject.list.push('UNCATEGORIZED');

	f.data.subject.list.each(function(value){
		f.data.subject.data.push([
			value
			,_('CurrikiCode.AssetClass_fw_items_'+value)
		]);
	});

	f.data.subsubject =  {
		mapping: Curriki.data.fw_item.fwMap
		,data: [
		]
	};
	f.data.subject.mapping.each(function(parentItem){
		f.data.subsubject.data.push([
			parentItem.id
			,_('CurrikiCode.AssetClass_fw_items_'+parentItem.id+'.UNSPECIFIED')
			,parentItem.id
		]);
		f.data.subsubject.mapping[parentItem.id].each(function(subject){
			f.data.subsubject.data.push([
				subject.id
				,_('CurrikiCode.AssetClass_fw_items_'+subject.id)
				,parentItem.id
			]);
		});
	});

	f.data.level =  {
		list: Curriki.data.el.list
		,data: [
			['', _('CurrikiCode.AssetClass_educational_level_UNSPECIFIED')]
		]
	};
	f.data.level.list.each(function(value){
		f.data.level.data.push([
			value
			,_('CurrikiCode.AssetClass_educational_level_'+value)
		]);
	});

	f.data.ict =  {
		fullList: Curriki.data.ict.list
		,parentList: {}
		,list: []
		,data: [
			['', _('CurrikiCode.AssetClass_instructional_component_UNSPECIFIED', '   ')]
		]
	};
	f.data.ict.fullList.each(function(value){
		var name = value.replace(/_.*/, '');
		f.data.ict.parentList[name] = name;
	});
	Object.keys(f.data.ict.parentList).each(function(value){
        var sort = _('CurrikiCode.AssetClass_instructional_component_'+value);
        if (value === 'other') {
                sort = 'zzz';
        }

		f.data.ict.data.push([
			value
			,_('CurrikiCode.AssetClass_instructional_component_'+value)
			,sort
		]);
	});

	f.data.subict =  {
		list: Curriki.data.ict.list
		,parents: {}
		,data: [
		]
	};
	f.data.subict.list.each(function(value){
		var parentICT = value.replace(/_.*/, '');
		if (parentICT !== value) {
			if (Ext.isEmpty(f.data.subict.parents[parentICT])) {
				f.data.subict.data.push([
					parentICT+'*'
					,_('CurrikiCode.AssetClass_instructional_component_'+parentICT+'_UNSPECIFIED')
					,parentICT
					,'   '
				]);
				f.data.subict.parents[parentICT] = parentICT;
			}

			var sort = _('CurrikiCode.AssetClass_instructional_component_'+value);
			if (value === 'other') {
					sort = 'zzz';
			}

			f.data.subict.data.push([
				value
				,_('CurrikiCode.AssetClass_instructional_component_'+value)
				,parentICT
				,sort
			]);
		}
	});

	f.data.language =  {
		list: Curriki.data.language.list
		,data: [
			['', _('CurrikiCode.AssetClass_language_UNSPECIFIED')]
		]
	};
	f.data.language.list.each(function(value){
		f.data.language.data.push([
			value
			,_('CurrikiCode.AssetClass_language_'+value)
		]);
	});

	f.data.category =  {
		list: Curriki.data.category.list
		,data: [
			['', _('CurrikiCode.AssetClass_category_UNSPECIFIED'), '   ']
		]
	};
	f.data.category.list.each(function(value){
		var sort = _('CurrikiCode.AssetClass_category_'+value);
		if (value === 'unknown') {
			sort = 'zzz';
		}
		if (value !== 'collection') { //collection should not be in the list
			f.data.category.data.push([
				value
				,_('CurrikiCode.AssetClass_category_'+value)
				,sort
			]);
		}
	});

	f.data.review = {
		list: [ 'partners', 'highest_rated', 'members.highest_rated' ]
		,data: [
			['', _('search.resource.review.selector.UNSPECIFIED')]
		]
	};
	f.data.review.list.each(function(review){
		f.data.review.data.push([
			review
			,_('search.resource.review.selector.'+review)
		]);
	});

	f.data.special = {
		list: [ 'contributions', 'collections', 'updated', 'info-only' ]
		,data: [
			['', _('search.resource.special.selector.UNSPECIFIED')]
		]
	};
	f.data.special.list.each(function(special){
		f.data.special.data.push([
			special
			,_('search.resource.special.selector.'+special)
		]);
	});


	f.store = {
		subject: new Ext.data.SimpleStore({
			fields: ['id', 'subject']
			,data: f.data.subject.data
			,id: 0
		})

		,subsubject: new Ext.data.SimpleStore({
			fields: ['id', 'subject', 'parentItem']
			,data: f.data.subsubject.data
			,id: 0
		})

		,level: new Ext.data.SimpleStore({
			fields: ['id', 'level']
			,data: f.data.level.data
			,id: 0
		})

		,ict: new Ext.data.SimpleStore({
			fields: ['id', 'ict', 'sortValue']
			,sortInfo: {field:'sortValue', direction:'ASC'}
			,data: f.data.ict.data
			,id: 0
		})

		,subict: new Ext.data.SimpleStore({
			fields: ['id', 'ict', 'parentICT', 'sortValue']
			,sortInfo: {field:'sortValue', direction:'ASC'}
			,data: f.data.subict.data
			,id: 0
		})

		,language: new Ext.data.SimpleStore({
			fields: ['id', 'language']
			,data: f.data.language.data
			,id: 0
		})

		,category: new Ext.data.SimpleStore({
			fields: ['id', 'category', 'sortValue']
			,sortInfo: {field:'sortValue', direction:'ASC'}
			,data: f.data.category.data
			,id: 0
		})

		,review: new Ext.data.SimpleStore({
			fields: ['id', 'review']
			,data: f.data.review.data
			,id: 0
		})

		,special: new Ext.data.SimpleStore({
			fields: ['id', 'special']
			,data: f.data.special.data
			,id: 0
		})
	};



	// Set up data store
	data.store = {};

	data.store.record = new Ext.data.Record.create([
		{ name: 'title' }
		,{ name: 'assetType' }
		,{ name: 'category' }
		,{ name: 'subcategory' }
		,{ name: 'ict' }
		,{ name: 'ictText' }
		,{ name: 'ictIcon' }
		,{ name: 'contributor' }
		,{ name: 'contributorName' }
		,{ name: 'rating', mapping: 'review' }
		,{ name: 'memberRating', mapping: 'rating' }
		,{ name: 'ratingCount' }
		,{ name: 'description' }
		,{ name: 'fwItems' }
		,{ name: 'levels' }
		,{ name: 'parents' }
		,{ name: 'lastUpdated' }
		,{ name: 'updated' }
        ,{ name: 'score' }
	]);

	data.store.results = new Ext.data.Store({
		storeId: 'search-store-'+modName
		,proxy: new Ext.data.HttpProxy({
			url: document.location.pathname.endsWith("Old") ?
                    '/xwiki/bin/view/Search/Resources' : (modName=='outerResource'? '/outerCurrikiExtjs' : '/currikiExtjs')
			,method:'GET'
		})
		,baseParams: {	xpage: "plain"
                     	//"json.wrf": "Curriki.module.search.data.resource.store.results.loadData" // parameter for Solr to wrap the json result into a function call
                     	//, '_dc':(new Date().getTime())
                      }
		,reader: new Ext.data.JsonReader({
			root: 'rows'
			,totalProperty: 'resultCount'
			,id: 'page'
		}, data.store.record)

		// turn on remote sorting
		,remoteSort: true
	});
    if(Curriki.userinfo.userGroups) data.store.results.baseParams.groupsId= Curriki.userinfo.userGroups;
    if(Curriki.userinfo.userName) data.store.results.baseParams.userId = Curriki.userinfo.userName;
    if(Curriki.userinfo.isAdmin) data.store.results.baseParams.isAdmin = true;
    if(Curriki.isISO8601DateParsing() ) data.store.results.baseParams.dateFormat="ISO8601";
	data.store.results.setDefaultSort('score', 'desc');



	// Set up renderers
	data.renderer = {
		title: function(value, metadata, record, rowIndex, colIndex, store){
            console.log("render title " + value);
            if(typeof(value)!="string") title ="";
			// Title
			var page = record.id.replace(/\./, '/');

			var desc = Ext.util.Format.stripTags(record.data.description);
			desc = Ext.util.Format.ellipsis(desc, 256);
			desc = Ext.util.Format.htmlEncode(desc);

			var fw = Curriki.data.fw_item.getRolloverDisplay(record.data.fwItems||[]);
			var lvl = Curriki.data.el.getRolloverDisplay(record.data.levels||[]);
			var lastUpdated = record.data.lastUpdated||'';

			var qTipFormat = '{1}<br />{0}<br /><br />';

			// Add lastUpdated if available
			if (lastUpdated !== '') {
				qTipFormat = qTipFormat+'{7}<br />{6}<br /><br />';
			}

			// Base qTip (framework, ed levels)
			qTipFormat = qTipFormat+'{3}<br />{2}<br />{5}<br />{4}';


			desc = String.format(qTipFormat
				,desc,_('global.title.popup.description')
				,fw,_('global.title.popup.subject')
				,lvl,_('global.title.popup.educationlevel')
				,lastUpdated,_('global.title.popup.last_updated')
			);

			// Asset Type icon
			var assetType = record.data.assetType;
			var category = record.data.category;
			var subcategory = record.data.subcategory;
			metadata.css = String.format('resource-{0} category-{1} subcategory-{1}_{2}', assetType, category, subcategory); // Added to <td>

			var rollover = _(category+'.'+subcategory);
			if (rollover === category+'.'+subcategory) {
				rollover = _('unknown.unknown');
			}

			if(Curriki.module.search.util.isInEmbeddedMode()){
				return String.format('<img class="x-tree-node-icon assettype-icon" src="{3}" ext:qtip="{4}" /><a  target="_blank" href="' + Curriki.module.search.resourceDisplay + '?resourceurl=/xwiki/bin/view/{0}" class="asset-title" ext:qtip="{2}">{1}</a>', escape(page+'?'+Curriki.module.search.embedViewMode), Ext.util.Format.ellipsis(value, 80), desc, Ext.BLANK_IMAGE_URL, rollover);			
				// return String.format('<img class="x-tree-node-icon assettype-icon" src="{3}" ext:qtip="{4}" /><a onclick="Curriki.module.search.util.sendResourceUrlToEmbeddingWindow(\'/xwiki/bin/view/{0}\')" href="#" class="asset-title" ext:qtip="{2}">{1}</a>', escape(page+"?viewer=embed-teachhub"), Ext.util.Format.ellipsis(value, 80), desc, Ext.BLANK_IMAGE_URL, rollover);			
            } else if(modName=="outerResource") {
                var outer = Curriki.module.search.outerResources;
                return String.format('<img class="x-tree-node-icon assettype-icon" src="{0}" ext:qtip="{1}" /><a href="{2}{3}{4}" target="{5}" class="asset-title" ext:qtip="{1}">{6}</a>',
                    Ext.BLANK_IMAGE_URL, desc, outer.prefix, page, outer.suffix, outer.target,  value);
            }else {
				return String.format('<img class="x-tree-node-icon assettype-icon" src="{3}" ext:qtip="{4}" /><a href="/xwiki/bin/view/{0}" class="asset-title" ext:qtip="{2}">{1}</a>',
                    page, Ext.util.Format.ellipsis(value, 80), desc, Ext.BLANK_IMAGE_URL, rollover);
			}
		}

		,ict: function(value, metadata, record, rowIndex, colIndex, store){
			var css;
			var dotIct;
			var ict = record.data.ict;
            console.log("render ict " + value);
			if (!Ext.isEmpty(ict)){
				// Find CSS classes needed
				var topIct = ict.replace(/_.*/, '');
				css = 'ict-'+topIct;
				if (topIct !== ict) {
					css = css + ' ict-'+ict;
				}

				// Get value to use in lookup key
				dotIct = ict.replace(/_/, '.');
			} else {
				css = 'ict-unknown';
				dotIct = 'unknown';
			}
			metadata.css = css;
			return String.format('<img class="ict-icon" src="{1}" /><span class="ict-title">{0}</span>', _('search.resource.ict.'+dotIct), Ext.BLANK_IMAGE_URL);
		}

		,contributor: function(value, metadata, record, rowIndex, colIndex, store){
			var page = value.replace(/\./, '/');
            console.log("render contributor " + value);
            if(typeof("value")!="string") value="";
            if(Curriki.module.search.util.isInEmbeddedMode()){
				return String.format('<a href="/xwiki/bin/view/{0}" target="_blank">{1}</a>', page, record.data.contributorName);
            } else if(modName=="outerResource") {
                var outer = Curriki.module.search.outerResources;
                return String.format('<a href="{0}{1}{2}" target="{3}">{4}</a>', outer.prefix, page, outer.suffix, outer.target, record.data.contributorName);
			} else{
				return String.format('<a href="/xwiki/bin/view/{0}">{1}</a>', page, record.data.contributorName);
			}
		}

		,rating: function(value, metadata, record, rowIndex, colIndex, store){
            console.log("render rating " + value);
			if (typeof(value)=="string" && value != "") {
				var page = record.id.replace(/\./, '/');

				metadata.css = String.format('crs-{0}', value); // Added to <td>
				//metadata.attr = String.format('title="{0}"', _('curriki.crs.rating'+value)); // Added to <div> around the returned HTML
				
				
				if(Curriki.module.search.util.isInEmbeddedMode()){
					return String.format('<a href="/xwiki/bin/view/{3}?viewer=comments" target="_blank"><img class="crs-icon" alt="" src="{2}" /><span class="crs-text">{1}</span></a>', value, _('search.resource.review.'+value), Ext.BLANK_IMAGE_URL, page);
                } else if(modName=="outerResource") {
                    var outer = Curriki.module.search.outerResources;
                    return String.format('<a "{0}{1}{2}" target="{3}"><img class="crs-icon" alt="" src="{4}" /><span class="crs-text">{5}</span></a>',
                        outer.ratingsPrefix, page, outer.ratingsSuffix, outer.target, Ext.BLANK_IMAGE_URL, _('search.resource.review.'+value));
                } else {
					return String.format('<a href="/xwiki/bin/view/{3}?viewer=comments"><img class="crs-icon" alt="" src="{2}" /><span class="crs-text">{1}</span></a>', value, _('search.resource.review.'+value), Ext.BLANK_IMAGE_URL, page);
				}
			} else {
				return String.format('');
			}
		}

		,memberRating: function(value, metadata, record, rowIndex, colIndex, store){
            console.log("render memberRating " + value);
			if (typeof(value)=="string"  && value != "" && value != "0" && value != 0) {
				var page = record.id.replace(/\./, '/');
				var ratingCount = record.data.ratingCount;

				if (ratingCount != "" && ratingCount != "0" && ratingCount != 0) {
					metadata.css = String.format('rating-{0}', value);
					if(Curriki.module.search.util.isInEmbeddedMode()){
						return String.format('<a href="/xwiki/bin/view/{2}?viewer=comments" target="_blank"><img class="rating-icon" src="{4}" ext:qtip="{3}" /></a><a href="/xwiki/bin/view/{2}?viewer=comments" ext:qtip="{3}" target="_blank"> ({1})</a>', value, ratingCount, page, _('search.resource.rating.'+value), Ext.BLANK_IMAGE_URL);
                    } else if(modName=="outerResource") {
                        var outer = Curriki.module.search.outerResources;
                        return String.format('<a href="{0}{1}{2}"><img class="rating-icon" src="{3}" ext:qtip="{4}" /></a><a href="{0}{1}{2}" ext:qtip="{4}"> ({5})</a>',
                            outer.ratingsPrefix, page, outer.ratingsSuffix, Ext.BLANK_IMAGE_URL, _('search.resource.rating.'+value), ratingCount);
                    }else{
						return String.format('<a href="/xwiki/bin/view/{2}?viewer=comments"><img class="rating-icon" src="{4}" ext:qtip="{3}" /></a><a href="/xwiki/bin/view/{2}?viewer=comments" ext:qtip="{3}"> ({1})</a>', value, ratingCount, page, _('search.resource.rating.'+value), Ext.BLANK_IMAGE_URL);
					}
				} else {
					return String.format('');
				}
			} else {
				return String.format('');
			}
		}

		,updated: function(value, metadata, record, rowIndex, colIndex, store){
            console.log("render updated " + value);
            if(typeof("value")!="string") return "";
			var dt = Ext.util.Format.date(value, 'M-d-Y');
            if(typeof(dt)!="string") return "";
			return String.format('{0}', dt);
		}
        , score: function(value, metadata, record, rowIndex, colIndex, store){
            if(typeof(value)!="number") value=0;
            return value;
         }
	};
    console.log("Finished initting data for " + modName + ".");
};
}
})();

Ext.onReady(function(){
    Curriki.module.search.data.outerResource.init("outerResource");
    Curriki.module.search.data.resource.init("resource");
});
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */
Ext.ns('Curriki.module.search');
var Search = Curriki.module.search;

(function(){

    var modNames = ['outerResource', 'resource'];
    for(var i=0; i<2; i++) {
    var modName = modNames[i];

Ext.ns('Curriki.module.search.form.'+modName);




Search.form[modName].init = function(modName){
    var form = Search.form[modName];
    var data = Search.data[modName];
    console.log('form.'+modName+': init');

	var comboWidth = 140;
	var comboListWidth = 250;

	// Plugin to add icons to ICT combo box
	form.ictCombo = function(config) {
		Ext.apply(this, config);
	};

    // disable top search box, it triggers inconsistent searches, rather not use this double of function
    var searchBox = $('curriki-searchbox');
    if(!Ext.isEmpty(searchBox)) {
        searchBox.setValue("...");
        searchBox.setAttribute("curriki:deftxt","...");
        searchBox.disable();
        
    }
    var searchBoxGoBtn = $('searchbtn');
    if(!Ext.isEmpty(searchBoxGoBtn)) {
        searchBoxGoBtn.innerHTML="";
    }


	Ext.extend(form.ictCombo, Ext.util.Observable, {
		init:function(combo){
			Ext.apply(combo, {
				tpl:  '<tpl for=".">'
					+ '<div class="x-combo-list-item ict-icon-combo-item '
					+ 'ict-{' + combo.valueField + '}">'
					+ '<img class="ict-icon" src="'+Ext.BLANK_IMAGE_URL+'"/>'
					+ '<span class="ict-title">{' + combo.displayField + '}</span>'
					+ '</div></tpl>',

				onRender:combo.onRender.createSequence(function(ct, position) {
					// adjust styles
					this.wrap.applyStyles({position:'relative'});
					this.el.addClass('ict-icon-combo-input');

					// add div for icon
					this.icon = Ext.DomHelper.append(this.el.up('div.x-form-field-wrap'), {
						tag:'div'
						,style:'position:absolute'
						,children:{tag:'div', cls:'ict-icon'}
					});
				}), // end of function onRender

				setIconCls:function() {
					var rec = this.store.query(this.valueField, this.getValue()).itemAt(0);
					if(rec) {
						this.icon.className = 'ict-icon-combo-icon ict-'+rec.get(this.valueField);
					}
				}, // end of function setIconCls

				setValue:combo.setValue.createSequence(function(value) {
					this.setIconCls();
				})
			});
		}
	});

	// Plugin to add icons to Category combo box
	form.categoryCombo = function(config) {
		Ext.apply(this, config);
	};
	Ext.extend(form.categoryCombo, Ext.util.Observable, {
		init:function(combo){
			Ext.apply(combo, {
				tpl:  '<tpl for=".">'
					+ '<div class="x-combo-list-item category-icon-combo-item '
					+ 'category-{' + combo.valueField + '}">'
					+ '<img class="category-icon" src="'+Ext.BLANK_IMAGE_URL+'"/>'
					+ '<span class="category-title">{' + combo.displayField + '}</span>'
					+ '</div></tpl>',

				onRender:combo.onRender.createSequence(function(ct, position) {
					// adjust styles
					this.wrap.applyStyles({position:'relative'});
					this.el.addClass('category-icon-combo-input');

					// add div for icon
					this.icon = Ext.DomHelper.append(this.el.up('div.x-form-field-wrap'), {
						tag:'div'
						,style:'position:absolute'
						,children:{tag:'div', cls:'category-icon'}
					});
				}), // end of function onRender

				setIconCls:function() {
					var rec = this.store.query(this.valueField, this.getValue()).itemAt(0);
					if(rec) {
						this.icon.className = 'category-icon-combo-icon category-'+rec.get(this.valueField);
					}
				}, // end of function setIconCls

				setValue:combo.setValue.createSequence(function(value) {
					this.setIconCls();
				})
			});
		}
	});

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
				,title:''// _('search.advanced.search.button')
				,id:'search-advanced-'+modName
				,autoHeight:true
				,collapsible:false
				,collapsed:false
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
							,emptyText:_('CurrikiCode.AssetClass_fw_items_FW_masterFramework.UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
							,listeners:{
								select:{
									fn:function(combo, value){
										var subSubject = Ext.getCmp('combo-subsubject-'+modName);
										if (combo.getValue() === '') {
											subSubject.clearValue();
											subSubject.hide();
										// Special case - UNCATEGORIZED does not show sub-items
										} else if (combo.getValue() === 'UNCATEGORIZED') {
											subSubject.show();
											subSubject.clearValue();
											subSubject.store.filter('parentItem', combo.getValue());
											subSubject.setValue(combo.getValue());
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
						},{
							xtype:'combo'
							,id:'combo-category-'+modName
							,fieldLabel:'Category'
							,hiddenName:'category'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.category
							,displayField:'category'
							,valueField:'id'
							,plugins:new form.categoryCombo()
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('CurrikiCode.AssetClass_category_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
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
							,emptyText:_('CurrikiCode.AssetClass_educational_level_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						},{
							xtype:'combo'
							,id:'combo-language-'+modName
							,fieldLabel:'Language'
							,hiddenName:'language'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.language
							,displayField:'language'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('CurrikiCode.AssetClass_language_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						},{
							xtype:'combo'
							,id:'combo-review-'+modName
							,fieldLabel:'Review'
							,hiddenName:'review'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.review
							,displayField:'review'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('search.resource.review.selector.UNSPECIFIED')
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
							,id:'combo-ictprfx-'+modName
							,fieldLabel:'Instructional Type'
							,hiddenName:'ictprfx'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.ict
							,displayField:'ict'
							,valueField:'id'
							,plugins:new form.ictCombo()
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('CurrikiCode.AssetClass_instructional_component_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
							,listeners:{
								select:{
									fn:function(combo, value){
										var subICT = Ext.getCmp('combo-subICT-'+modName);
										if (combo.getValue() === '') {
											subICT.clearValue();
											subICT.hide();
										} else {
											subICT.clearValue();
											subICT.store.filter('parentICT', combo.getValue());
											var p = subICT.store.getById(combo.getValue()+'*');
											if (Ext.isEmpty(p)) {
												subICT.setValue(combo.getValue());
												subICT.hide();
											} else {
												subICT.setValue(combo.getValue()+'*');
												subICT.show();
											}
										}
									}
								}
							}
						},{
							xtype:'combo'
							,fieldLabel:'Sub ICT'
							,hiddenName:'ict'
							,width:comboWidth
							,listWidth:comboListWidth
							,id:'combo-subICT-'+modName
							,mode:'local'
							,store:data.filter.store.subict
							,displayField:'ict'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:'Select a Sub ICT...'
							,selectOnFocus:true
							,forceSelection:true
							,lastQuery:''
							,hidden:true
							,hideMode:'visibility'
						},{
							xtype:'combo'
							,id:'combo-special-'+modName
							,fieldLabel:'Special Filters'
							,hiddenName:'special'
							,width:comboWidth
							,listWidth:comboListWidth
							,mode:'local'
							,store:data.filter.store.special
							,displayField:'special'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:_('search.resource.special.selector.UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
						}]
					}]
				}]
			}
		]
	};

	form.rowExpander = new Ext.grid.RowExpander({


		tpl: new Ext.XTemplate(
			_('search.resource.resource.expanded.title'),
			'<ul>',
			'<tpl for="parents">',
				'<li class="resource-{assetType} category-{category} subcategory-{category}_{subcategory}">',
					'<a target="{[this.getLinkTarget(values)]}" href="{[this.getParentURL(values)]}" ext:qtip="{[this.getQtip(values)]}">',
						'{title}',
					'</a>',
				'</li>',
			'</tpl>',
			'</ul>', {
				getParentURL: function(values){
					var page = values.page||false;
					if (page) {
						if(Curriki.module.search.util.isInEmbeddedMode()){
							return Curriki.module.search.resourceDisplay + '?resourceurl=/xwiki/bin/view/'+ escape(page.replace(/\./, '/')+'?'+Curriki.module.search.embedViewMode);
						}else{
							return '/xwiki/bin/view/'+page.replace(/\./, '/');
						}
					} else {
						return '';
					}
				},
				getQtip: function(values){
					var f = Curriki.module.search.data[modName].filter;

					var desc = Ext.util.Format.stripTags(values.description||'');
					desc = Ext.util.Format.ellipsis(desc, 256);
					desc = Ext.util.Format.htmlEncode(desc);

					var fw = Curriki.data.fw_item.getRolloverDisplay(values.fwItems||[]);
					var lvl = Curriki.data.el.getRolloverDisplay(values.levels||[]);
			
					return String.format("{1}<br />{0}<br /><br />{3}<br />{2}<br />{5}<br />{4}"
						,desc,_('global.title.popup.description')
						,fw,_('global.title.popup.subject')
						,lvl,_('global.title.popup.educationlevel')
					);
				},

				getLinkTarget: function(values){
					if(Curriki.module.search.util.isInEmbeddedMode()){
							return '_blank';
						}else{
							return '_self';
						}
				}
			}
		)
	});

	form.rowExpander.renderer = function(v, p, record){
		var cls;
        if (record.data.parents && record.data.parents.size() > 0) {
			p.cellAttr = 'rowspan="2"';
			cls = 'x-grid3-row-expander';
			return String.format('<img class="{0}" src="{1}" ext:qtip="{2}" />', cls, Ext.BLANK_IMAGE_URL, _('search.resource.icon.plus.rollover'));
		} else {
			cls = 'x-grid3-row-expander-empty';
			return String.format('<img class="{0}" src="{1}" />', cls, Ext.BLANK_IMAGE_URL);
		}
	};

	form.rowExpander.on('expand', function(expander, record, body, idx){
		var row = expander.grid.view.getRow(idx);
		var iconCol = Ext.DomQuery.selectNode('img[class*=x-grid3-row-expander]', row); // TODO: here
		Ext.fly(iconCol).set({'ext:qtip':_('search.resource.icon.minus.rollover')});
		
		if(Curriki.module.search.util.isInEmbeddedMode()){
			Curriki.module.search.util.sendResizeMessageToEmbeddingWindow();
		}
	});

	form.rowExpander.on('collapse', function(expander, record, body, idx){
		var row = expander.grid.view.getRow(idx);
		var iconCol = Ext.DomQuery.selectNode('img[class*=x-grid3-row-expander]', row); // TODO: here
		Ext.fly(iconCol).set({'ext:qtip':_('search.resource.icon.plus.rollover')});

		if(Curriki.module.search.util.isInEmbeddedMode()){
			Curriki.module.search.util.sendResizeMessageToEmbeddingWindow();
		}
	});

	form.columnModel = new Ext.grid.ColumnModel([
		Ext.apply(
			form.rowExpander
			,{
                id:'score'
                ,tooltip:_('search.resource.column.header.score.tooltip')
                ,header: ' '
                ,dataIndex: 'score'
                ,width: 30
                ,sortable:true
			}
		)
        ,{
			id: 'title'
			,header: _('search.resource.column.header.title')
			,width: 164
			,dataIndex: 'title'
			,sortable:true
			,hideable:false
			,renderer: data.renderer.title
//			,tooltip:_('search.resource.column.header.title')
		},{
			id: 'ict'
			,width: 108
			,header: _('search.resource.column.header.ict')
			,dataIndex:'ictText'
			,sortable:true
			,renderer: data.renderer.ict
//			,tooltip: _('search.resource.column.header.ict')
		},{
			id: 'contributor'
			,width: 110
			,header: _('search.resource.column.header.contributor')
			,dataIndex:'contributor'
			,sortable:true
			,renderer: data.renderer.contributor
//			,tooltip: _('search.resource.column.header.contributor')
		},{
			id: 'rating'
			,width: 88
			,header: _('search.resource.column.header.rating')
			,dataIndex:'rating'
			,sortable:true
			,renderer: data.renderer.rating
//			,tooltip: _('search.resource.column.header.rating')
		},{
			id: 'memberRating'
			,width: 105
			,header: _('search.resource.column.header.member.rating')
			,dataIndex:'memberRating'
			,sortable:true
			,renderer: data.renderer.memberRating
//			,tooltip: _('search.resource.column.header.member.rating')
		},{
			id: 'updated'
			,width: 80
			,header: _('search.resource.column.header.updated')
			,dataIndex:'updated'
			,hidden:true
			,sortable:true
			,renderer: data.renderer.updated
//			,tooltip: _('search.resource.column.header.updated')
	}]);

	form.resultsPanel = {
		xtype:'grid'
		,id:'search-results-'+modName
		//,title:'Results'
		,border:false
		,autoHeight:true
		,width:Search.settings.gridWidth
		,autoExpandColumn:'title'
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
    console.log("Finished initting form for " + modName + ".");
    console.log("Now get: Curriki.module.search.form['otherResource']: " + Curriki.module.search.form['otherResource'])
};
}
    Ext.onReady(function(){
        for(var i=0; i<2; i++) {
            var modName = modNames[i];
            Search.form[modName].init(modName);
        }
    });

})();




// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'group';

Ext.ns('Curriki.module.search.data.'+modName);

var data = Curriki.module.search.data.group;

data.init = function(){
	console.log('data.'+modName+': init');


	// Set up filters
	data.filter = {};
	var f = data.filter; // Alias

	f.data = {};

	f.data.subject =  {
		mapping: Curriki.data.fw_item.fwMap['FW_masterFramework.WebHome']
		,list: []
		,data: [
			['', _('XWiki.CurrikiSpaceClass_topic_FW_masterFramework.WebHome.UNSPECIFIED')]
		]
	};
	f.data.subject.mapping.each(function(value){
		f.data.subject.list.push(value.id);
	});
	f.data.subject.list.each(function(value){
		f.data.subject.data.push([
			value
			,_('XWiki.CurrikiSpaceClass_topic_'+value)
		]);
	});

	f.data.subsubject =  {
		mapping: Curriki.data.fw_item.fwMap
		,data: [
		]
	};
	f.data.subject.mapping.each(function(parentItem){
		f.data.subsubject.data.push([
			parentItem.id
			,_('XWiki.CurrikiSpaceClass_topic_'+parentItem.id+'.UNSPECIFIED')
			,parentItem.id
		]);
		f.data.subsubject.mapping[parentItem.id].each(function(subject){
			f.data.subsubject.data.push([
				subject.id
				,_('XWiki.CurrikiSpaceClass_topic_'+subject.id)
				,parentItem.id
			]);
		});
	});

	f.data.level =  {
		list: Curriki.data.el.list
		,data: [
			['', _('XWiki.CurrikiSpaceClass_educationLevel_UNSPECIFIED')]
		]
	};
	f.data.level.list.each(function(value){
		f.data.level.data.push([
			value
			,_('XWiki.CurrikiSpaceClass_educationLevel_'+value)
		]);
	});

	f.data.policy =  {
		list: ['open', 'closed']
		,data: [
			['', _('search.XWiki.SpaceClass_policy_UNSPECIFIED')]
		]
	};
	f.data.policy.list.each(function(value){
		f.data.policy.data.push([
			value
			,_('search.XWiki.SpaceClass_policy_'+value)
		]);
	});

	f.data.language =  {
		list: Curriki.data.language.list
		,data: [
			['', _('XWiki.CurrikiSpaceClass_language_UNSPECIFIED')]
		]
	};
	f.data.language.list.each(function(value){
		f.data.language.data.push([
			value
			,_('XWiki.CurrikiSpaceClass_language_'+value)
		]);
	});

	f.store = {
		subject: new Ext.data.SimpleStore({
			fields: ['id', 'subject']
			,data: f.data.subject.data
			,id: 0
		})

		,subsubject: new Ext.data.SimpleStore({
			fields: ['id', 'subject', 'parentItem']
			,data: f.data.subsubject.data
			,id: 0
		})

		,level: new Ext.data.SimpleStore({
			fields: ['id', 'level']
			,data: f.data.level.data
			,id: 0
		})

		,policy: new Ext.data.SimpleStore({
			fields: ['id', 'policy']
			,data: f.data.policy.data
			,id: 0
		})

		,language: new Ext.data.SimpleStore({
			fields: ['id', 'language']
			,data: f.data.language.data
			,id: 0
		})
	};



	// Set up data store
	data.store = {};

	data.store.record = new Ext.data.Record.create([
		{ name: 'title' }
		,{ name: 'url' }
		,{ name: 'policy' }
		,{ name: 'description' }
		,{ name: 'updated' }
	]);

	data.store.results = new Ext.data.Store({
		storeId: 'search-store-'+modName
		,proxy: new Ext.data.HttpProxy({
            url: document.location.pathname.endsWith("Old") ?
                '/xwiki/bin/view/Search/Groups' : '/currikiExtjs'
			,method:'GET'
		})
		,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

		,reader: new Ext.data.JsonReader({
			root: 'rows'
			,totalProperty: 'resultCount'
			,id: 'page'
		}, data.store.record)

		// turn on remote sorting
		,remoteSort: true
	});
    if(Curriki.isISO8601DateParsing() ) data.store.results.baseParams.dateFormat="ISO8601";
	data.store.results.setDefaultSort('title', 'asc');



	// Set up renderers
	data.renderer = {
		title: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('<a href="{0}">{1}</a>', record.data.url, value);
		}

		,policy: function(value, metadata, record, rowIndex, colIndex, store){
			if (value !== ''){
				metadata.css = 'policy-'+value;
			}
			var policy = _('search.group.icon.'+value);
			return String.format('<span ext:qtip="{1}">{0}</span>', policy, _('search.group.icon.'+value+'.rollover'));
		}

		,description: function(value, metadata, record, rowIndex, colIndex, store){
			var desc = Ext.util.Format.htmlDecode(value);
			desc = Ext.util.Format.stripScripts(value);
			desc = Ext.util.Format.stripTags(desc);
			desc = Ext.util.Format.ellipsis(desc, 128);
			desc = Ext.util.Format.htmlEncode(desc);
			desc = Ext.util.Format.trim(desc);
			return String.format('{0}', desc);
		}

		,updated: function(value, metadata, record, rowIndex, colIndex, store){
            if(typeof("value")!="string") return "";
            var dt = Ext.util.Format.date(value, 'M-d-Y');
            if(typeof(dt)!="string") return "";
			return String.format('{0}', dt);
		}
	};
};

Ext.onReady(function(){
	data.init();
});
})();
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
	form.init();
});


// TODO:  Register this tab somehow with the main form

})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'member';

Ext.ns('Curriki.module.search.data.'+modName);

var data = Curriki.module.search.data.member;

data.init = function(){
	console.log('data.'+modName+': init');

	// Set up filters
	data.filter = {};
	var f = data.filter; // Alias

	f.data = {};

	f.data.subject =  {
		mapping: Curriki.data.fw_item.fwMap['FW_masterFramework.WebHome']
		,list: []
		,data: [
			['', _('XWiki.XWikiUsers_topics_FW_masterFramework.WebHome.UNSPECIFIED')]
		]
	};
	f.data.subject.mapping.each(function(value){
		f.data.subject.list.push(value.id);
	});
	f.data.subject.list.each(function(value){
		f.data.subject.data.push([
			value
			,_('XWiki.XWikiUsers_topics_'+value)
		]);
	});

	f.data.subsubject =  {
		mapping: Curriki.data.fw_item.fwMap
		,data: [
		]
	};
	f.data.subject.mapping.each(function(parentItem){
		f.data.subsubject.data.push([
			parentItem.id
			,_('XWiki.XWikiUsers_topics_'+parentItem.id+'.UNSPECIFIED')
			,parentItem.id
		]);
		f.data.subsubject.mapping[parentItem.id].each(function(subject){
			f.data.subsubject.data.push([
				subject.id
				,_('XWiki.XWikiUsers_topics_'+subject.id)
				,parentItem.id
			]);
		});
	});

	f.data.country =  {
		list: 'AD|AE|AF|AG|AI|AL|AM|AN|AO|AQ|AR|AS|AT|AU|AW|AX|AZ|BA|BB|BD|BE|BF|BG|BH|BI|BJ|BM|BN|BO|BR|BS|BT|BV|BW|BY|BZ|CA|CC|CD|CF|CG|CH|CI|CK|CL|CM|CN|CO|CR|CU|CV|CX|CY|CZ|DE|DJ|DK|DM|DO|DZ|EC|EE|EG|EH|ER|ES|ET|FI|FJ|FK|FM|FO|FR|GA|GB|GD|GE|GF|GG|GH|GI|GL|GM|GN|GP|GQ|GR|GS|GT|GU|GW|GY|HK|HM|HN|HR|HT|HU|ID|IE|IL|IM|IN|IO|IQ|IR|IS|IT|JE|JM|JO|JP|KE|KG|KH|KI|KM|KN|KP|KR|KW|KY|KZ|LA|LB|LC|LI|LK|LR|LS|LT|LU|LV|LY|MA|MC|MD|ME|MG|MH|MK|ML|MM|MN|MO|MP|MQ|MR|MS|MT|MU|MV|MW|MX|MY|MZ|NA|NC|NE|NF|NG|NI|NL|NO|NP|NR|NU|NZ|OM|PA|PE|PF|PG|PH|PK|PL|PM|PN|PR|PS|PT|PW|PY|QA|RE|RO|RS|RU|RW|SA|SB|SC|SD|SE|SG|SH|SI|SJ|SK|SL|SM|SN|SO|SR|ST|SV|SY|SZ|TC|TD|TF|TG|TH|TJ|TK|TL|TM|TN|TO|TR|TT|TV|TW|TZ|UA|UG|UM|US|UY|UZ|VA|VC|VE|VG|VI|VN|VU|WF|WS|YE|YT|ZA|ZM|ZW'.split('|')
		,data: [
			['', _('XWiki.XWikiUsers_country_UNSPECIFIED')]
		]
	};
	f.data.country.list.each(function(value){
		f.data.country.data.push([
			value
			,_('XWiki.XWikiUsers_country_'+value)
		]);
	});

	f.data.member_type =  {
		list: ['student', 'teacher', 'parent','professional','administration','nonprofit','nonprofit_education','corporation']
		,data: [
			['', _('XWiki.XWikiUsers_member_type_UNSPECIFIED')]
		]
	};
	f.data.member_type.list.each(function(value){
		f.data.member_type.data.push([
			value
			,_('XWiki.XWikiUsers_member_type_'+value)
		]);
	});

	f.store = {
		subject: new Ext.data.SimpleStore({
			fields: ['id', 'subject']
			,data: f.data.subject.data
			,id: 0
		})

		,subsubject: new Ext.data.SimpleStore({
			fields: ['id', 'subject', 'parentItem']
			,data: f.data.subsubject.data
			,id: 0
		})

		,member_type: new Ext.data.SimpleStore({
			fields: ['id', 'member_type']
			,data: f.data.member_type.data
			,id: 0
		})

		,country: new Ext.data.SimpleStore({
			fields: ['id', 'country']
			,data: f.data.country.data
			,id: 0
		})
	};



	// Set up data store
	data.store = {};

	data.store.record = new Ext.data.Record.create([
		{ name: 'name1' }
		,{ name: 'name2' }
		,{ name: 'url' }
		,{ name: 'bio' }
		,{ name: 'picture' }
		,{ name: 'contributions' }
	]);

	data.store.results = new Ext.data.Store({
		storeId: 'search-store-'+modName
		,proxy: new Ext.data.HttpProxy({
            url: document.location.pathname.endsWith("Old") ?
                '/xwiki/bin/view/Search/Members' : '/currikiExtjs'

            ,method:'GET'
		})
		,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

		,reader: new Ext.data.JsonReader({
			root: 'rows'
			,totalProperty: 'resultCount'
			,id: 'page'
		}, data.store.record)

		// turn on remote sorting
		,remoteSort: true
	});
    if(Curriki.isISO8601DateParsing() ) data.store.results.baseParams.dateFormat="ISO8601";
	data.store.results.setDefaultSort('name1', 'asc');



	// Set up renderers
	data.renderer = {
		name1: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('<a href="{1}">{0}</a>', value, record.data.url);
		}

		,name2: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('<a href="{1}">{0}</a>', value, record.data.url);
		}

		,picture: function(value, metadata, record, rowIndex, colIndex, store){
			//TODO: Remove specialized style
			return String.format('<a href="{2}"><img src="{0}" alt="{1}" class="member-picture" style="width:88px" /></a>', value, _('search.member.column.picture.alt.text'), record.data.url);
		}

		,contributions: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('{0}', value);
		}

		,bio: function(value, metadata, record, rowIndex, colIndex, store){
			var desc = Ext.util.Format.htmlDecode(value);
			desc = Ext.util.Format.stripScripts(value);
			desc = Ext.util.Format.stripTags(desc);
			desc = Ext.util.Format.ellipsis(desc, 128);
			desc = Ext.util.Format.htmlEncode(desc);
			desc = Ext.util.Format.trim(desc);
			return String.format('{0}', desc);
		}
	};
};

Ext.onReady(function(){
	data.init();
});
})();
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
	form.init();
});


// TODO:  Register this tab somehow with the main form

})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'blog';

Ext.ns('Curriki.module.search.data.'+modName);

var data = Curriki.module.search.data.blog;

data.init = function(){
	console.log('data.'+modName+': init');

	// No filters for blog search

	// Set up data store
	data.store = {};

	data.store.record = new Ext.data.Record.create([
		{ name: 'name' }
		,{ name: 'title' }
		,{ name: 'text' }
		,{ name: 'comments' }
		,{ name: 'updated' }
		,{ name: 'memberUrl' }
		,{ name: 'blogUrl' }
	]);

	data.store.results = new Ext.data.Store({
		storeId: 'search-store-'+modName
		,proxy: new Ext.data.HttpProxy({
            url: document.location.pathname.endsWith("Old") ?
                '/xwiki/bin/view/Search/Blogs' : '/currikiExtjs'
            ,method:'GET'
		})
		,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

		,reader: new Ext.data.JsonReader({
			root: 'rows'
			,totalProperty: 'resultCount'
			,id: 'page'
		}, data.store.record)

		// turn on remote sorting
		,remoteSort: true
	});
    if(Curriki.isISO8601DateParsing() ) data.store.results.baseParams.dateFormat="ISO8601";
	data.store.results.setDefaultSort('updated', 'desc');



	// Set up renderers
	data.renderer = {
		name: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('<a href="{1}">{0}</a>', value, record.data.memberUrl);
		}

		,text: function(value, metadata, record, rowIndex, colIndex, store){
			var desc = Ext.util.Format.htmlDecode(value); // Reverse conversion
			desc = Ext.util.Format.stripScripts(value);
			desc = Ext.util.Format.stripTags(desc);
			desc = Ext.util.Format.trim(desc);
			desc = Ext.util.Format.ellipsis(desc, 128);
			//desc = Ext.util.Format.htmlEncode(desc);
			return String.format('<a href="{2}" class="search-blog-title">{1}</a><br /><br />{0}', desc, record.data.title, record.data.blogUrl);
		}

		,comments: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('{0}', value);
		}

		,updated: function(value, metadata, record, rowIndex, colIndex, store){
            if(typeof("value")!="string") return "";
            var dt = Ext.util.Format.date(value, 'M-d-Y');
            if(typeof(dt)!="string") return "";
			return String.format('{0}', dt);
		}
	};
};

Ext.onReady(function(){
	data.init();
});
})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'blog';

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
			,header:_('search.blog.column.header.name')
			,width: 160
			,dataIndex: 'name'
			,sortable:true
			,renderer: data.renderer.name
//			,tooltip:_('search.blog.column.header.name')
		},{
			id: 'text'
			,header: _('search.blog.column.header.text')
			,width: 260
			,dataIndex: 'text'
			,sortable:false
			,renderer: data.renderer.text
//			,tooltip:_('search.blog.column.header.text')
		},{
			id: 'comments'
			,header: _('search.blog.column.header.comments')
			,width: 80
			,dataIndex: 'comments'
			,sortable:false
			,renderer: data.renderer.comments
//			,tooltip:_('search.blog.column.header.comments')
		},{
			id: 'updated'
			,width: 96
			,header: _('search.blog.column.header.updated')
			,dataIndex:'updated'
			,sortable:true
			,renderer: data.renderer.updated
//			,tooltip: _('search.blog.column.header.updated')
	}]);

	form.resultsPanel = {
		xtype:'grid'
		,id:'search-results-'+modName
		//,title:'Results'
		,border:false
		,autoHeight:true
		,width:Search.settings.gridWidth
		,autoExpandColumn:'text'
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
	form.init();
});


// TODO:  Register this tab somehow with the main form

})();
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'curriki';

Ext.ns('Curriki.module.search.data.'+modName);

var data = Curriki.module.search.data.curriki;

data.init = function(){
	console.log('data.'+modName+': init');

	// No filters for curriki search

	// Set up data store
	data.store = {};

	data.store.record = new Ext.data.Record.create([
		{ name: 'name' }
//		,{ name: 'text' }
		,{ name: 'updated' }
		,{ name: 'url' }
        ,{ name: 'score'}
	]);

	data.store.results = new Ext.data.Store({
		storeId: 'search-store-'+modName
		,proxy: new Ext.data.HttpProxy({
            url: document.location.pathname.endsWith("Old") ?
                '/xwiki/bin/view/Search/Curriki' : '/currikiExtjs'
            ,method:'GET'
		})
		,baseParams: { xpage: "plain", '_dc':(new Date().getTime()) }

		,reader: new Ext.data.JsonReader({
			root: 'rows'
			,totalProperty: 'resultCount'
//			,id: 'page'
		}, data.store.record)

		// turn on remote sorting
		,remoteSort: true
	});
    if(Curriki.userinfo.userGroups) data.store.results.baseParams.groupsId= Curriki.userinfo.userGroups;
    if(Curriki.userinfo.userName) data.store.results.baseParams.userId = Curriki.userinfo.userName;
    if(Curriki.userinfo.isAdmin) data.store.results.baseParams.isAdmin = true;
    if(Curriki.isISO8601DateParsing() ) data.store.results.baseParams.dateFormat="ISO8601";
    data.store.results.setDefaultSort('score', 'desc');



	// Set up renderers
	data.renderer = {
		name: function(value, metadata, record, rowIndex, colIndex, store){
			return String.format('<a href="{1}">{0}</a>', value, record.data.url);
		}

/*
		,text: function(value, metadata, record, rowIndex, colIndex, store){
			var desc = Ext.util.Format.stripScripts(value);
			desc = Ext.util.Format.stripTags(desc);
			desc = Ext.util.Format.ellipsis(desc, 128);
			desc = Ext.util.Format.htmlEncode(desc);
			return String.format('{0}', desc);
		}
*/

		,updated: function(value, metadata, record, rowIndex, colIndex, store){
            if(typeof("value")!="string") return "";
            var dt = Ext.util.Format.date(value, 'M-d-Y');
            if(typeof(dt)!="string") return "";
			return String.format('{0}', dt);
		}, score: function(value, metadata, record, rowIndex, colIndex, store){
            if(typeof(value)!="number") value=0;
            return value;
        }

    };
};

Ext.onReady(function(){
	data.init();
});
})();
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
	form.init();
});


// TODO:  Register this tab somehow with the main form

})();
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
