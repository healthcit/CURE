/**
 * 
 */

/**
 * DWR methods
 */

// Module upload 
function checkModuleID(){
	alert ('in checkmoduleID function') ;
	UploadModuleController.moduleDupCheck(
			"abc","filepath", {
			callback:userWaring,
			errorHandler:fileUploadError});
}


function userWaring( data ){
	alert ('in user warning function') ;
	alert(data);
	
}

//Module dropdown's onchange method error handler
function fileUploadError( data ){
	alert('Error while uploading the file');
}

// Displays splash screen while the module is uploaded
function showUploadModuleSplashScreen(){
	jQuery('#uploadModule,#updateFormSkipData').hide();
	jQuery('#loadingSpinner').show();
	setInterval(function(){jQuery('#loadingSpinner .text').fadeToggle(2000);},2000);
}

