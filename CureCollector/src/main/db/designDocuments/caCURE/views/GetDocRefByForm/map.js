function(doc){
    if (doc.formId )
    {
        emit(doc.formId, { '_id':doc._id, '_rev':doc._rev });
    }
}