/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.implementations.nror;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.InvalidDataException;
import com.healthcit.how.api.ModuleActionsProvider;
import com.healthcit.how.api.ModuleActionsProvider.ModuleAction;
import com.healthcit.how.dao.FormDao;
import com.healthcit.how.models.Module;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.SharingGroupModule;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.models.SharingGroupModule.EntityModuleStatus;

public class ModuleActionsProviderNROR extends ModuleActionsProvider
{

	@Autowired
	FormDao formDao;
	
	private static final Logger log = LoggerFactory.getLogger(ModuleActionsProviderNROR.class);
			
	
	@Override
	public EnumSet<ModuleAction> getSupportedActions()
	{
		return EnumSet.allOf(ModuleActionsProvider.ModuleAction.class);
	}
	
	@Override
	public Map<String, List<String>> getModuleActions(List<SharingGroupModule> sModules)
	{
		
		Map<String, List<String>> availableActionsForModule = new HashMap<String, List<String>>();
		
		for(SharingGroupModule sModule: sModules)
		{
			List<String> availableActions = new ArrayList<String>();
			
			Map<String, String> availableFormInstances = sModule.getAvailableParentInstances();
			for(String key: availableFormInstances.keySet())
			{
				log.debug(key + " : " + availableFormInstances.get(key));
			}
			
			boolean isSubmittable = isSubmittable(sModule.getModule().getId(), sModule.getCoreEntity().getId());
			
			
			if(sModule.getStatus().equals(EntityModuleStatus.NEW))
			{
				availableActions.add(ModuleAction.EDIT.name());
			}
			else if(sModule.getStatus().equals(EntityModuleStatus.IN_PROGRESS) && isSubmittable)
			{
				availableActions.add(ModuleAction.EDIT.name());
				availableActions.add(ModuleAction.SUBMIT.name());
			}
			else if (sModule.getStatus().equals(EntityModuleStatus.IN_PROGRESS) && !isSubmittable)
			{
				availableActions.add(ModuleAction.EDIT.name());
			}
			else if (sModule.getStatus().equals(EntityModuleStatus.SUBMITTED))
			{
				availableActions.add(ModuleAction.REOPEN.name());
			}
			availableActionsForModule.put(sModule.getCoreEntity().getId(), availableActions);
		}
		//StringBuilder actions = new StringBuilder(100);
		
//		if(isSubmittable(moduleId, ownerId))
//		{
//			actions.append(getSubmitModuleAction());
//		}
		//return actions.toString();
		return availableActionsForModule;
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
	
	
	
	
//	@Override
//	protected boolean isSubmittable(String moduleId, String ownerId)
//	{
//		boolean isSubmittable = true;
//		LinkedList<QuestionnaireForm> forms = moduleManager.getReadableFormsByModuleAndOwner(ownerId, moduleId);
//		for(QuestionnaireForm form: forms)
//		{
//			if(!FormStatus.SUBMITTED.equals(form.getStatus()))
//			{
//				// ignore this form if it is one of the "skipped" forms;
//				// however, if this form was not skipped, 
//				// then the module cannot be submitted because one of the visible forms has not yet been submitted
//				if ( !isSkippedForm( ownerId, form ) ) 
//				{
//					isSubmittable = false;
//					break;
//				}
//			}
//		}
//		return isSubmittable;
//	}
	
	@Override
	protected boolean isSubmittable(String moduleId, String ownerId)
	{
//		Module module = sModule.getModule();
//		List<QuestionnaireForm> forms = module.getForms();
		/*In order for the module to be submittable all existing instances has to have the status of SUBMITTED and there should be at least one existing instance;
		 * 
		 * */
		boolean isSubmittable = true;
		LinkedList<QuestionnaireForm> forms = moduleManager.getReadableFormsByModuleAndOwner(ownerId, moduleId);
		for(QuestionnaireForm form: forms)
		{
			//Can return status for multiple isntances
			List<FormStatus> formStatuses = formDao.getFormInstanceStatusesByFormAndOwner( form.getId(), ownerId);
			boolean isInprogress = false;
			for(FormStatus formStatus: formStatuses)
			{
				if (formStatus.equals(FormStatus.SUBMITTED))
				{
					isSubmittable = true;
				}
				else if(formStatus.equals(FormStatus.IN_PROGRESS))
				{
					isSubmittable = false;
					isInprogress = true;
					break;
				}
			}
			//One of the instances has status in progress, we need to stop checking
			if(isInprogress)
			{
				break;
			}
			//getVisibleFormsByModuleAndEntity(entityId, ownerId, moduleId);
		}
		return isSubmittable;
	}
	private boolean isSkippedForm(String ownerId, QuestionnaireForm form)
	{
		List<String> skippedFormIds = moduleManager.getSkippedFormIds(ownerId);
		return skippedFormIds.contains( form.getId() );
	}
}
