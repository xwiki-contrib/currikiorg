// vim: sw=4:ts=4
//
// NOTE: This has velocity macros, so cannot be loaded as normal javascript
//       (it is embedded in the navigation panel page instead)
//
<script type="text/javascript">
/* <![CDATA[ */
Ext.onReady(function(){
#if($doc.web.startsWith("Coll_")) ##{
#set($cookiebase = "${context.user}_asset_")
#else ##}{
#set($cookiebase = "${context.user}_")
#end ##} 
#set($cookiebase = "${cookiebase}_currikinavcategory_home")
	Ext.ns('Curriki.data.nav');
	var nav = Curriki.data.nav;
	nav.expanded = true;
	nav.navCookieBase = "${cookiebase}";
	nav.expandedCookie = readCookie(nav.navCookieBase);
	if (!Ext.isEmpty(nav.expandedCookie)){
		if (nav.expandedCookie === 'false') {
			nav.expanded = false;
		} else {
			eraseCookie(nav.navCookieBase);
		}
	}
	Ext.select('#navigation-inner/div[class*=home]/a').first().dom.innerHTML = '<span id="navhome-expander" class="'+(nav.expanded?"expanded":"collapsed")+'"></span>' + Ext.select('#navigation-inner/div[class*=home]/a').first().dom.innerHTML;

	if (!nav.expanded) {
		Ext.select('#navigation-inner/div[id]').each(function(el) {el.setVisibilityMode(Ext.Element.DISPLAY); el.hide();})
	}

	Ext.get('navhome-expander').on({
		'click':{
			fn: function(e, t, o){
				var el = Ext.get(t.id);
				if (el.hasClass('expanded')) {
					el.replaceClass('expanded', 'collapsed');
					Ext.select('#navigation-inner/div[id]').each(function(el) {el.setVisibilityMode(Ext.Element.DISPLAY); el.hide();})
					createCookie(nav.navCookieBase, false, '');
				} else if (el.hasClass('collapsed')) {
					el.replaceClass('collapsed', 'expanded');
					Ext.select('#navigation-inner/div[id]').each(function(el) {el.setVisibilityMode(Ext.Element.DISPLAY); el.show();})
					eraseCookie(nav.navCookieBase);
				}
			}
			,stopEvent:true
		}
	});
});
/* ]]> */
</script>
