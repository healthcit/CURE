/*******************************************************************************
 *Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 *Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 *Proprietary and confidential
 ******************************************************************************/
function(doc){
    if (doc.entityId )
    {
        emit(doc.entityId, { '_id':doc._id, '_rev':doc._rev });
    }
}
