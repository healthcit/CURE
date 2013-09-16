/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.web.controller;


import java.util.List;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.healthcit.how.businessdelegates.UserManager;
import com.healthcit.how.models.User;
import com.healthcit.how.utils.Constants;
import com.healthcit.how.web.validators.UserValidator;

/**
 * Handles registration for new users.
 * @author Oawofolu
 *
 */
@Controller
@RequestMapping( value="/admin/registration" )
public class RegistrationController {
	
	/* Logger */
	private static final Logger log = LoggerFactory.getLogger( RegistrationController.class );
	
	/* Constants */
	private static final String REGISTRATION_PAGE = "registration";
	
	@Autowired
	private UserManager userManager;
	
	/* Validators */
	@SuppressWarnings("unused")
	@Autowired
	private UserValidator validator;
		
	// setup the User model
	@ModelAttribute( Constants.COMMAND_NAME )
	public List<User> initModel(  ){
		List<User> users = userManager.findAllUsers();
		
		// add a dummy user which will be used by the view layer to add new users
		User dummyUser = new User();
		dummyUser.setId( new Long(-1) );
		users.add( 0, dummyUser ); // add a dummy user which will be used by the form to add new users
		return users;
	}
			
	@RequestMapping( method=RequestMethod.GET )
	public String show(){
		log.debug( "In show method..." ) ;
		return REGISTRATION_PAGE;
	}
	
}
