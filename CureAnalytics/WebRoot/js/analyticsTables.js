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
 * Code which handles incorporating table questions into the Analytics app.
 * @oawofolu
 **/
/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
/**
 * Global variables
 **/
/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
// Constants
var FORMTABLE_MAPPING_VIEW_TABLETYPE_INDEX = 0;
var FORMTABLE_MAPPING_VIEW_TABLEID_INDEX = 1;
var FORMTABLE_MAPPING_VIEW_SHORTNAME_INDEX = 2;
var FORMTABLE_MAPPING_VIEW_IDENTIFYING_INDEX = 3;

/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
/**
 * Google Query functions
 **/
/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
/**
 * Function which processes Form Table metadata retrieved from the CouchDB datasource
 */
function handleFormTableQueryResponse( response ) {
	if (response.isError()) {
	  jAlert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	  return;
    }

	var data  = response.getDataTable();
	
	// Construct the FormTable metadata object
	for ( var i = 0; i < data.getNumberOfRows(); ++i ) {
		var key = data.getValue( i, 0 );
		var value = new Array();
		
		// If the key does not already exist in the hash, then generate a new key-value pair
		if ( !formTableMappings[ key ] ) formTableMappings[ key ] = new Array();
		
		for ( var j = 1; j <= data.getNumberOfColumns(); ++j ) {
			if ( j == data.getNumberOfColumns() ) {
				formTableMappings[ key ].push( value );
			}
			else {
				var fld = data.getValue( i, j );
				value.push( fld );
			}
		}
	}
}

/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
/**
 * DOM-update functions
 **/
/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

/**
 * Generates the HTML for the section of the screen which handles table-context related information
 */
function getFormTableHTMLSection( qId ){
	var htmlStr = '';
	
	if ( isAssociatedWithBothTableAndNonTableData( qId ) ) {
		htmlStr += '<div class="questionTypeSelectChild5" id="tableDiv_' + qId +'">';
		htmlStr += '<span>Tables:&nbsp;</span><select class="tabledata_select" name="tabledata_' + qId +'" id="tabledata_' + qId +'" onchange="updateDataType(\'' + qId + '\',\'string\')"><option value="table">Include table data only</option><option value="nontable">Include non-table data only</option><option value="both">Include both table and non-table data</option></select>'
		htmlStr += '</div>';
	}
	
	return htmlStr;
}

/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
/**
 * Validation functions
 **/
/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

/**
 * Validates the current query as it relates to its associated FormTable selections;
 * also, if it was invoked as the result of selecting a question, then it repairs the validation error by updating the DOM as appropriate.
 */
