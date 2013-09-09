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




