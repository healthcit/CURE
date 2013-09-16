/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
function(doc)
{  
    if (doc.entityId && doc.formId)
 	{ 
    	var instanceId = ( doc.instanceId ? doc.instanceId: 1 );
	    emit([doc.entityId, doc.formId, instanceId], doc);
	}
}
