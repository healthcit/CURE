/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;

import com.healthcit.cacure.beans.AnswerSearchCriteriaBean;
import com.healthcit.cacure.beans.AnswerSearchResultsBean;
import com.healthcit.cacure.dao.CouchDBDao;

public class CouchDBDaoTest extends TestCase
{

	CouchDBDao couchDBDao;
	protected void setUp() throws Exception
	{
		couchDBDao = new CouchDBDao();
		couchDBDao.setHost("127.0.0.1");
		couchDBDao.setPort(5984);
		couchDBDao.setDbName("test_db");
		couchDBDao.setDesignDoc("caCURE");
		couchDBDao.setBulkBatchSize(1000);
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testGetAnswersByEntityAndSingleQuestion()
	{
		try
		{
			Collection<String> result =
				couchDBDao.getAnswersByOwnerAndQuestion(
						"d035ec99-09f6-44f1-8439-04928294521d", "formId", null, "2654");
			assert(result.size() == 3);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	public void testGetAnswersByEntityAndBadQuestion()
	{
		try
		{
			Collection<String> result =
				couchDBDao.getAnswersByOwnerAndQuestion(
						"d035ec99-09f6-44f1-8439-04928294521d", "formId", null, "XXXXX");
			assert(result.size() == 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}

	public void testGetAnswersByEntityAndMultiQuestion()
	{
		try
		{
			Collection<String> result =
				couchDBDao.getAnswersByOwnerAndQuestion(
						"d035ec99-09f6-44f1-8439-04928294521d", "formId", null, "2654");
			assert(result.size() == 3);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}
	
	/*public void test()
	{
		try
		{
			JSONArray aKey = new JSONArray();
			aKey.add("b47bee48-eff4-4d55-bce0-f9bd58c932f6");
			aKey.add("0205083d-1cc2-45a2-bc56-8841ac36c53c");
			aKey.add("4dd99e4b-4b39-4e4c-ade0-2d7e63ea757e");
			URI uri = URIUtils.createURI("http", "127.0.0.1", 5984, "/test_db/_design/caCURE/_view/GetAnswersByEntityAndQuestion",
					"key=" + URLEncoder.encode(aKey.toString(), "UTF-8"), null);
			
			JSONObject jsonBody = new JSONObject();
			JSONArray keys = new JSONArray();
			JSONArray aKey = new JSONArray();
			aKey.add("b47bee48-eff4-4d55-bce0-f9bd58c932f6");
			aKey.add("4dd99e4b-4b39-4e4c-ade0-2d7e63ea757e");
//			aKey.add("0205083d-1cc2-45a2-bc56-8841ac36c53c");
			keys.add(aKey);
			jsonBody.put("keys", keys);
			
			HttpGet httpPost = new HttpGet(uri);
			httpPost.setHeader("Content-Type", "application/json");
//			StringEntity body = new StringEntity(jsonBody.toString());
//			httpPost.setEntity(body);

			String response = doHttp(httpPost);

			JSONObject jsonData =  (JSONObject) JSONSerializer.toJSON( response );
			
			System.out.println(jsonData.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}*/

	private static String doHttp(HttpUriRequest request) throws IOException
	{
		StringBuffer responseBody = new StringBuffer();
		HttpClient httpclient = new DefaultHttpClient();
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
	
	public void testGetAnswersByEntityAndQuestionStringCollectionOfString()
	{
		try
		{
			AnswerSearchCriteriaBean c1 = new AnswerSearchCriteriaBean("formId1", null, "2654");
			AnswerSearchCriteriaBean c2 = new AnswerSearchCriteriaBean("formId2", null, "TQ-13072");
			AnswerSearchCriteriaBean c3 = new AnswerSearchCriteriaBean("formId3", null, "8a3d1587-f170-41ea-8b4d-e0c6e00aaefe");
			List<AnswerSearchResultsBean> result = couchDBDao.getAnswersByOwnerAndQuestion(
						"d035ec99-09f6-44f1-8439-04928294521d", c1, c2, c3);
			assert(result.size() == 3);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("Exception occured");
		}
	}

}
