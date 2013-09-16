/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The Patient model
 * @author Oawofolu
 *
 */
@Entity
@Table(name="patients")
public class Patient extends AbstractUUIDKeyedEntity implements StateTracker {
	
	/**
	 * default constructor
	 */
	public Patient(){}
	
	/**
	 * explicit constructor
	 */
	public Patient( Status status, Date modifiedDate, User user ) {
		this.status = status;
		this.modifiedDate = modifiedDate;
		this.user = user;
	}
	
	/**
	 * Status Enum
	 */
	public enum Status { INCOMPLETE, ACTIVATED, DEACTIVATED }
	
	@Column( name="modified_date" )
	private Date modifiedDate;
	
	@Column( name="additional_info" )
	private String additionalInfo;
	
	@Column( name="phr_first_time_completed_date" )
	private Date phrFirstTimeCompletedDate;
	
	@ManyToOne( optional=false, cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH} )
	@JoinColumn( name="user_id" )
	private User user;
	
	@Column( name="status" )
	@Enumerated( EnumType.STRING )
	// default status is INCOMPLETE
	private Status status = Status.INCOMPLETE;
	
	@Transient
	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Transient
	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	@Transient
	public Date getPhrFirstTimeCompletedDate() {
		return phrFirstTimeCompletedDate;
	}

	public void setPhrFirstTimeCompletedDate(Date phrFirstTimeCompletedDate) {
		this.phrFirstTimeCompletedDate = phrFirstTimeCompletedDate;
	}

	@Transient
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Transient
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public boolean isNew() {
		return getId() == null;
	}

}
