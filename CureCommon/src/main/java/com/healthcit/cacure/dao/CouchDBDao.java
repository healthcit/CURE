/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.cacure.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.URIException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.format.annotation.NumberFormat;

import com.healthcit.cacure.beans.AnswerSearchCriteriaBean;
import com.healthcit.cacure.beans.AnswerSearchResultsBean;
import com.healthcit.cacure.data.utils.CouchJSONConverter;

public class CouchDBDao {

	private static final Logger log = LoggerFactory.getLogger(CouchDBDao.class);
	private static final String DESIGN_DOC_PREFIX = "_design";
	private String host;

	@NumberFormat
	private  int port;

	private  String dbName;
	
	/**
	 * The name of the CURE Collector database; might be the same as "dbName".
	 * Required for other applications that make use of this library (ex. Analytics).
	 */
	private String cacureDbName;
	
	/**
	 * The name of the CURE Collector database design document; might be the same as "designDoc".
	 * Required for other applications that make use of this library (ex. Analytics).
	 */
	private String cacureDesignDoc;

	@NumberFormat
	private  int batchSize;

	@NumberFormat
	private  int bulkBatchSize;

	private String designDoc;
	
	private String context;

    private List<String> docIdSet = null;
    
    /**
     * Username used for HTTP authentication when connecting to the CouchDB database;
     * could be null, in which case we assume that no HTTP authentication is needed.
     */
    private String dbLoginUserName;
    
    /**
     * Username used for HTTP authentication when connecting to the CouchDB database;
     * could be null, in which case we assume that no HTTP authentication is needed.
     */
    private String dbLoginPassword;


    public void setDesignDoc(String name)
    {
    	this.designDoc = name;
    }

    public String getDesignDoc()
    {
    	return designDoc;
    }

    public String getCacureDbName() 
    {
		return cacureDbName;
	}
    
    public String getDbName(String context) 
    {
		return constructDbName( context );
	}
    
    public String buildDbSuffix()
	{
		return buildDbSuffix( context );
	}
	
	public String buildDbSuffix(String context)
	{
		return ( StringUtils.isEmpty( context ) ? "" : "_" + context );
	}
	
	public String constructDbName( String context )
	{
		return dbName + buildDbSuffix( context );
	}
    
	public void setCacureDbName(String cacureDbName) 
	{
		this.cacureDbName = cacureDbName;
	}

	public String getCacureDesignDoc() 
	{
		return cacureDesignDoc;
	}

	public void setCacureDesignDoc(String cacureDesignDoc) 
	{
		this.cacureDesignDoc = cacureDesignDoc;
	}

	public void setHost(String host)
    {
    	this.host = host;
    }

    public String getHost()
    {
    	return host;
    }

    public void setPort(int port)
    {
    	this.port = port;
    }

    public int getPort()
    {
    	return port;
    }

    public void setDbName(String name)
    {
    	this.dbName = name;
    }
    public String getDbName()
    {
    	return dbName;
    }

    public void setBatchSize(int batchSize)
    {
    	this.batchSize = batchSize;
    }

    public int getBatchSize()
    {
    	return batchSize;
    }

    public void setBulkBatchSize(int batchSize)
    {
    	this.bulkBatchSize = batchSize;
    }

    public int getBulkBatchSize()
    {
    	return batchSize;
    }
    
    public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
	
	public String getDbLoginUserName() {
		return dbLoginUserName;
	}

	public void setDbLoginUserName(String dbLoginUserName) {
		this.dbLoginUserName = dbLoginUserName;
	}

	public String getDbLoginPassword() {
		return dbLoginPassword;
	}

	public void setDbLoginPassword(String dbLoginPassword) {
		this.dbLoginPassword = dbLoginPassword;
	}

	@SuppressWarnings("unchecked")
	public void bulkWriteToDb(JSONArray docList, String host, int port, String dbName) 
    throws IOException, URISyntaxException
	{
    	// write to DB in batches
		Iterator docListIter = docList.iterator();
	    JSONArray batchList = new JSONArray();
	    for (int i=1; docListIter.hasNext(); i++)
	    {
	    	if ((i % bulkBatchSize) == 0)
	    	{
	    		batchWriteToDb(batchList,host,port,dbName);
	    		System.out.println("CouchDBWriter: Sent " + i + " documents");
	    		batchList = new JSONArray();
	    	}
	    	batchList.add(docListIter.next());
	    }
	    // send the remainder
	    batchWriteToDb(batchList,host,port,dbName);
	}

	@SuppressWarnings("unchecked")
	public void bulkWriteToDb(JSONArray docList) throws IOException, URISyntaxException
	{
		// write to DB in batches
		Iterator docListIter = docList.iterator();
	    JSONArray batchList = new JSONArray();
	    for (int i=1; docListIter.hasNext(); i++)
	    {
	    	if ((i % bulkBatchSize) == 0)
	    	{
	    		batchWriteToDb(batchList,host,port,dbName);
	    		System.out.println("CouchDBWriter: Sent " + i + " documents");
	    		batchList = new JSONArray();
	    	}
	    	batchList.add(docListIter.next());
	    }
	    // send the remainder
	    batchWriteToDb(batchList,host,port,dbName);

	}

