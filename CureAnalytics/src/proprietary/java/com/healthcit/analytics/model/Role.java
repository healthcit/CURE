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

import org.springframework.security.core.GrantedAuthority;

public class Role implements GrantedAuthority {
	
	private static final long serialVersionUID = 7605042967844766652L;

	private Long id;
	private String authority;
	private String displayName;
	
	public Role() {
		// TODO Auto-generated constructor stub
	}
	
	public Role(Long id, String authority, String displayName) {
		this.id = id;
		this.authority = authority;
		this.displayName = displayName;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String getAuthority() {
		return this.authority;
	}
	
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
