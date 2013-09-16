/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.businessdelegates;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.healthcit.cacure.dao.PreferencesDao;
import com.healthcit.cacure.model.PreferenceSettings;

public class PreferencesManager {

	private static final Logger logger = LoggerFactory.getLogger(PreferencesManager.class);

	@Autowired
	private PreferencesDao preferencesDao;

	@Transactional
	public void savePreferenceSettings(PreferenceSettings settings) {
		preferencesDao.savePreferenceSettings(settings);
	}
	
	@Transactional
	public PreferenceSettings getPreferenceSettings() {
	    return preferencesDao.getPreferenceSettings();
	}
	
}
