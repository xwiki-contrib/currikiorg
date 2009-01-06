// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

// Some variables need to be defined before this script is loaded
// in order to set the initial tree

(function(){
Ext.ns('Curriki.module.toc');
Ext.ns('Curriki.data.toc');

var Toc = Curriki.module.toc;
var Data = Curriki.data.toc;

Toc.init = function(){
	Toc.vars = {};

	Toc.getQueryParam = function(name) {
		name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
		var regexS = "[\\?&]"+name+"=([^&#]*)";
		var regex = new RegExp(regexS);
		var results = regex.exec(window.location.href);
		if (results == null) {
			return "";
		} else {
			return results[1];
		}
	};

	Curriki.ui.treeLoader.TOC = function(config){
		Curriki.ui.treeLoader.TOC.superclass.constructor.call(this);
	};

	Ext.extend(Curriki.ui.treeLoader.TOC, Curriki.ui.treeLoader.Base, {
		setChildHref:true
		,createNode:function(attr){
console.log('TOC createNode: ',attr);
			if ('string' !== Ext.type(attr.qtip)
			    && 'string' === Ext.type(attr.description)
			    && 'array' === Ext.type(attr.fwItems)
			    && 'array' === Ext.type(attr.levels)
			    && 'array' === Ext.type(attr.ict)
			) {
				var desc = attr.description||'';
				desc = Ext.util.Format.ellipsis(desc, 256);
				desc = Ext.util.Format.htmlEncode(desc);

				var fw = Curriki.data.fw_item.getRolloverDisplay(attr.fwItems||[]);
				var lvl = Curriki.data.el.getRolloverDisplay(attr.levels||[]);
				var ict = Curriki.data.ict.getRolloverDisplay(attr.ict||[]);
				
				attr.qtip = String.format("{1}<br />{0}<br /><br />{3}<br />{2}<br />{5}<br />{4}<br /><br />{7}<br />{6}"
					,desc,_('mycurriki.favorites.mouseover.description')
					,fw,_('mycurriki.favorites.mouseover.subject')
					,lvl,_('mycurriki.favorites.mouseover.level')
					,ict,_('mycurriki.favorites.mouseover.ict')
				);
			}

			if ('string' === typeof attr.id) {
				var parent = Curriki.ui.treeLoader.TOC.superclass.createNode.call(this, attr);
console.log('TOC createNode: parent',parent);
				return parent;
			}

			var parent = Curriki.ui.treeLoader.TOC.superclass.createNode.call(this, attr);
console.log('TOC createNode: call super',parent);
			return parent;

/*
			var childInfo = {
				 id:attr.assetpage||attr.collectionPage
				,text:attr.displayTitle
				,qtip:attr.qtip||attr.description
				,cls:'resource-'+attr.assetType
			}
			childInfo.href = '/xwiki/bin/view/'+childInfo.id.replace('.', '/');

			if (attr.rights && !attr.rights.view){
				childInfo.text = _('add.chooselocation.resource_unavailable');
				childInfo.qtip = _('add.chooselocation.resource_unavailable_tooltip');
				childInfo.disabled = true;
				childInfo.leaf = true;
				childInfo.cls = childInfo.cls+' rights-unviewable';
			} else if (attr.assetType && attr.assetType.search(/Composite$/) === -1){
				childInfo.leaf = true;
			} else if (attr.assetType){
				childInfo.leaf = false;
			}

			Ext.apply(childInfo, attr);

			if(this.baseAttrs){
				Ext.applyIf(childInfo, this.baseAttrs);
			}
			if(this.applyLoader !== false){
				childInfo.loader = this;
			}
			if(typeof attr.uiProvider == 'string'){
			   childInfo.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
			}

console.log('createNodeTOC: End ',childInfo);
			return(childInfo.leaf
				   ? new Ext.tree.TreeNode(childInfo)
				   : new Ext.tree.AsyncTreeNode(childInfo));
*/
		}
	});

	Toc.displayMainPanel = function(root){
		// id = resource-toc
		Toc.vars.panel = new Ext.tree.TreePanel({
			id:'TOCPanel'
			,applyTo:'resource-toc'
			,title:_('curriki.toc.title')
			,autoHeight:true
			,cls:'resource resource-toc'
			,border:false
			,useArrows:true
			,lines:true
			,containerScroll:false
			,enableDD:false
			,loader:new Curriki.ui.treeLoader.TOC()
			,root:root
			,listeners:{
				'beforeclick':{
					fn:function(node, e){
						var bc = node.getPath().replace(/\//g, ';');
						var viewer = Curriki.module.toc.getQueryParam('viewer');
						if (viewer !== "") {
							viewer = '&viewer='+viewer;
						}
						window.location.href = '/xwiki/bin/view/'+node.id.replace('.', '/')+'?bc='+bc+viewer;
						return false;
					}
				}
				,'load':{
					fn:function(){
						if (typeof Toc.vars.foundSelect === 'undefined') {
							var node = root.findChild('id', Data.selected);
							if (!Ext.isEmpty(node)) {
								node.select();
								Toc.vars.foundSelect = true;
							}
						}
					}
				}
			}
		});
	};

	Toc.buildTree = function(){
		var root = Data.tocData;

		root.cls = root.cls+' toc-top';
		root.listeners = {
			'beforecollapse':{
				fn:function(){
					return false;
				}
			}
		};

		root = new Ext.tree.AsyncTreeNode(root);

		return root;
	}

	Toc.display = function(){
		Toc.displayMainPanel(Toc.buildTree());
	};

	Toc.initialized = true;

	return true;
};

Toc.start = function(){
	Ext.onReady(function(){
		if (Toc.init()) {
			Toc.display();
		}
	});
};

Ext.onReady(function(){
	Curriki.module.toc.start();
});
})();
