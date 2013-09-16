/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.scripts.utils;

/**
 * Interface defining methods which must be implemented by the Java-based update scripts which will be executed
 * by the #ApplicationUpdater on deployment.
 */
public interface SystemUpdater {
	/**
	 * Sets up any dependencies required for a task.
	 * For instance, it may be used to create new, non-managed instanced of beans that would otherwise be managed via the Spring context.
	 */
	public void setUpDependencies() throws Exception;
	
	/**
	 * Executes a desired task.
	 */
	public void executeUpdate() throws Exception;
}
