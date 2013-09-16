/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
function(head, req) {
  var template = this.templates.formAllEntities;
  var Mustache = require("vendor/couchapp/lib/mustache");
  var caCURE = require("hcit/lib/cacure"); 
  var row;
  var globalDocument = new Object();
  var owners = new Array();
  globalDocument["owners"] = owners;
  var ownerInstances = new Object();
  
  while (row = getRow())
  {
	var doc = row["value"];
	document = caCURE.prepareDoc(doc);
//	var ownerObject = new Object();
	var ownerId = document.ownerId;
	var instanceObject =  new Object();
	instanceObject.instanceId = document.instanceId;

	
	globalDocument.name=doc["formName"];
	globalDocument.formId = doc["formId"];
//	var ownerObject = new Object();
//	ownerObject.ownerId = ownerId;
//	ownerObject["instances"] = new Array();
//	owners.push(ownerObject);
	
	instanceObject["questions"] = document["questions"];
	instanceObject["simpleTables"] = document["simpleTables"];
	instanceObject["complexTables"] = document["complexTables"];
//	//ownerObject.ownerId = doc["ownerId"];
	var ownerObject;
	if(!ownerInstances[ownerId])
	{
		ownerObject = new Object();
		ownerObject.ownerId = ownerId;
		ownerObject["instances"] = new Array();
		ownerInstances[ownerId] = ownerObject;
	}
	else
	{
		ownerObject = ownerInstances[ownerId];
	}
	ownerObject["instances"].push(instanceObject);
	
  }
  
  for(var ownerId in ownerInstances)
  {
//	  var ownerObject = new Object();
//	  ownerObject.ownerId = ownerId;
//	  ownerObject["instances"] = ownerInstances[ownerId];
	  owners.push(ownerInstances[ownerId]);
  }
 // owners.push(ownerObject);
  
  return Mustache.to_html(template, globalDocument);
  //return Mustache.render(template, globalDocument);
}
