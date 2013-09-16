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
package com.healthcit.analytics.service;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.healthcit.analytics.businessdelegates.ModuleMetadataManager;

/**
 * Service performs the remote loading of all module metadata used by the application.
 */
@Service
@RemoteProxy(name="moduleMetadataService")
public class ModuleMetadataService {
	
	@Autowired
	ModuleMetadataManager moduleMetadataManager;
	
	/**
	 * Remote method used to load module metadata on the client side.
	 */
	@RemoteMethod
	public String loadModuleMetaData() throws Exception
	{
		return moduleMetadataManager.loadModuleMetaData();
	}
}
