/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.web.controller;


import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.healthcit.how.businessdelegates.SecurityQuestionManager;
import com.healthcit.how.businessdelegates.UserManager;
import com.healthcit.how.models.SecurityQuestion;
import com.healthcit.how.models.User;
import com.healthcit.how.services.Crypto;
import com.healthcit.how.utils.Constants;
import com.healthcit.how.web.propertyeditors.SecurityQuestionPropertyEditor;
import com.healthcit.how.web.validators.UserValidator;

/**
 * Handles registration for new users.
 * @author Oawofolu
 *
 */
@Controller
@RequestMapping( value="/howRegistration.page" )
public class HOWRegistrationController {
	
	/* Logger */
	private static final Logger log = LoggerFactory.getLogger( HOWRegistrationController.class );
	
	/* Constants */
	private static final String INVALID_USER_PAGE = "invalid-user";
	private static final String REGISTRATION_PAGE = "registration";
	private static final String REGISTRATION_SUCCESS_URL = "/registration-success.page";
	private static final String SECURITY_QUESTION_LIST = "securityQuestionList";
	public static final  String TOKEN_COMMAND_NAME     = "tokenCmd";
	
	/* Data Access */
	@Autowired
	private SecurityQuestionManager securityQuestionManager;
	@Autowired
	private UserManager userManager;
	
	/* Validators */
	@Autowired
	private UserValidator validator;
	
	/**
	 * The cryptographic service used to decrypt the registration token
	 */
	@Autowired
	private Crypto cryptoBean;
	public void setCryptoBean( Crypto cryptoBean ) {
		this.cryptoBean = cryptoBean;
	}

	// Property Editors
	@Autowired
	private SecurityQuestionPropertyEditor securityQuestionPropertyEditor;

	@InitBinder
	protected void initBinder( WebDataBinder binder ) {
		binder.registerCustomEditor( SecurityQuestion.class, securityQuestionPropertyEditor );
	}
	
	
	
	// set the registration token
	@ModelAttribute( TOKEN_COMMAND_NAME )
	public String getValidRegistrationToken( HttpSession session, @RequestParam( value=Constants.TOKEN, required=false ) String token ){
		if ( StringUtils.isEmpty( token ) )
			token = ( String )session.getAttribute( Constants.TOKEN );
		
		if ( cryptoBean.validateHash( token ) ){
			session.setAttribute( Constants.TOKEN, token );
			return token;
		}
		
		return null;
	}
	
	// setup the User model
	@ModelAttribute( Constants.COMMAND_NAME )
	public User initModel(  ){
		return new User();
	}
	
	// set the list of security questions displayed during registration
	@ModelAttribute( SECURITY_QUESTION_LIST )
	public List<SecurityQuestion> getSecurityQuestionList(){
		List<SecurityQuestion> list = securityQuestionManager.listQuestions();
		return list;
	}
		
	@RequestMapping( method=RequestMethod.GET )
	public String show( @ModelAttribute( TOKEN_COMMAND_NAME ) String token ){
		log.debug( "In show method..." ) ;
		if ( token == null )
			return INVALID_USER_PAGE;
		else
			return REGISTRATION_PAGE;
	}
	
	@RequestMapping( method=RequestMethod.POST )
	public ModelAndView register( @ModelAttribute( Constants.COMMAND_NAME ) User user, BindingResult result ) {
		log.debug( "In register method...");
		
		// If there are validation errors then redisplay the current page
		validator.validate( user, result );
		if ( result.hasErrors() ){
			return new ModelAndView( REGISTRATION_PAGE );
		}
		
		// Else save the user
		userManager.createUser( user );
		
		return new ModelAndView( new RedirectView( REGISTRATION_SUCCESS_URL, true ) );
	}
}
