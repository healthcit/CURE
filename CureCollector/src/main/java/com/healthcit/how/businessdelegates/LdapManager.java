/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import java.util.Collection;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import com.healthcit.how.dao.UserDao;
import com.healthcit.how.models.User;
import com.healthcit.how.businessdelegates.UserService;
import com.healthcit.how.utils.Constants;

public class LdapManager extends LdapUserDetailsMapper {
		
	Logger log = LoggerFactory.getLogger(LdapManager.class);
	
	@Autowired
	private UserDao userDao;
	

	@Autowired
	private UserManager userMgr;
	
	@Autowired
	private UserService userService;
	
	FilterBasedLdapUserSearch users = null;
		
	public User getUser( Long id ) {
		
		return userDao.getById( id );
	}
	
	
	
	public UserDetails mapUserFromContext(DirContextOperations ctx,
			String username, Collection<GrantedAuthority> authority) {
		
		userService.setAuthType(Constants.LDAP_AUTH_VALUE);		
		
		UserDetails originalUser = super.mapUserFromContext(ctx, username,
				authority);

		UserDetails res = new  org.springframework.security.core.userdetails.User(originalUser.getUsername(),
				originalUser.getPassword(), true, true, true, true,
				originalUser.getAuthorities());	
		
		//Check if the user exists in the database. Create user if doesn't exists.
		getUserFromDatabase(originalUser.getUsername());	
		
		return res;
	}
	
	
	private User getUserFromDatabase(String userName){		
		User user = new User();
		user.setUsername(userName);
		User dbUser = userDao.findByName(userName);
		if (dbUser == null) {
			log.debug("user not found in database...");
			dbUser = createDbUser(userName);
			user.setId(dbUser.getId());
		} else {
			log.debug("user found..." + dbUser.getId());
			user.setId(dbUser.getId());
		}
		return user;
	}

	private User createDbUser(String userName) {
		User user = new User();
		user.setUsername(userName);
		userMgr.createUser(user);		
		User dbUser = userDao.findByName(userName);
		return dbUser;
	}
	
	
	
}