	private void batchWriteToDb(JSONArray docList, String host, int port, String dbName) throws IOException, URISyntaxException
	{
		if (docList == null || docList.size() < 1)
			return; // nothing to send

		JSONObject jObj = new JSONObject();
		jObj.put("docs", docList);

		String jsonStr = jObj.toString();
		URI uri = URIUtils.createURI("http", host, port, "/" + dbName + "/_bulk_docs", null, null);

		// Prepare a request object
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setHeader("Content-Type", "application/json");
		StringEntity body = new StringEntity(jsonStr);
		httpPost.setEntity(body);

		String response = doHttp(httpPost);

	}

	public JSONObject getFormByOwnerAndFormInstance(JSONArray key)throws IOException, URISyntaxException
	{
		String keyString = key.toString();
		String encodedURL = URLEncoder.encode(keyString,"UTF-8");
		String viewURL = constructViewURL("GetDocByOwnerAndFormInstance");
		URI uri = URIUtils.createURI("http", host, port, viewURL,"key="+ encodedURL, null);
		HttpGet httpGet = new HttpGet(uri);
		String response = doHttp(httpGet);
		JSONObject json = JSONObject.fromObject(response);
		JSONArray objects = json.getJSONArray("rows");
		JSONObject form = null;
		if (objects!= null && objects.size() >0)
		{
		    JSONObject row = objects.getJSONObject(0);
		    form = row.getJSONObject("value");
		}
		return form;
	}
		
	/**
	 * This version of the "getFormsByModuleId" method is necessary
	 * to prevent Java heap size errors for large databases. 
	 * Rather than returning all the documents in the database,
	 * it allows documents to be returned in batches of size "batchSize", with "startingDocumentId" as the
	 * DocId of the first document in the list.
	 * @param moduleId
	 * @param batchSize
	 * @param startingDocId
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONArray getDocsByModule( String moduleId, Integer batchSize, boolean includeDocs, String startingDocId )throws IOException, URISyntaxException
	{
		String viewURL = includeDocs ? 
				         constructViewURL("GetDocsByModule") :
				         constructViewURL("GetDocIdsByModule");
				         
		String key = URLEncoder.encode("\"" + moduleId + "\"","UTF-8");
		
		String parameters = "key=" + key 
			                + ( batchSize != null ? "&limit=" + batchSize  : "" ) 
			                + ( startingDocId != null ? ("&startkey_docid=" + startingDocId) : "" ); 
			
		URI uri = URIUtils.createURI("http", host, port, viewURL, parameters, null);
		
		HttpGet httpGet = new HttpGet(uri);
		
		String response = doHttp(httpGet);
		
		JSONObject json = JSONObject.fromObject(response);
		
		JSONArray objects = json.getJSONArray("rows");
		
		return objects;
	}

	public String getFormXMLByOwnerIdAndFormInstance(JSONArray key)throws IOException, URISyntaxException
	{
		String keyString = key.toString();
		String encodedURL = URLEncoder.encode(keyString,"UTF-8");
		String viewName = ( key.size() >= 3  ? "GetDocByOwnerAndFormInstance" : "GetDocsByOwnerAndForm" );
		String viewURL = constractListURL("formToXml", viewName);
		URI uri = URIUtils.createURI("http", host, port, viewURL,"key="+ encodedURL, null);
		HttpGet httpGet = new HttpGet(uri);
		String response = doHttp(httpGet);
		return response;
	}
	
	public String getFormXMLByFormId(String keyString, List<String> owners)throws IOException, URISyntaxException {
		return getFormXMLByFormId(keyString, owners, null);
	}	
	
	public String getFormXMLByFormId(String keyString, List<String> owners, String context)throws IOException, URISyntaxException
	{
		JSONObject jsonBody = new JSONObject();
		JSONArray keys = new JSONArray();
		
		for (String ownerId: owners) {
			JSONArray aKey = new JSONArray();
			aKey.add(keyString);
			aKey.add(ownerId);
			keys.add(aKey);
		}
		jsonBody.put("keys", keys);
		String viewURL = constractListURL("formToXmlAllOwners", "GetDocsByFormAndOwner", context);
		URI uri = URIUtils.createURI("http", host, port, viewURL, null, null);
		String response = runPostQuery(uri, jsonBody.toString());
		return response;
	}
	
	public JSONArray getFormJSONByFormId(String keyString, List<String> owners)throws IOException, URISyntaxException {
		return getFormJSONByFormId(keyString, owners, null);
	}
	
	
	public JSONArray getFormJSONByFormId(String keyString, List<String> owners, String context)throws IOException, URISyntaxException
	{
		JSONObject jsonBody = new JSONObject();
		JSONArray keys = new JSONArray();
		
		for (String ownerId: owners) {
			JSONArray aKey = new JSONArray();
			aKey.add(keyString);
			aKey.add(ownerId);
			keys.add(aKey);
		}
		jsonBody.put("keys", keys);
		String viewURL = constructViewURL("GetDocsByFormAndOwner", context );
		URI uri = URIUtils.createURI("http", host, port, viewURL, null, null);
		String response = runPostQuery(uri, jsonBody.toString());
		JSONObject json = JSONObject.fromObject(response);
		JSONArray objects = json.getJSONArray("rows");
		JSONArray forms = null;
		//This used to return only one object. it has to return documents for all owners
		if (objects!= null && objects.size() >0)
		{
			forms = new JSONArray();
			for(int i=0; i<objects.size(); i++)
			{
				JSONObject row = objects.getJSONObject(i);
				forms.add(row.getJSONObject("value"));
			}
		}
		return forms;
		
	}


	/**
	 * Query for answers for a single question
	 * @param ownerId
	 * @param questionID
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Collection<String> getAnswersByOwnerAndQuestionAsList(String ownerId, String formID, String rowID, String questionID, Long... instanceIds )throws IOException, URISyntaxException
	{
		List<AnswerSearchResultsBean> results = getAnswerResultsByOwnerAndQuestion(ownerId, formID, rowID, questionID, instanceIds);
		
		// remove duplicates from the list of answers by using a Set
		Set<String> set = new HashSet<String>();
		for ( AnswerSearchResultsBean bean : results ){
			Collection<String> answers = bean.getAnswers();
			if ( answers != null && !answers.isEmpty() ) {
				set.addAll( answers );
			}
		}
		
		return new LinkedList<String>( set );
	}
	
	/**
	 * Query for answers for a single question
	 * @param ownerId
	 * @param questionID
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private List<AnswerSearchResultsBean> getAnswerResultsByOwnerAndQuestion(String ownerId, String formID, String rowID, String questionID, Long... instanceIds )throws IOException, URISyntaxException
	{
		AnswerSearchCriteriaBean[] beans = new AnswerSearchCriteriaBean[ instanceIds.length ];
		
		for ( int i = 0; i < instanceIds.length; ++i )
		{
			beans[ i ] = new AnswerSearchCriteriaBean(formID, rowID, questionID, instanceIds[ i ]);
		}
		
		List<AnswerSearchResultsBean> results = getAnswersByOwnerAndQuestion(ownerId, beans);
		return results;
	}
	
	/**
	 * Query for answers for a single question
	 * @param ownerId
	 * @param questionID
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Map<Long,Collection<String>> getAnswersByOwnerAndQuestionAsMap(String ownerId, String formID, String rowID, String questionID, Long... instanceIds )throws IOException, URISyntaxException
	{
		Map<Long,Collection<String>> map = new HashMap<Long,Collection<String>>();
		List<AnswerSearchResultsBean> results = getAnswerResultsByOwnerAndQuestion(ownerId, formID, rowID, questionID, instanceIds);
		
		for ( AnswerSearchResultsBean bean : results ){
			Collection<String> answers = bean.getAnswers();
			Long answerInstanceId = bean.getCriteria().getInstanceId();
			if ( answers != null && !answers.isEmpty() ) {
				map.put( answerInstanceId, answers );
			}
		}
		
		return map;
	}

	public List<AnswerSearchResultsBean> getAnswersByOwnerAndQuestion(String ownerId, AnswerSearchCriteriaBean... criterias )throws IOException, URISyntaxException {
		return getAnswersByOwnerAndQuestion(ownerId, Arrays.asList(criterias));
	}
	
	/**
	 * Query for answers for a list(Collection) of questions
	 * @param ownerId
	 * @param questionIDs - Collection of questions
	 * @return map (key = formId_questionId, values - related answers)
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("unchecked")
	public List<AnswerSearchResultsBean> getAnswersByOwnerAndQuestion(String ownerId, Collection<AnswerSearchCriteriaBean> criterias )throws IOException, URISyntaxException
	{
		// construct query body
		/* SAMPLE JSON BODY for this query:
			{
				"keys":[
					["88cdceae-6311-4994-ae4d-8e071b59b7b1","7e11e7b6-df8e-4787-a3e6-2c8b4a3cef52"],
					["6e086702-e0b9-4b5f-9304-8c01dd280380","8a3d1587-f170-41ea-8b4d-e0c6e00aaefe"]
				]
			}
		*/
		JSONObject jsonBody = new JSONObject();
		JSONArray keys = new JSONArray();
		
