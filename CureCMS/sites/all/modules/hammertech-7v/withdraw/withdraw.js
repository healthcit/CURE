Drupal.behaviors.withdraw = function(context) {
	$block = $("#edit-block");
	$remove = $("#edit-remove");
	
	$($block).click(function(){
	    if($($remove).attr('checked') == 1) {
	    	// need to uncheck remove and hide it's dynamic text
	    	$($remove).removeAttr('checked');
	    	$($remove).parent().removeClass('checked');
	    	$("#withdraw-remove").css('display', 'none');
	    }
	    // add the text
    	$("#withdraw-block").css('display', 'block');
	});
	$($remove).click(function() {
		if($($block).attr('checked') == 1) {
			// need to uncheck block and hide it's dynamic text
			$($block).removeAttr('checked');
			$($block).parent().removeClass('checked');
			$("#withdraw-block").css('display', 'none');
		}
		// add the text
		$("#withdraw-remove").css('display', 'block');
	});
	
	
};