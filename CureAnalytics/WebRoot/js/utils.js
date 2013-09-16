/*******************************************************************************
 *Copyright (c) 2013 HealthCare It, Inc.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the BSD 3-Clause license
 *which accompanies this distribution, and is available at
 *http://directory.fsf.org/wiki/License:BSD_3Clause
 *
 *Contributors:
 *    HealthCare It, Inc - initial API and implementation
 ******************************************************************************/
/*Date functions*/
var gsMonthNames = new Array('January','February','March','April','May','June','July','August','September','October','November','December');

var gsDayNames = new Array('Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday');

Number.prototype.zf = function(num){
	if ( !this ) return num;
	
	var numString = this.toString();
	
	while ( numString.length < num ) {
		numString = '0' + numString;
	}
	
	return numString;
}

Date.prototype.format = function(f)
{
    if (!this.valueOf())
        return ' ';

    var d = this;

    return f.replace(/(yyyy|mmmm|mmm|mm|dddd|ddd|dd|hh|nn|ss|a\/p)/gi,
        function($1)
        {
            switch ($1.toLowerCase())
            {
            case 'yyyy': return d.getFullYear();
            case 'mmmm': return gsMonthNames[d.getMonth()];
            case 'mmm':  return gsMonthNames[d.getMonth()].substr(0, 3);
            case 'mm':   return (d.getMonth() + 1).zf(2);
            case 'dddd': return gsDayNames[d.getDay()];
            case 'ddd':  return gsDayNames[d.getDay()].substr(0, 3);
            case 'dd':   return d.getDate().zf(2);
            case 'hh':   return ((h = d.getHours() % 12) ? h : 12).zf(2);
            case 'nn':   return d.getMinutes().zf(2);
            case 'ss':   return d.getSeconds().zf(2);
            case 'a/p':  return d.getHours() < 12 ? 'am' : 'pm';
            }
        }
    );
}

/* String functions */
String.prototype.capitalizeIt = function() {
	var rest = this.substring(1);
	var first = this[0] ? this[0].toUpperCase() : '';
	return first + rest;
}

/* Function which will output the HTML content of a JQuery element, including the values of input fields*/
jQuery.fn.htmlWithFormElementValues = function(){
	var htmlString = jQuery(this).html();
	var inputElms = jQuery(this).find('input,option');
	inputElms.each( function() {
		var elm = jQuery(this);
		var originalHtml = elm.outerHtml();
		var finalHtml = '';
		var isChecked  = elm.is(':checked');
		var isSelected = elm.is(':selected');
		if ( isChecked )  finalHtml = originalHtml.replace(/(\/?)>/,' checked="true"' + '$1>');
		if ( isSelected ) finalHtml = originalHtml.replace(/(\/?)>/,' selected="true"' + '$1>');
		if ( isChecked || isSelected ) htmlString = htmlString.replace( originalHtml, finalHtml );
	})
	return htmlString;
}

/* Function which returns the outer HTML of an element*/
jQuery.fn.outerHtml = function(){
	return jQuery('<div>').append(jQuery(this).clone()).remove().html();
}

/* Function which will return a new array with duplicate values removed */
if ( !Array.prototype.unique  ) Array.prototype.unique =
	function() {
	 var a = [];
	 var l = this.length;
	 for(var i=0; i<l; i++) {
	   for(var j=i+1; j<l; j++) {
	     // If this[i] is found later in the array
	     if (this[i] === this[j])
	       j = ++i;
	   }
	   a.push(this[i]);
	 }
	 return a;
	};

/*JQuery Download Plugin*/
$.download = function (url, data, method, callback) {
    var inputs = '';
    var iframeX;
    var downloadInterval;
    if (url && data) {
        // remove old iframe if has
        if (jQuery("#iframeX")) jQuery("#iframeX").remove();
        // creater new iframe
        iframeX = jQuery('<iframe src="[removed]false;" name="iframeX" id="iframeX"></iframe>').appendTo('body').hide();
        iframeX.ready(function(){
        	if ( callback ) callback();
    	}); 

        //split params into form inputs
        jQuery.each(data, function (p, val) {
            inputs += '<input type="hidden" name="' + p + '" value="' + val + '" />';
        });

        //create form to send request
        jQuery('<form action="' + url + '" method="' + (method || 'post') + '" target="iframeX">' + inputs + '</form>').appendTo('body').submit().remove();
    };
};

//Utility methods to purge all active links to a javascript object before destroying the object
function destroyObject( obj ){
	if ( obj )
	{
		for ( var property in obj )
		{
			obj[ property ] = null;
		}	
		obj = null;
	}
}
function purgeHTMLElement( elm ){
	var a = elm.attributes, i, l, n;
    if (a) {
        l = a.length;
        for (i = 0; i < l; i += 1) {
            n = a[i].name;
            if (typeof elm[n] === 'function') {
            	elm[n] = null;
            }
        }
    }
    a = elm.childNodes;
    if (a) {
        l = a.length;
        for (i = 0; i < l; i += 1) {
        	purgeHTMLElement(elm.childNodes[i]);
        }
    }
}
function arrayMatches(arr1,arr2){
	if ( !arr1 || !arr2 ) return false;
	if ( arr1.length != arr2.length ) return false;
	for ( var i = 0; i < arr1.length; ++i ){
		if ( arr1[i] != arr2[i]) return false;
	}
	return true;
}

// returns whether arr1 begins with the elements from arr2
function arrayStartsWith(arr1,arr2){
	if ( !arr1 || !arr2 ) return false;
	if ( arr1.length < arr2.length ) return false;
	for ( var i = 0; i < arr2.length; ++i ){
		if ( arr1[i] != arr2[i]) return false;
	}
	return true;
}

