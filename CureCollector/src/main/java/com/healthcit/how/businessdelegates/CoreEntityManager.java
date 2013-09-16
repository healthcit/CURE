/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

//import org.apache.log4j.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cacure.dao.CouchDBDao;
import com.healthcit.cacure.metadata.module.AvailableParentInstanceCollectionType;
import com.healthcit.cacure.metadata.module.AvailableParentInstanceType;
import com.healthcit.cacure.metadata.module.FormInstanceCollectionType;
import com.healthcit.cacure.metadata.module.FormInstanceType;
import com.healthcit.cacure.metadata.module.FormType;
import com.healthcit.cacure.metadata.module.ModuleCollectionType;
import com.healthcit.cacure.metadata.module.ModuleStatusType;
import com.healthcit.cacure.metadata.module.ModuleType;
import com.healthcit.how.dao.CoreEntityDao;
import com.healthcit.how.dao.SharingGroupDao;
import com.healthcit.how.dao.SharingGroupFormInstanceDao;
import com.healthcit.how.models.CoreEntity;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.QuestionnaireForm.FormPosition;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.models.SharingGroup;
import com.healthcit.how.models.SharingGroupFormInstance;
import com.healthcit.how.models.SharingGroupModule;
import com.healthcit.how.models.SharingGroupModule.EntityModuleStatus;
import com.healthcit.how.utils.AppUtils;
import com.healthcit.how.utils.Constants;
import com.healthcit.how.utils.DateUtils;

public class CoreEntityManager {

	@Autowired
	private CoreEntityDao coreEntityDao;
	
	@Autowired
	private SharingGroupDao sharingGroupDao;
	
	@Autowired
	private SharingGroupFormInstanceDao sharingGroupFormInstanceDao;
	
	@Autowired
	private ModuleManager moduleManager;

	@Autowired
	private CouchDBDao couchDbDao;

	@Autowired
	private FormManager formManager;
	
	@Autowired
	private JAXBContext moduleJaxbContext;

	private static final Logger log = LoggerFactory.getLogger(CoreEntityManager.class);

	
	public void assignEntityToGroup(String entityId, String groupId)
	{
		//coreEntityDao.assignEntityToGroup(entityId, groupId);
		CoreEntity entity = coreEntityDao.getById(entityId);
		SharingGroup sharingGroup = sharingGroupDao.getById(groupId);
		entity.addSharingGroup(sharingGroup);
		coreEntityDao.save(entity);
		
	}
	
	public CoreEntity addNewCoreEntity(CoreEntity coreEntity) {
		return coreEntityDao.create(coreEntity);
	}

	public CoreEntity updateCoreEntity(CoreEntity coreEntity){
		return coreEntityDao.save(coreEntity);
	}
	
	public int deletePermissionsForEntity(String entityId) {
		return coreEntityDao.deletePermissionsForEntity(entityId);
	}

	public boolean deleteCoreEntity(String entityId) throws Exception
	{

		boolean entityFound = false;
		CoreEntity coreEntity = getCoreEntity(entityId);

		if(coreEntity != null){
			entityFound = true;
			// delete meta data
			coreEntityDao.delete(coreEntity);
		}

		if(entityFound){
			// delete couch data
			couchDbDao.deleteAllOwnerDocs(entityId);
		}
		return true;
	}

	public boolean deleteCoreEntityFromGroup(String entityId, String groupId){


		coreEntityDao.deleteFromGroup(entityId, groupId);

		return true;
	}
	
	public void deleteCoreEntity(CoreEntity entity){

//		boolean entityFound = false;
//
//		try
//		{
//			CoreEntity coreEntity = getCoreEntity(entityId);
//
//			if(coreEntity != null){
//				entityFound = true;
//				// delete meta data
				coreEntityDao.delete(entity);
//			}
//		}
//		catch (Throwable t)
//		{
//			log.error("Unable to delete entity info", t);
//			return false;
//		}
//
//		if(entityFound){
			// delete couch data
//			try{
//					couchDbDao.deleteAllOwnerDocs(entity.getId());
//			}
//			catch (Exception e)
//			{
//				log.error("Error deleting documents from Couch", e);
//				// still good to go - we have no XA transactions here
//			}
//		}
//		return true;
	}
	
