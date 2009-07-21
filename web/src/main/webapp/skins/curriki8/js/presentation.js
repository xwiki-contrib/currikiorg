Ext.namespace('Layout');

Layout.accordion = function(containerName) {
	var subPanelsDom = Ext.query('#'+containerName+'>div.x-panel-body>div.x-panel');
	var subPanelsCmp = new Array();
	for (var x in subPanelsDom) {
		subPanelsCmp[x] = new Ext.Panel({el: subPanelsDom[x], collapsed: x!=0});
	}
	var acdnPanel = new Ext.Panel({
		layout: 'accordion'
		,applyTo: containerName
		,autoHeight: true
		,autoScroll: true
		,layoutConfig: {
			animate: true
			,fill: false
		}
		,items: subPanelsCmp
	});
}
