/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.healthcit.how.models.QuestionnaireForm.FormStatus;

@Entity
@Table(name="sharing_group_form_instance")
@AttributeOverride(name="instanceId",column=@Column(name="instance_id"))
@IdClass(SharingGroupFormInstancePk.class)
public class SharingGroupFormInstance implements StateTracker{

//	public enum EntityFormStatus {NEW, IN_PROGRESS, APPROVED, SUBMITTED}

	@Id
	@ManyToOne()
	@JoinColumn(name = "form_id")
	private QuestionnaireForm form;

	@Id
	@ManyToOne()
	@JoinColumn(name="sharing_group_id")
	private SharingGroup sharingGroup;

	
	@OneToOne()
	@JoinColumn(name="entity_id")
	private CoreEntity lastUpdatedBy;
	
	@OneToMany(mappedBy="formInstance", cascade={CascadeType.ALL}, fetch=FetchType.LAZY, orphanRemoval = true)
	protected List<FormSkipAnswer> formSkipAnswers = new ArrayList<FormSkipAnswer>();

	@Id
	private Long instanceId = 1L; // default value 
	
	@Column(nullable=false)
	@Enumerated (EnumType.STRING)
	private FormStatus status = FormStatus.NEW; //default value

	@Basic
	@Column(name="lastupdated")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdated = new Date();
	
	@Basic
	@Column(name="creationdate")
	private Date creationDate = new Date();
	
	@Basic
	@Column(name="parent_instance_id")
	private Long parentInstanceId;
		
	public SharingGroupFormInstance()
	{
		
	}
	
	public SharingGroupFormInstance(QuestionnaireForm form)
	{
		this.form = form;
	}
	
	public SharingGroupFormInstance(SharingGroup sharingGroup, QuestionnaireForm form, Long instanceId)
	{
		this.sharingGroup = sharingGroup;
		this.form = form;
		this.instanceId = instanceId;
	}
	
	public SharingGroupFormInstance(SharingGroup sharingGroup, QuestionnaireForm form, Long instanceId, Long parentInstanceId)
	{
		this.sharingGroup = sharingGroup;
		this.form = form;
		this.instanceId = instanceId;
		this.parentInstanceId = parentInstanceId;
	}
    public QuestionnaireForm getForm() {
		return form;
	}

	public void setForm(QuestionnaireForm form) {
		this.form = form;
	}

	public SharingGroup getSharingGroup() {
		return sharingGroup;
	}

	public void setSharingGroup(SharingGroup sharingGroup) {
		this.sharingGroup = sharingGroup;
	}

    public Date getCreationDate() {
		return creationDate;
	}
    
	public Long getParentInstanceId() {
		return parentInstanceId;
	}

	public void setParentInstanceId(Long parentInstanceId) {
		this.parentInstanceId = parentInstanceId;
	}

	@SuppressWarnings("unused")
	private void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/* This object is never handled on it's own, so the implementation of isNew() method is not important */
    @Transient
    public boolean isNew()
    {
    	return (creationDate == null);
    }

    public XMLGregorianCalendar getLastUpdatedGregCal() throws DatatypeConfigurationException
    {
    	if(lastUpdated == null)
    	{
    		return null;
    	}
    	GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(lastUpdated);
		//XMLGregorianCalendar xmlCal = new XMLGregorianCalendarImpl(cal);
		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		return xmlCal;
    }
    
	public Date getLastUpdated()
	{
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated)
	{
		this.lastUpdated = lastUpdated;
	}

	public FormStatus getStatus() {
		return status;
	}

	public void setStatus(FormStatus status) {
		this.status = status;
	}
	
	public Long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public void setStatus(String status) {
		if(status.equalsIgnoreCase(FormStatus.NEW.toString())) {
			this.status = FormStatus.NEW;
		}
		else if(status.equalsIgnoreCase(FormStatus.IN_PROGRESS.toString())) {
			this.status = FormStatus.IN_PROGRESS;
		}
		else if(status.equalsIgnoreCase(FormStatus.APPROVED.toString())) {
			this.status = FormStatus.APPROVED;
		}
		else if(status.equalsIgnoreCase(FormStatus.SUBMITTED.toString())) {
			this.status = FormStatus.SUBMITTED;
		}
	}

	public CoreEntity getLastUpdatedBy()
	{
		return this.lastUpdatedBy;
	}
	
	public void setLastUpdatedBy(CoreEntity entity)
	{
		this.lastUpdatedBy = entity;
	}	
	
	public List<FormSkipAnswer> getFormSkipAnswers() {
		return formSkipAnswers;
	}

	public void setFormSkipAnswers(List<FormSkipAnswer> formSkipAnswers) {
		if ( this.formSkipAnswers == null ) this.formSkipAnswers = new ArrayList<FormSkipAnswer>();
		this.formSkipAnswers.clear();
		for ( FormSkipAnswer formSkipAnswer : formSkipAnswers ) {
			this.addFormSkipAnswer(formSkipAnswer);
		}
	}
	
	public void addFormSkipAnswer(FormSkipAnswer formSkipAnswer) {
		formSkipAnswer.setFormInstance( this );
		this.formSkipAnswers.add( formSkipAnswer );
	}

	@Transient
	public String getInstanceFormId(){
		return ( getForm() == null ? null : getForm().getId() );
	}
	
	@Transient
	public String getInstanceParentFormId(){
		return ( (getForm() == null || getForm().getParentForm() == null) ? null : getForm().getParentForm().getId() );
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append( this.sharingGroup );
		builder.append( this.form );
		builder.append( this.instanceId );
		return builder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof SharingGroupFormInstance )
		{
			EqualsBuilder builder = new EqualsBuilder();
			builder.append( this.sharingGroup, ((SharingGroupFormInstance) obj).sharingGroup );
			builder.append( this.form, ((SharingGroupFormInstance) obj).form );
			builder.append( this.instanceId, ((SharingGroupFormInstance) obj).instanceId );
			return builder.isEquals();
		}		
		return false;
	}
}
