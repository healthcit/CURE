-- REGULAR QUESTION QUERY
select fe.description as "questionText", fe.uuid as "questionId", q.short_name as "questionSn" , 
a.uuid as "answerId",
av.value as "answerValue",av.description as "answerText"
from form_element fe, form_element fe2, form f, question q, answer a, answer_value av
where f.uuid='f4822bfc-e823-4e73-a9ac-dee1954ce598'
and fe2.form_id = f.id and fe.table_type is null
and ((fe2.link_id is null and fe.id=fe2.id) or (fe2.link_id is not null and fe2.link_id = fe.uuid)) -- NEW LINE ADDED
and f.status not in ('FORM_LIBRARY','QUESTION_LIBRARY')
and q.parent_id = fe.id
and a.question_id = q.id
and av.answer_id =a.id
order by q.ord,av.ord;

--SIMPLE TABLE QUERY (pls modify for links)
select fe.description as "tableText", fe.uuid as "tableId", fe.table_short_name as "tableSn" , 
q.uuid as "simpleTableRowId", q.description as "simpleTableRowText", q.short_name as "simpleTableRowShortName", a.uuid as "answerId",
av.value as "answerValue",av.description as "answerText", q.ord as "rowOrder"
from form_element fe, form_element fe2, form f, question q, answer a, answer_value av
where f.uuid='f4822bfc-e823-4e73-a9ac-dee1954ce598'
and fe2.form_id = f.id
and ((fe2.link_id is null and fe.id=fe2.id) or (fe2.link_id is not null and fe2.link_id = fe.uuid)) -- NEW LINE ADDED
and f.status not in ('FORM_LIBRARY','QUESTION_LIBRARY')
and fe.table_type='SIMPLE'
and q.parent_id = fe.id
and a.question_id = q.id
and av.answer_id =a.id
order by q.ord,av.ord;

-- COMPLEX TABLE QUERY (STATIC)
select fe.description as "tableText", fe.uuid as "tableId", fe.table_short_name as "tableSn" , 
q.uuid as "complexTableColumnId", q.description as "complexTableColumnText", q.short_name as "complexTableColumnShortName", q.is_identifying as "complexTableColumnIsIdentifying",
a.uuid as "answerId",
av.value as "answerValue",av.description as "answerText",
case when q.is_identifying then av.ord else null end as "rowOrder",
q.ord as "columnOrder",
case when q.is_identifying then av.permanent_id else null end as "rowId"
from form_element fe, form_element fe2, form f, question q, answer a
left join answer_value av on av.answer_id =a.id
where f.uuid='f4822bfc-e823-4e73-a9ac-dee1954ce598'
and ((fe2.link_id is null and fe.id=fe2.id) or (fe2.link_id is not null and fe2.link_id = fe.uuid))
and f.status not in ('FORM_LIBRARY','QUESTION_LIBRARY')
and fe2.form_id = f.id
and fe.table_type='STATIC'
and q.parent_id = fe.id
and a.question_id = q.id
order by q.ord,av.ord;

-- COMPLEX TABLE QUERY (DYNAMIC)
select fe.description as "tableText", fe.uuid as "tableId", fe.table_short_name as "tableSn" , 
q.uuid as "complexTableColumnId", q.description as "complexTableColumnText", q.short_name as "complexTableColumnShortName", q.is_identifying as "complexTableColumnIsIdentifying",
a.uuid as "answerId",
av.value as "answerValue",av.description as "answerText",
q.ord as "columnOrder"
from form_element fe, form_element fe2, form f, question q, answer a
left join answer_value av on av.answer_id =a.id
where f.uuid='f4822bfc-e823-4e73-a9ac-dee1954ce598'
and fe2.form_id = f.id
and ((fe2.link_id is null and fe.id=fe2.id) or (fe2.link_id is not null and fe2.link_id = fe.uuid))
and f.status not in ('FORM_LIBRARY','QUESTION_LIBRARY')
and fe.table_type='DYNAMIC'
and q.parent_id = fe.id
and a.question_id = q.id
order by q.ord,av.ord;

/*Getting the form Id for a table question*/
select m.uuid as "moduleId", f.uuid as "formId",f.name as "formName", fe.table_short_name as "shortName" 
from module m, form_element fe, form_element fe2, form f
where fe.table_short_name='HDietHabits'
and fe2.form_id = f.id
and ((fe2.link_id is null and fe.id=fe2.id) or (fe2.link_id is not null and fe2.link_id = fe.uuid))
and f.status not in ('FORM_LIBRARY','QUESTION_LIBRARY')
and m.id = f.module_id;

/*Getting the form Id for a regular question*/
select m.uuid as "moduleId", f.uuid as "formId",f.name as "formName", q.short_name as "shortName"
from module m, form_element fe, form_element fe2, form f, question q
where short_name='AgeRan'
and fe2.form_id = f.id and fe.table_type is null
and ((fe2.link_id is null and fe.id=fe2.id) or (fe2.link_id is not null and fe2.link_id = fe.uuid))
and f.status not in ('FORM_LIBRARY','QUESTION_LIBRARY')
and fe.id = q.parent_id
and m.id = f.module_id;