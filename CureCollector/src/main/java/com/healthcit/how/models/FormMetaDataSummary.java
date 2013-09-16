package com.healthcit.how.models;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class FormMetaDataSummary {
	
	private String formId;
	
	private String[] questionShortNames;
	
	private String tableShortName;
	
	private String rootFormId;
	
	public FormMetaDataSummary( String formId, String[] questionShortNames, String tableShortName, String rootFormId )
	{
		setFormId( formId );
		setQuestionShortNames( questionShortNames );
		setTableShortName( tableShortName );
		setRootFormId( rootFormId );
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String[] getQuestionShortNames() {
		return questionShortNames;
	}

	public void setQuestionShortNames(String[] questionShortNames) {
		this.questionShortNames = questionShortNames;
	}

	public String getTableShortName() {
		return tableShortName;
	}

	public void setTableShortName(String tableShortName) {
		this.tableShortName = tableShortName;
	}

	public String getRootFormId() {
		return rootFormId;
	}

	public void setRootFormId(String rootFormId) {
		this.rootFormId = rootFormId;
	}
	
	public boolean hasTableQuestion()
	{
		return ( StringUtils.isNotBlank( tableShortName ) );
	}
	
	public boolean hasQuestionAnswerData()
	{
		return ! ArrayUtils.isEmpty( questionShortNames );
	}
}
