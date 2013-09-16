/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.utils;

import com.healthcit.cacure.metadata.module.FormStatusType;
import com.healthcit.cacure.metadata.module.ModuleStatusType;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.models.SharingGroupModule.EntityModuleStatus;

public class AppUtils 
{

	public static FormStatusType mapFormStatusToXsdSchemaStatus(@SuppressWarnings("rawtypes") Enum status)

	{
		switch ((FormStatus)status)
		{
		case APPROVED: return FormStatusType.COMPLETED;
		case SUBMITTED: return FormStatusType.SUBMITTED;
		case IN_PROGRESS: return FormStatusType.IN_PROGRESS;
		case NEW: return FormStatusType.NEW;
		}
		// default
		return null;
	}
	

	public static ModuleStatusType mapModuleStatusToXsdSchemaStatus(@SuppressWarnings("rawtypes") Enum status)

	{
		switch ((EntityModuleStatus)status)
		{
			case SUBMITTED: return ModuleStatusType.SUBMITTED;
			case IN_PROGRESS: return ModuleStatusType.IN_PROGRESS;
			case NEW: return ModuleStatusType.NEW;
		}		
		// default
		return null;
	}

}
