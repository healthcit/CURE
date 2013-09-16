/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.xwork.StringEscapeUtils;
import org.apache.commons.lang.xwork.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.NumberFormat;

import com.healthcit.cacure.beans.AnswerSearchCriteriaBean;
import com.healthcit.cacure.beans.AnswerSearchResultsBean;
import com.healthcit.cacure.dao.CouchDBDao;
import com.healthcit.cacure.xforms.model.Answer;
import com.healthcit.cacure.xforms.model.Column;
import com.healthcit.cacure.xforms.model.ComplexAnswer;
import com.healthcit.cacure.xforms.model.ComplexTable;
import com.healthcit.cacure.xforms.model.Form;
import com.healthcit.cacure.xforms.model.Question;
import com.healthcit.cacure.xforms.model.QuestionElement;
import com.healthcit.cacure.xforms.model.QuestionTable;
import com.healthcit.cacure.xforms.model.Row;
import com.healthcit.how.api.FormActionsProvider;
import com.healthcit.how.dao.ModuleDao;
import com.healthcit.how.dao.SharingGroupFormInstanceDao;
import com.healthcit.how.models.EntityTagPermission.TagAccessPermissions;
import com.healthcit.how.models.QuestionnaireForm;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.utils.IOUtils;
import com.healthcit.how.utils.JSONUtils;
import com.healthcit.how.web.controller.FormAccessServiceController.DataFormat;

public class FormAccessService {

	@Autowired CouchDBDao couchDb;
	
	@Autowired
	private ModuleDao moduleDao;
				
	@Autowired
	private SharingGroupFormInstanceDao sharingGroupFormInstanceDao;
	
	@SuppressWarnings("unused")
	@Autowired private ModuleManager moduleManager;
	
	@Autowired private ModuleDeploymentManager moduleDeploymentManager;
	
	@Autowired
//  the specific implementations of the interface is defined in cacure.properties	
	private FormDataCollector formDataCollector;
		
	@Autowired
	private JAXBContext xformsJaxbContext;
	
	@NumberFormat
	private int ownersBatchSize;

	
	public static final String END_ANS_TAG = "</answer>";
	public static final String END_QUES_TAG = "</question>";
	public static final String CROSSFORMSKIP_INSTANCE = "CrossFormSkipsInstance";
	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	public static final String NAME_SPACE = "xmlns=\"http://www.healthcit.com/FormDataModel\"";
	public static final String STATUS_OK = "ok";
	public static final String VALUES_SEPARATOR = "__VALUE_SEPARATOR__";

	private static final String QUESTION_ANSWER_SET_INSTANCE_ID = "Q-ANSWER_SET-";
	private static Pattern answerStartTagPattern = Pattern.compile("\\<answer\\s\\w.*?\\>\\w?");
	private static Pattern formStartTagPattern = Pattern.compile("\\<form\\s\\w.*?\\>\\w?");
	private static Pattern questionOrColumnPattern = Pattern.compile("<(column|question)\\s+([^>]*?)(questionId|id)=\"([^\"]*?)\"([^>]*?)>(<([^>]*?)/?>([^<]*?</\\7>)?)?((<answer[^>]*?>)([^<]*?)</answer>)([^<]*?)</\\1>", Pattern.DOTALL);
		
	private static final Logger log = Logger.getLogger(FormAccessService.class);

	@Autowired
	private FormActionsProvider formActionsProvider;
	
	public int  getOwnersBatchSize()
	{
		return ownersBatchSize;
	}
	
	public void setOwnersBatchSize(int ownersBatchSize)
	{
		this.ownersBatchSize = ownersBatchSize;
	}
	
	public JSONObject processXForm(String passedXForm, String ownerId, String moduleId, Long instanceId, Long parentInstanceId, File xformFile)
	throws Exception
	{

		boolean status = false;
		JSONObject xForm = null;

		try {
			log.debug("Raw form data:\n" + passedXForm);
			xForm = null;
			if ( JSONUtils.isValidJson( passedXForm ) ) {
				xForm = JSONObject.fromObject( passedXForm );
			}
			else{
				String processedXForm = preProcessXForm(passedXForm,xformFile);
				xForm = unMarshallXForm(processedXForm, ownerId, moduleId, instanceId, parentInstanceId);
			}
			log.debug("JSON form data:\n" + xForm.toString());
			String response = persistXForm(xForm);
			status = response.contains(STATUS_OK);
			if (! status) {
				log.error("Problem saving form to CouchDB: response=" + response);
			}
		} catch(Exception ex){
			log.error("Error while processing XForm data.",ex);
			xForm = null;
			throw ex;
		}
		if ( ! status ) xForm = null;
		return xForm;
	}
	
