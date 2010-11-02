// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

Ext.ns('Curriki.ui');
Curriki.ui.InfoImg = '/xwiki/skins/curriki8/icons/exclamation.png';

Ext.ns('Curriki.ui.dialog');
Curriki.ui.dialog.Base = Ext.extend(Ext.Window, {
	 title:_('Untitled')
	,border:false
	,modal:true
	,width:634
	,minWidth:400
	,minHeight:100
	,maxHeight:575
	,autoScroll:false
	,constrain:true
	,collapsible:false
	,closable:false
	,resizable:false
	,shadow:false
	,defaults:{border:false}
	,listeners:{
		afterlayout:function(wnd, layout){
			if (this.afterlayout_maxheight) {
				// Don't collapse again
			} else {
				if (wnd.getBox().height > wnd.maxHeight){
					wnd.setHeight(wnd.maxHeight);
					wnd.center();
					this.afterlayout_maxheight = true;
				} else {
					wnd.setHeight('auto');
				}
			}
		}
	}
	,initComponent:function(){
		Curriki.ui.dialog.Base.superclass.initComponent.call(this);
	}
});

Curriki.ui.dialog.Actions = Ext.extend(Curriki.ui.dialog.Base, {
	 width:634
	,initComponent:function(){
		Curriki.ui.dialog.Actions.superclass.initComponent.call(this);
	}
});
Ext.reg('dialogueactions', Curriki.ui.dialog.Actions);

Curriki.ui.dialog.Messages = Ext.extend(Curriki.ui.dialog.Base, {
	 width:500
	,initComponent:function(){
		Curriki.ui.dialog.Messages.superclass.initComponent.call(this);
	}
});
Ext.reg('dialoguemessages', Curriki.ui.dialog.Messages);

Curriki.ui.show = function(xtype, options){
	var o = {xtype:xtype};
	Ext.apply(o,options);
	var p = Ext.ComponentMgr.create(o);

	p.show();
	Ext.ComponentMgr.register(p);
}

Ext.ns('Curriki.ui.treeLoader');
Curriki.ui.treeLoader.Base = function(config){
	Curriki.ui.treeLoader.Base.superclass.constructor.call(this);
};

