/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.models;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.RemoteProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

/**
 * The User model.
 * @author Oawofolu
 *
 */
@Entity
@Table(name="users")
public class User implements StateTracker {

	@Id
	@SequenceGenerator(name="userSequence", sequenceName="users_id_seq")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="userSequence")
	private Long id;

	@Column( name="username" )
	private String username;

	@Transient
	private String password;

	@Column( name="password_hint" )
	private String passwordHint;

	@Column( name="last_login_date" )
	private Date lastLoginDate;

	@Column( name="mustchangepassword", nullable=false )
	// Defaults to false
	private boolean mustChangePassword = false;

	@Column( name="system_usage_consent", nullable=false )
	private boolean systemUsageConsent = false;

	@Column( name="system_usage_consent_date" )
	private Date systemUsageConsentDate;

	@Column( name="email_addr" )
	private String emailAddress;
	
	@Column( name="role" )
	private String role;

	@ManyToOne
	@JoinColumn( name="security_question_id" )
	private SecurityQuestion securityQuestion;

	@Column( name="security_question_answer" )
	private String securityQuestionAnswer;

	// bi-directional association to Patient
	@OneToMany( mappedBy="user", cascade = CascadeType.ALL ) // indicates that this is the inverse end of the relationship
	@Fetch( FetchMode.SUBSELECT ) // use subselect fetch rather than join fetch
	private Set<Patient> patients = new HashSet<Patient>();

	@Transient
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Transient
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
		if ( StringUtils.isNotEmpty( password ) ) setEncryptedPassword( new ShaPasswordEncoder(256).encodePassword( password, null ));
	}

	@Transient
	private String passwordConfirmation;

	@Transient
	public String getPasswordConfirmation(){
		return passwordConfirmation;
	}
	public void setPasswordConfirmation( String passwordConfirmation ) {
		this.passwordConfirmation = passwordConfirmation;
	}

	/* The encryptedPassword is what actually gets persisted to the database */
	@Column( name="password" )
	private String encryptedPassword;

	@Transient
	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}
	
	@Transient
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Transient
	private Long passwordStrengthPercent = new Long(100);

	@Transient
	public Long getPasswordStrengthPercent() {
		return passwordStrengthPercent;
	}

	public void setPasswordStrengthPercent(Long passwordStrengthPercent) {
		this.passwordStrengthPercent = passwordStrengthPercent;
	}

	@Transient
	public String getPasswordHint() {
		return passwordHint;
	}

	public void setPasswordHint(String passwordHint) {
		this.passwordHint = passwordHint;
	}

	@Transient
	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	@Transient
	public Set<Patient> getPatients() {
		return patients;
	}

	public void setPatients(Set<Patient> patients) {
		this.patients = patients;
	}

	@Transient
	public boolean isMustChangePassword() {
		return mustChangePassword;
	}

	public void setMustChangePassword(boolean mustChangePassword) {
		this.mustChangePassword = mustChangePassword;
	}

	@Transient
	public boolean isSystemUsageConsent() {
		return systemUsageConsent;
	}

	public void setSystemUsageConsent(boolean systemUsageConsent) {
		this.systemUsageConsent = systemUsageConsent;
	}

	@Transient
	public Date getSystemUsageConsentDate() {
		return systemUsageConsentDate;
	}

	public void setSystemUsageConsentDate(Date systemUsageConsentDate) {
		this.systemUsageConsentDate = systemUsageConsentDate;
	}

	@Transient
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Transient
	private String emailAddressConfirmation;

	@Transient
	public String getEmailAddressConfirmation() {
		return emailAddressConfirmation;
	}

	public void setEmailAddressConfirmation(String emailAddressConfirmation) {
		this.emailAddressConfirmation = emailAddressConfirmation;
	}

	@Transient
	public SecurityQuestion getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(SecurityQuestion securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

	@Transient
	public String getSecurityQuestionAnswer() {
		return securityQuestionAnswer;
	}

	public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
		this.securityQuestionAnswer = securityQuestionAnswer;
	}

	// Builder methods
	public void addPatient( Patient patient ) {
		if ( patients == null )
			patients = new HashSet<Patient>();
		patients.add( patient );
	}

	public void addNewPatient() {
		Patient patient = new Patient( Patient.Status.INCOMPLETE, Calendar.getInstance().getTime(), this );
		this.addPatient( patient );
	}

	@Override
	public boolean isNew() {
		return false;
	}

}
