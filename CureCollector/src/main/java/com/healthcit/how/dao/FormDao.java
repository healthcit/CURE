/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.dao;


import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.healthcit.how.models.EntityTagPermission.TagAccessPermissions;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.models.SharingGroup;
import com.healthcit.how.models.SharingGroupFormInstance;
import com.healthcit.how.utils.CollectionUtils;


public class FormDao extends BaseJpaDao<QuestionnaireForm, String> {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(FormDao.class);

	public FormDao() {
		super(QuestionnaireForm.class);
	}

	/**
	 * @return List of QuestionnaireForm items
	 */

	@SuppressWarnings("unchecked")
	//public List<QuestionnaireForm> getStaleForms(int days) {
	public List<SharingGroupFormInstance> getStaleForms(int days) {

		Date cutoffDate = new Date();
		cutoffDate.setTime(cutoffDate.getTime() - days*24*3600*1000); //Subtracting number of millisecs in days)

		String jpql = "Select fi from SharingGroupFormInstance fi "
			 + " where fi.status = :status "
			 + " and fi.lastUpdated < :now "
			  ;
		Query query = em.createQuery(jpql);
		query.setParameter("status", FormStatus.NEW);
		query.setParameter("now", cutoffDate);
		return query.getResultList();
	}

	public void updateStaleForms(String formId, String entityId) {

		String jpqlUpdate = "update SharingGroupFormInstance set lastUpdated = :today ????";

		Query query = em.createQuery( jpqlUpdate );
		query.setParameter( "today", new Date() );
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	public List<QuestionnaireForm> getModuleFormsForEntity(String moduleId, String ownerId)
	{
		em.clear();
		/*
		 * The JPQL below retrieve all forms in a specified module, and only
		 * Associates Entity forms pertaining to an entity
		 * !IMPORTANT! If data integrity is maintained this will always return one
		 * FormEntity per QuestionnaireForm
		 */
		String jpql = "Select distinct f from SharingGroupFormInstance fi "
			 + " inner join fi.form f inner join fi.sharingGroup g"
			 + " inner join f.module m "
			 + " where m.id = :module_id "
			 + " and g.id = :owner_id "
			 + " order by f.order "
			  ;
			Query query = em.createQuery(jpql);
			query.setParameter("module_id", moduleId);
			query.setParameter("owner_id", ownerId);
			return query.getResultList();
	}
	
	public int updateSharingGroupFormInstanceStatusFromCompleteToInProgres(String formId) {

		Query query =  em.createNativeQuery("UPDATE sharing_group_form_instance SET status=:status WHERE form_id=:form_id and status=:completed");
		query.setParameter("form_id", formId);
		query.setParameter("status", FormStatus.IN_PROGRESS.toString());
		query.setParameter("completed", FormStatus.SUBMITTED.toString());
		return query.executeUpdate();
	}
		
	public String getFormTagId(String formId)
	{
		Query query=  em.createNativeQuery("SELECT tag_id from forms where id=:formId");
		query.setParameter("formId", formId);
		String tagId = null;
		try{
			tagId = (String)query.getSingleResult();
		}
		catch(javax.persistence.NoResultException e)
		{
			logger.debug("no tag found for form : " + formId);
		}
		return tagId;
		
	}
	
	public Long getMaxAvailableFormInstanceId(String formId)
	{
		String jpql = "Select f.maxInstances from QuestionnaireForm f where f.id= :id";
		Query query = em.createQuery( jpql );
		query.setParameter( "id", formId );
		return ( Long ) query.getSingleResult() ;
	}
	
	public QuestionnaireForm getFormByName(String name)
	{
		QuestionnaireForm form = null;
		String jpql = "Select f from QuestionnaireForm f "
			 + " join fetch f.tag t  where f.name= :name";
			 
		Query query=  em.createQuery(jpql);
		query.setParameter("name", name);
		query.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<QuestionnaireForm> forms = (List<QuestionnaireForm>)query.getResultList();
		if(forms.size()>0)
		{
			form = forms.get(0);
		}
		return form;
		
	}
	
	public QuestionnaireForm getFormByEagerLoading(String formId) 
	{
		try
		{
			String jpql = "Select f from QuestionnaireForm f "
					 + " left join fetch f.formSkipAffectees formSkipAffectees" 
					 + " where f.id=:form_id";
	
			Query query = em.createQuery(jpql);
			query.setParameter("form_id", formId);
			return (QuestionnaireForm) query.getSingleResult();
		}
		catch(NoResultException ex)
		{
			return null;
		}
	}
	

	@SuppressWarnings("unchecked")
	public List<SharingGroupFormInstance> getAvailableFormInstancesByFormAndOwner(String formId, String ownerId){
		String jpql = "Select fi from SharingGroupFormInstance fi "
				 + " join fi.form f join fi.sharingGroup g "
				 + " where f.id= :form_id and g.id = :sharing_group_id and fi.status != :status";
				 
		Query query=  em.createQuery(jpql);
		query.setParameter("form_id", formId);
		query.setParameter("sharing_group_id", ownerId);
		query.setParameter("status", FormStatus.NEW);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<FormStatus> getFormInstanceStatusesByFormAndOwner(String formId, String ownerId){
		/* Return statuses for multiple instances of the same form*/
		String jpql = "Select fi.status from SharingGroupFormInstance fi "
				 + " join fi.form f join fi.sharingGroup g "
				 + " where f.id= :form_id and g.id = :sharing_group_id";
		//FormStatus status = null;		 
		Query query=  em.createQuery(jpql);
		query.setParameter("form_id", formId);
		query.setParameter("sharing_group_id", ownerId);
		
		return query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<Long> getFormInstanceIdsByFormAndOwner(String formId, String ownerId){
		String jpql = "Select fi.instanceId from SharingGroupFormInstance fi "
				 + " join fi.form f join fi.sharingGroup g "
				 + " where f.id= :form_id and g.id = :sharing_group_id ORDER BY fi.lastUpdated DESC";
				 
		Query query=  em.createQuery(jpql);
		query.setParameter("form_id", formId);
		query.setParameter("sharing_group_id", ownerId);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getLastUpdatedFormInstanceByFormAndOwner(String formId, String ownerId){
		String jpql = "Select fi from SharingGroupFormInstance fi "
				 + " join fi.form f join fi.sharingGroup g "
				 + " where f.id= :form_id and g.id = :sharing_group_id ORDER BY fi.lastUpdated DESC";
				 
		Query query=  em.createQuery(jpql);
		query.setParameter("form_id", formId);
		query.setParameter("sharing_group_id", ownerId);
		query.setMaxResults(1);
		return query.getResultList();
	}
	
	public boolean isAncestor( QuestionnaireForm ancestor, QuestionnaireForm child )
	{
		QuestionnaireForm parent = child.getParentForm();
		
		while ( parent != null )
		{
			if ( parent.getId().equals( ancestor.getId() ))
			{
				return true;
			}
			
			parent = parent.getParentForm();
		}
		return false;
	}
	
	/**
	 * NOTES:
	 * Given the hierarchical nature of QuestionnaireForms, each form instance can have a specific set of descendants.
	 * This method takes a specific form instance as defined by the form ID (ancestorFormId) and instance ID (ancestorInstanceId),
	 * and returns a list of the instance IDs which are descendants of that form instance.
	 * Since these descendants can potentially be associated with several different QuestionnaireForms, 
	 * the "descendantFormId" must also be provided to 
	 * filter the list for a specific form.
	 * @param ancestorFormId
	 * @param ancestorInstanceId
	 * @param descendantFormId
	 * @param ownerId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Number> getDescendantFormInstanceIds( String ancestorFormId, Long ancestorInstanceId, String descendantFormId, String ownerId )
	{
		// WITH RECURSIVE is a Postgres Common Table Expression that allows recursive queries.
		String nativeSqlQuery = "WITH RECURSIVE descendants(form_id,sharing_group_id,instance_id,parent_instance_id) as " +
								"(select sg.form_id, sg.sharing_group_id,sg.instance_id,sg.parent_instance_id " +
								" from sharing_group_form_instance sg,forms f where sg.form_id= f.id and " +
								" sg.form_id=? and " + 
								" sg.sharing_group_id=? and " +
								" sg.instance_id=? " +
								" UNION " +
								" select sg.form_id, sg.sharing_group_id,sg.instance_id,sg.parent_instance_id " +
								" from sharing_group_form_instance sg, forms f, descendants c " + 
								" where c.form_id=f.parent_id and f.id = sg.form_id and sg.sharing_group_id = c.sharing_group_id and c.instance_id=sg.parent_instance_id) " +
								" select instance_id from descendants where form_id=?" ;	
		Query query = em.createNativeQuery( nativeSqlQuery );
		query.setParameter(1, ancestorFormId);
		query.setParameter(2, ownerId);
		query.setParameter(3, ancestorInstanceId);
		query.setParameter(4, descendantFormId);
		
		return (  List<Number> ) query.getResultList();
	}
		
	public Long getNumberOfAvailableFormInstances(String formId, String ownerId){
		String jpql = "Select count(fi) from SharingGroupFormInstance fi "
				 + " join fi.form f join fi.sharingGroup g "
				 + " where f.id= :form_id and g.id = :sharing_group_id and fi.status != :status";
				 
		Query query=  em.createQuery(jpql);
		query.setParameter("form_id", formId);
		query.setParameter("sharing_group_id", ownerId);
		query.setParameter("status", FormStatus.NEW);
		return (Long)query.getSingleResult();
	}
	
	public Long getMaxInstanceId(String formId, String ownerId){
		String jpql = "Select max(fi.instanceId) from SharingGroupFormInstance fi "
				 + " join fi.form f join fi.sharingGroup g "
				 + " where f.id= :form_id and g.id = :sharing_group_id and fi.status != :status";
				 
		Query query=  em.createQuery(jpql);
		query.setParameter("form_id", formId);
		query.setParameter("sharing_group_id", ownerId);
		query.setParameter("status", FormStatus.NEW);
		return (Long)query.getSingleResult();
	}
	
	public Long getMinInstanceId(String formId, String ownerId){
		String jpql = "Select min(fi.instanceId) from SharingGroupFormInstance fi "
				 + " join fi.form f join fi.sharingGroup g "
				 + " where f.id= :form_id and g.id = :sharing_group_id and fi.status != :status";
				 
		Query query=  em.createQuery(jpql);
		query.setParameter("form_id", formId);
		query.setParameter("sharing_group_id", ownerId);
		query.setParameter("status", FormStatus.NEW);
		return (Long)query.getSingleResult();
	}
	
	public Long getNumberOfAvailableFormInstancesForParent(String formId, String ownerId, Long parentInstanceId){
		String jpql = "Select count(fi) from SharingGroupFormInstance fi "
				 + " join fi.form f join fi.sharingGroup g "
				 + " where f.id= :form_id and g.id = :sharing_group_id "
				 + " and fi.parentInstanceId = :parentInstanceId and fi.status != :status";
				 
		Query query=  em.createQuery(jpql);
		query.setParameter("form_id", formId);
		query.setParameter("sharing_group_id", ownerId);
		query.setParameter("status", FormStatus.NEW);
		query.setParameter("parentInstanceId",parentInstanceId);
		return (Long)query.getSingleResult();
	}
	
	public Long getNumberOfAvailableParentFormInstances(String formId, String ownerId){
		QuestionnaireForm form = getById(formId);
		
		if ( form.isTopLevelForm() ) return 0L;
		
		String jpql = "Select count(fi) from SharingGroupFormInstance fi "
				 + " join fi.form f join fi.sharingGroup g "
				 + " where f.id= :form_id and g.id = :sharing_group_id and fi.status!=:status";
				 
		Query query=  em.createQuery(jpql);
		query.setParameter("form_id", form.getParentForm().getId());
		query.setParameter("sharing_group_id", ownerId);
		query.setParameter("status", FormStatus.NEW);
		return (Long)query.getSingleResult();
	}
	
	public Long getMaxParentFormInstanceId(String formId, String ownerId){
		QuestionnaireForm form = getById(formId);
		
		if ( form.isTopLevelForm() ) return 0L;
		
		String jpql = "Select max(fi.instanceId) from SharingGroupFormInstance fi "
				 + " join fi.form f join fi.sharingGroup g "
				 + " where f.id= :form_id and g.id = :sharing_group_id and fi.status!=:status";
				 
		Query query=  em.createQuery(jpql);
		query.setParameter("form_id", form.getParentForm().getId());
		query.setParameter("sharing_group_id", ownerId);
		query.setParameter("status", FormStatus.NEW);
		return (Long)query.getSingleResult();
	}
	
	public Long buildNewInstanceId(String formId, String ownerId){
		return getNumberOfAvailableFormInstances(formId, ownerId) + 1;
	}
	
	public boolean isExistingInstanceId(Long instanceId, String formId, String ownerId)
	{
		Long maxCurrentInstances = getNumberOfAvailableFormInstances(formId, ownerId);
		Long maxAllowedInstances = getMaxAvailableFormInstanceId(formId);
		Long minAllowedInstances = 0L;
		if ( instanceId > minAllowedInstances && instanceId <= maxCurrentInstances && instanceId < maxAllowedInstances )
			return true;
		else
			return false;
	}
	
	public FormStatus getFormStatus(QuestionnaireForm form, SharingGroup owner)
	{
		//FormCan only have one status for each owner. it doesn't make sense to return the list
		List<FormStatus> statuses = getFormInstanceStatusesByFormAndOwner(form.getId(), owner.getId());
		
		
		// Return FormStatus.NEW as the default status if no form instances exist
		if ( statuses.isEmpty() ) return FormStatus.NEW;
				
		// If the form instance statuses are all the same, the form status should be equal to the form instance statuses
		// Otherwise, the form status should be IN_PROGRESS
		else return CollectionUtils.containsOnly(statuses, statuses.get( 0 )) ? 
					statuses.get( 0 ) : FormStatus.IN_PROGRESS;
					
	}
	
	public String getFormLastUpdatedBy(List<SharingGroupFormInstance> formInstances)
	{
		Date maxDate = null;
		String lastUpdatedBy = null;
		
		for ( SharingGroupFormInstance formInstance : formInstances )
		{
			if ( maxDate == null ) maxDate = formInstance.getLastUpdated();
			
			else
			{			
				if ( formInstance.getLastUpdated().getTime() > maxDate.getTime() )
				{
					lastUpdatedBy = formInstance.getLastUpdatedBy().getId();
				}
			}
		}
		
		return lastUpdatedBy;
	}
	
	public Date getFormLastUpdatedDate(List<SharingGroupFormInstance> formInstances)
	{
		Date maxDate = null;
		
		for ( SharingGroupFormInstance formInstance : formInstances )
		{
			if ( maxDate == null ) maxDate = formInstance.getLastUpdated();
			
			else
			{			
				if ( formInstance.getLastUpdated().getTime() > maxDate.getTime() )
				{
					maxDate = formInstance.getLastUpdated();
				}
			}
		}
		
		return maxDate;
	}
	
	@SuppressWarnings("unchecked")
	public List<QuestionnaireForm> getReadableFormsByOwner(String entityId, String moduleId)
	{
		String jpql = "select f from QuestionnaireForm f, EntityTagPermission etp " +
				"join f.tag as tag " +
				"where tag.id = etp.pk.tagId " +
				"and etp.pk.tagAccessPermission = :permission and etp.pk.entityId = :entityId and f.module.id = :moduleId" +
				" order by f.order";
		Query query=  em.createQuery(jpql);
		query.setParameter("permission", TagAccessPermissions.READ);
		query.setParameter("entityId", entityId);
		query.setParameter("moduleId", moduleId);
		return ( List<QuestionnaireForm> )query.getResultList();
	}
	
	public void deleteFormInstancesByFormId( String formId )
	{
		String hql = "delete from SharingGroupFormInstance sg where sg.form.id = :formId and sg.status != :status";
			
		Query query = em.createQuery(hql);		
		query.setParameter("form", formId );
		query.setParameter("status", FormStatus.NEW);
		logger.info("Deleting from DB: Form ID======" + formId);
		query.executeUpdate();		
		
	}
}
