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
/**
 * 
 */
package com.healthcit.analytics.servlet;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for managing logout action
 *
 */
@Controller
@RequestMapping("/logout")
public class LogoutController {
	
	@RequestMapping(method=RequestMethod.GET)
	public String logout(HttpSession session){
		session.invalidate();
		
		return "redirect:/";
	}
}
