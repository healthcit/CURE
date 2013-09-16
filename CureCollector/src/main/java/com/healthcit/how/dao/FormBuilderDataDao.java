/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;

import com.healthcit.how.models.FormMetaDataSummary;

public class FormBuilderDataDao {
		
	public static final String FORM_UUID = "uuid";
	public static final String FORM_NAME ="name";
	public static final String FORM_PARENT_UUID = "parent_id";
	public static final String FORM_MULTIPLE_INSTANCES = "multiple_instances";
	
	public static final String QUESTION_ELEMENT_UUID ="uuid";
	public static final String QUESTION_ELEMENT_DESCRIPTION = "description";
	public static final String QUESTION_UUID = "question_uuid";
	public static final String QUESTION_SHORT_NAME = "question_short_name";
	public static final String QUESTION_ORDER ="question_ord";
	public static final String QUESTION_IS_IDENTIFYING ="question_is_identifying";
	public static final String QUESTION_TYPE ="question_type";
	public static final String QUESTION_IS_READONLY = "question_is_readonly";
	
	public static final String PARENT_FORM_UUID ="form_uuid";
	public static final String ANSWER_UUID ="answer_uuid";
	public static final String ANSWER_TYPE = "answer_type";
	public static final String ANSWER_VALUE_VALUE = "av_value";
	public static final String ANSWER_VALUE_DESCRIPTION = "av_description";
	public static final String ANSWER_VALUE_ORDER ="av_ord";
	public static final String ANSWER_VALUE_UUID ="av_uuid";
	public static final String ANSWER_VALUE_CONSTRAINT ="answer_value_constraint";
	
	public static final String TABLE_ELEMENT_UUID="uuid";
	public static final String TABLE_ELEMENT_DESCRIPTION="description";
	public static final String TABLE_ELEMENT_SHORT_NAME="table_short_name";
	public static final String TABLE_ELEMENT_TYPE="table_type";
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	Map<String,String> nativeSqlStatementsMap;
	
    public void setJdbcDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
	public Map<String, String> getNativeSqlStatementsMap() {
		return nativeSqlStatementsMap;
	}

	public void setNativeSqlStatementsMap(Map<String, String> nativeSqlStatementsMap) {
		this.nativeSqlStatementsMap = nativeSqlStatementsMap;
	}
    
    
    public void saveFormData(Map<String, Object> formMap)
    {
    	String insert="INSERT INTO form (uuid, name, parent_id, multiple_instances) VALUES (:uuid, :name, :parent_id, :multiple_instances)";
    			
    	String update="UPDATE form SET uuid=:uuid, parent_id=:parent_id, name=:name, multiple_instances=:multiple_instances WHERE uuid=:uuid";
    	
    	String select = "SELECT count(*) FROm form WHERE uuid=:uuid";
    	
    	int formExists = this.jdbcTemplate.queryForInt(select, formMap);
    	if(formExists>0)
    	{
    		jdbcTemplate.update(update, formMap);
    	}
    	else
    	{
    		jdbcTemplate.update(insert, formMap);
    	}
    	
    }
    
    public void saveQuestionData(Map<String, Object> questionData)
    {
    	String insert = "INSERT INTO question_element (uuid, form_uuid, description, " +
    			"question_short_name, question_ord, question_uuid, question_type, question_is_readonly," +
    			"answer_uuid, answer_type, answer_value_constraint, " +
    			"av_value, av_description, av_ord, av_uuid) " +
    			"VALUES (:uuid, :form_uuid, :description, " +
    			":question_short_name, :question_ord, :question_uuid, :question_type, :question_is_readonly, " +
    			":answer_uuid, :answer_type, :answer_value_constraint, :av_value, :av_description, :av_ord, :av_uuid)";
    	
    	String update = "UPDATE question_element SET uuid=:uuid, form_uuid=:form_uuid, description=:description, " +
    			"question_short_name=:question_short_name, question_ord=:question_ord, question_uuid=:question_uuid, question_type=:question_type, question_is_readonly=:question_is_readonly, " +
    			"answer_uuid=:answer_uuid, answer_type=:answer_type, answer_value_constraint=:answer_value_constraint, " +
    			"av_value=:av_value, av_description=:av_description, av_ord=:av_ord, av_uuid=:av_uuid " +
    			"WHERE uuid=:uuid and form_uuid=:form_uuid and question_uuid=:question_uuid and av_uuid=:av_uuid";
    	
    	String select = "select count(*) from question_element where uuid=:uuid and form_uuid=:form_uuid and av_uuid=:av_uuid";
    	
    	int questionExists = this.jdbcTemplate.queryForInt(select, questionData);
    	
    	if(questionExists>0)
    	{
    		jdbcTemplate.update(update, questionData);
    	}
    	else
    	{
    		jdbcTemplate.update(insert, questionData);
    	}
    }
    
