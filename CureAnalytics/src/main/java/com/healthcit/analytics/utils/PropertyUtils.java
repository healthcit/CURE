/*******************************************************************************
 * Copyright (c) 2013 HealthCare It, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-Clause license
 * which accompanies this distribution, and is available at
 * http://directory.fsf.org/wiki/License:BSD_3Clause
 * 
 * Contributors:
 *     HealthCare It, Inc - initial API and implementation
 ******************************************************************************/
package com.healthcit.analytics.utils;

import java.util.ResourceBundle;

public class PropertyUtils {

	
	private static ResourceBundle properties = ResourceBundle.getBundle( "application" );
	
	/* Gets the property with the specified key */
	public static String getProperty( String key )
	{
		String value = null;
		try {
			value = properties.getString( key );
		} catch ( Exception ex ){}
		
		return value;
	}
}
