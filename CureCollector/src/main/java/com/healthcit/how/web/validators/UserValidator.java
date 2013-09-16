/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.web.validators;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.healthcit.how.businessdelegates.UserManager;
import com.healthcit.how.models.SecurityQuestion;
import com.healthcit.how.models.User;
import com.healthcit.how.utils.Constants;
import com.healthcit.how.utils.NumberUtils;

public class UserValidator extends FieldLevelValidator implements Validator {
	
	@Autowired
	private ResourceBundleMessageSource messageSource;
	@Override
	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	@Autowired
	private UserManager userManager;

	public static final String EXAMPLE_PASSWORD = "Test$123";
	public static final Logger log  = Logger.getLogger( UserValidator.class );

	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports( Class clazz ) {
		return User.class.isAssignableFrom( clazz );
	}

	@Override
	public void validate( Object target, Errors errors ) {
		
		User user = ( User ) target;
		
		validateUsername( user.getUsername(), null, errors );
		validatePassword( user.getPassword(), new String[]{user.getUsername()}, errors );
		validatePasswordConfirmation( user.getPasswordConfirmation(), new String[]{user.getPassword()}, errors );
		validateEmailAddress( user.getEmailAddress(), null, errors );
		validateEmailAddressConfirmation( user.getEmailAddressConfirmation() , new String[]{user.getEmailAddress()}, errors);
		validateSecurityQuestion( user.getSecurityQuestion(), null, errors );
		validateSecurityQuestionAnswer( user.getSecurityQuestionAnswer(), null, errors );
				
	}
	
	/* FIELD-LEVEL VALIDATIONS */
	
	/**
	 * Username field-level validations
	 */
	public void validateUsername( String username, String[] optional, Errors errors ) {
		// Username is required
		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "username", Constants.USER_USERNAME_REQUIRED);
		
		// Username must not be less than the minimum
		if ( StringUtils.isNotEmpty( username ) && username.length() < 2 ) 
			errors.rejectValue( "username",  Constants.USER_USERNAME_MIN );
		// Username must match required pattern
		if ( StringUtils.isNotEmpty( username ) && ! username.matches ( messageSource.getMessage( Constants.USER_USERNAME_REGEX,null,Locale.ENGLISH ) ) )
			errors.rejectValue( "username", Constants.USER_USERNAME_REGEX_ERROR );
		
