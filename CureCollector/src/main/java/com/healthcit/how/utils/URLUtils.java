/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;

public class URLUtils 
{
	
	private static UrlValidator validator = new UrlValidator(new String[]{"http","https"},UrlValidator.ALLOW_LOCAL_URLS);

	public static String getPathInfo( HttpServletRequest request ) 
	{
		if ( request != null )
		{
			String requestUri = request.getRequestURI();
			String contextPath = request.getContextPath();
			return ( requestUri.substring( contextPath.length() ) );
		}
		return "";
	}
	
	public static boolean isValidUrl(String url)
	{
		return validator.isValid( url );
	}
}
