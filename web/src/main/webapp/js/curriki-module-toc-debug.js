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

	Ext.ns('Curriki.ui.treeLoader');
	Curriki.ui.treeLoader.TOC = function(config){
		Curriki.ui.treeLoader.TOC.superclass.constructor.call(this);
	};
	Ext.extend(Curriki.ui.treeLoader.TOC, Curriki.ui.treeLoader.Base, {
		setChildHref:true
		,setFullRollover:true
		,setTitleInRollover:true
		,setUniqueId:true
		,hideInvalid:true
		,truncateTitle:125
	});

	// private -- overridden to change tabIndex from the default "1"
	Ext.tree.TreeNodeUI.prototype.renderElements = function(n, a, targetNode, bulkRender){
		// add some indent caching, this helps performance when rendering a large tree
		this.indentMarkup = n.parentNode ? n.parentNode.ui.getChildIndent() : '';

		var cb = typeof a.checked == 'boolean';

		var href = a.href ? a.href : Ext.isGecko ? "" : "#";
		var buf = ['<li class="x-tree-node"><div ext:tree-node-id="',n.id,'" class="x-tree-node-el x-tree-node-leaf x-unselectable ', a.cls,'" unselectable="on">',
			'<span class="x-tree-node-indent">',this.indentMarkup,"</span>",
			'<img src="', this.emptyIcon, '" class="x-tree-ec-icon x-tree-elbow" />',
			'<img src="', a.icon || this.emptyIcon, '" class="x-tree-node-icon',(a.icon ? " x-tree-node-inline-icon" : ""),(a.iconCls ? " "+a.iconCls : ""),'" unselectable="on" />',
			cb ? ('<input class="x-tree-node-cb" type="checkbox" ' + (a.checked ? 'checked="checked" />' : '/>')) : '',
			'<a hidefocus="on" class="x-tree-node-anchor" href="',href,'" tabIndex="20" ',
			 a.hrefTarget ? ' target="'+a.hrefTarget+'"' : "", '><span unselectable="on">',n.text,"</span></a></div>",
			'<ul class="x-tree-node-ct" style="display:none;"></ul>',
			"</li>"].join('');

		var nel;
		if(bulkRender !== true && n.nextSibling && (nel = n.nextSibling.ui.getEl())){
			this.wrap = Ext.DomHelper.insertHtml("beforeBegin", nel, buf);
		}else{
			this.wrap = Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf);
		}
		
		this.elNode = this.wrap.childNodes[0];
		this.ctNode = this.wrap.childNodes[1];
		var cs = this.elNode.childNodes;
		this.indentNode = cs[0];
		this.ecNode = cs[1];
		this.iconNode = cs[2];
		var index = 3;
		if(cb){
			this.checkbox = cs[3];
			// fix for IE6
			this.checkbox.defaultChecked = this.checkbox.checked;			
			index++;
		}
		this.anchor = cs[index];
		this.textNode = cs[index].firstChild;
	};

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
						var bc = node.getPath().replace(/:\d+(\/|$)/g, '$1').replace(/\//g, ';').replace(/;[^;]*$/, '');
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

		if (!Ext.isEmpty(root.addCls)) {
			root.addCls = root.addCls+' toc-top';
		} else {
			root.addCls = ' toc-top';
		}

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
	if (Toc.init()) {
		Toc.display();
	}	
};

Ext.onReady(function(){
  Curriki.data.EventManager.on('Curriki.data:ready', function(){
	  Curriki.module.toc.start();
	});
});
})();
