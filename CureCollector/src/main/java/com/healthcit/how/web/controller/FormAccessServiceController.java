/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.healthcit.how.InvalidDataException;
import com.healthcit.how.api.AccessServices;
import com.healthcit.how.api.CureException;
import com.healthcit.how.utils.Constants;
import com.healthcit.how.utils.IOUtils;


/**
*
* @author Suleman Choudhry
*
*/

@Controller
@RequestMapping(value="/api")
public class FormAccessServiceController {
	/* Logger */
	private static final Logger log = LoggerFactory.getLogger( FormAccessServiceController.class );
	private static final String[] DEFAULT_CONTEXT = new String[]{"DEFAULT"};

	private String[] defaultModuleContext = DEFAULT_CONTEXT;
	
	@Value("${cacure.defaultModuleContext}")
	public void setDefaultModuleContext(String ctx) {
		this.defaultModuleContext = new String[]{ctx};
		log.debug("Setting default context to " + ctx);
	}
	
	public enum DataFormat { JSON, PDF, XML };
	
	
//  the specific implementations of the interface is defined in cacure.properties	
	@Autowired AccessServices accessServices;

	private String[] getCleanCtx(String[] ctx)
	{
		if (ctx == null || ctx.length == 0)
			return this.defaultModuleContext;
		else
			return ctx;
	}

//	@Autowired
//	private FormAccessService formAccessService;
	
	private void sendResults(HttpServletResponse response, String mimeType, String responseStatus) throws IOException{
		sendResults(response, mimeType, responseStatus, null);
	}

	@SuppressWarnings("deprecation")
	private void sendResults(HttpServletResponse response, String mimeType, String responseSummary, Exception exception) throws IOException
	{
		IOUtils.sendResults(response, mimeType, responseSummary, exception);
	}
	
	private void sendResults(HttpServletResponse response, String mimeType, Object responseContent)
	{
		IOUtils.sendResults(response, mimeType, responseContent);
	}
	
	private void sendProcessingError(HttpServletResponse response, Exception e)
	{
		log.error(e.getMessage(), e);
		JSONObject errorInfo = new JSONObject();		
		
		errorInfo.put( Constants.RESPONSE_STATUS, Constants.RESPONSE_STATUS_ERROR);
		errorInfo.put( Constants.RESPONSE_STATUS_DETAILS,  "There has been an error processing the request");
		sendResults(response, Constants.CONTENT_TYPE_JSON,  errorInfo);
	}
	
	private void sendProcessingError(HttpServletResponse response, String errorMessage, Exception e)
	{
		log.error(e.getMessage(), e);
		JSONObject errorInfo = new JSONObject();		
		
		errorInfo.put( Constants.RESPONSE_STATUS, Constants.RESPONSE_STATUS_ERROR);
		errorInfo.put( Constants.RESPONSE_STATUS_DETAILS,  errorMessage);
		sendResults(response, Constants.CONTENT_TYPE_JSON,  errorInfo);
	}
	
	@RequestMapping(value="/AllModules")
	public void getAllModules(
			HttpServletResponse response) 
	{
		try
		{
			String allModulesXml = accessServices.getAllActiveModules();
			sendResults(response, Constants.CONTENT_TYPE_XML,  allModulesXml);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}

	}
	
	@RequestMapping(value="/GetFormInstanceDataFromJSONSource", method=RequestMethod.POST)	
	public void getFormInstanceDataFromJSONSource
								   (@RequestParam( value="jsonSource", required=true  ) String jsonSource,
									@RequestParam( value="truncate",   required=false ) String truncate,
									HttpServletResponse response)
	{
		try
		{
			boolean doTruncate = ( StringUtils.isBlank( truncate ) ? true : Boolean.parseBoolean( truncate ) );
			String allFormInstancesXml = accessServices.getFormInstanceDataFromJSONSource( jsonSource, doTruncate );
			sendResults( response, Constants.CONTENT_TYPE_XML, allFormInstancesXml );
		}
		catch(Exception e)
		{
			sendProcessingError(response, e.getMessage(), e);
		}
	}
	
