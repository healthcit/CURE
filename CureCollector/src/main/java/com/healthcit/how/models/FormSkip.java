/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Entity
@Table(name="form_skip")
public class FormSkip implements StateTracker {

	public enum LogicalOperator { OR, AND };
	
	public enum ShowType { SHOW, HIDE };
	
	@Id
	@SequenceGenerator(name="userSequence", sequenceName="form_skip_id_seq")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="userSequence")
	private Long id;

	@Column(name="question_id")
	private String questionId;

	@Column(name="question_owner_form_id")
	private String questionOwnerFormId;
	
	@Column(name="row_id")
	private String rowId;
	
	private String rule;

	@OneToMany(mappedBy="parentSkip", cascade={CascadeType.ALL}, fetch=FetchType.EAGER, orphanRemoval = true )
	@Fetch(FetchMode.SUBSELECT)
	protected List<SkipPart> skipParts = new ArrayList<SkipPart>();
	
	@Column(name="logical_op")
	private String logicalOp;
	
//	@ManyToOne(cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional=false)
	@ManyToOne()
	@JoinColumn(name="form_id")
	private QuestionnaireForm form;

	public void setLogicalOp(String logicalOp)
	{
		this.logicalOp = logicalOp;
	}
	
	public String getLogicalOp()
	{
		return logicalOp;
	}
	
	@Override
	public boolean isNew() {
		return (id == null);

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public QuestionnaireForm getForm() {
		return form;
	}

	public void setForm(QuestionnaireForm form) {
		this.form = form;
	}

	public List<SkipPart> getSkipParts()
	{
		return skipParts;
	}
	
	public void addSkipPart(SkipPart part)
	{
		skipParts.add(part);
	}
	
	public String getAnswerValue()
	{
		StringBuilder values = new StringBuilder(100);
		for (int i=0; i<skipParts.size(); i++)
		{
			SkipPart skipPart = skipParts.get(i);
			values.append(skipPart.getAnswerValue());
			if(i< (skipParts.size() -1))
			{
				values.append(" " + logicalOp +" ");
			}
		}
		return values.toString();
	}
	
	public List<String> getAnswerValues()
	{
		List<String> values = new ArrayList<String>();
		for(SkipPart part: skipParts)
		{
			String value = part.getAnswerValue();
			values.add(value);
		}
		return values;
	}
	
	public String getQuestionOwnerFormId() {
		return questionOwnerFormId;
	}


	public void setQuestionOwnerFormId(String questionOwnerFormId) {
		this.questionOwnerFormId = questionOwnerFormId;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	
	public boolean isShowSkip() {
		return StringUtils.equalsIgnoreCase( getRule(), ShowType.SHOW.name() );
	}
	
	
	public boolean isHideSkip() {
		return StringUtils.equalsIgnoreCase( getRule(), ShowType.HIDE.name() );
	}
	
	public boolean willTriggerShowFormSkip(Collection<String> selectedAnswers){
		String logicalOp = this.getLogicalOp();

		Collection<String> skipValues = this.getAnswerValues();
		
		if (logicalOp == null || logicalOp.equals(FormSkip.LogicalOperator.OR.name()))
		{
			for (String value: skipValues)
			{
				if (selectedAnswers.contains(value))
				{
				    return ( isShowSkip() ? true : false );
				}
			}
		}
		
		else if(logicalOp.equals(FormSkip.LogicalOperator.AND.name()))
		{
			if(selectedAnswers.containsAll(skipValues))
			{
				return ( isShowSkip() ? true : false );
			}
		}
		
		return ( isShowSkip() ? false : true );
	}
	
	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append( this.form );
		builder.append( this.questionId );
		builder.append( this.questionOwnerFormId );
		builder.append( this.logicalOp );
		builder.append( this.rule );
		builder.append( new HashSet<SkipPart>(this.skipParts));
		return builder.toHashCode();
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof FormSkip )
		{
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(this.form, ((FormSkip) obj).form);
			builder.append(this.questionId, ((FormSkip) obj).questionId);
			builder.append(this.questionOwnerFormId, ((FormSkip) obj).questionOwnerFormId);
			builder.append(this.logicalOp, ((FormSkip) obj).logicalOp);
			builder.append(this.rule, ((FormSkip) obj).rule);
			builder.append(new HashSet<SkipPart>(this.skipParts), new HashSet<SkipPart>(((FormSkip) obj).skipParts));
			return builder.isEquals();
		}
		
		return false;
	}
}