	public boolean postProcessXForm(JSONObject xForm, String entityId, String ownerId, String formId, String moduleId, Long instanceId, Long parentInstanceId)
	throws Exception
	{
		// Save Form Instance to the relational DB
		int result = sharingGroupFormInstanceDao.updateFormInstanceData(ownerId, entityId, formId, instanceId, parentInstanceId, FormStatus.IN_PROGRESS.toString());
		if ( result == 0 ) throw new Exception("Could not update the form instance in the relational database for form ID " + formId + ", instance ID " + instanceId + ", owner ID " + ownerId );
		
		// Update the associated module
		result = moduleDao.updateEntityModuleStatus(FormStatus.IN_PROGRESS.toString(), moduleId, ownerId, entityId);
		if ( result == 0 ) throw new Exception("Could not update the SharingGroupModule in the relational database for form ID " + formId + ", instance ID " + instanceId + ", owner ID " + ownerId );
		
		// Delete all form instances that have been rendered invisible because of form skips
		moduleDeploymentManager.deleteNonVisibleFormInstances( xForm, formId, ownerId, instanceId );		
		
		return true;
	}

	private String preProcessXForm(String xForm, File xformFile) throws Exception {

		final String XML_INPUT = xForm.replaceAll("\\$", "\\\\\\$");

		String answerXml = "";

        //create new string with replaced data.
        StringBuffer newXml = new StringBuffer();

		Matcher questionOrColumnMatcher = questionOrColumnPattern.matcher(XML_INPUT);
		
		// get the collection of answervalue-to-text mappings for this XForms document
        JSONObject mappings = getAnswerTextMappings( xformFile );

		while(questionOrColumnMatcher.find()) {
			String questionId = questionOrColumnMatcher.group(4);

			String answerGroup = StringUtils.defaultIfEmpty( questionOrColumnMatcher.group(9), "" );

			String answerGroupValues = StringUtils.defaultIfEmpty( questionOrColumnMatcher.group(11), "");

			Matcher answerStartTagMatcher = answerStartTagPattern.matcher(answerGroup);

	        answerStartTagMatcher.find();

	        String startTag = answerStartTagMatcher.group().replaceFirst("\\>\\w", "\\>");

	        String text = null;

	        log.debug( "answergroup content (values)=====" + answerGroupValues);
	        log.debug( "Original Start tag ============ " + startTag );
	        log.debug( "Parent group =========== " + answerGroup );
	        log.debug( "Question id ========== " + questionId );
	        
	        // Split the answer tag's content, if appropriate
	        // (this applies if the answers were generated from multi-answer controls,
	        //  with checkboxes for example)
	        String[] answerGroupValuesArray = answerGroupValues.split(VALUES_SEPARATOR);

	        answerXml = "";

	        for ( String answerValue : answerGroupValuesArray )
	        {
	        	String key = questionId;

	        	String newStartTag = startTag;

	    		if ( mappings.containsKey( key ))
	    		{
	        		JSONObject questionJson = mappings.getJSONObject( key );

	        		if ( questionJson.containsKey( answerValue ) )
	        		{
	        			text = questionJson.getString( answerValue );

	        			if ( text != null )
	        			{
	        				newStartTag = startTag.replaceAll("text=\"([^\"]*?)\"", "");
	        				newStartTag = newStartTag.replaceFirst( "(\\s)*(/?>$)", "$1 text=\"" + text + "\"$2" );
	        			}
	        		}
	    		}

	    		// build the answer xml and clean < and > brackets from the data
	    		answerXml = answerXml + 
	    					newStartTag + 
	    					answerValue.replaceAll("\\<*\\>*", "") + 
	    					END_ANS_TAG;

	    		log.debug( " answerxml=======" + answerXml );
	        }

	        answerXml = "<$1 $2 $3=\"$4\"$5>$6" + answerXml + "$12</$1>";

    		questionOrColumnMatcher.appendReplacement(newXml, answerXml);
		}

		// Append namespace to the "form" tag if it does not already exist
        questionOrColumnMatcher.appendTail(newXml);

        String processedXml = XML_HEADER + newXml.toString().replace("xmlns=\"\"", NAME_SPACE);

        int nameSpaceIndex = processedXml.indexOf("xmlns=");

        if (nameSpaceIndex <0)
        {
        	processedXml = processedXml.replaceFirst("<form", "<form " + NAME_SPACE);
        }

        log.debug( "Processed xml:" + processedXml);

        return processedXml;
	}

