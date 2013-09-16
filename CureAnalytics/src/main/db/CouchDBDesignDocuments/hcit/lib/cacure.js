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
exports.prepareDoc = function(doc)
{
/*    var document = new Object();
    document.revision = doc["_rev"];
    document.entityId = doc["entityId"];
	document.name = doc["formName"];
	document.formId = doc.formId;
    var questions = doc["questions"];
	var questionsArray = new Array();
    for (key in questions)
    {
        var question = questions[key];
		questionsArray.push(question);
    }
	document["questions"] = questionsArray;
	return document;
	*/
	
	var document = new Object();
    document.revision = doc["_rev"];
    document.entityId = doc["entityId"];
	document.name = doc["formName"];
	document.formId = doc.formId;
    var questions = doc["questions"];
    var simpleTables = doc["simple_tables"];
	var complexTables = doc["complex_tables"];
	var questionsArray = new Array();
	var simpleTablesArray = new Array();
	var complexTablesArray = new Array();
	var key;
    for (key in questions)
    {
        var question = questions[key];
		questionsArray.push(question);
    }
    for (key in simpleTables)
    {
        var simpleTable = simpleTables[key];
        var tableObject = new Object();
        tableObject["tableText"] = simpleTable["table_text"];
        tableObject["uuid"] = key;
        tableObject["sn"] = simpleTable["short_name"];
        var tableQuestionsArray = new Array();
        var tableQuestions = simpleTable["questions"];
        for (tableQuestionId in tableQuestions)
        {
        	var tableQuestion = tableQuestions[tableQuestionId];
			tableQuestionsArray.push(tableQuestion);
        }
        tableObject["questions"] = tableQuestionsArray;
		simpleTablesArray.push(tableObject);
    }
    for (key in complexTables)
    {
        var complexTable = complexTables[key];
    	var tableObject = new Object();
        var metadata = complexTable["metadata"];
        tableObject["tableText"] = metadata["table_text"];
        tableObject["sn"] = metadata["short_name"];
        tableObject["uuid"] = complexTable["uuid"];
        var rows = complexTable["rows"];
        var rowsArray = new Array();
        var columnsOrder = metadata["columns_order"];
        for (var i=0; i<rows.length; i++)
        {
        	var row = rows[i];
        	var rowObject = new Object();
        	var columnsArray = new Array();
        	for(var j=0; j<columnsOrder.length; j++)
        	{
        		var currentColumn = columnsOrder[j];
        		var column = row[currentColumn];
        		columnsArray.push(column);
        	}
        	rowObject["columns"] = columnsArray;
        	rowsArray.push(rowObject);
        }
        tableObject["rows"] = rowsArray;

		complexTablesArray.push(tableObject);
    }
	document["questions"] = questionsArray;
	document["simpleTables"] = simpleTablesArray;
	document["complexTables"] = complexTablesArray;
	return document;
}
