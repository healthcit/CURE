/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.businessdelegates;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.healthcit.cacure.model.UserCredentials;
import com.healthcit.cacure.model.Role.RoleCode;

public interface LdapDatabaseManager {
	
	public UserCredentials findByName(String username);
	public UserCredentials getCurrentUser();
	public boolean isCurrentUserInRole(RoleCode role);
	public EnumSet<RoleCode> getCurrentUserRoleCodes();
	public Set<UserCredentials> loadUsersByRole(RoleCode roleCode);
	public List<UserCredentials> getAllUsers();
}
