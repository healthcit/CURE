/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application welcome page.
 */
@Controller
@RequestMapping("/welcome.page")
public class WelcomeController {

	/**
	 * Simply selects the welcome view to render by returning void and relying
	 * on the default request-to-view-translator.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String welcome() {
		return "main";
	}
}
