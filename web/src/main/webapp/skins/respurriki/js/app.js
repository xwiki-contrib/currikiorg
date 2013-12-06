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
    /*
    jQuery('#large-toc #toggle-secondary-toc').click(function() {
        var toc_visible = jQuery('#large-toc .secondary-toc').css('display');
        if (toc_visible == 'none') {
            jQuery('#large-toc .secondary-toc').slideDown('slow');
            jQuery(this).css('border-radius', '5px 5px 0px 0px');
        } else {
            jQuery('#large-toc .secondary-toc').slideUp('slow');
            jQuery(this).css('border-radius', '5px 5px 5px 5px');
        }
    });
    */
});
