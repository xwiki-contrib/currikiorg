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
});
