/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;


import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cacure.security.EntityTagPermissions;
import com.healthcit.cacure.security.FormTag;
import com.healthcit.cacure.security.FormTagPermission;
import com.healthcit.cacure.security.FormTagPermissions;
import com.healthcit.cacure.security.ModuleTagPermissions;
import com.healthcit.cacure.security.ObjectFactory;
import com.healthcit.cacure.security.Permissions;
import com.healthcit.cacure.security.Security;
import com.healthcit.how.models.CoreEntity;
import com.healthcit.how.models.EntityTagPermission;
import com.healthcit.how.models.EntityTagPermission.TagAccessPermissions;

public class EntityPermissionsManager {
	
	@Autowired
	FormManager formManager;
	@Autowired
	CoreEntityManager entityManager;
	
	public void saveEntityPermissions(String entityId, String xml) throws Exception
	{
		JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.security");
		//Create unmarshaller
		
		Unmarshaller m  = jc.createUnmarshaller();
		final String XML_INPUT = xml;

		ByteArrayInputStream input = new ByteArrayInputStream (XML_INPUT.getBytes());
		@SuppressWarnings("rawtypes")
		JAXBElement element = (JAXBElement) m.unmarshal(input);
		
		Security security = (Security) element.getValue ();
		Permissions permissionsJaxb = security.getPermissions();
		List<EntityTagPermissions> entityTagPermissionsListJaxb = permissionsJaxb.getEntityTagPermissions();
		
		// Delete all permissions before adding the new ones

		
		for(EntityTagPermissions entityTagPermissionsJaxb: entityTagPermissionsListJaxb)
		{
			String coreEntityId = entityTagPermissionsJaxb.getEntityId();
//			String groupId = entityTagPermissionsJaxb.getGroupId();
			if(coreEntityId.equals(entityId))
			{
				entityManager.deletePermissionsForEntity( entityId);
				CoreEntity entity = entityManager.getCoreEntity(coreEntityId);
				
				FormTagPermissions formTagPermissionsJaxb = entityTagPermissionsJaxb.getFormTagPermissions();
				List<EntityTagPermission> entityTagPermissions = new ArrayList<EntityTagPermission>();
				List<FormTag> formTagsJaxb = formTagPermissionsJaxb.getTag();
				for(FormTag formTag: formTagsJaxb)
				{
					String tagId = formTag.getId();
					List<FormTagPermission> formTagPermissionJaxb = formTag.getFormTagPermission();
					for(FormTagPermission permissionJaxb : formTagPermissionJaxb)
					{
						EntityTagPermission entityTagPermission = new EntityTagPermission(coreEntityId, tagId, TagAccessPermissions.valueOf(permissionJaxb.toString()));
						entityTagPermissions.add(entityTagPermission);
					}
				}
				entity.setFormTagAccessPermissions(entityTagPermissions);
				ModuleTagPermissions moduleTagPermissionsJaxb = entityTagPermissionsJaxb.getModuleTagPermissions();
				entity.setModuleTagAccessPermissions();
				entityManager.updateCoreEntity(entity);
			}
		}
		
	}

	
	public boolean hasReadAccess(String entityId,  String formId)
	{
		String tagId = formManager.getFormTagId(formId);
//		QuestionnaireForm form = formManager.getForm(formId);
		CoreEntity entity = entityManager.getCoreEntity(entityId);
		boolean canRead = entity.canRead(tagId);
		return canRead;
	}
	
	public EnumSet<TagAccessPermissions> getTagAccessPermissions(String entityId, String formId)
	{
		String tagId = formManager.getFormTagId(formId);
//		QuestionnaireForm form = formManager.getForm(formId);
		CoreEntity entity = entityManager.getCoreEntity(entityId);
		EnumSet<TagAccessPermissions> permissions = null;
		if( tagId != null && entity!= null)
		{
			permissions = entity.getTagPermissionsForTag(tagId);
		}
		return permissions;
	}
	
	
	public void getPermissions(PrintWriter out) throws Exception
	{
		JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.security");
		//Create unmarshaller
		
		Marshaller m  = jc.createMarshaller();
		ObjectFactory of = new ObjectFactory();
		Security security = of.createSecurity();
		
		Permissions permissionsJaxb = of.createPermissions();
		security.setPermissions(permissionsJaxb);
		List<EntityTagPermissions> entityTagPermissionsJaxb = permissionsJaxb.getEntityTagPermissions();
		List<CoreEntity> entities = entityManager.getAllCoreEntities();
		for (CoreEntity entity: entities)
		{
			populateEntityPermissions(entityTagPermissionsJaxb, entity, of);

		}
		m.marshal(of.createSecurity(security), out);
	}
	
