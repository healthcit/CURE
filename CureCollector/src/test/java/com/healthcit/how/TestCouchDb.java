/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.healthcit.cacure.dao.CouchDBDao;

public class TestCouchDb {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//TestCouchDb.testCouchDb();

		//JSONObject
		JSONArray jasonArray = new JSONArray();
		jasonArray.add("1");
		jasonArray.add("2");

		System.out.println("JASON Array: " + jasonArray);
		List<String> jasonList = jasonArray.toList(jasonArray);

		for(String value : jasonList){
			System.out.println(value);
		}
	}

	public static void testCouchDb(){
		try {
			JSONObject response = TestCouchDb.getFormDataJSON("fc2f9d51-c46a-4fa2-8e3d-93d5d19b9b41", "22550");
			System.out.println(response.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static JSONObject getFormDataJSON(String formId, String entityId) throws IOException, URISyntaxException
	{
		JSONArray key = getObjectKey(formId, entityId);
		JSONObject response = new CouchDBDao().getFormByOwnerIdAndFormId(key);
		return response;
	}

	private static JSONArray getObjectKey(String formId, String entityId)
	{
		JSONArray key = new JSONArray();
		key.add(entityId);
		key.add(formId);
		return key;
	}

}
