/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.web.validators;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.healthcit.how.businessdelegates.PatientManager;
import com.healthcit.how.businessdelegates.SecurityQuestionManager;
import com.healthcit.how.businessdelegates.UserManager;
import com.healthcit.how.models.Patient;
import com.healthcit.how.models.SecurityQuestion;
import com.healthcit.how.models.User;
import com.healthcit.how.utils.Constants;
import com.healthcit.how.utils.ReflectionUtils;


public abstract class FieldLevelValidator {

	/* Logger */
	private Logger log = LoggerFactory.getLogger( FieldLevelValidator.class );
	public static final String SETTER_PREFIX    = "set";
	public static final String VALIDATE_PREFIX  = "validate";
	@Autowired
	private ResourceBundleMessageSource messageSource;
	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@Autowired
	private PatientManager patientManager;
	@Autowired
	private SecurityQuestionManager securityQuestionManager;
	@Autowired
	private UserManager userManager;
	
	protected void invokeValidationMethod( Object target, String inputFieldId, String inputFieldValue, String[] optionalArguments, Errors errors ) {
		log.debug( "Validating "+inputFieldId );
		// get the setter method name
		String setterMethodName = SETTER_PREFIX + StringUtils.capitalize( inputFieldId );
		
		// get the input field's associated Java class (String/Long/Date, etc)
		@SuppressWarnings("rawtypes")
		Class argType = getAssociatedClass( target, inputFieldId );
		
		// get the Method object associated with this setter
		Method setterMethod = ReflectionUtils.getMethod( target, setterMethodName, new Class[] { argType });
		
		// get the value of the property to be set
		Object fieldValue = getFieldValue( target, inputFieldValue, argType );
		
		// set the property
		ReflectionUtils.invokeMethod( target, setterMethod, new Object[]{ fieldValue });
		
		// get the validation method name for this property
		String validationMethodName = VALIDATE_PREFIX + StringUtils.capitalize( inputFieldId );
			
		// get the Method object associated with this validator
		Method validationMethod = ReflectionUtils.getMethod( this, validationMethodName, new Class[]{ argType, String[].class, Errors.class });
		
		// invoke the validation method, thereby populating the errors object if any validation errors exist
		
		ReflectionUtils.invokeMethod( this, validationMethod, new Object[]{ fieldValue, optionalArguments, errors });
		
	}
	
	protected String getFormFieldNameFromId( String id ) {
		String fieldName = null;
		if ( id != null ) {
			String[] tokens = StringUtils.split( id, Constants.UNDERSCORE );
			if ( tokens.length != 0 )
				fieldName = tokens[ tokens.length-1 ];
		}
		return fieldName;
	}
	
	protected List<String> getValidationMessages( Errors errors, String fieldName ) {
		List<FieldError> fieldErrors = errors.getFieldErrors();
		List<String> errorMessages = new ArrayList<String>();
		
		String message = "";
		for ( FieldError fieldError : fieldErrors ) {
			if ( fieldError != null ) {
				message = messageSource.getMessage(fieldError.getCode(), null, Locale.ENGLISH );
				errorMessages.add( message );
			}
		}
		return errorMessages;
	}
	
	/**
	 * Determines this class field's type
	 * @param fieldName
	 */
	protected Class<?> getAssociatedClass( Object target, String fieldName ) {
		if ( target == null ) return null;
		
		Class<?> targetClass = target.getClass();
		Class<?> fieldClass = null;
			
		try {
			fieldClass = targetClass.getDeclaredField( fieldName ).getType();
		} catch( NoSuchFieldException ex ) { ex.printStackTrace();}
		
		return fieldClass;
	}
	
	/**
	 * Returns the property/association in the entity that corresponds to this field.
	 */
	protected Object getFieldValue( Object target, String fieldValue, Class<?> fieldClass ) {
		if ( fieldClass.isAssignableFrom( String.class ) ) 
			return fieldValue;
		
		else if ( fieldClass.isAssignableFrom( SecurityQuestion.class )) {
			Long primaryKey = NumberUtils.createLong( fieldValue );
			return securityQuestionManager.getQuestion( primaryKey );
		}
		
		else if ( fieldClass.isAssignableFrom( User.class )) {
			Long primaryKey = NumberUtils.createLong( fieldValue );
			return userManager.getUser( primaryKey );
		}
		
		else if ( fieldClass.isAssignableFrom( Patient.class )) {
			return patientManager.getPatient( fieldValue );
		}
		
		else {
			log.debug( "In getFieldValue: Could not lookup the field value for this object");
			return null;
		}
		
	}
}
