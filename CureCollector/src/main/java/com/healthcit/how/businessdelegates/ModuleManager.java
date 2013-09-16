/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import net.sf.json.JSONArray;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cacure.dao.CouchDBDao;
import com.healthcit.cacure.metadata.module.FormType;
import com.healthcit.cacure.metadata.module.ModuleCollectionType;
import com.healthcit.cacure.metadata.module.ModuleType;
import com.healthcit.how.InvalidDataException;
import com.healthcit.how.api.ModuleActionsProvider;
import com.healthcit.how.api.ModuleActionsProvider.ModuleAction;
import com.healthcit.how.dao.FormDao;
import com.healthcit.how.dao.ModuleDao;
import com.healthcit.how.dao.SharingGroupDao;
import com.healthcit.how.dao.SharingGroupFormInstanceDao;
import com.healthcit.how.models.FormSkip;
import com.healthcit.how.models.Module;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.models.SharingGroup;
import com.healthcit.how.models.SharingGroupFormInstance;
import com.healthcit.how.models.SharingGroupModule;
import com.healthcit.how.models.SharingGroupModule.EntityModuleStatus;
import com.healthcit.how.utils.Constants;
import com.healthcit.how.utils.ExceptionUtils;


public class ModuleManager {

	@Autowired
	private ModuleDao moduleDao;
	
	@Autowired
	ModuleActionsProvider moduleActionsProvider;

	@Autowired
	private FormDao formDao;

	@Autowired
	private CouchDBDao couchDbDao;

	@Autowired
	private SharingGroupDao sharingGroupDao;

	@Autowired
	EntityPermissionsManager entityPermissionsManager;

	@Autowired
	private SharingGroupFormInstanceDao sharingGroupFormInstanceDao;
	
	private static final Logger log = LoggerFactory.getLogger(ModuleManager.class);

	public Module addNewModule(Module module) {
		return moduleDao.create(module);
	}

	public Module updateModule(Module module){
		return moduleDao.save(module);
	}

	public void deleteModule(Module module){
		moduleDao.delete(module);
	}

	public Module getModule(String id){
		return moduleDao.getById(id);
	}

	public List<Module> getAllModules(){
		return moduleDao.list();
	}

	public List<Module> getAllActiveModules(){
		return moduleDao.getActiveModules();
	}

	public void setModuleDao(ModuleDao aModuleDao) {
		this.moduleDao = aModuleDao;
	}

//	public SharingGroupModule getLatestDeployedModule(String ownerId, String ctx)
//	{
//		return moduleDao.getLatestDeployedModule(ownerId, ctx);
//	}
	public String getAdjacentFormIdAndInstanceId(String entityId, String ownerId, String formId, Long instanceId, boolean next)
	{
		JSONArray ids = new JSONArray();
		
		// Compute the next instanceId
		Long nextInstanceId = ( next ? instanceId + 1 : instanceId - 1 );
				
		// if this instance exists, then the next form instance belongs to the current form
		if ( formDao.isExistingInstanceId(nextInstanceId, formId, ownerId) ){
			ids.add( formId );
			ids.add( nextInstanceId );
			return ids.toString();
		}
		
		// else, compute the next visible form
		LinkedList<QuestionnaireForm> visibleForms = getVisibleFormsByForm(entityId, ownerId, formId);

		QuestionnaireForm adjacentForm = null;
		ListIterator<QuestionnaireForm> iter = visibleForms.listIterator();
		while (iter.hasNext())
		{
			QuestionnaireForm form = iter.next();
			if (form.getId().equals(formId))
			{
				if (next)
				{
					if (iter.hasNext())
					{
						adjacentForm = iter.next();
					}
				}
				else
				{
					// a call to previous after next returns the same element
					// so have to call previous twice
					if (iter.hasPrevious())
						iter.previous();
					if (iter.hasPrevious())
					{
						adjacentForm = iter.previous();
					}
				}
				break;
			}
		}

		if (adjacentForm == null)
			return Constants.STATUS_NONE;
		else{
			ids.add( adjacentForm.getId() );
			ids.add( 1L );
			return ids.toString();
		}
	}

