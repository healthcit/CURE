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
var DEFAULT_DATE_REGEX = /([0-9]?[0-9])\/([0-9]?[0-9])\/([0-9]?[0-9]?[0-9][0-9])/; //MM/dd/yyyy

/*======================================================= */
// Custom DATE filter column control
// To be added to the main FilterDataTableControl from Google
// By Tola Awofolu
/*======================================================= */
com.healthcit.SimpleDateFilterColumnControl = Class.create({
	initialize: function(container) {
	    this.containerElement = container;
	},
	
	draw : function(data, options) {
	    var simpleOperatorLabels = { gt: "Later than", ge: "Later than or equal to", eq: "Equal to", ne: "Not Equal to", lt: "Earlier than", le: "Earlier than or equal to", btw: "Between" };
	    if (options.inheritedOptions && options.inheritedOptions.simpleOperatorLabels) {
	        simpleOperatorLabels = options.inheritedOptions.simpleOperatorLabels;
	    }
	
	    var columnIndex = options.assignedColumnIndex;
	    this.selectionElement = "filter_item_" + columnIndex;
	    this.textInputElement = "filter_item_value_" + columnIndex;
	    this.firstCompareTextInputElement = "filter_item_first_compare_value_" + columnIndex;
	    this.secondCompareTextInputElement = "filter_item_second_compare_value_" + columnIndex;
	    this.textInputElementContainer = "filter_item_value_" + columnIndex + "_container";
	    this.compareTextInputElementContainer = "filter_item_compare_value_" + columnIndex + "_container";
	
	    var html = "Operator<br/>";
	    html += "<select id='" + this.selectionElement + "'>";
	    html += "<option></option>";
	    html += "<option value='GT'  onclick=\"toggleFilterTextFields(['" + this.textInputElementContainer + "'],['" + this.compareTextInputElementContainer + "']);\">" + simpleOperatorLabels.gt + "</option>";
	    html += "<option value='GE'  onclick=\"toggleFilterTextFields(['" + this.textInputElementContainer + "'],['" + this.compareTextInputElementContainer + "']);\">" + simpleOperatorLabels.ge + "</option>";
	    html += "<option value='EQ'  onclick=\"toggleFilterTextFields(['" + this.textInputElementContainer + "'],['" + this.compareTextInputElementContainer + "']);\">" + simpleOperatorLabels.eq + "</option>";
	    html += "<option value='NE'  onclick=\"toggleFilterTextFields(['" + this.textInputElementContainer + "'],['" + this.compareTextInputElementContainer + "']);\">" + simpleOperatorLabels.ne + "</option>";
	    html += "<option value='LT'  onclick=\"toggleFilterTextFields(['" + this.textInputElementContainer + "'],['" + this.compareTextInputElementContainer + "']);\">" + simpleOperatorLabels.lt + "</option>";
	    html += "<option value='LE'  onclick=\"toggleFilterTextFields(['" + this.textInputElementContainer + "'],['" + this.compareTextInputElementContainer + "']);\">" + simpleOperatorLabels.le + "</option>";
	    html += "<option value='BTW' onclick=\"toggleFilterTextFields(['" + this.compareTextInputElementContainer + "'],['" + this.textInputElementContainer + "']);\">" + simpleOperatorLabels.btw + "</option>";
	    html += "</select>";
	    html += "<span id='" + this.textInputElementContainer + "'><br/>Compare Value<br/><input type='text' id='" + this.textInputElement + "'><span class=\"dateformat\"><br/>*MM/DD/YYYY</span></span>";
	    html += "<span id='" + this.compareTextInputElementContainer + "' style='display:none;'><br/>Between<br/><input type='text' id='" + this.firstCompareTextInputElement + "'><span class=\"dateformat\"><br/>*MM/DD/YYYY</span><br/>And<br/><input type='text' id='" + this.secondCompareTextInputElement + "'><span class=\"dateformat\"><br/>*MM/DD/YYYY</span></span>";
	
	    this.containerElement.innerHTML = html;
	},
	
	isActive: function() {
	    return (($(this.textInputElement).value || ($(this.firstCompareTextInputElement).value && $(this.secondCompareTextInputElement).value) ) && $(this.selectionElement).selectedIndex);
	},
	
	passes: function(value) {
	    if (this.isActive()) {
	        var compareInput = $(this.textInputElement);
	        var rangeInputA = $(this.firstCompareTextInputElement);
	        var rangeInputB = $(this.secondCompareTextInputElement);
	        var selectOperation = $(this.selectionElement);
	        if ( (compareInput.value || (rangeInputA.value && rangeInputB.value)) && selectOperation.selectedIndex) {
	            var selectedOption = selectOperation.options[selectOperation.selectedIndex];
	            if (selectedOption && selectedOption.value) {
	            	if ( selectedOption.value == 'BTW' )
	            		return this.compare(value, selectedOption.value, this.convertStringToDate(rangeInputA.value), this.convertStringToDate(rangeInputB.value));
	            	else
	            		return this.compare(value, selectedOption.value, this.convertStringToDate(compareInput.value));
	            }
	        }
	    }
	    return true;
	},
	
	resetFilter: function() {
	    var selectOperation = $(this.selectionElement);
	    for (var i = 0; i < selectOperation.options.length; i++) {
	        selectOperation.options[i].selected = false;
	    }
	    $(this.textInputElement).value = "";
	    $(this.firstCompareTextInputElement).value = "";
	    $(this.secondCompareTextInputElement).value = "";
	},
	
	compare: function(value, operator, filterValue, secondFilterValue) {
		switch (operator) {
	        case "GT" : return value > filterValue;
	        case "GE": return value >= filterValue;
	        case "EQ": return value == filterValue;
	        case "NE": return value != filterValue;
	        case "LT" : return value < filterValue;
	        case "LE": return value <= filterValue;
	        case "BTW": return ( secondFilterValue > filterValue ? 
	        		             value >= filterValue && value <= secondFilterValue :
	        		             value >= secondFilterValue && value <= filterValue );
	    }
	    return true;
	},
	
	convertStringToDate: function( dateStr ) {
		return this.convertStringToDateWithFormat( dateStr, DEFAULT_DATE_REGEX );
	},
	
	convertStringToDateWithFormat: function( dateStr, dateFormat ) {
		if ( dateStr ) {
			var dateComponents = dateStr.match( dateFormat );
			var month = ( dateComponents.length > 1 ? parseInt(dateComponents[1]) - 1 : null);
			var day   = ( dateComponents.length > 2 ? parseInt(dateComponents[2])     : null);
			var year  = ( dateComponents.length > 3 ? parseInt(dateComponents[3])     : null);
			if ( month!=null && day!=null && year!=null ) {
				var newDate = new Date();
				newDate.setMonth( month );
				newDate.setDate( day );
				newDate.setYear( year );
				return newDate;
			}
		}
	}
});

