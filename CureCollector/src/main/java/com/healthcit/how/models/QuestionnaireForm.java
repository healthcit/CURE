/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


@Entity
@Table(name="forms")
public class QuestionnaireForm implements StateTracker {

	public enum FormStatus {NEW, IN_PROGRESS, SUBMITTED, APPROVED}
	public enum FormPosition {FIRST, MIDDLE, LAST, NONE}

	@Id
	private String id; //UUID

	@Column(name="form_name")
	private String name;

	private String description;

	private String author;

//	@Column(nullable=false, name="status")
//	@Enumerated (EnumType.STRING)
	@Transient
	private FormStatus status = FormStatus.NEW; //default value.

	@Column(name="question_count")
	private BigInteger questionCount;

	@Column(name="xform_location")
	private String xformLocation;

	@Column(name="form_order")
	private Long order;

	@ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name="tag_id")
	private Tag tag;
	
//	@ManyToOne(cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional=false)
	@ManyToOne()
	@JoinColumn(name="module_id")
	private Module module;
	
	@Column(name="instance_group")
	private String instanceGroup;
	
	@Column(name="max_instances")
	private Long maxInstances;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST} )
	@JoinColumn(name="parent_id")
	private QuestionnaireForm parentForm;
	
	@OneToMany(mappedBy="parentForm", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OrderBy("order")
	private List<QuestionnaireForm> childForms = new ArrayList<QuestionnaireForm>();

	@OneToMany(mappedBy="form", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FormSkip> formSkips = new ArrayList<FormSkip>();
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="question_owner_form_id", referencedColumnName="id", insertable=false, updatable=false)
	private List<FormSkip> formSkipAffectees = new ArrayList<FormSkip>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public BigInteger getQuestionCount() {
		return questionCount;
	}

	public void setQuestionCount(BigInteger questionCount) {
		this.questionCount = questionCount;
	}

	public String getXformLocation() {
		return xformLocation;
	}

	public void setXformLocation(String xformLocation) {
		this.xformLocation = xformLocation;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public void setTag(Tag tag)
	{
		this.tag = tag;
	}
	
	public Tag getTag()
	{
		return this.tag;
	}
	@Override
	public boolean isNew() {
		return (id == null);

	}
	
	public String getInstanceGroup() {
		return instanceGroup;
	}

	public void setInstanceGroup(String instanceGroup) {
		this.instanceGroup = instanceGroup;
	}

	public Long getMaxInstances() {
		return maxInstances;
	}

	public void setMaxInstances(Long maxInstances) {
		this.maxInstances = maxInstances;
	}

	public QuestionnaireForm getParentForm() {
		return parentForm;
	}

	public void setParentForm(QuestionnaireForm parentForm) {
		this.parentForm = parentForm;
	}

	public List<QuestionnaireForm> getChildForms() {
		return childForms;
	}

	public void setChildForms(List<QuestionnaireForm> childForms) {
		this.childForms = childForms;
	}
	
	public void addChildForm(QuestionnaireForm childForm) {
		if ( childForm.getParentForm() == null ) {
			childForm.setParentForm( this );
		}
		getChildForms().add( childForm );		
	}
	
	
	public List<FormSkip> getFormSkipAffectees() {
		return formSkipAffectees;
	}

	public void setFormSkipAffectees(List<FormSkip> formSkipAffectees) {
		this.formSkipAffectees = formSkipAffectees;
	}

	@Transient
	public boolean isParentForm() {
		return CollectionUtils.isNotEmpty( getChildForms() );
	}
	
	@Transient
	public boolean isTopLevelForm() {
		return ! isChildForm();
	}
	
	@Transient
	public boolean isChildForm() {
		return getParentForm() != null;
	}

	@Transient
	public FormStatus getStatus() {
		return status;
	}
	
	@Transient
	public void setStatus(FormStatus status) {
		this.status = status;
	}

	@Transient
	public void setStatus(String status) {
		if(status.equalsIgnoreCase(FormStatus.NEW.toString())) {
			this.status = FormStatus.NEW;
		}
		if(status.equalsIgnoreCase(FormStatus.IN_PROGRESS.toString())) {
			this.status = FormStatus.IN_PROGRESS;
		}
		if(status.equalsIgnoreCase(FormStatus.SUBMITTED.toString())) {
			this.status = FormStatus.SUBMITTED;
		}
	}

	public Long getOrder() {
		return order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	public List<FormSkip> getFormSkips() {
		return formSkips;
	}

	public void setFormSkips(List<FormSkip> formSkips) {
		this.formSkips = formSkips;
	}
	
	@Transient
	public boolean hasSkips() {
		return CollectionUtils.isNotEmpty( getFormSkips() );
	}
	
	@Transient
	public boolean hasSameFormSkips( QuestionnaireForm otherForm ) {
		Set<FormSkip> thisFormSkips = 
				new HashSet<FormSkip>( this.getFormSkips() );
		
		Set<FormSkip> otherFormSkips = 
				otherForm == null ?
				new HashSet<FormSkip>() :
				new HashSet<FormSkip>( otherForm.getFormSkips() );
				
		return thisFormSkips.equals( otherFormSkips );		
	}

	public void addFormSkip(FormSkip formSkip) {
		this.formSkips.add(formSkip);
		formSkip.setForm(this);
	}
	@Override
	public int hashCode()
	{
		int hashCode = id == null ? 0 : (id.toString()).hashCode();
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		boolean areEqual = false;
		if ( obj!= null && obj instanceof QuestionnaireForm)
		{
			if(this == obj)
			{
				areEqual = true;
			}
			else
			{
				if(StringUtils.equals(this.id,((QuestionnaireForm)obj).id))
				{
					areEqual = true;
				}
			}
		}		
		return areEqual;
	}
	
}
