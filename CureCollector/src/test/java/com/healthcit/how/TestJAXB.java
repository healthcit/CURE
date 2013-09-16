/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.healthcit.cacure.metadata.module.FormType;
import com.healthcit.cacure.metadata.module.ModuleCollectionType;
import com.healthcit.cacure.metadata.module.ModuleType;
import com.healthcit.how.models.Module;
import com.healthcit.how.models.QuestionnaireForm;

public class TestJAXB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("Hello JAXB");

		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
							"<modules " +
									"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
									"xmlns=\"http://www.healthcit.com/ModuleMetadata\">" +

									"<module id=\"Module3\" name=\"Post Treatment Module\" status=\"new\">" +
										"<form id=\"Form31\" name=\"Pre-Treatment stuff\" status=\"new\" question-count=\"11\">" +
		                                	"<description>Questions regarding your well-being before the treatment</description>" +
		                                "</form>" +
		                                "<description>Module designed to elicit treatment process opinion</description>" +
		                            "</module>" +
		                     "</modules>";

		try {

			JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.metadata.module");
			//Create unmarshaller
			Unmarshaller um = jc.createUnmarshaller();

			String fileName = "modules.xml";
			File myFile = new File(fileName);

			//System.out.println(myFile.toString());

			//ByteArrayInputStream input = new ByteArrayInputStream (xmlString.getBytes());

			JAXBElement element = (JAXBElement) um.unmarshal (myFile);

			ModuleCollectionType moduleCollecType = (ModuleCollectionType) element.getValue ();

			System.out.println("Number of Modules in xml document:" + moduleCollecType.getModule().size());

			List <ModuleType> moduleTypeList = moduleCollecType.getModule();
			List <Module> modulesList = new ArrayList<Module>();

			for(ModuleType ml: moduleTypeList) {

				Module module = new Module();

				module.setId(ml.getId());
				module.setName(ml.getName());
				module.setDescription(ml.getDescription());
				module.setStatus(ml.getStatus());

				List<QuestionnaireForm> formsList = new ArrayList<QuestionnaireForm>();

				for(FormType ft: ml.getForm()) {

					QuestionnaireForm form = new QuestionnaireForm();

					form.setId(ft.getId());
					form.setName(ft.getName());
					form.setDescription(ft.getDescription());
					form.setAuthor(ft.getAuthor());
					form.setQuestionCount(ft.getQuestionCount());
					form.setStatus(ft.getStatus());

					formsList.add(form);
				}

				module.setForms(formsList);
				modulesList.add(module);
			}

			//*********************************************

			int ctr = 1;
			int ctr2 = 1;

			for(Module module: modulesList) {

				System.out.println("Module Id: " + module.getId());
				System.out.println("Module Name: " + module.getName());
				System.out.println("Module Description: " + module.getDescription());
				System.out.println("Module Status: " + module.getStatus());

				System.out.println("Start of Form: ***************************");

				for(QuestionnaireForm form: module.getForms()) {

					System.out.println("Form Id: " + form.getId());
					System.out.println("Form Name: " + form.getName());
					System.out.println("Form Description: " + form.getDescription());
					System.out.println("Form Status: " + form.getStatus());
					System.out.println("Form Author: " + form.getAuthor());
					System.out.println("Form QuestionCount: " + form.getQuestionCount());

					System.out.println("End of Form: ***************************" + ctr2);
					ctr2++;
				}

				System.out.println("End of Module: ##############################" + ctr);
				ctr++;
			}

			System.out.println("All is good");

		} catch (JAXBException exj) {
			exj.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
