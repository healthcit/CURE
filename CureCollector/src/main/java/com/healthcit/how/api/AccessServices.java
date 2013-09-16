/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.xml.bind.JAXBException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.how.InvalidDataException;
import com.healthcit.how.businessdelegates.CoreEntityManager;
import com.healthcit.how.businessdelegates.EntityPermissionsManager;
import com.healthcit.how.businessdelegates.FormAccessService;
import com.healthcit.how.businessdelegates.FormBuilderDataManager;
import com.healthcit.how.businessdelegates.FormManager;
import com.healthcit.how.businessdelegates.ModuleManager;
import com.healthcit.how.businessdelegates.SharingGroupManager;
import com.healthcit.how.businessdelegates.TagManager;
import com.healthcit.how.models.CoreEntity;
import com.healthcit.how.models.EntityTagPermission.TagAccessPermissions;
import com.healthcit.how.models.Module;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.models.SharingGroup;
import com.healthcit.how.models.SharingGroupModule;
import com.healthcit.how.models.SharingGroupModule.EntityModuleStatus;
import com.healthcit.how.utils.Constants;


public abstract class AccessServices {

	@Autowired
	protected ModuleManager moduleManager;
	
	@Autowired
	protected CoreEntityManager coreEntityManager;
	
	@Autowired
	protected SharingGroupManager sharingGroupManager;
	
	@Autowired
	protected FormAccessService formAccessService;
	
	@Autowired
	protected FormManager formManager;
	
	@Autowired 
	FormActionsProvider formActionProvider;
	
	@Autowired 
	ModuleActionsProvider moduleActionProvider; 
	
	@Autowired
	EntityPermissionsManager entityPermissionsManager;
	
	@Autowired
	FormBuilderDataManager formBuilderManager;
	
	@Autowired
	TagManager tagManager;
	
	private static final String COMMA = ",";
	
	public String getAllModules(String entityId, String groupId, String[] ctx) throws Exception
	{
//		String groupId = getSharingGroupIdForEntity(entityId);
	
		List<SharingGroupModule>entityModulesList = coreEntityManager.getAllModulesByStatus(groupId, Constants.STATUS_ALL, ctx);
		findCurrentModule(entityModulesList);
		String allModulesXml = coreEntityManager.transformEntityModulesForEntity(entityId, entityModulesList);
		return allModulesXml;
	}
	
	public String availableModules(String entityId, String groupId, String[] ctx) throws Exception
	{
//		String groupId = getSharingGroupIdForEntity(entityId);
		EntityModuleStatus[] statuses = {EntityModuleStatus.NEW, EntityModuleStatus.IN_PROGRESS};
		List<SharingGroupModule>entityModulesList = coreEntityManager.getAllModulesByStatuses(groupId, statuses, ctx);
		findCurrentModule(entityModulesList);
		String allModulesXml = coreEntityManager.transformEntityModulesForEntity(entityId, entityModulesList);
		return allModulesXml;
		
		
	}

	public String getEntityModule(String entityId, String groupId, String moduleId) throws Exception
	{
//		String groupId = getSharingGroupIdForEntity(entityId);
		List<SharingGroupModule>entityModulesList = coreEntityManager.getModuleById(groupId, moduleId);
		EntityModuleStatus[] statuses = {EntityModuleStatus.NEW, EntityModuleStatus.IN_PROGRESS};
		List<SharingGroupModule>allEntityModulesList = coreEntityManager.getAllModulesByStatuses(groupId, statuses, null);
		

		SharingGroupModule currentModule = findCurrentModule(allEntityModulesList);
		if(currentModule.getModule().getId().equals(entityModulesList.get(0).getModule().getId()))
		{
			entityModulesList.get(0).setIsEditable(true);
		}
		String allModulesXml = coreEntityManager.transformEntityModulesForEntity(entityId, entityModulesList);
		return allModulesXml;
	}