	private JSONObject unMarshallXForm(String processedXForm, String ownerId, String moduleId, Long instanceId, Long parentInstanceId) throws Exception {
		JSONObject jsonForm = null;
		JAXBContext jc = xformsJaxbContext;
		//Create unmarshaller
		Unmarshaller um = jc.createUnmarshaller();

		final String XML_INPUT = processedXForm;

		ByteArrayInputStream input = new ByteArrayInputStream (XML_INPUT.getBytes("UTF-8"));
		
		@SuppressWarnings("rawtypes")
		JAXBElement element = (JAXBElement) um.unmarshal (input);
		Form form = (Form) element.getValue ();
		String context = moduleDao.getModuleContextForForm(form.getId());
		jsonForm = new JSONObject();
		jsonForm.put("formId", form.getId());
		jsonForm.put("ownerId", ownerId);
		jsonForm.put("formName", form.getName());
		jsonForm.put("formText", form.getText());
		jsonForm.put("moduleId", moduleId);
		jsonForm.put("context", context);
		jsonForm.put("instanceId", instanceId);
		jsonForm.put("parentInstanceId", parentInstanceId);
		
		//Todays date	
		 DateTime dt = new DateTime();
		 DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		 String todaysDate = fmt.print(dt);

		jsonForm.put("updatedDate", todaysDate);
		
		String revision = form.getRevision();
		if (revision != null && revision.length() >0)
		{
			jsonForm.put("_rev", revision);
		}

		JSONObject questions = new JSONObject();
		JSONObject complexTables = new JSONObject();
		JSONObject simpleTables = new JSONObject();

		for (QuestionElement qe: form.getQuestionOrQuestionTableOrComplexTable())
		{
			JSONObject jsonQuestion = new JSONObject();
			String questionId = "";

			if (qe instanceof com.healthcit.cacure.xforms.model.Question)
			{
				Question qs = (Question)qe;
				questionId = qs.getId();

				jsonQuestion.put("questionId", qs.getId());
				jsonQuestion.put("questionSn", qs.getSn());
				jsonQuestion.put("questionText", qs.getText());
				JSONArray jsonAnswerValuesArray = new JSONArray();

				for(Answer ans: qs.getAnswer()){

					JSONObject jsonAnswer = new JSONObject();
					jsonAnswer.put("ansId", ans.getId());
					jsonAnswer.put("ansSn", ans.getSn());
					jsonAnswer.put("ansText", ans.getText());
					jsonAnswer.put("ansValue", ans.getValue());
					jsonAnswerValuesArray.add(jsonAnswer);

				}
				jsonQuestion.put("answerValues", jsonAnswerValuesArray);
				//jsonForm.put(questionId, jsonQuestion);
				questions.put(questionId, jsonQuestion);
			}
			else if (qe instanceof QuestionTable)
			{
				
				QuestionTable qt = (QuestionTable) qe;
				String tableId = qt.getId();
				JSONObject tableQuestions = new JSONObject();
				JSONObject jsonTable = new JSONObject();
				jsonTable.put("table_text", qt.getText());
				jsonTable.put("short_name", qt.getSn());
				for(Question qs: qt.getQuestion()){

					questionId = qs.getId();

					jsonQuestion.put("questionId", qs.getId());
					jsonQuestion.put("questionSn", qs.getSn());
					jsonQuestion.put("questionText", qs.getText());
					JSONArray jsonAnswerValuesArray = new JSONArray();

					for(Answer ans: qs.getAnswer()){

						JSONObject jsonAnswer = new JSONObject();
						jsonAnswer.put("ansId", ans.getId());
						jsonAnswer.put("ansSn", ans.getSn());
						jsonAnswer.put("ansText", ans.getText());
						jsonAnswer.put("ansValue", ans.getValue());
						jsonAnswerValuesArray.add(jsonAnswer);
					}
					jsonQuestion.put("answerValues", jsonAnswerValuesArray);
					//jsonForm.put(questionId, jsonQuestion);
					tableQuestions.put(questionId, jsonQuestion);
				}
				jsonTable.put("questions", tableQuestions);
				simpleTables.put(tableId, jsonTable);
			}
			else
			{
				JSONObject complexTable = new JSONObject();
				JSONObject metadataObj = new JSONObject();
				ComplexTable qt = (ComplexTable) qe;
				String tableUuid = qt.getId();
				complexTable.put("uuid", tableUuid);
				metadataObj.put("table_text", qt.getText());
				metadataObj.put("short_name", qt.getSn());
				
				JSONArray rowsArray = new JSONArray();
				JSONArray columnsOrder = new JSONArray();
				
				int rowCounter = 0;
				for(Row row: qt.getRow())
				{
					JSONObject rowObject = new JSONObject();
					if(row.getId() != null) {
						rowObject.put("rowId", row.getId());
					}
					List<Column>columns = row.getColumn();
					for(Column column: columns)
					{
						String questionUuid = column.getQuestionId();
						JSONObject cellObject = new JSONObject();
						cellObject.put("questionId", questionUuid);
						cellObject.put("questionSn", column.getQuestionSn());
						cellObject.put("questionText", column.getQuestionText());
						boolean isIdentifying = (column.isIsIdentifying()!= null)?true:false;
						if(isIdentifying)
						{
							metadataObj.put("ident_column_uuid", questionUuid);
							cellObject.put("identifying", isIdentifying);
						}
						
						if(rowCounter==0)
						{
							columnsOrder.add(questionUuid);
						}
						
						
						JSONArray jsonAnswerValuesArray = new JSONArray();
						for(ComplexAnswer answer : column.getAnswer())
						{
							JSONObject jsonAnswer = new JSONObject();
							jsonAnswer.put("ansId", answer.getAnswerId());
							jsonAnswer.put("ansSn", answer.getSn());
							jsonAnswer.put("ansText", answer.getText());
							jsonAnswer.put("ansValue", answer.getValue());
							jsonAnswerValuesArray.add(jsonAnswer);
						}
						cellObject.put("answerValues", jsonAnswerValuesArray);
						
						//jsonForm.put(questionId, jsonQuestion);
	//					questions.put(questionId, jsonQuestion);
						rowObject.put(questionUuid, cellObject);
					}
					rowsArray.add(rowObject);
					rowCounter++;
				}
				metadataObj.put("columns_order", columnsOrder);
				complexTable.put("metadata", metadataObj);
				complexTable.put("rows", rowsArray);
				complexTables.put(tableUuid, complexTable);
			}

		}
		//log.debug("JASON Form: " + jsonForm.toString());
		//log.debug("All is good");
		jsonForm.put("questions", questions);
		jsonForm.put("simple_tables", simpleTables);
		jsonForm.put("complex_tables", complexTables);
		return jsonForm;
	}

