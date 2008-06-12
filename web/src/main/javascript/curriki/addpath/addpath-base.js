// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

Ext.ns('Curriki.module.addpath');
Curriki.module.addpath.init = function() {
	// Local alias
	var AddPath = Curriki.module.addpath;

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
			if (entry_box.isVisible() != checked) {
				entry_box.setVisibilityMode(Ext.Element.DISPLAY).setVisible(checked);
			}
		}

		entry_box = Ext.getCmp(e.value+'-entry-value');
		if (change && entry_box) {
			// Has a value component
			entry_box.setDisabled(!checked);
		}

		Ext.getCmp(AddPath.AddSourceDialogueId).syncShadow();
	}


	AddPath.AddSourceDialogueId = 'resource-source-dialogue';
	AddPath.AddSource = Ext.extend(Curriki.ui.dialog.Actions, {
		  initComponent:function(){
			Ext.apply(this, {
				 closeAction:'hide'
				,title:_('add.contributemenu.title_addto_'+(this.toFolder?'composite':'site'))
				,cls:'resource resource-add'
				,id:AddPath.AddSourceDialogueId
				,items:[{
					 xtype:'form'
					,id:'addDialoguePanel'
					,formId:'addDialogueForm'
					,labelWidth:25
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
								 fn: function(){this.close();}
								,scope:this
							}
						}
					},{
						 text:_('add.contributemenu.next.button')
						,id:'nextbutton'
						,cls:'button next'
						,disabled:true
						,listeners:{
							click:{
								 fn: function(){
									var form = this.findByType('form')[0].getForm();
									var selected = (form.getValues(false))['assetSource'];
									AddPath.SourceSelected(selected, form.getValues(false));
								}
								,scope:this
							}
						}
					}]
					,monitorValid:true
					,listeners:{
						clientvalidation:function(panel, valid){
							if (valid) {
								AddPath.EnableNext();
							} else {
								AddPath.DisableNext();
							}
						}
					}
					,items:[{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,html:this.toFolder
								?_('add.contributemenu.guidingquestion_addto_composite', this.folderName)
								:_('add.contributemenu.guidingquestion_addto_site')
							,cls:'guidingquestion'
						}
	// Something had
					},{
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
						,listeners:{
							check:AddPath.RadioSelect
						}
					},{
						 xtype:'textfield'
						,inputType:'file'
						,id:'file-entry-box'
						,name:'filepath'
						,disabled:true
						,allowBlank:false
						,hideMode:'display'
						,hideLabel:true
						,hidden:true
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
						,hideLabel:true
						,hidden:true
						,disabled:true

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
						}
						,hidden:true
						,items:[{
							 xtype:'textfield'
							,id:'video_upload-entry-value'
							,allowBlank:false
							,hidden:true
							,disabled:true
						},{
							 xtype:'box'
							,id:'video_upload-entry-video'
							,autoEl:{
								 tag:'div'
								,html:''
							}
						}]
						,autoEl:{
							 tag:'div'
							,id:'video_upload-container'
							,html:''
						}

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
						 xtype:'textfield'
						,id:'link-entry-box'
						,name:'link'
						,emptyText:_('add.contributemenu.link.empty_msg')
						,disabled:true
						,allowBlank:false
						,hideMode:'display'
						,hideLabel:true
						,hidden:true
						,vtype:'url'

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
						,checked:true
						,boxLabel:_('add.contributemenu.option.template')

	// Create "from scratch"
					},{
						 xtype:'radio'
						,value:'scratch'
						,inputValue:'scratch'
						,boxLabel:_('add.contributemenu.option.scratch')

	// Create with VIDITalk
					},{
						 xtype:'radio'
						,value:'video_capture'
						,inputValue:'video_capture'
						,boxLabel:_('add.contributemenu.option.video_capture')
						,listeners:{
							check:AddPath.RadioSelect
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
						}
						,hidden:true
						,items:[{
							 xtype:'textfield'
							,id:'video_capture-entry-value'
							,allowBlank:false
							,hidden:true
							,disabled:true
						},{
							 xtype:'box'
							,id:'video_capture-entry-video'
							,autoEl:{
								 tag:'div'
								,html:''
							}
						}]
						,autoEl:{
							 tag:'div'
							,id:'video_capture-container'
							,html:''
						}

	// Create folder
					},{
						 xtype:'radio'
						,value:'folder'
						,inputValue:'folder'
						,boxLabel:_('add.contributemenu.option.folder')
						,hidden:!this.toFolder
						,hideLabel:!this.toFolder
						,hideParent:true
					}]
				}]
			});

			AddPath.AddSource.superclass.initComponent.call(this);
		}
	});
	Ext.reg('apSource', AddPath.AddSource);



	AddPath.TemplateList = function() {
		var retVal = [];

		var i = 1;
		while(_('add.selecttemplate.list'+i+'.header') !== 'add.selecttemplate.list'+i+'.header'){
			var tpl = [];
			tpl.push({
				 xtype:'radio'
				,name:'templateName'
				,value:'list'+i
				,boxLabel:_('add.selecttemplate.list'+i+'.header')
				,listeners:{
					check:AddPath.TemplateSelect
				}
			});
			tpl.push({
				 xtype:'box'
				,autoEl:{
					 tag:'div'
					,html:_('add.selecttemplate.list'+i+'.description')
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

		return retVal;
	};

	AddPath.TemplateSelect = function(radio, selected) {
		if (selected) {
			Ext.get('selecttemplate-thumbnail-header').dom.innerHTML = _('add.selecttemplate.'+radio.value+'.header')
			Ext.get('selecttemplate-thumbnail-image').set({'src': _('add.selecttemplate.'+radio.value+'.thumbnail')});
			Curriki.current.submitToTemplate = _('add.selecttemplate.'+radio.value+'.url');
			Ext.getCmp('nextbutton').enable();
		}
	};

	AddPath.SelectTemplate = Ext.extend(Curriki.ui.dialog.Actions, {
		  initComponent:function(){
			Ext.apply(this, {
				 id:'SelectTemplateDialogueWindow'
				,closeAction:'hide'
				,title:_('add.selecttemplate.title')
				,cls:'resource resource-add'
				,items:[{
					 xtype:'form'
					,id:'SelectTemplateDialoguePanel'
					,formId:'SelectTemplateDialogueForm'
					,labelWidth:25
					,defaults:{
						 labelSeparator:''
					}
					,buttonAlign:'right'
					,buttons:[{
						 text:_('add.selecttemplate.cancel.button')
						,id:'cancelbutton'
						,cls:'button cancel'
						,listeners:{
							'click':function(e, ev){
								Ext.getCmp(e.id).ownerCt.ownerCt.close();
							}
						}
					},{
						 text:_('add.selecttemplate.next.button')
						,id:'nextbutton'
						,cls:'button next'
						,disabled:true
						,listeners:{
							'click':function(e, ev){
								AddPath.PostToTemplate(Curriki.current.submitToTemplate);
								Ext.getCmp(e.id).ownerCt.ownerCt.close();
							}
						}
					}]
					,items:[{
						 layout:'column'
						,defaults:{border:false}
						,items:[{
							 columnWidth:0.6
							,items:[{
								 xtype:'box'
								,autoEl:{
									 tag:'div'
									,html:_('add.selecttemplate.guidingquestion')
									,cls:'guidingquestion'
								}
							},{
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
							 columnWidth:0.4
							,items:[{
								 xtype:'box'
								,id:'selecttemplate-thumbnail-container'
								,anchor:''
								,autoEl:{
									 tag:'div'
									,id:'selecttemplate-thumbnail'
									,style:'margin:8px 0 8px 10px'
									,children:[{
										 tag:'div'
										,id:'selecttemplate-thumbnail-header'
										,style:'margin:0 0 4px 0'
										,html:''
									},{
										 tag:'img'
										,id:'selecttemplate-thumbnail-image'
										,src:Ext.BLANK_IMAGE_URL
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
				 closeAction:'hide'
				,title:_('add.setrequiredinfo.part1.title')
				,cls:'resource resource-add'
				,items:[{
					 xtype:'form'
					,id:'MetadataDialoguePanel'
					,formId:'MetadataDialogueForm'
					,labelWidth:25
					,defaults:{
						 labelSeparator:''
					}
					,bbar:['->',{
						 text:_('add.setrequiredinfo.part1.cancel.button')
						,id:'cancelbutton'
						,cls:'button cancel'
						,listeners:{
							click:{
								 fn: function(){this.close();}
								,scope:this
							}
						}
					},{
						 text:_('add.setrequiredinfo.part1.next.button')
						,id:'nextbutton'
						,cls:'button next'
						,disabled:true
						,listeners:{
							click:{
								 fn: function(){
									var form = this.findByType('form')[0].getForm();
									Curriki.current.sri1 = form.getValues(false);
									Curriki.current.sri1.fw_items = this.findByType('treepanel')[0].getChecked('id');

									this.close();

									var p = Ext.ComponentMgr.create({'xtype':'apSRI2'});
									p.show();
									Ext.ComponentMgr.register(p);
								}
								,scope:this
							}
						}
					}]
					,monitorValid:true
					,listeners:{
						clientvalidation:function(panel, valid){
							if (valid) {
								AddPath.EnableNext();
							} else {
								AddPath.DisableNext();
							}
						}
					}
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

	// Title
					},{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,id:'metadata-title'
							,cls:'information-header'
							,children:[{
								 tag:'span'
								,id:'metadata-title-required'
								,html:_('form.required.fields.indicator')
							},{
								 tag:'span'
								,id:'metadata-title-title'
								,html:_('sri.title_title')
							},{
								 tag:'img'
								,id:'metadata-title-info'
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
						,hideLabel:true
						,width:'80%'

	// Description
					},{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,id:'metadata-description'
							,cls:'information-header'
							,children:[{
								 tag:'span'
								,id:'metadata-description-required'
								,html:_('form.required.fields.indicator')
							},{
								 tag:'span'
								,id:'metadata-description-title'
								,html:_('sri.description_title')
							},{
								 tag:'img'
								,id:'metadata-description-info'
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
						,emptyText:_('sri.description_content')
						,allowBlank:false
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
									,cls:'information-header'
									,children:[{
										 tag:'span'
										,id:'metadata-fw_items-required'
										,html:_('form.required.fields.indicator')
									},{
										 tag:'span'
										,id:'metadata-fw_items-title'
										,html:_('sri.fw_items_title')
									},{
										 tag:'img'
										,id:'metadata-fw_items-info'
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
								,minValue:1
								,invalidText:'TODO: TRANSLATE: This field is required'
								,hidden:true
								,listeners:{
									 valid:function(field){
										var fieldset = Ext.getCmp('fw_items-tree');
										fieldset.removeClass('x-form-invalid');
										fieldset.el.dom.qtip = '';
									}
									,invalid:function(field, msg){
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
							,Curriki.ui.component.asset.fwTree
							]

	// Educational Level
						},{
							 columnWidth:0.5
							,items:[{
								 xtype:'box'
								,autoEl:{
									 tag:'div'
									,id:'metadata-educational_level2'
									,cls:'information-header'
									,children:[{
										 tag:'span'
										,id:'metadata-educational_level2-required'
										,html:_('form.required.fields.indicator')
									},{
										 tag:'span'
										,id:'metadata-educational_level2-title'
										,html:_('sri.educational_level2_title')
									},{
										 tag:'img'
										,id:'metadata-educational_level2-info'
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
								}
							},{
								 xtype:'fieldset'
								,id:'educational_level2-set'
								,border:false
								,autoHeight:true
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

	// Instructional Component Type
					},{
						 xtype:'box'
						,autoEl:{
							 tag:'div'
							,id:'metadata-instructional_component2'
							,cls:'information-header'
							,children:[{
								 tag:'span'
								,id:'metadata-instructional_component2-required'
								,html:_('form.required.fields.indicator')
							},{
								 tag:'span'
								,id:'metadata-instructional_component2-title'
								,html:_('sri.instructional_component2_title')
							},{
								 tag:'img'
								,id:'metadata-instructional_component2-info'
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
				,closeAction:'hide'
				,title:_('add.setrequiredinfo.part2.title')
				,cls:'resource resource-add'
				,items:[{
					 xtype:'form'
					,id:'MetadataDialoguePanel'
					,formId:'MetadataDialogueForm'
					,labelWidth:25
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
							}
						}
					},{
						 text:_('add.setrequiredinfo.part2.next.button')
						,id:'nextbutton'
						,cls:'button next'
						,disabled:true
						,listeners:{
							click:{
								 fn: function(){
									var form = this.findByType('form')[0].getForm();
									Curriki.current.sri2 = form.getValues(false);

									this.close();

									AddPath.MetadataFinished();
								}
								,scope:this
							}
						}
					}]
					,monitorValid:true
					,listeners:{
						clientvalidation:function(panel, valid){
							if (valid) {
								AddPath.EnableNext();
							} else {
								AddPath.DisableNext();
							}
						}
					}
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

	// Access Privileges
						},{
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
										,html:_('sri.rights_title')
									},{
										 tag:'img'
										,id:'metadata-rights-info'
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
								,defaults:{
									 xtype:'radio'
									,name:'rights'
								}
								,items:Curriki.data.rights.data
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
										,html:_('sri.hidden_from_search_title')
									},{
										 tag:'img'
										,id:'metadata-hidden_from_search-info'
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
										,html:_('sri.keywords_title')
									},{
										 tag:'img'
										,id:'metadata-keywords-info'
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
								,hideLabel:true
								,width:'60%'
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
										,html:_('sri.language_title')
									},{
										 tag:'img'
										,id:'metadata-language-info'
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
							}]

	// Rights Holder(s)
						},{
							 border:false
							,items:[{
								 xtype:'box'
								,autoEl:{
									 tag:'div'
									,id:'metadata-right_holder'
									,cls:'information-header'
									,children:[{
										 tag:'span'
										,id:'metadata-title-required'
										,html:_('form.required.fields.indicator')
									},{
										 tag:'span'
										,id:'metadata-right_holder-title'
										,html:_('sri.right_holder_title')
									},{
										 tag:'img'
										,id:'metadata-right_holder-info'
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
								,width:'60%'
							}]

	// Licence Deed
						},{
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
										,html:_('sri.license_type_title')
									},{
										 tag:'img'
										,id:'metadata-license_type-info'
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

						AddPath.ShowDone();
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

		pageName = Curriki.current.asset.assetPage;

		disabled = false;

		switch(linkName) {
			case 'view':
				link = '/xwiki/bin/view/'+pageName.replace('.', '/');
				break;

			case 'add':
				// TODO:  ONLY show if the user has a collection to add to

				// Start Content Tree View screen
				if (Curriki.data.user.collectionChildren.length > 0
				    || Curriki.data.user.groupChildren.length > 0){
					handler = function(e,evt){
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

			case 'openbuilder':
				link = '/xwiki/bin/view/GWT/Editor?xpage=plain&page='+pageName+'&mode=edit';
				break;

			case 'viewtarget':
				link = '/xwiki/bin/view/'+pageName.replace('.', '/');
				text = _('add.finalmessage.viewtarget.link', Curriki.current.asset.title);
				break;

			case 'continue':
				// TODO: ???
				link = '';
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
			 text:_('add.finalmessage.'+linkName+'.link')
			,cls:'button link'
			,handler:handler
			,hidden:disabled
		};
	}

	AddPath.DoneMessage = function(name){
		return {
			 xtype:'box'
			,autoEl:{
				 tag:'div'
				,cls:'done-message'
				,html:_('add.finalmessage.text_'+name+'_success')
			}
		};
	}

	AddPath.CloseDone = function(dialog){
		// TODO: We need to have this go back to where we came from
		return {
				 text:_('add.finalmessage.close.button')
				,id:'closebutton'
				,cls:'button cancel'
				,listeners:{
					'click':{
						 fn:function(){this.close();}
						,scope:dialog
					}
				}
		};
	}

	AddPath.DoneA = Ext.extend(Curriki.ui.dialog.Messages, {
		  initComponent:function(){
			Ext.apply(this, {
				 closeAction:'hide'
				,title:_('add.finalmessage.title_resource')
				,cls:'resource resource-add'
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


	AddPath.DoneC = Ext.extend(Curriki.ui.dialog.Messages, {
		  initComponent:function(){
			Ext.apply(this, {
				 closeAction:'hide'
				,title:_('add.finalmessage.title_collection')
				,cls:'resource resource-add'
				,bbar:[
					 AddPath.FinalLink('openbuilder'),'-'
					,AddPath.FinalLink('collections'),'->'
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
				 closeAction:'hide'
				,title:_('add.finalmessage.title_successful')
				,cls:'resource resource-add'
				,bbar:[
					 AddPath.FinalLink('viewtarget'),'->'
					,AddPath.CloseDone(this)
				]
				,items:[
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
				 closeAction:'hide'
				,title:_('add.finalmessage.title_successful')
				,cls:'resource resource-add'
				,bbar:[
					'->',AddPath.CloseDone(this)
				]
				,items:[
// TODO: This needs arguments for "added {0} into {1}"
					 AddPath.DoneMessage('addto')
				]
			});
			AddPath.DoneF.superclass.initComponent.call(this);
		}
	});
	Ext.reg('apDoneF', AddPath.DoneF);
	Ext.reg('apDoneN', AddPath.DoneF);
	Ext.reg('apDoneL', AddPath.DoneF);

	// TODO: F, N, and L alternate here

	AddPath.DoneFFolder = Ext.extend(Curriki.ui.dialog.Messages, {
		  initComponent:function(){
			Ext.apply(this, {
				 closeAction:'hide'
				,title:_('add.finalmessage.title_folder')
				,cls:'resource resource-add'
				,bbar:[
					 '->',AddPath.FinalLink('continue')
				]
				,items:[
// TODO: This needs arguments for "added {0} into {1}"
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
				 closeAction:'hide'
				,title:_('add.finalmessage.title_folder')
				,cls:'resource resource-add'
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
				 closeAction:'hide'
				,title:_('add.finalmessage.title_collection')
				,cls:'resource resource-add'
				,bbar:[
					 '->',AddPath.FinalLink('continue')
				]
				,items:[
					 AddPath.DoneMessage('collection')
//TODO: Need to add collection_tip1 and 2 here
				]
			});
			AddPath.DoneK.superclass.initComponent.call(this);
		}
	});
	Ext.reg('apDoneK', AddPath.DoneK);

	AddPath.DoneM = Ext.extend(Curriki.ui.dialog.Messages, {
		  initComponent:function(){
			Ext.apply(this, {
				 closeAction:'hide'
				,title:_('add.finalmessage.title_collection')
				,cls:'resource resource-add'
				,bbar:[
					 '->',AddPath.FinalLink('continue')
				]
				,items:[
					 AddPath.DoneMessage('groupcollection')
//TODO: Need to add groupcollection_tip1 and 2 here
				]
			});
			AddPath.DoneM.superclass.initComponent.call(this);
		}
	});
	Ext.reg('apDoneM', AddPath.DoneM);

	AddPath.DoneI = Ext.extend(Curriki.ui.dialog.Messages, {
		  initComponent:function(){
			Ext.apply(this, {
				 closeAction:'hide'
				,title:_('add.finalmessage.title_resource')
				,cls:'resource resource-add'
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



	AddPath.ShowDone = function(){
		var p = Ext.ComponentMgr.create({
			 xtype:'apDone'+Curriki.current.flow
			,id:'done-dialogue'
		});
		p.show();
		Ext.ComponentMgr.register(p);
	}




	AddPath.ChooseLocation = Ext.extend(Curriki.ui.dialog.Messages, {
		  initComponent:function(){
			Ext.apply(this, {
				 id:'ChooseLocationDialogueWindow'
				,closeAction:'hide'
				,title:_('add.chooselocation.title')
				,cls:'resource resource-add'
				,items:[{
					 xtype:'form'
					,id:'ChooseLocationDialoguePanel'
					,formId:'ChooseLocationDialogueForm'
					,labelWidth:25
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
									});
								}
								,scope:this
							}
						}
					}]
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

	// TODO: DRAG BOX
					},{
						 xtype:'container'
						,id:'resource-pickup'
						,items:[{
							 xtype:'box'
							,id:'resource-pickup-title'
							,autoEl:{
								 tag:'div'
								,html:'TRANS: Click to drag this text'
							}
						},{
							 xtype:'treepanel'
							,loader: new Curriki.ui.treeLoader.Base()
							,id:'ctv-from-tree-cmp'
							,useArrows:true
							,autoScroll:true
							,border:false
							,cls:'ctv-from-tree'
							,animate:true
							,enableDrag:true
							,containerScroll:true
							,rootVisible:false
							,root: new Ext.tree.AsyncTreeNode({
								 text:_('ROOT - Unshown')
								,id:'ctv-drag-tree-root'
								,cls:'ctv-drag-root'
								,leaf:false
								,allowDrag:false
								,allowDrop:false
								,loaded:true
								,expanded:true
								,children:[{
									 text:Curriki.current.asset.title
									,id:'ctv-target-node'
									,assetName:Curriki.current.asset.assetPage
									,cls:'ctv-target ctv-resource resource-'+Curriki.current.asset.assetType
									,leaf:true
									,loaded:true
								}]
							})
						}]
						,autoEl:{
							 tag:'div'
							,id:'resource-pickup-box'
							,html:''
						}

	// TODO: DROP TREE
					},{
						 xtype:'container'
						,id:'resource-drop'
						,items:[{
							 xtype:'treepanel'
							,loader: new Curriki.ui.treeLoader.Base()
							,id:'ctv-to-tree-cmp'
							,useArrows:true
							,autoScroll:true
							,border:false
							,cls:'ctv-to-tree'
							,animate:true
							,enableDD:true
							,containerScroll:true
							,rootVisible:false
							,listeners:{
								nodedrop:{
									fn: function(dropEvent){
										var targetNode = Ext.getCmp('ctv-to-tree-cmp').getNodeById('ctv-target-node');
										var parentNode = targetNode.parentNode;
										var parentNodeId = parentNode.id;
										var nextSibling = targetNode.nextSibling;
										var targetIndex = -1;
										if (nextSibling){
											targetIndex = nextSibling.attributes.order||-1;
										}
										Curriki.current.drop = {
											 parentPage:parentNodeId
											,targetIndex:targetIndex
										};
										AddPath.EnableNext();
									}
									,scope:this
								}
							}
							,root: new Ext.tree.AsyncTreeNode({
								 text:_('ROOT - Unshown')
								,id:'ctv-drop-tree-root'
								,cls:'ctv-drop-root'
								,leaf:false
								,allowDrag:false
								,allowDrop:false
								,loaded:true
								,expanded:true
								,children:[
									Curriki.data.user.collectionChildren.length>0?{
									 text:_('panels.myCurriki.myCollections')
									,id:'ctv-drop-tree-collection-root'
									,cls:'ctv-top ctv-header ctv-collections'
									,leaf:false
									,allowDrag:false
									,allowDrop:false
									,loaded:true
									,expanded:(Curriki.data.user.collectionChildren.length < 4)
									,children:Curriki.data.user.collectionChildren
								}:{},
									Curriki.data.user.groupChildren.length>0?{
									 text:_('panels.myCurriki.myGroups')
									,id:'ctv-drop-tree-group-root'
									,cls:'ctv-top ctv-header ctv-groups'
									,leaf:false
									,allowDrag:false
									,allowDrop:false
									,loaded:true
									,expanded:(Curriki.data.user.groupChildren.length < 4)
									,children:Curriki.data.user.groupChildren
								}:{}
								]
							})
						}]
						,autoEl:{
							 tag:'div'
							,id:'resource-drop-box'
							,html:''
						}

					}]
				}]
			});
			AddPath.ChooseLocation.superclass.initComponent.call(this);
		}
	});
	Ext.reg('apLocation', AddPath.ChooseLocation);

	AddPath.PostToTemplate = function(templateUrl){
		Curriki.assets.CreateAsset(Curriki.current.parentAsset, function(asset){
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
					,{xtype:'hidden', name:'parentPage', value:Curriki.current.asset.parentAsset}
				]
			});
			sf.submit();

			var sourceDlg = Ext.getCmp(AddPath.AddSourceDialogueId);
			if (sourceDlg){
				sourceDlg.close();
			}
		});
	}

	AddPath.PostFile = function(callback){
		Curriki.assets.CreateAsset(Curriki.current.parentAsset, function(asset){
			Curriki.current.asset = asset;

			Ext.Ajax.request({
				url:'/xwiki/bin/upload/'+asset.assetPage.replace('.', '/')
				,isUpload:true
				,form:'addDialogueForm'
				,headers: {
					'Accept':'application/json'
				}
				,callback:function(options, success, response){
console.log('upload CB:', options, success, response);
					var sourceDlg = Ext.getCmp(AddPath.AddSourceDialogueId);
					if (sourceDlg){
						sourceDlg.close();
					}
					callback();
				}
			});
		});
	}

	AddPath.AddSubasset = function(callback){
		Curriki.assets.CreateSubasset(
			Curriki.current.drop.parentPage
			,Curriki.current.asset.assetPage
			,Curriki.current.drop.targetIndex
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

		var closeDlg = Ext.getCmp(current);
		if (closeDlg){
			closeDlg.close();
		}
	}

	AddPath.SourceSelected = function(selected, allValues){
		Curriki.current.selected = selected;

		var next;
		switch(selected) {
			case 'file':
				Curriki.current.fileName = allValues['filename'];
				next = 'apSRI1';
				AddPath.PostFile(function(){
					AddPath.ShowNextDialogue(next, AddPath.AddSourceDialogueId);
				});
				return;
				break;

			case 'video_upload':
			case 'video_capture':
				Curriki.current.videoId = allValues[selected+'-entry-value'];

				next = 'apSRI1';
				Curriki.assets.CreateAsset(
					Curriki.current.parentAsset,
					function(asset){
console.log("CreateAsset (video) CB: ", asset);
						// Initial asset has been created
						// 1. Add appropriate content (link, file, ...)
						// 2. Go to next step
						Curriki.current.asset = asset;

						Curriki.assets.CreateVIDITalk(
							asset.assetPage,
							Curriki.current.videoId,
							function(videoInfo){
console.log("Created viditalk CB: ", videoInfo);
								AddPath.ShowNextDialogue(next, AddPath.AddSourceDialogueId);
							}
						)
					}
				);
				return;
				break;

			case 'link':
				Curriki.current.linkUrl = allValues['link'];
				next = 'apSRI1';
				Curriki.assets.CreateAsset(
					Curriki.current.parentAsset,
					function(asset){
console.log("CreateAsset (link) CB: ", asset);
						// Initial asset has been created
						// 1. Add appropriate content (link, file, ...)
						// 2. Go to next step
						Curriki.current.asset = asset;

						Curriki.assets.CreateExternal(
							asset.assetPage,
							Curriki.current.linkUrl,
							function(linkInfo){
console.log("Created Link CB: ", linkInfo);
								AddPath.ShowNextDialogue(next, AddPath.AddSourceDialogueId);
							}
						)
					}
				);
				return;
				break;

			case 'repository':
				break;

			case 'template':
				if (AddPath.TemplateList().size() > 1) {
					next = 'apSelectTemplate';
				} else {
					AddPath.PostToTemplate(_('add.selecttemplate.list1.url'));
					return;
				}
				break;

			case 'scratch':
				AddPath.PostToTemplate(_('form.scratch.url'));
				return;
				break;

			case 'folder':
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


	AddPath.start = function(){
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

		// This should already have been handled, but do a simple test here
		if (Ext.isEmpty(Curriki.data.user.me) || 'XWiki.XWikiGuest' === Curriki.data.user.me.username){
			alert(_('createresources.needaccount', '/xwiki/bin/login/XWiki/XWikiLogin'));
			return;
		}

		// Defaults
		Curriki.current.cameFrom = window.location.href;
		Curriki.current.publishSpace = 'Coll_'+Curriki.data.user.me.username.replace(/.*\./, '');

		switch (Curriki.current.flow){
			case 'A':
				break;

			default:
				// Follow "A" path
				Curriki.current.flow = 'A';
				Curriki.ui.show('apSource');
				break;
		}
	}

	Curriki.module.addpath.initialized = true;
};
Curriki.module.addpath.loaded = true;

// Initialize "current" information
Ext.ns('Curriki.current');
Curriki.current = {
	init:function(){
		Ext.apply(this, {
			 assetName:null
			,parentAsset:null
			,publishSpace:null
			,cameFrom:null
			,flow:null
			,asset:null

			,selected:null
			,fileName:null
			,videoId:null
			,linkUrl:null

			,sri1:null
			,sri2:null

			,submitToTemplate:null

			,drop:null
		});
	}
}
Curriki.current.init();
