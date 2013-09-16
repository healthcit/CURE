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
function (doc) {
	var formId = ( doc.instanceId ? doc.formId + '_' + doc.instanceId : doc.formId );
	
    if (doc.questions) {
        var array = new Array();
        for (var questionId in doc.questions) {
            var question = doc.questions[questionId];
            var answerValues = question['answerValues'];
            if (answerValues) {
                for (var i = 0; i < answerValues.length; ++i) {
                    var answerValue = answerValues[i];
                    emit([questionId, question['questionText'], answerValue['ansId'], (answerValue['ansText'] ? answerValue['ansText'] : answerValue['ansValue'])], 1);
                }
            }
        }
    }
    
    /* COMPLEX TABLE QUESTIONS */
	if ( doc.complex_tables ) {
		var complexTables = doc.complex_tables;
		for ( var complexTableId in complexTables ) {
			var rows = complexTables[ complexTableId ][ 'rows' ];
			var complexTableShortName = complexTables[ complexTableId ][ 'metadata' ][ 'short_name' ];
			var complexTableText = complexTables[ complexTableId ][ 'metadata' ][ 'table_text' ];
			if ( rows ) {
				for ( var rowIndex = 0; rowIndex < rows.length; ++rowIndex ) {
					var row = rows[ rowIndex ];					
					for ( var key in row ) {
						if ( key != 'rowId' ) {
							var questionId = key;
							var question = row[ questionId ];
							var answerValues = question['answerValues'];
				            if (answerValues) {
				                for (var i = 0; i < answerValues.length; ++i) {
				                    var answerValue = answerValues[i];
				                    var questionText = question['questionText'];
				                    var questionSn = question['questionSn'];
				                    if ( !questionText ) {
				                    	if ( !questionSn ) { //must be the leading column of a static table
						            		questionText = complexTableText || ( complexTableShortName+' Header' );
						            	}
						            	else {
						            		questionText = questionSn;
						            	}
				                    }
				                    emit([questionId, questionText, answerValue['ansId'], (answerValue['ansText'] ? answerValue['ansText'] : answerValue['ansValue'])], 1);
				                }
				            }
						}
					}
				}
			}
		}
	}
	
	/* SIMPLE TABLES */
	if ( doc.simple_tables ) {
		var simpleTables = doc[ 'simple_tables' ];
		for ( var simpleTableId in simpleTables ) {
			var simpleTableText = simpleTables[ simpleTableId ][ 'table_text' ];
			var simpleTableShortName = simpleTables[ simpleTableId ][ 'short_name' ];
			var simpleTableQuestions = simpleTables[ simpleTableId ][ 'questions' ];
			if ( simpleTableQuestions ) {
    			for ( var questionId in simpleTableQuestions ) {
    				var question = simpleTableQuestions[questionId];
					var questionText = question['questionText'];
    	            var answerValues = question['answerValues'];
    	            emit([simpleTableId, simpleTableText, questionId, questionText], 1);
    	            
    	            if (answerValues) {
    	                for (var i = 0; i < answerValues.length; ++i) {
    	                    var answerValue = answerValues[i];
    	                    emit(['ans'+simpleTableId+'ans', simpleTableShortName+' Answers', answerValue['ansId'], (answerValue['ansText'] ? answerValue['ansText'] : answerValue['ansValue'])], 1);
    	                }
    	            }
    			}
			}
		}
	}
	
	/* SPECIAL FIELDS */
	// Module Id
	if ( doc.moduleId ) {
		emit (['moduleid000000000000000000000000', 'Module', doc.moduleId, doc.moduleId],1);
	}
	
	// Form Id
	if ( doc.formId ) {
		emit (['formid00000000000000000000000000', 'Form', formId, formId],1);
	}
	
	// Updated Date (timestamp)
	if ( doc.updatedDate ) {
		if ( doc.formId ){
			emit (['updatedDate000000000000000000000', 'ISO-formatted document update timestamp', formId + doc.updatedDate, doc.updatedDate],1);
		// TODO: allow "date" datatype
		}
	}
}
