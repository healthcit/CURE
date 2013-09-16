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
	
    if (doc.ownerId && doc.questions) {
        var array = new Array();
        for (var questionId in doc.questions) {
            var question = doc.questions[questionId];
            var answerValues = question['answerValues'];
            if (answerValues) {
                for (var i = 0; i < answerValues.length; ++i) {
                    var answerValue = answerValues[i];
                    emit([questionId, (answerValue['ansText'] ? answerValue['ansText'] : answerValue['ansValue']), doc.ownerId], null);
                }
            }
        }
    }
	
	/* SPECIAL FIELDS */
	// Module Id
	if ( doc.moduleId ) {
		emit (['moduleid000000000000000000000000', doc.moduleId, doc.ownerId],1);
	}
	
	// Form Id
	if ( doc.formId ) {
		emit (['formid00000000000000000000000000', formId, doc.ownerId],1);
	}
	
	// Updated Date (timestamp)
	if ( doc.updatedDate ) {
		emit (['updatedDate000000000000000000000', doc.updatedDate, doc.ownerId],1);
	}
}