	public LinkedList<QuestionnaireForm> getVisibleFormsByForm(String entityId, String ownerId, String formId)
	{
		QuestionnaireForm form1 = formDao.getById(formId);

		Module module = moduleDao.getById(form1.getModule().getId());
		return getReadableFormsByModule(entityId, ownerId, module.getId());
	}
	
	public LinkedList<QuestionnaireForm> getReadableFormsByModuleAndEntity(String entityId, String ownerId, String moduleId) {
		return getReadableFormsByModule(entityId, ownerId, moduleId);
	}
	
	public LinkedList<QuestionnaireForm> getReadableFormsByModuleAndOwner(String ownerId, String moduleId) {
		return getReadableFormsByModule(null, ownerId, moduleId);
	}
		
	public List<String> getSkippedFormIds( String ownerId )
	{
		return sharingGroupFormInstanceDao.getSkippedFormIds(ownerId);
	}
	
	private LinkedList<QuestionnaireForm> getReadableFormsByModule(String entityId, String ownerId, String moduleId) {

		// note! this does not get any form data per owner ID - only generic form info.
		Module module = moduleDao.getById(moduleId);
		
		
		List<QuestionnaireForm> readableForms = new LinkedList<QuestionnaireForm>();
		
		if ( module != null )
		{

			readableForms = 
				( StringUtils.isEmpty( entityId ) ?
				  module.getForms() :
			      formDao.getReadableFormsByOwner(entityId, moduleId) );
		}

		return new LinkedList<QuestionnaireForm>(readableForms);
	}

	public boolean isFormVisible(String questionId, String ownerId, FormSkip formSkip){

		//call couchDB to check for skip.
		if (log.isDebugEnabled())
		{
			log.debug("Checking form skip for entityId|questionId|value: " +ownerId + "|" + questionId + "|" + formSkip.getAnswerValues());
		}

		//entityId = "fc2f9d51-c46a-4fa2-8e3d-93d5d19b9b41";
		//questionId = "550";
		try
		{
			Collection<String> answerValues = couchDbDao.getAnswersByOwnerAndQuestionAsList(ownerId, formSkip.getQuestionOwnerFormId(), formSkip.getRowId(), questionId, (Long)null);
			return formSkip.willTriggerShowFormSkip( answerValues );
		}
		catch (Exception ex) {
			log.error("Error while CouchDBDao().getAnswersByEntityAndQuestion() from couchDB", ex);
		}

		return false;

	}

	@SuppressWarnings("unused")
	private JSONArray getObjectKey(String ownerId, String questionId)
	{
		JSONArray key = new JSONArray();
		key.add(ownerId);
		key.add(questionId);
		return key;
	}

	public void addModulesToSharingGroup(SharingGroup sharingGroup) throws Exception {
		List<Module> modulesList = getAllModules();

		for(Module module: modulesList){
			sharingGroup.addEntityModule(new SharingGroupModule(module));
//			moduleDao.addModuleToSharingGroup(module.getId(), groupId);
		}
	}
/* previous mothod
	public void addModulesToAllEntities(List<CoreEntity> coreEntityList, List <Module> modulesList) throws Exception {

		for(Module module: modulesList){
			log.debug("Registering for module " + module.getName());
			Iterator<CoreEntity> entityIterator = coreEntityList.iterator();
			int batchCnt = 0;
			while( entityIterator.hasNext())
			{
				moduleDao.bulkAddModuleToEntity(module.getId(), entityIterator);
				log.debug("  Processed " + (++batchCnt)*ModuleDao.BULK_INSERT_SIZE + " entities");
			}

//			for(CoreEntity coreEnt: coreEntityList) {
//				moduleDao.addModuleToEntity(module.getId(), coreEnt.getId());
//			}
		}
	}
	*/
/*	
	public void addModulesToAllEntities(List<CoreEntity> coreEntityList, List <Module> modulesList) throws Exception {

		for(Module module: modulesList){
			log.debug("Registering for module " + module.getName());
			moduleDao.bulkAddModuleToEntity(module.getId());
		}

	}
	*/
	// new mothod to fix the performance issue
	public void addModulesToAllSharingGroups(List <Module> modulesList) throws Exception {

		for(Module module: modulesList){
			log.debug("Registering for module " + module.getName());
			List <String> sharingGroups = sharingGroupDao.getOwnerForModule (module.getId()) ;
			moduleDao.bulkAddModuleToSharingGroup(module.getId(), sharingGroups);
		}

	}
	public void addModuleToAllSharingGroups(Module module) throws Exception {
		List <Module> modulesList = new ArrayList<Module>();
		modulesList.add(module);
		addModulesToAllSharingGroups(modulesList);

	}
/*	
	public void addFormsToAllEntities(List<CoreEntity> coreEntityList, List <Module> modulesList) throws Exception {

		for(Module module: modulesList){

			List<QuestionnaireForm> formsList = module.getForms();
			for(QuestionnaireForm form: formsList) {
				log.debug("Registering for module: " + module.getName() + ", form: " + form.getName());
					moduleDao.bulkAddFormToEntity(form.getId() );
			}
		}
	}
	*/
	
