/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cacure.dao.CouchDBDao;
import com.healthcit.cacure.metadata.module.AvailableParentInstanceCollectionType;
import com.healthcit.cacure.metadata.module.AvailableParentInstanceType;
import com.healthcit.cacure.metadata.module.FormInstanceCollectionType;
import com.healthcit.cacure.metadata.module.FormInstanceType;
import com.healthcit.cacure.metadata.module.FormStatusType;
import com.healthcit.cacure.metadata.module.ObjectFactory;
import com.healthcit.how.dao.FormDao;
import com.healthcit.how.dao.SharingGroupFormInstanceDao;
import com.healthcit.how.dto.FormInstanceDto;
import com.healthcit.how.models.FormSkip;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.models.SharingGroup;
import com.healthcit.how.models.SharingGroupFormInstance;
import com.healthcit.how.models.SharingGroupFormInstancePk;
import com.healthcit.how.utils.AppUtils;
import com.healthcit.how.utils.Constants;
import com.healthcit.how.utils.DateUtils;
import com.healthcit.how.utils.NumberUtils;



public class FormManager {

	private static final Logger log = LoggerFactory.getLogger(FormManager.class);

	@Autowired
	private FormDao formDao;
	
	@Autowired
	private CouchDBDao couchDbDao;
		
	@Autowired
	private SharingGroupFormInstanceDao sharingGroupFormInstanceDao;

	private String staleDays;
	
	private static final String INSTANCE = "INSTANCE";
	private static final String PARENT_INSTANCE = "PARENT_INSTANCE";

	
	public SharingGroupFormInstance getFormInstanceByFormAndOwnerAndInstance(String formId, String ownerId, Long instanceId)
	{
		SharingGroupFormInstancePk primaryKey = new SharingGroupFormInstancePk();
		primaryKey.setForm(formId);
		primaryKey.setSharingGroup(ownerId);
		primaryKey.setInstanceId(instanceId);
		SharingGroupFormInstance formInstance = sharingGroupFormInstanceDao.getById(primaryKey);
		return formInstance;
	}
	
	public List<SharingGroupFormInstance> getAvailableFormInstancesByFormAndOwner(String formId, String ownerId)
	{
		List<SharingGroupFormInstance> formInstances = formDao.getAvailableFormInstancesByFormAndOwner(formId, ownerId);
		return formInstances;
	} 
	
	public Long getNumberOfExistingInstances(String formId, String ownerId)
	{
		Long count = formDao.getNumberOfAvailableFormInstances(formId, ownerId);
		return count;
	}
	
	public Long getMaxInstanceId( String formId, String ownerId)
	{
		Long maxInstanceId = formDao.getMaxInstanceId(formId, ownerId);
		return maxInstanceId;
	}
	
	public Long getMinInstanceId( String formId, String ownerId)
	{
		Long minInstanceId = formDao.getMinInstanceId(formId, ownerId);
		return minInstanceId;
	}
	
	public Long getNumberOfExistingInstancesForParent(String formId, String ownerId, Long parentInstanceId)
	{
		Long count = formDao.getNumberOfAvailableFormInstancesForParent(formId, ownerId, parentInstanceId);
		return count;
	}
	
	public Long getNumberOfExistingParentInstances(String formId, String ownerId)
	{
		Long count = formDao.getNumberOfAvailableParentFormInstances(formId, ownerId);
		return count;
	}
	
	public Long getMaxParentInstanceId(String formId, String ownerId)
	{
		Long count = formDao.getMaxParentFormInstanceId(formId, ownerId);
		return count;
	}
		
	//public List<QuestionnaireForm> getStaleForms() throws Exception {
	public List<SharingGroupFormInstance> getStaleFormInstances() throws Exception {

		int days = new Integer(staleDays).intValue();

		//List<QuestionnaireForm> formsList = formDao.getStaleForms(days);
		List<SharingGroupFormInstance> formInstanceList = formDao.getStaleForms(days);

		//TODO
		//call updateStaleForms here.

		return formInstanceList;
	}

