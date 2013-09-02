/* 
 * Lets automatically convert all images into responsive using jquery and bootstrap
 */
var $j = jQuery.noConflict();
$j(document).ready(function() {
    $j("#mainContentArea img").each(function() {
        $j(this).addClass("img-responsive");
    });
});