function validateTableContexts(qId){
	var errorMsgStr = '';
	var qIdCheckboxPrefix = qId ? 'check_' + qId : null;
	var qIdElm = qIdCheckboxPrefix ? jQuery('#'+qIdCheckboxPrefix) : null;
	var canRepairError = !!qIdElm; // flag indicating whether or not validation errors should be repaired by automatically updating the DOM
	
	var existsTableContext = existsTableContextInCurrentQuery();
	var canValidateTableQuestion = ( isComplexFormTableQuestion(qId)  && !isIdentifyingColumn(qId) ) ||
								   ( isSimpleFormTableQuestion(qId) && isIdentifyingColumn(qId) );
	var canDoValidation = ( qId ? canValidateTableQuestion : true );
	if ( existsTableContext && canDoValidation ){
		var allSelectedQuestions = getAllSelectedQuestions();
		var tableIds = getTableIdsForQuestions( allSelectedQuestions );
		
		// 1) Validate that there is only one table context associated with the current query
		if ( compactArray( tableIds ).unique().length > 1 ) {
			errorMsgStr += '-Selected questions must not come from different Form Tables.<br/>';
			if ( canRepairError ) {
				qIdElm.attr('checked',false);
				return errorMsgStr;
			}
		}
		
		else {
			var tableJoinContexts = new Array();
			var isIndependentOrFilterTableQuestionSelected = false;
			var identifyingColumn = null;
			var isSelectedIdentifyingColumnAsIndFilt = null;
			var isSelectedIdentifyingColumnAsDep = null;
			var identifyingColumnShortName = null;
			var tableShortName = compactArray(getTableShortNamesForQuestions( allSelectedQuestions ))[ 0 ];
			var isComplexTbl = isComplexFormTableQuestion(qId);
			var isSimpleTbl = isSimpleFormTableQuestion(qId);
			
			for ( var i = 0; i < allSelectedQuestions.length; ++i ) {
				var currentQId = allSelectedQuestions[ i ];
				var currentHasTableContext = hasTableContext( currentQId );
				
				if ( !identifyingColumn ) {
					identifyingColumn = getAssociatedIdentifyingColumn( currentQId );
					isSelectedIdentifyingColumnAsIndFilt = ( identifyingColumn &&
												  arrayContains( allSelectedQuestions, identifyingColumn ) &&
				                                  (!isDependentVariable( identifyingColumn )));
				    isSelectedIdentifyingColumnAsDep     = ( identifyingColumn &&
												  arrayContains( allSelectedQuestions, identifyingColumn ) &&
					                              (isDependentVariable( identifyingColumn )));
				}
				
				// 1) Validate that all the dependent variables have table context
				if ( isDependentVariable( currentQId ) && !currentHasTableContext ) {
					errorMsgStr += '-<b>' + getQuestionShortName( currentQId ) + '</b> cannot be selected as a dependent variable because it is not associated with the Form Table <b>' + tableShortName + '</b>.<br/>';
				}
				
				// 2) Validate that when the table context is associated with a complex table that has an "identifying column",
				// the identifying column has been selected as an independent/filter variable				
				if ( identifyingColumn && !isSelectedIdentifyingColumnAsIndFilt && currentHasTableContext && isComplexTbl ) {
					if ( canRepairError ) {
						// Proceed to make the identifying column an independent variable
						isSelectedIdentifyingColumnAsIndFilt = true;
						var identifyingColumnElmId = 'check_' + identifyingColumn;
						jQuery('#' + identifyingColumnElmId ).attr( 'checked' , true );
						simulateBrowserUpdate( identifyingColumnElmId ); //Default selection should be "independent"
					}
					else {
						identifyingColumnShortName = getQuestionShortName( identifyingColumn );
						var error = '-When questions from the Form Table <b>' + tableShortName + '</b> have been selected, the identifying column <b>' + identifyingColumnShortName + '</b> must be selected as an independent or filter variable.<br/>';
						if ( errorMsgStr.indexOf( error ) == -1 ) errorMsgStr += error;
					}
				}
								
				// 3) Validate that when the leading column of a single table is selected,
				// the associated "_Answers" column has been selected as a dependent variable
				if ( identifyingColumn && isSelectedIdentifyingColumnAsIndFilt && currentHasTableContext && isSimpleTbl ) {
					if ( canRepairError ) {
						// Proceed to make the associated "_Answers" column a dependent variable
						var nonIdentifyingColumn = getSimpleTableNonIdentifyingQuestionId( identifyingColumn );
						var nonIdentifyingColumnElmId = 'check_' +  nonIdentifyingColumn;
						var nonIdentifyingColumnVarTypeElmId = 'select_' + nonIdentifyingColumn;
						jQuery('#' + nonIdentifyingColumnElmId ).attr( 'checked' , true );
						simulateBrowserUpdate( nonIdentifyingColumnElmId, null, null, null, function(){
							jQuery('#' + nonIdentifyingColumnVarTypeElmId ).val('dependent');
							simulateBrowserUpdate( nonIdentifyingColumnVarTypeElmId );
						} );
					}
					else {
						// else, do nothing. This may be an unlikely query, but it should still be permissible.
					}
				}
			
				// 4) Validate that all the questions with table context have the same join context
				if ( currentHasTableContext ) {
					var currentJoinContext = getQuestionSelectedJoinContext( currentQId );
					tableJoinContexts.push( currentJoinContext );
				}
				if ( i == allSelectedQuestions.length - 1 && compactArray(tableJoinContexts).unique().length > 1 ) {
					errorMsgStr += '-Questions that come from a Form Table should have the same <b>Group By</b> context.<br/>';
				}
								
				// 5) Validate that at least 1 independent/filter variable has table context
				if ( (isIndependentVariable( currentQId ) || isFilterVariable( currentQId )) && currentHasTableContext ) {
					isIndependentOrFilterTableQuestionSelected = true;
				}
				if ( i == allSelectedQuestions.length - 1 && !isIndependentOrFilterTableQuestionSelected && !isSelectedIdentifyingColumnAsIndFilt )
				{
					errorMsgStr += '-At least <b>1</b> independent/filter variable should come from a Form Table when other Form Table questions have been selected.<br/>';
				}
				
			}
		}	
	}
	return errorMsgStr;
}