	@SuppressWarnings("unused")
	private Namespace getNamespaceById(String id, Document doc, String prefix)
	throws JDOMException
	{
		XPath xpathForm = XPath.newInstance("//*[@id='"+ id +"']");
		Element form = (Element)xpathForm.selectSingleNode(doc);
		Namespace namespace = Namespace.getNamespace(prefix, form.getNamespace().getURI());
		return namespace;
	}

	@SuppressWarnings("unchecked")
	private void setXFormQuestions( Document xform, String formId, String ownerId, JSONObject jsonForm )
	throws Exception
	{

		// get the namespace to use
//		Namespace namespace = getNamespaceById(formId, xform, "x");

		// Get the JSONObject that contains the user's form data
//		JSONObject jsonForm = getFormDataJSON( formId, ownerId );

		if ( jsonForm != null )
		{
			XPath questionsXPath = XPath.newInstance("//form[@id='"+ formId +"']/question");
			XPath simpleTablesXPath = XPath.newInstance("//form[@id='"+ formId +"']/question-table");
			XPath complexTablesXPath = XPath.newInstance("//form[@id='"+ formId +"']/complex-table");
			
			
//			XPath questionsXPath = XPath.newInstance("//x:form[@id='"+ formId +"']//x:question");
//			questionsXPath.addNamespace(namespace);

			List<Element> questions = (List<Element>)questionsXPath.selectNodes(xform);
			List<Element> simpleTables = (List<Element>)simpleTablesXPath.selectNodes(xform);
			List<Element> complexTables = (List<Element>)complexTablesXPath.selectNodes(xform);
			if (questions != null)
			{
				for (Element question: questions)
				{
					Attribute questionId = question.getAttribute("id");
					try
					{
//						Element answer = question.getChild("answer", namespace);
						Element answer = question.getChild("answer");
						JSONObject questionsJSON = jsonForm.getJSONObject("questions");
					    JSONObject questionJSON = questionsJSON.getJSONObject(questionId.getValue());
					    JSONArray answers = questionJSON.getJSONArray("answerValues");

					    Iterator<JSONObject> iterator = answers.iterator();
					    StringBuilder values = new StringBuilder(500);

					    while(iterator.hasNext())
					    {
					    	JSONObject answerValue = iterator.next();
					    	//String answerId = answerValue.getString("ansId");
					    	String value = answerValue.getString("ansValue");
					    	values.append(value);
					    	if (iterator.hasNext())
					    	{
					    		values.append(VALUES_SEPARATOR);
					    	}
					    }
					    answer.setText(values.toString());
					}
					catch (JSONException e)
					{
						log.debug("There is no data for question with id = " + questionId);
					}
				}
			}
			if(simpleTables!= null)
			{
				JSONObject tablesJSON = jsonForm.getJSONObject("simple_tables");
				for (Element table: simpleTables)
				{
					String tableId = table.getAttribute("id").getValue();
					try
					{
						List<Element>tableQuestions = table.getChildren("question");
						JSONObject tableJSON= tablesJSON.getJSONObject(tableId);
						 JSONObject questionsJSON = tableJSON.getJSONObject("questions");
						for(Element question: tableQuestions)
						{
							String questionId = question.getAttribute("id").getValue();
							//						Element answer = question.getChild("answer", namespace);
							Element answer = question.getChild("answer");

							try
							{
	
							    JSONObject questionJSON = questionsJSON.getJSONObject(questionId);
							    JSONArray answers = questionJSON.getJSONArray("answerValues");
		
							    Iterator<JSONObject> iterator = answers.iterator();
							    StringBuilder values = new StringBuilder(500);
		
							    while(iterator.hasNext())
							    {
							    	JSONObject answerValue = iterator.next();
							    	//String answerId = answerValue.getString("ansId");
							    	String value = answerValue.getString("ansValue");
							    	values.append(value);
							    	if (iterator.hasNext())
							    	{
							    		values.append(VALUES_SEPARATOR);
							    	}
							    }
							    answer.setText(values.toString());
							}
							catch(Exception e)
							{
								log.debug("There is no data for question with id = " + questionId);
							}
						}
					}
					catch (JSONException e)
					{
						log.debug("There is no data for table with id = " + tableId);
					}
				}
			}
			if(complexTables!= null)
			{
				for (Element complexTable: complexTables)
				{
					
					Attribute tableId = complexTable.getAttribute("id");
					try
					{
//						Element row = complexTable.getChild("row");
						//XPath columnXPath = XPath.newInstance("//form[@id='"+ formId +"']/complex-table[@id='"+ tableId+"']/rows/column");
//						XPath columnXPath = XPath.newInstance("column");
//						
//						List<Element> columns = (List<Element>)columnXPath.selectNodes(row);
//						List<String> questionIds = new ArrayList<String>(); 
//						for(Element column: columns)
//						{
//							Attribute questionIdAtt = column.getAttribute("questionId");
//							if (questionIdAtt!= null)
//							{
//								String questionId = questionIdAtt.getValue();
//								questionIds.add(questionId);
//							}
//						}
						
//						Element answer = complexTable.getChild("answer");
						JSONObject complexTablesJSON = jsonForm.getJSONObject("complex_tables");
					    JSONObject complexTableJSON = complexTablesJSON.getJSONObject(tableId.getValue());
					    JSONArray rows = complexTableJSON.getJSONArray("rows");
					    
					    Iterator<JSONObject> rowsIterator = rows.iterator();
					    int i=1;
					    XPath firstRowXPath = XPath.newInstance("row[1]");
					    Element firstRow = (Element)firstRowXPath.selectSingleNode(complexTable);
					    while(rowsIterator.hasNext())
					    {
					    	JSONObject rowJSON = rowsIterator.next();
					    	XPath rowXPath = XPath.newInstance("row["+ i+"]");
					    	Element rowElement = (Element)rowXPath.selectSingleNode(complexTable);
					    	if(rowElement == null)
					    	{
					    		rowElement = (Element)firstRow.clone();
					    		complexTable.addContent(rowElement);
					    		
					    	}
					    	List<Element>columnElements = rowElement.getChildren("column");
					    	for( Element columnElement: columnElements)
					    	{
					    		Element answer = columnElement.getChild("answer");
					    		String questionId = columnElement.getAttributeValue("questionId");
					    		try{
					    			JSONObject questionObject = rowJSON.getJSONObject(questionId);
					    			JSONArray answers = questionObject.getJSONArray("answerValues");

								    Iterator<JSONObject> iterator = answers.iterator();
								    StringBuilder values = new StringBuilder(500);

								    while(iterator.hasNext())
								    {
								    	JSONObject answerValue = iterator.next();
								    	//String answerId = answerValue.getString("ansId");
								    	String value = answerValue.getString("ansValue");
								    	values.append(value);
								    	if (iterator.hasNext())
								    	{
								    		values.append(VALUES_SEPARATOR);
								    	}
								    }
								    answer.setText(values.toString());
					    		}
					    		catch (Exception e)
					    		{
					    			log.debug("There is no data for question inside complex-table with id = " + questionId);
					    			answer.setText("");
					    		}
					    	}
					    	i++;
					    }
					    
					}
					catch (JSONException e)
					{
						log.debug("There is no data for complex-table with id = " + tableId);
					}
				}
			}
		}
		else
		{
			log.debug("There is no data for form with formId: " + formId );
		}
	}

	