	public CoreEntity getCoreEntity(String id){

		return coreEntityDao.getById(id);
	}

	
	public void setCoreEntityDao(CoreEntityDao coreEntityDao) {
		this.coreEntityDao = coreEntityDao;
	}

	public int addCoreEntity(String entityId) throws Exception {
		return coreEntityDao.addCoreEntity(entityId);
	}

	//TODO: collapse  getAllModules and getAllModulesByStatus into one method!

//	public String getAllModules(String patientId){
//
//		try {
//
//			CoreEntity coreEntity = this.getCoreEntity(patientId);
//
//			JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.metadata.module");
//			//Create marshaller
//			Marshaller m = jc.createMarshaller();
//
//			com.healthcit.cacure.metadata.module.ObjectFactory jaxbFactory = new com.healthcit.cacure.metadata.module.ObjectFactory();
//
//			ModuleCollectionType mct = jaxbFactory.createModuleCollectionType();
//			List<ModuleType> moduleTypeList = mct.getModule();
//
//			if( coreEntity != null) {
//
//				log.info("coreEntity.getId(): " + coreEntity.getId());
//				List<EntityModule> entityModulesList = coreEntity.getEntityModules();
//
//				for(EntityModule entityModule : entityModulesList) {
//
//					ModuleType moduleType = jaxbFactory.createModuleType();
//
//					moduleType.setId(entityModule.getModule().getId());
//					moduleType.setName(entityModule.getModule().getName());
//					moduleType.setDescription(entityModule.getModule().getDescription());
//					moduleType.setStatus(entityModule.getStatus().toString());
//
////					List<QuestionnaireForm> formsList = entityModule.getModule().getForms();
//					List<QuestionnaireForm> formsList = moduleManager.getVisibleFormsByModule(patientId, entityModule.getModule().getId());
//					List<FormType> formTypeList = moduleType.getForm();
//
//					for(QuestionnaireForm form : formsList) {
//
//						FormType formType = jaxbFactory.createFormType();
//						formType.setId(form.getId());
//						formType.setName(form.getName());
//						formType.setDescription(form.getDescription());
//						formType.setAuthor(form.getAuthor());
//						formType.setQuestionCount(form.getQuestionCount());
//						formType.setStatus(form.getStatus().toString());
//
//						formTypeList.add(formType);
//					}
//					moduleTypeList.add(moduleType);
//				}
//			}
//
//			JAXBElement<ModuleCollectionType> element = jaxbFactory.createModules(mct);
//
//			//File xmlDocument = new File("/temp" + File.separator + "test.xml");
//			//Marshal object into file.
//			//m.marshal(element, new FileOutputStream(xmlDocument));
//			ByteArrayOutputStream xmlOutputStream = new ByteArrayOutputStream();
//			m.marshal(element, xmlOutputStream);
//			//log.info(xmlOutputStream);
//
//			return xmlOutputStream.toString();
//
//		} catch (JAXBException exj) {
//			log.error(exj.toString(), exj);
//		} catch (Exception ex) {
//			log.error(ex.toString(), ex);
//		}
//
//		return null;
//	}

	public QuestionnaireForm.FormPosition getFormPositionForEntity(String formId, String entityId, String ownerId)
	{
		LinkedList<QuestionnaireForm> forms = moduleManager.getVisibleFormsByForm(entityId, ownerId, formId);
		if (forms.size() == 0 )
			return FormPosition.NONE;
		else if (forms.getFirst().getId().equals(formId))
			return FormPosition.FIRST;
		else if (forms.getLast().getId().equals(formId))
			return FormPosition.LAST;
		else
			return FormPosition.MIDDLE;
	}
	
