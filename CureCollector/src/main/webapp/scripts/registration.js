/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
dojo.require("dojo.fx");

/* DWR Error/Warning Handlers */
//DWREngine.setErrorHandler(function(){alert("A system error occurred.");});
//DWREngine.setWarningHandler(null);

/* Required Registration fields */
var requiredRegistrationFields = [ 'username', 'password', 'passwordConfirmation', 'emailAddress', 'emailAddressConfirmation', 'securityQuestion', 'securityQuestionAnswer'];

/* doStartConsentShowHide shows or hides all appropriate elements when the user clicks on "Start Consent" */
function doStartConsentShowHide( event ) {	
	showWithBlindDownEffect("consent1");
	showWithBlindDownEffect("consent2");
	hideElement("step2");
	hideElement("step3");
}

/* doAcceptConsentShowHide shows or hides all appropriate elements when the user clicks on "I Accept"*/
function doAcceptConsentShowHide( event ) {
	hideElement("step1");
	hideElement("consent1");
	hideElement("consent2");
	showWithBlindDownEffect("step2");
	showElement("startRegistrationSection");
}

/* doExitRegistration alerts the user and redirects to the Home Page once the user clicks on "I Reject" */
function doExitRegistration( event ) {
	alert("Thank you for considering Health of Women study.");
	if ( elm = event.target ) elm.checked = false;
	window.location='main.page';
}

function doCheckRegisterButton(){
	for ( index in requiredRegistrationFields ) {
		if ( !dojo.byId( requiredRegistrationFields[ index ] ).value ) {
			disableSubmitButton();
			return;
		}
	}
	enableSubmitButton();
}

/* Disables the Submit button*/
function disableSubmitButton() {
	var submitButton = dojo.byId('submit1');
	var elm = document.activeElement;
	disableElement( submitButton );
	if ( !elm.id || requiredRegistrationFields.indexOf( elm.id ) == -1  )
		submitButton.focus();
}

/* Enables the Submit button*/
function enableSubmitButton() {
	var submitButton = dojo.byId('submit1');
	var elm = document.activeElement;
	enableElement( submitButton );
	if ( !elm.id || requiredRegistrationFields.indexOf( elm.id ) == -1 )
		submitButton.focus();
}

/* doStartRegistration shows or hides all appropriate elements when the user clicks on "Start Registration"*/
function doStartRegistration( event ) {
	showWithBlindDownEffect("register");
	disableElement("submit1");
}

/* hideElementsOnStartup hides the elements that need to be hidden upon the loading of the registration page*/
function hideElementsOnStartup() {
	hideElement("consent1");
	hideElement("consent2");
	hideElement("register");
	hideElement("startRegistrationSection");
}

/* doRedisplayRegistrationForm shows/hides the appropriate elements when the registration form is redisplayed after a previous submission contained validation errors */
function doRedisplayRegistrationForm() {
	hideElement("step1");
	hideElement("consent1");
	hideElement("consent2");
	showElement("step2");
	showElement("startRegistrationSection");
	showElement("register");
	disableElement("submit1");
}

/* checkPasswordStrength uses the JQuery utility to determine the password strength*/
function checkPasswordStrength( event ) {
	var username = dojo.byId("username").value;
	var password = dojo.byId("password").value;
	passwordStrength( password, username );
}

/* Set up AJAX validation (uses Spring-enabled DWR) */
function validateInputField( event ) {
	var element = event.target;
	var id = element.id;
	var value = element.value;
	
	// set up the array of optional arguments
	var argArray = validateInputFieldOptionalArguments( id );
	
	UserValidatorJS.getInputFieldErrorMessages( id, value, argArray, {
			callback: function(data){
				setErrorsFromServer( element.id, data );
			}
	});
}

function validateInputFieldOptionalArguments( elementId ) {
	var argArray = [];
	
	if ( elementId == 'password' ) {
		var val = dojo.byId( 'securityQuestionAnswer' ).value;
		argArray.push( val );
		var val2 = dojo.byId( 'passwordStrengthPercent' ).value;
		argArray.push( val2 );
	}
	if ( elementId == 'securityQuestionAnswer' ) {
		var val = dojo.byId( 'password' ).value;
		argArray.push( val );
	}
	if ( elementId == 'passwordConfirmation' ) {
		var val = dojo.byId( 'password' ).value;
		argArray.push( val );
	}
	if ( elementId == 'emailAddressConfirmation' ) {
		var val = dojo.byId( 'emailAddress' ).value;
		argArray.push( val );
	}
	return argArray;
}

function setErrorsFromServer( id, data ) {
	// Get the target element
	var fieldElm = dojo.byId( id );
	
	// Get the error element
	var errorId  = id + 'Error';
	var errorElm = dojo.byId( errorId );
	
	// Create a String that will store the error messages
	var errMsg = '';
	for ( index in data ) {
		errMsg += data[ index ] + '<br/>';
	}
	
	// Update the appropriate element on the form
	if ( errMsg == '' ) {
		removeCSSClass( fieldElm, 'errorFieldClass' );
		errorElm.innerHTML = '';
	}
	else {
		addCSSClass( fieldElm, 'errorFieldClass');
		errorElm.innerHTML = errMsg;
	}
	
	// Enable the Register button if all required fields have been filled out
	doCheckRegisterButton();
}

/* Set up onload event handlers */
function init(event) {
	hideElementsOnStartup();
	
	dojo.connect(dojo.byId("startConsentBtn"),"onclick","doStartConsentShowHide");
	dojo.connect(dojo.byId("acceptCheckbox"),"onclick","doAcceptConsentShowHide");
	dojo.connect(dojo.byId("rejectCheckbox"),"onclick","doExitRegistration");
	dojo.connect(dojo.byId("startRegistrationBtn"),"onclick","doStartRegistration");
	dojo.connect(dojo.byId("user_password"),"onblur","checkPasswordStrength");
	
	// Enable AJAX validation for all the form elements on this page
	registrationFields = dojo.query("input[type=text],input[type=password],select,textarea" );
	registrationFields.connect("onblur","validateInputField");
	
	// If the page is being redisplayed due to validation errors from submission of the registration form,
	// show/hide all appropriate divs
	if ( dojo.query('.errorClass.global').length > 0  ) {
		doRedisplayRegistrationForm();
	}
	
}

dojo.addOnLoad(init);