	public void getPermissionsForEntity(String entityId,  PrintWriter out) throws Exception
	{
		JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.security");
		//Create unmarshaller
		
		Marshaller m  = jc.createMarshaller();
		ObjectFactory of = new ObjectFactory();
		Security security = of.createSecurity();
		
		
		Permissions permissionsJaxb = of.createPermissions();
		security.setPermissions(permissionsJaxb);
		CoreEntity entity = entityManager.getCoreEntity(entityId);
		List<EntityTagPermissions> entityTagPermissionsJaxb = permissionsJaxb.getEntityTagPermissions();
		populateEntityPermissions(entityTagPermissionsJaxb, entity, of);	
		m.marshal(of.createSecurity(security), out);
	}
	
//	private void populateEntityPermissions(List<EntityTagPermissions> entityTagPermissionsJaxb, CoreEntity entity, ObjectFactory of)
//	{
//		Collection<EntityTagPermission> permissions = entity.getTagPermissions();
//		
//		for(SharingGroup sharingGroup: entity.getSharingGroups())
//		{
//			Map<String, List<FormTagPermission>> tagPermissionMap = new HashMap<String, List<FormTagPermission>>();
//
//			if(groupId != null && !sharingGroup.getId().equals(groupId))
//			{
//				continue;
//			}
//			EntityTagPermissions entityPermissionsJaxb = of.createEntityTagPermissions();
//			entityTagPermissionsJaxb.add(entityPermissionsJaxb);
//			entityPermissionsJaxb.setEntityId(entity.getId());
//			FormTagPermissions formTagPermissionsJaxb = of.createFormTagPermissions();
//			entityPermissionsJaxb.setFormTagPermissions(formTagPermissionsJaxb);
//			ModuleTagPermissions moduleTagPermissionsJaxb = of.createModuleTagPermissions();
//			entityPermissionsJaxb.setModuleTagPermissions(moduleTagPermissionsJaxb);
//
//			List<FormTag> formTagsJaxb = formTagPermissionsJaxb.getTag();
//			
//			for(EntityTagPermission permission: permissions)
//			{
//				String tagId = permission.getTagId();
//				/* If permission is for a current group */
//				if(sharingGroup.getId().equals(permission.getGroupId()))
//				{
//						String key = tagId + "-" + sharingGroup.getId();
//				
////				if(!tagPermissionMap.containsKey(tagId))
////				{
////					tagPermissionMap.put(tagId, new ArrayList<FormTagPermission>(4));
////				}
////				tagPermissionMap.get(tagId).add(FormTagPermission.valueOf(permission.getAccessPermission().toString()));
//					if(!tagPermissionMap.containsKey(key))
//					{
//						tagPermissionMap.put(key, new ArrayList<FormTagPermission>(4));
//					}
//					tagPermissionMap.get(key).add(FormTagPermission.valueOf(permission.getAccessPermission().toString()));
//				}
//			}
//			for(String key : tagPermissionMap.keySet())
//			{
//				FormTag formTagJaxb = of.createFormTag();
//				int index = key.indexOf('-');
//				String tagId = key.substring(0, index);
//				formTagJaxb.setId(tagId);
//				formTagJaxb.getFormTagPermission().addAll(tagPermissionMap.get(key));
//				formTagsJaxb.add(formTagJaxb);
//			}
//		}
//		
//	}
	private void populateEntityPermissions(List<EntityTagPermissions> entityTagPermissionsJaxb, CoreEntity entity, ObjectFactory of)
	{
		
		EntityTagPermissions entityPermissionsJaxb = of.createEntityTagPermissions();
		entityTagPermissionsJaxb.add(entityPermissionsJaxb);
		entityPermissionsJaxb.setEntityId(entity.getId());
		FormTagPermissions formTagPermissionsJaxb = of.createFormTagPermissions();
		entityPermissionsJaxb.setFormTagPermissions(formTagPermissionsJaxb);		
		ModuleTagPermissions moduleTagPermissionsJaxb = of.createModuleTagPermissions();
		entityPermissionsJaxb.setModuleTagPermissions(moduleTagPermissionsJaxb);
		
		Collection<EntityTagPermission> permissions = entity.getTagPermissions();
		
		List<FormTag> formTagsJaxb = formTagPermissionsJaxb.getTag();
		Map<String, List<FormTagPermission>> tagPermissionMap = new HashMap<String, List<FormTagPermission>>();
		for(EntityTagPermission permission: permissions)
		{
			String tagId = permission.getTagId();
			if(!tagPermissionMap.containsKey(tagId))
			{
				tagPermissionMap.put(tagId, new ArrayList<FormTagPermission>(4));
			}
			tagPermissionMap.get(tagId).add(FormTagPermission.valueOf(permission.getAccessPermission().toString()));
		}
		for(String tagId : tagPermissionMap.keySet())
		{
			FormTag formTagJaxb = of.createFormTag();
			formTagJaxb.setId(tagId);
			formTagJaxb.getFormTagPermission().addAll(tagPermissionMap.get(tagId));
			formTagsJaxb.add(formTagJaxb);
		}
	}
	
	
	public static void main(String args[]) {
		EntityPermissionsManager em = new EntityPermissionsManager();
		String xml = "" +
				"<security>" +
					"<permissions>" +
						"<entityTagPermissions entityId='3c6b1680-3a6c-40c8-8382-827667354ddf'>" +							
							"<tag>" +
								"<formTag id='Subcontractor Contact Information'>" +
									"<formTagPermissions>" +								
										"<formPermission>read</formPermission>" +
										"<formPermission>write</formPermission>" +
										"<formPermission>submit</formPermission>" +
										"<formPermission>approve</formPermission>" +
									"</formTagPermissions>" +
								"</formTag>" +
							"</tag>" +														
						"</entityTagPermissions>" +						
					"</permissions>" +					
				"</security>";		
		try {
			em.saveEntityPermissions("3c6b1680-3a6c-40c8-8382-827667354ddf", xml);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
