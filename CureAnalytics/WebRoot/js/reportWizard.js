//////////////////////////////////////////////////////
// STATIC VARIABLES
//////////////////////////////////////////////////////
var REPORT_WIZARD_IND_AUTOCOMPLETER_ELM;        			    // Autocomplete field for Independent variables section
var REPORT_WIZARD_DEP_AUTOCOMPLETER_ELM;                        // Autocomplete field for Dependent variables section
var REPORT_WIZARD_FIL_AUTOCOMPLETER_ELM;                        // Autocomplete field for Filter variables section   // 
var REPORT_WIZARD_TEXTBOXLIST_IND_ELM;                          // textboxlist for independent variables section
var REPORT_WIZARD_TEXTBOXLIST_DEP_ELM;                          // textboxlist for dependent variables section
var REPORT_WIZARD_TEXTBOXLIST_FIL_ELM;                          // textboxlist for filter variables section
var REPORT_WIZARD_SOURCE_LIST; 									// source list for autocomplete fields
var REPORT_WIZARD_PLACEHOLDERS;									// placeholder hash for the autocomplete fields
var REPORT_WIZARD_VARIABLE_TYPES;								// hash of variable types for each section



////////////////////////////////////////////////////////
//On load
////////////////////////////////////////////////////////
jQuery(document).ready(function(){ 
	// set up Autocomplete-related fields for the Report Wizard
	REPORT_WIZARD_PLACEHOLDERS = { 'check_dep' : 'Type to search...', 'check_ind' : 'No groups, or type to search...', 'check_fil' : 'No filters, or type to search...'};
	REPORT_WIZARD_VARIABLE_TYPES = { 'dep' : 'dependent', 'ind' : 'independent', 'fil' : 'filter'};
	REPORT_WIZARD_IND_AUTOCOMPLETER_ELM = jQuery('#check_ind');
	REPORT_WIZARD_DEP_AUTOCOMPLETER_ELM = jQuery('#check_dep');
	REPORT_WIZARD_FIL_AUTOCOMPLETER_ELM = jQuery('#check_fil');	
	
	
	// set up textboxlists
	REPORT_WIZARD_TEXTBOXLIST_IND_ELM = new jQuery.TextboxList('#ind_wizrd_selections', { });
	REPORT_WIZARD_TEXTBOXLIST_DEP_ELM = new jQuery.TextboxList('#dep_wizrd_selections', { });
	REPORT_WIZARD_TEXTBOXLIST_FIL_ELM = new jQuery.TextboxList('#fil_wizrd_selections', { });
	
	
	// set up blur handlers; this will allow different fields to be shown/hidden when the user finishes filling out a field
	setUpBlurHandlers();
	
	// make look-and-feel updates
	prepareReportWizardLookAndFeel();
	
	// reset the wizard
	resetWizard();
});

function setUpReportWizardAutocompleter( elm ){
	elm.autocomplete({source:REPORT_WIZARD_SOURCE_LIST})
	   .focus(function(){removeSearchTextPlaceHolder(this);})
	   .blur(function(){addSearchTextPlaceHolder(this);resetWizardAnswersList(this);});
	
	elm.bind('autocompleteselect', function( event, ui ){
		var shortName = ui.item ? ui.item.value : '';	
		var qId = getQuestionIdForShortName( shortName );
		var returnVal = canMakeQuestionSelection( qId ); // validate whether the question can be selected
		var section = getVariableType(elm);
		if ( returnVal ) executeReportWizardQueryForAnswers(qId,section);
		return returnVal; // (if the return value is false then any selection will be de-selected)
	});
		
	elm.bind('autocompletechange', function( event, ui ){
		if ( !ui.item ) {
			elm.val(''); //if nothing was selected from the list then clear the autocompleter field
		}
	});
}

function resetWizardAnswersList(elm){
	if ( isReportWizardFieldEmpty(elm) ) {
		var section = getVariableType(jQuery(elm));
		hideWizardAnswersIcon(section,'');
	}
}

function showWizardAnswersIcon(section,displayText){
	var answersIcon = jQuery('#' + section + '_check_icon');
	answersIcon.attr('title',displayText);
	answersIcon.css('visibility','visible');
}

