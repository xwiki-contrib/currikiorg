// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */
/*global _ */

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


Curriki.data.rights.initial = Curriki.data.rights.list[0];
Curriki.data.rights.data = [ ];
Curriki.data.rights.list.each(function(right){
	Curriki.data.rights.data.push({
		 inputValue:right
		,boxLabel:_('CurrikiCode.AssetClass_rights_'+right)
		,checked:Curriki.data.rights.initial == right?true:false
	});
});


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

Curriki.data.category.data = [ ];
Curriki.data.category.list.each(function(category){
	Curriki.data.category.data.push({
		 inputValue:category
		,boxLabel:_('CurrikiCode.AssetClass_category_'+category)
	});
});

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
