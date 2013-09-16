/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.healthcit.cacure.model.PreferenceSettings;

public class PreferencesDao {
	private static final Logger logger = LoggerFactory.getLogger(PreferencesDao.class);

	@PersistenceContext
	protected EntityManager em;

	public void savePreferenceSettings(PreferenceSettings settings) {
		em.merge(settings);
	}
	
	public PreferenceSettings getPreferenceSettings()
	{
		Query query = em.createQuery("from PreferenceSettings fe where id = 1");
	    return (PreferenceSettings) query.getSingleResult();
	}

}