function hideWizardAnswersIcon(section,displayText){
	var answersIcon = jQuery('#' + section + '_check_icon');
	answersIcon.attr('title',displayText);
	answersIcon.css('visibility','hidden');
}

function setUpReportWizardAutocompleteFields(){
	REPORT_WIZARD_SOURCE_LIST = getReportWizardAutocompleteList();
	setUpReportWizardAutocompleter(REPORT_WIZARD_IND_AUTOCOMPLETER_ELM); 
	setUpReportWizardAutocompleter(REPORT_WIZARD_DEP_AUTOCOMPLETER_ELM); 
	setUpReportWizardAutocompleter(REPORT_WIZARD_FIL_AUTOCOMPLETER_ELM);
}

function getReportWizardAutocompleteList(){
	var arr = jQuery.map(jQuery('input[type=checkbox][id^=check_]'), function( elm ){ 
		return { label: elm.value, value: elm.value };
	});
	var arr2 = jQuery.map(jQuery('#table_container_content table span[id^=searchmetadata_]:not(:empty)'), function(elm){
		return { label: elm.innerText || elm.textContent, value: getQuestionShortName(elm.id.substring('searchmetadata_'.length)) };
	});
	return jQuery.merge(arr,arr2);
}

function setUpBlurHandlers(){
	// Show/hide sections of the Report Wizard
	jQuery('[class*=_wizrd_input_]').each(function(){
		jQuery(this).change(function(event){
			startAddVariableToWizard();
			var classNames = jQuery(this).attr('class').split(' ');
			var ctr = parseInt(jQuery.map( classNames, function(value){ 
				return (value.match(/\d/));
			})[0]) + 1;
			var section = jQuery.map( classNames, function(value){ 
				return (value.match(/ind|dep|fil/));
			})[0];
			var nextSection = jQuery('.' + section + '_sectn_' + ctr);
			var indAndFilSections = jQuery('.wizrd_section.fil, .wizrd_section.ind');
			var hasNextField = jQuery('.wizrd_input.' + section + '_wizrd_input_' + ctr).length > 0;
			var isCurrentFieldEmpty = isReportWizardFieldEmpty(this);
			var shortName = jQuery('#check_' + section).val();
						
			if ( !!hasNextField ) {
				if ( isCurrentFieldEmpty ) nextSection.css('visibility','hidden');
				else nextSection.css('visibility','visible') && indAndFilSections.show();
			}
			else if ( !isCurrentFieldEmpty ) {
				addRegularVariableToWizard(section,shortName) ;
			}
		});
	});
	
	// Data Type Fields, Aggregation Fields
	jQuery('.dep .wizrd_sect [id^=aggregation_],.dep .wizrd_sect [id^=seldata_],').each(function(){
		jQuery(this).change(function(event){
			// Validate the Data Type selection Based on the Aggregation Selection
			var section = getVariableType(jQuery(this));
			var aggregationElm = jQuery('#aggregation_' + section + ' option:selected');
			var dataTypeElm = jQuery('#seldata_' + section);
			validateAggregationAndDataType(aggregationElm,dataTypeElm);			
		});
	});
		
	// Attach the main validation handlers from the "Create Report" screen to the Report Wizard
	attachValidationHandlersFromCreateReportPage();
}

///////////////////////////////////////////////////////////////////////////////////////////
// Attach the main validation handlers from the "Create Report" screen to the Report Wizard
///////////////////////////////////////////////////////////////////////////////////////////
function attachValidationHandlersFromCreateReportPage(){
	// for Data Type Fields:
	jQuery('.wizrd_sect [id^=seldata_],').each(function(){
		jQuery(this).change(function(event){
			var section = getVariableType(jQuery(this));
			var selectedDataType = jQuery('#seldata_' + section).val();
			var shortName = jQuery('#check_' + section).val();
			var qId = getQuestionIdForShortName(shortName);
			if ( !validateDatatypeSelection(qId,selectedDataType,section) )	{
				jQuery('#aggregation_' + section).val(1);
				jQuery('#seldata_' + section).val('string');				
			}
		});
	});
	
	// ... other validations ....
}