	/**
	 * Get only for module specified in the moduleId irrespective of it's status
	 * @param patientId
	 * @param moduleId
	 * @return
	 */
	public List<SharingGroupModule> getModuleById(String ownerId, String moduleId)
	{
		// gets all statuses if status == null
		List<SharingGroupModule> entityModulesList =	coreEntityDao.getModuleForEntity(ownerId, moduleId);
		return entityModulesList;

	}

	/**
	 * Return current active module for the entity
	 * @param patientId
	 * @param ctx
	 * @return
	 */
//	public String getCurrentModule(String patientId, String[] ctx)
//	{
//		CoreEntity coreEntity = this.getCoreEntity(patientId);
//		EntityModuleStatus[] statuses =
//			new EntityModuleStatus[]
//			    {EntityModuleStatus.NEW, EntityModuleStatus.IN_PROGRESS};
//		List<EntityModule> currentModules = getOneModuleAsList(patientId, null, statuses, ctx);
//		return tranformEntityModules(coreEntity, currentModules);
//
//	}
	private List<SharingGroupModule> getOneModuleAsList(String ownerId, String moduleName,
												EntityModuleStatus[] statuses, String[] ctx)
	{
//		CoreEntity coreEntity = this.getCoreEntity(patientId);

		List<SharingGroupModule> entityModulesList =
			coreEntityDao.getModulesForEntity(ownerId,statuses, ctx);

		List<SharingGroupModule> currentModules = new LinkedList<SharingGroupModule>();
		if (entityModulesList.size() > 0)
		{
			if (moduleName != null && moduleName.length() > 0)
			{
				// try to match
				for (SharingGroupModule em: entityModulesList)
				{
					String cmn = em.getModule().getName();
					if (moduleName.equals(cmn))
					{
						currentModules.add(em);
						break;
					}
				}
			}

			if (currentModules.size() == 0) // did not  found matching modules
			{
				// use only first one
				currentModules.add(entityModulesList.get(0));
			}
		}
		return currentModules;
	}

	public List<SharingGroupModule> getAllModulesByStatuses(String ownerId, EntityModuleStatus[] statuses, String[] ctx)
	{
		List<SharingGroupModule> entityModulesList =
			coreEntityDao.getModulesForEntity(ownerId,statuses, ctx);
		return entityModulesList;
	}
	public List<SharingGroupModule> getAllModulesByStatus(String ownerId, String status, String[] ctx)
	{
//		CoreEntity coreEntity = this.getCoreEntity(patientId);
		List<SharingGroupModule> entityModulesList = getAllModulesByStatusAsList(ownerId, status, ctx );
//		return tranformEntityModules(coreEntity, entityModulesList);
		return entityModulesList;
	}

	public List<SharingGroupModule> getAllModulesByStatusAsList(String patientId, String status, String[] ctx)
	{
		EntityModuleStatus [] statuses;
		if (status.equals(Constants.STATUS_ALL))
			statuses = new EntityModuleStatus[]{EntityModuleStatus.NEW, EntityModuleStatus.IN_PROGRESS, EntityModuleStatus.SUBMITTED};
		else
			statuses = new EntityModuleStatus[]{SharingGroupModule.getStatusByString(status)};

		List<SharingGroupModule> entityModulesList = coreEntityDao.getModulesForEntity(patientId, statuses, ctx);

		return entityModulesList;
	}
	
	public String transformAllEntityModules(List<SharingGroupModule> entityModulesList) throws Exception
	{
		return transformEntityModules(null, entityModulesList);
	}
	
