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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import com.healthcit.analytics.dao.impl.UserDAO;
import com.healthcit.analytics.model.Role;
import com.healthcit.analytics.model.User;

@Service("userManager")
public class UserManager implements UserDetailsManager {
	
	@Autowired
	private UserDAO userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		return this.userDao.loadUserByUsername(username);
	}

	@Override
	public void createUser(UserDetails user) {
		this.userDao.createUser(user);
	}

	@Override
	public void updateUser(UserDetails user) {
		this.userDao.updateUser(user);
	}

	@Override
	public void deleteUser(String username) {
		this.userDao.deleteUser(username);
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		this.userDao.changePassword(oldPassword, newPassword);

	}

	@Override
	public boolean userExists(String username) {
		return this.userDao.userExists(username);
	}
	
	public UserDetails loadUserById(Long id) {
		return this.userDao.loadUserById(id);
	}
	
	public List<User> loadUsers(){
		return this.userDao.loadUsers();
	}
	
	public List<Role> getRoles(){
		return this.userDao.getRoles();
	}
}