	public String getCurrentModulesForOwners(String entityId, String[] ownersId, String ctx) throws Exception
	{
		List<SharingGroupModule> ownerModuleMapping = new ArrayList<SharingGroupModule>();
		for(String ownerId: ownersId)
		{
			
			List<SharingGroupModule>modulesList = coreEntityManager.getAllModulesByStatus(ownerId, Constants.STATUS_ALL, new String[]{ctx});
			SharingGroupModule currentModule = findCurrentModule(modulesList);
//			SharingGroupModule
			ownerModuleMapping.add(currentModule);
		}
//		markCurrentModule(entityModulesList);
		String allModulesXml = coreEntityManager.transformCurrentEntityModulesForEntity(entityId, ownerModuleMapping);
		return allModulesXml;
		
	}
	
	

	protected List<SharingGroup> getSharingGroupForEntity(String entityId)
	{
		CoreEntity coreEntity = coreEntityManager.getCoreEntity(entityId);
		return coreEntity.getSharingGroups();
	}
//	protected List<String> getSharingGroupIdForEntity(String entityId)
//	{
//		CoreEntity coreEntity = coreEntityManager.getCoreEntity(entityId);
//		return coreEntity.getSharingGroup().getId();
//	}

	
	public boolean deleteCouchDbDocsByModule( String moduleId )
	{
		return moduleManager.deleteCouchDbDocsByModule(moduleId);
	}
	
	public String getSharingGroupIdByName(String groupName)
	{
		String sharingGroupId = null;
		List<SharingGroup> sharingGroups = sharingGroupManager.getSharingGroupsByName(groupName);
		if(sharingGroups!= null && sharingGroups.size()>0)
		{
			sharingGroupId = sharingGroups.get(0).getId();
		}
		return sharingGroupId;
	}
	public String getStaleFormInstances() throws Exception
	{
		String modulesXml = coreEntityManager.getStaleFormInstances();
		return modulesXml;
	}
	
	public void getAllSharingGroups(PrintWriter out)throws Exception
	{
		sharingGroupManager.getAllSharingGroups(out);
	}

	public String nextFormIdAndInstanceId(String entityId,String groupId, String formId, Long instanceId) throws IOException
	{
//		String groupId = getSharingGroupIdForEntity(entityId);
		String nextFormIdAndInstanceId = moduleManager.getAdjacentFormIdAndInstanceId(entityId, groupId, formId, instanceId, true);
		return nextFormIdAndInstanceId;
	}
	
	public String previousFormIdAndInstanceId(String entityId, String groupId, String formId, Long instanceId) throws IOException
	{
//		String groupId = getSharingGroupIdForEntity(entityId);
		String prevFormIdAndInstanceId = moduleManager.getAdjacentFormIdAndInstanceId(entityId, groupId, formId, instanceId, false);
		return prevFormIdAndInstanceId;
	}
	
	public Long getNextNewInstanceIdForForm(String formId, String groupId) throws IOException
	{
		Long maxInstanceId = formManager.getMaxInstanceId(formId, groupId);
		if ( maxInstanceId == null ) maxInstanceId = 0L;
		Long nextNewInstanceId = maxInstanceId + 1;
		return nextNewInstanceId;
	}
	
	public Long getNextNewInstanceIdForForm(String formId, String groupId, Long instanceOrdinal) throws Exception
	{
		Long numberOfInstances = formManager.getNumberOfExistingInstances(formId, groupId);
		if ( instanceOrdinal > numberOfInstances + 1 )
			throw new Exception("Instance with form ID " + formId + ", instance index " + instanceOrdinal + " could not be saved: only " + numberOfInstances + " instances exist");
		else return getNextNewInstanceIdForForm(formId, groupId);
	}
	
	public void validateFormInstanceOrdinal(String formId, String parentFormId, String groupId) throws Exception
	{
		// Validation errors
		StringBuilder builder = new StringBuilder();
		
		QuestionnaireForm form = formManager.getForm( formId );
		
		// Validate that the form ID is valid
		if ( form == null )
			builder.append( "Form ID " + formId + " is invalid");
		
		// Validate that the parent form ID is valid
		else if ( ! StringUtils.equals( form.getParentForm() == null ? null : form.getParentForm().getId(), parentFormId ) )
			builder.append("\nParent Form ID " + parentFormId + " is invalid for form ID " + formId );
		
		if ( builder.length() > 0 )
			throw new Exception( builder.toString() );
	}
		
