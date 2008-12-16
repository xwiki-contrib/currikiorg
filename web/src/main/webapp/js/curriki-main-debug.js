Array.prototype.contains = function(element) {
	return this.indexOf(element) !== -1;
};

Ext.namespace("Ext.ux"); 

/** 
 * @class Ext.ux.DDView 
 * A DnD enabled version of Ext.View. 
 * @param {Element/String} container The Element in which to create the View. 
 * @param {String} tpl The template string used to create the markup for each element of the View 
 * @param {Object} config The configuration properties. These include all the config options of 
 * {@link Ext.View} plus some specific to this class.<br> 
 * <p> 
 * Drag/drop is implemented by adding {@link Ext.data.Record}s to the target DDView. If copying is 
 * not being performed, the original {@link Ext.data.Record} is removed from the source DDView.<br> 
 * <p> 
 * The following extra CSS rules are needed to provide insertion point highlighting:<pre><code> 
.x-view-drag-insert-above { 
    border-top:1px dotted #3366cc; 
} 
.x-view-drag-insert-below { 
    border-bottom:1px dotted #3366cc; 
} 
</code></pre> 
 *  
 */ 
Ext.ux.DDView = function(config) {
	if (!config.itemSelector) {
		var tpl = config.tpl;
		if (this.classRe.test(tpl)) {
			config.tpl = tpl.replace(this.classRe, 'class=$1x-combo-list-item $2$1');
		}
		else {
			config.tpl = tpl.replace(this.tagRe, '$1 class="x-combo-list-item" $2');
		}
		config.itemSelector = ".x-combo-list-item";
	}
    Ext.ux.DDView.superclass.constructor.call(this, Ext.apply(config, { 
        border: false 
    })); 
}; 

