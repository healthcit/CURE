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
package com.healthcit.analytics.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Service;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.beans.factory.annotation.Autowired;




@Service("ldapManager")
public class LdapManager extends LdapUserDetailsMapper {
	
	@Autowired
	private UserService userService;
		
	public UserDetails mapUserFromContext(DirContextOperations ctx,
			String username, Collection<GrantedAuthority> authority) {
		
		userService.setAuthType("ldap");
		UserDetails originalUser = super.mapUserFromContext(ctx, username,
				authority);

		// Current authorities come from LDAP groups
		Collection<GrantedAuthority> newAuthorities = originalUser
				.getAuthorities();

		List<GrantedAuthority> grantedAuthorityList = new ArrayList<GrantedAuthority>();
		if (newAuthorities != null && !newAuthorities.isEmpty()) {

			for (GrantedAuthority currentUserRoles : newAuthorities) {
				grantedAuthorityList.add(new GrantedAuthorityImpl(
						currentUserRoles.getAuthority()));
			}
		}

		UserDetails res = new User(originalUser.getUsername(),
				originalUser.getPassword(), true, true, true, true,
				grantedAuthorityList);
		
		
		return res;
	}
	
	
	
}