	public void validateInstanceIdForForm(String formId, String groupId, Long instanceId, Long parentInstanceId) throws Exception
	{

		QuestionnaireForm form     = formManager.getForm( formId );
		
		boolean isChildForm        = form.isChildForm();
		
		long numMaxInstancesTotal  = form.getMaxInstances();

		
		//Construct validation message when appropriate
		StringBuffer validationError = new StringBuffer();
		
		if ( instanceId == null ) instanceId = 0L;
				
		Long numInstancesTotal = formManager.getNumberOfExistingInstances(formId, groupId);
		
		Long maxExistingInstanceId = formManager.getMaxInstanceId(formId, groupId);
		
		Long minExistingInstanceId = formManager.getMinInstanceId(formId, groupId);
		
		if ( maxExistingInstanceId == null ) maxExistingInstanceId = 0L;
		
		if ( minExistingInstanceId == null ) minExistingInstanceId = 0L;
		
		Long numInstancesCurrentBranch = 
				( isChildForm && parentInstanceId != null? 
				  formManager.getNumberOfExistingInstancesForParent(formId, groupId, parentInstanceId) :
				  numInstancesTotal );
				
		
		// Whether or not this instance already exists 
		boolean isNewInstance = ( instanceId > maxExistingInstanceId || instanceId < minExistingInstanceId );
		
		
		
		// Determine the minimum valid instance ID:
		Long minValidInstanceId = 1L;
		
		// If this is a child form, then the parent instance ID is required for validation
		if ( parentInstanceId == null && form.isChildForm() && isNewInstance )
		{
			validationError.append("The instanceId ").append(instanceId).append(" can not be saved for form ID ") 
							 .append(formId).append(": Valid parent instance ID is required");
			throw new CureException( validationError.toString() );
		}
		
		
		
		// Else, if invalid then throw an exception		
		
		
		
		// Other validations: maximum number of instances allowed
		boolean validateAsChildForm = ( parentInstanceId != null && form.isChildForm() );
		
		if ( numInstancesCurrentBranch >= numMaxInstancesTotal && isNewInstance )
		{
			validationError.append("The instanceId ").append(instanceId).append(" can not be saved for form ID ").append(formId);
			
			if ( validateAsChildForm )
			{ 
				validationError.append(": No more new child instances can be saved for parent instance ID ").append(parentInstanceId);
			}
			else
			{
				validationError.append(": No more new instances of this form are allowed");
			}

			throw new CureException(validationError.toString());
		}
		
		
		
		// Other validations: minimum instance ID allowed
		if ( instanceId < minValidInstanceId )		
		{
			validationError.append("The instanceId ").append(instanceId).append(" can not be saved for form ID ") 
							 .append(formId).append(": Instance ID for this form must not be less than ") 
							 .append(minValidInstanceId);
			throw new CureException(validationError.toString());
		}	
	}
	
	public void validateParentInstanceIdForForm(String formId, String groupId, Long parentInstanceId) throws Exception
	{
		if ( parentInstanceId == null ) parentInstanceId = 0L;
		
		
		//Construct validation message when appropriate
		StringBuffer validationError = new StringBuffer();
					
		
		QuestionnaireForm form = formManager.getForm(formId);
		Long maxExistingParentInstanceId = formManager.getMaxParentInstanceId(formId, groupId);
		Long maxValidParentInstanceId = ( form.isChildForm() ? maxExistingParentInstanceId : 0L );
		if ( maxValidParentInstanceId == null ) maxValidParentInstanceId = 0L;
		Long minValidParentInstanceId = ( form.isChildForm() ? 1L : 0L );
		
		
		boolean isMaxInvalid = ( maxValidParentInstanceId < minValidParentInstanceId );
		boolean isParentInstanceTooSmall = ( parentInstanceId < minValidParentInstanceId );
		boolean isParentInstanceTooLarge = ( parentInstanceId > maxValidParentInstanceId );
		
		
		// If invalid then throw an exception
		if (  isMaxInvalid || isParentInstanceTooSmall || isParentInstanceTooLarge )
		{
			validationError.append("The parentInstanceId ").append(parentInstanceId)
				.append(" can not be saved for form ID ").append(formId).append(":");
			
			if ( isMaxInvalid ) 
				validationError
				.append(formId).append("At least ").append(minValidParentInstanceId)
				.append(" instance must exist for form ID ").append( form.getParentForm().getId() )
				.append("...");
			
			if ( !isMaxInvalid && isParentInstanceTooLarge )
				validationError.append("Instance ID for the parent form must not be greater than ")
				.append(maxValidParentInstanceId)
				.append("...");
			
			if ( isParentInstanceTooSmall )
				validationError.append("Instance ID for the parent form must not be less than ")
				.append(minValidParentInstanceId)
				.append("...");
			
			throw new CureException(validationError.toString());
		}
	}
	
