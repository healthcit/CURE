/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
function(doc) {
	if (doc.ownerId && doc.formId)
	{
		var questions = doc['questions'];
		if (questions) {
		    for (key in questions) {
		        var question = questions[key];
		        var answers = question.answerValues;
		        if (answers) 
				{
					var answersArray = new Array();
					for ( var i = 0; i < answers.length; i++)
					{
						answersArray[i] = answers[i].ansValue;
					}
					emit([ doc.ownerId, doc.formId, key ], answersArray);
				}
			}
		}
		var simpleTables = doc['simple_tables'];
		if (simpleTables) 
		{
			for (tkey in simpleTables)
			{
				var questions = simpleTables[tkey]['questions'];
				if (questions) 
				{
					for (key in questions) 
					{
						var question = questions[key];
						var answers = question.answerValues;
						if (answers) 
						{
							var answersArray = new Array();
							for ( var i = 0; i < answers.length; i++)
							{
								answersArray[i] = answers[i].ansValue;
							}
							emit([ doc.ownerId, doc.formId, key ], answersArray);
						}
					}
				}
			}
		}
		var complexTables = doc['complex_tables'];
		if (complexTables) 
		{
			for (tkey in complexTables) 
			{
				var rows = complexTables[tkey]['rows'];
				if (rows) 
				{
					for (rkey in rows)
					{
						var row = rows[rkey];
						var rowId = null;
						for (qkey in row)
						{
							if(qkey == 'rowId')
							{
								rowId = row[qkey];
								break;
							}
						}
						for (qkey in row) 
						{
							var question = row[qkey];
							var answers = question.answerValues;
							if (answers) 
							{
								var answersArray = new Array();
								for ( var i = 0; i < answers.length; i++) 
								{
									answersArray[i] = answers[i].ansValue;
								}
								if(rowId) 
								{
									emit([ doc.ownerId, doc.formId, rowId, qkey ], answersArray);
								}
								else 
								{
									emit([ doc.ownerId, doc.formId, rkey, qkey ], answersArray);
								}
							}
						}
					}
				}
			}
		}
	}
}


/*function(doc)
{
    if(doc.ownerId)
  	{
	    var questions = doc["questions"];
		if (questions)
		{
		    for(key in questions)
            {
			    var question=questions[key];
                var answers = question.answerValues;
                if(answers)
				{
                    var answersArray = new Array();
                    for(var i=0; i<answers.length; i++)
                    {
                        answersArray[i] = answers[i].ansValue;
                    }
                    emit([doc.ownerId, key], answersArray);
                }
			}
		}
        
    }
}
*/