function startAddVariableToWizard(){
	var btn = jQuery('#wizrd_btn_proceed');
	btn.addClass('wait');
	btn.hide();
	jQuery('#wizrd_btn_panel').css('visibility','visible');
}

function finishAddVariableToWizard(){
	var btn = jQuery('#wizrd_btn_proceed');
	btn.fadeIn();
	btn.removeClass('wait');
}

function addAutogeneratedVariableToWizard(section,shortName){
	var variableHash                                     = new Object();
	var variableQuestionId                               = getQuestionIdForShortName( shortName );
	variableHash[ 'check_'+variableQuestionId ]          = shortName;
	variableHash[ 'select_'+variableQuestionId ]         = 'filter';
	variableHash[ 'seldata_'+variableQuestionId ]        = 'string';
	variableHash[ 'seljoin_'+variableQuestionId ]        = 'default';
	variableHash[ 'seljoinother_'+variableQuestionId ]   = '';
	addNewReportVariable(variableHash,variableQuestionId,'fil');
}

function addRegularVariableToWizard(section,shortName){
	var topElm = jQuery('.wizrd_section.' + section);	
	// Determine the value of the new variable
	var variableHash = new Object();
	var variableQuestionId = getQuestionIdForShortName(topElm.find('[id^=check_]').val());
	topElm.find('[class*=_wizrd_input_]').each(function(){
		var key = this.id.replace(/(ind|dep|fil)$/,variableQuestionId);
	    variableHash[key] = jQuery(this).val();
	});	
	addNewReportVariable(variableHash,variableQuestionId,section);
}

function addNewReportVariable(variableHash,variableQuestionId,section){
	var topElm = jQuery('.wizrd_section.' + section+':first');
	
	// Generate the description of the new variable
	var variableDesc = '';
	var variableDatatype = variableHash['seldata_'+variableQuestionId ];
	var variableName = variableHash['check_' +variableQuestionId ]; 
	var variableScope = variableHash['seljoin_' +variableQuestionId ]; 
	variableType = variableHash['select_' + variableQuestionId ];
	if ( variableType == 'filter'){
		variableDesc = variableName;
	}
	else{
		var variableAggregation='';
		if ( !!(variableAggregation=variableHash[ 'aggregation_' + variableQuestionId ]) )
			variableDesc  += getAggregationNameFor(variableAggregation,variableDatatype) +': ';
		variableDesc += variableName + ' (' +  variableDatatype + ')';
		if ( variableScope != 'default' ) 
			variableDesc += ' with scope ' + variableScope;
	}
	
	// Add the new variable
	var hsh = new Object();hsh[variableQuestionId]=variableHash;
	getTextboxlistFor(section).add(variableDesc, hsh, null);
	

	// Reset the visibility of fields in this section
	topElm.find('.wizrd_sect:not([class$=_sectn_1])').css('visibility','hidden');	
	
	// Add any autogenerated fields that need to be added	
	var autogeneratedShortName = getAutogeneratedShortNameForJoinContext( variableScope );
	if ( autogeneratedShortName ){
		addAutogeneratedVariableToWizard(section, autogeneratedShortName);
	}
	// blank out report fields
	blankOutReportWizardFields();
	
	// finish
	finishAddVariableToWizard();
}

function preProcessReportWizardQuery(reportQuery){
	// if the query only has one variable, reset its type to 'independent'
	var qIds = getKeys(reportQuery);
	console.log(JSON.stringify(qIds));
	if ( qIds.length == 1 ) {
		loop1:
		for ( var qId in reportQuery ) {
			var hsh = reportQuery[ qId ];
			loop2:
			for ( var key in hsh ) {
				if ( key.match(/select_/) ) {
					hsh[key] = 'independent'; 
					break loop1;
				}
			}
		}
	}
	
	//...other preprocessing operations 
	
	// return 
	return reportQuery;
}

