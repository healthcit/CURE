/**
 * Invoked when the "Update" button is clicked for a particular action;
 * Updates the template file associated with this action
 */
function updateWorkflow(xformAction)
{
	var actionLabel = jQuery('#'+xformAction+'_buttonLabel').val();
	var actionDescription = jQuery('#'+xformAction+'_buttonDescription').val();
	var actionHide = ''+jQuery('#'+xformAction+'_buttonHide').is(':checked');
	if ( !actionLabel.trim() && !actionDescription.trim() )
	{
		alert('The Associated Button Label and Associated Button Description fields may not be empty');
		return;
	}
		
	jQuery.post(window.location.pathname,
			    {beforeSend:function(){addSpinner(xformAction);},
				name:xformAction,label:actionLabel,description:actionDescription,hideFlag:actionHide},
			    function(data){setTimeout("updateWorkflowCallback('"+data+"')",1000);});
}

/**
 * Callback for the updateWorkflow method
 * @param data
 */
function updateWorkflowCallback(data){
	if ( data == 'OK' ) alert('Update Successful.');
	else alert('ERROR: An error occurred during the update.\nPlease see your System Administrator.');
	removeAllHighlights();
}
 
/**
 * Invoked when the onChange event is triggered for a text field(on the Workflow Configuration screen)
 * @param elm
 * @param action
 */
function updateOnChange(elm,action){
	// apply highlights to elements associated with this action
	highlight(elm,action);
	// Currently empty strings are not allowed; 
	// there should be at least 1 whitespace for the current server-side operation which updates the template file to work
	// (because of the Regex expressions currently used by this operation)
	if( jQuery(elm).val() == '' ) jQuery(elm).val(' ');
}

/**
 * Highlights elements associated with this particular action
 * @param elm
 * @param action
 */
function highlight(elm,action){
	removeAllHighlights();
	jQuery('tr:has(input#' + action +'_buttonLabel) td:first span').addClass('updatable');
	jQuery(elm).addClass('updatable');
}

/**
 * Removes all "highlights" currently on the page
 */
function removeAllHighlights(){
	jQuery('div.configureWorkflow span,div.configureWorkflow input').removeClass('updatable').removeClass('updating');
}

// Utility function which adds the spinner image to the highlighted row
function addSpinner(xformAction){
	jQuery('tr:has(input#' + xformAction +'_buttonLabel) td:first span').addClass('updating');
}