		// Username must not be a duplicate
		if ( StringUtils.isNotEmpty( username ) && userManager.checkDuplicateUsername( username )) {
			errors.rejectValue( "username", Constants.USER_USERNAME_DUPLICATE );
		}
			
	}
	
	/**
	 * Password field-level validations
	 */
	public void validatePassword( String password, String[] optional, Errors errors ) {
		
		// get the security answer
		String securityAnswer = null;
		if ( optional != null && optional.length > 0 ) 
			securityAnswer = optional[0];
		
		// get the password strength
		Long passwordStrengthPercent = null;
		if ( optional != null && optional.length > 1 ) 
			passwordStrengthPercent = NumberUtils.parseLong( optional[ 1 ] );
		
		// Password is required
		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "password", Constants.USER_PASSWORD_REQUIRED );
		
		// Password length must not be less than the minimum
		if ( StringUtils.isNotEmpty( password ) && password.length() < 8 )
			errors.rejectValue( "password", Constants.USER_PASSWORD_MIN );
		
		// Password length must not be greater than the maximum
		if ( StringUtils.isNotEmpty( password ) && password.length() > 10 )
			errors.rejectValue( "password", Constants.USER_PASSWORD_MAX ) ;
		
		// Password must not match example password
		if ( StringUtils.equals( password, EXAMPLE_PASSWORD ) )
			errors.rejectValue( "password", Constants.USER_PASSWORD_EXAMPLEMATCH );
		
		// Password must not match the Security Answer 
		if ( StringUtils.isNotEmpty( securityAnswer ) && StringUtils.equals( password, securityAnswer )) {
			errors.rejectValue( "password", Constants.USER_SECURITYANSWER_MATCHPASSWORD );
		}
		
		// Password must be strong
		if ( passwordStrengthPercent != null && passwordStrengthPercent <= 67 ) {
			errors.rejectValue( "password",  Constants.USER_PASSWORD_STRENGTH );
		}
	}
	
	/**
	 * Password confirmation field-level validations
	 */
	public void validatePasswordConfirmation( String passwordConfirmation, String[] optional, Errors errors ) {
		// Password confirmation is required
		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "passwordConfirmation", Constants.USER_PASSWORDCONFIRM_REQUIRED );
		
		// Password confirmation must match password
		String password = null;
		if ( optional != null && optional.length > 0 )
			password = optional[ 0 ];
		if ( StringUtils.isNotEmpty( password ) && ! StringUtils.equals( password, passwordConfirmation )) {
			errors.rejectValue(  "passwordConfirmation", Constants.USER_PASSWORDCONFIRM_MATCH );
		}
	}
	
	
	/**
	 * Email field-level validations
	 */
	public void validateEmailAddress( String email, String[] optional, Errors errors ) {
		// Email is required
		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "emailAddress", Constants.USER_EMAIL_REQUIRED );
		
		// Email must match required pattern
		if ( StringUtils.isNotEmpty( email ) && ! email.matches( messageSource.getMessage( Constants.USER_EMAIL_REGEX, null, Locale.ENGLISH ) ) ){
			errors.rejectValue( "emailAddress", Constants.USER_EMAIL_REGEX_ERROR );
		}
		
		// Email must not be a duplicate
		if ( StringUtils.isNotEmpty( email ) && userManager.checkDuplicateEmailAddress( email ) ) {
			errors.rejectValue( "emailAddress", Constants.USER_EMAIL_DUPLICATE );
		}
	}
	
	/**
	 * Email Confirmation field-level validations
	 */
	public void validateEmailAddressConfirmation( String emailConfirmation, String[] optional, Errors errors ) {
		// Email confirmation is required
		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "emailAddressConfirmation", Constants.USER_EMAILCONFIRM_REQUIRED );
		
		// Email confirmation must match email address
		String emailAddress = null;
		if ( optional != null && optional.length > 0 )
			emailAddress = optional[ 0 ];
		if ( StringUtils.isNotEmpty( emailAddress ) && ! StringUtils.equals( emailAddress, emailConfirmation ) ) {
			errors.rejectValue( "emailAddressConfirmation", Constants.USER_EMAILCONFIRM_MATCH );
		}
	}
	
	/**
	 * Security Question field-level validations
	 */
	public void validateSecurityQuestion( SecurityQuestion securityQuestion, String[] optional, Errors errors )
	{
		ValidationUtils.rejectIfEmpty( errors, "securityQuestion", Constants.USER_SECURITYQUESTION_REQUIRED );
	}
	
	/**
	 * Security Answer field-level validations
	 */
	public void validateSecurityQuestionAnswer( String securityAnswer, String[] optional, Errors errors ) {
		// Security Answer is required
		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "securityQuestionAnswer", Constants.USER_SECURITYANSWER_REQUIRED );
		
		// Security Answer must not match password
		String password = null;
		if ( optional != null && optional.length > 0 )
			password = optional[ 0 ];
		if ( StringUtils.isNotEmpty( password ) && StringUtils.equals( password, securityAnswer )) {
			errors.rejectValue( "securityQuestionAnswer", Constants.USER_SECURITYANSWER_MATCHPASSWORD );
		}
	}
	
	/**
	 * Method involved in AJAX callback; used for field-level validations
	 */
	public List<String> getInputFieldErrorMessages( String inputFieldId, String inputFieldValue, String[] optionalArguments ) {
		log.debug( "In getInputFieldErrorMessages method...");
		// The list of validation error messages, if any
//		List validationMessages = new ArrayList();
		
		Object target = new User();
		Errors errors = new BindException( target, Constants.COMMAND_NAME );
		
		// get the form field name
		String formFieldName = getFormFieldNameFromId( inputFieldId );
		
		// get any validation errors associated with this field
		invokeValidationMethod(target, formFieldName, inputFieldValue, optionalArguments, errors);
		
		// Associate the error messages with the appropriate fields
		List<String> validationMessages = getValidationMessages( errors, formFieldName );
		
		return validationMessages;
	}

}