		boolean includeInstanceData = false;
		
		for (AnswerSearchCriteriaBean criteria : criterias) {
			JSONArray aKey = new JSONArray();
			aKey.add(ownerId);
			aKey.add(criteria.getFormId());
			if(criteria.getRowId() != null) {
				aKey.add(criteria.getRowId());
			}
			
			aKey.add(criteria.getQuestionId());
			
			if ( criteria.getInstanceId() != null ) {
				includeInstanceData = true;
				aKey.add(criteria.getInstanceId());
			}
			
			keys.add(aKey);
		}
		jsonBody.put("keys", keys);

		// send it
		String view = ( includeInstanceData ? "GetAnswersByOwnerAndQuestionAndFormInstance" : "GetAnswersByOwnerAndQuestion" );
		URI uri = URIUtils.createURI(
				"http", host, port, constructViewURL(view),null, null);

		String response = runPostQuery(uri, jsonBody.toString());

		log.debug("In " + view + ": response:");
		log.debug("==================");
		log.debug(response);

		// parse response
		ArrayList<AnswerSearchResultsBean> results = new ArrayList<AnswerSearchResultsBean>();
		JSONObject jsonData =  (JSONObject) JSONSerializer.toJSON( response );
		// must contain an array of values
		if ( jsonData.containsKey("rows")){
			JSONArray resultSet = jsonData.getJSONArray("rows");
			Iterator<JSONObject> rowIter = resultSet.iterator();
			while (rowIter.hasNext())
			{
				JSONObject row = rowIter.next();
				JSONArray values = row.getJSONArray("value");
				Collection<String> javaValues = JSONArray.toCollection(values);
				JSONArray key = row.getJSONArray("key");
				
				boolean keyContainsRowId = ( includeInstanceData ? key.size() > 4 : key.size() == 4 );
				
				AnswerSearchCriteriaBean criteria = new AnswerSearchCriteriaBean();
				criteria.setFormId(key.getString(1));
				criteria.setRowId(keyContainsRowId ? key.getString(2) : null);
				criteria.setQuestionId(keyContainsRowId ? key.getString(3) : key.getString(2));
				if ( includeInstanceData ) criteria.setInstanceId( key.getLong( key.size() - 1 ) );
				results.add(new AnswerSearchResultsBean(criteria, javaValues));
			}
		}
		return results;
	}

	/**
	 * Query for answers for a list(Collection) of questions
	 * @param ownerId
	 * @param questionIDs - Collection of questions
	 * @return map (key = formId_questionId, values - related answers)
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONObject getLatestAnswersByOwnerAndQuestionAndFormInstance(String ownerId, Map<String,Long> ids)throws IOException, URISyntaxException
	{
		// construct query body
		/* SAMPLE JSON BODY for this query:
			{
				"keys":[
					["88cdceae-6311-4994-ae4d-8e071b59b7b1","7e11e7b6-df8e-4787-a3e6-2c8b4a3cef52",1],
					["6e086702-e0b9-4b5f-9304-8c01dd280380","8a3d1587-f170-41ea-8b4d-e0c6e00aaefe",1]
				]
			}
		*/
		JSONObject jsonBody = new JSONObject();
		JSONArray keys = new JSONArray();
		
		Set<String> questionIds = ids.keySet();
				
		for ( String questionId : questionIds ) {
			JSONArray aKey = new JSONArray();
			Long instanceId = ids.get( questionId );
			aKey.add(ownerId);
			aKey.add(questionId);
			aKey.add(instanceId);
			keys.add(aKey);
		}
		jsonBody.put("keys", keys);
