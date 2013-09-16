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
    if (doc.questions) {
        for (var questionId in doc.questions) {
            var questionText = doc.questions[questionId]['questionText'];
            var questionSn = doc.questions[questionId]['questionSn'];
            var questionSearchMetaData = ( questionText || '' );
            if (questionText) emit([questionId, questionSn, questionText, questionSearchMetaData, ''], null);
        }
    }
    
    /* COMPLEX TABLE QUESTIONS */
	if ( doc.complex_tables ) {
		var complexTables = doc.complex_tables;
		for ( var complexTableId in complexTables ) {
			var rows = complexTables[ complexTableId ][ 'rows' ];
			var complexTableShortName = complexTables[ complexTableId ][ 'metadata' ][ 'short_name' ] || '';
			var complexTableText = complexTables[ complexTableId ][ 'metadata' ][ 'table_text' ] || '';
			var complexTableIdentId = complexTables[ complexTableId ][ 'metadata' ][ 'ident_column_uuid' ] || '';
			var complexTableMetaData = '';
			var searchMetaData = '|KEYWORD MATCHES:-|';
			if ( complexTableShortName ) {
				complexTableMetaData =  'Column in Table: ' + ( complexTableText || complexTableShortName ) 
										+ ' (' + complexTableShortName + ')';
				searchMetaData += complexTableText + '...' +  complexTableShortName + '...';
			}
			if ( rows ) {    			
    			// Append identifying column info to "searchMetaData", if any exists
				if ( complexTableIdentId ) {
	    			for ( var i = 0; i < rows.length; ++i ) {
	    				var leadingColumn = rows[i][complexTableIdentId];
	    				if ( leadingColumn ) {
	    					var answerValues = leadingColumn['answerValues'];				                	            
	        	            if (answerValues) {
	        	                for (var j = 0; j < answerValues.length; ++j) {
	        	                    var answerValue = answerValues[j];
	        	                    var answerValueValue = (answerValue['ansText'] ? answerValue['ansText'] : answerValue['ansValue']);
	        	                    if ( answerValueValue && searchMetaData.indexOf( answerValueValue ) == -1 ) {
	        	                    	searchMetaData += answerValueValue + '...';
	        	                    }
	        	                }
	        	            }
	    				}
	    			}
				}
    			
				// Get other information
				for ( var rowIndex = 0; rowIndex < rows.length; ++rowIndex ) {
					var row = rows[ rowIndex ];					
					for ( var key in row ) {
						if ( key != 'rowId' ) {
							var questionId = key;
							var question = row[ questionId ];
							var questionText = question['questionText'];
				            var questionSn = question['questionSn'];
				            if (questionText) {
				            	emit([questionId, questionSn, questionText, searchMetaData, complexTableMetaData], null);
				            }
				            else {
				            	if ( !questionSn ) { //must be the leading column of a static table
				            		if ( complexTableShortName ) {
				            			var text = complexTableText || ( complexTableShortName+' Leading Column' );				            			
				            			emit([questionId, complexTableShortName+'Header', text, searchMetaData, complexTableMetaData ], null);
				            		}
				            	}
				            	else {
				            		if ( complexTableIdentId ) { //meaning these must be the columns of a static table
				            			var text = questionSn; //TODO: The actual question text should be stored in the CouchDB database 
				            			emit([questionId, questionSn, text, searchMetaData, complexTableMetaData ], null);
				            		}
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
			var simpleTableMetaData = '';
			var searchMetaData = '|KEYWORD MATCHES:-|';
			if ( !!simpleTableShortName ) {
				simpleTableMetaData =  'Column in Table: ' + ( simpleTableText || simpleTableShortName )
									+ ' (' + simpleTableShortName + ')';
				// Generate "searchMetaData"
				searchMetaData += simpleTableText + '...' +  simpleTableShortName + '...';
				for ( var questionId in simpleTableQuestions ) {
					var simpleTableQuestion = simpleTableQuestions[ questionId ];
					var questionSn = simpleTableQuestion[ 'questionSn' ] || '';
					var questionText = simpleTableQuestion[ 'questionText' ] || '';
					searchMetaData += questionSn + '...' + questionText + '...';
				}
				
				emit([simpleTableId, simpleTableShortName, simpleTableText, searchMetaData, simpleTableMetaData], null);
				emit(['ans'+simpleTableId+'ans', simpleTableShortName+'_Answers', simpleTableShortName+' Answers', searchMetaData, simpleTableMetaData], null);
			}
		}
	}
	
	/* SPECIAL FIELDS */
	// Module Id
	if ( doc.moduleId ) emit (['moduleid000000000000000000000000', 'Module', 'The Module Id', 'Module', ''],null);
	
	// Form Id
	if ( doc.formId ) emit (['formid00000000000000000000000000', 'Form', 'The Form Id', 'Form', ''],null);
	
	// Updated Date (timestamp)
	if ( doc.updatedDate ) emit (['updatedDate000000000000000000000', 'UpdatedDate', 'ISO-formatted document update timestamp', 'UpdatedDate...Timestamp', ''],null);
}
