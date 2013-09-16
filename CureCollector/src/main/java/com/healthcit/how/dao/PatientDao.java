/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.dao;

import java.util.UUID;

import com.healthcit.how.models.Patient;

public class PatientDao extends BaseJpaDao<Patient, UUID> {
	public PatientDao()
	{
		super(Patient.class);
	}
}