	@SuppressWarnings("unchecked")
	private void setXFormCrossFormSkips(Document xform, String formId, String ownerId )
	throws Exception
	{

		// get the namespace to use
//		Namespace namespace = getNamespaceById(formId, xform, "x");

		//XPath crossSkipXPath = XPath.newInstance("//x:cross-form-skip");
		XPath crossSkipXPath = XPath.newInstance("//cross-form-skip");
//		crossSkipXPath.addNamespace( namespace );

		List<Element> crossFormQuestions = crossSkipXPath.selectNodes( xform );
		if ( crossFormQuestions != null )
		{
			// If no cross-form skips exist, then return
			if ( crossFormQuestions.isEmpty() )
			{
				log.debug("There are no cross form skips for form ID " + formId);
				return;
			}

			// Else, get the full list of question IDs that trigger cross-form skips
			ArrayList<AnswerSearchCriteriaBean> criterias = new ArrayList<AnswerSearchCriteriaBean>();
			for ( Iterator<Element> iter = crossFormQuestions.iterator(); iter.hasNext(); )
			{
				Element skipElement = iter.next();
				AnswerSearchCriteriaBean criteria = new AnswerSearchCriteriaBean();
				criteria.setFormId(skipElement.getAttributeValue("formId"));
				criteria.setQuestionId(skipElement.getAttributeValue("id"));
				criteria.setRowId(skipElement.getAttributeValue("rowId"));
				criterias.add(criteria);
			}

			// Perform a CouchDB query for the user's answers to the questions
			List<AnswerSearchResultsBean> answers = couchDb.getAnswersByOwnerAndQuestion(ownerId, criterias);

			// Use this data to populate the CrossFormSkips model instance
			if ( answers != null && !answers.isEmpty())
			{
				for ( Iterator<Element> aIterator = crossFormQuestions.iterator(); aIterator.hasNext(); )
				{
					Element skipElement = aIterator.next();
					AnswerSearchCriteriaBean criteria = new AnswerSearchCriteriaBean();
					criteria.setFormId(skipElement.getAttributeValue("formId"));
					criteria.setQuestionId(skipElement.getAttributeValue("id"));
					criteria.setRowId(skipElement.getAttributeValue("rowId"));
					for (AnswerSearchResultsBean answer : answers) {
						if(answer.getCriteria().equals(criteria)) {
							Collection<String> answerValues = answer.getAnswers();
							for ( Iterator<String> bIterator = answerValues.iterator(); bIterator.hasNext(); ) {
								String answerValue = bIterator.next();
								Element answerValueElement = new Element("answer");
								answerValueElement.setAttribute("value",answerValue);
								skipElement.addContent( answerValueElement );
							}
						}
					}
					
				}
			}
		}
	}

