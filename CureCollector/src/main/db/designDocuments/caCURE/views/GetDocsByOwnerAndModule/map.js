/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
function(doc)
{  
    if (doc.ownerId && doc.moduleId)
 	{   
	    emit(doc.ownerId+"_"+doc.moduleId, doc);
	}
}
