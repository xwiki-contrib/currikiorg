// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

Ext.ns('Curriki.module.addpath');
Curriki.module.addpath.init = function(){
	if (Ext.isEmpty(Curriki.module.addpath.initialized)) {
		// Local alias
		var AddPath = Curriki.module.addpath;

		AddPath.ie_size_shift = 10;

		AddPath.EnableNext = function() {
			Ext.getCmp('nextbutton').enable();
		}
		AddPath.DisableNext = function() {
			Ext.getCmp('nextbutton').disable();
		}

		AddPath.RadioSelect = function(e, checked){
			var entry_box;
			var change = false;

			entry_box = Ext.getCmp(e.value+'-entry-box');
			if (entry_box) {
				// Is a component
				if (entry_box.isVisible() != checked) {
					change = true;
					entry_box.setVisible(checked);
					entry_box.setDisabled(!checked);
				}
			} else {
				// Is just a HTML element
				entry_box = Ext.get(e.value+'-entry-box');
				if (entry_box && entry_box.isVisible() != checked) {
					entry_box.setVisibilityMode(Ext.Element.DISPLAY).setVisible(checked);
				}
			}

			entry_box = Ext.getCmp(e.value+'-entry-value');
			if (change && entry_box) {
				// Has a value component
				entry_box.setDisabled(!checked);
			}

			entry_box = Ext.getCmp(e.value+'-container-cmp');
			if (change && entry_box) {
				if (entry_box.isVisible() != checked) {
					entry_box.setVisible(checked);
					entry_box.setDisabled(!checked);
				}
			}

			var dlg = Ext.getCmp(AddPath.AddSourceDialogueId);
			if (dlg) {
				dlg.doLayout();
				dlg.syncShadow();
			}
		}


		AddPath.AddSourceDialogueId = 'resource-source-dialogue';
		AddPath.AddSource = Ext.extend(Curriki.ui.dialog.Actions, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.contributemenu.title_addto_'+(this.toFolder?'composite':'site'))
					,cls:'addpath addpath-source resource resource-add'
					,id:AddPath.AddSourceDialogueId
					,items:[{
						 xtype:'panel'
						,cls:'guidingquestion-container'
						,items:[{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:this.toFolder
									?_('add.contributemenu.guidingquestion_addto_composite', this.folderName)
									:_('add.contributemenu.guidingquestion_addto_site')
								,cls:'guidingquestion'
							}
						}]
					},{
						 xtype:'form'
						,id:'addDialoguePanel'
						,formId:'addDialogueForm'
						,cls:'form-container'
						,labelWidth:25
						,autoScroll:true
						,border:false
						,defaults:{
							 labelSeparator:''
							,hideLabel:true
							,name:'assetSource'
						}
						,bbar:['->',{
							 text:_('add.contributemenu.cancel.button')
							,id:'cancelbutton'
							,cls:'button cancel'
							,listeners:{
								click:{
									 fn: function(){
											this.close();
                                            if(Curriki.current.cameFrom.endsWith("#startAdd")) {
                                                Curriki.current.cameFrom = Curriki.current.cameFrom.substring(0, Curriki.current.cameFrom.length-"#startAdd".length);
                                            }
											window.location.href = Curriki.current.cameFrom;
										}
									,scope:this
								}
							}
						},{
							 text:_('add.contributemenu.next.button')
							,id:'nextbutton'
							,cls:'button next'
							,listeners:{
								click:{
									 fn: function(){
										var form = this.findByType('form')[0].getForm();
										var selected = (form.getValues(false))['assetSource'];
										if (form.isValid()){
											AddPath.SourceSelected(selected, form.getValues(false));
										} else {
											alert((_('add.contributemenu.required.fields.dialog_'+selected) !== 'add.contributemenu.required.fields.dialog_'+selected)?_('add.contributemenu.required.fields.dialog_'+selected):_('add.contributemenu.required.fields.dialog'));
										}
									}
									,scope:this
								}
							}
						}]
						,monitorValid:true
						,listeners:{
							render:function(fPanel){
	//TODO: Try to generalize this (for different # of panels)
								fPanel.ownerCt.on(
									'bodyresize'
									,function(wPanel, width, height){
										if (height === 'auto') {
											fPanel.setHeight('auto');
										} else {
											fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
										}
									}
								);
							}
						}
						,items:[{
		// Something had
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('add.contributemenu.subtitle_have')
								,cls:'subtitle'
							}

		// File upload
						},{
							 xtype:'radio'
							,value:'file'
							,inputValue:'file'
							,boxLabel:_('add.contributemenu.option.file')
							,checked:true
							,listeners:{
								check:AddPath.RadioSelect
							}
						},{
							 xtype:'container'
							,id:'file-container-cmp'
							,autoEl:{
								 tag:'div'
								,id:'file-container'
								,html:''
							}
							,items:[{
								 xtype:'textfield'
								,inputType:'file'
								,id:'file-entry-box'
								,name:'filepath'
								,allowBlank:false
								,preventMark:true
								,hideMode:'display'
								,hideLabel:true
		//						,hidden:true
		//						,disabled:true
								,listeners:{ // focus, invalid, blur, valid
									focus:function(){
										var pName = Ext.getCmp('file-entry-box').getValue();
										var i = pName.lastIndexOf('\\');
										var j = pName.lastIndexOf('/');

										var k = (i>j)?i:j;
										pName = pName.substring(k+1);

										Ext.getCmp('filename-entry-box').setValue(pName);
									}
								}
							},{
								 xtype:'textfield'
								,id:'filename-entry-box'
								,name:'filename'
								,allowBlank:false
								,preventMark:true
								,hideLabel:true
								,hidden:true
								,disabled:true
							}]

		// Video upload
						},{
							 xtype:'radio'
							,value:'video_upload'
							,inputValue:'video_upload'
							,boxLabel:_('add.contributemenu.option.video_upload')
							,listeners:{
								check:AddPath.RadioSelect
							}
						},{
							 xtype:'container'
							,id:'video_upload-container-cmp'
							,hidden:true
							,autoEl:{
								 tag:'div'
								,id:'video_upload-container'
								,html:''
							}
							,items:[{
								 xtype:'textfield'
								,inputType:'file'
								,id:'video_upload-entry-box'
								,name:'upload[file]'
								,allowBlank:false
								,preventMark:true
								,hideMode:'display'
								,hideLabel:true
								,hidden:true
								,disabled:true
								,listeners:{ // focus, invalid, blur, valid
									focus:function(){
									}
								}
							}]

/*
		// VIDITalk Video Upload
						},{
							 xtype:'radio'
							,value:'video_upload'
							,inputValue:'video_upload'
							,boxLabel:_('add.contributemenu.option.video_upload')
							,listeners:{
								check:AddPath.RadioSelect
							}
						},{
							 xtype:'container'
							,id:'video_upload-entry-box'
							,hidden:true
							,autoEl:{
								 tag:'div'
								,id:'video_upload-container'
								,html:''
							}
							,listeners:{
								show:function(){
									window.uploadComplete = function(videoId) {
										Ext.getCmp('video_upload-entry-value').setValue(videoId);
										// TODO: I think there is a better way to do this
										Ext.getCmp('nextbutton').events.click.fire();
									}
									window.capture_div='';
									window.flashLoaded=false;
									window.called_once=false;
									embedVidiCapture('video_upload-entry-video', _('viditalk.sitecode'), null, null, false);
								}
								,hide:function(){
									if (Ext.get('video_upload-entry-video')) {
										Ext.DomHelper.overwrite(Ext.get('video_upload-entry-video'), '');
									}
								}
							}
							,items:[{
								 xtype:'textfield'
								,id:'video_upload-entry-value'
								,allowBlank:false
								,preventMark:true
								,hidden:true
								,disabled:true
							},{
								 xtype:'box'
								,id:'video_upload-entry-video'
								,autoEl:{
									 tag:'div'
									,html:''
									,height:'320px'
								}
							}]
*/

		// External Web Link
						},{
							 xtype:'radio'
							,value:'link'
							,inputValue:'link'
							,boxLabel:_('add.contributemenu.option.link')
							,listeners:{
								check:AddPath.RadioSelect
							}
						},{
							 xtype:'container'
							,id:'link-container-cmp'
							,hidden:true
							,autoEl:{
								 tag:'div'
								,id:'link-container'
								,html:''
							}
							,items:[{
								 xtype:'textfield'
								,id:'link-entry-box'
								,name:'link'
								//,emptyText:_('add.contributemenu.link.empty_msg')
								,value:'http://'
								,disabled:true
								,hidden:true
								,allowBlank:false
								,preventMark:true
								,hideMode:'display'
								,hideLabel:true
								,vtype:'url'
							}]

		// Search Repository
						},{
							 xtype:'radio'
							,value:'repository'
							,inputValue:'repository'
							,boxLabel:_('add.contributemenu.option.repository')
	// TODO: Removed for EOU1, add back in EOU2
							,hidden:true|| !this.toFolder
							,hideLabel:true|| !this.toFolder
							,hideParent:true



		// Something to make
						},{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('add.contributemenu.subtitle_make')
								,cls:'subtitle'
							}

		// Create with Template
						},{
							 xtype:'radio'
							,value:'template'
							,inputValue:'template'
							,boxLabel:_('add.contributemenu.option.template')

		// Create "from scratch"
						},{
							 xtype:'radio'
							,value:'scratch'
							,inputValue:'scratch'
							,boxLabel:_('add.contributemenu.option.scratch')

/*
		// Create with VIDITalk
						},{
							 xtype:'radio'
							,value:'video_capture'
							,inputValue:'video_capture'
							,boxLabel:_('add.contributemenu.option.video_capture')
							,listeners:{
								check:{
									fn:function(e, checked){
										AddPath.RadioSelect(e, checked);
										Ext.fly('video_capture-container').scrollIntoView('addDialogueForm');
									}
									,scope:this
								}
							}
						},{
							 xtype:'container'
							,id:'video_capture-entry-box'
							,listeners:{
								show:function(){
									window.uploadComplete = function(videoId) {
										Ext.getCmp('video_capture-entry-value').setValue(videoId);
										// TODO: I think there is a better way to do this
										Ext.getCmp('nextbutton').events.click.fire();
									}
									window.capture_div='';
									window.flashLoaded=false;
									window.called_once=false;
									embedVidiCapture('video_capture-entry-video', _('viditalk.sitecode'), null, null, false);
								}
								,hide:function(){
									if (Ext.get('video_capture-entry-video')) {
										Ext.DomHelper.overwrite(Ext.get('video_capture-entry-video'), '');
									}
								}
							}
							,hidden:true
							,items:[{
								 xtype:'textfield'
								,id:'video_capture-entry-value'
								,allowBlank:false
								,preventMark:true
								,hidden:true
								,disabled:true
							},{
								 xtype:'box'
								,id:'video_capture-entry-video'
								,autoEl:{
									 tag:'div'
									,html:''
									,height:'320px'
								}
							}]
							,autoEl:{
								 tag:'div'
								,id:'video_capture-container'
								,html:''
							}
*/

		// Create folder
						},{
							 xtype:'radio'
							,value:'folder'
							,inputValue:'folder'
							,boxLabel:_('add.contributemenu.option.folder')
							,hidden:!this.toFolder
							,hideParent:true
						}]
					}]
				});

				AddPath.AddSource.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apSource', AddPath.AddSource);



		AddPath.TemplateList = function() {
			var pfx = 'add.select'+Curriki.current.templateType;

			var retVal = [];

			var i = 1;
			while(_(pfx+'.list'+i+'.header') !== pfx+'.list'+i+'.header'){
				var tpl = [];
				tpl.push({
					 xtype:'radio'
					,name:'templateName'
					,value:'list'+i
					,checked:(i===1?true:false) // Default first as checked
					,boxLabel:_(pfx+'.list'+i+'.header')
					,listeners:{
						check:AddPath.TemplateSelect
					}
				});
				tpl.push({
					 xtype:'box'
					,autoEl:{
						 tag:'div'
						,html:_(pfx+'.list'+i+'.description')
						,cls:'description'
					}
				});

				retVal.push({
					 xtype:'container'
					,id:'selecttemplate-list'+i
					,items:tpl
					,autoEl:{
						 tag:'div'
						,id:'selecttemplate-list'+i+'-box'
						,html:''
					}
				});

				++i;
			}

			// Default to first item in the list
			Curriki.current.submitToTemplate = _(pfx+'.list1.url');

			return retVal;
		};

		AddPath.TemplateSelect = function(radio, selected) {
			var pfx = 'add.select'+Curriki.current.templateType;
			if (selected) {
				// CURRIKI-2434
				// - Gets called while dialogue is being created (before shown)
				//   so need to check if item is shown yet
				var img = Ext.get('selecttemplate-thumbnail-image');
				if (!Ext.isEmpty(img)) {
					img.set({'src': _(pfx+'.'+radio.value+'.thumbnail')});
					Curriki.current.submitToTemplate = _(pfx+'.'+radio.value+'.url');
				}
			}
		};

		AddPath.SelectTemplate = Ext.extend(Curriki.ui.dialog.Actions, {
			  initComponent:function(){
				var tmplPfx = 'add.select'+Curriki.current.templateType;
				var dlgType = (Curriki.current.templateType === 'format')?'Format':'Template';

				Ext.apply(this, {
					 id:'SelectTemplateDialogueWindow'
					,title:_(tmplPfx+'.title')
					,cls:'addpath addpath-templates resource resource-add'
					,border:false
					,bodyBorder:false
					,items:[{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,html:_(tmplPfx+'.guidingquestion')
							,cls:'guidingquestion'
						}
					},{
						 xtype:'box'
						,hidden:!(Curriki.current.templateType == 'format')
						,autoEl:{
							 tag:'div'
							,html:_('add.selectformat.instruction')
							,cls:'instructionTxt'
							,hidden:!(Curriki.current.templateType == 'format')
						}
					},{
						 xtype:'form'
						,id:'SelectTemplateDialoguePanel'
						,formId:'Select'+dlgType+'DialogueForm'
						,border:false
						,bodyBorder:false
						,labelWidth:25
						,defaults:{
							 labelSeparator:''
							,border:false
							,bodyBorder:false
						}
						,buttonAlign:'right'
						,buttons:[{
							 text:_(tmplPfx+'.cancel.button')
							,id:'cancelbutton'
							,cls:'button cancel'
							,listeners:{
								'click':function(e, ev){
									Ext.getCmp(e.id).ownerCt.ownerCt.close();
									window.location.href = Curriki.current.cameFrom;
								}
							}
						},{
							 text:_(tmplPfx+'.next.button')
							,id:'nextbutton'
							,cls:'button next'
							,listeners:{
								'click':function(e, ev){
									AddPath.PostToTemplate(Curriki.current.submitToTemplate);
									Ext.getCmp(e.id).ownerCt.ownerCt.close();
								}
							}
						}]
						,items:[{
							id:'selecttemplate-form-container'
							,layout:'column'
							,defaults:{border:false}
							,items:[{
								 columnWidth:0.55
								,items:[{
									 xtype:'container'
									,id:'selecttemplate-list'
									,items:AddPath.TemplateList()
									,autoEl:{
										 tag:'div'
										,id:'selecttemplate-list-box'
										,html:''
									}
								}]
							},{
								 columnWidth:0.35
								,items:[{
									 xtype:'box'
									,id:'selecttemplate-thumbnail-container'
									,anchor:''
									,autoEl:{
										 tag:'div'
										,id:'selecttemplate-thumbnail'
										,style:'margin:8px 0 8px 10px'
										,children:[{
											 tag:'img'
											,id:'selecttemplate-thumbnail-image'
											,src:_(tmplPfx+'.list1.thumbnail')
											,onLoad:"Ext.getCmp('SelectTemplateDialogueWindow').syncShadow();"
										}]
									}
								}]
							}]
						}]
					}]
				});

				AddPath.SelectTemplate.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apSelectTemplate', AddPath.SelectTemplate);



		AddPath.Metadata1 = Ext.extend(Curriki.ui.dialog.Actions, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.setrequiredinfo.part1.title')
					,cls:'addpath addpath-metadata resource resource-add'
					,items:[{
						 xtype:'panel'
						,cls:'guidingquestion-container'
						,items:[{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('add.setrequiredinfo.part1.guidingquestion')
								,cls:'guidingquestion'
							}
						},{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('form.required.fields.instruction')
								,cls:'instruction'
							}
						}]
					},{
						 xtype:'form'
						,id:'MetadataDialoguePanel'
						,formId:'MetadataDialogueForm'
						,labelWidth:25
						,autoScroll:true
						,border:false
						,defaults:{
							 labelSeparator:''
						}
						,bbar:['->',{
							 text:_('add.setrequiredinfo.part1.cancel.button')
							,id:'cancelbutton'
							,cls:'button cancel'
							,listeners:{
								click:{
									 fn: function(){
										this.close();
										window.location.href = Curriki.current.cameFrom;
									}
									,scope:this
								}
							}
						},{
							 text:_('add.setrequiredinfo.part1.next.button')
							,id:'nextbutton'
							,cls:'button next'
							,listeners:{
								click:{
									 fn: function(){
										var form = this.findByType('form')[0].getForm();
										if (form.isValid()){
											Curriki.current.sri1 = form.getValues(false);
											Curriki.current.sri1.fw_items = this.findByType('treepanel')[0].getChecked('id');

											this.close();

											var p = Ext.ComponentMgr.create({'xtype':'apSRI2'});
											p.show();
											Ext.ComponentMgr.register(p);
										} else {
											var errorMsg = {
												 msg:_('form.scratch.required.fields.dialog')
												,invalid:0
												,form:form
											}
											Ext.each(['title', 'description', 'subject', 'level', 'ict'], function(item){
												var invalid = null;

												switch (item){
													case 'title':
													case 'description':
													case 'ict':
														if (!this.form.findField(item).isValid()){
															invalid = item;
														}
														break;

													case 'subject':
														if (!this.form.findField('fw_items-validation').isValid()){
															invalid = item;
														}
														break;

													case 'level':
														if (!this.form.findField('educational_level2-validation').isValid()){
															invalid = item;
														}
														break;

													default:
														break;
												}
												var title = Ext.get('metadata-'+item+'-title');
												if (Ext.isEmpty(title)) {
													switch (invalid){
														case 'ict':
															title = Ext.get('metadata-instructional_component2-title');
															break;

														case 'subject':
															title = Ext.get('metadata-fw_items-title');
															break;

														case 'level':
															title = Ext.get('metadata-educational_level2-title');
															break;
													}
												}
												if (!Ext.isEmpty(invalid)){
													this.msg = this.msg+"\n\t"+_('form.scratch.required.fields.dialog.'+item);
													if (title) {
														title.addClass('metadata-title-field-invalid');
													}
													this.invalid++;
												} else {
													if (title) {
														title.removeClass('metadata-title-field-invalid');
													}
												}
											}, errorMsg);
											if (errorMsg.invalid > 0){
												alert(errorMsg.msg);
											}
										}
									}
									,scope:this
								}
							}
						}]
						,monitorValid:true
						,listeners:{
							render:function(fPanel){
	//TODO: Try to generalize this (for different # of panels)
								fPanel.ownerCt.on(
									'bodyresize'
									,function(wPanel, width, height){
										if (height === 'auto') {
											fPanel.setHeight('auto');
										} else {
											fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
										}
									}
								);
							}
						}
						,items:[{
		// Title
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,id:'metadata-title'
								,cls:'information-header information-header-required'
								,children:[{
									 tag:'em'
									,id:'metadata-title-required'
									,cls:'required-indicator'
									,html:_('form.required.fields.indicator')
								},{
									 tag:'span'
									,id:'metadata-title-title'
									,cls:'metadata-title'
									,html:_('sri.title_title')
								},{
									 tag:'img'
									,id:'metadata-title-info'
									,cls:'metadata-tooltip'
									,src:Curriki.ui.InfoImg
									,qtip:_('sri.title_tooltip')
								}]
							}
						},{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('sri.title_txt')
								,cls:'directions'
							}
						},{
							 xtype:'textfield'
							,id:'metadata-title-entry'
							,name:'title'
							,emptyText:_('sri.title_content')
							,allowBlank:false
							,preventMark:true
							,hideLabel:true
							,width:'80%'

		// Description
						},{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,id:'metadata-description'
								,cls:'information-header information-header-required'
								,children:[{
									 tag:'em'
									,id:'metadata-description-required'
									,cls:'required-indicator'
									,html:_('form.required.fields.indicator')
								},{
									 tag:'span'
									,id:'metadata-description-title'
									,cls:'metadata-title'
									,html:_('sri.description_title')
								},{
									 tag:'img'
									,id:'metadata-description-info'
									,cls:'metadata-tooltip'
									,src:Curriki.ui.InfoImg
									,qtip:_('sri.description_tooltip')
								}]
							}
						},{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('sri.description_txt')
								,cls:'directions'
							}
						},{
							 xtype:'textarea'
							,id:'metadata-description-entry'
							,name:'description'
							,emptyText:_('sri.description.empty_msg')
							,allowBlank:false
							,preventMark:true
							,hideLabel:true
							,width:'80%'

		// Subject  -- Educational Level
		// Subject
						},{
							 layout:'column'
							,border:false
							,defaults:{border:false}
							,items:[{
								 columnWidth:0.5
								,items:[{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,id:'metadata-fw_items'
										,cls:'information-header information-header-required'
										,children:[{
											 tag:'em'
											,id:'metadata-fw_items-required'
											,cls:'required-indicator'
											,html:_('form.required.fields.indicator')
										},{
											 tag:'span'
											,id:'metadata-fw_items-title'
											,cls:'metadata-title'
											,html:_('sri.fw_items_title')
										},{
											 tag:'img'
											,id:'metadata-fw_items-info'
											,cls:'metadata-tooltip'
											,src:Curriki.ui.InfoImg
											,qtip:_('sri.fw_items_tooltip')
										}]
									}
								},{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,html:_('sri.fw_items_txt')
										,cls:'directions'
									}
								},{
									// A "TreeCheckBoxGroup" would be nice here
									 xtype:'numberfield'
									,id:'fw_items-validation'
									,allowBlank:false
									,preventMark:true
									,minValue:1
									,hidden:true
									,listeners:{
										 valid:function(field){
											if (!this.rendered || this.preventMark) {
												return;
											}
											var fieldset = Ext.getCmp('fw_items-tree');
											fieldset.removeClass('x-form-invalid');
											fieldset.el.dom.qtip = '';
										}
										,invalid:function(field, msg){
											if (!this.rendered || this.preventMark) {
												return;
											}
											var fieldset = Ext.getCmp('fw_items-tree');
											fieldset.addClass('x-form-invalid');
											var iMsg = field.invalidText;
											fieldset.el.dom.qtip = iMsg;
											fieldset.el.dom.qclass = 'x-form-invalid-tip';
											if(Ext.QuickTips){ // fix for floating editors interacting with DND
												Ext.QuickTips.enable();
											}

										}
									}
								}
								,(function(){
									var checkedCount = 0;
									var md = Curriki.current.metadata;
									if (md) {
										var fw = md.fw_items;
										Ext.isArray(fw) && (function(ca){
											var childrenFn = arguments.callee;
											Ext.each(ca, function(c){
												if (c.id) {
													if (c.checked = (fw.indexOf(c.id) !== -1)) {
														checkedCount++;
													}
													if (c.children) {
														childrenFn(c.children);
													}
												}
											});
										})(Curriki.data.fw_item.fwChildren);
									}
									return Ext.apply(AddPath.fwTree = Curriki.ui.component.asset.getFwTree(), {
										listeners: {
											render:function(comp){
												comp.findParentByType('apSRI1').on('show', function() {
													Ext.getCmp('fw_items-validation').setValue(checkedCount)
												});
											}
										}
									})
								})()]

		// Educational Level
							},{
								 columnWidth:0.5
								,items:[{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,id:'metadata-educational_level2'
										,cls:'information-header information-header-required'
										,children:[{
											 tag:'em'
											,id:'metadata-educational_level2-required'
											,cls:'required-indicator'
											,html:_('form.required.fields.indicator')
										},{
											 tag:'span'
											,id:'metadata-educational_level2-title'
											,cls:'metadata-title'
											,html:_('sri.educational_level2_title')
										},{
											 tag:'img'
											,id:'metadata-educational_level2-info'
											,cls:'metadata-tooltip'
											,src:Curriki.ui.InfoImg
											,qtip:_('sri.educational_level2_tooltip')
										}]
									}
								},{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,html:_('sri.educational_level2_txt')
										,cls:'directions'
									}
								},{
									// A "CheckBoxGroup" would be nice here
									 xtype:'numberfield'
									,id:'educational_level2-validation'
									,allowBlank:false
									,preventMark:true
									,minValue:1
									,invalidText:'TRANSLATE: This field is required'
									,hidden:true
									,listeners:{
										 valid:function(field){
											var fieldset = Ext.getCmp('educational_level2-set');
											fieldset.removeClass('x-form-invalid');
											fieldset.el.dom.qtip = '';
										}
										,invalid:function(field, msg){
											var fieldset = Ext.getCmp('educational_level2-set');
											fieldset.addClass('x-form-invalid');
											var iMsg = field.invalidText;
											fieldset.el.dom.qtip = iMsg;
											fieldset.el.dom.qclass = 'x-form-invalid-tip';
											if(Ext.QuickTips){ // fix for floating editors interacting with DND
												Ext.QuickTips.enable();
											}

										}
										,render:function(comp){
											comp.findParentByType('apSRI1').on('show', function() {
												if (!Ext.isEmpty(Curriki.current.metadata)) {
													var md = Curriki.current.metadata;

													if (!Ext.isEmpty(md.educational_level) && Ext.isArray(md.educational_level)){
														md.educational_level.each(function(el){
															Ext.getCmp(Ext.select('input[type="checkbox"][name="educational_level2"][value="'+el+'"]').first().dom.id).setValue(true);
														});
													}
												}
											});
										}
									}
								},{
									 xtype:'fieldset'
									,id:'educational_level2-set'
									,border:false
									,autoHeight:true
									,preventMark:true
									,defaults:{
										 xtype:'checkbox'
										,name:'educational_level2'
										,hideLabel:true
										,labelSeparator:''
										,listeners:{
											check:function(e, checked){
												var validator = Ext.getCmp('educational_level2-validation');
												validator.setValue(validator.getValue()+(checked?1:-1));
											}
										}
									}
									,items:Curriki.data.el.data
								}]
							}]

		// Keywords
							},{
								 border:false
								,items:[{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,id:'metadata-keywords'
										,cls:'information-header'
										,children:[{
											 tag:'span'
											,id:'metadata-keywords-title'
											,cls:'metadata-title'
											,html:_('sri.keywords_title')
										},{
											 tag:'img'
											,id:'metadata-keywords-info'
											,cls:'metadata-tooltip'
											,src:Curriki.ui.InfoImg
											,qtip:_('sri.keywords_tooltip')
										}]
									}
								},{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,html:_('sri.keywords_txt')
										,cls:'directions'
									}
								},{
									 xtype:'textfield'
									,id:'metadata-keywords-entry'
									,name:'keywords'
									,emptyText:_('sri.keywords.empty_msg')
									,hideLabel:true
									,listeners:{
										render:function(comp){
											comp.findParentByType('apSRI1').on('show', function() {
												if (!Ext.isEmpty(Curriki.current.metadata)) {
													var md = Curriki.current.metadata;

													if (!Ext.isEmpty(md.keywords)){
														if (Ext.isArray(md.keywords)){
															md.keywords = md.keywords.join(' ');
														}
														Ext.getCmp('metadata-keywords-entry').setValue(md.keywords);
													}
												}
											})
										}
									}
								}]

		// Instructional Component Type
						},{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,id:'metadata-instructional_component2'
								,cls:'information-header information-header-required'
								,children:[{
									 tag:'em'
									,id:'metadata-instructional_component2-required'
									,cls:'required-indicator'
									,html:_('form.required.fields.indicator')
								},{
									 tag:'span'
									,id:'metadata-instructional_component2-title'
									,cls:'metadata-title'
									,html:_('sri.instructional_component2_title')
								},{
									 tag:'img'
									,id:'metadata-instructional_component2-info'
									,cls:'metadata-tooltip'
									,src:Curriki.ui.InfoImg
									,qtip:_('sri.instructional_component2_tooltip')
								}]
							}
						},{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('sri.instructional_component2_txt')
								,cls:'directions'
							}
						},{

							 xtype:'multiselect'
							,name:'ict'
							,hideLabel:true
							,enableToolbar:false
							//,legend:_('sri.instructional_component2_title')
							,legend:' '
							,store:Curriki.data.ict.store
							,valueField:'id'
							,displayField:'ict'
							,width:250
							,height:100
							,allowBlank:false
							,preventMark:true
							,minLength:1
							,isFormField:true
		/*
						},{
							 xtype:'combo'
							,id:'metadata-instructional_component2-entry'
							,hiddenName:'instructional_component2'
							,hideLabel:true
							,mode:'local'
							,store:Curriki.data.ict.store
							,displayField:'ict'
							,valueField:'id'
							,typeAhead:true
							,triggerAction:'all'
							,emptyText:'Select an ICT...'
							,selectOnFocus:true
							,forceSelection:true
		*/

						}]
					}]
				});

				AddPath.Metadata1.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apSRI1', AddPath.Metadata1);


		AddPath.Metadata2 = Ext.extend(Curriki.ui.dialog.Actions, {
			  initComponent:function(){
				Ext.apply(this, {
					 id:'MetadataDialogueWindow'
					,title:_('add.setrequiredinfo.part2.title')
					,cls:'addpath addpath-metadata resource resource-add'
					,items:[{
						 xtype:'panel'
						,cls:'guidingquestion-container'
						,items:[{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('add.setrequiredinfo.part2.guidingquestion')
								,cls:'guidingquestion'
							}
						},{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('form.required.fields.instruction')
								,cls:'instruction'
							}
						}]
					},{
						 xtype:'form'
						,id:'MetadataDialoguePanel'
						,formId:'MetadataDialogueForm'
						,labelWidth:25
						,autoScroll:true
						,border:false
						,defaults:{
							 labelSeparator:''
						}
						,bbar:['->',{
							 text:_('add.setrequiredinfo.part2.cancel.button')
							,id:'cancelbutton'
							,cls:'button cancel'
							,listeners:{
								'click':function(e, ev){
									Ext.WindowMgr.get('MetadataDialogueWindow').close();
									window.location.href = Curriki.current.cameFrom;
								}
							}
						},{
							 text:_('add.setrequiredinfo.part2.next.button')
							,id:'nextbutton'
							,cls:'button next'
							,listeners:{
								click:{
									 fn: function(){
										var form = this.findByType('form')[0].getForm();
										if (form.isValid()){
											Curriki.current.sri2 = form.getValues(false);

											this.close();

											AddPath.MetadataFinished();
										} else {
											var errorMsg = {
												 msg:_('form.scratch.required.fields.dialog')
												,invalid:0
												,form:form
											}
											Ext.each(['rights'], function(item){
												var invalid = null;

												switch (item){
													case 'rights':
														if (!this.form.findField('right_holder').isValid()){
															invalid = item;
														}
														break;
													default:
														break;
												}
												var title = Ext.get('metadata-'+item+'-title');
												if (Ext.isEmpty(title)) {
													switch (invalid){
														case 'rights':
															title = Ext.get('metadata-rights_holder-title');
															break;
													}
												}
												if (!Ext.isEmpty(invalid)){
													this.msg = this.msg+"\n\t"+_('form.scratch.required.fields.dialog.'+item);
													if (title) {
														title.addClass('metadata-title-field-invalid');
													}
													this.invalid++;
												} else {
													if (title) {
														title.removeClass('metadata-title-field-invalid');
													}
												}
											}, errorMsg);
											if (errorMsg.invalid > 0){
												alert(errorMsg.msg);
											}
										}
									}
									,scope:this
								}
							}
						}]
						,monitorValid:true
						,listeners:{
							render:function(fPanel){
	//TODO: Try to generalize this (for different # of panels)
								fPanel.ownerCt.on(
									'bodyresize'
									,function(wPanel, width, height){
										if (height === 'auto') {
											fPanel.setHeight('auto');
										} else {
											fPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+(Ext.isIE?AddPath.ie_size_shift:0)));
										}
									}
								);
							}
						}
						,items:[{
		// Access Privileges
								 border:false
								,items:[{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,id:'metadata-rights'
										,cls:'information-header'
										,children:[{
											 tag:'span'
											,id:'metadata-rights-title'
											,cls:'metadata-title'
											,html:_('sri.rights_title')
										},{
											 tag:'img'
											,id:'metadata-rights-info'
											,cls:'metadata-tooltip'
											,src:Curriki.ui.InfoImg
											,qtip:_('sri.rights_tooltip')
										}]
									}
								},{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,html:_('sri.rights_txt')
										,cls:'directions'
									}
								},{
									 border:false
									,xtype:'radiogroup'
									,width:'90%'
									,columns:[.95]
									,vertical:true
									,defaults:{
										name:'rights'
									}
									,items:Curriki.data.rights.data
									,listeners:{
										render:function(comp){
											comp.findParentByType('apSRI2').on('show', function() {
												if (!Ext.isEmpty(Curriki.current.metadata)) {
													var md = Curriki.current.metadata;

													if (!Ext.isEmpty(md.rights)){
														Ext.getCmp(Ext.select('input[type="radio"][name="rights"][value="'+md.rights+'"]').first().dom.id).setValue(md.rights);
													}
												}
											})
										}
									}
								}]

		// Hide from Search
							},{
								 border:false
								,items:[{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,id:'metadata-hidden_from_search'
										,cls:'information-header'
										,children:[{
											 tag:'span'
											,id:'metadata-hidden_from_search-title'
											,cls:'metadata-title'
											,html:_('sri.hidden_from_search_title')
										},{
											 tag:'img'
											,id:'metadata-hidden_from_search-info'
											,cls:'metadata-tooltip'
											,src:Curriki.ui.InfoImg
											,qtip:_('sri.hidden_from_search_tooltip')
										}]
									}
								},{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,html:_('sri.hidden_from_search_txt')
										,cls:'directions'
									}
								},{
									 xtype:'checkbox'
									,name:'hidden_from_search'
									,boxLabel:_('sri.hidden_from_search_after')
								}]

		// Language
							},{
								 border:false
								,items:[{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,id:'metadata-language'
										,cls:'information-header'
										,children:[{
											 tag:'span'
											,id:'metadata-language-title'
											,cls:'metadata-title'
											,html:_('sri.language_title')
										},{
											 tag:'img'
											,id:'metadata-language-info'
											,cls:'metadata-tooltip'
											,src:Curriki.ui.InfoImg
											,qtip:_('sri.language_tooltip')
										}]
									}
								},{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,html:_('sri.language_txt')
										,cls:'directions'
									}
								},{
									 xtype:'combo'
									,id:'metadata-language-entry'
									,hiddenName:'language'
									,hideLabel:true
									,width:'60%'
									,mode:'local'
									,store:Curriki.data.language.store
									,displayField:'language'
									,valueField:'id'
									,typeAhead:true
									,triggerAction:'all'
									,emptyText:_('sri.language_empty_msg')
									,selectOnFocus:true
									,forceSelection:true
									,value:Curriki.data.language.initial
										?Curriki.data.language.initial
										:undefined
									,listeners:{
										render:function(comp){
											comp.findParentByType('apSRI2').on('show', function() {
												if (!Ext.isEmpty(Curriki.current.metadata)) {
													var md = Curriki.current.metadata;

													if (!Ext.isEmpty(md.language)){
														Ext.getCmp('metadata-language-entry').setValue(md.language);
													}
												}
											})
										}
									}
								}]

		// Rights Holder(s)
							},{
								 border:false
								,items:[{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,id:'metadata-right_holder'
										,cls:'information-header information-header-required'
										,children:[{
											 tag:'em'
											,id:'metadata-title-required'
											,cls:'required-indicator'
											,html:_('form.required.fields.indicator')
										},{
											 tag:'span'
											,id:'metadata-right_holder-title'
											,cls:'metadata-title'
											,html:_('sri.right_holder_title')
										},{
											 tag:'img'
											,id:'metadata-right_holder-info'
											,cls:'metadata-tooltip'
											,src:Curriki.ui.InfoImg
											,qtip:_('sri.right_holder_tooltip')
										}]
									}
								},{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,html:_('sri.right_holder_txt')
										,cls:'directions'
									}
								},{
									 xtype:'textfield'
									,id:'metadata-right_holder-entry'
									,name:'right_holder'
									,hideLabel:true
									,value:Curriki.data.user.me.fullname
									,allowBlank:false
									,preventMark:true
									,width:'60%'
									,listeners:{
										render:function(comp){
											comp.findParentByType('apSRI2').on('show', function() {
												if (!Ext.isEmpty(Curriki.current.metadata)) {
													var md = Curriki.current.metadata;

													if (!Ext.isEmpty(md.rightsHolder)){
														Ext.getCmp('metadata-right_holder-entry').setValue(md.rightsHolder);
													}
												}
											})
										}
									}
								}]
							},{ // Licence Deed
								 border:false
								,items:[{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,id:'metadata-license_type'
										,cls:'information-header'
										,children:[{
											 tag:'span'
											,id:'metadata-license_type-title'
											,cls:'metadata-title'
											,html:_('sri.license_type_title')
										},{
											 tag:'img'
											,id:'metadata-license_type-info'
											,cls:'metadata-tooltip'
											,src:Curriki.ui.InfoImg
											,qtip:_('sri.license_type_tooltip')
										}]
									}
								},{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,html:_('sri.license_type_txt')
										,cls:'directions'
									}
								},{
									 xtype:'box'
									,autoEl:{
										 tag:'div'
										,html:_('sri.license_type_heading')
									}
								},{
									 xtype:'combo'
									,id:'metadata-license_type-entry'
									,hiddenName:'license_type'
									,hideLabel:true
									,width:'75%'
									,mode:'local'
									,store:Curriki.data.licence.store
									,displayField:'licence'
									,valueField:'id'
									,typeAhead:true
									,triggerAction:'all'
									,emptyText:_('sri.license_type_empty_msg')
									,selectOnFocus:true
									,forceSelection:true
									,value:Curriki.data.licence.initial
										?Curriki.data.licence.initial
										:undefined
									,listeners:{
										render:function(comp){
											comp.findParentByType('apSRI2').on('show', function() {
												if (!Ext.isEmpty(Curriki.current.metadata)) {
													var md = Curriki.current.metadata;

													if (!Ext.isEmpty(md.licenseType)){
														Ext.getCmp('metadata-license_type-entry').setValue(md.licenseType);
													}
												}
											})
										}
									}
								}
                            ]

						}, { // grant_curriki_commercial_license
                        border:false,
                        items:[{ xtype:'checkbox',name:'grantCurrikiCommercialLicense',
                                boxLabel:_("sri.license_type_license_to_curriki"),
                                checked: true,
                                id: 'metadata-grantCurrikiCommercialLicense-entry',
                                listeners:{
                                    render:function(comp){
                                        comp.findParentByType('apSRI2').on('show', function() {
                                            if (!Ext.isEmpty(Curriki.current.metadata)) {
                                                var md = Curriki.current.metadata;
                                                if (!Ext.isEmpty(md.grantCurrikiCommercialLicense) && md.grantCurrikiCommercialLicense=="off"){
                                                    Ext.getCmp('metadata-grantCurrikiCommercialLicense-entry').setVisible(false);
                                                    Ext.getCmp('metadata-grantCurrikiCommercialLicense-entry').originalValue=false;
                                                    Ext.getCmp('metadata-grantCurrikiCommercialLicense-entry').setValue(false);
                                                }
                                            }
                                        })
                                    }
                                }
								}]
                        }]
					}]
				});

				AddPath.Metadata2.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apSRI2', AddPath.Metadata2);



		AddPath.MetadataFinished = function(){
			// Save asset
			// Publish Asset
			// Set any items in Curriki.current as needed
			// Display "Done" message

			// Initial asset has been created
			// 1. Fill in metadata
			// 2. Publish asset
			// 3. Display "Done" message

			if ("string" === typeof Curriki.current.sri1.ict){
				Curriki.current.sri1.ict = Curriki.current.sri1.ict.split(',');
			}
			if ("string" === typeof Curriki.current.sri1.educational_level2){
				Curriki.current.sri1.educational_level2 = Curriki.current.sri1.educational_level2.split(',');
			}
            if("undefined"==typeof(Curriki.current.sri2.grantCurrikiCommercialLicense)) {
                Curriki.current.sri2.grantCurrikiCommercialLicense = "off";
            }

			var metadata = Curriki.current.sri1;
			Ext.apply(metadata, Curriki.current.sri2);

			Curriki.assets.SetMetadata(
				Curriki.current.asset.assetPage,
				metadata,
				function(newMetadata){
					console.log("SetMD CB: ", newMetadata);
					Curriki.assets.Publish(
						Curriki.current.asset.assetPage,
						Curriki.current.publishSpace,
						function(newAsset){
							console.log("Published CB: ", newAsset);
							Curriki.current.assetName = newAsset.assetPage;
							Curriki.current.asset.assetPage = newAsset.assetPage;
							Curriki.current.asset.assetType = newAsset.assetType;
							Curriki.current.asset.fullAssetType = newAsset.fullAssetType;
							Curriki.current.asset.title = newAsset.title;
							Curriki.current.asset.description = newAsset.description;

							if (Curriki.current.parentAsset){
								Curriki.assets.CreateSubasset(
									 Curriki.current.parentAsset
									,Curriki.current.assetName
									,(Curriki.current.drop&&(('undefined' != typeof Curriki.current.drop.targetIndex)?Curriki.current.drop.targetIndex:-1))
									,AddPath.ShowDone()
								);
							} else {
								AddPath.ShowDone();
							}
						}
					)
				}
			);
		}



		AddPath.FinalLink = function(linkName){
		// Types of links to deal with:
		//   View - Need page created
		//   Add - Only shown if have collection available - Open CTV view
		//   Open in Currikulum Builder - Need page created
		//   View (Folder/Collection name) - Need page created
		//   "Continue >>" - ?
		//   Go to My Contributions - Go to this MyCurriki section
		//   Go to My Collections - 
		//   Go to My Favorites - 
		//   Close - Go to where we came from

			var link, text, handler, pageName, disabled;
			text = _('add.finalmessage.'+linkName+'.link');

			pageName = (Curriki.current.asset&&Curriki.current.asset.assetPage)||Curriki.current.assetName;

			disabled = false;

			switch(linkName) {
				case 'view':
					link = '/xwiki/bin/view/'+pageName.replace('.', '/');
					break;

				case 'add':
					// Is like starting from add path entry point E, J, or P (and sort of H)
					// Final message for add is per E

					// Start Content Tree View screen
					if (true || Curriki.data.user.collectionChildren.length > 0
						|| Curriki.data.user.groupChildren.length > 0){
						handler = function(e,evt){
							Curriki.current.flow = 'E';
							Curriki.ui.show('apLocation');
							var sourceDlg = Ext.getCmp('done-dialogue');
							if (sourceDlg){
								sourceDlg.close();
							}
						}
					} else {
						disabled = true;
					}
					break;

				case 'viewtarget':
					link = '/xwiki/bin/view/'+pageName.replace('.', '/');
					text = _('add.finalmessage.viewtarget.link', (Curriki.current.assetTitle||(Curriki.current.sri1&&Curriki.current.sri1.title)||(Curriki.current.asset&&Curriki.current.asset.title)||'UNKNOWN'));
					break;

				case 'continue': // F, N, L Folder version
					link = Curriki.current.cameFrom;
					break;

				case 'contributions':
					link = '/xwiki/bin/view/MyCurriki/Contributions';
					break;

				case 'collections':
					link = '/xwiki/bin/view/MyCurriki/Collections';
					break;

				case 'favorites':
					link = '/xwiki/bin/view/MyCurriki/Favorites';
					break;

				case 'close':
					link = Curriki.current.cameFrom;
					break;
			}

			if (!handler) {
				handler = function(e,evt){
					window.location.href=link;
				}
			}

			return {
				 text:text
				,cls:'button link'
				,handler:handler
				,hidden:disabled
			};
		}

		AddPath.DoneMessage = function(name){
			var msg;

			switch(Curriki.current.flow){
				case 'E':
				case 'H':
				case 'J':
				case 'P':
				case 'F':
				case 'N':
				case 'L':
				case 'FFolder':
				case 'NFolder':
				case 'LFolder':
					var msgArgs = [
						Curriki.current.assetTitle||(Curriki.current.sri1&&Curriki.current.sri1.title)||(Curriki.current.asset&&Curriki.current.asset.title)||'UNKNOWN'
						,Curriki.current.parentTitle||'UNKNOWN'
					];
					msg = '<p>'+_('add.finalmessage.text_'+name+'_success', msgArgs)+'</p>';
					break;

				case 'Copy':
					var msgArgs = [
						Curriki.current.copyOfTitle||'UNKNOWN'
						,Curriki.current.assetTitle||(Curriki.current.sri1&&Curriki.current.sri1.title)||(Curriki.current.asset&&Curriki.current.asset.title)||'UNKNOWN'
					];
					msg = '<p>'+_('add.finalmessage.text_'+name+'_success', msgArgs)+'</p>';
					break;

				case 'Copy2':
					var msgArgs = [
						Curriki.current.copyOfTitle||'UNKNOWN'
						,Curriki.current.assetTitle||(Curriki.current.sri1&&Curriki.current.sri1.title)||(Curriki.current.asset&&Curriki.current.asset.title)||'UNKNOWN'
						,Curriki.current.parentTitle||'UNKNOWN'
					];
					msg = '<p>'+_('add.finalmessage.text_'+name+'_success', msgArgs)+'</p>';
					break;

				default:
					msg = '<p>'+_('add.finalmessage.text_'+name+'_success')+'</p>';
					break;
			}

			switch(Curriki.current.flow){
				case 'K':
				case 'M':
					msg += _('add.finalmessage.text_'+name+'_tip1');
					break;
			}

			return {
				 xtype:'box'
				,autoEl:{
					 tag:'div'
					,cls:'done-message'
					,html:msg
				}
			};
		}

		AddPath.CloseDone = function(dialog){
			return {
					 text:_('add.finalmessage.close.button')
					,id:'closebutton'
					,listeners:{
						'click':{
							 fn:function(e,evt){
								this.close();
								if (!Ext.isEmpty(Curriki.current.cameFrom)){
									window.location.href=Curriki.current.cameFrom;
								}
							}
							,scope:dialog
						}
					}
			};
		}

		AddPath.DoneA = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.finalmessage.title_resource')
					,cls:'addpath addpath-done resource resource-add'
					,bbar:[
						 AddPath.FinalLink('view'),'-'
						,AddPath.FinalLink('add'),'-'
						,AddPath.FinalLink('contributions'),'->'
						,AddPath.CloseDone(this)
					]
					,items:[
						 AddPath.DoneMessage('resource')
					]
				});
				AddPath.DoneA.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apDoneA', AddPath.DoneA);
		Ext.reg('apDoneB', AddPath.DoneA);
		Ext.reg('apDoneD', AddPath.DoneA);
		Ext.reg('apDoneR', AddPath.DoneA);


		AddPath.DoneC = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.finalmessage.title_collection')
					,cls:'addpath addpath-done resource resource-add'
					,bbar:[
						AddPath.FinalLink('collections'),'->'
						,AddPath.CloseDone(this)
					]
					,items:[
						 AddPath.DoneMessage('collection')
					]
				});
				AddPath.DoneC.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apDoneC', AddPath.DoneC);


		AddPath.DoneE = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.finalmessage.title_successful')
					,cls:'addpath addpath-done resource resource-add'
					,bbar:[
						 '->',AddPath.CloseDone(this)
					]
					,items:[
						// This needs arguments for "added {0} into {1}"
						 AddPath.DoneMessage('addto')
					]
				});
				AddPath.DoneE.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apDoneE', AddPath.DoneE);
		Ext.reg('apDoneH', AddPath.DoneE);
		Ext.reg('apDoneJ', AddPath.DoneE);
		Ext.reg('apDoneP', AddPath.DoneE);


		AddPath.DoneF = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.finalmessage.title_successful')
					,cls:'addpath addpath-done resource resource-add'
					,bbar:[
						'->',AddPath.CloseDone(this)
					]
					,items:[
						// This needs arguments for "added {0} into {1}"
						 AddPath.DoneMessage('addto')
					]
				});
				AddPath.DoneF.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apDoneF', AddPath.DoneF);
		Ext.reg('apDoneN', AddPath.DoneF);
		Ext.reg('apDoneL', AddPath.DoneF);

		AddPath.DoneFFolder = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.finalmessage.title_folder')
					,cls:'addpath addpath-done resource resource-add'
					,bbar:[
						'->',AddPath.CloseDone(this)
					]
					,items:[
						// This needs arguments for "added {0} into {1}"
						 AddPath.DoneMessage('addto_folder')
					]
				});
				AddPath.DoneFFolder.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apDoneFFolder', AddPath.DoneFFolder);
		Ext.reg('apDoneNFolder', AddPath.DoneFFolder);
		Ext.reg('apDoneLFolder', AddPath.DoneFFolder);


		AddPath.DoneG = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.finalmessage.title_successful')
					,cls:'addpath addpath-done resource resource-add'
					,bbar:[
						 AddPath.FinalLink('favorites'),'->'
						,AddPath.CloseDone(this)
					]
					,items:[
						 AddPath.DoneMessage('favorites')
					]
				});
				AddPath.DoneG.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apDoneG', AddPath.DoneG);

		AddPath.DoneK = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.finalmessage.title_collection')
					,cls:'addpath addpath-done resource resource-add'
					,bbar:[
						 '->',AddPath.CloseDone(this)
					]
					,items:[
						 AddPath.DoneMessage('collection')
					]
				});
				AddPath.DoneK.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apDoneK', AddPath.DoneK);

		AddPath.DoneM = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.finalmessage.title_collection')
					,cls:'addpath addpath-done resource resource-add'
					,bbar:[
						 '->',AddPath.CloseDone(this)
					]
					,items:[
						 AddPath.DoneMessage('groupcollection')
					]
				});
				AddPath.DoneM.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apDoneM', AddPath.DoneM);

		AddPath.DoneI = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.finalmessage.title_resource')
					,cls:'addpath addpath-done resource resource-add'
					,bbar:[
						 AddPath.FinalLink('view'),'-'
						,AddPath.FinalLink('add'),'->'
						,AddPath.CloseDone(this)
					]
					,items:[
						 AddPath.DoneMessage('resource_simple')
					]
				});
				AddPath.DoneI.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apDoneI', AddPath.DoneI);
		Ext.reg('apDoneO', AddPath.DoneI);

		AddPath.DoneCopy = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.finalmessage.title_copied')
					,cls:'addpath addpath-done resource resource-add'
					,bbar:[
						 AddPath.FinalLink('view'),'-'
						,AddPath.FinalLink('add'),'-'
						,AddPath.FinalLink('contributions'),'->'
						,AddPath.CloseDone(this)
					]
					,items:[
						 AddPath.DoneMessage('copy')
					]
				});
				AddPath.DoneCopy.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apDoneCopy', AddPath.DoneCopy);

		AddPath.DoneCopy2 = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				Ext.apply(this, {
					 title:_('add.finalmessage.title_copiedtolocation')
					,cls:'addpath addpath-done resource resource-add'
					,bbar:[
						 AddPath.FinalLink('view'),'->'
						,AddPath.CloseDone(this)
					]
					,items:[
						 AddPath.DoneMessage('copiedtolocation')
					]
				});
				AddPath.DoneCopy2.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apDoneCopy2', AddPath.DoneCopy2);



		AddPath.ShowDone = function(){
			if (Ext.isEmpty(Curriki.current.flow)) {
				return;
			}

			var pageCreated = (Curriki.current.asset&&Curriki.current.asset.assetPage)||Curriki.current.assetName;
			Curriki.logView('/features/resources/add/'+Curriki.current.flow+Curriki.current.flowFolder+'/'+((Curriki.current.asset&&Curriki.current.asset.assetType)||Curriki.current.assetType||'UNKNOWN')+'/'+pageCreated.replace('.', '/'));

			var displayDone = function(){
				var p = Ext.ComponentMgr.create({
					 xtype:'apDone'+Curriki.current.flow+Curriki.current.flowFolder
					,id:'done-dialogue'
				});
				p.show();
				Ext.ComponentMgr.register(p);
			};

			Ext.ns('Curriki.settings');
			Curriki.settings.localCollectionFetch = true;

			switch (Curriki.current.flow) {
				// If flow in [ABDRIO],Copy then fetch collections for add link
				case 'A':
				case 'B':
				case 'D':
				case 'R':
				case 'I':
				case 'O':
				case 'Copy':
					Curriki.init(function(){
						//Curriki.data.user.GetCollections(function(){
							displayDone();
						//});
					});
					break;

				default:
					Curriki.init(function(){
						displayDone();
					});
					break;
			}
		}




		AddPath.ChooseLocation = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				var topChildren = [];
				//if (Curriki.data.user.collectionChildren.length>0){
					topChildren.push({
						 text:_('panels.myCurriki.myCollections')
						,id:'ctv-drop-tree-collection-root'
						,cls:'ctv-top ctv-header ctv-collections'
						,leaf:false
						,allowDrag:false
						,allowDrop:true // Needed to auto-expand on hover
						,disallowDropping:true // Disable drop on this node
						,expanded:false //(Curriki.data.user.collectionChildren.length < 4)
						//,children:Curriki.data.user.collectionChildren
						,currikiNodeType:'myCollections'
					});
				//}
				//if (Curriki.data.user.groupChildren.length>0){
					topChildren.push({
						 text:_('panels.myCurriki.myGroups')
						,id:'ctv-drop-tree-group-root'
						,cls:'ctv-top ctv-header ctv-groups'
						,leaf:false
						,allowDrag:false
						,allowDrop:true // Needed to auto-expand on hover
						,disallowDropping:true // Disable drop on this node
						,expanded:false //(Curriki.data.user.groupChildren.length < 4)
						//,children:Curriki.data.user.groupChildren
						,currikiNodeType:'myGroups'
					});
				//}

				Ext.apply(this, {
					 id:'ChooseLocationDialogueWindow'
					,title:_('add.chooselocation.title')
					,cls:'addpath addpath-ctv resource resource-add'
					,autoScroll:false
					,width:634
					,items:[{
						 xtype:'panel'
						,id:'guidingquestion-container'
						,cls:'guidingquestion-container'
						,items:[{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('add.chooselocation.guidingquestion')
								,cls:'guidingquestion'
							}
						},{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('add.chooselocation.instruction')
								,cls:'instruction'
							}
						}]
					},{
						 xtype:'form'
						,id:'ChooseLocationDialoguePanel'
						,formId:'ChooseLocationDialogueForm'
						,labelWidth:25
						,autoScroll:false
						,border:false
						,defaults:{
							 labelSeparator:''
						}
						,bbar:['->',{
							 text:_('add.chooselocation.cancel.button')
							,id:'cancelbutton'
							,cls:'button cancel'
							,listeners:{
								click:{
									 fn: function(){
										this.close();
										window.location.href = Curriki.current.cameFrom;
									}
									,scope:this
								}
							}
						},{
							 text:_('add.chooselocation.next.button')
							,id:'nextbutton'
							,cls:'button next'
							,disabled:true
							,listeners:{
								click:{
									 fn: function(){
										AddPath.AddSubasset(function(){
											var dlg = Ext.getCmp('ChooseLocationDialogueWindow');
											if (dlg) {
												dlg.close();
											}
											AddPath.ShowDone();
										});
									}
									,scope:this
								}
							}
						}]
						,listeners:{
						}
						,items:[{
		// DRAG BOX
							 xtype:'panel'
							,id:'resource-pickup'
							,border:false
							,items:[{
								 xtype:'box'
								,autoEl:{
									 tag:'div'
									,html:_('add.chooselocation.instruction_short')
									,cls:'instruction'
								}
							},{
								 xtype:'treepanel'
								,loader: new Curriki.ui.treeLoader.Base()
								,id:'ctv-from-tree-cmp'
								,useArrows:true
								,autoScroll:false
								,border:false
								,cls:'ctv-from-tree'
								,animate:true
								,enableDrag:true
								,rootVisible:false
								,root: new Ext.tree.AsyncTreeNode({
									 text:_('add.chooselocation.pickup_root')
									,id:'ctv-drag-tree-root'
									,cls:'ctv-drag-root'
									,leaf:false
									,hlColor:'93C53C'
									,hlDrop:false
									,allowDrag:false
									,allowDrop:false
									,expanded:true
									,children:[{
										 text:(Curriki.current.asset&&Curriki.current.asset.title)||Curriki.current.assetTitle||'UNKNOWN'
										,id:'ctv-target-node'
										,assetName:(Curriki.current.asset&&Curriki.current.asset.assetPage)||Curriki.current.assetName
										,cls:'ctv-target ctv-resource resource-'+((Curriki.current.asset&&Curriki.current.asset.assetType)||Curriki.current.assetType||'UNKNOWN')
										,leaf:true
									}]
								})
							}]

		// DROP TREE
						},{
							 xtype:'panel'
							,id:'resource-drop'
							,border:false
							,items:[{
								 xtype:'treepanel'
								,loader: new Curriki.ui.treeLoader.Base()
								,id:'ctv-to-tree-cmp'
								,autoScroll:true
								,useArrows:true
								,border:false
								,hlColor:'93C53C'
								,hlDrop:false
								,cls:'ctv-to-tree'
								,animate:true
								,enableDD:true
								,ddScroll:true
								,containerScroll:true
								,rootVisible:false
								,listeners:{
									nodedragover:{
										fn: function(dragOverEvent){
											var draggedNodeId = dragOverEvent.dropNode.attributes.assetName;
											var parentNode = dragOverEvent.target;
											if (dragOverEvent.point !== 'append') {
												parentNode = parentNode.parentNode;
												if (Ext.isEmpty(parentNode)) {
													return false;
												}
											}

											if (!Ext.isEmpty(parentNode.attributes.disallowDropping) && (parentNode.attributes.disallowDropping === true)) {
												dragOverEvent.cancel = true;
												return false;
											}

											var cancel = false;
											while (!Ext.isEmpty(parentNode) && !cancel){
												if (parentNode.id === draggedNodeId) {
													cancel = true;
													dragOverEvent.cancel = true;
													return false;
												} else {
													parentNode = parentNode.parentNode;
												}
											}
										}
										,scope:this
									}
									,nodedrop:{
										fn: function(dropEvent){
											var targetNode = Ext.getCmp('ctv-to-tree-cmp').getNodeById('ctv-target-node');
											var parentNode = targetNode.parentNode;
											var parentNodeId = parentNode.id;
											var nextSibling = targetNode.nextSibling;
											var targetIndex = -1;
											if (nextSibling){
												targetIndex = ('undefined' != typeof nextSibling.attributes.order)?nextSibling.attributes.order:-1;
											}
											Curriki.current.drop = {
												 parentPage:parentNodeId
												,targetIndex:targetIndex
											};
											Curriki.current.parentTitle = parentNode.text;
											AddPath.EnableNext();
										}
										,scope:this
									}
									,expandnode:{
										fn: function(node){
                                            console.log("expandnode 2");
											var wnd = this;
                                            console.log("expandnode 2: fire afterlayout");
											wnd.fireEvent('afterlayout', wnd, wnd.getLayout());
                                            console.log("expandnode 2: afterlayout finished");
										}
										,scope:this
									}
									,render:function(tPanel){
//TODO: Try to generalize this (for different # of panels)
										tPanel.ownerCt.ownerCt.ownerCt.on(
											'bodyresize'
											,function(wPanel, width, height){
												if (height === 'auto') {
													tPanel.setHeight('auto');
												} else {
													tPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+wPanel.findByType('panel')[0].el.getMargins('tb')+wPanel.findByType('panel')[1].getBox().height+wPanel.items.get(1).getFrameHeight()+wPanel.findByType('panel')[1].el.getMargins('tb')+(Ext.isIE?AddPath.ie_size_shift:0)+(Ext.isMac?(2*AddPath.ie_size_shift):0)));
												}
											}
										);
									}
								}
								,root: new Ext.tree.AsyncTreeNode({
									 text:_('add.chooselocation.drop_root')
									,id:'ctv-drop-tree-root'
									,cls:'ctv-drop-root'
									,leaf:false
									,allowDrag:false
									,allowDrop:false
									,expanded:true
									,children:topChildren
								})
							}]

						}]
					}]
				});
				AddPath.ChooseLocation.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apLocation', AddPath.ChooseLocation);




		AddPath.ChooseCopyLocation = Ext.extend(Curriki.ui.dialog.Messages, {
			  initComponent:function(){
				var topChildren = [];
                  // data.user.collectionChildren is loaded later, same for groups, assume there might be something
				//if (true || Curriki.data.user.collectionChildren.length>0){
					topChildren.push({
					 text:_('panels.myCurriki.myCollections')
					,id:'ctv-drop-tree-collection-root'
					,cls:'ctv-top ctv-header ctv-collections'
					,leaf:false
					,allowDrag:false
					,allowDrop:true // Needed to auto-expand on hover
					,disallowDropping:true // Disable drop on this node
					,expanded:false //(Curriki.data.user.collectionChildren.length < 4)
					//,children:Curriki.data.user.collectionChildren
                    ,currikiNodeType:'myCollections'
					});
				//}
				//if (true || Curriki.data.user.groupChildren.length>0){
					topChildren.push({
					 text:_('panels.myCurriki.myGroups')
					,id:'ctv-drop-tree-group-root'
					,cls:'ctv-top ctv-header ctv-groups'
					,leaf:false
					,allowDrag:false
					,allowDrop:true // Needed to auto-expand on hover
					,disallowDropping:true // Disable drop on this node
					,expanded:false //(Curriki.data.user.groupChildren.length < 4)
					//,children:Curriki.data.user.groupChildren
                    ,currikiNodeType:'myGroups'
					});
				//}

				Ext.apply(this, {
					 id:'ChooseLocationDialogueWindow'
					,title:_('add.chooselocation.copy.title')
					,cls:'addpath addpath-ctv resource resource-add'
					,autoScroll:false
					,width:634
					,items:[{
						 xtype:'panel'
						,id:'guidingquestion-container'
						,cls:'guidingquestion-container'
						,items:[{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('add.chooselocation.copy.guidingquestion', (Curriki.current.copyOfTitle||Current.copyOf||'UNKNOWN'))
								,cls:'guidingquestion'
							}
						},{
							 xtype:'box'
							,autoEl:{
								 tag:'div'
								,html:_('add.chooselocation.copy.instruction')
								,cls:'instruction'
							}
						}]
					},{
						 xtype:'form'
						,id:'ChooseLocationDialoguePanel'
						,formId:'ChooseLocationDialogueForm'
						,labelWidth:25
						,autoScroll:false
						,border:false
						,defaults:{
							 labelSeparator:''
						}
						,bbar:['->',{
							 text:_('add.chooselocation.copy.cancel.button')
							,id:'cancelbutton'
							,cls:'button cancel'
							,listeners:{
								click:{
									 fn: function(){
										if (Ext.isEmpty(Curriki.current.drop) || confirm(_('add.chooselocation.copy.error'))) {
											this.close();
											window.location.href = Curriki.current.cameFrom;
										}
									}
									,scope:this
								}
							}
						},{
							 text:_('add.chooselocation.copy.next.button')
							,id:'nextbutton'
							,cls:'button next'
							,listeners:{
								click:{
									 fn: function(){
										if (!Ext.isEmpty(Curriki.current.drop)){
											Curriki.current.publishSpace = Curriki.current.drop.parentPage.replace(/\..*/, '');
											Curriki.current.parentAsset = Curriki.current.drop.parentPage;
										} else {
											Curriki.current.flow = 'Copy';
										}
										next = 'apSRI1';
										Curriki.assets.CopyAsset(
											Curriki.current.copyOf,
											Curriki.current.publishSpace,
											function(asset){
													console.log("CopyAsset CB: ", asset);
													Curriki.current.asset = asset;
													callback = function(){
														var dlg = Ext.getCmp('ChooseLocationDialogueWindow');
														if (dlg) {
															dlg.close();
														}
														AddPath.ShowNextDialogue(next);
													};
													Curriki.assets.GetMetadata(asset.assetPage, function(metadata){
														Curriki.current.metadata = metadata;
														callback();
													});
											}
										);
									}
									,scope:this
								}
							}
						}]
						,listeners:{
						}
						,items:[{
		// DRAG BOX
							 xtype:'panel'
							,id:'resource-pickup'
							,border:false
							,items:[{
								 xtype:'box'
								,autoEl:{
									 tag:'div'
									,html:_('add.chooselocation.copy.instruction_short')
									,cls:'instruction'
								}
							},{
								 xtype:'treepanel'
								,loader: new Curriki.ui.treeLoader.Base()
								,id:'ctv-from-tree-cmp'
								,useArrows:true
								,autoScroll:false
								,border:false
								,cls:'ctv-from-tree'
								,animate:true
								,enableDrag:true
								,rootVisible:false
								,root: new Ext.tree.AsyncTreeNode({
									 text:_('add.chooselocation.pickup_root')
									,id:'ctv-drag-tree-root'
									,cls:'ctv-drag-root'
									,leaf:false
									,hlColor:'93C53C'
									,hlDrop:false
									,allowDrag:false
									,allowDrop:false
									,expanded:true
									,children:[{
										 text:Curriki.current.copyOfTitle||'UNKNOWN'
										,id:'ctv-target-node'
										,assetName:Curriki.current.copyOf
										,cls:'ctv-target ctv-resource resource-'+(Curriki.current.copyOfAssetType||'UNKNOWN')
										,leaf:true
									}]
								})
							}]

		// DROP TREE
						},{
							 xtype:'panel'
							,id:'resource-drop'
							,border:false
							,items:[{
								 xtype:'treepanel'
								,loader: new Curriki.ui.treeLoader.Base()
								,id:'ctv-to-tree-cmp'
								,autoScroll:true
								,useArrows:true
								,border:false
								,hlColor:'93C53C'
								,hlDrop:false
								,cls:'ctv-to-tree'
								,animate:true
								,enableDD:true
								,ddScroll:true
								,containerScroll:true
								,rootVisible:false
								,listeners:{
									nodedragover:{
										fn: function(dragOverEvent){
											var draggedNodeId = dragOverEvent.dropNode.attributes.assetName;
											var parentNode = dragOverEvent.target;
											if (dragOverEvent.point !== 'append') {
												parentNode = parentNode.parentNode;
												if (Ext.isEmpty(parentNode)) {
													return false;
												}
											}

											if (!Ext.isEmpty(parentNode.attributes.disallowDropping) && (parentNode.attributes.disallowDropping === true)) {
												dragOverEvent.cancel = true;
												return false;
											}

											var cancel = false;
											while (!Ext.isEmpty(parentNode) && !cancel){
												if (parentNode.id === draggedNodeId) {
													cancel = true;
													dragOverEvent.cancel = true;
													return false;
												} else {
													parentNode = parentNode.parentNode;
												}
											}
										}
										,scope:this
									}
									,nodedrop:{
										fn: function(dropEvent){
											var targetNode = Ext.getCmp('ctv-to-tree-cmp').getNodeById('ctv-target-node');
											var parentNode = targetNode.parentNode;
											var parentNodeId = parentNode.id;
											var nextSibling = targetNode.nextSibling;
											var targetIndex = -1;
											if (nextSibling){
												targetIndex = ('undefined' != typeof nextSibling.attributes.order)?nextSibling.attributes.order:-1;
											}
											Curriki.current.drop = {
												 parentPage:parentNodeId
												,targetIndex:targetIndex
											};
											Curriki.current.parentTitle = parentNode.text;
											AddPath.EnableNext();
										}
										,scope:this
									}
									,expandnode:{
										fn: function(node){
                                            console.log("expandnode 3")
											var wnd = this;
											wnd.fireEvent('afterlayout', wnd, wnd.getLayout());
										}
										,scope:this
									}
									,render:function(tPanel){
//TODO: Try to generalize this (for different # of panels)
										tPanel.ownerCt.ownerCt.ownerCt.on(
											'bodyresize'
											,function(wPanel, width, height){
												if (height === 'auto') {
													tPanel.setHeight('auto');
												} else {
													tPanel.setHeight(wPanel.getInnerHeight()-(wPanel.findByType('panel')[0].getBox().height+wPanel.findByType('panel')[0].el.getMargins('tb')+wPanel.findByType('panel')[1].getBox().height+wPanel.items.get(1).getFrameHeight()+wPanel.findByType('panel')[1].el.getMargins('tb')+(Ext.isIE?AddPath.ie_size_shift:0)+(Ext.isMac?(2*AddPath.ie_size_shift):0)));
												}
											}
										);
									}
								}
								,root: new Ext.tree.AsyncTreeNode({
									 text:_('add.chooselocation.drop_root')
									,id:'ctv-drop-tree-root'
									,cls:'ctv-drop-root'
									,leaf:false
									,allowDrag:false
									,allowDrop:false
									,expanded:true
									,children:topChildren
								})
							}]

						}]
					}]
				});
				AddPath.ChooseCopyLocation.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apCopyLocation', AddPath.ChooseCopyLocation);



		AddPath.PostToTemplate = function(templateUrl){
			Curriki.assets.CreateAsset(Curriki.current.parentAsset, Curriki.current.publishSpace, function(asset){
				Curriki.current.asset = asset;
				var sf = new Ext.FormPanel({
					 standardSubmit:true
					,url:templateUrl
					,method:'POST'
					,onSubmit: Ext.emptyFn
					,submit: function() {
						this.getForm().getEl().dom.action = this.getForm().url;
						this.getForm().getEl().dom.submit();
					}
					,applyTo:Ext.getBody()
					,cls:'x-hide-display'
					,items:[
						 {xtype:'hidden', name:'pageName', value:asset.assetPage}
						,{xtype:'hidden', name:'cameFrom', value:Curriki.current.cameFrom}
						,{xtype:'hidden', name:'flow', value:Curriki.current.flow}
						,{xtype:'hidden', name:'parentPage', value:Curriki.current.parentAsset}
						,{xtype:'hidden', name:'publishSpace', value:Curriki.current.publishSpace}
					]
				});
				sf.submit();

				/*  Removed (CURRIKI-2230), We are going to a new page anyway
				var sourceDlg = Ext.getCmp(AddPath.AddSourceDialogueId);
				if (sourceDlg){
					sourceDlg.close();
				}
				*/
			});
		}

		AddPath.PostFile = function(callback){
			Curriki.assets.CreateAsset(Curriki.current.parentAsset, Curriki.current.publishSpace, function(asset){
				Curriki.current.asset = asset;

				Ext.Ajax.request({
					url:'/xwiki/bin/upload/'+asset.assetPage.replace('.', '/')
					,isUpload:true
					,form:'addDialogueForm'
					,headers: {
						'Accept':'application/json'
					}
					,callback:function(options, success, response){
						if (success) {
							callback(asset);
						} else {
							console.log('Upload failed', options, response);
							alert(_('add.servertimedout.message.text'));
						}
					}
				});
			});
		}

		AddPath.UploadDlg = Ext.extend(Curriki.ui.dialog.Actions, {
			initComponent:function(){
				Ext.apply(this, {
					title: _('add.video.uploading.dialog.title')
					,cls:'addpath addpath-upload resource resource-add'
					,id:'upload-dlg'
					,width:254
					,minWidth:254
					,items:[{
						 xtype:'panel'
						,html: _('add.video.uploading.dialog.sub.title')
 					},{
						 xtype:'progress'
						,id:'upload-progress-bar'
						,text:'0%'
					}]
					,bbar:[{
						 text:_('add.video.uploading.dialog.cancel.button')
						,id:'upload-cancel-button'
						,cls:'button cancel btn-cancel'
						,listeners:{
							click:{
								 fn: function(){
										Ext.Msg.show({
											title:_('add.video.uploading.dialog.cancel.title')
											,msg:_('add.video.uploading.dialog.cancel.txt')
											,buttons:{
												ok:_('add.video.uploading.dialog.ok.button')
												,cancel:_('add.video.uploading.dialog.cancel.button')
											}
											,fn:function(buttonId){
												if (buttonId == 'yes' || buttonId == 'ok') {
													if (Curriki.current.videoStatusTask) {
														Ext.TaskMgr.stop(Curriki.current.videoStatusTask);
													}
													window.location.href = Curriki.current.cameFrom;
													this.close();
													Curriki.hideLoadingMask = false;
												}
											}
											,closable:false
											,scope:this
										});
										var btns = Ext.Msg.getDialog().buttons;
										btns[0].addClass('btn-next'); // OK
										btns[1].addClass('btn-next'); // YES
										btns[2].addClass('btn-cancel'); // NO
										btns[3].addClass('btn-cancel'); // CANCEL
										Ext.Msg.getDialog().body.findParent('div.x-window-mc', 2, true).setStyle('background-color', '#FFFFFF');
									}
								,scope:this
							}
						}
					}]
				});
				AddPath.UploadDlg.superclass.initComponent.call(this);
			}
		});
		Ext.reg('apUploadDlg', AddPath.UploadDlg);

		AddPath.PostVideo = function(callback, formId, uploadCompleteCallback){
			Curriki.current.videoCompleteCallback = callback;
			if (Ext.isEmpty(formId)) {
				formId = 'addDialogueForm';
			}
			if (Ext.isEmpty(uploadCompleteCallback)) {
				Curriki.current.uploadCompleteCallback = function(success) {
					Curriki.assets.CreateAsset(Curriki.current.parentAsset, Curriki.current.publishSpace, function(asset){
						Curriki.current.asset = asset;

						Curriki.current.videoId = success.id;

						Curriki.current.videoCompleteCallback(asset);
					});
				}
			} else {
				Curriki.current.uploadCompleteCallback = uploadCompleteCallback;
			}

			Curriki.current.uuid = Math.uuid(21);
			Curriki.hideLoadingMask = true;
			Curriki.current.uploading = true;
			Curriki.current.vu_last_update = 0;
			Curriki.ui.show('apUploadDlg');

			// Submit form to post file
			Ext.Ajax.request({
				url:'http://'+_('MEDIAHOST')+'/media/upload?key='+Curriki.current.uuid
				,isUpload:true
				,form:formId
				,headers: {
					'Accept':'text/html'
				}
				,callback:function(options, success, response){
					if (success) {
						// Empty, taken care of via jsonp callback
					} else {
						console.log('Upload failed', options, response);
						alert(_('add.servertimedout.message.text'));
					}
				}
			});

			// Watch status of uploaded file
			Curriki.current.videoStatusRequest = function() {
				Ext.ux.JSONP.request('http://'+_('MEDIAHOST')+'/media/uploadStatus', {
					callbackKey: 'callback',
					params: {
						key: Curriki.current.uuid,
						r: Math.uuid(21)
					},
					callback: Curriki.current.videoJsonCallback
				});
			};

			Curriki.current.videoSuccessCallback = function(success){
				Curriki.current.uploadCompleteCallback(success);
			};

			Curriki.current.videoErrorCallback = function(error){
				console.log('Video Upload Error', error);
				Ext.Msg.alert(_('add.video.cannot.process.title'), _('add.video.cannot.process.txt', _(error.msg)));
				var btns = Ext.Msg.getDialog().buttons;
				btns[0].addClass('btn-next'); // OK
				btns[1].addClass('btn-next'); // YES
				btns[2].addClass('btn-cancel'); // NO
				btns[3].addClass('btn-cancel'); // CANCEL
				Ext.Msg.getDialog().body.findParent('div.x-window-mc', 2, true).setStyle('background-color', '#FFFFFF');
			};

			Curriki.current.videoJsonCallback = function(data){
				if (!Curriki.current.uploading) {
					return;
				}

                if(console) console.log("Complete? " + data.complete + ", Error? " + data.error);
                if (data.complete) {
					if (data.error || data.success) {
						Curriki.current.uploading = false;
						Ext.TaskMgr.stop(Curriki.current.videoStatusTask);
						Curriki.hideLoadingMask = false;
						Ext.getCmp('upload-dlg').hide();

						if (data.error) {
							Curriki.current.videoErrorCallback(data.error);
							return;
						} else if (data.success) {
							Curriki.current.videoSuccessCallback(data.success);
							return;
						}
					}
				} else {
					var now = new Date().getTime();
					if (now < Curriki.current.vu_last_update) {
						return;
					}
					Curriki.current.vu_last_update = now;
					var current = data.current||0;
					var total = data.total||0;
					var percent = 0;
					var ptext = '%';
					if (total&&current) {
						percent = current/total;
					}
					ptext = Math.floor(percent*100) + '%';
					Ext.getCmp('upload-progress-bar').updateProgress(percent, ptext);
				}
			};

			var task = {
				run: Curriki.current.videoStatusRequest
				,interval: 3000
			};

			Curriki.current.videoStatusTask = Ext.TaskMgr.start(task);
		}

		AddPath.AddSubasset = function(callback){
			Curriki.assets.CreateSubasset(
				Curriki.current.drop.parentPage
				,(Curriki.current.asset&&Curriki.current.asset.assetPage)||Curriki.current.assetName
				,Curriki.current.drop.targetIndex
				,function(){
					if ("function" === typeof callback){
						callback();
					}
				}
			);
		}

		AddPath.AddFavorite = function(callback){
			Curriki.assets.CreateSubasset(
				'Coll_'+Curriki.data.user.me.username.replace('XWiki.', '')+'.Favorites'
				,Curriki.current.assetName
				,-1
				,function(){
					if ("function" === typeof callback){
						callback();
					}
				}
			);
		}

		AddPath.ShowNextDialogue = function(next, current){
			var p = Ext.ComponentMgr.create({xtype:next});
			p.show();
			Ext.ComponentMgr.register(p);

			if (!Ext.isEmpty(current)) {
				var closeDlg = Ext.getCmp(current);
				if (closeDlg){
					closeDlg.close();
				}
			}
		}

		AddPath.SourceSelected = function(selected, allValues){
			Curriki.current.selected = selected;

			var next;
			switch(selected) {
				case 'file':
					Curriki.current.fileName = allValues['filename'];
					next = 'apSRI1';
					AddPath.PostFile(function(asset){
						callback = function(){AddPath.ShowNextDialogue(next, AddPath.AddSourceDialogueId);};

						Curriki.assets.GetMetadata(asset.assetPage||Curriki.current.asset.assetPage, function(metadata){
							Curriki.current.metadata = metadata;
							callback();
						});
					});
					return;
					break;

				case 'video_upload':
				//case 'video_capture':
					next = 'apSRI1';
					// Check for "valid" extensions before continuing
					// TODO: Move this list into a translation key value
					var exts = "asf|avi|wma|wmv|flv|mov|movie|qt|mp4|mpg|mpeg|3gp|m4v|f4v|webm";
					var re = new RegExp("^.+\\.("+exts+")$", 'i');
					var pName = Ext.getCmp('video_upload-entry-box').getValue();
					if (! re.test(pName)) {
						if (!confirm(_('add.video.uploading.unknown.file.txt'))) {
							return false;
						}
					}

					AddPath.PostVideo(function(asset){
						console.log("CreateAsset (video) CB: ", asset);
						Curriki.current.asset = asset;

						Curriki.assets.CreateVIDITalk(
							asset.assetPage,
							Curriki.current.videoId,
							function(videoInfo){
								console.log("Created viditalk CB: ", videoInfo);
								callback = function(){AddPath.ShowNextDialogue(next, AddPath.AddSourceDialogueId);};
								Curriki.assets.GetMetadata(asset.assetPage, function(metadata){
									Curriki.current.metadata = metadata;
									callback();
								});
							}
						)
					});
					return;
					break;

				case 'link':
					Curriki.current.linkUrl = allValues['link'];
					next = 'apSRI1';
					Curriki.assets.CreateAsset(
						Curriki.current.parentAsset,
						Curriki.current.publishSpace,
						function(asset){
							console.log("CreateAsset (link) CB: ", asset);
							Curriki.current.asset = asset;

							Curriki.assets.CreateExternal(
								asset.assetPage,
								Curriki.current.linkUrl,
								function(linkInfo){
									console.log("Created Link CB: ", linkInfo);
									callback = function(){AddPath.ShowNextDialogue(next, AddPath.AddSourceDialogueId);};
									Curriki.assets.GetMetadata(asset.assetPage, function(metadata){
										Curriki.current.metadata = metadata;
										callback();
									});
								}
							)
						}
					);
					return;
					break;

				case 'template':
					Curriki.current.templateType = 'template';
					if (AddPath.TemplateList().size() > 1) {
						next = 'apSelectTemplate';
					} else {
						AddPath.PostToTemplate(_('add.selecttemplate.list1.url'));
						return;
					}
					break;

				case 'scratch':
					Curriki.current.templateType = 'format';
					if (AddPath.TemplateList().size() > 1) {
						next = 'apSelectTemplate';
					} else {
						AddPath.PostToTemplate(_('add.selectformat.list1.url'));
						return;
					}
					break;

				case 'folder':
					next = 'apSRI1';
					Curriki.assets.CreateAsset(
						Curriki.current.parentAsset,
						Curriki.current.publishSpace,
						function(asset){
							console.log("CreateAsset (folder) CB: ", asset);
							Curriki.current.asset = asset;

							Curriki.assets.CreateFolder(
								asset.assetPage,
								function(assetInfo){
									console.log("Created Folder CB: ", assetInfo);
									Curriki.current.flowFolder = 'Folder';
									callback = function(){AddPath.ShowNextDialogue(next, AddPath.AddSourceDialogueId);};
									Curriki.assets.GetMetadata(asset.assetPage, function(metadata){
										Curriki.current.metadata = metadata;
										callback();
									});
								}
							)
						}
					);
					return;
					break;

				case 'collection': // Only from start
					next = 'apSRI1';
					Curriki.assets.CreateAsset(
						Curriki.current.parentAsset,
						Curriki.current.publishSpace,
						function(asset){
							console.log("CreateAsset (collection) CB: ", asset);
							Curriki.current.asset = asset;

							Curriki.assets.CreateCollection(
								asset.assetPage,
								function(assetInfo){
									console.log("Created Collection CB: ", assetInfo);
									callback = function(){AddPath.ShowNextDialogue(next);};
									Curriki.assets.GetMetadata(asset.assetPage, function(metadata){
										Curriki.current.metadata = metadata;
										callback();
									});
								}
							)
						}
					);
					return;
					break;

				case 'toFavorites': // Only from start
					// Curriki.assets.{parentAsset,assetName} need to be set
					AddPath.AddFavorite(function(){
						Curriki.logView('/features/resources/favorites/'+Curriki.current.assetName.replace('.', '/'));
						AddPath.ShowDone();
					});
					return;
					break;

				case 'copy':
					// Check if the user has any personal or group collections
					// If so, determine location to put the copy
					// If not, then use old procedure
					//Curriki.data.user.GetCollections(function(){
						if (true || Curriki.data.user.collectionChildren.length > 0
							|| Curriki.data.user.groupChildren.length > 0){
							Curriki.current.flow = 'Copy2';
							next = 'apCopyLocation';
							AddPath.ShowNextDialogue(next);
						} else {
							next = 'apSRI1';
							Curriki.assets.CopyAsset(
								Curriki.current.copyOf,
								Curriki.current.publishSpace,
								function(asset){
										console.log("CopyAsset CB: ", asset);
										Curriki.current.asset = asset;
										callback = function(){AddPath.ShowNextDialogue(next);};
										Curriki.assets.GetMetadata(asset.assetPage, function(metadata){
											Curriki.current.metadata = metadata;
											callback();
										});
								}
							);
						}
					//});
					return;
					break;


				default:
					// Should never get here
					next = 'apSRI1';
					break;
			}

			if (!Ext.isEmpty(next)){
				AddPath.ShowNextDialogue(next, AddPath.AddSourceDialogueId);
			}
		};


		AddPath.start = function(path){
	// Possible ways to start:
	// 1. Add on left navigation (give list of sources)
	// 2. Add a collection link - type is predetermined
	// 3. Add a resource (same as #1)
	// 4. Make a Resource From Scratch - goto from scratch from
	// 5. Make a Resource From a Template - goto template selection
	// 6. Add (viewing resource) - list collections to add to
	// 7. Build Up (viewing resource) - 8 options, destination already set
	// 8. Favorite - source and destination determined -- just add and show final
	// 9. Add (from favorites) - list collections to add to
	// 10. Add a Resource (contributions tab) - like #1
	// 11. Add (contributions tab) - like #6
	// 12. Add a collection (collections tab) - like #2
	// 13. Build up (collections tab) - like #7
	//
	// 14. Add a Group Collection (group collections) - like #2 but group space
	// 15. Build up (group collections) - like #7 but groups
	// 16. Add a resource (view all group contributions) - like #1 but groups
	// 17. Add (view all group contributions) - like #6 but groups
	//
	// 18. Add Template - Skip type selection and just show template selection

			console.log('Starting path: ', path);

			// This should already have been handled, but do a simple test here
			if (Ext.isEmpty(Curriki.data.user.me) || 'XWiki.XWikiGuest' === Curriki.data.user.me.username){
				console.log('Not signed in:');
				window.location.href='/xwiki/bin/login/XWiki/XWikiLogin?xredirect='+window.location.href;
				return;
			}

			// Defaults
			if (Ext.isEmpty(Curriki.current.cameFrom)) {
				Curriki.current.cameFrom = window.location.href;
			}

			if (!Ext.isEmpty(Curriki.current.parentAsset)
			    && (Curriki.current.parentAsset.substr(0, 5) === 'Coll_')
			) {
				var parentSpace = Curriki.current.parentAsset.replace(/\..*$/, '');
				Curriki.current.publishSpace = parentSpace;
			}
			if (Ext.isEmpty(Curriki.current.publishSpace)) {
				Curriki.current.publishSpace = 'Coll_'+Curriki.data.user.me.username.replace(/XWiki\./, '');
			}

			if (!Ext.isEmpty(path)) {
				Curriki.current.flow = path;
			}

			var pathParts = window.location.pathname.split('/');
			var pathSize = pathParts.size();
			Curriki.current.subPath = "";
			for (i = pathSize-2; i < pathSize; i++){
				Curriki.current.subPath += "/"+pathParts[i];
			}
			Curriki.logView('/features/resources/add/'+Curriki.current.flow+Curriki.current.subPath);

			switch (Curriki.current.flow){
				// Add a resource to unknown - no parent
				//case 'A': // From "Home Page" - removed from spec
				case 'B': // Left nav
				// case 'D-Add': // About Contributing page -- Really path B
				case 'I': // Add a Resource in MyCurriki Contributions
				case 'O': // Add a Resource in Group Curriculum View All Contribs
					Curriki.ui.show('apSource');
					return;
					break;

				// About Contributing page
				case 'D':
					// D.Add - Add a Resource -- Is really path B
					Curriki.current.flow = 'B';
					Curriki.ui.show('apSource');
					return;
					break;
				case 'D1':
					// D.1   - Make a resource from scratch form
					Curriki.current.flow = 'D';
					AddPath.SourceSelected('scratch', {});
					return;
					break;
				case 'D2':
					// D.2   - Make a Lesson Plan from a Template
					Curriki.current.flow = 'R';
					AddPath.SourceSelected('template', {});
					return;
					break;

				// Add a new collection
				case 'C': // About finding and collecting page
				case 'K': // Add a Collection in MyCurriki Collections
				case 'M': // Add a group collection in Group Collections listing
					// Create collection, then SRI
					AddPath.SourceSelected('collection', {});
					return;
					break;

				// Add Known (Existing) into a Target Collection or Folder
				case 'E': // Add in view
				case 'H': // Add in MyCurriki Favorites
				case 'J': // Add in MyCurriki Contributions
				case 'P': // Add in Group Curriculum View All Contributions
					// Need titles for current and parent assets in Done Msg
					// We should be getting the current one passed in
					// Shows CTV
					console.log('Starting path:', Curriki.current.flow);
					//Curriki.data.user.GetCollections(function(){
						Curriki.ui.show('apLocation');
					//});
					return;
					break;

				// Add Known (Existing) into Favorites
				case 'G': // Favorite in view
					// Add asset as subasset in Favorites then show final dialogue
					AddPath.SourceSelected('toFavorites', {});
					return;
					break;

				// Add Unknown (New or Existing) into a Collection or Folder
				case 'F': // Build-up in view
				case 'L': // Build-up in MyCurriki Collections
				case 'N': // Build-up in Group Collections list
					// Need titles for new and parent assets in Done Msg
					// Parent known
					Curriki.ui.show('apSource', {toFolder:true, folderName:Curriki.current.parentTitle});
					return;
					break;

				// CURRIKI-2423
				// Add Template (choice already made)
				case 'R': // "Add From Template" in About Contributing page
					AddPath.SourceSelected('template', {});
					return;
					break;

				case 'Copy': // Copy an existing resource and allow metadata change
					AddPath.SourceSelected('copy', {});
					return;
					break;
			}
		}

		Curriki.module.addpath.initialized = true;
	}
};

