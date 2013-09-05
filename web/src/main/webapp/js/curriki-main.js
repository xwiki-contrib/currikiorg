/*
Math.uuid.js (v1.4)
http://www.broofa.com
mailto:robert@broofa.com

Copyright (c) 2009 Robert Kieffer
Dual licensed under the MIT and GPL licenses.
*/
Math.uuid=(function(){var a="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".split("");
return function(b,e){var g=a,d=[];e=e||g.length;if(b){for(var c=0;c<b;c++){d[c]=g[0|Math.random()*e]
}}else{var f;d[8]=d[13]=d[18]=d[23]="-";d[14]="4";for(var c=0;c<36;c++){if(!d[c]){f=0|Math.random()*16;
d[c]=g[(c==19)?(f&3)|8:f]}}}return d.join("")}})();Ext.ns("Ext.ux");Ext.ux.JSONP=(function(){var c=[],b=null,a=function(){b=null;
if(c.length){b=c.shift();b.script.src=b.url+"?"+b.params;document.getElementsByTagName("head")[0].appendChild(b.script)
}};return{request:function(e,h){if(!e){return}var f=this;h.params=h.params||{};if(h.callbackKey){h.params[h.callbackKey]="Ext.ux.JSONP.callback"
}var g=Ext.urlEncode(h.params);var d=document.createElement("script");d.type="text/javascript";
if(h.isRawJSON){if(Ext.isIE){Ext.fly(d).on("readystatechange",function(){if(d.readyState=="complete"){var k=d.innerHTML;
if(k.length){f.callback(Ext.decode(k))}}})}else{Ext.fly(d).on("load",function(){var k=d.innerHTML;
if(k.length){f.callback(Ext.decode(k))}})}}c.push({url:e,script:d,callback:h.callback||function(){},scope:h.scope||window,params:g||null});
if(!b){a()}},callback:function(d){b.callback.apply(b.scope,[d]);Ext.fly(b.script).removeAllListeners();
document.getElementsByTagName("head")[0].removeChild(b.script);a()}}})();Array.prototype.contains=function(a){return this.indexOf(a)!==-1
};Ext.namespace("Ext.ux");Ext.ux.DDView=function(b){if(!b.itemSelector){var a=b.tpl;
if(this.classRe.test(a)){b.tpl=a.replace(this.classRe,"class=$1x-combo-list-item $2$1")
}else{b.tpl=a.replace(this.tagRe,'$1 class="x-combo-list-item" $2')}b.itemSelector=".x-combo-list-item"
}Ext.ux.DDView.superclass.constructor.call(this,Ext.apply(b,{border:false}))};Ext.extend(Ext.ux.DDView,Ext.DataView,{sortDir:"ASC",isFormField:true,classRe:/class=(['"])(.*)\1/,tagRe:/(<\w*)(.*?>)/,reset:Ext.emptyFn,clearInvalid:Ext.form.Field.prototype.clearInvalid,msgTarget:"qtip",afterRender:function(){Ext.ux.DDView.superclass.afterRender.call(this);
if(this.dragGroup){this.setDraggable(this.dragGroup.split(","))}if(this.dropGroup){this.setDroppable(this.dropGroup.split(","))
}if(this.deletable){this.setDeletable()}this.isDirtyFlag=false;this.addEvents("drop")
},validate:function(){return true},destroy:function(){this.purgeListeners();this.getEl().removeAllListeners();
this.getEl().remove();if(this.dragZone){if(this.dragZone.destroy){this.dragZone.destroy()
}}if(this.dropZone){if(this.dropZone.destroy){this.dropZone.destroy()}}},getName:function(){return this.name
},setValue:function(a){if(!this.store){throw"DDView.setValue(). DDView must be constructed with a valid Store"
}var b={};b[this.store.reader.meta.root]=a?[].concat(a):[];this.store.proxy=new Ext.data.MemoryProxy(b);
this.store.load()},getValue:function(){var a="(";this.store.each(function(b){a+=b.id+","
});return a.substr(0,a.length-1)+")"},getIds:function(){var b=0,a=new Array(this.store.getCount());
this.store.each(function(c){a[b++]=c.id});return a},isDirty:function(){return this.isDirtyFlag
},getTargetFromEvent:function(b){var a=b.getTarget();while((a!==null)&&(a.parentNode!=this.el.dom)){a=a.parentNode
}if(!a){a=this.el.dom.lastChild||this.el.dom}return a},getDragData:function(d){var c=this.findItemFromChild(d.getTarget());
if(c){if(!this.isSelected(c)){delete this.ignoreNextClick;this.onItemClick(c,this.indexOf(c),d);
this.ignoreNextClick=true}var b={sourceView:this,viewNodes:[],records:[],copy:this.copy||(this.allowCopy&&d.ctrlKey)};
if(this.getSelectionCount()==1){var a=this.getSelectedIndexes()[0];var f=this.getNode(a);
b.viewNodes.push(b.ddel=f);b.records.push(this.store.getAt(a));b.repairXY=Ext.fly(f).getXY()
}else{b.ddel=document.createElement("div");b.ddel.className="multi-proxy";this.collectSelection(b)
}return b}return false},getRepairXY:function(a){return this.dragData.repairXY},collectSelection:function(b){b.repairXY=Ext.fly(this.getSelectedNodes()[0]).getXY();
if(this.preserveSelectionOrder===true){Ext.each(this.getSelectedIndexes(),function(c){var e=this.getNode(c);
var d=e.cloneNode(true);d.id=Ext.id();b.ddel.appendChild(d);b.records.push(this.store.getAt(c));
b.viewNodes.push(e)},this)}else{var a=0;this.store.each(function(d){if(this.isSelected(a)){var e=this.getNode(a);
var c=e.cloneNode(true);c.id=Ext.id();b.ddel.appendChild(c);b.records.push(this.store.getAt(a));
b.viewNodes.push(e)}a++},this)}},setDraggable:function(a){if(a instanceof Array){Ext.each(a,this.setDraggable,this);
return}if(this.dragZone){this.dragZone.addToGroup(a)}else{this.dragZone=new Ext.dd.DragZone(this.getEl(),{containerScroll:true,ddGroup:a});
if(!this.multiSelect){this.singleSelect=true}this.dragZone.getDragData=this.getDragData.createDelegate(this);
this.dragZone.getRepairXY=this.getRepairXY;this.dragZone.onEndDrag=this.onEndDrag
}},setDroppable:function(a){if(a instanceof Array){Ext.each(a,this.setDroppable,this);
return}if(this.dropZone){this.dropZone.addToGroup(a)}else{this.dropZone=new Ext.dd.DropZone(this.getEl(),{owningView:this,containerScroll:true,ddGroup:a});
this.dropZone.getTargetFromEvent=this.getTargetFromEvent.createDelegate(this);this.dropZone.onNodeEnter=this.onNodeEnter.createDelegate(this);
this.dropZone.onNodeOver=this.onNodeOver.createDelegate(this);this.dropZone.onNodeOut=this.onNodeOut.createDelegate(this);
this.dropZone.onNodeDrop=this.onNodeDrop.createDelegate(this)}},getDropPoint:function(g,l,d){if(l==this.el.dom){return"above"
}var f=Ext.lib.Dom.getY(l),a=f+l.offsetHeight;var k=f+(a-f)/2;var h=Ext.lib.Event.getPageY(g);
if(h<=k){return"above"}else{return"below"}},isValidDropPoint:function(b,e,a){if(!a.viewNodes||(a.viewNodes.length!=1)){return true
}var c=a.viewNodes[0];if(c==e){return false}if((b=="below")&&(e.nextSibling==c)){return false
}if((b=="above")&&(e.previousSibling==c)){return false}return true},onNodeEnter:function(d,a,c,b){if(this.highlightColor&&(b.sourceView!=this)){this.el.highlight(this.highlightColor)
}return false},onNodeOver:function(h,a,g,d){var b=this.dropNotAllowed;var f=this.getDropPoint(g,h,a);
if(this.isValidDropPoint(f,h,d)){if(this.appendOnly||this.sortField){return"x-tree-drop-ok-below"
}if(f){var c;if(f=="above"){b=h.previousSibling?"x-tree-drop-ok-between":"x-tree-drop-ok-above";
c="x-view-drag-insert-above"}else{b=h.nextSibling?"x-tree-drop-ok-between":"x-tree-drop-ok-below";
c="x-view-drag-insert-below"}if(this.lastInsertClass!=c){Ext.fly(h).replaceClass(this.lastInsertClass,c);
this.lastInsertClass=c}}}return b},onNodeOut:function(d,a,c,b){this.removeDropIndicators(d)
},onNodeDrop:function(c,l,h,f){if(this.fireEvent("drop",this,c,l,h,f)===false){return false
}var m=this.getDropPoint(h,c,l);var d=(this.appendOnly||(c==this.el.dom))?this.store.getCount():c.viewIndex;
if(m=="below"){d++}if(f.sourceView==this){if(m=="below"){if(f.viewNodes[0]==c){f.viewNodes.shift()
}}else{if(f.viewNodes[f.viewNodes.length-1]==c){f.viewNodes.pop()}}if(!f.viewNodes.length){return false
}if(d>this.store.indexOf(f.records[0])){d--}}if(f.node instanceof Ext.tree.TreeNode){var a=f.node.getOwnerTree().recordFromNode(f.node);
if(a){f.records=[a]}}if(!f.records){alert("Programming problem. Drag data contained no Records");
return false}for(var g=0;g<f.records.length;g++){var a=f.records[g];var b=this.store.getById(a.id);
if(b&&(l!=this.dragZone)){if(!this.allowDup&&!this.allowTrash){Ext.fly(this.getNode(this.store.indexOf(b))).frame("red",1);
return true}var k=new Ext.data.Record();a.id=k.id;delete k}if(f.copy){this.store.insert(d++,a.copy())
}else{if(f.sourceView){f.sourceView.isDirtyFlag=true;f.sourceView.store.remove(a)
}if(!this.allowTrash){this.store.insert(d++,a)}}if(this.sortField){this.store.sort(this.sortField,this.sortDir)
}this.isDirtyFlag=true}this.dragZone.cachedTarget=null;return true},onEndDrag:function(a,b){var c=Ext.get(this.dragData.ddel);
if(c&&c.hasClass("multi-proxy")){c.remove()}},removeDropIndicators:function(a){if(a){Ext.fly(a).removeClass(["x-view-drag-insert-above","x-view-drag-insert-left","x-view-drag-insert-right","x-view-drag-insert-below"]);
this.lastInsertClass="_noclass"}},setDeletable:function(a){if(!this.singleSelect&&!this.multiSelect){this.singleSelect=true
}var b=this.getContextMenu();this.contextMenu.on("itemclick",function(c){switch(c.id){case"delete":this.remove(this.getSelectedIndexes());
break}},this);this.contextMenu.add({icon:a||AU.resolveUrl("/images/delete.gif"),id:"delete",text:AU.getMessage("deleteItem")})
},getContextMenu:function(){if(!this.contextMenu){this.contextMenu=new Ext.menu.Menu({id:this.id+"-contextmenu"});
this.el.on("contextmenu",this.showContextMenu,this)}return this.contextMenu},disableContextMenu:function(){if(this.contextMenu){this.el.un("contextmenu",this.showContextMenu,this)
}},showContextMenu:function(b,a){a=this.findItemFromChild(b.getTarget());if(a){b.stopEvent();
this.select(this.getNode(a),this.multiSelect&&b.ctrlKey,true);this.contextMenu.showAt(b.getXY())
}},remove:function(b){b=[].concat(b);for(var a=0;a<b.length;a++){var c=this.store.getAt(b[a]);
this.store.remove(c)}},onDblClick:function(f){var d=this.findItemFromChild(f.getTarget());
if(d){if(this.fireEvent("dblclick",this,this.indexOf(d),d,f)===false){return false
}if(this.dragGroup){var a=Ext.dd.DragDropMgr.getRelated(this.dragZone,true);while(a.contains(this.dropZone)){a.remove(this.dropZone)
}if((a.length==1)&&(a[0].owningView)){this.dragZone.cachedTarget=null;var b=Ext.get(a[0].getEl());
var c=b.getBox(true);a[0].onNodeDrop(b.dom,{target:b.dom,xy:[c.x,c.y+c.height-1]},null,this.getDragData(f))
}}}},onItemClick:function(b,a,c){if(this.ignoreNextClick){delete this.ignoreNextClick;
return}if(this.fireEvent("beforeclick",this,a,b,c)===false){return false}if(this.multiSelect||this.singleSelect){if(this.multiSelect&&c.shiftKey&&this.lastSelection){this.select(this.getNodes(this.indexOf(this.lastSelection),a),false)
}else{if(this.isSelected(b)&&c.ctrlKey){this.deselect(b)}else{this.deselect(b);this.select(b,this.multiSelect&&c.ctrlKey);
this.lastSelection=b}}c.preventDefault()}return true}});Ext.ux.Multiselect=Ext.extend(Ext.form.Field,{store:null,dataFields:[],data:[],width:100,height:100,displayField:0,valueField:1,allowBlank:true,minLength:0,maxLength:Number.MAX_VALUE,blankText:Ext.form.TextField.prototype.blankText,minLengthText:"Minimum {0} item(s) required",maxLengthText:"Maximum {0} item(s) allowed",copy:false,allowDup:false,allowTrash:false,legend:null,focusClass:undefined,delimiter:",",view:null,dragGroup:null,dropGroup:null,tbar:null,appendOnly:false,sortField:null,sortDir:"ASC",defaultAutoCreate:{tag:"div"},initComponent:function(){Ext.ux.Multiselect.superclass.initComponent.call(this);
this.addEvents({dblclick:true,click:true,change:true,drop:true})},onRender:function(e,b){var a,c,d;
Ext.ux.Multiselect.superclass.onRender.call(this,e,b);c="ux-mselect";a=new Ext.form.FieldSet({renderTo:this.el,title:this.legend,height:this.height,width:this.width,style:"padding:1px;",tbar:this.tbar});
if(!this.legend){a.el.down("."+a.headerCls).remove()}a.body.addClass(c);d='<tpl for="."><div class="'+c+"-item";
if(Ext.isIE||Ext.isIE7){d+='" unselectable=on'}else{d+=' x-unselectable"'}d+=">{"+this.displayField+"}</div></tpl>";
if(!this.store){this.store=new Ext.data.SimpleStore({fields:this.dataFields,data:this.data})
}this.view=new Ext.ux.DDView({multiSelect:true,store:this.store,selectedClass:c+"-selected",tpl:d,allowDup:this.allowDup,copy:this.copy,allowTrash:this.allowTrash,dragGroup:this.dragGroup,dropGroup:this.dropGroup,itemSelector:"."+c+"-item",isFormField:false,applyTo:a.body,appendOnly:this.appendOnly,sortField:this.sortField,sortDir:this.sortDir});
a.add(this.view);this.view.on("click",this.onViewClick,this);this.view.on("beforeClick",this.onViewBeforeClick,this);
this.view.on("dblclick",this.onViewDblClick,this);this.view.on("drop",function(h,m,g,l,k){return this.fireEvent("drop",h,m,g,l,k)
},this);this.hiddenName=this.name;var f={tag:"input",type:"hidden",value:"",name:this.name};
if(this.isFormField){this.hiddenField=this.el.createChild(f)}else{this.hiddenField=Ext.get(document.body).createChild(f)
}a.doLayout()},initValue:Ext.emptyFn,onViewClick:function(d,b,c,f){var a=this.preClickSelections.indexOf(b);
if(a!=-1){this.preClickSelections.splice(a,1);this.view.clearSelections(true);this.view.select(this.preClickSelections)
}this.fireEvent("change",this,this.getValue(),this.hiddenField.dom.value);this.hiddenField.dom.value=this.getValue();
this.fireEvent("click",this,f);this.validate()},onViewBeforeClick:function(c,a,b,d){this.preClickSelections=this.view.getSelectedIndexes();
if(this.disabled){return false}},onViewDblClick:function(c,a,b,d){return this.fireEvent("dblclick",c,a,b,d)
},getValue:function(a){var d=[];var c=this.view.getSelectedIndexes();if(c.length==0){return""
}for(var b=0;b<c.length;b++){d.push(this.store.getAt(c[b]).get(((a!=null)?a:this.valueField)))
}return d.join(this.delimiter)},setValue:function(a){var b;var d=[];this.view.clearSelections();
this.hiddenField.dom.value="";if(!a||(a=="")){return}if(!(a instanceof Array)){a=a.split(this.delimiter)
}for(var c=0;c<a.length;c++){b=this.view.store.indexOf(this.view.store.query(this.valueField,new RegExp("^"+a[c]+"$","i")).itemAt(0));
d.push(b)}this.view.select(d);this.hiddenField.dom.value=this.getValue();this.validate()
},reset:function(){this.setValue("")},getRawValue:function(a){var b=this.getValue(a);
if(b.length){b=b.split(this.delimiter)}else{b=[]}return b},setRawValue:function(a){setValue(a)
},validateValue:function(a){if(a.length<1){if(this.allowBlank){this.clearInvalid();
return true}else{this.markInvalid(this.blankText);return false}}if(a.length<this.minLength){this.markInvalid(String.format(this.minLengthText,this.minLength));
return false}if(a.length>this.maxLength){this.markInvalid(String.format(this.maxLengthText,this.maxLength));
return false}return true}});Ext.reg("multiselect",Ext.ux.Multiselect);Ext.ux.ItemSelector=Ext.extend(Ext.form.Field,{msWidth:200,msHeight:300,hideNavIcons:false,imagePath:"",iconUp:"up2.gif",iconDown:"down2.gif",iconLeft:"left2.gif",iconRight:"right2.gif",iconTop:"top2.gif",iconBottom:"bottom2.gif",drawUpIcon:true,drawDownIcon:true,drawLeftIcon:true,drawRightIcon:true,drawTopIcon:true,drawBotIcon:true,fromStore:null,toStore:null,fromData:null,toData:null,displayField:0,valueField:1,switchToFrom:false,allowDup:false,focusClass:undefined,delimiter:",",readOnly:false,toLegend:null,fromLegend:null,toSortField:null,fromSortField:null,toSortDir:"ASC",fromSortDir:"ASC",toTBar:null,fromTBar:null,bodyStyle:null,border:false,defaultAutoCreate:{tag:"div"},initComponent:function(){Ext.ux.ItemSelector.superclass.initComponent.call(this);
this.addEvents({rowdblclick:true,change:true})},onRender:function(d,a){Ext.ux.ItemSelector.superclass.onRender.call(this,d,a);
this.fromMultiselect=new Ext.ux.Multiselect({legend:this.fromLegend,delimiter:this.delimiter,allowDup:this.allowDup,copy:this.allowDup,allowTrash:this.allowDup,dragGroup:this.readOnly?null:"drop2-"+this.el.dom.id,dropGroup:this.readOnly?null:"drop1-"+this.el.dom.id,width:this.msWidth,height:this.msHeight,dataFields:this.dataFields,data:this.fromData,displayField:this.displayField,valueField:this.valueField,store:this.fromStore,isFormField:false,tbar:this.fromTBar,appendOnly:true,sortField:this.fromSortField,sortDir:this.fromSortDir});
this.fromMultiselect.on("dblclick",this.onRowDblClick,this);if(!this.toStore){this.toStore=new Ext.data.SimpleStore({fields:this.dataFields,data:this.toData})
}this.toStore.on("add",this.valueChanged,this);this.toStore.on("remove",this.valueChanged,this);
this.toStore.on("load",this.valueChanged,this);this.toMultiselect=new Ext.ux.Multiselect({legend:this.toLegend,delimiter:this.delimiter,allowDup:this.allowDup,dragGroup:this.readOnly?null:"drop1-"+this.el.dom.id,dropGroup:this.readOnly?null:"drop2-"+this.el.dom.id+",drop1-"+this.el.dom.id,width:this.msWidth,height:this.msHeight,displayField:this.displayField,valueField:this.valueField,store:this.toStore,isFormField:false,tbar:this.toTBar,sortField:this.toSortField,sortDir:this.toSortDir});
this.toMultiselect.on("dblclick",this.onRowDblClick,this);var g=new Ext.Panel({bodyStyle:this.bodyStyle,border:this.border,layout:"table",layoutConfig:{columns:3}});
g.add(this.switchToFrom?this.toMultiselect:this.fromMultiselect);var c=new Ext.Panel({header:false});
g.add(c);g.add(this.switchToFrom?this.fromMultiselect:this.toMultiselect);g.render(this.el);
c.el.down("."+c.bwrapCls).remove();if(this.imagePath!=""&&this.imagePath.charAt(this.imagePath.length-1)!="/"){this.imagePath+="/"
}this.iconUp=this.imagePath+(this.iconUp||"up2.gif");this.iconDown=this.imagePath+(this.iconDown||"down2.gif");
this.iconLeft=this.imagePath+(this.iconLeft||"left2.gif");this.iconRight=this.imagePath+(this.iconRight||"right2.gif");
this.iconTop=this.imagePath+(this.iconTop||"top2.gif");this.iconBottom=this.imagePath+(this.iconBottom||"bottom2.gif");
var f=c.getEl();if(!this.toSortField){this.toTopIcon=f.createChild({tag:"img",src:this.iconTop,style:{cursor:"pointer",margin:"2px"}});
f.createChild({tag:"br"});this.upIcon=f.createChild({tag:"img",src:this.iconUp,style:{cursor:"pointer",margin:"2px"}});
f.createChild({tag:"br"})}this.addIcon=f.createChild({tag:"img",src:this.switchToFrom?this.iconLeft:this.iconRight,style:{cursor:"pointer",margin:"2px"}});
f.createChild({tag:"br"});this.removeIcon=f.createChild({tag:"img",src:this.switchToFrom?this.iconRight:this.iconLeft,style:{cursor:"pointer",margin:"2px"}});
f.createChild({tag:"br"});if(!this.toSortField){this.downIcon=f.createChild({tag:"img",src:this.iconDown,style:{cursor:"pointer",margin:"2px"}});
f.createChild({tag:"br"});this.toBottomIcon=f.createChild({tag:"img",src:this.iconBottom,style:{cursor:"pointer",margin:"2px"}})
}if(!this.readOnly){if(!this.toSortField){this.toTopIcon.on("click",this.toTop,this);
this.upIcon.on("click",this.up,this);this.downIcon.on("click",this.down,this);this.toBottomIcon.on("click",this.toBottom,this)
}this.addIcon.on("click",this.fromTo,this);this.removeIcon.on("click",this.toFrom,this)
}if(!this.drawUpIcon||this.hideNavIcons){this.upIcon.dom.style.display="none"}if(!this.drawDownIcon||this.hideNavIcons){this.downIcon.dom.style.display="none"
}if(!this.drawLeftIcon||this.hideNavIcons){this.addIcon.dom.style.display="none"}if(!this.drawRightIcon||this.hideNavIcons){this.removeIcon.dom.style.display="none"
}if(!this.drawTopIcon||this.hideNavIcons){this.toTopIcon.dom.style.display="none"
}if(!this.drawBotIcon||this.hideNavIcons){this.toBottomIcon.dom.style.display="none"
}var b=g.body.first();this.el.setWidth(g.body.first().getWidth());g.body.removeClass();
this.hiddenName=this.name;var e={tag:"input",type:"hidden",value:"",name:this.name};
this.hiddenField=this.el.createChild(e);this.valueChanged(this.toStore)},initValue:Ext.emptyFn,toTop:function(){var c=this.toMultiselect.view.getSelectedIndexes();
var a=[];if(c.length>0){c.sort();for(var b=0;b<c.length;b++){record=this.toMultiselect.view.store.getAt(c[b]);
a.push(record)}c=[];for(var b=a.length-1;b>-1;b--){record=a[b];this.toMultiselect.view.store.remove(record);
this.toMultiselect.view.store.insert(0,record);c.push(((a.length-1)-b))}}this.toMultiselect.view.refresh();
this.toMultiselect.view.select(c)},toBottom:function(){var c=this.toMultiselect.view.getSelectedIndexes();
var a=[];if(c.length>0){c.sort();for(var b=0;b<c.length;b++){record=this.toMultiselect.view.store.getAt(c[b]);
a.push(record)}c=[];for(var b=0;b<a.length;b++){record=a[b];this.toMultiselect.view.store.remove(record);
this.toMultiselect.view.store.add(record);c.push((this.toMultiselect.view.store.getCount())-(a.length-b))
}}this.toMultiselect.view.refresh();this.toMultiselect.view.select(c)},up:function(){var a=null;
var c=this.toMultiselect.view.getSelectedIndexes();c.sort();var d=[];if(c.length>0){for(var b=0;
b<c.length;b++){a=this.toMultiselect.view.store.getAt(c[b]);if((c[b]-1)>=0){this.toMultiselect.view.store.remove(a);
this.toMultiselect.view.store.insert(c[b]-1,a);d.push(c[b]-1)}}this.toMultiselect.view.refresh();
this.toMultiselect.view.select(d)}},down:function(){var a=null;var c=this.toMultiselect.view.getSelectedIndexes();
c.sort();c.reverse();var d=[];if(c.length>0){for(var b=0;b<c.length;b++){a=this.toMultiselect.view.store.getAt(c[b]);
if((c[b]+1)<this.toMultiselect.view.store.getCount()){this.toMultiselect.view.store.remove(a);
this.toMultiselect.view.store.insert(c[b]+1,a);d.push(c[b]+1)}}this.toMultiselect.view.refresh();
this.toMultiselect.view.select(d)}},fromTo:function(){var d=this.fromMultiselect.view.getSelectedIndexes();
var b=[];if(d.length>0){for(var c=0;c<d.length;c++){record=this.fromMultiselect.view.store.getAt(d[c]);
b.push(record)}if(!this.allowDup){d=[]}for(var c=0;c<b.length;c++){record=b[c];if(this.allowDup){var a=new Ext.data.Record();
record.id=a.id;delete a;this.toMultiselect.view.store.add(record)}else{this.fromMultiselect.view.store.remove(record);
this.toMultiselect.view.store.add(record);d.push((this.toMultiselect.view.store.getCount()-1))
}}}this.toMultiselect.view.refresh();this.fromMultiselect.view.refresh();if(this.toSortField){this.toMultiselect.store.sort(this.toSortField,this.toSortDir)
}if(this.allowDup){this.fromMultiselect.view.select(d)}else{this.toMultiselect.view.select(d)
}},toFrom:function(){var c=this.toMultiselect.view.getSelectedIndexes();var a=[];
if(c.length>0){for(var b=0;b<c.length;b++){record=this.toMultiselect.view.store.getAt(c[b]);
a.push(record)}c=[];for(var b=0;b<a.length;b++){record=a[b];this.toMultiselect.view.store.remove(record);
if(!this.allowDup){this.fromMultiselect.view.store.add(record);c.push((this.fromMultiselect.view.store.getCount()-1))
}}}this.fromMultiselect.view.refresh();this.toMultiselect.view.refresh();if(this.fromSortField){this.fromMultiselect.store.sort(this.fromSortField,this.fromSortDir)
}this.fromMultiselect.view.select(c)},valueChanged:function(c){var a=null;var b=[];
for(var d=0;d<c.getCount();d++){a=c.getAt(d);b.push(a.get(this.valueField))}this.hiddenField.dom.value=b.join(this.delimiter);
this.fireEvent("change",this,this.getValue(),this.hiddenField.dom.value)},getValue:function(){return this.hiddenField.dom.value
},onRowDblClick:function(c,a,b,d){return this.fireEvent("rowdblclick",c,a,b,d)},reset:function(){range=this.toMultiselect.store.getRange();
this.toMultiselect.store.removeAll();if(!this.allowDup){this.fromMultiselect.store.add(range);
this.fromMultiselect.store.sort(this.displayField,"ASC")}this.valueChanged(this.toMultiselect.store)
}});Ext.reg("itemselector",Ext.ux.ItemSelector);Ext.namespace("Ext.ux.Andrie");Ext.ux.Andrie.pPageSize=function(a){Ext.apply(this,a)
};Ext.extend(Ext.ux.Andrie.pPageSize,Ext.util.Observable,{beforeText:"Show",afterText:"items",addBefore:"-",addAfter:null,dynamic:false,variations:[5,10,20,50,100,200,500,1000],comboCfg:undefined,init:function(a){this.pagingToolbar=a;
this.pagingToolbar.pageSizeCombo=this;this.pagingToolbar.setPageSize=this.setPageSize.createDelegate(this);
this.pagingToolbar.getPageSize=function(){return this.pageSize};this.pagingToolbar.on("render",this.onRender,this)
},addSize:function(a){if(a>0){this.sizes.push([a])}},updateStore:function(){if(this.dynamic){var b=this.pagingToolbar.pageSize,e;
b=(b>0)?b:1;this.sizes=[];var c=this.variations;for(var d=0,a=c.length;d<a;d++){this.addSize(b-c[c.length-1-d])
}this.addToStore(b);for(var d=0,a=c.length;d<a;d++){this.addSize(b+c[d])}}else{if(!this.staticSizes){this.sizes=[];
var c=this.variations;var b=0;for(var d=0,a=c.length;d<a;d++){this.addSize(b+c[d])
}this.staticSizes=this.sizes.slice(0)}else{this.sizes=this.staticSizes.slice(0)}}this.combo.store.loadData(this.sizes);
this.combo.collapse();this.combo.setValue(this.pagingToolbar.pageSize)},setPageSize:function(f,k){var l=this.pagingToolbar;
this.combo.collapse();f=parseInt(f)||parseInt(this.combo.getValue());f=(f>0)?f:1;
if(f==l.pageSize){return}else{if(f<l.pageSize){l.pageSize=f;var a=Math.round(l.cursor/f)+1;
var h=(a-1)*f;var g=l.store;if(h>g.getTotalCount()){this.pagingToolbar.pageSize=f;
this.pagingToolbar.doLoad(h-f)}else{g.suspendEvents();for(var b=0,c=h-l.cursor;b<c;
b++){g.remove(g.getAt(0))}while(g.getCount()>f){g.remove(g.getAt(g.getCount()-1))
}g.resumeEvents();g.fireEvent("datachanged",g);l.cursor=h;var e=l.getPageData();l.afterTextEl.el.innerHTML=String.format(l.afterPageText,e.pages);
l.field.dom.value=a;l.first.setDisabled(a==1);l.prev.setDisabled(a==1);l.next.setDisabled(a==e.pages);
l.last.setDisabled(a==e.pages);l.updateInfo()}}else{this.pagingToolbar.pageSize=f;
this.pagingToolbar.doLoad(Math.floor(this.pagingToolbar.cursor/this.pagingToolbar.pageSize)*this.pagingToolbar.pageSize)
}}this.updateStore()},onRender:function(){this.combo=Ext.ComponentMgr.create(Ext.applyIf(this.comboCfg||{},{store:new Ext.data.SimpleStore({fields:["pageSize"],data:[]}),displayField:"pageSize",valueField:"pageSize",mode:"local",triggerAction:"all",width:50,xtype:"combo"}));
this.combo.on("select",this.setPageSize,this);this.updateStore();if(this.addBefore){this.pagingToolbar.add(this.addBefore)
}if(this.beforeText){this.pagingToolbar.add(this.beforeText)}this.pagingToolbar.add(this.combo);
if(this.afterText){this.pagingToolbar.add(this.afterText)}if(this.addAfter){this.pagingToolbar.add(this.addAfter)
}}});Ext.grid.RowExpander=function(a){Ext.apply(this,a);this.addEvents({beforeexpand:true,expand:true,beforecollapse:true,collapse:true});
Ext.grid.RowExpander.superclass.constructor.call(this);if(this.tpl){if(typeof this.tpl=="string"){this.tpl=new Ext.Template(this.tpl)
}this.tpl.compile()}this.state={};this.bodyContent={}};Ext.extend(Ext.grid.RowExpander,Ext.util.Observable,{header:"",width:20,sortable:false,fixed:true,menuDisabled:true,dataIndex:"",id:"expander",lazyRender:true,enableCaching:true,getRowClass:function(a,e,d,c){d.cols=d.cols-1;
var b=this.bodyContent[a.id];if(!b&&!this.lazyRender){b=this.getBodyContent(a,e)}if(b){d.body=b
}return this.state[a.id]?"x-grid3-row-expanded":"x-grid3-row-collapsed"},init:function(b){this.grid=b;
var a=b.getView();a.getRowClass=this.getRowClass.createDelegate(this);a.enableRowBody=true;
b.on("render",function(){a.mainBody.on("mousedown",this.onMouseDown,this)},this)},getBodyContent:function(a,b){if(!this.enableCaching){return this.tpl.apply(a.data)
}var c=this.bodyContent[a.id];if(!c){c=this.tpl.apply(a.data);this.bodyContent[a.id]=c
}return c},onMouseDown:function(b,a){if(a.className=="x-grid3-row-expander"){b.stopEvent();
var c=b.getTarget(".x-grid3-row");this.toggleRow(c)}},renderer:function(b,c,a){c.cellAttr='rowspan="2"';
return'<div class="x-grid3-row-expander">&#160;</div>'},beforeExpand:function(b,a,c){if(this.fireEvent("beforeexpand",this,b,a,c)!==false){if(this.tpl&&this.lazyRender){a.innerHTML=this.getBodyContent(b,c)
}return true}else{return false}},toggleRow:function(a){if(typeof a=="number"){a=this.grid.view.getRow(a)
}this[Ext.fly(a).hasClass("x-grid3-row-collapsed")?"expandRow":"collapseRow"](a)},expandRow:function(c){if(typeof c=="number"){c=this.grid.view.getRow(c)
}var b=this.grid.store.getAt(c.rowIndex);var a=Ext.DomQuery.selectNode("tr:nth(2) div.x-grid3-row-body",c);
if(this.beforeExpand(b,a,c.rowIndex)){this.state[b.id]=true;Ext.fly(c).replaceClass("x-grid3-row-collapsed","x-grid3-row-expanded");
this.fireEvent("expand",this,b,a,c.rowIndex)}},collapseRow:function(c){if(typeof c=="number"){c=this.grid.view.getRow(c)
}var b=this.grid.store.getAt(c.rowIndex);var a=Ext.fly(c).child("tr:nth(1) div.x-grid3-row-body",true);
if(this.fireEvent("beforecollapse",this,b,a,c.rowIndex)!==false){this.state[b.id]=false;
Ext.fly(c).replaceClass("x-grid3-row-expanded","x-grid3-row-collapsed");this.fireEvent("collapse",this,b,a,c.rowIndex)
}}});Ext.override(Ext.layout.FormLayout,{adjustWidthAnchor:function(b,a){return b-(a.isFormField?(a.hideLabel?0:this.labelAdjust):0)-a.el.getMargins("lr")
}});Ext.override(Ext.layout.FormLayout,{getAnchorViewSize:function(a,b){return(a.body||a.el).getStyleSize()
}});Ext.override(Ext.layout.AnchorLayout,{getAnchorViewSize:function(a,c){var b=a.body||a.el;
return{width:b.dom.clientWidth-b.getPadding("lr"),height:b.dom.clientHeight-b.getPadding("tb")}
},adjustWidthAnchor:function(b,a){return b-a.el.getMargins("lr")},adjustHeightAnchor:function(b,a){return b-a.el.getMargins("tb")
}});Ext.override(Ext.Component,{saveState:function(){if(Ext.state.Manager&&this.stateful!==false){var a=this.getState();
if(this.fireEvent("beforestatesave",this,a)!==false){Ext.state.Manager.set(this.stateId||this.id,a);
this.fireEvent("statesave",this,a)}}},stateful:false});Ext.override(Ext.form.TriggerField,{onShow:function(){if(this.wrap){this.wrap.show()
}},onHide:function(){this.wrap.hide()},onRender:function(b,a){Ext.form.TriggerField.superclass.onRender.call(this,b,a);
this.wrap=this.el.wrap({cls:"x-form-field-wrap"});this.trigger=this.wrap.createChild(this.triggerConfig||{tag:"img",src:Ext.BLANK_IMAGE_URL,cls:"x-form-trigger "+this.triggerClass});
if(this.hideTrigger){this.trigger.setDisplayed(false)}this.initTrigger();if(!this.width){this.wrap.setWidth(this.el.getWidth()+this.trigger.getWidth())
}else{this.wrap.setWidth(this.width)}}});Ext.override(Ext.PagingToolbar,{onRender:function(b,a){Ext.PagingToolbar.superclass.onRender.call(this,b,a);
this.first=this.addButton({tooltip:this.firstText,iconCls:"x-tbar-page-first",text:_("search.pagination.first.button"),disabled:true,handler:this.onClick.createDelegate(this,["first"])});
this.prev=this.addButton({tooltip:this.prevText,iconCls:"x-tbar-page-prev",text:_("search.pagination.prev.button"),disabled:true,handler:this.onClick.createDelegate(this,["prev"])});
this.addSeparator();this.add(this.beforePageText);this.field=Ext.get(this.addDom({tag:"input",type:"text",size:"3",value:"1",cls:"x-tbar-page-number"}).el);
this.field.on("keydown",this.onPagingKeydown,this);this.field.on("focus",function(){this.dom.select()
});this.afterTextEl=this.addText(String.format(this.afterPageText,1));this.field.setHeight(18);
this.addSeparator();this.next=this.addButton({tooltip:this.nextText,iconCls:"x-tbar-page-next",text:_("search.pagination.next.button"),disabled:true,handler:this.onClick.createDelegate(this,["next"])});
this.last=this.addButton({tooltip:this.lastText,iconCls:"x-tbar-page-last",text:_("search.pagination.last.button"),disabled:true,handler:this.onClick.createDelegate(this,["last"])});
this.loading=this.addButton({hidden:true,tooltip:this.refreshText,iconCls:"x-tbar-loading",handler:this.onClick.createDelegate(this,["refresh"])});
if(this.displayInfo){this.displayEl=Ext.fly(this.el.dom).createChild({cls:"x-paging-info"})
}if(this.dsLoaded){this.onLoad.apply(this,this.dsLoaded)}}});Ext.override(Ext.ux.Andrie.pPageSize,{setPageSize:function(b,a){var c=this.pagingToolbar;
this.combo.collapse();b=parseInt(b)||parseInt(this.combo.getValue());b=(b>0)?b:1;
if(b==c.pageSize){return}else{this.pagingToolbar.pageSize=b;this.pagingToolbar.doLoad(Math.floor(this.pagingToolbar.cursor/this.pagingToolbar.pageSize)*this.pagingToolbar.pageSize)
}this.updateStore()}});Ext.BLANK_IMAGE_URL="/xwiki/skins/curriki8/extjs/resources/images/default/s.gif";
Ext.Ajax.defaultHeaders={Accept:"application/json","Content-Type":"application/json; charset=utf-8"};
Ext.Ajax.disableCaching=false;Ext.Ajax.timeout=120000;if(!("console" in window)||!(console.log)){var names=["log","debug","info","warn","error","assert","dir","dirxml","group","groupEnd","time","timeEnd","count","trace","profile","profileEnd"];
window.console={};for(var i=0;i<names.length;++i){window.console[names[i]]=Ext.emptyFn
}}console.log("initing Curriki");Ext.ns("Curriki");Curriki.console=window.console;
Ext.ns("Curriki.module");Curriki.requestCount=0;Ext.onReady(function(){Curriki.loadingCount=0;
Curriki.hideLoadingMask=false;Curriki.loadingMask=new Ext.LoadMask(Ext.getBody(),{msg:_("loading.loading_msg")});
Ext.Ajax.on("beforerequest",function(b,a){a.requestCount=Curriki.requestCount++;console.log("beforerequest ("+a.requestCount+")",b,a);
Curriki.Ajax.beforerequest(b,a)});Ext.Ajax.on("requestcomplete",function(c,a,b){console.log("requestcomplete ("+b.requestCount+")",c,a,b);
Curriki.Ajax.requestcomplete(c,a,b)});Ext.Ajax.on("requestexception",Curriki.notifyException)
});Curriki.Ajax={beforerequest:function(b,a){Curriki.showLoading(a.waitMsg)},requestcomplete:function(c,a,b){Curriki.hideLoading()
},requestexception:function(c,a,b){Curriki.hideLoading(true)}};Curriki.notifyException=function(b){console.log("requestexception",b);
Curriki.Ajax.requestexception(null,null,null);Curriki.logView("/features/ajax/error/");
var a=new Ext.util.DelayedTask(function(){if(!Ext.isEmpty(Curriki.loadingMask)){Curriki.loadingMask.hide();
Curriki.loadingMask.disable()}Ext.MessageBox.alert(_("search.connection.error.title"),_("search.connection.error.body"))
});a.delay(100)};Curriki.id=function(a){return Ext.id("",a+":")};Curriki.showLoading=function(b,a){if(a===true){Curriki.loadingCount++
}if(!Curriki.hideLoadingMask&&!Ext.isEmpty(Curriki.loadingMask)){b=b||"loading.loading_msg";
Curriki.loadingMask.msg=_(b);Curriki.loadingMask.enable();Curriki.loadingMask.show()
}};Curriki.isISO8601DateParsing=function(){if(typeof(Curriki.ISO8601DateParsing)!="undefined"){return Curriki.ISO8601DateParsing
}var a=navigator.userAgent;Curriki.ISO8601DateParsing=a.indexOf("OS 5")!=-1&&(a.indexOf("iPhone")!=-1||a.indexOf("iPod")!=-1||a.indexOf("iPad")!=-1);
console.log("Set ISO8601 parsing to "+Curriki.ISO8601DateParsing);return Curriki.ISO8601DateParsing
};Curriki.hideLoading=function(a){if(a===true){Curriki.loadingCount--}if(Curriki.loadingCount==0&&!Ext.isEmpty(Curriki.loadingMask)){Curriki.loadingMask.hide();
Curriki.loadingMask.disable()}else{if(Curriki.loadingCount<0){Curriki.loadingCount=0
}}};Curriki.logEvent=function(c,b){var d=c.reverse();d.push("_trackEvent");d=d.reverse();
if(window._gaq){if(b){_gaq.push(d).push(b)}else{_gaq.push(d)}}else{try{if(b){window.top._gaq.push(d).push(b)
}else{window.top._gaq.push(d)}if(console){console.info("Would track: ",page)}}catch(a){try{if(console){console.info("Failed to track: ",page)
}}catch(a){}}}};Curriki.logView=function(a){if(window.pageTracker){pageTracker._trackPageview(a)
}else{if(_gaq){_gaq.push(["_trackPageview",a])}else{try{if(window.top._gaq){window.top._gaq.push(["_trackPageview",a])
}else{window.top.pageTrackerQueue=window.top.pageTrackerQueue||new Array();window.top.pageTrackerQueue.push(a)
}if(console){console.info("Would track: ",a)}}catch(b){try{window.pageTrackerQueue=window.pageTrackerQueue||new Array();
window.pageTrackerQueue.push(a);if(console){console.info("Would track: ",a)}}catch(b){}}}}};
Curriki.start=function(callback){console.log("Start Callback: ",callback);var args={};
if("object"===typeof callback){if(callback.args){args=callback.args}if(callback.callback){callback=callback.callback
}else{if(callback.module){callback=callback.module}}}if("string"===typeof callback){var module=eval("(Curriki.module."+callback.toLowerCase()+")");
if(module&&"function"===typeof module.init){module.init(args);if("function"===typeof module.start){callback=module.start
}else{callback=Ext.emptyFn}}else{switch(callback){default:callback=Ext.emptyFn;break
}}}if("function"===typeof callback){callback(args)}};Curriki.init=function(a){console.log("Curriki.init: ",a);
if(Ext.isEmpty(Curriki.initialized)){Curriki.data.user.GetUserinfo(function(){Curriki.start(a)
});Curriki.initialized=true}else{Curriki.start(a)}};Ext.ns("Curriki.data.user");Curriki.data.user={me:{username:"XWiki.XWikiGuest",fullname:"Guest"},collections:[],groups:[],collectionChildren:[],groupChildren:[],gotCollections:false,json_prefix:"/xwiki/curriki/users/",user_try:0,GetUserinfo:function(a){if(!Ext.isEmpty(Curriki.global)&&!Ext.isEmpty(Curriki.global.username)&&!Ext.isEmpty(Curriki.global.fullname)){this.me={username:Curriki.global.username,fullname:Curriki.global.fullname};
if(Curriki.settings&&Curriki.settings.localCollectionFetch){a()}else{this.GetCollections(a)
}}else{this.user_try++;Ext.Ajax.request({url:this.json_prefix+"me",method:"GET",disableCaching:true,headers:{Accept:"application/json"},scope:this,success:function(b,c){var d=b.responseText;
var e=d.evalJSON(true);if(!e){console.warn("Cannot get user information");if(this.user_try<5){this.GetUserinfo(a)
}else{console.error("Cannot get user information",b,c);alert(_("add.servertimedout.message.text"))
}}else{this.user_try=0;this.me=e;if(Curriki.settings&&Curriki.settings.localCollectionFetch){a()
}else{this.GetCollections(a)}}},failure:function(b,c){console.error("Cannot get user information",b,c);
alert(_("add.servertimedout.message.text"))}})}},collection_try:0,GetCollections:function(a){Ext.ns("Curriki.errors");
Curriki.errors.fetchFailed=false;if(Curriki.data.user.gotCollections){a()}else{this.collection_try++;
Ext.Ajax.request({url:this.json_prefix+this.me.username+"/collections",method:"GET",disableCaching:true,headers:{Accept:"application/json"},scope:this,success:function(b,c){var d=b.responseText;
var e=d.evalJSON(true);if(!e){console.warn("Cannot read user's collection information");
if(this.collection_try<5){this.GetCollections(a)}else{console.error("Cannot get user's collection information",b,c);
alert(_("add.servertimedout.message.text"))}}else{this.collection_try=0;this.gotCollections=true;
this.collections=e;this.collectionChildren=this.CreateCollectionChildren();console.log("Collections: ",this.collectionChildren);
if(Curriki.settings&&Curriki.settings.fetchMyCollectionsOnly){a()}else{this.GetGroups(a)
}}},failure:function(b,c){Curriki.errors.fetchFailed=true;console.error("Cannot get user's collection information",b,c);
alert(_("add.servertimedout.message.text"));this.collections=[];if(Curriki.settings&&Curriki.settings.fetchMyCollectionsOnly){a()
}else{this.GetGroups(a)}}})}},group_try:0,GetGroups:function(a){Ext.ns("Curriki.errors");
Curriki.errors.fetchFailed=false;Ext.Ajax.request({url:this.json_prefix+this.me.username+"/groups",method:"GET",disableCaching:true,headers:{Accept:"application/json"},scope:this,success:function(b,c){var d=b.responseText;
var e=d.evalJSON(true);if(!e){console.warn("Cannot read user's group information");
if(this.group_try<5){this.GetGroups(a)}else{throw {message:"GetUserinfo: Json object not found"}
}}else{this.group_try=0;this.groups=e;this.groupChildren=this.CreateGroupChildren();
a()}},failure:function(b,c){Curriki.errors.fetchFailed=true;console.error("Cannot get user's group information",b,c);
alert(_("add.servertimedout.message.text"));this.groups=[];a()}})},CreateCollectionChildren:function(){var a=[];
this.collections.each(function(b){var c={id:b.collectionPage,text:b.displayTitle,qtip:b.description,cls:"resource-"+b.assetType,allowDrag:false,allowDrop:true};
if(Ext.isArray(b.children)&&b.children.length>0){c.leaf=false}else{c.leaf=false;c.children=[]
}a.push(c)});return a},CreateGroupChildren:function(){var a=[];this.groups.each(function(b){if(b.editableCollectionCount>0){var c={id:b.groupSpace,currikiNodeType:"group",text:b.displayTitle,qtip:b.description,cls:"curriki-group",allowDrag:false,allowDrop:true,disallowDropping:true};
a.push(c)}});return a}};Ext.ns("Curriki.data.ict");Curriki.data.ict.list=["activity_assignment","activity_exercise","activity_lab","activity_game","activity_worksheet","activity_problemset","activity_webquest","book_fiction","book_nonfiction","book_readings","book_textbook","curriculum_answerkey","curriculum_assessment","curriculum_course","curriculum_unit","curriculum_lp","curriculum_rubric","curriculum_scope","curriculum_standards","curriculum_studyguide","curriculum_syllabus","curriculum_tutorial","curriculum_workbook","resource_animation","resource_article","resource_diagram","resource_glossary","resource_index","resource_photograph","resource_presentation","resource_collection","resource_script","resource_speech","resource_study","resource_table","resource_template","resource_webcast","other"];
Curriki.data.ict.data=[];Curriki.data.ict.list.each(function(a){var b=_("CurrikiCode.AssetClass_instructional_component_"+a);
if(a==="other"){b="zzz"}Curriki.data.ict.data.push([a,_("CurrikiCode.AssetClass_instructional_component_"+a),b])
});Curriki.data.ict.store=new Ext.data.SimpleStore({fields:["id","ict","sortValue"],sortInfo:{field:"sortValue",direction:"ASC"},data:Curriki.data.ict.data,id:0});
Curriki.data.ict.getRolloverDisplay=function(b){var d=b||[];var a="";var c='<div class="ict-{0}"><img class="ict-icon" src="/xwiki/skins/curriki8/extjs/resources/images/default/s.gif" /><span class="ict-title">{1}</span></div>';
if("undefined"!==typeof d&&"undefined"!==typeof d[0]){a+=String.format(c,d[0].replace(/_.*/,""),_("CurrikiCode.AssetClass_instructional_component_"+d[0]));
if("undefined"!==typeof d[1]){a+=String.format(c,d[1].replace(/_.*/,""),_("CurrikiCode.AssetClass_instructional_component_"+d[1]));
if("undefined"!==typeof d[2]){a+="...<br />"}}}else{a+=String.format(c,"none",_("global.title.popup.ict.missing"))
}return a};Ext.ns("Curriki.data.el");Curriki.data.el.list=["prek","gr-k-2","gr-3-5","gr-6-8","gr-9-10","gr-11-12","college_and_beyond","professional_development","special_education","na"];
Curriki.data.el.data=[];Curriki.data.el.list.each(function(a){Curriki.data.el.data.push({inputValue:a,boxLabel:_("CurrikiCode.AssetClass_educational_level_"+a)})
});Curriki.data.el.getRolloverDisplay=function(b){var c=b||undefined;var a="";if("undefined"!==typeof c&&"undefined"!==typeof c[0]){a+=Ext.util.Format.htmlEncode(_("CurrikiCode.AssetClass_educational_level_"+c[0]))+"<br />";
if("undefined"!==typeof c[1]){a+=Ext.util.Format.htmlEncode(_("CurrikiCode.AssetClass_educational_level_"+c[1]))+"<br />";
if("undefined"!==typeof c[2]){a+="...<br />"}}}else{a+=_("global.title.popup.none.selected")
}return a};Ext.ns("Curriki.data.rights");Curriki.data.rights.list=["public","members","private"];
Curriki.data.rights.initial=Curriki.data.rights.list[0];Curriki.data.rights.data=[];
Curriki.data.rights.list.each(function(a){Curriki.data.rights.data.push({inputValue:a,boxLabel:_("CurrikiCode.AssetClass_rights_"+a),checked:Curriki.data.rights.initial==a?true:false})
});Ext.ns("Curriki.data.language");Curriki.data.language.list=["eng","ind","zho","nld","fin","fra","deu","hin","ita","jpn","kor","nep","por","rus","sin","spa","tam","999"];
Curriki.data.language.initial=Curriki.data.language.list[0];Curriki.data.language.data=[];
Curriki.data.language.list.each(function(a){Curriki.data.language.data.push([a,_("CurrikiCode.AssetClass_language_"+a)])
});Curriki.data.language.store=new Ext.data.SimpleStore({fields:["id","language"],data:Curriki.data.language.data});
Ext.ns("Curriki.data.category");Curriki.data.category.list=["text","image","audio","video","interactive","archive","document","external","collection","unknown"];
Curriki.data.category.data=[];Curriki.data.category.list.each(function(a){Curriki.data.category.data.push({inputValue:a,boxLabel:_("CurrikiCode.AssetClass_category_"+a)})
});Ext.ns("Curriki.data.licence");Curriki.data.licence.list=["Licences.CreativeCommonsAttributionNon-Commercial","Licences.CurrikiLicense","Licences.PublicDomain","Licences.CreativeCommonsAttributionNoDerivatives","Licences.CreativeCommonsAttributionNon-CommercialNoDerivatives","Licences.CreativeCommonsAttributionSharealike","Licences.CreativeCommonsAttributionNon-CommercialShareAlike","Licences.TeachersDomainDownloadShare"];
Curriki.data.licence.initial=Curriki.data.licence.list[0];Curriki.data.licence.data=[];
Curriki.data.licence.list.each(function(a){Curriki.data.licence.data.push([a,_("CurrikiCode.AssetLicenseClass_licenseType_"+a)])
});Curriki.data.licence.store=new Ext.data.SimpleStore({fields:["id","licence"],data:Curriki.data.licence.data});
Ext.ns("Curriki.data.gCCL");Curriki.data.gCCL.list=["0","1"];Curriki.data.gCCL.initial=Curriki.data.licence.list[0];
Curriki.data.gCCL.data=["0","1"];Curriki.data.gCCL.store=new Ext.data.SimpleStore({fields:["id","gCCL"],data:Curriki.data.gCCL.data});
Ext.ns("Curriki.data.fw_item");Curriki.data.fw_item.fwCheckListener=function(c,b){var a=Ext.getCmp("fw_items-validation");
if(a){a.setValue(a.getValue()+(b?1:-1))}if(b){if("undefined"!==typeof c.parentNode){if(!c.parentNode.ui.isChecked()){c.parentNode.ui.toggleCheck()
}}}else{if(Ext.isArray(c.childNodes)){c.childNodes.each(function(d){if(d.ui.isChecked()){d.ui.toggleCheck()
}})}}};Curriki.data.fw_item.fwMap={TREEROOTNODE:[{id:"FW_masterFramework.WebHome",parent:""}],"FW_masterFramework.WebHome":[{id:"FW_masterFramework.Arts",parent:"FW_masterFramework.WebHome"},{id:"FW_masterFramework.VocationalEducation",parent:"FW_masterFramework.WebHome"},{id:"FW_masterFramework.Education&Teaching",parent:"FW_masterFramework.WebHome"},{id:"FW_masterFramework.EducationalTechnology",parent:"FW_masterFramework.WebHome"},{id:"FW_masterFramework.Health",parent:"FW_masterFramework.WebHome"},{id:"FW_masterFramework.Information&MediaLiteracy",parent:"FW_masterFramework.WebHome"},{id:"FW_masterFramework.LanguageArts",parent:"FW_masterFramework.WebHome"},{id:"FW_masterFramework.Mathematics",parent:"FW_masterFramework.WebHome"},{id:"FW_masterFramework.Science",parent:"FW_masterFramework.WebHome"},{id:"FW_masterFramework.SocialStudies",parent:"FW_masterFramework.WebHome"},{id:"FW_masterFramework.ForeignLanguages",parent:"FW_masterFramework.WebHome"}],"FW_masterFramework.Information&MediaLiteracy":[{id:"FW_masterFramework.EvaluatingSources",parent:"FW_masterFramework.Information&MediaLiteracy"},{id:"FW_masterFramework.MediaEthics",parent:"FW_masterFramework.Information&MediaLiteracy"},{id:"FW_masterFramework.OnlineSafety",parent:"FW_masterFramework.Information&MediaLiteracy"},{id:"FW_masterFramework.ResearchMethods",parent:"FW_masterFramework.Information&MediaLiteracy"}],"FW_masterFramework.SocialStudies":[{id:"FW_masterFramework.Anthropology",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.Careers_5",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.Civics",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.CurrentEvents",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.Economics",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.Entrepreneurship",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.Geography",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.GlobalAwareness",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.Government",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.History Local",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.PoliticalSystems",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.Psychology",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.Religion",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.Research_0",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.Sociology",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.StateHistory",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.Technology_1",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.Thinking&ProblemSolving",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.UnitedStatesGovernment",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.UnitedStatesHistory",parent:"FW_masterFramework.SocialStudies"},{id:"FW_masterFramework.WorldHistory",parent:"FW_masterFramework.SocialStudies"}],"FW_masterFramework.Arts":[{id:"FW_masterFramework.Architecture",parent:"FW_masterFramework.Arts"},{id:"FW_masterFramework.Careers",parent:"FW_masterFramework.Arts"},{id:"FW_masterFramework.Dance",parent:"FW_masterFramework.Arts"},{id:"FW_masterFramework.DramaDramatics",parent:"FW_masterFramework.Arts"},{id:"FW_masterFramework.Film",parent:"FW_masterFramework.Arts"},{id:"FW_masterFramework.History",parent:"FW_masterFramework.Arts"},{id:"FW_masterFramework.Music",parent:"FW_masterFramework.Arts"},{id:"FW_masterFramework.Photography",parent:"FW_masterFramework.Arts"},{id:"FW_masterFramework.PopularCulture",parent:"FW_masterFramework.Arts"},{id:"FW_masterFramework.Technology",parent:"FW_masterFramework.Arts"},{id:"FW_masterFramework.VisualArts",parent:"FW_masterFramework.Arts"}],"FW_masterFramework.EducationalTechnology":[{id:"FW_masterFramework.Careers_0",parent:"FW_masterFramework.EducationalTechnology"},{id:"FW_masterFramework.IntegratingTechnologyintotheClassroom",parent:"FW_masterFramework.EducationalTechnology"},{id:"FW_masterFramework.UsingMultimedia&theInternet",parent:"FW_masterFramework.EducationalTechnology"}],"FW_masterFramework.VocationalEducation":[{id:"FW_masterFramework.Agriculture_0",parent:"FW_masterFramework.VocationalEducation"},{id:"FW_masterFramework.Business",parent:"FW_masterFramework.VocationalEducation"},{id:"FW_masterFramework.Careers_6",parent:"FW_masterFramework.VocationalEducation"},{id:"FW_masterFramework.OccupationalHomeEconomics",parent:"FW_masterFramework.VocationalEducation"},{id:"FW_masterFramework.School-to-work",parent:"FW_masterFramework.VocationalEducation"},{id:"FW_masterFramework.Technology_2",parent:"FW_masterFramework.VocationalEducation"},{id:"FW_masterFramework.Trade&Industrial",parent:"FW_masterFramework.VocationalEducation"}],"FW_masterFramework.Health":[{id:"FW_masterFramework.BodySystems&Senses",parent:"FW_masterFramework.Health"},{id:"FW_masterFramework.Careers_1",parent:"FW_masterFramework.Health"},{id:"FW_masterFramework.EnvironmentalHealth",parent:"FW_masterFramework.Health"},{id:"FW_masterFramework.HumanSexuality",parent:"FW_masterFramework.Health"},{id:"FW_masterFramework.MentalEmotionalHealth",parent:"FW_masterFramework.Health"},{id:"FW_masterFramework.Nutrition",parent:"FW_masterFramework.Health"},{id:"FW_masterFramework.PhysicalEducation",parent:"FW_masterFramework.Health"},{id:"FW_masterFramework.SafetySmokingSubstanceAbusePrevention",parent:"FW_masterFramework.Health"}],"FW_masterFramework.Education&Teaching":[{id:"FW_masterFramework.Accessibility",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.AdultEducation",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.BilingualEducation",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.ClassroomManagement",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.EarlyChildhoodEducation",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.EducationAdministration",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.EducationalFoundations",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.EducationalPsychology",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.InstructionalDesign",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.MeasurementEvaluation",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.Mentoring",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.MulticulturalEducation",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.StandardsAlignment",parent:"FW_masterFramework.Education&Teaching"},{id:"FW_masterFramework.TeachingTechniques",parent:"FW_masterFramework.Education&Teaching"}],"FW_masterFramework.ForeignLanguages":[{id:"FW_masterFramework.Alphabet",parent:"FW_masterFramework.ForeignLanguages"},{id:"FW_masterFramework.Careers_7",parent:"FW_masterFramework.ForeignLanguages"},{id:"FW_masterFramework.CulturalAwareness",parent:"FW_masterFramework.ForeignLanguages"},{id:"FW_masterFramework.Grammar",parent:"FW_masterFramework.ForeignLanguages"},{id:"FW_masterFramework.InformalEducation",parent:"FW_masterFramework.ForeignLanguages"},{id:"FW_masterFramework.Linguistics",parent:"FW_masterFramework.ForeignLanguages"},{id:"FW_masterFramework.ListeningComprehension",parent:"FW_masterFramework.ForeignLanguages"},{id:"FW_masterFramework.Reading",parent:"FW_masterFramework.ForeignLanguages"},{id:"FW_masterFramework.Speaking",parent:"FW_masterFramework.ForeignLanguages"},{id:"FW_masterFramework.Spelling",parent:"FW_masterFramework.ForeignLanguages"},{id:"FW_masterFramework.VocabularyWriting",parent:"FW_masterFramework.ForeignLanguages"},{id:"FW_masterFramework.FLWriting",parent:"FW_masterFramework.ForeignLanguages"}],"FW_masterFramework.Mathematics":[{id:"FW_masterFramework.Algebra",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.Appliedmathematics",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.Arithmetic",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.Calculus",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.Careers_3",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.DataAnalysis&Probability",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.Equations",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.Estimation",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.Geometry",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.Graphing",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.Measurement",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.NumberSense&Operations",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.Patterns",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.ProblemSolving",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.Statistics",parent:"FW_masterFramework.Mathematics"},{id:"FW_masterFramework.Trigonometry",parent:"FW_masterFramework.Mathematics"}],"FW_masterFramework.Science":[{id:"FW_masterFramework.Agriculture",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Astronomy",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Biology",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Botany",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Careers_4",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Chemistry",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Earthscience",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Ecology",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Engineering",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Generalscience",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Geology",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.HistoryofScience",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.LifeSciences",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Meteorology",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.NaturalHistory",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Oceanography",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Paleontology",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.PhysicalSciences",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Physics",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.ProcessSkills",parent:"FW_masterFramework.Science"},{id:"FW_masterFramework.Technology_0",parent:"FW_masterFramework.Science"}],"FW_masterFramework.LanguageArts":[{id:"FW_masterFramework.Alphabet_0",parent:"FW_masterFramework.LanguageArts"},{id:"FW_masterFramework.Careers_2",parent:"FW_masterFramework.LanguageArts"},{id:"FW_masterFramework.LanguageArts_Grammar",parent:"FW_masterFramework.LanguageArts"},{id:"FW_masterFramework.Journalism",parent:"FW_masterFramework.LanguageArts"},{id:"FW_masterFramework.Listening&Speaking",parent:"FW_masterFramework.LanguageArts"},{id:"FW_masterFramework.Literature",parent:"FW_masterFramework.LanguageArts"},{id:"FW_masterFramework.Phonics",parent:"FW_masterFramework.LanguageArts"},{id:"FW_masterFramework.ReadingComprehension",parent:"FW_masterFramework.LanguageArts"},{id:"FW_masterFramework.Research",parent:"FW_masterFramework.LanguageArts"},{id:"FW_masterFramework.Spelling_0",parent:"FW_masterFramework.LanguageArts"},{id:"FW_masterFramework.StoryTelling",parent:"FW_masterFramework.LanguageArts"},{id:"FW_masterFramework.Vocabulary",parent:"FW_masterFramework.LanguageArts"},{id:"FW_masterFramework.Writing",parent:"FW_masterFramework.LanguageArts"}]};
var fwItem="FW_masterFramework.WebHome";Curriki.data.fw_item.fwAddNode=function(c,d){var b={id:d,text:_("CurrikiCode.AssetClass_fw_items_"+d),checked:false,listeners:{checkchange:Curriki.data.fw_item.fwCheckListener}};
if("undefined"!==typeof c[d]){var a=[];c[d].each(function(e){a.push(Curriki.data.fw_item.fwAddNode(c,e.id))
});b.children=a;b.cls="fw-item fw-item-parent"}else{b.leaf=true;b.cls="fw-item fw-item-bottom"
}return b};Curriki.data.fw_item.fwChildren=Curriki.data.fw_item.fwAddNode(Curriki.data.fw_item.fwMap,"FW_masterFramework.WebHome").children;
Curriki.data.fw_item.getRolloverDisplay=function(e){var a=e||[];var f="";var d=Curriki.data.fw_item.fwMap;
if(a[0]==="FW_masterFramework.WebHome"){a.shift()}if("undefined"!==typeof a&&"undefined"!==typeof a[0]){var b="";
var g=a[0];var c=d["FW_masterFramework.WebHome"].find(function(h){return(d[h.id].find(function(k){return k.id==g
}))});if(!Ext.type(c)){b=_("CurrikiCode.AssetClass_fw_items_"+g)}else{c=c.id;b=_("CurrikiCode.AssetClass_fw_items_"+c)+" > "+_("CurrikiCode.AssetClass_fw_items_"+g)
}f+=Ext.util.Format.htmlEncode(b)+"<br />";if("undefined"!==typeof a[1]){var b="";
var g=a[1];var c=d["FW_masterFramework.WebHome"].find(function(h){return(d[h.id].find(function(k){return k.id==g
}))});if(!Ext.type(c)){b=_("CurrikiCode.AssetClass_fw_items_"+g)}else{c=c.id;b=_("CurrikiCode.AssetClass_fw_items_"+c)+" > "+_("CurrikiCode.AssetClass_fw_items_"+g)
}f+=Ext.util.Format.htmlEncode(b)+"<br />";if("undefined"!==typeof a[2]){f+="...<br />"
}}}else{f+=_("global.title.popup.none.selected")+"<br />"}return f};Ext.ns("Curriki.ui.component.asset");
Curriki.ui.component.asset.getFwTree=function(){return{xtype:"treepanel",loader:new Ext.tree.TreeLoader({preloadChildren:true}),id:"fw_items-tree",useArrows:true,autoHeight:true,border:false,cls:"fw-tree",animate:true,enableDD:false,containerScroll:true,rootVisible:true,root:new Ext.tree.AsyncTreeNode({text:_("CurrikiCode.AssetClass_fw_items_FW_masterFramework.WebHome"),id:"FW_masterFramework.WebHome",cls:"fw-item-top fw-item-parent fw-item",leaf:false,expanded:true,children:Curriki.data.fw_item.fwChildren})}
};Ext.ns("Curriki.assets");Curriki.assets={json_prefix:"/xwiki/curriki/assets",CreateAsset:function(a,b,c){Ext.Ajax.request({url:this.json_prefix,method:"POST",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{parent:a||"",publishSpace:b||""},scope:this,success:function(d,e){var f=d.responseText;
var g=f.evalJSON(true);if(!g||!g.assetPage){console.warn("Cannot create resource",d.responseText,e);
alert(_("add.servertimedout.message.text"))}else{c(g)}},failure:function(d,e){console.error("Cannot create resource",d,e);
alert(_("add.servertimedout.message.text"))}})},CopyAsset:function(a,b,c){Ext.Ajax.request({url:this.json_prefix,method:"POST",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{copyOf:a||"",publishSpace:b||""},scope:this,success:function(d,e){var f=d.responseText;
var g=f.evalJSON(true);if(!g||!g.assetPage){console.warn("Cannot copy resource",d.responseText,e);
alert(_("add.servertimedout.message.text"))}else{c(g)}},failure:function(d,e){console.error("Cannot copy resource",d,e);
alert(_("add.servertimedout.message.text"))}})},GetAssetInfo:function(a,b){Ext.Ajax.request({url:this.json_prefix+"/"+a,method:"GET",disableCaching:true,headers:{Accept:"application/json","Content-type":"application/json"},scope:this,success:function(c,d){var e=c.responseText;
var f=e.evalJSON(true);if(!f){console.warn("Cannot get resource metadata",c.responseText,d);
alert(_("add.servertimedout.message.text"))}else{b(f)}},failure:function(c,d){console.error("Cannot get resource metadata",c,d);
alert(_("add.servertimedout.message.text"))}})},GetMetadata:function(a,b){Ext.Ajax.request({url:this.json_prefix+"/"+a+"/metadata",method:"GET",disableCaching:true,headers:{Accept:"application/json","Content-type":"application/json"},scope:this,success:function(c,d){var e=c.responseText;
var f=e.evalJSON(true);if(!f){console.warn("Cannot get resource metadata",c.responseText,d);
alert(_("add.servertimedout.message.text"))}else{if("string"===typeof f.rightsList){f.rightsList=f.rightsList.evalJSON(true)
}b(f)}},failure:function(c,d){console.error("Cannot get resource metadata",c,d);alert(_("add.servertimedout.message.text"))
}})},SetMetadata:function(a,b,c){Ext.Ajax.request({url:this.json_prefix+"/"+a+"/metadata",method:"PUT",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:b,scope:this,success:function(d,e){var f=d.responseText;
var g=f.evalJSON(true);if(!g){console.warn("Cannot set resource metadata",d.responseText,e);
alert(_("add.servertimedout.message.text"))}else{c(g)}},failure:function(d,e){console.error("Cannot set resource metadata",d,e);
alert(_("add.servertimedout.message.text"))}})},CreateExternal:function(b,a,c){Ext.Ajax.request({url:this.json_prefix+"/"+b+"/externals",method:"POST",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{link:a},scope:this,success:function(d,e){var f=d.responseText;
var g=f.evalJSON(true);if(!g){console.warn("Cannot create external link",d.responseText,e);
alert(_("add.servertimedout.message.text"))}else{c(g)}},failure:function(d,e){console.error("Cannot create external link",d,e);
alert(_("add.servertimedout.message.text"))}})},CreateSubasset:function(b,d,a,c){Ext.Ajax.request({url:this.json_prefix+"/"+b+"/subassets",method:"POST",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{page:d,order:a},scope:this,success:function(e,f){var g=e.responseText;
var h=g.evalJSON(true);if(!h){console.warn("Cannot add subasset",e.responseText,f);
alert(_("add.servertimedout.message.text"))}else{Curriki.data.user.GetCollections(function(){if("function"===typeof c){c(h)
}})}},failure:function(e,f){console.error("Cannot add subasset",e,f);alert(_("add.servertimedout.message.text"))
}})},CreateFolder:function(a,b){Ext.Ajax.request({url:this.json_prefix+"/"+a+"/subassets",method:"POST",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{collectionType:"folder"},scope:this,success:function(c,d){var e=c.responseText;
var f=e.evalJSON(true);if(!f){console.warn("Cannot create folder",c.responseText,d);
alert(_("add.servertimedout.message.text"))}else{b(f)}},failure:function(c,d){console.error("Cannot create folder",c,d);
alert(_("add.servertimedout.message.text"))}})},CreateCollection:function(a,b){Ext.Ajax.request({url:this.json_prefix+"/"+a+"/subassets",method:"POST",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{collectionType:"collection"},scope:this,success:function(c,d){var e=c.responseText;
var f=e.evalJSON(true);if(!f){console.warn("Cannot create collection",c.responseText,d);
alert(_("add.servertimedout.message.text"))}else{b(f)}},failure:function(c,d){console.error("Cannot create collection",c,d);
alert(_("add.servertimedout.message.text"))}})},CreateVIDITalk:function(a,b,c){Ext.Ajax.request({url:this.json_prefix+"/"+a+"/viditalks",method:"POST",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{page:a,videoId:b},scope:this,success:function(d,e){var f=d.responseText;
var g=f.evalJSON(true);if(!g){console.warn("Cannot add video",d.responseText,e);alert(_("add.servertimedout.message.text"))
}else{c(g)}},failure:function(d,e){console.error("Cannot add video",d,e);alert(_("add.servertimedout.message.text"))
}})},Publish:function(a,b,c){Ext.Ajax.request({url:this.json_prefix+"/"+a+"/published",method:"PUT",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{space:b},scope:this,success:function(d,e){var f=d.responseText;
var g=f.evalJSON(true);if(!g){console.warn("Cannot publish resource",d.responseText,e);
alert(_("add.servertimedout.message.text"))}else{c(g)}},failure:function(d,e){console.error("Cannot publish resource",d,e);
alert(_("add.servertimedout.message.text"))}})},ReorderRootCollection:function(a,d,c,b,e){Ext.Ajax.request({url:"/xwiki/curriki/"+a+"/"+d+"/collections",method:"PUT",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{original:c,wanted:b},scope:this,success:function(f,g){var h=f.responseText;
var k=h.evalJSON(true);if(!k){console.warn("Cannot reorder",f.responseText,g)}else{e(k)
}},failure:function(f,g){console.error("Cannot reorder",f,g);if(f.status==412){if(f.responseText.search(/ 107 [^ ]+ 101:/)!==-1){var h="mycurriki.collections.reorder.";
if(a==="groups"){h="groups_curriculum_collections_reorder."}alert(_(h+"error"))}else{alert(_("add.servertimedout.message.text"))
}}else{alert(_("add.servertimedout.message.text"))}}})},SetSubassets:function(a,d,c,b,f){var e={wanted:c};
if(!Ext.isEmpty(d)){e.previousRevision=d}else{e.ignorePreviousRevision=true}if(!Ext.isEmpty(b)){e.logMessage=b
}Ext.Ajax.request({url:this.json_prefix+"/"+a+"/subassets",method:"PUT",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:e,scope:this,success:function(g,h){var k=g.responseText;
var l=k.evalJSON(true);if(!l){console.warn("Cannot save subassets",g.responseText,h);
alert(_("add.servertimedout.message.text"))}else{f(l)}},failure:function(g,h){console.error("Cannot save subassets",g,h);
if(g.status==412){if(g.responseText.search(/ 107 [^ ]+ 101:/)!==-1){alert(_("error: Collision while saving -- only some changes saved"))
}else{alert(_("add.servertimedout.message.text"))}}else{alert(_("add.servertimedout.message.text"))
}}})},UnnominateAsset:function(a,b){Ext.Ajax.request({url:this.json_prefix+"/"+a+"/unnominate",method:"PUT",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{},scope:this,success:function(c,d){var e=c.responseText;
var f=e.evalJSON(true);if(!f){console.warn("Cannot unnominate resource",c.responseText,d);
alert(_("add.servertimedout.message.text"))}else{b(f)}},failure:function(c,d){console.error("Cannot unnominate resource",c,d);
alert(_("add.servertimedout.message.text"))}})},NominateAsset:function(a,b,c){Ext.Ajax.request({url:this.json_prefix+"/"+a+"/nominate",method:"PUT",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{comments:b},scope:this,success:function(d,e){var f=d.responseText;
var g=f.evalJSON(true);if(!g){console.warn("Cannot nominate resource",d.responseText,e);
alert(_("add.servertimedout.message.text"))}else{c(g)}},failure:function(d,e){console.error("Cannot nominate resource",d,e);
alert(_("add.servertimedout.message.text"))}})},PartnerAsset:function(a,b){Ext.Ajax.request({url:this.json_prefix+"/"+a+"/partner",method:"PUT",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{},scope:this,success:function(c,d){var e=c.responseText;
var f=e.evalJSON(true);if(!f){console.warn("Cannot set as Partner resource",c.responseText,d);
alert(_("add.servertimedout.message.text"))}else{b(f)}},failure:function(c,d){console.error("Cannot set as Partner resource",c,d);
alert(_("add.servertimedout.message.text"))}})},SetAsterixReview:function(a,c,b){Ext.Ajax.request({url:this.json_prefix+"/"+a+"/assetManager",method:"PUT",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{action:"setAsterixReview",asterixReviewValue:b},scope:this,success:function(d,e){var f=d.responseText;
var g=f.evalJSON(true);if(!g){console.warn("Cannot set as Partner resource",d.responseText,e);
alert(_("add.servertimedout.message.text"))}else{c(g)}},failure:function(d,e){console.error("Cannot execute the action",d,e);
alert(_("add.servertimedout.message.text"))}})},RemoveAsterixReview:function(a,b){Ext.Ajax.request({url:this.json_prefix+"/"+a+"/assetManager",method:"PUT",headers:{Accept:"application/json","Content-type":"application/json"},jsonData:{action:"removeAsterixReview"},scope:this,success:function(c,d){var e=c.responseText;
var f=e.evalJSON(true);if(!f){console.warn("Cannot set as Partner resource",c.responseText,d);
alert(_("add.servertimedout.message.text"))}else{b(f)}},failure:function(c,d){console.error("Cannot execute the action",c,d);
alert(_("add.servertimedout.message.text"))}})},Flag:function(a,c,b,d){Ext.Ajax.request({url:"/xwiki/bin/view/FileCheck/Flag?xpage=plain&page="+(a||"")+"&reason="+(c||"")+"&altreason="+(b||"")+"&_dc="+(new Date().getTime()),method:"POST",headers:{Accept:"application/json","Content-type":"application/json"},params:{page:a||"",reason:c||"",altreason:b||""},scope:this,success:function(f,g){var h=f.responseText;
var l={};try{l=h.evalJSON(true)}catch(k){l=null}if(!l){console.warn("Could not flag resource",f.responseText,g);
alert(_("add.servertimedout.message.text"))}else{if(l.success){d(l)}else{console.warn("Could not flag resource",f.responseText,g);
alert("Flagging Failed")}}},failure:function(e,f){console.error("Cannot execute the action",e,f);
alert(_("add.servertimedout.message.text"))}})}};Ext.ns("Curriki.ui");Curriki.ui.InfoImg="/xwiki/skins/curriki8/icons/exclamation.png";
Ext.ns("Curriki.ui.dialog");Curriki.ui.dialog.Base=Ext.extend(Ext.Window,{title:_("Untitled"),border:false,modal:true,width:634,minWidth:400,minHeight:100,maxHeight:6000,autoScroll:false,constrain:true,collapsible:false,closable:false,resizable:false,monitorResize:true,shadow:false,defaults:{border:false},listeners:{afterlayout:function(b,a){console.log("afterlayout 2 on "+b);
if(this.afterlayout_maxheight){}else{if(b.getBox().height>b.maxHeight){b.setHeight(b.maxHeight);
b.center();this.afterlayout_maxheight=true;console.log("afterlayout_maxheight reached: "+b.maxHeight)
}else{b.setHeight("auto");console.log("set auto height")}}}},initComponent:function(){Curriki.ui.dialog.Base.superclass.initComponent.call(this)
}});Curriki.ui.dialog.Actions=Ext.extend(Curriki.ui.dialog.Base,{width:634,initComponent:function(){Curriki.ui.dialog.Actions.superclass.initComponent.call(this)
}});Ext.reg("dialogueactions",Curriki.ui.dialog.Actions);Curriki.ui.dialog.Messages=Ext.extend(Curriki.ui.dialog.Base,{width:500,initComponent:function(){Curriki.ui.dialog.Messages.superclass.initComponent.call(this)
}});Ext.reg("dialoguemessages",Curriki.ui.dialog.Messages);Curriki.ui.show=function(d,a){var c={xtype:d};
Ext.apply(c,a);var b=Ext.ComponentMgr.create(c);b.show();Ext.ComponentMgr.register(b)
};Ext.ns("Curriki.ui.treeLoader");Curriki.ui.treeLoader.Base=function(a){Curriki.ui.treeLoader.Base.superclass.constructor.call(this)
};Ext.extend(Curriki.ui.treeLoader.Base,Ext.tree.TreeLoader,{dataUrl:"DYNAMICALLY DETERMINED",setChildHref:false,setFullRollover:false,setAllowDrag:false,setUniqueId:false,disableUnviewable:true,hideUnviewable:false,hideInvalid:false,setTitleInRollover:false,truncateTitle:false,unviewableText:_("add.chooselocation.resource_unavailable"),unviewableQtip:_("add.chooselocation.resource_unavailable_tooltip"),createNode:function(attr){console.log("createNode: ",attr);
if(this.setFullRollover){if("string"!==Ext.type(attr.qtip)&&"string"===Ext.type(attr.description)&&"array"===Ext.type(attr.fwItems)&&"array"===Ext.type(attr.levels)&&"array"===Ext.type(attr.ict)){var desc=attr.description||"";
desc=Ext.util.Format.stripTags(desc);desc=Ext.util.Format.ellipsis(desc,256);desc=Ext.util.Format.htmlEncode(desc);
var lastUpdated=attr.lastUpdated||"";var title=attr.displayTitle||attr.title;var fw=Curriki.data.fw_item.getRolloverDisplay(attr.fwItems||[]);
var lvl=Curriki.data.el.getRolloverDisplay(attr.levels||[]);var ict=Curriki.data.ict.getRolloverDisplay(attr.ict||[]);
var qTipFormat="";if(this.setTitleInRollover){qTipFormat="{1}<br />{0}<br /><br />"
}qTipFormat=qTipFormat+"{3}<br />{2}<br /><br />";if(lastUpdated!==""){qTipFormat=qTipFormat+"{11}<br />{10}<br /><br />"
}qTipFormat=qTipFormat+"{5}<br />{4}<br />{7}<br />{6}<br />{9}<br />{8}";attr.qtip=String.format(qTipFormat,title,_("global.title.popup.title"),desc,_("global.title.popup.description"),fw,_("global.title.popup.subject"),lvl,_("global.title.popup.educationlevel"),ict,_("global.title.popup.ict"),lastUpdated,_("global.title.popup.last_updated"))
}}if("string"===typeof attr.id){var p=Curriki.ui.treeLoader.Base.superclass.createNode.call(this,attr);
if(this.truncateTitle!==false){p.setText(Ext.util.Format.ellipsis(p.text,Ext.num(this.truncateTitle,125)))
}return p}attr.pageName=attr.assetpage||attr.collectionPage;if(attr.assetType=="Protected"){attr.category="unknown";
attr.subcategory="protected"}attr.category=Ext.value(attr.category,"unknown");attr.subcategory=Ext.value(attr.subcategory,"unknown");
var childInfo={id:this.setUniqueId?Curriki.id(attr.pageName):attr.pageName,text:attr.displayTitle||attr.title,qtip:attr.qtip||attr.description,cls:String.format("resource-{0} category-{1} subcategory-{1}_{2}",attr.assetType,attr.category,attr.subcategory),allowDrag:("boolean"==typeof attr.allowDrag)?attr.allowDrag:this.setAllowDrag,allowDrop:false};
if(!Ext.isEmpty(attr.addCls)){childInfo.cls+=" "+attr.addCls}if(this.setChildHref){childInfo.href="/xwiki/bin/view/"+attr.pageName.replace(".","/");
childInfo.onclick="return false;"}if(attr.rights&&!attr.rights.view){childInfo.text=this.unviewableText;
childInfo.qtip=this.unviewableQtip;if(this.disableUnviewable){childInfo.disabled=this.disableUnviewable
}childInfo.allowDrop=false;childInfo.leaf=true}else{if(attr.assetType&&attr.assetType.search(/Composite$/)===-1){childInfo.leaf=true
}else{if(attr.assetType){childInfo.leaf=false;childInfo.allowDrop=(attr.rights&&attr.rights.edit)||false
}}}if(attr.rights){if(attr.rights.view){childInfo.cls=childInfo.cls+" rights-viewable"
}else{childInfo.cls=childInfo.cls+" rights-unviewable";childInfo.hidden=(this.hideUnviewable||(this.hideInvalid&&attr.assetType&&attr.assetType.search(/Invalid/)!==-1))
}if(attr.rights.edit){childInfo.cls=childInfo.cls+" rights-editable"}else{childInfo.cls=childInfo.cls+" rights-uneditable"
}}Ext.apply(childInfo,attr);if(this.truncateTitle!==false){childInfo.text=Ext.util.Format.ellipsis(childInfo.text,Ext.num(this.truncateTitle,125))
}if(this.baseAttrs){Ext.applyIf(childInfo,this.baseAttrs)}if(this.applyLoader!==false){childInfo.loader=this
}if(typeof attr.uiProvider=="string"){childInfo.uiProvider=this.uiProviders[attr.uiProvider]||eval(attr.uiProvider)
}var retNode=(childInfo.leaf?new Ext.tree.TreeNode(childInfo):new Ext.tree.AsyncTreeNode(childInfo));
retNode.hidden=childInfo.hidden;return retNode},requestData:function(b,c){if(b.attributes.currikiNodeType==="group"){this.dataUrl="/xwiki/curriki/groups/"+(b.attributes.pageName||b.id)+"/collections"
}else{if(b.attributes.currikiNodeType==="myCollections"){this.dataUrl="myCollections"
}else{if(b.attributes.currikiNodeType==="myGroups"){this.dataUrl="myGroups"}else{this.dataUrl="/xwiki/curriki/assets/"+(b.attributes.pageName||b.id)+"/subassets"
}}}if(this.fireEvent("beforeload",this,b,c)!==false){if(this.dataUrl.indexOf("/")===0){this.transId=Ext.Ajax.request({method:"GET",url:this.dataUrl,disableCaching:true,headers:{Accept:"application/json"},success:this.handleResponse,failure:this.handleFailure,scope:this,argument:{callback:c,node:b},params:""})
}else{this.transId=Math.floor(Math.random()*65535);var a={argument:{callback:c,node:b}};
if(b.attributes.currikiNodeType==="myCollections"){Curriki.settings.fetchMyCollectionsOnly=true;
Curriki.data.user.GetCollections((function(){if(Curriki.errors.fetchFailed){a.responseText='[{"id":"NOUSERCOLLECTIONS", "text":"'+_("add.chooselocation.collections.user.empty")+'", "allowDrag":false, "allowDrop":false, "leaf":true}]';
this.handleFailure(a)}else{if(Curriki.data.user.collectionChildren.length>0){a.responseText=Ext.util.JSON.encode(Curriki.data.user.collectionChildren)
}else{a.responseText='[{"id":"NOUSERCOLLECTIONS", "text":"'+_("add.chooselocation.collections.user.empty")+'", "allowDrag":false, "allowDrop":false, "leaf":true}]'
}this.handleResponse(a)}}).createDelegate(this))}else{if(b.attributes.currikiNodeType==="myGroups"){Curriki.data.user.GetGroups((function(){if(Curriki.errors.fetchFailed){a.responseText='[{"id":"NOGROUPCOLLECTIONS", "text":"'+_("add.chooselocation.groups.empty")+'", "allowDrag":false, "allowDrop":false, "leaf":true}]';
this.handleFailure(a)}else{if(Curriki.data.user.groupChildren.length>0){a.responseText=Ext.util.JSON.encode(Curriki.data.user.groupChildren)
}else{a.responseText='[{"id":"NOGROUPCOLLECTIONS", "text":"'+_("add.chooselocation.groups.empty")+'", "allowDrag":false, "allowDrop":false, "leaf":true}]'
}this.handleResponse(a)}}).createDelegate(this))}}}}else{if(typeof c=="function"){c()
}}}});Ext.ns("Curriki.ui.login");Curriki.ui.login.displayLoginDialog=function(b){if(Curriki.ui.login.loginDialog&&window.opener.top.Curriki.ui.login.loginDialog.isVisible()){Curriki.ui.login.loginDialog.hide()
}var a=630,c=400;if(window.innerWidth&&window.innerWidth<a){a=Math.round(window.innerWidth*0.95)
}if(b.indexOf("?")>=0){b=b+"&framed=true"}else{b=b+"?framed=true"}var d=".x-window .x-window-tl, .x-panel-ghost .x-window-tl";
if(Ext&&Ext.isIE){d=".x-window .x-window-tl"}Ext.util.CSS.updateRule(d,"background-color","#4E83C7");
Curriki.ui.login.loginDialog=new Ext.Window({title:_("join.login.title"),border:false,id:"loginDialogWindow",scrollbars:false,modal:true,width:a,minWidth:400,minHeight:100,maxHeight:575,autoScroll:false,constrain:true,collapsible:false,closable:false,resizable:false,monitorResize:true,shadow:false,defaults:{border:false},html:"<iframe style='border:none' frameBorder='0' name='curriki-login-dialog' id='loginIframe' src='"+b+"' width='"+(a-5)+"' height='"+(c-31)+"'/>"});
Curriki.ui.login.loginDialog.headerCls="registration-dialog-header";Curriki.ui.login.loginDialog.show();
return Ext.get("loginIframe").dom.contentWindow};Curriki.ui.login.readScrollPos=function(a){if(typeof(a)=="undefined"){a=window
}try{if(a&&a.Ext){var b=a.Ext.getBody().getScroll();return escape("&l="+b.left+"&t="+b.top)
}else{return""}}catch(c){Curriki.console.log(c);return""}};Curriki.ui.login.restoreScrollPos=function(b){try{Curriki.console.log("Intending to restoreScroll.");
if(!b.match(/t=[0-9]/)){Curriki.console.log("No coordinates passed.");return}var a=b.replace(/.*l=([0-9]+).*/,"$1");
var c=b.replace(/.*t=([0-9]+).*/,"$1");if(typeof(a)=="undefined"){a=0}if(typeof(c)=="undefined"){c=0
}Curriki.console.log("Would scroll to "+a+":"+c+" if I were IE.");if(Ext.isIE){Curriki.console.log("Scrolling by "+a+":"+c);
window.scrollBy(a,c)}}catch(d){Curriki.console.log(d)}};Curriki.ui.login.ensureProperBodyCssClass=function(){window.onload=function(){try{if(document.body){var a=document.body.className;
if(a){document.body.className=a+" insideIframe"}else{if(!Ext.isIE()){document.body.className="insideIframe"
}}}}catch(b){Curriki.console.log(b)}}};Curriki.ui.login.popupPopupAndIdentityAuthorization=function(f,c,b){try{Curriki.console.log("Opening pop-up that will request authorization.");
window.top.name="currikiMainWindow";if(!Ext.isIE){Curriki.ui.login.popupIdentityAuthorization2(c,null)
}var a=Curriki.ui.login.displayLoginDialog("/xwiki/bin/view/Registration/RequestAuthorization?xpage=popup&provider="+f+"&to="+encodeURIComponent(c)+"&xredirect="+encodeURIComponent(b));
if(Ext.isIE){Curriki.ui.login.popupIdentityAuthorization2(c,null)}window.Curriki.ui.login.windowThatShouldNextGoTo=a
}catch(d){Curriki.console.log(d)}};Curriki.ui.login.popupIdentityAuthorization=function(a){return Curriki.ui.login.popupIdentityAuthorization2(a,null)
};Curriki.ui.login.popupIdentityAuthorization2=function(a,b){return Curriki.ui.login.popupAuthorization4(a,b,"curriki-login-dialog","curriki_login_authorize")
};Curriki.ui.login.popupGCheckout=function(a,b){if(!Ext.isIE){Curriki.ui.login.popupAuthorization4(a,window,"curriki-login-dialog","checkoutWindow")
}if(b&&b.startsWith("close-now-")){window.top.location.href=b.substring(10)}else{if(b){window.location.href=b
}}if(Ext.isIE){Curriki.ui.login.popupAuthorization4(a,window,"curriki-login-dialog","checkoutWindow")
}window.top.name="currikiMainWindow"};Curriki.ui.login.popupAuthorization4=function(c,f,e,d){Curriki.console.log("Opening authorization to "+c);
if(window!=window.top){window.name="curriki-login-dialog"}if(e){window.name=e}if(d){}else{d="curriki_login_authorize"
}var b;if(window.frames[d]){Curriki.console.log("Re-using window.");b=window.frames[d];
b.location.href=c}else{Curriki.console.log("Creating window.");var a=Math.max(0,(screen.width-980)/2);
var g=Math.max(0,(screen.height-600)/2);b=window.open(c,d,"toolbar=no,scrollbars=yes,status=yes,menubar=no,resizable=yes,width=980,height=600,left="+a+",top="+g);
if(!b||typeof(b)==undefined){if(d=="checkoutWindow"){if(window.localtion.indexOf()){window.location="http://welcome.curriki.org/about-curriki/donate/"
}}else{if(d=="curriki_login_authorize"){window.location.pathname="/xwiki/bin/view/Registration/ManualLogin"
}}}}window.focusIt=window.setInterval(function(){window.clearInterval(window.focusIt);
b.focus()},100);window.Curriki.ui.login.authorizeDialog=b;window.top.Curriki.ui.login.authorizeDialog=b;
if(f&&f!=null){window.Curriki.ui.login.windowThatShouldNextGoTo=f}return false};Curriki.ui.login.finishAuthorizationPopup=function(a,d,f,c){Curriki.console.log("Finishing popup, (toTop? "+c+") target: "+a);
if(typeof(d)=="undefined"||d==window){d=window.open(a,"currikiMainWindow")}if(d){Curriki.console.log("We are in popup, closing and opening popup.");
var e=d;if(d.Curriki.ui.login.windowThatShouldNextGoTo){e=d.Curriki.ui.login.windowThatShouldNextGoTo
}Curriki.console.log("targetWindow: "+e+" with force to top "+c);if(c){e=e.top}else{if(d.Ext&&d.Ext.get("loginIframe")){e=d.Ext.get("loginIframe").dom.contentWindow
}}if(e&&e.location){e.location.href=a;f.setInterval(function(){try{e.focus()}catch(g){Curriki.console.log(g)
}try{f.close()}catch(g){Curriki.console.log(g)}},20)}else{var b=window;if(c){b=b.top
}b.location.href=a}return false}else{Curriki.console.log("No popup parent found... ah well.");
var b=f;if(c){b=b.top}b.location.href=a}};Curriki.ui.login.makeSureWeAreFramed=function(a){if(window==window.top){if(!a||a==null){a=window.location.href
}Curriki.ui.login.displayLoginDialog(a)}else{if(window.name!="curriki-login-dialog"&&a&&a!=null){Curriki.console.log("Redirecting to "+a);
var b=window.opener;if(typeof(b)!="object"){b=window.top}b.replace(a);window.setInterval("window.close();",50);
return}}};Curriki.ui.login.showLoginLoading=function(f,b){try{if(navigator.appVersion.indexOf(" Chrome")>0){Curriki.showLoading(f,true);
if(window.parent&&window.parent.Ext&&window.parent.Ext.get("loginIframe")){var c=window.parent.Ext.get("loginIframe");
Curriki.console.log("will set bg on "+c);while(typeof(c)!="undefined"&&c!=null&&c.setStyle){if(c.id&&"loginDialogWindow"==c.id){break
}Curriki.console.log("setting bg on "+c);c.setStyle("background-color","#DDD");c=c.parent()
}}}else{if(window.parent&&window.parent.Ext&&window.parent.Ext.get("loginIframe")){window.parent.Curriki.showLoading(f,true)
}else{Curriki.showLoading(f,b)}}}catch(a){Curriki.console.log(a)}};Curriki.ui.login.hideLoginLoading=function(){try{if(navigator.appVersion.indexOf(" Chrome")>0){Curriki.hideLoading(true);
if(window.parent&&window.parent.Ext&&window.parent.Ext.get("loginIframe")){var b=window.parent.Ext.get("loginIframe");
while(typeof(b)!="undefined"&&b!=null&&b.setStyle){if(b.id&&"loginDialogWindow"==b.id){break
}b.setStyle("background-color","white");b=b.parent()}}}else{if(window.parent&&window.parent.Ext&&window.parent.Ext.get("loginIframe")){window.parent.Curriki.hideLoading(true)
}else{Curriki.hideLoading(true)}}}catch(a){Curriki.console.log(a)}};Ext.namespace("Curriki.ui.login.liveValidation");
Curriki.ui.login.liveValidation=function(){var a=new Array();return{queue:a,launchCheckFieldRequest:function(c,e,d){Curriki.ui.login.liveValidation.notifyValidationResult(e,"waiting");
Curriki.Ajax.beforerequest=function(){};var b;console.log("launching check field request "+e+" of name "+e.dom.name);
if(e.dom&&e.dom.name&&e.dom.name=="postalCode"){b=Ext.Ajax.request({url:"/locations",method:"GET",headers:{Accept:"application/json","Content-type":"application/json"},params:{q:"postalCode:"+e.dom.value,fl:"cityName,stateCode,long,lati",rows:1},scope:this,success:function(f,g){var k=f.responseText;
var h=k.evalJSON(true);if(console){console.log("Results: ",h)}window.results=h;var m=h.response.docs;
if(!m||m.length==0||!(m[0].cityName&&m[0].stateCode)){if(console){console.log("Docs returned unusable.",m)
}Ext.get("postalCode_results").dom.innerHTML="-";Curriki.ui.login.liveValidation.notifyValidationResult(e,false)
}else{var l=m[0];if(console){console.log(l.cityName+" "+l.stateCode,l)}Curriki.ui.login.liveValidation.updatePostalCodeResult(l.cityName,l.stateCode,l["long"],l.lati);
Curriki.ui.login.liveValidation.notifyValidationResult(e,true)}},failure:function(f,g){console.error("Cannot resolve location",f,g)
}})}else{b=Ext.Ajax.request({url:"/xwiki/bin/view/Registration/CheckValid",headers:{Accept:"application/json"},method:"GET",failure:function(f,g){Curriki.ui.login.liveValidation.queriedValue=d.value;
Curriki.ui.login.liveValidation.notifyValidationResult(e,null);Curriki.console.log("failed validation: ",f,g)
},success:function(f,g){var h=f.responseText;if(h){h=h.trim()}Curriki.console.log("Response: "+h);
a.remove(d);if(d.value!=e.getValue()){return}Curriki.ui.login.liveValidation.notifyValidationResult(e,"true"==h)
},params:{what:e.dom.name,value:c,xpage:"plain"},scope:this})}return b},updatePostalCodeResult:function(c,f,d,e){if(c){}else{c=""
}if(f){}else{f=""}if(d){}else{d=""}if(e){}else{e=""}var b="-";if(c&&f){b=c+", "+f
}Ext.get("postalCode_results").dom.innerHTML=b;Ext.get("city_input").dom.value=c;
Ext.get("state_input").dom.value=f;Ext.get("longitude_input").dom.value=d;Ext.get("latitude_input").dom.value=e
},notifyValidationResult:function(f,c){Curriki.console.log("Notifying validation result "+c+" on field "+f);
try{if(f){}else{Curriki.console.log("Warning: missing field.");return}window.lastField=f;
var b=f.parent();if(null==c){b.removeClass("okField");b.removeClass("waiting");b.removeClass("warningField");
if(f.dom&&f.dom.name&&"postalCode"==f.dom.name){Curriki.ui.login.liveValidation.updatePostalCodeResult(null,null,null,null)
}}else{if("waiting"==c){b.addClass("waiting")}else{if(true==c||"true"==c){b.removeClass("waiting");
b.removeClass("warningField");b.addClass("okField")}else{if(false==c||"false"==c){b.removeClass("waiting");
b.removeClass("okField");b.addClass("warningField")}}}}}catch(d){Curriki.console.log("Error: ",d)
}},activate:function(b){Ext.Ajax.purgeListeners();Ext.each(b,function(d){Curriki.console.log("Registering on "+d);
var c=Ext.get(d);if(c){}else{Curriki.console.log("Not found: "+d);return}if(c.purgeListeners){c.purgeListeners()
}c.addListener("blur",function(e){Curriki.console.log("Focus-out...");Curriki.ui.login.liveValidation.queueQueryNow(c);
Curriki.ui.login.liveValidation.stopPolling()});c.addListener("focus",function(e){Curriki.console.log("Focus-in...");
var f=window.setInterval(function(){clearInterval(f);Curriki.ui.login.liveValidation.startPollingTextField(c)
},50)})})},queueQueryNow:function(f){var h=f.dom.name;var d=f.dom.value;Curriki.console.log("Validation on field "+h+" with value '"+d+"'.");
if(h!="email"&&h!="username"&&h!="postalCode"){var g=false;var b=h=="firstName"||h=="lastName"||h=="password";
if(h=="agree"){g=d!="0"}if(h=="member_type"){g=d!="-"}if(h=="firstName"||h=="lastName"){g=d.length>=1
}if(h=="password"){g=d.length>=5&&!(d.indexOf(" ")>-1)}Curriki.console.log("passed? "+g+".");
if(g==false){if(b){Curriki.ui.login.liveValidation.notifyValidationResult(f,null)
}else{Curriki.ui.login.liveValidation.notifyValidationResult(f,false)}}if(g==true){Curriki.ui.login.liveValidation.notifyValidationResult(f,true)
}return}var e=new Object();e.value=f.getValue();Curriki.ui.login.liveValidation.queriedValue=f.getValue();
Curriki.console.log("Queuing query for "+e.value);if(typeof(e.value)=="undefined"||e.value==null){Curriki.console.log("Undefined value, stop.");
return}if(typeof(e.value)!="undefined"&&e.value.length<2){Curriki.ui.login.liveValidation.notifyValidationResult(f,null);
return}for(x in a){if(x.value==e.value){var c=a.indexOf(x);if(c>0){for(j=c-1;j>=0;
j--){a[j+1]=a[j]}}Curriki.console.log("Swapping existing queue entries.");a[0]=x;
return}}Curriki.console.log("Launching in queue.");e.request=this.launchCheckFieldRequest(e.value,f,e);
a[a.length]=e},intervalPointer:null,startedPollingTime:null,inputFieldBeingPolled:null,queriedValue:null,lastValue:null,startPollingTextField:function(b){var d=Curriki.ui.login.liveValidation;
if(b){}else{return}if(d.intervalPointer&&d.intervalPointer!=null){d.stopPolling()
}Curriki.console.log("Start polling on "+d);d.inputFieldBeingPolled=b;d.startedPollingTime=new Date().getTime();
var c=50;if(Ext.isIE){c=300}d.intervalPointer=window.setInterval(d.inputFieldPoll,c)
},stopPolling:function(){Curriki.console.log("Stop polling.");try{var b=Curriki.ui.login.liveValidation;
if(b.intervalPointer&&b.intervalPointer!=null){window.clearInterval(b.intervalPointer)
}b.startedPollingTime=null;b.inputFieldBeingPolled=null}catch(c){Curriki.console.log(c)
}},inputFieldPoll:function(){var d=Curriki.ui.login.liveValidation;var b=d.inputFieldBeingPolled;
if(b){}else{return}var c=new Date().getTime();if(d.startedPollingTime&&d.startedPollingTime==null){d.startedPollingTime=c
}var e=b.dom.value;if(typeof(e)!="undefined"){if(typeof(d.lastValue)!="undefined"){if(!(e==d.lastValue)){d.lastChanged=c;
d.lastValue=e}else{if(d.lastChanged&&c-d.lastChanged>200&&(d.lastChanged>d.lastChecked||d.lastChecked===undefined)&&(typeof(d.queriedValue)=="undefined"||d.queriedValue!=e)){d.lastChecked=c;
d.queueQueryNow(b)}}}else{d.lastValue=e;d.lastChanged=c}}else{Curriki.console.log("Giving up value undefined.")
}}}}();function postMessageHandler(c){console.log("postMessage: ",c);var b=c.data;
var a=b.substring(0,b.indexOf(":"));var g=b.indexOf(":");var e=b.indexOf(":",g+1);
var f=b.substring(g+1,e);var d=b.substring(e+1);switch(a){case"resize":console.log("received resize event (resize "+f+" to "+d+")");
window.resizeThatCurrikiIframe(f,d);break}}function resizeThatCurrikiIframe(c,b){var a="currikiIFrame_"+c;
if(c.startsWith("currikiIFrame")){a=c}var d=document.getElementById(a);if(d){d.setAttribute("style",b);
d.parentNode.setAttribute("style",b)}else{console.log("No frame found for "+a)}}if(typeof window.attachEvent==="function"||typeof window.attachEvent==="object"){console.log("attached Listener to event via window.attachEvent");
window.attachEvent("onmessage",postMessageHandler)}else{if(typeof window.addEventListener==="function"){console.log("attached Listener to event via window.addEvenListener");
window.addEventListener("message",postMessageHandler,false)}else{if(typeof document.attachEvent==="function"){console.log("cors iframe communication is not possible");
document.attachEvent("onmessage",postMessageHandler)}else{console.log("Frame communication not possible")
}}}(function(){Ext.ns("Curriki.ui.Rating");var b=[];for(var a=0;a<5;a++){b.push(_("CurrikiCode.AssetClass_member_rating_"+(a+1)))
}Curriki.ui.Rating=Ext.extend(Ext.form.NumberField,{fieldClass:"x-form-field x-form-rating-field",allowDecimals:false,allowNegative:false,minValue:0,maxValue:5,unit:17,wrapClass:"ux-form-rater-wrap",starsClass:"ux-form-rater-stars",hoverClass:"ux-form-rater-hover",voteClass:"ux-form-rater-vote",votedClass:"ux-form-rater-voted",textRightClass:"ux-form-rater-text-right",hoverText:b,displayValue:undefined,ratedValue:undefined,hoverValue:undefined,rated:false,initComponent:function(){Curriki.ui.Rating.superclass.initComponent.call(this);
this.addEvents("beforerating","rate")},onRender:function(d,c){Curriki.ui.Rating.superclass.onRender.apply(this,arguments);
this.wrap=this.el.wrap({cls:this.wrapClass});if(Ext.isIE){this.wrap.setHeight(this.unit)
}this.el.addClass("x-hidden");this.createStars();this.createTextContainers();this.displayValue=(this.displayValue>this.maxValue)?this.maxValue:this.displayValue;
if(this.displayValue>0||this.getValue()>0){this.displayRating()}},initEvents:function(){Curriki.ui.Rating.superclass.initEvents.call(this);
var d=this.getStarsContainer();var c=this.getStars();c.on("mouseover",this.displayHover,this);
c.on("mouseout",this.removeHover,this);c.on("click",this.rate,this);c.on("mouseup",this.rate,this)
},displayHover:function(h){if(this.disabled){return}var g=Ext.get(h.getTarget());
g.addClass(this.hoverClass);var f=this.getRating();f.hide();var c=this.getStars();
var d=0;while(c.item(d)!=null){if(c.item(d)==g){this.hoverValue=this.maxValue-d;if(this.hoverText instanceof Array){if(!Ext.isEmpty(this.hoverText[this.hoverValue-1])){this.setRightText(this.hoverText[this.hoverValue-1])
}}return}d++}},removeHover:function(f){if(this.disabled){return}var d=this.getRating();
d.show();var c=f.getTarget();Ext.fly(c).removeClass(this.hoverClass);this.setRightText("")
},rate:function(d){if(this.disabled){return}var c=this.hoverValue;this.setValue(c);
if(this.fireEvent("beforerating",this)===false){return}this.removeHover(d);this.onBlur();
this.rated=true;this.displayRating(c);this.fireEvent("rate",this,c)},createStars:function(){if(this.getStars().getCount()>0){return
}var d=this.wrap.createChild({tag:"ul",cls:this.starsClass}).setSize(this.unit*this.maxValue,this.unit);
var g=new Ext.Template('<li class="rating"></li>');var f=new Ext.Template('<li class="star"></li>');
g.append(d,[],true).setHeight(this.unit);for(var c=this.maxValue;c>0;c--){var e=f.append(d,[],true);
e.setSize(this.unit*c,this.unit)}this.alignStars()},createTextContainers:function(){var c=this.getStarsContainer();
if(!this.textRightContainer){this.textRightContainer=Ext.DomHelper.insertAfter(c,{tag:"span",cls:this.textRightClass},true);
this.textRightContainer.addClass("x-hidden")}},setRightText:function(c){this.textRightContainer.dom.innerHTML=c;
if(c==null||c==""){this.textRightContainer.addClass("x-hidden")}else{this.textRightContainer.removeClass("x-hidden")
}},getRightText:function(){return this.textRightContainer.dom.innerHTML},displayRating:function(d,f){var e=this.getRating();
if(Ext.isEmpty(d)){d=(this.displayValue==null)?this.getValue():this.displayValue;
d=Ext.isEmpty(d)?0:d}if(this.ratedValue>0){d=this.ratedValue;this.rated=true}var c=function(g,h){if(f==true){e.replaceClass(h,g)
}else{e.replaceClass(g,h)}};c(this.votedClass,this.voteClass);e.setWidth(d*this.unit);
return},getStars:function(){return this.wrap.select("li.star",true)},getStarsContainer:function(){return this.wrap.select("."+this.starsClass,true).item(0)
},getRating:function(){return this.wrap.select("li.rating",true)},alignStars:function(){var d=this.getStarsContainer();
var e=this.getRating();var c=this.getStars();var g=(d.findParent(".x-form-item",5))?true:false;
if(false&&!g){var f=Ext.fly(document.body).getAlignToXY(d)[0];e.setLeft(f);c.setLeft(f)
}else{e.alignTo(d,"tl");c.alignTo(d,"tl")}},onDisable:function(){Curriki.ui.Rating.superclass.onDisable.call(this);
this.wrap.addClass("x-item-disabled")},onEnable:function(){Curriki.ui.Rating.superclass.onEnable.call(this);
this.wrap.removeClass("x-item-disabled")},onHide:function(){this.wrap.addClass("x-hidden")
},onShow:function(){this.wrap.removeClass("x-hidden")}});Ext.reg("rating",Curriki.ui.Rating)
})();function videoInsert(c,d,b){var e=document.createElement("script");e.type="text/javascript";
e.src=window.videoPrefixToDownload+c+"-sizes.js";var a=document.getElementsByTagName("script")[0];
a.parentNode.insertBefore(e,a);if(typeof(window.videoTitles)!="object"){window.videoTitles=new Object()
}if(typeof(window.videoFullNames)!="object"){window.videoFullNames=new Object()}window.videoTitles[c]=d;
window.videoFullNames[c]=b;window.setTimeout("videoWatchSizesArrived('"+c+"');",50)
}function videoWatchSizesArrived(c){var b=window["video_"+c+"_sizes"];window.numWatches=window.numWatchers||new Object();
if(typeof(window.numWatches[c])!="number"){window.numWatches[c]=0}if(b){videoNotifyVideoSizeArrived(c,b)
}else{if(window.numWatches[c]<500){var a=50;window.numWatches[c]=window.numWatches[c]+1;
if(window.numWatches[c]>200){a=a*5}window.setTimeout("videoWatchSizesArrived('"+c+"');",a)
}}}function videoNotifyVideoSizeArrived(h,b){var l=Ext.get("video_img_"+h+"_image");
if(typeof(b)=="string"){if(console){console.log("size is still a string, display it: "+b)
}if(l){l=l.parent();l.setSize(320,80);var d=_(b);var a=_("video.errors.reportErrorsToEmail");
a="mailto:"+_("video.errors.reportErrorsToEmail")+"?subject="+encodeURI(_(d))+"&body="+encodeURI(_(b+".details",[a,h]));
if(b.startsWith("video.errors.")||b.startsWith("video.processingMessages")){}d=d+"</p><p style='font-size:small'>"+_(b+".details",[a,h]);
l.update("<div width='320' height='240'><p>"+d+"</p></div>")}}else{if(typeof(b)=="object"){if(l){l.setSize(b[0].width,b[0].height);
l.dom.setAttribute("src",window.videoPrefixToDownload+b[0].image)}for(var e=0;e<b.length;
e++){var o=b[e];o.file=window.videoPrefixToDownload+o.file}var k=window.videoFullNames[h];
var c="http://"+location.host+"/xwiki/bin/view/"+k.replace(".","/")+"?viewer=embed";
var g="<iframe width='558' height='490' \n src='"+c+"'></iframe>";jwplayer("video_div_"+h).setup({playlist:[{image:window.videoPrefixToDownload+b[0].image,sources:b,width:b[0].width,height:b[0].height}],ga:{},sharing:{code:encodeURI(g),link:c,title:_("video.sharing.title")}})
}}var f=window["video_"+h+"_originalName"];if(f){Ext.get("download_original_"+h+"_div").setVisible(true);
var n=f.substring(f.lastIndexOf(".")+1);Ext.get("download_original_"+h+"_div").addClass("filetype-"+n);
Ext.get("video_download_link_"+h).dom.setAttribute("href",window.videoPrefixToDownload.replace("/deliver/","/original/")+f+"?forceDownload=1");
Ext.get("video_download_link_"+h+"_text").dom.setAttribute("href",window.videoPrefixToDownload.replace("/deliver/","/original/")+f+"?forceDownload=1")
}}function videoDownloadOriginal(b){var a=window["video_"+b+"_originalName"];location.href=window.videoPrefixToDownload.replace("/deliver/","/original/")+a+"?forceDownload=1";
return false};