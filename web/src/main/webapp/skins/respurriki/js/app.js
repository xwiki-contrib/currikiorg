// In case we forget to take out console statements. IE becomes very unhappy when we forget. Let's not make IE unhappy
if (typeof(console) === 'undefined') {
  var console = {};
  console.log = console.error = console.info = console.debug = console.warn = console.trace = console.dir = console.dirxml = console.group = console.groupEnd = console.time = console.timeEnd = console.assert = console.profile = function () {
  };
}

// Some backwards compatibility to ensure some of the
// olf functionality keeps flowing.
if (typeof(Curriki) === 'undefined') {
  var Curriki = {};
  Curriki.ui = {};
  Curriki.ui.login = {};
}

// Fix the Curriki console
Curriki.console = window.console;

// Fix for the "BackFromAuthorization"

Curriki.ui.login.finishAuthorizationPopup = function (targetURL, openerWindow, openedWindow, toTop) {
  Curriki.console.log("Finishing popup, (toTop? " + toTop + ") target: " + targetURL);
  if (typeof(openerWindow) == "undefined" || openerWindow == window) {
    openerWindow = window.open(targetURL, "currikiMainWindow");
  }
  if (openerWindow) { // && (openerWindow.Curriki.ui.login.authorizeDialog && openerWindow.Curriki.ui.login.authorizeDialog==window || (openerWindow.top.Curriki.ui.login.authorizeDialog && openerWindow.top.Curriki.ui.login.authorizeDialog==window))

    // we are in a popup relationship, can close and revert to that popup
    Curriki.console.log("We are in popup, closing and opening popup.");
    var targetWindow = openerWindow;
    if (openerWindow.Curriki.ui.login.windowThatShouldNextGoTo)
      targetWindow = openerWindow.Curriki.ui.login.windowThatShouldNextGoTo;
    Curriki.console.log("targetWindow: " + targetWindow + " with force to top " + toTop);
    if (toTop) targetWindow = targetWindow.top;
    //      else if(openerWindow.Ext && openerWindow.Ext.get('loginIframe'))
    //          targetWindow = openerWindow.Ext.get('loginIframe').dom.contentWindow;
    if (targetWindow && targetWindow.location) {
      targetWindow.location.href = targetURL;
      //alert("Would go to " + targetURL + " from " + targetWindow);
      // schedule a close
      openedWindow.setInterval(function () {
        try {
          targetWindow.focus();
        } catch (e) {
          Curriki.console.log(e);
        }
        try {
          openedWindow.close();
        } catch (e) {
          Curriki.console.log(e);
        }
      }, 20);
    } else {
      var w = window;
      if (toTop) w = w.top;
      w.location.href = targetURL;
    }
    return false;
  } else {
    Curriki.console.log("No popup parent found... ah well.");
    var w = openedWindow;
    if (toTop) w = w.top;
    w.location.href = targetURL;
    //alert("Would go to " + targetURL + " from " + openedWindow);
  }
};

/*
 * Lets automatically convert all images into responsive using jquery and bootstrap
 */
