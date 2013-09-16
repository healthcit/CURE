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
/**
 * Proprietary code for Analytics
 * @author oawofolu
 */

// Constants
var ANNOTATED_TIME_LINE_CHART = '10';

//======================================================
// Overriden functions
//======================================================
function setUpSecondaryVisualizations(){
	var oHsh = {
		10: new google.visualization.AnnotatedTimeLine($("reports_div_10"))//annotated time line
	};
	return oHsh;
}

//Sets up the columns that will be appropriate for this report type.
function resetColumnsForVisualization(oReportType,dataTable,dataView){	
	var reportType = oReportType.toString();
	var newDataView = new google.visualization.DataView(dataView);
	if ( reportType == ANNOTATED_TIME_LINE_CHART ) {
		// 1. There must be at least 2 columns.
		if ( dataView.getNumberOfColumns() < 2 ) return;
		// 2. The first column must be a Date Column.
		else if ( dataTable.getColumnType(0) != 'date') return;
		// 3. The succeeding columns should be numeric columns.
		else {
			for (var index=1; index<dataView.getNumberOfColumns(); ++index){
				if ( dataView.getColumnType(index) != 'number' ) return;
			}			
			newDataView = new google.visualization.DataView(dataView);
			// sort the rows by date
			newDataView.setRows(newDataView.getSortedRows([0]));
		}
	}// END reportType = ANNOTATED_TIME_LINE_CHART
	
	return newDataView;
}

//Transforms the data as appropriate for this report type.
function transformDataForVisualization(oReportType,dataTable){	
	var reportType = oReportType.toString();
	var newDataView = new google.visualization.DataView(dataTable);
	if ( reportType == ANNOTATED_TIME_LINE_CHART ) {
		// It may be possible to transform this dataset
		// if it contains one Date column,
		// or one String column that may be converted to a Date column;
		// and at least one numeric column.
		// Otherwise, assume that it is not possible to transform this dataset.
		var dateColumn = null;
		var numericColumnIndices = new Array();
		var sortColumns = new Array();
		for ( var i = 0; i<dataTable.getNumberOfColumns(); ++i ){
			var dataType = dataTable.getColumnType(i);
			if ( dataType == 'number' ) {
				numericColumnIndices.push(i);
			}
			else if ( dataType == 'date' && dateColumn == null ) {
				dateColumn = i;
				sortColumns.push({column:i,desc:false});
			}
			else if ( dataType == 'string' && dateColumn == null ){
				//Convert the String column to Date if possible
				var canConvert = true;
				for (j=0; j<dataTable.getNumberOfRows(); ++j){
					var val = dataTable.getValue(j,i);
					if ( !Date.parse(val) ) {
						canConvert = false;
						break;
					}
				}
				
				if ( canConvert ) {
					var dateConverter = new DateConverter(i); 
					function oFunc(dataTable,rownum){ return dateConverter.convertToDate(dataTable,rownum);}
					dateColumn = {calc:oFunc, type:'date', label:dataTable.getColumnLabel(i)}; 
					sortColumns.push({column:i,desc:false});
				}
			}
		}
		
		var isDataTransformable = dateColumn && numericColumnIndices.length > 0;
		if ( isDataTransformable ) {			
			// Construct newDataView
			var columnArray = new Array();
			columnArray.push(dateColumn);
			for ( var x=0; x < numericColumnIndices.length; ++x) columnArray.push(numericColumnIndices[x]);
			newDataView.setColumns( columnArray );
			newDataView.setRows( dataTable.getSortedRows(sortColumns) );
		}
		else return;
	}	// END reportType = ANNOTATED_LINE_CHART
	return newDataView;
}

//returns a hash of mismatch errors associated with the visualizations
function setUpVisualizationMismatchErrors(){
	return { 
		10: [ "The independent variable must be a Date", "The dependent variables should be numeric" ]
	};
}