	/* original
	 public void addFormsToAllEntities(List<CoreEntity> coreEntityList, List <Module> modulesList) throws Exception {

		for(Module module: modulesList){

			List<QuestionnaireForm> formsList = module.getForms();
			for(QuestionnaireForm form: formsList) {
				log.debug("Registering for module: " + module.getName() + ", form: " + form.getName());
				int batchCnt = 0;
				Iterator<CoreEntity> entityIterator = coreEntityList.iterator();
				while( entityIterator.hasNext())
				{
					// for the form id get the core entities 
					
					moduleDao.bulkAddFormToEntity(form.getId(), entityIterator);
					log.debug("  Processed " + (++batchCnt)*ModuleDao.BULK_INSERT_SIZE + " entities");
				}
//				for(CoreEntity coreEntity: coreEntityList){
//					moduleDao.addFormToEntity(form.getId(), coreEntity.getId());
//				}
			}
		}
	}
	 */
	
	public boolean isNewModule(Module module)
	{
		boolean isNew = true;
		Module storedModule = moduleDao.getById(module.getId());
		if (storedModule != null)
		{
			isNew = false;
		}
		return isNew;
	}
	
	public void addFormsToSharingGroup(SharingGroup sharingGroup) throws Exception {
		List<Module> modulesList = getAllModules();

		for(Module module: modulesList){

			List<QuestionnaireForm> formsList = module.getForms();

			for(QuestionnaireForm form: formsList) {
				//moduleDao.addFormToEntity(form.getId(), entityId);
				sharingGroup.addFormInstance(new SharingGroupFormInstance(form));
			}
		}
	}

//	 testing JPA
	public void addFormsToEntityJPA(String groupId) throws Exception {
		List<Module> modulesList = getAllModules();

		SharingGroup sharingGroup = sharingGroupDao.getById(groupId);

		for(Module module: modulesList){

			List<QuestionnaireForm> formsList = module.getForms();

			for(QuestionnaireForm form: formsList)
			{
				SharingGroupFormInstance ef = new SharingGroupFormInstance(sharingGroup,form,1L);
				ef.setStatus(FormStatus.NEW);
				sharingGroupFormInstanceDao.create(ef);
				sharingGroup.addFormInstance(ef);
				formDao.update(form);
//				moduleDao.addFormToEntity(form.getId(), entityId);
			}
		}
		sharingGroupDao.update(sharingGroup);
	}


	public boolean updateEntityModule(String moduleId, String ownerId, String entityId, String status) {
		int result = moduleDao.updateEntityModuleStatus(status, moduleId, ownerId, entityId);
		return result >0 ? true:false;
	}

	public EntityModuleStatus getEntityModuleStatus(String moduleId, String ownerId) throws InvalidDataException
	{
		String status = moduleDao.getEntityModuleStatus(moduleId, ownerId);
		
		if(status == null)
		{
			throw new InvalidDataException ("Status of the entityModule is null.");
		}
		try 
		{
			return EntityModuleStatus.valueOf(status);
		}
		catch (Exception e)
		{
			throw new InvalidDataException ("Unknown module status: " + status);
		}
		
	}
	
