// vim: ts=4:sw=4
/*global Ext */
/*global Curriki */

Ext.onReady(function(){

//	Curriki.current.assetName = 'AssetTemp.test';
//	Curriki.current.cameFrom = 'nav';
//	Curriki.current.flow = 'fromNav';

	Curriki.init({module:'addPath'});

/*
startfunc = function(){
Ext.apply(Curriki.current, {
	assetName:"Coll_dward.a"
	,cameFrom:"http://localhost:9080/xwiki/bin/view/AddPath/AddTo"
	,flow:"A"
	,linkUrl:"http://test1.com/"
	,publishSpace:"Coll_dward"
	,selected:"link"
	,asset:{
		assetPage:"Coll_dward.a"
		,assetType:"External"
		,description:"a"
		,title:"a"
		,fullAssetType:"org.curriki.xwiki.plugin.asset.external.ExternalAsset"
	}
});
	Curriki.module.addpath.init();
	Curriki.ui.show('apLocation');


//Ext.util.Observable.capture(Ext.getCmp('ctv-to-tree-cmp'), function(e){console.info(e)}); 
};


console.log('Init Curriki');
	Curriki.init(startfunc);
console.log('Done Curriki');

Ext.getCmp('ctv-to-tree-cmp').getNodeById('ctv-target-node');


*/
});
