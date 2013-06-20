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