// Updates the section which displays the Saved Report's title
function updateSavedReportTitleSection(elmId){
	jQuery('#currentQueryName').remove();
	if ( savedReport ){
		var title = getSavedReportTitle( savedReport );
		if ( title ){
			var str = "<div id=\"currentQueryName\"><span class=\"value\">";
			str += title;
			str += "</span></div>";
			jQuery('#'+elmId).prepend(str);
		}
	}
}

//Populates the full list of saved report queries on the Welcome Screen
function populateSavedQueryListSection(){
	jQuery("#savedReportsDiv").empty();
	
	if ( !savedReportQueries ) generateAllReportQueries(displayReportCallBack);
	
	
}
//======================================================
//END Overriden functions
//======================================================



//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//Saving Report Templates
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

//Function which creates a JSONObject representing the user's current query
function buildReportQuery(title){	
	var report = new Object();
	
	// get the selected question checkboxes
	var selectedQuestions = jQuery('input[type=checkbox][id^=check_]:checked');
	
	// add all the relevant selected element metadata to the "selected" Hash
	selectedQuestions.each(function(){
		var qElmPrefix = 'check_';
		var qId = this.id.substring(qElmPrefix.length);
		var qMetaData = new Object();
		var questionAssociatedElements = jQuery('select[id$=_' + qId + '],input[type=checkbox][id$=_' + qId + ']:checked,input[type=text][id$=_' + qId + '],span[id=aggregation_' + qId + 'TextBox]');
		questionAssociatedElements.each(function(){
			var currId = this.id;
			qMetaData[currId] = jQuery('#' + currId).val() || jQuery('#' + currId).attr('value');
		});
		report[qId] = qMetaData;
	});
	
	return report; 
}

function validateName(e){
	var key = window.event ? e.keyCode : e.which;
	var keychar = String.fromCharCode(key);
	reg = /[a-zA-Z0-9_-]{1}/;
	return reg.test(keychar);
}

// Saves the current report query
function saveReportQuery(){
	var reportTitle = getSavedReportTitle(savedReport);
	var reportQuery = buildReportQuery(reportTitle);
	if ( !reportTitle ) reportTitle = "";
	
	// Set up the Report Title Form
	var htmlStr = "<form id=\"reportTitleForm\" style=\"display:none;\">";
	htmlStr += "<div id=\"reportTitleFormErrorWarnings\"></div>";
	htmlStr += "<label for=\"Title\">What is the Report Title?</label>";
	htmlStr += "<input type=\"text\" name=\"reportTitleFld\" id=\"reportTitleFld\" class=\"text ui-widget-content ui-corner-all\" onblur=\"updateReportTitleErrorWarnings();\" onKeyPress=\"return validateName(event);\" value=\"\"/>";
	htmlStr += ("<br/><input type=\"checkbox\" name=\"sharedQuery\" id=\"sharedQuery\"  onblur=\"updateReportTitleErrorWarnings();\"/> <span class=\"guidance\">Shared query</span>");
	htmlStr += (savedReport ? "<br/><input type=\"checkbox\" name=\"useCurrentReportTitleFld\" id=\"useCurrentReportTitleFld\" onclick=\"setReportTitleAsCurrent();\"/>" : "");
	htmlStr += (savedReport ? "<span class=\"guidance\">Update current report template</span>" : "");
	htmlStr += "</form>";
	jQuery('#reports_container').append(htmlStr);
	
	// Display the Report Title Form in a dialog box
	jQuery('#reportTitleForm').dialog({
		width: 350,title:"Save Or Update Query",modal:true,autoOpen:false,buttons:{
		"Save/Update" : function(){
			jQuery("#reportTitleFormErrorWarnings").empty();
			var title = jQuery("#reportTitleFld").val();
			var updateId = jQuery("#useCurrentReportTitleFld").is(":checked") ? savedReport : null;
			if ( ! isEmptyString(title) ){
				// Construct the JSONObject which will be saved to the database
				var newReportObject = {title:title,report:reportQuery,id:updateId};
				var sharedQueryControl = jQuery("#sharedQuery");
				if(sharedQueryControl){
					newReportObject.sharedQuestion = sharedQueryControl.is(':checked');
				}
				
				reportTemplateService.saveOrUpdateReportTemplate(
						JSON.stringify(newReportObject),{
							callback:saveTemplateCallback,
							errorHandler:displayGenericError,
							async:false
						});
			} else {
				jQuery("#reportTitleFormErrorWarnings").append("Title must not be empty.")
			}
		},
		"Cancel Save" : function(){
			jQuery("#reportTitleFormErrorWarnings").empty();
			jQuery("#reportTitleFld").val("");
			jQuery("#useCurrentReportTitleFld").attr("checked",false);
			jQuery("#reportTitleForm").dialog('close');
		}}
	});
	jQuery('#reportTitleForm').dialog('open');
}

