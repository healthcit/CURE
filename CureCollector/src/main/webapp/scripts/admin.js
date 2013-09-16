/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
var formActionPath;

var truncateModuleDialog;

jQuery(document).ready( function(){
	  truncateModuleDialog = jQuery( "#truncateByModuleDialog" ).dialog({
      autoOpen: false,
      height: 300,
      width: 350,
      modal: true,
      buttons: {
        "Truncate": function() {
        	var moduleSelect = jQuery('#moduleSelectWidget option:selected');
        	var deleteModuleId = moduleSelect.val();
        	var deleteModuleName = moduleSelect.text();
        	if ( confirm("Are you sure you want to truncate '" + deleteModuleName + "'?") ) {
        		
        		window.location.href = formActionPath + "?moduleId=" + deleteModuleId;
        	}
        	jQuery( this ).dialog( "close" );
        },
        "Cancel": function() {
          $( this ).dialog( "close" );
        }
      },
      close: function() {
        var resetModuleId = jQuery('#moduleSelectWidget option:first').val();
        jQuery('#moduleSelectWidget').val(resetModuleId);
      }
});});

function truncateModule(formAction)
{
	formActionPath = formAction;
	truncateModuleDialog.dialog("open");
}

function executePostProcessingUrls(url, ctr)
{
	jQuery.getJSON(url, function( results ) {
		if ( results["status"] == "ok") {
			jQuery('li#modRedUrl_'+ctr+' span.success').html("SUCCESS!!!");
		} else {
			jQuery('li#modRedUrl_'+ctr+' span.warning').html("WARNING: " + results["status"] + ": " + url);
		}
	}).fail(function( jqxhr, textStatus, error ) {
		var err = textStatus + ', ' + error;
		jQuery('li#modRedUrl_'+ctr+' span.warning').html("WARNING: " + err+ ": " + url);
	});
}
