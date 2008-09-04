// vim: ts=4:sw=4
/*global Ext */

Ext.override(Ext.layout.FormLayout, {
    adjustWidthAnchor : function(value, comp){
        return value - (comp.isFormField  ? (comp.hideLabel ? 0 : this.labelAdjust) : 0) - comp.el.getMargins('lr');
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

/*
Ext.override(Ext.Shadow.prototype, {
	realign: function(l, t, w, h){
		if(!this.el){
		    return;
		}
		var a = this.adjusts, d = this.el.dom, s = d.style;
		if (s.visibility === "hidden" || s.display === "none"){
			return;
		}
		var iea = 0;
		s.left = (l+a.l)+"px";
		s.top = (t+a.t)+"px";
		var sw = (w+a.w), sh = (h+a.h), sws = sw +"px", shs = sh + "px";
		if(s.width != sws || s.height != shs){
		    s.width = sws;
		    s.height = shs;
		    if(!Ext.isIE){
			var cn = d.childNodes;
			var sww = Math.max(0, (sw-12))+"px";
			cn[0].childNodes[1].style.width = sww;
			cn[1].childNodes[1].style.width = sww;
			cn[2].childNodes[1].style.width = sww;
			cn[1].style.height = Math.max(0, (sh-12))+"px";
		    }
		}
    }
});
*/