function saveTemplateCallback(data){
	if ( data ){
		generateAllReportQueries();
		savedReport = data;
		updateSavedReportTitleSection('help_container_content');
		jAlert("Your report template has been saved.", "Save Successful");

		//clear all fields on the "Save Report" form
		jQuery('#reportTitleForm input').val("");
		jQuery('#reportTitleForm input[type=checkbox]').attr("checked",false);
		jQuery('#reportTitleForm').dialog('close');
	}
}

//Prepares to delete the report query
function prepareForSavedReportQueryDelete(id){	
	jConfirm('This will delete the query template. Are you sure you want to delete it?', 'Confirmation Dialog', function(confirmed) {
	    if ( confirmed )
	    {
	    	// Provide an overlay wait message
	    	overlayScreen('Preparing to delete...');
	    	
	    	// Proceed to delete
	    	executeDeleteSavedReport(id,true);
	    }
	});
}

// Deletes the report query
function executeDeleteSavedReport(id,deleteFlag){	
	if ( deleteFlag ) 
	{		
		// Remove any "delete" markers from the DOM
		// NOTE: The current version of the code no longer uses "delete" markers, so technically this should not be necessary.
		jQuery('#savedReportsDiv span.delete_setup_msg').remove();
	
		// Remove the overlay
		removeOverlayScreen();
		
		// Perform the deletion
    	reportTemplateService.deleteReportTemplate(id,{callback:deleteTemplateCallback,errorHandler:displayGenericError});
	}
}

function deleteTemplateCallback(data){
	if ( data ) {
		// Remove the report template from the savedReportQueries object
		delete savedReportQueries[data];
		
		// Remove the report template from the list of templates on the Home Page
		var reportElmId = 'savedReportQuery_' + data;
		var reportElm = jQuery('#' + reportElmId);
		if ( reportElm ) {
			reportElm.slideUp();
		}
		
		// Update the caption which displays the number of reports
		var captionElm = jQuery('#savedReportsDiv div.none');
		captionElm.html(createNumberOfReportsCaption());
		
		// Display an alert message
		jAlert("The report has been deleted.","Report Deleted");
	}
}


function checkIfReportTitleExistsCallback(data){
	var errorMessageBlk = jQuery("#reportTitleFormErrorWarnings");
	if ( errorMessageBlk ){
		errorMessageBlk.empty();
		if ( data ){
			var err = "WARNING: A report template with this title already exists. Saving will override the previously saved report. Are you sure you want to proceed?";
			errorMessageBlk.append(err);
			return true;
		}
	}
}

function listAllTemplatesCallback(data){
	var arr = eval(data);
	if ( arr && arr.length > 0 ){
		savedReportQueries = new Object();
		for ( var i = 0 ; i < arr.length; ++i ){
			var obj = arr[i];
			savedReportQueries[obj['id']] = obj;
		}
	}
}

