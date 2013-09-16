/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.web.controller.admin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.healthcit.cacure.metadata.module.FormInstanceCollectionType;
import com.healthcit.cacure.metadata.module.FormType;
import com.healthcit.cacure.metadata.module.ModuleCollectionType;
import com.healthcit.cacure.metadata.module.ModuleType;
import com.healthcit.cacure.metadata.module.SkipRuleType;
import com.healthcit.how.businessdelegates.FormBuilderDataManager;
import com.healthcit.how.businessdelegates.FormManager;
import com.healthcit.how.businessdelegates.ModuleDeploymentManager;
import com.healthcit.how.businessdelegates.TagManager;
import com.healthcit.how.editors.TagPropertyEditor;
import com.healthcit.how.models.FormSkip;
import com.healthcit.how.models.Module;
import com.healthcit.how.models.Module.ModuleStatus;
import com.healthcit.how.models.ModuleFile;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.SkipPart;
import com.healthcit.how.models.Tag;
import com.healthcit.how.utils.Constants;
import com.healthcit.how.utils.IOUtils;
import com.healthcit.how.utils.URLUtils;

/**
 *
 * @author Suleman Choudhry
 *
 */

@Controller
public class UploadModuleController {

	public static final String COMMAND_NAME = "moduleFileCmd";
	public static final String UPLOAD_STATUS = "uploadStatus";
	public static final String MODULE = "module";
	public static final String MODULE_MAP = "moduleMap";

	/* Logger */
	private static final Logger log = LoggerFactory.getLogger( UploadModuleController.class );

	@Autowired
	private TagManager tagManager;
	
	@Autowired
	private FormManager formManager;
	
	@Autowired
	private FormBuilderDataManager formBuilderDataManager;
	
	@Autowired
	private ModuleDeploymentManager moduleDeploymentManager;
	
	private ResourceBundle remoteServices = ResourceBundle.getBundle( "remoteServiceUrls", Locale.getDefault(), this.getClass().getClassLoader() );
	
	@ModelAttribute(COMMAND_NAME)
	public ModuleFile createCommand()
	{
		log.debug("in UploadModuleController.createCommand() **************");		
		
		return new ModuleFile();
	}
	