/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
/**
 * Utility functions
 **/
/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

/**
 * Function which retrieves FormTable metadata about a question field, if it exists
 */
function getFormTableMetaData( qid ) {
	var qIdMetaData = formTableMappings[ qid ];
	
	if ( qIdMetaData ) {
		for ( var i = 0; i < qIdMetaData.length; ++i ) {
			var tableType = '' + qIdMetaData[i][FORMTABLE_MAPPING_VIEW_TABLETYPE_INDEX];
			var tableShortName = '' + qIdMetaData[i][FORMTABLE_MAPPING_VIEW_SHORTNAME_INDEX];
			var isEmptyVal = arrayContains( ['null','undefined',''], tableType ) || 
							 arrayContains( ['null','undefined',''], tableShortName );
			if ( !isEmptyVal ) return qIdMetaData[i];
		}
	}
	
	return [ 'null', 'null', 'null', 'null' ];
}

/**
 * Function which determines if a question field has associated data that is not associated with a table question from the Collector
 */
function isAssociatedWithNonFormTableData( qId ) {
	var qIdMetaData = formTableMappings[ qId ];
	
	if ( qIdMetaData ) {
		for ( var i = 0; i < qIdMetaData.length; ++i ) {
			var tableType = '' + qIdMetaData[i][FORMTABLE_MAPPING_VIEW_TABLETYPE_INDEX];
			var isEmptyVal = arrayContains( ['null','undefined',''], tableType );
			if ( isEmptyVal ) return true;
		}
	}
	
	return false;  
}

/**
 * Function which determines whether this question field comes from a table question in the Collector
 */
function isFormTableQuestion(qId){
	if ( !qId ) return false;
	var formTableMetaData = getFormTableMetaData( qId );
	var stringified = '' + formTableMetaData[ FORMTABLE_MAPPING_VIEW_TABLEID_INDEX ];
	return !arrayContains( ['null','undefined',''], stringified );
}

/**
 * Returns the Collector table ID associated with this question, if any
 */
function getFormTableQuestionId(qId){
	var formTableMetaData = getFormTableMetaData( qId );
	for ( var i = 0; i < formTableMetaData.length; ++i ) {
		var val = formTableMetaData[ FORMTABLE_MAPPING_VIEW_TABLEID_INDEX ];
		if ( !!val ) return (val == 'null' ? null : val);
	}
	return null;
}

/**
 * Returns the Collector table short name associated with this question, if any
 */
function getFormTableShortName(qId) {
	var formTableMetaData = getFormTableMetaData( qId );
	for ( var i = 0; i < formTableMetaData.length; ++i ) {
		var val = formTableMetaData[ FORMTABLE_MAPPING_VIEW_SHORTNAME_INDEX ];
		if ( !!val ) return (val == 'null' ? null : val);
	}
	return null;
}

/**
 * Returns the associated identifying column for this question, if any
 */
function getAssociatedIdentifyingColumn(qId) {
	var formTableMetaData = getFormTableMetaData( qId );
	for ( var i = 0; i < formTableMetaData.length; ++i ) {
		var val = formTableMetaData[ FORMTABLE_MAPPING_VIEW_IDENTIFYING_INDEX ];
		if ( !!val ) return (val == 'null' ? null : val);
	}
	return null;
}

/**
 * Returns the associated Collector table type for this question (simple/complex), if any
 */
function getFormTableType(qId) {
	var formTableMetaData = getFormTableMetaData( qId );
	for ( var i = 0; i < formTableMetaData.length; ++i ) {
		var val = formTableMetaData[ FORMTABLE_MAPPING_VIEW_TABLETYPE_INDEX ];
		if ( !!val ) return (val == 'null' ? null : val);
	}	
	return null;
}

/**
 * Returns whether this question is an "identifying column" in a Collector table question
 */
function isIdentifyingColumn(qId) {
	if ( !qId ) return false;
	var identifyingColumn = getAssociatedIdentifyingColumn(qId);
	return ( identifyingColumn == qId );
}


/**
 * Returns whether this question is from a "Simple" table
 */
function isSimpleFormTableQuestion(qId){
	var formTableType = getFormTableType( qId );
	return ( formTableType == 'SIMPLE' );
}

