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
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name="form_skip_answers")
public class FormSkipAnswer implements StateTracker 
{
	public FormSkipAnswer()
	{
		
	}
	
	public FormSkipAnswer(SharingGroupFormInstance formInstance, FormSkip formSkip, String answer)
	{
		setFormInstance( formInstance );
		setFormSkip( formSkip );
		setAnswerValue( answer );		
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Override
	public boolean isNew() {
		return (id == null);
	}
	
	@ManyToOne()
	@JoinColumn(name="form_skip_id")
	public FormSkip formSkip;
	
	@Column(name="trigger_form_id")
	public String triggerFormId;
	
	@Column(name="trigger_question_id")
	public String triggerQuestionId;
	
	@Column(name="answer_value")
	public String answerValue;
	
	@Column(name="owner_id")
	public String ownerId;
	
	@Column(name="trigger_instance_id")
	public Long triggerInstanceId;
	
	@ManyToOne
	@JoinColumns({@JoinColumn(name="trigger_form_id", referencedColumnName="form_id", insertable=false, updatable=false),
	              @JoinColumn(name="owner_id", referencedColumnName="sharing_group_id", insertable=false, updatable=false),
	              @JoinColumn(name="trigger_instance_id", referencedColumnName="instance_id",  insertable=false, updatable=false)})
	public SharingGroupFormInstance formInstance;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FormSkip getFormSkip() {
		return formSkip;
	}

	public void setFormSkip(FormSkip formSkip) {
		this.formSkip = formSkip;
		setTriggerQuestionId( formSkip.getQuestionId() );
	}

	public String getTriggerFormId() {
		return triggerFormId;
	}

	public void setTriggerFormId(String triggerFormId) {
		this.triggerFormId = triggerFormId;
	}

	public String getTriggerQuestionId() {
		return triggerQuestionId;
	}

	public void setTriggerQuestionId(String triggerQuestionId) {
		this.triggerQuestionId = triggerQuestionId;
	}

	public String getAnswerValue() {
		return answerValue;
	}

	public void setAnswerValue(String answerValue) {
		this.answerValue = answerValue;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public Long getTriggerInstanceId() {
		return triggerInstanceId;
	}

	public void setTriggerInstanceId(Long instanceId) {
		this.triggerInstanceId = instanceId;
	}

	public SharingGroupFormInstance getFormInstance() {
		return formInstance;
	}

	public void setFormInstance(SharingGroupFormInstance formInstance) {
		this.formInstance = formInstance;
		setTriggerFormId    ( this.formInstance.getForm().getId() );
		setOwnerId          ( this.formInstance.getSharingGroup().getId() );
		setTriggerInstanceId( this.formInstance.getInstanceId() );
	}
	
	@Override
	public int hashCode()
	{
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append( this.formInstance );
		hashCodeBuilder.append( this.formSkip );
		hashCodeBuilder.append( this.answerValue );
		return hashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof FormSkipAnswer )
		{
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append( this.formInstance, ((FormSkipAnswer) obj).formInstance );
			equalsBuilder.append( this.formSkip, ((FormSkipAnswer) obj).formSkip );
			equalsBuilder.append( this.answerValue, ((FormSkipAnswer) obj).answerValue );
			return equalsBuilder.isEquals();
		}
		
		return false;
	}

}