	@InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(Tag.class, new TagPropertyEditor(tagManager));
        //dataBinder.registerCustomEditor(null, "skipRule", new SkipPatternPropertyEditor<FormElementSkipRule>(FormElementSkipRule.class, skipDao));
    }
	
	
	@ModelAttribute(MODULE)
	public Module initModuleData(HttpSession session) throws Exception
	{
		
		Module module = null;
		@SuppressWarnings("unchecked")
		Map<String, String> uploadStatus = (Map<String, String>)session.getAttribute(UPLOAD_STATUS);
		if(uploadStatus!= null)
		{
			
//			try
//			{
				String tempDirectoryName = uploadStatus.get("tmpLocation");
				String archiveName = uploadStatus.get("fileName");
//				if(tempDirectoryName != null)
//				{
					File tempDir = new File(tempDirectoryName);
			    	String dataDirPath = moduleDeploymentManager.getDataDirPath();
			    	File destination = new File(dataDirPath);
					if(!destination.exists()){
						log.debug("Destination dir doesnt exists:" + dataDirPath);
						destination.mkdir();
					}
					IOUtils.copyDirectory(tempDir, destination, archiveName);
					File metaFile = findMetaDataFile(tempDir);
					module = loadModuleInfo(tempDir, metaFile.getName(), dataDirPath);
//				}
//			}
//			catch(Exception e)
//			{
//				log.error(e.getMessage(), e);
//			}
		}

		return module;
	}
	
	@ModelAttribute("uploadMessages")
	public List<String> getUploadMessages(){
		return new ArrayList<String>();
	}

    @RequestMapping(value = "/admin/uploadModule.form",method = RequestMethod.POST)  
    public ModelAndView handleFormUpload(
        		@ModelAttribute("uploadMessages") List<String> uploadMessages,
        		@RequestParam(value = "context", required = true) String context,
                @RequestParam("file") MultipartFile file,
                HttpServletRequest request, 
                HttpServletResponse response, 
                HttpSession session) throws Exception {

    	
    	ModelAndView modelAndView = null;
//    	if(moduleFileName != null && (moduleFileName.equalsIgnoreCase("") || moduleFileName.length() == 0)){
//    		log.error("Module Descriptor Name is required!");
//    		throw new Exception("Module Descriptor Name is required!");
//    	}

    	log.debug("in UploadModuleController.handleFormUpload() **************");
    	File tempDir = IOUtils.getTempDir();
    	
    	String dataDirPath = moduleDeploymentManager.getDataDirPath();
    	String archiveFileName = file.getOriginalFilename();
    	String archiveFilePath = tempDir.getPath() + File.separator + archiveFileName;

    	try {
    		if (!file.isEmpty()) {

				byte[] bytes = file.getBytes();
				FileOutputStream fos = new FileOutputStream(archiveFilePath);
				fos.write(bytes);
				fos.close();

				//unzip the archive file.
				IOUtils.unzipFile(archiveFilePath, tempDir.getPath());
				File metaFile = findMetaDataFile(tempDir);
				String moduleFileName = metaFile.getName();
//				for(File metaFile: metaFiles)
//				{
//					Module module = loadModuleInfo(tempDir, metaFile.getName(), dataDirPath);
//					moduleManager.updateModuleData(module);
//				}

				Module module = loadModuleInfo(tempDir, moduleFileName, dataDirPath);
				module.setContext(context);
				boolean isNewModule = true;
				//check if module already exists
				Module storedModule = moduleDeploymentManager.checkIfModuleExists(module.getId());
//				if(!moduleManager.isNewModule(module))
				if(storedModule!= null)
				{
					isNewModule = false;
				}
				
				List<Tag> tags = tagManager.getAllTags();
				Map<String, String> lookupData = new HashMap<String, String>();
				lookupData.put("fileLocation", "tmp directory" );
				lookupData.put("fileName", archiveFileName);
				lookupData.put("moduleFileName", moduleFileName);
				lookupData.put("tmpLocation",tempDir.getPath() );
				
				lookupData.put("newModuleContext", context);
				if(isNewModule)
				{
					lookupData.put("status", "success");
					lookupData.put("isNew", "true");
	
					modelAndView = new ModelAndView("assignTags", UPLOAD_STATUS, lookupData);		
	
				}
				else
				{
					lookupData.put("moduleContext", storedModule.getContext());
					lookupData.put("status", "error");
					lookupData.put("isNew", "false");
					modelAndView = new ModelAndView("assignTags");	
					//modelAndView = new ModelAndView("assignTags", UPLOAD_STATUS, lookupData);
				}
//				modelAndView.addObject(MODULE_MAP, initModuleMap());
				modelAndView.addObject(MODULE, module);
				modelAndView.addObject("tags", tags);
				session.setAttribute(UPLOAD_STATUS, lookupData);
	        } else {
	        	log.debug("file is empty *********");
	        	modelAndView = setModelAndViewWithErrorsOrMessages(uploadMessages, "An error occurred during processing: Uploaded file was empty", session);  	
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			modelAndView = setModelAndViewWithErrorsOrMessages(uploadMessages, null, session);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			modelAndView = setModelAndViewWithErrorsOrMessages(uploadMessages, null, session);
		}
return modelAndView;
    }
    
    @RequestMapping(value="/admin/updateFormSkip.form", method = RequestMethod.GET)
    public String showUpdateFormSkip()
    {
    	return "updateFormSkipData";
    }
    
    @RequestMapping(value = "/admin/uploadModule.form",method = RequestMethod.POST, params="confirmed")
    public ModelAndView updateModule(@ModelAttribute("uploadMessages") List<String> uploadMessages,
    								 @RequestParam(value = "dirName", required = true) String tempDirectoryName,
    		                         @RequestParam(value = "archiveName", required = true) String archiveName,
    		                         @RequestParam(value = "isNew", required = true) String isNew)
    {
    	
//    	String tempDirPath = IOUtils.getTempDirBase();
    	File tempDir = new File(tempDirectoryName);
    	
    	String dataDirPath = moduleDeploymentManager.getDataDirPath();
    	File destination = new File(dataDirPath);
		if(!destination.exists()){
			log.debug("Destination dir doesnt exists:" + dataDirPath);
			destination.mkdir();
		}
		try
		{
			IOUtils.copyDirectory(tempDir, destination, archiveName);
			File metaFile = findMetaDataFile(tempDir);
			Module module = loadModuleInfo(tempDir, metaFile.getName(), dataDirPath);
			moduleDeploymentManager.updateModuleData(module);
			if(Boolean.parseBoolean(isNew))
			{
				log.debug("Registering modules...");
				moduleDeploymentManager.addModuleToAllSharingGroups(module);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return setModelAndViewWithErrorsOrMessages(uploadMessages, null, null);
		}
    	
    	return setModelAndViewWithErrorsOrMessages(uploadMessages, "Upload was successful.", null);
    }
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/admin/uploadModule.form",method = RequestMethod.POST, params="saveTags")
    public ModelAndView saveTags(@ModelAttribute(MODULE) Module module, 
    							 @ModelAttribute("uploadMessages") List<String> uploadMessages,
    							 HttpSession session)
    {
    	boolean isNewModule = false;
		try
		{
			isNewModule = Boolean.parseBoolean(((Map<String, String>)session.getAttribute(UPLOAD_STATUS)).get("isNew"));
			if(isNewModule)
			{
				moduleDeploymentManager.updateModule(module);
				log.debug("Registering modules...");
				moduleDeploymentManager.addModuleToAllSharingGroups(module);
			}else
			{
				moduleDeploymentManager.updateModuleData(module);
			}
			
			Map<String, String> uploadStatus = (Map<String, String>) session.getAttribute(UPLOAD_STATUS);
			
			String tempDirectoryName = uploadStatus.get("tmpLocation");
			File tempDir = new File(tempDirectoryName);
			
			//Update formBuilderData for ETL
			File formBuilderDataFile = findFormBuilderDataFile(tempDir);
			
			formBuilderDataManager.load(formBuilderDataFile);
			
			session.removeAttribute(UPLOAD_STATUS);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			return setModelAndViewWithErrorsOrMessages(uploadMessages, null, session);
		}
    	
    	return isNewModule ?
    		   setModelAndViewWithErrorsOrMessages(uploadMessages, "Upload was successful.", null) :
    		   new ModelAndView( "updateFormSkipData", MODULE, module );
    }
    
    @RequestMapping(value = "/admin/uploadModule.form",method = RequestMethod.POST, params="recalculateFormSkips")
    public ModelAndView completeFormSkipUpdate(@ModelAttribute("uploadMessages") List<String> uploadMessages)
    {
    	return doFormSkipUpdate(uploadMessages);
    }
    
    @RequestMapping(value = "/admin/updateFormSkip.form", method = RequestMethod.POST)
    public ModelAndView updateFormSkipData(@ModelAttribute("uploadMessages") List<String> uploadMessages)
    {
		return doFormSkipUpdate(uploadMessages);
    }
    
    private ModelAndView doFormSkipUpdate(List<String> uploadMessages)
    {
    	try
		{
			moduleDeploymentManager.deleteAllNonVisibleFormInstances(null);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			return setModelAndViewWithErrorsOrMessages(uploadMessages, null, null);
		}
    	
    	return setModelAndViewWithErrorsOrMessages(uploadMessages, "Upload was successful.", null);
    }
    
    private File findFormBuilderDataFile(File directory)
    {
    	File xmlFile = null;
    	FileFilter filter = new FileFilter() {

    		@Override
			public boolean accept(File filename) {
	            return filename.getName().startsWith("export-module");
	        }
    	};
    	File[] xmlFiles = directory.listFiles(filter);
    	if (xmlFiles!=null && xmlFiles.length>0)
    	{
    		xmlFile = xmlFiles[0];
    	}
    	return xmlFile;
    }
    private File findMetaDataFile(File directory)
    {
    	File xmlFile = null;
    	FileFilter filter = new FileFilter() {

    		@Override
			public boolean accept(File filename) {
	            return filename.getName().toLowerCase().startsWith("metadata");
	        }
    	};
    	File[] xmlFiles = directory.listFiles(filter);
    	if (xmlFiles!=null && xmlFiles.length>0)
    	{
    		xmlFile = xmlFiles[0];
    	}
    	return xmlFile;
    }
    @RequestMapping(value = "/admin/uploadModule.form",method = RequestMethod.GET)
	public ModelAndView showUploadModule(@ModelAttribute(COMMAND_NAME) ModuleFile moduleFile, HttpSession session) {

		log.debug("in UploadModuleController.showUploadModule() ****************");


//		Map<String, String> lookupData = new HashMap<String, String>();
//		lookupData.put("uploadPage", "true");
		ModelAndView modelAndView = new ModelAndView("uploadModule");
//		session.setAttribute(UPLOAD_STATUS, lookupData);
		return (modelAndView);
	}

    //private List <Module> loadModuleInfo(String dataDirPath, String moduleFileName){
    private Module loadModuleInfo(File xformLocationDir, String moduleFileName, String destination) throws Exception
    {

    	Module module = null;

		log.debug("destination path: " + destination);

		try {

			JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.metadata.module");
			//Create unmarshaller
			Unmarshaller um = jc.createUnmarshaller();
//			File moduleFile = new File(destination.getPath() + File.separator + moduleFileName);
			File moduleFile = new File(xformLocationDir.getPath() + File.separator + moduleFileName);
			
			@SuppressWarnings("rawtypes")
			JAXBElement element = (JAXBElement) um.unmarshal (moduleFile);

			ModuleCollectionType moduleCollecType = (ModuleCollectionType) element.getValue ();
			List <ModuleType> moduleTypeList = moduleCollecType.getModule();
			if(moduleTypeList.size()!=1)
			{
				log.debug("Number of Modules in xml document:" + moduleCollecType.getModule().size());
				throw new RuntimeException("Found "+ moduleTypeList.size()+ " modules in the MAR file, should be one");
			}

			ModuleType ml =  moduleTypeList.get(0);

			module = new Module();

			module.setId(ml.getId());
			module.setName(ml.getName());
			module.setDescription(ml.getDescription());
			module.setStatus(ml.getStatus() == null ? ModuleStatus.INACTIVE : ModuleStatus.ACTIVE );
			module.setIsFlat(ml.isIsFlat());
			List<QuestionnaireForm> formsList = new ArrayList<QuestionnaireForm>();

			int formOrder = 0;

			for(FormType ft: ml.getForm()) {
				formOrder = loadFormInfo(xformLocationDir, destination, ft, ++formOrder, formsList, null);
			}
			module.setForms(formsList);
			
			log.debug("All is good");

		} catch (JAXBException exj) {
			log.error(exj.toString(), exj);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		
		return module;
    }
    
    private int loadFormInfo(File xformLocationDir, String destination, FormType ft, int formOrder, List<QuestionnaireForm> formList, QuestionnaireForm parentForm)
    throws Exception
    {
    	//String xformLocaiton = destination.getPath() + File.separator + ft.getId() + ".xform";
		String xformLocaiton = xformLocationDir.getPath() + File.separator + ft.getId() + ".xform";
		String xformDestination = new File(destination).getPath() + File.separator + ft.getId() + ".xform";
		log.debug("xformLocaiton:" + xformLocaiton);

		QuestionnaireForm form = new QuestionnaireForm();
		form.setId(ft.getId());
		File tempFile = new File(xformLocaiton);

		if(!tempFile.exists())
		{
			log.debug("file: " + tempFile.getPath() + " --- not found **********");
			throw new IOException("file: " + tempFile.getPath() + " --- not found **********");
		}
		
		form.setName(ft.getName());
		form.setDescription(ft.getDescription());
		form.setAuthor(ft.getAuthor() != null ? ft.getAuthor() : "");
		form.setQuestionCount(ft.getQuestionCount());
		form.setStatus(ft.getStatus().name());
		form.setXformLocation(xformDestination);
		form.setOrder(new Long(formOrder));
		findTags(form);

		List<SkipRuleType> skipRuleTypeList = ft.getSkipRule();

		for(SkipRuleType skipRuleType : skipRuleTypeList)
		{
			FormSkip formSkip = new FormSkip();
			formSkip.setLogicalOp(skipRuleType.getLogicalOp());
			formSkip.setQuestionId(skipRuleType.getQuestionId());
			formSkip.setQuestionOwnerFormId(skipRuleType.getFormId());
			formSkip.setRowId(skipRuleType.getRowId());
			List<String> values = skipRuleType.getValue();
			for(String value: values)
			{
				SkipPart part = new SkipPart();
				part.setParentSkip(formSkip);
				part.setAnswerValue(value);
				formSkip.getSkipParts().add(part);
			}
			formSkip.setRule(skipRuleType.getRule());
			form.addFormSkip(formSkip);
		}
		
		FormInstanceCollectionType formInstances = ft.getFormInstances();
		if ( formInstances != null )
		{
			form.setMaxInstances( formInstances.getMaxInstances() );
			form.setInstanceGroup( formInstances.getInstanceGroup() );
		}
		
		if ( parentForm != null ) parentForm.addChildForm( form );
		
		formList.add(form);
		
		for ( FormType childFt : ft.getForm() )
		{
			formOrder = loadFormInfo(xformLocationDir, destination, childFt, ++formOrder, formList, form);
		}
		
		return formOrder;
    }


	private void findTags(QuestionnaireForm form)
	{
		QuestionnaireForm storedForm = formManager.getForm(form.getId());

		if(storedForm == null)
		{
			//Try to find forms with similar name and get the tag from it
			storedForm = formManager.getFormByName(form.getName());
			
		}
		if(storedForm != null)
		{
			form.setTag(storedForm.getTag());
		}
		
	}

	class JsonResponse
	{
	private String name;
	private String value;
	
	public String getName()
	{
		return name;
	}
	public String getValue()
	{
		return value;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public void setValue(String value)
	{
		this.value = value;
	}
	}
	
	// API method
	@RequestMapping(value="/apiadmin/UpdateLockedFormSkipData")
	public void updateFormSkipData(
			@RequestParam(value="moduleId", required=false) String moduleId,
			HttpServletResponse response)
	{
		try
		{
			moduleDeploymentManager.deleteAllNonVisibleFormInstances(moduleId);
			JSONObject statusInfo = new JSONObject();				
			statusInfo.put( Constants.RESPONSE_STATUS, Constants.STATUS_OK);			
			IOUtils.sendResults(response, Constants.CONTENT_TYPE_JSON,  statusInfo);
		}
		catch(Exception e)
		{
			IOUtils.sendProcessingError(response, e);
		}
	}
	
	/* Helper Methods */
	
	public ModelAndView setModelAndViewWithErrorsOrMessages(List<String> uploadMessages, String message, HttpSession session)
	{
		boolean errorOccurred = false;
		
		if ( session != null )
		{
			session.removeAttribute( UPLOAD_STATUS );
		}
		
		if ( uploadMessages != null )
		{
			if ( StringUtils.isBlank( message ) ) 
			{
				message = "An error has occurred during processing. Please contact your System Administrator.";
				
				errorOccurred = true;
			}
			
			uploadMessages.add( message );
			
			ModelAndView modelAndView = new ModelAndView( "mainAdmin" );
			
			modelAndView.addObject( "uploadMessages", uploadMessages );
			
			
			

			// Execute any post-processing URLs, if applicable			
			List<String> moduleRedeploymentUrls = new ArrayList<String>();
			
			if ( !errorOccurred )
			{				
				Enumeration<String> urls = remoteServices.getKeys();
				
				while ( urls.hasMoreElements() )
				{
					String url = remoteServices.getString( urls.nextElement() );
					if ( URLUtils.isValidUrl(url) )
					{
						log.info("Post-processing url: " + url );
						moduleRedeploymentUrls.add( url );
					}
				}				
				
			}

			modelAndView.addObject( "moduleRedeploymentUrls", moduleRedeploymentUrls );
			
			
			
			//return moduleAndView
			return modelAndView;
			
		}
		else 
		{
			return null;
		}
		
	}
}
