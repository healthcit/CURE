/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.implementations.loveavon;


import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.InvalidDataException;
import com.healthcit.how.api.FormActionsProvider;
import com.healthcit.how.businessdelegates.FormManager;
import com.healthcit.how.models.EntityTagPermission.TagAccessPermissions;
import com.healthcit.how.models.QuestionnaireForm.FormPosition;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;

public class FormActionsProviderLoveAvon extends FormActionsProvider {

	@Autowired
	FormManager formManager;

	@Override
	public String getXFormActionElementsSection(FormPosition formPosition, FormStatus status,EnumSet<TagAccessPermissions> tagAccessPermissions)
	{
		StringBuilder xformsSubmissionSection= new StringBuilder();
		//If class is loaded as a resource via classLoader it only reads it from disc once.
		if(tagAccessPermissions.contains(TagAccessPermissions.WRITE) )
		{
			if( formPosition == FormPosition.FIRST ) {
				xformsSubmissionSection.append(getSaveFormAction() + getNextFormAction()); 
			} else {
				xformsSubmissionSection.append(getPreviousFormAction() + getSaveFormAction() + getNextFormAction());
			}
		}
		return xformsSubmissionSection.toString();
	}
	
	public void changeFormInstanceStatus(String ownerId, String entityId, String formId, Long instanceId, String action)throws InvalidDataException
	{
		//valueOf will throw IllegalArgumentException if the value doesn't belong in enum, 
		//however the use of the contains, allows for overwriting getSupportedActions to narrow down 
		//the list of available actions for this particular implementation
		if(action == null || !getSupportedActions().contains(FormAction.valueOf(action.toUpperCase())))
		{
				throw new InvalidDataException("unknown action:  " + action);
		}
		FormStatus formInstanceStatus = null;
		FormStatus currentStatus = formManager.getFormInstanceStatus(formId, ownerId, instanceId);
		if(FormAction.SUBMIT.toString().equals(action.toUpperCase()))
		{
			if(!FormStatus.IN_PROGRESS.equals(currentStatus)&& !FormStatus.SUBMITTED.equals(currentStatus))
			{
					throw new InvalidDataException("Cannot submit form instance with status " + currentStatus+ ". Form instance's status should be " + FormStatus.IN_PROGRESS + " before it can be submitted");
			}
			formInstanceStatus = FormStatus.SUBMITTED;
		}
		
		if ( formInstanceStatus != null )
		{
			formManager.updateFormInstanceData(ownerId, entityId, formId, instanceId, formInstanceStatus);
		}
		
	}
	
	protected EnumSet<FormAction> getSupportedActions() {
		return EnumSet.of(FormAction.SUBMIT);
	}
}
