(function(){Ext.ns("Curriki.module.reorder");Ext.ns("Curriki.data.reorder");var a=Curriki.module.reorder;
var c=Curriki.data.reorder;var d=Curriki.ui;var b="mycurriki.collections.reorder.";
if(c.place&&c.place==="groups"){b="groups_curriculum_collections_reorder."}a.init=function(){if(!c.place||!c.which){return false
}c.orig=[];a.store=new Ext.data.JsonStore({storeId:"CollectionsStore",url:"/xwiki/curriki/"+c.place+"/"+c.which+"/collections?_dc="+(new Date().getTime()),fields:["displayTitle","assetpage"],autoLoad:true,listeners:{load:{fn:function(f,e,g){var h=[];
f.each(function(i){h.push(i.data.assetpage)});c.orig=h;console.log("Fetched list",h)
}}}});a.mainDlg=Ext.extend(d.dialog.Messages,{initComponent:function(){Ext.apply(this,{id:"ReorderDialogueWindow",title:_(b+"dialog_title"),cls:"reorder resource",autoScroll:false,width:634,items:[{xtype:"panel",id:"guidingquestion-container",cls:"guidingquestion-container",items:[{xtype:"box",autoEl:{tag:"div",html:_(b+"guidingquestion"),cls:"guidingquestion"}},{xtype:"box",autoEl:{tag:"div",html:_(b+"instruction"),cls:"instruction"}}]},{xtype:"panel",id:"collections-list-panel",cls:"collections-list",bbar:["->",{text:_(b+"cancel.btt"),id:"cancelbutton",cls:"button cancel",listeners:{click:{fn:function(){this.close();
if(Ext.isIE){window.location.reload()}},scope:this}}},{text:_(b+"next.btt"),id:"nextbutton",cls:"button next",listeners:{click:{fn:function(){var e=[];
Ext.getCmp("reorderCollectionsMS").store.each(function(h){e.push(h.data.assetpage)
});console.log("Reordering",e);var f=this;var g=function(h){console.log("Reorder callback",h);
f.close();a.msgComplete();window.location.reload()};Curriki.assets.ReorderRootCollection(c.place,c.which,c.orig,e,g)
},scope:this}}}],items:[{xtype:"box",autoEl:{tag:"div",html:_(b+"listheader"),cls:"listheader"}},{xtype:"form",id:"ReorderDialoguePanel",formId:"ReorderDialogueForm",labelWidth:25,autoScroll:false,border:false,defaults:{labelSeparator:""},listeners:{},items:[{xtype:"multiselect",id:"reorderCollectionsMS",name:"reorderCollections",hideLabel:true,border:false,enableToolbar:false,legend:" ",store:a.store,valueField:"assetpage",displayField:'[""]}<span class="resource-CollectionComposite"><img class="x-tree-node-icon assettype-icon" src="'+Ext.BLANK_IMAGE_URL+'" /></span> {displayTitle',width:600,height:200,allowBlank:false,preventMark:true,minLength:1,isFormField:true,dragGroup:"reorderMSGroup",dropGroup:"reorderMSGroup"}]}]}]});
a.mainDlg.superclass.initComponent.call(this)}});Ext.reg("reorderDialog",a.mainDlg);
a.msgComplete=function(){Curriki.logView("/features/reorder/"+(c.place==="groups"?"groups":"mycurriki")+"/saved");
alert(_(b+"set.confirm"))};a.initialized=true;return true};a.confirmMsg={first:function(){return confirm(_(b+"checkfirst.dialog"))
},after:function(){return confirm(_(b+"checkafter.dialog"))},display:function(){if(!c.reordered){return a.confirmMsg.first()
}else{return a.confirmMsg.after()}}};a.display=function(){if(a.init()){d.show("reorderDialog");
Curriki.logView("/features/reorder/"+(c.place==="groups"?"groups":"mycurriki")+"/started")
}};a.start=function(){Ext.onReady(function(){if(a.confirmMsg.display()){a.display()
}})};Ext.onReady(function(){if(!c.place||!c.which){return}var e=Ext.DomQuery.selectNode("#hider-link");
if(e){Ext.DomHelper.insertBefore(e,{id:"reorder-link",tag:"a",cls:"reorder-link",onclick:"Curriki.module.reorder.start(); return false;",html:_(b+"link")});
Ext.DomHelper.insertBefore(e,{id:"reorder-sep",tag:"span",cls:"separator",html:"|"})
}})})();