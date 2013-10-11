/* 
 * Lets automatically convert all images into responsive using jquery and bootstrap
 */
$.noConflict();
jQuery(document).ready(function() {
    jQuery("#mainContentArea img").each(function() {
        jQuery(this).addClass("img-responsive");
    });

    jQuery("#breadcrumb-icon a").click(function() {
        jQuery("#dropable-toc").slideToggle();
        return false;
    });

    jQuery("body").on("click", "#dropable-toc ul li a.has-dropdown", function() {
        //lets change the icon arrow
        if (jQuery(this).hasClass('item-type-Text') === false){
            var item_status = jQuery(this).next('ul').css('display');
            if (item_status === 'none') {
                jQuery(this).find('i').removeClass().addClass('icon-caret-down');
            } else {
                jQuery(this).find('i').removeClass().addClass('icon-caret-left');
            }
            jQuery(this).next('ul').slideToggle();
            return false;
        }
    });
});
