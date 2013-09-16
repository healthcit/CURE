/*******************************************************************************
 * Copyright (c) 2013 HealthCare It, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-Clause license
 * which accompanies this distribution, and is available at
 * http://directory.fsf.org/wiki/License:BSD_3Clause
 * 
 * Contributors:
 *     HealthCare It, Inc - initial API and implementation
 ******************************************************************************/
package com.healthcit.analytics.web.model;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

public class UserModel {
	private Long id;
	
	@Pattern(regexp="^([a-zA-Z0-9]{6,})$", message="User name should contain at least 6 symbols and contain only letters or numbers.")
	private String username;
	
	@Email(message="Invalid email address. ")
	private String email;
	
	private String password;
	private String confirmPassword;
	private ArrayList<Long> roles;
	
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return this.confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public ArrayList<Long> getRoles() {
		return this.roles;
	}
	public void setRoles(ArrayList<Long> roles) {
		this.roles = roles;
	}
}
