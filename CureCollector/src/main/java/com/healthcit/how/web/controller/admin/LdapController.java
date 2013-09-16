/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.web.controller.admin;


import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class LdapController {
	
	@RequestMapping(value="/ldap/ldapList.view")
	public ModelAndView showLdapList() {
		return this.getModel();
	}
	
	/**
	 * @param moduleId Long
	 * @return view with list of QuestionnaireForm items
	 */
	private ModelAndView getModel() {		
		ModelAndView mav = new ModelAndView("ldapList"); // initialize with view name
		
		return mav;
	}


}	
