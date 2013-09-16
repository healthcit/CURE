/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.api;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.healthcit.how.models.EntityTagPermission.TagAccessPermissions;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.utils.Constants;
import com.healthcit.how.utils.ExceptionUtils;
import com.healthcit.how.utils.IOUtils;
import com.healthcit.how.utils.RegexUtils;

public abstract class FormActionsProvider {
	private static final Logger log = LoggerFactory.getLogger(FormActionsProvider.class);
	public enum FormAction {SUBMIT, APPROVE, DECLINE};
	public enum XFormTemplate {PREVIOUS,NEXT,SUBMIT,SAVE,APPROVE,DECLINE,REOPEN};
	
	private String formActionsPath;
	private String FORM_ACTION_SOURCE_PATH = "formActions";
	
	private String previousFormAction;
	private String nextFormAction;
	private String submitFormAction;
	private String saveFormAction;
	private String approveFormAction;
	private String declineFormAction;	
	private String reopenFormAction;

	private String previousFormActionTemplateFile;
	private String nextFormActionTemplateFile;
	private String submitFormActionTemplateFile;
	private String saveFormActionTemplateFile;
	private String approveFormActionTemplateFile;
	private String declineFormActionTemplateFile;	
	private String reopenFormActionTemplateFile;
	
	private String XFORM_ACTION_LABEL_REGEX = "(<xform:submit .*?<xform:label[^>]*?>)([^<]+)";
	private String XFORM_ACTION_DESCRIPTION_REGEX = "(<xform:output.*? value=\"')([^']+)";
	private String XFORM_ACTION_HIDEFLAG_REGEX = "(<span style=\"display:none;\">)(.+)(</span>)";
	
	protected String getTempate(String templateFile)
	{
		String data = "";
		try
		{
			data = IOUtils.readFileContent( new File( this.formActionsPath + "/" + templateFile ));
		}
		catch(IOException e)
		{
			log.error("Unable to load form action template '" + templateFile + "' from path '" + this.formActionsPath + "' ", e);
		}
		return data;
	}
	
	/**
	 * Updates the template file associated with the specified template by updating the label, descripiton
	 * and/or any other properties in the file.
	 * @param template
	 * @param label
	 * @param description
	 * @throws IOException
	 */
	public void updateXFormActionsTemplateFile(XFormTemplate template, String label, String description, String hideFlag) throws IOException
	{
		try
		{				
			String data = getXFormTemplateActionSection( template );
				
			// update the label
			if ( StringUtils.isNotEmpty( label ) )
			{
				data = RegexUtils.replaceAll(data, XFORM_ACTION_LABEL_REGEX, "$1"+label);
			}
			
			// update the description
			if ( StringUtils.isNotEmpty( description ))
			{
				data = RegexUtils.replaceAll(data, XFORM_ACTION_DESCRIPTION_REGEX, "$1"+description);
			}
			
			// update the visibility based on the value of the hideFlag
			data = StringUtils.defaultIfEmpty( RegexUtils.getCapturingGroup(2, data, XFORM_ACTION_HIDEFLAG_REGEX), data );
			
			if ( StringUtils.equalsIgnoreCase( hideFlag, Constants.TRUE ))
			{
				data = "<span style=\"display:none;\">" + data + "</span>";
			}
						
			// Update the template on the hard drive			
			IOUtils.writeToFile( new File( this.formActionsPath + "/" + getXFormTemplateFile( template )), 
								 data );
			
			// Update the associated action variable
			setXFormTemplateActionSectionHolder(template, data);
		}

		catch(IOException e)
		{
			log.error("Unable to update form action template '" + template.toString() + "' from path '" + this.formActionsPath + "' ", e);
			throw e;
		}
	}
	
