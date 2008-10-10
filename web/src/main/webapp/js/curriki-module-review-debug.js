// vim: ts=4:sw=4
/* global Ext */
/* global Curriki */
/* global _ */

Ext.ns('Curriki.module.review');

Curriki.module.review.validateCRS = function(appropriatepedagogyName,contentaccuracyName,technicalcompletnessName ){
	var appropriatepedagogyRadios = Ext.select('input[type="radio"][name='+appropriatepedagogyName+']').elements
	var contentaccuracyRadios = Ext.select('input[type="radio"][name='+contentaccuracyName+']').elements
	var technicalcompletnessRadios = Ext.select('input[type="radio"][name='+technicalcompletnessName+']').elements

	for( i = 0; i < appropriatepedagogyRadios.length; i++ ) {
		if (appropriatepedagogyRadios[i].checked == true) {
			break;
		}

		if (appropriatepedagogyRadios[i].checked == false && i == appropriatepedagogyRadios.length-1) {
			alert(_('curriki.crs.review.mustSelectAValueInAllCategories'));
			return false;
		}
	}

	for( i = 0; i < contentaccuracyRadios.length; i++ ) {
		if (contentaccuracyRadios[i].checked == true) {
			break;
		}
		if (contentaccuracyRadios[i].checked == false && i == contentaccuracyRadios.length-1) {
			alert(_('curriki.crs.review.mustSelectAValueInAllCategories'));
			return false;
		}
	}

	for( i = 0; i < technicalcompletnessRadios.length; i++ ) {
		if (technicalcompletnessRadios[i].checked == true) {
			break;
		}
		if (technicalcompletnessRadios[i].checked == false && i == technicalcompletnessRadios.length-1) {
			alert(_('curriki.crs.review.mustSelectAValueInAllCategories'));
			return false;
		}
	}

	var appropriatepedagogyNotRatedRadio = Ext.select('input[type="radio"][name='+appropriatepedagogyName+'][value="0"]').first().dom;
	var contentaccuracyNotRatedRadio = Ext.select('input[type="radio"][name='+contentaccuracyName+'][value="0"]').first().dom;
	var technicalcompletnessNotRatedRadio = Ext.select('input[type="radio"][name='+technicalcompletnessName+'][value="0"]').first().dom;

	if (appropriatepedagogyNotRatedRadio.checked && contentaccuracyNotRatedRadio.checked && technicalcompletnessNotRatedRadio.checked) {
		return true;
	}
	if (!appropriatepedagogyNotRatedRadio.checked && (contentaccuracyNotRatedRadio.checked || technicalcompletnessNotRatedRadio.checked)) {
		alert(_('curriki.crs.review.notValidNotRatedCategorySelection'));
		return false;
	}
	if (appropriatepedagogyNotRatedRadio.checked &&
			((contentaccuracyNotRatedRadio.checked && !technicalcompletnessNotRatedRadio.checked)||(!contentaccuracyNotRatedRadio.checked && technicalcompletnessNotRatedRadio.checked))) {
		alert(_('curriki.crs.review.notValidNotRatedCategorySelection'));
		return false;
	}

	return true;
}