	@RequestMapping(value="/{entityId}/GetCurrentModulesForOwners")
	public void getCurrentModulesForOwners(
			@PathVariable String entityId,
			@RequestParam String ownerId,
			//@RequestParam(value="ownerId") String[] ownersId,
			@RequestParam(value="ctx", required=false) String ctx,
			HttpServletResponse response) throws IOException
	{
		String[] ownerIds = ownerId.split(",");
		try
		{
			String allModulesXml = accessServices.getCurrentModulesForOwners(entityId, ownerIds, ctx);
			sendResults(response, Constants.CONTENT_TYPE_XML,  allModulesXml);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	
	@RequestMapping(value="/GetAvailableModuleActions")
	public void getAvailableModuleActions(
			@RequestParam String moduleId,
			@RequestParam String ownerId,
			HttpServletResponse response) throws IOException
	{
		try
		{
			//String allModulesXml = accessServices.getModuleStatusForOwners(moduleId);
			//sendResults(response, Constants.CONTENT_TYPE_XML,  allModulesXml);
			String[] ownerIds = ownerId.split(",");
			JSONArray statuses = accessServices.getAvailableModuleActionsForOwners(moduleId, ownerIds);
			JSONObject statusInfo = new JSONObject();
			statusInfo.put(Constants.RESPONSE_STATUS, Constants.STATUS_OK);
			statusInfo.put(Constants.RESPONSE_CONTENT, statuses);
			sendResults(response, Constants.RESPONSE_CONTENT, statusInfo );
		} catch(Exception e)
		{
			sendProcessingError(response, e);
		}	

	}
	
	@RequestMapping(value="/GetModuleStatusByOwner")
	public void getModuleStatusByOwner(
			@RequestParam String moduleId,
			HttpServletResponse response) throws IOException
	{
		try
		{
			String allModulesXml = accessServices.getModuleStatusByModule(moduleId);
			sendResults(response, Constants.CONTENT_TYPE_XML,  allModulesXml);
		} catch(Exception e)
		{
			sendProcessingError(response, e);
		}
		

	}
	
	@RequestMapping(value="/{entityId}/{groupId}/AllUserModules")
	public void getAllUserModules(
			@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="ctx", required=false) String[] ctx,
			HttpServletResponse response) throws IOException
	{

		ctx = getCleanCtx(ctx);
		try
		{
			String allModulesXml = accessServices.getAllModules(entityId, groupId, ctx);
			sendResults(response, Constants.CONTENT_TYPE_XML,  allModulesXml);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}

	}
	
	@RequestMapping(value="/{entityId}/{groupId}/AvailableModules")
	public void availableModules(
			@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="ctx", required=false) String[] ctx,
			HttpServletResponse response) throws IOException
	{

		try
		{
			ctx = getCleanCtx(ctx);
	
			// Collection of Module metadata objects (XML)
			// All modules with status new for an entity.
	
			log.debug(" FormAccessServiceController.availableModules() called. entityId: " + entityId);
	
			String modulesXml = accessServices.availableModules(entityId, groupId, ctx);
			sendResults(response, Constants.CONTENT_TYPE_XML,  modulesXml);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}

	}


