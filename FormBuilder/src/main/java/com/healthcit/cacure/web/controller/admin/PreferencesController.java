/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.web.controller.admin;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.healthcit.cacure.businessdelegates.PreferencesManager;
import com.healthcit.cacure.model.PreferenceSettings;
import com.healthcit.cacure.utils.Constants;

@Controller
public class PreferencesController {
	
	public static final String PREFERENCES_SETTINGS_NAME = "preferenceSettings";
	public static final String PREFERENCES_WAS_SAVED_NAME = "preferenceSettingsSaved";
	
	private static Logger log = LoggerFactory.getLogger( PreferencesController.class );
	
	@Autowired
	private PreferencesManager preferencesManager;
	
	@ModelAttribute
	public void createMainModel(ModelMap modelMap) 
	{
		PreferenceSettings preferenceSettings = preferencesManager.getPreferenceSettings();
		modelMap.addAttribute(PREFERENCES_SETTINGS_NAME, preferenceSettings);
		
	}
	
	@RequestMapping( value=Constants.PREFERENCES_URI, method=RequestMethod.POST )
	public ModelAndView submitForm( @ModelAttribute PreferenceSettings preferences )
	{
		log.debug( "In submitForm method..." );
		preferencesManager.savePreferenceSettings(preferences);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(PREFERENCES_WAS_SAVED_NAME, true);
		modelAndView.setViewName("preferences");
		return modelAndView;
	}

	@RequestMapping( value=Constants.PREFERENCES_URI, method=RequestMethod.GET )
	public String showForm() {
		log.debug( "In showForm method..." );
		return "preferences";
	}
}
