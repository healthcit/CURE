/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.utils;

import net.sf.json.JSONObject;

public class JSONUtils {
	public static Object getProperty( JSONObject source, String property )
	{
		Object propertyValue = null;
		
		if ( source.containsKey( property) )
		{
			propertyValue = source.get( property );
			if ( isNull( propertyValue ) )
			{
				propertyValue = null;
			}
		}
		
		return propertyValue;
	}
	
	public static boolean isNull(Object obj)
	{
		return net.sf.json.util.JSONUtils.isNull(obj);
	}
}
