/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.implementations.ncccp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.api.AccessServices;
import com.healthcit.how.businessdelegates.CoreEntityManager;
import com.healthcit.how.models.SharingGroupModule;
import com.healthcit.how.models.SharingGroupModule.EntityModuleStatus;
import com.healthcit.how.utils.Constants;

public class NCCCPAccessServices extends AccessServices{

	@Autowired
	CoreEntityManager coreEntityManager;
//	@Override
//	public String getAllModules(String entityId, String groupId, String[] ctx) throws Exception
//	{
////		String groupId = getSharingGroupIdForEntity(entityId);
//	
//		List<SharingGroupModule>entityModulesList = coreEntityManager.getAllModulesByStatus(groupId, Constants.STATUS_ALL, ctx);
//		markCurrentModule(entityModulesList);
//		String allModulesXml = coreEntityManager.transformEntityModulesForEntity(entityId, entityModulesList);
//		return allModulesXml;
//	}
//	
//	@Override
//	public String availableModules(String entityId, String groupId, String[] ctx) throws Exception
//	{
////		String groupId = getSharingGroupIdForEntity(entityId);
//		EntityModuleStatus[] statuses = {EntityModuleStatus.NEW, EntityModuleStatus.IN_PROGRESS};
//		List<SharingGroupModule>entityModulesList = coreEntityManager.getAllModulesByStatuses(groupId, statuses, ctx);
//		markCurrentModule(entityModulesList);
//		String allModulesXml = coreEntityManager.transformEntityModulesForEntity(entityId, entityModulesList);
//		return allModulesXml;
//		
//		
//	}
//
//	@Override
//	public String getEntityModule(String entityId, String groupId, String moduleId) throws Exception
//	{
////		String groupId = getSharingGroupIdForEntity(entityId);
//		List<SharingGroupModule>entityModulesList = coreEntityManager.getModuleById(groupId, moduleId);
//		EntityModuleStatus[] statuses = {EntityModuleStatus.NEW, EntityModuleStatus.IN_PROGRESS};
//		List<SharingGroupModule>allEntityModulesList = coreEntityManager.getAllModulesByStatuses(groupId, statuses, null);
//		
//
//		SharingGroupModule currentModule = markCurrentModule(allEntityModulesList);
//		if(currentModule.getModule().getId().equals(entityModulesList.get(0).getModule().getId()))
//		{
//			entityModulesList.get(0).setIsEditable(true);
//		}
//		String allModulesXml = coreEntityManager.transformEntityModulesForEntity(entityId, entityModulesList);
//		return allModulesXml;
//	}

//	private SharingGroupModule markCurrentModule(List<SharingGroupModule> entityModules)
//	{
//		List<SharingGroupModule> modules = new ArrayList<SharingGroupModule>();
//		for(int i=0; i< entityModules.size(); i++)
//		{
//			if(entityModules.get(i).getStatus().name().equals(Constants.STATUS_NEW) || entityModules.get(i).getStatus().name().equals(Constants.STATUS_IN_PROGRESS))
//			{
//				
//				modules.add(entityModules.get(i));
//			}
//		}
//		SharingGroupModule currentModule = null;
//		if(modules.size()>1)
//		{
//			Collections.sort(modules, new  Comparator<SharingGroupModule>() {
//				  @Override
//				public int compare(SharingGroupModule e1, SharingGroupModule e2)
//				  {
//					 Date date1 = e1.getModule().getDeployDate();
//					 Date date2 = e2.getModule().getDeployDate();
//					  return date1.compareTo(date2);
//				  }
//				
//			});
//			currentModule = modules.get(0);
//		}
//		else if(modules.size()==1)
//		{
//			currentModule = modules.get(0);
//		}
//		if(currentModule != null)
//		{
//			currentModule.setIsEditable(true);
//		}
//		return currentModule;
//	}

	
}