Ext.extend(Ext.ux.DDView, Ext.DataView, { 
/**    @cfg {String/Array} dragGroup The ddgroup name(s) for the View's DragZone. */ 
/**    @cfg {String/Array} dropGroup The ddgroup name(s) for the View's DropZone. */ 
/**    @cfg {Boolean} copy Causes drag operations to copy nodes rather than move. */ 
/**    @cfg {Boolean} allowCopy Causes ctrl/drag operations to copy nodes rather than move. */ 

	sortDir: 'ASC',

    isFormField: true, 
     
    classRe: /class=(['"])(.*)\1/, 

    tagRe: /(<\w*)(.*?>)/, 

    reset: Ext.emptyFn, 
     
    clearInvalid: Ext.form.Field.prototype.clearInvalid, 

    msgTarget: 'qtip', 

	afterRender: function() {
		Ext.ux.DDView.superclass.afterRender.call(this);
	    if (this.dragGroup) { 
	        this.setDraggable(this.dragGroup.split(",")); 
	    } 
	    if (this.dropGroup) { 
	        this.setDroppable(this.dropGroup.split(",")); 
	    } 
	    if (this.deletable) { 
	        this.setDeletable(); 
	    } 
	    this.isDirtyFlag = false; 
	    this.addEvents( 
	        "drop" 
	    );
	},
     
    validate: function() { 
        return true; 
    }, 
     
    destroy: function() { 
        this.purgeListeners(); 
        this.getEl().removeAllListeners(); 
        this.getEl().remove(); 
        if (this.dragZone) { 
            if (this.dragZone.destroy) { 
                this.dragZone.destroy(); 
            } 
        } 
        if (this.dropZone) { 
            if (this.dropZone.destroy) { 
                this.dropZone.destroy(); 
            } 
        } 
    }, 

/**    Allows this class to be an Ext.form.Field so it can be found using {@link Ext.form.BasicForm#findField}. */ 
    getName: function() { 
        return this.name; 
    }, 

/**    Loads the View from a JSON string representing the Records to put into the Store. */ 
    setValue: function(v) { 
        if (!this.store) { 
            throw "DDView.setValue(). DDView must be constructed with a valid Store"; 
        } 
        var data = {}; 
        data[this.store.reader.meta.root] = v ? [].concat(v) : []; 
        this.store.proxy = new Ext.data.MemoryProxy(data); 
        this.store.load(); 
    }, 

/**    @return {String} a parenthesised list of the ids of the Records in the View. */ 
    getValue: function() { 
        var result = '('; 
        this.store.each(function(rec) { 
            result += rec.id + ','; 
        }); 
        return result.substr(0, result.length - 1) + ')'; 
    }, 
     
    getIds: function() { 
        var i = 0, result = new Array(this.store.getCount()); 
        this.store.each(function(rec) { 
            result[i++] = rec.id; 
        }); 
        return result; 
    }, 
     
    isDirty: function() { 
        return this.isDirtyFlag; 
    }, 

/** 
 *    Part of the Ext.dd.DropZone interface. If no target node is found, the 
 *    whole Element becomes the target, and this causes the drop gesture to append. 
 */ 
    getTargetFromEvent : function(e) { 
        var target = e.getTarget(); 
        while ((target !== null) && (target.parentNode != this.el.dom)) { 
            target = target.parentNode; 
        } 
        if (!target) { 
            target = this.el.dom.lastChild || this.el.dom; 
        } 
        return target; 
    }, 

/** 
 *    Create the drag data which consists of an object which has the property "ddel" as 
 *    the drag proxy element.  
 */ 
    getDragData : function(e) { 
        var target = this.findItemFromChild(e.getTarget()); 
        if(target) { 
            if (!this.isSelected(target)) { 
                delete this.ignoreNextClick; 
                this.onItemClick(target, this.indexOf(target), e); 
                this.ignoreNextClick = true; 
            } 
            var dragData = { 
                sourceView: this, 
                viewNodes: [], 
                records: [], 
                copy: this.copy || (this.allowCopy && e.ctrlKey) 
            }; 
            if (this.getSelectionCount() == 1) { 
                var i = this.getSelectedIndexes()[0]; 
                var n = this.getNode(i); 
                dragData.viewNodes.push(dragData.ddel = n); 
                dragData.records.push(this.store.getAt(i)); 
                dragData.repairXY = Ext.fly(n).getXY(); 
            } else { 
                dragData.ddel = document.createElement('div'); 
                dragData.ddel.className = 'multi-proxy'; 
                this.collectSelection(dragData); 
            } 
            return dragData; 
        } 
        return false; 
    }, 

//    override the default repairXY. 
    getRepairXY : function(e){ 
        return this.dragData.repairXY; 
    }, 

/**    Put the selections into the records and viewNodes Arrays. */ 
    collectSelection: function(data) { 
        data.repairXY = Ext.fly(this.getSelectedNodes()[0]).getXY(); 
        if (this.preserveSelectionOrder === true) { 
            Ext.each(this.getSelectedIndexes(), function(i) { 
                var n = this.getNode(i); 
                var dragNode = n.cloneNode(true); 
                dragNode.id = Ext.id(); 
                data.ddel.appendChild(dragNode); 
                data.records.push(this.store.getAt(i)); 
                data.viewNodes.push(n); 
            }, this); 
        } else { 
            var i = 0; 
            this.store.each(function(rec){ 
                if (this.isSelected(i)) { 
                    var n = this.getNode(i); 
                    var dragNode = n.cloneNode(true); 
                    dragNode.id = Ext.id(); 
                    data.ddel.appendChild(dragNode); 
                    data.records.push(this.store.getAt(i)); 
                    data.viewNodes.push(n); 
                } 
                i++; 
            }, this); 
        } 
    }, 
     
/**    Specify to which ddGroup items in this DDView may be dragged. */ 
    setDraggable: function(ddGroup) { 
        if (ddGroup instanceof Array) { 
            Ext.each(ddGroup, this.setDraggable, this); 
            return; 
        } 
        if (this.dragZone) { 
            this.dragZone.addToGroup(ddGroup); 
        } else { 
            this.dragZone = new Ext.dd.DragZone(this.getEl(), { 
                containerScroll: true, 
                ddGroup: ddGroup 
            }); 
//            Draggability implies selection. DragZone's mousedown selects the element. 
            if (!this.multiSelect) { this.singleSelect = true; } 

//            Wire the DragZone's handlers up to methods in *this* 
            this.dragZone.getDragData = this.getDragData.createDelegate(this); 
            this.dragZone.getRepairXY = this.getRepairXY; 
            this.dragZone.onEndDrag = this.onEndDrag; 
        } 
    }, 

/**    Specify from which ddGroup this DDView accepts drops. */ 
    setDroppable: function(ddGroup) { 
        if (ddGroup instanceof Array) { 
            Ext.each(ddGroup, this.setDroppable, this); 
            return; 
        } 
        if (this.dropZone) { 
            this.dropZone.addToGroup(ddGroup); 
        } else { 
            this.dropZone = new Ext.dd.DropZone(this.getEl(), { 
                owningView: this, 
                containerScroll: true, 
                ddGroup: ddGroup 
            }); 

//            Wire the DropZone's handlers up to methods in *this* 
            this.dropZone.getTargetFromEvent = this.getTargetFromEvent.createDelegate(this); 
            this.dropZone.onNodeEnter = this.onNodeEnter.createDelegate(this); 
            this.dropZone.onNodeOver = this.onNodeOver.createDelegate(this); 
            this.dropZone.onNodeOut = this.onNodeOut.createDelegate(this); 
            this.dropZone.onNodeDrop = this.onNodeDrop.createDelegate(this); 
        } 
    }, 

/**    Decide whether to drop above or below a View node. */ 
    getDropPoint : function(e, n, dd){ 
        if (n == this.el.dom) { return "above"; } 
        var t = Ext.lib.Dom.getY(n), b = t + n.offsetHeight; 
        var c = t + (b - t) / 2; 
        var y = Ext.lib.Event.getPageY(e); 
        if(y <= c) { 
            return "above"; 
        }else{ 
            return "below"; 
        } 
    }, 
     
    isValidDropPoint: function(pt, n, data) { 
        if (!data.viewNodes || (data.viewNodes.length != 1)) { 
            return true; 
        } 
        var d = data.viewNodes[0]; 
        if (d == n) { 
            return false; 
        } 
        if ((pt == "below") && (n.nextSibling == d)) { 
            return false; 
        } 
        if ((pt == "above") && (n.previousSibling == d)) { 
            return false; 
        } 
        return true; 
    }, 

    onNodeEnter : function(n, dd, e, data){ 
    	if (this.highlightColor && (data.sourceView != this)) {
	    	this.el.highlight(this.highlightColor);
	    }
        return false; 
    }, 
     
    onNodeOver : function(n, dd, e, data){ 
        var dragElClass = this.dropNotAllowed; 
        var pt = this.getDropPoint(e, n, dd); 
        if (this.isValidDropPoint(pt, n, data)) { 
    		if (this.appendOnly || this.sortField) {
    			return "x-tree-drop-ok-below";
    		}

//            set the insert point style on the target node 
            if (pt) { 
                var targetElClass; 
                if (pt == "above"){ 
                    dragElClass = n.previousSibling ? "x-tree-drop-ok-between" : "x-tree-drop-ok-above"; 
                    targetElClass = "x-view-drag-insert-above"; 
                } else { 
                    dragElClass = n.nextSibling ? "x-tree-drop-ok-between" : "x-tree-drop-ok-below"; 
                    targetElClass = "x-view-drag-insert-below"; 
                } 
                if (this.lastInsertClass != targetElClass){ 
                    Ext.fly(n).replaceClass(this.lastInsertClass, targetElClass); 
                    this.lastInsertClass = targetElClass; 
                } 
            } 
        } 
        return dragElClass; 
    }, 

    onNodeOut : function(n, dd, e, data){ 
        this.removeDropIndicators(n); 
    }, 

    onNodeDrop : function(n, dd, e, data){ 
        if (this.fireEvent("drop", this, n, dd, e, data) === false) { 
            return false; 
        } 
        var pt = this.getDropPoint(e, n, dd); 
        var insertAt = (this.appendOnly || (n == this.el.dom)) ? this.store.getCount() : n.viewIndex; 
        if (pt == "below") { 
            insertAt++; 
        } 

//        Validate if dragging within a DDView 
        if (data.sourceView == this) { 
//            If the first element to be inserted below is the target node, remove it 
            if (pt == "below") { 
                if (data.viewNodes[0] == n) { 
                    data.viewNodes.shift(); 
                } 
            } else { //    If the last element to be inserted above is the target node, remove it 
                if (data.viewNodes[data.viewNodes.length - 1] == n) { 
                    data.viewNodes.pop(); 
                } 
            } 
     
//            Nothing to drop... 
            if (!data.viewNodes.length) { 
                return false; 
            } 

//            If we are moving DOWN, then because a store.remove() takes place first, 
//            the insertAt must be decremented. 
            if (insertAt > this.store.indexOf(data.records[0])) { 
                insertAt--; 
            } 
        } 

//        Dragging from a Tree. Use the Tree's recordFromNode function. 
        if (data.node instanceof Ext.tree.TreeNode) { 
            var r = data.node.getOwnerTree().recordFromNode(data.node); 
            if (r) { 
                data.records = [ r ]; 
            } 
        } 
         
        if (!data.records) { 
            alert("Programming problem. Drag data contained no Records"); 
            return false; 
        } 

        for (var i = 0; i < data.records.length; i++) { 
            var r = data.records[i]; 
            var dup = this.store.getById(r.id); 
            if (dup && (dd != this.dragZone)) { 
				if(!this.allowDup && !this.allowTrash){
                	Ext.fly(this.getNode(this.store.indexOf(dup))).frame("red", 1); 
					return true
				}
				var x=new Ext.data.Record();
				r.id=x.id;
				delete x;
			}
            if (data.copy) { 
                this.store.insert(insertAt++, r.copy()); 
            } else { 
                if (data.sourceView) { 
                    data.sourceView.isDirtyFlag = true; 
                    data.sourceView.store.remove(r); 
                } 
                if(!this.allowTrash)this.store.insert(insertAt++, r); 
            } 
			if(this.sortField){
				this.store.sort(this.sortField, this.sortDir);
			}
            this.isDirtyFlag = true; 
        } 
        this.dragZone.cachedTarget = null; 
        return true; 
    }, 

//    Ensure the multi proxy is removed 
    onEndDrag: function(data, e) { 
        var d = Ext.get(this.dragData.ddel); 
        if (d && d.hasClass("multi-proxy")) { 
            d.remove(); 
            //delete this.dragData.ddel; 
        } 
    }, 

    removeDropIndicators : function(n){ 
        if(n){ 
            Ext.fly(n).removeClass([ 
                "x-view-drag-insert-above", 
				"x-view-drag-insert-left",
				"x-view-drag-insert-right",
                "x-view-drag-insert-below"]); 
            this.lastInsertClass = "_noclass"; 
        } 
    }, 

/** 
 *    Utility method. Add a delete option to the DDView's context menu. 
 *    @param {String} imageUrl The URL of the "delete" icon image. 
 */ 
    setDeletable: function(imageUrl) { 
        if (!this.singleSelect && !this.multiSelect) { 
            this.singleSelect = true; 
        } 
        var c = this.getContextMenu(); 
        this.contextMenu.on("itemclick", function(item) { 
            switch (item.id) { 
                case "delete": 
                    this.remove(this.getSelectedIndexes()); 
                    break; 
            } 
        }, this); 
        this.contextMenu.add({ 
            icon: imageUrl || AU.resolveUrl("/images/delete.gif"), 
            id: "delete", 
            text: AU.getMessage("deleteItem") 
        }); 
    }, 
     
/**    Return the context menu for this DDView. */ 
    getContextMenu: function() { 
        if (!this.contextMenu) { 
//            Create the View's context menu 
            this.contextMenu = new Ext.menu.Menu({ 
                id: this.id + "-contextmenu" 
            }); 
            this.el.on("contextmenu", this.showContextMenu, this); 
        } 
        return this.contextMenu; 
    }, 
     
    disableContextMenu: function() { 
        if (this.contextMenu) { 
            this.el.un("contextmenu", this.showContextMenu, this); 
        } 
    }, 

    showContextMenu: function(e, item) { 
        item = this.findItemFromChild(e.getTarget()); 
        if (item) { 
            e.stopEvent(); 
            this.select(this.getNode(item), this.multiSelect && e.ctrlKey, true); 
            this.contextMenu.showAt(e.getXY()); 
        } 
    }, 

/** 
 *    Remove {@link Ext.data.Record}s at the specified indices. 
 *    @param {Array/Number} selectedIndices The index (or Array of indices) of Records to remove. 
 */ 
    remove: function(selectedIndices) { 
        selectedIndices = [].concat(selectedIndices); 
        for (var i = 0; i < selectedIndices.length; i++) { 
            var rec = this.store.getAt(selectedIndices[i]); 
            this.store.remove(rec); 
        } 
    }, 

/** 
 *    Double click fires the event, but also, if this is draggable, and there is only one other 
 *    related DropZone that is in another DDView, it drops the selected node on that DDView. 
 */ 
    onDblClick : function(e){ 
        var item = this.findItemFromChild(e.getTarget()); 
        if(item){ 
            if (this.fireEvent("dblclick", this, this.indexOf(item), item, e) === false) { 
                return false; 
            } 
            if (this.dragGroup) { 
                var targets = Ext.dd.DragDropMgr.getRelated(this.dragZone, true); 

//                Remove instances of this View's DropZone 
                while (targets.contains(this.dropZone)) { 
                    targets.remove(this.dropZone); 
                } 

//                If there's only one other DropZone, and it is owned by a DDView, then drop it in 
                if ((targets.length == 1) && (targets[0].owningView)) { 
                    this.dragZone.cachedTarget = null; 
                    var el = Ext.get(targets[0].getEl()); 
                    var box = el.getBox(true); 
                    targets[0].onNodeDrop(el.dom, { 
                        target: el.dom, 
                        xy: [box.x, box.y + box.height - 1] 
                    }, null, this.getDragData(e)); 
                } 
            } 
        } 
    }, 
     
    onItemClick : function(item, index, e){ 
//        The DragZone's mousedown->getDragData already handled selection 
        if (this.ignoreNextClick) { 
            delete this.ignoreNextClick; 
            return; 
        } 

        if(this.fireEvent("beforeclick", this, index, item, e) === false){ 
            return false; 
        } 
        if(this.multiSelect || this.singleSelect){ 
            if(this.multiSelect && e.shiftKey && this.lastSelection){ 
                this.select(this.getNodes(this.indexOf(this.lastSelection), index), false); 
            } else if (this.isSelected(item) && e.ctrlKey) { 
                this.deselect(item); 
            }else{ 
                this.deselect(item); 
                this.select(item, this.multiSelect && e.ctrlKey); 
                this.lastSelection = item; 
            } 
            e.preventDefault(); 
        } 
        return true; 
    } 
});  
//version 3.0

Ext.ux.Multiselect = Ext.extend(Ext.form.Field,  {
	store:null,
	dataFields:[],
	data:[],
	width:100,
	height:100,
	displayField:0,
	valueField:1,
	allowBlank:true,
	minLength:0,
	maxLength:Number.MAX_VALUE,
	blankText:Ext.form.TextField.prototype.blankText,
	minLengthText:'Minimum {0} item(s) required',
	maxLengthText:'Maximum {0} item(s) allowed',
	copy:false,
	allowDup:false,
	allowTrash:false,
	legend:null,
	focusClass:undefined,
	delimiter:',',
	view:null,
	dragGroup:null,
	dropGroup:null,
	tbar:null,
	appendOnly:false,
	sortField:null,
	sortDir:'ASC',
	defaultAutoCreate : {tag: "div"},
	
    initComponent: function(){
		Ext.ux.Multiselect.superclass.initComponent.call(this);
		this.addEvents({
			'dblclick' : true,
			'click' : true,
			'change' : true,
			'drop' : true
		});		
	},
    onRender: function(ct, position){
		var fs, cls, tpl;
		Ext.ux.Multiselect.superclass.onRender.call(this, ct, position);

		cls = 'ux-mselect';

		fs = new Ext.form.FieldSet({
			renderTo:this.el,
			title:this.legend,
			height:this.height,
			width:this.width,
			style:"padding:1px;",
			tbar:this.tbar
		});
		if(!this.legend)fs.el.down('.'+fs.headerCls).remove();
		fs.body.addClass(cls);

		tpl = '<tpl for="."><div class="' + cls + '-item';
		if(Ext.isIE || Ext.isIE7)tpl+='" unselectable=on';
		else tpl+=' x-unselectable"';
		tpl+='>{' + this.displayField + '}</div></tpl>';

		if(!this.store){
			this.store = new Ext.data.SimpleStore({
				fields: this.dataFields,
				data : this.data
			});
		}

		this.view = new Ext.ux.DDView({
			multiSelect: true, store: this.store, selectedClass: cls+"-selected", tpl:tpl,
			allowDup:this.allowDup, copy: this.copy, allowTrash: this.allowTrash, 
			dragGroup: this.dragGroup, dropGroup: this.dropGroup, itemSelector:"."+cls+"-item",
			isFormField:false, applyTo:fs.body, appendOnly:this.appendOnly,
			sortField:this.sortField, sortDir:this.sortDir
		});

		fs.add(this.view);
		
		this.view.on('click', this.onViewClick, this);
		this.view.on('beforeClick', this.onViewBeforeClick, this);
		this.view.on('dblclick', this.onViewDblClick, this);
		this.view.on('drop', function(ddView, n, dd, e, data){
	    	return this.fireEvent("drop", ddView, n, dd, e, data);
		}, this);
		
		this.hiddenName = this.name;
		var hiddenTag={tag: "input", type: "hidden", value: "", name:this.name};
		if (this.isFormField) { 
			this.hiddenField = this.el.createChild(hiddenTag);
		} else {
			this.hiddenField = Ext.get(document.body).createChild(hiddenTag);
		}
		fs.doLayout();
	},
	
	initValue:Ext.emptyFn,
	
	onViewClick: function(vw, index, node, e) {
		var arrayIndex = this.preClickSelections.indexOf(index);
		if (arrayIndex  != -1)
		{
			this.preClickSelections.splice(arrayIndex, 1);
			this.view.clearSelections(true);
			this.view.select(this.preClickSelections);
		}
		this.fireEvent('change', this, this.getValue(), this.hiddenField.dom.value);
		this.hiddenField.dom.value = this.getValue();
		this.fireEvent('click', this, e);
		this.validate();		
	},

	onViewBeforeClick: function(vw, index, node, e) {
		this.preClickSelections = this.view.getSelectedIndexes();
		if (this.disabled) {return false;}
	},

	onViewDblClick : function(vw, index, node, e) {
		return this.fireEvent('dblclick', vw, index, node, e);
	},	
	
	getValue: function(valueField){
		var returnArray = [];
		var selectionsArray = this.view.getSelectedIndexes();
		if (selectionsArray.length == 0) {return '';}
		for (var i=0; i<selectionsArray.length; i++) {
			returnArray.push(this.store.getAt(selectionsArray[i]).get(((valueField != null)? valueField : this.valueField)));
		}
		return returnArray.join(this.delimiter);
	},

	setValue: function(values) {
		var index;
		var selections = [];
		this.view.clearSelections();
		this.hiddenField.dom.value = '';
		
		if (!values || (values == '')) { return; }
		
		if (!(values instanceof Array)) { values = values.split(this.delimiter); }
		for (var i=0; i<values.length; i++) {
			index = this.view.store.indexOf(this.view.store.query(this.valueField, 
				new RegExp('^' + values[i] + '$', "i")).itemAt(0));
			selections.push(index);
		}
		this.view.select(selections);
		this.hiddenField.dom.value = this.getValue();
		this.validate();
	},
	
	reset : function() {
		this.setValue('');
	},
	
	getRawValue: function(valueField) {
        var tmp = this.getValue(valueField);
        if (tmp.length) {
            tmp = tmp.split(this.delimiter);
        }
        else{
            tmp = [];
        }
        return tmp;
    },

    setRawValue: function(values){
        setValue(values);
    },

    validateValue : function(value){
        if (value.length < 1) { // if it has no value
             if (this.allowBlank) {
                 this.clearInvalid();
                 return true;
             } else {
                 this.markInvalid(this.blankText);
                 return false;
             }
        }
        if (value.length < this.minLength) {
            this.markInvalid(String.format(this.minLengthText, this.minLength));
            return false;
        }
        if (value.length > this.maxLength) {
            this.markInvalid(String.format(this.maxLengthText, this.maxLength));
            return false;
        }
        return true;
    }
});

Ext.reg("multiselect", Ext.ux.Multiselect);

Ext.ux.ItemSelector = Ext.extend(Ext.form.Field,  {
	msWidth:200,
	msHeight:300,
	hideNavIcons:false,
	imagePath:"",
	iconUp:"up2.gif",
	iconDown:"down2.gif",
	iconLeft:"left2.gif",
	iconRight:"right2.gif",
	iconTop:"top2.gif",
	iconBottom:"bottom2.gif",
	drawUpIcon:true,
	drawDownIcon:true,
	drawLeftIcon:true,
	drawRightIcon:true,
	drawTopIcon:true,
	drawBotIcon:true,
	fromStore:null,
	toStore:null,
	fromData:null, 
	toData:null,
	displayField:0,
	valueField:1,
	switchToFrom:false,
	allowDup:false,
	focusClass:undefined,
	delimiter:',',
	readOnly:false,
	toLegend:null,
	fromLegend:null,
	toSortField:null,
	fromSortField:null,
	toSortDir:'ASC',
	fromSortDir:'ASC',
	toTBar:null,
	fromTBar:null,
	bodyStyle:null,
	border:false,
	defaultAutoCreate:{tag: "div"},
	
    initComponent: function(){
		Ext.ux.ItemSelector.superclass.initComponent.call(this);
		this.addEvents({
			'rowdblclick' : true,
			'change' : true
		});			
	},

    onRender: function(ct, position){
		Ext.ux.ItemSelector.superclass.onRender.call(this, ct, position);

		this.fromMultiselect = new Ext.ux.Multiselect({
			legend: this.fromLegend,
			delimiter: this.delimiter,
			allowDup: this.allowDup,
			copy: this.allowDup,
			allowTrash: this.allowDup,
			dragGroup: this.readOnly ? null : "drop2-"+this.el.dom.id,
			dropGroup: this.readOnly ? null : "drop1-"+this.el.dom.id,
			width: this.msWidth,
			height: this.msHeight,
			dataFields: this.dataFields,
			data: this.fromData,
			displayField: this.displayField,
			valueField: this.valueField,
			store: this.fromStore,
			isFormField: false,
			tbar: this.fromTBar,
			appendOnly: true,
			sortField: this.fromSortField,
			sortDir: this.fromSortDir
		});
		this.fromMultiselect.on('dblclick', this.onRowDblClick, this);

		if (!this.toStore) {
			this.toStore = new Ext.data.SimpleStore({
				fields: this.dataFields,
				data : this.toData
			});
		}
		this.toStore.on('add', this.valueChanged, this);
		this.toStore.on('remove', this.valueChanged, this);
		this.toStore.on('load', this.valueChanged, this);

		this.toMultiselect = new Ext.ux.Multiselect({
			legend: this.toLegend,
			delimiter: this.delimiter,
			allowDup: this.allowDup,
			dragGroup: this.readOnly ? null : "drop1-"+this.el.dom.id,
			//dropGroup: this.readOnly ? null : "drop2-"+this.el.dom.id+(this.toSortField ? "" : ",drop1-"+this.el.dom.id),
			dropGroup: this.readOnly ? null : "drop2-"+this.el.dom.id+",drop1-"+this.el.dom.id,
			width: this.msWidth,
			height: this.msHeight,
			displayField: this.displayField,
			valueField: this.valueField,
			store: this.toStore,
			isFormField: false,
			tbar: this.toTBar,
			sortField: this.toSortField,
			sortDir: this.toSortDir
		});
		this.toMultiselect.on('dblclick', this.onRowDblClick, this);
				
		var p = new Ext.Panel({
			bodyStyle:this.bodyStyle,
			border:this.border,
			layout:"table",
			layoutConfig:{columns:3}
		});
		p.add(this.switchToFrom ? this.toMultiselect : this.fromMultiselect);
		var icons = new Ext.Panel({header:false});
		p.add(icons);
		p.add(this.switchToFrom ? this.fromMultiselect : this.toMultiselect);
		p.render(this.el);
		icons.el.down('.'+icons.bwrapCls).remove();

		if (this.imagePath!="" && this.imagePath.charAt(this.imagePath.length-1)!="/")
			this.imagePath+="/";
		this.iconUp = this.imagePath + (this.iconUp || 'up2.gif');
		this.iconDown = this.imagePath + (this.iconDown || 'down2.gif');
		this.iconLeft = this.imagePath + (this.iconLeft || 'left2.gif');
		this.iconRight = this.imagePath + (this.iconRight || 'right2.gif');
		this.iconTop = this.imagePath + (this.iconTop || 'top2.gif');
		this.iconBottom = this.imagePath + (this.iconBottom || 'bottom2.gif');
		var el=icons.getEl();
		if (!this.toSortField) {
			this.toTopIcon = el.createChild({tag:'img', src:this.iconTop, style:{cursor:'pointer', margin:'2px'}});
			el.createChild({tag: 'br'});
			this.upIcon = el.createChild({tag:'img', src:this.iconUp, style:{cursor:'pointer', margin:'2px'}});
			el.createChild({tag: 'br'});
		}
		this.addIcon = el.createChild({tag:'img', src:this.switchToFrom?this.iconLeft:this.iconRight, style:{cursor:'pointer', margin:'2px'}});
		el.createChild({tag: 'br'});
		this.removeIcon = el.createChild({tag:'img', src:this.switchToFrom?this.iconRight:this.iconLeft, style:{cursor:'pointer', margin:'2px'}});
		el.createChild({tag: 'br'});
		if (!this.toSortField) {
			this.downIcon = el.createChild({tag:'img', src:this.iconDown, style:{cursor:'pointer', margin:'2px'}});
			el.createChild({tag: 'br'});
			this.toBottomIcon = el.createChild({tag:'img', src:this.iconBottom, style:{cursor:'pointer', margin:'2px'}});
		}
		if (!this.readOnly) {
			if (!this.toSortField) {
				this.toTopIcon.on('click', this.toTop, this);
				this.upIcon.on('click', this.up, this);
				this.downIcon.on('click', this.down, this);
				this.toBottomIcon.on('click', this.toBottom, this);
			}
			this.addIcon.on('click', this.fromTo, this);
			this.removeIcon.on('click', this.toFrom, this);
		}
		if (!this.drawUpIcon || this.hideNavIcons) { this.upIcon.dom.style.display='none'; }
		if (!this.drawDownIcon || this.hideNavIcons) { this.downIcon.dom.style.display='none'; }
		if (!this.drawLeftIcon || this.hideNavIcons) { this.addIcon.dom.style.display='none'; }
		if (!this.drawRightIcon || this.hideNavIcons) { this.removeIcon.dom.style.display='none'; }
		if (!this.drawTopIcon || this.hideNavIcons) { this.toTopIcon.dom.style.display='none'; }
		if (!this.drawBotIcon || this.hideNavIcons) { this.toBottomIcon.dom.style.display='none'; }

		var tb = p.body.first();
		this.el.setWidth(p.body.first().getWidth());
		p.body.removeClass();
		
		this.hiddenName = this.name;
		var hiddenTag={tag: "input", type: "hidden", value: "", name:this.name};
		this.hiddenField = this.el.createChild(hiddenTag);
		this.valueChanged(this.toStore);
	},
	
	initValue:Ext.emptyFn,
	
	toTop : function() {
		var selectionsArray = this.toMultiselect.view.getSelectedIndexes();
		var records = [];
		if (selectionsArray.length > 0) {
			selectionsArray.sort();
			for (var i=0; i<selectionsArray.length; i++) {
				record = this.toMultiselect.view.store.getAt(selectionsArray[i]);
				records.push(record);
			}
			selectionsArray = [];
			for (var i=records.length-1; i>-1; i--) {
				record = records[i];
				this.toMultiselect.view.store.remove(record);
				this.toMultiselect.view.store.insert(0, record);
				selectionsArray.push(((records.length - 1) - i));
			}
		}
		this.toMultiselect.view.refresh();
		this.toMultiselect.view.select(selectionsArray);
	},

	toBottom : function() {
		var selectionsArray = this.toMultiselect.view.getSelectedIndexes();
		var records = [];
		if (selectionsArray.length > 0) {
			selectionsArray.sort();
			for (var i=0; i<selectionsArray.length; i++) {
				record = this.toMultiselect.view.store.getAt(selectionsArray[i]);
				records.push(record);
			}
			selectionsArray = [];
			for (var i=0; i<records.length; i++) {
				record = records[i];
				this.toMultiselect.view.store.remove(record);
				this.toMultiselect.view.store.add(record);
				selectionsArray.push((this.toMultiselect.view.store.getCount()) - (records.length - i));
			}
		}
		this.toMultiselect.view.refresh();
		this.toMultiselect.view.select(selectionsArray);
	},
	
	up : function() {
		var record = null;
		var selectionsArray = this.toMultiselect.view.getSelectedIndexes();
		selectionsArray.sort();
		var newSelectionsArray = [];
		if (selectionsArray.length > 0) {
			for (var i=0; i<selectionsArray.length; i++) {
				record = this.toMultiselect.view.store.getAt(selectionsArray[i]);
				if ((selectionsArray[i] - 1) >= 0) {
					this.toMultiselect.view.store.remove(record);
					this.toMultiselect.view.store.insert(selectionsArray[i] - 1, record);
					newSelectionsArray.push(selectionsArray[i] - 1);
				}
			}
			this.toMultiselect.view.refresh();
			this.toMultiselect.view.select(newSelectionsArray);
		}
	},

	down : function() {
		var record = null;
		var selectionsArray = this.toMultiselect.view.getSelectedIndexes();
		selectionsArray.sort();
		selectionsArray.reverse();
		var newSelectionsArray = [];
		if (selectionsArray.length > 0) {
			for (var i=0; i<selectionsArray.length; i++) {
				record = this.toMultiselect.view.store.getAt(selectionsArray[i]);
				if ((selectionsArray[i] + 1) < this.toMultiselect.view.store.getCount()) {
					this.toMultiselect.view.store.remove(record);
					this.toMultiselect.view.store.insert(selectionsArray[i] + 1, record);
					newSelectionsArray.push(selectionsArray[i] + 1);
				}
			}
			this.toMultiselect.view.refresh();
			this.toMultiselect.view.select(newSelectionsArray);
		}
	},
	
	fromTo : function() {
		var selectionsArray = this.fromMultiselect.view.getSelectedIndexes();
		var records = [];
		if (selectionsArray.length > 0) {
			for (var i=0; i<selectionsArray.length; i++) {
				record = this.fromMultiselect.view.store.getAt(selectionsArray[i]);
				records.push(record);
			}
			if(!this.allowDup)selectionsArray = [];
			for (var i=0; i<records.length; i++) {
				record = records[i];
				if(this.allowDup){
					var x=new Ext.data.Record();
					record.id=x.id;
					delete x;	
					this.toMultiselect.view.store.add(record);
				}else{
					this.fromMultiselect.view.store.remove(record);
					this.toMultiselect.view.store.add(record);
					selectionsArray.push((this.toMultiselect.view.store.getCount() - 1));
				}
			}
		}
		this.toMultiselect.view.refresh();
		this.fromMultiselect.view.refresh();
		if(this.toSortField)this.toMultiselect.store.sort(this.toSortField, this.toSortDir);
		if(this.allowDup)this.fromMultiselect.view.select(selectionsArray);
		else this.toMultiselect.view.select(selectionsArray);
	},
	
	toFrom : function() {
		var selectionsArray = this.toMultiselect.view.getSelectedIndexes();
		var records = [];
		if (selectionsArray.length > 0) {
			for (var i=0; i<selectionsArray.length; i++) {
				record = this.toMultiselect.view.store.getAt(selectionsArray[i]);
				records.push(record);
			}
			selectionsArray = [];
			for (var i=0; i<records.length; i++) {
				record = records[i];
				this.toMultiselect.view.store.remove(record);
				if(!this.allowDup){
					this.fromMultiselect.view.store.add(record);
					selectionsArray.push((this.fromMultiselect.view.store.getCount() - 1));
				}
			}
		}
		this.fromMultiselect.view.refresh();
		this.toMultiselect.view.refresh();
		if(this.fromSortField)this.fromMultiselect.store.sort(this.fromSortField, this.fromSortDir);
		this.fromMultiselect.view.select(selectionsArray);
	},
	
	valueChanged: function(store) {
		var record = null;
		var values = [];
		for (var i=0; i<store.getCount(); i++) {
			record = store.getAt(i);
			values.push(record.get(this.valueField));
		}
		this.hiddenField.dom.value = values.join(this.delimiter);
		this.fireEvent('change', this, this.getValue(), this.hiddenField.dom.value);
	},
	
	getValue : function() {
		return this.hiddenField.dom.value;
	},
	
	onRowDblClick : function(vw, index, node, e) {
		return this.fireEvent('rowdblclick', vw, index, node, e);
	},
	
	reset: function(){
		range = this.toMultiselect.store.getRange();
		this.toMultiselect.store.removeAll();
		if (!this.allowDup) {
			this.fromMultiselect.store.add(range);
			this.fromMultiselect.store.sort(this.displayField,'ASC');
		}
		this.valueChanged(this.toMultiselect.store);
	}
});

Ext.reg("itemselector", Ext.ux.ItemSelector);Ext.namespace('Ext.ux.Andrie');

/**
 * @class Ext.ux.Andrie.pPageSize
 * @extends Ext.PagingToolbar
 * A combobox control that glues itself to a PagingToolbar's pageSize configuration property.
 * @constructor
 * Create a new PageSize plugin.
 * @param {Object} config Configuration options
 * @author Andrei Neculau - andrei.neculau@gmail.com / http://andreineculau.wordpress.com
 * @version 0.6
 */
Ext.ux.Andrie.pPageSize = function(config){
	Ext.apply(this, config);
};

Ext.extend(Ext.ux.Andrie.pPageSize, Ext.util.Observable, {
	/**
	 * @cfg {String} beforeText
	 * Text to display before the comboBox
	 */
	beforeText: 'Show',
	
	/**
	 * @cfg {String} afterText
	 * Text to display after the comboBox
	 */
	afterText: 'items',
	
	/**
	 * @cfg {Mixed} addBefore
	 * Toolbar item(s) to add before the PageSizer
	 */
	addBefore: '-',
	
	/**
	 * @cfg {Mixed} addAfter
	 * Toolbar item(s) to be added after the PageSizer
	 */
	addAfter: null,
	
	/**
	 * @cfg {Bool} dynamic
	 * True for dynamic variations, false for static ones
	 */
	dynamic: false,
	
	/**
	 * @cfg {Array} variations
	 * Variations used for determining pageSize options
	 */
	variations: [5, 10, 20, 50, 100, 200, 500, 1000],
	
	/**
	 * @cfg {Object} comboCfg
	 * Combo config object that overrides the defaults
	 */
	comboCfg: undefined,
	
	init: function(pagingToolbar){
		this.pagingToolbar = pagingToolbar;
		this.pagingToolbar.pageSizeCombo = this;
		this.pagingToolbar.setPageSize = this.setPageSize.createDelegate(this);
		this.pagingToolbar.getPageSize = function(){
			return this.pageSize;
		}
		this.pagingToolbar.on('render', this.onRender, this);
	},
	
	//private
	addSize:function(value){
		if (value>0){
			this.sizes.push([value]);
		}
	},
	
	//private
	updateStore: function(){
		if (this.dynamic) {
			var middleValue = this.pagingToolbar.pageSize, start;
			middleValue = (middleValue > 0) ? middleValue : 1;
			this.sizes = [];
			var v = this.variations;
			for (var i = 0, len = v.length; i < len; i++) {
				this.addSize(middleValue - v[v.length - 1 - i]);
			}
			this.addToStore(middleValue);
			for (var i = 0, len = v.length; i < len; i++) {
				this.addSize(middleValue + v[i]);
			}
		}else{
			if (!this.staticSizes){
				this.sizes = [];
				var v = this.variations;
				var middleValue = 0;
				for (var i = 0, len = v.length; i < len; i++) {
					this.addSize(middleValue + v[i]);
				}
				this.staticSizes = this.sizes.slice(0);
			}else{
				this.sizes = this.staticSizes.slice(0);
			}
		}
		this.combo.store.loadData(this.sizes);
		this.combo.collapse();
		this.combo.setValue(this.pagingToolbar.pageSize);
	},

	setPageSize:function(value, forced){
		var pt = this.pagingToolbar;
		this.combo.collapse();
		value = parseInt(value) || parseInt(this.combo.getValue());
		value = (value>0)?value:1;
		if (value == pt.pageSize){
			return;
		}else if (value < pt.pageSize){
			pt.pageSize = value;
			var ap = Math.round(pt.cursor/value)+1;
			var cursor = (ap-1)*value;
			var store = pt.store;
			if (cursor > store.getTotalCount()) {
				this.pagingToolbar.pageSize = value;
				this.pagingToolbar.doLoad(cursor-value);
			}else{
				store.suspendEvents();
				for (var i = 0, len = cursor - pt.cursor; i < len; i++) {
					store.remove(store.getAt(0));
				}
				while (store.getCount() > value) {
					store.remove(store.getAt(store.getCount() - 1));
				}
				store.resumeEvents();
				store.fireEvent('datachanged', store);
				pt.cursor = cursor;
				var d = pt.getPageData();
				pt.afterTextEl.el.innerHTML = String.format(pt.afterPageText, d.pages);
				pt.field.dom.value = ap;
				pt.first.setDisabled(ap == 1);
				pt.prev.setDisabled(ap == 1);
				pt.next.setDisabled(ap == d.pages);
				pt.last.setDisabled(ap == d.pages);
				pt.updateInfo();
			}
		}else{
			this.pagingToolbar.pageSize = value;
			this.pagingToolbar.doLoad(Math.floor(this.pagingToolbar.cursor/this.pagingToolbar.pageSize) * this.pagingToolbar.pageSize);
		}
		this.updateStore();
	},
	
	//private
	onRender: function(){
		this.combo = Ext.ComponentMgr.create(Ext.applyIf(this.comboCfg||{}, {
			store:new Ext.data.SimpleStore({
				fields:['pageSize'],
				data:[]
			}),
			displayField:'pageSize',
			valueField:'pageSize',
			mode:'local',
			triggerAction:'all',
			width:50,
			xtype:'combo'
		}));
		this.combo.on('select', this.setPageSize, this);
		this.updateStore();
		
		if (this.addBefore){
			this.pagingToolbar.add(this.addBefore);
		}
		if (this.beforeText){
			this.pagingToolbar.add(this.beforeText);
		}
		this.pagingToolbar.add(this.combo);
		if (this.afterText){
			this.pagingToolbar.add(this.afterText);
		}
		if (this.addAfter){
			this.pagingToolbar.add(this.addAfter);
		}
	}
})/*
 * Ext JS Library 2.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.grid.RowExpander = function(config){
    Ext.apply(this, config);

    this.addEvents({
        beforeexpand : true,
        expand: true,
        beforecollapse: true,
        collapse: true
    });

    Ext.grid.RowExpander.superclass.constructor.call(this);

    if(this.tpl){
        if(typeof this.tpl == 'string'){
            this.tpl = new Ext.Template(this.tpl);
        }
        this.tpl.compile();
    }

    this.state = {};
    this.bodyContent = {};
};

Ext.extend(Ext.grid.RowExpander, Ext.util.Observable, {
    header: "",
    width: 20,
    sortable: false,
    fixed:true,
    menuDisabled:true,
    dataIndex: '',
    id: 'expander',
    lazyRender : true,
    enableCaching: true,

    getRowClass : function(record, rowIndex, p, ds){
        p.cols = p.cols-1;
        var content = this.bodyContent[record.id];
        if(!content && !this.lazyRender){
            content = this.getBodyContent(record, rowIndex);
        }
        if(content){
            p.body = content;
        }
        return this.state[record.id] ? 'x-grid3-row-expanded' : 'x-grid3-row-collapsed';
    },

    init : function(grid){
        this.grid = grid;

        var view = grid.getView();
        view.getRowClass = this.getRowClass.createDelegate(this);

        view.enableRowBody = true;

        grid.on('render', function(){
            view.mainBody.on('mousedown', this.onMouseDown, this);
        }, this);
    },

    getBodyContent : function(record, index){
/* From one version, but not in the one grabbed from extjs
        //convert keys (replace minus (-) by two underscores (__))
        var data = Array();
        for (var key in record.data) {
          var new_key = key.replace(/-/g, '__');
          data[new_key] = record.data[key];
        }
*/

        if(!this.enableCaching){
            return this.tpl.apply(record.data);
        }
        var content = this.bodyContent[record.id];
        if(!content){
            content = this.tpl.apply(record.data);
            this.bodyContent[record.id] = content;
        }
        return content;
    },

    onMouseDown : function(e, t){
        if(t.className == 'x-grid3-row-expander'){
            e.stopEvent();
            var row = e.getTarget('.x-grid3-row');
            this.toggleRow(row);
        }
    },

    renderer : function(v, p, record){
        p.cellAttr = 'rowspan="2"';
        return '<div class="x-grid3-row-expander">&#160;</div>';
    },

    beforeExpand : function(record, body, rowIndex){
        if(this.fireEvent('beforeexpand', this, record, body, rowIndex) !== false){
            if(this.tpl && this.lazyRender){
                body.innerHTML = this.getBodyContent(record, rowIndex);
            }
            return true;
        }else{
            return false;
        }
    },

    toggleRow : function(row){
        if(typeof row == 'number'){
            row = this.grid.view.getRow(row);
        }
        this[Ext.fly(row).hasClass('x-grid3-row-collapsed') ? 'expandRow' : 'collapseRow'](row);
    },

    expandRow : function(row){
        if(typeof row == 'number'){
            row = this.grid.view.getRow(row);
        }
        var record = this.grid.store.getAt(row.rowIndex);
        var body = Ext.DomQuery.selectNode('tr:nth(2) div.x-grid3-row-body', row);
        if(this.beforeExpand(record, body, row.rowIndex)){
            this.state[record.id] = true;
            Ext.fly(row).replaceClass('x-grid3-row-collapsed', 'x-grid3-row-expanded');
            this.fireEvent('expand', this, record, body, row.rowIndex);
        }
    },

    collapseRow : function(row){
        if(typeof row == 'number'){
            row = this.grid.view.getRow(row);
        }
        var record = this.grid.store.getAt(row.rowIndex);
        var body = Ext.fly(row).child('tr:nth(1) div.x-grid3-row-body', true);
        if(this.fireEvent('beforecollapse', this, record, body, row.rowIndex) !== false){
            this.state[record.id] = false;
            Ext.fly(row).replaceClass('x-grid3-row-expanded', 'x-grid3-row-collapsed');
            this.fireEvent('collapse', this, record, body, row.rowIndex);
        }
    }
});
// vim: ts=4:sw=4
/*global Ext */

