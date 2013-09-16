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
	function emitTypesForAnswerValues(answerValues,isTableQuestion){
		if (answerValues) {
			var tableFld = ( isTableQuestion ? 'table' : null );
            for (var i = 0; i < answerValues.length; ++i) {
                var answerValueObj = answerValues[i];
                var answerValueValue = answerValueObj['ansValue'];
                var answerValueText = answerValueObj['ansText'];
				
				// numeric
                if (!isNaN(answerValueText ? answerValueText : answerValueValue)) emit([questionId, tableFld, 'number'], 1);
				
				// date 
                if (Date.parse(answerValueValue)) emit([questionId, tableFld, 'date'], 1);
				
				//TODO: geographic
            }
        }
	}
	
	
    if (doc.questions) {
        for (var questionId in doc.questions) {
            var question = doc.questions[questionId];
			
			//all questions can have 'string' datatype
            emit([questionId, null, 'string'], 1);
			
            var answerValues = question['answerValues'];
            emitTypesForAnswerValues(answerValues);
        }
    }
    
    if ( doc.complex_tables ) {
		var complexTables = doc.complex_tables;
		for ( var complexTableId in complexTables ) {
			var rows = complexTables[ complexTableId ][ 'rows' ];
			if ( rows ) {
				for ( var rowIndex = 0; rowIndex < rows.length; ++rowIndex ) {
					var row = rows[ rowIndex ];					
					for ( var key in row ) {
						if ( key != 'rowId' ) {
							var questionId = key;
							var question = row[ questionId ];
							var answerValues = question['answerValues'];
							emit([questionId, 'table', 'string'], 1); //all questions can have 'string' datatype
							emitTypesForAnswerValues(answerValues,true);
						}
					}
				}
			}
		}
	}
    
    if ( doc.simple_tables ) {
    	var simpleTables = doc[ 'simple_tables' ];
		for ( var simpleTableId in simpleTables ) {
			var simpleTableText = simpleTables[ simpleTableId ][ 'table_text' ];
			var simpleTableQuestions = simpleTables[ simpleTableId ][ 'questions' ];
            emit([simpleTableId, 'table', 'string'], 1); 	
            
			if ( simpleTableQuestions ) {
    			for ( var id in simpleTableQuestions ) {
    				var question = simpleTableQuestions[id];
    	            var answerValues = question['answerValues'];
    	            var questionId = 'ans'+simpleTableId+'ans';
    	            emit([questionId, 'table', 'string'], 1); //all questions can have 'string' datatype
    	            if (answerValues) emitTypesForAnswerValues(answerValues,true);
    			}
			}
		}
    }
	
	/* SPECIAL FIELDS */
	// Module Id
	if ( doc.moduleId ) {
		emit (['moduleid000000000000000000000000', null, 'string'],1);
	}
	
	// Form Id
	if ( doc.formId ) {
		emit (['formid00000000000000000000000000', null, 'string'],1);
	}
	
	// Updated Date (timestamp)
	if ( doc.updatedDate ) {
		emit (['updatedDate000000000000000000000', null, 'string'],1);
		// TODO: allow "date" datatype
	}
}