function generateReportsSectionReportWizard(){
	// Overlay screen
	overlayScreen('Preparing to load report. This may take up to a minute. Please wait...');
	
	// generate reportQuery object
	var reportQuery = new Object();
	jQuery.each( [ REPORT_WIZARD_TEXTBOXLIST_IND_ELM, 
	               REPORT_WIZARD_TEXTBOXLIST_DEP_ELM, 
	               REPORT_WIZARD_TEXTBOXLIST_FIL_ELM ], function(){
		jQuery.each( this.getValues(), function(){
			var current = this[ 0 ];
			for ( var key in current ) reportQuery[ key ] = current[ key ];
		});
	});

	// perform any necessary pre-processing operations
	reportQuery = preProcessReportWizardQuery(reportQuery);
	
	// Indicate that a Report Wizard report was just loaded
	wasSavedOrWizardReportLoaded = true;
	
	generateReportFromReportQueryObject( reportQuery, true );
}

function validateAggregationAndDataType(aggregationElm, dataTypeElm){
	var aggValText = aggregationElm.attr('label');
	var datatypeVal = dataTypeElm.val();
	if ( aggValText != '' && datatypeVal != '' ){
		var aggregationList = getListOfAggregationsByDataType(datatypeVal);
		var aggVal = aggregationElm.text();
		if ( ! arrayContains( aggregationList, aggValText  ) ){
			jAlert('<b>' + aggVal+'</b> is not a valid aggregation for this data type.','ERROR');
			aggregationElm.closest('select').val(1);
			dataTypeElm.val('string');
		}
	}
	
}

///////////////////////////////////////////////////////////////
// Utility Methods
///////////////////////////////////////////////////////////////
function blankOutReportWizardFields(){
	// blank out report fields
	jQuery('.wizrd_input').val('');
	addSearchTextPlaceHolder(jQuery('#check_ind')[0]);
	addSearchTextPlaceHolder(jQuery('#check_dep')[0]);
	addSearchTextPlaceHolder(jQuery('#check_fil')[0]);
}

function resetWizard(){
	blankOutReportWizardFields();
	REPORT_WIZARD_TEXTBOXLIST_IND_ELM.clear();
	REPORT_WIZARD_TEXTBOXLIST_DEP_ELM.clear();
	REPORT_WIZARD_TEXTBOXLIST_FIL_ELM.clear();
	prepareReportWizardLookAndFeel();
	jQuery('#wizrd_btn_panel').css('visibility','hidden');
}

function addSearchTextPlaceHolder(elm){
	var section = elm.id.split('_')[1];
	(elm.value=='') && (elm.value=REPORT_WIZARD_PLACEHOLDERS[elm.id] ) 
					&& (elm.style.color='#7E8489')
					&& (elm.id.match(/check_/) ? hideWizardAnswersIcon(section,'') : true);
}

function removeSearchTextPlaceHolder(elm, placeHolder){
	(elm.value==REPORT_WIZARD_PLACEHOLDERS[elm.id]) && (elm.value='') && (elm.style.color='#000000');;
}

function getAggregationNameFor(index,datatype){
	var list = getListOfAggregationsByDataType(datatype);
	return ( index >= list.length ? list[0] : list[index]);
}

function getTextboxlistFor(variabletype){
	return (variabletype == 'ind' ? REPORT_WIZARD_TEXTBOXLIST_IND_ELM :
		    variabletype == 'dep' ? REPORT_WIZARD_TEXTBOXLIST_DEP_ELM :
		    variabletype == 'fil' ? REPORT_WIZARD_TEXTBOXLIST_FIL_ELM :
		    null);
}

function prepareReportWizardLookAndFeel(){
	// set font color
	var inputFlds = jQuery('#wizrd_content_form input[type=text]');
	inputFlds.each(function(){
		this.style.color='#7E8489';
	});
	
	// hide dependent/filter sections
	jQuery('.wizrd_section.fil, .wizrd_section.ind').hide();
	
	// hide all fields in each section except the first
	jQuery('.wizrd_section.ind .wizrd_sect:not(:first),' +
		   '.wizrd_section.dep .wizrd_sect:not(:first),' +
		   '.wizrd_section.fil .wizrd_sect:not(:first)').css('visibility','hidden');
}

function isReportWizardFieldEmpty(elm){
	return !jQuery(elm).val() || (jQuery(elm).val().toLowerCase().indexOf('search...')>-1);
}

function getVariableType(field){
	return field.attr('id').split('_')[ 1 ];
}