$.noConflict();
jQuery(document).ready(function() {
    jQuery("#show-admin-menu").mouseover(function() {
        jQuery('#menuview').slideDown('slow');
    });
    jQuery("#show-admin-menu").mouseout(function() {
        if (window.hider) {
            window.clearInterval(window.hider);
        }
        ;
        window.hider = window.setTimeout('jQuery(jQuery("#menuview")[0]).slideUp("slow");', 4000);

    });

    jQuery("#mainContentArea img").each(function() {
        jQuery(this).addClass("img-responsive");
    });

    jQuery("#breadcrumb-icon a").click(function() {
        jQuery("#dropable-toc").slideToggle();
        return false;
    });

    jQuery("body").on("click", "#dropable-toc ul li .main-item a.icon-large", function() {
        //lets change the icon arrow
        var item_status = jQuery(this).parent().parent().find('ul').css('display');
        if (item_status === 'none') {
            jQuery(this).removeClass('icon-caret-left').addClass('icon-caret-down');
        } else {
            jQuery(this).removeClass('icon-caret-down').addClass('icon-caret-left');
        }
        jQuery(this).parent().parent().find('ul').slideToggle();
        return false;
    });

    //style tables automatically
    jQuery(".asset-display-text table").addClass('table table-bordered table-hover table-striped');
    //enable tooltips automatically
    jQuery('.has-tooltip').tooltip({
        placement: 'top',
    });

    /*
     jQuery('.wikicreatelink a').append(' <i class="icon-external-link"></i>');
     jQuery('.wikiexternallink a').append(' <i class="icon-external-link"></i>');
     */

    jQuery('.wikicreatelink a').each(function() {
        var the_rel = jQuery(this).attr('rel');
        var the_target = jQuery(this).attr('target');

        if (typeof the_rel !== 'undefined') {
            if (the_rel.indexOf('blank') !== -1) {
                jQuery(this).append(' <i class="icon-external-link"></i>');
            } else {
                if (typeof the_target !== 'undefined') {
                    if (the_target.indexOf('blank') !== -1) {
                        jQuery(this).append(' <i class="icon-external-link"></i>');
                    }
                }
            }
        } else {
            if (typeof the_target !== 'undefined') {
                if (the_target.indexOf('blank') !== -1) {
                    jQuery(this).append(' <i class="icon-external-link"></i>');
                }
            }
        }
    });

    jQuery('.wikiexternallink a').each(function() {
        var the_rel = jQuery(this).attr('rel');
        var the_target = jQuery(this).attr('target');

        if (typeof the_rel !== 'undefined') {
            if (the_rel.indexOf('blank') !== -1) {
                jQuery(this).append(' <i class="icon-external-link"></i>');
            } else {
                if (typeof the_target !== 'undefined') {
                    if (the_target.indexOf('blank') !== -1) {
                        jQuery(this).append(' <i class="icon-external-link"></i>');
                    }
                }
            }
        } else {
            if (typeof the_target !== 'undefined') {
                if (the_target.indexOf('blank') !== -1) {
                    jQuery(this).append(' <i class="icon-external-link"></i>');
                }
            }
        }
    });

    jQuery('.wikilink a').each(function() {
        var the_rel = jQuery(this).attr('rel');
        var the_target = jQuery(this).attr('target');

        if (typeof the_rel !== 'undefined') {
            if (the_rel.indexOf('blank') !== -1) {
                jQuery(this).append(' <i class="icon-external-link"></i>');
            } else {
                if (typeof the_target !== 'undefined') {
                    if (the_target.indexOf('blank') !== -1) {
                        jQuery(this).append(' <i class="icon-external-link"></i>');
                    }
                }
            }
        } else {
            if (typeof the_target !== 'undefined') {
                if (the_target.indexOf('blank') !== -1) {
                    jQuery(this).append(' <i class="icon-external-link"></i>');
                }
            }
        }
    });

    //now the click events
    jQuery('.wikicreatelink a').click(function() {
        var the_rel = jQuery(this).attr('rel');
        var the_target = jQuery(this).attr('target');
        if (typeof the_rel !== 'undefined' || typeof the_target !== 'undefined') {
            var url = jQuery(this).attr('href');
            window.open(url, '_blank');
            return false;
        }
    });
    jQuery('.wikiexternallink a').click(function() {
        var the_rel = jQuery(this).attr('rel');
        var the_target = jQuery(this).attr('target');
        if (typeof the_rel !== 'undefined' || typeof the_target !== 'undefined') {
            var url = jQuery(this).attr('href');
            window.open(url, '_blank');
            return false;
        }
    });
    jQuery('.wikilink a').click(function() {
        var the_rel = jQuery(this).attr('rel');
        var the_target = jQuery(this).attr('target');
        if (typeof the_rel !== 'undefined' || typeof the_target !== 'undefined') {
            var url = jQuery(this).attr('href');
            window.open(url, '_blank');
            return false;
        }
    });

    jQuery('#tab-email a').on('click', function(e) {
        e.preventDefault();
        //var url = jQuery(this).attr('href');
        //jQuery('#modal_addthis .modal-body').html('<iframe width="100%" height="2550px" frameborder="0" scrolling="no" allowtransparency="true" src="' + url + '"></iframe>');
        addthis.button(".addthis-button");
    });

});
