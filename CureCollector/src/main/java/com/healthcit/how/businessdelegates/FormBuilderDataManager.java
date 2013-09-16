/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sf.json.JSONObject;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cacure.export.model.Cure;
import com.healthcit.cacure.export.model.Cure.Form;
import com.healthcit.cacure.export.model.Cure.Form.ExternalQuestionElement;
import com.healthcit.cacure.export.model.Cure.Form.LinkElement;
import com.healthcit.cacure.export.model.Cure.Form.LinkElement.SourceElement;
import com.healthcit.cacure.export.model.Cure.Module;
import com.healthcit.cacure.export.model.Cure.Module.Section;
import com.healthcit.cacure.export.model.Description;
import com.healthcit.cacure.export.model.QuestionElementType;
import com.healthcit.cacure.export.model.QuestionType;
import com.healthcit.cacure.export.model.QuestionType.Answer;
import com.healthcit.cacure.export.model.QuestionType.Answer.AnswerValue;
import com.healthcit.cacure.export.model.TableElementType;
import com.healthcit.cacure.export.model.TableElementType.Question;
import com.healthcit.cacure.metadata.gateway.ObjectFactory;
import com.healthcit.cacure.metadata.gateway.OwnerType;
import com.healthcit.how.dao.FormBuilderDataDao;
import com.healthcit.how.handlers.FormMetaDataSummaryHandler;
import com.healthcit.how.models.FormMetaDataSummary;
import com.healthcit.how.utils.Constants;
import com.healthcit.how.utils.JSONUtils;

public class FormBuilderDataManager {
	
	@Autowired
	FormBuilderDataDao formBuilderDao;
	
	@Autowired
	private JAXBContext gatewayJaxbContext;
	
	private  enum ElementType {QUESTION, TABLE};
	private  enum JSONValidationType { GATEWAY };
	
	public void load (File dataFile) throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance("com.healthcit.cacure.export.model");
		Unmarshaller m = jc.createUnmarshaller();
		Cure cure = (Cure)m.unmarshal(dataFile);
		
