(function(){Ext.ns("Curriki.module.toc");Ext.ns("Curriki.data.toc");var b=Curriki.module.toc;
var a=Curriki.data.toc;b.init=function(){b.vars={};b.getQueryParam=function(d){d=d.replace(/[\[]/,"\\[").replace(/[\]]/,"\\]");
var c="[\\?&]"+d+"=([^&#]*)";var f=new RegExp(c);var e=f.exec(window.location.href);
if(e==null){return""}else{return e[1]}};Ext.ns("Curriki.ui.treeLoader");Curriki.ui.treeLoader.TOC=function(c){Curriki.ui.treeLoader.TOC.superclass.constructor.call(this)
};Ext.extend(Curriki.ui.treeLoader.TOC,Curriki.ui.treeLoader.Base,{setChildHref:true,setFullRollover:true,setTitleInRollover:true,setUniqueId:true,hideInvalid:true,truncateTitle:125});
Ext.tree.TreeNodeUI.prototype.renderElements=function(f,k,j,l){this.indentMarkup=f.parentNode?f.parentNode.ui.getChildIndent():"";
var g=typeof k.checked=="boolean";var d=k.href?k.href:Ext.isGecko?"":"#";var e=['<li class="x-tree-node"><div ext:tree-node-id="',f.id,'" class="x-tree-node-el x-tree-node-leaf x-unselectable ',k.cls,'" unselectable="on">','<span class="x-tree-node-indent">',this.indentMarkup,"</span>",'<img src="',this.emptyIcon,'" class="x-tree-ec-icon x-tree-elbow" />','<img src="',k.icon||this.emptyIcon,'" class="x-tree-node-icon',(k.icon?" x-tree-node-inline-icon":""),(k.iconCls?" "+k.iconCls:""),'" unselectable="on" />',g?('<input class="x-tree-node-cb" type="checkbox" '+(k.checked?'checked="checked" />':"/>")):"",'<a hidefocus="on" class="x-tree-node-anchor" href="',d,'" tabIndex="20" ',k.hrefTarget?' target="'+k.hrefTarget+'"':"",'><span unselectable="on">',f.text,"</span></a></div>",'<ul class="x-tree-node-ct" style="display:none;"></ul>',"</li>"].join("");
var c;if(l!==true&&f.nextSibling&&(c=f.nextSibling.ui.getEl())){this.wrap=Ext.DomHelper.insertHtml("beforeBegin",c,e)
}else{this.wrap=Ext.DomHelper.insertHtml("beforeEnd",j,e)}this.elNode=this.wrap.childNodes[0];
this.ctNode=this.wrap.childNodes[1];var i=this.elNode.childNodes;this.indentNode=i[0];
this.ecNode=i[1];this.iconNode=i[2];var h=3;if(g){this.checkbox=i[3];this.checkbox.defaultChecked=this.checkbox.checked;
h++}this.anchor=i[h];this.textNode=i[h].firstChild};b.displayMainPanel=function(c){b.vars.panel=new Ext.tree.TreePanel({id:"TOCPanel",applyTo:"resource-toc",title:_("curriki.toc.title"),autoHeight:true,cls:"resource resource-toc",border:false,useArrows:true,lines:true,containerScroll:false,enableDD:false,loader:new Curriki.ui.treeLoader.TOC(),root:c,listeners:{beforeclick:{fn:function(d,g){var f=d.getPath().replace(/:\d+(\/|$)/g,"$1").replace(/\//g,";").replace(/;[^;]*$/,"");
var h=Curriki.module.toc.getQueryParam("viewer");if(h!==""){h="&viewer="+h}window.location.href="/xwiki/bin/view/"+(d.attributes.pageName||d.id).replace(".","/")+"?bc="+f+h;
return false}},load:{fn:function(){if(typeof b.vars.foundSelect==="undefined"){var d=c.findChild("id",a.selected);
if(!Ext.isEmpty(d)){d.select();d.ensureVisible();b.vars.foundSelect=true}}}}}})};
b.buildTree=function(){var c=a.tocData;if(!Ext.isEmpty(c.addCls)){c.addCls=c.addCls+" toc-top"
}else{c.addCls=" toc-top"}c.listeners={beforecollapse:{fn:function(){return false
}}};var d=new Curriki.ui.treeLoader.TOC();c=d.createNode(c);return c};b.display=function(){b.displayMainPanel(b.buildTree())
};b.initialized=true;return true};b.start=function(){Ext.onReady(function(){if(b.init()){b.display()
}})};Ext.onReady(function(){Curriki.module.toc.start()})})();