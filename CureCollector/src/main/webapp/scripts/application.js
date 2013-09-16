/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
/* Global variables */
var allFormElements;

/* Utility method to show a hidden element */
function showElement( elmId ) {
	dojo.style(elmId, "display", "block");
}

/* Utility method to hide a displayed element */
function hideElement( elmId ) {
	dojo.style(elmId, "display", "none");
}

/* Utility method to enable an element */
function enableElement( elmId ) {
	dojo.byId(elmId).disabled=false;
}

/* Utility method to disable an element */
function disableElement( elmId ) {
	dojo.byId(elmId).disabled=true;
}

/* Utility method to add a CSS class to an element*/
function addCSSClass( elm, cssClass ) {
	dojo.addClass( elm, cssClass );
}

/* Utility method to remove a CSS class from an element*/
function removeCSSClass( elm, cssClass ) {
	dojo.removeClass( elm, cssClass );
}


/* Utility method to disable an element */

/* Utility method which will display a hidden element with a blind-down effect */
function showWithBlindDown( elmId, timeDuration ) {
	dojo.style(elmId, "display", "none");
	var wipeArgs = {
      node: elmId,
      duration: timeDuration
    };
	dojo.fx.wipeIn(wipeArgs).play();
}

/* Utility method which will display a hidden element with a blind-up effect */
function hideWithBlindUp( elmId, timeDuration ) {
	dojo.style(elmId, "display", "block");
	var wipeArgs = {
      node: elmId,
      duration: timeDuration
    };
	dojo.fx.wipeOut(wipeArgs).play();
}

function showWithBlindDownEffect( elmId ) {
	showWithBlindDown( elmId, 600 );
}

function hideWithBlindUpEffect( elmId ) {
	hideWithBlindUp( elmId, 600 );
}

function showEffectOnFocus( event ) {
	addCSSClass( event.target, "focusCSS");
	return false;
}

function removeEffectOnBlur( event ) {
	removeCSSClass( event.target, "focusCSS");
	return false;
}

/* Initialization method */
function initForm( event ){
	/* Add an onfocus effect to all form fields */
	allFormElements = dojo.query("input[type=text],input[type=password],input[type=checkbox],select,textarea" );
	allFormElements.connect("onfocus","showEffectOnFocus"); 
	allFormElements.connect("onblur", "removeEffectOnBlur"); 
}

dojo.addOnLoad( initForm );
