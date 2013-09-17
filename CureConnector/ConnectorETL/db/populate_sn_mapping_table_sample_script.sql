 update gateway_etl.question_element set question_short_name = trim(question_short_name);
 update gateway_etl.table_element set table_short_name = trim(table_short_name);
 truncate table fileprocess.sn_mapping;
 insert into fileprocess.sn_mapping (hcit_sn, table_type)
 select question_short_name, null from gateway_etl.question_element
 UNION
 select question_short_name, table_type from gateway_etl.table_element
 UNION
 select table_short_name, table_type from gateway_etl.table_element;