	/**
	 * This method takes a File object which represents an XForms document
	 * and updates the UI section as appropriate.
	 * @author Oawofolu
	 * @param formFile
	 */
	public Reader processFileOnLoad( File xformFile, QuestionnaireForm.FormPosition formPosition, FormStatus formInstanceStatus, EnumSet<TagAccessPermissions> tagAccessPermissions) throws IOException
	{

		String formContent = IOUtils.prettyPrintXML(IOUtils.readFileContent( xformFile ));

		// Add Submit buttons to the form
		String submitSectionContent = getXFormActionsSection(formPosition, formInstanceStatus, tagAccessPermissions);

		formContent = formContent.replaceAll( "</body>", submitSectionContent + "\n</body>") ;

		return new StringReader(formContent);

	}

	public void setXFormDefaultValues(Reader xformData, String formId, String ownerId, Long instanceId, Writer writer) throws Exception
	{
		// Generate a JDOM document from the file
		SAXBuilder builder = new SAXBuilder();

		Document xform = builder.build(xformData);

		try
		{
			// initialize data for questions
			
			JSONObject jsonForm = formDataCollector.getFormDataJSON( xform, formId, ownerId, instanceId );
			setXFormQuestions( xform, formId, ownerId, jsonForm );

			// initialize data for cross-form skips
			setXFormCrossFormSkips( xform, formId, ownerId );

			// set up all appropriate attributes in the model for answers

		}
		catch (Exception ex )
		{
			ex.printStackTrace();
		}

		try
		{
			// output the results
	        Source source = new JDOMSource(xform);
	        Result result = new StreamResult(writer);
	        TransformerFactory factory = TransformerFactory.newInstance();
	        Transformer transformer = factory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	        transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
	        transformer.transform(source, result);

	        // delete the temporary file
//	        if ( formFile != null ) formFile.delete();

		}
		catch (TransformerException e)
		{
			//throw new XFormsConstructionException(e);
		}
	}

