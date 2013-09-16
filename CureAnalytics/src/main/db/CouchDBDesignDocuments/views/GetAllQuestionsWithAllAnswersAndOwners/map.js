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
    if (doc.ownerId ) {
    	
    	var formId = ( doc.instanceId ? doc.formId + '_' + doc.instanceId : doc.formId );
    	
    	/* REGULAR QUESTIONS */
    	if ( doc.questions) {
	        var array = new Array();
	        for (var questionId in doc.questions) {
	            var question = doc.questions[questionId];
	            var answerValues = question['answerValues'];
	            if (answerValues) {
	                for (var i = 0; i < answerValues.length; ++i) {
	                    var answerValue = answerValues[i];
	                    emit([questionId, (answerValue['ansText'] ? answerValue['ansText'] : answerValue['ansValue']), doc.ownerId, null, doc.moduleId, formId, doc.instanceId], 1);
	                }
	            }
	        }
    	}
    	
    	/* COMPLEX TABLE QUESTIONS */
    	if ( doc.complex_tables ) {
    		var complexTables = doc.complex_tables;
    		for ( var complexTableId in complexTables ) {
    			var rows = complexTables[ complexTableId ][ 'rows' ];
    			var identifyingColumn = complexTables[ complexTableId ][ 'metadata' ][ 'ident_column_uuid' ] || null;
    			if ( rows ) {
    				for ( var rowIndex = 0; rowIndex < rows.length; ++rowIndex ) {
    					var row = rows[ rowIndex ];
    					
    					// Determine the rowId
    					var rowId = row[ 'rowId' ] || complexTableId + '_' + rowIndex;
    					
    					for ( var key in row ) {
    						if ( key != 'rowId' ) {
    							var questionId = key;
    							var question = row[questionId];
    				            var answerValues = question['answerValues'];
    				            if (answerValues) {
    				                for (var i = 0; i < answerValues.length; ++i) {
    				                    var answerValue = answerValues[i];
    				                    emit([questionId, (answerValue['ansText'] ? answerValue['ansText'] : answerValue['ansValue']), doc.ownerId, rowId, doc.moduleId, formId, doc.instanceId], 1);
    				                }
    				            }
    						}
    					}
    				}
    			}
    		}
    	}
    	
    	/* SIMPLE TABLE QUESTIONS */
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
	    	            var rowId = questionId;
	    	            emit([simpleTableId, questionText, doc.ownerId, rowId, doc.moduleId, formId, instanceId], 1);
	    	            
	    	            if (answerValues) {
	    	                for (var i = 0; i < answerValues.length; ++i) {
	    	                    var answerValue = answerValues[i];
	    	                    emit(['ans'+simpleTableId+'ans', (answerValue['ansText'] ? answerValue['ansText'] : answerValue['ansValue']), doc.ownerId, rowId, doc.moduleId, formId, doc.instanceId], 1);
	    	                }
	    	            }
	    			}
    			}
    		}
    	}
	
		/* SPECIAL FIELDS */
		// Module Id
		if ( doc.moduleId ) {
			emit (['moduleid000000000000000000000000', doc.moduleId, doc.ownerId, null, doc.moduleId, formId, doc.instanceId ],1);
		}
		
		// Form Id
		if ( doc.formId ) {
			emit (['formid00000000000000000000000000', formId, doc.ownerId, null, doc.moduleId, formId, doc.instanceId],1);
		}
		
		// Updated Date (timestamp)
		if ( doc.updatedDate ) {
			emit (['updatedDate000000000000000000000', doc.updatedDate, doc.ownerId, null, doc.moduleId, formId, doc.instanceId],1);
		}
    }
}