	public QuestionnaireForm updateForm(QuestionnaireForm form ){
		formDao.save(form);
		return form;
	}

	public void deleteForm(QuestionnaireForm form){
		formDao.delete(form);
	}

	public void deleteForm(String id){
		formDao.delete(id);
	}

	public QuestionnaireForm getForm(String id) {
		return formDao.getById(id);
	}

	public void setFormDao(FormDao formDao) {
		this.formDao = formDao;
	}

	public String getXForm(String formId) {

		File file = getXFormFile(formId);
		int ch;
		String xFormData = null;

		try {
			StringBuilder strContent = new StringBuilder();
			FileInputStream fin = new FileInputStream(file);

			while( (ch = fin.read()) != -1) {
		        strContent.append((char)ch);
			}
			fin.close();
			xFormData = strContent.toString();

		} catch (FileNotFoundException e) {
			log.error("File " + file.getAbsolutePath() +  "could not be found on filesystem", e);
		} catch(IOException ioe) {
			log.error("Exception while reading the file", ioe);
		}

		return xFormData;
	}
	public File getXFormFile(String formId) {

		QuestionnaireForm form = formDao.getById(formId);

		String xFormLocation = form.getXformLocation();
		File file = new File(xFormLocation);

        return file;
	}


//	public boolean updateDateAndStatusEntityForm(String formId, String ownerId, Date updateDate, String status, String entityId)
//	{
//		int result = entityFormDao.updateDateAndStatusEntityForm(formId, ownerId, updateDate, status, entityId);
//		return result >0 ? true:false;
//	}

	public String getStaleDays() {
		return staleDays;
	}

	public void setStaleDays(String staleDays) {
		this.staleDays = staleDays;
	}
	
	public boolean updateFormInstanceData(String ownerId, String entityId, String formId, Long instanceId, FormStatus status)
	{
		int result = sharingGroupFormInstanceDao.updateFormInstanceData(ownerId, entityId, formId, instanceId, status.toString());
		return result >0 ? true:false;
	}
	
	public boolean updateFormInstanceData(String ownerId, String entityId, String formId, Long instanceId, Long parentInstanceId, FormStatus status)
	{
		int result = sharingGroupFormInstanceDao.updateFormInstanceData(ownerId, entityId, formId, instanceId, parentInstanceId, status.toString());
		return result >0 ? true:false;
	}
	
	public FormStatus getFormInstanceStatus(String formId, String ownerId, Long instanceId)
	{
		FormStatus status = sharingGroupFormInstanceDao.getFormInstanceStatus(ownerId, formId, instanceId);
		// If status is null then assume that this must be a new form instance
		if(status == null) status = FormStatus.NEW;
		return status;
	}
	
	public List<FormStatus> getFormInstanceStatusList(String ownerId, String formId)
	{
		return sharingGroupFormInstanceDao.getFormInstanceStatusList(ownerId, formId);
	}
	
	
	public String getFormTagId(String formId)
	{
		String tagId = formDao.getFormTagId(formId);
		return tagId;
	}
	
	public Long[] getInstanceIdAndParentInstanceIdByTreePath( String treePath, String formId, String ownerId )
	{
		return sharingGroupFormInstanceDao.getInstanceIdAndParentInstanceIdByTreePath(treePath, formId, ownerId);
	}
	
	public QuestionnaireForm getFormByName(String name)
	{
		 
		QuestionnaireForm form = formDao.getFormByName(name);
		return form;
	}
	
