/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.xforms;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cacure.businessdelegates.QuestionAnswerManager;
import com.healthcit.cacure.metadata.module.FormInstanceCollectionType;
import com.healthcit.cacure.metadata.module.FormStatusType;
import com.healthcit.cacure.metadata.module.FormType;
import com.healthcit.cacure.metadata.module.ModuleCollectionType;
import com.healthcit.cacure.metadata.module.ModuleStatusType;
import com.healthcit.cacure.metadata.module.ModuleType;
import com.healthcit.cacure.metadata.module.SkipRuleType;
import com.healthcit.cacure.model.Answer;
import com.healthcit.cacure.model.AnswerSkipRule;
import com.healthcit.cacure.model.AnswerValue;
import com.healthcit.cacure.model.BaseForm;
import com.healthcit.cacure.model.BaseQuestion;
import com.healthcit.cacure.model.BaseSkipPatternDetail;
import com.healthcit.cacure.model.FormElement;
import com.healthcit.cacure.model.FormSkipRule;
import com.healthcit.cacure.model.Module;
import com.healthcit.cacure.model.QuestionSkipRule;
import com.healthcit.cacure.model.QuestionnaireForm;


public class XModuleModel {
	private static final Logger log = LoggerFactory.getLogger(XModuleModel.class);

	@Autowired
	QuestionAnswerManager questionAnswerManager;

	private com.healthcit.cacure.metadata.module.ObjectFactory jaxbFactory =
		new com.healthcit.cacure.metadata.module.ObjectFactory();

	private Module moduleRoot;

	private Marshaller jaxbMarshaller;

	public XModuleModel(Module module) {
		this.moduleRoot = module;

		try {
			JAXBContext jc = JAXBContext.newInstance( "com.healthcit.cacure.metadata.module");

			jaxbMarshaller = jc.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

		} catch (JAXBException e) {
			log.error("Error creating module metadata via jaxb", e);
		}
	}

	public void writeMetadata(OutputStream oStream) {
		List<BaseForm> forms = moduleRoot.getForms();
		ModuleCollectionType collection = jaxbFactory.createModuleCollectionType();
		List<ModuleType> moduleList = collection.getModule();

		// for the purposes of deployment - only one module at a time
		ModuleType moduleType = createModuleType(moduleRoot);
		moduleList.add(moduleType);

		// add forms to this module
		List<FormType> formList = moduleType.getForm();
		boolean isFlat = true;
		for(BaseForm form : forms) {
			
			if ( form instanceof QuestionnaireForm )
			{
				QuestionnaireForm questionnaireForm = ( QuestionnaireForm ) form;
				
				if ( ! questionnaireForm.isChildForm() )
				{
					formList.add(createFormType(questionnaireForm));
				}
				if(isFlat)
				{
					if(questionnaireForm.isMultipleInstances())
					{
						isFlat= false;
					}
					else if(questionnaireForm.isParentForm() || questionnaireForm.isChildForm())
					{
						isFlat = false;
					}
				}
			}
		}
	     moduleType.setIsFlat(isFlat);
		JAXBElement<ModuleCollectionType> jaxbElement = jaxbFactory.createModules(collection);

		try {
			jaxbMarshaller.marshal(jaxbElement, oStream);
		} catch (JAXBException e) {
			log.error("Error creating module metadata via jaxb", e);
		}
	}

	public ModuleType createModuleType(Module module) {

		ModuleType moduleType = jaxbFactory.createModuleType();

		moduleType.setDescription(module.getDescription());
		moduleType.setName(module.getDescription());
		moduleType.setId(module.getUuid());
		moduleType.setEstimatedCompletionTime(module.getCompletionTime());

		switch(module.getStatus()) {
			case IN_PROGRESS:
				moduleType.setStatus(ModuleStatusType.IN_PROGRESS);
				break;
			case APPROVED_FOR_PILOT:
			case APPROVED_FOR_PRODUCTION:
			case RELEASED:
				moduleType.setStatus(ModuleStatusType.SUBMITTED);
				break;
		}

		return moduleType;
	}

	public FormType createFormType(QuestionnaireForm form) {

		FormType formType = jaxbFactory.createFormType();

		formType.setAuthor(form.getAuthor().getUserName());
		formType.setDescription(form.getName());
		formType.setName(form.getName());
		formType.setId(form.getUuid());
		
		FormInstanceCollectionType formInstances = jaxbFactory.createFormInstanceCollectionType();
		if ( StringUtils.isNotEmpty( form.getRelationshipName() ))
			formInstances.setInstanceGroup( form.getRelationshipName() );
		formInstances.setMaxInstances( form.isMultipleInstances() ? Long.MAX_VALUE : 1 );
		formType.setFormInstances( formInstances );
		
		FormSkipRule formSkipRule = form.getFormSkipRule();
//		List<FormSkip> formSkipSet = form.getFormSkip();

		if(formSkipRule != null)
		{
			//for (FormSkip formSkip: formSkipSet) {
			List<QuestionSkipRule> questionRules = formSkipRule.getQuestionSkipRules();
			for(QuestionSkipRule questionRule: questionRules)
			{
	
				SkipRuleType skipRuleType = new SkipRuleType();
				String logicalOp = questionRule.getLogicalOp();
				if (logicalOp != null)
				{
					skipRuleType.setLogicalOp(logicalOp);
				}
				
				List<AnswerSkipRule> parts = questionRule.getSkipParts();
				Map<String, String> skipAnswersMap = new HashMap<String, String>();
	            for(AnswerSkipRule part: parts)
	            {
	            	skipAnswersMap.put(part.getAnswerValueId(), part.getAnswerValue().getValue());
	            }
	            List<String> skipRuleTypeValues =  skipRuleType.getValue();
				BaseSkipPatternDetail skipDetail = questionRule.getDetails();
				BaseQuestion skipTrigQustn = skipDetail.getSkipTriggerQuestion();
	
				skipRuleType.setQuestionId(skipTrigQustn.getUuid());
				skipRuleType.setRule(questionRule.getRuleValue()); //show -- hide.
				skipRuleType.setFormId(skipDetail.getSkipTriggerForm().getUuid());
				if(questionRule.getIdentifyingAnswerValue() != null) {
					skipRuleType.setRowId(questionRule.getIdentifyingAnswerValue().getPermanentId());
				}
	
				Answer answer = skipTrigQustn.getAnswer();
	
	//			for(Answer answer: answersList) 
	//			{
					List<AnswerValue> answerValuesList = answer.getAnswerValues();
					for(AnswerValue answerValue : answerValuesList)
					{
						if( skipAnswersMap.containsKey(answerValue.getPermanentId()))
						{
							skipRuleTypeValues.add(answerValue.getValue());
						}
					}
	//			}
	
				formType.getSkipRule().add(skipRuleType);
			}
		}
		List<FormElement> formQuestions = form.getElements();
		int questionCount = formQuestions.size();
		formType.setQuestionCount(BigInteger.valueOf(questionCount));

		switch(form.getStatus()) {
			case IN_PROGRESS:
				formType.setStatus(FormStatusType.IN_PROGRESS);
				break;
			case IN_REVIEW:
				formType.setStatus(FormStatusType.SUBMITTED);
				break;
			case APPROVED:
				formType.setStatus(FormStatusType.COMPLETED);
				break;
		}
		
		// Add all child Forms
		for ( QuestionnaireForm childForm : form.getChildren() )
		{
			formType.getForm().add( createFormType( childForm ));
		}
		
		return formType;
	}
}