	public String transformEntityModulesForEntity(String entityId, List<SharingGroupModule> entityModulesList) throws Exception
	{
		return transformEntityModules(entityId, entityModulesList);
	}
	public String transformCurrentEntityModulesForEntity(String entityId, List<SharingGroupModule> entityModulesList) throws Exception
	{
		String allModules = "";
		JAXBContext jc = moduleJaxbContext;
		//Create marshaller
		Marshaller m = jc.createMarshaller();

		com.healthcit.cacure.metadata.module.ObjectFactory jaxbFactory = new com.healthcit.cacure.metadata.module.ObjectFactory();

		ModuleCollectionType mct = transformEntityModulesToXML(entityId, entityModulesList);
		
		ModuleCollectionType currentModuleTypes = jaxbFactory.createModuleCollectionType();
		List<ModuleType> currentModules = currentModuleTypes.getModule();
		
		List<ModuleType> mts = mct.getModule();
		for(ModuleType moduleType: mts)
		{
			ModuleType currentModuleType = jaxbFactory.createModuleType();
			copyModuleType(moduleType, currentModuleType);
			currentModules.add(currentModuleType);
			//ModuleType currentModule = new ModuleType();
			FormType currentForm = null;
			List<FormType>forms = moduleType.getForm();
			currentForm = findFirstFormWithStatus(forms, FormStatus.NEW.name());
			if(currentForm == null)
			{
				currentForm = findLastFormWithStatus(forms, FormStatus.IN_PROGRESS.name());
			}
			if(currentForm == null)
			{
				currentForm = findLastFormWithStatus(forms, FormStatus.SUBMITTED.name());
			}
			if(currentForm == null)
			{
				if(forms.size()>0)
				{
					currentForm = forms.get(0);
				}
			}
			
			log.debug("Current Form Id is: " + currentForm.getId());
			//find current formInstance
			if(currentForm != null)
			{
				FormInstanceType currentInstance = null;
				FormInstanceCollectionType  formInstancesType = currentForm.getFormInstances();
				FormInstanceCollectionType newFormInstancesType = jaxbFactory.createFormInstanceCollectionType();
				if(formInstancesType!= null )
				{
					newFormInstancesType.setAvailableParentInstances(formInstancesType.getAvailableParentInstances());
					newFormInstancesType.setMaxInstances(formInstancesType.getMaxInstances());
					newFormInstancesType.setExistingInstances(formInstancesType.getExistingInstances());
					newFormInstancesType.setInstanceGroup(formInstancesType.getInstanceGroup());
					
					List<FormInstanceType> instances =formInstancesType.getInstance();
					for(FormInstanceType instance: instances)
					{
						if(instance.getStatus().equals(FormStatus.IN_PROGRESS.name()))
						{
							currentInstance = instance;
							break;
						}
					}
					if (currentInstance == null && instances.size()>0)
					{
						currentInstance = instances.get(0);
					}
				}
				//newFormInstancesType = jaxbFactory.createFormInstanceCollectionType();
				if(currentInstance!=null)
				{
					List<FormInstanceType> instances = newFormInstancesType.getInstance();
					log.debug("Number of instances: " + instances.size());
					instances.add(currentInstance);
				}
				FormType newCurrentForm = jaxbFactory.createFormType();
				copyFormType(currentForm, newCurrentForm);
				newCurrentForm.setFormInstances(newFormInstancesType);
				currentModuleType.getForm().add(newCurrentForm);
			}
		}
		
		
		
		JAXBElement<ModuleCollectionType> element = jaxbFactory.createModules(currentModuleTypes);

		//File xmlDocument = new File("/temp" + File.separator + "test.xml");
		//Marshal object into file.
		//m.marshal(element, new FileOutputStream(xmlDocument));
		ByteArrayOutputStream xmlOutputStream = new ByteArrayOutputStream();
		m.marshal(element, xmlOutputStream);
		log.trace("{}",xmlOutputStream);

		allModules = xmlOutputStream.toString();
		return allModules;
	}
    private void copyModuleType(ModuleType fromModule, ModuleType toModule)
    {
    	toModule.setId(fromModule.getId());
    	toModule.setName(fromModule.getName());
    	toModule.setDescription(fromModule.getDescription());
    	toModule.setStatus(fromModule.getStatus());
    	toModule.setDateModified(fromModule.getDateModified());
    	toModule.setIsFlat(fromModule.isIsFlat());
    	toModule.setAvailableActions(fromModule.getAvailableActions());
    	toModule.setUpdatedBy(fromModule.getUpdatedBy());
		toModule.setEstimatedCompletionTime(fromModule.getEstimatedCompletionTime());
		toModule.setIsEditable(fromModule.isIsEditable());
		toModule.setOwner(fromModule.getOwner());
    }
    