	public FormInstanceCollectionType marshallFormInstances( QuestionnaireForm form, SharingGroup owner, List<SharingGroupFormInstance> formInstances, Map<String,String> availableParentInstanceMap, ObjectFactory jaxbFactory )
	throws DatatypeConfigurationException
	{

		FormInstanceCollectionType formInstanceCollectionType = jaxbFactory.createFormInstanceCollectionType();
		
		formInstanceCollectionType.setInstanceGroup( form.getInstanceGroup() );
		formInstanceCollectionType.setMaxInstances ( form.getMaxInstances() == null ? 1L : form.getMaxInstances() );
		formInstanceCollectionType.setExistingInstances( new Long( formInstances.size() ) );
		
		
		if ( form.isChildForm() && form.hasSkips() )
		{
			AvailableParentInstanceCollectionType availableParents = jaxbFactory.createAvailableParentInstanceCollectionType();
			String availableParentString = availableParentInstanceMap.get( form.getId() );
			if ( StringUtils.isNotBlank( availableParentString ) )
			{
				String[] instanceIds = availableParentString.split( Constants.APP_SPLITTER );
				for ( String instanceId : instanceIds )
				{
					AvailableParentInstanceType availableParent = jaxbFactory.createAvailableParentInstanceType();				
					availableParent.setInstanceId( new Long(instanceId) );				
					availableParents.getParentInstance().add( availableParent );
				}
			}
			formInstanceCollectionType.setAvailableParentInstances( availableParents );
		}
			

				
			
		for ( SharingGroupFormInstance formInstance : formInstances )
		{
			FormInstanceType formInstanceType = jaxbFactory.createFormInstanceType();
			
			formInstanceType.setInstanceId  ( formInstance.getInstanceId() == null ? 
											  1L : 
											  formInstance.getInstanceId() );
			
			formInstanceType.setStatus      ( formInstance.getStatus() == null ?
										      FormStatusType.NEW :
										      AppUtils.mapFormStatusToXsdSchemaStatus( formInstance.getStatus() ) );
										
			formInstanceType.setCreationDate( DateUtils.getXMLGregorianCalendar( formInstance.getCreationDate() ) );
	
			if ( formInstance.getParentInstanceId() != null )
			{
				formInstanceType.setParentInstanceId( formInstance.getParentInstanceId() );
			}
			
			
			
			
							
			formInstanceCollectionType.getInstance().add( formInstanceType );
		}
		
		return formInstanceCollectionType;
	}
	
