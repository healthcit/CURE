/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.dao;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.xwork.StringUtils;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.healthcit.how.models.CoreEntity;
import com.healthcit.how.models.SharingGroupModule;
import com.healthcit.how.models.SharingGroupModule.EntityModuleStatus;

public class CoreEntityDao extends BaseJpaDao<CoreEntity, String> {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CoreEntityDao.class);

	public 	CoreEntityDao()
	{
		super(CoreEntity.class);
	}

	public int addCoreEntity(String entityId) {

		return em.createNativeQuery("INSERT INTO core_entity(id) VALUES ('" + entityId + "')").executeUpdate();

	}

	public int deleteFromGroup(String entityId, String groupId)
	{
		String sql = "delete from core_entity_sharing_group where entity_id =:entityId and group_id =:groupId";
		Query query = em.createNativeQuery(sql);
		query.setParameter("groupId", groupId);
		query.setParameter("entityId", entityId);
		return query.executeUpdate();
	}
//	public int assignEntityToGroup(String entityId, String groupId)
//	{
//		String sql = "update core_entity set sharing_group_id = :groupId where id = :entityId";
//		Query query = em.createNativeQuery(sql);
//		query.setParameter("groupId", groupId);
//		query.setParameter("entityId", entityId);
//		return query.executeUpdate();
//	}
	@SuppressWarnings("unchecked")
	public List<SharingGroupModule> getModulesForEntity(String ownerId, EntityModuleStatus[] status, String[] ctx)
	{
		/*
		 * The JPQL below retrieves all modules, and only
		 * Associates Entity forms pertaining to an entity
		 * !IMPORTANT! If data integrity is maintained this will always return one
		 * FormEntity per QuestionnaireForm
		 */
		String jpql = "Select em from SharingGroupModule em "
			 + " inner join em.sharingGroup e"
			 + " inner join em.module m "
			 + " where e.id = :owner_id "
		     ;
		if (ctx != null && ctx.length > 0)
		{
			String ctxs = StringUtils.join(ctx, "','");
			jpql += (" and m.context in  ('" + ctxs + "')");
		}
		if (status != null && status.length > 0)
		{
			String statuses = StringUtils.join(status, "','");
			jpql += (" and em.status in  ('" + statuses + "')");
		}
		jpql += (" order by m.deployDate ");
		Query query = em.createQuery(jpql);
		query.setParameter("owner_id", ownerId);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<SharingGroupModule> getModuleForEntity(String ownerId, String moduleId)
	{
		/*
		 * The JPQL below retrieves all modules, and only
		 * Associates Entity forms pertaining to an entity
		 * !IMPORTANT! If data integrity is maintained this will always return one
		 * FormEntity per QuestionnaireForm
		 */
		String jpql = "Select em from SharingGroupModule em "
			 + " inner join em.sharingGroup e"
			 + " inner join em.module m "
			 + " where e.id = :owner_id and m.id=:module_id";

		Query query = em.createQuery(jpql);
		query.setParameter("owner_id", ownerId);
		query.setParameter("module_id", moduleId);
		return query.getResultList();
	}
	
	public int deleteEntitiesForGroup(String groupId)
	{
		String sql = "delete from core_entity where sharing_group_id = :groupId";
		Query query = em.createNativeQuery(sql);
		query.setParameter("groupId", groupId);
		return query.executeUpdate();
	}
	
	public int deletePermissionsForEntity(String entityId)
	{
		String jpql = "delete from EntityTagPermission e where e.pk.entityId = :entityId";
		Query query = em.createQuery( jpql );
		query.setParameter( "entityId", entityId );
		return query.executeUpdate();
	}
	
	public int updateLastAccessedContext(String entityId, String context)
	{
		String jpql = "update CoreEntity e set e.lastAccessedContext = :context where e.id = :entityId";
		Query query = em.createQuery( jpql );
		query.setParameter( "entityId", entityId );
		query.setParameter( "context", context );
		return query.executeUpdate();		
	}
}