Ext.override(Ext.layout.FormLayout, {
    adjustWidthAnchor : function(value, comp){
        return value - (comp.isFormField  ? (comp.hideLabel ? 0 : this.labelAdjust) : 0) - comp.el.getMargins('lr');
    }
});

/* CURRIKI-2989
 * From https://extjs.com/forum/showthread.php?p=195381
 */
Ext.override(Ext.layout.FormLayout, {
    getAnchorViewSize : function(ct, target)
    {
        return (ct.body || ct.el).getStyleSize();
    }
});

Ext.override(Ext.layout.AnchorLayout, {
    getAnchorViewSize : function(ct, target){
        var el = ct.body||ct.el; 
        return {
        	width: el.dom.clientWidth - el.getPadding('lr'),
        	height: el.dom.clientHeight - el.getPadding('tb')
        };
    },
    // private
    adjustWidthAnchor : function(value, comp){
        return value - comp.el.getMargins('lr');
    },

    // private
    adjustHeightAnchor : function(value, comp){
        return value - comp.el.getMargins('tb');
    }
});

// Fix some state issues
// From: http://extjs.com/forum/showthread.php?t=34787&page=2
Ext.override(Ext.Component, {
    saveState : function(){
        if(Ext.state.Manager && this.stateful !== false){
            var state = this.getState();
            if(this.fireEvent('beforestatesave', this, state) !== false){
                Ext.state.Manager.set(this.stateId || this.id, state);
                this.fireEvent('statesave', this, state);
            }
        }
    },

    stateful : false
}); 