		List<Form> forms = cure.getForm();
		List<Module> modules = cure.getModule();
		Map<String, String> formIdToParentId = new HashMap<String, String>();
		for(Module module: modules)
		{
			List<Section> sections = module.getSection();
			for(Section section: sections)
			{
				String parentId = (section.getParentId()!= null) ? section.getParentId(): "";
				Form refForm = (Form)section.getRef();
				formIdToParentId.put(refForm.getId(), parentId);
			}
		}
		for(Form form : forms)
		{
			Map<String, Object> formData = new HashMap<String, Object> ();
			formData.put(FormBuilderDataDao.FORM_UUID, form.getId());
			formData.put(FormBuilderDataDao.FORM_NAME, form.getName());
			formData.put(FormBuilderDataDao.FORM_MULTIPLE_INSTANCES, form.isMultipleInstances());
			formData.put(FormBuilderDataDao.FORM_PARENT_UUID, formIdToParentId.get(form.getId()));
			//formData.put(FormBuilderDataDao.FORM_PARENT_UUID, form.getParentId());
			//formData.put(FormBuilderDataDao.FORM_PARENT_UUID, "");
			
			formBuilderDao.saveFormData(formData);
			/*Question Elements */
			
			List<QuestionElementType> questionElements = form.getQuestionElement();
			for(QuestionElementType questionElement: questionElements)
			{
				Map<String, Object> questionData = new HashMap<String, Object> ();
				Description description = questionElement.getDescriptions();
				QuestionType question = questionElement.getQuestion();
				questionData.put(FormBuilderDataDao.PARENT_FORM_UUID, form.getId());
				questionData.put(FormBuilderDataDao.QUESTION_ELEMENT_UUID, questionElement.getUuid());
				questionData.put(FormBuilderDataDao.QUESTION_ELEMENT_DESCRIPTION, description.getMainDescription());
				questionData.put(FormBuilderDataDao.QUESTION_IS_READONLY, questionElement.isIsReadonly());
				populateQuestionData(questionData, question, ElementType.QUESTION);
			}
			/*External Questions */
			List<ExternalQuestionElement> externalQuestions = form.getExternalQuestionElement();
			for(ExternalQuestionElement externalQuestion: externalQuestions)
			{
				Map<String, Object> questionData = new HashMap<String, Object> ();
				QuestionType question = externalQuestion.getQuestion();
				Description description = externalQuestion.getDescriptions();
				
				questionData.put(FormBuilderDataDao.PARENT_FORM_UUID, form.getId());
				questionData.put(FormBuilderDataDao.QUESTION_ELEMENT_UUID, externalQuestion.getUuid());
				questionData.put(FormBuilderDataDao.QUESTION_ELEMENT_DESCRIPTION, description.getMainDescription());
				questionData.put(FormBuilderDataDao.QUESTION_IS_READONLY, externalQuestion.isIsReadonly());
				populateQuestionData(questionData, question, ElementType.QUESTION);
			}
			
			/*Table Elements */
			List<TableElementType> tables = form.getTableElement();
			for(TableElementType tableElement: tables)
			{
				Map<String, Object> tableData = new HashMap<String, Object>();
				
				tableData.put(FormBuilderDataDao.PARENT_FORM_UUID, form.getId());
				tableData.put(FormBuilderDataDao.TABLE_ELEMENT_TYPE, tableElement.getTableType());
				tableData.put(FormBuilderDataDao.TABLE_ELEMENT_UUID, tableElement.getUuid());
				tableData.put(FormBuilderDataDao.TABLE_ELEMENT_DESCRIPTION, tableElement.getDescriptions().getMainDescription());
				tableData.put(FormBuilderDataDao.TABLE_ELEMENT_SHORT_NAME, tableElement.getTableShortName());
				List<Question> questions = tableElement.getQuestion();
				for(Question question: questions)
				{
					populateQuestionData(tableData, question, ElementType.TABLE);
				}
			}
			
			/*Link Elements */
			List<LinkElement> linkElements = form.getLinkElement();
			for(LinkElement linkElement: linkElements)
			{	
				Map<String, Object> questionData = new HashMap<String, Object> ();
				SourceElement source = linkElement.getSourceElement();
				QuestionElementType questionElement = source.getQuestionElement();
				TableElementType tableElement = source.getTableElement();
				if(questionElement != null)
				{
					
					questionData.put(FormBuilderDataDao.PARENT_FORM_UUID, form.getId());
					questionData.put(FormBuilderDataDao.QUESTION_ELEMENT_UUID, questionElement.getUuid());
					questionData.put(FormBuilderDataDao.QUESTION_ELEMENT_DESCRIPTION, linkElement.getDescription());
					questionData.put(FormBuilderDataDao.QUESTION_IS_READONLY, questionElement.isIsReadonly());
					
					QuestionType question = questionElement.getQuestion();
					populateQuestionData(questionData, question, ElementType.QUESTION);
					
				
				}
				else
				{
					Map<String, Object> tableData = new HashMap<String, Object>();
					
					tableData.put(FormBuilderDataDao.PARENT_FORM_UUID, form.getId());
					tableData.put(FormBuilderDataDao.TABLE_ELEMENT_TYPE, tableElement.getTableType());
					tableData.put(FormBuilderDataDao.TABLE_ELEMENT_UUID, tableElement.getUuid());
					tableData.put(FormBuilderDataDao.TABLE_ELEMENT_DESCRIPTION, tableElement.getDescriptions().getMainDescription());
					tableData.put(FormBuilderDataDao.TABLE_ELEMENT_SHORT_NAME, StringUtils.trim( tableElement.getTableShortName() ));
					List<Question> questions = tableElement.getQuestion();
					for(Question question: questions)
					{
						populateQuestionData(tableData, question, ElementType.TABLE);
					}
				
				}
			}
			
		}
		
	}
	private void populateQuestionData(Map<String, Object> questionData, QuestionType question, ElementType elementType)
	{
		
		questionData.put(FormBuilderDataDao.QUESTION_UUID, question.getUuid());
		questionData.put(FormBuilderDataDao.QUESTION_SHORT_NAME, StringUtils.trim( question.getShortName() ));
		questionData.put(FormBuilderDataDao.QUESTION_ORDER, question.getOrder());
		questionData.put(FormBuilderDataDao.QUESTION_TYPE, question.getAnswerType());
		if(ElementType.TABLE.equals(elementType))
		{
			questionData.put(FormBuilderDataDao.QUESTION_IS_IDENTIFYING, ((Question)question).isIsIdentifying());
		}
		
		Answer answer = question.getAnswer();
		questionData.put(FormBuilderDataDao.ANSWER_TYPE, answer.getType());
		questionData.put(FormBuilderDataDao.ANSWER_UUID, answer.getUuid());
		questionData.put(FormBuilderDataDao.ANSWER_VALUE_CONSTRAINT, answer.getValueConstraint());
			
		List<AnswerValue> answerValues = answer.getAnswerValue();
		if(answerValues!=null && answerValues.size()>0)
			for(AnswerValue answerValue: answerValues)
			{
				questionData.put(FormBuilderDataDao.ANSWER_VALUE_VALUE, answerValue.getValue());
				questionData.put(FormBuilderDataDao.ANSWER_VALUE_ORDER, answerValue.getOrder());
				questionData.put(FormBuilderDataDao.ANSWER_VALUE_DESCRIPTION, answerValue.getDescription());
				questionData.put(FormBuilderDataDao.ANSWER_VALUE_UUID, answerValue.getUuid());
				
				
				if(ElementType.QUESTION.equals(elementType))
				{
					formBuilderDao.saveQuestionData(questionData);
				}
				else
				{
					formBuilderDao.saveTableData(questionData);
				}
			}
		/* for dynamic tables it seems that we do not store answer value in formBuilder */
		else
		{
			questionData.put(FormBuilderDataDao.ANSWER_VALUE_VALUE, "");
			questionData.put(FormBuilderDataDao.ANSWER_VALUE_ORDER, 1);
			questionData.put(FormBuilderDataDao.ANSWER_VALUE_DESCRIPTION, "");
			questionData.put(FormBuilderDataDao.ANSWER_VALUE_UUID, "");
			if(ElementType.QUESTION.equals( elementType ) )
			{
				formBuilderDao.saveQuestionData(questionData);
			}
			else
			{
				formBuilderDao.saveTableData(questionData);
			}
		}
	}
	
	public String buildGatewayFormInstanceDataXml( JSONObject jsonSource )
	throws Exception
	{
		// Validate the JSON to make sure that all is OK
		// (if the JSON is invalid, then an exception will be thrown)
		validateJSONSource( jsonSource, JSONValidationType.GATEWAY );
		
		
		
		// Get the user ID associated with this JSONObject
		String userId = getUserIdFromJson( jsonSource );
		
		
		
		// Get the form metadata associated with the questions in "json"
		String questionShortNameList = getQuestionShortNamesFromJson( jsonSource );		
		List<FormMetaDataSummary> formData = formBuilderDao.getFormMetaDataByQuestionShortNames( questionShortNameList );
		
		
		
		// Construct the XML
		String formInstanceXml = constructXml(jsonSource, formData, userId);
		
		
		// return the XML
		return formInstanceXml;
	}
	
	
	private String constructXml( JSONObject json, List<FormMetaDataSummary> formData, String userId )
	throws JAXBException
	{
		Marshaller m                        	  = gatewayJaxbContext.createMarshaller();		
		ObjectFactory objectFactory         	  = new ObjectFactory();		
		JSONObject questionsAndAnswers  		  = getQuestionAndAnswerValuesFromJSON( json );
		OwnerType ownerType                 	  = constructOwnerType( objectFactory, userId );
		FormMetaDataSummaryHandler handler        = new FormMetaDataSummaryHandler(formData, objectFactory, questionsAndAnswers, ownerType);
				
		// Iterate through the list of metadata elements.
		// (For each metadata-related element in the "formData" collection,
		//  this process will generate the appropriate formType elements, and add to the ownerType as appropriate.)
		handler.iterateThroughMetaDataElements();
				
		// Marshall to output
		JAXBElement<OwnerType> owner  = objectFactory.createOwner( ownerType );
		ByteArrayOutputStream  output = new ByteArrayOutputStream();
		m.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE );
		m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		m.marshal( owner, output );
		
		return output.toString();
	}
	
	private OwnerType constructOwnerType( ObjectFactory objectFactory, String userId )
	{
		OwnerType ownerType = objectFactory.createOwnerType();
		ownerType.setUserid( userId );	
		return ownerType;
	}
			
	private void validateJSONSource( JSONObject jsonSource, JSONValidationType validationType )
	throws Exception
	{
		JSONObject questionShortNames = ( JSONObject )JSONUtils.getObject( jsonSource, Constants.GATEWAY_QUESTION_SHORT_NAMES );		
		String userId                 = ( String ) JSONUtils.getObject( jsonSource, Constants.GATEWAY_PATIENT_ID );		
		String facilityCode			  = ( String ) JSONUtils.getObject( jsonSource, Constants.GATEWAY_FACILITY_CODE );
		String notificationEmail	  = ( String ) JSONUtils.getObject( jsonSource, Constants.GATEWAY_NOTIFICATION_EMAIL );		
		StringBuilder errorMessage	  = new StringBuilder();	
		
		switch ( validationType )
		{
		case GATEWAY:
			if ( questionShortNames == null ) errorMessage.append( "Question short names not provided\n" );		
			if ( userId == null )             errorMessage.append( "User ID not provided\n" );
			if ( facilityCode == null )       errorMessage.append( "Facility code not provided\n");
			if ( notificationEmail == null )  errorMessage.append( "Notification email not provided\n");
			break;
		
		default:
			break;
		}
		
		if ( errorMessage.length() > 0 ) throw new Exception( errorMessage.toString() );
	}
	
	private String getQuestionShortNamesFromJson( JSONObject json )
	{
		JSONObject shortNamesJsonObject = getQuestionAndAnswerValuesFromJSON( json );
		
		String shortNames = ( shortNamesJsonObject == null ? null : shortNamesJsonObject.names().join(",").replaceAll("\"", "'") );
		
		return shortNames;
	}
	
	private String getUserIdFromJson( JSONObject json )
	{
		String userId = ( String ) JSONUtils.getObject( json, Constants.GATEWAY_PATIENT_ID );
		
		return userId;
	}
	
	private JSONObject getQuestionAndAnswerValuesFromJSON( JSONObject json )
	{
		JSONObject shortNamesJsonObject = ( JSONObject )JSONUtils.getObject( json, Constants.GATEWAY_QUESTION_SHORT_NAMES );
		
		return shortNamesJsonObject;
	}
}
