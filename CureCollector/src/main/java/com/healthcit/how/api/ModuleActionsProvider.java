/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.api;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.InvalidDataException;
import com.healthcit.how.businessdelegates.ModuleManager;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.models.SharingGroupModule;

public abstract class ModuleActionsProvider
{
	@Autowired
	protected ModuleManager moduleManager;
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ModuleActionsProvider.class);
	
	public enum ModuleAction {EDIT, SUBMIT, REOPEN};
	
	protected boolean isSubmittable(String moduleId, String ownerId)
	{
		boolean isSubmittable = true;
		LinkedList<QuestionnaireForm> forms = moduleManager.getReadableFormsByModuleAndOwner(ownerId, moduleId);
		for(QuestionnaireForm form: forms)
		{
			if(!FormStatus.APPROVED.equals(form.getStatus()))
			{
				isSubmittable = false;
				break;
			}
		}
		return isSubmittable;
	}
	
	protected EnumSet<ModuleAction> getSupportedActions() {
		return EnumSet.allOf(ModuleAction.class);
	}
	
	public String getSubmitModuleAction()
	{
		return ModuleAction.SUBMIT.toString();
	}
	
	public String getReOpenModuleAction()
	{
		return ModuleAction.REOPEN.toString();
	}
	
	public abstract  Map<String, List<String>> getModuleActions(List<SharingGroupModule> sModules);
	public abstract void changeModuleStatus(String moduleId, String ownerId, String entityId, String action) throws InvalidDataException;
}