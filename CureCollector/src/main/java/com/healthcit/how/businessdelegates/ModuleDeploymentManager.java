/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cacure.dao.CouchDBDao;
import com.healthcit.how.dao.FormDao;
import com.healthcit.how.dao.ModuleDao;
import com.healthcit.how.dao.SharingGroupDao;
import com.healthcit.how.dao.SharingGroupFormInstanceDao;
import com.healthcit.how.models.Module;
import com.healthcit.how.models.Module.ModuleStatus;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.utils.ExceptionUtils;
import com.healthcit.how.utils.JAXBUtils;

public class ModuleDeploymentManager {
	
	private Logger log = LoggerFactory.getLogger( ModuleDeploymentManager.class );
	
	private String dataDirPath;
	
	@Autowired
	private ModuleDao moduleDao;

	@Autowired
	private FormDao formDao;

	@Autowired
	private CouchDBDao couchDbDao;
	
	@Autowired
	private SharingGroupDao sharingGroupDao;
	
	@Autowired
	private SharingGroupFormInstanceDao sharingGroupFormInstanceDao;
	
	private static final String COUCHDB_DOC_REF_BY_OWNER_AND_FORM_INSTANCE_VIEW = "GetDocRefsByOwnerAndFormInstance";
	
	private static final String COUCHDB_DOC_BY_FORM_VIEW = "GetDocsByForm";	
	
	private static final int NTHREADS = 20;

	public Module checkIfModuleExists(String id)
	{
		Module storedModule = moduleDao.getById(id);
		return storedModule;
	}
	
	public void updateModuleData(Module module)
	throws Exception
	{
		Module storedModule = moduleDao.getById(module.getId());
		boolean isNewModule = (storedModule == null);

		//Change Status of the Module to IN_PROGRESS for completed modules
		moduleDao.updateEntityModuleStatusFromCompleteToInProgres(module.getId());
		
		// For modules that are not new,
		// Change the status of the module to DEPLOYMENT_LOCKED
		// (the status of the module can only be set to ACTIVE once form skips have been recalculated)
		module.setStatus( isNewModule ? ModuleStatus.ACTIVE : ModuleStatus.DEPLOYMENT_LOCKED );

		if (storedModule != null)
		{
			List<QuestionnaireForm> storedForms = storedModule.getForms();
			List<QuestionnaireForm> forms = module.getForms();
			Map<String, QuestionnaireForm> newFormsMap = new HashMap<String, QuestionnaireForm>();
			Map<String, QuestionnaireForm> storedFormsMap = new HashMap<String, QuestionnaireForm>();
			List<QuestionnaireForm>formsToDelete = new ArrayList<QuestionnaireForm>();
			for (QuestionnaireForm form: forms)
			{
				newFormsMap.put(form.getId(), form);
			}
			for (QuestionnaireForm form: storedForms)
			{
				storedFormsMap.put(form.getId(), form);
			}
			//Check if any of the forms should be removed
			for (QuestionnaireForm form: storedForms)
			{
				if(!newFormsMap.containsKey(form.getId()))
				{
						formsToDelete.add(form);					
				}
				else
				{
					formDao.updateSharingGroupFormInstanceStatusFromCompleteToInProgres(form.getId());
				}
			}
			
			for ( QuestionnaireForm form : storedForms )
			{
				String oldParentFormId = form.isChildForm() ? form.getParentForm().getId() : null;
				QuestionnaireForm newForm = newFormsMap.get(  form.getId() );
				String newParentFormId = ( newForm != null ? ( newForm.isChildForm() ? newForm.getParentForm().getId() : null  ) : null);
								
				// Check if the hierarchy of any of the forms was changed:
				// If the hierarchy changed, 
				// assign any orphan instances to a parent instance with instance ID=1
				if ( newForm != null && ! StringUtils.equals( oldParentFormId, newParentFormId ) )
				{
					assignAllFormInstancesToNewParentInstance( 1L, newForm );
				}
				
			}
			
			for(QuestionnaireForm form: formsToDelete)
			{
				try
				{
					module.getForms().remove(form);
					Collection<Map<String, String>> docRefs = couchDbDao.getDocRefsByForm(form.getId());
					couchDbDao.deleteDocs(docRefs);
				}
				catch (Exception e)
				{
				    log.error("Could not delete form from " + form.getId()  + " from couch", e);	
				}
			}
			moduleDao.update(module);			
		}
		
		// Update the Module JAXB Context
		JAXBUtils.updateModuleMetadataContext();
		
		// Update the Module Metadata Document in the database
		updateMetadataForAllModules();

	}	

	public void assignAllFormInstancesToNewParentInstance(Long parentInstanceId, QuestionnaireForm form) {
		try 
		{
			// Update relational database
			sharingGroupFormInstanceDao.assignAllFormInstancesToNewParentInstance(parentInstanceId, form);
		
			// TODO: Update CouchDB database
			assignAllCouchDbInstancesToNewParentInstance(parentInstanceId, form);
		} 
		catch (Exception e) 
		{
			log.error("Error while assigning form instances for form ID " + form.getId() + " to new parent instance(s):");
			log.error( ExceptionUtils.getExceptionStackTrace(e) );
		}		
	}
	