	public String getFormData(String formId, String ownerId, Long instanceId, String format) throws Exception
	{
		String output= null;
		if (DataFormat.JSON.name().equals(format))
		{
			JSONObject jsonObject = formDataCollector.getFormDataJSON(null, formId, ownerId, instanceId);
			if(jsonObject != null)
			{
				output = jsonObject.toString();
			}
		}
		else if(DataFormat.XML.name().equals(format))
		{
			output = getFormDataXML(formId, ownerId, instanceId);
		}
		else
		{
			throw new UnsupportedOperationException("Unsupported format: " + format);
		}
		return output;
	}
	
	
	public void getFormData(String formId, String format, PrintWriter out) throws Exception
	{

		List<String> sharingGroups = sharingGroupFormInstanceDao.getSharingGroupForForm(formId);
		
		if (DataFormat.JSON.name().equals(format))
		{
			getFormDataJSON(formId, sharingGroups, out);
			
		}
		else if(DataFormat.XML.name().equals(format))
		{
			getFormDataXML(formId, sharingGroups, out);
		}
		else
		{
			throw new UnsupportedOperationException("Unsupported format: " + format);
		}
		
	}
		
	private String persistXForm(JSONObject jsonForm) throws Exception {
		String formId = jsonForm.getString("formId");
		String ownerId = jsonForm.getString("ownerId");
		Long instanceId = jsonForm.getLong("instanceId");
		JSONArray key = getObjectKey(formId, ownerId, instanceId);
		String response = couchDb.saveForm(jsonForm, key);
		log.debug("Attempted saving form to couch. Response: " + response);
		
		return response;
	}	
	
	public void getFormDataJSON(String formId, List<String> sharingGroups, PrintWriter out) throws URISyntaxException, IOException
	{
		int n = sharingGroups.size()/ownersBatchSize;
		boolean isFirst = true;
        int i=0;
        out.print("[\n");
		for(; i<n*ownersBatchSize; i+=ownersBatchSize)
		{
			JSONArray docs = couchDb.getFormJSONByFormId(formId, sharingGroups.subList(i, i+ownersBatchSize));
			if(docs!= null)
			{
				
				for(int j = 0; j<docs.size(); j++)
				{
					JSONObject instance = docs.getJSONObject(j);
					if(!isFirst)
					{
						out.print(",\n");
					}
					else
					{
						isFirst = false;
						
					}
					out.print(instance);
				}	
				
//				for(Object key : chunk.keySet())
//				{
//					if(!isFirst)
//					{
//						out.print(",\n");
//					}
//					else
//					{
//						isFirst = false;
//					}
//					out.print("\""+key +"\":");
//					out.print(chunk.get(key));
//					
//				}
			}
		}
		if(i<sharingGroups.size())
		{
			JSONArray docs = couchDb.getFormJSONByFormId(formId, sharingGroups.subList(i, sharingGroups.size()));
			if(docs != null)
			{
				for(int j=0; j<docs.size(); j++)
				{
					JSONObject instance = docs.getJSONObject(j);
					if(!isFirst)
						{
							out.print(",\n");
						}
						else
						{
							isFirst = false;
						}
					out.print(instance);
				}
//				for(Object key : chunk.keySet())
//				{
//					if(!isFirst)
//					{
//						out.print(",\n");
//					}
//					else
//					{
//						isFirst = false;
//					}
//					out.print("\""+key +"\":");
//					out.print(chunk.get(key));
//					
//				}
			}
		}
		out.print("\n]");
	}
	
