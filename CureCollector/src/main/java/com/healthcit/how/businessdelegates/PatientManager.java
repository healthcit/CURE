/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.dao.PatientDao;
import com.healthcit.how.models.Patient;

public class PatientManager {
	
	@Autowired
	private PatientDao patientDao;
	
	public Patient getPatient( String id ) {
		UUID primaryKeyID = UUID.fromString( id );
		return patientDao.getById( primaryKeyID );
	}

}
