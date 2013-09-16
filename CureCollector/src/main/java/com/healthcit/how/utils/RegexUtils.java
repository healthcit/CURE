/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

	/**
	 * Gets the value of the capturing group specified by groupIndex
	 */
	public static String getCapturingGroup(int groupIndex, String str, String regex)
	{
		try
		{
			Pattern pattern = Pattern.compile( regex , Pattern.DOTALL );
			Matcher matcher = pattern.matcher( str );
			if ( matcher.find() )
			{
				return matcher.group( groupIndex );
			}
			else
			{
			}
		}
		catch(Exception ex){}
				
		return null;
	}
	
	/**
	 * Enhances the basic String#replaceAll method by including the Pattern.DOTALL bitmask in the replacement operation.
	 */
	public static String replaceAll(String str, String regex, String replacement){
		StringBuffer sb = new StringBuffer();
		try
		{
			Pattern pattern = Pattern.compile( regex , Pattern.DOTALL );
			Matcher matcher = pattern.matcher( str );
			while ( matcher.find() )
			{
				matcher.appendReplacement(sb, replacement);
			}
			matcher.appendTail(sb);
		}
		catch(Exception ex){}
		return sb.toString();
	}
}