	public void assignAllCouchDbInstancesToNewParentInstance(Long parentInstanceId, QuestionnaireForm form)
	throws Exception
	{
		// Get the form ID
		String formId = form.getId();
	
		// Get the associated CouchDb documents
		JSONArray oldDocs = couchDbDao.getDocsByFormList( Arrays.asList( formId ) );
		
		JSONArray newDocs = new JSONArray();
		
		
		// Update the "parentInstanceId" attribute
		for ( Object doc : oldDocs )
		{
			JSONObject jsonDoc = ( JSONObject )(( JSONObject ) doc).get( "value" );
			
			if ( jsonDoc != null )
			{
				if ( form.isChildForm() )
					jsonDoc.put( "parentInstanceId", parentInstanceId );
				else
					jsonDoc.remove( "parentInstanceId" );
				
				newDocs.add( jsonDoc );
			}
		}	
		
		// Update the documents in CouchDb
		if ( !newDocs.isEmpty() ) couchDbDao.bulkWriteToDb(newDocs);
		
	}
	
	public boolean deleteNonVisibleFormInstances( JSONObject jsonForm, String triggerFormId, String ownerId, Long triggerInstanceId )
	throws Exception
	{
		log.debug("Attempting to update any instances impacted by form skips triggered by form ID" + triggerFormId + ", owner ID " + ownerId + ", instance ID " + triggerInstanceId + "...");
		
		
		
		// Save new FormSkipAnswers (answers related to any skip trigger questions) to the database:
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~		
		
		// Save the list of FormSkipAnswers
		sharingGroupFormInstanceDao.saveFormSkipAnswers(jsonForm, triggerFormId, triggerInstanceId, ownerId);
		
			
		// Proceed to delete all form instances that have now been rendered invisible
		List<Object[]> nonVisibleInstances = sharingGroupFormInstanceDao.getNonVisibleInstances( triggerFormId, triggerInstanceId, ownerId );
		log.debug("Deleting the following form instances: " +  Arrays.deepToString( nonVisibleInstances.toArray( new Object[] {} )) );
		
		
		
		// clean up relational DB (delete from relational DB/reset "available parent" instances/any other required cleanup operations)
		cleanUpFormInstancesInDb( triggerFormId, triggerInstanceId, ownerId, nonVisibleInstances );
		
		
		
		// delete from CouchDB
		List<Object[]> couchDbKeys = new ArrayList<Object[]>();
		for ( Object[] array : nonVisibleInstances )
		{
			String[] couchDbKey = new String[ array.length ];
			if ( array.length > 0 ) couchDbKey[ 0 ] = ( array[ 0 ] == null ? "" : "\"" + array[ 0 ].toString() + "\"" );
			if ( array.length > 1 ) couchDbKey[ 1 ] = ( array[ 1 ] == null ? "" : "\"" + array[ 1 ].toString() + "\"" );
			if ( array.length > 2 ) couchDbKey[ 2 ] = ( array[ 2 ] == null ? "" : array[ 2 ].toString() );
			couchDbKeys.add( couchDbKey );
		}
		boolean deleteSuccessful = deleteDocumentsByCouchDbQuery( COUCHDB_DOC_REF_BY_OWNER_AND_FORM_INSTANCE_VIEW, couchDbKeys );
		if ( !deleteSuccessful ) log.error( "WARNING: Could not delete document from CouchDB!" );
		
		
		
		// return
		return true;
	}

	public void cleanUpFormInstancesInDb( String formId, Long instanceId, String ownerId, List<Object[]> nonVisibleInstances )
	{

		sharingGroupFormInstanceDao.createAvailableParentInstances(
				formId, 
				instanceId, 
				ownerId);
		
		
		// lastly, delete from relational DB
		sharingGroupFormInstanceDao.deleteNonVisibleEntities( nonVisibleInstances );
	}
	
