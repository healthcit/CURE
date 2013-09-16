/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cacure.security.Entities;
import com.healthcit.cacure.security.Entity;
import com.healthcit.cacure.security.Group;
import com.healthcit.cacure.security.GroupRef;
import com.healthcit.cacure.security.ObjectFactory;
import com.healthcit.cacure.security.Security;
import com.healthcit.cacure.security.SharingGroups;
import com.healthcit.how.dao.SharingGroupDao;
import com.healthcit.how.models.CoreEntity;
import com.healthcit.how.models.SharingGroup;


public class SharingGroupManager {

	
	@Autowired
	private SharingGroupDao sharingGroupDao;

	public SharingGroup addNewSharingGroup(SharingGroup sharingGroup) {
		return sharingGroupDao.create(sharingGroup);
	}
	
	public SharingGroup getSharingGroup(String id){

		return sharingGroupDao.getById(id);
	}
	
	public List<SharingGroup> getSharingGroupsByName(String name)
	{
		return sharingGroupDao.getSharingGroupByName(name);
	}
	
	public boolean renameSharingGroup(String oldName, String newName)
	{
		return sharingGroupDao.renameSharingGroup(oldName, newName) > 0;
	}
	
	public void getAllSharingGroups(PrintWriter os)throws Exception
	{
		List<SharingGroup> sGroups = sharingGroupDao.list();
		JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.security");
		//Create unmarshaller
		
		Marshaller m  = jc.createMarshaller();
		ObjectFactory of = new ObjectFactory();
		Security security = of.createSecurity();
		SharingGroups groups = of.createSharingGroups();
		@SuppressWarnings("unused")
		Entities entities = of.createEntities();
		for(SharingGroup sGroup: sGroups)
		{
			Group group = of.createGroup();
			group.setId(sGroup.getId());
			group.setName(sGroup.getName());
			GroupRef groupRef = of.createGroupRef();
			groupRef.setRef(group);
/*			
			Set<CoreEntity> cEntities = sGroup.getEntities();
			for(CoreEntity cEntity: cEntities)
			{
				Entity entity = of.createEntity();
				entity.setId(cEntity.getId());
				entity.setSharingGroup(groupRef);
				entities.getEntity().add(entity);
			}
			security.setEntities(entities);
*/			
			groups.getGroup().add(group);
		}
		security.setSharingGroups(groups);
		
		m.marshal(of.createSecurity(security), os);

	}
	
	public void deleteSharingGroup(SharingGroup sGroup)
	{
		sharingGroupDao.delete(sGroup);
	}
	
	public void getEntitiesForSharingGroup(String groupId, PrintWriter os)throws Exception
	{
		SharingGroup sGroup = sharingGroupDao.getById(groupId);
		JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.security");
		//Create unmarshaller
		
		Marshaller m  = jc.createMarshaller();
		ObjectFactory of = new ObjectFactory();
		Security security = of.createSecurity();
		SharingGroups groups = of.createSharingGroups();
		Entities entities = of.createEntities();
		Group group = of.createGroup();
		group.setId(sGroup.getId());
		group.setName(sGroup.getName());
		GroupRef groupRef = of.createGroupRef();
		groupRef.setRef(group);
			
		Set<CoreEntity> cEntities = sGroup.getEntities();
		for(CoreEntity cEntity: cEntities)
		{
			Entity entity = of.createEntity();
			entity.setId(cEntity.getId());
			entity.setSharingGroup(groupRef);
			entities.getEntity().add(entity);
		}
		security.setEntities(entities);
		
		groups.getGroup().add(group);
		security.setSharingGroups(groups);
		
		m.marshal(of.createSecurity(security), os);

	}
}
