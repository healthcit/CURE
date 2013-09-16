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
 * Script which handles table pagination in the Analytics application.
 * @author oawofolu
 **/

/**
 * Global Variables
 */
var NUM_TABLE_RECORDS_PER_PAGE = 10; // number of table rows displayed per page
var CURRENT_PAGINATOR_PAGE = 1; // current table page
var TABLE_PAGINATOR_ID = 'table_paginator_container'; // DOM ID of the element which will contain the paginator
var reportTableRowElements = null; // DOM elements representing the set of table rows to be paginated

/**
 * Adds a client-side paginator to the "reportTable" visualization
 */
function setUpReportTablePaginator() {
	
	var tableRows = getPaginateableTableRowElements();
	
	var totalNumberOfTableRows = tableRows.length;
	
	var totalNumberOfPages = Math.ceil(totalNumberOfTableRows/NUM_TABLE_RECORDS_PER_PAGE);
	
	if ( totalNumberOfTableRows > 0 )
	{
		initializeReportTablePaginatorContainer();
		jQuery('#' + TABLE_PAGINATOR_ID).paginate({
			count 		: totalNumberOfPages,
			start 		: 1,
			display     : 10,
			border					: true,
			border_color			: 'black',
			text_color  			: 'black',
			background_color    	: '#E6E5F3',	
			border_hover_color		: 'black',
			text_hover_color  		: '#000',
			background_hover_color	: '#fff', 
			images					: false,
			mouse					: 'press',
			onChange     			: function(page){ displayReportTableRowSubset(page); }
		});
	
	}
}

/**
 * Displays the appropriate subset of table rows based on the page number which was clicked
 **/
function displayReportTableRowSubset(pageNumber){
	var start = (pageNumber-1) * NUM_TABLE_RECORDS_PER_PAGE;
	var end = start + NUM_TABLE_RECORDS_PER_PAGE;
	getAllReportTableRowElements().hide();
	getPaginateableTableRowElements().slice( start, end ).show();
	jQuery("#table_container_content").show();
}

/**
 * Resets the table paginator
 */
function resetTablePaginator(rowsToDisplay){
	if ( rowsToDisplay ) {
		reportTableRowElements = rowsToDisplay;
	}
	setUpReportTablePaginator();
	displayReportTableRowSubset(1);
}

/**
 * Sets up the DOM element which will contain the paginator
 **/
function initializeReportTablePaginatorContainer(){
	removePaginator();
	jQuery('#page_container').append('<div id="' + TABLE_PAGINATOR_ID + '" class="container"></div>' );
}

/**
 * Returns the collection of DOM elements representing the table rows to be paginated
 */
function getPaginateableTableRowElements(){
	if ( !reportTableRowElements ) {
		reportTableRowElements = getAllReportTableRowElements();
	}	
	return reportTableRowElements;
}

/**
 * Removes the paginator
 **/
function removePaginator() {
	jQuery('#'+TABLE_PAGINATOR_ID).remove();
}



/**
 * Sets the DOM elements representing the content rows of the "reportsTable" visualization
 */
function setReportTableRowElementVar(elms){
	reportTableRowElements = elms;
}
