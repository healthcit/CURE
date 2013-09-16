/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.businessdelegates;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;


import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cacure.model.UserCredentials;
import com.healthcit.cacure.model.Role.RoleCode;

public class UserManagerService {  
	
	private String authType;

	@Autowired
	private LdapUserManager ldapUserMgr;
	
	@Autowired
	private UserManager userMgr;
	
	private LdapDatabaseManager mgr;
	
	public void setMgr(LdapDatabaseManager mgr) {
		this.mgr = mgr;
	}

	public UserCredentials getCurrentUser() {
		return mgr.getCurrentUser();	
	}
	
	public boolean isCurrentUserInRole(RoleCode role){
		return mgr.isCurrentUserInRole(role);
	}
	
	public EnumSet<RoleCode> getCurrentUserRoleCodes(){
		return mgr.getCurrentUserRoleCodes();
	}
	
	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {		
		this.authType = authType;
		if(authType != null){
			if(authType.equalsIgnoreCase("ldap")){
				mgr = ldapUserMgr;
			} else {
				mgr = userMgr;
			}
		}
	}	
	
	public Set<UserCredentials> loadUsersByRole(RoleCode roleCode){
		return mgr.loadUsersByRole(roleCode);
	}
	
	public UserCredentials findByName(String userName){
		return mgr.findByName(userName);
	}
	
	public List<UserCredentials> getAllUsers(){
		return mgr.getAllUsers();
	}
	
}
