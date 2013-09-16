/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.implementations.ncccp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cacure.dao.CouchDBDao;
import com.healthcit.how.businessdelegates.FormDataCollector;
import com.healthcit.how.businessdelegates.FormManager;
import com.healthcit.how.models.QuestionnaireForm.FormStatus;
import com.healthcit.how.models.SharingGroupFormInstance;
import com.healthcit.how.utils.Constants;


public class FormDataCollectorNCCCP implements FormDataCollector
{
	
	@Autowired
	CouchDBDao couchDb;
	
	@Autowired
	FormManager formManager;
	
	@Autowired
	private FormDataCollector formDataCollectorDefault;
	
	@Override
	public JSONObject getFormDataJSON(Document xform, String formId, String ownerId, Long instanceId) throws Exception
	{
		JSONObject formJSON = null;
		SharingGroupFormInstance formInstance  = formManager.getFormInstanceByFormAndOwnerAndInstance(formId, ownerId, instanceId);
		FormStatus formStatus = formInstance.getStatus();
		if(FormStatus.NEW.equals(formStatus))
		{
			formJSON = getFormLatestDataJSON(xform, formId, ownerId, instanceId);
		}
		else
		{
			formJSON = getFormDataWithReadOnlyJSON(xform, formId, ownerId, instanceId);
		//	formJSON = formDataCollectorDefault.getFormDataJSON(xform, formId, ownerId);
		}
		return formJSON;
	}
	
//	@Override
//	public JSONObject getFormDataJSON(Document xform, String formId) throws Exception
//	{
//		JSONObject formJSON = null;
//		SharingGroupForm form  = formManager.getEntityFormByIdAndOwner(formId);
//		FormStatus formStatus = form.getStatus();
//		if(FormStatus.NEW.equals(formStatus))
//		{
//			formJSON = getFormLatestDataJSON(xform, formId);
//		}
//		else
//		{
//			formJSON = getFormDataWithReadOnlyJSON(xform, formId);
//		//	formJSON = formDataCollectorDefault.getFormDataJSON(xform, formId, ownerId);
//		}
//		return formJSON;
//	}
	
	
	@SuppressWarnings("unchecked")
	private JSONObject getFormLatestDataJSON( Document xform, String formId, String ownerId, Long instanceId )
	throws Exception
	{
//		XPath questionsXPath = XPath.newInstance("//form[@id='"+ formId +"']/question");
//		XPath simpleTablesXPath = XPath.newInstance("//form[@id='"+ formId +"']/question-table");
//		XPath complexTablesXPath = XPath.newInstance("//form[@id='"+ formId +"']/complex-table");

		XPath questionsXPath = XPath.newInstance("//form/question");
		XPath simpleTablesXPath = XPath.newInstance("//form/question-table");
		XPath complexTablesXPath = XPath.newInstance("//form/complex-table");

		List<Element> questions = (List<Element>)questionsXPath.selectNodes(xform);
		List<Element> simpleTables = (List<Element>)simpleTablesXPath.selectNodes(xform);
		List<Element> complexTables = (List<Element>)complexTablesXPath.selectNodes(xform);
		Map<String,Long> ids = new HashMap<String, Long>();
		if (questions != null)
		{
			for (Element question: questions)
			{
				Attribute questionId = question.getAttribute("id");
				ids.put(questionId.getValue(),instanceId);
			}
		}
		if(simpleTables!= null)
		{
			for (Element table: simpleTables)
			{
				String tableId = table.getAttribute("id").getValue();
				ids.put(tableId,instanceId);
			}
		}
		if(complexTables!= null)
		{
			for (Element complexTable: complexTables)
			{
				
				Attribute tableId = complexTable.getAttribute("id");
				ids.put(tableId.getValue(),instanceId);
			}
		}
		JSONObject json = getLatestAnswersForOwner( ownerId, ids );
		JSONObject jsonForm = constractJsonForm(json);
		return jsonForm;
	}
	