// Fix issues with combo boxes not hiding with correct method
// Default uses display CSS method with no thought to what hideMode is
Ext.override(Ext.form.TriggerField, {
    // private
    onShow : function(){
        if(this.wrap){
            this.wrap.show();
        }
    },

    // private
    onHide : function(){
        this.wrap.hide();
    },

	// CURRIKI-2873
	// Width seems to end up being 0 for the wrapper when the element is
	// not being displayed in Safari

	// private
	onRender : function(ct, position){
		Ext.form.TriggerField.superclass.onRender.call(this, ct, position);
		this.wrap = this.el.wrap({cls: "x-form-field-wrap"});
		this.trigger = this.wrap.createChild(this.triggerConfig ||
				{tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass});
		if(this.hideTrigger){
			this.trigger.setDisplayed(false);
		}
		this.initTrigger();
		if(!this.width){
			this.wrap.setWidth(this.el.getWidth()+this.trigger.getWidth());
		} else {
			// Added this for CURRIKI-2873
			this.wrap.setWidth(this.width);
		}
    }
});

// CURRIKI-2724
// Remove "Refresh" button from paging toolbar
Ext.override(Ext.PagingToolbar, {
	// private
    onRender : function(ct, position){
        Ext.PagingToolbar.superclass.onRender.call(this, ct, position);
        this.first = this.addButton({
            tooltip: this.firstText,
            iconCls: "x-tbar-page-first",
            text: _('search.pagination.first.button'),
            disabled: true,
            handler: this.onClick.createDelegate(this, ["first"])
        });
        this.prev = this.addButton({
            tooltip: this.prevText,
            iconCls: "x-tbar-page-prev",
            text: _('search.pagination.prev.button'),
            disabled: true,
            handler: this.onClick.createDelegate(this, ["prev"])
        });
        this.addSeparator();
        this.add(this.beforePageText);
        this.field = Ext.get(this.addDom({
           tag: "input",
           type: "text",
           size: "3",
           value: "1",
           cls: "x-tbar-page-number"
        }).el);
        this.field.on("keydown", this.onPagingKeydown, this);
        this.field.on("focus", function(){this.dom.select();});
        this.afterTextEl = this.addText(String.format(this.afterPageText, 1));
        this.field.setHeight(18);
        this.addSeparator();
        this.next = this.addButton({
            tooltip: this.nextText,
            iconCls: "x-tbar-page-next",
            text: _('search.pagination.next.button'),
            disabled: true,
            handler: this.onClick.createDelegate(this, ["next"])
        });
        this.last = this.addButton({
            tooltip: this.lastText,
            iconCls: "x-tbar-page-last",
            text: _('search.pagination.last.button'),
            disabled: true,
            handler: this.onClick.createDelegate(this, ["last"])
        });
/* Removed for CURRIKI-2724
        this.addSeparator();
*/
        this.loading = this.addButton({
            hidden: true, // Added for CURRIKI-2724 - We can't actually remove this item as it is referred to elsewhere
            tooltip: this.refreshText,
            iconCls: "x-tbar-loading",
            handler: this.onClick.createDelegate(this, ["refresh"])
        });

        if(this.displayInfo){
            this.displayEl = Ext.fly(this.el.dom).createChild({cls:'x-paging-info'});
        }
        if(this.dsLoaded){
            this.onLoad.apply(this, this.dsLoaded);
        }
    }
});

