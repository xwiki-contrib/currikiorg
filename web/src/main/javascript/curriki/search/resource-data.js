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