// returns whether the given array contains this object
function arrayContains( arr, obj ) {
	return jQuery.inArray(obj,arr) != -1;
}

// returns the intersection of these 2 arrays
function arrayIntersection( arr1, arr2 ) {
	if ( arr1 == null || arr2 == null ) return null;
	return arr1.filter(function(x){
		return arrayContains(arr2,x);
	});
}

// returns whether or not this object is an array
function isArray(obj) {
	if ( !obj ) return false;
	return (obj.constructor.toString().indexOf('Array') != -1);
}

// a simple "sleep" function
function simulatedSleep(duration,callback){
	var interval;
	if ( !isNaN(duration) ) interval = duration;
	else interval = (duration == 'extrafast' ? 500 : (duration == 'fast' ? 1000 : (duration == 'medium' ? 6000 : 10000)));
	var params = {url:  window.location.pathname + 'serverSideTimer.jsp',
	         	  data:{interval:interval},
	         	  async:false};
	if ( callback && typeof callback == 'function' ) params['success'] = callback;
	jQuery.ajax(params);
}

// returns whether or not this number is even
function isEvenNumber(num){
	return ( num%2 == 0 );
}

// returns the size of this hash
function getHashSize(hsh){
	 var size = 0;
     for (var key in hsh) {
        if (hsh.hasOwnProperty(key)) ++size;
     }
     return size;
}

// returns whether or not this string is empty
function isEmptyString(str){
	return (str == null) || (str == undefined) || (typeof str == "string" && str == "");
}

// returns an array prefilled with numbers from the specified range, from startNum to endNum, in order
function fillArrayWithRange(startNum,endNum){
	var arr = new Array();
	for ( var i = startNum; i <= endNum; ++i ) {
		arr.push(i);
	}
	return arr;
}

// returns whether or not the given property exists as a function in this object
function isFunctionInObject(obj,funcName){
	return obj[funcName] && typeof obj[funcName] == 'function';
}

//Function which invokes the onclick/onchange events attached to the given DOM element
function invokeAttachedDOMEvent(elm,callback){
	var eventFunction = elm.attr('onchange') ? elm.attr('onchange') : elm.attr('onclick');
	if ( eventFunction ) {
		eventFunction.call();
	}
	if ( callback && typeof callback == 'function' ) callback.call();
}

// Function which updates the given DOM element as if it were being updated by a user interacting 
// with the client browser
function simulateBrowserUpdate(elmId,value,excludeDomEvent,domEventDelay,callbackFunc){
	// get the associated question ID
	var qId = getQuestionIDfromDOMID( elmId );
	
	// show the spinner for this question
	showSpinner('spinner_'+qId);
	
	var elm = jQuery('#'+elmId);
	var elmType = elm.attr('nodeName');
	// if the referenced element is a checkbox then update the "checked" value
	// (workaround for a bug in this version of JQuery to allow checkboxes to be checked)
	if ( jQuery('[id='+elmId+']:checkbox').length > 0 ){
		elm.attr('checked','checked');
	}
	else if (elmType && arrayContains(['span','div'],elmType.toLowerCase())){
		elm.attr('value',value);
	}
	else{
		elm.val(value);
	}
	
	// invoke the onclick/onchange events associated with this element, if applicable
	if ( ! excludeDomEvent ) {
		invokeAttachedDOMEvent(elm);
		//insert a brief delay
		simulatedSleep(domEventDelay ? domEventDelay : 'extrafast');
	}
	
	// invoke the callback function, if applicable
	if ( callbackFunc ) {
		return callbackFunc.call();
	}
	
	// hide the spinner for this question
	hideSpinner('spinner_'+qId);
}

// Case-insensitive version of the JQuery "contains" selector
jQuery.expr[':'].Contains = function(a, i, m) { 
  return jQuery(a).text().toUpperCase().indexOf(m[3].toUpperCase()) >= 0; 
};

// Utility function which will cause this element to blink once
function doBlink(selector){
	jQuery(selector).fadeTo(160,0,function(){jQuery(this).fadeTo(160,1)});
}

// Utility function which will overlay the screen with a mask
function overlayScreen(message){
	var overlaySelector = 'loadmask-msg div';
	if (!jQuery('html').isMasked()){
		jQuery('html').mask('<div class="loadmask-msg-messageContent"><span>' + message + '</span></div>');
	} else{
		jQuery('div.loadmask-msg-messageContent span').text(message);
	}
}

//Utility function which will remove a previously overlaid mask from the screen
function removeOverlayScreen(){
	var overlaySelector = 'loadmask-msg div'
	jQuery('html').unmask();
}

// Utility function which will determine the current overlay message, if any
function getOverlayMessage(){
	var elm = jQuery('div.loadmask-msg-messageContent span');
	return ( elm.length > 0 ? elm.html() : '' );
}

// Utility function to provide a sliding background animation effect
function addSlidingBackground(jQueryElm){
	var backgroundPosition = jQueryElm.css('background-position');
	jQueryElm.animate( {'background-position':'200px 0px'}, 800, 'linear', function(){
				jQueryElm.animate( {'background-position':'0px 0px'}, 800, 'linear' );
				addSlidingBackground(jQueryElm);
	});
}

//Utility function to stop a previously added sliding background animation effect
function removeSlidingBackground(jQueryElm){
	jQueryElm.stop();
}

// Utility function used to return the list of keys in a hash
function getKeys(hsh)
{
  var keys = [];
  for(var i in hsh) if (hsh.hasOwnProperty(i))
  {
    keys.push(i);
  }
  return keys;
}

// Utility function which "compacts" an array ( returns the array without any null elements )
function compactArray( arr ) {
	var compacted = arr.filter(function(elm){ return (elm != null);});  
	return compacted;
}