	public String getFormData(String entityId, String groupId, String formId, Long instanceId, String format)throws Exception
	{
//		String groupId = getSharingGroupIdForEntity(entityId);
		String output = formAccessService.getFormData(formId, groupId, instanceId, format);
		return output;
	}
	
	public void getFormData(String formId, String format, PrintWriter out)throws Exception
	{
		formAccessService.getFormData(formId, format, out);
		
	}

	public String createNewSharingGroup(String name) throws Exception
		{
		List<SharingGroup> groups = sharingGroupManager.getSharingGroupsByName(name);
		if(groups!= null && groups.size()>0)
			{
			throw new InvalidDataException("The sharing group with name: " + name + " already exists");
			}
		SharingGroup sharingGroup = new SharingGroup(name);
		moduleManager.addModulesToSharingGroup(sharingGroup);
		moduleManager.addFormsToSharingGroup(sharingGroup);
		
		sharingGroupManager.addNewSharingGroup(sharingGroup);

		return sharingGroup.getId();
		}
	
	public String registerNewEntityInNewGroup(String name) throws Exception
		{
		List<SharingGroup> groups = sharingGroupManager.getSharingGroupsByName(name);
		if(groups!= null && groups.size()>0)
		{
			throw new InvalidDataException("The sharing group with name: " + name + " already exists");
		}
		SharingGroup sharingGroup = new SharingGroup(name);
		CoreEntity coreEntity = new CoreEntity();
		sharingGroup.addCoreEntity(coreEntity);	
		moduleManager.addModulesToSharingGroup(sharingGroup);
		moduleManager.addFormsToSharingGroup(sharingGroup);
		
		sharingGroupManager.addNewSharingGroup(sharingGroup);

		return coreEntity.getId();
	}
	
	public void assignEntityToGroup(String entityId, String groupId)
	{
		coreEntityManager.assignEntityToGroup(entityId, groupId);
	}
	public String registerNewEntityInGroup(String groupId) throws Exception
	{
		CoreEntity coreEntity = new CoreEntity();
		SharingGroup sharingGroup = sharingGroupManager.getSharingGroup(groupId);
		sharingGroup.addCoreEntity(coreEntity);
		coreEntityManager.addNewCoreEntity(coreEntity);
		return coreEntity.getId();
	}
	
	public boolean renameSharingGroup(String oldGroupName, String newGroupName)
	{
		return sharingGroupManager.renameSharingGroup(oldGroupName, newGroupName);
	}

	public boolean deleteEntity(String entityId) throws Exception
	{
		 return coreEntityManager.deleteCoreEntity(entityId);
	}

