/* 
 * Lets automatically convert all images into responsive using jquery and bootstrap
 */
var $j = jQuery.noConflict();
$j(document).ready(function() {
    $j("#mainContentArea img").each(function() {
        $j(this).addClass("img-responsive");
    });
    
    $j("#breadcrumb-icon a").click(function(){
        $j("#dropable-toc").slideToggle();
        return false;
    });
    
    $j("body").on("click", "#dropable-toc ul li a.has-dropdown", function() {
        //lets change the icon arrow
        var item_status = $j(this).next('ul').css('display');
        if (item_status === 'none') {
            $j(this).find('i').removeClass().addClass('icon-caret-down');
        } else {
            $j(this).find('i').removeClass().addClass('icon-caret-left');
        }
        $j(this).next('ul').slideToggle();
        return false;
    });
});