	public String getFormDataXML(String formId, String ownerId, Long instanceId)throws IOException, URISyntaxException
	{
		JSONArray key = getObjectKey(formId, ownerId, instanceId);
		String xml = couchDb.getFormXMLByOwnerIdAndFormInstance(key);
	    return xml;
	}

	private void getFormDataXML(String formId, List<String> sharingGroups, PrintWriter out)throws IOException, URISyntaxException
	{
		int n = sharingGroups.size()/ownersBatchSize;
		boolean isFirstStartFormTag = true;
		String formEndTagString = "</form>";
		
		
        int i=0;
		
		for(; i<n*ownersBatchSize; i+=ownersBatchSize)
		{
			String chunk = couchDb.getFormXMLByFormId(formId, sharingGroups.subList(i, i+ownersBatchSize));
			chunk = chunk.replaceFirst("</form>", "");
			Matcher formStartTagMatcher = formStartTagPattern.matcher(chunk);
			
			if (isFirstStartFormTag == true && formStartTagMatcher.find())
			{
				isFirstStartFormTag = false;
			}
			else
			{
				chunk = formStartTagMatcher.replaceFirst("");
			}
			out.print(chunk);
		}
		if(i<sharingGroups.size())
		{
			String chunk = couchDb.getFormXMLByFormId(formId, sharingGroups.subList(i, sharingGroups.size()));
			chunk = chunk.replaceFirst("</form>", "");
			Matcher formStartTagMatcher = formStartTagPattern.matcher(chunk);
			
			if (isFirstStartFormTag == true && formStartTagMatcher.find())
			{
				isFirstStartFormTag = false;
			}
			else
			{
				chunk = formStartTagMatcher.replaceFirst("");
			}
			out.print(chunk);
		}
		out.print(formEndTagString);
	}
	
	private JSONArray getObjectKey(String formId, String ownerId, Long instanceId)
	{
		JSONArray key = new JSONArray();
		key.add(ownerId);
		key.add(formId);
		if ( instanceId != null ) key.add(instanceId);
		log.debug("The key is: " + key);
		return key;
	}

	private String getXFormActionsSection(QuestionnaireForm.FormPosition formPosition, FormStatus formStatus, EnumSet<TagAccessPermissions> tagAccessPermissions)
	{
		String xformsSubmissionSection= formActionsProvider.getXFormActionElementsSection(formPosition, formStatus, tagAccessPermissions);
		return xformsSubmissionSection;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getAnswerTextMappings( File xformFile )
	{
		JSONObject mappings = new JSONObject();

		String textAttribute = "text";

		try
		{
			Document xform = new SAXBuilder().build( xformFile );

			XPath answerSetParentElementsXPath = XPath.newInstance( "//*[contains(@id,'" + QUESTION_ANSWER_SET_INSTANCE_ID + "')]" );

			List<Element> parentElements = answerSetParentElementsXPath.selectNodes( xform );

			for ( Element parentElement : parentElements )
			{
				// Map the parent element's questionId to a map of text-value pairs 
				String id = parentElement.getAttributeValue( "id" );
				
				String questionId = id.replaceFirst(QUESTION_ANSWER_SET_INSTANCE_ID,"");

				XPath answerSetChildElementsXPath = XPath.newInstance("//*[@id='" + id +"']//answer" );

				List<Element> childElements = answerSetChildElementsXPath.selectNodes( parentElement );

				JSONObject childElementsObj = new JSONObject();

				for ( Element childElement : childElements )
				{
					String text = StringEscapeUtils.escapeXml(childElement.getAttributeValue( textAttribute ));

					String value = StringEscapeUtils.escapeXml(childElement.getValue());

					childElementsObj.put( value, text );

				}

				mappings.put( questionId, childElementsObj );
				
				// If the parent element is a "simple" table question,
				// then map the parent element's child questions to the map of text-value pairs as well
				XPath tableQuestionSetElementsXPath = XPath.newInstance("//question-table[@id='" + questionId +"']//question");
								
				List<Element> tableQuestionChildElements = tableQuestionSetElementsXPath.selectNodes( xform );
				
				for ( Element tableQuestionChildElement : tableQuestionChildElements )
				{					
					String tableQuestionChildId = tableQuestionChildElement.getAttributeValue("id");
					
					JSONObject tableQuestionObj = new JSONObject();
					
					tableQuestionObj.putAll( childElementsObj );
					
					mappings.put( tableQuestionChildId, tableQuestionObj );
				}
				
			}
		}
		catch( Exception ex)
		{
			log.error( "Could not access QuestionAnswerSet instance(s) from the XForms document file, Reason:", ex );
		}

		return mappings;
	}
}