	public void deleteAllNonVisibleFormInstances(String moduleId)
	throws Exception
	{
		log.info("Deleting all hidden form instances triggered by form skips...");
		
		// Set up a pool of threads to process each CouchDB document 
		// and to delete form instances when appropriate
		ExecutorService executor = Executors.newFixedThreadPool( NTHREADS );
				
		// Get the list of form IDs for this owner which can trigger form skips
		List<String> formIds = ( moduleId == null ?
				                 moduleDao.getSkipTriggeringFormIdsForLockedModules() :
				                 moduleDao.getSkipTriggeringFormIdsForModule( moduleId ) );
		
		
		for ( String formId : formIds )
		{
			// Get the documents from CouchDB in batches (to avoid the potential for OutOfMemory errors):
			
			// Get the startkey
			String startKey = "\"" + formId + "\"";
			
			// Get the endkey
			String endKey = "\"" + formId + "\"";
			
			// This represents the first document of the batch to be fetched from CouchDb
			String batchFirstDocId = null;
			
			// This represents the size of each batch of documents to be fetched from CouchDb
			int batchSize = couchDbDao.getBatchSize();
			
			// Set up a flag indicating whether or not there are more documents that can still be retrieved from CouchDb
			boolean hasMoreDocuments = true;
			
			while ( hasMoreDocuments )
			{
				// Get CouchDb URL parameters
				Map<String,Object> parameters = couchDbDao.getPaginationParameters( startKey, endKey, batchFirstDocId );
				
				// Get the CouchDB response
				JSONObject response = couchDbDao.getDataForView( COUCHDB_DOC_BY_FORM_VIEW, parameters );	
				
				// Get the array of rows in the response
				JSONArray rows = ( response == null ? null : response.getJSONArray( "rows" ) );
		
				// Get the number of rows that will be processed from this batch
				// (We will process all rows except the last one;
				// this is because we need to use the last document ID as the first document of the next batch
				// for CouchDb pagination to work.)
				int numRows = 
					( rows == null ? 0 : 
						( rows.size() == batchSize ? rows.size() - 1 : rows.size() ));
				
				// Set up a list representing the doc IDs for which processing failed, if any
				final List<String> failedDocIds = new ArrayList<String>();
				
				for ( int i = 0; ( i < numRows && failedDocIds.isEmpty() ); ++i )
				{
					// get the current CouchDB document
					final JSONObject couchDbDoc = rows.getJSONObject( i ).getJSONObject("value");
					
					// add a new task to the thread worker queue for this document
					executor.execute(new Runnable() {
						
						@Override
						public void run() {
							try
							{
								String formId = couchDbDoc.getString("formId");
								
								String ownerId = couchDbDoc.getString("ownerId");
								
								Long instanceId = new Long( couchDbDoc.getInt( "instanceId" ) );
								
								deleteNonVisibleFormInstances( couchDbDoc, formId, ownerId, instanceId );
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
								failedDocIds.add( couchDbDoc.getString("_id") );
							}
						}
					});
				}
				
				// Wait for the threads to complete processing
				executor.shutdown();
				while ( !executor.isTerminated() ) ;
				
				
				// If any of the documents failed to process, then throw an exception
				if ( ! failedDocIds.isEmpty() )
				{
					throw new Exception(
							"Deletion of hidden form instances could not be completed - " +
							"the following CouchDB Doc IDs failed to process: " +
						     StringUtils.join( failedDocIds, "," ));
				}
				
				// Else, reset the batchFirstDocId for the next batch of documents
				if ( rows != null && ! rows.isEmpty() ) {
					batchFirstDocId = rows.getJSONObject( rows.size() - 1 ).getJSONObject("value").getString("_id");
				}
				
				// Determine whether or not to continue processing
				if ( numRows == 0 || numRows != batchSize - 1 ) {
					hasMoreDocuments = false;
				}
			}
		}
		
		// Finally, set all DEPLOYMENT_LOCKED modules to ACTIVE
		moduleDao.unlockModules();	
	}
	

	public boolean deleteDocumentsByCouchDbQuery( String couchDbViewName, List<Object[]> keys )
	{
		boolean success = true;
		try
		{
			// get the "Doc-Refs" of the documents to delete 
			// by performing a CouchDB query using the provided CouchDB view and keys
			// NOTE: We assume that the result of this query will be an array of Doc-Refs (Doc IDs and Revs)
			String keyString = Arrays.deepToString( keys.toArray( new Object[] {} ));
			JSONArray docRefs = couchDbDao.getKeyValuesFromView( couchDbViewName, keyString );
			
			// delete the documents
			couchDbDao.deleteDocsByDocRef(docRefs);			
		}
		
		catch ( Exception ex )
		{
			log.error( "Unable to delete documents from CouchDB: " + ex.getMessage() );
			log.debug( ExceptionUtils.getExceptionStackTrace( ex ) );
			success = false;
		}
		
		return success;
	}
	
	/**
	 * Uploads a CouchDB design document attachment with the latest module metadata
	 */
	private String updateMetadataForAllModules()
	{
		log.info( "Updating module metadata information...");
		
		JSONObject metadata = moduleDao.getMetadataForAllModules();
		
		String response = null;
		
		try 
		{
			response = couchDbDao.addAttachment( "moduleMetaData", metadata );
		} 
		catch (Exception e) 
		{
			log.error( e.toString(), e );
		}
		
		return response;
	}
	
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
	
	public boolean checkIfDeploymentLockedModulesExist(){
		return moduleDao.existsDeploymentLockedModules();
	}
	
	public Module updateModule(Module module){
		return moduleDao.save(module);
	}	
	
	public String getDataDirPath() {
		return dataDirPath;
	}

	public void setDataDirPath(String dataDirPath) {
		this.dataDirPath = dataDirPath;
	}
}
