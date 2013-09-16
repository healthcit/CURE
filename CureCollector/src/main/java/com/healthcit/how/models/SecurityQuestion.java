/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The SecurityQuestion model.
 * @author Oawofolu
 *
 */
@Entity
@Table(name="security_questions")
@NamedQuery(name="question.cachedList", 
		    query="from SecurityQuestion", 
		    hints={ @QueryHint( name="javax.persistence.cache.retrieveMode", value="USE" ),
				    @QueryHint( name="javax.persistence.cache.storeMode", value="USE" )})
public class SecurityQuestion implements StateTracker {
	
	@Id
	@GeneratedValue( strategy=GenerationType.IDENTITY )
	private Long id;
	
	@Column( name="value" )
	private String value;

	@Transient
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean isNew() {
		return false;
	}

}
