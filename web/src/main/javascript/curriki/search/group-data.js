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
			url: '/xwiki/bin/view/Search/Groups'
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