	@Deprecated
	public Map<String,Set<FormInstanceDto>> getVisibleFormInstances( QuestionnaireForm form, SharingGroup owner ) 
	throws Exception{
		
		//"dtos" is a map of sets:
		// "INSTANCE" set contains all visible form instances for this form
		// "PARENT_INSTANCE" set contains all parent instances of the first set that can still have children
		Map<String,Set<FormInstanceDto>> dtos = new HashMap<String,Set<FormInstanceDto>>();

		// Get a set of form instances not hidden by form skips -- will map to "INSTANCE" key 
		Set<FormInstanceDto> visibleInstanceDtos = new HashSet<FormInstanceDto>();
		
		// Get a set of associated parent form instances that can still have children -- will map to "PARENT_INSTANCE" key
		Set<FormInstanceDto> visibleParentInstanceDtos = new HashSet<FormInstanceDto>();
		
		
		
		try {
			List<FormSkip> formSkips = form.getFormSkips();
			
			List<SharingGroupFormInstance> formInstances = formDao.getAvailableFormInstancesByFormAndOwner(form.getId(), owner.getId());
			
			List<SharingGroupFormInstance> parentFormInstances = 
					form.isChildForm() ?
					formDao.getAvailableFormInstancesByFormAndOwner(form.getParentForm().getId(), owner.getId()) :		
					new ArrayList<SharingGroupFormInstance>();
			
			
			if ( ! form.hasSkips() ) {
				
				visibleInstanceDtos.addAll( populateVisibleFormInstancesFromIds( formInstances ) );
				
				visibleParentInstanceDtos.addAll( populateVisibleFormInstancesFromIds( parentFormInstances ) );
			}
			
			else for ( FormSkip formSkip : formSkips )
			{
				// The skip trigger form ID
				String triggerFormId = formSkip.getQuestionOwnerFormId();
				
				
				// Determine if the trigger form is an ancestor of this form
				boolean isTriggerFormAncestor = formDao.isAncestor(formDao.getById(triggerFormId), form);
				
				// Find all form instance Ids associated with this owner and this skip trigger form
				
				// (NOTE: If the trigger form is an ancestor of this form, then consider all the trigger form instances;
				// Else, only consider the last updated trigger form instance.)				
				List<Long> triggerFormInstanceIds = formDao.getFormInstanceIdsByFormAndOwner(triggerFormId, owner.getId());
				Long[] instanceIdArray = triggerFormInstanceIds.isEmpty() ? 
										 new Long[]{null} :
										 (isTriggerFormAncestor ?
										 triggerFormInstanceIds.toArray(new Long[ triggerFormInstanceIds.size() ]):
										 new Long[]{triggerFormInstanceIds.get( 0 )});	
											
				
				// Find any existing form instances containing answers that trigger the skip
				Map<Long,Collection<String>> answers = couchDbDao.getAnswersByOwnerAndQuestionAsMap(owner.getId(), triggerFormId, formSkip.getRowId(), formSkip.getQuestionId(), instanceIdArray );
				
				for ( Long instanceId : answers.keySet() )
				{
					Collection<String> instanceAnswers = answers.get( instanceId );
					
					boolean causesShowSkip = formSkip.willTriggerShowFormSkip( instanceAnswers );
					
					if ( isTriggerFormAncestor ) // only include descendants of the trigger form instances
					{
						if ( causesShowSkip ) { //only include form instances made visible by the form skip
							List< Number > list = formDao.getDescendantFormInstanceIds(triggerFormId, instanceId, form.getId(), owner.getId());
							
							visibleInstanceDtos.addAll( populateVisibleFormInstances( list, formInstances ) );
							
							List< Number > parentList = formDao.getDescendantFormInstanceIds(triggerFormId, instanceId, form.getParentForm().getId(), owner.getId());
							
							visibleParentInstanceDtos.addAll( populateVisibleFormInstances( parentList, parentFormInstances ) );
						}						
					}
					
					else // (descendancy does not apply so include all form instances)
					{
						if ( causesShowSkip ) //only include form instances made visible by the form skip
						{							
							visibleInstanceDtos.addAll( populateVisibleFormInstancesFromIds( formInstances ) );
							
							visibleParentInstanceDtos.addAll( populateVisibleFormInstancesFromIds( parentFormInstances ) );
						}
					}					
				}
			}			
			
		} catch (Exception e) {
			log.error("Error getting visible form instances for form ID " + form.getId() + ", owner ID " + owner.getId() );
			throw e;
		}
		
		dtos.put( INSTANCE, visibleInstanceDtos );
		
		dtos.put( PARENT_INSTANCE, visibleParentInstanceDtos );
		
		
		return dtos;
	}
	
	@Deprecated
	private Set<FormInstanceDto> populateVisibleFormInstancesFromIds( List<SharingGroupFormInstance> formInstances )
	{
		return populateVisibleFormInstances( null, formInstances );
	}
	
	@Deprecated
	private Set<FormInstanceDto> populateVisibleFormInstances( Collection<Number> ids, List<SharingGroupFormInstance> formInstances )
	{
		Set<FormInstanceDto> set = new HashSet<FormInstanceDto>();
		
		for ( SharingGroupFormInstance formInstance : formInstances )
		{			
			Long instanceId        =  formInstance.getInstanceId();
			
			String formId          =  formInstance.getInstanceFormId();
										
			boolean include        = ( instanceId != null 
											&& formId != null 
											&& ( ids == null ? true : NumberUtils.containsNumber( ids, instanceId ))  );
			
			if ( include ) 
			{
				set.add( new FormInstanceDto( formId, instanceId ) );
			}
			
		}
		
		return set;
	}
	
	public String getFormLastUpdatedBy(List<SharingGroupFormInstance> formInstances)
	{
		return formDao.getFormLastUpdatedBy(formInstances);
	}
	
	public Date getFormLastUpdatedDate(List<SharingGroupFormInstance> formInstances)
	{
		return formDao.getFormLastUpdatedDate(formInstances);
	}
	
	public FormStatus getFormStatus(QuestionnaireForm form, SharingGroup owner)
	{
		return formDao.getFormStatus(form, owner);
	}	
	
}
