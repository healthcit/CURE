/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.healthcit.cacure.dao.CouchDBDao;


public class FormDataCollectorDefault implements FormDataCollector
{
	@Autowired
	CouchDBDao couchDbDao;
	
	@Override
	public JSONObject getFormDataJSON(Document xform, String formId, String ownerId, Long instanceId) throws Exception
	{
		JSONArray key = getObjectKey(formId, ownerId, instanceId);
		JSONObject response = couchDbDao.getFormByOwnerAndFormInstance(key);
		return response;
	}
	
	
	private JSONArray getObjectKey(String formId, String ownerId, Long instanceId)
	{
		JSONArray key = new JSONArray();
		key.add(ownerId);
		key.add(formId);
		key.add(instanceId);
//		log.debug("The key is: " + key);
		return key;
	}
}
