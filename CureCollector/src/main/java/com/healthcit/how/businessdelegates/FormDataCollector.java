/*******************************************************************************
 * Copyright (C) 2013 HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************************************/
package com.healthcit.how.businessdelegates;

import org.jdom.Document;

import net.sf.json.JSONObject;

public interface FormDataCollector
{
	JSONObject getFormDataJSON(Document xform, String formId, String ownerId, Long instanceId) throws Exception;
	
	}
