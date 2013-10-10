/*!
Math.uuid.js (v1.4)
http://www.broofa.com
mailto:robert@broofa.com

Copyright (c) 2009 Robert Kieffer
Dual licensed under the MIT and GPL licenses.
*/

/*
 * Generate a random uuid.
 *
 * USAGE: Math.uuid(length, radix)
 *   length - the desired number of characters
 *   radix  - the number of allowable values for each character.
 *
 * EXAMPLES:
 *   // No arguments  - returns RFC4122, version 4 ID
 *   >>> Math.uuid()
 *   "92329D39-6F5C-4520-ABFC-AAB64544E172"
 * 
 *   // One argument - returns ID of the specified length
 *   >>> Math.uuid(15)     // 15 character ID (default base=62)
 *   "VcydxgltxrVZSTV"
 *
 *   // Two arguments - returns ID of the specified length, and radix. (Radix must be <= 62)
 *   >>> Math.uuid(8, 2)  // 8 character ID (base=2)
 *   "01001010"
 *   >>> Math.uuid(8, 10) // 8 character ID (base=10)
 *   "47473046"
 *   >>> Math.uuid(8, 16) // 8 character ID (base=16)
 *   "098F4D35"
 */
Math.uuid = (function() {
  // Private array of chars to use
  var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split(''); 

  return function (len, radix) {
    var chars = CHARS, uuid = [];
    radix = radix || chars.length;

    if (len) {
      // Compact form
      for (var i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
    } else {
      // rfc4122, version 4 form
      var r;

      // rfc4122 requires these characters
      uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
      uuid[14] = '4';

      // Fill in random data.  At i==19 set the high bits of clock sequence as
      // per rfc4122, sec. 4.1.5
      for (var i = 0; i < 36; i++) {
        if (!uuid[i]) {
          r = 0 | Math.random()*16;
          uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
        }
      }
    }

    return uuid.join('');
  };
})();
/*
 * Ext Core Library Examples 3.0
 * http://extjs.com/
 * Copyright(c) 2006-2009, Ext JS, LLC.
 * 
 * The MIT License
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

Ext.ns('Ext.ux');

Ext.ux.JSONP = (function(){
    var _queue = [],
        _current = null,
        _nextRequest = function() {
            _current = null;
            if(_queue.length) {
                _current = _queue.shift();
    			_current.script.src = _current.url + '?' + _current.params;
    			document.getElementsByTagName('head')[0].appendChild(_current.script);
            }
        };

    return {
        request: function(url, o) {
            if(!url) {
                return;
            }
            var me = this;

            o.params = o.params || {};
            if(o.callbackKey) {
                o.params[o.callbackKey] = 'Ext.ux.JSONP.callback';
            }
            var params = Ext.urlEncode(o.params);

            var script = document.createElement('script');
			script.type = 'text/javascript';

            if(o.isRawJSON) {
                if(Ext.isIE) {
                    Ext.fly(script).on('readystatechange', function() {
                        if(script.readyState == 'complete') {
                            var data = script.innerHTML;
                            if(data.length) {
                                me.callback(Ext.decode(data));
                            }
                        }
                    });
                }
                else {
                     Ext.fly(script).on('load', function() {
                        var data = script.innerHTML;
                        if(data.length) {
                            me.callback(Ext.decode(data));
                        }
                    });
                }
            }

            _queue.push({
                url: url,
                script: script,
                callback: o.callback || function(){},
                scope: o.scope || window,
                params: params || null
            });

            if(!_current) {
                _nextRequest();
            }
        },

        callback: function(json) {
            _current.callback.apply(_current.scope, [json]);
            Ext.fly(_current.script).removeAllListeners();
            document.getElementsByTagName('head')[0].removeChild(_current.script);
            _nextRequest();
        }
    }
})();
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
	,'Content-Type': 'application/json; charset=utf-8'
};
Ext.Ajax.disableCaching=false;
Ext.Ajax.timeout=120000;


if (!('console' in window) || !(console.log) /* || !('firebug' in console) */){
	var names = ["log", "debug", "info", "warn", "error", "assert", "dir",
	             "dirxml", "group", "groupEnd", "time", "timeEnd", "count",
	             "trace", "profile", "profileEnd"];
	window.console = {};
	for (var i = 0; i < names.length; ++i)
		window.console[names[i]] = Ext.emptyFn
}
console.log('initing Curriki');
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
Curriki.console = window.console;
Ext.ns('Curriki.module');

Curriki.requestCount = 0;

Ext.onReady(function(){
	Curriki.loadingCount = 0;
	Curriki.hideLoadingMask = false;
	Curriki.loadingMask = new Ext.LoadMask(Ext.getBody(), {msg:_('loading.loading_msg')});

    Ext.Ajax.on('beforerequest', function(conn, options){
        options.requestCount = Curriki.requestCount++;
        console.log('beforerequest (' + options.requestCount + ")", conn, options);
        // protection
        //if(options.requestCount>10) throw "No more than 10 requests!";
        Curriki.Ajax.beforerequest(conn, options);
	});
    Ext.Ajax.on('requestcomplete', function(conn, response, options){
console.log('requestcomplete (' + options.requestCount + ")", conn, response, options);
		Curriki.Ajax.requestcomplete(conn, response, options);
	});
    Ext.Ajax.on('requestexception', Curriki.notifyException);
});

Curriki.Ajax = {
	'beforerequest': function(conn, options) {
		Curriki.showLoading(options.waitMsg);
	}

	,'requestcomplete': function(conn, response, options) {
		Curriki.hideLoading();
	}

	,'requestexception': function(conn, response, options) {
		Curriki.hideLoading(true);
	}
};


Curriki.notifyException = function(exception){
        console.log('requestexception', exception);
		Curriki.Ajax.requestexception(null, null, null);
        Curriki.logView('/features/ajax/error/');
        var task = new Ext.util.DelayedTask(function(){
            if(!Ext.isEmpty(Curriki.loadingMask)) {
                Curriki.loadingMask.hide();
                Curriki.loadingMask.disable();
            }
            Ext.MessageBox.alert(_("search.connection.error.title"),
                    _("search.connection.error.body"));
        });
        task.delay(100);
	};

Curriki.id = function(prefix){
	return Ext.id('', prefix+':');
};

Curriki.showLoading = function(msg, multi){
	if (multi === true) {
		Curriki.loadingCount++;
	}
	if (!Curriki.hideLoadingMask && !Ext.isEmpty(Curriki.loadingMask)){
		msg = msg||'loading.loading_msg';
		Curriki.loadingMask.msg = _(msg);
		Curriki.loadingMask.enable();
		Curriki.loadingMask.show();
	}
}

Curriki.isISO8601DateParsing = function() {
    if(typeof(Curriki.ISO8601DateParsing)!="undefined") return Curriki.ISO8601DateParsing;
    var s = navigator.userAgent;
    Curriki.ISO8601DateParsing = s.indexOf("OS 5")!=-1 && ( s.indexOf("iPhone")!=-1 || s.indexOf("iPod")!=-1 || s.indexOf("iPad")!=-1);
    console.log("Set ISO8601 parsing to " + Curriki.ISO8601DateParsing);
    return Curriki.ISO8601DateParsing;
}

Curriki.hideLoading = function(multi){
	if (multi === true) {
		Curriki.loadingCount--;
	}
	if (Curriki.loadingCount == 0 && !Ext.isEmpty(Curriki.loadingMask)){
		Curriki.loadingMask.hide();
		Curriki.loadingMask.disable();
	} else if (Curriki.loadingCount < 0) {
		Curriki.loadingCount = 0;
	}
}

Curriki.logEvent = function(eventParams, followup) {
    var gaqParams=eventParams.reverse();
    gaqParams.push("_trackEvent"); gaqParams = gaqParams.reverse();
    if(window._gaq) {
        if(followup) {
            _gaq.push(gaqParams).push(followup);
        } else {
            _gaq.push(gaqParams);
        }
    } else {
        try{
            if(followup) {
                window.top._gaq.push(gaqParams).push(followup);
            } else {
                window.top._gaq.push(gaqParams);
            }
            if(console) console.info('Would track: ', page);
        }catch(e){
            try{
                if(console) console.info('Failed to track: ', page);
            }catch(e){

            }
        }

    }
}

