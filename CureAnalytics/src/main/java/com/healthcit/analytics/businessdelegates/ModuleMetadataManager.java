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
package com.healthcit.analytics.businessdelegates;

import org.apache.commons.lang.StringUtils;

import com.healthcit.analytics.dao.CouchDBDaoUtils;
import com.healthcit.analytics.utils.PropertyUtils;
import com.healthcit.cacure.dao.CouchDBDao;

/**
 * Business delegate which handles the loading of module metadata.
 */
public class ModuleMetadataManager {
	
	private static CouchDBDao couchDb = CouchDBDaoUtils.getCouchDBDaoInstance();

	/**
	 * Loads module metadata
	 */
	public String loadModuleMetaData() throws Exception
	{
		String moduleMetadata = couchDb.getAttachment( PropertyUtils.getProperty( "couchDBModuleMetadataDoc" ) );
		
		if ( StringUtils.isEmpty( moduleMetadata ) ) throw new Exception("ERROR: Could not load the module metadata");
		
		else return moduleMetadata;
	}
}
