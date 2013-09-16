/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.data.utils;

import net.sf.json.JSONObject;


public class JSONUtils 
{
	public static boolean isJSONObject( Object obj )
	{
		boolean isJson = true;
		
		try
		{
			JSONObject.fromObject( obj );
		}
		
		catch( Exception ex )
		{
			isJson = false;
		}
		
		return isJson;
	}	
}