/**
 * Returns whether this question is from a "Complex" table 
 */
function isComplexFormTableQuestion(qId){
	var formTableType = getFormTableType( qId );
	return ( formTableType == 'COMPLEX' );
}

/**
 * Returns the Google API join method to use for joining form table questions
 */
function getJoinMethodForFormTableQuestions(){
	if ( hasTableContext( baseVarArray[ 0 ] ) ){
		return 'left';
	}
	else{
		return 'right';
	}
}

/**
 * Returns whether this question should be processed as a Form Table question or not
 **/
function hasTableContext( qId ) {
	if ( !qId ) return false;
	
	// if the question is not associated with any form data, then return false
	if ( !isFormTableQuestion( qId ) ) return false;
	
	// else, if the question is associated with both form data and non-form data,
	// return true if the appropriate field(s) in the UI have been selected
	if ( isAssociatedWithNonFormTableData( qId ) ) {
		return jQuery('#tabledata_'+qId).val() != getNonTableDataOnlyContextType();
	}
	
	// else, return true
	return true;
}

/**
 * Returns this question's table context type selection
 */
function getFormTableContextTypeForQuestion( qId ) {
	return jQuery('#tabledata_'+qId).val();
}

/**
 * Returns the table context type selection which matches only data from questions that come from FormTables 
 */
function getTableDataOnlyContextType() {
	return 'table';
}

/**
 * Returns the table context type selection which matches only data from questions that do NOT come from FormTables 
 */
function getNonTableDataOnlyContextType() {
	return 'nontable';
}

/**
 * Returns the table context type selection which matches both data that comes from FormTables and data that does not
 */
function getTableAndNonTableContextType() {
	return 'both';
}

/**
 * Returns whether this question field is associated with both FormTable and non-FormTable data
 */
function isAssociatedWithBothTableAndNonTableData( qId ) {
	return isFormTableQuestion( qId ) && isAssociatedWithNonFormTableData( qId );
}

/**
 * Returns the opposite of "hasTableContext"
 **/
function doesNotHaveTableContext( qId ) {
	//TODO: Should depend on the user's selection in the UI
	return !hasTableContext( qId );
}

/**
 * Returns whether the current query is associated with any questions that have table context
 */
function existsTableContextInCurrentQuery(){
	var allSelectedQuestions = getAllSelectedQuestions();
	for ( var i = 0; i < allSelectedQuestions.length; ++i ) {
		if ( hasTableContext( allSelectedQuestions[ i ] ) ) return true;
	}
	return false;
}

/**
 * Returns the FormTable IDs associated with the current query
 */
function getTableContextsInCurrentQuery() {
	return getTableIdsForQuestions( getAllSelectedQuestions() );
}

/**
 * Returns the FormTable IDs associated with these questions
 */
function getTableIdsForQuestions( qIds ) {
	var tableContexts = new Array();
	for ( var i = 0; i < qIds.length; ++i ) {
		if ( isFormTableQuestion( qIds[ i ] ) ) tableContexts.push( getFormTableQuestionId(qIds[ i ]) );
		else tableContexts.push( null );
	}
	return tableContexts;
}

/**
 * Returns the FormTable ShortNames associated with these questions
 */
function getTableShortNamesForQuestions( qIds ) {
	var tableContexts = new Array();
	for ( var i = 0; i < qIds.length; ++i ) {
		if ( isFormTableQuestion( qIds[ i ] ) ) tableContexts.push( getFormTableShortName(qIds[ i ]) );
		else tableContexts.push( null );
	}
	return tableContexts;
}

/**
 * Returns which of the 2 questions, if any, is associated with a Form Table
 */
function findQuestionWithTableContext(qId1,qId2){
	return ( hasTableContext(qId1) ? qId1 : ( hasTableContext(qId2) ? qId2 : null ) );
}

/**
 * Returns whether the provided DOM ID matches the format of the question ID of non-leading columns from SIMPLE FormTables
 */
function matchesNonIdentifyingIdFromSimpleTable( domId ) {
	return domId.length >= SIMPLETABLEANSWER_ID_LENGTH && domId.match(/_ans(.)+ans/);
}

function getSimpleTableNonIdentifyingQuestionId( identifyingColumn ) {
	if ( !identifyingColumn ) return null;
	return 'ans' + identifyingColumn + 'ans';
}
