/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.utils;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.json.simple.parser.JSONParser;

public class JSONUtils {
	private static JSONParser parser = new JSONParser();
	private static String START_CURLY_BRACE = "{";
	private static String END_CURLY_BRACE = "}";
	
	public static boolean isValidJson(String str)
	{
		boolean isValid = false;
		try 
		{
			parser.reset();
			parser.parse( str );
			isValid = true;
		}catch( Exception ex ) 
		{
		}		
		return isValid;
	}
	
	public static boolean isJson(String str) 
	{
		if ( StringUtils.isBlank(str) ) return false;
		return str.startsWith( START_CURLY_BRACE ) && str.endsWith( END_CURLY_BRACE );
	}
	
	public static Object getObject( JSONObject obj, String key )
	{
		if ( obj == null ) return null;
		
		if ( obj.containsKey( key ) ) return obj.get( key );
		
		return null;
	}
}