Curriki.module.addpath.startPath = function(path, options){
	Curriki.module.addpath.initAndStart(function(){
		Curriki.module.addpath.start(path);
	}, options);
}

Curriki.module.addpath.startDoneMessage = function(options){
	Curriki.module.addpath.initAndStart(function(){
		Curriki.module.addpath.ShowDone();
	}, options);
}

Curriki.module.addpath.initAndStart = function(fcn, options){
	// parentTitle needs to be passed for E, H, J, P, F, N, and L
	// assetTitle needs to be passed for E, H, J, P (known asset)

	var current = Curriki.current;
	if (!Ext.isEmpty(options)){
		current.assetName = options.assetName||current.assetName;
		current.parentAsset = options.parentAsset||current.parentAsset;
		current.publishSpace = options.publishSpace||current.publishSpace;
		current.copyOf = options.copyOf||current.copyOf;
		current.cameFrom = options.cameFrom||current.cameFrom;

		current.assetTitle = options.assetTitle||current.assetTitle;
		current.assetType = options.assetType||current.assetType;
		current.parentTitle = options.parentTitle||current.parentTitle;
		current.copyOfTitle = options.copyOfTitle||current.copyOfTitle;
		current.copyOfAssetType = options.copyOfAssetType||current.copyOfAssetType;
	}

	Ext.ns('Curriki.settings');
	Curriki.settings.localCollectionFetch = true;

	Curriki.init(function(){
		if (Ext.isEmpty(Curriki.data.user.me) || 'XWiki.XWikiGuest' === Curriki.data.user.me.username){
			window.location.href='/xwiki/bin/login/XWiki/XWikiLogin?xredirect='+window.location.href;
			return;
		}

		Curriki.module.addpath.init();

		var startFn = function(){
			fcn();
		}

		var parentFn;
		if (!Ext.isEmpty(current.parentAsset)
		    && Ext.isEmpty(current.parentTitle)) {
			// Get parent asset info
			parentFn = function(){
				Curriki.assets.GetAssetInfo(current.parentAsset, function(info){
					Curriki.current.parentTitle = info.title;
					startFn();
				});
			}
		} else {
			parentFn = function(){
				startFn();
			};
		}

		var copyOfFn;
		if (!Ext.isEmpty(current.copyOf)
		    && (Ext.isEmpty(current.copyOfTitle)
		        || Ext.isEmpty(current.copyOfAssetType))) {
			// Get parent asset info
			copyOfFn = function(){
				Curriki.assets.GetAssetInfo(current.copyOf, function(info){
					Curriki.current.copyOfTitle = info.title;
					Curriki.current.copyOfAssetType = info.assetType;
					parentFn();
				});
			}
		} else {
			copyOfFn = function(){
				parentFn();
			};
		}

		var currentFn;
		if (!Ext.isEmpty(current.assetName)
		    && (Ext.isEmpty(current.assetTitle)
		        || Ext.isEmpty(current.assetType))) {
			// Get asset info
			currentFn = function(){
				Curriki.assets.GetAssetInfo(current.assetName, function(info){
					Curriki.current.assetTitle = info.title;
					Curriki.current.assetType = info.assetType;
					copyOfFn();
				});
			}
		} else {
			currentFn = function(){
				copyOfFn();
			};
		}

		currentFn();
	});
}

Curriki.module.addpath.loaded = true;

// Initialize "current" information
Ext.ns('Curriki.current');
Curriki.current = {
	init:function(){
		Ext.apply(this, {
			 assetName:null
			,parentAsset:null
			,publishSpace:null
			,copyOf:null
			,cameFrom:null
			,flow:null
			,flowFolder:''

			,assetTitle:null
			,assetType:null
			,parentTitle:null
			,copyOfTitle:null
			,copyOfAssetType:null

			,asset:null
			,metadata:null

			,selected:null
			,fileName:null
			,uuid:null
			,videoId:null
			,linkUrl:null

			,sri1:null
			,sri1fillin:null
			,sri2fillin:null

			,submitToTemplate:null

			,templateType:null

			,drop:null
		});
	}
}
Curriki.current.init();
