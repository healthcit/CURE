/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.healthcit.cacure.businessdelegates.ModuleManager;
import com.healthcit.cacure.businessdelegates.QuestionAnswerManager;
import com.healthcit.cacure.businessdelegates.export.DataExporter;
import com.healthcit.cacure.businessdelegates.export.DataImporter;
import com.healthcit.cacure.export.model.Cure;
import com.healthcit.cacure.model.BaseForm;
import com.healthcit.cacure.model.Module;
import com.healthcit.cacure.utils.AppConfig;
import com.healthcit.cacure.utils.Constants;
import com.healthcit.cacure.utils.Constants.ExportFormat;
import com.healthcit.cacure.xforms.XForm;
import com.healthcit.cacure.xforms.XModuleModel;
import com.healthcit.cacure.xforms.XForm.XFormContainerType;

@Controller
@RequestMapping(value=Constants.MODULE_XML_EXPORT_URI)
public class ModuleImportExportController {
	private static final Logger log = LoggerFactory.getLogger(ModuleImportExportController.class);

	@Autowired
	DataExporter dataExporter;
	
	@Autowired
	DataImporter dataImporter;
	
	@Autowired
	private ModuleManager moduleManager;
	
	@Autowired
	private QuestionAnswerManager qaManager;
	
	
	public final static String XML_FILE_EXTENSION = "xml";
	public final static String XFORM_FILE_EXTENSION = "xform";
	
	private static final Logger logger = LoggerFactory.getLogger(ModuleImportExportController.class);
	
	@RequestMapping(method =RequestMethod.GET)
	public void exportModule(
			@RequestParam(value="moduleId", required = true) long id,
			@RequestParam(value="format", required = true) String format,
			HttpServletResponse response)
	{
		

		try {
			response.setContentType("text/xml");
			
			OutputStream oStream = response.getOutputStream();
			Cure cureXml = dataExporter.constructModuleXML(id);
			JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.export.model");
			if(ExportFormat.XML.name().equals(format))
			{
				String fileNameHeader =
						String.format("attachment; filename=form-%d.xml;",
						id);

				response.setHeader("Content-Disposition", fileNameHeader);
				Marshaller m = jc.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				m.marshal(cureXml, oStream);
			}
			else if(ExportFormat.EXCEL.name().equals(format))
			{
				String fileNameHeader =
							String.format("attachment; filename=form-%d.xlxml;",
							id);

				response.setHeader("Content-Disposition", fileNameHeader);
				response.setContentType("application/xml");
				StreamSource xslSource = new StreamSource(this.getClass().getClassLoader().getResourceAsStream(AppConfig.getString(Constants.EXPORT_EXCEL_XSLT_FILE)));
			    JAXBSource xmlSource = new JAXBSource(jc, cureXml);
				Transformer transformer = TransformerFactory.newInstance().newTransformer(xslSource);
				transformer.transform(xmlSource,new StreamResult(oStream));
			}
			else if(ExportFormat.MAR.name().equals(format))
			{
				response.setContentType("application/zip");
				String fileNameHeader =
							String.format("inline; filename=module-%d.zip;",
							id);

				response.setHeader("Content-Disposition", fileNameHeader);
				//try {
					Module module = (Module)moduleManager.getModule(id);
					//ZipFileUtil.writeMar(module, oStream, qaManager);
					//baos = new ByteArrayOutputStream();
					ZipOutputStream zipOutStream = new ZipOutputStream(oStream);
					
					List<BaseForm> forms = module.getForms();
					
					// First we add the forms
					for(BaseForm form : forms) {
						
						String fileName = String.format("%s.%s", form.getUuid(), XFORM_FILE_EXTENSION);
						ZipEntry xformZipEntry = new ZipEntry(fileName);
						XForm xForm = new XForm( form, XFormContainerType.HTML , qaManager);
						Writer zipWriter = new PrintWriter(zipOutStream);
						try {
							zipOutStream.putNextEntry(xformZipEntry);
							xForm.write(zipWriter);
						} catch (Exception e) {
							logger.error("Error outputing module: " + module.getId(), e);
						}
					}
					
					// Then the metadata
					String fileName = "metaData." + XML_FILE_EXTENSION;
					ZipEntry metadataZipEntry = new ZipEntry(fileName);
					
					try {
						zipOutStream.putNextEntry(metadataZipEntry);
						XModuleModel moduleModel = new XModuleModel(module);
						moduleModel.writeMetadata(zipOutStream);
						
					} catch (IOException e) {
						logger.error("Error outputing module: " + module.getId(), e);
					}
					
					//Add export xml file
					String xmlFileName =
							String.format("export-module-%d.xml",
							id);
					ZipEntry moduleXmlZipEntry = new ZipEntry(xmlFileName);
					zipOutStream.putNextEntry(moduleXmlZipEntry);
					Marshaller m = jc.createMarshaller();
					m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
					m.marshal(cureXml, zipOutStream);
					zipOutStream.finish();
					//baos.writeTo(oStream);
//				} catch (IOException e) {
//					logger.error("Error outputing module as MAR: " + id, e);
//					e.printStackTrace();
			//	} 
					
					
//				} catch (IOException e) {
//					log.error("Unable to obtain output stream from the response");
//				}

			}
			oStream.flush();
			oStream.close();
		} catch (IOException e) {
			log.error("Unable to obtain output stream from the response");
			log.error(e.getMessage(), e);
		}
		catch(JAXBException e)
		{
			log.error("Unable to marshal the object");
			log.error(e.getMessage(), e);
		}
		catch(TransformerException e)
		{
			log.error("XSLT transformation failed");
			log.error(e.getMessage(), e);
		}
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView importModule(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			if(file != null)
			{
				Map<String, String> existingForms = new HashMap<String, String>();
				Map<String, String> existingModules = new HashMap<String, String>();
				List<String> existingQuestions = new ArrayList<String>();
				
				InputStream is = file.getInputStream();
				JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.export.model");
				Unmarshaller m = jc.createUnmarshaller();
				Cure cure = (Cure)m.unmarshal(is);
				dataImporter.importModule(cure, existingModules, existingForms, existingQuestions);
				if(existingModules.size()>0 || existingForms.size()>0 || existingQuestions.size()>0)
				{
					ModelAndView mav = new ModelAndView("formUploadStatus"); // initialize with view name
					ModelMap model = mav.getModelMap();
					model.addAttribute("existingModules", existingModules);
					model.addAttribute("existingForms", existingForms);
					model.addAttribute("existingQuestions", existingQuestions);
					return mav;
					/* there had been errors */
//					return new ModelAndView("formUploadStatus", "existingForms", existingForms);
				}
			}
			return new ModelAndView("formUploadStatus", "status", "OK");
		     
		}catch(Exception e)
		{
			log.error(e.getMessage(), e);
			return new ModelAndView("formUploadStatus", "status", "FAIL");
		}
		
	}
	
	
	
	

}
