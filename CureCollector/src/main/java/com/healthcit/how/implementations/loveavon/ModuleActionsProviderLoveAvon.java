/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.implementations.loveavon;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.healthcit.how.InvalidDataException;
import com.healthcit.how.api.ModuleActionsProvider;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.SharingGroupModule;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.models.SharingGroupModule.EntityModuleStatus;

public class ModuleActionsProviderLoveAvon extends ModuleActionsProvider
{

	
	@Override
	public EnumSet<ModuleAction> getSupportedActions()
	{
		return EnumSet.allOf(ModuleActionsProvider.ModuleAction.class);
	}
	
	//@Override
	public String getModuleActions(String moduleId, String ownerId, String entityId)
	{
		
		StringBuilder actions = new StringBuilder(100);
		if(isSubmittable(moduleId, ownerId))
		{
			actions.append(getSubmitModuleAction());
		}
		return actions.toString();
	}
		
//	@Override
//	public void changeModuleStatus(String moduleId, String ownerId, String entityId, String action) throws InvalidDataException
//	{
//		//valueOf will throw IllegalArgumentException if the value doesn't belong in enum, 
//		//however the use of the contains, allows for overwriting getSupportedActions to narrow down 
//		//the list of available actions for this particular implementation
//		if(action == null || !getSupportedActions().contains(ModuleAction.valueOf(action.toUpperCase())))
//		{
//			throw new InvalidDataException("unknown action:  " + action);
//		}
//		EntityModuleStatus moduleStatus = null;
//		if(ModuleAction.SUBMIT.toString().equals(action.toUpperCase()))
//		{
//			if(isSubmittable(moduleId, ownerId))
//			{
//				moduleStatus=EntityModuleStatus.SUBMITTED;
//			}
//			else
//			{
//				throw new InvalidDataException("Cannot submit the module because it is not in submittable state");
//			}
//		}
//
//		moduleManager.updateEntityModuleStatus(ownerId, moduleId, entityId, moduleStatus.toString());
//	}
//	
	@Override
	public Map<String, List<String>> getModuleActions(List<SharingGroupModule> sModules)
	{
		return null;
	}
	
	@Override
	public void changeModuleStatus(String moduleId, String ownerId, String entityId, String action) throws InvalidDataException
	{
		//valueOf will throw IllegalArgumentException if the value doesn't belong in enum, 
		//however the use of the contains, allows for overwriting getSupportedActions to narrow down 
		//the list of available actions for this particular implementation
		if(action == null || !getSupportedActions().contains(ModuleAction.valueOf(action.toUpperCase())))
		{
			throw new InvalidDataException("unknown action:  " + action);
		}
		EntityModuleStatus moduleStatus = null;
		if(ModuleAction.SUBMIT.toString().equals(action.toUpperCase()))
		{
			if(isSubmittable(moduleId, ownerId))
			{
				moduleStatus=EntityModuleStatus.SUBMITTED;
			}
			else
			{
				throw new InvalidDataException("Cannot submit the module because it is not in submittable state");
			}
		}
		else if (ModuleAction.REOPEN.toString().equals(action.toUpperCase()))
		{
			EntityModuleStatus currentModuleStatus = moduleManager.getEntityModuleStatus(moduleId, ownerId);
			if(EntityModuleStatus.SUBMITTED.equals(currentModuleStatus))
			{
				moduleStatus=EntityModuleStatus.IN_PROGRESS;
			}
			else
			{
				throw new InvalidDataException("Cannot reopen module that has not been submitted");
			}
		}

		moduleManager.updateEntityModule(moduleId, ownerId, entityId, moduleStatus.toString());
	}
	
	
	
	
	@Override
	protected boolean isSubmittable(String moduleId, String ownerId)
	{
		boolean isSubmittable = true;
		LinkedList<QuestionnaireForm> forms = moduleManager.getReadableFormsByModuleAndOwner(ownerId, moduleId);
		for(QuestionnaireForm form: forms)
		{
			if(!FormStatus.SUBMITTED.equals(form.getStatus()))
			{
				// ignore this form if it is one of the "skipped" forms;
				// however, if this form was not skipped, 
				// then the module cannot be submitted because one of the visible forms has not yet been submitted
				if ( !isSkippedForm( ownerId, form ) ) 
				{
					isSubmittable = false;
					break;
				}
			}
		}
		return isSubmittable;
	}
	
	private boolean isSkippedForm(String ownerId, QuestionnaireForm form)
	{
		List<String> skippedFormIds = moduleManager.getSkippedFormIds(ownerId);
		return skippedFormIds.contains( form.getId() );
	}
}