	public JSONArray getAvailableModuleActionsForOwners(String moduleId, String[] ownerIds )
	{
		//return moduleDao.getModuleStatusForOwners(moduleId, ownerIds );
		List<SharingGroupModule> sModules = moduleDao.getEntityModulesForOwners(moduleId, ownerIds);
		JSONArray statuses = new JSONArray();
		Map<String,List<String>> availableActionsForOwners = moduleActionsProvider.getModuleActions(sModules);
		
		for(String ownerId: availableActionsForOwners.keySet())
		{
			JSONObject sModuleObject = new JSONObject();
			JSONArray allowedOperations = new JSONArray();
			for(String action: availableActionsForOwners.get(ownerId))
			{
				allowedOperations.add(action);
			}
			sModuleObject.put(ownerId, allowedOperations);
			statuses.add(sModuleObject);

		}
		
		return statuses;
	}
	
	public List<SharingGroupModule> getEntityModules(String moduleId)
	{
		List<SharingGroupModule> modules = moduleDao.getEntityModules(moduleId);
		return modules;
	}
	
	public String tranformModules(List<Module> modulesList) throws Exception
	{

		String allModules = "";
			JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.metadata.module");
			//Create marshaller
			Marshaller m = jc.createMarshaller();

			com.healthcit.cacure.metadata.module.ObjectFactory jaxbFactory = new com.healthcit.cacure.metadata.module.ObjectFactory();

			ModuleCollectionType mct = jaxbFactory.createModuleCollectionType();
			List<ModuleType> moduleTypeList = mct.getModule();

//			if( ownerId != null) {

				for(Module module : modulesList)
				{
					ModuleType moduleType = jaxbFactory.createModuleType();

					moduleType.setId(module.getId());
					moduleType.setName(module.getName());
					moduleType.setDescription(module.getDescription());
					moduleType.setDateModified(module.getDateDeployed());
					moduleType.setEstimatedCompletionTime(module.getEstimatedCompletionTime());		

					List<FormType> formTypeList = moduleType.getForm();

					for(QuestionnaireForm form : module.getForms()) {

						FormType formType = jaxbFactory.createFormType();
						formType.setId(form.getId());
						formType.setName(form.getName());
						formType.setDescription(form.getDescription());
						formType.setAuthor(form.getAuthor());
						formType.setQuestionCount(form.getQuestionCount());

						formTypeList.add(formType);
					}
					moduleTypeList.add(moduleType);
				}
//			}

			JAXBElement<ModuleCollectionType> element = jaxbFactory.createModules(mct);

			//File xmlDocument = new File("/temp" + File.separator + "test.xml");
			//Marshal object into file.
			//m.marshal(element, new FileOutputStream(xmlDocument));
			ByteArrayOutputStream xmlOutputStream = new ByteArrayOutputStream();
			m.marshal(element, xmlOutputStream);
			log.trace("{}", xmlOutputStream);

			allModules = xmlOutputStream.toString();

		return allModules;
	}
	
	public boolean deleteCouchDbDocsByModule( String moduleId )
	{
		try
		{
			List<QuestionnaireForm> forms = moduleDao.getById( moduleId ).getForms();
			for ( QuestionnaireForm form : forms )
			{
				couchDbDao.deleteAllFormDocs(form.getId());
			}
			
			moduleDao.deleteFormInstances( moduleId );
			
			return true;
		}
		catch ( Exception ex )
		{
			log.error( "Could not delete form instances for module " + moduleId + ", Reason: " + ex.getMessage() );
			log.error( ExceptionUtils.getExceptionStackTrace( ex ) );
			return false;
		}
	}
	
	/**
	 * Deletes all CouchDB documents associated with this module
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public boolean deleteAllDocsForContext(String context) {
		List<Module> modules = moduleDao.getModuleByContext( context );
		
		
		
		if ( CollectionUtils.isEmpty( modules ) )
		{
			log.error("No module exists with context " + context );
			return false;
		}
		
		
		for ( Module module : modules )
		{
			for ( QuestionnaireForm form : module.getForms() )
			{
				try
				{
					log.info("Deleting documents for form: " + form.getId() );
					Collection<Map<String,String>> docRefs = couchDbDao.getDocRefsByForm( form.getId() );
					couchDbDao.deleteDocs( docRefs );
					formDao.deleteFormInstancesByFormId( form.getId() );
				}
				catch(Exception ex)
				{
					log.error("Could not delete form " + form.getId() );
					log.error( ExceptionUtils.getExceptionStackTrace(ex) );
				}
				
				moduleDao.updateEntityModuleStatus( module.getId(), SharingGroupModule.EntityModuleStatus.NEW.name() );
			}
		}
		
		return true;
	}

}
