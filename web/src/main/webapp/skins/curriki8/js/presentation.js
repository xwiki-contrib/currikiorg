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



var CurrikiJS = {

	validEmail: function(emailStr){
		// return true if 'emailStr' has valid email address format; from jt_.js, wingo.com
		var emailPat=/^(.+)@(.+)$/
		var specialChars="\\(\\)<>@,;:\\\\\\\"\\.\\[\\]"
		var validChars="\[^\\s" + specialChars + "\]"
		var quotedUser="(\"[^\"]*\")"
		var ipDomainPat=/^\[(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\]$/
		var atom=validChars + '+'
		var word="(" + atom + "|" + quotedUser + ")"
		var userPat=new RegExp("^" + word + "(\\." + word + ")*$")
		var domainPat=new RegExp("^" + atom + "(\\." + atom +")*$")
		var matchArray=emailStr.match(emailPat)
		if (matchArray==null) return false
		var user=matchArray[1]
		var domain=matchArray[2]
		if (user.match(userPat)==null) return false
		var IPArray=domain.match(ipDomainPat)
		if (IPArray!=null) {
			for (var i=1;i<=4;i++) {
				if (IPArray[i]>255) return false
			}
			return true
		}
		var domainArray=domain.match(domainPat)
		if (domainArray==null) return false
		var atomPat=new RegExp(atom,"g")
		var domArr=domain.match(atomPat)
		var len=domArr.length
		if (domArr[domArr.length-1].length<2 || 
		domArr[domArr.length-1].length>4) return false
		if (len<2) return false
		return true;
	},

	trimFields: function(aForm) {
		for (var i=0; i<aForm.elements.length; i++) {
			if ((aForm.elements[i].type == 'text') || (aForm.elements[i].type == 'textarea')) {
				aForm.elements[i].value = aForm.elements[i].value.trim();
			}
		}
	},

	hightLightEl: function (elName){
		$(elName).addClassName('highlight');
	},

	errMsg: '',

	errMsgAdd: function(msg){
		CurrikiJS.errMsg += msg + "\n";
	},

	errMsgShow: function(){
		if (CurrikiJS.errMsg) {
			var tmp = CurrikiJS.errMsg;
			CurrikiJS.errMsg = '';
			alert(tmp);
		}
	}

}