function listAndDisplayAllTemplatesCallback(data){
	listAllTemplatesCallback(data);
	displayReportCallBack();
}

function displayGenericError(message,exception){
	var errorMsg = "<b>Sorry, an error occurred.</b>";
	jAlert(errorMsg,"Error Occurred");
}

function updateReportTitleErrorWarnings(){
	var reportTitleFld = jQuery("#reportTitleFld");
	var sharedQueryFld = jQuery("#sharedQuery");
	if ( reportTitleFld ){
		var title = reportTitleFld.val();
		var shared = sharedQueryFld.is(':checked');
		if ( !isEmptyString(title) ){
			reportTemplateService.checkIfReportTitleExists(
				title, shared,{
					callback:checkIfReportTitleExistsCallback,
					errorHandler:displayGenericError,
					async:false
				});
		}
	}
}

function setReportTitleAsCurrent(){
	if ( jQuery("#useCurrentReportTitleFld").is(":checked") ){
		var reportTitle = getSavedReportTitle(savedReport);	
		if ( ! isEmptyString(reportTitle) ) {
			jQuery("#reportTitleFld").val(reportTitle);
		}
	}
}

//Gets the full set of saved report queries from the database.
function generateAllReportQueries(queryCallback){
	reportTemplateService.getAllReportTemplates({
				callback:( typeof queryCallback === 'function' ? listAndDisplayAllTemplatesCallback : listAllTemplatesCallback ),
				errorHandler:displayGenericError,
				async:false
			});	
}	

//Displays list of saved reports
function displayReportCallBack(){
	var htmlStr = "";
	var ctr = 0;
	for ( var queryId in savedReportQueries ) {
		var isLastQuery = ( ctr == getHashSize(savedReportQueries)+1 );
		htmlStr += "<div id=\"savedReportQuery_" + queryId + "\" class=\"item"; 
		htmlStr += (isEvenNumber(ctr) ? " odd" : "") + (isLastQuery ? " last\"" : "\"") + ">";
		if(!savedReportQueries[queryId]["shared"]){
			htmlStr += "<span class=\"private\"></span>";
		} else {
			htmlStr += "<span class=\"non-private\"></span>";
		}
		htmlStr += "<span class=\"titleText\" onclick=\"generateSavedReport(" + queryId + ");\">";
		htmlStr += savedReportQueries[queryId]["title"] + "</span>";
		htmlStr += "<span class=\"delete\" title=\"Click to delete\" onclick=\"prepareForSavedReportQueryDelete(" + queryId + ");\">&nbsp;</span></div>";
		htmlStr += "<div class=\"tooltip\"><span class=\"content\">";
		var date = savedReportQueries[queryId]["timestamp"];
		if ( date && date.indexOf(' ') > -1 ) date = Date.parse(date.split(' ')[0]).toString('dddd, MMMM d, yyyy'); 
		htmlStr += "<b>Created on:&nbsp;&nbsp;&nbsp;</b><i>" + date + "</i><br/>";
		htmlStr += "<b>Created by:&nbsp;&nbsp;&nbsp;</b><i>"+savedReportQueries[queryId]["ownerName"]+"</i><br/>";
		htmlStr += "</span></div>";
		++ctr;
	}
	htmlStr = "<div class=\"item none\">" + createNumberOfReportsCaption(ctr) + "</div>" + htmlStr;
	
	jQuery("#savedReportsDiv").append(htmlStr);
}

function createNumberOfReportsCaption(){
	var ctr;
	if ( arguments.length > 0 ) ctr = arguments[0];
	else ctr = Object.keys(savedReportQueries).length;
	var htmlStr = 
		"<span>There " + ( parseInt(ctr) == 1 ? "is " : "are " ) 
		+ "currently <span class=\"number\">" 
		+ ( parseInt(ctr) > 0 ? ctr : "no" ) 
		+ "</span> saved report" 
		+ ( parseInt(ctr) == 1 ? "" : "s" )
	    + ".<br/><br/></span>";
	return htmlStr;
}

