/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="tag")
public class Tag implements StateTracker {
	
	@OneToMany(mappedBy="tag", fetch=FetchType.LAZY)
	List<QuestionnaireForm> forms = new ArrayList<QuestionnaireForm>();
	
	
	@Id
	@Column(name="tag_id")
	String id;
	
	public void addForm(QuestionnaireForm form)
	{
		if (form != null)
		{
			forms.add(form);
			form.setTag(this);
		}
		
	}
	
	public void removeForm(QuestionnaireForm form)
	{
		forms.remove(form);
	}
	
	public List<QuestionnaireForm> geetForms()
	{
		return forms;
	}

	public void setId(String tagId)
	{
		this.id = tagId;
	}
	
	
	public String getId()
	{
		return id;
	}
	public boolean isNew()
	{
		return false;
	}
}
