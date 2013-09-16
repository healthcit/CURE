/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.utils;

public class Constants {

	public static final String TOKEN = "token";
	public static final String INVALID_USER_URL = "/invalid-user";
	public static final String REGISTRATION_URL = "/registration";
	public static final String COMMAND_NAME = "commandName";
	public static final String PAGE_SUFFIX = ".page";
	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String UNDERSCORE = "_";
	public static final String PASSWORD_CRYPT_SEED = "HOW";
	public static final String LABEL = "label";
	public static final String DESCRIPTION = "description";
	public static final String HIDE_FLAG = "hideFlag";
	public static final String OK = "OK";
	public static final String FAIL = "FAIL";
	public static final String TRUE = "true";
	public static final String APP_SPLITTER = ",";
	public static final String TRUNCATE_FAIL = "Truncation failed";

	/* Error code mappings */
	public static final String USER_USERNAME_REQUIRED         = "user.username.required";
	public static final String USER_USERNAME_MIN              = "user.username.min";
	public static final String USER_USERNAME_REGEX            = "user.username.regex";
	public static final String USER_USERNAME_DUPLICATE		  = "user.username.duplicate";
	public static final String USER_PASSWORD_REQUIRED         = "user.password.required";
	public static final String USER_PASSWORD_MIN              = "user.password.min";
	public static final String USER_PASSWORD_MAX              = "user.password.max";
	public static final String USER_PASSWORD_EXAMPLEMATCH     = "user.password.examplematch";
	public static final String USER_PASSWORD_STRENGTH         = "user.password.strength";
	public static final String USER_PASSWORDCONFIRM_REQUIRED  = "user.passwordconfirm.required";
	public static final String USER_PASSWORDCONFIRM_MATCH     = "user.passwordconfirm.match";
	public static final String USER_EMAIL_REQUIRED            = "user.email.required";
	public static final String USER_EMAIL_REGEX_ERROR         = "user.email.regex.error";
	public static final String USER_USERNAME_REGEX_ERROR      = "user.username.regex.error";
	public static final String USER_EMAIL_REGEX               = "user.email.regex";
	public static final String USER_EMAIL_DUPLICATE			  = "user.email.duplicate";
	public static final String USER_EMAILCONFIRM_REQUIRED     = "user.emailconfirm.required";
	public static final String USER_EMAILCONFIRM_MATCH        = "user.emailconfirm.match";
	public static final String USER_SECURITYQUESTION_REQUIRED = "user.securityquestion.required";
	public static final String USER_SECURITYANSWER_REQUIRED	  = "user.securityanswer.required";
	public static final String USER_SECURITYANSWER_MATCHPASSWORD = "user.securityanswer.matchpassword";
	public static final String USER_PASSWORD_USERNAMEMATCH    = "user.password.usernamematch";

	/* Named Queries */
	public static final String NAMED_QUERY_QUESTION_CACHEDLIST = "question.cachedList";
	public static final String STATUS_NEW = "NEW";
	public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
	public static final String STATUS_SUBMITTED = "SUBMITTED";
	public static final String STATUS_NONE = "NONE";
	public static final String STATUS_OK = "OK";
	public static final String STATUS_FAIL = "FAIL";
	public static final String STATUS_ALL = "ALL";
	
	/* Error Messaging */
	public static final String RESPONSE_STATUS = "status";
	public static final String RESPONSE_STATUS_OK = "ok";
	public static final String RESPONSE_STATUS_ERROR = "error";
	public static final String RESPONSE_STATUS_DETAILS = "details";
	public static final String RESPONSE_CONTENT = "content";
	public static final String ERR_MESSAGE_SUMMARY = "message";
	public static final String ERR_MESSAGE_DETAILS = "details";
	
	public static final String READ_ONLY_INSTANCE_ID_PREFIX = "read-only-";

	/* Form Access errors */
	public static final String ACCESS_DENIED = "access denied";
	public static final String NO_READ_ACCESS="no read access";
	public static final String NO_WRITE_ACCESS="no write access";
	public static final String NO_APPROVE_ACCESS="no approve access";
	public static final String NO_SUBMIT_ACCESS="no submit access";
	
	
	/* Content-types */ 
	 public static final String CONTENT_TYPE_XML = "text/xml";
	 public static final String CONTENT_TYPE_TEXT = "text/plain";
	 public static final String CONTENT_TYPE_JSON = "application/json";
	 
	/*XForm Actions */
	public static final String PREVIOUS_ACTION = "PREVIOUS";
	public static final String NEXT_ACTION     = "NEXT";
	public static final String SUBMIT_ACTION   = "SUBMIT";
	public static final String SAVE_ACTION     = "SAVE";
	public static final String APPROVE_ACTION  = "APPROVE";
	public static final String DECLINE_ACTION  = "DECLINE";
	public static final String REOPEN_ACTION   = "REOPEN";
	
	/*JAXB*/
	public static String MODULE_METADATA_CONTEXT_PATH = "com.healthcit.cacure.metadata.module";
	public static String XFORMS_CONTEXT_PATH = "com.healthcit.cacure.xforms.model";
	public static String GATEWAY_METADATA_CONTEXT_PATH = "com.healthcit.cacure.metadata.gateway";
	
	/*ETL*/
	public static String GATEWAY_QUESTION_SHORT_NAMES = "shortNames";
	public static String GATEWAY_PATIENT_ID           = "patientId";
	public static String GATEWAY_FACILITY_CODE		  = "facilityCode";
	public static String GATEWAY_NOTIFICATION_EMAIL   = "notificationEmail";
	
	//LDAP
	public static final String LDAP_AUTH_VALUE = "ldap";
	public static final String DB_AUTH_VALUE = "db";
	
}
