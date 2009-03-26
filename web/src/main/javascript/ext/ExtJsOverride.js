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

/* CURRIKI-3999, CURRIKI-4109
 * Safari has an issue with a grid with autoHeight: true
 *
 * Fix from http://extjs.com/forum/showthread.php?t=44623
 *
 * TODO: Remove this override after upgrading ExtJS (currently 2.2)
 */
Ext.override(Ext.grid.GridView, {
	layout : function(){
		if(!this.mainBody){
			return;         }
		var g = this.grid;
		var c = g.getGridEl();
		var csize = c.getSize(true);
		var vw = csize.width;
		if(vw < 20 || csize.height < 20){
			return;
		}
		if(g.autoHeight){
			this.scroller.dom.style.overflow = 'visible';
			this.scroller.dom.style.position = 'static';
		}else{
			this.el.setSize(csize.width, csize.height);
			var hdHeight = this.mainHd.getHeight();
			var vh = csize.height - (hdHeight);
			this.scroller.setSize(vw, vh);
			if(this.innerHd){
				this.innerHd.style.width = (vw)+'px';
			}
		}
		if(this.forceFit){
			if(this.lastViewWidth != vw){
				this.fitColumns(false, false);
				this.lastViewWidth = vw;
			}
		}else {
			this.autoExpand();
			this.syncHeaderScroll();
		}
		this.onLayout(vw, vh);
	}
});
