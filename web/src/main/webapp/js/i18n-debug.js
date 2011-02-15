// vim: ts=4:sw=4
function _() {
	if (arguments.length < 1){
		return '';
	}
	if (typeof i18nDict != 'undefined' && typeof i18nDict[arguments[0]] === 'string'){
		var t = i18nDict[arguments[0]];
		var args;

		if (Object.isArray(arguments[1])){
			args = arguments[1];
		} else {
			args = $A(arguments);
			args.shift();
		}
		args.each(function(s, i){
			t = t.replace('{' + i + '}', s);
		});
		return t;
	} else {
		return arguments[0];
	}
}
