function _() {
    window.thisArguments = arguments;
	if (arguments.length < 1){
		return '';
	}
	if (i18nDict && typeof i18nDict[arguments[0]] === 'string'){
		var t = i18nDict[arguments[0]];
		var args;

		if (jQuery.isArray(arguments[1])){
			args = arguments[1];
		} else {
			args = new Array();
            for(var i=1; i<arguments.length; i++) {
                args.push(i);
            }
		}
		$(args).each(function(i,s){
			t = t.replace('{' + i + '}', s);
		});
		return t;
	} else {
		return arguments[0];
	}
}
