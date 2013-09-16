/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.healthcit.how.dao.UserDao;
import com.healthcit.how.models.User;

public class UserManager {
	
	@Autowired
	private UserDao userDao;
	
	@Transactional( propagation=Propagation.REQUIRES_NEW )
	public void createUser(User user) {
		// populate other User fields that were left empty from the form submission
		user.setSystemUsageConsent( true );
		user.setSystemUsageConsentDate( Calendar.getInstance().getTime() );
		
		//save the new user		
		userDao.create( user );
	}
	
	@Transactional
	public void saveUser( User user ) {
		// populate other User fields that were left empty from the form submission
		user.setSystemUsageConsent( true );
		user.setSystemUsageConsentDate( Calendar.getInstance().getTime() );
		
		//save the user		
		userDao.save( user );		
	}
	
	@Transactional
	public void deleteUser( Long id ) {
		// delete the user
		userDao.delete( id );
	}
	
	public User getUser( Long id ) {
		return userDao.getById( id );
	}
	
	public boolean checkDuplicateEmailAddress( String email ) {
		return userDao.doesEmailExist( email );
	}
	
	public boolean checkDuplicateUsername( String username ) {
		return userDao.doesUsernameExist( username );
	}
	
	public List<User> findAllUsers() {
		return userDao.findAllUsers();
	}
}
