/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

function clearBeforeUnloadMsg(){
	window.onbeforeunload = null;
}

function setBeforeUnloadMsg(){
	window.onbeforeunload = beforeLeave;
}

function beforeLeave() {
	return 'Navigating away from this screen will cause any changes to be lost. You can click on \'Save for Later\' at the bottom of the form page before trying to navigate away.';
}

document.onclick = function(e) {
	var target = getEventTarget(e);
       alert(target.tagName);
	if ( target && 
	     (target.tagName.toLowerCase() == 'button' &&
	     hasCSSClass(target,XFORMS_SUBMIT_CSS_CLASS))) {
		clearBeforeUnloadMsg();
	}
}

function hasCSSClass( targetElm, targetClass ) {
	var obj = targetElm.parentNode;
	while ( obj ) {
		var cssClass = obj.className;
		if ( cssClass && cssClass.toLowerCase().match(XFORMS_SUBMIT_CSS_CLASS)) {
			return true;
		}
		obj = obj.parentNode;
	}
	return false;
}


function getEventTarget(e) {
	var targetElm;
	if (!e) var e = window.event;
	if (e.target) targetElm = e.target;
	else if (e.srcElement) targetElm = e.srcElement;
	if ( targetElm && targetElm.nodeType == 3 )
		targetElm = targetElm.parentNode;
	return targetElm;
}

//setBeforeUnloadMsg();
