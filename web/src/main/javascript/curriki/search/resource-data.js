// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'resource';

Ext.ns('Curriki.module.search.data.'+modName);

var data = Curriki.module.search.data.resource;

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
			['', _('CurrikiCode.AssetClass_instructional_component_UNSPECIFIED')]
		]
	};
	f.data.ict.fullList.each(function(value){
		var name = value.replace(/_.*/, '');
		f.data.ict.parentList[name] = name;
	});
	Object.keys(f.data.ict.parentList).each(function(value){
		f.data.ict.data.push([
			value
			,_('CurrikiCode.AssetClass_instructional_component_'+value)
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
				]);
				f.data.subict.parents[parentICT] = parentICT;
			}
			f.data.subict.data.push([
				value
				,_('CurrikiCode.AssetClass_instructional_component_'+value)
				,parentICT
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
		list: [
			'partners', 'highest_rated', 'members.highest_rated'
		]
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
		list: [
			'contributions', 'collections', 'updated'
		]
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
			fields: ['id', 'ict']
			,data: f.data.ict.data
			,id: 0
		})

		,subict: new Ext.data.SimpleStore({
			fields: ['id', 'ict', 'parentICT']
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
		,{ name: 'updated' }
	]);

	data.store.results = new Ext.data.Store({
		storeId: 'search-store-'+modName
		,proxy: new Ext.data.HttpProxy({
			url: '/xwiki/bin/view/Search/Resources'
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
	data.store.results.setDefaultSort('title', 'asc');



	// Set up renderers
	data.renderer = {
		title: function(value, metadata, record, rowIndex, colIndex, store){
			// Title
			var page = record.id.replace(/\./, '/');

			var desc = Ext.util.Format.stripTags(record.data.description);
			desc = Ext.util.Format.ellipsis(desc, 256);
			desc = Ext.util.Format.htmlEncode(desc);

			var fw = Curriki.data.fw_item.getRolloverDisplay(record.data.fwItems||[]);
			var lvl = Curriki.data.el.getRolloverDisplay(record.data.levels||[]);

			desc = String.format("{1}<br />{0}<br /><br />{3}<br />{2}<br />{5}<br />{4}"
				,desc,_('global.title.popup.description')
				,fw,_('global.title.popup.subject')
				,lvl,_('global.title.popup.educationlevel')
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

//			return String.format('<img class="x-tree-node-icon assettype-icon" style="width:16px;height:17px;background-repeat:no-repeat;" src="{0}" alt="{1}" ext:qtip="{1}" />', Ext.BLANK_IMAGE_URL, rollover);
			return String.format('<img class="x-tree-node-icon assettype-icon" src="{3}" ext:qtip="{4}" /><a href="/xwiki/bin/view/{0}" class="asset-title" ext:qtip="{2}">{1}</a>', page, Ext.util.Format.ellipsis(value, 80), desc, Ext.BLANK_IMAGE_URL, rollover);
		}

		,ict: function(value, metadata, record, rowIndex, colIndex, store){
			var css;
			var dotIct;
			var ict = record.data.ict;
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
			return String.format('<a href="/xwiki/bin/view/{0}">{1}</a>', page, record.data.contributorName);
		}

		,rating: function(value, metadata, record, rowIndex, colIndex, store){
			if (value != "") {
				var page = record.id.replace(/\./, '/');

				metadata.css = String.format('crs-{0}', value); // Added to <td>
				//metadata.attr = String.format('title="{0}"', _('curriki.crs.rating'+value)); // Added to <div> around the returned HTML
				return String.format('<a href="/xwiki/bin/view/{3}?viewer=comments"><img class="crs-icon" alt="" src="{2}" /><span class="crs-text">{1}</span></a>', value, _('search.resource.review.'+value), Ext.BLANK_IMAGE_URL, page);
			} else {
				return String.format('');
			}
		}

		,memberRating: function(value, metadata, record, rowIndex, colIndex, store){
			if (value != "") {
				var page = record.id.replace(/\./, '/');
				var ratingCount = record.data.ratingCount;

				metadata.css = String.format('rating-{0}', value);
				return String.format('<a href="/xwiki/bin/view/{2}?viewer=comments"><img class="rating-icon" src="{4}" ext:qtip="{3}" /></a><a href="/xwiki/bin/view/{2}?viewer=comments" ext:qtip="{3}"> ({1})</a>', value, ratingCount, page, _('search.resource.rating.'+value), Ext.BLANK_IMAGE_URL);
			} else {
				return String.format('');
			}
		}

		,updated: function(value, metadata, record, rowIndex, colIndex, store){
			var dt = Ext.util.Format.date(value, 'M-d-Y');
			return String.format('{0}', dt);
		}
	};
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
	  data.init();
	});
});
})();
