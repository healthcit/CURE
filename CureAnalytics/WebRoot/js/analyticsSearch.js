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
 * Code which allows a user to search for specific fields within the listing of questions
 **/
// Global Variables
var globalTableContentId = null;
var globalTableContentParentId = null;
var searchModeExecuted = false;

//prepare to show the search form
function showSearchBoxes(tableContentId,tableContentParentId){
	// Set global variables
	globalTableContentId = tableContentId;
	globalTableContentParentId = tableContentParentId;
	
	// Redirect to the "Create Reports" screen if necessary
	var isHomePageLoaded = jQuery('#table_container').is(':visible');
	if ( !isHomePageLoaded ) {
		overlayScreen('Redirecting to the "Create Reports" screen. Please wait...');
		navigateToCreateReports();
		simulatedSleep(500);
		removeOverlayScreen(); 
		openSearchForm();
	}	
	else { //put search input and dropdown on page
		openSearchForm();
	}
}

// opens the search form
function openSearchForm(){
	var searchFormElm = jQuery('#searchQuestionForm');
	if( searchFormElm.length == 0 ) setUpSearchForm();
	else searchFormElm.dialog('open');
}

// generate the search form
function setUpSearchForm(){
	var htmlStr = "<form id=\"searchQuestionForm\" style=\"display:none;\">";
	htmlStr += "Search for the following text: <input type=\"text\" name=\"searchQuestionFld\" id=\"searchQuestionFld\" class=\"text ui-widget-content ui-corner-all search_column\" value=\"\"/>";
	htmlStr += "</form>";
	jQuery("#" + globalTableContentParentId).before(htmlStr);
	var inputFld = jQuery('input#searchQuestionFld');
	
	// set up autocomplete
	var list = getFullListOfMatches(globalTableContentId);
	inputFld.autocomplete({source:list,delay:500});
	
	// set up search form
	jQuery('#searchQuestionForm').dialog({
		width: 350,title:"Search Questions",modal:false,autoOpen:false,buttons:{
		"Search" : function(){
			resetTable();
			inputFld.val('');
			jQuery("#searchQuestionForm").dialog('close');
		},
		"Clear Results" : function(){
			clearTableSearch();
		},
		"Close" : function(){
			inputFld.val('');
			jQuery("#searchQuestionForm").dialog('close');
		}}
	});
		
	jQuery('#searchQuestionForm').dialog('open');
}

// reset the table with the questions that match the given text
function resetTable(){	
	var text = jQuery('input#searchQuestionFld').val();
	var tableContentElms = getAllReportTableRowElements(); // the content of the table
	var totalNumRows = tableContentElms.length; // total number of rows on the current page of the table
	var useSearchToken = ( text && text.trim() != '' );
		
	if ( useSearchToken )
	{		
		tableContentElms.hide();
		
		var displayElms = jQuery("#" + globalTableContentId + ' table tbody tr:Contains('+text+')');
		
		tableContentElms = displayElms;
		
		displaySearchResults(tableContentElms);
		
		searchModeExecuted = true;
	}
	
	else
	{
		displayReportTableFirstPage();
		
		searchModeExecuted = false;
	}
	
	updateSearchStatus( text );
}

// displays search results
function displaySearchResults(tableContentElms){
	resetTablePaginator(tableContentElms);
	
	// If no search results were displayed then display "No search results found"
	if ( jQuery('#table_container_message').length == 0 ) { 
		jQuery('#table_container_content').append('<div id="table_container_message" style="display:none">No search results found.</div>');
	}
	showOrHideById( 'table_container_message', (tableContentElms.length == 0 ? 1 : 2) );
}

// returns whether or not the questions displayed on the "Create Reports" page are the result of a search
function isInSearchMode() {
	return searchModeExecuted;
}

// clear search criteria
function clearTableSearch(){
	jQuery('input#searchQuestionFld').val('');
	setReportTableRowElementVar(getAllReportTableRowElements());
	resetTable();
	jQuery("#searchQuestionForm").dialog('close');
}

// get the full list of possible matches
function getFullListOfMatches(tableContentId){
	var arr = jQuery("#" + tableContentId + ' table td').contents().map(function(){return this.textContent;})
	return jQuery.grep(arr,function(a){return a.trim()!=""});
}

// update "Showing search results for abc"
function updateSearchStatus(searchToken) {
	var searchTokenMsg = searchToken ?
			    'Showing search results for:<br/><b><span class=\'searchTokenFld\' title=\'' + searchToken + '\'>' + (searchToken.length > 20 ? searchToken.substring(0,20)+'&hellip;' : searchToken) + '<br/></span></b>' :
				'';
	jQuery('#searchFormStatusFld').html( searchTokenMsg );
	var searchLabel = searchToken ? '<span class="current">Search again</span>' : 'Search for question';
	jQuery('span.searchLabel').html( searchLabel );
}