    public void saveTableData(Map<String, Object> tableData)
    {
    	String insert = "INSERT INTO table_element (uuid, form_uuid, table_short_name, table_type, description, " +
    			"question_short_name, question_ord, question_uuid, question_is_identifying, question_type, " +
    			"answer_uuid, answer_type, answer_value_constraint, av_value, av_description, av_ord, av_uuid) " +
    			"VALUES (:uuid, :form_uuid, :table_short_name, :table_type, :description," +
    			":question_short_name, :question_ord, :question_uuid, :question_is_identifying, :question_type, " +
    			":answer_uuid, :answer_type, :answer_value_constraint, :av_value, :av_description, :av_ord, :av_uuid)";
    	
    	String update = "UPDATE table_element SET uuid=:uuid, form_uuid=:form_uuid, table_short_name=:table_short_name, table_type=:table_type," +
    			"description=:description, " +
    			"question_short_name=:question_short_name, question_ord=:question_ord, question_uuid=:question_uuid, question_is_identifying=:question_is_identifying, question_type=:question_type, " +
    			"answer_uuid=:answer_uuid, answer_type=:answer_type, answer_value_constraint=:answer_value_constraint, av_value=:av_value, av_description=:av_description, av_ord=:av_ord, av_uuid=:av_uuid " +
    			"WHERE uuid=:uuid and form_uuid=:form_uuid and question_uuid=:question_uuid and av_uuid=:av_uuid";
    	
    	String select = "select count(*) from table_element where uuid=:uuid and form_uuid=:form_uuid and question_uuid=:question_uuid and av_uuid=:av_uuid";
    	
    	int questionExists = this.jdbcTemplate.queryForInt(select, tableData);
    	
    	if(questionExists>0)
    	{
    		jdbcTemplate.update(update, tableData);
    	}
    	else
    	{
    		jdbcTemplate.update(insert, tableData);
    	}
    }
    
    /**
     * @param questionShortNameList
     * @return
     */
    public List<FormMetaDataSummary> getFormMetaDataByQuestionShortNames( String questionShortNameList )
    {
    	String query = nativeSqlStatementsMap.get( "getGatewayFormInstanceMetaData" );    	
    	
    	
    	//MapSqlParameterSource params = new MapSqlParameterSource("questionShortNameList", questionShortNameList);
    	query = StringUtils.replace( query, ":questionShortNameList", questionShortNameList );
    	
    	//List<Map<String,Object>> results = jdbcTemplate.queryForList( query, params );    	
    	List<Map<String,Object>> results = jdbcTemplate.queryForList( query, (Map<String,?>)null );
    	final List<FormMetaDataSummary> resultsList = new ArrayList<FormMetaDataSummary>();
    	
    	
    	CollectionUtils.forAllDo(results, new Closure(){    		
    		public void execute(Object obj) {				
	    		@SuppressWarnings("unchecked")
    			Map< String, Object > map      = ( Map< String, Object > ) obj;
    			String formId                  = ( String ) map.get( "formId" );
    			String[] questionShortNames    = StringUtils.commaDelimitedListToStringArray( ( String )map.get( "questionShortNames" ) );
    			String tableShortName          = ( String )map.get( "tableShortName" );   
    			String rootFormId              = ( String )map.get( "rootForm" );
    			FormMetaDataSummary metadata = new FormMetaDataSummary( formId, questionShortNames, tableShortName, rootFormId );
    			resultsList.add( metadata );
    		}
    	});
    	
    	return resultsList;
    }

}