//		jsonBody.put("group", "true");

		// send it
		URI uri = URIUtils.createURI(
				"http", host, port, constructViewURL("GetLatestAnswersByOwnerAndQuestionAndFormInstance"),null, null);

		String response = runPostQuery(uri, jsonBody.toString());

		log.debug("In getLatestAnswersByOwnerAndQuestionAndFormInstance: response:");
		log.debug("==================");
		log.debug(response);

//		// parse response
		ArrayList<AnswerSearchResultsBean> results = new ArrayList<AnswerSearchResultsBean>();
		JSONObject jsonData =  (JSONObject) JSONSerializer.toJSON( response );
//		// must contain an array of values
//		if ( jsonData.containsKey("rows")){
//			JSONArray resultSet = jsonData.getJSONArray("rows");
//			Iterator<JSONObject> rowIter = resultSet.iterator();
//			while (rowIter.hasNext())
//			{
//				JSONObject row = rowIter.next();
//				JSONArray values = row.getJSONArray("value");
//				Collection<String> javaValues = JSONArray.toCollection(values);
//				JSONArray key = row.getJSONArray("key");
//				AnswerSearchCriteriaBean criteria = new AnswerSearchCriteriaBean();
//				criteria.setFormId(key.getString(1));
//				criteria.setRowId(key.size() >= 4 ? key.getString(2) : null);
//				criteria.setQuestionId(key.size() >= 4 ? key.getString(3) : key.getString(2));
//				results.add(new AnswerSearchResultsBean(criteria, javaValues));
//			}
//		}
		return jsonData;

	}
	
	public Collection<Map<String, String>> getAllDocRefs()throws IOException, URISyntaxException
	{
		String viewURL = constructViewURL("GetDocRefsByOwner");
		URI uri = URIUtils.createURI("http", host, port, viewURL, null, null);
		return getDocRefs(uri);
	}

	public Collection<Map<String, String>> getDocRefsByForm(String formId )throws IOException, URISyntaxException
	{
		// send
		String encodedURL = URLEncoder.encode("\""+formId+"\"","UTF-8");
		String viewURL = constructViewURL("GetDocRefByForm");
		URI uri = URIUtils.createURI("http", host, port, viewURL,"key="+ encodedURL, null);
		return getDocRefs(uri);

	}

	public Collection<Map<String, String>> getDocRefsByOwner(String ownerId )throws IOException, URISyntaxException
	{
		// send
		String encodedURL = URLEncoder.encode("\""+ownerId+"\"","UTF-8");
		String viewURL = constructViewURL("GetDocRefsByOwner");
		URI uri = URIUtils.createURI("http", host, port, viewURL,"key="+ encodedURL, null);
		return getDocRefs(uri);

	}
