/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.services;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.businessdelegates.UserManager;
import com.healthcit.how.models.User;

/**
 * Implements methods used for remotely adding, editing and deleting users
 * @author oawofolu
 *
 */

public class RegistrationService {

	@Autowired
	private UserManager userManager;
	
	public String registerUser( User user ) 
	{
		String errors = validateUser( user );
		
		if ( StringUtils.isEmpty( errors ) ) 
		{
			userManager.saveUser( user );
			return "OK";
		}
		else
		{
			return errors;
		}
	}
	
	public String deleteUser( Long id )
	{
		userManager.deleteUser( id );
		return "OK";
	}
	
	// Perform simple validations on the provided User parameter
	private String validateUser( User user ) {
		StringBuffer errorMessage = new StringBuffer();
		
		// username must not be blank
		if ( StringUtils.isBlank(user.getUsername()) ) {
			errorMessage.append("- Username cannot be blank\n");
		}
		
		// username must not already exist
		if ( userManager.checkDuplicateUsername(user.getUsername()) ) {
			errorMessage.append("- Username already exists\n");
		}
		
		return errorMessage.toString();
	}
}