	public boolean deleteEntityFromGroup(String entityId, String groupId) throws IOException
	{
		 return coreEntityManager.deleteCoreEntityFromGroup(entityId, groupId);
	}
	public void getFormInstance(String entityId, String groupId, String formId, Long instanceId, Long parentInstanceId, Writer out) throws Exception
	{
		getFormInstance(entityId, groupId, formId, instanceId, parentInstanceId, true, out);
	}
	public void getFormInstance(String entityId, String groupId, String formId, Long instanceId, Long parentInstanceId, boolean doValidate, Writer out) throws Exception
	{
		if ( doValidate )
		{
			// Check if this instance ID is valid for this form/owner; if not then an exception is thrown
			validateInstanceIdForForm(formId, groupId, instanceId, parentInstanceId);
		}
		
		EnumSet<TagAccessPermissions> tagAccessPermissions = entityPermissionsManager.getTagAccessPermissions(entityId, formId);
		
		if(tagAccessPermissions != null && tagAccessPermissions.contains(TagAccessPermissions.READ))
		{
//		String groupId = getSharingGroupIdForEntity(entityId);
		QuestionnaireForm.FormPosition formPosition = coreEntityManager.getFormPositionForEntity(formId, entityId, groupId);
		FormStatus formInstanceStatus = formManager.getFormInstanceStatus(formId, groupId, instanceId);
		Reader formData = formAccessService.processFileOnLoad( formManager.getXFormFile(formId), formPosition, formInstanceStatus, tagAccessPermissions );
		formAccessService.setXFormDefaultValues(formData, formId, groupId, instanceId, out);
//		return true;
		}
		else
		{
			throw new CureException(Constants.NO_READ_ACCESS);
//			JSONObject errorInfo = new JSONObject();	
//			errorInfo.put( Constants.ERR_MESSAGE_SUMMARY, Constants.ACCESS_DENIED );
//			
//			errorInfo.put( Constants.ERR_MESSAGE_DETAILS, Constants.NO_READ_ACCESS );
//			errorInfo.write(out);
//			return false;
		}
	}

	public void saveFormInstance(String entityId, String groupId, String formId, Long instanceId, Long parentInstanceId, String xForm, Writer out) throws Exception
	{
//		String groupId = getSharingGroupIdForEntity(entityId);
		@SuppressWarnings("unused") boolean status = false;
		EnumSet<TagAccessPermissions> tagAccessPermissions = entityPermissionsManager.getTagAccessPermissions(entityId, formId);
		
		if(tagAccessPermissions.contains(TagAccessPermissions.WRITE))
		{
			if( xForm != null && xForm.length() > 0)
			{
				QuestionnaireForm form = formManager.getForm(formId);
				String moduleId = form.getModule().getId();
				JSONObject jsonForm = formAccessService.processXForm(xForm, groupId, moduleId, instanceId, parentInstanceId, formManager.getXFormFile(formId));
		
				if(jsonForm != null)
				{
					try
					{
						status = formAccessService.postProcessXForm(jsonForm, entityId, groupId, formId, moduleId, instanceId, parentInstanceId);
					}
					catch (Exception e)
					{
						status = false;
						throw new CureException( e.getMessage() );
					}
		
				}
			}
		}
		else
		{
			throw new CureException (Constants.NO_WRITE_ACCESS );
//			JSONObject errorInfo = new JSONObject();	
//			errorInfo.put( Constants.ERR_MESSAGE_SUMMARY, Constants.ACCESS_DENIED );
//			
//			errorInfo.put( Constants.ERR_MESSAGE_DETAILS, Constants.NO_WRITE_ACCESS );
//			errorInfo.write(out);
			//return false;
		}
		//return status;

	}
	
	/**
	 * Updates the template file associated with the given XForms template name
	 * by modifying the label, description and/or any other properties provided as parameters.
	 * @param template
	 * @param label
	 * @param description
	 * @throws IOException
	 */
	public void updateXFormsAction(FormActionsProvider.XFormTemplate template, String label, String description, String hideFlag) throws IOException
	{
		formActionProvider.updateXFormActionsTemplateFile(template, label, description, hideFlag);
	}
		
//	public void reopenModule(String moduleId,String entityId)throws OperationNotSupportedException
//	{
//		String groupId = getSharingGroupIdForEntity(entityId);
//		moduleManager.updateEntityModuleStatus(moduleId, groupId,Constants.STATUS_IN_PROGRESS);
//	}
	
	

	public void changeFormInstanceStatus(String formId, Long instanceId, String entityId, String groupId, String action)
	{
//		String groupId = getSharingGroupIdForEntity(entityId);
		formActionProvider.changeFormInstanceStatus(groupId, entityId, formId, instanceId, action);
	}
	