Ext.extend(Curriki.ui.treeLoader.Base, Ext.tree.TreeLoader, {
		dataUrl:'DYNAMICALLY DETERMINED'
		,setChildHref:false
		,setFullRollover:false
		,setAllowDrag:false
		,setUniqueId:false
		,disableUnviewable:true
		,hideUnviewable:false
		,hideInvalid:false
		,setTitleInRollover:false
		,truncateTitle:false
		,unviewableText:_('add.chooselocation.resource_unavailable')
		,unviewableQtip:_('add.chooselocation.resource_unavailable_tooltip')
		,createNode:function(attr){
//console.log('createNode: ',attr);
			if (this.setFullRollover) {
				if ('string' !== Ext.type(attr.qtip)
					&& 'string' === Ext.type(attr.description)
					&& 'array' === Ext.type(attr.fwItems)
					&& 'array' === Ext.type(attr.levels)
					&& 'array' === Ext.type(attr.ict)
				) {
					var desc = attr.description||'';
					desc = Ext.util.Format.stripTags(desc);
					desc = Ext.util.Format.ellipsis(desc, 256);
					desc = Ext.util.Format.htmlEncode(desc);

					var lastUpdated = attr.lastUpdated||'';

					var title = attr.displayTitle||attr.title;

					var fw = Curriki.data.fw_item.getRolloverDisplay(attr.fwItems||[]);
					var lvl = Curriki.data.el.getRolloverDisplay(attr.levels||[]);
					var ict = Curriki.data.ict.getRolloverDisplay(attr.ict||[]);
					

					var qTipFormat = '';

					// Show Title if desired
					if (this.setTitleInRollover) {
						qTipFormat = '{1}<br />{0}<br /><br />';
					}

					// Add Description
					qTipFormat = qTipFormat+'{3}<br />{2}<br /><br />';

					// Add lastUpdated if available
					if (lastUpdated !== '') {
						qTipFormat = qTipFormat+'{11}<br />{10}<br /><br />';
					}

					// Base qTip (framework, ed levels, ict)
					qTipFormat = qTipFormat+'{5}<br />{4}<br />{7}<br />{6}<br />{9}<br />{8}';

					attr.qtip = String.format(qTipFormat
						,title,_('global.title.popup.title')
						,desc,_('global.title.popup.description')
						,fw,_('global.title.popup.subject')
						,lvl,_('global.title.popup.educationlevel')
						,ict,_('global.title.popup.ict')
						,lastUpdated,_('global.title.popup.last_updated')
					);
				}
			}

			if ('string' === typeof attr.id) {
				var p = Curriki.ui.treeLoader.Base.superclass.createNode.call(this, attr);
				if (this.truncateTitle !== false) {
					p.setText(Ext.util.Format.ellipsis(p.text, Ext.num(this.truncateTitle, 125)));
				}
//console.log('createNode: parent', p);
				return p;
			}

			attr.pageName = attr.assetpage||attr.collectionPage;

			if (attr.assetType == 'Protected') {
				attr.category = 'unknown';
				attr.subcategory = 'protected';
			}

			// Make sure we have a category and subcategory
			attr.category = Ext.value(attr.category, "unknown");
			attr.subcategory = Ext.value(attr.subcategory, "unknown");

			var childInfo = {
				 id:this.setUniqueId?Curriki.id(attr.pageName):attr.pageName
				,text:attr.displayTitle||attr.title
				,qtip:attr.qtip||attr.description
				,cls:String.format('resource-{0} category-{1} subcategory-{1}_{2}', attr.assetType, attr.category, attr.subcategory)
				,allowDrag:('boolean' == typeof attr.allowDrag)?attr.allowDrag:this.setAllowDrag
				,allowDrop:false
			}

			if (!Ext.isEmpty(attr.addCls)) {
				childInfo.cls += ' '+attr.addCls;
			}

			if (this.setChildHref) {
				childInfo.href = '/xwiki/bin/view/'+attr.pageName.replace('.', '/');
			}

			if (attr.rights && !attr.rights.view){
				childInfo.text = this.unviewableText;
				childInfo.qtip = this.unviewableQtip;
				if (this.disableUnviewable) {
					childInfo.disabled = this.disableUnviewable;
				}
				childInfo.allowDrop = false;
				childInfo.leaf = true;
			} else if (attr.assetType && attr.assetType.search(/Composite$/) === -1){
				childInfo.leaf = true;
			} else if (attr.assetType){
				childInfo.leaf = false;
				childInfo.allowDrop = (attr.rights && attr.rights.edit)||false;
			}

			if (attr.rights){
				if (attr.rights.view){
					childInfo.cls = childInfo.cls+' rights-viewable';
				} else {
					childInfo.cls = childInfo.cls+' rights-unviewable';
					childInfo.hidden = (this.hideUnviewable || (this.hideInvalid && attr.assetType && attr.assetType.search(/Invalid/) !== -1));
				}
				if (attr.rights.edit){
					childInfo.cls = childInfo.cls+' rights-editable';
				} else {
					childInfo.cls = childInfo.cls+' rights-uneditable';
				}
			}

			// ?? = attr.order;

			Ext.apply(childInfo, attr);

			if (this.truncateTitle !== false) {
				childInfo.text = Ext.util.Format.ellipsis(childInfo.text, Ext.num(this.truncateTitle, 125));
			}

			if(this.baseAttrs){
				Ext.applyIf(childInfo, this.baseAttrs);
			}
			if(this.applyLoader !== false){
				childInfo.loader = this;
			}
			if(typeof attr.uiProvider == 'string'){
			   childInfo.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
			}

//console.log('createNode: End ',childInfo);
			var retNode = (childInfo.leaf
				   ? new Ext.tree.TreeNode(childInfo)
				   : new Ext.tree.AsyncTreeNode(childInfo));

			// hidden seems to need to be applied after node is created
			retNode.hidden = childInfo.hidden;

			return retNode;
		}

		,requestData:function(node, callback){
			if (node.attributes.currikiNodeType === 'group'){
				this.dataUrl = '/xwiki/curriki/groups/'+(node.attributes.pageName||node.id)+'/collections';
			} else if (node.attributes.currikiNodeType === 'myCollections'){
				// Fetch user collections
				this.dataUrl = 'myCollections';
			} else if (node.attributes.currikiNodeType === 'myGroups'){
				// Fetch user's groups
				this.dataUrl = 'myGroups';
			} else {
				this.dataUrl = '/xwiki/curriki/assets/'+(node.attributes.pageName||node.id)+'/subassets';
			}

			// From parent
			if(this.fireEvent("beforeload", this, node, callback) !== false){
				if (this.dataUrl.indexOf('/') === 0) {
					this.transId = Ext.Ajax.request({
						 method: 'GET'
						,url: this.dataUrl
						,disableCaching:true
						,headers: {
							'Accept':'application/json'
						}
						,success: this.handleResponse
						,failure: this.handleFailure
						,scope: this
						,argument: {callback: callback, node: node}
						,params: ''
					});
				} else {
					this.transId = Math.floor(Math.random()*65535);
					// Is a mycollections or mygroups request
					var response = {argument:{callback: callback, node: node}};
					if (node.attributes.currikiNodeType === 'myCollections'){
						Curriki.settings.fetchMyCollectionsOnly = true;
						// Load collections, then call handle[Response,Failure] with
						// {resonseText: <collections>, argument: {node: node, callback: callback} }
						Curriki.data.user.GetCollections((function(){
							if (Curriki.errors.fetchFailed) {
								response.responseText = '[{"id":"NOUSERCOLLECTIONS", "text":"'+_('add.chooselocation.collections.user.empty')+'", "allowDrag":false, "allowDrop":false, "leaf":true}]';
								this.handleFailure(response);
							} else {
								if (Curriki.data.user.collectionChildren.length > 0) {
									response.responseText = Ext.util.JSON.encode(Curriki.data.user.collectionChildren);
								} else {
									response.responseText = '[{"id":"NOUSERCOLLECTIONS", "text":"'+_('add.chooselocation.collections.user.empty')+'", "allowDrag":false, "allowDrop":false, "leaf":true}]';
								}
								this.handleResponse(response);
							}
						}).createDelegate(this));
					} else if (node.attributes.currikiNodeType === 'myGroups'){
						Curriki.data.user.GetGroups((function(){
							if (Curriki.errors.fetchFailed) {
								response.responseText = '[{"id":"NOGROUPCOLLECTIONS", "text":"'+_('add.chooselocation.groups.empty')+'", "allowDrag":false, "allowDrop":false, "leaf":true}]';
								this.handleFailure(response);
							} else {
								if (Curriki.data.user.groupChildren.length > 0) {
									response.responseText = Ext.util.JSON.encode(Curriki.data.user.groupChildren);
								} else {
									response.responseText = '[{"id":"NOGROUPCOLLECTIONS", "text":"'+_('add.chooselocation.groups.empty')+'", "allowDrag":false, "allowDrop":false, "leaf":true}]';
								}
								this.handleResponse(response);
							}
						}).createDelegate(this));
					}
				}
			} else {
				// if the load is cancelled, make sure we notify
				// the node that we are done
				if(typeof callback == "function"){
					callback();
				}
			}
		}
});