Ext.override(Ext.ux.Andrie.pPageSize, {
	setPageSize:function(value, forced){
		var pt = this.pagingToolbar;
		this.combo.collapse();
		value = parseInt(value) || parseInt(this.combo.getValue());
		value = (value>0)?value:1;
		if (value == pt.pageSize){
			return;

			/* CURRIKI-2665
			 *  - Always force a reload so that the page size is remembered */
		}else{
			this.pagingToolbar.pageSize = value;
			this.pagingToolbar.doLoad(Math.floor(this.pagingToolbar.cursor/this.pagingToolbar.pageSize) * this.pagingToolbar.pageSize);
		}
		this.updateStore();
	}
});
// vim: ts=4:sw=4
/*global Ext */
/*global _ */

Ext.BLANK_IMAGE_URL = '/xwiki/skins/curriki8/extjs/resources/images/default/s.gif';

Ext.Ajax.defaultHeaders = {
	 'Accept': 'application/json'
	,'Content-Type': 'application/json; charset=utf-8;'
};
Ext.Ajax.disableCaching=false;
Ext.Ajax.timeout=120000;


if (!('console' in window) || !('firebug' in console)){
	var names = ["log", "debug", "info", "warn", "error", "assert", "dir",
	             "dirxml", "group", "groupEnd", "time", "timeEnd", "count",
	             "trace", "profile", "profileEnd"];
	window.console = {};
	for (var i = 0; i < names.length; ++i)
		window.console[names[i]] = Ext.emptyFn
}
console.log('initing Curriki');

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.apply(Ext.QuickTips.getQuickTip(), {
		showDelay: 1000
		,hideDelay: 0
	});
});

