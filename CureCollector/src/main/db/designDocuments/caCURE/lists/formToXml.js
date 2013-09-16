/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
function(head, req) {
  var template = this.templates.form;
  var Mustache = require("vendor/couchapp/lib/mustache");
  var caCURE = require("hcit/lib/cacure"); 
  var row;
  var document = new Object();

  while (row = getRow())
  {
	var doc = row["value"];
	document = caCURE.prepareDoc(doc);
/*	document.revision = doc["_rev"];
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
*/	
  }
  return Mustache.to_html(template, document);
}
