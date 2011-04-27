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
		list: ['parent', 'teacher', 'professional', 'student']
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
			url: '/xwiki/bin/view/Search/Members'
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
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
	  data.init();
	});
});
})();