/*
 * Example of dynamically loading javascript
function initLoader() {
  var script = document.createElement("script");
  script.src = "http://www.google.com/jsapi?key=ABCDEFG&callback=loadMaps";
  script.type = "text/javascript";
  document.getElementsByTagName("head")[0].appendChild(script);
}
*/

Ext.ns('Curriki');
Ext.ns('Curriki.module');

Ext.onReady(function(){
	Curriki.loadingMask = new Ext.LoadMask(Ext.getBody(), {msg:_('loading.loading_msg')});

    Ext.Ajax.on('beforerequest', function(conn, options){
console.log('beforerequest', conn, options);
		Curriki.showLoading(options.waitMsg);
	});
    Ext.Ajax.on('requestcomplete', function(conn, response, options){
console.log('requestcomplete', conn, response, options);
		Curriki.hideLoading();
	});
    Ext.Ajax.on('requestexception', function(conn, response, options){
console.log('requestexception', conn, response, options);
		Curriki.hideLoading();
	});
});


Curriki.id = function(prefix){
	return Ext.id('', prefix+':');
};

Curriki.showLoading = function(msg){
	if (!Ext.isEmpty(Curriki.loadingMask)){
		msg = msg||'loading.loading_msg';
		Curriki.loadingMask.msg = _(msg);
		Curriki.loadingMask.enable();
		Curriki.loadingMask.show();
	}
}

Curriki.hideLoading = function(){
	if (!Ext.isEmpty(Curriki.loadingMask)){
		Curriki.loadingMask.hide();
		Curriki.loadingMask.disable();
	}
}

Curriki.logView = function(page){
	// Usage in site example:
	// <a onClick="javascript:Curriki.logView('/Download/attachment/${space}/${name}/${attach.filename}');"> .. </a>
	if (window.pageTracker) {
		pageTracker._trackPageview(page);
	} else {
		console.info('Would track: ', page);
	}
}

Curriki.start = function(callback){
console.log('Start Callback: ', callback);
	var args = {};

	if ("object" === typeof callback){
		if (callback.args){
			args = callback.args;
		}
		if (callback.callback){
			callback = callback.callback;
		} else if (callback.module){
			callback = callback.module;
		}
	}

	if ("string" === typeof callback){
		var module = eval('(Curriki.module.'+callback.toLowerCase()+')');

		if (module && "function" === typeof module.init){
			// callback is the name of a module
			module.init(args);
			if ("function" === typeof module.start) {
				callback = module.start;
			} else {
				callback = Ext.emptyFn;
			}
		} else {
			// callback is a known string
			switch(callback){
				default:
					callback = Ext.emptyFn;
					break;
			}
		}
	}

	if ("function" === typeof callback) {
		callback(args);
	}
};

Curriki.init = function(callback){
console.log('Curriki.init: ', callback);
	if (Ext.isEmpty(Curriki.initialized)) {
		Curriki.data.user.GetUserinfo(function(){Curriki.start(callback);});
		Curriki.initialized = true;
	} else {
		Curriki.start(callback);
	}
};
// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

Ext.ns('Curriki.data.user');
Curriki.data.user = {
	 me:{username:'XWiki.XWikiGuest', fullname:'Guest'}
	,collections:[]
	,groups:[]

	,collectionChildren:[]
	,groupChildren:[]

	,json_prefix:'/xwiki/curriki/users/'
	,user_try:0
	,GetUserinfo:function(callback){
		this.user_try++;
		Ext.Ajax.request({
			 url: this.json_prefix+'me'
			,method:'GET'
			,disableCaching:true
			,headers: {
				'Accept':'application/json'
			}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot get user information');
					if (this.user_try < 5){
						this.GetUserinfo(callback);
					} else {
						console.error('Cannot get user information', response, options);
						alert(_('add.servertimedout.message.text'));
					}
				} else {
					this.user_try = 0;
					this.me = o;
					this.GetCollections(callback);
				}
			}
			,failure:function(response, options){
				console.error('Cannot get user information', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}

	,collection_try:0
	,GetCollections:function(callback){
		this.collection_try++;
		Ext.Ajax.request({
			 url: this.json_prefix+this.me.username+'/collections'
			,method:'GET'
			,disableCaching:true
			,headers: {
				'Accept':'application/json'
			}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot read user\'s collection information');
					if (this.collection_try < 5){
						this.GetCollections(callback);
					} else {
						console.error('Cannot get user\'s collection information', response, options);
						alert(_('add.servertimedout.message.text'));
					}
				} else {
					this.collection_try = 0;
					this.collections = o;
					this.collectionChildren = this.CreateCollectionChildren();
console.log('Collections: ', this.collectionChildren);
					this.GetGroups(callback);
				}
			}
			,failure:function(response, options){
				console.error('Cannot get user\'s collection information', response, options);
				alert(_('add.servertimedout.message.text'));
				this.collections = [];
				this.GetGroups(callback);
			}
		});
	}

	,group_try:0
	,GetGroups:function(callback){
		Ext.Ajax.request({
			 url: this.json_prefix+this.me.username+'/groups'
			,method:'GET'
			,disableCaching:true
			,headers: {
				'Accept':'application/json'
			}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot read user\'s group information');
					if (this.group_try < 5){
						this.GetGroups(callback);
					} else {
						throw {message: "GetUserinfo: Json object not found"};
						console.error('Cannot get user\'s group information', response, options);
						alert(_('add.servertimedout.message.text'));
					}
				} else {
					this.group_try = 0;
					this.groups = o;
					this.groupChildren = this.CreateGroupChildren();
					callback();
				}
			}
			,failure:function(response, options){
				console.error('Cannot get user\'s group information', response, options);
				alert(_('add.servertimedout.message.text'));
				this.groups = [];
				callback();
			}
		});
	}

	,CreateCollectionChildren:function(){
		var retVal = [];

		this.collections.each(function(collection){
			var colInfo = {
				 id:collection.collectionPage
				,text:collection.displayTitle
				,qtip:collection.description
				,cls:'resource-'+collection.assetType
				,allowDrag:false
				,allowDrop:true
			};

			if (Ext.isArray(collection.children) && collection.children.length > 0) {
				var children = [];

				collection.children.each(function(child){
					var childInfo = {
						 id:child.assetpage
						,order:child.order
						,text:child.displayTitle
						,qtip:child.description
						,cls:'ctv-resource resource-'+child.assetType
						,allowDrag:false
						,allowDrop:false
					};

					if ("undefined" === typeof child.rights || !child.rights.view){
						childInfo.text = _('add.chooselocation.resource_unavailable');
						childInfo.qtip = undefined;
						childInfo.disabled = true;
						childInfo.leaf = true;
						childInfo.cls = childInfo.cls+' rights-unviewable';
					} else if (child.assetType.search(/Composite$/) === -1){
						childInfo.leaf = true;
					} else {
						childInfo.leaf = false;
						childInfo.allowDrop = true;
						childInfo.disallowDropping = (child.rights.edit?null:true);
					}

					children.push(childInfo);
				});

				colInfo.children = children;
			} else {
				colInfo.leaf = false;
				colInfo.children = [];
			}

			retVal.push(colInfo);
		});

		return retVal;
	}

	,CreateGroupChildren:function(){
		var retVal = [];

		this.groups.each(function(group){
			if (group.editableCollectionCount > 0) {
				var colInfo = {
					 id:group.groupSpace
					,currikiNodeType:'group'
					,text:group.displayTitle
					,qtip:group.description
					,cls:'curriki-group'
					,allowDrag:false
					,allowDrop:true
					,disallowDropping:true
				};

				retVal.push(colInfo);
			}
		});

		return retVal;
	}
};

// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */


Ext.ns('Curriki.data.ict');
// TODO:  Fetch the list from /xwiki/curriki/metadata/XWiki.AssetClass/fields/instructional_component2  OR  Get filled in JS created by xwiki
Curriki.data.ict.list = ["activity_assignment","activity_exercise","activity_lab","activity_game","activity_worksheet","activity_problemset","activity_webquest","book_fiction","book_nonfiction","book_readings","book_textbook","curriculum_assessment","curriculum_course","curriculum_unit","curriculum_lp","curriculum_rubric","curriculum_scope","curriculum_standards","curriculum_studyguide","curriculum_syllabus","curriculum_tutorial","curriculum_workbook","resource_animation","resource_diagram","resource_glossary","resource_index","resource_photograph","resource_presentation","resource_collection","resource_script","resource_speech","resource_table","resource_template","resource_webcast","other"];
Curriki.data.ict.data = [ ];
Curriki.data.ict.list.each(function(ict){
	Curriki.data.ict.data.push([
		 ict
		,_('XWiki.AssetClass_instructional_component2_'+ict)
	]);
});
Curriki.data.ict.store = new Ext.data.SimpleStore({
	fields: ['id', 'ict'],
	data: Curriki.data.ict.data
});


Ext.ns('Curriki.data.el');
// TODO:  Fetch the list from /xwiki/curriki/metadata/XWiki.AssetClass/fields/educational_level2  OR  Get filled in JS created by xwiki
Curriki.data.el.list = [ "prek", "gr-k-2", "gr-3-5", "gr-6-8", "gr-9-10", "gr-11-12", "college_and_beyond", "professional_development", "special_education", "na" ];
Curriki.data.el.data = [ ];
Curriki.data.el.list.each(function(el){
	Curriki.data.el.data.push({
		 inputValue:el
		,boxLabel:_('XWiki.AssetClass_educational_level2_'+el)
	});
});


Ext.ns('Curriki.data.rights');
// TODO:  Fetch the list from /xwiki/curriki/metadata/XWiki.AssetClass/fields/rights  OR  Get filled in JS created by xwiki
Curriki.data.rights.list = [ "public", "members", "private" ];
Curriki.data.rights.initial = Curriki.data.rights.list[0];
Curriki.data.rights.data = [ ];
Curriki.data.rights.list.each(function(right){
	Curriki.data.rights.data.push({
		 inputValue:right
		,boxLabel:_('XWiki.AssetClass_rights_'+right)
		,checked:Curriki.data.rights.initial == right?true:false
	});
});


Ext.ns('Curriki.data.language');
// TODO:  Fetch the list from /xwiki/curriki/metadata/XWiki.AssetClass/fields/language  OR  Get filled in JS created by xwiki
Curriki.data.language.list = ["eng","ind","zho","nld","fra","deu","hin","ita","jpn","kor","nep","por","rus","sin","spa","tam","999"];
Curriki.data.language.initial = Curriki.data.language.list[0];
Curriki.data.language.data = [ ];
Curriki.data.language.list.each(function(lang){
	Curriki.data.language.data.push([
		 lang
		,_('XWiki.AssetClass_language_'+lang)
	]);
});
Curriki.data.language.store = new Ext.data.SimpleStore({
	fields: ['id', 'language'],
	data: Curriki.data.language.data
});


Ext.ns('Curriki.data.licence');
// TODO:  Fetch the list from /xwiki/curriki/metadata/XWiki.AssetLicenseClass/fields/licenseType2  OR  Get filled in JS created by xwiki
Curriki.data.licence.list = [ "Licences.CurrikiLicense", "Licences.PublicDomain", "Licences.CreativeCommonsAttributionNon-Commercial", "Licences.CreativeCommonsAttributionNoDerivatives", "Licences.CreativeCommonsAttributionNon-CommercialNoDerivatives", "Licences.CreativeCommonsAttributionSharealike", "Licences.CreativeCommonsAttributionNon-CommercialShareAlike" ];
Curriki.data.licence.initial = Curriki.data.licence.list[0];
Curriki.data.licence.data = [ ];
Curriki.data.licence.list.each(function(lic){
	Curriki.data.licence.data.push([
		 lic
		,_('XWiki.AssetLicenseClass_licenseType2_'+lic)
	]);
});
Curriki.data.licence.store = new Ext.data.SimpleStore({
	fields: ['id', 'licence'],
	data: Curriki.data.licence.data
});


Ext.ns('Curriki.data.fw_item');
// For fwTree
Curriki.data.fw_item.fwCheckListener = function(node, checked){
	var validator = Ext.getCmp('fw_items-validation');
	if (validator) {
		validator.setValue(validator.getValue()+(checked?1:-1));
	}
	if (checked){
		if ("undefined" !== typeof node.parentNode){
			if (!node.parentNode.ui.isChecked()){
				node.parentNode.ui.toggleCheck();
			}
		}
	} else {
		if (Ext.isArray(node.childNodes)){
			node.childNodes.each(function(node){
				if (node.ui.isChecked()) {
					node.ui.toggleCheck();
				}
			});
		}
	}
};

// TODO:  Fetch the list from /xwiki/curriki/metadata/XWiki.AssetLicenseClass/fields/fw_items  OR  Get filled in JS created by xwiki
Curriki.data.fw_item.fwMap = {"TREEROOTNODE":[{"id":"FW_masterFramework.WebHome","parent":""}],"FW_masterFramework.WebHome":[{"id":"FW_masterFramework.Arts","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.VocationalEducation","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Education&Teaching","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.EducationalTechnology","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Health","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Information&MediaLiteracy","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.LanguageArts","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Mathematics","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Science","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.SocialStudies","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.ForeignLanguages","parent":"FW_masterFramework.WebHome"}],"FW_masterFramework.Information&MediaLiteracy":[{"id":"FW_masterFramework.EvaluatingSources","parent":"FW_masterFramework.Information&MediaLiteracy"},{"id":"FW_masterFramework.MediaEthics","parent":"FW_masterFramework.Information&MediaLiteracy"},{"id":"FW_masterFramework.OnlineSafety","parent":"FW_masterFramework.Information&MediaLiteracy"},{"id":"FW_masterFramework.ResearchMethods","parent":"FW_masterFramework.Information&MediaLiteracy"}],"FW_masterFramework.SocialStudies":[{"id":"FW_masterFramework.Anthropology","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Careers_5","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Civics","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.CurrentEvents","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Economics","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Entrepreneurship","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Geography","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.GlobalAwareness","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Government","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.History Local","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.PoliticalSystems","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Psychology","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Religion","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Research_0","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Sociology","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.StateHistory","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Technology_1","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Thinking&ProblemSolving","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.UnitedStatesGovernment","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.UnitedStatesHistory","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.WorldHistory","parent":"FW_masterFramework.SocialStudies"}],"FW_masterFramework.Arts":[{"id":"FW_masterFramework.Architecture","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Careers","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Dance","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.DramaDramatics","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Film","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.History","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Music","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Photography","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.PopularCulture","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Technology","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.VisualArts","parent":"FW_masterFramework.Arts"}],"FW_masterFramework.EducationalTechnology":[{"id":"FW_masterFramework.Careers_0","parent":"FW_masterFramework.EducationalTechnology"},{"id":"FW_masterFramework.IntegratingTechnologyintotheClassroom","parent":"FW_masterFramework.EducationalTechnology"},{"id":"FW_masterFramework.UsingMultimedia&theInternet","parent":"FW_masterFramework.EducationalTechnology"}],"FW_masterFramework.VocationalEducation":[{"id":"FW_masterFramework.Agriculture_0","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.Business","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.Careers_6","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.OccupationalHomeEconomics","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.School-to-work","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.Technology_2","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.Trade&Industrial","parent":"FW_masterFramework.VocationalEducation"}],"FW_masterFramework.Health":[{"id":"FW_masterFramework.BodySystems&Senses","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.Careers_1","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.EnvironmentalHealth","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.HumanSexuality","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.MentalEmotionalHealth","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.Nutrition","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.SafetySmokingSubstanceAbusePrevention","parent":"FW_masterFramework.Health"}],"FW_masterFramework.Education&Teaching":[{"id":"FW_masterFramework.Accessibility","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.AdultEducation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.BilingualEducation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.ClassroomManagement","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.EarlyChildhoodEducation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.EducationAdministration","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.EducationalFoundations","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.EducationalPsychology","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.InstructionalDesign","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.MeasurementEvaluation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.Mentoring","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.MulticulturalEducation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.StandardsAlignment","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.TeachingTechniques","parent":"FW_masterFramework.Education&Teaching"}],"FW_masterFramework.ForeignLanguages":[{"id":"FW_masterFramework.Alphabet","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Careers_7","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.CulturalAwareness","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Grammar","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.InformalEducation","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Linguistics","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.ListeningComprehension","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Reading","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Speaking","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Spelling","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.VocabularyWriting","parent":"FW_masterFramework.ForeignLanguages"}],"FW_masterFramework.Mathematics":[{"id":"FW_masterFramework.Algebra","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Appliedmathematics","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Arithmetic","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Calculus","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Careers_3","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.DataAnalysis&Probability","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Equations","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Estimation","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Geometry","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Graphing","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Measurement","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.NumberSense&Operations","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Patterns","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.ProblemSolving","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Statistics","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Trigonometry","parent":"FW_masterFramework.Mathematics"}],"FW_masterFramework.Science":[{"id":"FW_masterFramework.Agriculture","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Astronomy","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Biology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Botany","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Careers_4","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Chemistry","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Earthscience","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Ecology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Engineering","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Generalscience","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Geology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.HistoryofScience","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.LifeSciences","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Meteorology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.NaturalHistory","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Oceanography","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Paleontology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.PhysicalSciences","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Physics","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.ProcessSkills","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Technology_0","parent":"FW_masterFramework.Science"}],"FW_masterFramework.LanguageArts":[{"id":"FW_masterFramework.Alphabet_0","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Careers_2","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Journalism","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Listening&Speaking","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Literature","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Phonics","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.ReadingComprehension","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Research","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Spelling_0","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.StoryTelling","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Vocabulary","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Writing","parent":"FW_masterFramework.LanguageArts"}]};
var fwItem = 'FW_masterFramework.WebHome';
Curriki.data.fw_item.fwAddNode = function(fwMap, nodeName){
	var nodeInfo = {
		 id:nodeName
		,text:_('XWiki.AssetClass_fw_items_'+nodeName)
		,checked:false
		,listeners:{
			checkchange: Curriki.data.fw_item.fwCheckListener
		}
	};
	if ("undefined" !== typeof fwMap[nodeName]){
		var children = [];
		fwMap[nodeName].each(function(childNode){
			children.push(Curriki.data.fw_item.fwAddNode(fwMap, childNode.id));
		});
		nodeInfo.children = children;
		nodeInfo.cls = 'fw-item fw-item-parent';
	} else {
		nodeInfo.leaf = true;
		nodeInfo.cls = 'fw-item fw-item-bottom';
	}

	return nodeInfo;
};
Curriki.data.fw_item.fwChildren = Curriki.data.fw_item.fwAddNode(Curriki.data.fw_item.fwMap, 'FW_masterFramework.WebHome').children;
Ext.ns('Curriki.ui.component.asset');
Curriki.ui.component.asset.getFwTree = function(){
	return {
		 xtype:'treepanel'
		,loader: new Ext.tree.TreeLoader({
			preloadChildren:true
		})
		,id:'fw_items-tree'
		,useArrows:true
		,autoHeight:true
		,border:false
		,cls:'fw-tree'
		,animate:true
		,enableDD:false
		,containerScroll:true
		,rootVisible:true
		,root: new Ext.tree.AsyncTreeNode({
			 text:_('XWiki.AssetClass_fw_items_FW_masterFramework.WebHome')
			,id:'FW_masterFramework.WebHome'
			,cls:'fw-item-top fw-item-parent fw-item'
			,leaf:false
			,expanded:true
			,children:Curriki.data.fw_item.fwChildren
		})
	};
};
// vim: ts=4:sw=4
/*global Curriki */
/*global _ */