//Gets the report query associated with this report id
function getSavedReportQuery(reportId){
	if ( !savedReportQueries ) generateAllReportQueries();	
	var reportQuery = savedReportQueries[reportId.toString()]["report"];
	return reportQuery;
}

//Gets the title associated with this report id
function getSavedReportTitle(reportId) {
	if ( !savedReportQueries ) generateAllReportQueries(); 
	var title = reportId ? savedReportQueries[reportId.toString()]["title"] : null;
	return title;
}

//Generates this saved query
function generateSavedReport(reportId){	
	var reportElmId = 'savedReportQuery_' + reportId;
	
	// Indicate that a saved report was just loaded
	wasSavedOrWizardReportLoaded = true;
	
	// Apply "bounce" effect
	jQuery('#' + reportElmId).effect("bounce", { times:3 }, 300);
	
	// Insert a delay
	// simulatedSleep(500);

	// Show page overlay
	setTimeout(function(){overlayScreen('Preparing to load report. This may take up to a minute. Please wait...');},500);
	
	var waitMessages = jQuery('#savedReportsDiv span.setup_msg');
	if ( ! isReportTableReady ) {
		if ( waitMessages.length > 0 ){
			waitMessages.remove();
		}
		jQuery('#' + reportElmId).append('<span class="setup_msg spinner">&nbsp;</span>');
	}
	else {	
		waitMessages.remove();
		
		var reportQuery = getSavedReportQuery(reportId);
		
		oldSavedReport = savedReport;
		
		// Update "savedReport"
		savedReport = reportId;
		// show spinner 
		showSpinner('saved_reports_container_spinner');
		// hide the list of reports
		showOrHideById('savedReportsDiv',2);
		
		generateReportFromReportQueryObject( reportQuery );
		
	}
}

function generateReportFromReportQueryObject( reportQuery, isReportWizard ){
	if ( reportQuery ) {
		var qIds = getKeys(reportQuery);
		var deferredDOMElms = [] ; // array which will store the IDs of all DOM elements whose updates will be deferred till last
		// (some elements needed to be updated last because they have dependencies on other DOM elements)
		for ( var i = 0; i < qIds.length; ++i ) {
			var qId = qIds[i];
			var qMetaData = reportQuery[qId];
			
			// update the DOM element representing the question
			var qElmPrefix = 'check_';
			var qElmId = qElmPrefix + qId;
			var qElm = jQuery('#' + qElmId);
			jQuery('#'+qElmId).show();
			qElm.attr('checked','checked');
			
			// process the question selection
			var isFinalQuestion = ( i == qIds.length - 1 );
			updateDOM(qId, isFinalQuestion);
		
			//insert a delay
			simulatedSleep('fast');
			
			// update all other relevant DOM elements:
			
			// First update DOM elements that do not have any dependencies
			for ( var elmId in qMetaData ) {
				// then proceed
				var elm = jQuery('#'+elmId);
				jQuery('#'+elmId).show();
				
				var elmIdPrefix = elmId.split('_')[0] + '_';
				// Exclude the following elements from having their onchange events executed:
				// - seljoin_***
				// - aggregation_***
				var noDomEvent = arrayContains( [ 'seljoin_', 'aggregation_' ], elmIdPrefix );	
				simulateBrowserUpdate(elmId, qMetaData[elmId.toString()], noDomEvent, 'extrafast');
				if ( noDomEvent ) deferredDOMElms.push( elmId );
				
				// Hide the following elements after they have been made visible:
				// - seljoinother_***
				var elmVisibility = ! arrayContains( ['seljoinother_'], elmIdPrefix );
				jQuery('#'+elmId).toggle(elmVisibility);
			}	
		}
		
		// Now, update the DOM elements that were deferred
		for ( var i = 0; i < deferredDOMElms.length; ++i ){
			var elmId = deferredDOMElms[ i ];
			var qId = getQuestionIDfromDOMID( elmId );
			qMetaData = reportQuery[ qId ];
							
			// ... handle any special cases ...//
			if ( elmId.match(/^seljoin_/) ) {
				var joinQId = qMetaData['seljoinother_' + qId];
				if ( !isEmptyString(joinQId) ) {
					alternateSimulateBrowserUpdate(elmId, qId, joinQId);
				}
			}
			else if ( elmId.match(/^aggregation_/) ){
				var aggregation = qMetaData['aggregation_' + qId];
				alternateSimulateBrowserUpdate(elmId, qId, aggregation);
			}
			
			// ... handle any other special cases here ... //
			
			// handle all other cases //
			else {
				simulateBrowserUpdate(elmId, qMetaData[elmId.toString()] );
			}
		}
		
		// Now that the DOM has been prepared, proceed to generate the report
		overlayScreen('Loading report. Please wait...');
		var errorMsg = generateReportsSection();
		
		if ( errorMsg ) {
			if (!isReportWizard ){
				// update "savedReport"
				savedReport = oldSavedReport;
				jAlert("<b><i>Sorry, this query is invalid. Please check your query criteria and try again.</i></b><br/><br/>" + errorMsg,"Invalid Query");
				window.location.reload();
			}
			else {
				jAlert("<b><i>Sorry, this query is invalid. Please check your query criteria and try again.</i></b><br/><br/>" + errorMsg,"Invalid Query");
				removeOverlayScreen();
			}
		}
	} else{
		if ( !isReportWizard ) {
			// update "savedReport"
			savedReport = oldSavedReport;
			jAlert("<b><i>Sorry, could not find the query.</i></b>");
		}
	}	
}