//	public Collection<Map<String, String>> getDocsByEntity(String entityId )throws IOException, URISyntaxException
//	{
//		// send
//		String encodedURL = URLEncoder.encode("\""+entityId+"\"","UTF-8");
//		String viewURL = constructViewURL("GetDocsByEntity");
//		URI uri = URIUtils.createURI("http", host, port, viewURL,"key="+ encodedURL, null);
//		return getDocRefs(uri);
//
//	}
	
	public void getDocsByOwnerList(List<String> ownerIds, CouchJSONConverter jsonConverter) throws Exception
	{
		String viewURL = constructViewURL("GetDocsByOwner",getCacureDbName(),getCacureDesignDoc());
		URI uri = URIUtils.createURI("http", host, port, viewURL,null, null);
		JSONObject keys = new JSONObject();
		JSONArray values =  new JSONArray();
		for (String ownerId: ownerIds)
		{
			values.add(ownerId);
		}
		keys.put("keys", values);
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setHeader("Content-Type", "application/json");
		StringEntity body = new StringEntity(keys.toString());
		httpPost.setEntity(body);

		doHttp(httpPost, jsonConverter);
	}
	
	public JSONArray getDocsByFormList(List<String> formIds) throws Exception
	{
		String viewURL = constructViewURL("GetDocsByForm",getCacureDbName(),getCacureDesignDoc());
		URI uri = URIUtils.createURI("http", host, port, viewURL,null, null);
		JSONObject keys = new JSONObject();
		JSONArray values =  new JSONArray();
		for (String formId: formIds)
		{
			values.add(formId);
		}
		keys.put("keys", values);
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setHeader("Content-Type", "application/json");
		StringEntity body = new StringEntity(keys.toString());
		httpPost.setEntity(body);

		String response = doHttp(httpPost);		
		JSONObject json = JSONObject.fromObject(response);		
		JSONArray docs = json.getJSONArray("rows");		
		return docs;
	}
	
	
	public void getDocsByOwnersAndModules(List<String> keysList, OutputStream os) throws Exception{
		String viewURL = this.constructListUrl(getCacureDesignDoc(), "formsToXml", "GetDocsByOwnerAndModule");
		URI uri = URIUtils.createURI("http", host, port, viewURL,null, null);
		JSONObject keys = new JSONObject();
		JSONArray values =  new JSONArray();
		for (String key: keysList)
		{
			values.add(key);
		}
		keys.put("keys", values);
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setHeader("Content-Type", "application/json");
		StringEntity body = new StringEntity(keys.toString());
		httpPost.setEntity(body);
		doHttp(httpPost, os);
	}
	
	 private Collection<Map<String, String>> getDocRefs(URI uri) throws IOException
	 {
		 HttpGet httpGet = new HttpGet(uri);
		String response = doHttp(httpGet);

		Collection<Map<String,String>> results = new LinkedList<Map<String, String>>();

		// parse response
		JSONObject jsonData =  (JSONObject) JSONSerializer.toJSON( response );
		// must contain an array of values

		// check number of records
		/*JSONObject rows = jsonData.getJSONObject("rows");
		if (rows == null || !rows.isArray())
		{
			//no data returned.
			return results;
		}*/

		JSONArray resultSet = jsonData.getJSONArray("rows");
		Iterator<JSONObject> rowIter = resultSet.iterator();
		while (rowIter.hasNext())
		{
			JSONObject row = rowIter.next();
			JSONObject value = row.getJSONObject("value");
			Map<String, String> docRef = new HashMap<String, String>();
			docRef.put("_id", value.getString("_id"));
			docRef.put("_rev", value.getString("_rev"));
			results.add(docRef);
		}

		return results;
	}


	public void deleteAllOwnerDocs(String ownerId )throws IOException, URISyntaxException
	{
		Collection<Map<String, String>> docRefs = getDocRefsByOwner(ownerId);
		for (Map<String, String> docRef : docRefs)
		{
			deleteDoc(docRef.get("_id"), docRef.get("_rev"));
		}
	}
	
	public void deleteAllFormDocs( String formId ) throws IOException, URISyntaxException
	{
		Collection<Map<String, String>> docRefs = getDocRefsByForm(formId);
		for (Map<String, String> docRef : docRefs)
		{
			deleteDoc(docRef.get("_id"), docRef.get("_rev"));
		}
	}

	public void deleteDocs(Collection<Map<String, String>> docsToDelete)throws IOException, URISyntaxException
	{
		for (Map<String, String> docRef : docsToDelete)
		{
			deleteDoc(docRef.get("_id"), docRef.get("_rev"));
		}
	}
	
	public void deleteDocsByDocRef( JSONArray docRefs ) throws Exception
	{
		for ( Object docRef : docRefs )
		{
			(( JSONObject ) docRef).put( "_deleted", true );
		}
		
		JSONObject jsonPost = new JSONObject();
		jsonPost.put( "docs",  docRefs );
		
		URI uri = URIUtils.createURI("http", host, port, "/" + dbName + "/_bulk_docs", null, null);
		runPostQuery( uri, jsonPost.toString() );
	}
	
	public long deleteAllDocs()throws IOException, URISyntaxException
	{
		Collection<Map<String, String>> docRefs = getAllDocRefs();
		for (Map<String, String> docRef : docRefs)
		{
			deleteDoc(docRef.get("_id"), docRef.get("_rev"));
		}
		return docRefs.size();
	}

	public void deleteDoc(String docId, String docRev )throws IOException, URISyntaxException
	{
		// send
		URI uri = URIUtils.createURI("http", host, port, "/" + dbName + "/" + docId,"rev="+ docRev, null);

		HttpDelete httpDel = new HttpDelete(uri);
		String response = doHttp(httpDel);

	}


	public JSONArray getAnswersByOwnerAndQuestion(JSONArray key)throws IOException, URISyntaxException
	{
		String keyString = key.toString();
		String encodedURL = URLEncoder.encode(keyString,"UTF-8");
		boolean includeInstanceData = ( key.size() == 2 ); 
		String viewName = includeInstanceData ? "GetAnswersByOwnerAndQuestionAndFormInstance" : "GetAnswersByOwnerAndQuestion";
		String viewURL = constructViewURL( viewName );
		URI uri = URIUtils.createURI("http", host, port, viewURL,"key="+ encodedURL, null);
		HttpGet httpGet = new HttpGet(uri);
		String response = doHttp(httpGet);
		JSONObject json = JSONObject.fromObject(response);
		JSONArray answers= null;
		if ( json.containsKey("rows")){
			JSONArray objects = json.getJSONArray("rows");
			if (objects!= null && objects.size() >0)
			{
			    JSONObject row = objects.getJSONObject(0);
			    answers = row.getJSONArray("value");
			}
		}
		return answers;
	}

	public JSONObject readObject(String id)throws IOException, URISyntaxException
	{
		URI uri = URIUtils.createURI("http", host, port, "/" + dbName + "/","id="+ id, null);
		HttpGet httpGet = new HttpGet(uri);
		String response = doHttp(httpGet);
		JSONObject json = JSONObject.fromObject(response);
		return json;
	}

	public String saveForm(JSONObject jsonDoc, JSONArray key) throws IOException, URISyntaxException
	{
		JSONObject object = getFormByOwnerAndFormInstance(key);
		String id, rev;
        if(object != null)
        {
        	id = object.getString("_id");
        	rev = object.getString("_rev");
        	jsonDoc.put("_id", id);
        	jsonDoc.put("_rev", rev);
        }
		else
		{
			id = getDocId();
		}
		String jsonStr = jsonDoc.toString();
		//URI uri = URIUtils.createURI("http", DB_HOST, DB_PORT, "/" + DB_NAME + "/" + getDocId(),
		URI uri = URIUtils.createURI("http", host, port, "/" + dbName + "/" + id,
			    null, null);
		String response = runPutQuery(uri, jsonStr);
		return response;
	}
	
	public String addAttachment( String attachmentName, JSONObject attachment )  throws IOException, URISyntaxException
	{
		return addAttachment( attachmentName, attachment, getCacureDbName(), getCacureDesignDoc() );
	}
	
	public String addAttachment( String attachmentName, JSONObject attachment, String dbName, String designDocName ) throws IOException, URISyntaxException
	{
		String revision = getCouchDbDesignDocRevisionNumber( dbName, designDocName );
		
		URI uri = URIUtils.createURI("http", host, port, "/" + dbName + "/" + DESIGN_DOC_PREFIX + "/" + designDocName + "/" + attachmentName + 
				(StringUtils.isEmpty(revision) ? "" : "?rev=" +revision),
			    null, null);
		
		String response = runPutQuery( uri, attachment.toString() );
		
		return response;
	}
	
	public String getAttachment( String attachmentName )
	{
		return getAttachment( attachmentName, getCacureDbName(), getCacureDesignDoc() );
	}
	
	public String getAttachment( String attachmentName, String dbName, String designDocName )
	{
		String response = null;
		
		try
		{		
			URI uri = URIUtils.createURI("http", host, port, "/" + dbName + "/" + DESIGN_DOC_PREFIX + "/" + designDocName + "/" + attachmentName, 
					null, null);
			
			response = doHttp( new HttpGet( uri ) );
		}
		catch( Exception ex )
		{
			log.error( "Could not read the attachment " + attachmentName + " in the database " + dbName );
			log.error( ExceptionUtils.getFullStackTrace( ex ) );
		}
		
		return response;
	}

	private String runPutQuery(URI uri, String jsonBody) throws IOException
	{
		// Prepare a request object
		HttpPut httpPut = new HttpPut(uri);
		httpPut.setHeader("Content-Type", "application/json");
		StringEntity body = new StringEntity(jsonBody);
		httpPut.setEntity(body);

		String response = doHttp(httpPut);
		return response;

	}

	private String runPostQuery(URI uri, String jsonBody) throws IOException
	{
		// Prepare a request object
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setHeader("Content-Type", "application/json");
		StringEntity body = new StringEntity(jsonBody);
		httpPost.setEntity(body);

		String response = doHttp(httpPost);
		return response;

	}
	
	private String getCouchDbDesignDocRevisionNumber(String databaseName, String designDocName) throws IOException, URISyntaxException
	{
		// Prepare a request object
		URI uri = URIUtils.createURI("http", host, port, "/" + databaseName + "/" + DESIGN_DOC_PREFIX + "/" + designDocName,
			    null, null);
		HttpGet httpGet = new HttpGet(uri);

		// response is a JSON object with one array in it
		String response = doHttp(httpGet);
		
		JSONObject doc= JSONObject.fromObject(response);
		
		return ( doc.containsKey( "_rev" ) ? doc.getString("_rev") : null );
	}

	public String writeToDb(JSONObject jsonDoc) throws IOException, URISyntaxException
	{
		jsonDoc.remove( "_rev");
		String jsonStr = jsonDoc.toString();
		String revision = getCouchDbDesignDocRevisionNumber(dbName, designDoc);
		URI uri = URIUtils.createURI("http", host, port, "/" + dbName + "/" + getDocId()
				+ (StringUtils.isEmpty(revision) ? "" : "?rev=" +revision),
			    null, null);

		// Prepare a request object
		HttpPut httpPut = new HttpPut(uri);
		httpPut.setHeader("Content-Type", "application/json");
		StringEntity body = new StringEntity(jsonStr);
		httpPut.setEntity(body);

		String response = doHttp(httpPut);
		return response;
	}
	
	public String updateDesignDoc(JSONObject jsonDoc, String designDocName) throws IOException, URISyntaxException
	{
		String revision = getCouchDbDesignDocRevisionNumber(dbName, designDocName);
		
		
		if ( !StringUtils.isEmpty( revision ) ) jsonDoc.put("_rev", revision);
		

		String jsonStr = jsonDoc.toString();
		
			
		URI uri = URIUtils.createURI("http", host, port, "/" + dbName + "/" + DESIGN_DOC_PREFIX + "/" + designDocName,
			    null, null);

		// Prepare a request object
		HttpPut httpPut = new HttpPut(uri);
		httpPut.setHeader("Content-Type", "application/json");
		StringEntity body = new StringEntity(jsonStr);
		httpPut.setEntity(body);

		String response = doHttp(httpPut);
		return response;
	}

	private String getDocId() throws IOException, URISyntaxException
	{
		if (docIdSet == null || docIdSet.isEmpty())
		{
			batchGetDocumentIDs();
		}
		String id = docIdSet.remove(0);
		return id;
	}

	private void batchGetDocumentIDs() throws IOException, URISyntaxException
	{

		// retrieve a set of IDs from DB
		URI uri = URIUtils.createURI("http", host, port, "/_uuids",
			    "count=" + batchSize , null);

		// Prepare a request object
		HttpGet httpGet = new HttpGet(uri);

		// response is a JSON object with one array in it
		String response = doHttp(httpGet);
//		Object obj=JSONValue.parse(response);
		JSONObject doc=JSONObject.fromObject(response);
		List<String> idList = (List<String>)(doc.get("uuids"));

		if (docIdSet == null)
		{
			docIdSet = new LinkedList<String>();
		}
		for (String id: idList)
		{
			docIdSet.add(id);
		}

	}
	
	/**
	 * Returns a map of parameters
	 * which includes the CouchDB parameters "startkey", "endkey", "startkey_docid" and "limit",
	 * useful for pagination.
	 * @param startKey
	 * @param endKey
	 * @param startKeyDocId
	 * @return
	 */
	public Map<String,Object> getPaginationParameters( Object startKey, Object endKey, String startKeyDocId  ) {
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put( "startkey", startKey );
		parameters.put( "endkey", endKey );
		parameters.put( "limit", batchSize );
		parameters.put( "startkey_docid", startKeyDocId );
		return parameters;
	}
	
	private String constructParameterList(Map<String,Object> parameters) {
		StringBuilder builder = new StringBuilder();
		
		if ( parameters != null )
		{
			Set<Map.Entry<String,Object>> parameterSet = parameters.entrySet();
			for ( Map.Entry<String,Object> param : parameterSet )
			{
				String key = param.getKey();
				Object value = param.getValue();
				if ( value != null )
					try 
					{
						builder.append("&").append(key).append("=").append(URLEncoder.encode(value.toString(),"UTF-8"));
					} 
				    catch (UnsupportedEncodingException e) 
				    {
					}
			}
			
			String queryString = StringUtils.substring(builder.toString(),1);
			return ( StringUtils.isBlank( queryString ) ? null : queryString );
		}
		
		else
		{
			return null;
		}
	}
	
	/**
	 * Generates the data associated with this CouchDB view.
	 * @param viewName - name of the CouchDB view
	 * @param parameterMap - map of parameter name-value pairs
	 * @return
	 * */
	public JSONObject getDataForView( String viewName, Map<String,Object> parameterMap )throws URISyntaxException
	{
		JSONObject obj = null;
		URI uri = null;
		try
		{
			String viewURI = constructViewURL( viewName );
			
			String parameterList = constructParameterList(parameterMap);
				
			// Construct the full URL
			uri = URIUtils.createURI("http", host, port, viewURI, parameterList, null);
			HttpGet httpGet = new HttpGet(uri);
			String response = doHttp(httpGet);
			obj = JSONObject.fromObject( response );
		}catch(IOException ex)
		{
			log.error( "Could not read from CouchDB: " + uri );
			ex.printStackTrace();
		}
		return obj;
	}
	
	/**
	 * Returns the "values" from the key-value pairs generated in the response body
	 * when a CouchDB view is invoked.
	 * 
	 * @param viewName
	 * @param keys
	 * @param httpMethod (GET/POST)
	 * @return
	 * @throws URIException
	 */
	public JSONArray getKeyValuesFromView( String viewName, String keys ) throws Exception
	{
		JSONObject jsonRequest = new JSONObject();
		JSONObject jsonResponse = null;
		jsonRequest.put( "keys", keys );
		URI uri = null;
		try
		{
			String viewURI = constructViewURL( viewName );
				
			// Construct the full URL
			uri = URIUtils.createURI("http", host, port, viewURI, null, null);
			
			// Get the response
			String response = runPostQuery(uri,jsonRequest.toString());
			jsonResponse = JSONObject.fromObject( response );
			
			// Get the rows from the response
			JSONArray rows = jsonResponse.getJSONArray("rows");
			
			// Get the values from the rows
			JSONArray values = new JSONArray();
			for ( Object row : rows )
			{
				JSONObject value = (( JSONObject ) row).getJSONObject( "value" );
				values.add( value );
			}
			
			// return the values
			return values;
		}catch(IOException ex)
		{
			log.error( "Could not read from CouchDB: " + uri );
			ex.printStackTrace();
			throw ex;
		}
	}
	
	private void doHttp(HttpUriRequest request, OutputStream os)
			throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		
		// setup authentication credentials when appropriate
		setUpAuthenticationCredentials((DefaultHttpClient)httpclient);
		
		HttpResponse response = httpclient.execute(request);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			InputStream instream = null;
			try {
				instream = entity.getContent();
				IOUtils.copy(instream, os);
			} catch (RuntimeException ex) {
				request.abort();
				throw ex;

			} finally {
				try {
					instream.close();
					os.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			httpclient.getConnectionManager().shutdown();
		}
	}
	
	private void doHttp(HttpUriRequest request, CouchJSONConverter converter) throws Exception{
		HttpClient httpclient = new DefaultHttpClient();
		
		// setup authentication credentials when appropriate
		setUpAuthenticationCredentials((DefaultHttpClient)httpclient);
		
		// Execute the request
		HttpResponse response = httpclient.execute(request);
		// Get hold of the response entity
		HttpEntity entity = response.getEntity();

		// If the response does not enclose an entity, there is no need
		// to worry about connection release
		if (entity != null)
		{
			InputStream instream = null;
			try
			{
				instream = entity.getContent();
				
				converter.setInputStream(instream);
				converter.convert();
				} catch (RuntimeException ex) {

		         // In case of an unexpected exception you may want to abort
		         // the HTTP request in order to shut down the underlying
		         // connection and release it back to the connection manager.
		    	 request.abort();
		         throw ex;

		     } finally {

		         // Closing the input stream will trigger connection release
		    	 try{
		    		 instream.close();
		    	 } catch(IOException ex){
		    		 ex.printStackTrace();
		    	 }
		     }

		     // When HttpClient instance is no longer needed,
		     // shut down the connection manager to ensure
		     // immediate deallocation of all system resources
		     httpclient.getConnectionManager().shutdown();
		}
	}
	
	private void setUpAuthenticationCredentials(DefaultHttpClient client)
	{
		log.info("In setUpAuthenticationCredentials method...");
		String username = getDbLoginUserName();
		String password = getDbLoginPassword();
		if ( StringUtils.isNotBlank( username ) && StringUtils.isNotBlank( password))
		{
			client.getCredentialsProvider().setCredentials( AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		}
		else
		{
			log.info("No authentication credentials provided for connecting to CouchDB");
		}
	}
	
	
	private String doHttp(HttpUriRequest request) throws IOException
	{
		StringBuffer responseBody = new StringBuffer();
		HttpClient httpclient = new DefaultHttpClient();
		
		// setup authentication credentials when appropriate
		setUpAuthenticationCredentials((DefaultHttpClient)httpclient);
		
		// Execute the request
		HttpResponse response = httpclient.execute(request);

		// Get hold of the response entity
		HttpEntity entity = response.getEntity();

		// If the response does not enclose an entity, there is no need
		// to worry about connection release
		if (entity != null)
		{
		     InputStream instream = entity.getContent();
		     try {

		         BufferedReader reader = new BufferedReader(
		                 new InputStreamReader(instream));
		         // do something useful with the response
		         String line;
		         while ((line=reader.readLine()) != null)
		         {
		        	 responseBody.append(line).append("\n");
		         }

		     } catch (IOException ex) {

		         // In case of an IOException the connection will be released
		         // back to the connection manager automatically
		         throw ex;

		     } catch (RuntimeException ex) {

		         // In case of an unexpected exception you may want to abort
		         // the HTTP request in order to shut down the underlying
		         // connection and release it back to the connection manager.
		    	 request.abort();
		         throw ex;

		     } finally {

		         // Closing the input stream will trigger connection release
		         instream.close();

		     }

		     // When HttpClient instance is no longer needed,
		     // shut down the connection manager to ensure
		     // immediate deallocation of all system resources
		     httpclient.getConnectionManager().shutdown();

		}
		return responseBody.toString();
	}
	
	private String constructViewURL(String viewName, String dbName, String designDoc)
	{
		StringBuilder url = new StringBuilder(100);
		url.append("/");
		url.append(dbName);
		url.append("/_design/");
		url.append(designDoc);
		url.append("/_view/");
		url.append(viewName);
		log.debug("URL to acces view " + viewName + " is: " + url);
		return url.toString();
	}

	private String constructViewURL(String viewName)
	{
		return constructViewURL(viewName,dbName,designDoc);
	}
	
	
	/* This method is used when context is set for just one call only */
	private String constructViewURL(String viewName, String context)
	{
		return constructViewURL(viewName,( context == null ? dbName : getDbName(context) ),designDoc);
	}
	
	/* This method is used when context is set for just one call only */
	private String constractListURL(String listName, String viewName, String context)
	{
		return this.constructListUrl(this.designDoc, listName, viewName, context);
	}


	private String constractListURL(String listName, String viewName)
	{
		return this.constructListUrl(this.designDoc, listName, viewName);
	}
	
	private String constructListUrl(String designDoc, String listName, String viewName){
		StringBuilder url = new StringBuilder(100);
		url.append("/");
		url.append(dbName);
		url.append("/_design/");
		url.append(designDoc);
		url.append("/_list/");
		url.append(listName);
		url.append("/");
		url.append(viewName);
		log.debug("URL to acces list: " + listName + "with view: "+ viewName + " is: " + url);
		return url.toString();
	}
	
	/* This method is used when context is set for just one call only */
	private String constructListUrl(String designDoc, String listName, String viewName, String context){
		StringBuilder url = new StringBuilder(100);
		url.append("/");
		url.append( context == null ? dbName : getDbName(context) );
		url.append("/_design/");
		url.append(designDoc);
		url.append("/_list/");
		url.append(listName);
		url.append("/");
		url.append(viewName);
		log.debug("URL to acces list: " + listName + "with view: "+ viewName + " is: " + url);
		return url.toString();
	}
	

}