	public void changeModuleStatus(String formId, String entityId, String groupId, String action)
	{
//		String groupId = getSharingGroupIdForEntity(entityId);
		moduleActionProvider.changeModuleStatus(formId, groupId, entityId, action);
	}
	
	public JSONArray getAvailableModuleActionsForOwners(String moduleId, String[] ownerIds) throws Exception
	{

		JSONArray   moduleStatuses = moduleManager.getAvailableModuleActionsForOwners(moduleId, ownerIds);
		return moduleStatuses;

	}
	
	public String getModuleStatusByModule(String moduleId) throws Exception
	{

		List<SharingGroupModule> entityModulesList = moduleManager.getEntityModules(moduleId);
		String allModulesXml = coreEntityManager.transformAllEntityModules(entityModulesList);
		return allModulesXml;

	}
	public String getAllActiveModules() throws Exception
	{

		List<Module> modulesList = moduleManager.getAllActiveModules();
		String allModulesXml = moduleManager.tranformModules(modulesList);
		return allModulesXml;

	}
	
	public void saveEntityPermissions(String entityId,  String permissions)throws Exception
	{
		entityPermissionsManager.saveEntityPermissions(entityId, permissions);
	}
	
	public void getPermissions (PrintWriter out) throws Exception
	{
		entityPermissionsManager.getPermissions(out);
	}
	public void getPermissionsForEntity (String entityId, PrintWriter out) throws Exception
	{
		entityPermissionsManager.getPermissionsForEntity(entityId, out);
	}
	public void getTags(PrintWriter out) throws JAXBException
	{
		tagManager.getAllTagsXML(out);
	}
	
	public void getEntitiesForSharingGroup(String groupId, PrintWriter out) throws Exception
	{
		sharingGroupManager.getEntitiesForSharingGroup(groupId,out);
	}
	
	public Long[] getInstanceIdAndParentInstanceIdByTreePath( String treePath, String formId, String ownerId )
	{
		return formManager.getInstanceIdAndParentInstanceIdByTreePath(treePath, formId, ownerId);
	}
	
	public String getParentTreePath( String treePath )
	{
		return ( StringUtils.contains ( treePath, COMMA ) ?
				 StringUtils.substringBeforeLast( treePath, COMMA ) :
				 "" );
	}
	
	protected SharingGroupModule findCurrentModule(List<SharingGroupModule> entityModules)
	{
		List<SharingGroupModule> modules = new ArrayList<SharingGroupModule>();
		for(int i=0; i< entityModules.size(); i++)
		{
			if(entityModules.get(i).getStatus().name().equals(Constants.STATUS_NEW) || entityModules.get(i).getStatus().name().equals(Constants.STATUS_IN_PROGRESS))
			{
				
				modules.add(entityModules.get(i));
			}
		}
		SharingGroupModule currentModule = null;
		if(modules.size()>1)
		{
			Collections.sort(modules, new  Comparator<SharingGroupModule>() {
				  @Override
				public int compare(SharingGroupModule e1, SharingGroupModule e2)
				  {
					 Date date1 = e1.getModule().getDeployDate();
					 Date date2 = e2.getModule().getDeployDate();
					  return date1.compareTo(date2);
				  }
				
			});
			currentModule = modules.get(0);
		}
		else if(modules.size()==1)
		{
			currentModule = modules.get(0);
		}
		if(currentModule != null)
		{
			currentModule.setIsEditable(true);
		}
		return currentModule;
	}

	/**
	 * 
	 * @param jsonSource
	 * @param truncate
	 * @return
	 * @throws Exception
	 */
	public String getFormInstanceDataFromJSONSource( String jsonSource, boolean truncate )
	throws Exception
	{
		if ( truncate ) // then, return only form instances for the forms that are referenced in "jsonMetadata" or their parents
		{
			JSONObject json = JSONObject.fromObject( jsonSource );
			return formBuilderManager.buildGatewayFormInstanceDataXml( json );
		}
		else //TODO: return form instances for every form in the module 
		{
			return null;
		}
	}
	
}