// Clears out the global variables which reference information about the currently loaded saved report
function resetCurrentSavedReport() {
	oldSavedReport = null;
	savedReport = null;
	updateSavedReportTitleSection();
}

// Returns the value of the currently loaded saved report, if any
function getCurrentSavedReport() {
	return savedReport;
}

//Constructs the warning message displayed to the user upon clicking the CLEAR button
// (Overrides function in main.js)
function getClearReportsSectionWarningMessage() {
	var confirmMsg = "Are you sure you want to clear the current query?";
	if ( savedReport ) confirmMsg += "<br/><br/>(Your query template will still be saved.)";
	return confirmMsg;
}

// If a saved report was previously selected but could not be executed because the DOM was not ready,
// this function will determine what the previous selection is and generate/delete the report.
function performActionOnSelectedSavedReport(){
	var waitMessages = jQuery('#savedReportsDiv span.setup_msg,#savedReportsDiv span.delete_setup_msg');
	if ( waitMessages.length > 0 ) {
		var clickedReportSelector = waitMessages.attr('class').match(/delete/) ? 'span.delete' : 'span.titleText';
		var clickedReport = waitMessages.parent().find(clickedReportSelector);
		clickedReport.attr('onclick').call();
	}
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//END Saving Report Templates
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// Update the DOM with this question's corresponding DOM elements
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
function updateDOM(qId, attachValidationCallback){
	// update the DOM
	addAnswersBlock(qId);
}

//======================================================
//Utility methods...
//======================================================
function DateConverter(i){
	this.columnIndex = i;
	this.convertToDate = function(dataTable,rownum){
		var val = dataTable.getValue(rownum,this.columnIndex);
		return Date.parse(val);
	}
}
//======================================================
//END Utility methods
//======================================================

//======================================================
//Onload functions
//======================================================
jQuery(document).ready(function(){
	if ( savedReportQueries ) {
		for ( var id in savedReportQueries ){
			jQuery("#savedReportQuery_"+id).tooltip({ effect: 'slide'});
		}
	}
});
//======================================================
//END Onload functions
//======================================================
