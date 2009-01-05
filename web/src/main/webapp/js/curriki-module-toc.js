(function(){Ext.ns("Curriki.module.toc");Ext.ns("Curriki.data.toc");var B=Curriki.module.toc;var A=Curriki.data.toc;B.init=function(){B.vars={};B.getQueryParam=function(D){D=D.replace(/[\[]/,"\\[").replace(/[\]]/,"\\]");var C="[\\?&]"+D+"=([^&#]*)";var F=new RegExp(C);var E=F.exec(window.location.href);if(E==null){return""}else{return E[1]}};Curriki.ui.treeLoader.TOC=function(C){Curriki.ui.treeLoader.TOC.superclass.constructor.call(this)};Ext.extend(Curriki.ui.treeLoader.TOC,Curriki.ui.treeLoader.Base,{setChildHref:true,createNode:function(C){console.log("TOC createNode: ",C);if("string"!==Ext.type(C.qtip)&&"string"===Ext.type(C.description)&&"array"===Ext.type(C.fwItems)&&"array"===Ext.type(C.levels)){var G=C.description||"";G=Ext.util.Format.ellipsis(G,256);G=Ext.util.Format.htmlEncode(G);var F=Curriki.data.fw_item.getRolloverDisplay(C.fwItems||[]);var D=Curriki.data.el.getRolloverDisplay(C.levels||[]);C.qtip=String.format("{1}<br />{0}<br /><br />{3}<br />{2}<br />{5}<br />{4}",G,_("mycurriki.favorites.mouseover.description"),F,_("mycurriki.favorites.mouseover.subject"),D,_("mycurriki.favorites.mouseover.level"))}if("string"===typeof C.id){var E=Curriki.ui.treeLoader.TOC.superclass.createNode.call(this,C);console.log("TOC createNode: parent",E);return E}var E=Curriki.ui.treeLoader.TOC.superclass.createNode.call(this,C);console.log("TOC createNode: call super",E);return E}});B.displayMainPanel=function(C){B.vars.panel=new Ext.tree.TreePanel({id:"TOCPanel",applyTo:"resource-toc",title:_("curriki.toc.title"),autoHeight:true,cls:"resource resource-toc",border:false,useArrows:true,lines:true,containerScroll:false,enableDD:false,loader:new Curriki.ui.treeLoader.TOC(),root:C,listeners:{beforeclick:{fn:function(D,F){var E=D.getPath().replace(/\//g,";");var G=Curriki.module.toc.getQueryParam("viewer");if(G!==""){G="&viewer="+G}window.location.href="/xwiki/bin/view/"+D.id.replace(".","/")+"?bc="+E+G;return false}},load:{fn:function(){if(typeof B.vars.foundSelect==="undefined"){var D=C.findChild("id",A.selected);if(!Ext.isEmpty(D)){D.select();B.vars.foundSelect=true}}}}}})};B.buildTree=function(){var C=A.tocData;C.cls=C.cls+" toc-top";C.listeners={beforecollapse:{fn:function(){return false}}};C=new Ext.tree.AsyncTreeNode(C);return C};B.display=function(){B.displayMainPanel(B.buildTree())};B.initialized=true;return true};B.start=function(){Ext.onReady(function(){if(B.init()){B.display()}})};Ext.onReady(function(){Curriki.module.toc.start()})})();