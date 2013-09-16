/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.beans;

import java.util.Collection;

public class AnswerSearchResultsBean {

	private AnswerSearchCriteriaBean criteria;
	private Collection<String> answers;
	
	public AnswerSearchResultsBean() {
		super();
	}
			
	public AnswerSearchResultsBean(AnswerSearchCriteriaBean criteria,
			Collection<String> answers) {
		super();
		this.criteria = criteria;
		this.answers = answers;
	}
	
	public AnswerSearchCriteriaBean getCriteria() {
		return criteria;
	}
	public void setCriteria(AnswerSearchCriteriaBean criteria) {
		this.criteria = criteria;
	}
	public Collection<String> getAnswers() {
		return answers;
	}
	public void setAnswers(Collection<String> answers) {
		this.answers = answers;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answers == null) ? 0 : answers.hashCode());
		result = prime * result
				+ ((criteria == null) ? 0 : criteria.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnswerSearchResultsBean other = (AnswerSearchResultsBean) obj;
		if (answers == null) {
			if (other.answers != null)
				return false;
		} else if (!answers.equals(other.answers))
			return false;
		if (criteria == null) {
			if (other.criteria != null)
				return false;
		} else if (!criteria.equals(other.criteria))
			return false;
		return true;
	}
	
}