	/**
	 * Parses the template file associated with this template and returns the value of the specified property in the file.
	 * @param template
	 * @param propertyName
	 * @return
	 */
	public String getXFormsTemplateProperty( XFormTemplate template, String propertyName )
	{		
		String data = getXFormTemplateActionSection( template );
				
		// label
		if ( StringUtils.equalsIgnoreCase( propertyName, Constants.LABEL ))
		{
			return RegexUtils.getCapturingGroup( 2, data, XFORM_ACTION_LABEL_REGEX );
		}
		
		// description
		else if ( StringUtils.equalsIgnoreCase( propertyName, Constants.DESCRIPTION))
		{
			return RegexUtils.getCapturingGroup( 2, data, XFORM_ACTION_DESCRIPTION_REGEX );
		}
		
		// hide flag
		else if ( StringUtils.equalsIgnoreCase( propertyName, Constants.HIDE_FLAG ))
		{
			return String.valueOf(StringUtils.isNotBlank( RegexUtils.getCapturingGroup( 2, data, XFORM_ACTION_HIDEFLAG_REGEX )));
		}
		
		else 
		{
			return null;
		}
	}
	/**
	 * Creates the directory for the form action templates if it doesn't exist,
	 * and updates the directory with the latest template files when applicable.
	 */
	public void updateFormActionsDirectory(){		
		try
		{
			File targetDirectory = new File(this.formActionsPath);
			if ( !targetDirectory.exists() ){
				targetDirectory.mkdir();
				File sourceDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(FORM_ACTION_SOURCE_PATH).toURI());
				FileUtils.copyDirectory(sourceDirectory, targetDirectory, true);
			}			
		}
		catch(Exception ex)
		{
			log.error("ERROR: Could not update the form actions directory");
			log.error(ExceptionUtils.getExceptionStackTrace(ex));
		}
	}
	
	protected EnumSet<FormAction> getSupportedActions() {
		return EnumSet.allOf(FormAction.class);
	}
	
	public EnumSet<XFormTemplate> getSupportedXFormTemplates() {
		return EnumSet.allOf( XFormTemplate.class );
	}
	
	public void setFormActionsPath(String formActionsPath) {
		this.formActionsPath = formActionsPath;
		updateFormActionsDirectory();
	}

	public void setPreviousFormActionTemplateFile(String previousFormActionTemplateFile) {
		this.previousFormActionTemplateFile = previousFormActionTemplateFile;
		this.previousFormAction = getTempate( previousFormActionTemplateFile );
	}

	public void setNextFormActionTemplateFile(String nextFormActionTemplateFile) {
		this.nextFormActionTemplateFile = nextFormActionTemplateFile;
		this.nextFormAction = getTempate(nextFormActionTemplateFile);
	}

	public void setSubmitFormActionTemplateFile(String submitFormActionTemplateFile) {
		this.submitFormActionTemplateFile = submitFormActionTemplateFile;
		this.submitFormAction = getTempate( submitFormActionTemplateFile );
	}

	public void setSaveFormActionTemplateFile(String saveFormActionTemplateFile) {
		this.saveFormActionTemplateFile = saveFormActionTemplateFile;
		this.saveFormAction = getTempate(saveFormActionTemplateFile);
	}


	public void setApproveFormActionTemplateFile(String approveFormActionTemplateFile) {
		this.approveFormActionTemplateFile = approveFormActionTemplateFile;
		this.approveFormAction = getTempate(approveFormActionTemplateFile);
	}


	public void setDeclineFormActionTemplateFile(String declineFormActionTemplateFile) {
		this.declineFormActionTemplateFile = declineFormActionTemplateFile;
		this.declineFormAction = getTempate(declineFormActionTemplateFile);
	}
	
	public void setReopenFormActionTemplateFile(String reopenFormActionTemplateFile) {
		this.reopenFormActionTemplateFile = reopenFormActionTemplateFile;
		this.reopenFormAction = getTempate(reopenFormActionTemplateFile);
	}

	protected String getPreviousFormAction() {
		return previousFormAction;
	}

	protected String getNextFormAction() {
		return nextFormAction;
	}

	protected String getSubmitFormAction() {
		return submitFormAction;
	}

	protected String getSaveFormAction() {
		return saveFormAction;
	}

	protected String getApproveFormAction() {
		return approveFormAction;
	}

	protected String getDeclineFormAction() {
		return declineFormAction;
	}
	
	protected String getReopenFormAction() {
		return reopenFormAction;
	}	

	/**
	 * Returns the content of the template file.
	 * @param template
	 * @return
	 */
	public final String getXFormTemplateActionSection( XFormTemplate template )
	{
		switch( template )
		{
		case APPROVE: return approveFormAction;
		case DECLINE: return declineFormAction;
		case NEXT: return nextFormAction;
		case PREVIOUS: return previousFormAction;
		case SAVE: return saveFormAction;
		case SUBMIT: return submitFormAction;
		case REOPEN: return reopenFormAction;
		default: return "";
		}
	}
	
	/**
	 * Updates the in-memory variable which stores the content of the template file.
	 * @param template
	 * @param action
	 */
	private void setXFormTemplateActionSectionHolder( XFormTemplate template, String action )
	{
		switch( template )
		{
		case APPROVE: 
			this.approveFormAction = action;
			break;
		case DECLINE: 
			this.declineFormAction = action;
			break;
		case NEXT: 
			this.nextFormAction = action;
			break;
		case PREVIOUS: 
			this.previousFormAction = action;
			break;
		case SAVE: 
			this.saveFormAction = action;
			break;
		case SUBMIT: 
			this.submitFormAction = action;
			break;
		case REOPEN: 
			this.reopenFormAction = action;
			break;
		default: 
			return;
		}
		
	}
	
	/**
	 * Returns the name of the template file.
	 * @param template
	 * @return
	 */
	private final String getXFormTemplateFile( XFormTemplate template )
	{
		switch( template )
		{
		case APPROVE: return this.approveFormActionTemplateFile;
		case DECLINE: return this.declineFormActionTemplateFile;
		case NEXT: return this.nextFormActionTemplateFile;
		case PREVIOUS: return this.previousFormActionTemplateFile;
		case SAVE: return this.saveFormActionTemplateFile;
		case SUBMIT: return this.submitFormActionTemplateFile;
		case REOPEN: return this.reopenFormActionTemplateFile;
		default: return "";
		}
	}
	
	abstract public String getXFormActionElementsSection(QuestionnaireForm.FormPosition formPosition, FormStatus formStatus, EnumSet<TagAccessPermissions> tagAccessPermissions);


	abstract public void changeFormInstanceStatus(String ownerId, String entityId, String formId, Long instanceId, String status);
	
}
