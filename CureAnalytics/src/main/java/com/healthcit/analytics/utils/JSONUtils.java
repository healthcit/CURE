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

import net.sf.json.JSONObject;

public class JSONUtils {
	public static Object getValue( JSONObject json, Object key ){
		if ( json.containsKey( key )) {
			return json.get( key );
		}
		else {
			return null;
		}
	}
}
