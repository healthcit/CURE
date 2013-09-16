/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.implementations.ncccp;



import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.InvalidDataException;
import com.healthcit.how.api.ModuleActionsProvider;
import com.healthcit.how.businessdelegates.ModuleManager;
import com.healthcit.how.models.SharingGroupModule;
import com.healthcit.how.models.SharingGroupModule.EntityModuleStatus;

public class ModuleActionsProviderNCCCP extends ModuleActionsProvider
{
	@Autowired
	ModuleManager moduleManager;
	
	//@Override
	public String getModuleActions(String moduleId, String ownerId, String entityId)
	{
		
		StringBuilder actions = new StringBuilder(100);
		EntityModuleStatus moduleStatus = moduleManager.getEntityModuleStatus(moduleId, ownerId);
		if(EntityModuleStatus.SUBMITTED.equals(moduleStatus))
		{
			actions.append(getReOpenModuleAction());
		}
		else if(isSubmittable(moduleId, ownerId))
		{
			actions.append(getSubmitModuleAction());
		}
		return actions.toString();
	}
	
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

		moduleManager.updateEntityModule(moduleId, ownerId, entityId,  moduleStatus.toString());
	}
}