Curriki.logView = function(page){
	// Usage in site example:
	// <a onClick="javascript:Curriki.logView('/Download/attachment/${space}/${name}/${attach.filename}');"> .. </a>
	if (window.pageTracker) {
		pageTracker._trackPageview(page);
    } else if (_gaq) {
        _gaq.push(["_trackPageview", page]);
    } else {

		// Double try catch for CURRIKI-5828
		// This is needed because we can not define if we
		// are coming from an embedded search in a resource proxy.
		// So we need to try to address not the top frame if thats fails.
		try{
            if (window.top._gaq) {
                window.top._gaq.push(["_trackPageview", page]);
            } else {
                window.top.pageTrackerQueue = window.top.pageTrackerQueue || new Array();
                window.top.pageTrackerQueue.push(page);
            }
			if(console) console.info('Would track: ', page);
		}catch(e){
			try{
	 			window.pageTrackerQueue = window.pageTrackerQueue || new Array();
		        window.pageTrackerQueue.push(page);
				if(console) console.info('Would track: ', page);
			}catch(e){

			}
		}
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

	,gotCollections:false

	,json_prefix:'/xwiki/curriki/users/'
	,user_try:0
	,GetUserinfo:function(callback){
		if (!Ext.isEmpty(Curriki.global)
		    && !Ext.isEmpty(Curriki.global.username)
		    && !Ext.isEmpty(Curriki.global.fullname)) {
			this.me = {
				'username':Curriki.global.username
				,'fullname':Curriki.global.fullname
			};
			if (Curriki.settings&&Curriki.settings.localCollectionFetch){
				callback();
			} else {
				this.GetCollections(callback);
			}
		} else {
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
						if (Curriki.settings&&Curriki.settings.localCollectionFetch){
							callback();
						} else {
							this.GetCollections(callback);
						}
					}
				}
				,failure:function(response, options){
					console.error('Cannot get user information', response, options);
					alert(_('add.servertimedout.message.text'));
				}
			});
		}
	}

	,collection_try:0
	,GetCollections:function(callback){
		Ext.ns('Curriki.errors');
		Curriki.errors.fetchFailed = false;
		if (Curriki.data.user.gotCollections){
			// Already have collections
			callback();
		} else {
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
						this.gotCollections = true;
						this.collections = o;
						this.collectionChildren = this.CreateCollectionChildren();
	console.log('Collections: ', this.collectionChildren);
						if (Curriki.settings&&Curriki.settings.fetchMyCollectionsOnly){
							callback();
						} else {
							this.GetGroups(callback);
						}
					}
				}
				,failure:function(response, options){
					Curriki.errors.fetchFailed = true;
					console.error('Cannot get user\'s collection information', response, options);
					alert(_('add.servertimedout.message.text'));
					this.collections = [];
					if (Curriki.settings&&Curriki.settings.fetchMyCollectionsOnly){
						callback();
					} else {
						this.GetGroups(callback);
					}
				}
			});
		}
	}

	,group_try:0
	,GetGroups:function(callback){
		Ext.ns('Curriki.errors');
		Curriki.errors.fetchFailed = false;
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
						//console.error('Cannot get user\'s group information', response, options);
						//alert(_('add.servertimedout.message.text'));
						throw {message: "GetUserinfo: Json object not found"};
					}
				} else {
					this.group_try = 0;
					this.groups = o;
					this.groupChildren = this.CreateGroupChildren();
					callback();
				}
			}
			,failure:function(response, options){
				Curriki.errors.fetchFailed = true;
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
/* Removed for CURRIKI-4874
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
*/
				colInfo.leaf = false;
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
// TODO:  Fetch the list from /xwiki/curriki/metadata/CurrkiCode.AssetClass/fields/instructional_component  OR  Get filled in JS created by xwiki
Curriki.data.ict.list = ["activity_assignment","activity_exercise","activity_lab","activity_game","activity_worksheet","activity_problemset","activity_webquest","book_fiction","book_nonfiction","book_readings","book_textbook","curriculum_answerkey","curriculum_assessment","curriculum_course","curriculum_unit","curriculum_lp","curriculum_rubric","curriculum_scope","curriculum_standards","curriculum_studyguide","curriculum_syllabus","curriculum_tutorial","curriculum_workbook","resource_animation","resource_article","resource_diagram","resource_glossary","resource_index","resource_photograph","resource_presentation","resource_collection","resource_script","resource_speech","resource_study","resource_table","resource_template","resource_webcast","other"];
Curriki.data.ict.data = [ ];
Curriki.data.ict.list.each(function(ict){
	var sort = _('CurrikiCode.AssetClass_instructional_component_'+ict);
	if (ict === 'other') {
		sort = 'zzz';
	}
	Curriki.data.ict.data.push([
		 ict
		,_('CurrikiCode.AssetClass_instructional_component_'+ict)
		,sort
	]);
});
Curriki.data.ict.store = new Ext.data.SimpleStore({
	fields: ['id', 'ict', 'sortValue']
	,sortInfo: {field:'sortValue', direction:'ASC'}
	,data: Curriki.data.ict.data
	,id: 0
});

Curriki.data.ict.getRolloverDisplay = function(el_ict){
	var icts = el_ict||[];
	var ict = "";

	var wrap = '<div class="ict-{0}"><img class="ict-icon" src="/xwiki/skins/curriki8/extjs/resources/images/default/s.gif" /><span class="ict-title">{1}</span></div>';

	if ("undefined" !== typeof icts && "undefined" !== typeof icts[0]) {
		ict += String.format(wrap, icts[0].replace(/_.*/, ''), _('CurrikiCode.AssetClass_instructional_component_'+icts[0]));
		if ("undefined" !== typeof icts[1]) {
			ict += String.format(wrap, icts[1].replace(/_.*/, ''), _('CurrikiCode.AssetClass_instructional_component_'+icts[1]));
			if ("undefined" !== typeof icts[2]) {
				ict += "...<br />";
			}
		}
	} else {
		ict += String.format(wrap, 'none', _('global.title.popup.ict.missing'));
	}

	return ict;
};



Ext.ns('Curriki.data.el');
// TODO:  Fetch the list from /xwiki/curriki/metadata/CurrikiCode.AssetClass/fields/educational_level  OR  Get filled in JS created by xwiki
Curriki.data.el.list = [ "prek", "gr-k-2", "gr-3-5", "gr-6-8", "gr-9-10", "gr-11-12", "college_and_beyond", "professional_development", "special_education", "na" ];
Curriki.data.el.data = [ ];
Curriki.data.el.list.each(function(el){
	Curriki.data.el.data.push({
		 inputValue:el
		,boxLabel:_('CurrikiCode.AssetClass_educational_level_'+el)
	});
});

Curriki.data.el.getRolloverDisplay = function(el_array){
	var lvls = el_array||undefined;
	var lvl = "";

	if ("undefined" !== typeof lvls && "undefined" !== typeof lvls[0]) {
		lvl += Ext.util.Format.htmlEncode(_('CurrikiCode.AssetClass_educational_level_'+lvls[0]))+"<br />";
		if ("undefined" !== typeof lvls[1]) {
			lvl += Ext.util.Format.htmlEncode(_('CurrikiCode.AssetClass_educational_level_'+lvls[1]))+"<br />";
			if ("undefined" !== typeof lvls[2]) {
				lvl += "...<br />";
			}
		}
	} else {
		lvl += _('global.title.popup.none.selected');
	}

	return lvl;
};


Ext.ns('Curriki.data.rights');
// TODO:  Fetch the list from /xwiki/curriki/metadata/CurrikiCode.AssetClass/fields/rights  OR  Get filled in JS created by xwiki
Curriki.data.rights.list = [ "public", "members", "private" ];
Curriki.data.rights.initial = Curriki.data.rights.list[0];
Curriki.data.rights.data = [ ];
Curriki.data.rights.list.each(function(right){
	Curriki.data.rights.data.push({
		 inputValue:right
		,boxLabel:_('CurrikiCode.AssetClass_rights_'+right)
		,checked:Curriki.data.rights.initial == right?true:false
	});
});


Ext.ns('Curriki.data.language');
// TODO:  Fetch the list from /xwiki/curriki/metadata/CurrikiCode.AssetClass/fields/language  OR  Get filled in JS created by xwiki
Curriki.data.language.list = ["eng","ind","zho","nld","fin","fra","deu", "hin","ita","jpn","kor","nep","por","rus","sin","spa","tam","999"];
Curriki.data.language.initial = Curriki.data.language.list[0];
Curriki.data.language.data = [ ];
Curriki.data.language.list.each(function(lang){
	Curriki.data.language.data.push([
		 lang
		,_('CurrikiCode.AssetClass_language_'+lang)
	]);
});
Curriki.data.language.store = new Ext.data.SimpleStore({
	fields: ['id', 'language'],
	data: Curriki.data.language.data
});

Ext.ns('Curriki.data.category');
// TODO:  Fetch the list from /xwiki/curriki/metadata/CurrikiCode.AssetClass/fields/category  OR  Get filled in JS created by xwiki
Curriki.data.category.list = ["text","image","audio","video","interactive","archive","document","external","collection","unknown"];
Curriki.data.category.data = [ ];
Curriki.data.category.list.each(function(category){
	Curriki.data.category.data.push({
		 inputValue:category
		,boxLabel:_('CurrikiCode.AssetClass_category_'+category)
	});
});

Ext.ns('Curriki.data.licence');
// TODO:  Fetch the list from /xwiki/curriki/metadata/CurrikiCode.AssetLicenseClass/fields/licenseType  OR  Get filled in JS created by xwiki
Curriki.data.licence.list = ["Licences.CreativeCommonsAttributionNon-Commercial", "Licences.CurrikiLicense", "Licences.PublicDomain", "Licences.CreativeCommonsAttributionNoDerivatives", "Licences.CreativeCommonsAttributionNon-CommercialNoDerivatives", "Licences.CreativeCommonsAttributionSharealike", "Licences.CreativeCommonsAttributionNon-CommercialShareAlike","Licences.TeachersDomainDownloadShare" ];
Curriki.data.licence.initial = Curriki.data.licence.list[0];
Curriki.data.licence.data = [ ];
Curriki.data.licence.list.each(function(lic){
	Curriki.data.licence.data.push([
		 lic
		,_('CurrikiCode.AssetLicenseClass_licenseType_'+lic)
	]);
});
Curriki.data.licence.store = new Ext.data.SimpleStore({
	fields: ['id', 'licence'],
	data: Curriki.data.licence.data
});



Ext.ns('Curriki.data.gCCL');
Curriki.data.gCCL.list = ["0","1"];
Curriki.data.gCCL.initial = Curriki.data.licence.list[0];
Curriki.data.gCCL.data = ["0","1"];
Curriki.data.gCCL.store = new Ext.data.SimpleStore({
	fields: ['id', 'gCCL'],
	data: Curriki.data.gCCL.data
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

// TODO:  Fetch the list from /xwiki/curriki/metadata/CurrikiCode.AssetLicenseClass/fields/fw_items  OR  Get filled in JS created by xwiki
Curriki.data.fw_item.fwMap = {"TREEROOTNODE":[{"id":"FW_masterFramework.WebHome","parent":""}],"FW_masterFramework.WebHome":[{"id":"FW_masterFramework.Arts","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.VocationalEducation","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Education&Teaching","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.EducationalTechnology","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Health","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Information&MediaLiteracy","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.LanguageArts","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Mathematics","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.Science","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.SocialStudies","parent":"FW_masterFramework.WebHome"},{"id":"FW_masterFramework.ForeignLanguages","parent":"FW_masterFramework.WebHome"}],"FW_masterFramework.Information&MediaLiteracy":[{"id":"FW_masterFramework.EvaluatingSources","parent":"FW_masterFramework.Information&MediaLiteracy"},{"id":"FW_masterFramework.MediaEthics","parent":"FW_masterFramework.Information&MediaLiteracy"},{"id":"FW_masterFramework.OnlineSafety","parent":"FW_masterFramework.Information&MediaLiteracy"},{"id":"FW_masterFramework.ResearchMethods","parent":"FW_masterFramework.Information&MediaLiteracy"}],"FW_masterFramework.SocialStudies":[{"id":"FW_masterFramework.Anthropology","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Careers_5","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Civics","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.CurrentEvents","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Economics","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Entrepreneurship","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Geography","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.GlobalAwareness","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Government","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.History Local","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.PoliticalSystems","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Psychology","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Religion","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Research_0","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Sociology","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.StateHistory","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Technology_1","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.Thinking&ProblemSolving","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.UnitedStatesGovernment","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.UnitedStatesHistory","parent":"FW_masterFramework.SocialStudies"},{"id":"FW_masterFramework.WorldHistory","parent":"FW_masterFramework.SocialStudies"}],"FW_masterFramework.Arts":[{"id":"FW_masterFramework.Architecture","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Careers","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Dance","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.DramaDramatics","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Film","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.History","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Music","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Photography","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.PopularCulture","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.Technology","parent":"FW_masterFramework.Arts"},{"id":"FW_masterFramework.VisualArts","parent":"FW_masterFramework.Arts"}],"FW_masterFramework.EducationalTechnology":[{"id":"FW_masterFramework.Careers_0","parent":"FW_masterFramework.EducationalTechnology"},{"id":"FW_masterFramework.IntegratingTechnologyintotheClassroom","parent":"FW_masterFramework.EducationalTechnology"},{"id":"FW_masterFramework.UsingMultimedia&theInternet","parent":"FW_masterFramework.EducationalTechnology"}],"FW_masterFramework.VocationalEducation":[{"id":"FW_masterFramework.Agriculture_0","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.Business","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.Careers_6","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.OccupationalHomeEconomics","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.School-to-work","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.Technology_2","parent":"FW_masterFramework.VocationalEducation"},{"id":"FW_masterFramework.Trade&Industrial","parent":"FW_masterFramework.VocationalEducation"}],"FW_masterFramework.Health":[{"id":"FW_masterFramework.BodySystems&Senses","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.Careers_1","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.EnvironmentalHealth","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.HumanSexuality","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.MentalEmotionalHealth","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.Nutrition","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.PhysicalEducation","parent":"FW_masterFramework.Health"},{"id":"FW_masterFramework.SafetySmokingSubstanceAbusePrevention","parent":"FW_masterFramework.Health"}],"FW_masterFramework.Education&Teaching":[{"id":"FW_masterFramework.Accessibility","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.AdultEducation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.BilingualEducation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.ClassroomManagement","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.EarlyChildhoodEducation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.EducationAdministration","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.EducationalFoundations","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.EducationalPsychology","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.InstructionalDesign","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.MeasurementEvaluation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.Mentoring","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.MulticulturalEducation","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.StandardsAlignment","parent":"FW_masterFramework.Education&Teaching"},{"id":"FW_masterFramework.TeachingTechniques","parent":"FW_masterFramework.Education&Teaching"}],"FW_masterFramework.ForeignLanguages":[{"id":"FW_masterFramework.Alphabet","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Careers_7","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.CulturalAwareness","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Grammar","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.InformalEducation","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Linguistics","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.ListeningComprehension","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Reading","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Speaking","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.Spelling","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.VocabularyWriting","parent":"FW_masterFramework.ForeignLanguages"},{"id":"FW_masterFramework.FLWriting","parent":"FW_masterFramework.ForeignLanguages"}],"FW_masterFramework.Mathematics":[{"id":"FW_masterFramework.Algebra","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Appliedmathematics","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Arithmetic","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Calculus","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Careers_3","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.DataAnalysis&Probability","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Equations","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Estimation","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Geometry","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Graphing","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Measurement","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.NumberSense&Operations","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Patterns","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.ProblemSolving","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Statistics","parent":"FW_masterFramework.Mathematics"},{"id":"FW_masterFramework.Trigonometry","parent":"FW_masterFramework.Mathematics"}],"FW_masterFramework.Science":[{"id":"FW_masterFramework.Agriculture","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Astronomy","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Biology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Botany","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Careers_4","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Chemistry","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Earthscience","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Ecology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Engineering","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Generalscience","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Geology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.HistoryofScience","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.LifeSciences","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Meteorology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.NaturalHistory","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Oceanography","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Paleontology","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.PhysicalSciences","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Physics","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.ProcessSkills","parent":"FW_masterFramework.Science"},{"id":"FW_masterFramework.Technology_0","parent":"FW_masterFramework.Science"}],"FW_masterFramework.LanguageArts":[{"id":"FW_masterFramework.Alphabet_0","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Careers_2","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.LanguageArts_Grammar","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Journalism","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Listening&Speaking","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Literature","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Phonics","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.ReadingComprehension","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Research","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Spelling_0","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.StoryTelling","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Vocabulary","parent":"FW_masterFramework.LanguageArts"},{"id":"FW_masterFramework.Writing","parent":"FW_masterFramework.LanguageArts"}]};
var fwItem = 'FW_masterFramework.WebHome';
Curriki.data.fw_item.fwAddNode = function(fwMap, nodeName){
	var nodeInfo = {
		 id:nodeName
		,text:_('CurrikiCode.AssetClass_fw_items_'+nodeName)
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

Curriki.data.fw_item.getRolloverDisplay = function(fw_array){
	var fws = fw_array||[];
	var fw = "";
	var fwMap = Curriki.data.fw_item.fwMap;

	if (fws[0] === 'FW_masterFramework.WebHome') {
		fws.shift();
	}

	if ("undefined" !== typeof fws && "undefined" !== typeof fws[0]) {
		var fwD = "";
		var fwi = fws[0];
		var fwParent = fwMap['FW_masterFramework.WebHome'].find(function(item){
			return (fwMap[item.id].find(function(sub){
				return sub.id==fwi;
			}));
		});

		if (!Ext.type(fwParent)) {
			fwD = _('CurrikiCode.AssetClass_fw_items_'+fwi);
		} else {
			fwParent = fwParent.id;
			fwD = _('CurrikiCode.AssetClass_fw_items_'+fwParent) + " > "+_('CurrikiCode.AssetClass_fw_items_'+fwi);
		}
		fw += Ext.util.Format.htmlEncode(fwD) + "<br />";
		if ("undefined" !== typeof fws[1]) {
			var fwD = "";
			var fwi = fws[1];
			var fwParent = fwMap['FW_masterFramework.WebHome'].find(function(item){
				return (fwMap[item.id].find(function(sub){
					return sub.id==fwi;
				}));
			});

			if (!Ext.type(fwParent)) {
				fwD = _('CurrikiCode.AssetClass_fw_items_'+fwi);
			} else {
				fwParent = fwParent.id;
				fwD = _('CurrikiCode.AssetClass_fw_items_'+fwParent) + " > "+_('CurrikiCode.AssetClass_fw_items_'+fwi);
			}
			fw += Ext.util.Format.htmlEncode(fwD) + "<br />";
			if ("undefined" !== typeof fws[2]) {
				fw += "...<br />";
			}
		}
	} else {
		fw += _('global.title.popup.none.selected')+'<br />';
	}

	return fw;
};


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
			 text:_('CurrikiCode.AssetClass_fw_items_FW_masterFramework.WebHome')
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
	,CopyAsset:function(copyOf, publishSpace, callback){
		Ext.Ajax.request({
			 url: this.json_prefix
			,method:'POST'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData: {
				'copyOf':copyOf||''
				,'publishSpace':publishSpace||''
			}
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object with
				//   assetPage, assetType, and fullAssetType items
				var o = json.evalJSON(true);
				if(!o || !o.assetPage) {
					console.warn('Cannot copy resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot copy resource', response, options);
				alert(_('add.servertimedout.message.text'));
			}
		});
	}
	,GetAssetInfo:function(assetPage, callback){
		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage
			,method:'GET'
			,disableCaching:true
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
			,disableCaching:true
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
					if ('string' === typeof o.rightsList) {
						o.rightsList = o.rightsList.evalJSON(true);
					}
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
	,SetSubassets:function(assetPage, revision, wanted, logMsg, callback){
		var jsData = {wanted:wanted};
		if (!Ext.isEmpty(revision)) {
			jsData.previousRevision = revision;
		} else {
			jsData.ignorePreviousRevision = true;
		}
		if (!Ext.isEmpty(logMsg)) {
			jsData.logMessage = logMsg;
		}

		Ext.Ajax.request({
			 url: this.json_prefix+'/'+assetPage+'/subassets'
			,method:'PUT'
			,headers: {
				'Accept':'application/json'
				,'Content-type':'application/json'
			}
			,jsonData:jsData
			,scope:this
			,success:function(response, options){
				var json = response.responseText;
				// Should return an object for the current asset
				var o = json.evalJSON(true);
				if(!o) {
					console.warn('Cannot save subassets', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					callback(o);
				}
			}
			,failure:function(response, options){
				console.error('Cannot save subassets', response, options);
				if (response.status == 412){
					if (response.responseText.search(/ 107 [^ ]+ 101:/) !== -1){
						alert(_('error: Collision while saving -- only some changes saved'));
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
	,Flag : function(assetPage, reason, altReason, callback) {
		Ext.Ajax.request({
			url: '/xwiki/bin/view/FileCheck/Flag?xpage=plain&page='+(assetPage||'')+'&reason='+(reason||'')+'&altreason='+(altReason||'')+'&_dc='+(new Date().getTime())
			,method: 'POST'
			,headers: {
				'Accept' : 'application/json',
				'Content-type' : 'application/json'
			}
			,params: {
				page: assetPage||''
				,reason: reason||''
				,altreason: altReason||''
			}
			,scope: this
			,success: function(response, options) {
				var json = response.responseText;
				var o = {};
				try {
					o = json.evalJSON(true);
				} catch (e) {
					o = null;
				}
				if (!o) {
					console.warn('Could not flag resource', response.responseText, options);
					alert(_('add.servertimedout.message.text'));
				} else {
					if (o.success) {
						callback(o);
					} else {
						console.warn('Could not flag resource', response.responseText, options);
						alert('Flagging Failed');
					}
				}
			}
			,failure: function(response, options) {
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
	,maxHeight:6000
	,autoScroll:false
	,constrain:true
	,collapsible:false
	,closable:false
	,resizable:false
    , monitorResize: true
	,shadow:false
	,defaults:{border:false}
	,listeners:{
		afterlayout:function(wnd, layout){
            console.log("afterlayout 2 on " + wnd);
			if (this.afterlayout_maxheight) {
				// Don't collapse again
			} else {
				if (wnd.getBox().height > wnd.maxHeight){
					wnd.setHeight(wnd.maxHeight);
					wnd.center();
					this.afterlayout_maxheight = true;
                    console.log("afterlayout_maxheight reached: " + wnd.maxHeight);
				} else {
					wnd.setHeight('auto');
                    console.log("set auto height");
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
                childInfo.onclick="return false;"
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
// This script is the set of pages used to command the UI for the login and registration steps
Ext.ns('Curriki.ui.login');

Curriki.ui.login.displayLoginDialog = function(url) {
    if(Curriki.ui.login.loginDialog && window.opener.top.Curriki.ui.login.loginDialog.isVisible())
        Curriki.ui.login.loginDialog.hide();
    var w = 630, h=400;
    //if(window.innerHeight && window.innerHeight <h) h = Math.round(window.innerHeight*0.9);
    if(window.innerWidth && window.innerWidth<w)   w = Math.round(window.innerWidth*0.95);
    if(url.indexOf('?')>=0) url = url+"&framed=true"; else url=url+"?framed=true";
    // the default header should be blue, not green as it is in AddPath, adjust the CSS live
    var rule = ".x-window .x-window-tl, .x-panel-ghost .x-window-tl";
    if(Ext && Ext.isIE) rule=".x-window .x-window-tl";
    Ext.util.CSS.updateRule(rule,
        "background-color", "#4E83C7");
    Curriki.ui.login.loginDialog = new Ext.Window({
        title:_("join.login.title"),
        border:false,
        id: 'loginDialogWindow',
        scrollbars: false
        ,modal:true
        ,width: w
        //, height: h
        ,minWidth:400
        ,minHeight:100
        ,maxHeight:575
        ,autoScroll:false
        ,constrain:true
        ,collapsible:false
        ,closable:false
        ,resizable:false
        , monitorResize: true
        ,shadow:false
        ,defaults:{border:false},
         html: "<iframe style='border:none' frameBorder='0' name='curriki-login-dialog' id='loginIframe' src='"+url+"' width='"+(w-5)+"' height='"+(h-31)+"'/>" //
            });
    Curriki.ui.login.loginDialog.headerCls = "registration-dialog-header";
    Curriki.ui.login.loginDialog.show();
    return Ext.get("loginIframe").dom.contentWindow; 
};

Curriki.ui.login.readScrollPos = function(w) {
    if(typeof(w)=="undefined") w=window;
    try {
        if (w && w.Ext) {
            var s = w.Ext.getBody().getScroll();
            return escape("&l=" + s.left + "&t=" + s.top);
        } else
            return "";
    } catch(e) { Curriki.console.log(e); return "";}
};

Curriki.ui.login.restoreScrollPos = function(url) {
    try {
        Curriki.console.log("Intending to restoreScroll.");
        if(!url.match(/t=[0-9]/)) {
            Curriki.console.log("No coordinates passed.");
            return;
        }
        var l = url.replace(/.*l=([0-9]+).*/, "$1");
        var t = url.replace(/.*t=([0-9]+).*/, "$1");
        if (typeof(l) == "undefined") {
            l = 0;
        }
        if (typeof(t) == "undefined") {
            t = 0;
        }
        Curriki.console.log("Would scroll to " + l + ":" + t + " if I were IE.");
        if (Ext.isIE) {
            Curriki.console.log("Scrolling by "+l + ":" + t);
            window.scrollBy(l, t);
        }
    } catch(e) { Curriki.console.log(e); }
};

Curriki.ui.login.ensureProperBodyCssClass = function() {
    window.onload = function() {
        try {
            if (document.body) {
                var x = document.body.className;
                if (x) {
                    document.body.className = x + " insideIframe";
                } else if(!Ext.isIE()) {
                    document.body.className = "insideIframe";
                }

            }
        } catch(e) { Curriki.console.log(e); }
    };
}


Curriki.ui.login.popupPopupAndIdentityAuthorization = function(provider, requestURL, xredirect) {
    try { 
        Curriki.console.log("Opening pop-up that will request authorization.");
        window.top.name = "currikiMainWindow";
        if(!Ext.isIE) Curriki.ui.login.popupIdentityAuthorization2(requestURL, null);
        var dialog = Curriki.ui.login.displayLoginDialog("/xwiki/bin/view/Registration/RequestAuthorization?xpage=popup&provider=" + provider + "&to=" + encodeURIComponent(requestURL) + '&xredirect=' + encodeURIComponent(xredirect))
        if(Ext.isIE) Curriki.ui.login.popupIdentityAuthorization2(requestURL, null);
        window.Curriki.ui.login.windowThatShouldNextGoTo = dialog;
    } catch(e) { Curriki.console.log(e); }
}
Curriki.ui.login.popupIdentityAuthorization = function(requestURL) {
    return Curriki.ui.login.popupIdentityAuthorization2(requestURL, null);
}
Curriki.ui.login.popupIdentityAuthorization2 = function(requestURL, windowThatShouldNextGoTo) {
    return Curriki.ui.login.popupAuthorization4(requestURL, windowThatShouldNextGoTo, 'curriki-login-dialog', 'curriki_login_authorize');
}

Curriki.ui.login.popupGCheckout = function(requestURL, nextURLhere) {
    if(!Ext.isIE)  Curriki.ui.login.popupAuthorization4(requestURL, window, "curriki-login-dialog", "checkoutWindow");
    if(nextURLhere && nextURLhere.startsWith("close-now-")) window.top.location.href=nextURLhere.substring(10);
        else if(nextURLhere) window.location.href = nextURLhere;
    if(Ext.isIE)  Curriki.ui.login.popupAuthorization4(requestURL, window, "curriki-login-dialog", "checkoutWindow");
    window.top.name="currikiMainWindow";
}

Curriki.ui.login.popupAuthorization4 = function(requestURL, windowThatShouldNextGoTo, dialogName, popupName) {
    // called from the login-or-register dialog or from the in-header-icons
    Curriki.console.log("Opening authorization to " + requestURL);
    if(window!=window.top) window.name='curriki-login-dialog';
    if(dialogName) window.name = dialogName;
    if(popupName) {} else { popupName = 'curriki_login_authorize'; }
    var otherWindow;
    if(window.frames[popupName]) {
        Curriki.console.log("Re-using window.");
        otherWindow = window.frames[popupName];
        otherWindow.location.href= requestURL;
    } else {
        Curriki.console.log("Creating window.");
        var x = Math.max(0,(screen.width-980)/2);
        var y = Math.max(0,(screen.height-600)/2);
        otherWindow = window.open(requestURL, popupName, "toolbar=no,scrollbars=yes,status=yes,menubar=no,resizable=yes,width=980,height=600,left="+x+",top="+y);

        if(!otherWindow || typeof (otherWindow) == undefined){
            if(popupName == "checkoutWindow"){
                if(window.localtion.indexOf())
                window.location = "http://welcome.curriki.org/about-curriki/donate/";
            }else
            if(popupName == "curriki_login_authorize"){
                window.location.pathname = "/xwiki/bin/view/Registration/ManualLogin";
            }
        }

    }
    window.focusIt = window.setInterval(function() { window.clearInterval(window.focusIt); otherWindow.focus(); }, 100)
    window.Curriki.ui.login.authorizeDialog = otherWindow;
    window.top.Curriki.ui.login.authorizeDialog = otherWindow;
    if(windowThatShouldNextGoTo && windowThatShouldNextGoTo != null) window.Curriki.ui.login.windowThatShouldNextGoTo = windowThatShouldNextGoTo;
    return false;
};

 Curriki.ui.login.finishAuthorizationPopup = function(targetURL, openerWindow, openedWindow, toTop) {
    Curriki.console.log("Finishing popup, (toTop? "+ toTop+ ") target: " + targetURL);
     if(typeof(openerWindow)=="undefined" || openerWindow==window) {
         openerWindow = window.open(targetURL, "currikiMainWindow");
     }
    if(openerWindow
        //&& (openerWindow.Curriki.ui.login.authorizeDialog && openerWindow.Curriki.ui.login.authorizeDialog==window
        //    || (openerWindow.top.Curriki.ui.login.authorizeDialog && openerWindow.top.Curriki.ui.login.authorizeDialog==window))
        ) {
        // we are in a popup relationship, can close and revert to that popup
        Curriki.console.log("We are in popup, closing and opening popup.");
        var targetWindow = openerWindow;
        if(openerWindow.Curriki.ui.login.windowThatShouldNextGoTo)
            targetWindow = openerWindow.Curriki.ui.login.windowThatShouldNextGoTo;
        Curriki.console.log("targetWindow: " + targetWindow + " with force to top " + toTop);
        if(toTop) targetWindow = targetWindow.top;
        else if(openerWindow.Ext && openerWindow.Ext.get('loginIframe'))
            targetWindow = openerWindow.Ext.get('loginIframe').dom.contentWindow;
        if(targetWindow && targetWindow.location) {
            targetWindow.location.href = targetURL;
            //alert("Would go to " + targetURL + " from " + targetWindow);
            // schedule a close
            openedWindow.setInterval(function() {
                try {
                    targetWindow.focus();
                } catch(e) { Curriki.console.log(e); }
                try {
                    openedWindow.close();
                } catch(e) { Curriki.console.log(e); }
            },20);
        } else {
            var w = window;
            if(toTop) w = w.top;
            w.location.href = targetURL;
        }
        return false;
    } else {
        Curriki.console.log("No popup parent found... ah well.");
        var w = openedWindow;
        if(toTop) w = w.top;
        w.location.href = targetURL;
        //alert("Would go to " + targetURL + " from " + openedWindow);
    }
}




Curriki.ui.login.makeSureWeAreFramed = function(framedContentURL) {
    if(window==window.top) {
        if(!framedContentURL || framedContentURL==null) framedContentURL = window.location.href;
        Curriki.ui.login.displayLoginDialog(framedContentURL);
    } else if (window.name != 'curriki-login-dialog' && framedContentURL && framedContentURL!=null) {
        Curriki.console.log("Redirecting to " + framedContentURL);
        var t= window.opener;
        if(typeof(t)!="object") t=window.top;
        t.replace(framedContentURL);
        window.setInterval("window.close();", 50);
        return;
    }

};

Curriki.ui.login.showLoginLoading=function(msg, multi) {
    try {
        if(navigator.appVersion.indexOf(" Chrome")>0) {
            // we need this here because a failure would only leave grey borders around while
            // a failure with the other system leaves a whole glasspane on top
            // that failure happens in Chrome where LoginSuccessful is displayed from https
            Curriki.showLoading(msg, true);
            if (window.parent && window.parent.Ext && window.parent.Ext.get('loginIframe')) { // also make the surroundings grey
                var d = window.parent.Ext.get('loginIframe');
                Curriki.console.log("will set bg on " + d);
                while (typeof(d) != "undefined" && d != null && d.setStyle) {
                    if (d.id && "loginDialogWindow" == d.id) break;
                    Curriki.console.log("setting bg on " + d);
                    d.setStyle("background-color", "#DDD");
                    d = d.parent();
                }
            }
        } else {
            if (window.parent && window.parent.Ext && window.parent.Ext.get('loginIframe'))
                window.parent.Curriki.showLoading(msg, true);
            else
                Curriki.showLoading(msg, multi);
        }
    } catch(e) { Curriki.console.log(e); }
};
Curriki.ui.login.hideLoginLoading=function() {
    try {
        if(navigator.appVersion.indexOf(" Chrome")>0) {
            // see remark above
            Curriki.hideLoading(true);
            if (window.parent && window.parent.Ext && window.parent.Ext.get('loginIframe')) { // no more make the surroundings grey
                var d = window.parent.Ext.get('loginIframe');
                while (typeof(d) != "undefined" && d != null && d.setStyle) {
                    if (d.id && "loginDialogWindow" == d.id) break;
                    d.setStyle("background-color", "white");
                    d = d.parent();
                }
            }
        } else {
            if (window.parent && window.parent.Ext && window.parent.Ext.get('loginIframe'))
                window.parent.Curriki.hideLoading(true);
            else
                Curriki.hideLoading(true);
        }
    } catch(e) { Curriki.console.log(e); }
}






Ext.namespace("Curriki.ui.login.liveValidation");
Curriki.ui.login.liveValidation = function() {
    var queue = new Array();

    return {
        queue: queue,


        launchCheckFieldRequest: function(value, field, queueEntry) {
            Curriki.ui.login.liveValidation.notifyValidationResult(field, "waiting");
            Curriki.Ajax.beforerequest = function() {};
            var r;
            console.log("launching check field request " + field + " of name " + field.dom.name);
            if(field.dom && field.dom.name && field.dom.name=="postalCode") {
                r = Ext.Ajax.request({
                    url: "/locations"
                    ,method:'GET'
                    ,headers: { 'Accept':'application/json' ,'Content-type':'application/json' }
                    ,params: { 'q':"postalCode:" + field.dom.value,
                        "fl": "cityName,stateCode,long,lati", rows:1}
                    ,scope:this
                    ,success:function(response, options){
                        var json = response.responseText;
                        var results = json.evalJSON(true);
                        if(console) console.log("Results: ",results);
                        window.results = results;

                        var docs = results.response.docs;
                        if(!docs || docs.length==0 || !(docs[0].cityName && docs[0].stateCode)) {
                            if(console) console.log("Docs returned unusable.",docs);
                            Ext.get("postalCode_results").dom.innerHTML = "-";
                            Curriki.ui.login.liveValidation.notifyValidationResult(field, false);
                        } else {
                            var d = docs[0];
                            if(console) console.log(d.cityName + " " + d.stateCode, d);
                            Curriki.ui.login.liveValidation.updatePostalCodeResult(d.cityName, d.stateCode, d['long'], d['lati']);
                            Curriki.ui.login.liveValidation.notifyValidationResult(field, true);
                        }
                    }
                    ,failure:function(response, options){
                        console.error('Cannot resolve location', response, options);
                    }
                });
            } else {
                r = Ext.Ajax.request({
                    url: "/xwiki/bin/view/Registration/CheckValid"
                    ,headers: {'Accept':'application/json'}
                    ,method: "GET"
                    ,failure:function(response, options) {
                        Curriki.ui.login.liveValidation.queriedValue=queueEntry.value;
                        Curriki.ui.login.liveValidation.notifyValidationResult(field, null);
                        Curriki.console.log("failed validation: ", response, options);
                    }
                    ,success:function(response, options){
                        var t = response.responseText;
                        if(t) t = t.trim();
                        Curriki.console.log("Response: " + t);
                        queue.remove(queueEntry);
                        if(queueEntry.value!=field.getValue()) return;
                        Curriki.ui.login.liveValidation.notifyValidationResult(field, "true" == t);
                    }
                    , params: { what: field.dom.name,
                        value: value,
                        xpage: "plain"
                    }
                    , scope: this
                });
            }
            return r;
        },

        updatePostalCodeResult: function(cityName, stateCode, longitude, latitude) {
            if(cityName) {} else {cityName="";}
            if(stateCode) {} else {stateCode = "";}
            if(longitude) {} else {longitude="";}
            if(latitude) {} else {latitude = "";}
            var label = "-";
            if(cityName && stateCode) label = cityName + ", " + stateCode
            Ext.get("postalCode_results").dom.innerHTML = label;
            Ext.get("city_input").dom.value= cityName
            Ext.get("state_input").dom.value= stateCode;
            Ext.get("longitude_input").dom.value = longitude;
            Ext.get("latitude_input").dom.value = latitude;
        },

        notifyValidationResult:function(field, res) {
            /*
             Ext.get("loginIframe").dom.contentWindow.Ext.get("username_input").parent().addClass("warningField")
             */
            Curriki.console.log("Notifying validation result " + res + " on field " + field);
            try {
                if (field) {
                } else {
                    Curriki.console.log("Warning: missing field.");
                    return;
                }
                window.lastField = field;
                var pElt = field.parent();
                if (null == res) {
                    pElt.removeClass("okField");
                    pElt.removeClass("waiting");
                    pElt.removeClass("warningField");
                    if(field.dom && field.dom.name && "postalCode"==field.dom.name) {
                        Curriki.ui.login.liveValidation.updatePostalCodeResult(null, null, null, null);
                    }
                } else if("waiting" == res) {
                    pElt.addClass("waiting");
                } else if (true == res || "true" == res) {
                    pElt.removeClass("waiting");
                    pElt.removeClass("warningField");
                    pElt.addClass("okField");
                } else if (false == res || "false" == res) {
                    pElt.removeClass("waiting");
                    pElt.removeClass("okField");
                    pElt.addClass("warningField");
                }
            } catch(e) {
                Curriki.console.log("Error: ", e)
            }
        },




        activate:function(ids) {
            // disable flashy XHR witness
            Ext.Ajax.purgeListeners();

            Ext.each(ids, function(name) {
                Curriki.console.log("Registering on " + name);
                var x = Ext.get(name);
                if(x) {} else {
                    Curriki.console.log("Not found: " + name);
                    return;
                }
                if(x.purgeListeners) x.purgeListeners();
                x.addListener("blur", function(evt) {
                    Curriki.console.log("Focus-out...");
                    Curriki.ui.login.liveValidation.queueQueryNow(x);
                    Curriki.ui.login.liveValidation.stopPolling();
                });
                x.addListener("focus", function(evt) {
                    Curriki.console.log("Focus-in...");
                    var handle=window.setInterval(function() {
                        clearInterval(handle);
                        Curriki.ui.login.liveValidation.startPollingTextField(x);
                    }, 50);
                });
            });
        }
        , queueQueryNow: function(inputElt) {
            // this is the main function to call the validation
            var fieldName = inputElt.dom.name;
            var fieldValue = inputElt.dom.value;
            Curriki.console.log("Validation on field " + fieldName + " with value '" + fieldValue + "'.");
            //var min_length=3;
            //if(fieldName=="firsName" || fieldName=="lastName" || fieldName=="agree" || fieldName=="member_type")
            //    min_length=1;
            //if(typeof(fieldValue)!="undefined" && fieldValue.length<=min_length) return;
            if(fieldName!="email" && fieldName!="username" && fieldName!="postalCode") {
                var passed = false;
                var silentFailure = fieldName=="firstName" || fieldName=="lastName" || fieldName=="password";
                if(fieldName=="agree") passed = fieldValue!="0";
                if(fieldName=="member_type") passed = fieldValue!="-";
                if(fieldName=="firstName" || fieldName=="lastName") passed = fieldValue.length>=1;
                if(fieldName=="password") passed = fieldValue.length>5 && !(fieldValue.indexOf(" ")>-1);
                Curriki.console.log("passed? " + passed + ".");
                // manual check here, just long enough
                if(passed==false) {
                    if(silentFailure) {
                        // silentFailure == true && passed == false (no need to bother folks for too short names: cler mark)
                        Curriki.ui.login.liveValidation.notifyValidationResult(inputElt, null);
                    } else {
                        Curriki.ui.login.liveValidation.notifyValidationResult(inputElt, false);
                    }
                } if(passed==true)
                    Curriki.ui.login.liveValidation.notifyValidationResult(inputElt, true);
                return;
            }
            //
            // we're left with email and username, only check if longer than 3
            var queueEntry = new Object();
            queueEntry.value = inputElt.getValue();
            Curriki.ui.login.liveValidation.queriedValue = inputElt.getValue();
            Curriki.console.log("Queuing query for " + queueEntry.value);
            if(typeof(queueEntry.value)=="undefined" || queueEntry.value==null) {
                Curriki.console.log("Undefined value, stop.");
                return;
            }
            if(typeof(queueEntry.value)!="undefined" && queueEntry.value.length<2) {
                Curriki.ui.login.liveValidation.notifyValidationResult(inputElt, null);
                return;
            }
            // something to check on the server
            // scan the queue if there's a query with same value, bring it to front
            for(x in queue) {
                if(x.value == queueEntry.value) {
                    var i = queue.indexOf(x);
                    if(i>0) for(j=i-1; j>=0; j--) {
                        queue[j+1] = queue[j];
                    }
                    Curriki.console.log("Swapping existing queue entries.");
                    queue[0] = x;
                    return;
                }
            }
            // otherwise launch request
            Curriki.console.log("Launching in queue.");
            queueEntry.request = this.launchCheckFieldRequest(queueEntry.value, inputElt, queueEntry);
            // add to queue
            queue[queue.length] = queueEntry;
            // cancel any other? not now
        }

        , intervalPointer: null
        , startedPollingTime: null
        , inputFieldBeingPolled: null
        , queriedValue: null
        , lastValue: null

        , startPollingTextField: function(inputField) {
            var t = Curriki.ui.login.liveValidation;
            if(inputField) {} else {return;}
            if(t.intervalPointer && t.intervalPointer!=null)
                t.stopPolling();
            Curriki.console.log("Start polling on " + t);
            t.inputFieldBeingPolled = inputField;
            t.startedPollingTime = new Date().getTime();
            var interval = 50;
            if(Ext.isIE) interval = 300;
            t.intervalPointer = window.setInterval(t.inputFieldPoll, interval);
        }
        , stopPolling: function() {
            Curriki.console.log("Stop polling.");
            try {
                var t = Curriki.ui.login.liveValidation;
                if (t.intervalPointer && t.intervalPointer != null)
                    window.clearInterval(t.intervalPointer);
                t.startedPollingTime = null;
                t.inputFieldBeingPolled = null;
            } catch(e) { Curriki.console.log(e); }
        }
        , inputFieldPoll: function() {
            //console.log("poll4");
            var t = Curriki.ui.login.liveValidation;
            var input = t.inputFieldBeingPolled;
            //console.log("Checking input " + input + ".");
            if(input) {} else {return;}
            var now = new Date().getTime();
            if(t.startedPollingTime && t.startedPollingTime==null)
                t.startedPollingTime = now;
            /* if(now - t.startedPollingTime > 30000) {
                t.stopPolling(); return;
            }*/
            var value = input.dom.value;
            // Evaluating value=asdasd@i2go.e wrt t.lastValue=asdasd@i2go.e and t.lastChanged=1314641854765 with now 1314641858380
            //console.log("Checking " + value + " of type " + typeof(value));
            if(typeof(value)!="undefined") {
                if(typeof(t.lastValue)!="undefined") {
                    if(! (value==t.lastValue)) {
                        //console.log("not same value.");
                        t.lastChanged = now;
                        t.lastValue = value;
                    } else { // same value: act if nothing happened since 200ms
                        //console.log("same value since " + (now - t.lastChanged));
                        if(t.lastChanged && now-t.lastChanged>200 && (t.lastChanged > t.lastChecked || t.lastChecked===undefined) &&
                                (typeof(t.queriedValue)=="undefined" || t.queriedValue!=value)) {
                            t.lastChecked = now;
                            t.queueQueryNow(input);
                        }
                    }
                } else {
                    t.lastValue = value;
                    t.lastChanged = now;
                }
            } else Curriki.console.log("Giving up value undefined.");

        }


    };
}();

// vim: ts=4:sw=4

/*
 * Based on Ext.ux.form.Rater (http://extjs.com/forum/showthread.php?t=10822)
 */

/*global Ext */
/*global Curriki */
/*global _ */

(function(){
Ext.ns('Curriki.ui.Rating');
var rating_list = [];

for (var i=0; i<5; i++){
	rating_list.push(_('CurrikiCode.AssetClass_member_rating_'+(i+1)));
}

Curriki.ui.Rating = Ext.extend(Ext.form.NumberField, {
	fieldClass : 'x-form-field x-form-rating-field',

	allowDecimals : false,

	allowNegative : false,

	minValue : 0,

	maxValue : 5,

	// For Rating
	unit : 17,
	wrapClass : 'ux-form-rater-wrap',
	starsClass : 'ux-form-rater-stars',
	hoverClass : 'ux-form-rater-hover',
	voteClass : 'ux-form-rater-vote',
	votedClass : 'ux-form-rater-voted',
	textRightClass : 'ux-form-rater-text-right',
	hoverText: rating_list,

	// private config
	displayValue : undefined,
	ratedValue : undefined,
	hoverValue : undefined,

	rated : false,



	// Methods

	initComponent : function(){
		Curriki.ui.Rating.superclass.initComponent.call(this);
		this.addEvents(
			'beforerating',
			'rate'
		);
	},

	// private
	onRender : function(ct, position){
		Curriki.ui.Rating.superclass.onRender.apply(this, arguments);

		this.wrap = this.el.wrap({cls: this.wrapClass});
		if(Ext.isIE) this.wrap.setHeight(this.unit);

		// TODO: Needed?  fix for ie using in dynamic form
		this.el.addClass('x-hidden');

		this.createStars();
		this.createTextContainers();

		this.displayValue = (this.displayValue > this.maxValue) ? this.maxValue : this.displayValue;

		if (this.displayValue > 0 || this.getValue() > 0){
			this.displayRating();
		}
	},

	// private
	initEvents: function(){
		Curriki.ui.Rating.superclass.initEvents.call(this);

		var ct = this.getStarsContainer();
		var stars = this.getStars();

		stars.on('mouseover', this.displayHover, this);
		stars.on('mouseout', this.removeHover, this);
		stars.on('click', this.rate, this);
		stars.on('mouseup', this.rate, this);
	},

	// private
	displayHover: function(e){
		if (this.disabled) return;

		var target = Ext.get(e.getTarget());
		target.addClass(this.hoverClass);

		var rating = this.getRating();
		rating.hide();

		var stars = this.getStars();
		var i = 0;
		while (stars.item(i) != null){
			if (stars.item(i) == target) {
				this.hoverValue = this.maxValue - i;
				if (this.hoverText instanceof Array){
					if (!Ext.isEmpty(this.hoverText[this.hoverValue-1])) {
						this.setRightText(this.hoverText[this.hoverValue-1]);
					}
				}
				return;
			}
			i++;
		}
	},

	// private
	removeHover: function(e){
		if (this.disabled) return;

		var rating = this.getRating();
		rating.show();

		var el = e.getTarget();
		Ext.fly(el).removeClass(this.hoverClass);

		this.setRightText('');
	},

	// private
	rate: function(e){
		if (this.disabled) return;

		var hv = this.hoverValue;
		this.setValue(hv);
		if (this.fireEvent('beforerating', this) === false){
			return;
		}

		this.removeHover(e);
		this.onBlur();
		this.rated = true;
		this.displayRating(hv);

		this.fireEvent('rate', this, hv);
	},

	// private
	createStars: function() {
		if (this.getStars().getCount() > 0){
			return;
		}

		var ul = this.wrap.createChild({tag:'ul', cls:this.starsClass}).setSize(this.unit*this.maxValue, this.unit);

		// append to rating container
		var tplr = new Ext.Template('<li class="rating"></li>');
		var tpls = new Ext.Template('<li class="star"></li>');

		tplr.append(ul, [], true).setHeight(this.unit);

		for (var i=this.maxValue; i>0; i--){
			var star = tpls.append(ul, [], true);
			star.setSize(this.unit*i, this.unit);
		}

		this.alignStars();
	},

	// private
	createTextContainers: function() {
		var ct = this.getStarsContainer();

		if (!this.textRightContainer) {
			this.textRightContainer = Ext.DomHelper.insertAfter(ct, {tag:"span", cls:this.textRightClass}, true);
			this.textRightContainer.addClass('x-hidden');
		}
	},

	setRightText: function(t){
		this.textRightContainer.dom.innerHTML = t;
		if (t == null || t == '') {
			this.textRightContainer.addClass('x-hidden');
		} else {
			this.textRightContainer.removeClass('x-hidden');
		}
	},

	getRightText: function(){
		return this.textRightContainer.dom.innerHTML;
	},

	displayRating: function(v, finalRating) {
		var el = this.getRating();

		if (Ext.isEmpty(v)) {
			v = (this.displayValue == null)?this.getValue():this.displayValue;
			v = Ext.isEmpty(v)?0:v;
		}

		if (this.ratedValue > 0){
			v = this.ratedValue;
			this.rated = true;
		}

		var replaceClass = function(vtd, vt){
			if(finalRating == true){
				el.replaceClass(vt, vtd);
			} else {
				el.replaceClass(vtd, vt);
			}
		}

		replaceClass(this.votedClass, this.voteClass);
		el.setWidth(v*this.unit);
		return;
	},

	// private
	getStars: function() {
		return this.wrap.select('li.star', true);
	},

	// private
	getStarsContainer: function() {
		return this.wrap.select('.'+this.starsClass, true).item(0);
	},

	// private
	getRating: function(){
		return this.wrap.select('li.rating', true);
	},

	// private
	alignStars: function() {
		var ct = this.getStarsContainer();
		var rating = this.getRating();
		var stars = this.getStars();

		var isInForm = (ct.findParent('.x-form-item', 5))?true:false;

		if (false && !isInForm){
			var leftOffset = Ext.fly(document.body).getAlignToXY(ct)[0];
			rating.setLeft(leftOffset);
			stars.setLeft(leftOffset);
		} else {
			rating.alignTo(ct, 'tl');
			stars.alignTo(ct, 'tl');
		}
	},

	// private
	onDisable : function(){
		Curriki.ui.Rating.superclass.onDisable.call(this);
		this.wrap.addClass('x-item-disabled');
	},

	// private
	onEnable : function(){
		Curriki.ui.Rating.superclass.onEnable.call(this);
		this.wrap.removeClass('x-item-disabled');
	},
	
	// private
	onHide : function(){
		this.wrap.addClass('x-hidden');
	},

	// private
	onShow : function(){
		this.wrap.removeClass('x-hidden');
	}
});
Ext.reg('rating', Curriki.ui.Rating);
})();
// needs two configurations:
// -- window.jwplayer.key= "---"; (obtained when you download jwplayer)
// -- window.videoPrefixToDownload = "http://media.dev.curriki.org/---"; (the video hosting server, including jwplayer code and videos)


function videoInsert(videoId, title, rsrcName) {
    // insert script
    var sizeScript = document.createElement('script'); sizeScript.type = 'text/javascript';
    sizeScript.src = window.videoPrefixToDownload + videoId + "-sizes.js";
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(sizeScript, s);

    if(typeof(window.videoTitles)!="object") window.videoTitles = new Object();
    if(typeof(window.videoFullNames)!="object") window.videoFullNames= new Object();
    window.videoTitles[videoId] = title;
    window.videoFullNames[videoId] = rsrcName;
    window.setTimeout("videoWatchSizesArrived('"+videoId+"');", 50)
}


function videoWatchSizesArrived(videoId) {
    var candidateValue = window["video_" + videoId + "_sizes"];
    window.numWatches = window.numWatchers || new Object();
    if(typeof(window.numWatches[videoId])!="number") window.numWatches[videoId] = 0;
    if(candidateValue) {
        videoNotifyVideoSizeArrived(videoId, candidateValue);
    } else {
        if(window.numWatches[videoId]<500) {
            var timeout = 50;
            window.numWatches[videoId] = window.numWatches[videoId] + 1;
            if(window.numWatches[videoId]>200) timeout = timeout*5;
            window.setTimeout("videoWatchSizesArrived('"+videoId+"');", timeout);
        }
    }
}

function videoNotifyVideoSizeArrived(videoId, sources) {
    var im = Ext.get("video_img_" + videoId+"_image");
    if(typeof(sources)=="string") {
        if(console) console.log("size is still a string, display it: " + sources);
        if(im) {
            im=im.parent();
            im.setSize(320, 80);
            var m = _(sources);
            var mailTo = _('video.errors.reportErrorsToEmail');
            mailTo = "mailto:" + _('video.errors.reportErrorsToEmail') + '?subject=' + encodeURI(_(m)) + '&body=' + encodeURI(_(sources + ".details", [mailTo, videoId]));
            if(sources.startsWith("video.errors.") || sources.startsWith("video.processingMessages"));
                m = m + "</p><p style='font-size:small'>" + _(sources + ".details", [mailTo, videoId]);
            im.update("<div width='320' height='240'><p>"+m+"</p></div>")
        }
    } else if (typeof(sources)=="object") {
        if(im) {
            im.setSize(sources[0].width, sources[0].height);
            im.dom.setAttribute("src",   window.videoPrefixToDownload +  sources[0].image);
        }
        for(var i=0; i<sources.length; i++) {
            var s = sources[i];
            s.file = window.videoPrefixToDownload + s.file;
        }
        var rsrcName = window.videoFullNames[videoId];
        var sharingURL = "http://"+ location.host + "/xwiki/bin/view/" + rsrcName.replace('.','/')  +"?viewer=embed";
        var sharingCode = "<iframe width='558' height='490' \n src='"+sharingURL+"'></iframe>";

        jwplayer("video_div_" + videoId).setup({
            playlist: [{
                image: window.videoPrefixToDownload + sources[0].image,
                sources: sources,
                //title: window.videoTitles[videoId],
                width: sources[0].width,
                height: sources[0].height
            }],
            ga: {},
            sharing: {
                code: encodeURI(sharingCode),
                link: sharingURL,
                title: _('video.sharing.title')
            }
        });
    }
    var origPath = window['video_' + videoId + "_originalName"];
    if(origPath) {
        Ext.get("download_original_"+videoId+"_div").setVisible(true);
        var extension = origPath.substring(origPath.lastIndexOf('.')+1);
        Ext.get("download_original_"+videoId+"_div").addClass("filetype-" + extension)
        Ext.get("video_download_link_" + videoId).dom.setAttribute("href",
            window.videoPrefixToDownload.replace('/deliver/', '/original/') + origPath + "?forceDownload=1");
        Ext.get("video_download_link_" + videoId + "_text").dom.setAttribute("href",
            window.videoPrefixToDownload.replace('/deliver/', '/original/') + origPath + "?forceDownload=1");
    }
    //jwplayer("video_div_" + videoId).onQualityChange(videoQualityChange);

}

/* Currently ignored: was used to pop up the video when the quality change is requested.
function videoQualityChange(evt) {
    var quality = evt.levels[evt.currentQuality];
    if(typeof(quality)!="object") return;
    var width= quality.width; var  height=quality.height;
    if(width>window.innerWidth || height>window.innerHeight) {
        var factor = Math.min(window.innerWidth/width, window.innerHeight/height);
        width = factor*width; height=factor*height;
    }
    var divElt = Ext.get(evt.id);
    if(quality.label=='hq') {
        // detach from tree, make floating
        divElt.setStyle("position","absolute");
        divElt.setBounds((window.innerWidth-width)/2, (window.innerHeight-height)/2, width, height, true);
        jwplayer(evt.id).resize(quality.width, quality.height);
    } else {
        // come back, make
        divElt.setBounds(0,0);
        divElt.setStyle("position","relative");
        jwplayer(evt.id).resize(quality.width, quality.height);
    }
}
*/

function videoDownloadOriginal(videoId) {
    var p = window['video_' + videoId + "_originalName"];
    location.href= window.videoPrefixToDownload.replace('/deliver/', '/original/') + p + "?forceDownload=1";
    return false;
}
