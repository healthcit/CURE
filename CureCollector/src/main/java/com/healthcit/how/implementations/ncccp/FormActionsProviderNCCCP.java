/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.implementations.ncccp;


import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.InvalidDataException;
import com.healthcit.how.api.FormActionsProvider;
import com.healthcit.how.businessdelegates.FormManager;
import com.healthcit.how.models.EntityTagPermission.TagAccessPermissions;
import com.healthcit.how.models.QuestionnaireForm.FormPosition;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;

public class FormActionsProviderNCCCP extends FormActionsProvider {
	
	@Autowired
	FormManager formManager;
	
	@Override
	public String getXFormActionElementsSection(FormPosition formPosition, FormStatus formStatus, EnumSet<TagAccessPermissions> tagAccessPermissions) {

//		String actions = null;
		StringBuilder actions = new StringBuilder();
		if( formPosition == FormPosition.NONE ) 
		{
			return "";
		}
		else
		{
			
			if((FormStatus.NEW.equals(formStatus)||FormStatus.IN_PROGRESS.equals(formStatus)) )
			{
				if(tagAccessPermissions.contains(TagAccessPermissions.WRITE) )
				{
					actions.append(getSaveFormAction());
					actions.append(getSubmitFormAction());
				}
			}

			else if(FormStatus.SUBMITTED.equals(formStatus) && tagAccessPermissions.contains(TagAccessPermissions.APPROVE))
			{
				actions.append(getApproveFormAction() + getDeclineFormAction());
			}
			else if(FormStatus.APPROVED.equals(formStatus) && tagAccessPermissions.contains(TagAccessPermissions.APPROVE))
			{
				actions.append(getReopenFormAction());
			}
		}
		
		return actions.toString();
	}
		
	@Override
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
		FormStatus currentFormInstanceStatus = formManager.getFormInstanceStatus(formId, ownerId, instanceId);
				
		
		if(FormAction.SUBMIT.toString().equals(action.toUpperCase()))
		{
			if(!FormStatus.IN_PROGRESS.equals(currentFormInstanceStatus) && !FormStatus.SUBMITTED.equals(currentFormInstanceStatus))
			{
				throw new InvalidDataException("Cannot submit form with status " + currentFormInstanceStatus+ ". Form's status should be " + FormStatus.IN_PROGRESS + " before it can be submitted");
			}
			
			formInstanceStatus = FormStatus.SUBMITTED;
		}
		
		else if(FormAction.DECLINE.toString().equals(action.toUpperCase()))
		{
			if(!FormStatus.SUBMITTED.equals(currentFormInstanceStatus)&& !FormStatus.APPROVED.equals(currentFormInstanceStatus))
			{
				throw new InvalidDataException("Cannot decline a form that hasn't been submitted or approved");
			}
			formInstanceStatus = FormStatus.IN_PROGRESS;	
		}
		
		else if(FormAction.APPROVE.toString().equals(action.toUpperCase()))
		{
			if(!FormStatus.SUBMITTED.equals(currentFormInstanceStatus))
			{
				throw new InvalidDataException("Cannot approve a form that hasn't been submitted");
			}
			formInstanceStatus=FormStatus.APPROVED;
		}
		
		if ( formInstanceStatus != null )
		{
			formManager.updateFormInstanceData(ownerId, entityId, formId, instanceId, formInstanceStatus);
		}
	}

	// overriding getTempate to remove all button explanatory text		
	@Override
	protected String getTempate(String templateName)
	{
		String actionTemplate = super.getTempate(templateName);
		return actionTemplate.replaceAll("(<xform:output .+?value=\"')([^']*)('\".*?/>)", "$1 $3");
	}

}