    private void copyFormType(FormType fromForm, FormType toForm)
    {
    	toForm.setId(fromForm.getId());
    	toForm.setName(fromForm.getName());
    	toForm.setDescription(fromForm.getDescription());
    	toForm.setAuthor(fromForm.getAuthor());
    	toForm.setQuestionCount(fromForm.getQuestionCount());
    	toForm.setStatus(fromForm.getStatus());
		toForm.setLastUpdatedBy(fromForm.getLastUpdatedBy());
		toForm.setDateModified(fromForm.getDateModified());
		
    }
	private FormType findFirstFormWithStatus(List<FormType> forms, String status)
	{
		FormType currentForm = null;
		for(FormType form: forms)
		{
			if(form.getStatus().name().equals(status))
			{
				AvailableParentInstanceCollectionType parentCollection = form.getFormInstances().getAvailableParentInstances();
				if( parentCollection == null)
				{
					//There are no skips to affect the visibility of this form or it's not a child form
					currentForm = form;
					break;
				}
				else
				{
					//check to make sure there are available parent instances
					List<AvailableParentInstanceType> parentInstances = parentCollection.getParentInstance();
					if (parentInstances.size() >0)
					{
						currentForm = form;
						break;
					}
					
				}				
			}
			List<FormType> childrenForms = form.getForm();
			if(childrenForms!= null)
			{
				currentForm = findFirstFormWithStatus(childrenForms, status);
				if(currentForm != null)
				{
//					currentForm = form;
					break;
				}
			}
			//form.getFormInstances();
		}
		return currentForm;
	}
	
