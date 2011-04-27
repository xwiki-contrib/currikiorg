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
	]);

	data.store.results = new Ext.data.Store({
		storeId: 'search-store-'+modName
		,proxy: new Ext.data.HttpProxy({
			url: '/xwiki/bin/view/Search/Curriki'
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
	data.store.results.setDefaultSort('name', 'asc');



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
