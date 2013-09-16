/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import net.sf.json.JSONObject;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Hibernate;

import com.healthcit.how.models.FormSkip;
import com.healthcit.how.models.FormSkipAnswer;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.models.SharingGroup;
import com.healthcit.how.models.SharingGroupFormInstance;
import com.healthcit.how.models.SharingGroupFormInstancePk;
import com.healthcit.how.utils.FormAccessServiceUtils;
import com.healthcit.how.utils.NumberUtils;

public class SharingGroupFormInstanceDao  extends BaseJpaDao<SharingGroupFormInstance, SharingGroupFormInstancePk> {
	private static final Logger logger = LoggerFactory.getLogger(SharingGroupFormInstanceDao.class);
	
	Map<String,String> nativeSqlStatementsMap;

	public 	SharingGroupFormInstanceDao()
	{
		super(SharingGroupFormInstance.class);
	}
	
	public Map<String, String> getNativeSqlStatementsMap() {
		return nativeSqlStatementsMap;
	}

	public void setNativeSqlStatementsMap(Map<String, String> nativeSqlStatementsMap) {
		this.nativeSqlStatementsMap = nativeSqlStatementsMap;
	}

	public int setFormInstanceStatus( String ownerId,  String formId, Long instanceId, String status )
	{
		Query query = em.createNativeQuery("UPDATE sharing_group_form_instance SET status=? where form_id=? and sharing_group_id=? and instance_id=?");
		query.setParameter(1, status );
		query.setParameter(2, formId);
		query.setParameter(3, ownerId);
		query.setParameter(4, instanceId);
		return query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public List<QuestionnaireForm.FormStatus> getFormInstanceStatusList( String ownerId, String formId )
	{
		String jpql = "Select fi.status from SharingGroupFormInstance fi "
				 + " inner join fi.sharingGroup g"
				 + " inner join fi.form f "
				 + " where g.id = :owner_id and f.id=:form_id";

		Query query = em.createQuery(jpql);
		query.setParameter("owner_id", ownerId);
		query.setParameter("form_id", formId);
		return query.getResultList();
	}
	
	public QuestionnaireForm.FormStatus getFormInstanceStatus( String ownerId, String formId, Long instanceId )
	{
		try
		{
			String jpql = "Select fi.status from SharingGroupFormInstance fi "
					 + " inner join fi.sharingGroup g"
					 + " inner join fi.form f "
					 + " where g.id = :owner_id and f.id=:form_id and fi.instanceId=:instance_id";
	
			Query query = em.createQuery(jpql);
			query.setParameter("owner_id", ownerId);
			query.setParameter("form_id", formId);
			query.setParameter("instance_id", instanceId);
			return (QuestionnaireForm.FormStatus) query.getSingleResult();
		}
		catch(NoResultException ex)
		{
			logger.debug("No SharingGroupFormInstance found for formID " + formId +", groupID " + ownerId + ", instanceID " + instanceId);
			return null;
		}
	}
	
	public SharingGroupFormInstance getByOwnerAndFormIdAndInstanceId( String ownerId, String formId, Long instanceId, boolean includeSkipAnswers )
	{
		try
		{
			String jpql = "Select fi from SharingGroupFormInstance fi "
					 + " inner join fetch fi.sharingGroup g"
					 + " inner join fetch fi.form f" 
					 + (includeSkipAnswers ? " left join fetch fi.formSkipAnswers formSkipAnswers" : "") 
					 + " where g.id = :owner_id and f.id=:form_id and fi.instanceId=:instance_id";
	
			Query query = em.createQuery(jpql);
			query.setParameter("owner_id", ownerId);
			query.setParameter("form_id", formId);
			query.setParameter("instance_id", instanceId);
			return (SharingGroupFormInstance) query.getSingleResult();
		}
		catch(NoResultException ex)
		{
			logger.debug("No SharingGroupFormInstance found for formID " + formId +", groupID " + ownerId + ", instanceID " + instanceId);
			return null;
		}
	}
	
	public List<String> getSharingGroupForForm(String formId)
	{
		Query query = em.createNativeQuery("Select distinct sharing_group_id from sharing_group_form_instance where form_id=?");
		query.setParameter(1, formId);
		@SuppressWarnings("unchecked")
		List<String> sharingGroups = query.getResultList();
		return sharingGroups;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String> getAvailableParentInstances( String moduleId, String sharingGroupId )
	{
		try
		{
			String jpql = "Select s.availableParentInstances from SharingGroupModule s "
					 + " inner join s.sharingGroup g"
					 + " inner join s.module m "
					 + " inner join s.availableParentInstances p "
					 + " where g.id = :owner_id and m.id=:module_id";
	
			Query query = em.createQuery(jpql);
			query.setParameter("owner_id", sharingGroupId);
			query.setParameter("module_id", moduleId);
			return (Map<String,String>) query.getSingleResult();
		}
		catch(NoResultException ex)
		{
			return null;
		}
	}
	

	public int updateFormInstanceData( String ownerId,  String entityId, String formId, Long instanceId, String status )
	{
		Query query = em.createNativeQuery("UPDATE sharing_group_form_instance SET status=?, lastupdated=?, entity_id = ?, creationdate = (case when status='NEW' then now() else creationdate end) where form_id=? and sharing_group_id=? and instance_id=?");
		query.setParameter(1,status );
		query.setParameter(2, new Date());
		query.setParameter(3, entityId);
		query.setParameter(4, formId);
		query.setParameter(5, ownerId);
		query.setParameter(6, instanceId);
		int result = query.executeUpdate();
		
		if ( result == 0 )
		{
			Query query2 =  em.createNativeQuery("INSERT INTO sharing_group_form_instance(form_id, sharing_group_id, entity_id, status, instance_id, lastupdated) " +
			"VALUES (?1, ?2, ?3, ?4, ?5, now())");
			query2.setParameter(1, formId);
			query2.setParameter(2, ownerId);
			query2.setParameter(3, entityId);
			query2.setParameter(4, status);
			query2.setParameter(5, instanceId);
			return query2.executeUpdate();
		}
								
		return result;
	}
	
	public int updateFormInstanceData( String ownerId,  String entityId, String formId, Long instanceId, Long parentInstanceId, String status )
	{
		if ( parentInstanceId == null ) 
			return updateFormInstanceData(ownerId, entityId, formId, instanceId, status);
		
		Query query = em.createNativeQuery("UPDATE sharing_group_form_instance SET parent_instance_id=?, status=?, lastupdated=?, entity_id = ?, creationdate = (case when status='NEW' then now() else creationdate end) where form_id=? and sharing_group_id=? and instance_id=?");
		query.setParameter(1,parentInstanceId );
		query.setParameter(2,status );
		query.setParameter(3, new Date());
		query.setParameter(4, entityId);
		query.setParameter(5, formId);
		query.setParameter(6, ownerId);
		query.setParameter(7, instanceId);
		int result = query.executeUpdate();
		
		if ( result == 0 )
		{
			Query query2 =  em.createNativeQuery("INSERT INTO sharing_group_form_instance(parent_instance_id, form_id, sharing_group_id, entity_id, status, instance_id, lastupdated) " +
			"VALUES (?1, ?2, ?3, ?4, ?5, ?6, now())");
			query2.setParameter(1, parentInstanceId);
			query2.setParameter(2, formId);
			query2.setParameter(3, ownerId);
			query2.setParameter(4, entityId);
			query2.setParameter(5, status);
			query2.setParameter(6, instanceId);
			return query2.executeUpdate();
		}
								
		return result;
	}
	

	@SuppressWarnings("unchecked")
	public void assignAllFormInstancesToNewParentInstance(Long parentInstanceId, QuestionnaireForm form) {
		// If form == null then return
		if ( form == null ) return;
		
		// Else,
		if ( form.isChildForm() )
		{

			// First, create all parent form instances if they do not exist 
			Query query1 = em.createQuery("SELECT g from SharingGroup g where g.id not in " +
					"(SELECT sg.sharingGroup.id from SharingGroupFormInstance sg where sg.form.id=:parent_form_id)");
			query1.setParameter("parent_form_id", form.getParentForm().getId());
			List<SharingGroup> results = (List<SharingGroup>)query1.getResultList();
			QuestionnaireForm parentForm = em.find(QuestionnaireForm.class, form.getParentForm().getId());
			for ( SharingGroup owner : results )
			{
				SharingGroupFormInstance instance = new SharingGroupFormInstance(owner,parentForm,parentInstanceId); 
				instance.setStatus(FormStatus.IN_PROGRESS);
				this.create(instance);
			}			

			// Next, associate all instances of the child form with the given parent instance ID
			Query query3 = em.createQuery("UPDATE SharingGroupFormInstance sg set sg.parentInstanceId=:parent_instance_id where sg.form.id=:form_id" );
			query3.setParameter("parent_instance_id", parentInstanceId);
			query3.setParameter("form_id", form.getId());		
			query3.executeUpdate();
		}
		
		else // This form used to be a child form, but no longer is
		{
			Query query = em.createQuery("UPDATE SharingGroupFormInstance sg set sg.parentInstanceId=null where sg.form.id=:form_id" );
			query.setParameter("form_id", form.getId());		
			query.executeUpdate();
		}
		
		
		
		// Proceed to assign correct parents to all instances of the parent form, if any
		assignAllFormInstancesToNewParentInstance( parentInstanceId, form.getParentForm() );
		
	}
	
	public List<Object[]> getNonVisibleInstances( String triggerFormId, Long triggerInstanceId, String ownerId )
	{
		String sql = ( String ) nativeSqlStatementsMap.get( "getSkippedFormInstances" );
		Query query = em.createNativeQuery( sql );
		query.setParameter( 1, triggerFormId );
		query.setParameter( 2, triggerInstanceId );
		query.setParameter( 3, ownerId );
		
		
		// This query will return a list of arrays with the following structure: [ ownerId, formId, instanceId ] 
		// They represent the instances to be deleted for that owner
		@SuppressWarnings("unchecked") List<Object[]> deleteList = ( List<Object[]> ) query.getResultList();
		return deleteList;
		
	}
	
	public List<String> getSkippedFormIds( String ownerId )
	{
		String sql = ( String ) nativeSqlStatementsMap.get( "getSkippedForms" );
		Query query = em.createNativeQuery( sql );
		query.setParameter( 1, ownerId );
		
		// This query will return a list of form IDs
		@SuppressWarnings("unchecked") List<String> skippedFormIds = ( List<String> )  query.getResultList();
		return skippedFormIds;		
	}
	
	/**
	 * Returns the instanceId that corresponds to the form instance with the given formId, ownerId, ordinal and ancestorOrdinals.
	 * 
	 * NOTE:  
	 * 1) "treePath" is the list of ancestors of this instance (starting from the root), plus the current instance itself.
	 * In terms of binary tree concepts, it corresponds to the BST path for this instance within its form hierarchy tree.
	 * It is represented in terms of the ordinals of the instances.
	 * @param treePath
	 * @param formId
	 * @param ownerId
	 * @return
	 */
	public Long[] getInstanceIdAndParentInstanceIdByTreePath( String treePath, String formId, String ownerId )
	{
		// If the form ID is null, then return null
		if ( formId == null ) return null;
		
		// NOTE: Native SQL was used here for performance purposes
		String sql = ( String ) nativeSqlStatementsMap.get( "getInstanceIdAndParentInstanceIdByTreePath" );
		Query query = em.createNativeQuery( sql );
		query.setParameter( 1, ownerId );
		query.setParameter( 2, formId );
		query.setParameter( 3, treePath );
		@SuppressWarnings("unchecked") List<Object[]> results = ( List<Object[]> ) query.getResultList();
		if ( results.isEmpty() ) {
			return new Long[] { null, null };
		}
		
		else {
			Object[] record = results.get( 0 ); 
			Long[] ids = new Long[ 2 ];
			ids[ 0 ] = ( record == null ? null : 
							( record[ 0 ] == null ? null : NumberUtils.parseLong( record[ 0 ].toString() ) ) );
			ids[ 1 ] = ( record == null ? null : 
							( record[ 1 ] == null ? null : NumberUtils.parseLong( record[ 1 ].toString() ) ) );
			return ids;
		}
	}
		
	public void createAvailableParentInstances( String triggerFormId, Long triggerInstanceId, String ownerId )
	{
		// NOTE: Native SQL was used for performance-based reasons
		
		String sql = ( String ) nativeSqlStatementsMap.get( "getAvailableParentForms" );
		Query selectQuery = em.createNativeQuery( sql );
		selectQuery.setParameter( 1, triggerFormId );
		selectQuery.setParameter( 2, triggerInstanceId );
		selectQuery.setParameter( 3, ownerId );
		
		// This query will return a list of arrays with the following structure: [ triggerFormId, triggerInstanceId, formId, ownerId, moduleId ]
		@SuppressWarnings("unchecked") List<Object[]> parentList = ( List<Object[]> ) selectQuery.getResultList();
		
		// First delete any related records before inserting
		String deleteSql = "delete from available_parent_form where parent_form_id=:trigger_form_id and parent_instance_id=:trigger_instance_id and sharing_group_id=:owner_id";
		Query deleteQuery = em.createNativeQuery(deleteSql);
		deleteQuery.setParameter("trigger_form_id", triggerFormId);
		deleteQuery.setParameter("trigger_instance_id", triggerInstanceId);
		deleteQuery.setParameter("owner_id", ownerId);
		deleteQuery.executeUpdate();
		
		
		// Now, insert the records
		String insertSql = "insert into available_parent_form(parent_form_id, parent_instance_id, child_form_id, sharing_group_id, module_id)" +
				"values(:trigger_form_id, :trigger_instance_id, :child_form_id, :owner_id, :module_id)"; 				
		
		for ( Object[] parent : parentList )
		{
			Query insertQuery = em.createNativeQuery(insertSql);
			insertQuery.setParameter("trigger_form_id", parent[0]);
			insertQuery.setParameter("trigger_instance_id", parent[1]);
			insertQuery.setParameter("child_form_id", parent[2]);
			insertQuery.setParameter("owner_id", parent[3]);
			insertQuery.setParameter("module_id", parent[4]);
			insertQuery.executeUpdate();
		}
	}
	
	public void deleteNonVisibleEntities( List<Object[]> params )
	{
		String hql = "delete from SharingGroupFormInstance sg " +
				     "where sg.sharingGroup.id = :owner and sg.form.id = :form and sg.instanceId = :instance and sg.status != :status";
		
		for ( Object[] param : params )
		{			
			Query query = em.createQuery(hql);			
			query.setParameter("owner",param[0]);
			query.setParameter("form",param[1]);
			query.setParameter("instance",param[2] == null ? param[2] : ((Number)param[2]).longValue());	
			query.setParameter("status", FormStatus.NEW);
			logger.info("Deleting from DB: Form ID======" + String.valueOf(param[1]==null ? "" :param[1]) + ", Instance ID=======" + String.valueOf(param[2]==null ? "" : param[2]));
			query.executeUpdate();			
		}	
		
	}
	
	public void saveFormSkipAnswers( JSONObject jsonForm, String triggerFormId, Long triggerInstanceId, String ownerId )
	{
		SharingGroupFormInstance formInstance = getByOwnerAndFormIdAndInstanceId(ownerId, triggerFormId, triggerInstanceId, true);	
		
		Hibernate.initialize( formInstance.getForm().getFormSkipAffectees() );
		
		List<FormSkip> skipAffectees = formInstance.getForm().getFormSkipAffectees();		
		
		List<FormSkipAnswer> formSkipAnswers = FormAccessServiceUtils.getPossibleSkipTriggeringAnswers(jsonForm, formInstance, skipAffectees);
		
		formInstance.setFormSkipAnswers(formSkipAnswers);
		
		save( formInstance );
	}

}