	private FormType findLastFormWithStatus(List<FormType> forms, String status)
	{
		FormType currentForm = null;
		for(FormType form: forms)
		{
			
			if(form.getStatus().name().equals(status))
			{
				//currentForm = form;
				AvailableParentInstanceCollectionType parentCollection = form.getFormInstances().getAvailableParentInstances();
				if( parentCollection == null)
				{
					//There are no skips to affect the visibility of this form or it's not a child form
					currentForm = form;
					//break;
				}
				else
				{
					//check to make sure there are available parent instances
					List<AvailableParentInstanceType> parentInstances = parentCollection.getParentInstance();
					if (parentInstances.size() >0)
					{
						currentForm = form;
						//break;
					}
					
				}			
				
				//break;
			}
			List<FormType> childrenForms = form.getForm();
			if(childrenForms!= null)
			{
				FormType childCurrentForm = findLastFormWithStatus(childrenForms, status);
				if(childCurrentForm!= null)
				{
					currentForm = childCurrentForm;
				}
			}
			//form.getFormInstances();
		}
		return currentForm;
	}
	private ModuleCollectionType transformEntityModulesToXML(String entityId, List<SharingGroupModule> entityModulesList) throws Exception
	{

			com.healthcit.cacure.metadata.module.ObjectFactory jaxbFactory = new com.healthcit.cacure.metadata.module.ObjectFactory();

			ModuleCollectionType mct = jaxbFactory.createModuleCollectionType();
			List<ModuleType> moduleTypeList = mct.getModule();
			

//			if( ownerId != null) {

				for(SharingGroupModule entityModule : entityModulesList)
				{
					SharingGroup owner = entityModule.getCoreEntity();
					String ownerId = owner.getId();
					JSONArray availableActionsForOwners = moduleManager.getAvailableModuleActionsForOwners(entityModule.getModule().getId(), new String[]{ownerId});
					
					JSONArray availableActionsArray = availableActionsForOwners.getJSONObject(0).getJSONArray(ownerId);
					StringBuilder availableActions = new StringBuilder();
					if(availableActionsArray != null)
					{
						for(int i=0; i<availableActionsArray.size(); i++)
						{
							if(i>0)
							{
								availableActions.append(",");
							}
							availableActions.append(availableActionsArray.get(i));
						}
					}
					
					ModuleType moduleType = jaxbFactory.createModuleType();

					moduleType.setId(entityModule.getModule().getId());
					moduleType.setName(entityModule.getModule().getName());
					moduleType.setDescription(entityModule.getModule().getDescription());
					moduleType.setStatus(AppUtils.mapModuleStatusToXsdSchemaStatus(entityModule.getStatus()));
					moduleType.setDateModified(entityModule.getDateSubmitted());
					moduleType.setIsFlat(entityModule.getModule().getIsFlat());
					moduleType.setAvailableActions(availableActions.toString());
					if(entityModule.getLastUpdatedBy()!= null)
					{
						moduleType.setUpdatedBy(entityModule.getLastUpdatedBy().getId());
					}
					moduleType.setEstimatedCompletionTime(entityModule.getModule().getEstimatedCompletionTime());
					moduleType.setIsEditable(entityModule.getIsEditable());
					moduleType.setOwner(ownerId);
					

					List<QuestionnaireForm> visibleForms = moduleManager.getReadableFormsByModuleAndEntity(entityId, ownerId, entityModule.getModule().getId());
					List<FormType> formTypeList = moduleType.getForm();
					
					Map<String,String> availableParentInstances = entityModule.getAvailableParentInstances();
					

					for(QuestionnaireForm form : visibleForms) 
					{
						if ( form.isTopLevelForm() )
						{
							FormType formType = transformFormInstance( owner, form, visibleForms, availableParentInstances, jaxbFactory );						
							if ( formType != null ) formTypeList.add(formType);
						}
					}
					moduleTypeList.add(moduleType);
				}
//			}

			return mct;
	}
	
	public String transformEntityModules(String entityId, List<SharingGroupModule> entityModulesList) throws Exception
	{
		String allModules = "";
		JAXBContext jc = moduleJaxbContext;
		//Create marshaller
		Marshaller m = jc.createMarshaller();

		com.healthcit.cacure.metadata.module.ObjectFactory jaxbFactory = new com.healthcit.cacure.metadata.module.ObjectFactory();

		ModuleCollectionType mct = transformEntityModulesToXML(entityId, entityModulesList);
		JAXBElement<ModuleCollectionType> element = jaxbFactory.createModules(mct);

		//File xmlDocument = new File("/temp" + File.separator + "test.xml");
		//Marshal object into file.
		//m.marshal(element, new FileOutputStream(xmlDocument));
		ByteArrayOutputStream xmlOutputStream = new ByteArrayOutputStream();
		m.marshal(element, xmlOutputStream);
		log.trace("{}",xmlOutputStream);

		allModules = xmlOutputStream.toString();
	return allModules;
	}
	
	private FormType transformFormInstance( SharingGroup owner, QuestionnaireForm form, Map<String, String> availableParentInstances, com.healthcit.cacure.metadata.module.ObjectFactory jaxbFactory ) 
	throws DatatypeConfigurationException
	{
		List<QuestionnaireForm> permissibleForms = form.getModule().getForms();
		
		return transformFormInstance(owner, form, permissibleForms, availableParentInstances, jaxbFactory);
	}
	
