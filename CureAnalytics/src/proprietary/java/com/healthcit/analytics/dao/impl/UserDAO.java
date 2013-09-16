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
package com.healthcit.analytics.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.healthcit.analytics.dao.rowmapper.RoleRowMapper;
import com.healthcit.analytics.dao.rowmapper.UserRowMapper;
import com.healthcit.analytics.model.Role;
import com.healthcit.analytics.model.User;

public class UserDAO implements
		MessageSourceAware {
	
	private static final String SQL_INSERT_USER = "sqlInsertUser";
	private static final String SQL_INSERT_USER_ROLE = "sqlInsertUserRole";
	private static final String SQL_GET_NEXT_USER_ID = "sqlGetNextUserId";
	
	private static final String SQL_UPDATE_USER = "sqlUpdateUser";
	private static final String SQL_UPDATE_USER_WITH_PASS = "sqlUpdateUserWithPass";
	private static final String SQL_DELETE_USER_ROLES = "sqlDeleteUserRoles";
	
	private static final String SQL_DELETE_USER = "sqlDeleteUser";
	
	private static final String SQL_USER_EXISTS = "sqlUserExists";
	
	private static final String SQL_GET_USER = "sqlGetUser";
	private static final String SQL_GET_USER_BY_ID = "sqlGetUserById";
	private static final String SQL_GET_USER_ROLES = "sqlGetUserRoles";
	private static final String SQL_GET_USERS = "sqlGetUsers";
	private static final String SQL_GET_ROLES = "sqlGetRoles";
	
	private MessageSource messageSource;
	private JdbcTemplate jdbcTemplate;
	
	public void createUser(UserDetails user) {
		User usr = (User)user;
		Long userId = this.jdbcTemplate.queryForLong(messageSource.getMessage(SQL_GET_NEXT_USER_ID, null, Locale.getDefault()));
		usr.setId(userId);
		this.jdbcTemplate.update(messageSource.getMessage(SQL_INSERT_USER, null, Locale.getDefault()), usr.getId(), usr.getUsername(), usr.getPassword(), new Date(), usr.getEmail());
		for(GrantedAuthority a : user.getAuthorities()){
			Role role = (Role)a;
			this.jdbcTemplate.update(messageSource.getMessage(SQL_INSERT_USER_ROLE, null, Locale.getDefault()), usr.getId(), role.getId());
		}
	}

	public void updateUser(UserDetails user) {
		User usr = (User)user;
		if(user.getPassword() == null){
			this.jdbcTemplate.update(messageSource.getMessage(SQL_UPDATE_USER, null, Locale.getDefault()), usr.getEmail(), usr.getId());
		} else {
			this.jdbcTemplate.update(messageSource.getMessage(SQL_UPDATE_USER_WITH_PASS, null, Locale.getDefault()), user.getPassword(), usr.getEmail(), usr.getId());
		}
		this.jdbcTemplate.update(messageSource.getMessage(SQL_DELETE_USER_ROLES, null, Locale.getDefault()), usr.getId());
		for(GrantedAuthority a : user.getAuthorities()){
			Role role = (Role)a;
			this.jdbcTemplate.update(messageSource.getMessage(SQL_INSERT_USER_ROLE, null, Locale.getDefault()), usr.getId(), role.getId());
		}
	}

	public void deleteUser(String username) {
		this.jdbcTemplate.update(messageSource.getMessage(SQL_DELETE_USER, null, Locale.getDefault()), username);
	}

	public void changePassword(String oldPassword, String newPassword) {
		// TODO Auto-generated method stub

	}

	public boolean userExists(String username) {
		return this.jdbcTemplate.queryForInt(messageSource.getMessage(SQL_USER_EXISTS, null, Locale.getDefault()), username) > 0;
	}
	
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		List<User> userList = this.jdbcTemplate.query(messageSource.getMessage(SQL_GET_USER, null, Locale.getDefault()), new String[]{username}, new UserRowMapper());
		if(userList.isEmpty()){
			throw new UsernameNotFoundException("User with name '"+username+"' is not found. ");
		}
		User user = userList.iterator().next();
		List<Role> roles = this.jdbcTemplate
				.query(messageSource.getMessage(SQL_GET_USER_ROLES, null, Locale.getDefault()), new Long[]{user.getId()}, new RoleRowMapper());
		user.getAuthorities().addAll(roles);
		return user;
	}
	
	public UserDetails loadUserById(Long id) {
		List<User> userList = this.jdbcTemplate.query(messageSource.getMessage(SQL_GET_USER_BY_ID, null, Locale.getDefault()), new Long[]{id}, new UserRowMapper());
		User user = userList.iterator().next();
		List<Role> roles = this.jdbcTemplate
				.query(messageSource.getMessage(SQL_GET_USER_ROLES, null, Locale.getDefault()), new Long[]{user.getId()}, new RoleRowMapper());
		user.getAuthorities().addAll(roles);
		return user;
	}
	
	public List<User> loadUsers(){
		List<User> userList = this.jdbcTemplate.query(messageSource.getMessage(SQL_GET_USERS, null, Locale.getDefault()), new UserRowMapper());
		for(User user : userList){
			List<Role> roles = this.jdbcTemplate.query(messageSource.getMessage(SQL_GET_USER_ROLES, null, Locale.getDefault()), new Long[]{user.getId()}, new RoleRowMapper());
			user.getAuthorities().addAll(roles);
		}
		return userList;
	}
	
	public List<Role> getRoles(){
		return this.jdbcTemplate.query(messageSource.getMessage(SQL_GET_ROLES, null, Locale.getDefault()), new RoleRowMapper());
	}
	
	public void setMessageSource(
			MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}
