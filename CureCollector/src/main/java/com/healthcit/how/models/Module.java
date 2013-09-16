/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.healthcit.how.utils.DateUtils;


@Entity
@Table(name="modules")
public class Module implements StateTracker {

	// module status is for general module availability
	public enum ModuleStatus {ACTIVE, INACTIVE, DEPLOYMENT_LOCKED}

	@Id
	private String id;

	private String name;

	private String description;

	@Column (name="deploy_date", insertable=false, updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date deployDate;

	@Column(nullable=false)
	@Enumerated (EnumType.STRING)
	private ModuleStatus status = ModuleStatus.ACTIVE; //default value.

	@Column(name="estimated_completion_time")
	private String estimatedCompletionTime;

	@Column(name="context")
	private String context;
	
	@Column(name="is_flat")
	private boolean isFlat=true;

	//@OneToMany(mappedBy="module", cascade = CascadeType.ALL, orphanRemoval = true)
	@OneToMany(mappedBy="module", cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE} , orphanRemoval = true)
	//{CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
	@OrderBy("order")
	private List<QuestionnaireForm> forms = new ArrayList<QuestionnaireForm>();

	@OneToMany(mappedBy="module", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	private List<SharingGroupModule> entityModules = new ArrayList<SharingGroupModule>();

	public Module(){

	}

	public Date getDeployDate()
	{
		return deployDate;
	}
	public String getEstimatedCompletionTime()
	{
		return estimatedCompletionTime;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean getIsFlat()
	{
		return isFlat;
	}
	
	public void setIsFlat(boolean isFlat)
	{
		this.isFlat = isFlat;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<QuestionnaireForm> getForms() {
		return forms;
	}

	/**
	 * @param questionInfo the questionInfo to set
	 */
	public void setForms(List<QuestionnaireForm> forms) {
		this.forms = forms;
		for (QuestionnaireForm f: this.forms)
			f.setModule(this);
	}

	public void addForm(QuestionnaireForm form)
	{

		if(form.getModule() != this) {
			form.setModule(this);
		}
		this.forms.add(form);

	}

	@Override
	public boolean isNew() {
		return (id == null);

	}

	public List<SharingGroupModule> getEntityModules() {
		return entityModules;
	}

	public void setEntityModules(List<SharingGroupModule> entityModules) {
		this.entityModules = entityModules;
	}

	public void addEntityModule(SharingGroupModule entityModule)
	{

		if(entityModule.getModule() != this) {
			entityModule.setModule(this);
		}
		this.entityModules.add(entityModule);

	}

	public ModuleStatus getStatus() {
		return status;
	}

	public void setStatus(ModuleStatus status) {
		this.status = status;
	}

	public void setContext(String context)
	{
		this.context = context;
	}

	public String getContext()
	{
		return context;
	}

	public void setStatus(String status) {
		if(status.equalsIgnoreCase(ModuleStatus.ACTIVE.toString())) {
			this.status = ModuleStatus.ACTIVE;
		}
		else if(status.equalsIgnoreCase(ModuleStatus.INACTIVE.toString())) {
			this.status = ModuleStatus.INACTIVE;
		}
		else if(status.equalsIgnoreCase(ModuleStatus.DEPLOYMENT_LOCKED.toString())) {
			this.status = ModuleStatus.DEPLOYMENT_LOCKED;
		}
	}

	public XMLGregorianCalendar getDateDeployed() throws DatatypeConfigurationException
	{
		if (deployDate==null)
			return null;
		//XMLGregorianCalendar xmlCal = new XMLGregorianCalendarImpl(cal);		
		XMLGregorianCalendar xmlCal = DateUtils.getXMLGregorianCalendar(deployDate);			    
				
		return xmlCal;
	}
	
	@Override
	public int hashCode()
	{
		int hashCode = (id.toString()).hashCode();
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		boolean areEqual = false;
		if ( obj!= null && obj instanceof Module)
		{
			if(this == obj)
			{
				areEqual = true;
			}
			else
			{
				if(this.id.equals(((Module)obj).id))
				{
					areEqual = true;
				}
			}
		}		
		return areEqual;
	}

}
