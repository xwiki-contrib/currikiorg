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
		,setFullRollover:true
		,setUniqueId:true
		,hideInvalid:true
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
						var bc = node.getPath().replace(/:\d+(\/|$)/g, '$1').replace(/\//g, ';');
						var viewer = Curriki.module.toc.getQueryParam('viewer');
						if (viewer !== "") {
							viewer = '&viewer='+viewer;
						}
						window.location.href = '/xwiki/bin/view/'+(node.attributes.pageName||node.id).replace('.', '/')+'?bc='+bc+viewer;
						return false;
					}
				}
				,'load':{
					fn:function(){
						if (typeof Toc.vars.foundSelect === 'undefined') {
							var node = root.findChild('id', Data.selected);
							if (!Ext.isEmpty(node)) {
								node.select();
								node.ensureVisible();
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

		//root = new Ext.tree.AsyncTreeNode(root);
		var tl = new Curriki.ui.treeLoader.TOC();
		root = tl.createNode(root);

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