Ext.ns('Curriki.assets');
Curriki.assets = {
	 json_prefix:'/xwiki/curriki/assets'
	,CreateAsset:function(parentPage, publishSpace, callback){
		Ext.Ajax.request({
			 url: this.json_prefix
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {
				'parent':parentPage||''
				,'publishSpace':publishSpace||''
			}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with
				//   assetPage, assetType, and fullAssetType items
				var o = json.evalJSON(true);
				if(!o || !o.assetPage) {
					console.warn('Cannot create resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot create resource', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,GetAssetInfo:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage
			,method:'GET'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with metadata
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot get resource metadata', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot get resource metadata', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,GetMetadata:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/metadata'
			,method:'GET'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with metadata
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot get resource metadata', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot get resource metadata', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,SetMetadata:function(assetPage, metadata, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/metadata'
			,method:'PUT'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: metadata
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with metadata
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot set resource metadata', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot set resource metadata', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,CreateExternal:function(assetPage, linkUrl, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/externals'
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {'link':linkUrl}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with new ref info
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot create external link', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot create external link', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,CreateSubasset:function(assetPage, subassetPage, order, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/subassets'
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {page:subassetPage, order:order}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with new ref info
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot add subasset', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					// We need to refresh the collections for the user
					Curriki.data.user.GetCollections(function(){if ('function' === typeof callback) {callback(o);}});
				}
			}
			,failure:function(response, options){
				console.error('Cannot add subasset', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,CreateFolder:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/subassets'
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {collectionType:'folder'}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object for the current asset
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot create folder', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot create folder', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,CreateCollection:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/subassets'
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {collectionType:'collection'}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object for the current asset
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot create collection', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot create collection', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,CreateVIDITalk:function(assetPage, videoId, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/viditalks'
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {page:assetPage, videoId:videoId}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with new ref info
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot add video', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot add video', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,Publish:function(assetPage, space, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/published'
			,method:'PUT'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {space: space}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with the new asset URL
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot publish resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot publish resource', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,ReorderRootCollection:function(place, which, original, wanted, callback){
		Ext.Ajax.request({
			url: '/xwiki/curriki/'+place+'/'+which+'/collections'
			,method:'PUT'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData:{original:original, wanted:wanted}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot reorder', response.responseText, options);
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot reorder', response, options);
				if (response.status == 412){
					if (response.responseText.search(/ 107 [^ ]+ 101:/) !== -1){
						var msgPfx = 'mycurriki.collections.reorder.';
						if (place === 'groups'){
							msgPfx = 'groups_curriculum_collections_reorder.';
						}
						alert(_(msgPfx+'error'));
					} else {
						alert(_('add.servertimedout.message.text'));
					}
				} else {
					alert(_('add.servertimedout.message.text'));
				}
			}
		});
	}
	,UnnominateAsset:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/unnominate'
			,method:'PUT'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with the new asset URL
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot unnominate resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot unnominate resource', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,NominateAsset:function(assetPage, comments, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/nominate'
			,method:'PUT'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {comments:comments}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with the new asset URL
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot nominate resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot nominate resource', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,PartnerAsset : function(assetPage, callback) {
		Ext.Ajax.request({
			url : this.json_prefix + '/' + assetPage + '/partner',
			method : 'PUT',
			headers : {
				'Accept' : 'application/json',
				'Content-type' : 'application/json'
			},
			jsonData : {},
			scope : this,
			success : function(response, options) {
				var json = response.responseText;
				var o = json.evalJSON(true);
				if (!o) {
					console.warn('Cannot set as Partner resource',
							response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			},
			failure : function(response, options) {
				console.error('Cannot set as Partner resource', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,SetAsterixReview : function(assetPage, callback, asterixReviewValue) {
		Ext.Ajax.request({
			url : this.json_prefix + "/" + assetPage + "/assetManager",
			method : 'PUT',
			headers : {
				'Accept' : 'application/json',
				'Content-type' : 'application/json'
			},
			jsonData : {
				action : 'setAsterixReview',
				asterixReviewValue : asterixReviewValue
				},
			scope : this,
			success : function(response, options) {
				var json = response.responseText;
				var o = json.evalJSON(true);
				if (!o) {
					console.warn('Cannot set as Partner resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			},
			failure : function(response, options) {
				console.error('Cannot execute the action', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,RemoveAsterixReview : function(assetPage, callback) {
		Ext.Ajax.request({
			url : this.json_prefix + "/" + assetPage + "/assetManager",
			method : 'PUT',
			headers : {
				'Accept' : 'application/json',
				'Content-type' : 'application/json'
			},
			jsonData : {
				action : 'removeAsterixReview'
				},
			scope : this,
			success : function(response, options) {
				var json = response.responseText;
				var o = json.evalJSON(true);
				if (!o) {
					console.warn('Cannot set as Partner resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			},
			failure : function(response, options) {
				console.error('Cannot execute the action', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
}
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
		,createNode:function(attr){
console.log('createNode: ',attr);

			if ('string' === typeof attr.id) {
				var parent = Curriki.ui.treeLoader.Base.superclass.createNode.call(this, attr);
console.log('createNode: parent',parent);
				return parent;
			}

			var childInfo = {
				 id:attr.assetpage||attr.collectionPage
				,text:attr.displayTitle
				,qtip:attr.description
				,cls:'resource-'+attr.assetType
				,allowDrag:false
				,allowDrop:false
			}

			if (attr.rights && !attr.rights.view){
				childInfo.text = _('add.chooselocation.resource_unavailable');
				childInfo.qtip = _('add.chooselocation.resource_unavailable_tooltip');
				childInfo.disabled = true;
				childInfo.allowDrop = false;
				childInfo.leaf = true;
				childInfo.cls = childInfo.cls+' rights-unviewable';
			} else if (attr.assetType && attr.assetType.search(/Composite$/) === -1){
				childInfo.leaf = true;
			} else if (attr.assetType){
				childInfo.leaf = false;
				childInfo.allowDrop = (attr.rights && attr.rights.edit);
			}

			// ?? = attr.order;

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

console.log('createNode: End ',childInfo);
			return(childInfo.leaf
			       ? new Ext.tree.TreeNode(childInfo)
			       : new Ext.tree.AsyncTreeNode(childInfo));
		}

		,requestData:function(node, callback){
			if (node.attributes.currikiNodeType === 'group'){
				this.dataUrl = '/xwiki/curriki/groups/'+node.attributes.id+'/collections';
			} else {
				this.dataUrl = '/xwiki/curriki/assets/'+node.attributes.id+'/subassets';
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