	private FormType transformFormInstance( SharingGroup owner, QuestionnaireForm form, List<QuestionnaireForm> permissibleForms, Map<String,String> availableParentInstances, com.healthcit.cacure.metadata.module.ObjectFactory jaxbFactory ) 
	throws DatatypeConfigurationException
	{
		FormType formType = null;
		
		if ( permissibleForms.contains( form ))
		{
		
			List<SharingGroupFormInstance> formInstances = formManager.getAvailableFormInstancesByFormAndOwner(form.getId(), owner.getId());
					
			
			formType = jaxbFactory.createFormType();
			formType.setId(form.getId());
			formType.setName(form.getName());
			formType.setDescription(form.getDescription());
			formType.setAuthor(form.getAuthor());
			formType.setQuestionCount(form.getQuestionCount());
			formType.setStatus(AppUtils.mapFormStatusToXsdSchemaStatus(form.getStatus()));
			String lastUpdatedBy = formManager.getFormLastUpdatedBy(formInstances);
			if( lastUpdatedBy!= null )
			{
				formType.setLastUpdatedBy(lastUpdatedBy);
			}
			Date lastUpdatedDate = formManager.getFormLastUpdatedDate(formInstances);
			if ( lastUpdatedDate != null )
			{
				formType.setDateModified(DateUtils.getXMLGregorianCalendar(lastUpdatedDate));
			}
			
			
			
			FormInstanceCollectionType formInstanceCollectionType = formManager.marshallFormInstances(form, owner, formInstances, availableParentInstances, jaxbFactory);						
			formType.setFormInstances( formInstanceCollectionType );	
		
			for ( QuestionnaireForm childForm : form.getChildForms() )
			{
				FormType childFormType = transformFormInstance( owner, childForm, permissibleForms, availableParentInstances, jaxbFactory );
				if ( childFormType != null ) formType.getForm().add( childFormType );
			}
			
		}		
		
		return formType;
	}

	public String getStaleFormInstances() throws Exception
	{

		JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.metadata.module");
		//Create marshaller
		Marshaller m = jc.createMarshaller();

		com.healthcit.cacure.metadata.module.ObjectFactory jaxbFactory = new com.healthcit.cacure.metadata.module.ObjectFactory();

		ModuleCollectionType mct = jaxbFactory.createModuleCollectionType();

		List<ModuleType> moduleTypeList = mct.getModule();
		ModuleType moduleType = jaxbFactory.createModuleType();
		moduleType.setId("FAKE-MODULE");
		moduleType.setName("Fake Module");
		moduleType.setDescription("Wrapper for Stale Forms");
		moduleType.setStatus(ModuleStatusType.NEW);

		// TODO: fill in the data
		//1. get stale forms from Form ( stale: EntityForm.status == IN_PROGRESS && today - EntityForm.last_updated > getDaysFromProperties())
		//2. populate form structure with form.author = entityId
		//   no need to worry about skips
		//3. update date field on the formEntities returned (set EntityForm.last_updated = now )
		// The data below is all fake

		//List<QuestionnaireForm> formsList = formManager.getStaleForms();
		List<SharingGroupFormInstance> formsList = formManager.getStaleFormInstances();
		List<FormType> formTypeList = moduleType.getForm();

		for(SharingGroupFormInstance form : formsList) {

			SharingGroup sharingGroup = form.getSharingGroup();
			QuestionnaireForm qForm = form.getForm();
			Map<String,String> availableParentInstances = sharingGroupFormInstanceDao.getAvailableParentInstances(qForm.getModule().getId(),sharingGroup.getId());
			FormType formType = transformFormInstance(sharingGroup, qForm, availableParentInstances, jaxbFactory);
			formTypeList.add(formType);
		}

		moduleTypeList.add(moduleType);

		JAXBElement<ModuleCollectionType> element = jaxbFactory.createModules(mct);

		//File xmlDocument = new File("/temp" + File.separator + "test.xml");
		//Marshal object into file.
		//m.marshal(element, new FileOutputStream(xmlDocument));
		ByteArrayOutputStream xmlOutputStream = new ByteArrayOutputStream();
		m.marshal(element, xmlOutputStream);
		log.trace("{}",xmlOutputStream);

		return xmlOutputStream.toString();
	}

	public List<CoreEntity> getAllCoreEntities() throws Exception {
		return coreEntityDao.list();
	}

}