	/**
	 * This API returns The output will be an XML in ModuleMetadata.xsd
	 * schema with a single artificial element for a module, and multiple form elements.
	 * Each form element will have author attribute set to entity ID value
	 * (values, that are used for <identification> in other REST calls )
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="/GetStaleFormInstances")
	public void getStaleFormInstances(HttpServletResponse response) throws Exception
	{
		try
		{
			String modulesXml = accessServices.getStaleFormInstances();
			sendResults(response, Constants.CONTENT_TYPE_XML,  modulesXml);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}

	@RequestMapping(value="/{entityId}/{groupId}/NextFormIdAndInstanceId")
	public void nextFormIdAndInstanceId(
			@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="formId", required=true) String formId,
			@RequestParam(value="instanceId", required=true) Long instanceId,
			HttpServletResponse response) throws IOException {

		// Form ID and Instance ID of the form instance which has to be filled out after    form

		//create a column called order in Forms column
		//give every form an order number.  Order is same as in the xml file.
		try
		{
			log.debug(" FormAccessServiceController.nextFormIdAndInstanceId() called. entityId: " + entityId + "; formId:" + formId + ";  instanceId: " + instanceId );
	
			String nextFormId = accessServices.nextFormIdAndInstanceId(entityId, groupId, formId, instanceId);
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);
			statusInfo.put( Constants.RESPONSE_CONTENT, nextFormId);
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
			
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}

	}

	@RequestMapping(value="/{entityId}/{groupId}/PreviousFormIdAndInstanceId")
	public void previousFormId(
			@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="formId", required=true) String formId,
			@RequestParam(value="instanceId", required=true) Long instanceId,
			HttpServletResponse response) throws IOException {

		// Form ID of the form which has to be filled out after current form

		//create a column called order in Forms column
		//give every form an order number.  Order is same as in the xml file.
		try
		{
			log.debug(" FormAccessServiceController.previousFormIdAndInstanceId() called. entityId: " + entityId + "; formId:" + formId + "; instanceId: " + instanceId);
	
			String prevFormId = accessServices.previousFormIdAndInstanceId(entityId, groupId,  formId, instanceId);
			
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);
			statusInfo.put( Constants.RESPONSE_CONTENT, prevFormId);
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);

		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}

	}

	@RequestMapping(value="/{entityId}/{groupId}/GetUserFormInstanceData")
	public void getUserFormData(
			@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="formId", required=true) String formId,
			@RequestParam(value="instanceId", required=true) Long instanceId,
			@RequestParam(value="format", required=true) String format,
			HttpServletResponse response) throws Exception {

		//Either JSON or PDF representation of a form
		//Data comes from couchDB.
		//Skip for now. ************************************************
		try
		{
			if (log.isDebugEnabled())
			{
				log.info(" FormAccessServiceController.getFormData() called. entityId: " + entityId +"; formId: " + formId + "; format: " + format + "; instanceId: " + instanceId);
			}
			String output = null;
			String mimeType = null;
			if(!DataFormat.JSON.name().equals(format) && !DataFormat.XML.name().equals(format))
			{
				mimeType = ("text");
				output = ("I am not ready yet...please come again!");
			}
			else
			{
				output = accessServices.getFormData(entityId, groupId, formId, instanceId, format);
				if (DataFormat.JSON.name().equals(format))
				{
					mimeType = Constants.CONTENT_TYPE_JSON;
				}
				else if(DataFormat.XML.name().equals(format))
				{
					mimeType = Constants.CONTENT_TYPE_XML;
				}
			}
			sendResults(response, mimeType,  output);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}

	@RequestMapping(value="/GetFormData")
	public void getFormData(
			@RequestParam(value="id", required=true) String formId,
			@RequestParam(value="format", required=true) String format,
			HttpServletResponse response) throws Exception {

		//Either JSON or PDF representation of a form
		//Data comes from couchDB.
		//Skip for now. ************************************************
		PrintWriter writer = null;
		try
		{
			if (log.isDebugEnabled())
			{
				log.info(" FormAccessServiceController.getFormData() called. formId: " + formId + "; format: " + format);
			}
			@SuppressWarnings("unused") String output = null;
			@SuppressWarnings("unused") String mimeType = null;
			if(!DataFormat.JSON.name().equals(format) && !DataFormat.XML.name().equals(format))
			{
				mimeType = ("text");
				output = ("I am not ready yet...please come again later!");
			}
			else
			{
				 writer = response.getWriter();
				if (DataFormat.JSON.name().equals(format))
				{
					response.setContentType(Constants.CONTENT_TYPE_JSON);
					accessServices.getFormData(formId, format, writer);
				}
				else if(DataFormat.XML.name().equals(format))
				{
					response.setContentType("application/xml");
					accessServices.getFormData(formId, format, writer);
				}
				
			}
		}
		catch(Exception e)
		{
			writer.close();
			sendProcessingError(response, e);
		}
		//sendResults(response, mimeType,  output);
	}

	
	@RequestMapping(value="/CreateNewSharingGroup")
	public void registerNewSharingGroup(@RequestParam(value="name", required=true) String name, HttpServletResponse response) throws Exception
	{
		try
		{
			String groupId = accessServices.createNewSharingGroup(name);
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);
			statusInfo.put( Constants.RESPONSE_CONTENT, groupId);
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	
	@RequestMapping(value="/GetNewEntityInGroup")
	public void registerNewEntityInGroup(@RequestParam(value="grpid", required=true) String groupId, HttpServletResponse response) throws Exception
	{
		try
		{
			String entityId = accessServices.registerNewEntityInGroup(groupId);
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);
			statusInfo.put( Constants.RESPONSE_CONTENT, entityId);
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	
	@RequestMapping(value="/RenameSharingGroup")
	public void renameSharingGroup(@RequestParam(value="oldName", required=true) String oldGroupName, 
								   @RequestParam(value="newName", required=true) String newGroupName,
								   HttpServletResponse response) throws Exception
	{
		try
		{
			String body = accessServices.renameSharingGroup(oldGroupName, newGroupName) ?
				      Constants.STATUS_OK :
				      Constants.STATUS_FAIL;
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, body);
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	
	
	@RequestMapping(value="/{entityId}/AssignEntityToGroup")
	public void assignEntityToGroup(@PathVariable String entityId,
									@RequestParam(value="grpid", required=true) String groupId, HttpServletResponse response) throws Exception
	{
		try
		{
			accessServices.assignEntityToGroup(entityId, groupId);
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS,  Constants.STATUS_OK);
			sendResults(response, Constants.CONTENT_TYPE_JSON, statusInfo);
		}catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	@RequestMapping(value="/GetNewEntityInNewGroup")
	public void registerNewEntityInNewGroup(@RequestParam(value="name", required=true) String groupName, HttpServletResponse response) throws Exception{

		try
		{
			String entityId = accessServices.registerNewEntityInNewGroup(groupName);
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);
			statusInfo.put( Constants.RESPONSE_CONTENT, entityId);
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		}catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	
	@RequestMapping(value="/{entityId}/DeleteEntity")
	public void deleteEntity(
			@PathVariable String entityId,
			HttpServletResponse response) throws IOException {
		try
		{
			// Remove entity relations with forms and module tables
			// also remove entity info from couch DB
			log.debug(" deleteEntity() called. EntityId: " + entityId);
			JSONObject statusInfo = new JSONObject();
			
			if (accessServices.deleteEntity(entityId))
			{
				statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);	
			}
			else
			{
				statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_FAIL);
			}
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}

	@RequestMapping(value="/{entityId}/DeleteEntityFromGroup")
	public void deleteEntityFromGroup(
			@PathVariable String entityId,
			@RequestParam(value="grpid", required=true) String groupId,
			HttpServletResponse response) throws IOException {
		try
		{
			// Remove entity relations with forms and module tables
			// also remove entity info from couch DB
			log.debug(" deleteEntityFromGroup() called. EntityId: " + entityId + ", groupId: " + groupId);
			JSONObject statusInfo = new JSONObject();				
			
			if (accessServices.deleteEntityFromGroup(entityId, groupId))
			{
				statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);
				
			}else
			{
				statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_FAIL);
			}
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}

	}
	
	@RequestMapping(value="/GetAllSharingGroups")
	public void getAllSharingGroups(
			HttpServletResponse response) throws Exception
	{
		try
		{
			PrintWriter out = response.getWriter();
	
			response.setContentType(Constants.CONTENT_TYPE_XML);
	
			accessServices.getAllSharingGroups(out);
			out.close();

		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	
	@RequestMapping(value="/GetGroupId")
	public void getGroupId(
			@RequestParam(value="name", required=true) String groupName,
			HttpServletResponse response) throws Exception
	{
		try
		{
			String groupId = accessServices.getSharingGroupIdByName(groupName);
		
			if (groupId == null)
			{
				throw new InvalidDataException("A sharingGroup with name " + groupName + " is not found");
			}
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);
			statusInfo.put( Constants.RESPONSE_CONTENT, groupId);
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	@RequestMapping(value="/{entityId}/{groupId}/GetFormInstance")
	public void getFormInstance(
			@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="formId", required=true) String formId,
			@RequestParam(value="instanceId", required=true) Long instanceId,
			@RequestParam(value="parentInstanceId", required=false) Long parentInstanceId,
			@RequestParam(value="doValidate", required=false) String doValidate,
			HttpServletResponse response)  {
		try
		{
			// Return XForm ( Formated xml )
			// read the file from disk prepopulate it with data and spit it out.
			log.debug("FormAccessServiceController.GetFormInstance() called. EntityId: " + entityId + "; formId: " + formId + "; instanceId: " + instanceId);
	
			Writer writer = new StringWriter(10000);
			
			// formatting single line output. 
			// For some unfathomable reason JAXP transformer refuses to create multi-line output
			accessServices.getFormInstance(entityId, groupId, formId, instanceId, parentInstanceId, (doValidate == null ? true : Boolean.parseBoolean(doValidate)), writer);
			String xml = writer.toString().replaceAll(">(  +)<",">\n$1<");
		
			sendResults(response, Constants.CONTENT_TYPE_XML, xml);
			
		}
		catch(CureException e)
		{
			
			sendProcessingError(response, e.getMessage(), e);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	
	@RequestMapping(value="/{entityId}/{groupId}/GetFormInstanceByOrdinal")
	public void getFormInstanceByOrdinal(
			@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="formId", required=true) String formId,
			@RequestParam(value="treePath", required=true) String treePath,
			@RequestParam(value="parentFormId", required=false) String parentFormId,
			@RequestParam(value="instanceOrdinal", required=true) Long instanceOrdinal,
			@RequestParam(value="parentInstanceOrdinal", required=false) Long parentInstanceOrdinal,
			HttpServletResponse response) 
	{
		try
		{

			log.debug("In getFormInstanceByOrdinal() method...");
			
			
			Long[] ids            = accessServices.getInstanceIdAndParentInstanceIdByTreePath(treePath, formId, groupId);
			Long instanceId       = ids[ 0 ];
			Long parentInstanceId = ids[ 1 ];
			
			if ( instanceId == null ) 
			{
				getNewFormInstance(entityId, groupId, formId, (parentInstanceId == null ? 1L : parentInstanceId), response);
			}
			
			else 
			{
				getFormInstance(entityId, groupId, formId, instanceId, parentInstanceId, "false", response);
			}
		}
		catch( Exception ex)
		{
			sendProcessingError(response, ex);
		}
	}
	
	@RequestMapping(value="/{entityId}/{groupId}/GetNewFormInstance")
	public void getNewFormInstance(
			@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="formId", required=true) String formId,
			@RequestParam(value="parentInstanceId", required=false) Long newParentInstanceId,
			HttpServletResponse response) throws Exception {
		try
		{

			// Return XForm ( Formated xml )
			// read the file from disk prepopulate it with data and spit it out.
			log.debug("FormAccessServiceController.GetNewFormInstance() called. EntityId: " + entityId + "; formId: " + formId);
			
			Long newInstanceId = accessServices.getNextNewInstanceIdForForm(formId, groupId);
			getFormInstance(entityId, groupId, formId, newInstanceId, newParentInstanceId, "false", response);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	@RequestMapping(value="/{entityId}/{groupId}/SaveFormInstance")
	public void saveFormInstance(
			@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="formId", required=true) String formId,
			@RequestParam(value="instanceId", required=true) Long instanceId,
			@RequestParam(value="parentInstanceId", required=false) Long parentInstanceId,
			@RequestParam(value="doValidate", required=false) String doValidate,
			HttpServletRequest request, HttpServletResponse response) {

		// XForm data will be passed as POST body.
		// should be saved in couch db.
		try {
			log.info("********************** FormAccessServiceController.saveForm() called. entityId: " + entityId);
			log.info("********************** FormAccessServiceController.saveForm() called. formId: " + formId);
			log.info("********************** FormAccessServiceController.saveForm() called. instanceId: " + instanceId);
						
			Writer writer = new StringWriter(10000);
			saveFormInstance(entityId, groupId, formId, instanceId, parentInstanceId, request, (doValidate==null ? true : Boolean.parseBoolean(doValidate)), writer);
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);
			statusInfo.put( Constants.RESPONSE_CONTENT, groupId);
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);				
		}
		catch(CureException ce)
		{
			sendProcessingError(response, ce.getMessage(), ce);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}

	}
	
	@RequestMapping(value="/{entityId}/{groupId}/SaveFormInstanceByOrdinal")
	public void saveFormInstanceByOrdinal(
			@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="formId", required=true) String formId,
			@RequestParam(value="treePath", required=true) String treePath,
			@RequestParam(value="parentFormId", required=false) String parentFormId,
			@RequestParam(value="instanceOrdinal", required=true) Long instanceOrdinal,
			@RequestParam(value="parentInstanceOrdinal", required=false) Long parentInstanceOrdinal,
			HttpServletRequest request, HttpServletResponse response) 
	{
		try
		{
			log.debug("In saveFormInstanceByOrdinal() method...");
			
			
			Long[] ids            = accessServices.getInstanceIdAndParentInstanceIdByTreePath(treePath, formId, groupId);
			Long instanceId       = ids[ 0 ];
			Long parentInstanceId = ids[ 1 ];
			
			
			// If instanceId is null, then there is no pre-existing data for this instance in the database,
			// so it should be treated like a new instance 
			if ( instanceId == null )
			{
				ids = getNewInstanceIdAndParentInstanceIdByOrdinal(instanceOrdinal, groupId, formId, treePath, parentFormId );
				instanceId       = ids[ 0 ];
				parentInstanceId = ids[ 1 ];
			}
			
			// perform validations specific to this method (saving a form by instance ordinal)
			// if any validation errors exist, throw an exception
			log.info("Instance ID to save: " + (instanceId == null ? "null" : instanceId) );
			log.info("Parent Instance ID to save: " + (parentInstanceId == null ? "null" : parentInstanceId) );
			
			accessServices.validateFormInstanceOrdinal(formId, parentFormId, groupId);
			
			saveFormInstance(entityId, groupId, formId, instanceId, parentInstanceId, "true", request, response);
		}

		catch(Exception e)
		{
			sendProcessingError(response, e.getMessage(), e);
		}
		
	}
	
	private void saveFormInstance(String entityId, String groupId, String formId, Long instanceId, Long parentInstanceId, HttpServletRequest request, Writer writer) throws Exception
	{
		saveFormInstance(entityId, groupId, formId, instanceId, parentInstanceId, request, true, writer);
	}
	
	
	private void saveFormInstance(String entityId, String groupId, String formId, Long instanceId, Long parentInstanceId, HttpServletRequest request, boolean doValidate, Writer writer) throws Exception
	{
		//boolean status = false;		

		if ( doValidate )
		{
			// 	Check if this instance ID is valid for this form/owner; if not then an exception is thrown
			accessServices.validateInstanceIdForForm(formId, groupId, instanceId, parentInstanceId);
			
			// Check if this parent instance ID is valid for this form/owner; if not then an exception is thrown
			accessServices.validateParentInstanceIdForForm(formId, groupId, parentInstanceId);
		}
		
		
		String xForm = org.apache.commons.io.IOUtils.toString(request.getInputStream(), "UTF-8");
		if ( StringUtils.isNotEmpty( xForm ) ){
			log.debug("FormAccessServiceController.saveForm() called. xform: " + xForm);

			accessServices.saveFormInstance(entityId, groupId, formId, instanceId, parentInstanceId, xForm, writer);
		}
		
		//return status;
	}
	@RequestMapping(value="/{entityId}/{groupId}/GetModule")
	public void getModule(@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="id", required=true) String moduleId,
			HttpServletResponse response) throws Exception
	{
		try
		{
			// Return XForm ( Formated xml )
			// read the file from disk prepopulate it with data and spit it out.
			log.debug("FormAccessServiceController.getform() called. EntityId: " + entityId + "; formId: " + moduleId);
	
			String moduleXml = accessServices.getEntityModule(entityId, groupId, moduleId);
	
			sendResults(response, Constants.CONTENT_TYPE_XML,  moduleXml);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	
	private Long[] getNewInstanceIdAndParentInstanceIdByOrdinal(Long ordinal, String groupId, String formId, String treePath, String parentFormId)
	throws Exception
	{
		Long instanceId       = null;
		
		Long parentInstanceId = null;

		instanceId            = accessServices.getNextNewInstanceIdForForm( formId, groupId, ordinal );
		
		
		
		if ( StringUtils.isNotBlank ( parentFormId ) )
		{
			String parentTreePath = accessServices.getParentTreePath( treePath );
			parentInstanceId      = accessServices.getInstanceIdAndParentInstanceIdByTreePath( parentTreePath, parentFormId, groupId )[ 0 ];
		}
		
		
		
		Long[] ids                = new Long[]{ instanceId, parentInstanceId };
		
		return ids;
	}
	
	@RequestMapping(value="/{entityId}/{groupId}/ChangeFormInstanceStatus")
	public void changeFormInstanceStatus(@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="formId", required=true) String formId,
			@RequestParam(value="instanceId", required=true) Long instanceId,
			@RequestParam(value="status", required=true) String status,
			HttpServletResponse response) throws Exception
	{
		try
		{
			accessServices.changeFormInstanceStatus(formId, instanceId, entityId, groupId, status);
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);
			
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
			
		}
		catch (Exception e)
		{
			sendProcessingError(response, e);
		}
		
	}
	
	@RequestMapping(value="/{entityId}/{groupId}/ChangeModuleStatus")
	public void changeModuleStatus(
			@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="id", required=true) String moduleId,
			@RequestParam(value="status", required=true) String status,
			HttpServletResponse response) throws Exception
	{
		try
		{
			accessServices.changeModuleStatus(moduleId, entityId, groupId, status);
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);
			
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
		
	}

	@RequestMapping(value="/{entityId}/{groupId}/SaveAndSubmitFormInstance")
	public void saveAndSubmitFormInstance(
			@PathVariable String entityId,
			@PathVariable String groupId,
			@RequestParam(value="formId", required=true) String formId,
			@RequestParam(value="instanceId", required=true) Long instanceId,
			@RequestParam(value="parentInstanceId", required=false) Long parentInstanceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		try 
		{			
			StringWriter writer = new StringWriter(1000);
			saveFormInstance(entityId, groupId, formId, instanceId, parentInstanceId, request, writer);
			accessServices.changeFormInstanceStatus(formId, instanceId, entityId, groupId, "submit");
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);
			statusInfo.put( Constants.RESPONSE_CONTENT, groupId);
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		}catch(CureException ce)
		{
			sendProcessingError(response, ce.getMessage(), ce);
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}

	}
	
	@RequestMapping(value="/GetPermissions")
	public void getPermissions(HttpServletResponse response)
	{
		try
		{
			PrintWriter out = response.getWriter();
			response.setContentType(Constants.CONTENT_TYPE_XML);
			accessServices.getPermissions(out);
			out.close();
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	@RequestMapping(value="{entityId}/GetPermissionsForEntity")
	public void getPermissionsForEntity(
			@PathVariable String entityId, 
			HttpServletResponse response)
	{
		try
		{
			PrintWriter out = response.getWriter();
			response.setContentType(Constants.CONTENT_TYPE_XML);
			accessServices.getPermissionsForEntity(entityId, out);
			out.close();
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	
	@RequestMapping(value="{entityId}/SavePermissions")
	public void savePermissions(
			@PathVariable String entityId,
			HttpServletRequest request, HttpServletResponse response)	
	 {
		try
		{
			ServletInputStream in = request.getInputStream();
		    int contentLength = request.getContentLength();
	
		    if(contentLength > 0 ) {
				byte [] permissions = new byte[contentLength];
	
				int count = 0;
		        int i = in.read();
		        while (i != -1) {
		        	permissions[count++] = (byte) i;
		            i = in.read();
		        }
	
				String entityPermissions = new String(permissions);
			    log.debug("FormAccessServiceController.savePermissions() called. : " + entityPermissions);	
			
				accessServices.saveEntityPermissions(entityId, entityPermissions);
		    }
		    JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);
			
			sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
			
	    }
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	
	@RequestMapping(value="/GetEntitiesForSharingGroup")
	public void getEntitiesForSharingGroup(
			@RequestParam(value="grpid", required=true) String groupId,
			HttpServletResponse response)
	{
		try
		{
			PrintWriter out = response.getWriter();
			response.setContentType(Constants.CONTENT_TYPE_XML);
			accessServices.getEntitiesForSharingGroup(groupId, out);
			out.close();
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
	
	@RequestMapping(value="/GetTags")
	public void getTags(
			HttpServletResponse response)
	{
		try
		{
			PrintWriter out = response.getWriter();
			response.setContentType(Constants.CONTENT_TYPE_XML);
			accessServices.getTags(out);
			out.close();
		}
		catch(Exception e)
		{
			sendProcessingError(response, e);
		}
	}
			
}

