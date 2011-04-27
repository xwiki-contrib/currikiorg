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
console.log('createNode: ',attr);
			if (this.setFullRollover) {
				if ('string' !== Ext.type(attr.qtip)
					&& 'string' === Ext.type(attr.description)
					&& 'array' === Ext.type(attr.fwItems)
					&& 'array' === Ext.type(attr.levels)
					&& 'array' === Ext.type(attr.ict)
				) {
					attr.qtip = Curriki.ui.util.getTitleRollover(attr, this.setTitleInRollover);
				}
			}

			if ('string' === typeof attr.id) {
				var p = Curriki.ui.treeLoader.Base.superclass.createNode.call(this, attr);
				if (this.truncateTitle !== false) {
					p.setText(Ext.util.Format.ellipsis(p.text, Ext.num(this.truncateTitle, 125)));
				}
console.log('createNode: parent', p);
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

console.log('createNode: End ',childInfo);
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
			} else {
				this.dataUrl = '/xwiki/curriki/assets/'+(node.attributes.pageName||node.id)+'/subassets';
			}

			// From parent
			if(this.fireEvent("beforeload", this, node, callback) !== false){
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
				// if the load is cancelled, make sure we notify
				// the node that we are done
				if(typeof callback == "function"){
					callback();
				}
			}
		}
});

Ext.ns('Curriki.ui.util');
Curriki.ui.util.getTitleRollover = function(attr, hasTitle) {
	var hasTitle = !Ext.isEmpty(hasTitle)?hasTitle:false;
	var qtip = '';

	var desc = attr.description||'';
	desc = Ext.util.Format.stripTags(desc);
	desc = Ext.util.Format.ellipsis(desc, 256);
	desc = Ext.util.Format.htmlEncode(desc);

	var title = attr.displayTitle||attr.title||'';

	var fw = Curriki.data.fw_item.getRolloverDisplay(attr.fwItems||[]);
	var lvl = Curriki.data.el.getRolloverDisplay(attr.levels||[]);
	var ict = Curriki.data.ict.getRolloverDisplay(attr.ict||[]);
	
	if (!hasTitle) {
		qtip = String.format("{1}<br />{0}<br /><br />{3}<br />{2}<br />{5}<br />{4}<br />{7}<br />{6}"
			,desc,_('global.title.popup.description')
			,fw,_('global.title.popup.subject')
			,lvl,_('global.title.popup.educationlevel')
			,ict,_('global.title.popup.ict')
		);
	} else {
		qtip = String.format("{1}<br />{0}<br /><br />{3}<br />{2}<br /><br />{5}<br />{4}<br />{7}<br />{6}<br />{9}<br />{8}"
			,title,_('global.title.popup.title')
			,desc,_('global.title.popup.description')
			,fw,_('global.title.popup.subject')
			,lvl,_('global.title.popup.educationlevel')
			,ict,_('global.title.popup.ict')
		);
	}

	return qtip;
};

/*
Ext.ns('Curriki.mycurriki.util.title');
Curriki.mycurriki.util.title.popup = function(title_id, attr){
	if ('string' === Ext.type(attr.description)
	    && 'array' === Ext.type(attr.fwItems)
	    && 'array' === Ext.type(attr.levels)
	    && 'array' === Ext.type(attr.ict)
	) {
		var qtip = Curriki.ui.util.getTitleRollover(attr);

		Ext.QuickTips.getQuickTip().register({
			target:title_id
			,text:qtip
		});
	}
};
*/