	private JSONObject getFormDataWithReadOnlyJSON( Document xform, String formId, String ownerId, Long instanceId )
	throws Exception
	{
		XPath questionsXPath = XPath.newInstance("//form[@id='"+ Constants.READ_ONLY_INSTANCE_ID_PREFIX +formId +"']/question");
		XPath simpleTablesXPath = XPath.newInstance("//form[@id='"+ Constants.READ_ONLY_INSTANCE_ID_PREFIX +formId +"']/question-table");
		XPath complexTablesXPath = XPath.newInstance("//form[@id='"+ Constants.READ_ONLY_INSTANCE_ID_PREFIX +formId +"']/complex-table");

		List<Element> questions = (List<Element>)questionsXPath.selectNodes(xform);
		List<Element> simpleTables = (List<Element>)simpleTablesXPath.selectNodes(xform);
		List<Element> complexTables = (List<Element>)complexTablesXPath.selectNodes(xform);
		Map<String,Long> ids = new HashMap<String,Long>();
		if (questions != null)
		{
			for (Element question: questions)
			{
				Attribute questionId = question.getAttribute("id");
				ids.put(questionId.getValue(),instanceId);
			}
		}
		if(simpleTables!= null)
		{
			for (Element table: simpleTables)
			{
				String tableId = table.getAttribute("id").getValue();
				ids.put(tableId,instanceId);
			}
		}
		if(complexTables!= null)
		{
			for (Element complexTable: complexTables)
			{
				
				Attribute tableId = complexTable.getAttribute("id");
				ids.put(tableId.getValue(),instanceId);
			}
		}
		JSONObject storedValues = formDataCollectorDefault.getFormDataJSON(xform, formId, ownerId, instanceId);
		JSONObject jsonForm = null;
		if(ids.size() >0)
		{
			JSONObject json = getLatestAnswersForOwner( ownerId, ids );
			jsonForm = constractJsonForm(json);
			JSONObject questionsJSON = storedValues.getJSONObject("questions");
			JSONObject simpleTablesJSON = storedValues.getJSONObject("simple_tables");
			JSONObject complexTablesJSON = storedValues.getJSONObject("complex_tables");
			for (Object questionId : questionsJSON.keySet())
			{
				jsonForm.getJSONObject("questions").put((String)questionId, questionsJSON.getJSONObject((String)questionId));
			}
			for (Object questionId : simpleTablesJSON.keySet())
			{
				jsonForm.getJSONObject("simple_tables").put((String)questionId, questionsJSON.getJSONObject((String)questionId));
			}
			for (Object questionId : complexTablesJSON.keySet())
			{
				jsonForm.getJSONObject("questions").put((String)questionId, questionsJSON.getJSONObject((String)questionId));
			}
		}
		else
		{
			jsonForm = storedValues;
		}
		return jsonForm;
	}
	
	private JSONObject constractJsonForm(JSONObject json)
	{
		JSONObject jsonForm = null;
		JSONArray rows = json.getJSONArray("rows");
		if(rows.size()>0)
		{
			jsonForm = new JSONObject();
			jsonForm.put("questions", new JSONObject());
			jsonForm.put("simple_tables", new JSONObject());
			jsonForm.put("complex_tables", new JSONObject());

			//Group questions by couchDB keys
			Map<String, List<String>> questionsMap = new HashMap<String, List<String>>();
			for(int i=0; i< rows.size(); i++)
			{
				JSONObject row = rows.getJSONObject(i);
				String couchDbKey = row.getString("key");
				if (!questionsMap.containsKey(couchDbKey))
				{
					questionsMap.put(couchDbKey, new ArrayList<String>());	
				}
				questionsMap.get(couchDbKey).add(row.getJSONObject("value").toString());
				
			}
			//Find the latest question among the questions witht he same key
			for(String couchDbKey:questionsMap.keySet())
			{
				List<String> questions = questionsMap.get(couchDbKey);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for(String question: questions)
				{
					JSONObject jsonObject = (JSONObject)JSONSerializer.toJSON(question);
					String date = jsonObject.getString("updatedDate");
	//				DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
					DateTime updatedDate = new DateTime(date);
					String questionType = jsonObject.getString("questionType");
					JSONObject questionJSON = jsonObject.getJSONObject(questionType);
					Map<String, Object> hash = new HashMap<String, Object>();
					hash.put("updatedDate", updatedDate);
					hash.put("questionType", questionType);
					hash.put("question", questionJSON);
					list.add(hash);
					
				}
				Collections.sort(list, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map <String, Object>m1, Map <String, Object>m2)
					{
						DateTime dt1 =(DateTime) m1.get("updatedDate");
						DateTime dt2 = (DateTime)m2.get("updatedDate");
						return (int)(dt1.getMillis() - dt2.getMillis());
					}
				});
				Map<String, Object> latestQuestionMap = list.get(list.size()-1);
				JSONObject value = (JSONObject)latestQuestionMap.get("question");
				String uuid = null;
				try
				{
					uuid = value.getString("questionId");
				}
				catch(Exception e)
				{
				   //questionId not found	
				}
				//Check for null in case if library changes and will stop throwing exception when key is not found.
				if(uuid == null)
				{
					uuid = value.getString("uuid");
				}
				jsonForm.getJSONObject((String)latestQuestionMap.get("questionType")).put(uuid, value);
				
			}
		}
		return jsonForm;
	}
	public JSONObject getLatestAnswersForOwner(String ownerId, Map<String,Long> ids)throws IOException, URISyntaxException
	{
		
		JSONObject response = couchDb.getLatestAnswersByOwnerAndQuestionAndFormInstance(ownerId, ids);
		return response;
	}
}
