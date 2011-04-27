// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */
Ext.onReady(function(){

Curriki.DataObservable = function() {
  this.addEvents({'Curriki.data:ready': true,
                  'Curriki.data.ict:ready': true,
                  'Curriki.data.fw_item:ready': true,
                  'Curriki.data.el:ready': true,
                  'Curriki.data.rights:ready': true,
                  'Curriki.data.language:ready': true,
                  'Curriki.data.category:ready': true,
                  'Curriki.data.license:ready': true
                  });
}
Ext.extend(Curriki.DataObservable, Ext.util.Observable);
Curriki.data.EventManager = new Curriki.DataObservable();

Ext.util.Observable.capture(Curriki.data.EventManager, function(event){
  if(event == 'Curriki.data.ict:ready') {
    Curriki.data.ict.initialized = true;
  } else if (event == 'Curriki.data.fw_item:ready') {
    Curriki.data.fw_item.initialized = true;
  } else if (event == 'Curriki.data.el:ready') {
    Curriki.data.el.initialized = true;
  } else if (event == 'Curriki.data.rights:ready') {
    Curriki.data.rights.initialized = true;
  } else if (event == 'Curriki.data.language:ready') {
    Curriki.data.language.initialized = true;
  } else if (event == 'Curriki.data.category:ready') {
    Curriki.data.category.initialized = true;
  } else if (event == 'Curriki.data.license:ready') {
    Curriki.data.license.initialized = true;
  } 
  if (Curriki.data.ict.initialized && 
      Curriki.data.fw_item.initialized &&
      Curriki.data.el.initialized &&
      Curriki.data.rights.initialized &&
      Curriki.data.language.initialized &&
      Curriki.data.category.initialized &&
      Curriki.data.license.initialized) {
    Ext.util.Observable.releaseCapture(Curriki.data.EventManager);
    Curriki.data.EventManager.fireEvent('Curriki.data:ready');    
  }
});

Ext.ns('Curriki.data.ict');
Curriki.data.EventManager.addListener('Curriki.data.ict:ready', function() {
  Curriki.data.ict.data = [];
  Curriki.data.ict.list.each(function(ict) {
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
});
Ext.Ajax.request({
  url: "/xwiki/curriki/metadata/CurrikiCode.AssetClass/fields/instructional_component",
  method: 'GET',
  headers: {
    'Accept':'application/json'
  },
  success:function(response,options) {
    try {    
      Curriki.data.ict.list = Ext.util.JSON.decode(response.responseText).allowedValues;      
    } catch(e) {
      console.error('Invalid metadata information', response, options);      
    }
    Curriki.data.EventManager.fireEvent('Curriki.data.ict:ready');
  },
  failure:function(response,options) {
    console.error('Cannot get metadata information', response, options);
  }
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

Curriki.data.EventManager.addListener('Curriki.data.el:ready', function() {
  Curriki.data.el.data = [];
  Curriki.data.el.list.each(function(el){
    Curriki.data.el.data.push({
      inputValue:el
      ,boxLabel:_('CurrikiCode.AssetClass_educational_level_'+el)
    });
  });
});

Ext.Ajax.request({
  url: "/xwiki/curriki/metadata/CurrikiCode.AssetClass/fields/educational_level",
  method: 'GET',
  headers: {
    'Accept':'application/json'
  },
  success:function(response,options) {
    try {
      Curriki.data.el.list = Ext.util.JSON.decode(response.responseText).allowedValues;      
    } catch(e) {
      console.error('Invalid metadata information', response, options);
    }
    Curriki.data.EventManager.fireEvent('Curriki.data.el:ready');
  },
  failure:function(response,options) {
    console.error('Cannot get metadata information', response, options);
  }
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
Curriki.data.EventManager.addListener('Curriki.data.rights:ready', function() {
  Curriki.data.rights.data = [];
  Curriki.data.rights.initial = Curriki.data.rights.list[0];
  Curriki.data.rights.list.each(function(right){
    Curriki.data.rights.data.push({
      inputValue:right
      ,boxLabel:_('CurrikiCode.AssetClass_rights_'+right)
      ,checked:Curriki.data.rights.initial == right?true:false
    });      
  });
});

Ext.Ajax.request({
  url: "/xwiki/curriki/metadata/CurrikiCode.AssetClass/fields/rights",
  method: 'GET',
  headers: {
    'Accept':'application/json'
  },
  success:function(response,options) {
    try {
      Curriki.data.rights.list = Ext.util.JSON.decode(response.responseText).allowedValues;            
    } catch(e) {
      console.error('Invalid metadata information', response, options);
    }
    Curriki.data.EventManager.fireEvent('Curriki.data.rights:ready');
  },
  failure:function(response,options) {
    console.error('Cannot get metadata information', response, options);
  }
});

Ext.ns('Curriki.data.language');
Curriki.data.EventManager.addListener('Curriki.data.language:ready', function() {
  Curriki.data.language.data = [];
  Curriki.data.language.initial = Curriki.data.language.list[0];
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
});

Ext.Ajax.request({
  url: "/xwiki/curriki/metadata/CurrikiCode.AssetClass/fields/language",
  method: 'GET',
  headers: {
    'Accept':'application/json'
  },
  success:function(response,options) {
    try {
      Curriki.data.language.list = Ext.util.JSON.decode(response.responseText).allowedValues;            
    } catch(e) {
      console.error('Invalid metadata information', response, options);
    }    
    Curriki.data.EventManager.fireEvent('Curriki.data.language:ready');
  },
  failure:function(response,options) {
    console.error('Cannot get metadata information', response, options);
  }
});

Ext.ns('Curriki.data.category');
Curriki.data.EventManager.addListener('Curriki.data.category:ready', function() {
  Curriki.data.category.data = [];
  Curriki.data.category.list.each(function(category){
    Curriki.data.category.data.push({
      inputValue:category
      ,boxLabel:_('CurrikiCode.AssetClass_category_'+category)
    });
  });
});
Ext.Ajax.request({
  url: "/xwiki/curriki/metadata/CurrikiCode.AssetClass/fields/category",
  method: 'GET',
  headers: {
    'Accept':'application/json'
  },
  success:function(response,options) {
    try {
      Curriki.data.category.list = Ext.util.JSON.decode(response.responseText).allowedValues;      
    } catch(e) {
      console.error('Invalid metadata information', response, options);
    }
    Curriki.data.EventManager.fireEvent('Curriki.data.category:ready');
  },
  failure:function(response,options) {
    console.error('Cannot get metadata information', response, options);
  }
});

Ext.ns('Curriki.data.license');
Curriki.data.EventManager.addListener('Curriki.data.license:ready', function() {
  Curriki.data.license.data = [];
  Curriki.data.license.initial = Curriki.data.license.list[0];    
    Curriki.data.license.list.each(function(lic){
      Curriki.data.license.data.push([
        lic
        ,_('CurrikiCode.AssetLicenseClass_licenseType_'+lic)
      ]);
    });
    Curriki.data.license.store = new Ext.data.SimpleStore({
      fields: ['id', 'license'],
      data: Curriki.data.license.data
    });
});
Ext.Ajax.request({
  url: "/xwiki/curriki/metadata/CurrikiCode.AssetLicenseClass/fields/licenseType",
  method: 'GET',
  headers: {
    'Accept':'application/json'
  },
  success:function(response,options) {
    try {
      Curriki.data.license.list = Ext.util.JSON.decode(response.responseText).allowedValues;      
    } catch(e) {
      console.error('Invalid metadata information', response, options);
    }   
    Curriki.data.EventManager.fireEvent('Curriki.data.license:ready');
  },
  failure:function(response,options) {
    console.error('Cannot get metadata information', response, options);
  }
});


Ext.ns('Curriki.data.fw_item');
Curriki.data.EventManager.addListener('Curriki.data.fw_item:ready', function() {  
  Curriki.data.fw_item.fwChildren = Curriki.data.fw_item.fwAddNode(Curriki.data.fw_item.fwMap, 'FW_masterFramework.WebHome').children;
});
Ext.Ajax.request({
  url: "/xwiki/curriki/metadata/CurrikiCode.AssetClass/fields/fw_items",
  method: 'GET',
  headers: {
    'Accept':'application/json'
  },
  success:function(response,options) {
    try {
      Curriki.data.fw_item.fwMap = Ext.util.JSON.decode(response.responseText).allowedValueMap;      
    } catch(e) {
      console.error('Invalid metadata information', response, options);
    }    
    Curriki.data.EventManager.fireEvent('Curriki.data.fw_item:ready');
  },
  failure:function(response,options) {
    console.error('Cannot get metadata information', response, options);
  }
});
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

});