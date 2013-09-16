/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
function(doc)
{
    if(doc.entityId)
  	{
	    var questions = doc["questions"];
		if (questions)
		{
		    for(key in questions)
            {
			    /* get question data */
				var question=questions[key];
                var answers = question.answerValues;
                if(answers)
				{
                    var answersArray = new Array();
                    for(var i=0; i<answers.length; i++)
                    {
                        answersArray[i] = answers[i].ansValue;
                    }
                    emit([doc.entityId, key], answersArray);
                }
			}
		}
        
    }
}
