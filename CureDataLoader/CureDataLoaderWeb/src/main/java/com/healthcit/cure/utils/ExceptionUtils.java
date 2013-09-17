/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cure.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;


public class ExceptionUtils {
	
	public static String getExceptionStackTrace( Exception exception ) 
	{
		StringWriter message = new StringWriter();
		
		exception.printStackTrace( new PrintWriter( message ) );
		
		return ( message == null ? null : message.toString() );		
	}	

	public static JSONObject getHttpResponseErrorDetails( String responseSummary, Exception exception )
	{
		JSONObject errorInfo = new JSONObject();
		
		String exceptionMessage = getExceptionStackTrace(exception);
		
		errorInfo.put( Constants.ERR_MESSAGE_SUMMARY, responseSummary );
		
		errorInfo.put( Constants.ERR_MESSAGE_DETAILS, StringUtils.defaultString( exceptionMessage, "" ) );
		
		return errorInfo;
	}
	
}
