/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.implementations.nror;


import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.businessdelegates.FormManager;
import com.healthcit.how.implementations.loveavon.FormActionsProviderLoveAvon;
import com.healthcit.how.models.EntityTagPermission.TagAccessPermissions;
import com.healthcit.how.models.QuestionnaireForm.FormPosition;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;

public class FormActionsProviderNror extends FormActionsProviderLoveAvon {

	@Autowired
	FormManager formManager;

	@Override
	public String getXFormActionElementsSection(FormPosition formPosition, FormStatus status,EnumSet<TagAccessPermissions> tagAccessPermissions)
	{
		StringBuilder xformsSubmissionSection= new StringBuilder();
		//If class is loaded as a resource via classLoader it only reads it from disc once.
		if(tagAccessPermissions.contains(TagAccessPermissions.WRITE) )
		{
			xformsSubmissionSection.append(getSubmitFormAction());
		}
		return xformsSubmissionSection.toString();
	}

}