function toggleFilterTextFields( showFieldArray, hideFieldArray ){
	for ( var i=0; i < showFieldArray.length; ++i ) {
		var elmId = showFieldArray[i];
		$(elmId).show();
	}
	for ( var i=0; i < hideFieldArray.length; ++i ) {
			var elmId = hideFieldArray[i];
			$(elmId).getElementsBySelector('input[type=text]').each(function(elm){elm.value='';});
			$(elmId).hide();
	}
}

/*====================================================================*/
// Add this filter column control to the main FilterDataTableControl widget
/*==================================================================== */
org.systemsbiology.visualization.FilterDataTableControl.addMethods({
	getFilterColumnControlInstance: function(columnIndex, columnType, container, options) {
	    // TODO: Load impls from options and columnIndex
	    switch (columnType) {
	        case "string":
	            return new org.systemsbiology.visualization.SelectDistinctValuesStringFilterColumnControl(container);
	        case "number":
	            return new org.systemsbiology.visualization.SimpleOperatorNumberFilterColumnControl(container);
	        case "boolean":
	            return new org.systemsbiology.visualization.SimpleChoiceBooleanFilterColumnControl(container);
	        // ADDED custom Filter Control for dates - TOLA 04/15/2011
	        case "date":
	        	return new com.healthcit.SimpleDateFilterColumnControl(container);
	    }
	    return null;
	}
});
