// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

(function(){
var modName = 'resource';

Ext.ns('Curriki.module.search.form.'+modName);

var Search = Curriki.module.search;

var form = Search.form[modName];
var data = Search.data[modName];

form.init = function(){
	console.log('form.'+modName+': init');

	var comboWidth = 160;
	var comboListWidth = 250;

	// Plugin to add icons to ICT combo box
	form.ictCombo = function(config) {
		Ext.apply(this, config);
	};
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
						tag: 'div', style:'position:absolute'
					});
				}), // end of function onRender

				setIconCls:function() {
					var rec = this.store.query(this.valueField, this.getValue()).itemAt(0);
					if(rec) {
						this.icon.className = 'ict-icon-combo-icon ict-'+rec.get(this.valueField)+' ict-icon';
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
				,title:_('search.advanced.search.button')
				,id:'search-advanced-'+modName
				,autoHeight:true
				,collapsible:true
				,collapsed:true
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
							,emptyText:_('XWiki.AssetClass_fw_items_FW_masterFramework.UNSPECIFIED')
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
							,emptyText:_('XWiki.AssetClass_educational_level2_UNSPECIFIED')
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
							,emptyText:_('XWiki.AssetClass_language_UNSPECIFIED')
							,selectOnFocus:true
							,forceSelection:true
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
							,emptyText:_('XWiki.AssetClass_instructional_component2_UNSPECIFIED')
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
						}]
					}]
				}]
			}
		]
	}

	form.rowExpander = new Ext.grid.RowExpander({
		tpl: new Ext.Template(
			'{info}'
		)
	});

	form.rowExpander.renderer = function(v, p, record){
		var cls;
		if (record.data.info !== '') {
			p.cellAttr = 'rowspan="2"';
			cls = 'x-grid3-row-expander';
//			return '<div class="x-grid3-row-expander">&#160;</div>';
			return String.format('<img class="{0}" src="{1}" height="18px" width="18px" ext:qtip="{2}" />', cls, Ext.BLANK_IMAGE_URL, _('search.resource.icon.plus.rollover'));
		} else {
			cls = 'x-grid3-row-expander-empty';
//			return '<div class="x-grid3-row-expander-empty">&#160;</div>';
			return String.format('<img class="{0}" src="{1}" height="18px" width="18px" />', cls, Ext.BLANK_IMAGE_URL);
		}
	};

	form.rowExpander.on('expand', function(expander, record, body, idx){
		var row = expander.grid.view.getRow(idx);
		var iconCol = Ext.DomQuery.selectNode('img[class=x-grid3-row-expander]', row);
		Ext.fly(iconCol).set({'ext:qtip':_('search.resource.icon.minus.rollover')});
	});

	form.rowExpander.on('collapse', function(expander, record, body, idx){
		var row = expander.grid.view.getRow(idx);
		var iconCol = Ext.DomQuery.selectNode('img[class=x-grid3-row-expander]', row);
		Ext.fly(iconCol).set({'ext:qtip':_('search.resource.icon.plus.rollover')});
	});

	form.columnModel = new Ext.grid.ColumnModel([
		Ext.apply(
			form.rowExpander
			,{
				tooltip:_('search.resource.icon.plus.title')
			}
		)
/*
		,{
			id: 'assetType'
			,header: ''
			,width: 17
			,dataIndex: 'assetType'
			,sortable:false
			,resizable:false
			,hideable:false
			,menuDisabled:true
			,renderer: data.renderer.assetType
			,tooltip:_('search.resource.column.header.assetType')
		}*/
		,{
			id: 'title'
			,header: _('search.resource.column.header.title')
			,width: 179
			,dataIndex: 'title'
			,sortable:true
			,hideable:false
			,renderer: data.renderer.title
			,tooltip:_('search.resource.column.header.title')
		},{
			id: 'ict'
			,width: 169
			,header: _('search.resource.column.header.ict')
			,dataIndex:'ictText'
			,sortable:true
			,renderer: data.renderer.ict
			,tooltip: _('search.resource.column.header.ict')
		},{
			id: 'contributor'
			,width: 147
			,header: _('search.resource.column.header.contributor')
			,dataIndex:'contributor'
			,sortable:true
			,renderer: data.renderer.contributor
			,tooltip: _('search.resource.column.header.contributor')
		},{
			id: 'rating'
			,width: 74
			,header: _('search.resource.column.header.rating')
			,dataIndex:'rating'
			,sortable:true
			,renderer: data.renderer.rating
			,tooltip: _('search.resource.column.header.rating')
		},{
			id: 'updated'
			,width: 68
			,header: _('search.resource.column.header.updated')
			,dataIndex:'updated'
			,hidden:true
			,sortable:false
			,renderer: data.renderer.updated
			,tooltip: _('search.resource.column.header.updated')
	}]);

	form.resultsPanel = {
		xtype:'grid'
		,id:'search-results-'+modName
		//,title:'Results'
		,border:false
		,autoHeight:true
		,width:Search.settings.gridWidth
		,autoExpandColumn:'title'
		,frame:false
		,stripeRows:true
		,viewConfig: {
			forceFit:true
			,enableRowBody:true
			,showPreview:true
		}
		,store: data.store.results
		,sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn})
		,cm: form.columnModel
		,loadMask: false
		,plugins: form.rowExpander
		,bbar: new Ext.PagingToolbar({
			id: 'search-pager-'+modName
			,plugins:new Ext.ux.Andrie.pPageSize({variations: [10, 25, 50]})
			,pageSize: 25
			,store: data.store.results
			,displayInfo: true
			,displayMsg: _('search.pagination.count.displayed')
			,emptyMsg: _('search.find.no.results')
			,afterPageText: _('search.pagination.count.total')
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
