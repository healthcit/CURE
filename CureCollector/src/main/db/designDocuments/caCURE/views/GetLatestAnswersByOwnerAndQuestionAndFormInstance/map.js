/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
function(doc)
{
	var instanceId = ( doc.instanceId ? doc.instanceId : 1 );
    if(doc.ownerId)
  	{
	    var questions = doc["questions"];
		var simpleTables = doc["simple_tables"];
		var complexTables = doc["complex_tables"];
		if (questions)
		{
		    for(key in questions)
            {
			    
				var question=questions[key];
                var answers = question.answerValues;
				var answersArray = new Array();
                if(answers)
				{
                    
                    for(var i=0; i<answers.length; i++)
                    {
					if(answers[i].ansValue != '')
                        answersArray[i] = answers[i].ansValue;
                    }
                }
				if(answersArray.length>0)
				{
					emit([doc.ownerId, key, instanceId], {'updatedDate': doc.updatedDate, 'questions': question, 'questionType': 'questions'});
				}
			}
		}
		
		if (simpleTables)
		{
		    for(key in simpleTables)
            {
			    var answersArray = new Array();
				var simpleTable=simpleTables[key];
				simpleTable["uuid"] = key;
				var questions = simpleTable["questions"];
				for (var questionId in questions)
				{
					var question=questions[questionId];
					var answers = question.answerValues;
					if(answers)
					{
                    
						for(var i=0; i<answers.length; i++)
						{
						if(answers[i].ansValue != '')
							answersArray[i] = answers[i].ansValue;
						}
					}
					
				}
				if(answersArray.length>0)
				{
					emit([doc.ownerId, key, instanceId], {'updatedDate': doc.updatedDate, 'simple_tables': simpleTable, 'questionType': 'simple_tables'});
				}
                   
			}
		}
		if (complexTables)
		{
		    for(key in complexTables)
            {
			    var answersArray = new Array();
				var complexTable=complexTables[key];
				var rows = complexTable["rows"];
				for(var i=0; i<rows.length; i++)
				{
					var row = rows[i];
					
					for (tableId in row)
					{
//						 emit([doc.ownerId, key], {'updatedDate': row[tableId]});
						var question = row[tableId];
						var answers = question["answerValues"];
//						var singleAnswer = answers.length;
						if (answers)
						{
							
							for(var j=0; j<answers.length; j++)
							{
								if(answers[j].ansValue != '')
								answersArray[j] = answers[j].ansValue;
							}
						}
					}
				}
				if(answersArray.length >0)
				{
                    emit([doc.ownerId, key, instanceId], {'updatedDate': doc.updatedDate, 'complex_tables': complexTable, 'questionType': 'complex_tables'});
				}
//				emit([doc.ownerId, key], {'updatedDate': doc.updatedDate, 'complex_tables': complexTable, 'questionType': 'complex_tables'});
			}
		}
        
    }
}
