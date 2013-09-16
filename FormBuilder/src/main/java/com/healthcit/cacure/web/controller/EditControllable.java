/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.web.controller;

import org.springframework.web.servlet.ModelAndView;

public interface EditControllable
{
	public boolean isModelEditable(ModelAndView mav);
}
