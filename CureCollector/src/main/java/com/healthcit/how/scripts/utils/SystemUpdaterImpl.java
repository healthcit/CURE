/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.scripts.utils;

/**
 * This class must be extended by all classes which the #ApplicationUpdater will invoke for the execution of 
 * java-based update scripts.
 */
public abstract class SystemUpdaterImpl implements SystemUpdater {
	
	public final void init() throws Exception {
		setUpDependencies();
		
		executeUpdate();
	}
}
