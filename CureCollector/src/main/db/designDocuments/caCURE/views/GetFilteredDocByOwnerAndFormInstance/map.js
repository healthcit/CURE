/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
function(doc)
{
    function wasAnswered( question ) 
    {
        var answerValues = question.answerValues;
        for ( var index=0; index < answerValues.length; ++index ) 
        {
            if ( answerValues[index]['ansValue'] != '' )
                return true;
        }
        return false;
    }

    function removeBlankAnswers(doc)
    {
        if ( doc.questions )
        {
            for ( index in doc.questions )
            {    
                var question = doc.questions[index];
                if ( ! wasAnswered( question ) ) 
                {
                    // Remove questions with blank answers
                    delete doc.questions[index];
                }
            }
        }
        return doc;
    }
    
    if (doc.ownerId && doc.formId)
    {
    	var instanceId = ( doc.instanceId ? doc.instanceId: 1 );
        emit([doc.ownerId, doc.formId, instanceId], removeBlankAnswers( doc ));
    }
}
