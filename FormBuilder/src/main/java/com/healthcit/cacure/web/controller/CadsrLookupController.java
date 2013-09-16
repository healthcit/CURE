/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.web.controller;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping(value="/cadsrLookUp.form")
public class CadsrLookupController {

	private static final Logger log = LoggerFactory.getLogger(CadsrLookupController.class);
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm() {
		 
		 //TODO
	     return new ModelAndView("?????");
	}	
}

