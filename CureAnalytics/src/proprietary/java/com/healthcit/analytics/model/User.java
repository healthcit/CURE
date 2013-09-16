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
package com.healthcit.analytics.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements UserDetails {

	private static final long serialVersionUID = -4671089426654473027L;
	
	private Long id;
	private Collection<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
	private String password;
	private String userName;
	private String email;
	private Date creationDate;
	
	public User() {
	}
	
	public User(Long id, String userName, String password, String email, Date creationDate) {
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.creationDate = creationDate;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return this.roles;
	}
	
	public void setAuthorities(Collection<GrantedAuthority> roles) {
		this.roles = roles;
	}

	@Override
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getUsername() {
		return this.userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	public String getListOfRoles() {
		if (roles == null || roles.isEmpty()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Iterator<GrantedAuthority> it = roles.iterator();
		while (it.hasNext()) {
			GrantedAuthority role = it.next();
			sb.append(((Role)role).getDisplayName());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

}
