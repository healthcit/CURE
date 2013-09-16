/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.web.controller;

import org.springframework.web.bind.annotation.ModelAttribute;

import com.healthcit.cacure.utils.Constants;

/**
 * Base class for libraries editing controllers
 *
 */
public class BaseLibraryEditController extends BaseModuleEditController {
	
	public static final String CANCEL_URL = "cancelUrl";
	
	@ModelAttribute(CANCEL_URL)
	public String getCancelUrl()
	{
		return Constants.LIBRARY_MANAGE_URI;
	}
}
